package wtf.novoline.utils.render;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.ARBShaderObjects;

public class BlurRenderer {
   private BlurFramebuffer blurFramebuffer;
   private final ShaderProgram shaderProgram = new ShaderProgram(new ResourceLocation("novoline", "shaders/blur.fsh"));
   private final FramebufferManager framebufferManager;

   public Framebuffer getFramebuffer() {
      return this.blurFramebuffer.getFramebuffer();
   }

   public BlurRenderer(FramebufferManager framebufferManager2) {
      this.framebufferManager = framebufferManager2;
   }

   private void blurPass(Framebuffer framebuffer, ScaledResolution scaledResolution, int i, int i2, float f, int i3) {
      this.shaderProgram.use();
      ARBShaderObjects.glUniform1iARB(this.shaderProgram.getUniformLocation("u_texture"), 0);
      ARBShaderObjects.glUniform2fARB(this.shaderProgram.getUniformLocation("u_textureSize"), framebuffer.framebufferWidth, framebuffer.framebufferHeight);
      ARBShaderObjects.glUniform2fARB(this.shaderProgram.getUniformLocation("u_dir"), i == 1 ? 0.0F : 1.0F, i == 1 ? 1.0F : 0.0F);
      ARBShaderObjects.glUniform1iARB(this.shaderProgram.getUniformLocation("u_step"), i3);
      ARBShaderObjects.glUniform1fARB(this.shaderProgram.getUniformLocation("u_sigma"), f);
      ARBShaderObjects.glUniform1iARB(this.shaderProgram.getUniformLocation("u_radius"), i2);
      RenderUtils.applyBlendFunc();
      this.shaderProgram.drawFullscreenQuad(scaledResolution);
   }

   public void blur(String text, BlurFramebuffer blurFramebuffer2, ScaledResolution scaledResolution, int i, float f) {
      GlStateManager.enableTexture2D();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      BlurFramebuffer blurFramebuffer3 = this.framebufferManager.acquire(text + " horizontal blur");
      blurFramebuffer3.bindFramebuffer();
      blurFramebuffer2.bindTexture();
      this.blurPass(blurFramebuffer3.getFramebuffer(), scaledResolution, 0, i, f, 1);
      blurFramebuffer2.release();
      this.blurFramebuffer = this.framebufferManager.reuseOrAcquire(this.blurFramebuffer, text + " vertical blur");
      this.blurFramebuffer.bindFramebuffer();
      blurFramebuffer3.bindTexture();
      this.blurPass(this.blurFramebuffer.getFramebuffer(), scaledResolution, 1, i, f, 1);
      blurFramebuffer3.release();
   }

   public void blur(String text, Framebuffer framebuffer, ScaledResolution scaledResolution, int i, float f, int i2) {
      if (i < 1) {
         this.blurFramebuffer = this.framebufferManager.reuseOrAcquire(this.blurFramebuffer, text + " no blur fallback");
         this.blurFramebuffer.bindFramebuffer();
         framebuffer.bindFramebufferTexture();
         RenderUtils.drawFullscreenQuad(scaledResolution);
      }

      GlStateManager.enableTexture2D();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      BlurFramebuffer blurFramebuffer2 = this.framebufferManager.acquire(text + " horizontal blur");
      blurFramebuffer2.bindFramebuffer();
      framebuffer.bindFramebufferTexture();
      this.blurPass(blurFramebuffer2.getFramebuffer(), scaledResolution, 0, i, f, i2);
      this.blurFramebuffer = this.framebufferManager.reuseOrAcquire(this.blurFramebuffer, text + " vertical blur");
      this.blurFramebuffer.bindFramebuffer();
      blurFramebuffer2.bindTexture();
      this.blurPass(this.blurFramebuffer.getFramebuffer(), scaledResolution, 1, i, f, i2);
      blurFramebuffer2.release();
   }

   public void drawResult(ScaledResolution scaledResolution) {
      this.bindResultTexture();
      RenderUtils.drawFullscreenQuad(scaledResolution);
   }

   public void drawResultRegion(ScaledResolution scaledResolution, double d, double d2, double d3, double d4) {
      this.bindResultTexture();
      RenderUtils.drawFlippedTexturedQuad(scaledResolution, d, d2, d3, d4);
   }

   public void releaseResult() {
      if (this.blurFramebuffer != null) {
         this.blurFramebuffer.release();
      }

      this.blurFramebuffer = null;
   }

   public int bindResultTexture() {
      this.getFramebuffer().bindFramebufferTexture();
      return 0;
   }

   public boolean hasNoResult() {
      return this.blurFramebuffer == null;
   }
}
