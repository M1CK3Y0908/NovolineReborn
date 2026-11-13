package net.optifine;

import java.nio.ByteBuffer;
import java.util.Properties;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.TextureUtils;
import org.lwjgl.opengl.GL11;

public class TextureAnimation {
   private String srcTex = null;
   private String dstTex = null;
   ResourceLocation dstTexLoc = null;
   private int dstTextId = -1;
   private int dstX = 0;
   private int dstY = 0;
   private int frameWidth = 0;
   private int frameHeight = 0;
   private TextureAnimationFrame[] frames = null;
   private int currentFrameIndex = 0;
   private boolean interpolate = false;
   private int interpolateSkip = 0;
   private ByteBuffer interpolateData = null;
   byte[] srcData = null;
   private ByteBuffer imageData = null;
   private boolean active = true;
   private boolean valid = true;

   private int mix(int col1, int col2, double d) {
      return (int)(col1 * (1.0 - d) + col2 * d);
   }

   public String getDstTex() {
      return this.dstTex;
   }

   private void updateTextureInerpolate(TextureAnimationFrame frame1, TextureAnimationFrame frame2, double dd) {
      int i = this.frameWidth * this.frameHeight * 4;
      int i2 = i * frame1.index;
      if (i2 + i <= this.imageData.limit()) {
         int i3 = i * frame2.index;
         if (i3 + i <= this.imageData.limit()) {
            this.interpolateData.clear();

            for (int outerIndex = 0; outerIndex < i; outerIndex++) {
               int i4 = this.imageData.get(i2 + outerIndex) & 255;
               int i5 = this.imageData.get(i3 + outerIndex) & 255;
               byte b0 = (byte)this.mix(i4, i5, dd);
               this.interpolateData.put(b0);
            }

            this.interpolateData.flip();
            GlStateManager.bindTexture(this.dstTextId);
            GL11.glTexSubImage2D(3553, 0, this.dstX, this.dstY, this.frameWidth, this.frameHeight, 6408, 5121, this.interpolateData);
         }
      }
   }

   public ResourceLocation getDstTexLoc() {
      return this.dstTexLoc;
   }

   public boolean isActive() {
      return this.active;
   }

   public void updateTexture() {
      if (this.valid) {
         if (this.dstTextId < 0) {
            ITextureObject textureObject = TextureUtils.getTexture(this.dstTexLoc);
            if (textureObject == null) {
               this.valid = false;
               return;
            }

            this.dstTextId = textureObject.getGlTextureId();
         }

         if (this.imageData == null) {
            this.imageData = GLAllocation.createDirectByteBuffer(this.srcData.length);
            this.imageData.put(this.srcData);
            this.imageData.flip();
            this.srcData = null;
         }

         this.active = SmartAnimations.isActive() ? SmartAnimations.isTextureRendered(this.dstTextId) : true;
         if (this.nextFrame() && this.active) {
            int i = this.frameWidth * this.frameHeight * 4;
            TextureAnimationFrame textureAnimationFrame = this.getCurrentFrame();
            if (textureAnimationFrame != null) {
               int i2 = i * textureAnimationFrame.index;
               if (i2 + i <= this.imageData.limit()) {
                  if (!this.interpolate || textureAnimationFrame.counter <= 0) {
                     this.imageData.position(i2);
                     GlStateManager.bindTexture(this.dstTextId);
                     GL11.glTexSubImage2D(3553, 0, this.dstX, this.dstY, this.frameWidth, this.frameHeight, 6408, 5121, this.imageData);
                  } else if (this.interpolateSkip <= 1 || textureAnimationFrame.counter % this.interpolateSkip == 0) {
                     TextureAnimationFrame textureAnimationFrame2 = this.getFrame(this.currentFrameIndex + 1);
                     double d = 1.0 * textureAnimationFrame.counter / textureAnimationFrame.duration;
                     this.updateTextureInerpolate(textureAnimationFrame, textureAnimationFrame2, d);
                  }
               }
            }
         }
      }
   }

   public TextureAnimation(
      String texFrom, byte[] srcData, String texTo, ResourceLocation locTexTo, int dstX, int dstY, int frameWidth, int frameHeight, Properties props
   ) {
      this.srcTex = texFrom;
      this.dstTex = texTo;
      this.dstTexLoc = locTexTo;
      this.dstX = dstX;
      this.dstY = dstY;
      this.frameWidth = frameWidth;
      this.frameHeight = frameHeight;
      int i = frameWidth * frameHeight * 4;
      if (srcData.length % i != 0) {
         Config.warn("Invalid animated texture length: " + srcData.length + ", frameWidth: " + frameWidth + ", frameHeight: " + frameHeight);
      }

      this.srcData = srcData;
      int i2 = srcData.length / i;
      if (props.get("tile.0") != null) {
         for (int i3 = 0; props.get("tile." + i3) != null; i3++) {
            i2 = i3 + 1;
         }
      }

      String text = (String)props.get("duration");
      int i4 = Math.max(Config.parseInt(text, 1), 1);
      this.frames = new TextureAnimationFrame[i2];

      for (int i5 = 0; i5 < this.frames.length; i5++) {
         int i6 = Config.parseInt((String)props.get("tile." + i5), i5);
         int i7 = Math.max(Config.parseInt((String)props.get("duration." + i5), i4), 1);
         TextureAnimationFrame textureAnimationFrame3 = new TextureAnimationFrame(i6, i7);
         this.frames[i5] = textureAnimationFrame3;
      }

      this.interpolate = Config.parseBoolean(props.getProperty("interpolate"), false);
      this.interpolateSkip = Config.parseInt(props.getProperty("skip"), 0);
      if (this.interpolate) {
         this.interpolateData = GLAllocation.createDirectByteBuffer(i);
      }
   }

   public String getSrcTex() {
      return this.srcTex;
   }

   public int getFrameCount() {
      return this.frames.length;
   }

   public TextureAnimationFrame getCurrentFrame() {
      return this.getFrame(this.currentFrameIndex);
   }

   public TextureAnimationFrame getFrame(int index) {
      if (this.frames.length <= 0) {
         return null;
      }

      if (index < 0 || index >= this.frames.length) {
         index = 0;
      }

      return this.frames[index];
   }

   public boolean nextFrame() {
      TextureAnimationFrame textureAnimationFrame = this.getCurrentFrame();
      if (textureAnimationFrame == null) {
         return false;
      }

      textureAnimationFrame.counter++;
      if (textureAnimationFrame.counter < textureAnimationFrame.duration) {
         return this.interpolate;
      }

      textureAnimationFrame.counter = 0;
      this.currentFrameIndex++;
      if (this.currentFrameIndex >= this.frames.length) {
         this.currentFrameIndex = 0;
      }

      return true;
   }
}
