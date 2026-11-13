package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBoat extends Entity {
   private int boatPosRotationIncrements;
   private double boatX;
   private boolean isBoatEmpty = true;
   private double boatPitch;
   private double boatY;
   private double boatYaw;
   private double velocityZ;
   private double velocityX;
   private double speedMultiplier = 0.07;
   private double velocityY;
   private double boatZ;

   @Override
   public boolean interactFirst(EntityPlayer playerIn) {
      if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != playerIn) {
         return true;
      }

      if (!this.worldObj.isRemote) {
         playerIn.mountEntity(this);
      }

      return true;
   }

   public EntityBoat(World worldIn) {
      super(worldIn);
      this.preventEntitySpawning = true;
      this.setSize(1.5F, 0.6F);
   }

   @Override
   public void performHurtAnimation() {
      this.setForwardDirection(-this.getForwardDirection());
      this.setTimeSinceHit(10);
      this.setDamageTaken(this.getDamageTaken() * 11.0F);
   }

   @Override
   public AxisAlignedBB getCollisionBox(Entity entityIn) {
      return entityIn.getEntityBoundingBox();
   }

   @Override
   public double getMountedYOffset() {
      return -0.3;
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox() {
      return this.getEntityBoundingBox();
   }

   public void setForwardDirection(int i) {
      this.dataWatcher.updateObject(18, i);
   }

   @Override
   protected void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos) {
      if (onGroundIn) {
         if (this.fallDistance > 3.0F) {
            this.fall(this.fallDistance, 1.0F);
            if (!this.worldObj.isRemote && !this.isDead) {
               this.setDead();
               if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
                  for (int index = 0; index < 3; index++) {
                     this.getEntityItem2(Item.getItemFromBlock(Blocks.planks), 0.0F);
                  }

                  for (int innerIndex = 0; innerIndex < 2; innerIndex++) {
                     this.getEntityItem2(Items.stick, 0.0F);
                  }
               }
            }

            this.fallDistance = 0.0F;
         }
      } else if (this.worldObj.getBlockState(new BlockPos(this).down()).getBlock().getMaterial() != Material.water && y < 0.0) {
         this.fallDistance = (float)(this.fallDistance - y);
      }
   }

   public int getForwardDirection() {
      return this.dataWatcher.getWatchableObjectInt(18);
   }

   @Override
   protected void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
   }

   public void setIsBoatEmpty(boolean flag) {
      this.isBoatEmpty = flag;
   }

   @Override
   public void updateRiderPosition() {
      if (this.riddenByEntity != null) {
         double d = Math.cos(this.rotationYaw * Math.PI / 180.0) * 0.4;
         double d2 = Math.sin(this.rotationYaw * Math.PI / 180.0) * 0.4;
         this.riddenByEntity
            .setPosition(this.posX + d, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ + d2);
      }
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (this.getTimeSinceHit() > 0) {
         this.setTimeSinceHit(this.getTimeSinceHit() - 1);
      }

      if (this.getDamageTaken() > 0.0F) {
         this.setDamageTaken(this.getDamageTaken() - 1.0F);
      }

      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      double d = 0.0;

      for (int i = 0; i < 5; i++) {
         double d2 = this.getEntityBoundingBox().minY + (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY) * (i + 0) / 5 - 0.125;
         double d3 = this.getEntityBoundingBox().minY + (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY) * (i + 1) / 5 - 0.125;
         AxisAlignedBB axisAlignedBB = new AxisAlignedBB(
            this.getEntityBoundingBox().minX,
            d2,
            this.getEntityBoundingBox().minZ,
            this.getEntityBoundingBox().maxX,
            d3,
            this.getEntityBoundingBox().maxZ
         );
         if (this.worldObj.isAABBInMaterial(axisAlignedBB, Material.water)) {
            d += 1.0 / 5;
         }
      }

      double d4 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
      if (d4 > 0.2975) {
         double d5 = Math.cos(this.rotationYaw * Math.PI / 180.0);
         double d6 = Math.sin(this.rotationYaw * Math.PI / 180.0);

         for (int i2 = 0; i2 < 1.0 + d4 * 60.0; i2++) {
            double d7 = this.rand.nextFloat() * 2.0F - 1.0F;
            double d8 = (this.rand.nextInt(2) * 2 - 1) * 0.7;
            if (this.rand.nextBoolean()) {
               double d9 = this.posX - d5 * d7 * 0.8 + d6 * d8;
               double d10 = this.posZ - d6 * d7 * 0.8 - d5 * d8;
               this.worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, d9, this.posY - 0.125, d10, this.motionX, this.motionY, this.motionZ);
            } else {
               double d11 = this.posX + d5 + d6 * d7 * 0.7;
               double d12 = this.posZ + d6 - d5 * d7 * 0.7;
               this.worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, d11, this.posY - 0.125, d12, this.motionX, this.motionY, this.motionZ);
            }
         }
      }

      if (this.worldObj.isRemote && this.isBoatEmpty) {
         if (this.boatPosRotationIncrements > 0) {
            double d13 = this.posX + (this.boatX - this.posX) / this.boatPosRotationIncrements;
            double d14 = this.posY + (this.boatY - this.posY) / this.boatPosRotationIncrements;
            double d15 = this.posZ + (this.boatZ - this.posZ) / this.boatPosRotationIncrements;
            double d16 = MathHelper.wrapAngleTo180_double(this.boatYaw - this.rotationYaw);
            this.rotationYaw = (float)(this.rotationYaw + d16 / this.boatPosRotationIncrements);
            this.rotationPitch = (float)(this.rotationPitch + (this.boatPitch - this.rotationPitch) / this.boatPosRotationIncrements);
            this.boatPosRotationIncrements--;
            this.setPosition(d13, d14, d15);
            this.setRotation(this.rotationYaw, this.rotationPitch);
         } else {
            double d17 = this.posX + this.motionX;
            double d18 = this.posY + this.motionY;
            double d19 = this.posZ + this.motionZ;
            this.setPosition(d17, d18, d19);
            if (this.onGround) {
               this.motionX *= 0.5;
               this.motionY *= 0.5;
               this.motionZ *= 0.5;
            }

            this.motionX *= 0.99F;
            this.motionY *= 0.95F;
            this.motionZ *= 0.99F;
         }
      } else {
         if (d < 1.0) {
            double d20 = d * 2.0 - 1.0;
            this.motionY += 0.04F * d20;
         } else {
            if (this.motionY < 0.0) {
               this.motionY /= 2.0;
            }

            this.motionY += 0.007F;
         }

         if (this.riddenByEntity instanceof EntityLivingBase entityLivingBase) {
            float f = this.riddenByEntity.rotationYaw + -entityLivingBase.moveStrafing * 90.0F;
            this.motionX = this.motionX + -Math.sin(f * (float) Math.PI / 180.0F) * this.speedMultiplier * entityLivingBase.moveForward * 0.05F;
            this.motionZ = this.motionZ + Math.cos(f * (float) Math.PI / 180.0F) * this.speedMultiplier * entityLivingBase.moveForward * 0.05F;
         }

         double d21 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (d21 > 0.35) {
            double d22 = 0.35 / d21;
            this.motionX *= d22;
            this.motionZ *= d22;
            d21 = 0.35;
         }

         if (d21 > d4 && this.speedMultiplier < 0.35) {
            this.speedMultiplier = this.speedMultiplier + (0.35 - this.speedMultiplier) / 35.0;
            if (this.speedMultiplier > 0.35) {
               this.speedMultiplier = 0.35;
            }
         } else {
            this.speedMultiplier = this.speedMultiplier - (this.speedMultiplier - 0.07) / 35.0;
            if (this.speedMultiplier < 0.07) {
               this.speedMultiplier = 0.07;
            }
         }

         for (int i3 = 0; i3 < 4; i3++) {
            int i4 = MathHelper.floor_double(this.posX + (i3 % 2 - 0.5) * 0.8);
            int i5 = MathHelper.floor_double(this.posZ + (i3 / 2 - 0.5) * 0.8);

            for (int i6 = 0; i6 < 2; i6++) {
               int i7 = MathHelper.floor_double(this.posY) + i6;
               BlockPos blockPos = new BlockPos(i4, i7, i5);
               Block block2 = this.worldObj.getBlockState(blockPos).getBlock();
               if (block2 == Blocks.snow_layer) {
                  this.worldObj.setBlockToAir(blockPos);
                  this.isCollidedHorizontally = false;
               } else if (block2 == Blocks.waterlily) {
                  this.worldObj.destroyBlock(blockPos, true);
                  this.isCollidedHorizontally = false;
               }
            }
         }

         if (this.onGround) {
            this.motionX *= 0.5;
            this.motionY *= 0.5;
            this.motionZ *= 0.5;
         }

         this.moveEntity(this.motionX, this.motionY, this.motionZ);
         if (this.isCollidedHorizontally && d4 > 0.2975) {
            if (!this.worldObj.isRemote && !this.isDead) {
               this.setDead();
               if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
                  for (int i8 = 0; i8 < 3; i8++) {
                     this.getEntityItem2(Item.getItemFromBlock(Blocks.planks), 0.0F);
                  }

                  for (int i9 = 0; i9 < 2; i9++) {
                     this.getEntityItem2(Items.stick, 0.0F);
                  }
               }
            }
         } else {
            this.motionX *= 0.99F;
            this.motionY *= 0.95F;
            this.motionZ *= 0.99F;
         }

         this.rotationPitch = 0.0F;
         double rotationYaw2 = this.rotationYaw;
         double d23 = this.prevPosX - this.posX;
         double d24 = this.prevPosZ - this.posZ;
         if (d23 * d23 + d24 * d24 > 0.001) {
            rotationYaw2 = (float)(MathHelper.atan2(d24, d23) * 180.0 / Math.PI);
         }

         double d25 = MathHelper.wrapAngleTo180_double(rotationYaw2 - this.rotationYaw);
         if (d25 > 20.0) {
            d25 = 20.0;
         }

         if (d25 < -20.0) {
            d25 = -20.0;
         }

         this.rotationYaw = (float)(this.rotationYaw + d25);
         this.setRotation(this.rotationYaw, this.rotationPitch);
         if (!this.worldObj.isRemote) {
            List entitiesWithinAABBExcludingEntity = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(0.2F, 0.0, 0.2F));
            if (entitiesWithinAABBExcludingEntity != null && !entitiesWithinAABBExcludingEntity.isEmpty()) {
               for (int i10 = 0; i10 < entitiesWithinAABBExcludingEntity.size(); i10++) {
                  Entity entity2 = (Entity)entitiesWithinAABBExcludingEntity.get(i10);
                  if (entity2 != this.riddenByEntity && entity2.canBePushed() && entity2 instanceof EntityBoat) {
                     entity2.applyEntityCollision(this);
                  }
               }
            }

            if (this.riddenByEntity != null && this.riddenByEntity.isDead) {
               this.riddenByEntity = null;
            }
         }
      }
   }

   public float getDamageTaken() {
      return this.dataWatcher.getWatchableObjectFloat(19);
   }

   public EntityBoat(World worldIn, double d, double d2, double d3) {
      this(worldIn);
      this.setPosition(d, d2, d3);
      this.motionX = 0.0;
      this.motionY = 0.0;
      this.motionZ = 0.0;
      this.prevPosX = d;
      this.prevPosY = d2;
      this.prevPosZ = d3;
   }

   @Override
   protected void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
   }

   public int getTimeSinceHit() {
      return this.dataWatcher.getWatchableObjectInt(17);
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if (!this.worldObj.isRemote && !this.isDead) {
         if (this.riddenByEntity != null && this.riddenByEntity == source.getEntity() && source instanceof EntityDamageSourceIndirect) {
            return false;
         }

         this.setForwardDirection(-this.getForwardDirection());
         this.setTimeSinceHit(10);
         this.setDamageTaken(this.getDamageTaken() + amount * 10.0F);
         this.setBeenAttacked();
         boolean flag = source.getEntity() instanceof EntityPlayer && ((EntityPlayer)source.getEntity()).capabilities.isCreativeMode;
         if (flag || this.getDamageTaken() > 40.0F) {
            if (this.riddenByEntity != null) {
               this.riddenByEntity.mountEntity(this);
            }

            if (!flag && this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
               this.getEntityItem2(Items.boat, 0.0F);
            }

            this.setDead();
         }

         return true;
      } else {
         return true;
      }
   }

   @Override
   public void handleDouble(double d, double d2, double d3, float f, float f2, boolean flag) {
      if (flag && this.riddenByEntity != null) {
         this.prevPosX = this.posX = d;
         this.prevPosY = this.posY = d2;
         this.prevPosZ = this.posZ = d3;
         this.rotationYaw = f;
         this.rotationPitch = f2;
         this.boatPosRotationIncrements = 0;
         this.setPosition(d, d2, d3);
         this.motionX = this.velocityX = 0.0;
         this.motionY = this.velocityY = 0.0;
         this.motionZ = this.velocityZ = 0.0;
      } else {
         if (this.isBoatEmpty) {
            this.boatPosRotationIncrements = 8;
         } else {
            double d4 = d - this.posX;
            double d5 = d2 - this.posY;
            double d6 = d3 - this.posZ;
            double d7 = d4 * d4 + d5 * d5 + d6 * d6;
            if (d7 <= 1.0) {
               return;
            }

            this.boatPosRotationIncrements = 3;
         }

         this.boatX = d;
         this.boatY = d2;
         this.boatZ = d3;
         this.boatYaw = f;
         this.boatPitch = f2;
         this.motionX = this.velocityX;
         this.motionY = this.velocityY;
         this.motionZ = this.velocityZ;
      }
   }

   @Override
   protected boolean canTriggerWalking() {
      return false;
   }

   @Override
   public boolean canBeCollidedWith() {
      return !this.isDead;
   }

   @Override
   protected void entityInit() {
      this.dataWatcher.addObject(17, new Integer(0));
      this.dataWatcher.addObject(18, new Integer(1));
      this.dataWatcher.addObject(19, new Float(0.0F));
   }

   public void setDamageTaken(float f) {
      this.dataWatcher.updateObject(19, f);
   }

   @Override
   public boolean canBePushed() {
      return true;
   }

   @Override
   public void setVelocity(double x, double y, double z) {
      this.velocityX = this.motionX = x;
      this.velocityY = this.motionY = y;
      this.velocityZ = this.motionZ = z;
   }

   public void setTimeSinceHit(int i) {
      this.dataWatcher.updateObject(17, i);
   }
}
