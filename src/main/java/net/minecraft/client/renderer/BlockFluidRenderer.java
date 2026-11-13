package net.minecraft.client.renderer;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.optifine.CustomColors;
import net.optifine.render.RenderEnv;
import net.optifine.shaders.SVertexBuilder;

public class BlockFluidRenderer {
   private TextureAtlasSprite[] atlasSpritesLava = new TextureAtlasSprite[2];
   private TextureAtlasSprite[] atlasSpritesWater = new TextureAtlasSprite[2];

   public boolean renderFluid(IBlockAccess blockAccess, IBlockState blockStateIn, BlockPos blockPosIn, WorldRenderer worldRendererIn) {
      try {
         if (Config.isShaders()) {
            SVertexBuilder.pushEntity(blockStateIn, blockPosIn, blockAccess, worldRendererIn);
         }

         BlockLiquid blockLiquid = (BlockLiquid)blockStateIn.getBlock();
         blockLiquid.setBlockBoundsBasedOnState(blockAccess, blockPosIn);
         TextureAtlasSprite[] textureAtlasSprites = blockLiquid.getMaterial() == Material.lava ? this.atlasSpritesLava : this.atlasSpritesWater;
         RenderEnv renderEnv = worldRendererIn.getRenderEnv(blockStateIn, blockPosIn);
         int fluidColor = CustomColors.getFluidColor(blockAccess, blockStateIn, blockPosIn, renderEnv);
         float f = (fluidColor >> 16 & 0xFF) / 255.0F;
         float f2 = (fluidColor >> 8 & 0xFF) / 255.0F;
         float f3 = (fluidColor & 0xFF) / 255.0F;
         int i = (blockLiquid.shouldSideBeRendered(blockAccess, blockPosIn.up(), EnumFacing.UP) ? 1 : 0);
         int i2 = (blockLiquid.shouldSideBeRendered(blockAccess, blockPosIn.down(), EnumFacing.DOWN) ? 1 : 0);
         boolean[] borderFlags = renderEnv.getBorderFlags();
         borderFlags[0] = blockLiquid.shouldSideBeRendered(blockAccess, blockPosIn.north(), EnumFacing.NORTH);
         borderFlags[1] = blockLiquid.shouldSideBeRendered(blockAccess, blockPosIn.south(), EnumFacing.SOUTH);
         borderFlags[2] = blockLiquid.shouldSideBeRendered(blockAccess, blockPosIn.west(), EnumFacing.WEST);
         borderFlags[3] = blockLiquid.shouldSideBeRendered(blockAccess, blockPosIn.east(), EnumFacing.EAST);
         if (i != 0 || i2 != 0 || borderFlags[0] || borderFlags[1] || borderFlags[2] || borderFlags[3]) {
            boolean flag = false;
            float f4 = 1.0F;
            Material material2 = blockLiquid.getMaterial();
            float fluidHeight = this.getFluidHeight(blockAccess, blockPosIn, material2);
            float fluidHeight2 = this.getFluidHeight(blockAccess, blockPosIn.south(), material2);
            float fluidHeight3 = this.getFluidHeight(blockAccess, blockPosIn.east().south(), material2);
            float fluidHeight4 = this.getFluidHeight(blockAccess, blockPosIn.east(), material2);
            double x = blockPosIn.getX();
            double y = blockPosIn.getY();
            double z = blockPosIn.getZ();
            float f5 = 0.001F;
            if (i != 0) {
               flag = true;
               TextureAtlasSprite textureAtlasSprite = textureAtlasSprites[0];
               float f6 = (float)BlockLiquid.getFlowDirection(blockAccess, blockPosIn, material2);
               if (f6 > -999.0F) {
                  textureAtlasSprite = textureAtlasSprites[1];
               }

               worldRendererIn.setSprite(textureAtlasSprite);
               fluidHeight -= f5;
               fluidHeight2 -= f5;
               fluidHeight3 -= f5;
               fluidHeight4 -= f5;
               float f7;
               float f8;
               float f9;
               float f10;
               float f11;
               float f12;
               float f13;
               float f14;
               if (f6 < -999.0F) {
                  f7 = textureAtlasSprite.getInterpolatedU(0.0);
                  f11 = textureAtlasSprite.getInterpolatedV(0.0);
                  f8 = f7;
                  f12 = textureAtlasSprite.getInterpolatedV(16.0);
                  f9 = textureAtlasSprite.getInterpolatedU(16.0);
                  f13 = f12;
                  f10 = f9;
                  f14 = f11;
               } else {
                  float f15 = MathHelper.sin(f6) * 0.25F;
                  float f16 = MathHelper.cos(f6) * 0.25F;
                  f7 = textureAtlasSprite.getInterpolatedU(8.0F + (-f16 - f15) * 16.0F);
                  f11 = textureAtlasSprite.getInterpolatedV(8.0F + (-f16 + f15) * 16.0F);
                  f8 = textureAtlasSprite.getInterpolatedU(8.0F + (-f16 + f15) * 16.0F);
                  f12 = textureAtlasSprite.getInterpolatedV(8.0F + (f16 + f15) * 16.0F);
                  f9 = textureAtlasSprite.getInterpolatedU(8.0F + (f16 + f15) * 16.0F);
                  f13 = textureAtlasSprite.getInterpolatedV(8.0F + (f16 - f15) * 16.0F);
                  f10 = textureAtlasSprite.getInterpolatedU(8.0F + (f16 - f15) * 16.0F);
                  f14 = textureAtlasSprite.getInterpolatedV(8.0F + (-f16 - f15) * 16.0F);
               }

               int mixedBrightnessForBlock = blockLiquid.getMixedBrightnessForBlock(blockAccess, blockPosIn);
               int i3 = mixedBrightnessForBlock >> 16 & 65535;
               int i4 = mixedBrightnessForBlock & 65535;
               float f17 = f4 * f;
               float f18 = f4 * f2;
               float f19 = f4 * f3;
               worldRendererIn.pos(x + 0.0, y + fluidHeight, z + 0.0)
                  .color(f17, f18, f19, 1.0F)
                  .tex(f7, f11)
                  .lightmap(i3, i4)
                  .endVertex();
               worldRendererIn.pos(x + 0.0, y + fluidHeight2, z + 1.0)
                  .color(f17, f18, f19, 1.0F)
                  .tex(f8, f12)
                  .lightmap(i3, i4)
                  .endVertex();
               worldRendererIn.pos(x + 1.0, y + fluidHeight3, z + 1.0)
                  .color(f17, f18, f19, 1.0F)
                  .tex(f9, f13)
                  .lightmap(i3, i4)
                  .endVertex();
               worldRendererIn.pos(x + 1.0, y + fluidHeight4, z + 0.0)
                  .color(f17, f18, f19, 1.0F)
                  .tex(f10, f14)
                  .lightmap(i3, i4)
                  .endVertex();
               if (blockLiquid.shouldRenderSides(blockAccess, blockPosIn.up())) {
                  worldRendererIn.pos(x + 0.0, y + fluidHeight, z + 0.0)
                     .color(f17, f18, f19, 1.0F)
                     .tex(f7, f11)
                     .lightmap(i3, i4)
                     .endVertex();
                  worldRendererIn.pos(x + 1.0, y + fluidHeight4, z + 0.0)
                     .color(f17, f18, f19, 1.0F)
                     .tex(f10, f14)
                     .lightmap(i3, i4)
                     .endVertex();
                  worldRendererIn.pos(x + 1.0, y + fluidHeight3, z + 1.0)
                     .color(f17, f18, f19, 1.0F)
                     .tex(f9, f13)
                     .lightmap(i3, i4)
                     .endVertex();
                  worldRendererIn.pos(x + 0.0, y + fluidHeight2, z + 1.0)
                     .color(f17, f18, f19, 1.0F)
                     .tex(f8, f12)
                     .lightmap(i3, i4)
                     .endVertex();
               }
            }

            if (i2 != 0) {
               worldRendererIn.setSprite(textureAtlasSprites[0]);
               float minU = textureAtlasSprites[0].getMinU();
               float maxU = textureAtlasSprites[0].getMaxU();
               float minV = textureAtlasSprites[0].getMinV();
               float maxV = textureAtlasSprites[0].getMaxV();
               int mixedBrightnessForBlock2 = blockLiquid.getMixedBrightnessForBlock(blockAccess, blockPosIn.down());
               int i5 = mixedBrightnessForBlock2 >> 16 & 65535;
               int i6 = mixedBrightnessForBlock2 & 65535;
               float faceBrightness = FaceBakery.getFaceBrightness(EnumFacing.DOWN);
               worldRendererIn.pos(x, y, z + 1.0)
                  .color(f * faceBrightness, f2 * faceBrightness, f3 * faceBrightness, 1.0F)
                  .tex(minU, maxV)
                  .lightmap(i5, i6)
                  .endVertex();
               worldRendererIn.pos(x, y, z)
                  .color(f * faceBrightness, f2 * faceBrightness, f3 * faceBrightness, 1.0F)
                  .tex(minU, minV)
                  .lightmap(i5, i6)
                  .endVertex();
               worldRendererIn.pos(x + 1.0, y, z)
                  .color(f * faceBrightness, f2 * faceBrightness, f3 * faceBrightness, 1.0F)
                  .tex(maxU, minV)
                  .lightmap(i5, i6)
                  .endVertex();
               worldRendererIn.pos(x + 1.0, y, z + 1.0)
                  .color(f * faceBrightness, f2 * faceBrightness, f3 * faceBrightness, 1.0F)
                  .tex(maxU, maxV)
                  .lightmap(i5, i6)
                  .endVertex();
               flag = true;
            }

            for (int i7 = 0; i7 < 4; i7++) {
               int i8 = 0;
               int i9 = 0;
               if (i7 == 0) {
                  i9--;
               }

               if (i7 == 1) {
                  i9++;
               }

               if (i7 == 2) {
                  i8--;
               }

               if (i7 == 3) {
                  i8++;
               }

               BlockPos blockPos2 = blockPosIn.add(i8, 0, i9);
               TextureAtlasSprite textureAtlasSprite2 = textureAtlasSprites[1];
               worldRendererIn.setSprite(textureAtlasSprite2);
               if (borderFlags[i7]) {
                  float f20;
                  float f21;
                  double d;
                  double d2;
                  double d3;
                  double d4;
                  if (i7 == 0) {
                     f20 = fluidHeight;
                     f21 = fluidHeight4;
                     d = x;
                     d3 = x + 1.0;
                     d2 = z + f5;
                     d4 = z + f5;
                  } else if (i7 == 1) {
                     f20 = fluidHeight3;
                     f21 = fluidHeight2;
                     d = x + 1.0;
                     d3 = x;
                     d2 = z + 1.0 - f5;
                     d4 = z + 1.0 - f5;
                  } else if (i7 == 2) {
                     f20 = fluidHeight2;
                     f21 = fluidHeight;
                     d = x + f5;
                     d3 = x + f5;
                     d2 = z + 1.0;
                     d4 = z;
                  } else {
                     f20 = fluidHeight4;
                     f21 = fluidHeight3;
                     d = x + 1.0 - f5;
                     d3 = x + 1.0 - f5;
                     d2 = z;
                     d4 = z + 1.0;
                  }

                  flag = true;
                  float interpolatedU = textureAtlasSprite2.getInterpolatedU(0.0);
                  float interpolatedU2 = textureAtlasSprite2.getInterpolatedU(8.0);
                  float interpolatedV = textureAtlasSprite2.getInterpolatedV((1.0F - f20) * 16.0F * 0.5F);
                  float interpolatedV2 = textureAtlasSprite2.getInterpolatedV((1.0F - f21) * 16.0F * 0.5F);
                  float interpolatedV3 = textureAtlasSprite2.getInterpolatedV(8.0);
                  int mixedBrightnessForBlock3 = blockLiquid.getMixedBrightnessForBlock(blockAccess, blockPos2);
                  int i10 = mixedBrightnessForBlock3 >> 16 & 65535;
                  int i11 = mixedBrightnessForBlock3 & 65535;
                  float f22 = i7 < 2 ? FaceBakery.getFaceBrightness(EnumFacing.NORTH) : FaceBakery.getFaceBrightness(EnumFacing.WEST);
                  float f23 = f4 * f22 * f;
                  float f24 = f4 * f22 * f2;
                  float f25 = f4 * f22 * f3;
                  worldRendererIn.pos(d, y + f20, d2).color(f23, f24, f25, 1.0F).tex(interpolatedU, interpolatedV).lightmap(i10, i11).endVertex();
                  worldRendererIn.pos(d3, y + f21, d4).color(f23, f24, f25, 1.0F).tex(interpolatedU2, interpolatedV2).lightmap(i10, i11).endVertex();
                  worldRendererIn.pos(d3, y + 0.0, d4).color(f23, f24, f25, 1.0F).tex(interpolatedU2, interpolatedV3).lightmap(i10, i11).endVertex();
                  worldRendererIn.pos(d, y + 0.0, d2).color(f23, f24, f25, 1.0F).tex(interpolatedU, interpolatedV3).lightmap(i10, i11).endVertex();
                  worldRendererIn.pos(d, y + 0.0, d2).color(f23, f24, f25, 1.0F).tex(interpolatedU, interpolatedV3).lightmap(i10, i11).endVertex();
                  worldRendererIn.pos(d3, y + 0.0, d4).color(f23, f24, f25, 1.0F).tex(interpolatedU2, interpolatedV3).lightmap(i10, i11).endVertex();
                  worldRendererIn.pos(d3, y + f21, d4).color(f23, f24, f25, 1.0F).tex(interpolatedU2, interpolatedV2).lightmap(i10, i11).endVertex();
                  worldRendererIn.pos(d, y + f20, d2).color(f23, f24, f25, 1.0F).tex(interpolatedU, interpolatedV).lightmap(i10, i11).endVertex();
               }
            }

            worldRendererIn.setSprite((TextureAtlasSprite)null);
            return flag;
         }
      } finally {
         if (Config.isShaders()) {
            SVertexBuilder.popEntity(worldRendererIn);
         }
      }

      return false;
   }

