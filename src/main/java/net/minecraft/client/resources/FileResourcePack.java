package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileResourcePack extends AbstractResourcePack implements Closeable {
   private ZipFile resourcePackZipFile;
   public static final Splitter entryNameSplitter = Splitter.on('/').omitEmptyStrings().limit(3);

   @Override
   protected void finalize() throws Throwable {
      this.close();
      super.finalize();
   }

   public FileResourcePack(File resourcePackFileIn) {
      super(resourcePackFileIn);
   }

   @Override
   public Set<String> getResourceDomains() {
      ZipFile zipFile2;
      try {
         zipFile2 = this.getResourcePackZipFile();
      } catch (IOException ioException) {
         return Collections.emptySet();
      }

      Enumeration enumeration = zipFile2.entries();
      HashSet hashSet = Sets.newHashSet();

      while (enumeration.hasMoreElements()) {
         String name2 = ((ZipEntry)enumeration.nextElement()).getName();
         if (name2.startsWith("assets/")) {
            ArrayList arrayList = Lists.newArrayList(entryNameSplitter.split(name2));
            if (arrayList.size() > 1) {
               String text = (String)arrayList.get(1);
               if (!text.equals(text.toLowerCase())) {
                  this.logNameNotLowercase(text);
               } else {
                  hashSet.add(text);
               }
            }
         }
      }

      return hashSet;
   }

   @Override
   protected InputStream getInputStreamByName(String name) throws IOException {
      ZipFile zipFile = this.getResourcePackZipFile();
      ZipEntry zipEntry = zipFile.getEntry(name);
      if (zipEntry == null) {
         throw new ResourcePackFileNotFoundException(this.resourcePackFile, name);
      } else {
         return zipFile.getInputStream(zipEntry);
      }
   }

   private ZipFile getResourcePackZipFile() throws IOException {
      if (this.resourcePackZipFile == null) {
         this.resourcePackZipFile = new ZipFile(this.resourcePackFile);
      }

      return this.resourcePackZipFile;
   }

   @Override
   public boolean hasResourceName(String name) {
      try {
         return this.getResourcePackZipFile().getEntry(name) != null;
      } catch (IOException ioException) {
         return false;
      }
   }

   @Override
   public void close() throws IOException {
      if (this.resourcePackZipFile != null) {
         this.resourcePackZipFile.close();
         this.resourcePackZipFile = null;
      }
   }
}
