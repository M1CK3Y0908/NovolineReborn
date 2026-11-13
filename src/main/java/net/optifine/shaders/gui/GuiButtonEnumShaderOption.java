package net.optifine.shaders.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.config.EnumShaderOption;

public class GuiButtonEnumShaderOption extends GuiButton {
   private EnumShaderOption enumShaderOption = null;

   public EnumShaderOption getEnumShaderOption() {
      return this.enumShaderOption;
   }

   public void updateButtonText() {
      this.displayString = getButtonText(this.enumShaderOption);
   }

   private static String getButtonText(EnumShaderOption eso) {
      String text = I18n.format(eso.getResourceKey()) + ": ";
      switch (eso) {
         case ANTIALIASING:
            return text + GuiShaders.toStringAa(Shaders.configAntialiasingLevel);
         case NORMAL_MAP:
            return text + GuiShaders.toStringOnOff(Shaders.configNormalMap);
         case SPECULAR_MAP:
            return text + GuiShaders.toStringOnOff(Shaders.configSpecularMap);
         case RENDER_RES_MUL:
            return text + GuiShaders.toStringQuality(Shaders.configRenderResMul);
         case SHADOW_RES_MUL:
            return text + GuiShaders.toStringQuality(Shaders.configShadowResMul);
         case HAND_DEPTH_MUL:
            return text + GuiShaders.toStringHandDepth(Shaders.configHandDepthMul);
         case CLOUD_SHADOW:
            return text + GuiShaders.toStringOnOff(Shaders.configCloudShadow);
         case OLD_HAND_LIGHT:
            return text + Shaders.configOldHandLight.getUserValue();
         case OLD_LIGHTING:
            return text + Shaders.configOldLighting.getUserValue();
         case SHADOW_CLIP_FRUSTRUM:
            return text + GuiShaders.toStringOnOff(Shaders.configShadowClipFrustrum);
         case TWEAK_BLOCK_DAMAGE:
            return text + GuiShaders.toStringOnOff(Shaders.configTweakBlockDamage);
         default:
            return text + Shaders.getEnumShaderOption(eso);
      }
   }

   public GuiButtonEnumShaderOption(EnumShaderOption enumShaderOption2, int i, int i2) {
      super(enumShaderOption2.ordinal(), i, i2, 120, 20, getButtonText(enumShaderOption2));
      this.enumShaderOption = enumShaderOption2;
   }
}
