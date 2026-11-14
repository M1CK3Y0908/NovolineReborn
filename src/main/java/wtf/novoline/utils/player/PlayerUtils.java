package wtf.novoline.utils.player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
import wtf.novoline.event.MoveFlyingEvent;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import wtf.novoline.utils.combat.RotationUtils;

public class PlayerUtils {
   private static final Minecraft minecraft = Minecraft.getMinecraft();

   public static boolean isInsideBlock() {
      for (int i = MathHelper.floor_double(minecraft.thePlayer.getEntityBoundingBox().minX);
         i < MathHelper.floor_double(minecraft.thePlayer.getEntityBoundingBox().maxX) + 1;
         i++
      ) {
         for (int i2 = MathHelper.floor_double(minecraft.thePlayer.getEntityBoundingBox().minY);
            i2 < MathHelper.floor_double(minecraft.thePlayer.getEntityBoundingBox().maxY) + 1;
            i2++
         ) {
            for (int i3 = MathHelper.floor_double(minecraft.thePlayer.getEntityBoundingBox().minZ);
               i3 < MathHelper.floor_double(minecraft.thePlayer.getEntityBoundingBox().maxZ) + 1;
               i3++
            ) {
               Block block2 = minecraft.theWorld.getBlockState(new BlockPos(i, i2, i3)).getBlock();
               AxisAlignedBB axisAlignedBB;
               if (block2 != null
                  && !(block2 instanceof BlockAir)
                  && (
                        axisAlignedBB = block2.getCollisionBoundingBox(
                           minecraft.theWorld, new BlockPos(i, i2, i3), minecraft.theWorld.getBlockState(new BlockPos(i, i2, i3))
                        )
                     )
                     != null
                  && minecraft.thePlayer.getEntityBoundingBox().intersectsWith(axisAlignedBB)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static float getMoveDirectionRadians() {
      float rotationYaw2 = minecraft.thePlayer.rotationYaw;
      if (minecraft.thePlayer.moveForward < 0.0F) {
         rotationYaw2 += 180.0F;
      }

      float f2 = 1.0F;
      if (minecraft.thePlayer.moveForward < 0.0F) {
         f2 = -0.5F;
      } else if (minecraft.thePlayer.moveForward > 0.0F) {
         f2 = 0.5F;
      }

      if (minecraft.thePlayer.moveStrafing > 0.0F) {
         rotationYaw2 -= 90.0F * f2;
      }

      if (minecraft.thePlayer.moveStrafing < 0.0F) {
         rotationYaw2 += 90.0F * f2;
      }

      return rotationYaw2 * (float) (Math.PI / 180.0);
   }

   public static boolean hasBlockBelowAt(double d, double d2, double d3) {
      for (int i = (int)d2; i > 0; i--) {
         if (minecraft.theWorld.getBlockState(new BlockPos(d, i, d3)).getBlock() != Blocks.air) {
            return true;
         }
      }

      return false;
   }

   public static double getDefaultMoveSpeed() {
      return getBaseMoveSpeed(0.2);
   }

   public static EntityOtherPlayerMP getEntityOtherPlayerMP() {
      EntityOtherPlayerMP entityOtherPlayerMP = new EntityOtherPlayerMP(minecraft.theWorld, minecraft.thePlayer.getGameProfile());
      entityOtherPlayerMP.inventory = minecraft.thePlayer.inventory;
      entityOtherPlayerMP.inventoryContainer = minecraft.thePlayer.inventoryContainer;
      entityOtherPlayerMP.onGround = minecraft.thePlayer.onGround;
      entityOtherPlayerMP.getDataWatcher().updateObject(10, minecraft.thePlayer.getDataWatcher().getWatchableObjectByte(10));
      entityOtherPlayerMP.setPositionAndRotation(
         minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, minecraft.thePlayer.rotationYaw, minecraft.thePlayer.rotationPitch
      );
      return entityOtherPlayerMP;
   }

   public static String getTeamColorCode(EntityPlayer entityPlayer) {
      Matcher matcher2 = Pattern.compile("§(.).*§r").matcher(entityPlayer.getDisplayName().getFormattedText());
      return matcher2.find() ? matcher2.group(1) : "f";
   }

   public static boolean isStandingOnLiquid() {
      double d = minecraft.thePlayer.posY - 0.1;

      for (int i = MathHelper.floor_double(minecraft.thePlayer.posX); i < MathHelper.ceiling_double_int(minecraft.thePlayer.posX); i++) {
         for (int i2 = MathHelper.floor_double(minecraft.thePlayer.posZ); i2 < MathHelper.ceiling_double_int(minecraft.thePlayer.posZ); i2++) {
            BlockPos blockPos = new BlockPos(i, MathHelper.floor_double(d), i2);
            if (minecraft.theWorld.getBlockState(blockPos).getBlock() instanceof BlockLiquid) {
               return true;
            }
         }
      }

      return false;
   }

   public static float getMoveDirectionDegrees() {
      float f2 = MathHelper.wrapAngleTo180_float(minecraft.thePlayer.rotationYaw);
      MovementInput movementInput2 = minecraft.thePlayer.movementInput;
      float moveStrafe2 = movementInput2.moveStrafe;
      float moveForward2 = movementInput2.moveForward;
      if (moveForward2 != 0.0F) {
         if (moveStrafe2 < 0.0F) {
            f2 += moveForward2 < 0.0F ? 135.0F : 45.0F;
         } else if (moveStrafe2 > 0.0F) {
            f2 -= moveForward2 < 0.0F ? 135.0F : 45.0F;
         } else if (moveForward2 < 0.0F) {
            f2 -= 180.0F;
         }
      } else if (moveStrafe2 < 0.0F) {
         f2 += 90.0F;
      } else if (moveStrafe2 > 0.0F) {
         f2 -= 90.0F;
      }

      return MathHelper.wrapAngleTo180_float(f2);
   }

   public static boolean isOnSolidGround() {
      double minY2 = minecraft.thePlayer.getEntityBoundingBox().offset(0.0, -0.01, 0.0).minY;
      Block block2 = Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(minecraft.thePlayer.posX, minY2, minecraft.thePlayer.posZ)).getBlock();
      return block2 != null && !(block2 instanceof BlockAir) && !(block2 instanceof BlockLiquid) ? minecraft.thePlayer.onGround : false;
   }

   public static void setSpeed(double d) {
      double moveForward2 = minecraft.thePlayer.moveForward;
      double moveStrafing2 = minecraft.thePlayer.moveStrafing;
      float rotationYaw2 = minecraft.thePlayer.rotationYaw;
      if (moveForward2 != 0.0) {
         if (moveStrafing2 > 0.0) {
            rotationYaw2 += moveForward2 > 0.0 ? -45 : 45;
         } else if (moveStrafing2 < 0.0) {
            rotationYaw2 += moveForward2 > 0.0 ? 45 : -45;
         }

         moveStrafing2 = 0.0;
         if (moveForward2 > 0.0) {
            moveForward2 = 1.0;
         } else if (moveForward2 < 0.0) {
            moveForward2 = -1.0;
         }
      }

      if (moveStrafing2 > 0.0) {
         moveStrafing2 = 1.0;
      } else if (moveStrafing2 < 0.0) {
         moveStrafing2 = -1.0;
      }

      double d2 = Math.cos(Math.toRadians(rotationYaw2 + 90.0F));
      double d3 = Math.sin(Math.toRadians(rotationYaw2 + 90.0F));
      minecraft.thePlayer.motionX = moveForward2 * d * d2 + moveStrafing2 * d * d3;
      minecraft.thePlayer.motionZ = moveForward2 * d * d3 - moveStrafing2 * d * d2;
   }

   public static double getSprintSpeed(double d, int i) {
      double d2 = minecraft.thePlayer.isSprinting() ? 0.287 : 0.223;
      return minecraft.thePlayer.isPotionActive(Potion.moveSpeed) ? d2 * (1.0 + d * i) : d2;
   }

   public static double getJumpMotion() {
      return addJumpBoost(0.419999986886978);
   }

   public static boolean hasCollisionBelowEntity(double d, Entity entity2) {
      return !minecraft.theWorld.getCollidingBoundingBoxes(minecraft.thePlayer, entity2.getEntityBoundingBox().offset(0.0, -d, 0.0)).isEmpty();
   }

   public static boolean isInsideLiquid() {
      double d = minecraft.thePlayer.posY + 0.01;

      for (int i = MathHelper.floor_double(minecraft.thePlayer.posX); i < MathHelper.ceiling_double_int(minecraft.thePlayer.posX); i++) {
         for (int i2 = MathHelper.floor_double(minecraft.thePlayer.posZ); i2 < MathHelper.ceiling_double_int(minecraft.thePlayer.posZ); i2++) {
            BlockPos blockPos = new BlockPos(i, (int)d, i2);
            if (minecraft.theWorld.getBlockState(blockPos).getBlock() instanceof BlockLiquid) {
               return true;
            }
         }
      }

      return false;
   }

   public static void setMotionInMoveDirection(double d) {
      minecraft.thePlayer.motionX = -(Math.sin(getMoveDirectionRadians()) * d);
      minecraft.thePlayer.motionZ = Math.cos(getMoveDirectionRadians()) * d;
   }

   public static boolean hasCollisionBelow(double d) {
      return hasCollisionBelowEntity(d, minecraft.thePlayer);
   }

   public static double getBaseMoveSpeed(double d) {
      double d2 = minecraft.thePlayer.isSprinting() ? 0.287 : 0.223;
      if (minecraft.thePlayer.isPotionActive(Potion.moveSpeed)) {
         int amplifier = minecraft.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
         if (minecraft.thePlayer.isPotionActive(Potion.moveSlowdown)) {
            int amplifier2 = minecraft.thePlayer.getActivePotionEffect(Potion.moveSlowdown).getAmplifier();
            amplifier -= amplifier2;
         }

         return d2 * (1.0 + d * amplifier);
      } else {
         return d2;
      }
   }

   public static boolean hasBlockBelow() {
      for (int i = (int)(minecraft.thePlayer.posY - 1.0); i > 0; i--) {
         BlockPos blockPos = new BlockPos(minecraft.thePlayer.posX, i, minecraft.thePlayer.posZ);
         if (!(minecraft.theWorld.getBlockState(blockPos).getBlock() instanceof BlockAir)) {
            return true;
         }
      }

      return false;
   }

   public static double getLastTickDistance() {
      return Math.hypot(minecraft.thePlayer.posX - minecraft.thePlayer.prevPosX, minecraft.thePlayer.posZ - minecraft.thePlayer.prevPosZ);
   }

   public static boolean isMoving() {
      return minecraft.thePlayer.movementInput.moveForward != 0.0F || minecraft.thePlayer.movementInput.moveStrafe != 0.0F;
   }

   public static double addJumpBoost(double d) {
      double d2 = d;
      if (minecraft.thePlayer.isPotionActive(Potion.jump)) {
         d2 += 0.1 * (minecraft.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1);
      }

      return d2;
   }

   public static boolean isSameTeam(EntityPlayer entityPlayer, EntityPlayer entityPlayer2) {
      return getTeamColorCode(entityPlayer).equals(getTeamColorCode(entityPlayer2));
   }

   public static void correctMovement(MoveFlyingEvent moveFlyingEvent, float f2) {
      float strafe2 = moveFlyingEvent.strafe;
      float forward2 = moveFlyingEvent.forward;
      float friction2 = moveFlyingEvent.friction;
      int i = (int)((MathHelper.wrapAngleTo180_float(minecraft.thePlayer.rotationYaw - f2 - 23.5F - 135.0F) + 180.0F) / 45.0F);
      float f3 = 0.0F;
      float f4 = 0.0F;
      switch (i) {
         case 0:
            f3 = forward2;
            f4 = strafe2;
            break;
         case 1:
            f3 += forward2;
            f4 -= forward2;
            f3 += strafe2;
            f4 += strafe2;
            break;
         case 2:
            f3 = strafe2;
            f4 = -forward2;
            break;
         case 3:
            f3 -= forward2;
            f4 -= forward2;
            f3 += strafe2;
            f4 -= strafe2;
            break;
         case 4:
            f3 = -forward2;
            f4 = -strafe2;
            break;
         case 5:
            f3 -= forward2;
            f4 += forward2;
            f3 -= strafe2;
            f4 -= strafe2;
            break;
         case 6:
            f3 = -strafe2;
            f4 = forward2;
            break;
         case 7:
            f3 += forward2;
            f4 += forward2;
            f3 -= strafe2;
            f4 += strafe2;
      }

      if (f3 > 1.0F || f3 < 0.9F && f3 > 0.3F || f3 < -1.0F || f3 > -0.9F && f3 < -0.3F) {
         f3 *= 0.5F;
      }

      if (f4 > 1.0F || f4 < 0.9F && f4 > 0.3F || f4 < -1.0F || f4 > -0.9F && f4 < -0.3F) {
         f4 *= 0.5F;
      }

      float f5 = f4 * f4 + f3 * f3;
      if (f5 >= 1.0E-4F) {
         f5 = (float)(friction2 / Math.max(1.0, Math.sqrt(f5)));
         f4 *= f5;
         f3 *= f5;
         float f6 = MathHelper.sin((float)(f2 * Math.PI / 180.0));
         float f7 = MathHelper.cos((float)(f2 * Math.PI / 180.0));
         minecraft.thePlayer.motionX += f4 * f7 - f3 * f6;
         minecraft.thePlayer.motionZ += f3 * f7 + f4 * f6;
      }
   }

   public static boolean inTeam(ICommandSender entity0, ICommandSender entity1) {
      if (entity0 == null || entity1 == null) return false;
      String s = "§" + teamColor(entity0);
      return entity0.getDisplayName().getFormattedText().contains(s)
            && entity1.getDisplayName().getFormattedText().contains(s);
   }

   public static boolean inTeamWithMinecraftPlayer(ICommandSender entity) {
      if (minecraft.thePlayer == null || entity == null) return false;
      return inTeam(minecraft.thePlayer, entity);
   }

   public static String teamColor(ICommandSender player) {
      Matcher matcher = Pattern.compile("§(.).*§r").matcher(player.getDisplayName().getFormattedText());
      return matcher.find() ? matcher.group(1) : "f";
   }

   public static boolean inFov(float fov, Entity entity) {
      if (minecraft.thePlayer == null || entity == null) return false;
      float[] rot = RotationUtils.toEntityBox(entity);
      if (rot == null) return false;
      float diffYaw = MathHelper.wrapAngleTo180_float(minecraft.thePlayer.rotationYaw - rot[0]);
      return Math.abs(diffYaw) <= fov / 2.0f;
   }

   public static boolean isInvisibleUnarmoured(EntityLivingBase entity) {
      if (entity == null || !entity.isInvisible()) {
         return false;
      }
      for (int i = 0; i <= 4; ++i) {
         if (entity.getEquipmentInSlot(i) != null) {
            return false;
         }
      }
      return true;
   }

   public static boolean holdingSword() {
      if (minecraft.thePlayer == null || minecraft.thePlayer.getHeldItem() == null) return false;
      return minecraft.thePlayer.getHeldItem().getItem() instanceof ItemSword;
   }

   public static boolean holdingWeapon() {
      if (minecraft.thePlayer == null || minecraft.thePlayer.getHeldItem() == null) return false;
      Item item = minecraft.thePlayer.getHeldItem().getItem();
      return item instanceof ItemSword || item instanceof ItemAxe;
   }

}
