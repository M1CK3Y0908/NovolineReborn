package net.minecraft.client.renderer;

import java.util.BitSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.optifine.BetterSnow;
import net.optifine.CustomColors;
import net.optifine.model.BlockModelCustomizer;
import net.optifine.model.ListQuadsOverlay;
import net.optifine.reflect.Reflector;
import net.optifine.render.RenderEnv;
import net.optifine.shaders.SVertexBuilder;
import net.optifine.shaders.Shaders;

public class BlockModelRenderer {
   private static float aoLightValueOpaque = 0.2F;
   private static boolean separateAoLightValue = false;
   private static final EnumWorldBlockLayer[] OVERLAY_LAYERS = new EnumWorldBlockLayer[]{
      EnumWorldBlockLayer.CUTOUT, EnumWorldBlockLayer.CUTOUT_MIPPED, EnumWorldBlockLayer.TRANSLUCENT
   };

   private void handleIBlockAccess(
      IBlockAccess iBlockAccess, IBakedModel iBakedModel, IBlockState iBlockState, BlockPos blockPos, WorldRenderer worldRenderer, boolean flag, RenderEnv renderEnv2, boolean flag2
   ) {
      if (renderEnv2.isOverlaysRendered()) {
         for (int i = 0; i < OVERLAY_LAYERS.length; i++) {
            EnumWorldBlockLayer enumWorldBlockLayer = OVERLAY_LAYERS[i];
            ListQuadsOverlay listQuadsOverlay = renderEnv2.getListQuadsOverlay(enumWorldBlockLayer);
            if (listQuadsOverlay.size() > 0) {
               RegionRenderCacheBuilder regionRenderCacheBuilder = renderEnv2.getRegionRenderCacheBuilder();
               if (regionRenderCacheBuilder != null) {
                  WorldRenderer worldRendererByLayer = regionRenderCacheBuilder.getWorldRendererByLayer(enumWorldBlockLayer);
                  if (!worldRendererByLayer.isDrawing()) {
                     worldRendererByLayer.begin(7, DefaultVertexFormats.BLOCK);
                     worldRendererByLayer.setTranslation(worldRenderer.getXOffset(), worldRenderer.getYOffset(), worldRenderer.getZOffset());
                  }

                  for (int i2 = 0; i2 < listQuadsOverlay.size(); i2++) {
                     BakedQuad quad = listQuadsOverlay.getQuad(i2);
                     List listQuadsSingle = listQuadsOverlay.getListQuadsSingle(quad);
                     IBlockState blockState = listQuadsOverlay.getBlockState(i2);
                     if (quad.getQuadEmissive() != null) {
                        listQuadsOverlay.addQuad(quad.getQuadEmissive(), blockState);
                     }

                     renderEnv2.reset(blockState, blockPos);
                     if (flag2) {
                        this.renderQuadsSmooth(iBlockAccess, blockState, blockPos, worldRendererByLayer, listQuadsSingle, renderEnv2);
                     } else {
                        int mixedBrightnessForBlock = blockState.getBlock().getMixedBrightnessForBlock(iBlockAccess, blockPos.offset(quad.getFace()));
                        this.renderQuadsFlat(iBlockAccess, blockState, blockPos, quad.getFace(), mixedBrightnessForBlock, false, worldRendererByLayer, listQuadsSingle, renderEnv2);
                     }
                  }
               }

               listQuadsOverlay.clear();
            }
         }
      }

      if (Config.isActive2() && !renderEnv2.isBreakingAnimation() && BetterSnow.shouldRender(iBlockAccess, iBlockState, blockPos)) {
         IBakedModel modelSnowLayer = BetterSnow.getModelSnowLayer();
         IBlockState stateSnowLayer = BetterSnow.getStateSnowLayer();
         this.renderModel(iBlockAccess, modelSnowLayer, stateSnowLayer, blockPos, worldRenderer, flag);
      }
   }

   public static void updateAoLightValue() {
      aoLightValueOpaque = 1.0F - Config.getAmbientOcclusionLevel() * 0.8F;
      separateAoLightValue = Config.isShaders() && Shaders.isSeparateAo();
   }

   public boolean renderModel(IBlockAccess blockAccessIn, IBakedModel modelIn, IBlockState blockStateIn, BlockPos blockPosIn, WorldRenderer worldRendererIn) {
      blockStateIn.getBlock().setBlockBoundsBasedOnState(blockAccessIn, blockPosIn);
      return this.renderModel(blockAccessIn, modelIn, blockStateIn, blockPosIn, worldRendererIn, true);
   }

   public void renderModelBrightnessColor(IBakedModel bakedModel, float brightness, float red, float green, float blue) {
      for (EnumFacing enumFacing : EnumFacing.VALUES) {
         this.renderModelBrightnessColorQuads(brightness, red, green, blue, bakedModel.getFaceQuads(enumFacing));
      }

      this.renderModelBrightnessColorQuads(brightness, red, green, blue, bakedModel.getGeneralQuads());
   }

   private void renderModelBrightnessColorQuads(float brightness, float red, float green, float blue, List<BakedQuad> listQuads) {
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();

      for (BakedQuad bakedQuad : listQuads) {
         worldRenderer.begin(7, DefaultVertexFormats.ITEM);
         worldRenderer.addVertexData(bakedQuad.getVertexData());
         worldRenderer.putSprite(bakedQuad.getSprite());
         if (bakedQuad.hasTintIndex()) {
            worldRenderer.putColorRGB_F4(red * brightness, green * brightness, blue * brightness);
         } else {
            worldRenderer.putColorRGB_F4(brightness, brightness, brightness);
         }

         Vec3i directionVec = bakedQuad.getFace().getDirectionVec();
         worldRenderer.putNormal(directionVec.getX(), directionVec.getY(), directionVec.getZ());
         instance.draw();
      }
   }

