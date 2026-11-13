package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.EmissiveTextures;
import net.optifine.shaders.ShadersTex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleTexture extends AbstractTexture {
   public ResourceLocation locationEmissive;
   public boolean isEmissive;
   private static final Logger logger = LogManager.getLogger();
   protected final ResourceLocation textureLocation;

   public SimpleTexture(ResourceLocation textureResourceLocation) {
      this.textureLocation = textureResourceLocation;
   }

   @Override
   public void loadTexture(IResourceManager resourceManager) throws IOException {
      this.deleteGlTexture();
      InputStream inputStream = null;

      try {
         IResource resource = resourceManager.getResource(this.textureLocation);
         inputStream = resource.getInputStream();
         BufferedImage bufferedImage = TextureUtil.readBufferedImage(inputStream);
         boolean flag = false;
         boolean flag2 = false;
         if (resource.hasMetadata()) {
            try {
               TextureMetadataSection metadata2 = resource.getMetadata("texture");
               if (metadata2 != null) {
                  flag = metadata2.getTextureBlur();
                  flag2 = metadata2.getTextureClamp();
               }
            } catch (RuntimeException runtimeException) {
               logger.warn("Failed reading metadata of: " + this.textureLocation, runtimeException);
            }
         }

         if (Config.isShaders()) {
            ShadersTex.loadSimpleTexture(this.getGlTextureId(), bufferedImage, flag, flag2, resourceManager, this.textureLocation, this.getMultiTexID());
         } else {
            TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), bufferedImage, flag, flag2);
         }

         if (EmissiveTextures.isActive()) {
            EmissiveTextures.loadTexture(this.textureLocation, this);
         }
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
   }
}
