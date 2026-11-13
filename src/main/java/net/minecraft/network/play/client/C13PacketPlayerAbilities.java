package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C13PacketPlayerAbilities implements Packet<INetHandlerPlayServer> {
   public boolean creativeMode;
   public boolean flying;
   public float walkSpeed;
   public boolean invulnerable;
   public boolean allowFlying;
   public float flySpeed;

   public void processPacket(INetHandlerPlayServer handler) {
      handler.processPlayerAbilities(this);
   }

   public C13PacketPlayerAbilities() {
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      byte b0 = buf.readByte();
      this.invulnerable = (b0 & 1) > 0;
      this.flying = (b0 & 2) > 0;
      this.allowFlying = (b0 & 4) > 0;
      this.creativeMode = (b0 & 8) > 0;
      this.flySpeed = buf.readFloat();
      this.walkSpeed = buf.readFloat();
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      byte b0 = 0;
      if (this.invulnerable) {
         b0 = 1;
      }

      if (this.flying) {
         b0 = (byte)(b0 | 2);
      }

      if (this.allowFlying) {
         b0 = (byte)(b0 | 4);
      }

      if (this.creativeMode) {
         b0 = (byte)(b0 | 8);
      }

      buf.writeByte(b0);
      buf.writeFloat(this.flySpeed);
      buf.writeFloat(this.walkSpeed);
   }

   public C13PacketPlayerAbilities(PlayerCapabilities capabilities) {
      this.invulnerable = capabilities.disableDamage;
      this.flying = capabilities.isFlying;
      this.allowFlying = capabilities.allowFlying;
      this.creativeMode = capabilities.isCreativeMode;
      this.flySpeed = capabilities.getFlySpeed();
      this.walkSpeed = capabilities.getWalkSpeed();
   }
}
