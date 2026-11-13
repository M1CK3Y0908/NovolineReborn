package net.minecraft.entity.ai;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIMoveThroughVillage extends EntityAIBase {
   private List<VillageDoorInfo> doorList = Lists.newArrayList();
   private EntityCreature theEntity;
   private double movementSpeed;
   private PathEntity entityPathNavigate;
   private VillageDoorInfo doorInfo;
   private boolean isNocturnal;

   private void resizeDoorList() {
      if (this.doorList.size() > 15) {
         this.doorList.remove(0);
      }
   }

   public EntityAIMoveThroughVillage(EntityCreature theEntityIn, double movementSpeedIn, boolean isNocturnalIn) {
      this.theEntity = theEntityIn;
      this.movementSpeed = movementSpeedIn;
      this.isNocturnal = isNocturnalIn;
      this.setMutexBits(1);
      if (!(theEntityIn.getNavigator() instanceof PathNavigateGround)) {
         throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
      }
   }

   @Override
   public void resetTask() {
      if (this.theEntity.getNavigator().noPath() || this.theEntity.getDistanceSq(this.doorInfo.getDoorBlockPos()) < 16.0) {
         this.doorList.add(this.doorInfo);
      }
   }

   @Override
   public void startExecuting() {
      this.theEntity.getNavigator().setPath(this.entityPathNavigate, this.movementSpeed);
   }

   private VillageDoorInfo findNearestDoor(Village villageIn) {
      VillageDoorInfo villageDoorInfo = null;
      int i = Integer.MAX_VALUE;

      for (VillageDoorInfo villageDoorInfo2 : villageIn.getVillageDoorInfoList()) {
         int distanceSquared = villageDoorInfo2.getDistanceSquared(
            MathHelper.floor_double(this.theEntity.posX), MathHelper.floor_double(this.theEntity.posY), MathHelper.floor_double(this.theEntity.posZ)
         );
         if (distanceSquared < i && !this.doesDoorListContain(villageDoorInfo2)) {
            villageDoorInfo = villageDoorInfo2;
            i = distanceSquared;
         }
      }

      return villageDoorInfo;
   }

   @Override
   public boolean continueExecuting() {
      if (this.theEntity.getNavigator().noPath()) {
         return false;
      }

      float f = this.theEntity.width + 4.0F;
      return this.theEntity.getDistanceSq(this.doorInfo.getDoorBlockPos()) > f * f;
   }

   private boolean doesDoorListContain(VillageDoorInfo doorInfoIn) {
      for (VillageDoorInfo villageDoorInfo : this.doorList) {
         if (doorInfoIn.getDoorBlockPos().equals(villageDoorInfo.getDoorBlockPos())) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean shouldExecute() {
      this.resizeDoorList();
      if (this.isNocturnal && this.theEntity.worldObj.isDaytime()) {
         return false;
      }

      Village village = this.theEntity.worldObj.getVillageCollection().getNearestVillage(new BlockPos(this.theEntity), 0);
      if (village == null) {
         return false;
      }

      this.doorInfo = this.findNearestDoor(village);
      if (this.doorInfo == null) {
         return false;
      }

      PathNavigateGround pathNavigateGround = (PathNavigateGround)this.theEntity.getNavigator();
      boolean conditionMet = pathNavigateGround.getEnterDoors();
      pathNavigateGround.setBreakDoors(false);
      this.entityPathNavigate = pathNavigateGround.getPathToPos(this.doorInfo.getDoorBlockPos());
      pathNavigateGround.setBreakDoors(conditionMet);
      if (this.entityPathNavigate != null) {
         return true;
      }

      Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockTowards(
         this.theEntity,
         10,
         7,
         new Vec3(this.doorInfo.getDoorBlockPos().getX(), this.doorInfo.getDoorBlockPos().getY(), this.doorInfo.getDoorBlockPos().getZ())
      );
      if (vec3 == null) {
         return false;
      }

      pathNavigateGround.setBreakDoors(false);
      this.entityPathNavigate = this.theEntity.getNavigator().getPathToXYZ(vec3.xCoord, vec3.yCoord, vec3.zCoord);
      pathNavigateGround.setBreakDoors(conditionMet);
      return this.entityPathNavigate != null;
   }
}
