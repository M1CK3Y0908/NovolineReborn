package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S0FPacketSpawnMob implements Packet<INetHandlerPlayClient> {
   private byte pitch;
   private int type;
   private int z;
   private int y;
   private DataWatcher dataWatcher;
   private int velocityZ;
   private byte headPitch;
   private int entityId;
   private List<net.minecraft.entity.DataWatcher.WatchableObject> watcher;
   private byte yaw;
   private int x;
   private int velocityX;
   private int velocityY;

   public int getZ() {
      return this.z;
   }

   public byte getYaw() {
      return this.yaw;
   }

   public int getVelocityZ() {
      return this.velocityZ;
   }

   public int getVelocityY() {
      return this.velocityY;
   }

   public S0FPacketSpawnMob() {
   }

   public List<net.minecraft.entity.DataWatcher.WatchableObject> getWatcher() {
      if (this.watcher == null) {
         this.watcher = this.dataWatcher.getAllWatched();
      }

      return this.watcher;
   }

   public int getY() {
      return this.y;
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleSpawnMob(this);
   }

   public int getEntityID() {
      return this.entityId;
   }

   public int getVelocityX() {
      return this.velocityX;
   }

   public S0FPacketSpawnMob(EntityLivingBase entityIn) {
      this.entityId = entityIn.getEntityId();
      this.type = (byte)EntityList.getEntityID(entityIn);
      this.x = MathHelper.floor_double(entityIn.posX * 32.0);
      this.y = MathHelper.floor_double(entityIn.posY * 32.0);
      this.z = MathHelper.floor_double(entityIn.posZ * 32.0);
      this.yaw = (byte)(entityIn.rotationYaw * 256.0F / 360.0F);
      this.pitch = (byte)(entityIn.rotationPitch * 256.0F / 360.0F);
      this.headPitch = (byte)(entityIn.rotationYawHead * 256.0F / 360.0F);
      double d = 3.9;
      double motionX2 = entityIn.motionX;
      double motionY2 = entityIn.motionY;
      double motionZ2 = entityIn.motionZ;
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

      this.velocityX = (int)(motionX2 * 8000.0);
      this.velocityY = (int)(motionY2 * 8000.0);
      this.velocityZ = (int)(motionZ2 * 8000.0);
      this.dataWatcher = entityIn.getDataWatcher();
   }

   public byte getHeadPitch() {
      return this.headPitch;
   }

   public byte getPitch() {
      return this.pitch;
   }

   public int getX() {
      return this.x;
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.entityId);
      buf.writeByte(this.type & 0xFF);
      buf.writeInt(this.x);
      buf.writeInt(this.y);
      buf.writeInt(this.z);
      buf.writeByte(this.yaw);
      buf.writeByte(this.pitch);
      buf.writeByte(this.headPitch);
      buf.writeShort(this.velocityX);
      buf.writeShort(this.velocityY);
      buf.writeShort(this.velocityZ);
      this.dataWatcher.writeTo(buf);
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readVarIntFromBuffer();
      this.type = buf.readByte() & 255;
      this.x = buf.readInt();
      this.y = buf.readInt();
      this.z = buf.readInt();
      this.yaw = buf.readByte();
      this.pitch = buf.readByte();
      this.headPitch = buf.readByte();
      this.velocityX = buf.readShort();
      this.velocityY = buf.readShort();
      this.velocityZ = buf.readShort();
      this.watcher = DataWatcher.readWatchedListFromPacketBuffer(buf);
   }

   public int getEntityType() {
      return this.type;
   }
}
