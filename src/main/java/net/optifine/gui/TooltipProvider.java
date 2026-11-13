package net.optifine.gui;

import java.awt.Rectangle;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public interface TooltipProvider {
   boolean isRenderBorder();

   String[] getTooltipLines(GuiButton guiButton, int i);

   Rectangle getRectangle(GuiScreen guiScreen, int i);
}
