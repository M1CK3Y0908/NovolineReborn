package wtf.novoline.gui;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.utils.render.CFontRenderer;
import wtf.novoline.utils.render.RenderUtils;

public class ColorButton extends GuiButton {
   private final Color normalColor;
   private final Color hoverColor;
   private static final CFontRenderer cFontRenderer = Fonts.rubikRegularSize14;
   private int currentColor;

   public ColorButton(int i, int i2, int i3, String text, Color color) {
      super(i, i2, i3, cFontRenderer.getStringWidth(text), cFontRenderer.getHeight(), text);
      this.normalColor = color;
      this.hoverColor = color.darker();
      this.currentColor = color.getRGB();
   }

   @Override
   public void drawButton(Minecraft minecraft2, int i, int i2) {
      cFontRenderer.drawString(this.displayString, this.xPosition, this.yPosition, this.currentColor);
      this.hovered = ButtonUtils.containsMouse(this, i, i2);
      if (this.hovered) {
         this.currentColor = RenderUtils.smoothBlendColors(this.currentColor, this.hoverColor.getRGB(), 10.0F);
      } else {
         this.currentColor = RenderUtils.smoothBlendColors(this.currentColor, this.normalColor.getRGB(), 10.0F);
      }
   }
}
