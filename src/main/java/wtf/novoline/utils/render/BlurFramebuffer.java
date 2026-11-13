package wtf.novoline.utils.render;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL13;

public class BlurFramebuffer {
   private final FramebufferManager framebufferManager;
   private final Framebuffer framebuffer;
   private String label;

   public void drawFullscreen(ScaledResolution scaledResolution) {
      this.bindTexture();
      RenderUtils.drawFullscreenQuad(scaledResolution);
   }

   public BlurFramebuffer(FramebufferManager framebufferManager2, Framebuffer framebuffer2, String text) {
      this.framebufferManager = framebufferManager2;
      this.framebuffer = framebuffer2;
      this.label = text;
   }

   public int bindTexture() {
      return this.bindTextureUnit(0);
   }

   public void release() {
      this.framebufferManager.release(this);
   }

   public void clear() {
      this.framebuffer.framebufferClear();
   }

   public void bindFramebuffer() {
      this.framebuffer.bindFramebuffer(true);
   }

   public String getLabel() {
      return this.label;
   }

   public void setLabel(String text) {
      this.label = text;
   }

   public Framebuffer getFramebuffer() {
      return this.framebuffer;
   }

   public int bindTextureUnit(int i) {
      if (i != 0) {
         GL13.glActiveTexture(33984 + i);
      }

      this.framebuffer.bindFramebufferTexture();
      if (i != 0) {
         GL13.glActiveTexture(33984);
      }

      return i;
   }
}
