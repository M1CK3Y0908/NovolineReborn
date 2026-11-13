package net.optifine.shaders;

import com.google.common.base.Joiner;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.src.Config;
import net.optifine.util.StrUtils;

public class ShaderPackZip implements IShaderPack {
   protected File packFile;
   protected ZipFile packZipFile;
   protected String baseFolder;

   @Override
   public String getName() {
      return this.packFile.getName();
   }

   public ShaderPackZip(String name, File file) {
      this.packFile = file;
      this.packZipFile = null;
      this.baseFolder = "";
   }

   private String detectBaseFolder(ZipFile zip) {
      ZipEntry entry = zip.getEntry("shaders/");
      if (entry != null && entry.isDirectory()) {
         return "";
      }

      Pattern pattern = Pattern.compile("([^/]+/)shaders/");
      Enumeration enumeration = zip.entries();

      while (enumeration.hasMoreElements()) {
         String name2 = ((ZipEntry)enumeration.nextElement()).getName();
         Matcher matcher2 = pattern.matcher(name2);
         if (matcher2.matches()) {
            String text = matcher2.group(1);
            if (text != null) {
               if (text.equals("shaders/")) {
                  return "";
               }

               return text;
            }
         }
      }

      return "";
   }

   @Override
   public void close() {
      if (this.packZipFile != null) {
         try {
            this.packZipFile.close();
         } catch (Exception exception) {
         }

         this.packZipFile = null;
      }
   }

   @Override
   public boolean hasDirectory(String resName) {
      try {
         if (this.packZipFile == null) {
            this.packZipFile = new ZipFile(this.packFile);
            this.baseFolder = this.detectBaseFolder(this.packZipFile);
         }

         String text = StrUtils.removePrefix(resName, "/");
         return this.packZipFile.getEntry(this.baseFolder + text) != null;
      } catch (IOException ioException) {
         return false;
      }
   }

   private String resolveRelative(String name) {
      ArrayDeque arrayDeque = new ArrayDeque();
      String[] stringArray = Config.tokenize(name, "/");

      for (int index = 0; index < stringArray.length; index++) {
         String text = stringArray[index];
         if (text.equals("..")) {
            if (arrayDeque.isEmpty()) {
               return "";
            }

            arrayDeque.removeLast();
         } else {
            arrayDeque.add(text);
         }
      }

      return Joiner.on('/').join(arrayDeque);
   }

   @Override
   public InputStream getResourceAsStream(String resName) {
      try {
         if (this.packZipFile == null) {
            this.packZipFile = new ZipFile(this.packFile);
            this.baseFolder = this.detectBaseFolder(this.packZipFile);
         }

         String text = StrUtils.removePrefix(resName, "/");
         if (text.contains("..")) {
            text = this.resolveRelative(text);
         }

         ZipEntry entry = this.packZipFile.getEntry(this.baseFolder + text);
         return entry == null ? null : this.packZipFile.getInputStream(entry);
      } catch (Exception exception) {
         return null;
      }
   }
}
