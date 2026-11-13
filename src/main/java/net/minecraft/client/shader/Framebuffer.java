package net.minecraft.client.shader;

import java.nio.ByteBuffer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class Framebuffer {
   public boolean useDepth;
   public int framebufferTextureWidth;
   public int framebufferHeight;
   public int framebufferObject;
   public int framebufferTextureHeight;
   public int framebufferTexture;
   public int framebufferWidth;
   public int framebufferFilter;
   public int depthBuffer;
   public float[] framebufferColor;

   public void bindFramebuffer(boolean flag) {
      if (OpenGlHelper.isFramebufferEnabled()) {
         OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.framebufferObject);
         if (flag) {
            GlStateManager.viewport(0, 0, this.framebufferWidth, this.framebufferHeight);
         }
      }
   }

   public void setFramebufferColor(float f, float f2, float f3, float f4) {
      this.framebufferColor[0] = f;
      this.framebufferColor[1] = f2;
      this.framebufferColor[2] = f3;
      this.framebufferColor[3] = f4;
   }

   public void framebufferClear() {
      this.bindFramebuffer(true);
      GlStateManager.clearColor(this.framebufferColor[0], this.framebufferColor[1], this.framebufferColor[2], this.framebufferColor[3]);
      int i = 16384;
      if (this.useDepth) {
         GlStateManager.clearDepth(1.0);
         i = 16640;
      }

      GlStateManager.clear(i);
      this.unbindFramebuffer();
   }

   public void bindFramebufferTexture() {
      if (OpenGlHelper.isFramebufferEnabled()) {
         GlStateManager.bindTexture(this.framebufferTexture);
      }
   }

   public void checkFramebufferComplete() {
      int i = OpenGlHelper.glCheckFramebufferStatus(OpenGlHelper.GL_FRAMEBUFFER);
      if (i != OpenGlHelper.GL_FRAMEBUFFER_COMPLETE) {
         if (i == OpenGlHelper.GL_FB_INCOMPLETE_ATTACHMENT) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
         } else if (i == OpenGlHelper.GL_FB_INCOMPLETE_MISS_ATTACH) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
         } else if (i == OpenGlHelper.GL_FB_INCOMPLETE_DRAW_BUFFER) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
         } else if (i == OpenGlHelper.GL_FB_INCOMPLETE_READ_BUFFER) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
         } else {
            throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + i);
         }
      }
   }

   public void unbindFramebufferTexture() {
      if (OpenGlHelper.isFramebufferEnabled()) {
         GlStateManager.bindTexture(0);
      }
   }

   public void deleteFramebuffer() {
      if (OpenGlHelper.isFramebufferEnabled()) {
         this.unbindFramebufferTexture();
         this.unbindFramebuffer();
         if (this.depthBuffer > -1) {
            OpenGlHelper.glDeleteRenderbuffers(this.depthBuffer);
            this.depthBuffer = -1;
         }

         if (this.framebufferTexture > -1) {
            TextureUtil.deleteTexture(this.framebufferTexture);
            this.framebufferTexture = -1;
         }

         if (this.framebufferObject > -1) {
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, 0);
            OpenGlHelper.glDeleteFramebuffers(this.framebufferObject);
            this.framebufferObject = -1;
         }
      }
   }

   public void createBindFramebuffer(int width, int height) {
      if (!OpenGlHelper.isFramebufferEnabled()) {
         this.framebufferWidth = width;
         this.framebufferHeight = height;
      } else {
         GlStateManager.enableDepth();
         if (this.framebufferObject >= 0) {
            this.deleteFramebuffer();
         }

         this.createFramebuffer(width, height);
         this.checkFramebufferComplete();
         OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, 0);
      }
   }

   public void setFramebufferFilter(int i) {
      if (OpenGlHelper.isFramebufferEnabled()) {
         this.framebufferFilter = i;
         GlStateManager.bindTexture(this.framebufferTexture);
         GL11.glTexParameterf(3553, 10241, i);
         GL11.glTexParameterf(3553, 10240, i);
         GL11.glTexParameterf(3553, 10242, 10496.0F);
         GL11.glTexParameterf(3553, 10243, 10496.0F);
         GlStateManager.bindTexture(0);
      }
   }

   public void framebufferRender(int i, int i2) {
      this.framebufferRenderExt(i, i2, true);
   }

   public void framebufferRenderExt(int i, int i2, boolean flag) {
      if (OpenGlHelper.isFramebufferEnabled()) {
         GlStateManager.colorMask(true, true, true, false);
         GlStateManager.disableDepth();
         GlStateManager.depthMask(false);
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.ortho(0.0, i, i2, 0.0, 1000.0, 3000.0);
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         GlStateManager.translate(0.0F, 0.0F, -2000.0F);
         GlStateManager.viewport(0, 0, i, i2);
         GlStateManager.enableTexture2D();
         GlStateManager.disableLighting();
         GlStateManager.disableAlpha();
         if (flag) {
            GlStateManager.disableBlend();
            GlStateManager.enableColorMaterial();
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.bindFramebufferTexture();
         float f = i;
         float f2 = i2;
         float f3 = (float)this.framebufferWidth / this.framebufferTextureWidth;
         float f4 = (float)this.framebufferHeight / this.framebufferTextureHeight;
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldRenderer = tessellator.getWorldRenderer();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         worldRenderer.pos(0.0, f2, 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex();
         worldRenderer.pos(f, f2, 0.0).tex(f3, 0.0).color(255, 255, 255, 255).endVertex();
         worldRenderer.pos(f, 0.0, 0.0).tex(f3, f4).color(255, 255, 255, 255).endVertex();
         worldRenderer.pos(0.0, 0.0, 0.0).tex(0.0, f4).color(255, 255, 255, 255).endVertex();
         tessellator.draw();
         this.unbindFramebufferTexture();
         GlStateManager.depthMask(true);
         GlStateManager.colorMask(true, true, true, true);
      }
   }

   public void unbindFramebuffer() {
      if (OpenGlHelper.isFramebufferEnabled()) {
         OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, 0);
      }
   }

   public void createFramebuffer(int width, int height) {
      this.framebufferWidth = width;
      this.framebufferHeight = height;
      this.framebufferTextureWidth = width;
      this.framebufferTextureHeight = height;
      if (!OpenGlHelper.isFramebufferEnabled()) {
         this.framebufferClear();
      } else {
         this.framebufferObject = OpenGlHelper.glGenFramebuffers();
         this.framebufferTexture = TextureUtil.glGenTextures();
         if (this.useDepth) {
            this.depthBuffer = OpenGlHelper.glGenRenderbuffers();
         }

         this.setFramebufferFilter(9728);
         GlStateManager.bindTexture(this.framebufferTexture);
         GL11.glTexImage2D(3553, 0, 32856, this.framebufferTextureWidth, this.framebufferTextureHeight, 0, 6408, 5121, (ByteBuffer)null);
         OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.framebufferObject);
         OpenGlHelper.handleInt4(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, this.framebufferTexture);
         if (this.useDepth) {
            OpenGlHelper.glBindRenderbuffer(OpenGlHelper.GL_RENDERBUFFER, this.depthBuffer);
            OpenGlHelper.handleInt6(OpenGlHelper.GL_RENDERBUFFER, this.framebufferTextureWidth, this.framebufferTextureHeight);
            OpenGlHelper.glRenderbufferStorage(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_DEPTH_ATTACHMENT, OpenGlHelper.GL_RENDERBUFFER, this.depthBuffer);
         }

         this.framebufferClear();
         this.unbindFramebufferTexture();
      }
   }

   public Framebuffer(int i, int i2, boolean flag) {
      this.useDepth = flag;
      this.framebufferObject = -1;
      this.framebufferTexture = -1;
      this.depthBuffer = -1;
      this.framebufferColor = new float[4];
      this.framebufferColor[0] = 1.0F;
      this.framebufferColor[1] = 1.0F;
      this.framebufferColor[2] = 1.0F;
      this.framebufferColor[3] = 0.0F;
      this.createBindFramebuffer(i, i2);
   }
}
