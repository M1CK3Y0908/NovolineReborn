package wtf.novoline.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import wtf.novoline.event.DeviceButtonDownEvent;
import wtf.novoline.event.LoadWorldEvent;
import wtf.novoline.event.DeviceButtonDownEvent.MouseType;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;

public class FreeLook extends Module {
   private final MultiComboSetting freeLook = settingManager.register(
      new MultiComboSetting(this, "Addons")
         .add("Modify yaw", true)
         .add("Modify pitch", true)
         .add("Lock pitch", false)
         .add("Invert pitch", true)
         .add("Custom FOV", false)
   );
   private boolean lookKeyHeld;
   private final CheckboxSetting enableOnToggle = settingManager.register(new CheckboxSetting(this, "Enable on toggle", false) {
      @Override
      public void interactCallback() {
         if (FreeLook.this.enableOnToggle.isEnabled()) {
            FreeLook.this.onToggled();
         }
      }
   });
   private final NumberSetting fov = settingManager.register(new NumberSetting(this, "FOV", 100, 30, 120) {
      @Override
      public boolean shouldShow() {
         return FreeLook.this.freeLook.isSelected(4);
      }
   });
   private float lookPitch;
   private float lookYaw;
   private float savedFov;
   @EventLink
   private final Listener<LoadWorldEvent> onLoadWorld = loadWorldEvent -> {
      if (this.enableOnToggle.isEnabled()) {
         this.disable();
      } else {
         this.lookKeyHeld = false;
      }
   };
   private int savedThirdPersonView;
   @EventLink
   private final Listener<DeviceButtonDownEvent> onDeviceButtonDown = deviceButtonDownEvent -> {
      if (deviceButtonDownEvent.mouseType == MouseType.MOUSE && !this.enableOnToggle.isEnabled()) {
         if (deviceButtonDownEvent.keyId == 2) {
            this.lookKeyHeld = Mouse.isButtonDown(2);
            this.onToggled();
         }
      }
   };

   public float getLookYaw() {
      return this.lookYaw;
   }

   public boolean isFreeLookActive() {
      return this.enableOnToggle.isEnabled() ? this.isEnabled() : this.lookKeyHeld && this.isEnabled();
   }

   @Override
   protected void onToggled() {
      if (minecraft.thePlayer != null) {
         if (this.enableOnToggle.isEnabled() && this.isEnabled() || !this.enableOnToggle.isEnabled() && this.lookKeyHeld) {
            this.lookYaw = minecraft.thePlayer.rotationYaw;
            this.lookPitch = minecraft.thePlayer.rotationPitch;
            this.savedThirdPersonView = minecraft.gameSettings.thirdPersonView;
            if (this.freeLook.isSelected(4)) {
               this.savedFov = minecraft.gameSettings.fovSetting;
               minecraft.gameSettings.fovSetting = this.fov.getCurrentValue();
            }

            minecraft.gameSettings.thirdPersonView = 1;
         } else {
            if (this.freeLook.isSelected(4)) {
               minecraft.gameSettings.fovSetting = this.savedFov;
            }

            minecraft.gameSettings.thirdPersonView = this.savedThirdPersonView;
         }

         if (minecraft.renderGlobal != null) {
            minecraft.renderGlobal.setDisplayListEntitiesDirty();
         }
      }
   }

   public float getLookPitch() {
      return this.lookPitch;
   }

   public FreeLook() {
      super(Category.VISUAL, "FreeLook", "Allows to move the camera freely");
   }

   public boolean handleMouseLook() {
      if (minecraft.inGameHasFocus) {
         if (!this.isFreeLookActive()) {
            return true;
         }

         minecraft.mouseHelper.mouseXYChange();
         float f = minecraft.gameSettings.mouseSensitivity * 0.6F + 0.2F;
         float f2 = f * f * f * 8.0F;
         if (this.freeLook.isSelected(0)) {
            float f3 = minecraft.mouseHelper.deltaX * f2;
            this.lookYaw += f3 * 0.15F;
         }

         if (this.freeLook.isSelected(1)) {
            float f4 = minecraft.mouseHelper.deltaY * f2;
            if (this.freeLook.isSelected(3)) {
               f4 = -f4;
            }

            this.lookPitch += f4 * 0.15F;
            if (this.freeLook.isSelected(2)) {
               this.lookPitch = MathHelper.clamp_float(this.lookPitch, -90.0F, 90.0F);
            }
         }

         minecraft.renderGlobal.setDisplayListEntitiesDirty();
      }

      return false;
   }

   @Override
   protected void onDisable() {
      this.lookKeyHeld = false;
   }
}
