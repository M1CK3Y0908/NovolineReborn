package net.optifine.gui;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;

public class GuiScreenOF extends GuiScreen {
   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      if (mouseButton == 1) {
         GuiButton guiButton = getSelectedButton(mouseX, mouseY, this.buttonList);
         if (guiButton != null && guiButton.enabled) {
            guiButton.playPressSound(this.mc.getSoundHandler());
            this.actionPerformedRightClick(guiButton);
         }
      }
   }

   public static GuiButton getSelectedButton(int x, int y, List<GuiButton> listButtons) {
      for (int index = 0; index < listButtons.size(); index++) {
         GuiButton guiButton = listButtons.get(index);
         if (guiButton.visible) {
            int buttonWidth = GuiVideoSettings.getButtonWidth(guiButton);
            int buttonHeight = GuiVideoSettings.getButtonHeight(guiButton);
            if (x >= guiButton.xPosition && y >= guiButton.yPosition && x < guiButton.xPosition + buttonWidth && y < guiButton.yPosition + buttonHeight) {
               return guiButton;
            }
         }
      }

      return null;
   }

   protected void actionPerformedRightClick(GuiButton button) throws IOException {
   }
}
