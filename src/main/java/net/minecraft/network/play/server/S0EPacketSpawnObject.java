package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S0EPacketSpawnObject implements Packet<INetHandlerPlayClient> {
   private int y;
   private int speedX;
   private int data;
   private int entityId;
   private int type;
   private int x;
   private int pitch;
   private int z;
   private int yaw;
   private int speedZ;
   private int speedY;

   public void setSpeedX(int newSpeedX) {
      this.speedX = newSpeedX;
   }

   public S0EPacketSpawnObject() {
   }

   public int getPitch() {
      return this.pitch;
   }

   public void setZ(int newZ) {
      this.z = newZ;
   }

   public int getZ() {
      return this.z;
   }

   public void update() {
      this.data = 0;
   }

   public void setSpeedZ(int newSpeedZ) {
      this.speedZ = newSpeedZ;
   }

   public int getYaw() {
      return this.yaw;
   }

   public S0EPacketSpawnObject(Entity entityIn, int typeIn, int dataIn) {
      this.entityId = entityIn.getEntityId();
      this.x = MathHelper.floor_double(entityIn.posX * 32.0);
      this.y = MathHelper.floor_double(entityIn.posY * 32.0);
      this.z = MathHelper.floor_double(entityIn.posZ * 32.0);
      this.pitch = MathHelper.floor_float(entityIn.rotationPitch * 256.0F / 360.0F);
      this.yaw = MathHelper.floor_float(entityIn.rotationYaw * 256.0F / 360.0F);
      this.type = typeIn;
      this.data = dataIn;
      if (dataIn > 0) {
         double motionX2 = entityIn.motionX;
         double motionY2 = entityIn.motionY;
         double motionZ2 = entityIn.motionZ;
         double d = 3.9;
         if (motionX2 < -d) {
            motionX2 = -d;
         }

         if (motionY2 < -d) {
            motionY2 = -d;
         }

         if (motionZ2 < -d) {
            motionZ2 = -d;
         }

         if (motionX2 > d) {
            motionX2 = d;
         }

         if (motionY2 > d) {
            motionY2 = d;
         }

         if (motionZ2 > d) {
            motionZ2 = d;
         }

         this.speedX = (int)(motionX2 * 8000.0);
         this.speedY = (int)(motionY2 * 8000.0);
         this.speedZ = (int)(motionZ2 * 8000.0);
      }
   }

   public void setSpeedY(int newSpeedY) {
      this.speedY = newSpeedY;
   }

   public S0EPacketSpawnObject(Entity entityIn, int typeIn) {
      this(entityIn, typeIn, 0);
   }

   public int getSpeedX() {
      return this.speedX;
   }

   public int getX() {
      return this.x;
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.entityId);
      buf.writeByte(this.type);
      buf.writeInt(this.x);
      buf.writeInt(this.y);
      buf.writeInt(this.z);
      buf.writeByte(this.pitch);
      buf.writeByte(this.yaw);
      buf.writeInt(this.data);
      if (this.data > 0) {
         buf.writeShort(this.speedX);
         buf.writeShort(this.speedY);
         buf.writeShort(this.speedZ);
      }
   }

   public void setY(int newY) {
      this.y = newY;
   }

   public int getEntityID() {
      return this.entityId;
   }

   public int getY() {
      return this.y;
   }

   public int getSpeedZ() {
      return this.speedZ;
   }

   public int getSpeedY() {
      return this.speedY;
   }

   public int getType() {
      return this.type;
   }

   public int getData() {
      return this.data;
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleSpawnObject(this);
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readVarIntFromBuffer();
      this.type = buf.readByte();
      this.x = buf.readInt();
      this.y = buf.readInt();
      this.z = buf.readInt();
      this.pitch = buf.readByte();
      this.yaw = buf.readByte();
      this.data = buf.readInt();
      if (this.data > 0) {
         this.speedX = buf.readShort();
         this.speedY = buf.readShort();
         this.speedZ = buf.readShort();
      }
   }

   public void setX(int newX) {
      this.x = newX;
   }
}
