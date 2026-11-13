package net.minecraft.client.renderer.tileentity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBanner;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.LayeredColorMaskTexture;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TileEntityBannerRenderer extends TileEntitySpecialRenderer<TileEntityBanner> {
   private static final Map<String, TileEntityBannerRenderer.TimedBannerTexture> DESIGNS = Maps.newHashMap();
   private ModelBanner bannerModel = new ModelBanner();
   private static final ResourceLocation BANNERTEXTURES = new ResourceLocation("textures/entity/banner_base.png");

   private ResourceLocation getBannerTexture(TileEntityBanner bannerObj) {
      String patternResourceLocation = bannerObj.getPatternResourceLocation();
      if (patternResourceLocation.isEmpty()) {
         return null;
      }

      TileEntityBannerRenderer.TimedBannerTexture timedBannerTexture = DESIGNS.get(patternResourceLocation);
      if (timedBannerTexture == null) {
         if (DESIGNS.size() >= 256) {
            long l = System.currentTimeMillis();
            Iterator iterator2 = DESIGNS.keySet().iterator();

            while (iterator2.hasNext()) {
               String text = (String)iterator2.next();
               TileEntityBannerRenderer.TimedBannerTexture timedBannerTexture2 = DESIGNS.get(text);
               if (l - timedBannerTexture2.systemTime > 60000L) {
                  Minecraft.getMinecraft().getTextureManager().deleteTexture(timedBannerTexture2.bannerTexture);
                  iterator2.remove();
               }
            }

            if (DESIGNS.size() >= 256) {
               return null;
            }
         }

         List patternList = bannerObj.getPatternList();
         List colorList = bannerObj.getColorList();
         ArrayList arrayList = Lists.newArrayList();

         for (net.minecraft.tileentity.TileEntityBanner.EnumBannerPattern enumBannerPattern : (Iterable<TileEntityBanner.EnumBannerPattern>) patternList) {
            arrayList.add("textures/entity/banner/" + enumBannerPattern.getPatternName() + ".png");
         }

         timedBannerTexture = new TileEntityBannerRenderer.TimedBannerTexture();
         timedBannerTexture.bannerTexture = new ResourceLocation(patternResourceLocation);
         Minecraft.getMinecraft().getTextureManager().loadTexture(timedBannerTexture.bannerTexture, new LayeredColorMaskTexture(BANNERTEXTURES, arrayList, colorList));
         DESIGNS.put(patternResourceLocation, timedBannerTexture);
      }

      timedBannerTexture.systemTime = System.currentTimeMillis();
      return timedBannerTexture.bannerTexture;
   }

   public void renderTileEntityAt(TileEntityBanner tileEntityBanner, double d, double d2, double d3, float f, int unused5) {
      boolean flag = tileEntityBanner.getWorld() != null;
      boolean flag2 = !flag || tileEntityBanner.getBlockType() == Blocks.standing_banner;
      int i = flag ? tileEntityBanner.getBlockMetadata() : 0;
      long l = flag ? tileEntityBanner.getWorld().getTotalWorldTime() : 0L;
      GlStateManager.pushMatrix();
      float f2 = 0.6666667F;
      if (flag2) {
         GlStateManager.translate((float)d + 0.5F, (float)d2 + 0.75F * f2, (float)d3 + 0.5F);
         float f3 = i * 360 / 16.0F;
         GlStateManager.rotate(-f3, 0.0F, 1.0F, 0.0F);
         this.bannerModel.bannerStand.showModel = true;
      } else {
         float f4 = 0.0F;
         if (i == 2) {
            f4 = 180.0F;
         }

         if (i == 4) {
            f4 = 90.0F;
         }

         if (i == 5) {
            f4 = -90.0F;
         }

         GlStateManager.translate((float)d + 0.5F, (float)d2 - 0.25F * f2, (float)d3 + 0.5F);
         GlStateManager.rotate(-f4, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
         this.bannerModel.bannerStand.showModel = false;
      }

      BlockPos pos = tileEntityBanner.getPos();
      float f5 = pos.getX() * 7 + pos.getY() * 9 + pos.getZ() * 13 + (float)l + f;
      this.bannerModel.bannerSlate.rotateAngleX = (-0.0125F + 0.01F * MathHelper.cos(f5 * (float) Math.PI * 0.02F)) * (float) Math.PI;
      GlStateManager.enableRescaleNormal();
      ResourceLocation bannerTexture2 = this.getBannerTexture(tileEntityBanner);
      if (bannerTexture2 != null) {
         this.bindTexture(bannerTexture2);
         GlStateManager.pushMatrix();
         GlStateManager.scale(f2, -f2, -f2);
         this.bannerModel.renderBanner();
         GlStateManager.popMatrix();
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }

   static class TimedBannerTexture {
      public ResourceLocation bannerTexture;
      public long systemTime;

      private TimedBannerTexture() {
      }
   }
}
