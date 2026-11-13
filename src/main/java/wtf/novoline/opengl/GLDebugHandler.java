package wtf.novoline.opengl;

import java.nio.IntBuffer;
import net.minecraft.src.Config;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.ARBDebugOutputCallback;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.opengl.ARBDebugOutputCallback.Handler;

public class GLDebugHandler implements Handler {
   public String sourceName(int i) {
      switch (i) {
         case 33350:
            return "API";
         case 33351:
            return "WIN";
         case 33352:
            return "SHADER";
         case 33353:
            return "EXT";
         case 33354:
            return "APP";
         case 33355:
            return "OTHER";
         default:
            return this.unknownName(i);
      }
   }

   public String typeName(int i) {
      switch (i) {
         case 33356:
            return "ERROR";
         case 33357:
            return "DEPRECATED";
         case 33358:
            return "UNDEFINED";
         case 33359:
            return "PORTABILITY";
         case 33360:
            return "PERFORMANCE";
         case 33361:
            return "OTHER";
         default:
            return this.unknownName(i);
      }
   }

   public void handleMessage(int i, int i2, int i3, int i4, String text) {
      if (!text.contains("glBindFramebuffer") && !text.contains("Wide lines") && !text.contains("shader recompiled")) {
         Config.dbg(
            "[LWJGL] source: "
               + this.sourceName(i)
               + ", type: "
               + this.typeName(i2)
               + ", id: "
               + i3
               + ", severity: "
               + this.severityName(i4)
               + ", message: "
               + text
         );
         new Throwable("StackTrace").printStackTrace();
      }
   }

   private String unknownName(int i) {
      return "Unknown (0x" + Integer.toHexString(i).toUpperCase() + ")";
   }

   public static void enableDebugOutput() throws LWJGLException {
      boolean flag = GLContext.getCapabilities().GL_ARB_debug_output;
      ContextAttribs contextAttribs = new ContextAttribs().withDebug(true);
      Display.create(new PixelFormat().withDepthBits(24), contextAttribs);
      ARBDebugOutput.glDebugMessageCallbackARB(new ARBDebugOutputCallback(new GLDebugHandler()));
      ARBDebugOutput.glDebugMessageControlARB(4352, 4352, 4352, (IntBuffer)null, true);
      GL11.glEnable(33346);
   }

   public String severityName(int i) {
      switch (i) {
         case 37190:
            return "HIGH";
         case 37191:
            return "MEDIUM";
         case 37192:
            return "LOW";
         default:
            return this.unknownName(i);
      }
   }
}
