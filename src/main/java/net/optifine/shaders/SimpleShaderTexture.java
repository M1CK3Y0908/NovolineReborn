package net.optifine.shaders;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.data.FontMetadataSection;
import net.minecraft.client.resources.data.FontMetadataSectionSerializer;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.client.resources.data.LanguageMetadataSectionSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.resources.data.PackMetadataSectionSerializer;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSectionSerializer;
import org.apache.commons.io.IOUtils;

public class SimpleShaderTexture extends AbstractTexture {
   private String texturePath;
   private static final IMetadataSerializer METADATA_SERIALIZER = makeMetadataSerializer();

   public SimpleShaderTexture(String texturePath) {
      this.texturePath = texturePath;
   }

   public static TextureMetadataSection loadTextureMetadataSection(String texturePath, TextureMetadataSection def) {
      String text = texturePath + ".mcmeta";
      InputStream shaderPackResourceStream = Shaders.getShaderPackResourceStream(text);
      if (shaderPackResourceStream != null) {
         IMetadataSerializer iMetadataSerializer = METADATA_SERIALIZER;
         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(shaderPackResourceStream));

         TextureMetadataSection textureMetadataSection;
         try {
            JsonObject asJsonObject = new JsonParser().parse(bufferedReader).getAsJsonObject();
            TextureMetadataSection textureMetadataSection2 = iMetadataSerializer.parseMetadataSection("texture", asJsonObject);
            if (textureMetadataSection2 == null) {
               return def;
            }

            textureMetadataSection = textureMetadataSection2;
         } catch (RuntimeException runtimeException) {
            SMCLog.warning("Error reading metadata: " + text);
            SMCLog.warning(runtimeException.getClass().getName() + ": " + runtimeException.getMessage());
            return def;
         } finally {
            IOUtils.closeQuietly(bufferedReader);
            IOUtils.closeQuietly(shaderPackResourceStream);
         }

         return textureMetadataSection;
      } else {
         return def;
      }
   }

   @Override
   public void loadTexture(IResourceManager resourceManager) throws IOException {
      this.deleteGlTexture();
      InputStream shaderPackResourceStream = Shaders.getShaderPackResourceStream(this.texturePath);
      if (shaderPackResourceStream == null) {
         throw new FileNotFoundException("Shader texture not found: " + this.texturePath);
      }

      try {
         BufferedImage bufferedImage = TextureUtil.readBufferedImage(shaderPackResourceStream);
         TextureMetadataSection textureMetadataSection = loadTextureMetadataSection(this.texturePath, new TextureMetadataSection(false, false, new ArrayList<>()));
         TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), bufferedImage, textureMetadataSection.getTextureBlur(), textureMetadataSection.getTextureClamp());
      } finally {
         IOUtils.closeQuietly(shaderPackResourceStream);
      }
   }

   private static IMetadataSerializer makeMetadataSerializer() {
      IMetadataSerializer iMetadataSerializer = new IMetadataSerializer();
      iMetadataSerializer.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
      iMetadataSerializer.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
      iMetadataSerializer.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
      iMetadataSerializer.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
      iMetadataSerializer.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
      return iMetadataSerializer;
   }
}
