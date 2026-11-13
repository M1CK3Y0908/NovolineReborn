package wtf.novoline.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemDye;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class FireworkEffects {
   public static class EntityFXChild extends EntityFX {
      private final EffectRenderer theEffectRenderer;
      private NBTTagList fireworkExplosions;
      private int fireworkAge;
      boolean twinkle;

      private void createBall(double speed, int size, int[] colours, int[] fadeColours, boolean trail, boolean twinkleIn) {
         double xCoordinate = this.posX;
         double yCoordinate = this.posY;
         double zCoordinate = this.posZ;

         for (int index = -size; index <= size; index++) {
            for (int innerIndex = -size; innerIndex <= size; innerIndex++) {
               for (int nestedIndex = -size; nestedIndex <= size; nestedIndex++) {
                  double d = innerIndex + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5;
                  double d2 = index + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5;
                  double d3 = nestedIndex + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5;
                  double d4 = MathHelper.sqrt_double(d * d + d2 * d2 + d3 * d3) / speed
                     + this.rand.nextGaussian() * 0.05;
                  this.createParticle(
                     xCoordinate,
                     yCoordinate,
                     zCoordinate,
                     d / d4,
                     d2 / d4,
                     d3 / d4,
                     colours,
                     fadeColours,
                     trail,
                     twinkleIn
                  );
                  if (index != -size && index != size && innerIndex != -size && innerIndex != size) {
                     nestedIndex += size * 2 - 1;
                  }
               }
            }
         }
      }

      @Override
      public int getFXLayer() {
         return 0;
      }

      public EntityFXChild(
         World worldIn,
         double xCoord,
         double yCoord,
         double zCoord,
         double motionX,
         double motionY,
         double motionZ,
         EffectRenderer effectRendererIn,
         NBTTagCompound fireworkTag
      ) {
         super(worldIn, xCoord, yCoord, zCoord, 0.0, 0.0, 0.0);
         this.motionX = motionX;
         this.motionY = motionY;
         this.motionZ = motionZ;
         this.theEffectRenderer = effectRendererIn;
         this.particleMaxAge = 8;
         if (fireworkTag != null) {
            this.fireworkExplosions = fireworkTag.getTagList("Explosions", 10);
            if (this.fireworkExplosions.tagCount() == 0) {
               this.fireworkExplosions = null;
            } else {
               this.particleMaxAge = this.fireworkExplosions.tagCount() * 2 - 1;

               for (int i = 0; i < this.fireworkExplosions.tagCount(); i++) {
                  if (this.fireworkExplosions.getCompoundTagAt(i).getBoolean("Flicker")) {
                     this.twinkle = true;
                     this.particleMaxAge += 15;
                     break;
                  }
               }
            }
         }
      }

      private void createParticle(
         double x, double y, double z, double motionX, double motionY, double motionZ, int[] colours, int[] fadeColours, boolean trail, boolean twinkleIn
      ) {
         FireworkEffects.EntityFXChild3 entityFXChild3 = new FireworkEffects.EntityFXChild3(this.worldObj, x, y, z, motionX, motionY, motionZ, this.theEffectRenderer);
         entityFXChild3.setAlphaF(0.99F);
         entityFXChild3.setTrail(trail);
         entityFXChild3.setTwinkle(twinkleIn);
         int count = this.rand.nextInt(colours.length);
         entityFXChild3.setColour(colours[count]);
         if (fadeColours != null && fadeColours.length > 0) {
            entityFXChild3.setFadeColour(fadeColours[this.rand.nextInt(fadeColours.length)]);
         }

         this.theEffectRenderer.addEffect(entityFXChild3);
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
      }

      private void createBurst(int[] colours, int[] fadeColours, boolean trail, boolean twinkleIn) {
         double d = this.rand.nextGaussian() * 0.05;
         double d2 = this.rand.nextGaussian() * 0.05;

         for (int index = 0; index < 70; index++) {
            double d3 = this.motionX * 0.5 + this.rand.nextGaussian() * 0.15 + d;
            double d4 = this.motionZ * 0.5 + this.rand.nextGaussian() * 0.15 + d2;
            double d5 = this.motionY * 0.5 + this.rand.nextDouble() * 0.5;
            this.createParticle(this.posX, this.posY, this.posZ, d3, d5, d4, colours, fadeColours, trail, twinkleIn);
         }
      }

      private boolean isFarAwayFromPlayer() {
         Minecraft minecraft = Minecraft.getMinecraft();
         return minecraft == null
            || minecraft.getRenderViewEntity() == null
            || minecraft.getRenderViewEntity().getDistanceSq(this.posX, this.posY, this.posZ) >= 256.0;
      }

      private void createShaped(double speed, double[][] shape, int[] colours, int[] fadeColours, boolean trail, boolean twinkleIn, boolean isCreeperShape) {
         double d = shape[0][0];
         double d2 = shape[0][1];
         this.createParticle(this.posX, this.posY, this.posZ, d * speed, d2 * speed, 0.0, colours, fadeColours, trail, twinkleIn);
         float f = this.rand.nextFloat() * (float) Math.PI;
         double d3 = isCreeperShape ? 0.034 : 0.34;

         for (int i = 0; i < 3; i++) {
            double d4 = f + i * (float) Math.PI * d3;
            double d5 = d;
            double d6 = d2;

            for (int i2 = 1; i2 < shape.length; i2++) {
               double d7 = shape[i2][0];
               double d8 = shape[i2][1];

               for (double d9 = 0.25; d9 <= 1.0; d9 += 0.25) {
                  double d10 = (d5 + (d7 - d5) * d9) * speed;
                  double d11 = (d6 + (d8 - d6) * d9) * speed;
                  double d12 = d10 * Math.sin(d4);
                  d10 *= Math.cos(d4);

                  for (double d13 = -1.0; d13 <= 1.0; d13 += 2.0) {
                     this.createParticle(this.posX, this.posY, this.posZ, d10 * d13, d11, d12 * d13, colours, fadeColours, trail, twinkleIn);
                  }
               }

               d5 = d7;
               d6 = d8;
            }
         }
      }

      @Override
      public void onUpdate() {
         if (this.fireworkAge == 0 && this.fireworkExplosions != null) {
            boolean isFarAwayFromPlayer = this.isFarAwayFromPlayer();
            boolean conditionMet = false;
            if (this.fireworkExplosions.tagCount() >= 3) {
               conditionMet = true;
            } else {
               for (int index = 0; index < this.fireworkExplosions.tagCount(); index++) {
                  NBTTagCompound nBTTagCompound = this.fireworkExplosions.getCompoundTagAt(index);
                  if (nBTTagCompound.getByte("Type") == 1) {
                     conditionMet = true;
                     break;
                  }
               }
            }

            String text = "fireworks." + (conditionMet ? "largeBlast" : "blast") + (isFarAwayFromPlayer ? "_far" : "");
            this.worldObj.playSound(this.posX, this.posY, this.posZ, text, 20.0F, 0.95F + this.rand.nextFloat() * 0.1F, true);
         }

         if (this.fireworkAge % 2 == 0 && this.fireworkExplosions != null && this.fireworkAge / 2 < this.fireworkExplosions.tagCount()) {
            int i = this.fireworkAge / 2;
            NBTTagCompound nBTTagCompound2 = this.fireworkExplosions.getCompoundTagAt(i);
            int byteValue = nBTTagCompound2.getByte("Type");
            boolean conditionMet2 = nBTTagCompound2.getBoolean("Trail");
            boolean conditionMet3 = nBTTagCompound2.getBoolean("Flicker");
            int[] intArray = nBTTagCompound2.getIntArray("Colors");
            int[] intArray1 = nBTTagCompound2.getIntArray("FadeColors");
            if (intArray.length == 0) {
               intArray = new int[]{ItemDye.dyeColors[0]};
            }

            if (byteValue == 1) {
               this.createBall(0.5, 4, intArray, intArray1, conditionMet2, conditionMet3);
            } else if (byteValue == 2) {
               this.createShaped(
                  0.5,
                  new double[][]{
                     {0.0, 1.0},
                     {0.3455, 0.309},
                     {0.9511, 0.309},
                     {0.3795918367346939, -0.12653061224489795},
                     {0.6122448979591837, -0.8040816326530612},
                     {0.0, -0.35918367346938773}
                  },
                  intArray,
                  intArray1,
                  conditionMet2,
                  conditionMet3,
                  false
               );
            } else if (byteValue == 3) {
               this.createShaped(
                  0.5,
                  new double[][]{
                     {0.0, 0.2},
                     {0.2, 0.2},
                     {0.2, 0.6},
                     {0.6, 0.6},
                     {0.6, 0.2},
                     {0.2, 0.2},
                     {0.2, 0.0},
                     {0.4, 0.0},
                     {0.4, -0.6},
                     {0.2, -0.6},
                     {0.2, -0.4},
                     {0.0, -0.4}
                  },
                  intArray,
                  intArray1,
                  conditionMet2,
                  conditionMet3,
                  true
               );
            } else if (byteValue == 4) {
               this.createBurst(intArray, intArray1, conditionMet2, conditionMet3);
            } else {
               this.createBall(0.25, 2, intArray, intArray1, conditionMet2, conditionMet3);
            }

            int i2 = intArray[0];
            float f = ((i2 & 0xFF0000) >> 16) / 255.0F;
            float f2 = ((i2 & 0xFF00) >> 8) / 255.0F;
            float f3 = ((i2 & 0xFF) >> 0) / 255.0F;
            FireworkEffects.EntityFXChild2 entityFXChild2 = new FireworkEffects.EntityFXChild2(this.worldObj, this.posX, this.posY, this.posZ);
            entityFXChild2.setRBGColorF(f, f2, f3);
            this.theEffectRenderer.addEffect(entityFXChild2);
         }

         this.fireworkAge++;
         if (this.fireworkAge > this.particleMaxAge) {
            if (this.twinkle) {
               boolean isFarAwayFromPlayer2 = this.isFarAwayFromPlayer();
               String text2 = "fireworks." + (isFarAwayFromPlayer2 ? "twinkle_far" : "twinkle");
               this.worldObj.playSound(this.posX, this.posY, this.posZ, text2, 20.0F, 0.9F + this.rand.nextFloat() * 0.15F, true);
            }

            this.setDead();
         }
      }
   }

   public static class EntityFXChild2 extends EntityFX {
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
         float f = 7.1F * MathHelper.sin((this.particleAge + partialTicks - 1.0F) * 0.25F * (float) Math.PI);
         this.particleAlpha = 0.6F - (this.particleAge + partialTicks - 1.0F) * 0.25F * 0.5F;
         float f2 = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
         float f3 = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
         float f4 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
         int brightnessForRender = this.getBrightnessForRender(partialTicks);
         int i = brightnessForRender >> 16 & 65535;
         int i2 = brightnessForRender & 65535;
         worldRendererIn.pos(f2 - rotationX * f - rotationXY * f, f3 - rotationZ * f, f4 - rotationYZ * f - rotationXZ * f)
            .tex(0.5, 0.375)
            .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
            .lightmap(i, i2)
            .endVertex();
         worldRendererIn.pos(f2 - rotationX * f + rotationXY * f, f3 + rotationZ * f, f4 - rotationYZ * f + rotationXZ * f)
            .tex(0.5, 0.125)
            .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
            .lightmap(i, i2)
            .endVertex();
         worldRendererIn.pos(f2 + rotationX * f + rotationXY * f, f3 + rotationZ * f, f4 + rotationYZ * f + rotationXZ * f)
            .tex(0.25, 0.125)
            .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
            .lightmap(i, i2)
            .endVertex();
         worldRendererIn.pos(f2 + rotationX * f - rotationXY * f, f3 - rotationZ * f, f4 + rotationYZ * f - rotationXZ * f)
            .tex(0.25, 0.375)
            .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
            .lightmap(i, i2)
            .endVertex();
      }

      protected EntityFXChild2(World worldIn, double xCoord, double yCoord, double zCoord) {
         super(worldIn, xCoord, yCoord, zCoord);
         this.particleMaxAge = 4;
      }
   }

   public static class EntityFXChild3 extends EntityFX {
      private float fadeColourGreen;
      private boolean twinkle;
      private int baseTextureIndex = 160;
      private final EffectRenderer effectRenderer;
      private boolean trail;
      private boolean hasFadeColour;
      private float fadeColourBlue;
      private float fadeColourRed;

      public EntityFXChild3(
         World worldIn, double xCoord, double yCoord, double zCoord, double motionX, double motionY, double motionZ, EffectRenderer effectRendererIn
      ) {
         super(worldIn, xCoord, yCoord, zCoord);
         this.motionX = motionX;
         this.motionY = motionY;
         this.motionZ = motionZ;
         this.effectRenderer = effectRendererIn;
         this.particleScale *= 0.75F;
         this.particleMaxAge = 48 + this.rand.nextInt(12);
         this.noClip = false;
      }

      public void setFadeColour(int faceColour) {
         this.fadeColourRed = ((faceColour & 0xFF0000) >> 16) / 255.0F;
         this.fadeColourGreen = ((faceColour & 0xFF00) >> 8) / 255.0F;
         this.fadeColourBlue = ((faceColour & 0xFF) >> 0) / 255.0F;
         this.hasFadeColour = true;
      }

      public void setTrail(boolean trailIn) {
         this.trail = trailIn;
      }

      public void setColour(int colour) {
         float f = ((colour & 0xFF0000) >> 16) / 255.0F;
         float f2 = ((colour & 0xFF00) >> 8) / 255.0F;
         float f3 = ((colour & 0xFF) >> 0) / 255.0F;
         float f4 = 1.0F;
         this.setRBGColorF(f * f4, f2 * f4, f3 * f4);
      }

      public void setTwinkle(boolean twinkleIn) {
         this.twinkle = twinkleIn;
      }

      @Override
      public int getBrightnessForRender(float partialTicks) {
         return 15728880;
      }

      @Override
      public boolean canBePushed() {
         return false;
      }

      @Override
      public AxisAlignedBB getCollisionBoundingBox() {
         return null;
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
         if (!this.twinkle || this.particleAge < this.particleMaxAge / 3 || (this.particleAge + this.particleMaxAge) / 3 % 2 == 0) {
            super.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
         }
      }

      @Override
      public void onUpdate() {
         this.prevPosX = this.posX;
         this.prevPosY = this.posY;
         this.prevPosZ = this.posZ;
         if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
         }

         if (this.particleAge > this.particleMaxAge / 2) {
            this.setAlphaF(1.0F - ((float)this.particleAge - this.particleMaxAge / 2) / this.particleMaxAge);
            if (this.hasFadeColour) {
               this.particleRed = this.particleRed + (this.fadeColourRed - this.particleRed) * 0.2F;
               this.particleGreen = this.particleGreen + (this.fadeColourGreen - this.particleGreen) * 0.2F;
               this.particleBlue = this.particleBlue + (this.fadeColourBlue - this.particleBlue) * 0.2F;
            }
         }

         this.setParticleTextureIndex(this.baseTextureIndex + (7 - this.particleAge * 8 / this.particleMaxAge));
         this.motionY -= 0.004;
         this.moveEntity(this.motionX, this.motionY, this.motionZ);
         this.motionX *= 0.91F;
         this.motionY *= 0.91F;
         this.motionZ *= 0.91F;
         if (this.onGround) {
            this.motionX *= 0.7F;
            this.motionZ *= 0.7F;
         }

         if (this.trail && this.particleAge < this.particleMaxAge / 2 && (this.particleAge + this.particleMaxAge) % 2 == 0) {
            FireworkEffects.EntityFXChild3 entityFXChild3 = new FireworkEffects.EntityFXChild3(
               this.worldObj, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0, this.effectRenderer
            );
            entityFXChild3.setAlphaF(0.99F);
            entityFXChild3.setRBGColorF(this.particleRed, this.particleGreen, this.particleBlue);
            entityFXChild3.particleAge = entityFXChild3.particleMaxAge / 2;
            if (this.hasFadeColour) {
               entityFXChild3.hasFadeColour = true;
               entityFXChild3.fadeColourRed = this.fadeColourRed;
               entityFXChild3.fadeColourGreen = this.fadeColourGreen;
               entityFXChild3.fadeColourBlue = this.fadeColourBlue;
            }

            entityFXChild3.twinkle = this.twinkle;
            this.effectRenderer.addEffect(entityFXChild3);
         }
      }
   }

   public static class IParticleFactoryImpl implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(World world2, double d, double d2, double d3, double d4, double d5, double d6, int... iArray) {
         FireworkEffects.EntityFXChild3 entityFXChild3 = new FireworkEffects.EntityFXChild3(
            world2, d, d2, d3, d4, d5, d6, Minecraft.getMinecraft().effectRenderer
         );
         entityFXChild3.setAlphaF(0.99F);
         return entityFXChild3;
      }
   }
}
