package net.optifine.shaders;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL20;

public class SVertexBuilder {
   boolean hasUV;
   boolean hasNormal;
   int offsetUV;
   long[] entityData = new long[10];
   boolean hasUVCenter;
   int offsetNormal;
   int vertexSize;
   boolean hasTangent;
   int entityDataIndex = 0;
   int offsetUVCenter;

   public SVertexBuilder() {
      this.entityData[this.entityDataIndex] = 0L;
   }

   public static void initVertexBuilder(WorldRenderer wrr) {
      wrr.sVertexBuilder = new SVertexBuilder();
   }

   public void pushEntity(long data) {
      this.entityDataIndex++;
      this.entityData[this.entityDataIndex] = data;
   }

   public void calcNormal(WorldRenderer wrr, int baseIndex) {
      FloatBuffer rawFloatBuffer2 = wrr.rawFloatBuffer;
      IntBuffer rawIntBuffer2 = wrr.rawIntBuffer;
      wrr.getBufferSize();
      float f = rawFloatBuffer2.get(baseIndex + 0 * this.vertexSize);
      float f2 = rawFloatBuffer2.get(baseIndex + 0 * this.vertexSize + 1);
      float f3 = rawFloatBuffer2.get(baseIndex + 0 * this.vertexSize + 2);
      float f4 = rawFloatBuffer2.get(baseIndex + 0 * this.vertexSize + this.offsetUV);
      float f5 = rawFloatBuffer2.get(baseIndex + 0 * this.vertexSize + this.offsetUV + 1);
      float f6 = rawFloatBuffer2.get(baseIndex + 1 * this.vertexSize);
      float f7 = rawFloatBuffer2.get(baseIndex + 1 * this.vertexSize + 1);
      float f8 = rawFloatBuffer2.get(baseIndex + 1 * this.vertexSize + 2);
      float f9 = rawFloatBuffer2.get(baseIndex + 1 * this.vertexSize + this.offsetUV);
      float f10 = rawFloatBuffer2.get(baseIndex + 1 * this.vertexSize + this.offsetUV + 1);
      float f11 = rawFloatBuffer2.get(baseIndex + 2 * this.vertexSize);
      float f12 = rawFloatBuffer2.get(baseIndex + 2 * this.vertexSize + 1);
      float f13 = rawFloatBuffer2.get(baseIndex + 2 * this.vertexSize + 2);
      float f14 = rawFloatBuffer2.get(baseIndex + 2 * this.vertexSize + this.offsetUV);
      float f15 = rawFloatBuffer2.get(baseIndex + 2 * this.vertexSize + this.offsetUV + 1);
      float f16 = rawFloatBuffer2.get(baseIndex + 3 * this.vertexSize);
      float f17 = rawFloatBuffer2.get(baseIndex + 3 * this.vertexSize + 1);
      float f18 = rawFloatBuffer2.get(baseIndex + 3 * this.vertexSize + 2);
      float f19 = rawFloatBuffer2.get(baseIndex + 3 * this.vertexSize + this.offsetUV);
      float f20 = rawFloatBuffer2.get(baseIndex + 3 * this.vertexSize + this.offsetUV + 1);
      float f21 = f11 - f;
      float f22 = f12 - f2;
      float f23 = f13 - f3;
      float f24 = f16 - f6;
      float f25 = f17 - f7;
      float f26 = f18 - f8;
      float f27 = f22 * f26 - f25 * f23;
      float f28 = f23 * f24 - f26 * f21;
      float f29 = f21 * f25 - f24 * f22;
      float f30 = f27 * f27 + f28 * f28 + f29 * f29;
      float f31 = f30 != 0.0 ? (float)(1.0 / Math.sqrt(f30)) : 1.0F;
      f27 *= f31;
      f28 *= f31;
      f29 *= f31;
      f21 = f6 - f;
      f22 = f7 - f2;
      f23 = f8 - f3;
      float f32 = f9 - f4;
      float f33 = f10 - f5;
      f24 = f11 - f;
      f25 = f12 - f2;
      f26 = f13 - f3;
      float f34 = f14 - f4;
      float f35 = f15 - f5;
      float f36 = f32 * f35 - f34 * f33;
      float f37 = f36 != 0.0F ? 1.0F / f36 : 1.0F;
      float f38 = (f35 * f21 - f33 * f24) * f37;
      float f39 = (f35 * f22 - f33 * f25) * f37;
      float f40 = (f35 * f23 - f33 * f26) * f37;
      float f41 = (f32 * f24 - f34 * f21) * f37;
      float f42 = (f32 * f25 - f34 * f22) * f37;
      float f43 = (f32 * f26 - f34 * f23) * f37;
      f30 = f38 * f38 + f39 * f39 + f40 * f40;
      f31 = f30 != 0.0 ? (float)(1.0 / Math.sqrt(f30)) : 1.0F;
      f38 *= f31;
      f39 *= f31;
      f40 *= f31;
      f30 = f41 * f41 + f42 * f42 + f43 * f43;
      f31 = f30 != 0.0 ? (float)(1.0 / Math.sqrt(f30)) : 1.0F;
      f41 *= f31;
      f42 *= f31;
      f43 *= f31;
      float f44 = f29 * f39 - f28 * f40;
      float f45 = f27 * f40 - f29 * f38;
      float f46 = f28 * f38 - f27 * f39;
      float f47 = f41 * f44 + f42 * f45 + f43 * f46 < 0.0F ? -1.0F : 1.0F;
      int i = (int)(f27 * 127.0F) & 0xFF;
      int i2 = (int)(f28 * 127.0F) & 0xFF;
      int i3 = (((int)(f29 * 127.0F) & 0xFF) << 16) + (i2 << 8) + i;
      rawIntBuffer2.put(baseIndex + 0 * this.vertexSize + this.offsetNormal, i3);
      rawIntBuffer2.put(baseIndex + 1 * this.vertexSize + this.offsetNormal, i3);
      rawIntBuffer2.put(baseIndex + 2 * this.vertexSize + this.offsetNormal, i3);
      rawIntBuffer2.put(baseIndex + 3 * this.vertexSize + this.offsetNormal, i3);
      int i4 = ((int)(f38 * 32767.0F) & 65535) + (((int)(f39 * 32767.0F) & 65535) << 16);
      int i5 = ((int)(f40 * 32767.0F) & 65535) + (((int)(f47 * 32767.0F) & 65535) << 16);
      rawIntBuffer2.put(baseIndex + 0 * this.vertexSize + 10, i4);
      rawIntBuffer2.put(baseIndex + 0 * this.vertexSize + 10 + 1, i5);
      rawIntBuffer2.put(baseIndex + 1 * this.vertexSize + 10, i4);
      rawIntBuffer2.put(baseIndex + 1 * this.vertexSize + 10 + 1, i5);
      rawIntBuffer2.put(baseIndex + 2 * this.vertexSize + 10, i4);
      rawIntBuffer2.put(baseIndex + 2 * this.vertexSize + 10 + 1, i5);
      rawIntBuffer2.put(baseIndex + 3 * this.vertexSize + 10, i4);
      rawIntBuffer2.put(baseIndex + 3 * this.vertexSize + 10 + 1, i5);
      float f48 = (f4 + f9 + f14 + f19) / 4.0F;
      float f49 = (f5 + f10 + f15 + f20) / 4.0F;
      rawFloatBuffer2.put(baseIndex + 0 * this.vertexSize + 8, f48);
      rawFloatBuffer2.put(baseIndex + 0 * this.vertexSize + 8 + 1, f49);
      rawFloatBuffer2.put(baseIndex + 1 * this.vertexSize + 8, f48);
      rawFloatBuffer2.put(baseIndex + 1 * this.vertexSize + 8 + 1, f49);
      rawFloatBuffer2.put(baseIndex + 2 * this.vertexSize + 8, f48);
      rawFloatBuffer2.put(baseIndex + 2 * this.vertexSize + 8 + 1, f49);
      rawFloatBuffer2.put(baseIndex + 3 * this.vertexSize + 8, f48);
      rawFloatBuffer2.put(baseIndex + 3 * this.vertexSize + 8 + 1, f49);
   }

