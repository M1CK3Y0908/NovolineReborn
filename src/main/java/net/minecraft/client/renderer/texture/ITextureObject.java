package net.minecraft.client.renderer.texture;

import java.io.IOException;
import net.minecraft.client.resources.IResourceManager;
import net.optifine.shaders.MultiTexID;

public interface ITextureObject {
   void restoreLastBlurMipmap();

   MultiTexID getMultiTexID();

   int getGlTextureId();

   void loadTexture(IResourceManager iResourceManager) throws IOException;

   void noop(boolean flag);
}
