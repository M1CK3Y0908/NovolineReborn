package net.optifine.shaders;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import net.optifine.util.StrUtils;

public class ShaderPackFolder implements IShaderPack {
   protected File packFile;

   @Override
   public InputStream getResourceAsStream(String resName) {
      try {
         String text = StrUtils.removePrefixSuffix(resName, "/", "/");
         File file2 = new File(this.packFile, text);
         return !file2.exists() ? null : new BufferedInputStream(new FileInputStream(file2));
      } catch (Exception exception) {
         return null;
      }
   }

   @Override
   public boolean hasDirectory(String name) {
      File file2 = new File(this.packFile, name.substring(1));
      return !file2.exists() ? false : file2.isDirectory();
   }

   public ShaderPackFolder(String name, File file) {
      this.packFile = file;
   }

   @Override
   public void close() {
   }

   @Override
   public String getName() {
      return this.packFile.getName();
   }
}
