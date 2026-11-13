package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.shaders.ShadersTex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LayeredTexture extends AbstractTexture {
   public final List<String> layeredTextureNames;
   private static final Logger logger = LogManager.getLogger();
   private ResourceLocation textureLocation;

   @Override
   public void loadTexture(IResourceManager resourceManager) throws IOException {
      this.deleteGlTexture();
      BufferedImage bufferedImage = null;

      try {
         for (String text : this.layeredTextureNames) {
            if (text != null) {
               BufferedImage bufferedImage2 = TextureUtil.readBufferedImage(resourceManager.getResource(new ResourceLocation(text)).getInputStream());
               if (bufferedImage == null) {
                  bufferedImage = new BufferedImage(bufferedImage2.getWidth(), bufferedImage2.getHeight(), 2);
               }

               bufferedImage.getGraphics().drawImage(bufferedImage2, 0, 0, (ImageObserver)null);
            }
         }
      } catch (IOException ioException) {
         logger.error("Couldn't load layered image", ioException);
         return;
      }

      if (Config.isShaders()) {
         ShadersTex.loadSimpleTexture(this.getGlTextureId(), bufferedImage, false, false, resourceManager, this.textureLocation, this.getMultiTexID());
      } else {
         TextureUtil.uploadTextureImage(this.getGlTextureId(), bufferedImage);
      }
   }

   public LayeredTexture(String... textureNames) {
      this.layeredTextureNames = Lists.newArrayList(textureNames);
      if (textureNames.length > 0 && textureNames[0] != null) {
         this.textureLocation = new ResourceLocation(textureNames[0]);
      }
   }
}
