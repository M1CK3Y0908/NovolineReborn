package net.minecraft.client.resources;

import com.google.common.collect.ImmutableSet;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import net.optifine.reflect.ReflectorForge;

public class DefaultResourcePack implements IResourcePack {
   public static final Set<String> defaultResourceDomains = ImmutableSet.of("minecraft", "realms", "novoline");
   private final Map<String, File> mapAssets;

   @Override
   public Set<String> getResourceDomains() {
      return defaultResourceDomains;
   }

   public DefaultResourcePack(Map<String, File> mapAssetsIn) {
      this.mapAssets = mapAssetsIn;
   }

   @Override
   public InputStream getInputStream(ResourceLocation location) throws IOException {
      InputStream inputStream = this.getResourceStream(location);
      if (inputStream != null) {
         return inputStream;
      } else {
         InputStream inputStream2 = this.getInputStreamAssets(location);
         if (inputStream2 != null) {
            return inputStream2;
         } else {
            throw new FileNotFoundException(location.getResourcePath());
         }
      }
   }

   @Override
   public String getPackName() {
      return "Default";
   }

   private InputStream getResourceStream(ResourceLocation location) {
      String text = "/assets/" + location.getResourceDomain() + "/" + location.getResourcePath();
      InputStream inputStream3 = ReflectorForge.createInputStream(text);
      return inputStream3 != null ? inputStream3 : DefaultResourcePack.class.getResourceAsStream(text);
   }

   @Override
   public <T extends IMetadataSection> T getPackMetadata(IMetadataSerializer metadataSerializer, String metadataSectionName) throws IOException {
      try {
         FileInputStream fileInputStream = new FileInputStream(this.mapAssets.get("pack.mcmeta"));
         return AbstractResourcePack.readMetadata(metadataSerializer, fileInputStream, metadataSectionName);
      } catch (RuntimeException runtimeException) {
         return null;
      } catch (FileNotFoundException fileNotFoundException) {
         return null;
      }
   }

   @Override
   public boolean resourceExists(ResourceLocation location) {
      return this.getResourceStream(location) != null || this.mapAssets.containsKey(location.toString());
   }

   public InputStream getInputStreamAssets(ResourceLocation location) throws IOException {
      File file = this.mapAssets.get(location.toString());
      return file != null && file.isFile() ? new FileInputStream(file) : null;
   }

   @Override
   public BufferedImage getPackImage() throws IOException {
      return TextureUtil.readBufferedImage(DefaultResourcePack.class.getResourceAsStream("/" + new ResourceLocation("pack.png").getResourcePath()));
   }
}
