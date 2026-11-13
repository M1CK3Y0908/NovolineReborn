package wtf.novoline.utils;

public abstract class ScheduledTask {
   private final int delayMs;
   private long startMs;

   public void onWaiting() {
   }

   public void run() {
   }

   public final boolean isDue() {
      return System.nanoTime() / 1000000L - this.startMs >= this.delayMs;
   }

   protected ScheduledTask(int i) {
      this.delayMs = i;
      this.startMs = System.nanoTime() / 1000000L;
   }
}
