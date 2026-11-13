package net.minecraft.client.shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.OpenGlHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;

public class ShaderUniform {
   private final String shaderName;
   private final int uniformCount;
   private boolean dirty;
   private final int uniformType;
   private final ShaderManager shaderManager;
   private final IntBuffer uniformIntBuffer;
   private static final Logger logger = LogManager.getLogger();
   private final FloatBuffer uniformFloatBuffer;
   private int uniformLocation;

   public void set(float x, float y, float z, float w) {
      this.uniformFloatBuffer.position(0);
      this.uniformFloatBuffer.put(x);
      this.uniformFloatBuffer.put(y);
      this.uniformFloatBuffer.put(z);
      this.uniformFloatBuffer.put(w);
      this.uniformFloatBuffer.flip();
      this.markDirty();
   }

   private void uploadFloat() {
      switch (this.uniformType) {
         case 4:
            OpenGlHelper.glUniform1(this.uniformLocation, this.uniformFloatBuffer);
            break;
         case 5:
            OpenGlHelper.glUniform2(this.uniformLocation, this.uniformFloatBuffer);
            break;
         case 6:
            OpenGlHelper.glUniform3(this.uniformLocation, this.uniformFloatBuffer);
            break;
         case 7:
            OpenGlHelper.glUniform4(this.uniformLocation, this.uniformFloatBuffer);
            break;
         default:
            logger.warn("Uniform.upload called, but count value (" + this.uniformCount + ") is not in the range of 1 to 4. Ignoring.");
      }
   }

   public String getShaderName() {
      return this.shaderName;
   }

   public void setUniformLocation(int uniformLocation) {
      this.uniformLocation = uniformLocation;
   }

   public void setFloatComponents(float x, float y, float z, float w) {
      this.uniformFloatBuffer.position(0);
      if (this.uniformType >= 4) {
         this.uniformFloatBuffer.put(0, x);
      }

      if (this.uniformType >= 5) {
         this.uniformFloatBuffer.put(1, y);
      }

      if (this.uniformType >= 6) {
         this.uniformFloatBuffer.put(2, z);
      }

      if (this.uniformType >= 7) {
         this.uniformFloatBuffer.put(3, w);
      }

      this.markDirty();
   }

   public static int parseType(String typeName) {
      int i = -1;
      if (typeName.equals("int")) {
         i = 0;
      } else if (typeName.equals("float")) {
         i = 4;
      } else if (typeName.startsWith("matrix")) {
         if (typeName.endsWith("2x2")) {
            i = 8;
         } else if (typeName.endsWith("3x3")) {
            i = 9;
         } else if (typeName.endsWith("4x4")) {
            i = 10;
         }
      }

      return i;
   }

   public void set(float value) {
      this.uniformFloatBuffer.position(0);
      this.uniformFloatBuffer.put(0, value);
      this.markDirty();
   }

   public void set(
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      float f9,
      float f10,
      float f11,
      float f12,
      float f13,
      float f14,
      float f15,
      float f16
   ) {
      this.uniformFloatBuffer.position(0);
      this.uniformFloatBuffer.put(0, f);
      this.uniformFloatBuffer.put(1, f2);
      this.uniformFloatBuffer.put(2, f3);
      this.uniformFloatBuffer.put(3, f4);
      this.uniformFloatBuffer.put(4, f5);
      this.uniformFloatBuffer.put(5, f6);
      this.uniformFloatBuffer.put(6, f7);
      this.uniformFloatBuffer.put(7, f8);
      this.uniformFloatBuffer.put(8, f9);
      this.uniformFloatBuffer.put(9, f10);
      this.uniformFloatBuffer.put(10, f11);
      this.uniformFloatBuffer.put(11, f12);
      this.uniformFloatBuffer.put(12, f13);
      this.uniformFloatBuffer.put(13, f14);
      this.uniformFloatBuffer.put(14, f15);
      this.uniformFloatBuffer.put(15, f16);
      this.markDirty();
   }

