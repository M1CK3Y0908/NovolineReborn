package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFX extends Entity {
   protected float particleAlpha = 1.0F;
   protected int particleTextureIndexX;
   protected float particleBlue;
   protected float particleGreen;
   public static double interpPosX;
   public static double interpPosY;
   protected float particleRed;
   protected TextureAtlasSprite particleIcon;
   protected float particleTextureJitterX;
   protected float particleTextureJitterY;
   protected float particleScale;
   public static double interpPosZ;
   protected float particleGravity;
   protected int particleMaxAge;
   protected int particleTextureIndexY;
   protected int particleAge;

   @Override
   public String toString() {
      String simpleName = this.getClass().getSimpleName();
      int particleAge2 = this.particleAge;
      float particleAlpha2 = this.particleAlpha;
      float particleBlue2 = this.particleBlue;
      float particleGreen2 = this.particleGreen;
      float particleRed2 = this.particleRed;
      double posZ2 = this.posZ;
      double posY2 = this.posY;
      double posX2 = this.posX;
      return simpleName + ", Pos (" + posX2 + "," + posY2 + "," + posZ2 + "), RGBA (" + particleRed2 + "," + particleGreen2 + "," + particleBlue2 + "," + particleAlpha2 + "), Age " + particleAge2;
   }

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
      float f = this.particleTextureIndexX / 16.0F;
      float f2 = f + 0.0624375F;
      float f3 = this.particleTextureIndexY / 16.0F;
      float f4 = f3 + 0.0624375F;
      float f5 = 0.1F * this.particleScale;
      if (this.particleIcon != null) {
         f = this.particleIcon.getMinU();
         f2 = this.particleIcon.getMaxU();
         f3 = this.particleIcon.getMinV();
         f4 = this.particleIcon.getMaxV();
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
         .tex(f2, f4)
         .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
         .lightmap(i, i2)
         .endVertex();
      worldRendererIn.pos(
            f6 - rotationX * f5 + rotationXY * f5,
            f7 + rotationZ * f5,
            f8 - rotationYZ * f5 + rotationXZ * f5
         )
         .tex(f2, f3)
         .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
         .lightmap(i, i2)
         .endVertex();
      worldRendererIn.pos(
            f6 + rotationX * f5 + rotationXY * f5,
            f7 + rotationZ * f5,
            f8 + rotationYZ * f5 + rotationXZ * f5
         )
         .tex(f, f3)
         .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
         .lightmap(i, i2)
         .endVertex();
      worldRendererIn.pos(
            f6 + rotationX * f5 - rotationXY * f5,
            f7 - rotationZ * f5,
            f8 + rotationYZ * f5 - rotationXZ * f5
         )
         .tex(f, f4)
         .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
         .lightmap(i, i2)
         .endVertex();
   }

   public int getFXLayer() {
      return 0;
   }

   public void setParticleTextureIndex(int particleTextureIndex) {
      if (this.getFXLayer() != 0) {
         throw new RuntimeException("Invalid call to Particle.setMiscTex");
      }

      this.particleTextureIndexX = particleTextureIndex % 16;
      this.particleTextureIndexY = particleTextureIndex / 16;
   }

   public void setRBGColorF(float particleRedIn, float particleGreenIn, float particleBlueIn) {
      this.particleRed = particleRedIn;
      this.particleGreen = particleGreenIn;
      this.particleBlue = particleBlueIn;
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setDead();
      }

      this.motionY = this.motionY - 0.04 * this.particleGravity;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.98F;
      this.motionY *= 0.98F;
      this.motionZ *= 0.98F;
      if (this.onGround) {
         this.motionX *= 0.7F;
         this.motionZ *= 0.7F;
      }
   }

   public EntityFX multiplyVelocity(float multiplier) {
      this.motionX *= multiplier;
      this.motionY = (this.motionY - 0.1F) * multiplier + 0.1F;
      this.motionZ *= multiplier;
      return this;
   }

   public EntityFX multipleParticleScaleBy(float scale) {
      this.setSize(0.2F * scale, 0.2F * scale);
      this.particleScale *= scale;
      return this;
   }

   public void setAlphaF(float alpha) {
      if (this.particleAlpha == 1.0F && alpha < 1.0F) {
         Minecraft.getMinecraft().effectRenderer.moveToAlphaLayer(this);
      } else if (this.particleAlpha < 1.0F && alpha == 1.0F) {
         Minecraft.getMinecraft().effectRenderer.moveToNoAlphaLayer(this);
      }

      this.particleAlpha = alpha;
   }

   public float getParticleRed() {
      return this.particleRed;
   }

   @Override
   protected boolean canTriggerWalking() {
      return false;
   }

   @Override
   protected void entityInit() {
   }

   public float getParticleGreen() {
      return this.particleGreen;
   }

   public void nextTextureIndexX() {
      this.particleTextureIndexX++;
   }

   public float getParticleBlue() {
      return this.particleBlue;
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
   }

   public EntityFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
      this(worldIn, xCoordIn, yCoordIn, zCoordIn);
      this.motionX = xSpeedIn + (Math.random() * 2.0 - 1.0) * 0.4F;
      this.motionY = ySpeedIn + (Math.random() * 2.0 - 1.0) * 0.4F;
      this.motionZ = zSpeedIn + (Math.random() * 2.0 - 1.0) * 0.4F;
      float f = (float)(Math.random() + Math.random() + 1.0) * 0.15F;
      float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
      this.motionX = this.motionX / f2 * f * 0.4F;
      this.motionY = this.motionY / f2 * f * 0.4F + 0.1F;
      this.motionZ = this.motionZ / f2 * f * 0.4F;
   }

   protected EntityFX(World worldIn, double posXIn, double posYIn, double posZIn) {
      super(worldIn);
      this.setSize(0.2F, 0.2F);
      this.setPosition(posXIn, posYIn, posZIn);
      this.lastTickPosX = this.prevPosX = posXIn;
      this.lastTickPosY = this.prevPosY = posYIn;
      this.lastTickPosZ = this.prevPosZ = posZIn;
      this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
      this.particleTextureJitterX = this.rand.nextFloat() * 3.0F;
      this.particleTextureJitterY = this.rand.nextFloat() * 3.0F;
      this.particleScale = (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;
      this.particleMaxAge = (int)(4.0F / (this.rand.nextFloat() * 0.9F + 0.1F));
      this.particleAge = 0;
   }

   public void setParticleIcon(TextureAtlasSprite icon) {
      if (this.getFXLayer() == 1) {
         this.particleIcon = icon;
      } else {
         throw new RuntimeException("Invalid call to Particle.setTex, use coordinate methods");
      }
   }

   @Override
   public boolean canAttackWithItem() {
      return false;
   }

   public float getAlpha() {
      return this.particleAlpha;
   }
}
