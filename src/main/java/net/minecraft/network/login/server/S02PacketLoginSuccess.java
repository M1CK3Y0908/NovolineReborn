package net.minecraft.network.login.server;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;

public class S02PacketLoginSuccess implements Packet<INetHandlerLoginClient> {
   private GameProfile profile;

   public GameProfile getProfile() {
      return this.profile;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      String text = buf.readStringFromBuffer(36);
      String text2 = buf.readStringFromBuffer(16);
      UUID uUID = UUID.fromString(text);
      this.profile = new GameProfile(uUID, text2);
   }

   public void processPacket(INetHandlerLoginClient handler) {
      handler.handleLoginSuccess(this);
   }

   public S02PacketLoginSuccess() {
   }

   public S02PacketLoginSuccess(GameProfile profileIn) {
      this.profile = profileIn;
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      UUID uUID = this.profile.getId();
      buf.writeString(uUID == null ? "" : uUID.toString());
      buf.writeString(this.profile.getName());
   }
}
