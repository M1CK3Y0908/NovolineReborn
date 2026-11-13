package wtf.novoline.gui;

import net.minecraft.client.gui.GuiButton;

public class ButtonUtils {
   public static boolean containsMouse(GuiButton guiButton, int i, int i2) {
      return i >= guiButton.xPosition && i2 >= guiButton.yPosition && i < guiButton.xPosition + guiButton.getButtonWidth() && i2 < guiButton.yPosition + guiButton.getHeight();
   }
}
