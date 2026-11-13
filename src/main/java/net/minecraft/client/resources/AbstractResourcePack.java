package net.minecraft.client.resources;

import com.google.common.base.Charsets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractResourcePack implements IResourcePack {
   private static final Logger resourceLog = LogManager.getLogger();
   public final File resourcePackFile;

   @Override
   public InputStream getInputStream(ResourceLocation location) throws IOException {
      return this.getInputStreamByName(locationToName(location));
   }

   private static String locationToName(ResourceLocation location) {
      return String.format("%s/%s/%s", "assets", location.getResourceDomain(), location.getResourcePath());
   }

   @Override
   public String getPackName() {
      return this.resourcePackFile.getName();
   }

   protected abstract InputStream getInputStreamByName(String text) throws IOException;

   @Override
   public BufferedImage getPackImage() throws IOException {
      return TextureUtil.readBufferedImage(this.getInputStreamByName("pack.png"));
   }

   protected abstract boolean hasResourceName(String text);

   @Override
   public <T extends IMetadataSection> T getPackMetadata(IMetadataSerializer metadataSerializer, String metadataSectionName) throws IOException {
      return readMetadata(metadataSerializer, this.getInputStreamByName("pack.mcmeta"), metadataSectionName);
   }

   public AbstractResourcePack(File resourcePackFileIn) {
      this.resourcePackFile = resourcePackFileIn;
   }

   protected void logNameNotLowercase(String name) {
      resourceLog.warn("ResourcePack: ignored non-lowercase namespace: {} in {}", name, this.resourcePackFile);
   }

   protected static String getRelativeName(File file, File file2) {
      return file.toURI().relativize(file2.toURI()).getPath();
   }

   static <T extends IMetadataSection> T readMetadata(IMetadataSerializer iMetadataSerializer, InputStream inputStream, String text) {
      BufferedReader bufferedReader = null;

      JsonObject jsonObject;
      try {
         bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charsets.UTF_8));
         jsonObject = new JsonParser().parse(bufferedReader).getAsJsonObject();
      } catch (RuntimeException runtimeException) {
         throw new JsonParseException(runtimeException);
      } finally {
         IOUtils.closeQuietly(bufferedReader);
      }

      return iMetadataSerializer.parseMetadataSection(text, jsonObject);
   }

   @Override
   public boolean resourceExists(ResourceLocation location) {
      return this.hasResourceName(locationToName(location));
   }
}
