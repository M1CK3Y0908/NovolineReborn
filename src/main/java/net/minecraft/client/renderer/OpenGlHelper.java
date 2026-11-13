package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;
import org.lwjgl.opengl.ARBCopyBuffer;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTBlendFuncSeparate;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GLContext;
import oshi.SystemInfo;
import oshi.hardware.Processor;

public class OpenGlHelper {
   public static int GL_PRIMARY_COLOR;
   public static boolean openGL31;
   private static boolean openGL14;
   public static int GL_SOURCE0_RGB;
   public static int GL_COPY_WRITE_BUFFER;
   public static int GL_FRAMEBUFFER;
   public static int GL_STATIC_DRAW;
   private static boolean arbTextureEnvCombine;
   public static int GL_COMPILE_STATUS;
   public static final int GL_QUADS = 7;
   public static boolean framebufferSupported;
   public static int GL_SOURCE0_ALPHA;
   public static boolean openGL21;
   public static boolean vboSupportedAti;
   public static int GL_OPERAND0_RGB;
   private static String cpu;
   public static int GL_FB_INCOMPLETE_READ_BUFFER;
   public static int GL_SOURCE1_RGB;
   public static int GL_VERTEX_SHADER;
   public static int GL_COLOR_ATTACHMENT0;
   public static int GL_INTERPOLATE;
   public static boolean nvidia;
   public static int GL_COPY_READ_BUFFER;
   private static boolean arbVbo;
   public static int GL_LINK_STATUS;
   public static int GL_TEXTURE2;
   public static boolean extBlendFuncSeparate;
   public static int GL_SOURCE2_ALPHA;
   public static int GL_ARRAY_BUFFER;
   public static int GL_FRAMEBUFFER_COMPLETE;
   public static boolean shadersSupported;
   private static int framebufferType;
   public static boolean vboRegions;
   public static int GL_SOURCE1_ALPHA;
   public static int GL_OPERAND0_ALPHA;
   private static String logText = "";
   public static int GL_COMBINE;
   public static final int GL_TRIANGLES = 4;
   public static boolean vboSupported;
   public static int lightmapTexUnit;
   public static int GL_FB_INCOMPLETE_ATTACHMENT;
   public static int GL_FB_INCOMPLETE_DRAW_BUFFER;
   public static int GL_OPERAND1_ALPHA;
   public static int GL_SOURCE2_RGB;
   public static float lastBrightnessX = 0.0F;
   private static boolean shadersAvailable;
   public static int GL_RENDERBUFFER;
   public static int GL_OPERAND2_RGB;
   public static int GL_FB_INCOMPLETE_MISS_ATTACH;
   private static boolean arbShaders;
   public static int GL_DEPTH_ATTACHMENT;
   public static float lastBrightnessY = 0.0F;
   public static int GL_PREVIOUS;
   public static int GL_OPERAND2_ALPHA;
   public static int GL_COMBINE_RGB;
   public static int GL_OPERAND1_RGB;
   private static boolean arbMultitexture;
   public static int GL_COMBINE_ALPHA;
   public static int GL_FRAGMENT_SHADER;
   public static boolean ati;
   public static int GL_CONSTANT;
   public static int defaultTexUnit;

   public static void glBindBuffer(int target, int buffer) {
      if (arbVbo) {
         ARBVertexBufferObject.glBindBufferARB(target, buffer);
      } else {
         GL15.glBindBuffer(target, buffer);
      }
   }

   public static void glUniform1(int location, IntBuffer values) {
      if (arbShaders) {
         ARBShaderObjects.glUniform1ARB(location, values);
      } else {
         GL20.glUniform1(location, values);
      }
   }

   public static String getCpu() {
      return cpu == null ? "<unknown>" : cpu;
   }

   public static int glGenFramebuffers() {
      if (!framebufferSupported) {
         return -1;
      }

      switch (framebufferType) {
         case 0:
            return GL30.glGenFramebuffers();
         case 1:
            return ARBFramebufferObject.glGenFramebuffers();
         case 2:
            return EXTFramebufferObject.glGenFramebuffersEXT();
         default:
            return -1;
      }
   }

