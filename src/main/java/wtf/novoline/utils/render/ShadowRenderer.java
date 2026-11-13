package wtf.novoline.utils.render;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.ARBShaderObjects;

public class ShadowRenderer {
   private final List<Runnable> pendingDraws = new ArrayList<>();
   private final ShaderProgram shaderProgram = new ShaderProgram(new ResourceLocation("novoline", "shaders/shadow.fsh"));
   private final FramebufferManager framebufferManager;

   public void submit(Runnable runnable) {
      this.pendingDraws.add(runnable);
   }

   public void render(Framebuffer framebuffer, ScaledResolution scaledResolution, int i, float f, float f2, float f3, boolean flag) {
      if (!this.pendingDraws.isEmpty()) {
         float[] fArray = RenderUtils.unpackColorNormalized(i);
         int i2 = RenderUtils.withAlpha(i, 0);
         GlStateManager.alphaFunc(516, 0.001F);
         GlStateManager.enableBlend();
         RenderUtils.applyBlendFunc();
         BlurFramebuffer blurFramebuffer = this.framebufferManager.acquire("screen-shadow-target", i2);
         blurFramebuffer.bindFramebuffer();

         for (Runnable runnable : this.pendingDraws) {
            runnable.run();
         }

         this.pendingDraws.clear();
         BlurFramebuffer blurFramebuffer2 = this.framebufferManager.acquire("screen-shadow-h-blur", i2);
         blurFramebuffer2.bindFramebuffer();
         blurFramebuffer.bindTexture();
         this.blurPass(scaledResolution, true, fArray, f, f2, f3);
         BlurFramebuffer blurFramebuffer3 = this.framebufferManager.acquire("screen-shadow-v-blur", i2);
         blurFramebuffer3.bindFramebuffer();
         blurFramebuffer2.bindTexture();
         this.blurPass(scaledResolution, false, fArray, f, f2, f3);
         framebuffer.bindFramebuffer(true);
         RenderUtils.resetColor();
         GlStateManager.enableBlend();
         RenderUtils.applyBlendFunc();
         blurFramebuffer3.bindTexture();
         RenderUtils.drawFullscreenQuad(scaledResolution);
         if (flag) {
            blurFramebuffer.bindTexture();
            RenderUtils.drawFullscreenQuad(scaledResolution);
         }

         GlStateManager.alphaFunc(516, 0.1F);
         blurFramebuffer.release();
         blurFramebuffer2.release();
         blurFramebuffer3.release();
      }
   }

   public ShadowRenderer(FramebufferManager framebufferManager2) {
      this.framebufferManager = framebufferManager2;
   }

   private void blurPass(ScaledResolution scaledResolution, boolean flag, float[] fArray, float f, float f2, float f3) {
      this.shaderProgram.use();
      ARBShaderObjects.glUniform1iARB(this.shaderProgram.getUniformLocation("u_texture"), 0);
      ARBShaderObjects.glUniform2fARB(
         this.shaderProgram.getUniformLocation("u_texelSize"), 1.0F / Minecraft.getMinecraft().displayWidth, 1.0F / Minecraft.getMinecraft().displayHeight
      );
      ARBShaderObjects.glUniform2fARB(this.shaderProgram.getUniformLocation("u_direction"), flag ? 1.0F : 0.0F, flag ? 0.0F : 1.0F);
      ARBShaderObjects.glUniform1fARB(this.shaderProgram.getUniformLocation("u_radius"), f);
      ARBShaderObjects.glUniform1fARB(this.shaderProgram.getUniformLocation("u_sigma"), f2);
      ARBShaderObjects.glUniform1fARB(this.shaderProgram.getUniformLocation("u_gamma"), f3);
      ARBShaderObjects.glUniform3fARB(this.shaderProgram.getUniformLocation("u_color"), fArray[0], fArray[1], fArray[2]);
      this.shaderProgram.drawFullscreenQuad(scaledResolution);
   }
}
