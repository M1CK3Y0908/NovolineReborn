package wtf.novoline.module.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Mouse;
import wtf.novoline.event.DeviceButtonDownEvent;
import wtf.novoline.event.UpdateEvent;
import wtf.novoline.event.DeviceButtonDownEvent.MouseType;
import wtf.novoline.gui.menu.MenuClickGuiScreen;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.ScheduledTask;
import wtf.novoline.utils.math.InterpolationUtils;
import wtf.novoline.utils.time.Stopwatch;

public class AutoClicker extends Module {
   private final ComboSetting autoClicker = settingManager.register(new ComboSetting(this, "Mode", 0, "Simple", "Click booster"));
   private final NumberSetting minCps = settingManager.register(new NumberSetting(this, "Min CPS", 7, 1, 20) {
      @Override
      public boolean shouldShow() {
         return AutoClicker.this.autoClicker.getCurrentElement() == 0;
      }
   });
   private final NumberSetting maxCps = settingManager.register(new NumberSetting(this, "Max CPS", 12, 1, 20) {
      @Override
      public boolean shouldShow() {
         return AutoClicker.this.autoClicker.getCurrentElement() == 0;
      }
   });
   private final NumberSetting cpsRiseDelay = settingManager.register(new NumberSetting(this, "CPS rise delay", 20, 0, 100) {
      @Override
      public boolean shouldShow() {
         return AutoClicker.this.autoClicker.getCurrentElement() == 0;
      }
   });
   private final CheckboxSetting blockHit = settingManager.register(new CheckboxSetting(this, "Block hit", false) {
      @Override
      public boolean shouldShow() {
         return AutoClicker.this.autoClicker.getCurrentElement() == 0;
      }
   });
   private final CheckboxSetting ignoreInventory = settingManager.register(new CheckboxSetting(this, "Ignore inventory", false) {
      @Override
      public boolean shouldShow() {
         return AutoClicker.this.autoClicker.getCurrentElement() == 0;
      }
   });
   private final ComboSetting cpsAmount = settingManager.register(new ComboSetting(this, "CPS amount", 0, "Random", "Smooth") {
      @Override
      public boolean shouldShow() {
         return AutoClicker.this.autoClicker.getCurrentElement() == 0;
      }
   });
   private final NumberSetting delayMs = settingManager.register(new NumberSetting(this, "Delay (ms)", 50, 1, 1000) {
      @Override
      public boolean shouldShow() {
         return AutoClicker.this.cpsAmount.getCurrentElement() == 1;
      }
   });
   private final ComboSetting blockHitMode = settingManager.register(new ComboSetting(this, "Block hit mode", 0, "Legit", "Packet") {
      @Override
      public boolean shouldShow() {
         return AutoClicker.this.autoClicker.getCurrentElement() == 0 && AutoClicker.this.blockHit.isEnabled();
      }
   });
   private final NumberSetting chance = settingManager.register(new NumberSetting(this, "Chance", 2, 1, 10) {
      @Override
      public boolean shouldShow() {
         return AutoClicker.this.autoClicker.getCurrentElement() == 1;
      }
   });
   private final NumberSetting attackDelay = settingManager.register(new NumberSetting(this, "Attack delay", 20, 0, 250) {
      @Override
      public boolean shouldShow() {
         return AutoClicker.this.autoClicker.getCurrentElement() == 1;
      }
   });
   private final CheckboxSetting ignoreBlockHit = settingManager.register(new CheckboxSetting(this, "Ignore block hit", false) {
      @Override
      public boolean shouldShow() {
         return AutoClicker.this.autoClicker.getCurrentElement() == 1;
      }
   });
   private final CheckboxSetting applyRandomization = settingManager.register(new CheckboxSetting(this, "Apply randomization", false) {
      @Override
      public boolean shouldShow() {
         return AutoClicker.this.autoClicker.getCurrentElement() == 1;
      }
   });
   private final Stopwatch cpsRiseTimer = new Stopwatch();
   private final Stopwatch clickTimer = new Stopwatch();
   private int blockHitPhase = 0;
   private int currentCps = 1;
   private int cpsRiseJitter = 0;
   private boolean canClick = true;
   @EventLink
   public final Listener<UpdateEvent> onUpdate = updateEvent -> {
      if (!(minecraft.currentScreen instanceof MenuClickGuiScreen)
         && (minecraft.currentScreen == null || this.ignoreInventory.isEnabled())
         && this.autoClicker.getCurrentElement() == 0) {
         if (Mouse.isButtonDown(0) && this.canClick) {
            if (this.currentCps < this.maxCps.getCurrentValue() && this.cpsRiseTimer.hasElapsedFloat(this.cpsRiseDelay.getCurrentValue() + this.cpsRiseJitter)) {
               this.currentCps++;
               this.cpsRiseJitter = ThreadLocalRandom.current().nextInt(-10, 10);
               this.cpsRiseTimer.reset();
            } else {
               int currentElement = this.cpsAmount.getCurrentElement();
               int i = (int)this.minCps.getCurrentValue();
               int i2 = (int)this.maxCps.getCurrentValue();
               int[] iArray = this.normalizeCpsRange(i, i2);
               i = iArray[0];
               i2 = iArray[1];

               this.currentCps = switch (currentElement) {
                  case 0 -> ThreadLocalRandom.current().nextInt(i, i2);
                  case 1 -> (int)InterpolationUtils.oscillate(i, i2, (long)this.delayMs.getCurrentValue());
                  default -> throw new IllegalStateException("Unexpected value: " + currentElement);
               };
            }
         } else {
            this.currentCps = 1;
         }

         if (this.currentCps != 0 && Mouse.isButtonDown(0) && this.clickTimer.hasElapsed(1000 / this.currentCps)) {
            KeyBinding keyBindUseItem2 = minecraft.gameSettings.keyBindUseItem;
            boolean keyDown = keyBindUseItem2.isKeyDown();
            if (this.blockHit.isEnabled()) {
               if (this.blockHitMode.getCurrentElement() == 0) {
                  if (this.blockHitPhase == 0 && keyDown) {
                     this.blockHitPhase = 1;
                     this.setUseItemKeyDown(false);
                     return;
                  }

                  if (this.blockHitPhase == 1) {
                     this.blockHitPhase = 2;
                     this.leftClick();
                     return;
                  }

                  if (this.blockHitPhase == 2) {
                     this.blockHitPhase = 0;
                     this.setUseItemKeyDown(true);
                     this.restartClickTimer();
                     return;
                  }
               } else if (keyDown) {
                  this.sendPacketUnregistered(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
               }
            }

            this.clickAndRestartTimer();
            if (this.blockHit.isEnabled() && keyDown && this.blockHitMode.getCurrentElement() == 1) {
               BlockPos blockPos = new BlockPos(-1, -1, -1);
               ItemStack heldItem = minecraft.thePlayer.getHeldItem();
               float f = 0.0F;
               float f2 = 0.0F;
               float f3 = 0.0F;
               ItemStack itemStack = heldItem;
               this.sendPacketUnregistered(new C08PacketPlayerBlockPlacement(blockPos, 255, itemStack, f3, f2, f));
            }
         }
      }
   };
   @EventLink
   private final Listener<DeviceButtonDownEvent> onDeviceButtonDown = deviceButtonDownEvent -> {
      int keyId2 = deviceButtonDownEvent.keyId;
      if (deviceButtonDownEvent.mouseType == MouseType.MOUSE) {
         keyId2 -= 100;
      }

      if (keyId2 == minecraft.gameSettings.keyBindAttack.getKeyCode()
         && this.autoClicker.getCurrentElement() == 1
         && minecraft.gameSettings.keyBindAttack.isKeyDown()) {
         this.scheduleClick();
      }
   };

   private void restartClickTimer() {
      this.clickTimer.reset();
      this.canClick = true;
   }

   static Minecraft access$000() {
      return minecraft;
   }

   private void setUseItemKeyDown(boolean flag) {
      KeyBinding.setKeyBindState(minecraft.gameSettings.keyBindUseItem.getKeyCode(), flag);
   }

   private void leftClick() {
      minecraft.leftClickCounter = 0;
      if (minecraft.currentScreen != null && !(minecraft.currentScreen instanceof MenuClickGuiScreen)) {
         GuiScreen currentScreen2 = minecraft.currentScreen;
         int i = Mouse.getEventX() * currentScreen2.width / minecraft.displayWidth;
         int i2 = currentScreen2.height - Mouse.getEventY() * currentScreen2.height / minecraft.displayHeight - 1;

         try {
            currentScreen2.handleInt2(i, i2);
            currentScreen2.handleInt5(i, i2);
         } catch (IOException ioException) {
         }
      } else {
         minecraft.clickMouse();
      }
   }

   private void clickAndRestartTimer() {
      this.leftClick();
      this.restartClickTimer();
   }

   private void scheduleClick() {
      if (ThreadLocalRandom.current().nextInt(0, 100) % this.chance.getCurrentValue() == 0.0F) {
         int i = (int)this.attackDelay.getCurrentValue();
         if (this.applyRandomization.isEnabled()) {
            i += ThreadLocalRandom.current().nextInt(-10, 10);
         }

         novoline.getTaskScheduler().schedule(new ScheduledTask(i) {
            @Override
            public void run() {
               if (!AutoClicker.this.ignoreBlockHit.isEnabled() || !AutoClicker.access$000().thePlayer.isUsingItem()) {
                  AutoClicker.this.leftClick();
               }
            }
         });
      }
   }

   private int[] normalizeCpsRange(int i, int i2) {
      if (i > i2) {
         i2 = i;
      }

      if (i == i2) {
         i = Math.max(0, i - 1);
      }

      if (i == 0 && i2 == 0) {
         i2 = 1;
      }

      return new int[]{i, i2};
   }

   public AutoClicker() {
      super(Category.COMBAT, "AutoClicker", "Automatically clicks for you");
   }
}