   public static int glGetProgrami(int program, int pname) {
      return arbShaders ? ARBShaderObjects.glGetObjectParameteriARB(program, pname) : GL20.glGetProgrami(program, pname);
   }

   public static void glUniform1i(int location, int v0) {
      if (arbShaders) {
         ARBShaderObjects.glUniform1iARB(location, v0);
      } else {
         GL20.glUniform1i(location, v0);
      }
   }

   public static void glBlendFunc(int sFactorRGB, int dFactorRGB, int sfactorAlpha, int dfactorAlpha) {
      if (openGL14) {
         if (extBlendFuncSeparate) {
            EXTBlendFuncSeparate.glBlendFuncSeparateEXT(sFactorRGB, dFactorRGB, sfactorAlpha, dfactorAlpha);
         } else {
            GL14.glBlendFuncSeparate(sFactorRGB, dFactorRGB, sfactorAlpha, dfactorAlpha);
         }
      } else {
         GL11.glBlendFunc(sFactorRGB, dFactorRGB);
      }
   }

   public static void glUniform3(int location, IntBuffer values) {
      if (arbShaders) {
         ARBShaderObjects.glUniform3ARB(location, values);
      } else {
         GL20.glUniform3(location, values);
      }
   }

   public static int glCreateProgram() {
      return arbShaders ? ARBShaderObjects.glCreateProgramObjectARB() : GL20.glCreateProgram();
   }

   public static void handleInt2(int i, FloatBuffer floatBuffer) {
      if (arbShaders) {
         ARBShaderObjects.glUniformMatrix4ARB(i, true, floatBuffer);
      } else {
         GL20.glUniformMatrix4(i, true, floatBuffer);
      }
   }

   public static void glUniform2(int location, IntBuffer values) {
      if (arbShaders) {
         ARBShaderObjects.glUniform2ARB(location, values);
      } else {
         GL20.glUniform2(location, values);
      }
   }

   public static void setLightmapTextureCoords(int target, float f, float f2) {
      if (arbMultitexture) {
         ARBMultitexture.glMultiTexCoord2fARB(target, f, f2);
      } else {
         GL13.glMultiTexCoord2f(target, f, f2);
      }

      if (target == lightmapTexUnit) {
         lastBrightnessX = f;
         lastBrightnessY = f2;
      }
   }

   public static void setActiveTexture(int texture) {
      if (arbMultitexture) {
         ARBMultitexture.glActiveTextureARB(texture);
      } else {
         GL13.glActiveTexture(texture);
      }
   }

   public static void glBufferSubData(int arg0, long arg1, ByteBuffer arg3) {
      if (arbVbo) {
         ARBVertexBufferObject.glBufferSubDataARB(arg0, arg1, arg3);
      } else {
         GL15.glBufferSubData(arg0, arg1, arg3);
      }
   }

   public static int glCheckFramebufferStatus(int target) {
      if (!framebufferSupported) {
         return -1;
      }

      switch (framebufferType) {
         case 0:
            return GL30.glCheckFramebufferStatus(target);
         case 1:
            return ARBFramebufferObject.glCheckFramebufferStatus(target);
         case 2:
            return EXTFramebufferObject.glCheckFramebufferStatusEXT(target);
         default:
            return -1;
      }
   }

   public static void glUniform4(int location, IntBuffer values) {
      if (arbShaders) {
         ARBShaderObjects.glUniform4ARB(location, values);
      } else {
         GL20.glUniform4(location, values);
      }
   }

   public static void glUniform1(int location, FloatBuffer values) {
      if (arbShaders) {
         ARBShaderObjects.glUniform1ARB(location, values);
      } else {
         GL20.glUniform1(location, values);
      }
   }

   public static void handleInt6(int i, int i2, int i3) {
      if (framebufferSupported) {
         switch (framebufferType) {
            case 0:
               GL30.glRenderbufferStorage(i, 33190, i2, i3);
               break;
            case 1:
               ARBFramebufferObject.glRenderbufferStorage(i, 33190, i2, i3);
               break;
            case 2:
               EXTFramebufferObject.glRenderbufferStorageEXT(i, 33190, i2, i3);
         }
      }
   }

