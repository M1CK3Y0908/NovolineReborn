package wtf.novoline.utils.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import wtf.novoline.utils.render.shape.IRectRenderer;

public class TrueTypeFont {
   public float imgSize = 1110.0F;
   protected TrueTypeFont.Helper[] helperArray = new TrueTypeFont.Helper[1110];
   protected int lineHeight = -1;
   protected int glyphSpacing = 0;
   protected Font font;
   protected boolean flag;
   protected boolean flag2;
   public DynamicTexture dynamicTexture;

   public boolean isFlag2() {
      return this.flag2;
   }

   public Font getFont() {
      return this.font;
   }

   public void addGlyphRect(IRectRenderer iRectRenderer, TrueTypeFont.Helper[] helpers, char c, float f, float f2, int i) {
      if (c < helpers.length) {
         this.drawGlyphRect(
            iRectRenderer, f, f2, helpers[c].width, helpers[c].height, helpers[c].atlasX, helpers[c].atlasY, helpers[c].width, helpers[c].height, i
         );
      }
   }

   protected BufferedImage getBufferedImage(Font font2, TrueTypeFont.Helper[] helpers) {
      int i = (int)this.imgSize;
      BufferedImage bufferedImage = new BufferedImage(i, i, 2);
      Graphics2D graphics2D = (Graphics2D)bufferedImage.getGraphics();
      graphics2D.setFont(font2);
      graphics2D.setColor(new Color(255, 255, 255, 0));
      graphics2D.fillRect(0, 0, i, i);
      graphics2D.setColor(Color.WHITE);
      graphics2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
      graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      FontMetrics fontMetrics = graphics2D.getFontMetrics();
      int i2 = 0;
      int i3 = 0;
      int i4 = 1;

      for (int i5 = 0; i5 < helpers.length; i5++) {
         char c = (char)i5;
         TrueTypeFont.Helper helper = new TrueTypeFont.Helper();
         Rectangle2D stringBounds = fontMetrics.getStringBounds(String.valueOf(c), graphics2D);
         helper.width = stringBounds.getBounds().width + 8;
         helper.height = stringBounds.getBounds().height;
         if (i3 + helper.width >= i) {
            i3 = 0;
            i4 += i2;
            i2 = 0;
         }

         if (helper.height > i2) {
            i2 = helper.height;
         }

         helper.atlasX = i3;
         helper.atlasY = i4;
         if (helper.height > this.lineHeight) {
            this.lineHeight = helper.height;
         }

         helpers[i5] = helper;
         graphics2D.drawString(String.valueOf(c), i3 + 2, i4 + fontMetrics.getAscent());
         i3 += helper.width;
      }

      return bufferedImage;
   }

   public TrueTypeFont(Font font2) {
      this.font = font2;
      this.flag = true;
      this.flag2 = true;
      this.dynamicTexture = this.getDynamicTexture(font2, this.helperArray);
   }

   public boolean isFlag() {
      return this.flag;
   }

   public int getHeight() {
      return (this.lineHeight - 8) / 2;
   }

   protected void drawGlyphRect(IRectRenderer iRectRenderer, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, int i) {
      iRectRenderer.drawTexturedRect(f, f2, f3, f4, f5, f6, f7, f8, i);
   }

   public int getStringHeight(String text) {
      return this.getHeight();
   }

   protected DynamicTexture getDynamicTexture(Font font2, TrueTypeFont.Helper[] helpers) {
      try {
         return new DynamicTexture(this.getBufferedImage(font2, helpers));
      } catch (Exception exception) {
         exception.printStackTrace();
         return null;
      }
   }

   protected void addGlyphQuad(
      WorldRenderer worldRenderer,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      float f9,
      float f10,
      float f11,
      float f12
   ) {
      float f13 = f5 / this.imgSize;
      float f14 = f6 / this.imgSize;
      float f15 = f7 / this.imgSize;
      float f16 = f8 / this.imgSize;
      worldRenderer.pos(f + f3, f2, 0.0).tex(f13 + f15, f14).color(f9, f10, f11, f12).endVertex();
      worldRenderer.pos(f, f2, 0.0).tex(f13, f14).color(f9, f10, f11, f12).endVertex();
      worldRenderer.pos(f, f2 + f4, 0.0).tex(f13, f14 + f16).color(f9, f10, f11, f12).endVertex();
      worldRenderer.pos(f, f2 + f4, 0.0).tex(f13, f14 + f16).color(f9, f10, f11, f12).endVertex();
      worldRenderer.pos(f + f3, f2 + f4, 0.0).tex(f13 + f15, f14 + f16).color(f9, f10, f11, f12).endVertex();
      worldRenderer.pos(f + f3, f2, 0.0).tex(f13 + f15, f14).color(f9, f10, f11, f12).endVertex();
   }

   public int getStringWidth(String text) {
      int i = 0;

      for (char c : text.toCharArray()) {
         if (c < this.helperArray.length && c >= 0) {
            i += this.helperArray[c].width - 8 + this.glyphSpacing;
         }
      }

      return i / 2;
   }

   public void addGlyph(WorldRenderer worldRenderer, TrueTypeFont.Helper[] helpers, char c, float f, float f2, float f3, float f4, float f5, float f6) throws ArrayIndexOutOfBoundsException {
      try {
         this.addGlyphQuad(
            worldRenderer,
            f,
            f2,
            helpers[c].width,
            helpers[c].height,
            helpers[c].atlasX,
            helpers[c].atlasY,
            helpers[c].width,
            helpers[c].height,
            f3,
            f4,
            f5,
            f6
         );
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public class Helper {
      public int width;
      public int atlasX;
      public int height;
      public int atlasY;

      protected Helper() {
      }
   }
}
