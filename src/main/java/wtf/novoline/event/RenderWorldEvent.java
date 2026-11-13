package wtf.novoline.event;

public class RenderWorldEvent extends Event {
   public float partialTicks;

   public RenderWorldEvent(float f) {
      this.partialTicks = f;
   }
}