   public static void glLinkProgram(int program) {
      if (arbShaders) {
         ARBShaderObjects.glLinkProgramARB(program);
      } else {
         GL20.glLinkProgram(program);
      }
   }

   public static void glAttachShader(int program, int shaderIn) {
      if (arbShaders) {
         ARBShaderObjects.glAttachObjectARB(program, shaderIn);
      } else {
         GL20.glAttachShader(program, shaderIn);
      }
   }

   public static void glDeleteBuffers(int buffer) {
      if (arbVbo) {
         ARBVertexBufferObject.glDeleteBuffersARB(buffer);
      } else {
         GL15.glDeleteBuffers(buffer);
      }
   }

   public static void glBindFramebuffer(int target, int framebufferIn) {
      if (framebufferSupported) {
         switch (framebufferType) {
            case 0:
               GL30.glBindFramebuffer(target, framebufferIn);
               break;
            case 1:
               ARBFramebufferObject.glBindFramebuffer(target, framebufferIn);
               break;
            case 2:
               EXTFramebufferObject.glBindFramebufferEXT(target, framebufferIn);
         }
      }
   }

   public static boolean useVbo() {
      return Config.isMultiTexture() ? false : (Config.isRenderRegions() && !vboRegions ? false : vboSupported && Minecraft.getMinecraft().gameSettings.useVbo);
   }

   public static void glRenderbufferStorage(int target, int internalFormat, int width, int height) {
      if (framebufferSupported) {
         switch (framebufferType) {
            case 0:
               GL30.glFramebufferRenderbuffer(target, internalFormat, width, height);
               break;
            case 1:
               ARBFramebufferObject.glFramebufferRenderbuffer(target, internalFormat, width, height);
               break;
            case 2:
               EXTFramebufferObject.glFramebufferRenderbufferEXT(target, internalFormat, width, height);
         }
      }
   }

   public static void glDeleteProgram(int program) {
      if (arbShaders) {
         ARBShaderObjects.glDeleteObjectARB(program);
      } else {
         GL20.glDeleteProgram(program);
      }
   }

   public static void glDeleteShader(int i) {
      if (arbShaders) {
         ARBShaderObjects.glDeleteObjectARB(i);
      } else {
         GL20.glDeleteShader(i);
      }
   }

   public static String createString2(int i) {
      return arbShaders ? ARBShaderObjects.glGetInfoLogARB(i, 32768) : GL20.glGetShaderInfoLog(i, 32768);
   }

   public static void setClientActiveTexture(int texture) {
      if (arbMultitexture) {
         ARBMultitexture.glClientActiveTextureARB(texture);
      } else {
         GL13.glClientActiveTexture(texture);
      }
   }

   public static void glUniform4(int location, FloatBuffer values) {
      if (arbShaders) {
         ARBShaderObjects.glUniform4ARB(location, values);
      } else {
         GL20.glUniform4(location, values);
      }
   }

   public static int glGetAttribLocation(int i, CharSequence charSequence) {
      return arbShaders ? ARBVertexShader.glGetAttribLocationARB(i, charSequence) : GL20.glGetAttribLocation(i, charSequence);
   }

   public static void glCopyBufferSubData(int arg0, int arg1, long arg2, long arg4, long arg6) {
      if (openGL31) {
         GL31.glCopyBufferSubData(arg0, arg1, arg2, arg4, arg6);
      } else {
         ARBCopyBuffer.glCopyBufferSubData(arg0, arg1, arg2, arg4, arg6);
      }
   }

   public static void handleInt(int i, FloatBuffer floatBuffer) {
      if (arbShaders) {
         ARBShaderObjects.glUniformMatrix3ARB(i, true, floatBuffer);
      } else {
         GL20.glUniformMatrix3(i, true, floatBuffer);
      }
   }

   public static String getLogText() {
      return logText;
   }

