package wtf.novoline.event;

public class SpawnParticleEvent extends Event {
   public int id;

   public SpawnParticleEvent(int i) {
      super(23992540611656L, true);
      this.id = i;
   }
}
