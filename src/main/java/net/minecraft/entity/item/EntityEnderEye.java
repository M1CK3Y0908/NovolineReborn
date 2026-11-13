package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityEnderEye extends Entity {
   private double targetY;
   private boolean shatterOrDrop;
   private double targetZ;
   private int despawnTimer;
   private double targetX;

   public EntityEnderEye(World worldIn) {
      super(worldIn);
      this.setSize(0.25F, 0.25F);
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
   }

   @Override
   public void setVelocity(double x, double y, double z) {
      this.motionX = x;
      this.motionY = y;
      this.motionZ = z;
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float f = MathHelper.sqrt_double(x * x + z * z);
         this.prevRotationYaw = this.rotationYaw = (float)(MathHelper.atan2(x, z) * 180.0 / Math.PI);
         this.prevRotationPitch = this.rotationPitch = (float)(MathHelper.atan2(y, f) * 180.0 / Math.PI);
      }
   }

   @Override
   public boolean isInRangeToRenderDist(double distance) {
      double d = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0;
      if (Double.isNaN(d)) {
         d = 4.0;
      }

      d *= 64.0;
      return distance < d * d;
   }

   @Override
   public boolean canAttackWithItem() {
      return false;
   }

   @Override
   protected void entityInit() {
   }

   public void moveTowards(BlockPos blockPos) {
      double xCoordinate = blockPos.getX();
      int yCoordinate = blockPos.getY();
      double zCoordinate = blockPos.getZ();
      double xCoordinate2 = xCoordinate - this.posX;
      double zCoordinate2 = zCoordinate - this.posZ;
      float f = MathHelper.sqrt_double(xCoordinate2 * xCoordinate2 + zCoordinate2 * zCoordinate2);
      if (f > 12.0F) {
         this.targetX = this.posX + xCoordinate2 / f * 12.0;
         this.targetZ = this.posZ + zCoordinate2 / f * 12.0;
         this.targetY = this.posY + 8.0;
      } else {
         this.targetX = xCoordinate;
         this.targetY = yCoordinate;
         this.targetZ = zCoordinate;
      }

      this.despawnTimer = 0;
      this.shatterOrDrop = this.rand.nextInt(5) > 0;
   }

   @Override
   public void onUpdate() {
      this.lastTickPosX = this.posX;
      this.lastTickPosY = this.posY;
      this.lastTickPosZ = this.posZ;
      super.onUpdate();
      this.posX = this.posX + this.motionX;
      this.posY = this.posY + this.motionY;
      this.posZ = this.posZ + this.motionZ;
      float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
      this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
      this.rotationPitch = (float)(MathHelper.atan2(this.motionY, f) * 180.0 / Math.PI);

      while (this.rotationPitch - this.prevRotationPitch < -180.0F) {
         this.prevRotationPitch -= 360.0F;
      }

      while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
         this.prevRotationPitch += 360.0F;
      }

      while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
         this.prevRotationYaw -= 360.0F;
      }

      while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
         this.prevRotationYaw += 360.0F;
      }

      this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
      this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
      if (!this.worldObj.isRemote) {
         double xCoordinate = this.targetX - this.posX;
         double zCoordinate = this.targetZ - this.posZ;
         float f2 = (float)Math.sqrt(xCoordinate * xCoordinate + zCoordinate * zCoordinate);
         float f3 = (float)MathHelper.atan2(zCoordinate, xCoordinate);
         double d = f + (f2 - f) * 0.0025;
         if (f2 < 1.0F) {
            d *= 0.8;
            this.motionY *= 0.8;
         }

         this.motionX = Math.cos(f3) * d;
         this.motionZ = Math.sin(f3) * d;
         if (this.posY < this.targetY) {
            this.motionY = this.motionY + (1.0 - this.motionY) * 0.015F;
         } else {
            this.motionY = this.motionY + (-1.0 - this.motionY) * 0.015F;
         }
      }

      float f4 = 0.25F;
      if (this.isInWater()) {
         for (int index = 0; index < 4; index++) {
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.WATER_BUBBLE,
                  this.posX - this.motionX * f4,
                  this.posY - this.motionY * f4,
                  this.posZ - this.motionZ * f4,
                  this.motionX,
                  this.motionY,
                  this.motionZ
               );
         }
      } else {
         this.worldObj
            .spawnParticle(
               EnumParticleTypes.PORTAL,
               this.posX - this.motionX * f4 + this.rand.nextDouble() * 0.6 - 0.3,
               this.posY - this.motionY * f4 - 0.5,
               this.posZ - this.motionZ * f4 + this.rand.nextDouble() * 0.6 - 0.3,
               this.motionX,
               this.motionY,
               this.motionZ
            );
      }

      if (!this.worldObj.isRemote) {
         this.setPosition(this.posX, this.posY, this.posZ);
         this.despawnTimer++;
         if (this.despawnTimer > 80 && !this.worldObj.isRemote) {
            this.setDead();
            if (this.shatterOrDrop) {
               this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(Items.ender_eye)));
            } else {
               this.worldObj.playAuxSFX(2003, new BlockPos(this), 0);
            }
         }
      }
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
   }

   @Override
   public int getBrightnessForRender(float partialTicks) {
      return 15728880;
   }

   public EntityEnderEye(World worldIn, double x, double y, double z) {
      super(worldIn);
      this.despawnTimer = 0;
      this.setSize(0.25F, 0.25F);
      this.setPosition(x, y, z);
   }
}
