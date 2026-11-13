package net.minecraft.client.renderer;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.optifine.render.VboRegion;

public class ViewFrustum {
   protected int countChunksY;
   private Map<ChunkCoordIntPair, VboRegion[]> mapVboRegions = new HashMap<>();
   protected final RenderGlobal renderGlobal;
   protected int countChunksX;
   public RenderChunk[] renderChunks;
   protected int countChunksZ;
   protected final World world;

   public RenderChunk getRenderChunk(BlockPos pos) {
      int i = pos.getX() >> 4;
      int yCoordinate = pos.getY() >> 4;
      int i2 = pos.getZ() >> 4;
      if (yCoordinate >= 0 && yCoordinate < this.countChunksY) {
         i %= this.countChunksX;
         if (i < 0) {
            i += this.countChunksX;
         }

         i2 %= this.countChunksZ;
         if (i2 < 0) {
            i2 += this.countChunksZ;
         }

         int i3 = (i2 * this.countChunksY + yCoordinate) * this.countChunksX + i;
         return this.renderChunks[i3];
      } else {
         return null;
      }
   }

   private int getBaseCoordinate(int viewCoordinate, int viewDistance, int index) {
      int i = index * 16;
      int i2 = i - viewCoordinate + viewDistance / 2;
      if (i2 < 0) {
         i2 -= viewDistance - 1;
      }

      return i - i2 / viewDistance * viewDistance;
   }

   public ViewFrustum(World worldIn, int renderDistanceChunks, RenderGlobal renderGlobal2, IRenderChunkFactory renderChunkFactory) {
      this.renderGlobal = renderGlobal2;
      this.world = worldIn;
      this.setCountChunksXYZ(renderDistanceChunks);
      this.createRenderChunks(renderChunkFactory);
   }

   public void updateChunkPositions(double viewEntityX, double viewEntityZ) {
      int i = MathHelper.floor_double(viewEntityX) - 8;
      int i2 = MathHelper.floor_double(viewEntityZ) - 8;
      int i3 = this.countChunksX * 16;

      for (int outerIndex = 0; outerIndex < this.countChunksX; outerIndex++) {
         int baseCoordinate = this.getBaseCoordinate(i, i3, outerIndex);

         for (int innerIndex = 0; innerIndex < this.countChunksZ; innerIndex++) {
            int baseCoordinate2 = this.getBaseCoordinate(i2, i3, innerIndex);

            for (int outerIndex2 = 0; outerIndex2 < this.countChunksY; outerIndex2++) {
               int i4 = outerIndex2 * 16;
               RenderChunk renderChunk = this.renderChunks[(innerIndex * this.countChunksY + outerIndex2) * this.countChunksX + outerIndex];
               BlockPos blockPos = renderChunk.getPosition();
               if (blockPos.getX() != baseCoordinate || blockPos.getY() != i4 || blockPos.getZ() != baseCoordinate2) {
                  BlockPos blockPos3 = new BlockPos(baseCoordinate, i4, baseCoordinate2);
                  if (!blockPos3.equals(renderChunk.getPosition())) {
                     renderChunk.setPosition(blockPos3);
                  }
               }
            }
         }
      }
   }

   protected void setCountChunksXYZ(int renderDistanceChunks) {
      int i = renderDistanceChunks * 2 + 1;
      this.countChunksX = i;
      this.countChunksY = 16;
      this.countChunksZ = i;
   }

