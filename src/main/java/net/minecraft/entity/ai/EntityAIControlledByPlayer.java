package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.pathfinder.WalkNodeProcessor;

public class EntityAIControlledByPlayer extends EntityAIBase {
   private int maxSpeedBoostTime;
   private final EntityLiving thisEntity;
   private final float maxSpeed;
   private float currentSpeed;
   private boolean speedBoosted;
   private int speedBoostTime;

   public boolean isControlledByPlayer() {
      return !this.isSpeedBoosted() && this.currentSpeed > this.maxSpeed * 0.3F;
   }

   public boolean isSpeedBoosted() {
      return this.speedBoosted;
   }

   @Override
   public void startExecuting() {
      this.currentSpeed = 0.0F;
   }

   public EntityAIControlledByPlayer(EntityLiving entitylivingIn, float maxspeed) {
      this.thisEntity = entitylivingIn;
      this.maxSpeed = maxspeed;
      this.setMutexBits(7);
   }

   @Override
   public void resetTask() {
      this.speedBoosted = false;
      this.currentSpeed = 0.0F;
   }

   public void boostSpeed() {
      this.speedBoosted = true;
      this.speedBoostTime = 0;
      this.maxSpeedBoostTime = this.thisEntity.getRNG().nextInt(841) + 140;
   }

   @Override
   public boolean shouldExecute() {
      return this.thisEntity.isEntityAlive()
         && this.thisEntity.riddenByEntity != null
         && this.thisEntity.riddenByEntity instanceof EntityPlayer
         && (this.speedBoosted || this.thisEntity.canBeSteered());
   }

   private boolean isStairOrSlab(Block blockIn) {
      return blockIn instanceof BlockStairs || blockIn instanceof BlockSlab;
   }

   @Override
   public void updateTask() {
      EntityPlayer entityPlayer = (EntityPlayer)this.thisEntity.riddenByEntity;
      EntityCreature entityCreature = (EntityCreature)this.thisEntity;
      float f = MathHelper.wrapAngleTo180_float(entityPlayer.rotationYaw - this.thisEntity.rotationYaw) * 0.5F;
      if (f > 5.0F) {
         f = 5.0F;
      }

      if (f < -5.0F) {
         f = -5.0F;
      }

      this.thisEntity.rotationYaw = MathHelper.wrapAngleTo180_float(this.thisEntity.rotationYaw + f);
      if (this.currentSpeed < this.maxSpeed) {
         this.currentSpeed = this.currentSpeed + (this.maxSpeed - this.currentSpeed) * 0.01F;
      }

      if (this.currentSpeed > this.maxSpeed) {
         this.currentSpeed = this.maxSpeed;
      }

      int i = MathHelper.floor_double(this.thisEntity.posX);
      int i2 = MathHelper.floor_double(this.thisEntity.posY);
      int i3 = MathHelper.floor_double(this.thisEntity.posZ);
      float currentSpeed2 = this.currentSpeed;
      if (this.speedBoosted) {
         if (this.speedBoostTime++ > this.maxSpeedBoostTime) {
            this.speedBoosted = false;
         }

         currentSpeed2 += currentSpeed2 * 1.15F * MathHelper.sin((float)this.speedBoostTime / this.maxSpeedBoostTime * (float) Math.PI);
      }

      float f2 = 0.91F;
      if (this.thisEntity.onGround) {
         f2 = this.thisEntity
               .worldObj
               .getBlockState(new BlockPos(MathHelper.floor_float(i), MathHelper.floor_float(i2) - 1, MathHelper.floor_float(i3)))
               .getBlock()
               .slipperiness
            * 0.91F;
      }

      float f3 = 0.16277136F / (f2 * f2 * f2);
      float f4 = MathHelper.sin(entityCreature.rotationYaw * (float) Math.PI / 180.0F);
      float f5 = MathHelper.cos(entityCreature.rotationYaw * (float) Math.PI / 180.0F);
      float f6 = entityCreature.getAIMoveSpeed() * f3;
      float f7 = Math.max(currentSpeed2, 1.0F);
      f7 = f6 / f7;
      float f8 = currentSpeed2 * f7;
      float f9 = -(f8 * f4);
      float f10 = f8 * f5;
      if (MathHelper.abs(f9) > MathHelper.abs(f10)) {
         if (f9 < 0.0F) {
            f9 -= this.thisEntity.width / 2.0F;
         }

         if (f9 > 0.0F) {
            f9 += this.thisEntity.width / 2.0F;
         }

         f10 = 0.0F;
      } else {
         f9 = 0.0F;
         if (f10 < 0.0F) {
            f10 -= this.thisEntity.width / 2.0F;
         }

         if (f10 > 0.0F) {
            f10 += this.thisEntity.width / 2.0F;
         }
      }

      int i4 = MathHelper.floor_double(this.thisEntity.posX + f9);
      int i5 = MathHelper.floor_double(this.thisEntity.posZ + f10);
      int i6 = MathHelper.floor_float(this.thisEntity.width + 1.0F);
      int i7 = MathHelper.floor_float(this.thisEntity.height + entityPlayer.height + 1.0F);
      int i8 = MathHelper.floor_float(this.thisEntity.width + 1.0F);
      if (i != i4 || i3 != i5) {
         Block block2 = this.thisEntity.worldObj.getBlockState(new BlockPos(i, i2, i3)).getBlock();
         boolean flag = !this.isStairOrSlab(block2)
            && (
               block2.getMaterial() != Material.air
                  || !this.isStairOrSlab(this.thisEntity.worldObj.getBlockState(new BlockPos(i, i2 - 1, i3)).getBlock())
            );
         if (flag
            && 0
               == WalkNodeProcessor.getPathableStatus(this.thisEntity.worldObj, this.thisEntity, i4, i2, i5, i6, i7, i8, false, false, true)
            && 1
               == WalkNodeProcessor.getPathableStatus(
                  this.thisEntity.worldObj, this.thisEntity, i, i2 + 1, i3, i6, i7, i8, false, false, true
               )
            && 1
               == WalkNodeProcessor.getPathableStatus(
                  this.thisEntity.worldObj, this.thisEntity, i4, i2 + 1, i5, i6, i7, i8, false, false, true
               )) {
            entityCreature.getJumpHelper().setJumping();
         }
      }

      if (!entityPlayer.capabilities.isCreativeMode
         && this.currentSpeed >= this.maxSpeed * 0.5F
         && this.thisEntity.getRNG().nextFloat() < 0.006F
         && !this.speedBoosted) {
         ItemStack heldItem = entityPlayer.getHeldItem();
         if (heldItem != null && heldItem.getItem() == Items.carrot_on_a_stick) {
            heldItem.damageItem(1, entityPlayer);
            if (heldItem.stackSize == 0) {
               ItemStack itemStack = new ItemStack(Items.fishing_rod);
               itemStack.setTagCompound(heldItem.getTagCompound());
               entityPlayer.inventory.mainInventory[entityPlayer.inventory.currentItem] = itemStack;
            }
         }
      }

      this.thisEntity.moveEntityWithHeading(0.0F, currentSpeed2);
   }
}