   public static float fixAoLightValue(float lightValue) {
      return lightValue == 0.2F ? aoLightValueOpaque : lightValue;
   }

   public void handleIBakedModel(IBakedModel iBakedModel, IBlockState iBlockState, float f) {
      Block block2 = iBlockState.getBlock();
      block2.setBlockBoundsForItemRender();
      GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
      int renderColor = block2.getRenderColor(block2.getStateForEntityRender(iBlockState));
      if (EntityRenderer.anaglyphEnable) {
         renderColor = TextureUtil.anaglyphColor(renderColor);
      }

      float f2 = (renderColor >> 16 & 0xFF) / 255.0F;
      float f3 = (renderColor >> 8 & 0xFF) / 255.0F;
      float f4 = (renderColor & 0xFF) / 255.0F;
      this.renderModelBrightnessColor(iBakedModel, f, f2, f3, f4);
   }

   private void renderQuadsSmooth(
      IBlockAccess blockAccessIn, IBlockState blockStateIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, List<BakedQuad> listQuads, RenderEnv renderEnv
   ) {
      Block block2 = blockStateIn.getBlock();
      float[] quadBounds2 = renderEnv.getQuadBounds();
      BitSet boundsFlags2 = renderEnv.getBoundsFlags();
      BlockModelRenderer.AmbientOcclusionFace aoFace = renderEnv.getAoFace();
      double x = blockPosIn.getX();
      double y = blockPosIn.getY();
      double z = blockPosIn.getZ();
      net.minecraft.block.Block.EnumOffsetType offsetType = block2.getOffsetType();
      if (offsetType != net.minecraft.block.Block.EnumOffsetType.NONE) {
         long positionRandom = MathHelper.getPositionRandom(blockPosIn);
         x += ((float)(positionRandom >> 16 & 15L) / 15.0F - 0.5) * 0.5;
         z += ((float)(positionRandom >> 24 & 15L) / 15.0F - 0.5) * 0.5;
         if (offsetType == net.minecraft.block.Block.EnumOffsetType.XYZ) {
            y += ((float)(positionRandom >> 20 & 15L) / 15.0F - 1.0) * 0.2;
         }
      }

      for (BakedQuad bakedQuad : listQuads) {
         this.fillQuadBounds(block2, bakedQuad.getVertexData(), bakedQuad.getFace(), quadBounds2, boundsFlags2);
         aoFace.updateVertexBrightness(blockAccessIn, block2, blockPosIn, bakedQuad.getFace(), quadBounds2, boundsFlags2);
         if (bakedQuad.getSprite().isEmissive) {
            aoFace.setMaxBlockLight();
         }

         if (worldRendererIn.isMultiTexture()) {
            worldRendererIn.addVertexData(bakedQuad.getVertexDataSingle());
         } else {
            worldRendererIn.addVertexData(bakedQuad.getVertexData());
         }

         worldRendererIn.putSprite(bakedQuad.getSprite());
         worldRendererIn.putBrightness4(aoFace.vertexBrightness[0], aoFace.vertexBrightness[1], aoFace.vertexBrightness[2], aoFace.vertexBrightness[3]);
         int colorMultiplier2 = CustomColors.getColorMultiplier(bakedQuad, blockStateIn, blockAccessIn, blockPosIn, renderEnv);
         if (!bakedQuad.hasTintIndex() && colorMultiplier2 == -1) {
            if (separateAoLightValue) {
               worldRendererIn.putColorMultiplierRgba(1.0F, 1.0F, 1.0F, aoFace.vertexColorMultiplier[0], 4);
               worldRendererIn.putColorMultiplierRgba(1.0F, 1.0F, 1.0F, aoFace.vertexColorMultiplier[1], 3);
               worldRendererIn.putColorMultiplierRgba(1.0F, 1.0F, 1.0F, aoFace.vertexColorMultiplier[2], 2);
               worldRendererIn.putColorMultiplierRgba(1.0F, 1.0F, 1.0F, aoFace.vertexColorMultiplier[3], 1);
            } else {
               worldRendererIn.putColorMultiplier(aoFace.vertexColorMultiplier[0], aoFace.vertexColorMultiplier[0], aoFace.vertexColorMultiplier[0], 4);
               worldRendererIn.putColorMultiplier(aoFace.vertexColorMultiplier[1], aoFace.vertexColorMultiplier[1], aoFace.vertexColorMultiplier[1], 3);
               worldRendererIn.putColorMultiplier(aoFace.vertexColorMultiplier[2], aoFace.vertexColorMultiplier[2], aoFace.vertexColorMultiplier[2], 2);
               worldRendererIn.putColorMultiplier(aoFace.vertexColorMultiplier[3], aoFace.vertexColorMultiplier[3], aoFace.vertexColorMultiplier[3], 1);
            }
         } else {
            int i;
            if (colorMultiplier2 != -1) {
               i = colorMultiplier2;
            } else {
               i = block2.colorMultiplier(blockAccessIn, blockPosIn, bakedQuad.getTintIndex());
            }

            if (EntityRenderer.anaglyphEnable) {
               i = TextureUtil.anaglyphColor(i);
            }

            float f = (i >> 16 & 0xFF) / 255.0F;
            float f2 = (i >> 8 & 0xFF) / 255.0F;
            float f3 = (i & 0xFF) / 255.0F;
            if (separateAoLightValue) {
               worldRendererIn.putColorMultiplierRgba(f, f2, f3, aoFace.vertexColorMultiplier[0], 4);
               worldRendererIn.putColorMultiplierRgba(f, f2, f3, aoFace.vertexColorMultiplier[1], 3);
               worldRendererIn.putColorMultiplierRgba(f, f2, f3, aoFace.vertexColorMultiplier[2], 2);
               worldRendererIn.putColorMultiplierRgba(f, f2, f3, aoFace.vertexColorMultiplier[3], 1);
            } else {
               worldRendererIn.putColorMultiplier(
                  aoFace.vertexColorMultiplier[0] * f, aoFace.vertexColorMultiplier[0] * f2, aoFace.vertexColorMultiplier[0] * f3, 4
               );
               worldRendererIn.putColorMultiplier(
                  aoFace.vertexColorMultiplier[1] * f, aoFace.vertexColorMultiplier[1] * f2, aoFace.vertexColorMultiplier[1] * f3, 3
               );
               worldRendererIn.putColorMultiplier(
                  aoFace.vertexColorMultiplier[2] * f, aoFace.vertexColorMultiplier[2] * f2, aoFace.vertexColorMultiplier[2] * f3, 2
               );
               worldRendererIn.putColorMultiplier(
                  aoFace.vertexColorMultiplier[3] * f, aoFace.vertexColorMultiplier[3] * f2, aoFace.vertexColorMultiplier[3] * f3, 1
               );
            }
         }

         worldRendererIn.putPosition(x, y, z);
      }
   }

