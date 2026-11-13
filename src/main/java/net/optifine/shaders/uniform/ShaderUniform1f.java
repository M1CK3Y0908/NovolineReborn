package net.optifine.shaders.uniform;

import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderUniform1f extends ShaderUniformBase {
   private static final float VALUE_UNKNOWN = -Float.MAX_VALUE;
   private float[] programValues;

   @Override
   protected void onProgramSet(int program) {
      if (program >= this.programValues.length) {
         float[] floatArray = this.programValues;
         float[] floatArray1 = new float[program + 10];
         System.arraycopy(floatArray, 0, floatArray1, 0, floatArray.length);

         for (int index = floatArray.length; index < floatArray1.length; index++) {
            floatArray1[index] = -Float.MAX_VALUE;
         }

         this.programValues = floatArray1;
      }
   }

   public ShaderUniform1f(String name) {
      super(name);
      this.resetValue();
   }

   public float getValue() {
      int program2 = this.getProgram();
      return this.programValues[program2];
   }

   public void setValue(float valueNew) {
      int program2 = this.getProgram();
      float f = this.programValues[program2];
      if (valueNew != f) {
         this.programValues[program2] = valueNew;
         int location = this.getLocation();
         if (location >= 0) {
            ARBShaderObjects.glUniform1fARB(location, valueNew);
            this.checkGLError();
         }
      }
   }

   @Override
   protected void resetValue() {
      this.programValues = new float[]{-Float.MAX_VALUE};
   }
}
