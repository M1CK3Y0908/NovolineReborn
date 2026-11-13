package wtf.novoline.module.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;
import net.minecraft.potion.PotionEffect;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.player.InventoryUtils;
import wtf.novoline.utils.player.PlayerUtils;
import wtf.novoline.utils.time.Stopwatch;

public class AutoPotion extends Module {
   private final MultiComboSetting autoPotion = settingManager.register(
      new MultiComboSetting(this, "Potions").add("Regeneration", true).add("Speed", false).add("Resistance", false).add("Instant Health", true)
   );
   private final ComboSetting throwMode = settingManager.register(new ComboSetting(this, "Throw mode", 0, "To ground", "Above player"));
   private static final Map<String, Integer> potionSettingIndex = Map.of(
      "potion.regeneration", 0, "potion.moveSpeed", 1, "potion.resistance", 2, "potion.heal", 3
   );
   private final NumberSetting delay = settingManager.register(new NumberSetting(this, "Delay", 100, 0, 1000));
   private final NumberSetting minimumHealth = settingManager.register(new NumberSetting(this, "Minimum health", 10, 1, 20) {
      @Override
      public boolean shouldShow() {
         return AutoPotion.this.autoPotion.isSelected(0) || AutoPotion.this.autoPotion.isSelected(2);
      }
   });
   private final CheckboxSetting ignoreInventory = settingManager.register(new CheckboxSetting(this, "Ignore inventory", false));
   private final CheckboxSetting silentlyOpenInventory = settingManager.register(new CheckboxSetting(this, "Silently open inventory", true));
   private final CheckboxSetting onlyWhileStanding = settingManager.register(new CheckboxSetting(this, "Only while standing", false));
   private final CheckboxSetting onlyOnGround = settingManager.register(new CheckboxSetting(this, "Only on ground", false));
   private final Stopwatch delayTimer = new Stopwatch();
   @EventLink(0)
   private final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      if ((!PlayerUtils.isMoving() || !this.onlyWhileStanding.isEnabled()) && !(minecraft.currentScreen instanceof GuiChest)) {
         if (!this.onlyOnGround.isEnabled() || minecraft.thePlayer.onGround) {
            if (!this.ignoreInventory.isEnabled() || !(minecraft.currentScreen instanceof GuiInventory)) {
               ItemStack[] mainInventory2 = minecraft.thePlayer.inventory.mainInventory;
               this.scanForPotions(preUpdateEvent, Arrays.copyOfRange(mainInventory2, 0, 9), false);
               this.scanForPotions(preUpdateEvent, Arrays.copyOfRange(mainInventory2, 9, mainInventory2.length), true);
            }
         }
      }
   };

   private void scanForPotions(PreUpdateEvent preUpdateEvent2, ItemStack[] itemStacks, boolean flag) {
      Arrays.stream(itemStacks)
         .filter(Objects::nonNull)
         .filter(itemStack -> itemStack.getItem() != null && itemStack.getItem() instanceof ItemPotion)
         .forEach(
            itemStack -> {
               ItemPotion itemPotion = (ItemPotion)itemStack.getItem();
               if (itemPotion.getEffects(itemStack) != null) {
                  for (PotionEffect potionEffect : itemPotion.getEffects(itemStack)) {
                     int orDefault = potionSettingIndex.getOrDefault(potionEffect.getEffectName(), -1);
                     if (orDefault != -1
                        && this.autoPotion.isSelected(orDefault)
                        && this.delayTimer.hasElapsed((long)this.delay.getCurrentValue())
                        && !minecraft.thePlayer.isPotionActive(potionEffect.getPotionID())
                        && (orDefault != 0 && orDefault != 2 && orDefault != 3 || !(minecraft.thePlayer.getHealth() > this.minimumHealth.getCurrentValue()))) {
                        this.throwPotion(preUpdateEvent2, itemStack, flag);
                        this.delayTimer.reset();
                        break;
                     }
                  }
               }
            }
         );
   }

   public AutoPotion() {
      super(Category.COMBAT, "AutoPotion", "Throws potions for you");
   }

   private int indexInInventory(ItemStack itemStack2) {
      ItemStack[] mainInventory2 = minecraft.thePlayer.inventory.mainInventory;

      for (int i = 0; i < mainInventory2.length; i++) {
         if (itemStack2.equals(mainInventory2[i])) {
            return i;
         }
      }

      return -1;
   }

   private void throwPotion(PreUpdateEvent preUpdateEvent2, ItemStack itemStack2, boolean flag) {
      boolean flag2 = minecraft.currentScreen instanceof GuiInventory;
      if (this.silentlyOpenInventory.isEnabled() && flag && !flag2) {
         this.sendPacket(new C16PacketClientStatus(EnumState.OPEN_INVENTORY_ACHIEVEMENT));
      }

      int i = this.indexInInventory(itemStack2);
      if (flag) {
         int emptyHotbarSlot = InventoryUtils.findEmptyHotbarSlot();
         emptyHotbarSlot = emptyHotbarSlot == -1 ? 8 : emptyHotbarSlot;
         minecraft.playerController.windowClick(0, i, emptyHotbarSlot, 2, minecraft.thePlayer);
         i = emptyHotbarSlot;
      }

      ItemStack currentEquippedItem = minecraft.thePlayer.getCurrentEquippedItem();
      int i2 = currentEquippedItem == null ? 0 : this.indexInInventory(currentEquippedItem);
      float rotationYaw2 = minecraft.thePlayer.rotationYaw;
      float rotationPitch2 = minecraft.thePlayer.rotationPitch;
      float f;
      float f2;
      if (this.throwMode.getCurrentElement() == 0) {
         f = 0.0F;
         f2 = 90.0F;
      } else {
         f = 0.0F;
         f2 = -90.0F;
      }

      if (this.silentlyOpenInventory.isEnabled() && flag) {
         this.sendPacket(new C0DPacketCloseWindow());
      }

      this.sendPacket(new C05PacketPlayerLook(f, f2, minecraft.thePlayer.onGround));
      this.sendPacket(new C09PacketHeldItemChange(i));
      this.sendPacket(new C08PacketPlayerBlockPlacement(itemStack2));
      this.sendPacket(new C09PacketHeldItemChange(i2));
      this.sendPacket(new C05PacketPlayerLook(rotationYaw2, rotationPitch2, minecraft.thePlayer.onGround));
      if (flag2) {
         this.sendPacket(new C16PacketClientStatus(EnumState.OPEN_INVENTORY_ACHIEVEMENT));
      }
   }
}
