package net.minecraft.client.renderer.tileentity;

import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.GlStateManager.SType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.ResourceLocation;
import net.optifine.shaders.ShadersRender;

public class TileEntityEndPortalRenderer extends TileEntitySpecialRenderer<TileEntityEndPortal> {
   private static final ResourceLocation END_SKY_TEXTURE = new ResourceLocation("textures/environment/end_sky.png");
   private static final ResourceLocation END_PORTAL_TEXTURE = new ResourceLocation("textures/entity/end_portal.png");
   private static final Random RANDOM = new Random(31100L);
   FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);

   public void renderTileEntityAt(TileEntityEndPortal tileEntityEndPortal, double d, double d2, double d3, float f, int unused5) {
      if (!Config.isShaders() || !ShadersRender.checkTileEntityEndPortal(tileEntityEndPortal, d, d2, d3, f, 0.75F)) {
         float f2 = (float)this.rendererDispatcher.entityX;
         float f3 = (float)this.rendererDispatcher.entityY;
         float f4 = (float)this.rendererDispatcher.entityZ;
         GlStateManager.disableLighting();
         RANDOM.setSeed(31100L);
         float f5 = 0.75F;

         for (int i = 0; i < 16; i++) {
            GlStateManager.pushMatrix();
            float f6 = 16 - i;
            float f7 = 0.0625F;
            float f8 = 1.0F / (f6 + 1.0F);
            if (i == 0) {
               this.bindTexture(END_SKY_TEXTURE);
               f8 = 0.1F;
               f6 = 65.0F;
               f7 = 0.125F;
               GlStateManager.enableBlend();
               GlStateManager.blendFunc(770, 771);
            }

            if (i >= 1) {
               this.bindTexture(END_PORTAL_TEXTURE);
            }

            if (i == 1) {
               GlStateManager.enableBlend();
               GlStateManager.blendFunc(1, 1);
               f7 = 0.5F;
            }

            float f9 = (float)(-(d2 + f5));
            float f10 = f9 + (float)ActiveRenderInfo.getPosition().yCoord;
            float f11 = f9 + f6 + (float)ActiveRenderInfo.getPosition().yCoord;
            float f12 = f10 / f11;
            f12 = (float)(d2 + f5) + f12;
            GlStateManager.translate(f2, f12, f4);
            GlStateManager.texGen(SType.S, 9217);
            GlStateManager.texGen(SType.T, 9217);
            GlStateManager.texGen(SType.R, 9217);
            GlStateManager.texGen(SType.Q, 9216);
            GlStateManager.texGen(SType.S, 9473, this.getFloatBuffer(1.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGen(SType.T, 9473, this.getFloatBuffer(0.0F, 0.0F, 1.0F, 0.0F));
            GlStateManager.texGen(SType.R, 9473, this.getFloatBuffer(0.0F, 0.0F, 0.0F, 1.0F));
            GlStateManager.texGen(SType.Q, 9474, this.getFloatBuffer(0.0F, 1.0F, 0.0F, 0.0F));
            GlStateManager.handleSType2(SType.S);
            GlStateManager.handleSType2(SType.T);
            GlStateManager.handleSType2(SType.R);
            GlStateManager.handleSType2(SType.Q);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, (float)(Minecraft.getSystemTime() % 700000L) / 700000.0F, 0.0F);
            GlStateManager.scale(f7, f7, f7);
            GlStateManager.translate(0.5F, 0.5F, 0.0F);
            GlStateManager.rotate((i * i * 4321 + i * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(-0.5F, -0.5F, 0.0F);
            GlStateManager.translate(-f2, -f4, -f3);
            f10 = f9 + (float)ActiveRenderInfo.getPosition().yCoord;
            GlStateManager.translate(
               (float)ActiveRenderInfo.getPosition().xCoord * f6 / f10, (float)ActiveRenderInfo.getPosition().zCoord * f6 / f10, -f3
            );
            Tessellator instance = Tessellator.getInstance();
            WorldRenderer worldRenderer = instance.getWorldRenderer();
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            float f13 = (RANDOM.nextFloat() * 0.5F + 0.1F) * f8;
            float f14 = (RANDOM.nextFloat() * 0.5F + 0.4F) * f8;
            float f15 = (RANDOM.nextFloat() * 0.5F + 0.5F) * f8;
            if (i == 0) {
               f13 = f14 = f15 = 1.0F * f8;
            }

            worldRenderer.pos(d, d2 + f5, d3).color(f13, f14, f15, 1.0F).endVertex();
            worldRenderer.pos(d, d2 + f5, d3 + 1.0).color(f13, f14, f15, 1.0F).endVertex();
            worldRenderer.pos(d + 1.0, d2 + f5, d3 + 1.0).color(f13, f14, f15, 1.0F).endVertex();
            worldRenderer.pos(d + 1.0, d2 + f5, d3).color(f13, f14, f15, 1.0F).endVertex();
            instance.draw();
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
            this.bindTexture(END_SKY_TEXTURE);
         }

         GlStateManager.disableBlend();
         GlStateManager.handleSType(SType.S);
         GlStateManager.handleSType(SType.T);
         GlStateManager.handleSType(SType.R);
         GlStateManager.handleSType(SType.Q);
         GlStateManager.enableLighting();
      }
   }

   private FloatBuffer getFloatBuffer(float value1, float value2, float value3, float value4) {
      this.buffer.clear();
      this.buffer.put(value1).put(value2).put(value3).put(value4);
      this.buffer.flip();
      return this.buffer;
   }
}