   public static int glGetShaderi(int shaderIn, int pname) {
      return arbShaders ? ARBShaderObjects.glGetObjectParameteriARB(shaderIn, pname) : GL20.glGetShaderi(shaderIn, pname);
   }

   public static String createString(int i) {
      return arbShaders ? ARBShaderObjects.glGetInfoLogARB(i, 32768) : GL20.glGetProgramInfoLog(i, 32768);
   }

   public static void glDeleteFramebuffers(int framebufferIn) {
      if (framebufferSupported) {
         switch (framebufferType) {
            case 0:
               GL30.glDeleteFramebuffers(framebufferIn);
               break;
            case 1:
               ARBFramebufferObject.glDeleteFramebuffers(framebufferIn);
               break;
            case 2:
               EXTFramebufferObject.glDeleteFramebuffersEXT(framebufferIn);
         }
      }
   }

   public static void handleInt3(int i, FloatBuffer floatBuffer) {
      if (arbShaders) {
         ARBShaderObjects.glUniformMatrix2ARB(i, true, floatBuffer);
      } else {
         GL20.glUniformMatrix2(i, true, floatBuffer);
      }
   }

   public static void glUniform2(int location, FloatBuffer values) {
      if (arbShaders) {
         ARBShaderObjects.glUniform2ARB(location, values);
      } else {
         GL20.glUniform2(location, values);
      }
   }