   protected void createRenderChunks(IRenderChunkFactory renderChunkFactory) {
      int i = this.countChunksX * this.countChunksY * this.countChunksZ;
      this.renderChunks = new RenderChunk[i];
      int i2 = 0;

      for (int nestedIndex = 0; nestedIndex < this.countChunksX; nestedIndex++) {
         for (int outerIndex = 0; outerIndex < this.countChunksY; outerIndex++) {
            for (int index = 0; index < this.countChunksZ; index++) {
               int i3 = (index * this.countChunksY + outerIndex) * this.countChunksX + nestedIndex;
               BlockPos blockPos2 = new BlockPos(nestedIndex * 16, outerIndex * 16, index * 16);
               this.renderChunks[i3] = renderChunkFactory.makeRenderChunk(this.world, this.renderGlobal, blockPos2, i2++);
               if (Config.isVbo() && Config.isRenderRegions()) {
                  this.updateVboRegion(this.renderChunks[i3]);
               }
            }
         }
      }

      for (int nestedIndex2 = 0; nestedIndex2 < this.renderChunks.length; nestedIndex2++) {
         RenderChunk renderChunk = this.renderChunks[nestedIndex2];

         for (int outerIndex2 = 0; outerIndex2 < EnumFacing.VALUES.length; outerIndex2++) {
            EnumFacing enumFacing = EnumFacing.VALUES[outerIndex2];
            BlockPos blockPos2 = renderChunk.getBlockPosOffset16(enumFacing);
            RenderChunk renderChunk2 = this.getRenderChunk(blockPos2);
            renderChunk.setRenderChunkNeighbour(enumFacing, renderChunk2);
         }
      }
   }

   public void deleteVboRegions() {
      for (ChunkCoordIntPair chunkCoordIntPair : this.mapVboRegions.keySet()) {
         VboRegion[] vboRegions = this.mapVboRegions.get(chunkCoordIntPair);

         for (int i = 0; i < vboRegions.length; i++) {
            VboRegion vboRegion = vboRegions[i];
            if (vboRegion != null) {
               vboRegion.deleteGlBuffers();
            }

            vboRegions[i] = null;
         }
      }

      this.mapVboRegions.clear();
   }

   private void updateVboRegion(RenderChunk arg1) {
      BlockPos blockPos = arg1.getPosition();
      int xCoordinate = blockPos.getX() >> 8 << 8;
      int zCoordinate = blockPos.getZ() >> 8 << 8;
      ChunkCoordIntPair chunkCoordIntPair = new ChunkCoordIntPair(xCoordinate, zCoordinate);
      EnumWorldBlockLayer[] worldBlockLayerArray = RenderChunk.ENUM_WORLD_BLOCK_LAYERS;
      VboRegion[] avboregion = this.mapVboRegions.get(chunkCoordIntPair);
      if (avboregion == null) {
         avboregion = new VboRegion[worldBlockLayerArray.length];

         for (int nestedIndex = 0; nestedIndex < worldBlockLayerArray.length; nestedIndex++) {
            avboregion[nestedIndex] = new VboRegion(worldBlockLayerArray[nestedIndex]);
         }

         this.mapVboRegions.put(chunkCoordIntPair, avboregion);
      }

      for (int outerIndex = 0; outerIndex < worldBlockLayerArray.length; outerIndex++) {
         VboRegion vboRegion = avboregion[outerIndex];
         if (vboRegion != null) {
            arg1.getVertexBufferByLayer(outerIndex).setVboRegion(vboRegion);
         }
      }
   }

   public void deleteGlResources() {
      RenderChunk[] renderChunks2 = this.renderChunks;
      int length2 = renderChunks2.length;

      for (int i = 0; i < length2; i++) {
         renderChunks2[i].deleteGlResources();
      }

      this.deleteVboRegions();
   }

   public void markBlocksForUpdate(int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
      int intValue = MathHelper.createInt(fromX);
      int nestedIndex = MathHelper.createInt(fromY);
      int intValue2 = MathHelper.createInt(fromZ);
      int index = MathHelper.createInt(toX);
      int intValue3 = MathHelper.createInt(toY);
      int index2 = MathHelper.createInt(toZ);

      for (int i = intValue; i <= index; i++) {
         int i2 = i % this.countChunksX;
         if (i2 < 0) {
            i2 += this.countChunksX;
         }

         for (int nestedIndex3 = nestedIndex; nestedIndex3 <= intValue3; nestedIndex3++) {
            int i3 = nestedIndex3 % this.countChunksY;
            if (i3 < 0) {
               i3 += this.countChunksY;
            }

            for (int i4 = intValue2; i4 <= index2; i4++) {
               int i5 = i4 % this.countChunksZ;
               if (i5 < 0) {
                  i5 += this.countChunksZ;
               }

               int i6 = (i5 * this.countChunksY + i3) * this.countChunksX + i2;
               this.renderChunks[i6].setNeedsUpdate(true);
            }
         }
      }
   }
}
