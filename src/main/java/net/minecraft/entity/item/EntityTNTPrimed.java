package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityTNTPrimed extends Entity {
   private EntityLivingBase tntPlacedBy;
   public int fuse;

   @Override
   protected void readEntityFromNBT(NBTTagCompound tagCompund) {
      this.fuse = tagCompund.getByte("Fuse");
   }

   @Override
   public float getEyeHeight() {
      return 0.0F;
   }

   @Override
   public boolean canBeCollidedWith() {
      return !this.isDead;
   }

   public EntityTNTPrimed(World worldIn, double x, double y, double z, EntityLivingBase igniter) {
      this(worldIn);
      this.setPosition(x, y, z);
      float f = (float)(Math.random() * Math.PI * 2.0);
      this.motionX = -((float)Math.sin(f)) * 0.02F;
      this.motionY = 0.2F;
      this.motionZ = -((float)Math.cos(f)) * 0.02F;
      this.fuse = 80;
      this.prevPosX = x;
      this.prevPosY = y;
      this.prevPosZ = z;
      this.tntPlacedBy = igniter;
   }

   public EntityLivingBase getTntPlacedBy() {
      return this.tntPlacedBy;
   }

   public EntityTNTPrimed(World worldIn) {
      super(worldIn);
      this.preventEntitySpawning = true;
      this.setSize(0.98F, 0.98F);
   }

   @Override
   protected boolean canTriggerWalking() {
      return false;
   }

   private void explode() {
      float f = 4.0F;
      this.worldObj.createExplosion(this, this.posX, this.posY + this.height / 16.0F, this.posZ, f, true);
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.motionY -= 0.04F;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.98F;
      this.motionY *= 0.98F;
      this.motionZ *= 0.98F;
      if (this.onGround) {
         this.motionX *= 0.7F;
         this.motionZ *= 0.7F;
         this.motionY *= -0.5;
      }

      if (this.fuse-- <= 0) {
         this.setDead();
         if (!this.worldObj.isRemote) {
            this.explode();
         }
      } else {
         this.handleWaterMovement();
         this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5, this.posZ, 0.0, 0.0, 0.0);
      }
   }

   @Override
   protected void entityInit() {
   }

   @Override
   protected void writeEntityToNBT(NBTTagCompound tagCompound) {
      tagCompound.setByte("Fuse", (byte)this.fuse);
   }
}
