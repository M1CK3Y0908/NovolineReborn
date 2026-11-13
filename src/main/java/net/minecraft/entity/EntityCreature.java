package net.minecraft.entity;

import java.util.UUID;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class EntityCreature extends EntityLiving {
   private BlockPos homePosition = BlockPos.ORIGIN;
   public static final UUID FLEEING_SPEED_MODIFIER_UUID = UUID.fromString("E199AD21-BA8A-4C53-8D13-6182D5C69D3A");
   private float maximumHomeDistance = -1.0F;
   public static final AttributeModifier FLEEING_SPEED_MODIFIER = new AttributeModifier(FLEEING_SPEED_MODIFIER_UUID, "Fleeing speed bonus", 2.0, 2)
      .getAttributeModifier();
   private EntityAIBase aiBase = new EntityAIMoveTowardsRestriction(this, 1.0);
   private boolean isMovementAITaskSet;

   protected void updateFallDamage(float distance) {
   }

   public EntityCreature(World worldIn) {
      super(worldIn);
   }

   public boolean hasPath() {
      return !this.navigator.noPath();
   }

   public float getBlockPathWeight(BlockPos pos) {
      return 0.0F;
   }

   public boolean isWithinHomeDistanceFromPosition(BlockPos pos) {
      return this.maximumHomeDistance == -1.0F ? true : this.homePosition.distanceSq(pos) < this.maximumHomeDistance * this.maximumHomeDistance;
   }

   public void setHomePosAndDistance(BlockPos pos, int distance) {
      this.homePosition = pos;
      this.maximumHomeDistance = distance;
   }

   public float getMaximumHomeDistance() {
      return this.maximumHomeDistance;
   }

   @Override
   public boolean getCanSpawnHere() {
      return super.getCanSpawnHere() && this.getBlockPathWeight(new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ)) >= 0.0F;
   }

   public boolean hasHome() {
      return this.maximumHomeDistance != -1.0F;
   }

   public BlockPos getHomePosition() {
      return this.homePosition;
   }

   public void detachHome() {
      this.maximumHomeDistance = -1.0F;
   }

   public boolean isWithinHomeDistanceCurrentPosition() {
      return this.isWithinHomeDistanceFromPosition(new BlockPos(this));
   }

   @Override
   protected void updateLeashedState() {
      super.updateLeashedState();
      if (this.getLeashed() && this.getLeashedToEntity() != null && this.getLeashedToEntity().worldObj == this.worldObj) {
         Entity entity = this.getLeashedToEntity();
         this.setHomePosAndDistance(new BlockPos((int)entity.posX, (int)entity.posY, (int)entity.posZ), 5);
         float distanceToEntity = this.getDistanceToEntity(entity);
         if (this instanceof EntityTameable && ((EntityTameable)this).isSitting()) {
            if (distanceToEntity > 10.0F) {
               this.clearLeashed(true, true);
            }

            return;
         }

         if (!this.isMovementAITaskSet) {
            this.tasks.addTask(2, this.aiBase);
            if (this.getNavigator() instanceof PathNavigateGround) {
               ((PathNavigateGround)this.getNavigator()).setAvoidsWater(false);
            }

            this.isMovementAITaskSet = true;
         }

         this.updateFallDamage(distanceToEntity);
         if (distanceToEntity > 4.0F) {
            this.getNavigator().tryMoveToEntityLiving(entity, 1.0);
         }

         if (distanceToEntity > 6.0F) {
            double xCoordinate = (entity.posX - this.posX) / distanceToEntity;
            double yCoordinate = (entity.posY - this.posY) / distanceToEntity;
            double zCoordinate = (entity.posZ - this.posZ) / distanceToEntity;
            this.motionX = this.motionX + xCoordinate * Math.abs(xCoordinate) * 0.4;
            this.motionY = this.motionY + yCoordinate * Math.abs(yCoordinate) * 0.4;
            this.motionZ = this.motionZ + zCoordinate * Math.abs(zCoordinate) * 0.4;
         }

         if (distanceToEntity > 10.0F) {
            this.clearLeashed(true, true);
         }
      } else if (!this.getLeashed() && this.isMovementAITaskSet) {
         this.isMovementAITaskSet = false;
         this.tasks.removeTask(this.aiBase);
         if (this.getNavigator() instanceof PathNavigateGround) {
            ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
         }

         this.detachHome();
      }
   }
}