   public static void initializeTextures() {
      Config.initDisplay();
      ContextCapabilities contextCapabilities = GLContext.getCapabilities();
      arbMultitexture = contextCapabilities.GL_ARB_multitexture && !contextCapabilities.OpenGL13;
      arbTextureEnvCombine = contextCapabilities.GL_ARB_texture_env_combine && !contextCapabilities.OpenGL13;
      openGL31 = contextCapabilities.OpenGL31;
      if (openGL31) {
         GL_COPY_READ_BUFFER = 36662;
         GL_COPY_WRITE_BUFFER = 36663;
      } else {
         GL_COPY_READ_BUFFER = 36662;
         GL_COPY_WRITE_BUFFER = 36663;
      }

      boolean flag = openGL31 || contextCapabilities.GL_ARB_copy_buffer;
      boolean conditionMet2 = contextCapabilities.OpenGL14;
      vboRegions = flag && conditionMet2;
      if (!vboRegions) {
         ArrayList text = new ArrayList();
         if (!flag) {
            text.add("OpenGL 1.3, ARB_copy_buffer");
         }

         if (!conditionMet2) {
            text.add("OpenGL 1.4");
         }

         String aprocessor = "VboRegions not supported, missing: " + Config.listToString(text);
         Config.dbg(aprocessor);
         logText = logText + aprocessor + "\n";
      }

      if (arbMultitexture) {
         logText = logText + "Using ARB_multitexture.\n";
         defaultTexUnit = 33984;
         lightmapTexUnit = 33985;
         GL_TEXTURE2 = 33986;
      } else {
         logText = logText + "Using GL 1.3 multitexturing.\n";
         defaultTexUnit = 33984;
         lightmapTexUnit = 33985;
         GL_TEXTURE2 = 33986;
      }

      if (arbTextureEnvCombine) {
         logText = logText + "Using ARB_texture_env_combine.\n";
         GL_COMBINE = 34160;
         GL_INTERPOLATE = 34165;
         GL_PRIMARY_COLOR = 34167;
         GL_CONSTANT = 34166;
         GL_PREVIOUS = 34168;
         GL_COMBINE_RGB = 34161;
         GL_SOURCE0_RGB = 34176;
         GL_SOURCE1_RGB = 34177;
         GL_SOURCE2_RGB = 34178;
         GL_OPERAND0_RGB = 34192;
         GL_OPERAND1_RGB = 34193;
         GL_OPERAND2_RGB = 34194;
         GL_COMBINE_ALPHA = 34162;
         GL_SOURCE0_ALPHA = 34184;
         GL_SOURCE1_ALPHA = 34185;
         GL_SOURCE2_ALPHA = 34186;
         GL_OPERAND0_ALPHA = 34200;
         GL_OPERAND1_ALPHA = 34201;
         GL_OPERAND2_ALPHA = 34202;
      } else {
         logText = logText + "Using GL 1.3 texture combiners.\n";
         GL_COMBINE = 34160;
         GL_INTERPOLATE = 34165;
         GL_PRIMARY_COLOR = 34167;
         GL_CONSTANT = 34166;
         GL_PREVIOUS = 34168;
         GL_COMBINE_RGB = 34161;
         GL_SOURCE0_RGB = 34176;
         GL_SOURCE1_RGB = 34177;
         GL_SOURCE2_RGB = 34178;
         GL_OPERAND0_RGB = 34192;
         GL_OPERAND1_RGB = 34193;
         GL_OPERAND2_RGB = 34194;
         GL_COMBINE_ALPHA = 34162;
         GL_SOURCE0_ALPHA = 34184;
         GL_SOURCE1_ALPHA = 34185;
         GL_SOURCE2_ALPHA = 34186;
         GL_OPERAND0_ALPHA = 34200;
         GL_OPERAND1_ALPHA = 34201;
         GL_OPERAND2_ALPHA = 34202;
      }

      extBlendFuncSeparate = contextCapabilities.GL_EXT_blend_func_separate && !contextCapabilities.OpenGL14;
      openGL14 = contextCapabilities.OpenGL14 || contextCapabilities.GL_EXT_blend_func_separate;
      framebufferSupported = openGL14
         && (contextCapabilities.GL_ARB_framebuffer_object || contextCapabilities.GL_EXT_framebuffer_object || contextCapabilities.OpenGL30);
      if (framebufferSupported) {
         logText = logText + "Using framebuffer objects because ";
         if (contextCapabilities.OpenGL30) {
            logText = logText + "OpenGL 3.0 is supported and separate blending is supported.\n";
            framebufferType = 0;
            GL_FRAMEBUFFER = 36160;
            GL_RENDERBUFFER = 36161;
            GL_COLOR_ATTACHMENT0 = 36064;
            GL_DEPTH_ATTACHMENT = 36096;
            GL_FRAMEBUFFER_COMPLETE = 36053;
            GL_FB_INCOMPLETE_ATTACHMENT = 36054;
            GL_FB_INCOMPLETE_MISS_ATTACH = 36055;
            GL_FB_INCOMPLETE_DRAW_BUFFER = 36059;
            GL_FB_INCOMPLETE_READ_BUFFER = 36060;
         } else if (contextCapabilities.GL_ARB_framebuffer_object) {
            logText = logText + "ARB_framebuffer_object is supported and separate blending is supported.\n";
            framebufferType = 1;
            GL_FRAMEBUFFER = 36160;
            GL_RENDERBUFFER = 36161;
            GL_COLOR_ATTACHMENT0 = 36064;
            GL_DEPTH_ATTACHMENT = 36096;
            GL_FRAMEBUFFER_COMPLETE = 36053;
            GL_FB_INCOMPLETE_MISS_ATTACH = 36055;
            GL_FB_INCOMPLETE_ATTACHMENT = 36054;
            GL_FB_INCOMPLETE_DRAW_BUFFER = 36059;
            GL_FB_INCOMPLETE_READ_BUFFER = 36060;
         } else if (contextCapabilities.GL_EXT_framebuffer_object) {
            logText = logText + "EXT_framebuffer_object is supported.\n";
            framebufferType = 2;
            GL_FRAMEBUFFER = 36160;
            GL_RENDERBUFFER = 36161;
            GL_COLOR_ATTACHMENT0 = 36064;
            GL_DEPTH_ATTACHMENT = 36096;
            GL_FRAMEBUFFER_COMPLETE = 36053;
            GL_FB_INCOMPLETE_MISS_ATTACH = 36055;
            GL_FB_INCOMPLETE_ATTACHMENT = 36054;
            GL_FB_INCOMPLETE_DRAW_BUFFER = 36059;
            GL_FB_INCOMPLETE_READ_BUFFER = 36060;
         }
      } else {
         logText = logText + "Not using framebuffer objects because ";
         logText = logText + "OpenGL 1.4 is " + (contextCapabilities.OpenGL14 ? "" : "not ") + "supported, ";
         logText = logText + "EXT_blend_func_separate is " + (contextCapabilities.GL_EXT_blend_func_separate ? "" : "not ") + "supported, ";
         logText = logText + "OpenGL 3.0 is " + (contextCapabilities.OpenGL30 ? "" : "not ") + "supported, ";
         logText = logText + "ARB_framebuffer_object is " + (contextCapabilities.GL_ARB_framebuffer_object ? "" : "not ") + "supported, and ";
         logText = logText + "EXT_framebuffer_object is " + (contextCapabilities.GL_EXT_framebuffer_object ? "" : "not ") + "supported.\n";
      }

      openGL21 = contextCapabilities.OpenGL21;
      shadersAvailable = openGL21
         || contextCapabilities.GL_ARB_vertex_shader && contextCapabilities.GL_ARB_fragment_shader && contextCapabilities.GL_ARB_shader_objects;
      logText = logText + "Shaders are " + (shadersAvailable ? "" : "not ") + "available because ";
      if (shadersAvailable) {
         if (contextCapabilities.OpenGL21) {
            logText = logText + "OpenGL 2.1 is supported.\n";
            arbShaders = false;
            GL_LINK_STATUS = 35714;
            GL_COMPILE_STATUS = 35713;
            GL_VERTEX_SHADER = 35633;
            GL_FRAGMENT_SHADER = 35632;
         } else {
            logText = logText + "ARB_shader_objects, ARB_vertex_shader, and ARB_fragment_shader are supported.\n";
            arbShaders = true;
            GL_LINK_STATUS = 35714;
            GL_COMPILE_STATUS = 35713;
            GL_VERTEX_SHADER = 35633;
            GL_FRAGMENT_SHADER = 35632;
         }
      } else {
         logText = logText + "OpenGL 2.1 is " + (contextCapabilities.OpenGL21 ? "" : "not ") + "supported, ";
         logText = logText + "ARB_shader_objects is " + (contextCapabilities.GL_ARB_shader_objects ? "" : "not ") + "supported, ";
         logText = logText + "ARB_vertex_shader is " + (contextCapabilities.GL_ARB_vertex_shader ? "" : "not ") + "supported, and ";
         logText = logText + "ARB_fragment_shader is " + (contextCapabilities.GL_ARB_fragment_shader ? "" : "not ") + "supported.\n";
      }

      shadersSupported = framebufferSupported && shadersAvailable;
      String text = GL11.glGetString(7936).toLowerCase();
      nvidia = text.contains("nvidia");
      arbVbo = !contextCapabilities.OpenGL15 && contextCapabilities.GL_ARB_vertex_buffer_object;
      vboSupported = contextCapabilities.OpenGL15 || arbVbo;
      logText = logText + "VBOs are " + (vboSupported ? "" : "not ") + "available because ";
      if (vboSupported) {
         if (arbVbo) {
            logText = logText + "ARB_vertex_buffer_object is supported.\n";
            GL_STATIC_DRAW = 35044;
            GL_ARRAY_BUFFER = 34962;
         } else {
            logText = logText + "OpenGL 1.5 is supported.\n";
            GL_STATIC_DRAW = 35044;
            GL_ARRAY_BUFFER = 34962;
         }
      }

      ati = text.contains("ati");
      if (ati) {
         if (vboSupported) {
            vboSupportedAti = true;
         } else {
            net.minecraft.client.settings.GameSettings.Options.RENDER_DISTANCE.setValueMax(16.0F);
         }
      }

      try {
         Processor[] aprocessor = new SystemInfo().getHardware().getProcessors();
         cpu = String.format("%dx %s", aprocessor.length, aprocessor[0]).replaceAll("\\s+", " ");
      } catch (Throwable throwable) {
      }
   }

