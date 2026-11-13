package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Sets;
import java.nio.FloatBuffer;
import java.util.BitSet;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RegionRenderCache;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.optifine.BlockPosM;
import net.optifine.CustomBlockLayers;
import net.optifine.override.ChunkCacheOF;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.render.AabbFrame;
import net.optifine.render.RenderEnv;
import net.optifine.shaders.SVertexBuilder;

public class RenderChunk {
   public static int renderChunksUpdated;
   public CompiledChunk compiledChunk = CompiledChunk.DUMMY;
   private final ReentrantLock lockCompileTask = new ReentrantLock();
   private final ReentrantLock lockCompiledChunk = new ReentrantLock();
   private ChunkCompileTaskGenerator compileTask = null;
   private final Set<TileEntity> setTileEntities = Sets.newHashSet();
   private BlockPos position;
   private final FloatBuffer modelviewMatrix = GLAllocation.createDirectFloatBuffer(16);
   private final VertexBuffer[] vertexBuffers = new VertexBuffer[EnumWorldBlockLayer.values().length];
   private int frameIndex = -1;
   private boolean needsUpdate = true;
   private EnumMap<EnumFacing, BlockPos> mapEnumFacing = null;
   private BlockPos[] positionOffsets16 = new BlockPos[EnumFacing.VALUES.length];
   public int regionZ;
   private final EnumWorldBlockLayer[] blockLayersSingle = new EnumWorldBlockLayer[1];
   private final boolean isMipmaps = Config.isMipmaps();
   private final boolean flag = !Reflector.reflectorClass.isActive();
   public AxisAlignedBB boundingBox;
   private boolean playerUpdate = false;
   private final RenderChunk[] renderChunksOfset16 = new RenderChunk[6];
   private boolean renderChunksOffset16Updated = false;
   private RenderChunk[] renderChunkNeighbours = new RenderChunk[EnumFacing.VALUES.length];
   private RenderChunk[] renderChunkNeighboursValid = new RenderChunk[EnumFacing.VALUES.length];
   private boolean renderChunkNeighboursUpated = false;
   public static final EnumWorldBlockLayer[] ENUM_WORLD_BLOCK_LAYERS = EnumWorldBlockLayer.values();
   private net.minecraft.client.renderer.RenderGlobal.ContainerLocalRenderInformation renderInfo = new net.minecraft.client.renderer.RenderGlobal.ContainerLocalRenderInformation(this, (EnumFacing)null);
   public int regionX;
   private final World world;
   private final RenderGlobal renderGlobal;
   public AabbFrame boundingBoxParent;
   private final int index;
   private Chunk chunk;

   public BlockPos getPositionOffset16(EnumFacing arg1) {
      int index2 = arg1.getIndex();
      BlockPos blockPos = this.positionOffsets16[index2];
      if (blockPos == null) {
         blockPos = this.getPosition().offset(arg1, 16);
         this.positionOffsets16[index2] = blockPos;
      }

      return blockPos;
   }

   @Override
   public String toString() {
      return "pos: " + this.getPosition() + ", frameIndex: " + this.frameIndex;
   }

   private boolean isWorldPlayerUpdate() {
      return this.world instanceof WorldClient ? ((WorldClient)this.world).isPlayerUpdate() : false;
   }

   public void setCompiledChunk(CompiledChunk compiledChunkIn) {
      this.lockCompiledChunk.lock();

      try {
         this.compiledChunk = compiledChunkIn;
      } finally {
         this.lockCompiledChunk.unlock();
      }
   }

   public RenderChunk getRenderChunkNeighbour(EnumFacing arg1) {
      if (!this.renderChunkNeighboursUpated) {
         this.updateRenderChunkNeighboursValid();
      }

      return this.renderChunkNeighboursValid[arg1.ordinal()];
   }

