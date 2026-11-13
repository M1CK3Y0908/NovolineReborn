package net.optifine;

import java.util.Properties;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;

public class CustomLoadingScreen {
   private int scaleMode = 0;
   private static final int SCALE_MODE_FIXED = 0;
   private int scale = 2;
   private static final int SCALE_MODE_FULL = 1;
   private ResourceLocation locationTexture;
   private static final int intValue = 2;
   private static final int intValue2 = 2;
   private boolean center;

   private static String getProperty(String key, int dim, Properties props) {
      if (props == null) {
         return null;
      }

      String property = props.getProperty("dim" + dim + "." + key);
      return property != null ? property : props.getProperty(key);
   }

   private static int parseScaleMode(String str) {
      if (str == null) {
         return 0;
      }

      str = str.toLowerCase().trim();
      if (str.equals("fixed")) {
         return 0;
      }

      if (str.equals("full")) {
         return 1;
      }

      if (str.equals("stretch")) {
         return 2;
      }

      CustomLoadingScreens.warn("Invalid scale mode: " + str);
      return 0;
   }

   private static int parseScale(String str, int def) {
      if (str == null) {
         return def;
      } else {
         str = str.trim();
         int i = Config.parseInt(str, -1);
         if (i < 1) {
            CustomLoadingScreens.warn("Invalid scale: " + str);
            return def;
         } else {
            return i;
         }
      }
   }

   public CustomLoadingScreen(ResourceLocation locationTexture, int scaleMode, int scale, boolean center) {
      this.locationTexture = locationTexture;
      this.scaleMode = scaleMode;
      this.scale = scale;
      this.center = center;
   }

   public static CustomLoadingScreen parseScreen(String path, int dimId, Properties props) {
      ResourceLocation resourceLocation = new ResourceLocation(path);
      int i = parseScaleMode(getProperty("scaleMode", dimId, props));
      int i2 = i == 0 ? 2 : 1;
      int i3 = parseScale(getProperty("scale", dimId, props), i2);
      boolean flag = Config.parseBoolean(getProperty("center", dimId, props), false);
      return new CustomLoadingScreen(resourceLocation, i, i3, flag);
   }

   public void drawBackground(int width, int height) {
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      Config.getTextureManager().bindTexture(this.locationTexture);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      double d = 16 * this.scale;
      double d2 = width / d;
      double d3 = height / d;
      double d4 = 0.0;
      double d5 = 0.0;
      if (this.center) {
         d4 = (d - width) / (d * 2.0);
         d5 = (d - height) / (d * 2.0);
      }

      switch (this.scaleMode) {
         case 1:
            d = Math.max(width, height);
            d2 = this.scale * width / d;
            d3 = this.scale * height / d;
            if (this.center) {
               d4 = this.scale * (d - width) / (d * 2.0);
               d5 = this.scale * (d - height) / (d * 2.0);
            }
            break;
         case 2:
            d2 = this.scale;
            d3 = this.scale;
            d4 = 0.0;
            d5 = 0.0;
      }

      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      worldRenderer.pos(0.0, height, 0.0).tex(d4, d5 + d3).color(255, 255, 255, 255).endVertex();
      worldRenderer.pos(width, height, 0.0).tex(d4 + d2, d5 + d3).color(255, 255, 255, 255).endVertex();
      worldRenderer.pos(width, 0.0, 0.0).tex(d4 + d2, d5).color(255, 255, 255, 255).endVertex();
      worldRenderer.pos(0.0, 0.0, 0.0).tex(d4, d5).color(255, 255, 255, 255).endVertex();
      instance.draw();
   }
}
