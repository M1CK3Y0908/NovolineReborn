package net.optifine.shaders;

import java.nio.ByteBuffer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class HFNoiseTexture implements ICustomTexture {
   private int texID = GL11.glGenTextures();
   private int textureUnit = 15;

   @Override
   public void deleteTexture() {
      GlStateManager.deleteTexture(this.texID);
      this.texID = 0;
   }

   @Override
   public int getTextureId() {
      return this.texID;
   }

   private byte random(int x, int y, int z) {
      int i = (this.random(x) + this.random(y * 19)) * this.random(z * 23) - z;
      return (byte)(this.random(i) % 128);
   }

   private byte[] genHFNoiseImage(int width, int height) {
      byte[] byteArray = new byte[width * height * 3];
      int i = 0;

      for (int innerIndex = 0; innerIndex < height; innerIndex++) {
         for (int nestedIndex = 0; nestedIndex < width; nestedIndex++) {
            for (int outerIndex = 1; outerIndex < 4; outerIndex++) {
               byteArray[i++] = this.random(nestedIndex, innerIndex, outerIndex);
            }
         }
      }

      return byteArray;
   }

   @Override
   public int getTarget() {
      return 3553;
   }

   @Override
   public int getTextureUnit() {
      return this.textureUnit;
   }

   public HFNoiseTexture(int width, int height) {
      byte[] byteArray = this.genHFNoiseImage(width, height);
      ByteBuffer byteBuffer = BufferUtils.createByteBuffer(byteArray.length);
      byteBuffer.put(byteArray);
      byteBuffer.flip();
      GlStateManager.bindTexture(this.texID);
      GL11.glTexImage2D(3553, 0, 6407, width, height, 0, 6407, 5121, byteBuffer);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glTexParameteri(3553, 10241, 9729);
      GlStateManager.bindTexture(0);
   }

   public int getTexID() {
      return this.texID;
   }

   private int random(int seed) {
      seed ^= seed << 13;
      seed ^= seed >> 17;
      return seed ^ seed << 5;
   }
}
