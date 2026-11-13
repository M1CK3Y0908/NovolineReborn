package net.minecraft.client.renderer.culling;

import java.nio.FloatBuffer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

public class ClippingHelperImpl extends ClippingHelper {
   private FloatBuffer projectionMatrixBuffer = GLAllocation.createDirectFloatBuffer(16);
   private FloatBuffer modelviewMatrixBuffer = GLAllocation.createDirectFloatBuffer(16);
   private static ClippingHelperImpl instance = new ClippingHelperImpl();
   private FloatBuffer scratchBuffer = GLAllocation.createDirectFloatBuffer(16);

   public void init() {
      this.projectionMatrixBuffer.clear();
      this.modelviewMatrixBuffer.clear();
      this.scratchBuffer.clear();
      GlStateManager.getFloat(2983, this.projectionMatrixBuffer);
      GlStateManager.getFloat(2982, this.modelviewMatrixBuffer);
      float[] floatArray = this.projectionMatrix;
      float[] floatArray1 = this.modelviewMatrix;
      this.projectionMatrixBuffer.flip().limit(16);
      this.projectionMatrixBuffer.get(floatArray);
      this.modelviewMatrixBuffer.flip().limit(16);
      this.modelviewMatrixBuffer.get(floatArray1);
      this.clippingMatrix[0] = floatArray1[0] * floatArray[0]
         + floatArray1[1] * floatArray[4]
         + floatArray1[2] * floatArray[8]
         + floatArray1[3] * floatArray[12];
      this.clippingMatrix[1] = floatArray1[0] * floatArray[1]
         + floatArray1[1] * floatArray[5]
         + floatArray1[2] * floatArray[9]
         + floatArray1[3] * floatArray[13];
      this.clippingMatrix[2] = floatArray1[0] * floatArray[2]
         + floatArray1[1] * floatArray[6]
         + floatArray1[2] * floatArray[10]
         + floatArray1[3] * floatArray[14];
      this.clippingMatrix[3] = floatArray1[0] * floatArray[3]
         + floatArray1[1] * floatArray[7]
         + floatArray1[2] * floatArray[11]
         + floatArray1[3] * floatArray[15];
      this.clippingMatrix[4] = floatArray1[4] * floatArray[0]
         + floatArray1[5] * floatArray[4]
         + floatArray1[6] * floatArray[8]
         + floatArray1[7] * floatArray[12];
      this.clippingMatrix[5] = floatArray1[4] * floatArray[1]
         + floatArray1[5] * floatArray[5]
         + floatArray1[6] * floatArray[9]
         + floatArray1[7] * floatArray[13];
      this.clippingMatrix[6] = floatArray1[4] * floatArray[2]
         + floatArray1[5] * floatArray[6]
         + floatArray1[6] * floatArray[10]
         + floatArray1[7] * floatArray[14];
      this.clippingMatrix[7] = floatArray1[4] * floatArray[3]
         + floatArray1[5] * floatArray[7]
         + floatArray1[6] * floatArray[11]
         + floatArray1[7] * floatArray[15];
      this.clippingMatrix[8] = floatArray1[8] * floatArray[0]
         + floatArray1[9] * floatArray[4]
         + floatArray1[10] * floatArray[8]
         + floatArray1[11] * floatArray[12];
      this.clippingMatrix[9] = floatArray1[8] * floatArray[1]
         + floatArray1[9] * floatArray[5]
         + floatArray1[10] * floatArray[9]
         + floatArray1[11] * floatArray[13];
      this.clippingMatrix[10] = floatArray1[8] * floatArray[2]
         + floatArray1[9] * floatArray[6]
         + floatArray1[10] * floatArray[10]
         + floatArray1[11] * floatArray[14];
      this.clippingMatrix[11] = floatArray1[8] * floatArray[3]
         + floatArray1[9] * floatArray[7]
         + floatArray1[10] * floatArray[11]
         + floatArray1[11] * floatArray[15];
      this.clippingMatrix[12] = floatArray1[12] * floatArray[0]
         + floatArray1[13] * floatArray[4]
         + floatArray1[14] * floatArray[8]
         + floatArray1[15] * floatArray[12];
      this.clippingMatrix[13] = floatArray1[12] * floatArray[1]
         + floatArray1[13] * floatArray[5]
         + floatArray1[14] * floatArray[9]
         + floatArray1[15] * floatArray[13];
      this.clippingMatrix[14] = floatArray1[12] * floatArray[2]
         + floatArray1[13] * floatArray[6]
         + floatArray1[14] * floatArray[10]
         + floatArray1[15] * floatArray[14];
      this.clippingMatrix[15] = floatArray1[12] * floatArray[3]
         + floatArray1[13] * floatArray[7]
         + floatArray1[14] * floatArray[11]
         + floatArray1[15] * floatArray[15];
      float[] fArray = this.frustum[0];
      fArray[0] = this.clippingMatrix[3] - this.clippingMatrix[0];
      fArray[1] = this.clippingMatrix[7] - this.clippingMatrix[4];
      fArray[2] = this.clippingMatrix[11] - this.clippingMatrix[8];
      fArray[3] = this.clippingMatrix[15] - this.clippingMatrix[12];
      this.normalize(fArray);
      float[] fArray2 = this.frustum[1];
      fArray2[0] = this.clippingMatrix[3] + this.clippingMatrix[0];
      fArray2[1] = this.clippingMatrix[7] + this.clippingMatrix[4];
      fArray2[2] = this.clippingMatrix[11] + this.clippingMatrix[8];
      fArray2[3] = this.clippingMatrix[15] + this.clippingMatrix[12];
      this.normalize(fArray2);
      float[] fArray3 = this.frustum[2];
      fArray3[0] = this.clippingMatrix[3] + this.clippingMatrix[1];
      fArray3[1] = this.clippingMatrix[7] + this.clippingMatrix[5];
      fArray3[2] = this.clippingMatrix[11] + this.clippingMatrix[9];
      fArray3[3] = this.clippingMatrix[15] + this.clippingMatrix[13];
      this.normalize(fArray3);
      float[] fArray4 = this.frustum[3];
      fArray4[0] = this.clippingMatrix[3] - this.clippingMatrix[1];
      fArray4[1] = this.clippingMatrix[7] - this.clippingMatrix[5];
      fArray4[2] = this.clippingMatrix[11] - this.clippingMatrix[9];
      fArray4[3] = this.clippingMatrix[15] - this.clippingMatrix[13];
      this.normalize(fArray4);
      float[] fArray5 = this.frustum[4];
      fArray5[0] = this.clippingMatrix[3] - this.clippingMatrix[2];
      fArray5[1] = this.clippingMatrix[7] - this.clippingMatrix[6];
      fArray5[2] = this.clippingMatrix[11] - this.clippingMatrix[10];
      fArray5[3] = this.clippingMatrix[15] - this.clippingMatrix[14];
      this.normalize(fArray5);
      float[] fArray6 = this.frustum[5];
      fArray6[0] = this.clippingMatrix[3] + this.clippingMatrix[2];
      fArray6[1] = this.clippingMatrix[7] + this.clippingMatrix[6];
      fArray6[2] = this.clippingMatrix[11] + this.clippingMatrix[10];
      fArray6[3] = this.clippingMatrix[15] + this.clippingMatrix[14];
      this.normalize(fArray6);
   }

   private void normalize(float[] fArray) {
      float f = MathHelper.sqrt_float(fArray[0] * fArray[0] + fArray[1] * fArray[1] + fArray[2] * fArray[2]);
      fArray[0] /= f;
      fArray[1] /= f;
      fArray[2] /= f;
      fArray[3] /= f;
   }

   public static ClippingHelper getInstance() {
      instance.init();
      return instance;
   }
}
