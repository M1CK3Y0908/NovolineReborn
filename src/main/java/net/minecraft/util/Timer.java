package net.minecraft.util;

import net.minecraft.client.Minecraft;

public class Timer {
   private double lastHRTime;
   public float timerSpeed = 1.0F;
   private long counter;
   public float renderPartialTicks;
   public int elapsedTicks;
   public float elapsedPartialTicks;
   private double timeSyncAdjustment = 1.0;
   float ticksPerSecond;
   private long lastSyncSysClock;
   private long lastSyncHRClock;

   public void updateTimer() {
      long systemTime = Minecraft.getSystemTime();
      long l = systemTime - this.lastSyncSysClock;
      long l2 = System.nanoTime() / 1000000L;
      double d = l2 / 1000.0;
      if (l <= 1000L && l >= 0L) {
         this.counter += l;
         if (this.counter > 1000L) {
            long l3 = l2 - this.lastSyncHRClock;
            double d2 = (double)this.counter / l3;
            this.timeSyncAdjustment = this.timeSyncAdjustment + (d2 - this.timeSyncAdjustment) * 0.2F;
            this.lastSyncHRClock = l2;
            this.counter = 0L;
         }

         if (this.counter < 0L) {
            this.lastSyncHRClock = l2;
         }
      } else {
         this.lastHRTime = d;
      }

      this.lastSyncSysClock = systemTime;
      double d3 = (d - this.lastHRTime) * this.timeSyncAdjustment;
      this.lastHRTime = d;
      d3 = MathHelper.clamp_double(d3, 0.0, 1.0);
      this.elapsedPartialTicks = (float)(this.elapsedPartialTicks + d3 * this.timerSpeed * this.ticksPerSecond);
      this.elapsedTicks = (int)this.elapsedPartialTicks;
      this.elapsedPartialTicks = this.elapsedPartialTicks - this.elapsedTicks;
      if (this.elapsedTicks > 10) {
         this.elapsedTicks = 10;
      }

      this.renderPartialTicks = this.elapsedPartialTicks;
   }

   public Timer(float tps) {
      this.ticksPerSecond = tps;
      this.lastSyncSysClock = Minecraft.getSystemTime();
      this.lastSyncHRClock = System.nanoTime() / 1000000L;
   }
}
