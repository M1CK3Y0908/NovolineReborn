package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.optifine.shaders.ShadersTex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LayeredColorMaskTexture extends AbstractTexture {
   private final ResourceLocation textureLocation;
   private static final Logger LOG = LogManager.getLogger();
   private final List<String> layeredTextureNames;
   private final List<EnumDyeColor> dyeColors;

   @Override
   public void loadTexture(IResourceManager resourceManager) throws IOException {
      this.deleteGlTexture();

      BufferedImage bufferedImage;
      try {
         BufferedImage bufferedImage2 = TextureUtil.readBufferedImage(resourceManager.getResource(this.textureLocation).getInputStream());
         int type = bufferedImage2.getType();
         if (type == 0) {
            type = 6;
         }

         bufferedImage = new BufferedImage(bufferedImage2.getWidth(), bufferedImage2.getHeight(), type);
         bufferedImage.getGraphics().drawImage(bufferedImage2, 0, 0, (ImageObserver)null);

         for (int i = 0; i < 17 && i < this.layeredTextureNames.size() && i < this.dyeColors.size(); i++) {
            String text = this.layeredTextureNames.get(i);
            MapColor mapColor = this.dyeColors.get(i).getMapColor();
            if (text != null) {
               BufferedImage bufferedImage3 = TextureUtil.readBufferedImage(resourceManager.getResource(new ResourceLocation(text)).getInputStream());
               if (bufferedImage3.getWidth() == bufferedImage.getWidth() && bufferedImage3.getHeight() == bufferedImage.getHeight() && bufferedImage3.getType() == 6) {
                  for (int i2 = 0; i2 < bufferedImage3.getHeight(); i2++) {
                     for (int i3 = 0; i3 < bufferedImage3.getWidth(); i3++) {
                        int rgb = bufferedImage3.getRGB(i3, i2);
                        if ((rgb & 0xFF000000) != 0) {
                           int i4 = (rgb & 0xFF0000) << 8 & 0xFF000000;
                           int i5 = MathHelper.multiplyColor(bufferedImage2.getRGB(i3, i2), mapColor.colorValue) & 16777215;
                           bufferedImage3.setRGB(i3, i2, i4 | i5);
                        }
                     }
                  }

                  bufferedImage.getGraphics().drawImage(bufferedImage3, 0, 0, (ImageObserver)null);
               }
            }
         }
      } catch (IOException ioException) {
         LOG.error("Couldn't load layered image", ioException);
         return;
      }

      if (Config.isShaders()) {
         ShadersTex.loadSimpleTexture(this.getGlTextureId(), bufferedImage, false, false, resourceManager, this.textureLocation, this.getMultiTexID());
      } else {
         TextureUtil.uploadTextureImage(this.getGlTextureId(), bufferedImage);
      }
   }

   public LayeredColorMaskTexture(ResourceLocation textureLocationIn, List<String> layeredTextureNamesIn, List<EnumDyeColor> dyeColorsIn) {
      this.textureLocation = textureLocationIn;
      this.layeredTextureNames = layeredTextureNamesIn;
      this.dyeColors = dyeColorsIn;
   }
}
