package net.minecraft.client.renderer.texture;

import net.minecraft.client.renderer.GlStateManager;
import net.optifine.shaders.MultiTexID;
import net.optifine.shaders.ShadersTex;
import org.lwjgl.opengl.GL11;

public abstract class AbstractTexture implements ITextureObject {
   protected boolean mipmapLast;
   public MultiTexID multiTex;
   protected int glTextureId = -1;
   protected boolean blurLast;
   protected boolean blur;
   protected boolean mipmap;

   @Override
   public void restoreLastBlurMipmap() {
      this.setBlurMipmapDirect(this.blurLast, this.mipmapLast);
   }

   public void deleteGlTexture() {
      ShadersTex.deleteTextures(this, this.glTextureId);
      if (this.glTextureId != -1) {
         TextureUtil.deleteTexture(this.glTextureId);
         this.glTextureId = -1;
      }
   }

   @Override
   public int getGlTextureId() {
      if (this.glTextureId == -1) {
         this.glTextureId = TextureUtil.glGenTextures();
      }

      return this.glTextureId;
   }

   @Override
   public MultiTexID getMultiTexID() {
      return ShadersTex.getMultiTexID(this);
   }

   public void setBlurMipmapDirect(boolean flag, boolean flag2) {
      this.blur = flag;
      this.mipmap = flag2;
      int i;
      int i2;
      if (flag) {
         i = flag2 ? 9987 : 9729;
         i2 = 9729;
      } else {
         i = flag2 ? 9986 : 9728;
         i2 = 9728;
      }

      GlStateManager.bindTexture(this.getGlTextureId());
      GL11.glTexParameteri(3553, 10241, i);
      GL11.glTexParameteri(3553, 10240, i2);
   }

   @Override
   public void noop(boolean flag) {
      this.blurLast = this.blur;
      this.mipmapLast = this.mipmap;
      this.setBlurMipmapDirect(false, flag);
   }
}
