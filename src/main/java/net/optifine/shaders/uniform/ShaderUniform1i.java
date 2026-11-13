package net.optifine.shaders.uniform;

import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderUniform1i extends ShaderUniformBase {
   private static final int VALUE_UNKNOWN = Integer.MIN_VALUE;
   private int[] programValues;

   @Override
   protected void resetValue() {
      this.programValues = new int[]{Integer.MIN_VALUE};
   }

   public ShaderUniform1i(String name) {
      super(name);
      this.resetValue();
   }

   @Override
   protected void onProgramSet(int program) {
      if (program >= this.programValues.length) {
         int[] intArray = this.programValues;
         int[] intArray1 = new int[program + 10];
         System.arraycopy(intArray, 0, intArray1, 0, intArray.length);

         for (int index = intArray.length; index < intArray1.length; index++) {
            intArray1[index] = Integer.MIN_VALUE;
         }

         this.programValues = intArray1;
      }
   }

   public void setValue(int valueNew) {
      int program2 = this.getProgram();
      int i = this.programValues[program2];
      if (valueNew != i) {
         this.programValues[program2] = valueNew;
         int location = this.getLocation();
         if (location >= 0) {
            ARBShaderObjects.glUniform1iARB(location, valueNew);
            this.checkGLError();
         }
      }
   }

   public int getValue() {
      int program2 = this.getProgram();
      return this.programValues[program2];
   }
}
