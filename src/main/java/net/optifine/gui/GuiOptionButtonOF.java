package net.optifine.gui;

import net.minecraft.client.gui.GuiOptionButton;

public class GuiOptionButtonOF extends GuiOptionButton implements IOptionControl {
   private net.minecraft.client.settings.GameSettings.Options option = null;

   public GuiOptionButtonOF(int id, int x, int y, net.minecraft.client.settings.GameSettings.Options option, String text) {
      super(id, x, y, option, text);
      this.option = option;
   }

   @Override
   public net.minecraft.client.settings.GameSettings.Options getOption() {
      return this.option;
   }
}