   public AabbFrame getBoundingBoxParent() {
      if (this.boundingBoxParent == null) {
         BlockPos position2 = this.getPosition();
         int x2 = position2.getX();
         int y2 = position2.getY();
         int z2 = position2.getZ();
         int i = x2 >> 5 << 5;
         int i2 = y2 >> 5 << 5;
         int i3 = z2 >> 5 << 5;
         if (i != x2 || i2 != y2 || i3 != z2) {
            AabbFrame boundingBoxParent2 = this.renderGlobal.getRenderChunk(new BlockPos(i, i2, i3)).getBoundingBoxParent();
            if (boundingBoxParent2 != null && boundingBoxParent2.minX == i && boundingBoxParent2.minY == i2 && boundingBoxParent2.minZ == i3) {
               this.boundingBoxParent = boundingBoxParent2;
            }
         }

         if (this.boundingBoxParent == null) {
            this.boundingBoxParent = new AabbFrame(i, i2, i3, i + 32, i2 + 32, i3 + 32);
         }
      }

      return this.boundingBoxParent;
   }

   private EnumWorldBlockLayer fixBlockLayer(IBlockState arg1, EnumWorldBlockLayer arg2) {
      if (CustomBlockLayers.isActive()) {
         EnumWorldBlockLayer worldBlockLayer = CustomBlockLayers.getRenderLayer(arg1);
         if (worldBlockLayer != null) {
            return worldBlockLayer;
         }
      }

      if (!this.flag) {
         return arg2;
      }

      if (this.isMipmaps) {
         if (arg2 == EnumWorldBlockLayer.CUTOUT) {
            Block block = arg1.getBlock();
            if (block instanceof BlockRedstoneWire) {
               return arg2;
            }

            if (block instanceof BlockCactus) {
               return arg2;
            }

            return EnumWorldBlockLayer.CUTOUT_MIPPED;
         }
      } else if (arg2 == EnumWorldBlockLayer.CUTOUT_MIPPED) {
         return EnumWorldBlockLayer.CUTOUT;
      }

      return arg2;
   }

   public BlockPos getBlockPosOffset16(EnumFacing enumFacing) {
      return this.getPositionOffset16(enumFacing);
   }

   private Chunk getChunk(BlockPos arg1) {
      Chunk chunk2 = this.chunk;
      if (chunk2 != null && chunk2.isLoaded()) {
         return chunk2;
      }

      chunk2 = this.world.getChunkFromBlockCoords(arg1);
      this.chunk = chunk2;
      return chunk2;
   }

   private ChunkCacheOF makeChunkCacheOF(BlockPos arg1) {
      BlockPos blockPos2 = arg1.add(-1, -1, -1);
      BlockPos blockPos3 = arg1.add(16, 16, 16);
      RegionRenderCache regionRenderCache = this.getRegionRenderCache(this.world, blockPos2, blockPos3);
      if (Reflector.reflectorMethod101.isActive()) {
         Reflector.createObject2(Reflector.reflectorMethod101, this.world, arg1, regionRenderCache);
      }

      return new ChunkCacheOF(regionRenderCache, blockPos2, blockPos3);
   }

   private void updateRenderChunkNeighboursValid() {
      int xCoordinate = this.getPosition().getX();
      int zCoordinate = this.getPosition().getZ();
      int i = EnumFacing.NORTH.ordinal();
      int i2 = EnumFacing.SOUTH.ordinal();
      int i3 = EnumFacing.WEST.ordinal();
      int i4 = EnumFacing.EAST.ordinal();
      this.renderChunkNeighboursValid[i] = this.renderChunkNeighbours[i].getPosition().getZ() == zCoordinate - 16
         ? this.renderChunkNeighbours[i]
         : null;
      this.renderChunkNeighboursValid[i2] = this.renderChunkNeighbours[i2].getPosition().getZ() == zCoordinate + 16
         ? this.renderChunkNeighbours[i2]
         : null;
      this.renderChunkNeighboursValid[i3] = this.renderChunkNeighbours[i3].getPosition().getX() == xCoordinate - 16
         ? this.renderChunkNeighbours[i3]
         : null;
      this.renderChunkNeighboursValid[i4] = this.renderChunkNeighbours[i4].getPosition().getX() == xCoordinate + 16
         ? this.renderChunkNeighbours[i4]
         : null;
      this.renderChunkNeighboursUpated = true;
   }

