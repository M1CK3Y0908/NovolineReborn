package net.optifine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.profiler.Profiler;
import net.minecraft.src.Config;
import net.optifine.util.MemoryMonitor;
import org.lwjgl.opengl.GL11;

public class Lagometer {
   public static boolean active = false;
   public static Lagometer.TimerNano timerTick = new Lagometer.TimerNano();
   public static Lagometer.TimerNano timerScheduledExecutables = new Lagometer.TimerNano();
   public static Lagometer.TimerNano timerChunkUpload = new Lagometer.TimerNano();
   public static Lagometer.TimerNano timerChunkUpdate = new Lagometer.TimerNano();
   public static Lagometer.TimerNano timerVisibility = new Lagometer.TimerNano();
   public static Lagometer.TimerNano timerTerrain = new Lagometer.TimerNano();
   public static Lagometer.TimerNano timerServer = new Lagometer.TimerNano();
   private static Minecraft mc;
   private static long[] timesFrame = new long[512];
   private static long[] timesTick = new long[512];
   private static Profiler profiler;
   private static long[] timesScheduledExecutables = new long[512];
   private static long[] timesChunkUpload = new long[512];
   private static long[] timesChunkUpdate = new long[512];
   private static long[] timesVisibility = new long[512];
   private static long[] timesTerrain = new long[512];
   private static GameSettings gameSettings;
   private static long[] timesServer = new long[512];
   private static boolean[] gcs = new boolean[512];
   private static int numRecordedFrameTimes = 0;
   private static long prevFrameTimeNano = -1L;
   private static long renderTimeNano = 0L;

   public static boolean isActive() {
      return active;
   }

   public static void showLagometer(ScaledResolution scaledResolution) {
      if (gameSettings != null && (gameSettings.ofLagometer || gameSettings.showLagometer)) {
         long l = System.nanoTime();
         GlStateManager.clear(256);
         GlStateManager.matrixMode(5889);
         GlStateManager.pushMatrix();
         GlStateManager.enableColorMaterial();
         GlStateManager.loadIdentity();
         GlStateManager.ortho(0.0, mc.displayWidth, mc.displayHeight, 0.0, 1000.0, 3000.0);
         GlStateManager.matrixMode(5888);
         GlStateManager.pushMatrix();
         GlStateManager.loadIdentity();
         GlStateManager.translate(0.0F, 0.0F, -2000.0F);
         GL11.glLineWidth(1.0F);
         GlStateManager.disableTexture2D();
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         worldRenderer.begin(1, DefaultVertexFormats.POSITION_COLOR);

         for (int i = 0; i < timesFrame.length; i++) {
            int i2 = (i - numRecordedFrameTimes & timesFrame.length - 1) * 100 / timesFrame.length;
            i2 += 155;
            float displayHeight2 = mc.displayHeight;
            if (gcs[i]) {
               renderTime(i, timesFrame[i], i2, i2 / 2, 0, displayHeight2, worldRenderer);
            } else {
               renderTime(i, timesFrame[i], i2, i2, i2, displayHeight2, worldRenderer);
               displayHeight2 -= (float)renderTime(i, timesServer[i], i2 / 2, i2 / 2, i2 / 2, displayHeight2, worldRenderer);
               displayHeight2 -= (float)renderTime(i, timesTerrain[i], 0, i2, 0, displayHeight2, worldRenderer);
               displayHeight2 -= (float)renderTime(i, timesVisibility[i], i2, i2, 0, displayHeight2, worldRenderer);
               displayHeight2 -= (float)renderTime(i, timesChunkUpdate[i], i2, 0, 0, displayHeight2, worldRenderer);
               displayHeight2 -= (float)renderTime(i, timesChunkUpload[i], i2, 0, i2, displayHeight2, worldRenderer);
               displayHeight2 -= (float)renderTime(i, timesScheduledExecutables[i], 0, 0, i2, displayHeight2, worldRenderer);
               renderTime(i, timesTick[i], 0, i2, i2, displayHeight2, worldRenderer);
            }
         }

         createLong(timesFrame.length, 33333333L, mc.displayHeight, worldRenderer);
         createLong(timesFrame.length, 16666666L, mc.displayHeight, worldRenderer);
         instance.draw();
         GlStateManager.enableTexture2D();
         int i3 = mc.displayHeight - 80;
         int i4 = mc.displayHeight - 160;
         mc.fontRendererObj.getInt("30", 2, i4 + 1, -8947849);
         mc.fontRendererObj.getInt("30", 1, i4, -3881788);
         mc.fontRendererObj.getInt("60", 2, i3 + 1, -8947849);
         mc.fontRendererObj.getInt("60", 1, i3, -3881788);
         GlStateManager.matrixMode(5889);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
         GlStateManager.popMatrix();
         GlStateManager.enableTexture2D();
         float f = 1.0F - (float)((System.currentTimeMillis() - MemoryMonitor.getStartTimeMs()) / 1000.0);
         f = Config.limit(f, 0.0F, 1.0F);
         int i5 = (int)(170.0F + f * 85.0F);
         int i6 = (int)(100.0F + f * 55.0F);
         int i7 = (int)(10.0F + f * 10.0F);
         int i8 = i5 << 16 | i6 << 8 | i7;
         int i9 = 512 / scaledResolution.getScaleFactor() + 2;
         int i10 = mc.displayHeight / scaledResolution.getScaleFactor() - 8;
         GuiIngame.handleInt(i9 - 1, i10 - 1, i9 + 50, i10 + 10, -1605349296);
         mc.fontRendererObj.getInt(" " + MemoryMonitor.getAllocationRateMb() + " MB/s", i9, i10, i8);
         renderTimeNano = System.nanoTime() - l;
      }
   }

