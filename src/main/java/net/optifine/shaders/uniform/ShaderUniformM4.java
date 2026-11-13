package net.optifine.shaders.uniform;

import java.nio.FloatBuffer;
import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderUniformM4 extends ShaderUniformBase {
   private boolean transpose;
   private FloatBuffer matrix;

   public ShaderUniformM4(String name) {
      super(name);
   }

   public void handleFloatBuffer(FloatBuffer floatBuffer) {
      this.transpose = false;
      this.matrix = floatBuffer;
      int location = this.getLocation();
      if (location >= 0) {
         ARBShaderObjects.glUniformMatrix4ARB(location, false, floatBuffer);
         this.checkGLError();
      }
   }

   @Override
   protected void resetValue() {
      this.matrix = null;
   }

   @Override
   protected void onProgramSet(int program) {
   }

   public float getValue(int row, int col) {
      if (this.matrix == null) {
         return 0.0F;
      }

      int i = this.transpose ? col * 4 + row : row * 4 + col;
      return this.matrix.get(i);
   }
}
