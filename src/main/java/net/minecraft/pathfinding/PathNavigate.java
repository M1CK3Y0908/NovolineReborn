package net.minecraft.pathfinding;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;

public abstract class PathNavigate {
   private Vec3 lastPosCheck = new Vec3(0.0, 0.0, 0.0);
   private int totalTicks;
   private float heightRequirement = 1.0F;
   protected double speed;
   protected EntityLiving theEntity;
   private int ticksAtLastPos;
   protected World worldObj;
   protected PathEntity currentPath;
   private final IAttributeInstance pathSearchRange;
   private final PathFinder pathFinder;

   public PathEntity getPathToEntityLiving(Entity entityIn) {
      if (!this.canNavigate()) {
         return null;
      }

      float pathSearchRange2 = this.getPathSearchRange();
      this.worldObj.theProfiler.startSection("pathfind");
      BlockPos blockPos = new BlockPos(this.theEntity).up();
      int i = (int)(pathSearchRange2 + 16.0F);
      ChunkCache chunkCache = new ChunkCache(this.worldObj, blockPos.add(-i, -i, -i), blockPos.add(i, i, i), 0);
      PathEntity pathEntity = this.pathFinder.createEntityPathTo(chunkCache, this.theEntity, entityIn, pathSearchRange2);
      this.worldObj.theProfiler.endSection();
      return pathEntity;
   }

   protected abstract boolean canNavigate();

   public void setHeightRequirement(float jumpHeight) {
      this.heightRequirement = jumpHeight;
   }

   public boolean tryMoveToEntityLiving(Entity entityIn, double speedIn) {
      PathEntity pathEntity = this.getPathToEntityLiving(entityIn);
      return pathEntity != null ? this.setPath(pathEntity, speedIn) : false;
   }

   public float getPathSearchRange() {
      return (float)this.pathSearchRange.getAttributeValue();
   }

   protected void checkForStuck(Vec3 positionVec3) {
      if (this.totalTicks - this.ticksAtLastPos > 100) {
         if (positionVec3.squareDistanceTo(this.lastPosCheck) < 2.25) {
            this.clearPathEntity();
         }

         this.ticksAtLastPos = this.totalTicks;
         this.lastPosCheck = positionVec3;
      }
   }

   public boolean setPath(PathEntity pathentityIn, double speedIn) {
      if (pathentityIn == null) {
         this.currentPath = null;
         return false;
      }

      if (!pathentityIn.isSamePath(this.currentPath)) {
         this.currentPath = pathentityIn;
      }

      this.removeSunnyPath();
      if (this.currentPath.getCurrentPathLength() == 0) {
         return false;
      }

      this.speed = speedIn;
      Vec3 vec3 = this.getEntityPosition();
      this.ticksAtLastPos = this.totalTicks;
      this.lastPosCheck = vec3;
      return true;
   }

   public void clearPathEntity() {
      this.currentPath = null;
   }

   public final PathEntity getPathToXYZ(double x, double y, double z) {
      return this.getPathToPos(new BlockPos(MathHelper.floor_double(x), (int)y, MathHelper.floor_double(z)));
   }

   public PathEntity getPathToPos(BlockPos pos) {
      if (!this.canNavigate()) {
         return null;
      }

      float pathSearchRange2 = this.getPathSearchRange();
      this.worldObj.theProfiler.startSection("pathfind");
      BlockPos blockPos2 = new BlockPos(this.theEntity);
      int i = (int)(pathSearchRange2 + 8.0F);
      ChunkCache chunkCache = new ChunkCache(this.worldObj, blockPos2.add(-i, -i, -i), blockPos2.add(i, i, i), 0);
      PathEntity pathEntity = this.pathFinder.createEntityPathTo(chunkCache, this.theEntity, pos, pathSearchRange2);
      this.worldObj.theProfiler.endSection();
      return pathEntity;
   }

   public PathNavigate(EntityLiving entitylivingIn, World worldIn) {
      this.theEntity = entitylivingIn;
      this.worldObj = worldIn;
      this.pathSearchRange = entitylivingIn.getEntityAttribute(SharedMonsterAttributes.followRange);
      this.pathFinder = this.getPathFinder();
   }

   public boolean noPath() {
      return this.currentPath == null || this.currentPath.isFinished();
   }

