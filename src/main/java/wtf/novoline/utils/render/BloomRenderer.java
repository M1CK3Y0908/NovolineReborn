package wtf.novoline.utils.render;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.ARBShaderObjects;

public class BloomRenderer {
   private boolean enabled = true;
   private final ShaderProgram shaderProgram = new ShaderProgram(new ResourceLocation("novoline", "shaders/bloom.fsh"));
   private final List<Runnable> pendingDraws = new ArrayList<>();
   private final FramebufferManager framebufferManager;
   private final BlurRenderer blurRenderer;

   public boolean isEnabled() {
      return this.enabled;
   }

   public BloomRenderer(FramebufferManager framebufferManager2) {
      this.framebufferManager = framebufferManager2;
      this.blurRenderer = new BlurRenderer(framebufferManager2);
   }

   public void setEnabled(boolean flag) {
      this.enabled = flag;
   }

   public void composeBloom(
      Framebuffer framebuffer, BlurFramebuffer blurFramebuffer, ScaledResolution scaledResolution, String text, int i, float f, float f2, float f3, float f4
   ) {
      GlStateManager.alphaFunc(516, 0.001F);
      GlStateManager.enableBlend();
      RenderUtils.applyBlendFunc();
      GlStateManager.enableTexture2D();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      BlurFramebuffer blurFramebuffer2 = this.framebufferManager.acquire(text + " bloom base", 0);
      blurFramebuffer2.bindFramebuffer();
      blurFramebuffer.bindTexture();
      RenderUtils.drawFullscreenQuad(scaledResolution);
      this.blurRenderer.blur(text + " bloom ", blurFramebuffer2, scaledResolution, i, f);
      framebuffer.bindFramebuffer(true);
      GlStateManager.enableBlend();
      RenderUtils.applyBlendFunc();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.shaderProgram.use();
      ARBShaderObjects.glUniform1iARB(this.shaderProgram.getUniformLocation("u_bloom_texture"), this.blurRenderer.bindResultTexture());
      ARBShaderObjects.glUniform1iARB(this.shaderProgram.getUniformLocation("u_source_texture"), blurFramebuffer.bindTextureUnit(8));
      ARBShaderObjects.glUniform1fARB(this.shaderProgram.getUniformLocation("u_source_alpha"), f2);
      ARBShaderObjects.glUniform1fARB(this.shaderProgram.getUniformLocation("u_exposure"), f3);
      ARBShaderObjects.glUniform1fARB(this.shaderProgram.getUniformLocation("u_gamma"), f4);
      this.shaderProgram.drawFullscreenQuad(scaledResolution);
      GlStateManager.alphaFunc(516, 0.1F);
      blurFramebuffer.release();
      this.blurRenderer.releaseResult();
   }

   public void submit(Runnable runnable) {
      if (this.enabled) {
         this.pendingDraws.add(runnable);
      } else {
         runnable.run();
      }
   }

   public void applyBloom(Framebuffer framebuffer, ScaledResolution scaledResolution, String text, int i, int i2, float f, float f2, float f3, float f4) {
      if (this.enabled) {
         BlurFramebuffer blurFramebuffer = this.framebufferManager.acquire(text + " bloom target", RenderUtils.withAlpha(i, 0));
         blurFramebuffer.bindFramebuffer();
         this.pendingDraws.forEach(Runnable::run);
         this.pendingDraws.clear();
         this.composeBloom(framebuffer, blurFramebuffer, scaledResolution, text, i2, f, f2, f3, f4);
      }
   }
}
