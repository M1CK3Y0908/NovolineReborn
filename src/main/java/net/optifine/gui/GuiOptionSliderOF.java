package net.optifine.gui;

import net.minecraft.client.gui.GuiOptionSlider;

public class GuiOptionSliderOF extends GuiOptionSlider implements IOptionControl {
   private net.minecraft.client.settings.GameSettings.Options option = null;

   public GuiOptionSliderOF(int id, int x, int y, net.minecraft.client.settings.GameSettings.Options option) {
      super(id, x, y, option);
      this.option = option;
   }

   @Override
   public net.minecraft.client.settings.GameSettings.Options getOption() {
      return this.option;
   }
}
