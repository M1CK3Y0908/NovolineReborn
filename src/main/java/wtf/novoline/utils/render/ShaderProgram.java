package wtf.novoline.utils.render;

import java.nio.charset.StandardCharsets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import wtf.novoline.support.SneakyThrow;

public class ShaderProgram {
   private final int programId = ARBShaderObjects.glCreateProgramObjectARB();
   private static final String DEFAULT_VERTEX_SHADER = "#version 120\n\nvoid main() {\n    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n    gl_TexCoord[0] = gl_MultiTexCoord0;\n}";
   private final String fragmentSource;
   private final String vertexSource;

   public void drawClipSpaceQuad() {
      GL11.glBegin(7);
      GL11.glVertex2f(-1.0F, -1.0F);
      GL11.glVertex2f(-1.0F, 1.0F);
      GL11.glVertex2f(1.0F, 1.0F);
      GL11.glVertex2f(1.0F, -1.0F);
      GL11.glEnd();
      ARBShaderObjects.glUseProgramObjectARB(0);
   }

   public int getUniformLocation(String text) {
      return ARBShaderObjects.glGetUniformLocationARB(this.programId, text);
   }

   public ShaderProgram(String text, String text2) {
      this.fragmentSource = text;
      this.vertexSource = text2;
      this.compileAndLink();
   }

   public ShaderProgram(ResourceLocation resourceLocation, ResourceLocation resourceLocation2) {
      this(readSource(resourceLocation), readSource(resourceLocation2));
   }

   public int getProgramId() {
      return this.programId;
   }

   public void drawQuad(float f, float f2) {
      GlStateManager.enableBlend();
      GL11.glBegin(7);
      GL11.glTexCoord2d(0.0, 1.0);
      GL11.glVertex2d(0.0, 0.0);
      GL11.glTexCoord2d(0.0, 0.0);
      GL11.glVertex2d(0.0, f2);
      GL11.glTexCoord2d(1.0, 0.0);
      GL11.glVertex2d(f, f2);
      GL11.glTexCoord2d(1.0, 1.0);
      GL11.glVertex2d(f, 0.0);
      GL11.glEnd();
      GlStateManager.disableBlend();
      ARBShaderObjects.glUseProgramObjectARB(0);
   }

   public void use() {
      ARBShaderObjects.glUseProgramObjectARB(this.programId);
   }

   private static String readSource(ResourceLocation resourceLocation) {
      try {
      return IOUtils.toString(Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream(), StandardCharsets.UTF_8);
   } catch (Throwable sneakyThrowable) {
         throw SneakyThrow.sneak(sneakyThrowable);
      }
   }

   public ShaderProgram(ResourceLocation resourceLocation) {
      this(readSource(resourceLocation));
   }

   public String getVertexSource() {
      return this.vertexSource;
   }

   private void compileAndLink() {
      int i = ARBShaderObjects.glCreateShaderObjectARB(35633);
      ARBShaderObjects.glShaderSourceARB(i, this.vertexSource);
      ARBShaderObjects.glCompileShaderARB(i);
      ARBShaderObjects.glAttachObjectARB(this.programId, i);
      int i2 = ARBShaderObjects.glCreateShaderObjectARB(35632);
      ARBShaderObjects.glShaderSourceARB(i2, this.fragmentSource);
      ARBShaderObjects.glValidateProgramARB(this.programId);
      ARBShaderObjects.glCompileShaderARB(i2);
      ARBShaderObjects.glAttachObjectARB(this.programId, i2);
      ARBShaderObjects.glLinkProgramARB(this.programId);
   }

   public ShaderProgram(String text) {
      this(text, "#version 120\n\nvoid main() {\n    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n    gl_TexCoord[0] = gl_MultiTexCoord0;\n}");
   }

   public String getFragmentSource() {
      return this.fragmentSource;
   }

   public void drawFullscreenQuad(ScaledResolution scaledResolution) {
      this.drawQuad(scaledResolution.getFloat(), scaledResolution.getFloat2());
   }
}
