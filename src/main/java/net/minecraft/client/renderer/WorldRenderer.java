package net.minecraft.client.renderer;

import com.google.common.primitives.Floats;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.optifine.SmartAnimations;
import net.optifine.render.RenderEnv;
import net.optifine.shaders.SVertexBuilder;
import net.optifine.util.TextureUtils;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.GL11;

public class WorldRenderer {
   private EnumWorldBlockLayer blockLayer = null;
   private ByteBuffer byteBufferTriangles;
   private double yOffset;
   private VertexFormatElement vertexFormatElement;
   private double zOffset;
   private boolean noColor;
   private boolean[] drawnIcons = new boolean[256];
   public int vertexCount;
   private TextureAtlasSprite[] quadSprites = null;
   private TextureAtlasSprite[] quadSpritesPrev = null;
   private double xOffset;
   public SVertexBuilder sVertexBuilder;
   private TextureAtlasSprite quadSprite = null;
   public RenderEnv renderEnv = null;
   public int drawMode;
   private VertexFormat vertexFormat;
   private int vertexFormatIndex;
   public BitSet animatedSprites = null;
   public BitSet animatedSpritesCached = new BitSet();
   private boolean modeTriangles = false;
   private boolean isDrawing;
   private ByteBuffer byteBuffer;
   public IntBuffer rawIntBuffer;
   private ShortBuffer rawShortBuffer;
   public FloatBuffer rawFloatBuffer;

   public void putPosition(double x, double y, double z) {
      int integerSize = this.vertexFormat.getIntegerSize();
      int i = (this.vertexCount - 4) * integerSize;

      for (int nestedIndex = 0; nestedIndex < 4; nestedIndex++) {
         int i2 = i + nestedIndex * integerSize;
         int i3 = i2 + 1;
         int i4 = i3 + 1;
         this.rawIntBuffer
            .put(i2, Float.floatToRawIntBits((float)(x + this.xOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(i2))));
         this.rawIntBuffer.put(i3, Float.floatToRawIntBits((float)(y + this.yOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(i3))));
         this.rawIntBuffer
            .put(i4, Float.floatToRawIntBits((float)(z + this.zOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(i4))));
      }
   }

   public void setSprite(TextureAtlasSprite sprite) {
      if (this.animatedSprites != null && sprite != null && sprite.getAnimationIndex() >= 0) {
         this.animatedSprites.set(sprite.getAnimationIndex());
      }

      if (this.quadSprites != null) {
         this.quadSprite = sprite;
      }
   }

   public WorldRenderer pos(double x, double y, double z) {
      if (Config.isShaders()) {
         SVertexBuilder.beginAddVertex(this);
      }

      int i = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch (this.vertexFormatElement.getType()) {
         case FLOAT:
            this.byteBuffer.putFloat(i, (float)(x + this.xOffset));
            this.byteBuffer.putFloat(i + 4, (float)(y + this.yOffset));
            this.byteBuffer.putFloat(i + 8, (float)(z + this.zOffset));
            break;
         case UINT:
         case INT:
            this.byteBuffer.putInt(i, Float.floatToRawIntBits((float)(x + this.xOffset)));
            this.byteBuffer.putInt(i + 4, Float.floatToRawIntBits((float)(y + this.yOffset)));
            this.byteBuffer.putInt(i + 8, Float.floatToRawIntBits((float)(z + this.zOffset)));
            break;
         case USHORT:
         case SHORT:
            this.byteBuffer.putShort(i, (short)(x + this.xOffset));
            this.byteBuffer.putShort(i + 2, (short)(y + this.yOffset));
            this.byteBuffer.putShort(i + 4, (short)(z + this.zOffset));
            break;
         case UBYTE:
         case BYTE:
            this.byteBuffer.put(i, (byte)(x + this.xOffset));
            this.byteBuffer.put(i + 1, (byte)(y + this.yOffset));
            this.byteBuffer.put(i + 2, (byte)(z + this.zOffset));
      }

      this.nextVertexFormatIndex();
      return this;
   }

   public ByteBuffer getByteBuffer() {
      return this.modeTriangles ? this.byteBufferTriangles : this.byteBuffer;
   }

