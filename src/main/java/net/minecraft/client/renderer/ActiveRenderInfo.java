package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.visual.FreeLook;

public class ActiveRenderInfo {
   public static final IntBuffer VIEWPORT = GLAllocation.createDirectIntBuffer(16);
   private static float rotationXY;
   public static final FloatBuffer MODELVIEW = GLAllocation.createDirectFloatBuffer(16);
   private static float rotationXZ;
   public static final FloatBuffer PROJECTION = GLAllocation.createDirectFloatBuffer(16);
   private static float rotationYZ;
   private static final FloatBuffer OBJECTCOORDS = GLAllocation.createDirectFloatBuffer(3);
   private static float rotationX;
   private static Vec3 position = new Vec3(0.0, 0.0, 0.0);
   private static float rotationZ;

   public static float getRotationZ() {
      return rotationZ;
   }

   public static float getRotationXZ() {
      return rotationXZ;
   }

   public static Vec3 getPosition() {
      return position;
   }

   public static void updateRenderInfo(EntityPlayer entityplayerIn, boolean flag) {
      GlStateManager.getFloat(2982, MODELVIEW);
      GlStateManager.getFloat(2983, PROJECTION);
      GL11.glGetInteger(2978, VIEWPORT);
      float f = (VIEWPORT.get(0) + VIEWPORT.get(2)) / 2;
      float f2 = (VIEWPORT.get(1) + VIEWPORT.get(3)) / 2;
      GLU.gluUnProject(f, f2, 0.0F, MODELVIEW, PROJECTION, VIEWPORT, OBJECTCOORDS);
      position = new Vec3(OBJECTCOORDS.get(0), OBJECTCOORDS.get(1), OBJECTCOORDS.get(2));
      int i = flag ? 1 : 0;
      float rotationPitch2 = entityplayerIn.rotationPitch;
      float rotationYaw2 = entityplayerIn.rotationYaw;
      FreeLook freeLook2 = ModuleManager.freeLook;
      if (freeLook2.isFreeLookActive()) {
         rotationPitch2 = freeLook2.getLookPitch();
         rotationYaw2 = freeLook2.getLookYaw();
      }

      rotationX = MathHelper.cos(rotationYaw2 * (float) Math.PI / 180.0F) * (1 - i * 2);
      rotationZ = MathHelper.sin(rotationYaw2 * (float) Math.PI / 180.0F) * (1 - i * 2);
      rotationYZ = -rotationZ * MathHelper.sin(rotationPitch2 * (float) Math.PI / 180.0F) * (1 - i * 2);
      rotationXY = rotationX * MathHelper.sin(rotationPitch2 * (float) Math.PI / 180.0F) * (1 - i * 2);
      rotationXZ = MathHelper.cos(rotationPitch2 * (float) Math.PI / 180.0F);
   }

   public static float getRotationXY() {
      return rotationXY;
   }

   public static Block getBlockAtEntityViewpoint(World worldIn, Entity entity2, float f) {
      Vec3 vec3 = projectViewFromEntity(entity2, f);
      BlockPos blockPos = new BlockPos(vec3);
      IBlockState blockState = worldIn.getBlockState(blockPos);
      Block block = blockState.getBlock();
      if (block.getMaterial().isLiquid()) {
         float f2 = 0.0F;
         if (blockState.getBlock() instanceof BlockLiquid) {
            f2 = BlockLiquid.getLiquidHeightPercent(blockState.getValue(BlockLiquid.LEVEL)) - 0.11111111F;
         }

         float f3 = blockPos.getY() + 1 - f2;
         if (vec3.yCoord >= f3) {
            block = worldIn.getBlockState(blockPos.up()).getBlock();
         }
      }

      return block;
   }

   public static Vec3 projectViewFromEntity(Entity entity2, double d) {
      double xCoordinate = entity2.prevPosX + (entity2.posX - entity2.prevPosX) * d;
      double yCoordinate = entity2.prevPosY + (entity2.posY - entity2.prevPosY) * d;
      double zCoordinate = entity2.prevPosZ + (entity2.posZ - entity2.prevPosZ) * d;
      double d2 = xCoordinate + position.xCoord;
      double d3 = yCoordinate + position.yCoord;
      double d4 = zCoordinate + position.zCoord;
      return new Vec3(d2, d3, d4);
   }

   public static float getRotationYZ() {
      return rotationYZ;
   }

   public static float getRotationX() {
      return rotationX;
   }
}
