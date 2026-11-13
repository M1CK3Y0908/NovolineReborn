package net.minecraft.client.particle;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityDiggingFX extends EntityFX {
   private BlockPos sourcePos;
   private IBlockState sourceState;

   @Override
   public int getFXLayer() {
      return 1;
   }

   public EntityDiggingFX setBlockPos(BlockPos pos) {
      this.sourcePos = pos;
      if (this.sourceState.getBlock() == Blocks.grass) {
         return this;
      }

      int i = this.sourceState.getBlock().colorMultiplier(this.worldObj, pos);
      this.particleRed *= (i >> 16 & 0xFF) / 255.0F;
      this.particleGreen *= (i >> 8 & 0xFF) / 255.0F;
      this.particleBlue *= (i & 0xFF) / 255.0F;
      return this;
   }

   protected EntityDiggingFX(
      World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, IBlockState state
   ) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      this.sourceState = state;
      this.setParticleIcon(Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state));
      this.particleGravity = state.getBlock().blockParticleGravity;
      this.particleRed = this.particleGreen = this.particleBlue = 0.6F;
      this.particleScale /= 2.0F;
   }

   public EntityDiggingFX colorizeFromSourceBlock() {
      this.sourcePos = new BlockPos(this.posX, this.posY, this.posZ);
      Block block = this.sourceState.getBlock();
      if (block == Blocks.grass) {
         return this;
      }

      int renderColor = block.getRenderColor(this.sourceState);
      this.particleRed *= (renderColor >> 16 & 0xFF) / 255.0F;
      this.particleGreen *= (renderColor >> 8 & 0xFF) / 255.0F;
      this.particleBlue *= (renderColor & 0xFF) / 255.0F;
      return this;
   }

   @Override
   public void renderParticle(
      WorldRenderer worldRendererIn,
      Entity entityIn,
      float partialTicks,
      float rotationX,
      float rotationZ,
      float rotationYZ,
      float rotationXY,
      float rotationXZ
   ) {
      float f = (this.particleTextureIndexX + this.particleTextureJitterX / 4.0F) / 16.0F;
      float f2 = f + 0.015609375F;
      float f3 = (this.particleTextureIndexY + this.particleTextureJitterY / 4.0F) / 16.0F;
      float f4 = f3 + 0.015609375F;
      float f5 = 0.1F * this.particleScale;
      if (this.particleIcon != null) {
         f = this.particleIcon.getInterpolatedU(this.particleTextureJitterX / 4.0F * 16.0F);
         f2 = this.particleIcon.getInterpolatedU((this.particleTextureJitterX + 1.0F) / 4.0F * 16.0F);
         f3 = this.particleIcon.getInterpolatedV(this.particleTextureJitterY / 4.0F * 16.0F);
         f4 = this.particleIcon.getInterpolatedV((this.particleTextureJitterY + 1.0F) / 4.0F * 16.0F);
      }

      float f6 = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
      float f7 = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
      float f8 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
      int brightnessForRender = this.getBrightnessForRender(partialTicks);
      int i = brightnessForRender >> 16 & 65535;
      int i2 = brightnessForRender & 65535;
      worldRendererIn.pos(
            f6 - rotationX * f5 - rotationXY * f5,
            f7 - rotationZ * f5,
            f8 - rotationYZ * f5 - rotationXZ * f5
         )
         .tex(f, f4)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(i, i2)
         .endVertex();
      worldRendererIn.pos(
            f6 - rotationX * f5 + rotationXY * f5,
            f7 + rotationZ * f5,
            f8 - rotationYZ * f5 + rotationXZ * f5
         )
         .tex(f, f3)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(i, i2)
         .endVertex();
      worldRendererIn.pos(
            f6 + rotationX * f5 + rotationXY * f5,
            f7 + rotationZ * f5,
            f8 + rotationYZ * f5 + rotationXZ * f5
         )
         .tex(f2, f3)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(i, i2)
         .endVertex();
      worldRendererIn.pos(
            f6 + rotationX * f5 - rotationXY * f5,
            f7 - rotationZ * f5,
            f8 + rotationYZ * f5 - rotationXZ * f5
         )
         .tex(f2, f4)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(i, i2)
         .endVertex();
   }

   @Override
   public int getBrightnessForRender(float partialTicks) {
      int brightnessForRender = super.getBrightnessForRender(partialTicks);
      int i = 0;
      if (this.worldObj.isBlockLoaded(this.sourcePos)) {
         i = this.worldObj.getCombinedLight(this.sourcePos, 0);
      }

      return brightnessForRender == 0 ? i : brightnessForRender;
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(World world2, double d, double d2, double d3, double d4, double d5, double d6, int... iArray) {
         return new EntityDiggingFX(world2, d, d2, d3, d4, d5, d6, Block.getStateById(iArray[0])).colorizeFromSourceBlock();
      }
   }
}
