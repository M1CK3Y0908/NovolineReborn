package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S12PacketEntityVelocity implements Packet<INetHandlerPlayClient> {
   private int entityID;
   private int motionX;
   private int originX;
   private int motionY;

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleEntityVelocity(this);
   }

   public S12PacketEntityVelocity(int entityIDIn, double motionXIn, double motionYIn, double motionZIn) {
      this.entityID = entityIDIn;
      double d = 3.9;
      if (motionXIn < -d) {
         motionXIn = -d;
      }

      if (motionYIn < -d) {
         motionYIn = -d;
      }

      if (motionZIn < -d) {
         motionZIn = -d;
      }

      if (motionXIn > d) {
         motionXIn = d;
      }

      if (motionYIn > d) {
         motionYIn = d;
      }

      if (motionZIn > d) {
         motionZIn = d;
      }

      this.motionX = (int)(motionXIn * 8000.0);
      this.originX = (int)(motionYIn * 8000.0);
      this.motionY = (int)(motionZIn * 8000.0);
   }

   public int getMotionZ() {
      return this.motionY;
   }

   public int getMotionX() {
      return this.motionX;
   }

   public S12PacketEntityVelocity(Entity entityIn) {
      this(entityIn.getEntityId(), entityIn.motionX, entityIn.motionY, entityIn.motionZ);
   }

   public int getMotionY() {
      return this.originX;
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.entityID);
      buf.writeShort(this.motionX);
      buf.writeShort(this.originX);
      buf.writeShort(this.motionY);
   }

   public S12PacketEntityVelocity() {
   }

   public int getEntityID() {
      return this.entityID;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityID = buf.readVarIntFromBuffer();
      this.motionX = buf.readShort();
      this.originX = buf.readShort();
      this.motionY = buf.readShort();
   }
}
