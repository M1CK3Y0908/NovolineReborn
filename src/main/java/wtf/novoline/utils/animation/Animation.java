package wtf.novoline.utils.animation;

public class Animation {
   private boolean paused;
   private boolean finished = true;
   private long delay;
   private double previousValue;
   private double start;
   private boolean flipped;
   private double end;
   private Easing easing;
   private long startTime;
   private long duration;
   private long elapsed;
   private double value;

   public Easing getEasing() {
      return this.easing;
   }

   public void restart() {
      this.stop();
      this.start(this.getStart(), this.getEnd(), this.getDuration(), this.getDelay(), this.getEasing());
   }

   public void stop() {
      this.finished = true;
      this.value = this.end;
      this.elapsed = 0L;
   }

   public double getValue() {
      return this.value;
   }

   public double getEnd() {
      return this.end;
   }

   public long getDuration() {
      return this.duration;
   }

   public long getStartTime() {
      return this.startTime;
   }

   public long getDelay() {
      return this.delay;
   }

   public void pause() {
      this.paused = true;
   }

   public void setFlipped(boolean flag) {
      this.flipped = flag;
   }

   public Animation update() {
      if (!this.hasFinished()) {
         if (this.delay <= 0L) {
            this.elapsed = System.currentTimeMillis() - this.getStartTime();
            this.previousValue = this.value;
            double d = this.isFlipped() ? this.end : this.start;
            double d2 = this.isFlipped() ? this.start : this.end;
            this.value = this.easing.ease(this.elapsed, d, d2 - d, this.duration);
            if (this.elapsed > this.duration) {
               this.stop();
            }
         } else if (System.currentTimeMillis() - this.getStartTime() >= this.delay) {
            this.delay = 0L;
            this.startTime = System.currentTimeMillis();
         }
      }

      return this;
   }

   public void resume() {
      this.paused = false;
   }

   public boolean isPaused() {
      return this.paused;
   }

   public void unlock() {
      this.finished = true;
   }

   public boolean isAnimating() {
      return !this.hasFinished() && !this.isPaused();
   }

   public void start(double d, double d2, long l, Easing easing2) {
      if (!this.isAnimating()) {
         this.start = d;
         this.value = this.previousValue = this.isFlipped() ? d2 : d;
         this.end = d2;
         this.duration = l;
         this.startTime = System.currentTimeMillis();
         this.finished = false;
         this.easing = easing2;
      }
   }

   public void flip() {
      this.setFlipped(!this.isFlipped());
   }

   public boolean hasFinished() {
      return this.finished;
   }

   public void start(double d, double d2, long l, long l2, Easing easing2) {
      if (!this.isAnimating()) {
         this.delay = l2;
         this.start(d, d2, l, easing2);
      }
   }

   public boolean isFlipped() {
      return this.flipped;
   }

   public double getStart() {
      return this.start;
   }
}
