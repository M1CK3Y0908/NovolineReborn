package net.optifine.shaders;

import java.nio.FloatBuffer;
import java.util.Arrays;

public class SMath {
   static void invertMat4FBFA(FloatBuffer fbInvOut, FloatBuffer fbMatIn, float[] faInv, float[] faMat) {
      fbMatIn.get(faMat);
      invertMat4(faInv, faMat);
      fbInvOut.put(faInv);
   }

   static void multiplyMat4xVec4(float[] vecOut, float[] matA, float[] vecB) {
      vecOut[0] = matA[0] * vecB[0] + matA[4] * vecB[1] + matA[8] * vecB[2] + matA[12] * vecB[3];
      vecOut[1] = matA[1] * vecB[0] + matA[5] * vecB[1] + matA[9] * vecB[2] + matA[13] * vecB[3];
      vecOut[2] = matA[2] * vecB[0] + matA[6] * vecB[1] + matA[10] * vecB[2] + matA[14] * vecB[3];
      vecOut[3] = matA[3] * vecB[0] + matA[7] * vecB[1] + matA[11] * vecB[2] + matA[15] * vecB[3];
   }

   static void multiplyMat4xMat4(float[] matOut, float[] matA, float[] matB) {
      for (int index = 0; index < 4; index++) {
         for (int innerIndex = 0; innerIndex < 4; innerIndex++) {
            matOut[4 * index + innerIndex] = matA[4 * index + 0] * matB[0 + innerIndex]
               + matA[4 * index + 1] * matB[4 + innerIndex]
               + matA[4 * index + 2] * matB[8 + innerIndex]
               + matA[4 * index + 3] * matB[12 + innerIndex];
         }
      }
   }

