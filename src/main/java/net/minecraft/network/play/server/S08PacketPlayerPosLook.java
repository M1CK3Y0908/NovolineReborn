package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S08PacketPlayerPosLook implements Packet<INetHandlerPlayClient> {
   private double y;
   private float yaw;
   private Set<S08PacketPlayerPosLook.EnumFlags> flags;
   private double x;
   private float pitch;
   private double z;

   public double getX() {
      return this.x;
   }

   public Set<S08PacketPlayerPosLook.EnumFlags> getFlags() {
      return this.flags;
   }

   public void setY(double d) {
      this.y = d;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.x = buf.readDouble();
      this.y = buf.readDouble();
      this.z = buf.readDouble();
      this.yaw = buf.readFloat();
      this.pitch = buf.readFloat();
      this.flags = S08PacketPlayerPosLook.EnumFlags.fromByte(buf.readUnsignedByte());
   }

   public void setYaw(float f) {
      this.yaw = f;
   }

   public double getZ() {
      return this.z;
   }

   public void setZ(double d) {
      this.z = d;
   }

   public double getY() {
      return this.y;
   }

   public S08PacketPlayerPosLook() {
   }

   public float getYaw() {
      return this.yaw;
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handlePlayerPosLook(this);
   }

   public void setX(double d) {
      this.x = d;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void setFlags(Set<S08PacketPlayerPosLook.EnumFlags> set) {
      this.flags = set;
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
      buf.writeFloat(this.yaw);
      buf.writeFloat(this.pitch);
      buf.writeByte(S08PacketPlayerPosLook.EnumFlags.toByte(this.flags));
   }

   public void setPitch(float f) {
      this.pitch = f;
   }

   public S08PacketPlayerPosLook(double xIn, double yIn, double zIn, float yawIn, float pitchIn, Set<S08PacketPlayerPosLook.EnumFlags> flagsIn) {
      this.x = xIn;
      this.y = yIn;
      this.z = zIn;
      this.yaw = yawIn;
      this.pitch = pitchIn;
      this.flags = flagsIn;
   }

   public enum EnumFlags {
      X(0),
      Y(1),
      Z(2),
      Y_ROT(3),
      X_ROT(4);

      private int bit;

      EnumFlags(int i) {
         this.bit = i;
      }

      private boolean isSet(int mask) {
         return (mask & this.getBit()) == this.getBit();
      }

      public static int toByte(Set<S08PacketPlayerPosLook.EnumFlags> flags) {
         int i = 0;

         for (S08PacketPlayerPosLook.EnumFlags enumFlags : flags) {
            i |= enumFlags.getBit();
         }

         return i;
      }

      private int getBit() {
         return 1 << this.bit;
      }

      public static Set<S08PacketPlayerPosLook.EnumFlags> fromByte(int b) {
         EnumSet enumSet = EnumSet.noneOf(S08PacketPlayerPosLook.EnumFlags.class);

         for (S08PacketPlayerPosLook.EnumFlags enumFlags : values()) {
            if (enumFlags.isSet(b)) {
               enumSet.add(enumFlags);
            }
         }

         return enumSet;
      }
   }
}
