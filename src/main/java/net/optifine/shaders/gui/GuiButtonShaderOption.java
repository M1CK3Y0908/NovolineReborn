package net.optifine.shaders.gui;

import net.minecraft.client.gui.GuiButton;
import net.optifine.shaders.config.ShaderOption;

public class GuiButtonShaderOption extends GuiButton {
   private ShaderOption shaderOption = null;

   public boolean isSwitchable() {
      return true;
   }

   public GuiButtonShaderOption(int buttonId, int x, int y, int widthIn, int heightIn, ShaderOption shaderOption, String text) {
      super(buttonId, x, y, widthIn, heightIn, text);
      this.shaderOption = shaderOption;
   }

   public ShaderOption getShaderOption() {
      return this.shaderOption;
   }

   public void valueChanged() {
   }
}
