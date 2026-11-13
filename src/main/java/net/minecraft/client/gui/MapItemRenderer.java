package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec4b;
import net.minecraft.world.storage.MapData;

public class MapItemRenderer {
   private static final ResourceLocation mapIcons = new ResourceLocation("textures/map/map_icons.png");
   private final Map<String, MapItemRenderer.Instance> loadedMaps = Maps.newHashMap();
   private final TextureManager textureManager;

   public void updateMapTexture(MapData mapdataIn) {
      this.getMapRendererInstance(mapdataIn).updateMapTexture();
   }

   private MapItemRenderer.Instance getMapRendererInstance(MapData mapdataIn) {
      MapItemRenderer.Instance mapitemrenderer$instance = this.loadedMaps.get(mapdataIn.mapName);
      if (mapitemrenderer$instance == null) {
         mapitemrenderer$instance = new MapItemRenderer.Instance(mapdataIn);
         this.loadedMaps.put(mapdataIn.mapName, mapitemrenderer$instance);
      }

      return mapitemrenderer$instance;
   }

   public MapItemRenderer(TextureManager textureManagerIn) {
      this.textureManager = textureManagerIn;
   }

   public void renderMap(MapData mapdataIn, boolean flag) {
      this.getMapRendererInstance(mapdataIn).render(flag);
   }

   public void clearLoadedMaps() {
      for (MapItemRenderer.Instance instance : this.loadedMaps.values()) {
         this.textureManager.deleteTexture(instance.location);
      }

      this.loadedMaps.clear();
   }

   class Instance {
      private final MapData mapData;
      private final DynamicTexture mapTexture;
      private final int[] mapTextureData;
      private final ResourceLocation location;

      private void updateMapTexture() {
         for (int index = 0; index < 16384; index++) {
            int i = this.mapData.colors[index] & 255;
            if (i / 4 == 0) {
               this.mapTextureData[index] = (index + index / 128 & 1) * 8 + 16 << 24;
            } else {
               this.mapTextureData[index] = MapColor.mapColorArray[i / 4].getMapColor(i & 3);
            }
         }

         this.mapTexture.updateDynamicTexture();
      }

      private void render(boolean noOverlayRendering) {
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         float f = 0.0F;
         MapItemRenderer.this.textureManager.bindTexture(this.location);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(1, 771, 0, 1);
         GlStateManager.disableAlpha();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldRenderer.pos(0 + f, 128 - f, -0.01F).tex(0.0, 1.0).endVertex();
         worldRenderer.pos(128 - f, 128 - f, -0.01F).tex(1.0, 1.0).endVertex();
         worldRenderer.pos(128 - f, 0 + f, -0.01F).tex(1.0, 0.0).endVertex();
         worldRenderer.pos(0 + f, 0 + f, -0.01F).tex(0.0, 0.0).endVertex();
         instance.draw();
         GlStateManager.enableAlpha();
         GlStateManager.disableBlend();
         MapItemRenderer.this.textureManager.bindTexture(MapItemRenderer.mapIcons);
         int i = 0;

         for (Vec4b vec4b : this.mapData.mapDecorations.values()) {
            if (!noOverlayRendering || vec4b.getIconBit() == 1) {
               GlStateManager.pushMatrix();
               GlStateManager.translate(0 + vec4b.getX() / 2.0F + 64.0F, 0 + vec4b.getY() / 2.0F + 64.0F, -0.02F);
               GlStateManager.rotate(vec4b.getRotation() * 360 / 16.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.scale(4.0F, 4.0F, 3.0F);
               GlStateManager.translate(-0.125F, 0.125F, 0.0F);
               byte iconBit = vec4b.getIconBit();
               float f2 = (iconBit % 4 + 0) / 4.0F;
               float f3 = (iconBit / 4 + 0) / 4.0F;
               float f4 = (iconBit % 4 + 1) / 4.0F;
               float f5 = (iconBit / 4 + 1) / 4.0F;
               worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
               worldRenderer.pos(-1.0, 1.0, i * -0.001F).tex(f2, f3).endVertex();
               worldRenderer.pos(1.0, 1.0, i * -0.001F).tex(f4, f3).endVertex();
               worldRenderer.pos(1.0, -1.0, i * -0.001F).tex(f4, f5).endVertex();
               worldRenderer.pos(-1.0, -1.0, i * -0.001F).tex(f2, f5).endVertex();
               instance.draw();
               GlStateManager.popMatrix();
               i++;
            }
         }

         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0F, 0.0F, -0.04F);
         GlStateManager.scale(1.0F, 1.0F, 1.0F);
         GlStateManager.popMatrix();
      }

      private Instance(MapData mapdataIn) {
         this.mapData = mapdataIn;
         this.mapTexture = new DynamicTexture(128, 128);
         this.mapTextureData = this.mapTexture.getTextureData();
         this.location = MapItemRenderer.this.textureManager.getDynamicTextureLocation("map/" + mapdataIn.mapName, this.mapTexture);

         for (int index = 0; index < this.mapTextureData.length; index++) {
            this.mapTextureData[index] = 0;
         }
      }
   }
}