   public void rebuildChunk(float x, float y, float z, ChunkCompileTaskGenerator generator) {
      CompiledChunk compiledChunk2 = new CompiledChunk();
      BlockPos blockPos2 = new BlockPos(this.position);
      BlockPos blockPos3 = blockPos2.add(15, 15, 15);
      generator.getLock().lock();

      try {
         if (generator.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING) {
            return;
         }

         generator.setCompiledChunk(compiledChunk2);
      } finally {
         generator.getLock().unlock();
      }

      VisGraph visGraph = new VisGraph();
      HashSet hashSet = Sets.newHashSet();
      if (!this.isChunkRegionEmpty(blockPos2)) {
         renderChunksUpdated++;
         ChunkCacheOF chunkCacheOF = this.makeChunkCacheOF(blockPos2);
         chunkCacheOF.renderStart();
         boolean[] flagArray = new boolean[ENUM_WORLD_BLOCK_LAYERS.length];
         BlockRendererDispatcher blockRendererDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
         boolean active = Reflector.reflectorMethod22.isActive();
         boolean active2 = Reflector.reflectorMethod72.isActive();

         for (Object object : BlockPosM.getAllInBoxMutable(blockPos2, blockPos3)) {
            BlockPosM blockPosM = (BlockPosM)object;
            IBlockState blockState = chunkCacheOF.getBlockState(blockPosM);
            Block block2 = blockState.getBlock();
            if (block2.isOpaqueCube()) {
               visGraph.setOpaqueCube(blockPosM);
            }

            if (ReflectorForge.checkIBlockState(blockState)) {
               TileEntity tileEntity = chunkCacheOF.getTileEntity(new BlockPos(blockPosM));
               TileEntitySpecialRenderer specialRenderer = TileEntityRendererDispatcher.instance.getSpecialRenderer(tileEntity);
               if (tileEntity != null && specialRenderer != null) {
                  compiledChunk2.addTileEntity(tileEntity);
                  if (specialRenderer.forceTileEntityRender()) {
                     hashSet.add(tileEntity);
                  }
               }
            }

            EnumWorldBlockLayer[] enumWorldBlockLayers;
            if (active) {
               enumWorldBlockLayers = ENUM_WORLD_BLOCK_LAYERS;
            } else {
               enumWorldBlockLayers = this.blockLayersSingle;
               enumWorldBlockLayers[0] = block2.getBlockLayer();
            }

            for (int i = 0; i < enumWorldBlockLayers.length; i++) {
               EnumWorldBlockLayer enumWorldBlockLayer = enumWorldBlockLayers[i];
               if (active) {
                  boolean flag2 = Reflector.checkObject(block2, Reflector.reflectorMethod22, enumWorldBlockLayer);
                  if (!flag2) {
                     continue;
                  }
               }

               if (active2) {
                  Reflector.handleReflectorMethod(Reflector.reflectorMethod72, enumWorldBlockLayer);
               }

               enumWorldBlockLayer = this.fixBlockLayer(blockState, enumWorldBlockLayer);
               int i2 = enumWorldBlockLayer.ordinal();
               if (block2.getRenderType() != -1) {
                  WorldRenderer worldRendererByLayerId = generator.getRegionRenderCacheBuilder().getWorldRendererByLayerId(i2);
                  worldRendererByLayerId.setBlockLayer(enumWorldBlockLayer);
                  RenderEnv renderEnv = worldRendererByLayerId.getRenderEnv(blockState, blockPosM);
                  renderEnv.setRegionRenderCacheBuilder(generator.getRegionRenderCacheBuilder());
                  if (!compiledChunk2.isLayerStarted(enumWorldBlockLayer)) {
                     compiledChunk2.setLayerStarted(enumWorldBlockLayer);
                     this.preRenderBlocks(worldRendererByLayerId, blockPos2);
                  }

                  flagArray[i2] |= blockRendererDispatcher.renderBlock(blockState, blockPosM, chunkCacheOF, worldRendererByLayerId);
                  if (renderEnv.isOverlaysRendered()) {
                     this.postRenderOverlays(generator.getRegionRenderCacheBuilder(), compiledChunk2, flagArray);
                     renderEnv.setOverlaysRendered(false);
                  }
               }
            }

            if (active2) {
               Reflector.handleReflectorMethod(Reflector.reflectorMethod72, null);
            }
         }

         for (EnumWorldBlockLayer enumWorldBlockLayer2 : ENUM_WORLD_BLOCK_LAYERS) {
            if (flagArray[enumWorldBlockLayer2.ordinal()]) {
               compiledChunk2.setLayerUsed(enumWorldBlockLayer2);
            }

            if (compiledChunk2.isLayerStarted(enumWorldBlockLayer2)) {
               if (Config.isShaders()) {
                  SVertexBuilder.calcNormalChunkLayer(generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(enumWorldBlockLayer2));
               }

               WorldRenderer worldRendererByLayer = generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(enumWorldBlockLayer2);
               this.postRenderBlocks(enumWorldBlockLayer2, x, y, z, worldRendererByLayer, compiledChunk2);
               if (worldRendererByLayer.animatedSprites != null) {
                  compiledChunk2.setAnimatedSprites(enumWorldBlockLayer2, (BitSet)worldRendererByLayer.animatedSprites.clone());
               }
            } else {
               compiledChunk2.setAnimatedSprites(enumWorldBlockLayer2, (BitSet)null);
            }
         }

         chunkCacheOF.renderFinish();
      }

      compiledChunk2.setVisibility(visGraph.computeVisibility());
      this.lockCompileTask.lock();

      try {
         HashSet hashSet2 = Sets.newHashSet(hashSet);
         HashSet hashSet3 = Sets.newHashSet(this.setTileEntities);
         hashSet2.removeAll(this.setTileEntities);
         hashSet3.removeAll(hashSet);
         this.setTileEntities.clear();
         this.setTileEntities.addAll(hashSet);
         this.renderGlobal.updateTileEntities(hashSet3, hashSet2);
      } finally {
         this.lockCompileTask.unlock();
      }
   }

