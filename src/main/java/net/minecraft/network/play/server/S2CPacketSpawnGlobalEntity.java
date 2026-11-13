package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S2CPacketSpawnGlobalEntity implements Packet<INetHandlerPlayClient> {
   private int y;
   private int type;
   private int entityId;
   private int x;
   private int z;

   public S2CPacketSpawnGlobalEntity() {
   }

   public int getEntityId() {
      return this.entityId;
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleSpawnGlobalEntity(this);
   }

   public int getType() {
      return this.type;
   }

   public int getX() {
      return this.x;
   }

   public S2CPacketSpawnGlobalEntity(Entity entityIn) {
      this.entityId = entityIn.getEntityId();
      this.x = MathHelper.floor_double(entityIn.posX * 32.0);
      this.y = MathHelper.floor_double(entityIn.posY * 32.0);
      this.z = MathHelper.floor_double(entityIn.posZ * 32.0);
      if (entityIn instanceof EntityLightningBolt) {
         this.type = 1;
      }
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.entityId);
      buf.writeByte(this.type);
      buf.writeInt(this.x);
      buf.writeInt(this.y);
      buf.writeInt(this.z);
   }

   public int getY() {
      return this.y;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readVarIntFromBuffer();
      this.type = buf.readByte();
      this.x = buf.readInt();
      this.y = buf.readInt();
      this.z = buf.readInt();
   }

   public int getZ() {
      return this.z;
   }
}
