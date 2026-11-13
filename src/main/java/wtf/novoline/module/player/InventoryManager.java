package wtf.novoline.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderEye;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import wtf.novoline.enums.HypixelGameType;
import wtf.novoline.event.GameEndEvent;
import wtf.novoline.event.LoadWorldEvent;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.hypixel.HypixelState;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.notification.NotificationType;
import wtf.novoline.utils.player.InventoryUtils;
import wtf.novoline.utils.time.Stopwatch;

public class InventoryManager extends Module {
   private final List<Integer> ignoredPotionIds = Arrays.asList(
      Potion.confusion.id, Potion.poison.id, Potion.blindness.id, Potion.moveSlowdown.id, Potion.hunger.id, Potion.digSlowdown.id, Potion.weakness.id
   );
   private final MultiComboSetting disableRules = settingManager.register(
      new MultiComboSetting(this, "Disable rules").add("World change", false).add("Death", false).add("Game end", false)
   );
   private final CheckboxSetting onlyInOpenInventory = settingManager.register(new CheckboxSetting(this, "Only in open inventory", false));
   private boolean gameEnded;
   private final CheckboxSetting silentlyOpenInventory = settingManager.register(new CheckboxSetting(this, "Silently open inventory", false) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.onlyInOpenInventory.isEnabled();
      }
   });
   private final CheckboxSetting onlyWhileStanding = settingManager.register(new CheckboxSetting(this, "Only while standing", false));
   private final CheckboxSetting disableInLobby = settingManager.register(new CheckboxSetting(this, "Disable in Lobby", true));
   private final CheckboxSetting savePotions = settingManager.register(new CheckboxSetting(this, "Save potions", true));
   private final CheckboxSetting removeBadPotions = settingManager.register(new CheckboxSetting(this, "Remove bad potions", true));
   private final CheckboxSetting saveBlocks = settingManager.register(new CheckboxSetting(this, "Save blocks", true));
   private final CheckboxSetting saveProjectiles = settingManager.register(new CheckboxSetting(this, "Save projectiles", true));
   private final CheckboxSetting saveFood = settingManager.register(new CheckboxSetting(this, "Save food", true));
   private final CheckboxSetting sortSword = settingManager.register(new CheckboxSetting(this, "Sort sword", true));
   private final CheckboxSetting sortBow = settingManager.register(new CheckboxSetting(this, "Sort bow", true));
   private final CheckboxSetting sortBlocks = settingManager.register(new CheckboxSetting(this, "Sort blocks", true));
   private final NumberSetting delay = settingManager.register(new NumberSetting(this, "Delay", 150, 0, 1000));
   private final NumberSetting startDelay = settingManager.register(new NumberSetting(this, "Start delay", 0, 0, 1000));
   private final Stopwatch moveTimer = new Stopwatch();
   private final Stopwatch startTimer = new Stopwatch();
   @EventLink
   public final Listener<GameEndEvent> onGameEnd = gameEndEvent -> {
      if (this.disableRules.isSelected(2)) {
         this.setEnabled(false);
         this.showNotification(
            NotificationType.INFO, "InventoryManager", Language.translate("feature.inventorymanager.disable_rules.notification.game_end"), 2500L
         );
      } else {
         this.gameEnded = true;
      }
   };
   @EventLink
   public final Listener<LoadWorldEvent> onLoadWorld = loadWorldEvent -> {
      if (this.disableRules.isSelected(0)) {
         this.setEnabled(false);
         this.showNotification(
            NotificationType.INFO, "InventoryManager", Language.translate("feature.inventorymanager.disable_rules.notification.world_change"), 2500L
         );
      } else if (this.gameEnded) {
         this.gameEnded = false;
      }
   };
   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      if (!minecraft.thePlayer.isEntityAlive() && this.disableRules.getElements().get(1).isSelected() && this.disableRules.isSelected(1)) {
         this.setEnabled(false);
         this.showNotification(
            NotificationType.INFO, "InventoryManager", Language.translate("feature.inventorymanager.disable_rules.notification.death"), 2500L
         );
      }

      if ((!this.onlyInOpenInventory.isEnabled() || this.silentlyOpenInventory.isEnabled() || minecraft.currentScreen instanceof GuiInventory)
         && (!this.onlyWhileStanding.isEnabled() || minecraft.thePlayer.moveStrafing == 0.0F && minecraft.thePlayer.moveForward == 0.0F)
         && (!this.disableInLobby.isEnabled() || !HypixelState.isGameType(HypixelGameType.LOBBY) && !HypixelState.isGameType(HypixelGameType.PRE_GAME))
         && !ModuleManager.autoArmor.processing
         && !this.gameEnded) {
         if (this.startTimer.hasElapsed((long)this.startDelay.getCurrentValue()) && this.moveTimer.hasElapsed((long)this.delay.getCurrentValue())) {
            if (this.sortSword.isEnabled()) {
               int bestItemSlot = InventoryUtils.findBestItemSlot(ItemSword.class);
               if (bestItemSlot != -1 && bestItemSlot != 0) {
                  if (bestItemSlot >= 36 && bestItemSlot <= 39) {
                     bestItemSlot = 44 - bestItemSlot;
                  } else if (bestItemSlot < 9) {
                     bestItemSlot += 36;
                  }

                  this.silentlyOpenInventory();
                  minecraft.playerController.windowClick(0, bestItemSlot, 0, 2, minecraft.thePlayer);
                  this.moveTimer.reset();
                  return;
               }
            }

            InventoryPlayer inventory2 = minecraft.thePlayer.inventory;

            for (int i = 0; i < inventory2.getSizeInventory(); i++) {
               if (i != 40) {
                  ItemStack stackInSlot = inventory2.getStackInSlot(i);
                  if (stackInSlot != null && stackInSlot.getItem() != null) {
                     Item item2 = stackInSlot.getItem();
                     int i2 = -1;
                     if (item2 instanceof ItemArmor itemArmor) {
                        i2 = InventoryUtils.findBestArmorSlot(itemArmor.armorType);
                     } else if (item2 instanceof ItemTool || item2 instanceof ItemSword || item2 instanceof ItemHoe || item2 instanceof ItemBow) {
                        i2 = InventoryUtils.findBestItemSlot(item2.getClass());
                     } else if (this.isJunk(stackInSlot)) {
                        i2 = Integer.MAX_VALUE;
                     }

                     if (i2 != -1 && i != i2) {
                        int i3 = i;
                        if (i3 >= 36 && i3 <= 39) {
                           i3 = 44 - i3;
                        } else if (i3 < 9) {
                           i3 += 36;
                        }

                        this.silentlyOpenInventory();
                        minecraft.playerController.windowClick(0, i3, 1, 4, minecraft.thePlayer);
                        this.moveTimer.reset();
                        return;
                     }
                  }
               }
            }

            if (this.sortBow.isEnabled()) {
               int bestItemSlot2 = InventoryUtils.findBestItemSlot(ItemBow.class);
               if (bestItemSlot2 != -1 && bestItemSlot2 != 1) {
                  if (bestItemSlot2 >= 36 && bestItemSlot2 <= 39) {
                     bestItemSlot2 = 44 - bestItemSlot2;
                  } else if (bestItemSlot2 < 9) {
                     bestItemSlot2 += 36;
                  }

                  this.silentlyOpenInventory();
                  minecraft.playerController.windowClick(0, bestItemSlot2, 1, 2, minecraft.thePlayer);
                  this.moveTimer.reset();
                  return;
               }
            }

            if (this.sortBlocks.isEnabled()) {
               ItemStack itemStack = minecraft.thePlayer.inventory.mainInventory[8];
               if (itemStack == null || !(itemStack.getItem() instanceof ItemBlock)) {
                  int slotWithBlocks = InventoryUtils.findSlotWithBlocks(true);
                  if (slotWithBlocks != -1 && slotWithBlocks != 8) {
                     int i4 = slotWithBlocks;
                     if (i4 >= 36 && i4 <= 39) {
                        i4 = 44 - i4;
                     } else if (i4 < 9) {
                        i4 += 36;
                     }

                     this.silentlyOpenInventory();
                     minecraft.playerController.windowClick(0, i4, 8, 2, minecraft.thePlayer);
                     this.moveTimer.reset();
                     return;
                  }
               }
            }

            this.silentlyCloseInventory();
         }
      } else {
         this.moveTimer.reset();
         this.startTimer.reset();
      }
   };
   private boolean inventoryOpened;

   private void silentlyOpenInventory() {
      if (!this.inventoryOpened && this.silentlyOpenInventory.isEnabled()) {
         this.sendPacket(new C16PacketClientStatus(EnumState.OPEN_INVENTORY_ACHIEVEMENT));
         this.inventoryOpened = true;
      }
   }

   private void silentlyCloseInventory() {
      if (this.inventoryOpened && this.silentlyOpenInventory.isEnabled()) {
         this.sendPacket(new C0DPacketCloseWindow());
         this.inventoryOpened = false;
      }
   }

   public InventoryManager() {
      super(Category.PLAYER, "InventoryManager", "");
   }

   private boolean isJunk(ItemStack itemStack) {
      Item item2 = itemStack.getItem();
      if (item2 == Items.arrow) {
         return false;
      }

      if (item2 instanceof ItemFlintAndSteel) {
         return false;
      }

      if (this.savePotions.isEnabled()) {
         if (item2 instanceof ItemExpBottle) {
            return false;
         }

         if (item2 instanceof ItemPotion itemPotion) {
            if (this.removeBadPotions.isEnabled()) {
               List effects = itemPotion.getEffects(itemStack);
               if (effects != null) {
                  for (PotionEffect potionEffect : itemPotion.getEffects(itemStack)) {
                     if (this.ignoredPotionIds.contains(potionEffect.getPotionID())) {
                        return true;
                     }
                  }
               }
            }

            return false;
         }
      }

      if (this.saveBlocks.isEnabled() && item2 instanceof ItemBlock itemBlock && itemBlock.getBlock().isFullCube()) {
         return false;
      }

      if (this.saveProjectiles.isEnabled()) {
         if (item2 instanceof ItemEnderPearl) {
            return false;
         }

         if (item2 instanceof ItemEnderEye) {
            return false;
         }

         if (item2 instanceof ItemEgg) {
            return false;
         }

         if (item2 instanceof ItemSnowball) {
            return false;
         }
      }

      return !this.saveFood.isEnabled() || !(item2 instanceof ItemFood);
   }
}