   public boolean setFrameIndex(int frameIndexIn) {
      if (this.frameIndex == frameIndexIn) {
         return false;
      }

      this.frameIndex = frameIndexIn;
      return true;
   }

   public void multModelviewMatrix() {
      GlStateManager.multMatrix(this.modelviewMatrix);
   }

   public void setRenderChunkNeighbour(EnumFacing arg1, RenderChunk arg2) {
      this.renderChunkNeighbours[arg1.ordinal()] = arg2;
      this.renderChunkNeighboursValid[arg1.ordinal()] = arg2;
   }

   public void setPosition(BlockPos pos) {
      this.stopCompileTask();
      this.position = pos;
      this.regionX = pos.getX() >> 8 << 8;
      this.regionZ = pos.getZ() >> 8 << 8;
      this.boundingBox = new AxisAlignedBB(pos, pos.add(16, 16, 16));
      this.initModelviewMatrix();

      for (int i = 0; i < this.positionOffsets16.length; i++) {
         this.positionOffsets16[i] = null;
      }

      this.renderChunksOffset16Updated = false;
      this.renderChunkNeighboursUpated = false;

      for (int i2 = 0; i2 < this.renderChunkNeighbours.length; i2++) {
         RenderChunk renderChunk = this.renderChunkNeighbours[i2];
         if (renderChunk != null) {
            renderChunk.renderChunkNeighboursUpated = false;
         }
      }

      this.chunk = null;
      this.boundingBoxParent = null;
   }

