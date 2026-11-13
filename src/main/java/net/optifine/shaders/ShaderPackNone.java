package net.optifine.shaders;

import java.io.InputStream;

public class ShaderPackNone implements IShaderPack {
   @Override
   public boolean hasDirectory(String name) {
      return false;
   }

   @Override
   public void close() {
   }

   @Override
   public String getName() {
      return "OFF";
   }

   @Override
   public InputStream getResourceAsStream(String resName) {
      return null;
   }
}