   public static int glGenBuffers() {
      return arbVbo ? ARBVertexBufferObject.glGenBuffersARB() : GL15.glGenBuffers();
   }

   public static void glCompileShader(int shaderIn) {
      if (arbShaders) {
         ARBShaderObjects.glCompileShaderARB(shaderIn);
      } else {
         GL20.glCompileShader(shaderIn);
      }
   }

   public static int glCreateShader(int type) {
      return arbShaders ? ARBShaderObjects.glCreateShaderObjectARB(type) : GL20.glCreateShader(type);
   }

   public static void handleInt4(int i, int i2, int i3) {
      if (framebufferSupported) {
         switch (framebufferType) {
            case 0:
               GL30.glFramebufferTexture2D(i, i2, 3553, i3, 0);
               break;
            case 1:
               ARBFramebufferObject.glFramebufferTexture2D(i, i2, 3553, i3, 0);
               break;
            case 2:
               EXTFramebufferObject.glFramebufferTexture2DEXT(i, i2, 3553, i3, 0);
         }
      }
   }

   public static void glBindRenderbuffer(int target, int renderbuffer) {
      if (framebufferSupported) {
         switch (framebufferType) {
            case 0:
               GL30.glBindRenderbuffer(target, renderbuffer);
               break;
            case 1:
               ARBFramebufferObject.glBindRenderbuffer(target, renderbuffer);
               break;
            case 2:
               EXTFramebufferObject.glBindRenderbufferEXT(target, renderbuffer);
         }
      }
   }

