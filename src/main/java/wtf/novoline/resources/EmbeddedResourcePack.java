package wtf.novoline.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public class EmbeddedResourcePack extends AbstractResourcePack implements Closeable {
   public static final Splitter splitter = Splitter.on('/').omitEmptyStrings().limit(3);
   private final Map<String, byte[]> entries = new LinkedHashMap<>();

   public EmbeddedResourcePack(ResourceLocation resourceLocation) {
      super(null);
      Minecraft minecraft2 = Minecraft.getMinecraft();

      try {
         IResource resource = minecraft2.getResourceManager().getResource(resourceLocation);
         ZipInputStream zipInputStream = new ZipInputStream(resource.getInputStream());

         ZipEntry zipEntry;
         while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            this.entries.put(zipEntry.getName(), IOUtils.toByteArray(zipInputStream));
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   @Override
   public void close() throws IOException {
      this.entries.clear();
   }

   @Override
   public IMetadataSection getPackMetadata(IMetadataSerializer iMetadataSerializer, String text) throws IOException {
      return createIMetadataSection(iMetadataSerializer, this.getInputStreamByName("pack.mcmeta"), text);
   }

   @Override
   public boolean resourceExists(ResourceLocation resourceLocation) {
      return this.hasResourceName(getEntryName(resourceLocation));
   }

   @Override
   public BufferedImage getPackImage() throws IOException {
      return TextureUtil.readBufferedImage(this.getInputStreamByName("pack.png"));
   }

   static IMetadataSection createIMetadataSection(IMetadataSerializer iMetadataSerializer, InputStream inputStream, String text) {
      BufferedReader bufferedReader = null;

      JsonObject jsonObject;
      try {
         bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
         jsonObject = new JsonParser().parse(bufferedReader).getAsJsonObject();
      } catch (RuntimeException runtimeException) {
         throw new JsonParseException(runtimeException);
      } finally {
         IOUtils.closeQuietly(bufferedReader);
      }

      return iMetadataSerializer.parseMetadataSection(text, jsonObject);
   }

   @Override
   public String getPackName() {
      return "KOCK";
   }

   @Override
   public Set getResourceDomains() {
      HashSet hashSet = Sets.newHashSet();

      for (String text : this.entries.keySet()) {
         if (text.startsWith("assets/")) {
            ArrayList arrayList = Lists.newArrayList(splitter.split(text));
            if (arrayList.size() > 1) {
               String text2 = (String)arrayList.get(1);
               if (text2.equals(text2.toLowerCase(Locale.ROOT))) {
                  hashSet.add(text2);
               } else {
                  this.logNameNotLowercase(text2);
               }
            }
         }
      }

      return hashSet;
   }

   private static String getEntryName(ResourceLocation resourceLocation) {
      return String.format("%s/%s/%s", "assets", resourceLocation.getResourceDomain(), resourceLocation.getResourcePath());
   }

   @Override
   public InputStream getInputStream(ResourceLocation resourceLocation) throws IOException {
      return this.getInputStreamByName(getEntryName(resourceLocation));
   }

   @Override
   protected boolean hasResourceName(String text) {
      return this.entries.containsKey(text);
   }

   @Override
   protected InputStream getInputStreamByName(String text) throws IOException {
      return this.hasResourceName(text) ? new ByteArrayInputStream(this.entries.get(text)) : null;
   }
}
