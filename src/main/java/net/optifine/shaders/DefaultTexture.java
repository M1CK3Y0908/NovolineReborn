package net.optifine.shaders;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.IResourceManager;

public class DefaultTexture extends AbstractTexture {
   @Override
   public void loadTexture(IResourceManager resourceManager) {
      int[] intArray = ShadersTex.getArray();
      ShadersTex.setupTexture(this.getMultiTexID(), intArray, 1, 1, false, false);
   }
}