   public BlockModelRenderer() {
      if (Reflector.reflectorField13.isActive()) {
         Reflector.checkReflectorField(Reflector.reflectorField13, false);
      }
   }

   public boolean renderModel(
      IBlockAccess blockAccessIn, IBakedModel modelIn, IBlockState blockStateIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSides
   ) {
      boolean flag = Minecraft.isAmbientOcclusionEnabled() && blockStateIn.getBlock().getLightValue() == 0 && modelIn.isAmbientOcclusion();

      try {
         if (Config.isShaders()) {
            SVertexBuilder.pushEntity(blockStateIn, blockPosIn, blockAccessIn, worldRendererIn);
         }

         RenderEnv renderEnv2 = worldRendererIn.getRenderEnv(blockStateIn, blockPosIn);
         modelIn = BlockModelCustomizer.getRenderModel(modelIn, blockStateIn, renderEnv2);
         boolean flag2 = flag
            ? this.renderModelSmooth(blockAccessIn, modelIn, blockStateIn, blockPosIn, worldRendererIn, checkSides)
            : this.renderModelFlat(blockAccessIn, modelIn, blockStateIn, blockPosIn, worldRendererIn, checkSides);
         if (flag2) {
            this.handleIBlockAccess(blockAccessIn, modelIn, blockStateIn, blockPosIn, worldRendererIn, checkSides, renderEnv2, flag);
         }

         if (Config.isShaders()) {
            SVertexBuilder.popEntity(worldRendererIn);
         }

         return flag2;
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Tesselating block model");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Block model being tesselated");
         CrashReportCategory.addBlockInfo(crashReportCategory, blockPosIn, blockStateIn);
         crashReportCategory.addCrashSection("Using AO", flag);
         throw new ReportedException(crashReport);
      }
   }

   public boolean renderModelFlat(
      IBlockAccess blockAccessIn, IBakedModel modelIn, IBlockState blockStateIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSides
   ) {
      boolean flag = false;
      Block block2 = blockStateIn.getBlock();
      RenderEnv renderEnv2 = worldRendererIn.getRenderEnv(blockStateIn, blockPosIn);
      EnumWorldBlockLayer blockLayer = worldRendererIn.getBlockLayer();

      for (EnumFacing enumFacing : EnumFacing.VALUES) {
         List faceQuads = modelIn.getFaceQuads(enumFacing);
         if (!faceQuads.isEmpty()) {
            BlockPos blockPos = blockPosIn.offset(enumFacing);
            if (!checkSides || block2.shouldSideBeRendered(blockAccessIn, blockPos, enumFacing)) {
               int mixedBrightnessForBlock = block2.getMixedBrightnessForBlock(blockAccessIn, blockPos);
               faceQuads = BlockModelCustomizer.createList(faceQuads, blockAccessIn, blockStateIn, blockPosIn, enumFacing, blockLayer, renderEnv2);
               this.renderQuadsFlat(blockAccessIn, blockStateIn, blockPosIn, enumFacing, mixedBrightnessForBlock, false, worldRendererIn, faceQuads, renderEnv2);
               flag = true;
            }
         }
      }

      List generalQuads = modelIn.getGeneralQuads();
      if (generalQuads.size() > 0) {
         generalQuads = BlockModelCustomizer.createList(generalQuads, blockAccessIn, blockStateIn, blockPosIn, (EnumFacing)null, blockLayer, renderEnv2);
         this.renderQuadsFlat(blockAccessIn, blockStateIn, blockPosIn, (EnumFacing)null, -1, true, worldRendererIn, generalQuads, renderEnv2);
         flag = true;
      }

      return flag;
   }

   private void fillQuadBounds(Block blockIn, int[] vertexData, EnumFacing facingIn, float[] quadBounds, BitSet boundsFlags) {
      float f = 32.0F;
      float f2 = 32.0F;
      float f3 = 32.0F;
      float f4 = -32.0F;
      float f5 = -32.0F;
      float f6 = -32.0F;
      int i = vertexData.length / 4;

      for (int i2 = 0; i2 < 4; i2++) {
         float f7 = Float.intBitsToFloat(vertexData[i2 * i]);
         float f8 = Float.intBitsToFloat(vertexData[i2 * i + 1]);
         float f9 = Float.intBitsToFloat(vertexData[i2 * i + 2]);
         f = Math.min(f, f7);
         f2 = Math.min(f2, f8);
         f3 = Math.min(f3, f9);
         f4 = Math.max(f4, f7);
         f5 = Math.max(f5, f8);
         f6 = Math.max(f6, f9);
      }

      if (quadBounds != null) {
         quadBounds[EnumFacing.WEST.getIndex()] = f;
         quadBounds[EnumFacing.EAST.getIndex()] = f4;
         quadBounds[EnumFacing.DOWN.getIndex()] = f2;
         quadBounds[EnumFacing.UP.getIndex()] = f5;
         quadBounds[EnumFacing.NORTH.getIndex()] = f3;
         quadBounds[EnumFacing.SOUTH.getIndex()] = f6;
         int length2 = EnumFacing.VALUES.length;
         quadBounds[EnumFacing.WEST.getIndex() + length2] = 1.0F - f;
         quadBounds[EnumFacing.EAST.getIndex() + length2] = 1.0F - f4;
         quadBounds[EnumFacing.DOWN.getIndex() + length2] = 1.0F - f2;
         quadBounds[EnumFacing.UP.getIndex() + length2] = 1.0F - f5;
         quadBounds[EnumFacing.NORTH.getIndex() + length2] = 1.0F - f3;
         quadBounds[EnumFacing.SOUTH.getIndex() + length2] = 1.0F - f6;
      }

      switch (facingIn) {
         case DOWN:
            boundsFlags.set(1, f >= 1.0E-4F || f3 >= 1.0E-4F || f4 <= 0.9999F || f6 <= 0.9999F);
            boundsFlags.set(0, (f2 < 1.0E-4F || blockIn.isFullCube()) && f2 == f5);
            break;
         case UP:
            boundsFlags.set(1, f >= 1.0E-4F || f3 >= 1.0E-4F || f4 <= 0.9999F || f6 <= 0.9999F);
            boundsFlags.set(0, (f5 > 0.9999F || blockIn.isFullCube()) && f2 == f5);
            break;
         case NORTH:
            boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
            boundsFlags.set(0, (f3 < 1.0E-4F || blockIn.isFullCube()) && f3 == f6);
            break;
         case SOUTH:
            boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
            boundsFlags.set(0, (f6 > 0.9999F || blockIn.isFullCube()) && f3 == f6);
            break;
         case WEST:
            boundsFlags.set(1, f2 >= 1.0E-4F || f3 >= 1.0E-4F || f5 <= 0.9999F || f6 <= 0.9999F);
            boundsFlags.set(0, (f < 1.0E-4F || blockIn.isFullCube()) && f == f4);
            break;
         case EAST:
            boundsFlags.set(1, f2 >= 1.0E-4F || f3 >= 1.0E-4F || f5 <= 0.9999F || f6 <= 0.9999F);
            boundsFlags.set(0, (f4 > 0.9999F || blockIn.isFullCube()) && f == f4);
      }
   }

   private boolean renderModelSmooth(
      IBlockAccess blockAccessIn, IBakedModel modelIn, IBlockState blockStateIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSides
   ) {
      boolean flag = false;
      Block block2 = blockStateIn.getBlock();
      RenderEnv renderEnv2 = worldRendererIn.getRenderEnv(blockStateIn, blockPosIn);
      EnumWorldBlockLayer blockLayer = worldRendererIn.getBlockLayer();

      for (EnumFacing enumFacing : EnumFacing.VALUES) {
         List faceQuads = modelIn.getFaceQuads(enumFacing);
         if (!faceQuads.isEmpty()) {
            BlockPos blockPos = blockPosIn.offset(enumFacing);
            if (!checkSides || block2.shouldSideBeRendered(blockAccessIn, blockPos, enumFacing)) {
               faceQuads = BlockModelCustomizer.createList(faceQuads, blockAccessIn, blockStateIn, blockPosIn, enumFacing, blockLayer, renderEnv2);
               this.renderQuadsSmooth(blockAccessIn, blockStateIn, blockPosIn, worldRendererIn, faceQuads, renderEnv2);
               flag = true;
            }
         }
      }

      List generalQuads = modelIn.getGeneralQuads();
      if (generalQuads.size() > 0) {
         generalQuads = BlockModelCustomizer.createList(generalQuads, blockAccessIn, blockStateIn, blockPosIn, (EnumFacing)null, blockLayer, renderEnv2);
         this.renderQuadsSmooth(blockAccessIn, blockStateIn, blockPosIn, worldRendererIn, generalQuads, renderEnv2);
         flag = true;
      }

      return flag;
   }

   private void renderQuadsFlat(
      IBlockAccess blockAccessIn,
      IBlockState blockStateIn,
      BlockPos blockPosIn,
      EnumFacing facingIn,
      int brightnessIn,
      boolean ownBrightness,
      WorldRenderer worldRendererIn,
      List<BakedQuad> listQuads,
      RenderEnv renderEnv
   ) {
      Block block2 = blockStateIn.getBlock();
      BitSet boundsFlags2 = renderEnv.getBoundsFlags();
      double x = blockPosIn.getX();
      double y = blockPosIn.getY();
      double z = blockPosIn.getZ();
      net.minecraft.block.Block.EnumOffsetType offsetType = block2.getOffsetType();
      if (offsetType != net.minecraft.block.Block.EnumOffsetType.NONE) {
         int x2 = blockPosIn.getX();
         int z2 = blockPosIn.getZ();
         long l = x2 * 3129871 ^ z2 * 116129781L;
         l = l * l * 42317861L + l * 11L;
         x += ((float)(l >> 16 & 15L) / 15.0F - 0.5) * 0.5;
         z += ((float)(l >> 24 & 15L) / 15.0F - 0.5) * 0.5;
         if (offsetType == net.minecraft.block.Block.EnumOffsetType.XYZ) {
            y += ((float)(l >> 20 & 15L) / 15.0F - 1.0) * 0.2;
         }
      }

      for (BakedQuad bakedQuad : listQuads) {
         if (ownBrightness) {
            this.fillQuadBounds(block2, bakedQuad.getVertexData(), bakedQuad.getFace(), (float[])null, boundsFlags2);
            brightnessIn = boundsFlags2.get(0)
               ? block2.getMixedBrightnessForBlock(blockAccessIn, blockPosIn.offset(bakedQuad.getFace()))
               : block2.getMixedBrightnessForBlock(blockAccessIn, blockPosIn);
         }

         if (bakedQuad.getSprite().isEmissive) {
            brightnessIn |= 240;
         }

         if (worldRendererIn.isMultiTexture()) {
            worldRendererIn.addVertexData(bakedQuad.getVertexDataSingle());
         } else {
            worldRendererIn.addVertexData(bakedQuad.getVertexData());
         }

         worldRendererIn.putSprite(bakedQuad.getSprite());
         worldRendererIn.putBrightness4(brightnessIn, brightnessIn, brightnessIn, brightnessIn);
         int colorMultiplier2 = CustomColors.getColorMultiplier(bakedQuad, blockStateIn, blockAccessIn, blockPosIn, renderEnv);
         if (bakedQuad.hasTintIndex() || colorMultiplier2 != -1) {
            int i;
            if (colorMultiplier2 != -1) {
               i = colorMultiplier2;
            } else {
               i = block2.colorMultiplier(blockAccessIn, blockPosIn, bakedQuad.getTintIndex());
            }

            if (EntityRenderer.anaglyphEnable) {
               i = TextureUtil.anaglyphColor(i);
            }

            float f = (i >> 16 & 0xFF) / 255.0F;
            float f2 = (i >> 8 & 0xFF) / 255.0F;
            float f3 = (i & 0xFF) / 255.0F;
            worldRendererIn.putColorMultiplier(f, f2, f3, 4);
            worldRendererIn.putColorMultiplier(f, f2, f3, 3);
            worldRendererIn.putColorMultiplier(f, f2, f3, 2);
            worldRendererIn.putColorMultiplier(f, f2, f3, 1);
         }

         worldRendererIn.putPosition(x, y, z);
      }
   }

   public static class AmbientOcclusionFace {
      private final float[] vertexColorMultiplier = new float[4];
      private final int[] vertexBrightness = new int[4];

      private int getAoBrightness(int br1, int br2, int br3, int br4) {
         if (br1 == 0) {
            br1 = br4;
         }

         if (br2 == 0) {
            br2 = br4;
         }

         if (br3 == 0) {
            br3 = br4;
         }

         return br1 + br2 + br3 + br4 >> 2 & 16711935;
      }

      public void updateVertexBrightness(
         IBlockAccess blockAccessIn, Block blockIn, BlockPos blockPosIn, EnumFacing facingIn, float[] quadBounds, BitSet boundsFlags
      ) {
         BlockPos blockPos = boundsFlags.get(0) ? blockPosIn.offset(facingIn) : blockPosIn;
         BlockModelRenderer.EnumNeighborInfo neighbourInfo = BlockModelRenderer.EnumNeighborInfo.getNeighbourInfo(facingIn);
         BlockPos blockPos2 = blockPos.offset(neighbourInfo.neighborFacings[0]);
         BlockPos blockPos3 = blockPos.offset(neighbourInfo.neighborFacings[1]);
         BlockPos blockPos4 = blockPos.offset(neighbourInfo.neighborFacings[2]);
         BlockPos blockPos5 = blockPos.offset(neighbourInfo.neighborFacings[3]);
         int mixedBrightnessForBlock = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPos2);
         int mixedBrightnessForBlock2 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPos3);
         int mixedBrightnessForBlock3 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPos4);
         int mixedBrightnessForBlock4 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPos5);
         float f = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockPos2).getBlock().getAmbientOcclusionLightValue());
         float f2 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockPos3).getBlock().getAmbientOcclusionLightValue());
         float f3 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockPos4).getBlock().getAmbientOcclusionLightValue());
         float f4 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockPos5).getBlock().getAmbientOcclusionLightValue());
         boolean translucent = blockAccessIn.getBlockState(blockPos2.offset(facingIn)).getBlock().isTranslucent();
         boolean translucent2 = blockAccessIn.getBlockState(blockPos3.offset(facingIn)).getBlock().isTranslucent();
         boolean translucent3 = blockAccessIn.getBlockState(blockPos4.offset(facingIn)).getBlock().isTranslucent();
         boolean translucent4 = blockAccessIn.getBlockState(blockPos5.offset(facingIn)).getBlock().isTranslucent();
         float f5;
         int i;
         if (!translucent3 && !translucent) {
            f5 = f;
            i = mixedBrightnessForBlock;
         } else {
            BlockPos blockPos6 = blockPos2.offset(neighbourInfo.neighborFacings[2]);
            f5 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockPos6).getBlock().getAmbientOcclusionLightValue());
            i = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPos6);
         }

         float f6;
         int i2;
         if (!translucent4 && !translucent) {
            f6 = f;
            i2 = mixedBrightnessForBlock;
         } else {
            BlockPos blockPos7 = blockPos2.offset(neighbourInfo.neighborFacings[3]);
            f6 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockPos7).getBlock().getAmbientOcclusionLightValue());
            i2 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPos7);
         }

         float f7;
         int i3;
         if (!translucent3 && !translucent2) {
            f7 = f2;
            i3 = mixedBrightnessForBlock2;
         } else {
            BlockPos blockPos8 = blockPos3.offset(neighbourInfo.neighborFacings[2]);
            f7 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockPos8).getBlock().getAmbientOcclusionLightValue());
            i3 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPos8);
         }

         float f8;
         int i4;
         if (!translucent4 && !translucent2) {
            f8 = f2;
            i4 = mixedBrightnessForBlock2;
         } else {
            BlockPos blockPos9 = blockPos3.offset(neighbourInfo.neighborFacings[3]);
            f8 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockPos9).getBlock().getAmbientOcclusionLightValue());
            i4 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPos9);
         }

         int mixedBrightnessForBlock5 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPosIn);
         if (boundsFlags.get(0) || !blockAccessIn.getBlockState(blockPosIn.offset(facingIn)).getBlock().isOpaqueCube()) {
            mixedBrightnessForBlock5 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPosIn.offset(facingIn));
         }

         float f9 = boundsFlags.get(0)
            ? blockAccessIn.getBlockState(blockPos).getBlock().getAmbientOcclusionLightValue()
            : blockAccessIn.getBlockState(blockPosIn).getBlock().getAmbientOcclusionLightValue();
         f9 = BlockModelRenderer.fixAoLightValue(f9);
         BlockModelRenderer.VertexTranslations vertexTranslations = BlockModelRenderer.VertexTranslations.getVertexTranslations(facingIn);
         if (boundsFlags.get(1) && neighbourInfo.useShape) {
            float f10 = (f4 + f + f6 + f9) * 0.25F;
            float f11 = (f3 + f + f5 + f9) * 0.25F;
            float f12 = (f3 + f2 + f7 + f9) * 0.25F;
            float f13 = (f4 + f2 + f8 + f9) * 0.25F;
            float f14 = quadBounds[neighbourInfo.orientationLookup0[0].boundsIndex] * quadBounds[neighbourInfo.orientationLookup0[1].boundsIndex];
            float f15 = quadBounds[neighbourInfo.orientationLookup0[2].boundsIndex] * quadBounds[neighbourInfo.orientationLookup0[3].boundsIndex];
            float f16 = quadBounds[neighbourInfo.orientationLookup0[4].boundsIndex] * quadBounds[neighbourInfo.orientationLookup0[5].boundsIndex];
            float f17 = quadBounds[neighbourInfo.orientationLookup0[6].boundsIndex] * quadBounds[neighbourInfo.orientationLookup0[7].boundsIndex];
            float f18 = quadBounds[neighbourInfo.orientationLookup1[0].boundsIndex] * quadBounds[neighbourInfo.orientationLookup1[1].boundsIndex];
            float f19 = quadBounds[neighbourInfo.orientationLookup1[2].boundsIndex] * quadBounds[neighbourInfo.orientationLookup1[3].boundsIndex];
            float f20 = quadBounds[neighbourInfo.orientationLookup1[4].boundsIndex] * quadBounds[neighbourInfo.orientationLookup1[5].boundsIndex];
            float f21 = quadBounds[neighbourInfo.orientationLookup1[6].boundsIndex] * quadBounds[neighbourInfo.orientationLookup1[7].boundsIndex];
            float f22 = quadBounds[neighbourInfo.orientationLookup2[0].boundsIndex] * quadBounds[neighbourInfo.orientationLookup2[1].boundsIndex];
            float f23 = quadBounds[neighbourInfo.orientationLookup2[2].boundsIndex] * quadBounds[neighbourInfo.orientationLookup2[3].boundsIndex];
            float f24 = quadBounds[neighbourInfo.orientationLookup2[4].boundsIndex] * quadBounds[neighbourInfo.orientationLookup2[5].boundsIndex];
            float f25 = quadBounds[neighbourInfo.orientationLookup2[6].boundsIndex] * quadBounds[neighbourInfo.orientationLookup2[7].boundsIndex];
            float f26 = quadBounds[neighbourInfo.orientationLookup3[0].boundsIndex] * quadBounds[neighbourInfo.orientationLookup3[1].boundsIndex];
            float f27 = quadBounds[neighbourInfo.orientationLookup3[2].boundsIndex] * quadBounds[neighbourInfo.orientationLookup3[3].boundsIndex];
            float f28 = quadBounds[neighbourInfo.orientationLookup3[4].boundsIndex] * quadBounds[neighbourInfo.orientationLookup3[5].boundsIndex];
            float f29 = quadBounds[neighbourInfo.orientationLookup3[6].boundsIndex] * quadBounds[neighbourInfo.orientationLookup3[7].boundsIndex];
            this.vertexColorMultiplier[vertexTranslations.vertex0Index] = f10 * f14 + f11 * f15 + f12 * f16 + f13 * f17;
            this.vertexColorMultiplier[vertexTranslations.vertex1Index] = f10 * f18 + f11 * f19 + f12 * f20 + f13 * f21;
            this.vertexColorMultiplier[vertexTranslations.vertex2Index] = f10 * f22 + f11 * f23 + f12 * f24 + f13 * f25;
            this.vertexColorMultiplier[vertexTranslations.vertex3Index] = f10 * f26 + f11 * f27 + f12 * f28 + f13 * f29;
            int aoBrightness = this.getAoBrightness(mixedBrightnessForBlock4, mixedBrightnessForBlock, i2, mixedBrightnessForBlock5);
            int aoBrightness2 = this.getAoBrightness(mixedBrightnessForBlock3, mixedBrightnessForBlock, i, mixedBrightnessForBlock5);
            int aoBrightness3 = this.getAoBrightness(mixedBrightnessForBlock3, mixedBrightnessForBlock2, i3, mixedBrightnessForBlock5);
            int aoBrightness4 = this.getAoBrightness(mixedBrightnessForBlock4, mixedBrightnessForBlock2, i4, mixedBrightnessForBlock5);
            this.vertexBrightness[vertexTranslations.vertex0Index] = this.getVertexBrightness(aoBrightness, aoBrightness2, aoBrightness3, aoBrightness4, f14, f15, f16, f17);
            this.vertexBrightness[vertexTranslations.vertex1Index] = this.getVertexBrightness(aoBrightness, aoBrightness2, aoBrightness3, aoBrightness4, f18, f19, f20, f21);
            this.vertexBrightness[vertexTranslations.vertex2Index] = this.getVertexBrightness(aoBrightness, aoBrightness2, aoBrightness3, aoBrightness4, f22, f23, f24, f25);
            this.vertexBrightness[vertexTranslations.vertex3Index] = this.getVertexBrightness(aoBrightness, aoBrightness2, aoBrightness3, aoBrightness4, f26, f27, f28, f29);
         } else {
            float f30 = (f4 + f + f6 + f9) * 0.25F;
            float f31 = (f3 + f + f5 + f9) * 0.25F;
            float f32 = (f3 + f2 + f7 + f9) * 0.25F;
            float f33 = (f4 + f2 + f8 + f9) * 0.25F;
            this.vertexBrightness[vertexTranslations.vertex0Index] = this.getAoBrightness(mixedBrightnessForBlock4, mixedBrightnessForBlock, i2, mixedBrightnessForBlock5);
            this.vertexBrightness[vertexTranslations.vertex1Index] = this.getAoBrightness(mixedBrightnessForBlock3, mixedBrightnessForBlock, i, mixedBrightnessForBlock5);
            this.vertexBrightness[vertexTranslations.vertex2Index] = this.getAoBrightness(mixedBrightnessForBlock3, mixedBrightnessForBlock2, i3, mixedBrightnessForBlock5);
            this.vertexBrightness[vertexTranslations.vertex3Index] = this.getAoBrightness(mixedBrightnessForBlock4, mixedBrightnessForBlock2, i4, mixedBrightnessForBlock5);
            this.vertexColorMultiplier[vertexTranslations.vertex0Index] = f30;
            this.vertexColorMultiplier[vertexTranslations.vertex1Index] = f31;
            this.vertexColorMultiplier[vertexTranslations.vertex2Index] = f32;
            this.vertexColorMultiplier[vertexTranslations.vertex3Index] = f33;
         }
      }

      public AmbientOcclusionFace() {
         this((BlockModelRenderer)null);
      }

      public void setMaxBlockLight() {
         this.vertexBrightness[0] = this.vertexBrightness[0] | 240;
         this.vertexBrightness[1] = this.vertexBrightness[1] | 240;
         this.vertexBrightness[2] = this.vertexBrightness[2] | 240;
         this.vertexBrightness[3] = this.vertexBrightness[3] | 240;
         this.vertexColorMultiplier[0] = 1.0F;
         this.vertexColorMultiplier[1] = 1.0F;
         this.vertexColorMultiplier[2] = 1.0F;
         this.vertexColorMultiplier[3] = 1.0F;
      }

      private int getVertexBrightness(
         int brightness1, int brightness2, int brightness3, int brightness4, float weight1, float weight2, float weight3, float weight4
      ) {
         int i = (int)(
               (brightness1 >> 16 & 0xFF) * weight1
                  + (brightness2 >> 16 & 0xFF) * weight2
                  + (brightness3 >> 16 & 0xFF) * weight3
                  + (brightness4 >> 16 & 0xFF) * weight4
            )
            & 0xFF;
         int i2 = (int)(
               (brightness1 & 0xFF) * weight1 + (brightness2 & 0xFF) * weight2 + (brightness3 & 0xFF) * weight3 + (brightness4 & 0xFF) * weight4
            )
            & 0xFF;
         return i << 16 | i2;
      }

      public AmbientOcclusionFace(BlockModelRenderer rendererIn) {
      }
   }

   public enum EnumNeighborInfo {
      DOWN(
         new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH},
         0.5F,
         false,
         new BlockModelRenderer.Orientation[0],
         new BlockModelRenderer.Orientation[0],
         new BlockModelRenderer.Orientation[0],
         new BlockModelRenderer.Orientation[0]
      ),
      UP(
         new EnumFacing[]{EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH},
         1.0F,
         false,
         new BlockModelRenderer.Orientation[0],
         new BlockModelRenderer.Orientation[0],
         new BlockModelRenderer.Orientation[0],
         new BlockModelRenderer.Orientation[0]
      ),
      NORTH(
         new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST},
         0.8F,
         true,
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.FLIP_WEST
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.FLIP_EAST
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.FLIP_EAST
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.FLIP_WEST
         }
      ),
      SOUTH(
         new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.UP},
         0.8F,
         true,
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.WEST
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.WEST
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.EAST
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.EAST
         }
      ),
      WEST(
         new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH},
         0.6F,
         true,
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.SOUTH,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.SOUTH
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.NORTH,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.NORTH
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.NORTH,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.NORTH
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.SOUTH,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.SOUTH
         }
      ),
      EAST(
         new EnumFacing[]{EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH},
         0.6F,
         true,
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.SOUTH,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.SOUTH
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.NORTH,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.NORTH
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.NORTH,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.NORTH
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.SOUTH,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.SOUTH
         }
      );

      protected final EnumFacing[] neighborFacings;
      protected final float shadeWeight;
      protected final boolean useShape;
      protected final BlockModelRenderer.Orientation[] orientationLookup0;
      protected final BlockModelRenderer.Orientation[] orientationLookup1;
      protected final BlockModelRenderer.Orientation[] orientationLookup2;
      protected final BlockModelRenderer.Orientation[] orientationLookup3;
      private static final BlockModelRenderer.EnumNeighborInfo[] VALUES = new BlockModelRenderer.EnumNeighborInfo[6];

      public static BlockModelRenderer.EnumNeighborInfo getNeighbourInfo(EnumFacing facingIn) {
         return VALUES[facingIn.getIndex()];
      }

      static {
         VALUES[EnumFacing.DOWN.getIndex()] = DOWN;
         VALUES[EnumFacing.UP.getIndex()] = UP;
         VALUES[EnumFacing.NORTH.getIndex()] = NORTH;
         VALUES[EnumFacing.SOUTH.getIndex()] = SOUTH;
         VALUES[EnumFacing.WEST.getIndex()] = WEST;
         VALUES[EnumFacing.EAST.getIndex()] = EAST;
      }

      EnumNeighborInfo(
         EnumFacing[] enumFacings,
         float f,
         boolean flag,
         BlockModelRenderer.Orientation[] orientations,
         BlockModelRenderer.Orientation[] orientations2,
         BlockModelRenderer.Orientation[] orientations3,
         BlockModelRenderer.Orientation[] orientations4
      ) {
         this.neighborFacings = enumFacings;
         this.shadeWeight = f;
         this.useShape = flag;
         this.orientationLookup0 = orientations;
         this.orientationLookup1 = orientations2;
         this.orientationLookup2 = orientations3;
         this.orientationLookup3 = orientations4;
      }
   }

   public enum Orientation {
      DOWN(EnumFacing.DOWN, false),
      UP(EnumFacing.UP, false),
      NORTH(EnumFacing.NORTH, false),
      SOUTH(EnumFacing.SOUTH, false),
      WEST(EnumFacing.WEST, false),
      EAST(EnumFacing.EAST, false),
      FLIP_DOWN(EnumFacing.DOWN, true),
      FLIP_UP(EnumFacing.UP, true),
      FLIP_NORTH(EnumFacing.NORTH, true),
      FLIP_SOUTH(EnumFacing.SOUTH, true),
      FLIP_WEST(EnumFacing.WEST, true),
      FLIP_EAST(EnumFacing.EAST, true);

      protected final int boundsIndex;

      Orientation(EnumFacing enumFacing, boolean flag) {
         this.boundsIndex = enumFacing.getIndex() + (flag ? EnumFacing.values().length : 0);
      }
   }

   enum VertexTranslations {
      DOWN(0, 1, 2, 3),
      UP(2, 3, 0, 1),
      NORTH(3, 0, 1, 2),
      SOUTH(0, 1, 2, 3),
      WEST(3, 0, 1, 2),
      EAST(1, 2, 3, 0);

      private final int vertex0Index;
      private final int vertex1Index;
      private final int vertex2Index;
      private static final BlockModelRenderer.VertexTranslations[] VALUES = new BlockModelRenderer.VertexTranslations[6];
      private final int vertex3Index;

      public static BlockModelRenderer.VertexTranslations getVertexTranslations(EnumFacing facingIn) {
         return VALUES[facingIn.getIndex()];
      }

      VertexTranslations(int i, int i2, int i3, int i4) {
         this.vertex0Index = i;
         this.vertex1Index = i2;
         this.vertex2Index = i3;
         this.vertex3Index = i4;
      }

      static {
         VALUES[EnumFacing.DOWN.getIndex()] = DOWN;
         VALUES[EnumFacing.UP.getIndex()] = UP;
         VALUES[EnumFacing.NORTH.getIndex()] = NORTH;
         VALUES[EnumFacing.SOUTH.getIndex()] = SOUTH;
         VALUES[EnumFacing.WEST.getIndex()] = WEST;
         VALUES[EnumFacing.EAST.getIndex()] = EAST;
      }
   }
}
