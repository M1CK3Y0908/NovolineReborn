package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class S10PacketSpawnPainting implements Packet<INetHandlerPlayClient> {
   private int entityID;
   private BlockPos position;
   private EnumFacing facing;
   private String title;

   public EnumFacing getFacing() {
      return this.facing;
   }

   public S10PacketSpawnPainting(EntityPainting painting) {
      this.entityID = painting.getEntityId();
      this.position = painting.getHangingPosition();
      this.facing = painting.facingDirection;
      this.title = painting.art.title;
   }

   public String getTitle() {
      return this.title;
   }

   public int getEntityID() {
      return this.entityID;
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleSpawnPainting(this);
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.entityID);
      buf.writeString(this.title);
      buf.writeBlockPos(this.position);
      buf.writeByte(this.facing.getHorizontalIndex());
   }

   public S10PacketSpawnPainting() {
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityID = buf.readVarIntFromBuffer();
      this.title = buf.readStringFromBuffer(net.minecraft.entity.item.EntityPainting.EnumArt.field_180001_A);
      this.position = buf.readBlockPos();
      this.facing = EnumFacing.getHorizontal(buf.readUnsignedByte());
   }

   public BlockPos getPosition() {
      return this.position;
   }
}
