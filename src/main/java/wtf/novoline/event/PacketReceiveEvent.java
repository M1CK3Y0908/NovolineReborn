package wtf.novoline.event;

import net.minecraft.network.Packet;

public class PacketReceiveEvent extends Event {
   public Packet<?> packet;

   public PacketReceiveEvent(Packet packet2) {
      super(23992540611656L, true);
      this.packet = packet2;
   }
}
