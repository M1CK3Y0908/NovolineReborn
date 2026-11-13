package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIFollowOwner extends EntityAIBase {
   private EntityTameable thePet;
   private boolean previousAvoidsWater;
   World theWorld;
   private EntityLivingBase theOwner;
   private double followSpeed;
   private int repathTimer;
   private PathNavigate petPathfinder;
   float minDist;
   float maxDist;

   @Override
   public void resetTask() {
      this.theOwner = null;
      this.petPathfinder.clearPathEntity();
      ((PathNavigateGround)this.thePet.getNavigator()).setAvoidsWater(true);
   }

   public EntityAIFollowOwner(EntityTameable thePetIn, double followSpeedIn, float minDistIn, float maxDistIn) {
      this.thePet = thePetIn;
      this.theWorld = thePetIn.worldObj;
      this.followSpeed = followSpeedIn;
      this.petPathfinder = thePetIn.getNavigator();
      this.minDist = minDistIn;
      this.maxDist = maxDistIn;
      this.setMutexBits(3);
      if (!(thePetIn.getNavigator() instanceof PathNavigateGround)) {
         throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
      }
   }

   @Override
   public boolean shouldExecute() {
      EntityLivingBase entityLivingBase = this.thePet.getOwner();
      if (entityLivingBase == null) {
         return false;
      }

      if (entityLivingBase instanceof EntityPlayer && ((EntityPlayer)entityLivingBase).isSpectator()) {
         return false;
      }

      if (this.thePet.isSitting()) {
         return false;
      }

      if (this.thePet.getDistanceSqToEntity(entityLivingBase) < this.minDist * this.minDist) {
         return false;
      }

      this.theOwner = entityLivingBase;
      return true;
   }

   @Override
   public void updateTask() {
      this.thePet.getLookHelper().setLookPositionWithEntity(this.theOwner, 10.0F, this.thePet.getVerticalFaceSpeed());
      if (!this.thePet.isSitting() && --this.repathTimer <= 0) {
         this.repathTimer = 10;
         if (!this.petPathfinder.tryMoveToEntityLiving(this.theOwner, this.followSpeed)
            && !this.thePet.getLeashed()
            && this.thePet.getDistanceSqToEntity(this.theOwner) >= 144.0) {
            int i = MathHelper.floor_double(this.theOwner.posX) - 2;
            int i2 = MathHelper.floor_double(this.theOwner.posZ) - 2;
            int i3 = MathHelper.floor_double(this.theOwner.getEntityBoundingBox().minY);

            for (int outerIndex = 0; outerIndex <= 4; outerIndex++) {
               for (int index = 0; index <= 4; index++) {
                  if ((outerIndex < 1 || index < 1 || outerIndex > 3 || index > 3)
                     && World.doesBlockHaveSolidTopSurface(this.theWorld, new BlockPos(i + outerIndex, i3 - 1, i2 + index))
                     && this.isSpaceClear(new BlockPos(i + outerIndex, i3, i2 + index))
                     && this.isSpaceClear(new BlockPos(i + outerIndex, i3 + 1, i2 + index))) {
                     this.thePet
                        .setLocationAndAngles(
                           i + outerIndex + 0.5F, i3, i2 + index + 0.5F, this.thePet.rotationYaw, this.thePet.rotationPitch
                        );
                     this.petPathfinder.clearPathEntity();
                     return;
                  }
               }
            }
         }
      }
   }

   @Override
   public boolean continueExecuting() {
      return !this.petPathfinder.noPath() && this.thePet.getDistanceSqToEntity(this.theOwner) > this.maxDist * this.maxDist && !this.thePet.isSitting();
   }

   @Override
   public void startExecuting() {
      this.repathTimer = 0;
      this.previousAvoidsWater = ((PathNavigateGround)this.thePet.getNavigator()).getAvoidsWater();
      ((PathNavigateGround)this.thePet.getNavigator()).setAvoidsWater(false);
   }

   private boolean isSpaceClear(BlockPos pos) {
      Block block2 = this.theWorld.getBlockState(pos).getBlock();
      return block2 == Blocks.air ? true : !block2.isFullCube();
   }
}
