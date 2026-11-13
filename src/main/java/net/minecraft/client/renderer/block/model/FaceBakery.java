package net.minecraft.client.renderer.block.model;

import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.src.Config;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3i;
import net.optifine.model.BlockModelUtils;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class FaceBakery {
   private static final float SCALE_ROTATION_22_5 = 1.0F / (float)Math.cos((float) (Math.PI / 8)) - 1.0F;
   private static final float SCALE_ROTATION_GENERAL = 1.0F / (float)Math.cos(Math.PI / 4) - 1.0F;

   public BakedQuad getBakedQuad(
      Vector3f vector3f, Vector3f vector3f2, BlockPartFace blockPartFace, TextureAtlasSprite textureAtlasSprite, EnumFacing enumFacing, ModelRotation modelRotation, BlockPartRotation blockPartRotation, boolean flag
   ) {
      return this.makeBakedQuad(vector3f, vector3f2, blockPartFace, textureAtlasSprite, enumFacing, modelRotation, blockPartRotation, false, flag);
   }

   private void rotatePart(Vector3f position, BlockPartRotation partRotation) {
      if (partRotation != null) {
         Matrix4f matrix4f = this.getMatrixIdentity();
         Vector3f vector3f = new Vector3f(0.0F, 0.0F, 0.0F);
         switch (partRotation.axis) {
            case X:
               Matrix4f.rotate(partRotation.angle * (float) (Math.PI / 180.0), new Vector3f(1.0F, 0.0F, 0.0F), matrix4f, matrix4f);
               vector3f.set(0.0F, 1.0F, 1.0F);
               break;
            case Y:
               Matrix4f.rotate(partRotation.angle * (float) (Math.PI / 180.0), new Vector3f(0.0F, 1.0F, 0.0F), matrix4f, matrix4f);
               vector3f.set(1.0F, 0.0F, 1.0F);
               break;
            case Z:
               Matrix4f.rotate(partRotation.angle * (float) (Math.PI / 180.0), new Vector3f(0.0F, 0.0F, 1.0F), matrix4f, matrix4f);
               vector3f.set(1.0F, 1.0F, 0.0F);
         }

         if (partRotation.rescale) {
            if (Math.abs(partRotation.angle) == 22.5F) {
               vector3f.scale(SCALE_ROTATION_22_5);
            } else {
               vector3f.scale(SCALE_ROTATION_GENERAL);
            }

            Vector3f.add(vector3f, new Vector3f(1.0F, 1.0F, 1.0F), vector3f);
         } else {
            vector3f.set(1.0F, 1.0F, 1.0F);
         }

         this.rotateScale(position, new Vector3f(partRotation.origin), matrix4f, vector3f);
      }
   }

   private float[] getPositionsDiv16(Vector3f pos1, Vector3f pos2) {
      float[] floatArray = new float[EnumFacing.values().length];
      floatArray[net.minecraft.client.renderer.EnumFaceDirection.Constants.WEST_INDEX] = pos1.x / 16.0F;
      floatArray[net.minecraft.client.renderer.EnumFaceDirection.Constants.DOWN_INDEX] = pos1.y / 16.0F;
      floatArray[net.minecraft.client.renderer.EnumFaceDirection.Constants.NORTH_INDEX] = pos1.z / 16.0F;
      floatArray[net.minecraft.client.renderer.EnumFaceDirection.Constants.EAST_INDEX] = pos2.x / 16.0F;
      floatArray[net.minecraft.client.renderer.EnumFaceDirection.Constants.UP_INDEX] = pos2.y / 16.0F;
      floatArray[net.minecraft.client.renderer.EnumFaceDirection.Constants.SOUTH_INDEX] = pos2.z / 16.0F;
      return floatArray;
   }

   private Matrix4f getMatrixIdentity() {
      Matrix4f matrix4f2 = new Matrix4f();
      matrix4f2.setIdentity();
      return matrix4f2;
   }

   private void rotateScale(Vector3f position, Vector3f rotationOrigin, Matrix4f rotationMatrix, Vector3f scale) {
      Vector4f vector4f = new Vector4f(position.x - rotationOrigin.x, position.y - rotationOrigin.y, position.z - rotationOrigin.z, 1.0F);
      Matrix4f.transform(rotationMatrix, vector4f, vector4f);
      vector4f.x = vector4f.x * scale.x;
      vector4f.y = vector4f.y * scale.y;
      vector4f.z = vector4f.z * scale.z;
      position.set(vector4f.x + rotationOrigin.x, vector4f.y + rotationOrigin.y, vector4f.z + rotationOrigin.z);
   }

   public void lockUv(int[] faceData, EnumFacing facing, BlockFaceUV faceUV, TextureAtlasSprite sprite) {
      for (int index = 0; index < 4; index++) {
         this.lockVertexUv(index, faceData, facing, faceUV, sprite);
      }
   }

   private void applyFacing(int[] faceData, EnumFacing facing) {
      int[] intArray = new int[faceData.length];
      System.arraycopy(faceData, 0, intArray, 0, faceData.length);
      float[] floatArray = new float[EnumFacing.values().length];
      floatArray[net.minecraft.client.renderer.EnumFaceDirection.Constants.WEST_INDEX] = 999.0F;
      floatArray[net.minecraft.client.renderer.EnumFaceDirection.Constants.DOWN_INDEX] = 999.0F;
      floatArray[net.minecraft.client.renderer.EnumFaceDirection.Constants.NORTH_INDEX] = 999.0F;
      floatArray[net.minecraft.client.renderer.EnumFaceDirection.Constants.EAST_INDEX] = -999.0F;
      floatArray[net.minecraft.client.renderer.EnumFaceDirection.Constants.UP_INDEX] = -999.0F;
      floatArray[net.minecraft.client.renderer.EnumFaceDirection.Constants.SOUTH_INDEX] = -999.0F;
      int count = faceData.length / 4;

      for (int innerIndex = 0; innerIndex < 4; innerIndex++) {
         int i = count * innerIndex;
         float f = Float.intBitsToFloat(intArray[i]);
         float f2 = Float.intBitsToFloat(intArray[i + 1]);
         float f3 = Float.intBitsToFloat(intArray[i + 2]);
         if (f < floatArray[net.minecraft.client.renderer.EnumFaceDirection.Constants.WEST_INDEX]) {
            floatArray[net.minecraft.client.renderer.EnumFaceDirection.Constants.WEST_INDEX] = f;
         }

         if (f2 < floatArray[net.minecraft.client.renderer.EnumFaceDirection.Constants.DOWN_INDEX]) {
            floatArray[net.minecraft.client.renderer.EnumFaceDirection.Constants.DOWN_INDEX] = f2;
         }

         if (f3 < floatArray[net.minecraft.client.renderer.EnumFaceDirection.Constants.NORTH_INDEX]) {
            floatArray[net.minecraft.client.renderer.EnumFaceDirection.Constants.NORTH_INDEX] = f3;
         }

         if (f > floatArray[net.minecraft.client.renderer.EnumFaceDirection.Constants.EAST_INDEX]) {
            floatArray[net.minecraft.client.renderer.EnumFaceDirection.Constants.EAST_INDEX] = f;
         }

         if (f2 > floatArray[net.minecraft.client.renderer.EnumFaceDirection.Constants.UP_INDEX]) {
            floatArray[net.minecraft.client.renderer.EnumFaceDirection.Constants.UP_INDEX] = f2;
         }

         if (f3 > floatArray[net.minecraft.client.renderer.EnumFaceDirection.Constants.SOUTH_INDEX]) {
            floatArray[net.minecraft.client.renderer.EnumFaceDirection.Constants.SOUTH_INDEX] = f3;
         }
      }

      EnumFaceDirection faceDirection = EnumFaceDirection.getFacing(facing);

      for (int innerIndex2 = 0; innerIndex2 < 4; innerIndex2++) {
         int i2 = count * innerIndex2;
         net.minecraft.client.renderer.EnumFaceDirection.VertexInformation enumfacedirection$vertexinformation = faceDirection.getVertexInformation(innerIndex2);
         float f4 = floatArray[enumfacedirection$vertexinformation.xIndex];
         float f5 = floatArray[enumfacedirection$vertexinformation.yIndex];
         float f6 = floatArray[enumfacedirection$vertexinformation.zIndex];
         faceData[i2] = Float.floatToRawIntBits(f4);
         faceData[i2 + 1] = Float.floatToRawIntBits(f5);
         faceData[i2 + 2] = Float.floatToRawIntBits(f6);

         for (int outerIndex = 0; outerIndex < 4; outerIndex++) {
            int i3 = count * outerIndex;
            float f7 = Float.intBitsToFloat(intArray[i3]);
            float f8 = Float.intBitsToFloat(intArray[i3 + 1]);
            float f9 = Float.intBitsToFloat(intArray[i3 + 2]);
            if (MathHelper.epsilonEquals(f4, f7)
               && MathHelper.epsilonEquals(f5, f8)
               && MathHelper.epsilonEquals(f6, f9)) {
               faceData[i2 + 4] = intArray[i3 + 4];
               faceData[i2 + 4 + 1] = intArray[i3 + 4 + 1];
            }
         }
      }
   }

   public static float getFaceBrightness(EnumFacing facing) {
      switch (facing) {
         case DOWN:
            if (Config.isShaders()) {
               return Shaders.blockLightLevel05;
            }

            return 0.5F;
         case UP:
            return 1.0F;
         case NORTH:
         case SOUTH:
            if (Config.isShaders()) {
               return Shaders.blockLightLevel08;
            }

            return 0.8F;
         case WEST:
         case EAST:
            if (Config.isShaders()) {
               return Shaders.blockLightLevel06;
            }

            return 0.6F;
         default:
            return 1.0F;
      }
   }

   public static EnumFacing getFacingFromVertexData(int[] faceData) {
      int i = faceData.length / 4;
      int i2 = i * 2;
      Vector3f vector3f = new Vector3f(Float.intBitsToFloat(faceData[0]), Float.intBitsToFloat(faceData[1]), Float.intBitsToFloat(faceData[2]));
      Vector3f vector3f2 = new Vector3f(Float.intBitsToFloat(faceData[i]), Float.intBitsToFloat(faceData[i + 1]), Float.intBitsToFloat(faceData[i + 2]));
      Vector3f vector3f3 = new Vector3f(Float.intBitsToFloat(faceData[i2]), Float.intBitsToFloat(faceData[i2 + 1]), Float.intBitsToFloat(faceData[i2 + 2]));
      Vector3f vector3f4 = new Vector3f();
      Vector3f vector3f5 = new Vector3f();
      Vector3f vector3f6 = new Vector3f();
      Vector3f.sub(vector3f, vector3f2, vector3f4);
      Vector3f.sub(vector3f3, vector3f2, vector3f5);
      Vector3f.cross(vector3f5, vector3f4, vector3f6);
      float f = (float)Math.sqrt(vector3f6.x * vector3f6.x + vector3f6.y * vector3f6.y + vector3f6.z * vector3f6.z);
      vector3f6.x /= f;
      vector3f6.y /= f;
      vector3f6.z /= f;
      EnumFacing enumFacing = null;
      float f2 = 0.0F;

      for (EnumFacing enumFacing2 : EnumFacing.values()) {
         Vec3i directionVec = enumFacing2.getDirectionVec();
         Vector3f vector3f7 = new Vector3f(directionVec.getX(), directionVec.getY(), directionVec.getZ());
         float f3 = Vector3f.dot(vector3f6, vector3f7);
         if (f3 >= 0.0F && f3 > f2) {
            f2 = f3;
            enumFacing = enumFacing2;
         }
      }

      return enumFacing == null ? EnumFacing.UP : enumFacing;
   }

   public BakedQuad makeBakedQuad(
      Vector3f posFrom,
      Vector3f posTo,
      BlockPartFace face,
      TextureAtlasSprite sprite,
      EnumFacing facing,
      ITransformation transform,
      BlockPartRotation partRotation,
      boolean uvLocked,
      boolean shade
   ) {
      int[] intArray = this.getArray(face, sprite, facing, this.getPositionsDiv16(posFrom, posTo), transform, partRotation, shade);
      EnumFacing facing2 = getFacingFromVertexData(intArray);
      if (uvLocked) {
         this.lockUv(intArray, facing2, face.blockFaceUV, sprite);
      }

      if (partRotation == null) {
         this.applyFacing(intArray, facing2);
      }

      if (Reflector.reflectorMethod55.isActive()) {
         Reflector.createObject2(Reflector.reflectorMethod55, intArray, facing2);
      }

      return new BakedQuad(intArray, face.tintIndex, facing2);
   }

   private int getFaceShadeColor(EnumFacing facing) {
      float faceBrightness = getFaceBrightness(facing);
      int i = MathHelper.clamp_int((int)(faceBrightness * 255.0F), 0, 255);
      return 0xFF000000 | i << 16 | i << 8 | i;
   }

   private void storeVertexData(
      int[] faceData, int storeIndex, int vertexIndex, Vector3f position, int shadeColor, TextureAtlasSprite sprite, BlockFaceUV faceUV
   ) {
      int count = faceData.length / 4;
      int i = storeIndex * count;
      faceData[i] = Float.floatToRawIntBits(position.x);
      faceData[i + 1] = Float.floatToRawIntBits(position.y);
      faceData[i + 2] = Float.floatToRawIntBits(position.z);
      faceData[i + 3] = shadeColor;
      faceData[i + 4] = Float.floatToRawIntBits(
         sprite.getInterpolatedU(faceUV.getU(vertexIndex) * 0.999 + faceUV.getU((vertexIndex + 2) % 4) * 0.001)
      );
      faceData[i + 4 + 1] = Float.floatToRawIntBits(
         sprite.getInterpolatedV(faceUV.getV(vertexIndex) * 0.999 + faceUV.getV((vertexIndex + 2) % 4) * 0.001)
      );
   }

   public int getInt(Vector3f vector3f, EnumFacing enumFacing, int i, ITransformation iTransformation) {
      if (iTransformation == ModelRotation.X0_Y0) {
         return i;
      }

      if (Reflector.reflectorMethod74.isActive()) {
         Reflector.createObject2(Reflector.reflectorMethod74, vector3f, iTransformation.getMatrix4f());
      } else {
         this.rotateScale(vector3f, new Vector3f(0.5F, 0.5F, 0.5F), ((ModelRotation)iTransformation).getMatrix4d(), new Vector3f(1.0F, 1.0F, 1.0F));
      }

      return iTransformation.rotate(enumFacing, i);
   }

   private void handleArray(
      int[] iArray,
      int i,
      EnumFacing enumFacing,
      BlockPartFace blockPartFace,
      float[] fArray,
      TextureAtlasSprite textureAtlasSprite,
      ITransformation iTransformation,
      BlockPartRotation blockPartRotation,
      boolean flag
   ) {
      EnumFacing enumFacing2 = iTransformation.rotate(enumFacing);
      int i2 = flag ? this.getFaceShadeColor(enumFacing2) : -1;
      net.minecraft.client.renderer.EnumFaceDirection.VertexInformation vertexInformation = EnumFaceDirection.getFacing(enumFacing).getVertexInformation(i);
      Vector3f vector3f = new Vector3f(fArray[vertexInformation.xIndex], fArray[vertexInformation.yIndex], fArray[vertexInformation.zIndex]);
      this.rotatePart(vector3f, blockPartRotation);
      int intValue = this.getInt(vector3f, enumFacing, i, iTransformation);
      BlockModelUtils.snapVertexPosition(vector3f);
      this.storeVertexData(iArray, intValue, i, vector3f, i2, textureAtlasSprite, blockPartFace.blockFaceUV);
   }

   private int[] getArray(
      BlockPartFace blockPartFace, TextureAtlasSprite textureAtlasSprite, EnumFacing enumFacing, float[] fArray, ITransformation iTransformation, BlockPartRotation blockPartRotation, boolean flag
   ) {
      byte b = 28;
      if (Config.isShaders()) {
         b = 56;
      }

      int[] iArray = new int[b];

      for (int i = 0; i < 4; i++) {
         this.handleArray(iArray, i, enumFacing, blockPartFace, fArray, textureAtlasSprite, iTransformation, blockPartRotation, flag);
      }

      return iArray;
   }

   private void lockVertexUv(int vertexIndex, int[] faceData, EnumFacing facing, BlockFaceUV faceUV, TextureAtlasSprite sprite) {
      int count = faceData.length / 4;
      int i = count * vertexIndex;
      float f = Float.intBitsToFloat(faceData[i]);
      float f2 = Float.intBitsToFloat(faceData[i + 1]);
      float f3 = Float.intBitsToFloat(faceData[i + 2]);
      if (f < -0.1F || f >= 1.1F) {
         f -= MathHelper.floor_float(f);
      }

      if (f2 < -0.1F || f2 >= 1.1F) {
         f2 -= MathHelper.floor_float(f2);
      }

      if (f3 < -0.1F || f3 >= 1.1F) {
         f3 -= MathHelper.floor_float(f3);
      }

      float f4 = 0.0F;
      float f5 = 0.0F;
      switch (facing) {
         case DOWN:
            f4 = f * 16.0F;
            f5 = (1.0F - f3) * 16.0F;
            break;
         case UP:
            f4 = f * 16.0F;
            f5 = f3 * 16.0F;
            break;
         case NORTH:
            f4 = (1.0F - f) * 16.0F;
            f5 = (1.0F - f2) * 16.0F;
            break;
         case SOUTH:
            f4 = f * 16.0F;
            f5 = (1.0F - f2) * 16.0F;
            break;
         case WEST:
            f4 = f3 * 16.0F;
            f5 = (1.0F - f2) * 16.0F;
            break;
         case EAST:
            f4 = (1.0F - f3) * 16.0F;
            f5 = (1.0F - f2) * 16.0F;
      }

      int i2 = faceUV.getVertexRotatedReverse(vertexIndex) * count;
      faceData[i2 + 4] = Float.floatToRawIntBits(sprite.getInterpolatedU(f4));
      faceData[i2 + 4 + 1] = Float.floatToRawIntBits(sprite.getInterpolatedV(f5));
   }
}
