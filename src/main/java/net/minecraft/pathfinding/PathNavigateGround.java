package net.minecraft.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.pathfinder.WalkNodeProcessor;

public class PathNavigateGround extends PathNavigate {
   private boolean shouldAvoidSun;
   protected WalkNodeProcessor nodeProcessor;

   @Override
   protected boolean canNavigate() {
      return this.theEntity.onGround
         || this.getCanSwim() && this.isInLiquid()
         || this.theEntity.isRiding() && this.theEntity instanceof EntityZombie && this.theEntity.ridingEntity instanceof EntityChicken;
   }

   private int getPathablePosY() {
      if (this.theEntity.isInWater() && this.getCanSwim()) {
         int i = (int)this.theEntity.getEntityBoundingBox().minY;
         Block block = this.worldObj
            .getBlockState(new BlockPos(MathHelper.floor_double(this.theEntity.posX), i, MathHelper.floor_double(this.theEntity.posZ)))
            .getBlock();
         int i2 = 0;

         while (block == Blocks.flowing_water || block == Blocks.water) {
            block = this.worldObj
               .getBlockState(new BlockPos(MathHelper.floor_double(this.theEntity.posX), ++i, MathHelper.floor_double(this.theEntity.posZ)))
               .getBlock();
            if (++i2 > 16) {
               return (int)this.theEntity.getEntityBoundingBox().minY;
            }
         }

         return i;
      } else {
         return (int)(this.theEntity.getEntityBoundingBox().minY + 0.5);
      }
   }

   public boolean getAvoidsWater() {
      return this.nodeProcessor.getAvoidsWater();
   }

   @Override
   protected Vec3 getEntityPosition() {
      return new Vec3(this.theEntity.posX, this.getPathablePosY(), this.theEntity.posZ);
   }

   @Override
   protected boolean isDirectPathBetweenPoints(Vec3 posVec31, Vec3 posVec32, int sizeX, int sizeY, int sizeZ) {
      int i = MathHelper.floor_double(posVec31.xCoord);
      int i2 = MathHelper.floor_double(posVec31.zCoord);
      double d = posVec32.xCoord - posVec31.xCoord;
      double d2 = posVec32.zCoord - posVec31.zCoord;
      double d3 = d * d + d2 * d2;
      if (d3 < 1.0E-8) {
         return false;
      }

      double d4 = 1.0 / Math.sqrt(d3);
      d *= d4;
      d2 *= d4;
      sizeX += 2;
      sizeZ += 2;
      if (!this.isSafeToStandAt(i, (int)posVec31.yCoord, i2, sizeX, sizeY, sizeZ, posVec31, d, d2)) {
         return false;
      }

      sizeX -= 2;
      sizeZ -= 2;
      double d5 = 1.0 / Math.abs(d);
      double d6 = 1.0 / Math.abs(d2);
      double d7 = i * 1 - posVec31.xCoord;
      double d8 = i2 * 1 - posVec31.zCoord;
      if (d >= 0.0) {
         d7++;
      }

      if (d2 >= 0.0) {
         d8++;
      }

      d7 /= d;
      d8 /= d2;
      int i3 = d < 0.0 ? -1 : 1;
      int i4 = d2 < 0.0 ? -1 : 1;
      int i5 = MathHelper.floor_double(posVec32.xCoord);
      int i6 = MathHelper.floor_double(posVec32.zCoord);
      int i7 = i5 - i;
      int i8 = i6 - i2;

      while (i7 * i3 > 0 || i8 * i4 > 0) {
         if (d7 < d8) {
            d7 += d5;
            i += i3;
            i7 = i5 - i;
         } else {
            d8 += d6;
            i2 += i4;
            i8 = i6 - i2;
         }

         if (!this.isSafeToStandAt(i, (int)posVec31.yCoord, i2, sizeX, sizeY, sizeZ, posVec31, d, d2)) {
            return false;
         }
      }

      return true;
   }

   public void setAvoidsWater(boolean avoidsWater) {
      this.nodeProcessor.setAvoidsWater(avoidsWater);
   }

   public void setAvoidSun(boolean par1) {
      this.shouldAvoidSun = par1;
   }

   public boolean getEnterDoors() {
      return this.nodeProcessor.getEnterDoors();
   }

   public void setEnterDoors(boolean par1) {
      this.nodeProcessor.setEnterDoors(par1);
   }

   public PathNavigateGround(EntityLiving entitylivingIn, World worldIn) {
      super(entitylivingIn, worldIn);
   }

   private boolean isSafeToStandAt(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3 vec3, double d, double d2) {
      int outerIndex = x - sizeX / 2;
      int i = z - sizeZ / 2;
      if (!this.isPositionClear(outerIndex, y, i, sizeX, sizeY, sizeZ, vec3, d, d2)) {
         return false;
      }

      for (int outerIndex2 = outerIndex; outerIndex2 < outerIndex + sizeX; outerIndex2++) {
         for (int i2 = i; i2 < i + sizeZ; i2++) {
            double d3 = outerIndex2 + 0.5 - vec3.xCoord;
            double d4 = i2 + 0.5 - vec3.zCoord;
            if (d3 * d + d4 * d2 >= 0.0) {
               Material material2 = this.worldObj.getBlockState(new BlockPos(outerIndex2, y - 1, i2)).getBlock().getMaterial();
               if (material2 == Material.air) {
                  return false;
               }

               if (material2 == Material.water && !this.theEntity.isInWater()) {
                  return false;
               }

               if (material2 == Material.lava) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   public void update() {
      this.nodeProcessor.update();
   }

   private boolean isPositionClear(int i, int i2, int i3, int i4, int i5, int i6, Vec3 vec32, double d, double d2) {
      for (BlockPos blockPos : BlockPos.getAllInBox(
         new BlockPos(i, i2, i3), new BlockPos(i + i4 - 1, i2 + i5 - 1, i3 + i6 - 1)
      )) {
         double d3 = blockPos.getX() + 0.5 - vec32.xCoord;
         double d4 = blockPos.getZ() + 0.5 - vec32.zCoord;
         if (d3 * d + d4 * d2 >= 0.0 && !this.worldObj.getBlockState(blockPos).getBlock().isPassable(this.worldObj, blockPos)) {
            return false;
         }
      }

      return true;
   }

   public void setBreakDoors(boolean canBreakDoors) {
      this.nodeProcessor.setBreakDoors(canBreakDoors);
   }

   @Override
   protected void removeSunnyPath() {
      super.removeSunnyPath();
      if (this.shouldAvoidSun) {
         if (this.worldObj
            .canSeeSky(
               new BlockPos(
                  MathHelper.floor_double(this.theEntity.posX),
                  (int)(this.theEntity.getEntityBoundingBox().minY + 0.5),
                  MathHelper.floor_double(this.theEntity.posZ)
               )
            )) {
            return;
         }

         for (int index = 0; index < this.currentPath.getCurrentPathLength(); index++) {
            PathPoint pathPoint = this.currentPath.getPathPointFromIndex(index);
            if (this.worldObj.canSeeSky(new BlockPos(pathPoint.xCoord, pathPoint.yCoord, pathPoint.zCoord))) {
               this.currentPath.setCurrentPathLength(index - 1);
               return;
            }
         }
      }
   }

   @Override
   protected PathFinder getPathFinder() {
      this.nodeProcessor = new WalkNodeProcessor();
      this.nodeProcessor.setEnterDoors(true);
      return new PathFinder(this.nodeProcessor);
   }

   public boolean getCanSwim() {
      return this.nodeProcessor.getCanSwim();
   }
}
