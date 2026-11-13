package wtf.novoline.event;

public class SyncCurrentSlotEvent extends Event {
   public int serverSlot;
   public int clientSlot;

   public SyncCurrentSlotEvent(int i, int i2) {
      super(23992540611656L, true);
      this.serverSlot = i;
      this.clientSlot = i2;
   }
}
