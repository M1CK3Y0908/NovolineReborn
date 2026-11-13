package net.optifine.shaders;

import java.io.InputStream;

public interface IShaderPack {
   void close();

   InputStream getResourceAsStream(String text);

   boolean hasDirectory(String text);

   String getName();
}
