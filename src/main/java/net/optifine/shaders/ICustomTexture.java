package net.optifine.shaders;

public interface ICustomTexture {
   int getTextureId();

   int getTarget();

   int getTextureUnit();

   void deleteTexture();
}
