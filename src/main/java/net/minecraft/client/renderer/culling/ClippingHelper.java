package net.minecraft.client.renderer.culling;

public class ClippingHelper {
   public float[][] frustum = new float[6][4];
   public float[] projectionMatrix = new float[16];
   public float[] modelviewMatrix = new float[16];
   public float[] clippingMatrix = new float[16];
   public boolean disabled = false;

   public boolean isBoxInFrustum(double d, double d2, double d3, double d4, double d5, double d6) {
      if (this.disabled) {
         return true;
      }

      float f = (float)d;
      float f2 = (float)d2;
      float f3 = (float)d3;
      float f4 = (float)d4;
      float f5 = (float)d5;
      float f6 = (float)d6;

      for (int index = 0; index < 6; index++) {
         float[] fArray = this.frustum[index];
         float f7 = fArray[0];
         float f8 = fArray[1];
         float f9 = fArray[2];
         float f10 = fArray[3];
         if (f7 * f + f8 * f2 + f9 * f3 + f10 <= 0.0F
            && f7 * f4 + f8 * f2 + f9 * f3 + f10 <= 0.0F
            && f7 * f + f8 * f5 + f9 * f3 + f10 <= 0.0F
            && f7 * f4 + f8 * f5 + f9 * f3 + f10 <= 0.0F
            && f7 * f + f8 * f2 + f9 * f6 + f10 <= 0.0F
            && f7 * f4 + f8 * f2 + f9 * f6 + f10 <= 0.0F
            && f7 * f + f8 * f5 + f9 * f6 + f10 <= 0.0F
            && f7 * f4 + f8 * f5 + f9 * f6 + f10 <= 0.0F) {
            return false;
         }
      }

      return true;
   }

   private float dot(float[] arg1, float arg2, float arg3, float arg4) {
      return arg1[0] * arg2 + arg1[1] * arg3 + arg1[2] * arg4 + arg1[3];
   }

   public boolean isBoxInFrustumFully(double arg1, double arg3, double arg5, double arg7, double arg9, double arg11) {
      if (this.disabled) {
         return true;
      }

      float f = (float)arg1;
      float f2 = (float)arg3;
      float f3 = (float)arg5;
      float f4 = (float)arg7;
      float f5 = (float)arg9;
      float f6 = (float)arg11;

      for (int index = 0; index < 6; index++) {
         float[] fArray = this.frustum[index];
         float f7 = fArray[0];
         float f8 = fArray[1];
         float f9 = fArray[2];
         float f10 = fArray[3];
         if (index < 4) {
            if (f7 * f + f8 * f2 + f9 * f3 + f10 <= 0.0F
               || f7 * f4 + f8 * f2 + f9 * f3 + f10 <= 0.0F
               || f7 * f + f8 * f5 + f9 * f3 + f10 <= 0.0F
               || f7 * f4 + f8 * f5 + f9 * f3 + f10 <= 0.0F
               || f7 * f + f8 * f2 + f9 * f6 + f10 <= 0.0F
               || f7 * f4 + f8 * f2 + f9 * f6 + f10 <= 0.0F
               || f7 * f + f8 * f5 + f9 * f6 + f10 <= 0.0F
               || f7 * f4 + f8 * f5 + f9 * f6 + f10 <= 0.0F) {
               return false;
            }
         } else if (f7 * f + f8 * f2 + f9 * f3 + f10 <= 0.0F
            && f7 * f4 + f8 * f2 + f9 * f3 + f10 <= 0.0F
            && f7 * f + f8 * f5 + f9 * f3 + f10 <= 0.0F
            && f7 * f4 + f8 * f5 + f9 * f3 + f10 <= 0.0F
            && f7 * f + f8 * f2 + f9 * f6 + f10 <= 0.0F
            && f7 * f4 + f8 * f2 + f9 * f6 + f10 <= 0.0F
            && f7 * f + f8 * f5 + f9 * f6 + f10 <= 0.0F
            && f7 * f4 + f8 * f5 + f9 * f6 + f10 <= 0.0F) {
            return false;
         }
      }

      return true;
   }
}