   protected abstract PathFinder getPathFinder();

   public boolean tryMoveToXYZ(double x, double y, double z, double speedIn) {
      PathEntity pathEntity = this.getPathToXYZ(MathHelper.floor_double(x), (int)y, MathHelper.floor_double(z));
      return this.setPath(pathEntity, speedIn);
   }

   public PathEntity getPath() {
      return this.currentPath;
   }

   protected abstract boolean isDirectPathBetweenPoints(Vec3 vec33, Vec3 vec34, int i, int i2, int i3);

   public void setSpeed(double speedIn) {
      this.speed = speedIn;
   }

   protected abstract Vec3 getEntityPosition();

   protected void removeSunnyPath() {
   }

   protected void pathFollow() {
      Vec3 vec3 = this.getEntityPosition();
      int currentPathLength = this.currentPath.getCurrentPathLength();

      for (int innerIndex = this.currentPath.getCurrentPathIndex(); innerIndex < this.currentPath.getCurrentPathLength(); innerIndex++) {
         if (this.currentPath.getPathPointFromIndex(innerIndex).yCoord != (int)vec3.yCoord) {
            currentPathLength = innerIndex;
            break;
         }
      }

      float f = this.theEntity.width * this.theEntity.width * this.heightRequirement;

      for (int nestedIndex = this.currentPath.getCurrentPathIndex(); nestedIndex < currentPathLength; nestedIndex++) {
         Vec3 vec32 = this.currentPath.getVectorFromIndex(this.theEntity, nestedIndex);
         if (vec3.squareDistanceTo(vec32) < f) {
            this.currentPath.setCurrentPathIndex(nestedIndex + 1);
         }
      }

      int i = MathHelper.ceiling_float_int(this.theEntity.width);
      int i2 = (int)this.theEntity.height + 1;
      int i3 = i;

      for (int index = currentPathLength - 1; index >= this.currentPath.getCurrentPathIndex(); index--) {
         if (this.isDirectPathBetweenPoints(vec3, this.currentPath.getVectorFromIndex(this.theEntity, index), i, i2, i3)) {
            this.currentPath.setCurrentPathIndex(index);
            break;
         }
      }

      this.checkForStuck(vec3);
   }

   public void onUpdateNavigation() {
      this.totalTicks++;
      if (!this.noPath()) {
         if (this.canNavigate()) {
            this.pathFollow();
         } else if (this.currentPath != null && this.currentPath.getCurrentPathIndex() < this.currentPath.getCurrentPathLength()) {
            Vec3 entityPosition = this.getEntityPosition();
            Vec3 vectorFromIndex = this.currentPath.getVectorFromIndex(this.theEntity, this.currentPath.getCurrentPathIndex());
            if (entityPosition.yCoord > vectorFromIndex.yCoord
               && !this.theEntity.onGround
               && MathHelper.floor_double(entityPosition.xCoord) == MathHelper.floor_double(vectorFromIndex.xCoord)
               && MathHelper.floor_double(entityPosition.zCoord) == MathHelper.floor_double(vectorFromIndex.zCoord)) {
               this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
            }
         }

         if (!this.noPath()) {
            Vec3 position = this.currentPath.getPosition(this.theEntity);
            if (position != null) {
               AxisAlignedBB axisAlignedBB = new AxisAlignedBB(position.xCoord, position.yCoord, position.zCoord, position.xCoord, position.yCoord, position.zCoord)
                  .expand(0.5, 0.5, 0.5);
               List collidingBoundingBoxes = this.worldObj.getCollidingBoundingBoxes(this.theEntity, axisAlignedBB.addCoord(0.0, -1.0, 0.0));
               double d = -1.0;
               axisAlignedBB = axisAlignedBB.offset(0.0, 1.0, 0.0);
               Iterator iterator2 = collidingBoundingBoxes.iterator();

               while (iterator2.hasNext()) {
                  d = ((AxisAlignedBB)iterator2.next()).calculateYOffset(axisAlignedBB, d);
               }

               this.theEntity.getMoveHelper().setMoveTo(position.xCoord, position.yCoord + d, position.zCoord, this.speed);
            }
         }
      }
   }

   protected boolean isInLiquid() {
      return this.theEntity.isInWater() || this.theEntity.isInLava();
   }
}