   public void deleteGlResources() {
      this.stopCompileTask();

      for (int index = 0; index < EnumWorldBlockLayer.values().length; index++) {
         if (this.vertexBuffers[index] != null) {
            this.vertexBuffers[index].deleteGlBuffers();
         }
      }
   }

   public VertexBuffer getVertexBufferByLayer(int layer) {
      return this.vertexBuffers[layer];
   }

   public void resortTransparency(float x, float y, float z, ChunkCompileTaskGenerator generator) {
      CompiledChunk compiledChunk = generator.getCompiledChunk();
      if (compiledChunk.getState() != null && !compiledChunk.isLayerEmpty(EnumWorldBlockLayer.TRANSLUCENT)) {
         WorldRenderer worldRenderer = generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(EnumWorldBlockLayer.TRANSLUCENT);
         this.preRenderBlocks(worldRenderer, this.position);
         worldRenderer.setVertexState(compiledChunk.getState());
         this.postRenderBlocks(EnumWorldBlockLayer.TRANSLUCENT, x, y, z, worldRenderer, compiledChunk);
      }
   }

   public Chunk getChunk() {
      return this.getChunk(this.position);
   }

   public net.minecraft.client.renderer.RenderGlobal.ContainerLocalRenderInformation getRenderInfo() {
      return this.renderInfo;
   }

   public ChunkCompileTaskGenerator makeCompileTaskTransparency() {
      this.lockCompileTask.lock();

      ChunkCompileTaskGenerator chunkCompileTaskGenerator2;
      try {
         if (this.compileTask != null && this.compileTask.getStatus() == ChunkCompileTaskGenerator.Status.PENDING) {
            return null;
         }

         if (this.compileTask != null && this.compileTask.getStatus() != ChunkCompileTaskGenerator.Status.DONE) {
            this.compileTask.finish();
            this.compileTask = null;
         }

         this.compileTask = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY);
         this.compileTask.setCompiledChunk(this.compiledChunk);
         chunkCompileTaskGenerator2 = this.compileTask;
      } finally {
         this.lockCompileTask.unlock();
      }

