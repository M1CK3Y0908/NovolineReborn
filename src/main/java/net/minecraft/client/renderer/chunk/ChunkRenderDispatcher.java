package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.VertexBufferUploader;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class ChunkRenderDispatcher {
   private final List<ChunkRenderWorker> listThreadedWorkers = Lists.newArrayList();
   private final BlockingQueue<ChunkCompileTaskGenerator> queueChunkUpdates = Queues.newArrayBlockingQueue(100);
   private final WorldVertexBufferUploader worldVertexUploader = new WorldVertexBufferUploader();
   private final VertexBufferUploader vertexUploader = new VertexBufferUploader();
   private static final Logger logger = LogManager.getLogger();
   private final Queue<ListenableFutureTask<?>> queueChunkUploads = Queues.newArrayDeque();
   private static final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("Chunk Batcher %d").setDaemon(true).build();
   private List<RegionRenderCacheBuilder> listPausedBuilders = new ArrayList<>();
   private final int countRenderBuilders;
   private final BlockingQueue<RegionRenderCacheBuilder> queueFreeRenderBuilders;
   private final ChunkRenderWorker renderWorker;

   private void uploadDisplayList(WorldRenderer worldRenderer, int i, RenderChunk chunkRenderer) {
      GL11.glNewList(i, 4864);
      GlStateManager.pushMatrix();
      chunkRenderer.multModelviewMatrix();
      this.worldVertexUploader.draw(worldRenderer);
      GlStateManager.popMatrix();
      GL11.glEndList();
   }

   public boolean updateTransparencyLater(RenderChunk chunkRenderer) {
      chunkRenderer.getLockCompileTask().lock();

      try {
         final ChunkCompileTaskGenerator chunkCompileTaskGenerator2 = chunkRenderer.makeCompileTaskTransparency();
         if (chunkCompileTaskGenerator2 != null) {
            chunkCompileTaskGenerator2.addFinishRunnable(new Runnable() {
               @Override
               public void run() {
                  ChunkRenderDispatcher.this.queueChunkUpdates.remove(chunkCompileTaskGenerator2);
               }
            });
            return this.queueChunkUpdates.offer(chunkCompileTaskGenerator2);
         }
      } finally {
         chunkRenderer.getLockCompileTask().unlock();
      }

      return true;
   }

   public ChunkRenderDispatcher() {
      int i = Math.max(1, (int)(Runtime.getRuntime().maxMemory() * 0.3) / 10485760);
      int i2 = Math.max(1, MathHelper.clamp_int(Runtime.getRuntime().availableProcessors() - 2, 1, i / 5));
      this.countRenderBuilders = MathHelper.clamp_int(i2 * 8, 1, i);

      for (int i3 = 0; i3 < i2; i3++) {
         ChunkRenderWorker chunkRenderWorker = new ChunkRenderWorker(this);
         threadFactory.newThread(chunkRenderWorker).start();
         this.listThreadedWorkers.add(chunkRenderWorker);
      }

      this.queueFreeRenderBuilders = Queues.newArrayBlockingQueue(this.countRenderBuilders);

      for (int i4 = 0; i4 < this.countRenderBuilders; i4++) {
         this.queueFreeRenderBuilders.add(new RegionRenderCacheBuilder());
      }

      this.renderWorker = new ChunkRenderWorker(this, new RegionRenderCacheBuilder());
   }

   public RegionRenderCacheBuilder allocateRenderBuilder() throws InterruptedException {
      return this.queueFreeRenderBuilders.take();
   }

   public void pauseChunkUpdates() {
      while (this.listPausedBuilders.size() != this.countRenderBuilders) {
         try {
            this.runChunkUploads(Long.MAX_VALUE);
            RegionRenderCacheBuilder regionRenderCacheBuilder = this.queueFreeRenderBuilders.poll(100L, TimeUnit.MILLISECONDS);
            if (regionRenderCacheBuilder != null) {
               this.listPausedBuilders.add(regionRenderCacheBuilder);
            }
         } catch (InterruptedException interruptedException) {
         }
      }
   }

   public boolean updateChunkLater(RenderChunk chunkRenderer) {
      chunkRenderer.getLockCompileTask().lock();

      boolean flag;
      try {
         final ChunkCompileTaskGenerator chunkCompileTaskGenerator2 = chunkRenderer.makeCompileTaskChunk();
         chunkCompileTaskGenerator2.addFinishRunnable(new Runnable() {
            @Override
            public void run() {
               ChunkRenderDispatcher.this.queueChunkUpdates.remove(chunkCompileTaskGenerator2);
            }
         });
         boolean flag2 = this.queueChunkUpdates.offer(chunkCompileTaskGenerator2);
         if (!flag2) {
            chunkCompileTaskGenerator2.finish();
         }

         flag = flag2;
      } finally {
         chunkRenderer.getLockCompileTask().unlock();
      }

      return flag;
   }

   public ChunkRenderDispatcher(long l) {
      this();
   }

   public void freeRenderBuilder(RegionRenderCacheBuilder regionRenderCacheBuilder2) {
      this.queueFreeRenderBuilders.add(regionRenderCacheBuilder2);
   }

   public boolean hasChunkUpdates() {
      return this.queueChunkUpdates.isEmpty() && this.queueChunkUploads.isEmpty();
   }

   private void uploadVertexBuffer(WorldRenderer worldRenderer, VertexBuffer vertexBufferIn) {
      this.vertexUploader.setVertexBuffer(vertexBufferIn);
      this.vertexUploader.draw(worldRenderer);
   }

   public boolean updateChunkNow(RenderChunk chunkRenderer) {
      chunkRenderer.getLockCompileTask().lock();

      try {
         ChunkCompileTaskGenerator chunkCompileTaskGenerator2 = chunkRenderer.makeCompileTaskChunk();

         try {
            this.renderWorker.processTask(chunkCompileTaskGenerator2);
         } catch (InterruptedException interruptedException) {
         }
      } finally {
         chunkRenderer.getLockCompileTask().unlock();
      }

      return true;
   }

   public ChunkCompileTaskGenerator getNextChunkUpdate() throws InterruptedException {
      return this.queueChunkUpdates.take();
   }

   public ListenableFuture<Object> uploadChunk(
      final EnumWorldBlockLayer player, final WorldRenderer worldRenderer, final RenderChunk chunkRenderer, final CompiledChunk compiledChunkIn
   ) {
      if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
         if (OpenGlHelper.useVbo()) {
            this.uploadVertexBuffer(worldRenderer, chunkRenderer.getVertexBufferByLayer(player.ordinal()));
         } else {
            this.uploadDisplayList(worldRenderer, ((ListedRenderChunk)chunkRenderer).getDisplayList(player, compiledChunkIn), chunkRenderer);
         }

         worldRenderer.setTranslation(0.0, 0.0, 0.0);
         return Futures.immediateFuture(null);
      } else {
         ListenableFutureTask listenableFutureTask = ListenableFutureTask.create(new Runnable() {
            @Override
            public void run() {
               ChunkRenderDispatcher.this.uploadChunk(player, worldRenderer, chunkRenderer, compiledChunkIn);
            }
         }, null);
         synchronized (this.queueChunkUploads) {
            this.queueChunkUploads.add(listenableFutureTask);
            return listenableFutureTask;
         }
      }
   }

   public void clearChunkUpdates() {
      while (!this.queueChunkUpdates.isEmpty()) {
         ChunkCompileTaskGenerator chunkCompileTaskGenerator = this.queueChunkUpdates.poll();
         if (chunkCompileTaskGenerator != null) {
            chunkCompileTaskGenerator.finish();
         }
      }
   }

   public void resumeChunkUpdates() {
      this.queueFreeRenderBuilders.addAll(this.listPausedBuilders);
      this.listPausedBuilders.clear();
   }

   public void stopChunkUpdates() {
      this.clearChunkUpdates();

      while (this.runChunkUploads(0L)) {
      }

      List<RegionRenderCacheBuilder> list = Lists.newArrayList();

      while (list.size() != this.countRenderBuilders) {
         try {
            list.add(this.allocateRenderBuilder());
         } catch (InterruptedException interruptedException) {
         }
      }

      this.queueFreeRenderBuilders.addAll(list);
   }

   public String getDebugInfo() {
      return String.format("pC: %03d, pU: %1d, aB: %1d", this.queueChunkUpdates.size(), this.queueChunkUploads.size(), this.queueFreeRenderBuilders.size());
   }

   public boolean runChunkUploads(long l) {
      boolean conditionMet2 = false;

      long l2;
      do {
         boolean flag = false;
         ListenableFutureTask listenableFutureTask;
         synchronized (this.queueChunkUploads) {
            listenableFutureTask = this.queueChunkUploads.poll();
         }

         if (listenableFutureTask != null) {
            listenableFutureTask.run();
            flag = true;
            conditionMet2 = true;
         }

         if (l == 0L || !flag) {
            break;
         }

         l2 = l - System.nanoTime();
      } while (l2 >= 0L);

      return conditionMet2;
   }
}
