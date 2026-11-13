package net.optifine.shaders.uniform;

import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderUniform2f extends ShaderUniformBase {
   private float[][] programValues;
   private static final float VALUE_UNKNOWN = -Float.MAX_VALUE;

   public ShaderUniform2f(String name) {
      super(name);
      this.resetValue();
   }

   @Override
   protected void onProgramSet(int program) {
      if (program >= this.programValues.length) {
         float[][] floatArray = this.programValues;
         float[][] floatArray1 = new float[program + 10][];
         System.arraycopy(floatArray, 0, floatArray1, 0, floatArray.length);
         this.programValues = floatArray1;
      }

      if (this.programValues[program] == null) {
         this.programValues[program] = new float[]{-Float.MAX_VALUE, -Float.MAX_VALUE};
      }
   }

   @Override
   protected void resetValue() {
      this.programValues = new float[][]{{-Float.MAX_VALUE, -Float.MAX_VALUE}};
   }

   public void setValue(float v0, float v1) {
      int program2 = this.getProgram();
      float[] fArray = this.programValues[program2];
      if (fArray[0] != v0 || fArray[1] != v1) {
         fArray[0] = v0;
         fArray[1] = v1;
         int location = this.getLocation();
         if (location >= 0) {
            ARBShaderObjects.glUniform2fARB(location, v0, v1);
            this.checkGLError();
         }
      }
   }

   public float[] getValue() {
      int program2 = this.getProgram();
      return this.programValues[program2];
   }
}