      return chunkCompileTaskGenerator2;
   }

   public boolean isBoundingBoxInFrustum(ICamera arg1, int arg2) {
      return this.getBoundingBoxParent().isBoundingBoxInFrustumFully(arg1, arg2) ? true : arg1.isBoundingBoxInFrustum(this.boundingBox);
   }

   public boolean isChunkRegionEmpty() {
      return this.isChunkRegionEmpty(this.position);
   }

   public boolean isPlayerUpdate() {
      return this.playerUpdate;
   }

   public void setNeedsUpdate(boolean needsUpdateIn) {
      this.needsUpdate = needsUpdateIn;
      if (needsUpdateIn) {
         if (this.isWorldPlayerUpdate()) {
            this.playerUpdate = true;
         }
      } else {
         this.playerUpdate = false;
      }
   }

   public ChunkCompileTaskGenerator makeCompileTaskChunk() {
      this.lockCompileTask.lock();

      ChunkCompileTaskGenerator chunkCompileTaskGenerator;
      try {
         this.finishCompileTask();
         this.compileTask = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.REBUILD_CHUNK);
         chunkCompileTaskGenerator = this.compileTask;
      } finally {
         this.lockCompileTask.unlock();
      }

      return chunkCompileTaskGenerator;
   }

   private void postRenderBlocks(EnumWorldBlockLayer layer, float x, float y, float z, WorldRenderer worldRendererIn, CompiledChunk compiledChunkIn) {
      if (layer == EnumWorldBlockLayer.TRANSLUCENT && !compiledChunkIn.isLayerEmpty(layer)) {
         worldRendererIn.sortVertexData(x, y, z);
         compiledChunkIn.setState(worldRendererIn.getVertexState());
      }

      worldRendererIn.finishDrawing();
   }

   private void postRenderOverlays(RegionRenderCacheBuilder arg1, CompiledChunk arg2, boolean[] arg3) {
      this.postRenderOverlay(EnumWorldBlockLayer.CUTOUT, arg1, arg2, arg3);
      this.postRenderOverlay(EnumWorldBlockLayer.CUTOUT_MIPPED, arg1, arg2, arg3);
      this.postRenderOverlay(EnumWorldBlockLayer.TRANSLUCENT, arg1, arg2, arg3);
   }

   public CompiledChunk getCompiledChunk() {
      return this.compiledChunk;
   }

   public ReentrantLock getLockCompileTask() {
      return this.lockCompileTask;
   }

   private void initModelviewMatrix() {
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      float f = 1.000001F;
      GlStateManager.translate(-8.0F, -8.0F, -8.0F);
      GlStateManager.scale(f, f, f);
      GlStateManager.translate(8.0F, 8.0F, 8.0F);
      GlStateManager.getFloat(2982, this.modelviewMatrix);
      GlStateManager.popMatrix();
   }

   public RenderChunk(World worldIn, RenderGlobal renderGlobalIn, BlockPos blockPosIn, int indexIn) {
      this.world = worldIn;
      this.renderGlobal = renderGlobalIn;
      this.index = indexIn;
      if (!blockPosIn.equals(this.getPosition())) {
         this.setPosition(blockPosIn);
      }

      if (OpenGlHelper.useVbo()) {
         for (int index = 0; index < EnumWorldBlockLayer.values().length; index++) {
            this.vertexBuffers[index] = new VertexBuffer(DefaultVertexFormats.BLOCK);
         }
      }
   }

   private boolean isChunkRegionEmpty(BlockPos arg1) {
      int yCoordinate = arg1.getY();
      int i = yCoordinate + 15;
      return this.getChunk(arg1).getAreLevelsEmpty(yCoordinate, i);
   }

   protected RegionRenderCache getRegionRenderCache(World world2, BlockPos blockPos2, BlockPos blockPos3) {
      return new RegionRenderCache(world2, blockPos2, blockPos3, 1);
   }

   private void preRenderBlocks(WorldRenderer worldRendererIn, BlockPos pos) {
      worldRendererIn.begin(7, DefaultVertexFormats.BLOCK);
      if (Config.isRenderRegions()) {
         pos.getX();
         int i = pos.getY() >> 8 << 8;
         pos.getZ();
         int regionX2 = this.regionX;
         int regionZ2 = this.regionZ;
         worldRendererIn.setTranslation(-regionX2, -i, -regionZ2);
      } else {
         worldRendererIn.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
      }
   }

   private void postRenderOverlay(EnumWorldBlockLayer arg1, RegionRenderCacheBuilder arg2, CompiledChunk arg3, boolean[] arg4) {
      if (arg2.getWorldRendererByLayer(arg1).isDrawing()) {
         arg3.setLayerStarted(arg1);
         arg4[arg1.ordinal()] = true;
      }
   }

   protected void finishCompileTask() {
      this.lockCompileTask.lock();

      try {
         if (this.compileTask != null && this.compileTask.getStatus() != ChunkCompileTaskGenerator.Status.DONE) {
            this.compileTask.finish();
            this.compileTask = null;
         }
      } finally {
         this.lockCompileTask.unlock();
      }
   }

   public BlockPos getPosition() {
      return this.position;
   }

   public void stopCompileTask() {
      this.finishCompileTask();
      this.compiledChunk = CompiledChunk.DUMMY;
   }

   public boolean isNeedsUpdate() {
      return this.needsUpdate;
   }
}
