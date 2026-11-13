package net.optifine.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;

public class ResUtils {
   public static Properties readProperties(InputStream in, String module) {
      if (in == null) {
         return null;
      }

      try {
         PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
         propertiesOrdered.load(in);
         in.close();
         return propertiesOrdered;
      } catch (FileNotFoundException fileNotFoundException) {
         return null;
      } catch (IOException ioException) {
         return null;
      }
   }

   private static String[] collectFilesFolder(File tpFile, String basePath, String[] prefixes, String[] suffixes) {
      ArrayList arrayList = new ArrayList();
      File[] files = tpFile.listFiles();
      if (files == null) {
         return new String[0];
      }

      for (int i = 0; i < files.length; i++) {
         File file2 = files[i];
         if (file2.isFile()) {
            String text = basePath + file2.getName();
            if (text.startsWith("assets/minecraft/")) {
               text = text.substring("assets/minecraft/".length());
               if (StrUtils.startsWith(text, prefixes) && StrUtils.endsWith(text, suffixes)) {
                  arrayList.add(text);
               }
            }
         } else if (file2.isDirectory()) {
            String text2 = basePath + file2.getName() + "/";
            String[] texts = collectFilesFolder(file2, text2, prefixes, suffixes);

            for (int i2 = 0; i2 < texts.length; i2++) {
               String text3 = texts[i2];
               arrayList.add(text3);
            }
         }
      }

      return (String[]) arrayList.toArray(new String[arrayList.size()]);
   }

   public static String[] collectFiles(IResourcePack rp, String[] prefixes, String[] suffixes) {
      return collectFiles(rp, prefixes, suffixes, (String[])null);
   }

   private static String[] collectFilesZIP(File tpFile, String[] prefixes, String[] suffixes) {
      ArrayList arrayList = new ArrayList();

      try {
         ZipFile zipFile = new ZipFile(tpFile);
         Enumeration enumeration = zipFile.entries();

         while (enumeration.hasMoreElements()) {
            String name = ((ZipEntry)enumeration.nextElement()).getName();
            if (name.startsWith("assets/minecraft/")) {
               name = name.substring("assets/minecraft/".length());
               if (StrUtils.startsWith(name, prefixes) && StrUtils.endsWith(name, suffixes)) {
                  arrayList.add(name);
               }
            }
         }

         zipFile.close();
         return (String[]) arrayList.toArray(new String[arrayList.size()]);
      } catch (IOException ioException) {
         ioException.printStackTrace();
         return new String[0];
      }
   }

   private static boolean isLowercase(String str) {
      return str.equals(str.toLowerCase(Locale.ROOT));
   }

   public static Properties readProperties(String path, String module) {
      ResourceLocation resourceLocation = new ResourceLocation(path);

      try {
         InputStream resourceStream = Config.getResourceStream(resourceLocation);
         if (resourceStream == null) {
            return null;
         }

         PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
         propertiesOrdered.load(resourceStream);
         resourceStream.close();
         Config.dbg(module + ": Loading " + path);
         return propertiesOrdered;
      } catch (FileNotFoundException fileNotFoundException) {
         return null;
      } catch (IOException ioException) {
         Config.warn(module + ": Error reading " + path);
         return null;
      }
   }

   public static String[] collectFiles(String[] prefixes, String[] suffixes) {
      LinkedHashSet linkedHashSet = new LinkedHashSet();
      IResourcePack[] resourcePacks = Config.getResourcePacks();

      for (int i = 0; i < resourcePacks.length; i++) {
         String[] texts = collectFiles(resourcePacks[i], prefixes, suffixes, (String[])null);
         linkedHashSet.addAll(Arrays.asList(texts));
      }

      return (String[]) linkedHashSet.toArray(new String[linkedHashSet.size()]);
   }

   public static String[] collectFiles(String prefix, String suffix) {
      return collectFiles(new String[]{prefix}, new String[]{suffix});
   }

   public static String[] collectFiles(IResourcePack rp, String[] prefixes, String[] suffixes, String[] defaultPaths) {
      if (rp instanceof DefaultResourcePack) {
         return collectFilesFixed(rp, defaultPaths);
      }

      if (!(rp instanceof AbstractResourcePack)) {
         Config.warn("Unknown resource pack type: " + rp);
         return new String[0];
      }

      File resourcePackFile2 = ((AbstractResourcePack)rp).resourcePackFile;
      if (resourcePackFile2 == null) {
         return new String[0];
      }

      if (resourcePackFile2.isDirectory()) {
         return collectFilesFolder(resourcePackFile2, "", prefixes, suffixes);
      }

      if (resourcePackFile2.isFile()) {
         return collectFilesZIP(resourcePackFile2, prefixes, suffixes);
      }

      Config.warn("Unknown resource pack file: " + resourcePackFile2);
      return new String[0];
   }

   public static String[] collectFiles(IResourcePack rp, String prefix, String suffix, String[] defaultPaths) {
      return collectFiles(rp, new String[]{prefix}, new String[]{suffix}, defaultPaths);
   }

   private static String[] collectFilesFixed(IResourcePack rp, String[] paths) {
      if (paths == null) {
         return new String[0];
      }

      ArrayList arrayList = new ArrayList();

      for (int index = 0; index < paths.length; index++) {
         String text = paths[index];
         ResourceLocation resourceLocation = new ResourceLocation(text);
         if (rp.resourceExists(resourceLocation)) {
            arrayList.add(text);
         }
      }

      return (String[]) arrayList.toArray(new String[arrayList.size()]);
   }
}