   public void begin(int glMode, VertexFormat format) {
      if (this.isDrawing) {
         throw new IllegalStateException("Already building!");
      }

      this.isDrawing = true;
      this.reset();
      this.drawMode = glMode;
      this.vertexFormat = format;
      this.vertexFormatElement = format.getElement(this.vertexFormatIndex);
      this.noColor = false;
      this.byteBuffer.limit(this.byteBuffer.capacity());
      if (Config.isShaders()) {
         SVertexBuilder.endSetVertexFormat(this);
      }

      if (Config.isMultiTexture()) {
         if (this.blockLayer != null) {
            if (this.quadSprites == null) {
               this.quadSprites = this.quadSpritesPrev;
            }

            if (this.quadSprites == null || this.quadSprites.length < this.getBufferQuadSize()) {
               this.quadSprites = new TextureAtlasSprite[this.getBufferQuadSize()];
            }
         }
      } else {
         if (this.quadSprites != null) {
            this.quadSpritesPrev = this.quadSprites;
         }

         this.quadSprites = null;
      }
   }

   public WorldRenderer normal(float x, float y, float z) {
      int i = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch (this.vertexFormatElement.getType()) {
         case FLOAT:
            this.byteBuffer.putFloat(i, x);
            this.byteBuffer.putFloat(i + 4, y);
            this.byteBuffer.putFloat(i + 8, z);
            break;
         case UINT:
         case INT:
            this.byteBuffer.putInt(i, (int)x);
            this.byteBuffer.putInt(i + 4, (int)y);
            this.byteBuffer.putInt(i + 8, (int)z);
            break;
         case USHORT:
         case SHORT:
            this.byteBuffer.putShort(i, (short)((int)(x * 32767.0F) & 65535));
            this.byteBuffer.putShort(i + 2, (short)((int)(y * 32767.0F) & 65535));
            this.byteBuffer.putShort(i + 4, (short)((int)(z * 32767.0F) & 65535));
            break;
         case UBYTE:
         case BYTE:
            this.byteBuffer.put(i, (byte)((int)(x * 127.0F) & 0xFF));
            this.byteBuffer.put(i + 1, (byte)((int)(y * 127.0F) & 0xFF));
            this.byteBuffer.put(i + 2, (byte)((int)(z * 127.0F) & 0xFF));
      }

      this.nextVertexFormatIndex();
      return this;
   }

   public void putColorMultiplier(float red, float green, float blue, int vertexIndex) {
      int colorIndex = this.getColorIndex(vertexIndex);
      int i = -1;
      if (!this.noColor) {
         i = this.rawIntBuffer.get(colorIndex);
         if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            int i2 = (int)((i & 0xFF) * red);
            int i3 = (int)((i >> 8 & 0xFF) * green);
            int i4 = (int)((i >> 16 & 0xFF) * blue);
            i &= -16777216;
            i = i | i4 << 16 | i3 << 8 | i2;
         } else {
            int i5 = (int)((i >> 24 & 0xFF) * red);
            int i6 = (int)((i >> 16 & 0xFF) * green);
            int i7 = (int)((i >> 8 & 0xFF) * blue);
            i &= 255;
            i = i | i5 << 24 | i6 << 16 | i7 << 8;
         }
      }