   public static void beginAddVertex(WorldRenderer wrr) {
      if (wrr.vertexCount == 0) {
         endSetVertexFormat(wrr);
      }
   }

   public static void endAddVertex(WorldRenderer wrr) {
      SVertexBuilder sVertexBuilder = wrr.sVertexBuilder;
      if (sVertexBuilder.vertexSize == 14) {
         if (wrr.drawMode == 7 && wrr.vertexCount % 4 == 0) {
            sVertexBuilder.calcNormal(wrr, wrr.getBufferSize() - 4 * sVertexBuilder.vertexSize);
         }

         long l = sVertexBuilder.entityData[sVertexBuilder.entityDataIndex];
         int i = wrr.getBufferSize() - 14 + 12;
         wrr.rawIntBuffer.put(i, (int)l);
         wrr.rawIntBuffer.put(i + 1, (int)(l >> 32));
      }
   }

   public static void popEntity(WorldRenderer wrr) {
      wrr.sVertexBuilder.popEntity();
   }

   public static void beginAddVertexData(WorldRenderer wrr, int[] data) {
      if (wrr.vertexCount == 0) {
         endSetVertexFormat(wrr);
      }

      SVertexBuilder sVertexBuilder = wrr.sVertexBuilder;
      if (sVertexBuilder.vertexSize == 14) {
         long l = sVertexBuilder.entityData[sVertexBuilder.entityDataIndex];

         for (int innerIndex = 12; innerIndex + 1 < data.length; innerIndex += 14) {
            data[innerIndex] = (int)l;
            data[innerIndex + 1] = (int)(l >> 32);
         }
      }
   }

