package net.minecraft.client.renderer.tileentity;

import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;
import wtf.novoline.utils.render.RenderUtils;

public class TileEntityBeaconRenderer extends TileEntitySpecialRenderer<TileEntityBeacon> {
   private static final ResourceLocation beaconBeam = new ResourceLocation("textures/entity/beacon_beam.png");

   @Override
   public boolean forceTileEntityRender() {
      return true;
   }

   public void renderTileEntityAt(TileEntityBeacon tileEntityBeacon, double d, double d2, double d3, float f, int unused5) {
      float beamRender = tileEntityBeacon.shouldBeamRender();
      if (beamRender > 0.0) {
         if (Config.isShaders()) {
            Shaders.run();
         }

         GlStateManager.alphaFunc(516, 0.1F);
         if (beamRender > 0.0F) {
            Tessellator instance = Tessellator.getInstance();
            WorldRenderer worldRenderer = instance.getWorldRenderer();
            GlStateManager.disableFog();
            List beamSegments = tileEntityBeacon.getBeamSegments();
            int i = 0;

            for (int i2 = 0; i2 < beamSegments.size(); i2++) {
               net.minecraft.tileentity.TileEntityBeacon.BeamSegment beamSegment = (net.minecraft.tileentity.TileEntityBeacon.BeamSegment)beamSegments.get(i2);
               int i3 = i + beamSegment.getHeight();
               this.bindTexture(beaconBeam);
               GL11.glTexParameterf(3553, 10242, 10497.0F);
               GL11.glTexParameterf(3553, 10243, 10497.0F);
               GlStateManager.disableLighting();
               GlStateManager.disableCull();
               GlStateManager.disableBlend();
               GlStateManager.depthMask(true);
               GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
               double d4 = (double)tileEntityBeacon.getWorld().getTotalWorldTime() + f;
               double d5 = MathHelper.frac(-d4 * 0.2 - MathHelper.floor_double(-d4 * 0.1));
               float f2 = beamSegment.getColors()[0];
               float f3 = beamSegment.getColors()[1];
               float f4 = beamSegment.getColors()[2];
               double d6 = d4 * 0.025 * -1.5;
               double d7 = 0.5 + Math.cos(d6 + (Math.PI * 3.0 / 4.0)) * 0.2;
               double d8 = 0.5 + Math.sin(d6 + (Math.PI * 3.0 / 4.0)) * 0.2;
               double d9 = 0.5 + Math.cos(d6 + (Math.PI / 4)) * 0.2;
               double d10 = 0.5 + Math.sin(d6 + (Math.PI / 4)) * 0.2;
               double d11 = 0.5 + Math.cos(d6 + (Math.PI * 5.0 / 4.0)) * 0.2;
               double d12 = 0.5 + Math.sin(d6 + (Math.PI * 5.0 / 4.0)) * 0.2;
               double d13 = 0.5 + Math.cos(d6 + (Math.PI * 7.0 / 4.0)) * 0.2;
               double d14 = 0.5 + Math.sin(d6 + (Math.PI * 7.0 / 4.0)) * 0.2;
               double d15 = -1.0 + d5;
               double d16 = beamSegment.getHeight() * beamRender * 2.5 + d15;
               worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
               worldRenderer.pos(d + d7, d2 + i3, d3 + d8).tex(1.0, d16).color(f2, f3, f4, 1.0F).endVertex();
               worldRenderer.pos(d + d7, d2 + i, d3 + d8).tex(1.0, d15).color(f2, f3, f4, 1.0F).endVertex();
               worldRenderer.pos(d + d9, d2 + i, d3 + d10).tex(0.0, d15).color(f2, f3, f4, 1.0F).endVertex();
               worldRenderer.pos(d + d9, d2 + i3, d3 + d10).tex(0.0, d16).color(f2, f3, f4, 1.0F).endVertex();
               worldRenderer.pos(d + d13, d2 + i3, d3 + d14).tex(1.0, d16).color(f2, f3, f4, 1.0F).endVertex();
               worldRenderer.pos(d + d13, d2 + i, d3 + d14).tex(1.0, d15).color(f2, f3, f4, 1.0F).endVertex();
               worldRenderer.pos(d + d11, d2 + i, d3 + d12).tex(0.0, d15).color(f2, f3, f4, 1.0F).endVertex();
               worldRenderer.pos(d + d11, d2 + i3, d3 + d12).tex(0.0, d16).color(f2, f3, f4, 1.0F).endVertex();
               worldRenderer.pos(d + d9, d2 + i3, d3 + d10).tex(1.0, d16).color(f2, f3, f4, 1.0F).endVertex();
               worldRenderer.pos(d + d9, d2 + i, d3 + d10).tex(1.0, d15).color(f2, f3, f4, 1.0F).endVertex();
               worldRenderer.pos(d + d13, d2 + i, d3 + d14).tex(0.0, d15).color(f2, f3, f4, 1.0F).endVertex();
               worldRenderer.pos(d + d13, d2 + i3, d3 + d14).tex(0.0, d16).color(f2, f3, f4, 1.0F).endVertex();
               worldRenderer.pos(d + d11, d2 + i3, d3 + d12).tex(1.0, d16).color(f2, f3, f4, 1.0F).endVertex();
               worldRenderer.pos(d + d11, d2 + i, d3 + d12).tex(1.0, d15).color(f2, f3, f4, 1.0F).endVertex();
               worldRenderer.pos(d + d7, d2 + i, d3 + d8).tex(0.0, d15).color(f2, f3, f4, 1.0F).endVertex();
               worldRenderer.pos(d + d7, d2 + i3, d3 + d8).tex(0.0, d16).color(f2, f3, f4, 1.0F).endVertex();
               instance.draw();
               GlStateManager.enableBlend();
               RenderUtils.applyBlendFunc();
               GlStateManager.depthMask(false);
               double d17 = -1.0 + d5;
               double d18 = beamSegment.getHeight() * beamRender + d17;
               worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
               worldRenderer.pos(d + 0.2, d2 + i3, d3 + 0.2).tex(1.0, d18).color(f2, f3, f4, 0.125F).endVertex();
               worldRenderer.pos(d + 0.2, d2 + i, d3 + 0.2).tex(1.0, d17).color(f2, f3, f4, 0.125F).endVertex();
               worldRenderer.pos(d + 0.8, d2 + i, d3 + 0.2).tex(0.0, d17).color(f2, f3, f4, 0.125F).endVertex();
               worldRenderer.pos(d + 0.8, d2 + i3, d3 + 0.2).tex(0.0, d18).color(f2, f3, f4, 0.125F).endVertex();
               worldRenderer.pos(d + 0.8, d2 + i3, d3 + 0.8).tex(1.0, d18).color(f2, f3, f4, 0.125F).endVertex();
               worldRenderer.pos(d + 0.8, d2 + i, d3 + 0.8).tex(1.0, d17).color(f2, f3, f4, 0.125F).endVertex();
               worldRenderer.pos(d + 0.2, d2 + i, d3 + 0.8).tex(0.0, d17).color(f2, f3, f4, 0.125F).endVertex();
               worldRenderer.pos(d + 0.2, d2 + i3, d3 + 0.8).tex(0.0, d18).color(f2, f3, f4, 0.125F).endVertex();
               worldRenderer.pos(d + 0.8, d2 + i3, d3 + 0.2).tex(1.0, d18).color(f2, f3, f4, 0.125F).endVertex();
               worldRenderer.pos(d + 0.8, d2 + i, d3 + 0.2).tex(1.0, d17).color(f2, f3, f4, 0.125F).endVertex();
               worldRenderer.pos(d + 0.8, d2 + i, d3 + 0.8).tex(0.0, d17).color(f2, f3, f4, 0.125F).endVertex();
               worldRenderer.pos(d + 0.8, d2 + i3, d3 + 0.8).tex(0.0, d18).color(f2, f3, f4, 0.125F).endVertex();
               worldRenderer.pos(d + 0.2, d2 + i3, d3 + 0.8).tex(1.0, d18).color(f2, f3, f4, 0.125F).endVertex();
               worldRenderer.pos(d + 0.2, d2 + i, d3 + 0.8).tex(1.0, d17).color(f2, f3, f4, 0.125F).endVertex();
               worldRenderer.pos(d + 0.2, d2 + i, d3 + 0.2).tex(0.0, d17).color(f2, f3, f4, 0.125F).endVertex();
               worldRenderer.pos(d + 0.2, d2 + i3, d3 + 0.2).tex(0.0, d18).color(f2, f3, f4, 0.125F).endVertex();
               instance.draw();
               GlStateManager.enableLighting();
               GlStateManager.enableTexture2D();
               GlStateManager.depthMask(true);
               i = i3;
            }

            GlStateManager.enableFog();
         }

         if (Config.isShaders()) {
            Shaders.run2();
         }
      }
   }
}
