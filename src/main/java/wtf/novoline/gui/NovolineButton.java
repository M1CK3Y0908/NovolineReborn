package wtf.novoline.gui;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.render.BlurRenderer;
import wtf.novoline.utils.render.FilledPolygonRenderer;
import wtf.novoline.utils.render.RoundedPolygonRenderer;

public class NovolineButton extends GuiButton {
   private final BlurRenderer blurRenderer;
   private float hoverProgress;

   public static boolean containsMouse(GuiButton guiButton, int i, int i2) {
      return i >= guiButton.xPosition && i2 >= guiButton.yPosition && i < guiButton.xPosition + guiButton.getButtonWidth() && i2 < guiButton.yPosition + guiButton.getHeight();
   }

   public NovolineButton(BlurRenderer blurRenderer2, int i, int i2, int i3, int i4, int i5, String text) {
      super(i, i2, i3, i4, i5, text);
      this.blurRenderer = blurRenderer2;
   }

   public NovolineButton(BlurRenderer blurRenderer2, int i, int i2, int i3, String text) {
      super(i, i2, i3, text);
      this.blurRenderer = blurRenderer2;
   }

   @Override
   public void drawButton(Minecraft minecraft2, int i, int i2) {
      ScaledResolution scaledResolution = new ScaledResolution(minecraft2);
      RoundedPolygonRenderer roundedPolygonRenderer = new RoundedPolygonRenderer((float)scaledResolution.getScaledWidth_double(), (float)scaledResolution.getScaledHeight_double());
      this.blurRenderer.bindResultTexture();
      roundedPolygonRenderer.putRoundedRect(this.xPosition, this.yPosition, this.width, this.height, 5.0F, 0);
      roundedPolygonRenderer.draw();
      FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
      filledPolygonRenderer.putRoundedRect(this.xPosition, this.yPosition, this.width, this.height, 5.0F, new Color(0, 0, 0, 85 - (int)this.hoverProgress * 10).getRGB());
      filledPolygonRenderer.draw();
      Fonts.rubikSemiboldSize16
         .drawStringWithShadow(
            this.displayString,
            this.xPosition + this.width / 2.0F - Fonts.rubikSemiboldSize16.getStringWidth(this.displayString) / 2.0F,
            this.yPosition + this.height / 2.0F - Fonts.rubikSemiboldSize16.getHeight() / 2.0F + 1.0F,
            -1
         );
      this.hovered = containsMouse(this, i, i2);
      if (this.isMouseOver()) {
         if (this.hoverProgress < 5.0F) {
            this.hoverProgress = SmoothingUtils.approach(this.hoverProgress, 5.0F, 20.0F);
         }
      } else if (this.hoverProgress > 0.0F) {
         this.hoverProgress = SmoothingUtils.approach(this.hoverProgress, 0.0F, 20.0F);
      }
   }
}