   public void set(float x, float y, float z) {
      this.uniformFloatBuffer.position(0);
      this.uniformFloatBuffer.put(0, x);
      this.uniformFloatBuffer.put(1, y);
      this.uniformFloatBuffer.put(2, z);
      this.markDirty();
   }

   private void uploadInt() {
      switch (this.uniformType) {
         case 0:
            OpenGlHelper.glUniform1(this.uniformLocation, this.uniformIntBuffer);
            break;
         case 1:
            OpenGlHelper.glUniform2(this.uniformLocation, this.uniformIntBuffer);
            break;
         case 2:
            OpenGlHelper.glUniform3(this.uniformLocation, this.uniformIntBuffer);
            break;
         case 3:
            OpenGlHelper.glUniform4(this.uniformLocation, this.uniformIntBuffer);
            break;
         default:
            logger.warn("Uniform.upload called, but count value (" + this.uniformCount + ") is  not in the range of 1 to 4. Ignoring.");
      }
   }

   public void set(Matrix4f matrix) {
      this.set(
         matrix.m00,
         matrix.m01,
         matrix.m02,
         matrix.m03,
         matrix.m10,
         matrix.m11,
         matrix.m12,
         matrix.m13,
         matrix.m20,
         matrix.m21,
         matrix.m22,
         matrix.m23,
         matrix.m30,
         matrix.m31,
         matrix.m32,
         matrix.m33
      );
   }

   public void set(int x, int y, int z, int w) {
      this.uniformIntBuffer.position(0);
      if (this.uniformType >= 0) {
         this.uniformIntBuffer.put(0, x);
      }

      if (this.uniformType >= 1) {
         this.uniformIntBuffer.put(1, y);
      }

      if (this.uniformType >= 2) {
         this.uniformIntBuffer.put(2, z);
      }

      if (this.uniformType >= 3) {
         this.uniformIntBuffer.put(3, w);
      }

      this.markDirty();
   }

   public ShaderUniform(String name, int type, int count, ShaderManager manager) {
      this.shaderName = name;
      this.uniformCount = count;
      this.uniformType = type;
      this.shaderManager = manager;
      if (type <= 3) {
         this.uniformIntBuffer = BufferUtils.createIntBuffer(count);
         this.uniformFloatBuffer = null;
      } else {
         this.uniformIntBuffer = null;
         this.uniformFloatBuffer = BufferUtils.createFloatBuffer(count);
      }

      this.uniformLocation = -1;
      this.markDirty();
   }

   private void uploadFloatMatrix() {
      switch (this.uniformType) {
         case 8:
            OpenGlHelper.handleInt3(this.uniformLocation, this.uniformFloatBuffer);
            break;
         case 9:
            OpenGlHelper.handleInt(this.uniformLocation, this.uniformFloatBuffer);
            break;
         case 10:
            OpenGlHelper.handleInt2(this.uniformLocation, this.uniformFloatBuffer);
      }
   }

   private void markDirty() {
      this.dirty = true;
      if (this.shaderManager != null) {
         this.shaderManager.markDirty();
      }
   }

   public void set(float x, float y) {
      this.uniformFloatBuffer.position(0);
      this.uniformFloatBuffer.put(0, x);
      this.uniformFloatBuffer.put(1, y);
      this.markDirty();
   }

   public void set(float[] values) {
      if (values.length < this.uniformCount) {
         logger.warn("Uniform.set called with a too-small value array (expected " + this.uniformCount + ", got " + values.length + "). Ignoring.");
      } else {
         this.uniformFloatBuffer.position(0);
         this.uniformFloatBuffer.put(values);
         this.uniformFloatBuffer.position(0);
         this.markDirty();
      }
   }

   public void upload() {
      if (!this.dirty) {
      }

      this.dirty = false;
      if (this.uniformType <= 3) {
         this.uploadInt();
      } else if (this.uniformType <= 7) {
         this.uploadFloat();
      } else {
         if (this.uniformType > 10) {
            logger.warn("Uniform.upload called, but type value (" + this.uniformType + ") is not a valid type. Ignoring.");
            return;
         }

         this.uploadFloatMatrix();
      }
   }
}
