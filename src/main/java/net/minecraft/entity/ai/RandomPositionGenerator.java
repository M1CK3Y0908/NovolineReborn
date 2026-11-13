package net.minecraft.entity.ai;

import java.util.Random;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RandomPositionGenerator {
   private static Vec3 staticVector = new Vec3(0.0, 0.0, 0.0);

   public static Vec3 findRandomTarget(EntityCreature entitycreatureIn, int xz, int y) {
      return findRandomTargetBlock(entitycreatureIn, xz, y, (Vec3)null);
   }

   private static Vec3 findRandomTargetBlock(EntityCreature entitycreatureIn, int xz, int y, Vec3 targetVec3) {
      Random rng = entitycreatureIn.getRNG();
      boolean flag = false;
      int i = 0;
      int i2 = 0;
      int i3 = 0;
      float f = -99999.0F;
      boolean flag2;
      if (entitycreatureIn.hasHome()) {
         double d = entitycreatureIn.getHomePosition()
               .distanceSq(
                  MathHelper.floor_double(entitycreatureIn.posX),
                  MathHelper.floor_double(entitycreatureIn.posY),
                  MathHelper.floor_double(entitycreatureIn.posZ)
               )
            + 4.0;
         double d2 = entitycreatureIn.getMaximumHomeDistance() + xz;
         flag2 = d < d2 * d2;
      } else {
         flag2 = false;
      }

      for (int i4 = 0; i4 < 10; i4++) {
         int i5 = rng.nextInt(2 * xz + 1) - xz;
         int i6 = rng.nextInt(2 * y + 1) - y;
         int i7 = rng.nextInt(2 * xz + 1) - xz;
         if (targetVec3 == null || i5 * targetVec3.xCoord + i7 * targetVec3.zCoord >= 0.0) {
            if (entitycreatureIn.hasHome() && xz > 1) {
               BlockPos homePosition = entitycreatureIn.getHomePosition();
               if (entitycreatureIn.posX > homePosition.getX()) {
                  i5 -= rng.nextInt(xz / 2);
               } else {
                  i5 += rng.nextInt(xz / 2);
               }

               if (entitycreatureIn.posZ > homePosition.getZ()) {
                  i7 -= rng.nextInt(xz / 2);
               } else {
                  i7 += rng.nextInt(xz / 2);
               }
            }

            i5 += MathHelper.floor_double(entitycreatureIn.posX);
            i6 += MathHelper.floor_double(entitycreatureIn.posY);
            i7 += MathHelper.floor_double(entitycreatureIn.posZ);
            BlockPos blockPos = new BlockPos(i5, i6, i7);
            if (!flag2 || entitycreatureIn.isWithinHomeDistanceFromPosition(blockPos)) {
               float blockPathWeight = entitycreatureIn.getBlockPathWeight(blockPos);
               if (blockPathWeight > f) {
                  f = blockPathWeight;
                  i = i5;
                  i2 = i6;
                  i3 = i7;
                  flag = true;
               }
            }
         }
      }

      return flag ? new Vec3(i, i2, i3) : null;
   }

   public static Vec3 findRandomTargetBlockTowards(EntityCreature entitycreatureIn, int xz, int y, Vec3 targetVec3) {
      staticVector = targetVec3.subtract(entitycreatureIn.posX, entitycreatureIn.posY, entitycreatureIn.posZ);
      return findRandomTargetBlock(entitycreatureIn, xz, y, staticVector);
   }

   public static Vec3 createVec3(EntityCreature entityCreature, Vec3 vec3) {
      staticVector = new Vec3(entityCreature.posX, entityCreature.posY, entityCreature.posZ).subtract(vec3);
      return findRandomTargetBlock(entityCreature, 16, 7, staticVector);
   }
}
