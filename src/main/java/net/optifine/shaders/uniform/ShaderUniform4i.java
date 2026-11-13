package net.optifine.shaders.uniform;

import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderUniform4i extends ShaderUniformBase {
   private static final int VALUE_UNKNOWN = Integer.MIN_VALUE;
   private int[][] programValues;

   @Override
   protected void onProgramSet(int program) {
      if (program >= this.programValues.length) {
         int[][] intArray = this.programValues;
         int[][] intArray1 = new int[program + 10][];
         System.arraycopy(intArray, 0, intArray1, 0, intArray.length);
         this.programValues = intArray1;
      }

      if (this.programValues[program] == null) {
         this.programValues[program] = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
      }
   }

   public ShaderUniform4i(String name) {
      super(name);
      this.resetValue();
   }

   @Override
   protected void resetValue() {
      this.programValues = new int[][]{{Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE}};
   }

   public void setValue(int v0, int v1, int v2, int v3) {
      int program2 = this.getProgram();
      int[] iArray = this.programValues[program2];
      if (iArray[0] != v0 || iArray[1] != v1 || iArray[2] != v2 || iArray[3] != v3) {
         iArray[0] = v0;
         iArray[1] = v1;
         iArray[2] = v2;
         iArray[3] = v3;
         int location = this.getLocation();
         if (location >= 0) {
            ARBShaderObjects.glUniform4iARB(location, v0, v1, v2, v3);
            this.checkGLError();
         }
      }
   }

   public int[] getValue() {
      int program2 = this.getProgram();
      return this.programValues[program2];
   }
}
