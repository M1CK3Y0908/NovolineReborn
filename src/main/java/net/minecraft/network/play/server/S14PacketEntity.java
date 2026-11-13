package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S14PacketEntity implements Packet<INetHandlerPlayClient> {
   protected byte posX;
   protected boolean isRotating;
   protected byte yaw;
   protected byte posY;
   protected boolean onGround;
   protected byte posZ;
   protected int entityId;
   protected byte pitch;

   @Override
   public String toString() {
      return "Entity_" + super.toString();
   }

   public byte getPosX() {
      return this.posX;
   }

   public boolean getOnGround() {
      return this.onGround;
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.entityId);
   }

   public byte getYaw() {
      return this.yaw;
   }

   public byte getPosZ() {
      return this.posZ;
   }

   public byte getPosY() {
      return this.posY;
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleEntityMovement(this);
   }

   public byte getPitch() {
      return this.pitch;
   }

   public Entity getEntity(World worldIn) {
      return worldIn.getEntityByID(this.entityId);
   }

   public S14PacketEntity(int entityIdIn) {
      this.entityId = entityIdIn;
   }

   public S14PacketEntity() {
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readVarIntFromBuffer();
   }

   public boolean hasRotation() {
      return this.isRotating;
   }

   public static class S15PacketEntityRelMove extends S14PacketEntity {
      public S15PacketEntityRelMove(int entityIdIn, byte x, byte y, byte z, boolean onGroundIn) {
         super(entityIdIn);
         this.posX = x;
         this.posY = y;
         this.posZ = z;
         this.onGround = onGroundIn;
      }

      @Override
      public void readPacketData(PacketBuffer buf) throws IOException {
         super.readPacketData(buf);
         this.posX = buf.readByte();
         this.posY = buf.readByte();
         this.posZ = buf.readByte();
         this.onGround = buf.readBoolean();
      }

      @Override
      public void writePacketData(PacketBuffer buf) throws IOException {
         super.writePacketData(buf);
         buf.writeByte(this.posX);
         buf.writeByte(this.posY);
         buf.writeByte(this.posZ);
         buf.writeBoolean(this.onGround);
      }

      public S15PacketEntityRelMove() {
      }
   }

   public static class S16PacketEntityLook extends S14PacketEntity {
      public S16PacketEntityLook() {
         this.isRotating = true;
      }

      @Override
      public void readPacketData(PacketBuffer buf) throws IOException {
         super.readPacketData(buf);
         this.yaw = buf.readByte();
         this.pitch = buf.readByte();
         this.onGround = buf.readBoolean();
      }

      public S16PacketEntityLook(int entityIdIn, byte yawIn, byte pitchIn, boolean onGroundIn) {
         super(entityIdIn);
         this.yaw = yawIn;
         this.pitch = pitchIn;
         this.isRotating = true;
         this.onGround = onGroundIn;
      }

      @Override
      public void writePacketData(PacketBuffer buf) throws IOException {
         super.writePacketData(buf);
         buf.writeByte(this.yaw);
         buf.writeByte(this.pitch);
         buf.writeBoolean(this.onGround);
      }
   }

   public static class S17PacketEntityLookMove extends S14PacketEntity {
      public S17PacketEntityLookMove(int entityIdIn, byte x, byte y, byte z, byte yawIn, byte pitchIn, boolean onGroundIn) {
         super(entityIdIn);
         this.posX = x;
         this.posY = y;
         this.posZ = z;
         this.yaw = yawIn;
         this.pitch = pitchIn;
         this.onGround = onGroundIn;
         this.isRotating = true;
      }

      @Override
      public void writePacketData(PacketBuffer buf) throws IOException {
         super.writePacketData(buf);
         buf.writeByte(this.posX);
         buf.writeByte(this.posY);
         buf.writeByte(this.posZ);
         buf.writeByte(this.yaw);
         buf.writeByte(this.pitch);
         buf.writeBoolean(this.onGround);
      }

      @Override
      public void readPacketData(PacketBuffer buf) throws IOException {
         super.readPacketData(buf);
         this.posX = buf.readByte();
         this.posY = buf.readByte();
         this.posZ = buf.readByte();
         this.yaw = buf.readByte();
         this.pitch = buf.readByte();
         this.onGround = buf.readBoolean();
      }

      public S17PacketEntityLookMove() {
         this.isRotating = true;
      }
   }
}
