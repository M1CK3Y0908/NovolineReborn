package net.minecraft.network.status.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.IChatComponent;

public class S00PacketServerInfo implements Packet<INetHandlerStatusClient> {
   private ServerStatusResponse response;
   private static final Gson GSON = new GsonBuilder()
      .registerTypeAdapter(
         net.minecraft.network.ServerStatusResponse.MinecraftProtocolVersionIdentifier.class, new net.minecraft.network.ServerStatusResponse.MinecraftProtocolVersionIdentifier.Serializer()
      )
      .registerTypeAdapter(net.minecraft.network.ServerStatusResponse.PlayerCountData.class, new net.minecraft.network.ServerStatusResponse.PlayerCountData.Serializer())
      .registerTypeAdapter(ServerStatusResponse.class, new net.minecraft.network.ServerStatusResponse.Serializer())
      .registerTypeHierarchyAdapter(IChatComponent.class, new net.minecraft.util.IChatComponent.Serializer())
      .registerTypeHierarchyAdapter(ChatStyle.class, new net.minecraft.util.ChatStyle.Serializer())
      .registerTypeAdapterFactory(new EnumTypeAdapterFactory())
      .create();

   public S00PacketServerInfo() {
   }

   public S00PacketServerInfo(ServerStatusResponse responseIn) {
      this.response = responseIn;
   }

   public ServerStatusResponse getResponse() {
      return this.response;
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeString(GSON.toJson(this.response));
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.response = GSON.fromJson(buf.readStringFromBuffer(32767), ServerStatusResponse.class);
   }

   public void processPacket(INetHandlerStatusClient handler) {
      handler.handleServerInfo(this);
   }
}
