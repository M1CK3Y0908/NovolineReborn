package wtf.novoline.event;

public class MoveFlyingEvent extends Event {
   public float strafe;
   public float friction;
   public float yaw;
   public float forward;

   public MoveFlyingEvent() {
      super(23992540611656L, true);
   }
}
