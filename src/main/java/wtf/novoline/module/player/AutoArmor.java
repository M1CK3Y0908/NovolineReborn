package wtf.novoline.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;
import wtf.novoline.event.GameEndEvent;
import wtf.novoline.event.LoadWorldEvent;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.notification.NotificationType;
import wtf.novoline.utils.player.InventoryUtils;
import wtf.novoline.utils.time.Stopwatch;

public class AutoArmor extends Module {
   private final MultiComboSetting autoArmor = settingManager.register(
      new MultiComboSetting(this, "Disable rules").add("World change", false).add("Death", false).add("Game end", false)
   );
   private final CheckboxSetting onlyInOpenInventory = settingManager.register(new CheckboxSetting(this, "Only in open inventory", false));
   private final CheckboxSetting silentlyOpenInventory = settingManager.register(new CheckboxSetting(this, "Silently open inventory", false) {
      @Override
      public boolean shouldShow() {
         return AutoArmor.this.onlyInOpenInventory.isEnabled();
      }
   });
   private final CheckboxSetting onlyWhileStanding = settingManager.register(new CheckboxSetting(this, "Only while standing", false));
   private final CheckboxSetting dropOldArmor = settingManager.register(new CheckboxSetting(this, "Drop old armor", false));
   private final NumberSetting delay = settingManager.register(new NumberSetting(this, "Delay", 150, 0, 1000));
   private final NumberSetting startDelay = settingManager.register(new NumberSetting(this, "Start delay", 0, 0, 1000));
   private final Stopwatch moveTimer = new Stopwatch();
   private final Stopwatch startTimer = new Stopwatch();
   @EventLink
   public final Listener<GameEndEvent> onGameEnd = gameEndEvent -> {
      if (this.autoArmor.isSelected(2)) {
         this.setEnabled(false);
         this.showNotification(NotificationType.INFO, "AutoArmor", Language.translate("feature.autoarmor.disable_rules.notification.game_end"), 2500L);
      }
   };
   @EventLink
   public final Listener<LoadWorldEvent> onLoadWorld = loadWorldEvent -> {
      if (this.autoArmor.isSelected(0)) {
         this.setEnabled(false);
         this.showNotification(NotificationType.INFO, "AutoArmor", Language.translate("feature.autoarmor.disable_rules.notification.world_change"), 2500L);
      }
   };
   public boolean processing;
   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      if (!minecraft.thePlayer.isEntityAlive() && this.autoArmor.getElements().get(1).isSelected() && this.autoArmor.isSelected(1)) {
         this.setEnabled(false);
         this.showNotification(NotificationType.INFO, "AutoArmor", Language.translate("feature.autoarmor.disable_rules.notification.death"), 2500L);
      }

      this.processing = false;
      if (this.onlyInOpenInventory.isEnabled() && !this.silentlyOpenInventory.isEnabled() && !(minecraft.currentScreen instanceof GuiInventory)) {
         this.moveTimer.reset();
         this.startTimer.reset();
      } else if (!this.onlyWhileStanding.isEnabled() || minecraft.thePlayer.moveStrafing == 0.0F && minecraft.thePlayer.moveForward == 0.0F) {
         this.processing = true;
         InventoryPlayer inventory2 = minecraft.thePlayer.inventory;
         int i = -1;
         int i2 = -1;

         for (int i3 = 0; i3 < inventory2.armorInventory.length; i3++) {
            int bestArmorSlot = InventoryUtils.findBestArmorSlot(i3);
            if (bestArmorSlot != -1 && bestArmorSlot != 39 - i3) {
               i = i3;
               i2 = bestArmorSlot;
               break;
            }
         }

         if (i != -1) {
            if (this.moveTimer.hasElapsed((long)this.delay.getCurrentValue()) && this.startTimer.hasElapsed((long)this.startDelay.getCurrentValue())) {
               this.moveTimer.reset();
               if (i2 < 9) {
                  i2 += 36;
               }

               boolean flag = true;
               ItemStack itemStack = inventory2.armorInventory[3 - i];
               if (itemStack != null && itemStack.getItem() instanceof ItemArmor) {
                  if (this.dropOldArmor.isEnabled()) {
                     minecraft.playerController.windowClick(0, 5 + i, 0, 4, minecraft.thePlayer);
                     return;
                  }
               } else {
                  flag = false;
               }

               if (!this.inventoryOpened && this.silentlyOpenInventory.isEnabled()) {
                  this.sendPacket(new C16PacketClientStatus(EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                  this.inventoryOpened = true;
               }

               if (flag) {
                  minecraft.playerController.windowClick(0, i2, 1, 0, minecraft.thePlayer);
                  minecraft.playerController.windowClick(0, 5 + i, 1, 0, minecraft.thePlayer);
                  minecraft.playerController.windowClick(0, i2, 0, 0, minecraft.thePlayer);
               } else {
                  minecraft.playerController.windowClick(0, i2, 0, 1, minecraft.thePlayer);
               }
            }
         } else {
            if (this.inventoryOpened && this.silentlyOpenInventory.isEnabled()) {
               this.sendPacket(new C0DPacketCloseWindow());
               this.inventoryOpened = false;
            }

            this.processing = false;
         }
      } else {
         this.moveTimer.reset();
         this.startTimer.reset();
      }
   };
   public boolean inventoryOpened;

   public AutoArmor() {
      super(Category.PLAYER, "AutoArmor", "");
   }

   @Override
   protected void onToggled() {
      this.processing = false;
   }
}