      this.rawIntBuffer.put(colorIndex, i);
   }

   public double getYOffset() {
      return this.yOffset;
   }

   public void drawMultiTexture() {
      if (this.quadSprites != null) {
         int countRegisteredSprites = Config.getMinecraft().getTextureMapBlocks().getCountRegisteredSprites();
         if (this.drawnIcons.length <= countRegisteredSprites) {
            this.drawnIcons = new boolean[countRegisteredSprites + 1];
         }

         Arrays.fill(this.drawnIcons, false);
         int i = 0;
         int i2 = -1;
         int i3 = this.vertexCount / 4;

         for (int index = 0; index < i3; index++) {
            TextureAtlasSprite textureAtlasSprite = this.quadSprites[index];
            if (textureAtlasSprite != null) {
               int indexInMap = textureAtlasSprite.getIndexInMap();
               if (!this.drawnIcons[indexInMap]) {
                  if (textureAtlasSprite == TextureUtils.iconGrassSideOverlay) {
                     if (i2 < 0) {
                        i2 = index;
                     }
                  } else {
                     index = this.drawForIcon(textureAtlasSprite, index) - 1;
                     i++;
                     if (this.blockLayer != EnumWorldBlockLayer.TRANSLUCENT) {
                        this.drawnIcons[indexInMap] = true;
                     }
                  }
               }
            }
         }

         if (i2 >= 0) {
            this.drawForIcon(TextureUtils.iconGrassSideOverlay, i2);
            i++;
         }

         if (i > 0) {
         }
      }
   }

   private void draw(int startQuad, int endQuad) {
      int i = endQuad - startQuad;
      if (i > 0) {
         int i2 = startQuad * 4;
         int i3 = i * 4;
         GL11.glDrawArrays(this.drawMode, i2, i3);
      }
   }

   public void putColorRGB_F4(float red, float green, float blue) {
      for (int index = 0; index < 4; index++) {
         this.putColorRGB_F(red, green, blue, index + 1);
      }
   }

   private void putColor(int argb, int vertexIndex) {
      int colorIndex = this.getColorIndex(vertexIndex);
      int i = argb >> 16 & 0xFF;
      int i2 = argb >> 8 & 0xFF;
      int i3 = argb & 0xFF;
      int i4 = argb >> 24 & 0xFF;
      this.putColorRGBA(colorIndex, i, i2, i3, i4);
   }

   public boolean isColorDisabled() {
      return this.noColor;
   }

   public void putColor4(int argb) {
      for (int index = 0; index < 4; index++) {
         this.putColor(argb, index + 1);
      }
   }

   public void putColorMultiplierRgba(float red, float green, float blue, float alpha, int vertexIndex) {
      int colorIndex = this.getColorIndex(vertexIndex);
      int i = -1;
      if (!this.noColor) {
         i = this.rawIntBuffer.get(colorIndex);
         if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            int i2 = (int)((i & 0xFF) * red);
            int i3 = (int)((i >> 8 & 0xFF) * green);
            int i4 = (int)((i >> 16 & 0xFF) * blue);
            int i5 = (int)((i >> 24 & 0xFF) * alpha);
            i = i5 << 24 | i4 << 16 | i3 << 8 | i2;
         } else {
            int i6 = (int)((i >> 24 & 0xFF) * red);
            int i7 = (int)((i >> 16 & 0xFF) * green);
            int i8 = (int)((i >> 8 & 0xFF) * blue);
            int i9 = (int)((i & 0xFF) * alpha);
            i = i6 << 24 | i7 << 16 | i8 << 8 | i9;
         }
      }

      this.rawIntBuffer.put(colorIndex, i);
   }

   public int getBufferSize() {
      return this.vertexCount * this.vertexFormat.getIntegerSize();
   }

   private int drawForIcon(TextureAtlasSprite sprite, int startQuad) {
      GL11.glBindTexture(3553, sprite.glSpriteTextureId);
      int outerIndex = -1;
      int i = -1;
      int i2 = this.vertexCount / 4;

      for (int startQuad2 = startQuad; startQuad2 < i2; startQuad2++) {
         if (this.quadSprites[startQuad2] == sprite) {
            if (i < 0) {
               i = startQuad2;
            }
         } else if (i >= 0) {
            this.draw(i, startQuad2);
            if (this.blockLayer == EnumWorldBlockLayer.TRANSLUCENT) {
               return startQuad2;
            }

            i = -1;
            if (outerIndex < 0) {
               outerIndex = startQuad2;
            }
         }
      }

      if (i >= 0) {
         this.draw(i, i2);
      }

      if (outerIndex < 0) {
         outerIndex = i2;
      }

      return outerIndex;
   }

   public void putSprite(TextureAtlasSprite sprite) {
      if (this.animatedSprites != null && sprite != null && sprite.getAnimationIndex() >= 0) {
         this.animatedSprites.set(sprite.getAnimationIndex());
      }

      if (this.quadSprites != null) {
         int i = this.vertexCount / 4;
         this.quadSprites[i - 1] = sprite;
      }
   }

   public void noColor() {
      this.noColor = true;
   }

   public void setTranslation(double x, double y, double z) {
      this.xOffset = x;
      this.yOffset = y;
      this.zOffset = z;
   }

   public void setBlockLayer(EnumWorldBlockLayer blockLayerIn) {
      this.blockLayer = blockLayerIn;
      if (blockLayerIn == null) {
         if (this.quadSprites != null) {
            this.quadSpritesPrev = this.quadSprites;
         }

         this.quadSprites = null;
         this.quadSprite = null;
      }
   }

   private void growBuffer(int requiredInts) {
      if (requiredInts > this.rawIntBuffer.remaining()) {
         int i = this.byteBuffer.capacity();
         int i2 = i % 2097152;
         int i3 = i2 + (((this.rawIntBuffer.position() + requiredInts) * 4 - i2) / 2097152 + 1) * 2097152;
         LogManager.getLogger().warn("Needed to grow BufferBuilder buffer: Old size " + i + " bytes, new size " + i3 + " bytes.");
         int position2 = this.rawIntBuffer.position();
         ByteBuffer byteBuffer = GLAllocation.createDirectByteBuffer(i3);
         this.byteBuffer.position(0);
         byteBuffer.put(this.byteBuffer);
         byteBuffer.rewind();
         this.byteBuffer = byteBuffer;
         this.rawFloatBuffer = this.byteBuffer.asFloatBuffer();
         this.rawIntBuffer = this.byteBuffer.asIntBuffer();
         this.rawIntBuffer.position(position2);
         this.rawShortBuffer = this.byteBuffer.asShortBuffer();
         this.rawShortBuffer.position(position2 << 1);
         if (this.quadSprites != null) {
            TextureAtlasSprite[] atextureatlassprite = this.quadSprites;
            int bufferQuadSize = this.getBufferQuadSize();
            this.quadSprites = new TextureAtlasSprite[bufferQuadSize];
            System.arraycopy(atextureatlassprite, 0, this.quadSprites, 0, Math.min(atextureatlassprite.length, this.quadSprites.length));
            this.quadSpritesPrev = null;
         }
      }
   }

   private static float getDistanceSq(FloatBuffer buffer, float x, float y, float z, int stride, int index) {
      float f = buffer.get(index + stride * 0 + 0);
      float f2 = buffer.get(index + stride * 0 + 1);
      float f3 = buffer.get(index + stride * 0 + 2);
      float f4 = buffer.get(index + stride * 1 + 0);
      float f5 = buffer.get(index + stride * 1 + 1);
      float f6 = buffer.get(index + stride * 1 + 2);
      float f7 = buffer.get(index + stride * 2 + 0);
      float f8 = buffer.get(index + stride * 2 + 1);
      float f9 = buffer.get(index + stride * 2 + 2);
      float f10 = buffer.get(index + stride * 3 + 0);
      float f11 = buffer.get(index + stride * 3 + 1);
      float f12 = buffer.get(index + stride * 3 + 2);
      float f13 = (f + f4 + f7 + f10) * 0.25F - x;
      float f14 = (f2 + f5 + f8 + f11) * 0.25F - y;
      float f15 = (f3 + f6 + f9 + f12) * 0.25F - z;
      return f13 * f13 + f14 * f14 + f15 * f15;
   }

   public WorldRenderer lightmap(int skyLight, int blockLight) {
      int i = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch (this.vertexFormatElement.getType()) {
         case FLOAT:
            this.byteBuffer.putFloat(i, skyLight);
            this.byteBuffer.putFloat(i + 4, blockLight);
            break;
         case UINT:
         case INT:
            this.byteBuffer.putInt(i, skyLight);
            this.byteBuffer.putInt(i + 4, blockLight);
            break;
         case USHORT:
         case SHORT:
            this.byteBuffer.putShort(i, (short)blockLight);
            this.byteBuffer.putShort(i + 2, (short)skyLight);
            break;
         case UBYTE:
         case BYTE:
            this.byteBuffer.put(i, (byte)blockLight);
            this.byteBuffer.put(i + 1, (byte)skyLight);
      }

      this.nextVertexFormatIndex();
      return this;
   }

   public EnumWorldBlockLayer getBlockLayer() {
      return this.blockLayer;
   }

   public void putColorRGB_F(float red, float green, float blue, int vertexIndex) {
      int colorIndex = this.getColorIndex(vertexIndex);
      int i = MathHelper.clamp_int((int)(red * 255.0F), 0, 255);
      int i2 = MathHelper.clamp_int((int)(green * 255.0F), 0, 255);
      int i3 = MathHelper.clamp_int((int)(blue * 255.0F), 0, 255);
      this.putColorRGBA(colorIndex, i, i2, i3, 255);
   }

   public boolean isMultiTexture() {
      return this.quadSprites != null;
   }

   public int getColorIndex(int vertexIndex) {
      return ((this.vertexCount - vertexIndex) * this.vertexFormat.getNextOffset() + this.vertexFormat.getColorOffset()) / 4;
   }

   public WorldRenderer(int bufferSizeIn) {
      this.byteBuffer = GLAllocation.createDirectByteBuffer(bufferSizeIn * 4);
      this.rawIntBuffer = this.byteBuffer.asIntBuffer();
      this.rawShortBuffer = this.byteBuffer.asShortBuffer();
      this.rawFloatBuffer = this.byteBuffer.asFloatBuffer();
      SVertexBuilder.initVertexBuilder(this);
   }

   public int getVertexCount() {
      return this.modeTriangles ? this.vertexCount / 4 * 6 : this.vertexCount;
   }

   public RenderEnv getRenderEnv(IBlockState blockStateIn, BlockPos blockPosIn) {
      if (this.renderEnv == null) {
         this.renderEnv = new RenderEnv(blockStateIn, blockPosIn);
         return this.renderEnv;
      } else {
         this.renderEnv.reset(blockStateIn, blockPosIn);
         return this.renderEnv;
      }
   }

   public void quadsToTriangles() {
      if (this.drawMode == 7) {
         if (this.byteBufferTriangles == null) {
            this.byteBufferTriangles = GLAllocation.createDirectByteBuffer(this.byteBuffer.capacity() * 2);
         }

         if (this.byteBufferTriangles.capacity() < this.byteBuffer.capacity() * 2) {
            this.byteBufferTriangles = GLAllocation.createDirectByteBuffer(this.byteBuffer.capacity() * 2);
         }

         int nextOffset = this.vertexFormat.getNextOffset();
         int i = this.byteBuffer.limit();
         this.byteBuffer.rewind();
         this.byteBufferTriangles.clear();

         for (int nestedIndex = 0; nestedIndex < this.vertexCount; nestedIndex += 4) {
            this.byteBuffer.limit((nestedIndex + 3) * nextOffset);
            this.byteBuffer.position(nestedIndex * nextOffset);
            this.byteBufferTriangles.put(this.byteBuffer);
            this.byteBuffer.limit((nestedIndex + 1) * nextOffset);
            this.byteBuffer.position(nestedIndex * nextOffset);
            this.byteBufferTriangles.put(this.byteBuffer);
            this.byteBuffer.limit((nestedIndex + 2 + 2) * nextOffset);
            this.byteBuffer.position((nestedIndex + 2) * nextOffset);
            this.byteBufferTriangles.put(this.byteBuffer);
         }

         this.byteBuffer.limit(i);
         this.byteBuffer.rewind();
         this.byteBufferTriangles.flip();
         this.modeTriangles = true;
      }
   }

   public WorldRenderer tex(double u, double v) {
      if (this.quadSprite != null && this.quadSprites != null) {
         u = this.quadSprite.toSingleU((float)u);
         v = this.quadSprite.toSingleV((float)v);
         this.quadSprites[this.vertexCount / 4] = this.quadSprite;
      }

      int i = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch (this.vertexFormatElement.getType()) {
         case FLOAT:
            this.byteBuffer.putFloat(i, (float)u);
            this.byteBuffer.putFloat(i + 4, (float)v);
            break;
         case UINT:
         case INT:
            this.byteBuffer.putInt(i, (int)u);
            this.byteBuffer.putInt(i + 4, (int)v);
            break;
         case USHORT:
         case SHORT:
            this.byteBuffer.putShort(i, (short)v);
            this.byteBuffer.putShort(i + 2, (short)u);
            break;
         case UBYTE:
         case BYTE:
            this.byteBuffer.put(i, (byte)v);
            this.byteBuffer.put(i + 1, (byte)u);
      }

      this.nextVertexFormatIndex();
      return this;
   }

   public void putBrightness4(int brightness1, int brightness2, int brightness3, int brightness4) {
      int i = (this.vertexCount - 4) * this.vertexFormat.getIntegerSize() + this.vertexFormat.getUvOffsetById(1) / 4;
      int i2 = this.vertexFormat.getNextOffset() >> 2;
      this.rawIntBuffer.put(i, brightness1);
      this.rawIntBuffer.put(i + i2, brightness2);
      this.rawIntBuffer.put(i + i2 * 2, brightness3);
      this.rawIntBuffer.put(i + i2 * 3, brightness4);
   }

   public WorldRenderer color(int red, int green, int blue, int alpha) {
      if (this.noColor) {
         return this;
      }

      int i = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch (this.vertexFormatElement.getType()) {
         case FLOAT:
            this.byteBuffer.putFloat(i, red / 255.0F);
            this.byteBuffer.putFloat(i + 4, green / 255.0F);
            this.byteBuffer.putFloat(i + 8, blue / 255.0F);
            this.byteBuffer.putFloat(i + 12, alpha / 255.0F);
            break;
         case UINT:
         case INT:
            this.byteBuffer.putFloat(i, red);
            this.byteBuffer.putFloat(i + 4, green);
            this.byteBuffer.putFloat(i + 8, blue);
            this.byteBuffer.putFloat(i + 12, alpha);
            break;
         case USHORT:
         case SHORT:
            this.byteBuffer.putShort(i, (short)red);
            this.byteBuffer.putShort(i + 2, (short)green);
            this.byteBuffer.putShort(i + 4, (short)blue);
            this.byteBuffer.putShort(i + 6, (short)alpha);
            break;
         case UBYTE:
         case BYTE:
            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
               this.byteBuffer.put(i, (byte)red);
               this.byteBuffer.put(i + 1, (byte)green);
               this.byteBuffer.put(i + 2, (byte)blue);
               this.byteBuffer.put(i + 3, (byte)alpha);
            } else {
               this.byteBuffer.put(i, (byte)alpha);
               this.byteBuffer.put(i + 1, (byte)blue);
               this.byteBuffer.put(i + 2, (byte)green);
               this.byteBuffer.put(i + 3, (byte)red);
            }
      }

      this.nextVertexFormatIndex();
      return this;
   }

   private void nextVertexFormatIndex() {
      this.vertexFormatIndex++;
      this.vertexFormatIndex = this.vertexFormatIndex % this.vertexFormat.getElementCount();
      this.vertexFormatElement = this.vertexFormat.getElement(this.vertexFormatIndex);
      if (this.vertexFormatElement.getUsage() == net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.PADDING) {
         this.nextVertexFormatIndex();
      }
   }

   public void putNormal(float x, float y, float z) {
      int i = (byte)(x * 127.0F) & 255;
      int i2 = (byte)(y * 127.0F) & 255;
      int i3 = (byte)(z * 127.0F) & 255;
      int i4 = i | i2 << 8 | i3 << 16;
      int i5 = this.vertexFormat.getNextOffset() >> 2;
      int i6 = (this.vertexCount - 4) * i5 + this.vertexFormat.getNormalOffset() / 4;
      this.rawIntBuffer.put(i6, i4);
      this.rawIntBuffer.put(i6 + i5, i4);
      this.rawIntBuffer.put(i6 + i5 * 2, i4);
      this.rawIntBuffer.put(i6 + i5 * 3, i4);
   }

   public VertexFormat getVertexFormat() {
      return this.vertexFormat;
   }

   public double getZOffset() {
      return this.zOffset;
   }

   public void reset() {
      this.vertexCount = 0;
      this.vertexFormatElement = null;
      this.vertexFormatIndex = 0;
      this.quadSprite = null;
      if (SmartAnimations.isActive()) {
         if (this.animatedSprites == null) {
            this.animatedSprites = this.animatedSpritesCached;
         }

         this.animatedSprites.clear();
      } else if (this.animatedSprites != null) {
         this.animatedSprites = null;
      }

      this.modeTriangles = false;
   }

   private int getBufferQuadSize() {
      return this.rawIntBuffer.capacity() * 4 / (this.vertexFormat.getIntegerSize() * 4);
   }

   public boolean isDrawing() {
      return this.isDrawing;
   }

   public void setVertexState(WorldRenderer.State state) {
      this.rawIntBuffer.clear();
      this.growBuffer(state.getRawBuffer().length);
      this.rawIntBuffer.put(state.getRawBuffer());
      this.vertexCount = state.getVertexCount();
      this.vertexFormat = new VertexFormat(state.getVertexFormat());
      if (state.stateQuadSprites != null) {
         if (this.quadSprites == null) {
            this.quadSprites = this.quadSpritesPrev;
         }

         if (this.quadSprites == null || this.quadSprites.length < this.getBufferQuadSize()) {
            this.quadSprites = new TextureAtlasSprite[this.getBufferQuadSize()];
         }

         TextureAtlasSprite[] atextureatlassprite = state.stateQuadSprites;
         System.arraycopy(atextureatlassprite, 0, this.quadSprites, 0, atextureatlassprite.length);
      } else {
         if (this.quadSprites != null) {
            this.quadSpritesPrev = this.quadSprites;
         }

         this.quadSprites = null;
      }
   }

   public void endVertex() {
      this.vertexCount++;
      this.growBuffer(this.vertexFormat.getIntegerSize());
      this.vertexFormatIndex = 0;
      this.vertexFormatElement = this.vertexFormat.getElement(this.vertexFormatIndex);
      if (Config.isShaders()) {
         SVertexBuilder.endAddVertex(this);
      }
   }

   public void sortVertexData(float cameraX, float cameraY, float cameraZ) {
      int i = this.vertexCount / 4;
      final float[] fArray = new float[i];

      for (int i2 = 0; i2 < i; i2++) {
         fArray[i2] = getDistanceSq(
            this.rawFloatBuffer,
            (float)(cameraX + this.xOffset),
            (float)(cameraY + this.yOffset),
            (float)(cameraZ + this.zOffset),
            this.vertexFormat.getIntegerSize(),
            i2 * this.vertexFormat.getNextOffset()
         );
      }

      Integer[] integers = new Integer[i];

      for (int outerIndex = 0; outerIndex < integers.length; outerIndex++) {
         integers[outerIndex] = outerIndex;
      }

      Arrays.sort(integers, new Comparator<Integer>() {
         public int compare(Integer left, Integer right) {
            return Floats.compare(fArray[right], fArray[left]);
         }
      });
      BitSet bitSet = new BitSet();
      int nextOffset = this.vertexFormat.getNextOffset();
      int[] iArray = new int[nextOffset];

      for (int i3 = 0; (i3 = bitSet.nextClearBit(i3)) < integers.length; i3++) {
         int i4 = integers[i3];
         if (i4 != i3) {
            this.rawIntBuffer.limit(i4 * nextOffset + nextOffset);
            this.rawIntBuffer.position(i4 * nextOffset);
            this.rawIntBuffer.get(iArray);
            int i5 = i4;

            for (int i6 = integers[i4]; i5 != i3; i6 = integers[i6]) {
               this.rawIntBuffer.limit(i6 * nextOffset + nextOffset);
               this.rawIntBuffer.position(i6 * nextOffset);
               IntBuffer intBuffer = this.rawIntBuffer.slice();
               this.rawIntBuffer.limit(i5 * nextOffset + nextOffset);
               this.rawIntBuffer.position(i5 * nextOffset);
               this.rawIntBuffer.put(intBuffer);
               bitSet.set(i5);
               i5 = i6;
            }

            this.rawIntBuffer.limit(i3 * nextOffset + nextOffset);
            this.rawIntBuffer.position(i3 * nextOffset);
            this.rawIntBuffer.put(iArray);
         }

         bitSet.set(i3);
      }

      this.rawIntBuffer.limit(this.rawIntBuffer.capacity());
      this.rawIntBuffer.position(this.getBufferSize());
      if (this.quadSprites != null) {
         TextureAtlasSprite[] textureAtlasSprites = new TextureAtlasSprite[this.vertexCount / 4];
         int i7 = this.vertexFormat.getNextOffset() / 4;

         for (int i8 = 0; i8 < integers.length; i8++) {
            int i9 = integers[i8];
            textureAtlasSprites[i8] = this.quadSprites[i9];
         }

         System.arraycopy(textureAtlasSprites, 0, this.quadSprites, 0, textureAtlasSprites.length);
      }
   }

   public int getDrawMode() {
      return this.modeTriangles ? 4 : this.drawMode;
   }

   public void addVertexData(int[] vertexData) {
      if (Config.isShaders()) {
         SVertexBuilder.beginAddVertexData(this, vertexData);
      }

      this.growBuffer(vertexData.length);
      this.rawIntBuffer.position(this.getBufferSize());
      this.rawIntBuffer.put(vertexData);
      this.vertexCount = this.vertexCount + vertexData.length / this.vertexFormat.getIntegerSize();
      if (Config.isShaders()) {
         SVertexBuilder.endAddVertexData(this);
      }
   }

   public WorldRenderer color(float red, float green, float blue, float alpha) {
      return this.color((int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F), (int)(alpha * 255.0F));
   }

   public WorldRenderer.State getVertexState() {
      this.rawIntBuffer.rewind();
      int bufferSize = this.getBufferSize();
      this.rawIntBuffer.limit(bufferSize);
      int[] intArray = new int[bufferSize];
      this.rawIntBuffer.get(intArray);
      this.rawIntBuffer.limit(this.rawIntBuffer.capacity());
      this.rawIntBuffer.position(bufferSize);
      TextureAtlasSprite[] atextureatlassprite = null;
      if (this.quadSprites != null) {
         int i = this.vertexCount / 4;
         atextureatlassprite = new TextureAtlasSprite[i];
         System.arraycopy(this.quadSprites, 0, atextureatlassprite, 0, i);
      }

      return new WorldRenderer.State(intArray, new VertexFormat(this.vertexFormat), atextureatlassprite);
   }

   public void putColorRGBA(int index, int red, int green, int blue, int alpha) {
      if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
         this.rawIntBuffer.put(index, alpha << 24 | blue << 16 | green << 8 | red);
      } else {
         this.rawIntBuffer.put(index, red << 24 | green << 16 | blue << 8 | alpha);
      }
   }

   public void finishDrawing() {
      if (!this.isDrawing) {
         throw new IllegalStateException("Not building!");
      }

      this.isDrawing = false;
      this.byteBuffer.position(0);
      this.byteBuffer.limit(this.getBufferSize() * 4);
   }

   public double getXOffset() {
      return this.xOffset;
   }

   public class State {
      private TextureAtlasSprite[] stateQuadSprites;
      private final int[] stateRawBuffer;
      private final VertexFormat stateVertexFormat;

      public int getVertexCount() {
         return this.stateRawBuffer.length / this.stateVertexFormat.getIntegerSize();
      }

      public int[] getRawBuffer() {
         return this.stateRawBuffer;
      }

      public VertexFormat getVertexFormat() {
         return this.stateVertexFormat;
      }

      public State(int[] buffer, VertexFormat format) {
         this.stateRawBuffer = buffer;
         this.stateVertexFormat = format;
      }

      public State(int[] buffer, VertexFormat format, TextureAtlasSprite[] quadSpritesIn) {
         this.stateRawBuffer = buffer;
         this.stateVertexFormat = format;
         this.stateQuadSprites = quadSpritesIn;
      }
   }
}