   static void invertMat4(float[] matOut, float[] floatArray) {
      matOut[0] = floatArray[5] * floatArray[10] * floatArray[15]
         - floatArray[5] * floatArray[11] * floatArray[14]
         - floatArray[9] * floatArray[6] * floatArray[15]
         + floatArray[9] * floatArray[7] * floatArray[14]
         + floatArray[13] * floatArray[6] * floatArray[11]
         - floatArray[13] * floatArray[7] * floatArray[10];
      matOut[1] = -floatArray[1] * floatArray[10] * floatArray[15]
         + floatArray[1] * floatArray[11] * floatArray[14]
         + floatArray[9] * floatArray[2] * floatArray[15]
         - floatArray[9] * floatArray[3] * floatArray[14]
         - floatArray[13] * floatArray[2] * floatArray[11]
         + floatArray[13] * floatArray[3] * floatArray[10];
      matOut[2] = floatArray[1] * floatArray[6] * floatArray[15]
         - floatArray[1] * floatArray[7] * floatArray[14]
         - floatArray[5] * floatArray[2] * floatArray[15]
         + floatArray[5] * floatArray[3] * floatArray[14]
         + floatArray[13] * floatArray[2] * floatArray[7]
         - floatArray[13] * floatArray[3] * floatArray[6];
      matOut[3] = -floatArray[1] * floatArray[6] * floatArray[11]
         + floatArray[1] * floatArray[7] * floatArray[10]
         + floatArray[5] * floatArray[2] * floatArray[11]
         - floatArray[5] * floatArray[3] * floatArray[10]
         - floatArray[9] * floatArray[2] * floatArray[7]
         + floatArray[9] * floatArray[3] * floatArray[6];
      matOut[4] = -floatArray[4] * floatArray[10] * floatArray[15]
         + floatArray[4] * floatArray[11] * floatArray[14]
         + floatArray[8] * floatArray[6] * floatArray[15]
         - floatArray[8] * floatArray[7] * floatArray[14]
         - floatArray[12] * floatArray[6] * floatArray[11]
         + floatArray[12] * floatArray[7] * floatArray[10];
      matOut[5] = floatArray[0] * floatArray[10] * floatArray[15]
         - floatArray[0] * floatArray[11] * floatArray[14]
         - floatArray[8] * floatArray[2] * floatArray[15]
         + floatArray[8] * floatArray[3] * floatArray[14]
         + floatArray[12] * floatArray[2] * floatArray[11]
         - floatArray[12] * floatArray[3] * floatArray[10];
      matOut[6] = -floatArray[0] * floatArray[6] * floatArray[15]
         + floatArray[0] * floatArray[7] * floatArray[14]
         + floatArray[4] * floatArray[2] * floatArray[15]
         - floatArray[4] * floatArray[3] * floatArray[14]
         - floatArray[12] * floatArray[2] * floatArray[7]
         + floatArray[12] * floatArray[3] * floatArray[6];
      matOut[7] = floatArray[0] * floatArray[6] * floatArray[11]
         - floatArray[0] * floatArray[7] * floatArray[10]
         - floatArray[4] * floatArray[2] * floatArray[11]
         + floatArray[4] * floatArray[3] * floatArray[10]
         + floatArray[8] * floatArray[2] * floatArray[7]
         - floatArray[8] * floatArray[3] * floatArray[6];
      matOut[8] = floatArray[4] * floatArray[9] * floatArray[15]
         - floatArray[4] * floatArray[11] * floatArray[13]
         - floatArray[8] * floatArray[5] * floatArray[15]
         + floatArray[8] * floatArray[7] * floatArray[13]
         + floatArray[12] * floatArray[5] * floatArray[11]
         - floatArray[12] * floatArray[7] * floatArray[9];
      matOut[9] = -floatArray[0] * floatArray[9] * floatArray[15]
         + floatArray[0] * floatArray[11] * floatArray[13]
         + floatArray[8] * floatArray[1] * floatArray[15]
         - floatArray[8] * floatArray[3] * floatArray[13]
         - floatArray[12] * floatArray[1] * floatArray[11]
         + floatArray[12] * floatArray[3] * floatArray[9];
      matOut[10] = floatArray[0] * floatArray[5] * floatArray[15]
         - floatArray[0] * floatArray[7] * floatArray[13]
         - floatArray[4] * floatArray[1] * floatArray[15]
         + floatArray[4] * floatArray[3] * floatArray[13]
         + floatArray[12] * floatArray[1] * floatArray[7]
         - floatArray[12] * floatArray[3] * floatArray[5];
      matOut[11] = -floatArray[0] * floatArray[5] * floatArray[11]
         + floatArray[0] * floatArray[7] * floatArray[9]
         + floatArray[4] * floatArray[1] * floatArray[11]
         - floatArray[4] * floatArray[3] * floatArray[9]
         - floatArray[8] * floatArray[1] * floatArray[7]
         + floatArray[8] * floatArray[3] * floatArray[5];
      matOut[12] = -floatArray[4] * floatArray[9] * floatArray[14]
         + floatArray[4] * floatArray[10] * floatArray[13]
         + floatArray[8] * floatArray[5] * floatArray[14]
         - floatArray[8] * floatArray[6] * floatArray[13]
         - floatArray[12] * floatArray[5] * floatArray[10]
         + floatArray[12] * floatArray[6] * floatArray[9];
      matOut[13] = floatArray[0] * floatArray[9] * floatArray[14]
         - floatArray[0] * floatArray[10] * floatArray[13]
         - floatArray[8] * floatArray[1] * floatArray[14]
         + floatArray[8] * floatArray[2] * floatArray[13]
         + floatArray[12] * floatArray[1] * floatArray[10]
         - floatArray[12] * floatArray[2] * floatArray[9];
      matOut[14] = -floatArray[0] * floatArray[5] * floatArray[14]
         + floatArray[0] * floatArray[6] * floatArray[13]
         + floatArray[4] * floatArray[1] * floatArray[14]
         - floatArray[4] * floatArray[2] * floatArray[13]
         - floatArray[12] * floatArray[1] * floatArray[6]
         + floatArray[12] * floatArray[2] * floatArray[5];
      matOut[15] = floatArray[0] * floatArray[5] * floatArray[10]
         - floatArray[0] * floatArray[6] * floatArray[9]
         - floatArray[4] * floatArray[1] * floatArray[10]
         + floatArray[4] * floatArray[2] * floatArray[9]
         + floatArray[8] * floatArray[1] * floatArray[6]
         - floatArray[8] * floatArray[2] * floatArray[5];
      float f = floatArray[0] * matOut[0] + floatArray[1] * matOut[4] + floatArray[2] * matOut[8] + floatArray[3] * matOut[12];
      if (f != 0.0) {
         for (int index = 0; index < 16; index++) {
            matOut[index] /= f;
         }
      } else {
         Arrays.fill(matOut, 0.0F);
      }
   }
}
