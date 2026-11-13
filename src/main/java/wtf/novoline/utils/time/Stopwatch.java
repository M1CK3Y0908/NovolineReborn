package wtf.novoline.utils.time;

public class Stopwatch {
   private long startTime;

   public Stopwatch() {
      this.reset();
   }

   public boolean hasElapsedFloat(float f) {
      return (float)this.getElapsedMillis() >= f;
   }

   public long getElapsedMillis() {
      return System.currentTimeMillis() - this.startTime;
   }

   public boolean hasElapsed(long l) {
      return this.getElapsedMillis() >= l;
   }

   public void reset() {
      this.startTime = System.currentTimeMillis();
   }
}
