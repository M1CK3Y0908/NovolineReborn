package net.optifine.shaders;

import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.util.MathHelper;

public class ClippingHelperShadow extends ClippingHelper {
   int shadowClipPlaneCount;
   float[] frustumTest = new float[6];
   private static ClippingHelperShadow instance = new ClippingHelperShadow();
   float[][] shadowClipPlanes = new float[10][4];
   float[] matInvMP = new float[16];
   float[] vecIntersection = new float[4];

   private double dot3(float[] vecA, float[] vecB) {
      return (double)vecA[0] * vecB[0] + (double)vecA[1] * vecB[1] + (double)vecA[2] * vecB[2];
   }

   private void addShadowClipPlane(float[] plane) {
      this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], plane);
   }

   public void init() {
      float[] floatArray = this.projectionMatrix;
      float[] floatArray1 = this.modelviewMatrix;
      float[] floatArray2 = this.clippingMatrix;
      System.arraycopy(Shaders.faProjection, 0, floatArray, 0, 16);
      System.arraycopy(Shaders.faModelView, 0, floatArray1, 0, 16);
      SMath.multiplyMat4xMat4(floatArray2, floatArray1, floatArray);
      this.assignPlane(
         this.frustum[0], floatArray2[3] - floatArray2[0], floatArray2[7] - floatArray2[4], floatArray2[11] - floatArray2[8], floatArray2[15] - floatArray2[12]
      );
      this.assignPlane(
         this.frustum[1], floatArray2[3] + floatArray2[0], floatArray2[7] + floatArray2[4], floatArray2[11] + floatArray2[8], floatArray2[15] + floatArray2[12]
      );
      this.assignPlane(
         this.frustum[2], floatArray2[3] + floatArray2[1], floatArray2[7] + floatArray2[5], floatArray2[11] + floatArray2[9], floatArray2[15] + floatArray2[13]
      );
      this.assignPlane(
         this.frustum[3], floatArray2[3] - floatArray2[1], floatArray2[7] - floatArray2[5], floatArray2[11] - floatArray2[9], floatArray2[15] - floatArray2[13]
      );
      this.assignPlane(
         this.frustum[4],
         floatArray2[3] - floatArray2[2],
         floatArray2[7] - floatArray2[6],
         floatArray2[11] - floatArray2[10],
         floatArray2[15] - floatArray2[14]
      );
      this.assignPlane(
         this.frustum[5],
         floatArray2[3] + floatArray2[2],
         floatArray2[7] + floatArray2[6],
         floatArray2[11] + floatArray2[10],
         floatArray2[15] + floatArray2[14]
      );
      float[] floatArray3 = Shaders.shadowLightPositionVector;
      float f = (float)this.dot3(this.frustum[0], floatArray3);
      float f2 = (float)this.dot3(this.frustum[1], floatArray3);
      float f3 = (float)this.dot3(this.frustum[2], floatArray3);
      float f4 = (float)this.dot3(this.frustum[3], floatArray3);
      float f5 = (float)this.dot3(this.frustum[4], floatArray3);
      float f6 = (float)this.dot3(this.frustum[5], floatArray3);
      this.shadowClipPlaneCount = 0;
      if (f >= 0.0F) {
         this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[0]);
         if (f > 0.0F) {
            if (f3 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[0], this.frustum[2], floatArray3);
            }

            if (f4 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[0], this.frustum[3], floatArray3);
            }

            if (f5 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[0], this.frustum[4], floatArray3);
            }

            if (f6 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[0], this.frustum[5], floatArray3);
            }
         }
      }

      if (f2 >= 0.0F) {
         this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[1]);
         if (f2 > 0.0F) {
            if (f3 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[1], this.frustum[2], floatArray3);
            }

            if (f4 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[1], this.frustum[3], floatArray3);
            }

            if (f5 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[1], this.frustum[4], floatArray3);
            }

            if (f6 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[1], this.frustum[5], floatArray3);
            }
         }
      }

      if (f3 >= 0.0F) {
         this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[2]);
         if (f3 > 0.0F) {
            if (f < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[2], this.frustum[0], floatArray3);
            }

            if (f2 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[2], this.frustum[1], floatArray3);
            }

            if (f5 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[2], this.frustum[4], floatArray3);
            }

            if (f6 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[2], this.frustum[5], floatArray3);
            }
         }
      }

      if (f4 >= 0.0F) {
         this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[3]);
         if (f4 > 0.0F) {
            if (f < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[3], this.frustum[0], floatArray3);
            }

            if (f2 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[3], this.frustum[1], floatArray3);
            }

            if (f5 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[3], this.frustum[4], floatArray3);
            }

            if (f6 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[3], this.frustum[5], floatArray3);
            }
         }
      }

      if (f5 >= 0.0F) {
         this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[4]);
         if (f5 > 0.0F) {
            if (f < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[4], this.frustum[0], floatArray3);
            }

            if (f2 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[4], this.frustum[1], floatArray3);
            }

            if (f3 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[4], this.frustum[2], floatArray3);
            }

            if (f4 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[4], this.frustum[3], floatArray3);
            }
         }
      }

      if (f6 >= 0.0F) {
         this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[5]);
         if (f6 > 0.0F) {
            if (f < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[5], this.frustum[0], floatArray3);
            }

            if (f2 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[5], this.frustum[1], floatArray3);
            }

            if (f3 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[5], this.frustum[2], floatArray3);
            }

            if (f4 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[5], this.frustum[3], floatArray3);
            }
         }
      }
   }

   public static ClippingHelper getInstance() {
      instance.init();
      return instance;
   }

   private void normalizePlane(float[] plane) {
      float f = MathHelper.sqrt_float(plane[0] * plane[0] + plane[1] * plane[1] + plane[2] * plane[2]);
      plane[0] /= f;
      plane[1] /= f;
      plane[2] /= f;
      plane[3] /= f;
   }

   private void assignPlane(float[] plane, float a, float b, float c, float f) {
      float f2 = (float)Math.sqrt(a * a + b * b + c * c);
      plane[0] = a / f2;
      plane[1] = b / f2;
      plane[2] = c / f2;
      plane[3] = f / f2;
   }

   private double dot4(float[] plane, double x, double y, double z) {
      return plane[0] * x + plane[1] * y + plane[2] * z + plane[3];
   }

   @Override
   public boolean isBoxInFrustum(double x1, double y1, double z1, double x2, double y2, double z2) {
      for (int index = 0; index < this.shadowClipPlaneCount; index++) {
         float[] fArray = this.shadowClipPlanes[index];
         if (this.dot4(fArray, x1, y1, z1) <= 0.0
            && this.dot4(fArray, x2, y1, z1) <= 0.0
            && this.dot4(fArray, x1, y2, z1) <= 0.0
            && this.dot4(fArray, x2, y2, z1) <= 0.0
            && this.dot4(fArray, x1, y1, z2) <= 0.0
            && this.dot4(fArray, x2, y1, z2) <= 0.0
            && this.dot4(fArray, x1, y2, z2) <= 0.0
            && this.dot4(fArray, x2, y2, z2) <= 0.0) {
            return false;
         }
      }

      return true;
   }

   private void normalize3(float[] plane) {
      float f = MathHelper.sqrt_float(plane[0] * plane[0] + plane[1] * plane[1] + plane[2] * plane[2]);
      if (f == 0.0F) {
         f = 1.0F;
      }

      plane[0] /= f;
      plane[1] /= f;
      plane[2] /= f;
   }

   private float length(float x, float y, float z) {
      return (float)Math.sqrt(x * x + y * y + z * z);
   }

   private float distance(float x1, float y1, float z1, float x2, float y2, float z2) {
      return this.length(x1 - x2, y1 - y2, z1 - z2);
   }

   private void copyPlane(float[] dst, float[] src) {
      dst[0] = src[0];
      dst[1] = src[1];
      dst[2] = src[2];
      dst[3] = src[3];
   }

   private void makeShadowPlane(float[] shadowPlane, float[] positivePlane, float[] negativePlane, float[] vecSun) {
      this.cross3(this.vecIntersection, positivePlane, negativePlane);
      this.cross3(shadowPlane, this.vecIntersection, vecSun);
      this.normalize3(shadowPlane);
      float f = (float)this.dot3(positivePlane, negativePlane);
      float f2 = (float)this.dot3(shadowPlane, negativePlane);
      float distance2 = this.distance(
         shadowPlane[0], shadowPlane[1], shadowPlane[2], negativePlane[0] * f2, negativePlane[1] * f2, negativePlane[2] * f2
      );
      float distance3 = this.distance(
         positivePlane[0], positivePlane[1], positivePlane[2], negativePlane[0] * f, negativePlane[1] * f, negativePlane[2] * f
      );
      float f3 = distance2 / distance3;
      float f4 = (float)this.dot3(shadowPlane, positivePlane);
      float distance4 = this.distance(
         shadowPlane[0], shadowPlane[1], shadowPlane[2], positivePlane[0] * f4, positivePlane[1] * f4, positivePlane[2] * f4
      );
      float distance5 = this.distance(
         negativePlane[0], negativePlane[1], negativePlane[2], positivePlane[0] * f, positivePlane[1] * f, positivePlane[2] * f
      );
      float f5 = distance4 / distance5;
      shadowPlane[3] = positivePlane[3] * f3 + negativePlane[3] * f5;
   }

   private void cross3(float[] out, float[] a, float[] b) {
      out[0] = a[1] * b[2] - a[2] * b[1];
      out[1] = a[2] * b[0] - a[0] * b[2];
      out[2] = a[0] * b[1] - a[1] * b[0];
   }
}
