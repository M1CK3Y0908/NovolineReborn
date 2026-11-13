package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0BPacketEntityAction implements Packet<INetHandlerPlayServer> {
   public int entityID;
   public C0BPacketEntityAction.Action action;
   public int auxData;

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.entityID);
      buf.writeEnumValue(this.action);
      buf.writeVarIntToBuffer(this.auxData);
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityID = buf.readVarIntFromBuffer();
      this.action = buf.readEnumValue(C0BPacketEntityAction.Action.class);
      this.auxData = buf.readVarIntFromBuffer();
   }

   public C0BPacketEntityAction(Entity entity, C0BPacketEntityAction.Action action, int auxData) {
      this.entityID = entity.getEntityId();
      this.action = action;
      this.auxData = auxData;
   }

   public C0BPacketEntityAction() {
   }

   public C0BPacketEntityAction(Entity entity, C0BPacketEntityAction.Action action) {
      this(entity, action, 0);
   }

   public void processPacket(INetHandlerPlayServer handler) {
      handler.processEntityAction(this);
   }

   public enum Action {
      START_SNEAKING,
      STOP_SNEAKING,
      STOP_SLEEPING,
      START_SPRINTING,
      STOP_SPRINTING,
      RIDING_JUMP,
      OPEN_INVENTORY,
      START_FALL_FLYING;
   }
}
