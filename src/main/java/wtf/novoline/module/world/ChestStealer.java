package wtf.novoline.module.world;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.event.RunTickEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.time.Stopwatch;

public class ChestStealer extends Module {
   private final NumberSetting chestStealer = settingManager.register(new NumberSetting(this, "Start delay", 50, 0, 500));
   private GuiScreen guiScreen;
   private final NumberSetting closeDelay = settingManager.register(new NumberSetting(this, "Close delay", 50, 0, 500) {
      @Override
      public boolean shouldShow() {
         return ChestStealer.this.autoClose.isEnabled();
      }
   });
   private final NumberSetting stealDelay = settingManager.register(new NumberSetting(this, "Steal delay", 100, 0, 500));
   private final CheckboxSetting randomizeDelay = settingManager.register(new CheckboxSetting(this, "Randomize delay", false));
   private final CheckboxSetting autoClose = settingManager.register(new CheckboxSetting(this, "Auto close", true));
   private final CheckboxSetting checkTitle = settingManager.register(new CheckboxSetting(this, "Check title", true));
   private final Stopwatch stealTimer = new Stopwatch();
   private final Stopwatch openTimer = new Stopwatch();
   private final Stopwatch closeTimer = new Stopwatch();
   @EventLink
   public final Listener<RunTickEvent> onRunTick = runTickEvent -> {
      if (this.guiScreen == null && minecraft.currentScreen instanceof GuiChest) {
         this.openTimer.reset();
      }

      this.guiScreen = minecraft.currentScreen;
   };
   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      GuiScreen currentScreen2 = minecraft.currentScreen;
      int i = (int)this.chestStealer.getCurrentValue();
      if (currentScreen2 instanceof GuiChest guiChest && this.openTimer.hasElapsed(i)) {
         boolean flag = this.stealDelay.getCurrentValue() == 0.0F;
         if (this.checkTitle.isEnabled()) {
            String unformattedText = guiChest.lowerChestInventory.getDisplayName().getUnformattedText();
            if (!this.isChestTitle(unformattedText)) {
               return;
            }
         }

         if (flag
            || this.stealTimer.hasElapsed((long)this.stealDelay.getCurrentValue())
               && (!this.randomizeDelay.isEnabled() || minecraft.thePlayer.getRNG().nextInt(10) > 3)) {
            boolean flag2 = !this.autoClose.isEnabled();
            ContainerChest containerChest = (ContainerChest)guiChest.inventorySlots;
            int sizeInventory = containerChest.getLowerChestInventory().getSizeInventory();
            List list = containerChest.inventorySlots.stream().limit(sizeInventory).collect(Collectors.toList());
            Collections.shuffle(list);

            for (Slot slot : (Iterable<Slot>) list) {
               ItemStack stack = slot.getStack();
               if (stack != null && stack.getItem() != null) {
                  minecraft.playerController.windowClick(containerChest.windowId, slot.slotNumber, 0, 1, minecraft.thePlayer);
                  this.stealTimer.reset();
                  flag2 = true;
                  if (!flag) {
                     break;
                  }
               }
            }

            if (flag2) {
               this.closeTimer.reset();
            } else if (this.autoClose.isEnabled() && this.closeTimer.hasElapsedFloat(this.closeDelay.getCurrentValue())) {
               minecraft.thePlayer.closeScreen();
            }
         }
      }
   };

   public ChestStealer() {
      super(Category.WORLD, "ChestStealer", "");
   }

   private boolean isChestTitle(String text) {
      return text.equals(I18n.format("container.chest"))
         || text.equals(I18n.format("container.chestDouble"))
         || text.contains("LOW")
         || StringUtils.stripControlCodes(text).isEmpty();
   }
}