   private static long renderTime(int frameNum, long time, int i, int g, int b, float baseHeight, WorldRenderer tessellator) {
      long l = time / 200000L;
      if (l < 3L) {
         return 0L;
      }

      tessellator.pos(frameNum + 0.5F, baseHeight - (float)l + 0.5F, 0.0).color(i, g, b, 255).endVertex();
      tessellator.pos(frameNum + 0.5F, baseHeight + 0.5F, 0.0).color(i, g, b, 255).endVertex();
      return l;
   }

   private static long createLong(int i, long l, float f, WorldRenderer worldRenderer) {
      long l2 = l / 200000L;
      if (l2 < 3L) {
         return 0L;
      }

      worldRenderer.pos(0 + 0.5F, f - (float)l2 + 0.5F, 0.0).color(196, 196, 196, 255).endVertex();
      worldRenderer.pos(i + 0.5F, f - (float)l2 + 0.5F, 0.0).color(196, 196, 196, 255).endVertex();
      return l2;
   }

   public static void updateLagometer() {
      if (mc == null) {
         mc = Minecraft.getMinecraft();
         gameSettings = mc.gameSettings;
         profiler = mc.mcProfiler;
      }

      if (gameSettings.showDebugInfo && (gameSettings.ofLagometer || gameSettings.showLagometer)) {
         active = true;
         long timeNowNano = System.nanoTime();
         if (prevFrameTimeNano == -1L) {
            prevFrameTimeNano = timeNowNano;
         } else {
            int count = numRecordedFrameTimes & timesFrame.length - 1;
            numRecordedFrameTimes++;
            boolean isGcEvent = MemoryMonitor.isGcEvent();
            timesFrame[count] = timeNowNano - prevFrameTimeNano - renderTimeNano;
            timesTick[count] = timerTick.timeNano;
            timesScheduledExecutables[count] = timerScheduledExecutables.timeNano;
            timesChunkUpload[count] = timerChunkUpload.timeNano;
            timesChunkUpdate[count] = timerChunkUpdate.timeNano;
            timesVisibility[count] = timerVisibility.timeNano;
            timesTerrain[count] = timerTerrain.timeNano;
            timesServer[count] = timerServer.timeNano;
            gcs[count] = isGcEvent;
            timerTick.reset();
            timerScheduledExecutables.reset();
            timerVisibility.reset();
            timerChunkUpdate.reset();
            timerChunkUpload.reset();
            timerTerrain.reset();
            timerServer.reset();
            prevFrameTimeNano = System.nanoTime();
         }
      } else {
         active = false;
         prevFrameTimeNano = -1L;
      }
   }

   public static class TimerNano {
      public long timeStartNano = 0L;
      public long timeNano = 0L;

      public void end() {
         if (Lagometer.active && this.timeStartNano != 0L) {
            this.timeNano = this.timeNano + (System.nanoTime() - this.timeStartNano);
            this.timeStartNano = 0L;
         }
      }

      public void start() {
         if (Lagometer.active && this.timeStartNano == 0L) {
            this.timeStartNano = System.nanoTime();
         }
      }

      private void reset() {
         this.timeNano = 0L;
         this.timeStartNano = 0L;
      }
   }
}
