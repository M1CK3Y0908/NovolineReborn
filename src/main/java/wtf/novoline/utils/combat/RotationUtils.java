package wtf.novoline.utils.combat;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.optifine.reflect.Reflector;

public class RotationUtils {
   private static final Minecraft minecraft = Minecraft.getMinecraft();

   public static float[] getRotationsTo(Vec3 vec3) {
      return getRotationsToFace(vec3, null, false);
   }

   public static boolean canHitEntity(EntityLivingBase entityLivingBase, float f) {
      float[] rotationsToEntityExact = getRotationsToEntityExact(entityLivingBase, minecraft.thePlayer.rotationYaw, minecraft.thePlayer.rotationPitch);
      MovingObjectPosition movingObjectPosition = rayTraceEntities(minecraft.thePlayer, f, rotationsToEntityExact[0], rotationsToEntityExact[1]);
      return movingObjectPosition != null && movingObjectPosition.typeOfHit == MovingObjectType.ENTITY;
   }

   public static float getYawToPoint(double d, double d2) {
      EntityPlayerSP thePlayer2 = minecraft.thePlayer;
      float elapsedPartialTicks2 = minecraft.timer.elapsedPartialTicks;
      double d3 = d - (thePlayer2.lastTickPosX + (thePlayer2.posX - thePlayer2.lastTickPosX) * elapsedPartialTicks2);
      double d4 = d2 - (thePlayer2.lastTickPosZ + (thePlayer2.posZ - thePlayer2.lastTickPosZ) * elapsedPartialTicks2);
      return (float)(StrictMath.atan2(d4, d3) * 180.0 / Math.PI) - 90.0F;
   }

   public static float[] getRotationsToFace(Vec3 vec3, EnumFacing enumFacing, boolean flag) {
      double d = vec3.xCoord - minecraft.thePlayer.posX + (flag ? 0.5005 : 0.0);
      double d2 = vec3.yCoord - minecraft.thePlayer.posY - minecraft.thePlayer.getEyeHeight() + (flag ? 0.9343 : 0.0);
      double d3 = vec3.zCoord - minecraft.thePlayer.posZ + (flag ? 0.4995 : 0.0);
      if (enumFacing != null) {
         if (enumFacing == EnumFacing.UP) {
            d2 += 0.5;
         } else if (enumFacing == EnumFacing.DOWN) {
            d2 -= 0.5;
         } else if (enumFacing == EnumFacing.EAST) {
            d += 0.5;
         } else if (enumFacing == EnumFacing.WEST) {
            d -= 0.5;
         } else if (enumFacing == EnumFacing.NORTH) {
            d3 -= 0.5;
         } else if (enumFacing == EnumFacing.SOUTH) {
            d3 += 0.5;
         }
      }

      double d4 = MathHelper.sqrt_double(d * d + d3 * d3);
      float f = (float)(Math.atan2(d3, d) * 180.0 / Math.PI) - 90.0F;
      float f2 = (float)(-(Math.atan2(d2, d4) * 180.0 / Math.PI));
      return new float[]{f, f2};
   }

   public static MovingObjectPosition rayTraceBlocks(Entity entity2, double d, float f, float f2) {
      Vec3 positionEyes = entity2.getPositionEyes(1.0F);
      Vec3 vectorForRotation = getVectorForRotation(f, f2);
      Vec3 vec3 = positionEyes.addVector(vectorForRotation.xCoord * d, vectorForRotation.yCoord * d, vectorForRotation.zCoord * d);
      return minecraft.theWorld.rayTraceBlocks(positionEyes, vec3, false, false, true);
   }

   public static float[] getRotationsToPoint(double d, double d2, double d3) {
      double d4 = d - minecraft.thePlayer.posX;
      double d5 = d2 - minecraft.thePlayer.posY + minecraft.thePlayer.getEyeHeight();
      double d6 = d3 - minecraft.thePlayer.posZ;
      double d7 = MathHelper.sqrt_double(d4 * d4 + d6 * d6);
      float f = (float)(Math.atan2(d6, d4) * 180.0 / Math.PI) - 90.0F;
      float f2 = (float)(-(Math.atan2(d5, d7) * 180.0 / Math.PI));
      return new float[]{f, f2};
   }

   public static float getMoveYawOffset() {
      float f = minecraft.thePlayer.moveForward < 0.0F ? 180.0F : 0.0F;
      float f2 = minecraft.thePlayer.moveStrafing > 0.0F ? -45.0F : (minecraft.thePlayer.moveStrafing < 0.0F ? 45.0F : 0.0F);
      if (minecraft.thePlayer.moveForward == 0.0F) {
         f2 *= 2.0F;
      } else if (minecraft.thePlayer.moveForward < 0.0F) {
         f2 *= -1.0F;
      }

      return minecraft.thePlayer.rotationYaw + f + f2;
   }

