package net.minecraft.entity.passive;

import java.util.Calendar;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBat extends EntityAmbientCreature {
   private BlockPos spawnPosition;

   @Override
   protected float getSoundPitch() {
      return super.getSoundPitch() * 0.95F;
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (this.getIsBatHanging()) {
         this.motionX = this.motionY = this.motionZ = 0.0;
         this.posY = MathHelper.floor_double(this.posY) + 1.0 - this.height;
      } else {
         this.motionY *= 0.6F;
      }
   }

   @Override
   protected String getDeathSound() {
      return "mob.bat.death";
   }

   public boolean getIsBatHanging() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
   }

   @Override
   public void fall(float distance, float damageMultiplier) {
   }

   @Override
   public float getEyeHeight() {
      return this.height / 2.0F;
   }

   @Override
   protected String getHurtSound() {
      return "mob.bat.hurt";
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.dataWatcher.updateObject(16, tagCompund.getByte("BatFlags"));
   }

   public void setIsBatHanging(boolean isHanging) {
      byte b0 = this.dataWatcher.getWatchableObjectByte(16);
      if (isHanging) {
         this.dataWatcher.updateObject(16, (byte)(b0 | 1));
      } else {
         this.dataWatcher.updateObject(16, (byte)(b0 & -2));
      }
   }

   @Override
   public boolean doesEntityNotTriggerPressurePlate() {
      return true;
   }

   @Override
   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(6.0);
   }

   @Override
   protected String getLivingSound() {
      return this.getIsBatHanging() && this.rand.nextInt(4) != 0 ? null : "mob.bat.idle";
   }

   @Override
   protected void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos) {
   }

   @Override
   public boolean canBePushed() {
      return false;
   }

   @Override
   public boolean getCanSpawnHere() {
      BlockPos blockPos = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);
      if (blockPos.getY() >= this.worldObj.getSeaLevel()) {
         return false;
      }

      int lightFromNeighbors = this.worldObj.getLightFromNeighbors(blockPos);
      int i = 4;
      if (this.isDateAroundHalloween(this.worldObj.getCurrentDate())) {
         i = 7;
      } else if (this.rand.nextBoolean()) {
         return false;
      }

      return lightFromNeighbors > this.rand.nextInt(i) ? false : super.getCanSpawnHere();
   }

   @Override
   protected boolean canTriggerWalking() {
      return false;
   }

   @Override
   protected void updateAITasks() {
      super.updateAITasks();
      BlockPos blockPos = new BlockPos(this);
      BlockPos blockPos2 = blockPos.up();
      if (this.getIsBatHanging()) {
         if (!this.worldObj.getBlockState(blockPos2).getBlock().isNormalCube()) {
            this.setIsBatHanging(false);
            this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1015, blockPos, 0);
         } else {
            if (this.rand.nextInt(200) == 0) {
               this.rotationYawHead = this.rand.nextInt(360);
            }

            if (this.worldObj.getClosestPlayerToEntity(this, 4.0) != null) {
               this.setIsBatHanging(false);
               this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1015, blockPos, 0);
            }
         }
      } else {
         if (this.spawnPosition != null && (!this.worldObj.isAirBlock(this.spawnPosition) || this.spawnPosition.getY() < 1)) {
            this.spawnPosition = null;
         }

         if (this.spawnPosition == null || this.rand.nextInt(30) == 0 || this.spawnPosition.distanceSq((int)this.posX, (int)this.posY, (int)this.posZ) < 4.0) {
            this.spawnPosition = new BlockPos(
               (int)this.posX + this.rand.nextInt(7) - this.rand.nextInt(7),
               (int)this.posY + this.rand.nextInt(6) - 2,
               (int)this.posZ + this.rand.nextInt(7) - this.rand.nextInt(7)
            );
         }

         double xCoordinate = this.spawnPosition.getX() + 0.5 - this.posX;
         double yCoordinate = this.spawnPosition.getY() + 0.1 - this.posY;
         double zCoordinate = this.spawnPosition.getZ() + 0.5 - this.posZ;
         this.motionX = this.motionX + (Math.signum(xCoordinate) * 0.5 - this.motionX) * 0.1F;
         this.motionY = this.motionY + (Math.signum(yCoordinate) * 0.7F - this.motionY) * 0.1F;
         this.motionZ = this.motionZ + (Math.signum(zCoordinate) * 0.5 - this.motionZ) * 0.1F;
         float f = (float)(MathHelper.atan2(this.motionZ, this.motionX) * 180.0 / Math.PI) - 90.0F;
         float f2 = MathHelper.wrapAngleTo180_float(f - this.rotationYaw);
         this.moveForward = 0.5F;
         this.rotationYaw += f2;
         if (this.rand.nextInt(100) == 0 && this.worldObj.getBlockState(blockPos2).getBlock().isNormalCube()) {
            this.setIsBatHanging(true);
         }
      }
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if (!this.worldObj.isRemote && this.getIsBatHanging()) {
         this.setIsBatHanging(false);
      }

      return super.attackEntityFrom(source, amount);
   }

   @Override
   protected float getSoundVolume() {
      return 0.1F;
   }

   private boolean isDateAroundHalloween(Calendar calendar) {
      return calendar.get(2) + 1 == 10 && calendar.get(5) >= 20 || calendar.get(2) + 1 == 11 && calendar.get(5) <= 3;
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setByte("BatFlags", this.dataWatcher.getWatchableObjectByte(16));
   }

   @Override
   protected void collideWithEntity(Entity entityIn) {
   }

   public EntityBat(World worldIn) {
      super(worldIn);
      this.setSize(0.5F, 0.9F);
      this.setIsBatHanging(true);
   }

   @Override
   protected void collideWithNearbyEntities() {
   }

   @Override
   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, new Byte((byte)0));
   }
}
