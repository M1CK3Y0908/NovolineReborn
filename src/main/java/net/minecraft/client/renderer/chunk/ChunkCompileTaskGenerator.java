package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;

public class ChunkCompileTaskGenerator {
   private RegionRenderCacheBuilder regionRenderCacheBuilder;
   private CompiledChunk compiledChunk;
   private final ReentrantLock lock = new ReentrantLock();
   private final List<Runnable> listFinishRunnables = Lists.newArrayList();
   private ChunkCompileTaskGenerator.Status status = ChunkCompileTaskGenerator.Status.PENDING;
   private final RenderChunk renderChunk;
   private boolean finished;
   private final ChunkCompileTaskGenerator.Type type;

   public void setRegionRenderCacheBuilder(RegionRenderCacheBuilder regionRenderCacheBuilderIn) {
      this.regionRenderCacheBuilder = regionRenderCacheBuilderIn;
   }

   public void setStatus(ChunkCompileTaskGenerator.Status statusIn) {
      this.lock.lock();

      try {
         this.status = statusIn;
      } finally {
         this.lock.unlock();
      }
   }

   public RenderChunk getRenderChunk() {
      return this.renderChunk;
   }

   public boolean isFinished() {
      return this.finished;
   }

   public ReentrantLock getLock() {
      return this.lock;
   }

   public ChunkCompileTaskGenerator(RenderChunk renderChunkIn, ChunkCompileTaskGenerator.Type typeIn) {
      this.renderChunk = renderChunkIn;
      this.type = typeIn;
   }

   public void setCompiledChunk(CompiledChunk compiledChunkIn) {
      this.compiledChunk = compiledChunkIn;
   }

   public ChunkCompileTaskGenerator.Type getType() {
      return this.type;
   }

   public RegionRenderCacheBuilder getRegionRenderCacheBuilder() {
      return this.regionRenderCacheBuilder;
   }

   public void finish() {
      this.lock.lock();

      try {
         if (this.type == ChunkCompileTaskGenerator.Type.REBUILD_CHUNK && this.status != ChunkCompileTaskGenerator.Status.DONE) {
            this.renderChunk.setNeedsUpdate(true);
         }

         this.finished = true;
         this.status = ChunkCompileTaskGenerator.Status.DONE;
         Iterator iterator2 = this.listFinishRunnables.iterator();

         while (iterator2.hasNext()) {
            ((Runnable)iterator2.next()).run();
         }
      } finally {
         this.lock.unlock();
      }
   }

   public void addFinishRunnable(Runnable runnable) {
      this.lock.lock();

      try {
         this.listFinishRunnables.add(runnable);
         if (this.finished) {
            runnable.run();
         }
      } finally {
         this.lock.unlock();
      }
   }

   public CompiledChunk getCompiledChunk() {
      return this.compiledChunk;
   }

   public ChunkCompileTaskGenerator.Status getStatus() {
      return this.status;
   }

   public enum Status {
      PENDING,
      COMPILING,
      UPLOADING,
      DONE;
   }

   public enum Type {
      REBUILD_CHUNK,
      RESORT_TRANSPARENCY;
   }
}