   public static float limitAngleChange(float f, float f2, float f3) {
      float f4 = MathHelper.wrapAngleTo180_float(f2 - f);
      if (f4 > f3) {
         f4 = f3;
      }

      if (f4 < -f3) {
         f4 = -f3;
      }

      return f + f4;
   }

   public static Vec3 getVectorForRotation(float f, float f2) {
      float f3 = MathHelper.cos(-f * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f4 = MathHelper.sin(-f * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f5 = -MathHelper.cos(-f2 * (float) (Math.PI / 180.0));
      float f6 = MathHelper.sin(-f2 * (float) (Math.PI / 180.0));
      return new Vec3(f4 * f5, f6, f3 * f5);
   }

   public static float[] getRotationsToEntityExact(EntityLivingBase entityLivingBase, float f, float f2) {
      return getRotationsToEntity(entityLivingBase, f, f2, false);
   }

   public static MovingObjectPosition rayTraceEntities(Entity entity2, double d, float f, float f2) {
      Entity entity3 = entity2;
      MovingObjectPosition movingObjectPosition = null;
      if (entity3 != null && minecraft.theWorld != null) {
         double d2 = d;
         movingObjectPosition = rayTraceBlocks(entity2, d, f, f2);
         double d3 = d2;
         Vec3 positionEyes = entity3.getPositionEyes(1.0F);
         if (movingObjectPosition != null) {
            d3 = movingObjectPosition.hitVec.distanceTo(positionEyes);
         }

         Vec3 vectorForRotation = getVectorForRotation(f, f2);
         Vec3 vec3 = positionEyes.addVector(vectorForRotation.xCoord * d2, vectorForRotation.yCoord * d2, vectorForRotation.zCoord * d2);
         Entity entity4 = null;
         Vec3 vec32 = null;
         float f3 = 1.0F;
         List entitiesInAABBexcluding = minecraft.theWorld
            .getEntitiesInAABBexcluding(
               entity3,
               entity3.getEntityBoundingBox().addCoord(vectorForRotation.xCoord * d2, vectorForRotation.yCoord * d2, vectorForRotation.zCoord * d2).expand(f3, f3, f3),
               Predicates.and(EntitySelectors.NOT_SPECTATING, new RotationUtils.PredicateImpl())
            );
         double d4 = d3;

         for (int i = 0; i < entitiesInAABBexcluding.size(); i++) {
            Entity entity5 = (Entity)entitiesInAABBexcluding.get(i);
            float collisionBorderSize = entity5.getCollisionBorderSize();
            AxisAlignedBB axisAlignedBB = entity5.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
            MovingObjectPosition movingObjectPosition2 = axisAlignedBB.calculateIntercept(positionEyes, vec3);
            if (axisAlignedBB.isVecInside(positionEyes)) {
               if (d4 >= 0.0) {
                  entity4 = entity5;
                  vec32 = movingObjectPosition2 == null ? positionEyes : movingObjectPosition2.hitVec;
                  d4 = 0.0;
               }
            } else if (movingObjectPosition2 != null) {
               double d5 = positionEyes.distanceTo(movingObjectPosition2.hitVec);
               if (d5 < d4 || d4 == 0.0) {
                  boolean flag = false;
                  if (Reflector.reflectorMethod34.isActive()) {
                     flag = Reflector.checkObject(entity5, Reflector.reflectorMethod34);
                  }

                  if (flag || entity5 != entity3.ridingEntity) {
                     entity4 = entity5;
                     vec32 = movingObjectPosition2.hitVec;
                     d4 = d5;
                  } else if (d4 == 0.0) {
                     entity4 = entity5;
                     vec32 = movingObjectPosition2.hitVec;
                  }
               }
            }
         }

         if (entity4 != null) {
         }

         if (entity4 != null && (d4 < d3 || movingObjectPosition == null)) {
            movingObjectPosition = new MovingObjectPosition(entity4, vec32);
            if (!(entity4 instanceof EntityLivingBase) && entity4 instanceof EntityItemFrame) {
            }
         }
      }

      return movingObjectPosition;
   }

   public static float[] getRotationsToEntity(EntityLivingBase entityLivingBase, float f, float f2, boolean flag) {
      Vec3 vec3 = minecraft.thePlayer.getPositionVector().addVector(0.0, minecraft.thePlayer.getEyeHeight(), 0.0);
      AxisAlignedBB entityBoundingBox = entityLivingBase.getEntityBoundingBox();
      float f3 = -1.0F;
      ArrayList arrayList = new ArrayList();
      if (flag) {
         Random random = new Random();
         float f4 = -1.0F;
         arrayList.add(
            new Vec3(entityLivingBase.posX - random.nextFloat() * 0.12, entityBoundingBox.minY + entityLivingBase.getEyeHeight() + f3, entityLivingBase.posZ - random.nextFloat() * 0.12).subtract(vec3)
         );
         arrayList.add(new Vec3(entityBoundingBox.minX, entityBoundingBox.minY + entityLivingBase.getEyeHeight() + f3, entityBoundingBox.maxZ).subtract(vec3));
         arrayList.add(new Vec3(entityBoundingBox.maxX, entityBoundingBox.minY + entityLivingBase.getEyeHeight() + f3, entityBoundingBox.minZ).subtract(vec3));
         arrayList.add(new Vec3(entityBoundingBox.minX, entityBoundingBox.minY + entityLivingBase.getEyeHeight() + f3, entityBoundingBox.minZ).subtract(vec3));
         arrayList.add(new Vec3(entityBoundingBox.maxX, entityBoundingBox.minY + entityLivingBase.getEyeHeight() - f3 - random.nextFloat() * 0.02 + f4, entityBoundingBox.maxZ).subtract(vec3));
         arrayList.add(new Vec3(entityBoundingBox.minX, entityBoundingBox.minY + entityLivingBase.getEyeHeight() - f3 - random.nextFloat() * 0.02 + f4, entityBoundingBox.maxZ).subtract(vec3));
         arrayList.add(new Vec3(entityBoundingBox.maxX, entityBoundingBox.minY + entityLivingBase.getEyeHeight() - f3 - random.nextFloat() * 0.02 + f4, entityBoundingBox.minZ).subtract(vec3));
         arrayList.add(new Vec3(entityBoundingBox.minX, entityBoundingBox.minY + entityLivingBase.getEyeHeight() - f3 - random.nextFloat() * 0.02 + f4, entityBoundingBox.minZ).subtract(vec3));
      } else {
         arrayList.add(new Vec3(entityLivingBase.posX, entityLivingBase.posY, entityLivingBase.posZ).subtract(vec3));
         arrayList.add(new Vec3(entityBoundingBox.minX, entityBoundingBox.minY + entityLivingBase.getEyeHeight(), entityBoundingBox.maxZ).subtract(vec3));
         arrayList.add(new Vec3(entityBoundingBox.maxX, entityBoundingBox.minY + entityLivingBase.getEyeHeight() / 2.0F, entityBoundingBox.minZ).subtract(vec3));
         arrayList.add(new Vec3(entityBoundingBox.minX, entityBoundingBox.maxY, entityBoundingBox.minZ).subtract(vec3));
         arrayList.add(new Vec3(entityBoundingBox.maxX, entityBoundingBox.minY + entityLivingBase.getEyeHeight(), entityBoundingBox.maxZ).subtract(vec3));
         arrayList.add(new Vec3(entityBoundingBox.minX, entityBoundingBox.minY + entityLivingBase.getEyeHeight(), entityBoundingBox.maxZ).subtract(vec3));
         arrayList.add(new Vec3(entityBoundingBox.maxX, entityBoundingBox.minY + entityLivingBase.getEyeHeight(), entityBoundingBox.minZ).subtract(vec3));
         arrayList.add(new Vec3(entityBoundingBox.minX, entityBoundingBox.minY + entityLivingBase.getEyeHeight(), entityBoundingBox.minZ).subtract(vec3));
      }

      ArrayList arrayList2 = new ArrayList();
      ArrayList arrayList3 = new ArrayList();

      for (Vec3 vec32 : (Iterable<Vec3>) arrayList) {
         double xCoord2 = vec32.xCoord;
         double yCoord2 = vec32.yCoord;
         double zCoord2 = vec32.zCoord;
         double d = MathHelper.sqrt_double(xCoord2 * xCoord2 + zCoord2 * zCoord2);
         float f5 = MathHelper.wrapAngleTo180_float((float)(Math.atan2(zCoord2, xCoord2) * 180.0 / Math.PI) - 90.0F);
         float f6 = MathHelper.wrapAngleTo180_float((float)(-(Math.atan2(yCoord2, d) * 180.0 / Math.PI)));
         float f7 = (f5 - f + 180.0F) % 360.0F - 180.0F;
         float f8 = (f6 - f2 + 180.0F) % 360.0F - 180.0F;
         arrayList2.add(f7 < -180.0F ? f7 + 360.0F : f7);
         arrayList3.add(f8 < -180.0F ? f8 + 360.0F : f8);
      }

      float f9 = (Float)Collections.min(arrayList3);
      float f10 = (Float)Collections.max(arrayList3);
      return new float[]{f + (Float)arrayList2.get(0), f2 + f9 + (f10 - f9) * 0.15F};
   }

   static class PredicateImpl implements Predicate<Entity> {
      public boolean apply(Entity arg1) {
         return arg1.canBeCollidedWith();
      }
   }
}