   public static void handleInt(int i, int i2, WorldRenderer worldRenderer) {
      if (i2 != 0) {
         int nextOffset = worldRenderer.getVertexFormat().getNextOffset();
         if (nextOffset == 56) {
            ByteBuffer byteBuffer = worldRenderer.getByteBuffer();
            byteBuffer.position(32);
            GL20.glVertexAttribPointer(Shaders.midTexCoordAttrib, 2, 5126, false, nextOffset, byteBuffer);
            byteBuffer.position(40);
            GL20.glVertexAttribPointer(Shaders.tangentAttrib, 4, 5122, false, nextOffset, byteBuffer);
            byteBuffer.position(48);
            GL20.glVertexAttribPointer(Shaders.entityAttrib, 3, 5122, false, nextOffset, byteBuffer);
            byteBuffer.position(0);
            GL20.glEnableVertexAttribArray(Shaders.midTexCoordAttrib);
            GL20.glEnableVertexAttribArray(Shaders.tangentAttrib);
            GL20.glEnableVertexAttribArray(Shaders.entityAttrib);
            GlStateManager.glNewList(i, i2);
            GL20.glDisableVertexAttribArray(Shaders.midTexCoordAttrib);
            GL20.glDisableVertexAttribArray(Shaders.tangentAttrib);
            GL20.glDisableVertexAttribArray(Shaders.entityAttrib);
         } else {
            GlStateManager.glNewList(i, i2);
         }
      }
   }

   public static void calcNormalChunkLayer(WorldRenderer wrr) {
      if (wrr.getVertexFormat().hasNormal() && wrr.drawMode == 7 && wrr.vertexCount % 4 == 0) {
         SVertexBuilder sVertexBuilder = wrr.sVertexBuilder;
         endSetVertexFormat(wrr);
         int i = wrr.vertexCount * sVertexBuilder.vertexSize;

         for (int innerIndex = 0; innerIndex < i; innerIndex += sVertexBuilder.vertexSize * 4) {
            sVertexBuilder.calcNormal(wrr, innerIndex);
         }
      }
   }

   public void popEntity() {
      this.entityData[this.entityDataIndex] = 0L;
      this.entityDataIndex--;
   }

   public static void endAddVertexData(WorldRenderer wrr) {
      SVertexBuilder sVertexBuilder = wrr.sVertexBuilder;
      if (sVertexBuilder.vertexSize == 14 && wrr.drawMode == 7 && wrr.vertexCount % 4 == 0) {
         sVertexBuilder.calcNormal(wrr, wrr.getBufferSize() - 4 * sVertexBuilder.vertexSize);
      }
   }

   public static void endSetVertexFormat(WorldRenderer wrr) {
      SVertexBuilder sVertexBuilder = wrr.sVertexBuilder;
      VertexFormat vertexFormat = wrr.getVertexFormat();
      sVertexBuilder.vertexSize = vertexFormat.getNextOffset() / 4;
      sVertexBuilder.hasNormal = vertexFormat.hasNormal();
      sVertexBuilder.hasTangent = sVertexBuilder.hasNormal;
      sVertexBuilder.hasUV = vertexFormat.hasColor();
      sVertexBuilder.offsetNormal = sVertexBuilder.hasNormal ? vertexFormat.getNormalOffset() / 4 : 0;
      sVertexBuilder.offsetUV = sVertexBuilder.hasUV ? vertexFormat.getUvOffsetById(0) / 4 : 0;
      sVertexBuilder.offsetUVCenter = 8;
   }

   public static void pushEntity(IBlockState blockState, BlockPos blockPos, IBlockAccess blockAccess, WorldRenderer wrr) {
      Block block2 = blockState.getBlock();
      int i;
      int i2;
      if (blockState instanceof BlockStateBase blockStateBase) {
         i = blockStateBase.getBlockId();
         i2 = blockStateBase.getMetadata();
      } else {
         i = Block.getIdFromBlock(block2);
         i2 = block2.getMetaFromState(blockState);
      }

      int blockAliasId = BlockAliases.getBlockAliasId(i, i2);
      if (blockAliasId >= 0) {
         i = blockAliasId;
      }

      int i3 = ((block2.getRenderType() & 65535) << 16) + (i & 65535);
      int i4 = i2 & 65535;
      wrr.sVertexBuilder.pushEntity(((long)i4 << 32) + i3);
   }
}
