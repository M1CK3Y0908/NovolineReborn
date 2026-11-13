package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumWorldBlockLayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkRenderWorker implements Runnable {
   private final ChunkRenderDispatcher chunkRenderDispatcher;
   private static final Logger LOGGER = LogManager.getLogger();
   private final RegionRenderCacheBuilder regionRenderCacheBuilder;

   private void freeRenderBuilder(ChunkCompileTaskGenerator taskGenerator) {
      if (this.regionRenderCacheBuilder == null) {
         this.chunkRenderDispatcher.freeRenderBuilder(taskGenerator.getRegionRenderCacheBuilder());
      }
   }

   private RegionRenderCacheBuilder getRegionRenderCacheBuilder() throws InterruptedException {
      return this.regionRenderCacheBuilder != null ? this.regionRenderCacheBuilder : this.chunkRenderDispatcher.allocateRenderBuilder();
   }

   protected void processTask(final ChunkCompileTaskGenerator generator) throws InterruptedException {
      generator.getLock().lock();

      try {
         if (generator.getStatus() != ChunkCompileTaskGenerator.Status.PENDING) {
            if (!generator.isFinished()) {
               LOGGER.warn("Chunk render task was " + generator.getStatus() + " when I expected it to be pending; ignoring task");
            }

            return;
         }

         generator.setStatus(ChunkCompileTaskGenerator.Status.COMPILING);
      } finally {
         generator.getLock().unlock();
      }

      Entity renderViewEntity = Minecraft.getMinecraft().getRenderViewEntity();
      if (renderViewEntity == null) {
         generator.finish();
      } else {
         generator.setRegionRenderCacheBuilder(this.getRegionRenderCacheBuilder());
         float f = (float)renderViewEntity.posX;
         float f2 = (float)renderViewEntity.posY + renderViewEntity.getEyeHeight();
         float f3 = (float)renderViewEntity.posZ;
         ChunkCompileTaskGenerator.Type type = generator.getType();
         if (type == ChunkCompileTaskGenerator.Type.REBUILD_CHUNK) {
            generator.getRenderChunk().rebuildChunk(f, f2, f3, generator);
         } else if (type == ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY) {
            generator.getRenderChunk().resortTransparency(f, f2, f3, generator);
         }

         generator.getLock().lock();

         try {
            if (generator.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING) {
               if (!generator.isFinished()) {
                  LOGGER.warn("Chunk render task was " + generator.getStatus() + " when I expected it to be compiling; aborting task");
               }

               this.freeRenderBuilder(generator);
               return;
            }

            generator.setStatus(ChunkCompileTaskGenerator.Status.UPLOADING);
         } finally {
            generator.getLock().unlock();
         }

         final CompiledChunk compiledChunk = generator.getCompiledChunk();
         ArrayList arrayList = Lists.newArrayList();
         if (type == ChunkCompileTaskGenerator.Type.REBUILD_CHUNK) {
            for (EnumWorldBlockLayer enumWorldBlockLayer : EnumWorldBlockLayer.values()) {
               if (compiledChunk.isLayerStarted(enumWorldBlockLayer)) {
                  arrayList.add(
                     this.chunkRenderDispatcher
                        .uploadChunk(enumWorldBlockLayer, generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(enumWorldBlockLayer), generator.getRenderChunk(), compiledChunk)
                  );
               }
            }
         } else if (type == ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY) {
            arrayList.add(
               this.chunkRenderDispatcher
                  .uploadChunk(
                     EnumWorldBlockLayer.TRANSLUCENT,
                     generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(EnumWorldBlockLayer.TRANSLUCENT),
                     generator.getRenderChunk(),
                     compiledChunk
                  )
            );
         }

         final ListenableFuture listenableFuture = Futures.allAsList(arrayList);
         generator.addFinishRunnable(new Runnable() {
            @Override
            public void run() {
               listenableFuture.cancel(false);
            }
         });
         Futures.addCallback(listenableFuture, new FutureCallback<List<Object>>() {
            public void onFailure(Throwable arg1) {
               ChunkRenderWorker.this.freeRenderBuilder(generator);
               if (!(arg1 instanceof CancellationException) && !(arg1 instanceof InterruptedException)) {
                  Minecraft.getMinecraft().crashed(CrashReport.makeCrashReport(arg1, "Rendering chunk"));
               }
            }

            public void onSuccess(List<Object> arg1) {
               ChunkRenderWorker.this.freeRenderBuilder(generator);
               generator.getLock().lock();

               try {
                  if (generator.getStatus() != ChunkCompileTaskGenerator.Status.UPLOADING) {
                     if (!generator.isFinished()) {
                        ChunkRenderWorker.LOGGER.warn("Chunk render task was " + generator.getStatus() + " when I expected it to be uploading; aborting task");
                     }

                     return;
                  }

                  generator.setStatus(ChunkCompileTaskGenerator.Status.DONE);
               } finally {
                  generator.getLock().unlock();
               }

               generator.getRenderChunk().setCompiledChunk(compiledChunk);
            }
         });
      }
   }

   public ChunkRenderWorker(ChunkRenderDispatcher chunkRenderDispatcherIn, RegionRenderCacheBuilder regionRenderCacheBuilderIn) {
      this.chunkRenderDispatcher = chunkRenderDispatcherIn;
      this.regionRenderCacheBuilder = regionRenderCacheBuilderIn;
   }

   public ChunkRenderWorker(ChunkRenderDispatcher chunkRenderDispatcher2) {
      this(chunkRenderDispatcher2, (RegionRenderCacheBuilder)null);
   }

   @Override
   public void run() {
      while (true) {
         try {
            this.processTask(this.chunkRenderDispatcher.getNextChunkUpdate());
         } catch (InterruptedException interruptedException) {
            LOGGER.debug("Stopping due to interrupt");
            return;
         } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Batching chunks");
            Minecraft.getMinecraft().crashed(Minecraft.getMinecraft().addGraphicsAndWorldToCrashReport(crashReport));
            return;
         }
      }
   }
}
