package net.optifine.gui;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class TooltipManager {
   private int lastMouseX = 0;
   private int lastMouseY = 0;
   private long mouseStillTime = 0L;
   private GuiScreen guiScreen;
   private TooltipProvider tooltipProvider;

   public void drawTooltips(int x, int y, List buttonList) {
      if (Math.abs(x - this.lastMouseX) > 5 || Math.abs(y - this.lastMouseY) > 5) {
         this.lastMouseX = x;
         this.lastMouseY = y;
         this.mouseStillTime = System.currentTimeMillis();
      } else if (System.currentTimeMillis() >= this.mouseStillTime + 700L) {
         GuiButton selectedButton = GuiScreenOF.getSelectedButton(x, y, buttonList);
         if (selectedButton != null) {
            Rectangle rectangle = this.tooltipProvider.getRectangle(this.guiScreen, y);
            String[] tooltipLines = this.tooltipProvider.getTooltipLines(selectedButton, rectangle.width);
            if (tooltipLines != null) {
               if (tooltipLines.length > 8) {
                  tooltipLines = Arrays.copyOf(tooltipLines, 8);
                  tooltipLines[tooltipLines.length - 1] = tooltipLines[tooltipLines.length - 1] + " ...";
               }

               if (this.tooltipProvider.isRenderBorder()) {
                  this.handleInt(rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y + rectangle.height);
               }

               Gui.handleInt(rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y + rectangle.height, -536870912);

               for (int i = 0; i < tooltipLines.length; i++) {
                  String text = tooltipLines[i];
                  int i2 = 14540253;
                  if (text.endsWith("!")) {
                     i2 = 16719904;
                  }

                  float f = rectangle.x + 5;
                  Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(text, f, rectangle.y + 5 + i * 11, i2);
               }
            }
         }
      }
   }

   public TooltipManager(GuiScreen guiScreen, TooltipProvider tooltipProvider) {
      this.guiScreen = guiScreen;
      this.tooltipProvider = tooltipProvider;
   }

   private void handleInt(int i, int i2, int i3, int i4) {
      Gui.handleInt(i, i2 - 1, i3, i2, -528449408);
      Gui.handleInt(i, i4, i3, i4 + 1, -528449408);
      Gui.handleInt(i - 1, i2, i, i4, -528449408);
      Gui.handleInt(i3, i2, i3 + 1, i4, -528449408);
   }
}
