package wtf.novoline.event;

public class PreUpdateEvent extends Event {
   public double posX;
   public double posY;
   public double posZ;
   public double motionX;
   public double motionY;
   public double motionZ;
   public float rotationYaw;
   public float rotationPitch;
   public boolean onGround;

   public PreUpdateEvent(double d, double d2, double d3, double d4, double d5, double d6, float f, float f2, boolean flag) {
      super(23992540611656L, true);
      this.posX = d;
      this.posY = d2;
      this.posZ = d3;
      this.motionX = d4;
      this.motionY = d5;
      this.motionZ = d6;
      this.rotationYaw = f;
      this.rotationPitch = f2;
      this.onGround = flag;
   }
}
