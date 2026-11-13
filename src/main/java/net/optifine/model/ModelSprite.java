package net.optifine.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelSprite {
   private ModelRenderer modelRenderer = null;
   private int textureOffsetX = 0;
   private int textureOffsetY = 0;
   private float posX = 0.0F;
   private float posY = 0.0F;
   private float posZ = 0.0F;
   private int sizeX = 0;
   private int sizeY = 0;
   private int sizeZ = 0;
   private float sizeAdd = 0.0F;
   private float minU = 0.0F;
   private float minV = 0.0F;
   private float maxU = 0.0F;
   private float maxV = 0.0F;

   public ModelSprite(
      ModelRenderer modelRenderer, int textureOffsetX, int textureOffsetY, float posX, float posY, float posZ, int sizeX, int sizeY, int sizeZ, float sizeAdd
   ) {
      this.modelRenderer = modelRenderer;
      this.textureOffsetX = textureOffsetX;
      this.textureOffsetY = textureOffsetY;
      this.posX = posX;
      this.posY = posY;
      this.posZ = posZ;
      this.sizeX = sizeX;
      this.sizeY = sizeY;
      this.sizeZ = sizeZ;
      this.sizeAdd = sizeAdd;
      this.minU = textureOffsetX / modelRenderer.textureWidth;
      this.minV = textureOffsetY / modelRenderer.textureHeight;
      this.maxU = (textureOffsetX + sizeX) / modelRenderer.textureWidth;
      this.maxV = (textureOffsetY + sizeY) / modelRenderer.textureHeight;
   }

   public static void renderItemIn2D(
      Tessellator tess, float minU, float minV, float maxU, float maxV, int sizeX, int sizeY, float width, float texWidth, float texHeight
   ) {
      if (width < 6.25E-4F) {
         width = 6.25E-4F;
      }

      float f = maxU - minU;
      float f2 = maxV - minV;
      double d = MathHelper.abs(f) * (texWidth / 16.0F);
      double d2 = MathHelper.abs(f2) * (texHeight / 16.0F);
      WorldRenderer worldRenderer = tess.getWorldRenderer();
      GL11.glNormal3f(0.0F, 0.0F, -1.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(0.0, d2, 0.0).tex(minU, maxV).endVertex();
      worldRenderer.pos(d, d2, 0.0).tex(maxU, maxV).endVertex();
      worldRenderer.pos(d, 0.0, 0.0).tex(maxU, minV).endVertex();
      worldRenderer.pos(0.0, 0.0, 0.0).tex(minU, minV).endVertex();
      tess.draw();
      GL11.glNormal3f(0.0F, 0.0F, 1.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(0.0, 0.0, width).tex(minU, minV).endVertex();
      worldRenderer.pos(d, 0.0, width).tex(maxU, minV).endVertex();
      worldRenderer.pos(d, d2, width).tex(maxU, maxV).endVertex();
      worldRenderer.pos(0.0, d2, width).tex(minU, maxV).endVertex();
      tess.draw();
      float f3 = 0.5F * f / sizeX;
      float f4 = 0.5F * f2 / sizeY;
      GL11.glNormal3f(-1.0F, 0.0F, 0.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

      for (int index = 0; index < sizeX; index++) {
         float f5 = (float)index / sizeX;
         float f6 = minU + f * f5 + f3;
         worldRenderer.pos(f5 * d, d2, width).tex(f6, maxV).endVertex();
         worldRenderer.pos(f5 * d, d2, 0.0).tex(f6, maxV).endVertex();
         worldRenderer.pos(f5 * d, 0.0, 0.0).tex(f6, minV).endVertex();
         worldRenderer.pos(f5 * d, 0.0, width).tex(f6, minV).endVertex();
      }

      tess.draw();
      GL11.glNormal3f(1.0F, 0.0F, 0.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

      for (int innerIndex = 0; innerIndex < sizeX; innerIndex++) {
         float f7 = (float)innerIndex / sizeX;
         float f8 = minU + f * f7 + f3;
         float f9 = f7 + 1.0F / sizeX;
         worldRenderer.pos(f9 * d, 0.0, width).tex(f8, minV).endVertex();
         worldRenderer.pos(f9 * d, 0.0, 0.0).tex(f8, minV).endVertex();
         worldRenderer.pos(f9 * d, d2, 0.0).tex(f8, maxV).endVertex();
         worldRenderer.pos(f9 * d, d2, width).tex(f8, maxV).endVertex();
      }

      tess.draw();
      GL11.glNormal3f(0.0F, 1.0F, 0.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

      for (int nestedIndex = 0; nestedIndex < sizeY; nestedIndex++) {
         float f10 = (float)nestedIndex / sizeY;
         float f11 = minV + f2 * f10 + f4;
         float f12 = f10 + 1.0F / sizeY;
         worldRenderer.pos(0.0, f12 * d2, width).tex(minU, f11).endVertex();
         worldRenderer.pos(d, f12 * d2, width).tex(maxU, f11).endVertex();
         worldRenderer.pos(d, f12 * d2, 0.0).tex(maxU, f11).endVertex();
         worldRenderer.pos(0.0, f12 * d2, 0.0).tex(minU, f11).endVertex();
      }

      tess.draw();
      GL11.glNormal3f(0.0F, -1.0F, 0.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

      for (int outerIndex = 0; outerIndex < sizeY; outerIndex++) {
         float f13 = (float)outerIndex / sizeY;
         float f14 = minV + f2 * f13 + f4;
         worldRenderer.pos(d, f13 * d2, width).tex(maxU, f14).endVertex();
         worldRenderer.pos(0.0, f13 * d2, width).tex(minU, f14).endVertex();
         worldRenderer.pos(0.0, f13 * d2, 0.0).tex(minU, f14).endVertex();
         worldRenderer.pos(d, f13 * d2, 0.0).tex(maxU, f14).endVertex();
      }

      tess.draw();
   }

   public void render(Tessellator tessellator, float scale) {
      GlStateManager.translate(this.posX * scale, this.posY * scale, this.posZ * scale);
      float minU2 = this.minU;
      float maxU2 = this.maxU;
      float minV2 = this.minV;
      float maxV2 = this.maxV;
      if (this.modelRenderer.mirror) {
         minU2 = this.maxU;
         maxU2 = this.minU;
      }

      if (this.modelRenderer.mirrorV) {
         minV2 = this.maxV;
         maxV2 = this.minV;
      }

      renderItemIn2D(
         tessellator,
         minU2,
         minV2,
         maxU2,
         maxV2,
         this.sizeX,
         this.sizeY,
         scale * this.sizeZ,
         this.modelRenderer.textureWidth,
         this.modelRenderer.textureHeight
      );
      GlStateManager.translate(-this.posX * scale, -this.posY * scale, -this.posZ * scale);
   }
}
