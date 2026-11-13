package wtf.novoline.event;

public class SlowdownEvent extends Event {
   public float forwardMultiplier = 0.2F;
   public float strafeMultiplier = 0.2F;

   public SlowdownEvent() {
      super(23992540611656L, true);
   }
}
