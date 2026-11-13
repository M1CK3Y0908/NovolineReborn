package wtf.novoline.gui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.shaders.ShadersTex;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BackgroundManager {
   private static final Logger logger = LogManager.getLogger(BackgroundManager.class);
   private static final ResourceLocation BUNDLE_LOCATION = new ResourceLocation("novoline", "anime_pictures.pngs");
   private boolean flag = false;
   private final Map<String, byte[]> bundleEntries;
   private final LinkedHashMap<String, BackgroundManager.Entry> linkedHashMap;
   private BackgroundManager.Entry entry;
   private Timer timer;

   public void loadTexture(BackgroundManager.Entry entry4) {
      ResourceLocation resourceLocation = new ResourceLocation("novoline", "anime-pictures/" + entry4.getFileName());
      BackgroundManager.AbstractTextureChild abstractTextureChild = new BackgroundManager.AbstractTextureChild(entry4.getEntryBytes(), resourceLocation);
      TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
      if (textureManager.getTexture(resourceLocation) == null) {
         textureManager.loadTexture(resourceLocation, abstractTextureChild);
      }

      textureManager.bindTexture(resourceLocation);
   }

   public LinkedHashMap<String, BackgroundManager.Entry> getLinkedHashMap() {
      return this.linkedHashMap;
   }

   public boolean isFlag() {
      return this.flag;
   }

   private BackgroundManager.Record parseColors(String text) {
      List list = Arrays.stream(text.split(", ")).map(Color::decode).collect(Collectors.toList());
      return new BackgroundManager.Record((Color)list.get(0), (Color)list.get(1), (Color)list.get(2));
   }

   public BackgroundManager.Entry getCurrentEntry() {
      return this.entry;
   }

   private Map readZipEntries(InputStream inputStream) throws IOException {
      HashMap hashMap = new HashMap();
      ZipInputStream zipInputStream = new ZipInputStream(inputStream);

      ZipEntry zipEntry;
      while ((zipEntry = zipInputStream.getNextEntry()) != null) {
         byte[] bArray = IOUtils.toByteArray(zipInputStream);
         hashMap.put(zipEntry.getName(), bArray);
         zipInputStream.closeEntry();
      }

      return hashMap;
   }

   private BackgroundManager.Metadata readMetadata(Properties properties) {
      return BackgroundManager.Metadata.builder()
         .alias(properties.getProperty("alias"))
         .colors(this.parseColors(properties.getProperty("colors")))
         .width(Integer.parseInt(properties.getProperty("width")))
         .height(Integer.parseInt(properties.getProperty("height")))
         .getMetadata();
   }

   public BackgroundManager.Entry byAlias(String text) {
      return this.linkedHashMap.values().stream().filter(entry2 -> entry2.getMetadata().getAlias().equals(text)).reduce((entry2, entry3) -> {
         throw new IllegalStateException("Multiple choices!");
      }).orElse(null);
   }

   public BackgroundManager.Entry getEntryAt(int i) {
      int i2 = 0;

      for (BackgroundManager.Entry entry4 : this.linkedHashMap.values()) {
         if (i2 == i) {
            return entry4;
         }

         i2++;
      }

      return null;
   }

   public BackgroundManager() throws IOException {
      logger.debug("Loading resource bundle");
      IResource resource2 = Minecraft.getMinecraft().getResourceManager().getResource(BUNDLE_LOCATION);
      this.bundleEntries = this.readZipEntries(resource2.getInputStream());
      logger.debug("Loaded {} entries", this.bundleEntries.size());
      this.linkedHashMap = new LinkedHashMap<>();
      this.bundleEntries.forEach((string, byteArray2) -> {
         if (!string.endsWith(".metadata")) {
            logger.debug("Loading metadata for {}", string);
            Properties properties = new Properties();
            byte[] bArray = this.bundleEntries.get(string + ".metadata");

            try {
               String text = new String(bArray, StandardCharsets.UTF_8);
               properties.load(new StringReader(text));
            } catch (IOException ioException) {
               logger.error("Failed to load metadata for {}!", string);
               logger.error(ioException);
               return;
            }

            this.linkedHashMap.put(string, BackgroundManager.Entry.createEntry(string, byteArray2, this.readMetadata(properties)));
         }
      });
      this.timer = new Timer();
   }

   public static final class AbstractTextureChild extends AbstractTexture {
      private final byte[] byteArray;
      private final ResourceLocation location;

      public AbstractTextureChild(byte[] bArray, ResourceLocation resourceLocation) {
         this.byteArray = bArray;
         this.location = resourceLocation;
      }

      @Override
      public void loadTexture(IResourceManager iResourceManager) throws IOException {
         this.deleteGlTexture();

         try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.byteArray)) {
            BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
            if (Config.isShaders()) {
               ShadersTex.loadSimpleTexture(this.getGlTextureId(), bufferedImage, false, false, iResourceManager, this.location, this.getMultiTexID());
            } else {
               TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), bufferedImage, false, false);
            }
         }
      }
   }

   public static final class Entry {
      private final String fileName;
      private final byte[] entryBytes;
      private final BackgroundManager.Metadata metadata;

      @Override
      public String toString() {
         return "AnimePictureStore.Entry(fileName="
            + this.getFileName()
            + ", entryBytes="
            + Arrays.toString(this.getEntryBytes())
            + ", metadata="
            + this.getMetadata()
            + ")";
      }

      public String getFileName() {
         return this.fileName;
      }

      @Override
      public boolean equals(Object object) {
         if (object == this) {
            return true;
         } else if (!(object instanceof BackgroundManager.Entry entry4)) {
            return false;
         } else {
            String fileName2 = this.getFileName();
            String fileName3 = entry4.getFileName();
            if (fileName2 == null ? fileName3 == null : fileName2.equals(fileName3)) {
               if (!Arrays.equals(this.getEntryBytes(), entry4.getEntryBytes())) {
                  return false;
               }

               BackgroundManager.Metadata metadata2 = this.getMetadata();
               BackgroundManager.Metadata metadata3 = entry4.getMetadata();
               return metadata2 == null ? metadata3 == null : metadata2.equals(metadata3);
            } else {
               return false;
            }
         }
      }

      public BackgroundManager.Metadata getMetadata() {
         return this.metadata;
      }

      @Override
      public int hashCode() {
         String fileName2 = this.getFileName();
         int i = 59 + (fileName2 == null ? 43 : fileName2.hashCode());
         i = i * 59 + Arrays.hashCode(this.getEntryBytes());
         BackgroundManager.Metadata metadata2 = this.getMetadata();
         return i * 59 + (metadata2 == null ? 43 : metadata2.hashCode());
      }

      public byte[] getEntryBytes() {
         return this.entryBytes;
      }

      public static BackgroundManager.Entry createEntry(String text, byte[] bArray, BackgroundManager.Metadata metadata2) {
         return new BackgroundManager.Entry(text, bArray, metadata2);
      }

      private Entry(String text, byte[] bArray, BackgroundManager.Metadata metadata2) {
         this.fileName = text;
         this.entryBytes = bArray;
         this.metadata = metadata2;
      }
   }

   public static final class Metadata {
      private final String alias;
      private final BackgroundManager.Record colors;
      private final int width;
      private final int height;

      Metadata(String text, BackgroundManager.Record recordValue, int i, int i2) {
         this.alias = text;
         this.colors = recordValue;
         this.width = i;
         this.height = i2;
      }

      @Override
      public boolean equals(Object object) {
         if (object == this) {
            return true;
         } else if (!(object instanceof BackgroundManager.Metadata metadata2)) {
            return false;
         } else if (this.getWidth() != metadata2.getWidth()) {
            return false;
         } else if (this.getHeight() != metadata2.getHeight()) {
            return false;
         } else {
            String alias2 = this.getAlias();
            String alias3 = metadata2.getAlias();
            if (alias2 == null ? alias3 == null : alias2.equals(alias3)) {
               BackgroundManager.Record colors2 = this.getColors();
               BackgroundManager.Record colors3 = metadata2.getColors();
               return colors2 == null ? colors3 == null : colors2.equals(colors3);
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         int i = 59 + this.getWidth();
         i = i * 59 + this.getHeight();
         String alias2 = this.getAlias();
         i = i * 59 + (alias2 == null ? 43 : alias2.hashCode());
         BackgroundManager.Record colors2 = this.getColors();
         return i * 59 + (colors2 == null ? 43 : colors2.hashCode());
      }

      public int getHeight() {
         return this.height;
      }

      @Override
      public String toString() {
         return "AnimePictureStore.Metadata(alias="
            + this.getAlias()
            + ", colors="
            + this.getColors()
            + ", width="
            + this.getWidth()
            + ", height="
            + this.getHeight()
            + ")";
      }

      public static BackgroundManager.MetadataBuilder builder() {
         return new BackgroundManager.MetadataBuilder();
      }

      public BackgroundManager.Record getColors() {
         return this.colors;
      }

      public String getAlias() {
         return this.alias;
      }

      public int getWidth() {
         return this.width;
      }
   }

   public static class MetadataBuilder {
      private int width;
      private int height;
      private String alias;
      private BackgroundManager.Record colors;

      public BackgroundManager.MetadataBuilder width(int i) {
         this.width = i;
         return this;
      }

      public BackgroundManager.MetadataBuilder height(int i) {
         this.height = i;
         return this;
      }

      @Override
      public String toString() {
         return "AnimePictureStore.Metadata.MetadataBuilder(alias="
            + this.alias
            + ", colors="
            + this.colors
            + ", width="
            + this.width
            + ", height="
            + this.height
            + ")";
      }

      public BackgroundManager.MetadataBuilder colors(BackgroundManager.Record recordValue) {
         this.colors = recordValue;
         return this;
      }

      MetadataBuilder() {
      }

      public BackgroundManager.Metadata getMetadata() {
         return new BackgroundManager.Metadata(this.alias, this.colors, this.width, this.height);
      }

      public BackgroundManager.MetadataBuilder alias(String text) {
         this.alias = text;
         return this;
      }
   }

   public record Record(Color color, Color color2, Color color3) {
      public Color getColor() {
         return this.color;
      }

      public Color getColor2() {
         return this.color2;
      }

      public Color getColor3() {
         return this.color3;
      }
   }

   class TimerTaskChild extends TimerTask {
      @Override
      public void run() {
         BackgroundManager.Entry[] entrys = BackgroundManager.this.linkedHashMap.values().toArray(new BackgroundManager.Entry[0]);
         BackgroundManager.this.entry = entrys[ThreadLocalRandom.current().nextInt(0, entrys.length - 1)];
      }
   }
}
