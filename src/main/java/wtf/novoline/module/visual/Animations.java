package wtf.novoline.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.EnumAction;
import net.minecraft.util.MathHelper;
import wtf.novoline.event.RenderScreenEvent;
import wtf.novoline.event.RunTickEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.NumberSetting;

public class Animations extends Module {
   private final ComboSetting animations;
   private final ComboSetting consuming;
   private final ComboSetting bow;
   private final NumberSetting slowdown;
   private final NumberSetting downscale;
   private final NumberSetting hitHeight;
   private final NumberSetting blockHitHeight;
   private final CheckboxSetting rotateAroundScreen;
   private static final String[] stringArray = new String[]{"1.7", "Type 1", "Type 2", "Type 3", "Spinning"};
   @EventLink
   private final Listener<RenderScreenEvent> onRenderScreen;
   private float animatedHeight;
   private int spinAngle;
   @EventLink
   private final Listener<RunTickEvent> onRunTick;

   public NumberSetting getDownscale() {
      return this.downscale;
   }

   public NumberSetting getBlockHitHeight() {
      return this.blockHitHeight;
   }

   public ComboSetting getBow() {
      return this.bow;
   }

   public float getAnimatedHeight() {
      return this.animatedHeight;
   }

   public NumberSetting getSlowdown() {
      return this.slowdown;
   }

   public CheckboxSetting getRotateAroundScreen() {
      return this.rotateAroundScreen;
   }

   public ComboSetting getConsuming() {
      return this.consuming;
   }

   public Animations() {
      super(Category.VISUAL, "Animations", "Modifies item animations");
      this.animations = settingManager.register(new ComboSetting(this, "Blocking", 0, stringArray));
      this.consuming = settingManager.register(new ComboSetting(this, "Consuming", 0, stringArray));
      this.bow = settingManager.register(new ComboSetting(this, "Bow", 0, stringArray));
      this.slowdown = settingManager.register(new NumberSetting(this, "Slowdown", 0, -4, 6));
      this.downscale = settingManager.register(new NumberSetting(this, "Downscale", 0.0F, 0.0F, 0.5F));
      this.hitHeight = settingManager.register(new NumberSetting(this, "Hit height", 10, 0, 60));
      this.blockHitHeight = settingManager.register(new NumberSetting(this, "BlockHit height", 10, 0, 60));
      this.rotateAroundScreen = settingManager.register(new CheckboxSetting(this, "Rotate around screen", false));
      this.onRenderScreen = renderScreenEvent -> {
         float f2 = 20.0F;
         if (minecraft.thePlayer.isSwingInProgress && minecraft.thePlayer.isBlocking()) {
            f2 -= this.blockHitHeight.getCurrentValue();
         } else if (minecraft.thePlayer.getHeldItem() != null) {
            f2 -= this.hitHeight.getCurrentValue();
         }

         this.animatedHeight = f2;
         if (this.animatedHeight < f2) {
            this.animatedHeight++;
         } else if (this.animatedHeight > f2) {
            this.animatedHeight--;
         }
      };
      this.onRunTick = runTickEvent -> this.setSuffix(this.animations.getCurrentElementAsString());
   }

   private void applyTransform(ComboSetting comboSetting, Consumer consumer, BiConsumer biConsumer, Animations.ItemTransformState itemTransformState) {
      this.spinAngle++;
      this.spinAngle %= 360;
      if (this.rotateAroundScreen.isEnabled()) {
         GlStateManager.rotate(this.spinAngle, 0.0F, 0.0F, 1.0F);
      }

      switch (comboSetting.getCurrentElement()) {
         case 0:
            biConsumer.accept(itemTransformState.equipProgress, itemTransformState.swingProgress);
            consumer.accept(0.0F);
            break;
         case 1:
            biConsumer.accept(itemTransformState.equipProgress / 2.0F, itemTransformState.swingProgress);
            float floatValue = MathHelper.createFloat2(MathHelper.sqrt_double(itemTransformState.swingProgress) * Math.PI);
            GlStateManager.rotate(floatValue * 30.0F / 2.0F, -floatValue, -0.0F, 9.0F);
            GlStateManager.rotate(floatValue * 40.0F, 1.0F, -floatValue / 2.0F, -0.0F);
            consumer.accept(0.4F);
            break;
         case 2:
            GlStateManager.translate(-0.04, 0.13, 0.0);
            biConsumer.accept(itemTransformState.equipProgress / 2.5F, 0.0F);
            GlStateManager.rotate(-itemTransformState.swingSine * 40.0F / 2.0F, itemTransformState.swingSine / 2.0F, 1.0F, 4.0F);
            GlStateManager.rotate(-itemTransformState.swingSine * 30.0F, 1.0F, itemTransformState.swingSine / 3.0F, -0.0F);
            consumer.accept(0.2F);
            break;
         case 3:
            biConsumer.accept(itemTransformState.equipProgress, 0.0F);
            consumer.accept(0.2F);
            GlStateManager.translate(0.1F, 0.2F, 0.3F);
            GlStateManager.rotate(-itemTransformState.swingSine * 30.0F, -5.0F, 0.0F, 9.0F);
            GlStateManager.rotate(-itemTransformState.swingSine * 10.0F, 1.0F, -0.4F, -0.5F);
            break;
         case 4:
            GlStateManager.translate(-0.04, 0.1, 0.0);
            biConsumer.accept(itemTransformState.equipProgress / 2.5F, 0.0F);
            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.2F);
            GlStateManager.rotate(this.spinAngle, 0.0F, -1.0F, 0.0F);
      }
   }

   public void performTransform(EnumAction enumAction, Consumer<Float> consumer, BiConsumer<Float, Float> biConsumer, Animations.ItemTransformState itemTransformState) {
      ComboSetting comboSetting = switch (enumAction) {
         case EAT, DRINK -> this.consuming;
         case BLOCK -> this.animations;
         case BOW -> this.bow;
         default -> throw new IllegalStateException("Unexpected value: " + enumAction);
      };
      boolean flag = enumAction == EnumAction.EAT || enumAction == EnumAction.DRINK;
      if (flag) {
         consumer.accept(0.0F);
      }

      this.applyTransform(comboSetting, f -> {
         if (!flag) {
            consumer.accept((Float)f);
         }
      }, biConsumer, itemTransformState);
   }

   public ComboSetting getAnimations() {
      return this.animations;
   }

   public NumberSetting getHitHeight() {
      return this.hitHeight;
   }

   public record ItemTransformState(float equipProgress, float swingProgress, float pitch, float yaw, float swingSine) {
      public float getSwingProgress() {
         return this.swingProgress;
      }

      public float getPitch() {
         return this.pitch;
      }

      public float getYaw() {
         return this.yaw;
      }

      public float getEquipProgress() {
         return this.equipProgress;
      }

      public float getSwingSine() {
         return this.swingSine;
      }
   }
}