   public static boolean isFramebufferEnabled() {
      return !Config.isAntialiasing() && framebufferSupported && Minecraft.getMinecraft().gameSettings.fboEnable;
   }

   public static void glShaderSource(int shaderIn, ByteBuffer string) {
      if (arbShaders) {
         ARBShaderObjects.glShaderSourceARB(shaderIn, string);
      } else {
         GL20.glShaderSource(shaderIn, string);
      }
   }

   public static void glBufferData(int arg0, long arg1, int arg3) {
      if (arbVbo) {
         ARBVertexBufferObject.glBufferDataARB(arg0, arg1, arg3);
      } else {
         GL15.glBufferData(arg0, arg1, arg3);
      }
   }

   public static void glUseProgram(int program) {
      if (arbShaders) {
         ARBShaderObjects.glUseProgramObjectARB(program);
      } else {
         GL20.glUseProgram(program);
      }
   }

   public static int glGenRenderbuffers() {
      if (!framebufferSupported) {
         return -1;
      }

      switch (framebufferType) {
         case 0:
            return GL30.glGenRenderbuffers();
         case 1:
            return ARBFramebufferObject.glGenRenderbuffers();
         case 2:
            return EXTFramebufferObject.glGenRenderbuffersEXT();
         default:
            return -1;
      }
   }

   public static boolean areShadersSupported() {
      return shadersSupported;
   }

   public static void glUniform3(int location, FloatBuffer values) {
      if (arbShaders) {
         ARBShaderObjects.glUniform3ARB(location, values);
      } else {
         GL20.glUniform3(location, values);
      }
   }

   public static void glDeleteRenderbuffers(int renderbuffer) {
      if (framebufferSupported) {
         switch (framebufferType) {
            case 0:
               GL30.glDeleteRenderbuffers(renderbuffer);
               break;
            case 1:
               ARBFramebufferObject.glDeleteRenderbuffers(renderbuffer);
               break;
            case 2:
               EXTFramebufferObject.glDeleteRenderbuffersEXT(renderbuffer);
         }
      }
   }

   public static int glGetUniformLocation(int programObj, CharSequence name) {
      return arbShaders ? ARBShaderObjects.glGetUniformLocationARB(programObj, name) : GL20.glGetUniformLocation(programObj, name);
   }

   public static void handleInt5(int i, ByteBuffer byteBuffer) {
      if (arbVbo) {
         ARBVertexBufferObject.glBufferDataARB(i, byteBuffer, 35044);
      } else {
         GL15.glBufferData(i, byteBuffer, 35044);
      }
   }
}
