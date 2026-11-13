package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C03PacketPlayer implements Packet<INetHandlerPlayServer> {
   public boolean moving;
   public float pitch;
   public float yaw;
   public double z;
   public double x;
   public double y;
   public boolean onGround;
   public boolean rotating;

   @Override
   public void writePacketData(PacketBuffer packetBuffer) throws IOException {
      packetBuffer.writeByte(this.onGround ? 1 : 0);
   }

   public C03PacketPlayer() {
   }

   @Override
   public void readPacketData(PacketBuffer packetBuffer) throws IOException {
      this.onGround = packetBuffer.readUnsignedByte() != 0;
   }

   public void processPacket(INetHandlerPlayServer handler) {
      handler.processPlayer(this);
   }

   public C03PacketPlayer(boolean flag) {
      this.onGround = flag;
   }

   public static class C04PacketPlayerPosition extends C03PacketPlayer {
      @Override
      public void writePacketData(PacketBuffer packetBuffer) throws IOException {
         packetBuffer.writeDouble(this.x);
         packetBuffer.writeDouble(this.y);
         packetBuffer.writeDouble(this.z);
         super.writePacketData(packetBuffer);
      }

      public C04PacketPlayerPosition() {
         this.moving = true;
      }

      @Override
      public void readPacketData(PacketBuffer packetBuffer) throws IOException {
         this.x = packetBuffer.readDouble();
         this.y = packetBuffer.readDouble();
         this.z = packetBuffer.readDouble();
         super.readPacketData(packetBuffer);
      }

      public C04PacketPlayerPosition(double d, double d2, double d3, boolean flag) {
         this.x = d;
         this.y = d2;
         this.z = d3;
         this.onGround = flag;
         this.moving = true;
      }
   }

   public static class C05PacketPlayerLook extends C03PacketPlayer {
      public C05PacketPlayerLook() {
         this.rotating = true;
      }

      @Override
      public void readPacketData(PacketBuffer packetBuffer) throws IOException {
         this.yaw = packetBuffer.readFloat();
         this.pitch = packetBuffer.readFloat();
         super.readPacketData(packetBuffer);
      }

      public C05PacketPlayerLook(float f, float f2, boolean flag) {
         this.yaw = f;
         this.pitch = f2;
         this.onGround = flag;
         this.rotating = true;
      }

      @Override
      public void writePacketData(PacketBuffer packetBuffer) throws IOException {
         packetBuffer.writeFloat(this.yaw);
         packetBuffer.writeFloat(this.pitch);
         super.writePacketData(packetBuffer);
      }
   }

   public static class C06PacketPlayerPosLook extends C03PacketPlayer {
      @Override
      public void readPacketData(PacketBuffer buf) throws IOException {
         this.x = buf.readDouble();
         this.y = buf.readDouble();
         this.z = buf.readDouble();
         this.yaw = buf.readFloat();
         this.pitch = buf.readFloat();
         super.readPacketData(buf);
      }

      public C06PacketPlayerPosLook() {
         this.moving = true;
         this.rotating = true;
      }

      @Override
      public void writePacketData(PacketBuffer buf) throws IOException {
         buf.writeDouble(this.x);
         buf.writeDouble(this.y);
         buf.writeDouble(this.z);
         buf.writeFloat(this.yaw);
         buf.writeFloat(this.pitch);
         super.writePacketData(buf);
      }

      public C06PacketPlayerPosLook(double d, double d2, double d3, float f, float f2, boolean flag) {
         this.x = d;
         this.y = d2;
         this.z = d3;
         this.yaw = f;
         this.pitch = f2;
         this.onGround = flag;
         this.rotating = true;
         this.moving = true;
      }
   }
}