   public BlockFluidRenderer() {
      this.initAtlasSprites();
   }

   protected void initAtlasSprites() {
      TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
      this.atlasSpritesLava[0] = textureMap.getAtlasSprite("minecraft:blocks/lava_still");
      this.atlasSpritesLava[1] = textureMap.getAtlasSprite("minecraft:blocks/lava_flow");
      this.atlasSpritesWater[0] = textureMap.getAtlasSprite("minecraft:blocks/water_still");
      this.atlasSpritesWater[1] = textureMap.getAtlasSprite("minecraft:blocks/water_flow");
   }

   private float getFluidHeight(IBlockAccess blockAccess, BlockPos blockPosIn, Material blockMaterial) {
      int i = 0;
      float f = 0.0F;

      for (int innerIndex = 0; innerIndex < 4; innerIndex++) {
         BlockPos blockPos = blockPosIn.add(-(innerIndex & 1), 0, -(innerIndex >> 1 & 1));
         if (blockAccess.getBlockState(blockPos.up()).getBlock().getMaterial() == blockMaterial) {
            return 1.0F;
         }

         IBlockState blockState = blockAccess.getBlockState(blockPos);
         Material material = blockState.getBlock().getMaterial();
         if (material != blockMaterial) {
            if (!material.isSolid()) {
               f++;
               i++;
            }
         } else {
            int value = blockState.getValue(BlockLiquid.LEVEL);
            if (value >= 8 || value == 0) {
               f += BlockLiquid.getLiquidHeightPercent(value) * 10.0F;
               i += 10;
            }

            f += BlockLiquid.getLiquidHeightPercent(value);
            i++;
         }
      }

      return 1.0F - f / i;
   }
}
