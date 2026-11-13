package net.minecraft.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import wtf.novoline.utils.render.RenderUtils;

public class Gui {
   public static final ResourceLocation optionsBackground = new ResourceLocation("textures/gui/options_background.png");
   public static final ResourceLocation statIcons = new ResourceLocation("textures/gui/container/stats_icons.png");
   protected float zLevel;
   public static final ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");

   public void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV) {
      float f = 0.00390625F;
      float f2 = 0.00390625F;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(xCoord + 0.0F, yCoord + maxV, this.zLevel).tex((minU + 0) * f, (minV + maxV) * f2).endVertex();
      worldRenderer.pos(xCoord + maxU, yCoord + maxV, this.zLevel).tex((minU + maxU) * f, (minV + maxV) * f2).endVertex();
      worldRenderer.pos(xCoord + maxU, yCoord + 0.0F, this.zLevel).tex((minU + maxU) * f, (minV + 0) * f2).endVertex();
      worldRenderer.pos(xCoord + 0.0F, yCoord + 0.0F, this.zLevel).tex((minU + 0) * f, (minV + 0) * f2).endVertex();
      tessellator.draw();
   }

   public void handleInt4(int i, int i2, TextureAtlasSprite textureAtlasSprite) {
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer2 = instance.getWorldRenderer();
      worldRenderer2.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer2.pos(i + 0, i2 + 16, this.zLevel).tex(textureAtlasSprite.getMinU(), textureAtlasSprite.getMaxV()).endVertex();
      worldRenderer2.pos(i + 16, i2 + 16, this.zLevel).tex(textureAtlasSprite.getMaxU(), textureAtlasSprite.getMaxV()).endVertex();
      worldRenderer2.pos(i + 16, i2 + 0, this.zLevel).tex(textureAtlasSprite.getMaxU(), textureAtlasSprite.getMinV()).endVertex();
      worldRenderer2.pos(i + 0, i2 + 0, this.zLevel).tex(textureAtlasSprite.getMinU(), textureAtlasSprite.getMinV()).endVertex();
      instance.draw();
   }

   public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
      fontRendererIn.drawStringWithShadow(text, x - fontRendererIn.getInt3(text) / 2, y, color);
   }

   public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight) {
      float f = 1.0F / textureWidth;
      float f2 = 1.0F / textureHeight;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(x, y + height, 0.0).tex(u * f, (v + height) * f2).endVertex();
      worldRenderer.pos(x + width, y + height, 0.0).tex((u + width) * f, (v + height) * f2).endVertex();
      worldRenderer.pos(x + width, y, 0.0).tex((u + width) * f, v * f2).endVertex();
      worldRenderer.pos(x, y, 0.0).tex(u * f, v * f2).endVertex();
      tessellator.draw();
   }

   public static void handleInt3(int i, int i2, float f, float f2, int i3, int i4, int i5, float f3, float f4) {
      float f5 = 1.0F / f3;
      float f6 = 1.0F / f4;
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer2 = instance.getWorldRenderer();
      worldRenderer2.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer2.pos(i, i2 + i5, 0.0).tex(f * f5, (f2 + i3) * f6).endVertex();
      worldRenderer2.pos(i + i4, i2 + i5, 0.0).tex((f + 8) * f5, (f2 + i3) * f6).endVertex();
      worldRenderer2.pos(i + i4, i2, 0.0).tex((f + 8) * f5, f2 * f6).endVertex();
      worldRenderer2.pos(i, i2, 0.0).tex(f * f5, f2 * f6).endVertex();
      instance.draw();
   }

   protected void drawHorizontalLine(int startX, int endX, int y, int color) {
      if (endX < startX) {
         int startX2 = startX;
         startX = endX;
         endX = startX2;
      }

      handleInt(startX, y, endX + 1, y + 1, color);
   }

   protected void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
      float f = (startColor >> 24 & 0xFF) / 255.0F;
      float f2 = (startColor >> 16 & 0xFF) / 255.0F;
      float f3 = (startColor >> 8 & 0xFF) / 255.0F;
      float f4 = (startColor & 0xFF) / 255.0F;
      float f5 = (endColor >> 24 & 0xFF) / 255.0F;
      float f6 = (endColor >> 16 & 0xFF) / 255.0F;
      float f7 = (endColor >> 8 & 0xFF) / 255.0F;
      float f8 = (endColor & 0xFF) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      RenderUtils.applyBlendFunc();
      GlStateManager.shadeModel(7425);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(right, top, this.zLevel).color(f2, f3, f4, f).endVertex();
      worldRenderer.pos(left, top, this.zLevel).color(f2, f3, f4, f).endVertex();
      worldRenderer.pos(left, bottom, this.zLevel).color(f6, f7, f8, f5).endVertex();
      worldRenderer.pos(right, bottom, this.zLevel).color(f6, f7, f8, f5).endVertex();
      tessellator.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
      float f = 0.00390625F;
      float f2 = 0.00390625F;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(x + 0, y + height, this.zLevel).tex((textureX + 0) * f, (textureY + height) * f2).endVertex();
      worldRenderer.pos(x + width, y + height, this.zLevel).tex((textureX + width) * f, (textureY + height) * f2).endVertex();
      worldRenderer.pos(x + width, y + 0, this.zLevel).tex((textureX + width) * f, (textureY + 0) * f2).endVertex();
      worldRenderer.pos(x + 0, y + 0, this.zLevel).tex((textureX + 0) * f, (textureY + 0) * f2).endVertex();
      tessellator.draw();
   }

   public void drawString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
      fontRendererIn.drawStringWithShadow(text, x, y, color);
   }

   public static void handleInt(int i, int i2, int i3, int i4, int i5) {
      if (i < i3) {
         int i6 = i;
         i = i3;
         i3 = i6;
      }

      if (i2 < i4) {
         int i7 = i2;
         i2 = i4;
         i4 = i7;
      }

      float f = (i5 >> 24 & 0xFF) / 255.0F;
      float f2 = (i5 >> 16 & 0xFF) / 255.0F;
      float f3 = (i5 >> 8 & 0xFF) / 255.0F;
      float f4 = (i5 & 0xFF) / 255.0F;
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer2 = instance.getWorldRenderer();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      RenderUtils.applyBlendFunc();
      GlStateManager.color(f2, f3, f4, f);
      worldRenderer2.begin(7, DefaultVertexFormats.POSITION);
      worldRenderer2.pos(i, i4, 0.0).endVertex();
      worldRenderer2.pos(i3, i4, 0.0).endVertex();
      worldRenderer2.pos(i3, i2, 0.0).endVertex();
      worldRenderer2.pos(i, i2, 0.0).endVertex();
      instance.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   protected void drawVerticalLine(int x, int startY, int endY, int color) {
      if (endY < startY) {
         int startY2 = startY;
         startY = endY;
         endY = startY2;
      }

      handleInt(x, startY + 1, x + 1, endY, color);
   }
}
