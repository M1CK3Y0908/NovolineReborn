package net.minecraft.client.resources.model;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.renderer.block.model.ITransformation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.optifine.reflect.Reflector;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import wtf.novoline.api.Contract5;

public enum ModelRotation implements Contract5, ITransformation {
   X0_Y0(0, 0),
   X0_Y90(0, 90),
   X0_Y180(0, 180),
   X0_Y270(0, 270),
   X90_Y0(90, 0),
   X90_Y90(90, 90),
   X90_Y180(90, 180),
   X90_Y270(90, 270),
   X180_Y0(180, 0),
   X180_Y90(180, 90),
   X180_Y180(180, 180),
   X180_Y270(180, 270),
   X270_Y0(270, 0),
   X270_Y90(270, 90),
   X270_Y180(270, 180),
   X270_Y270(270, 270);

   private final int combinedXY;
   private final Matrix4f matrix4d;
   private final int quartersX;
   private final int quartersY;
   private static final Map<Integer, ModelRotation> mapRotations = Maps.newHashMap();

   public EnumFacing rotateFace(EnumFacing enumFacing) {
      EnumFacing facing = enumFacing;

      for (int index = 0; index < this.quartersX; index++) {
         facing = facing.rotateAround(net.minecraft.util.EnumFacing.Axis.X);
      }

      if (facing.getAxis() != net.minecraft.util.EnumFacing.Axis.Y) {
         for (int innerIndex = 0; innerIndex < this.quartersY; innerIndex++) {
            facing = facing.rotateAround(net.minecraft.util.EnumFacing.Axis.Y);
         }
      }

      return facing;
   }

   @Override
   public javax.vecmath.Matrix4f getMatrix4f() {
      return Reflector.reflectorMethod61.isActive()
         ? (javax.vecmath.Matrix4f)Reflector.createObject2(Reflector.reflectorMethod61, this)
         : new javax.vecmath.Matrix4f();
   }

   @Override
   public int rotate(EnumFacing arg1, int arg2) {
      return this.rotateVertex(arg1, arg2);
   }

   public Matrix4f getMatrix4d() {
      return this.matrix4d;
   }

   private static int combineXY(int i, int i2) {
      return i * 360 + i2;
   }

   public static ModelRotation getModelRotation(int i, int i2) {
      return mapRotations.get(combineXY(MathHelper.normalizeAngle(i, 360), MathHelper.normalizeAngle(i2, 360)));
   }

   public int rotateVertex(EnumFacing facing, int vertexIndex) {
      int vertexIndex2 = vertexIndex;
      if (facing.getAxis() == net.minecraft.util.EnumFacing.Axis.X) {
         vertexIndex2 = (vertexIndex + this.quartersX) % 4;
      }

      EnumFacing facing2 = facing;

      for (int innerIndex = 0; innerIndex < this.quartersX; innerIndex++) {
         facing2 = facing2.rotateAround(net.minecraft.util.EnumFacing.Axis.X);
      }

      if (facing2.getAxis() == net.minecraft.util.EnumFacing.Axis.Y) {
         vertexIndex2 = (vertexIndex2 + this.quartersY) % 4;
      }

      return vertexIndex2;
   }

   @Override
   public EnumFacing rotate(EnumFacing arg1) {
      return this.rotateFace(arg1);
   }

   ModelRotation(int i, int i2) {
      this.combinedXY = combineXY(i, i2);
      this.matrix4d = new Matrix4f();
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.setIdentity();
      Matrix4f.rotate(-i * (float) (Math.PI / 180.0), new Vector3f(1.0F, 0.0F, 0.0F), matrix4f, matrix4f);
      this.quartersX = MathHelper.abs_int(i / 90);
      Matrix4f matrix4f2 = new Matrix4f();
      matrix4f2.setIdentity();
      Matrix4f.rotate(-i2 * (float) (Math.PI / 180.0), new Vector3f(0.0F, 1.0F, 0.0F), matrix4f2, matrix4f2);
      this.quartersY = MathHelper.abs_int(i2 / 90);
      Matrix4f.mul(matrix4f2, matrix4f, this.matrix4d);
   }

   static {
      for (ModelRotation modelRotation : values()) {
         mapRotations.put(modelRotation.combinedXY, modelRotation);
      }
   }
}
