package net.minecraft.client.network;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.Collections;
import java.util.List;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wtf.novoline.util.Helper1;
import wtf.novoline.utils.MinecraftUtils;

public class OldServerPinger2 {
   private static final Logger logger = LogManager.getLogger();
   private final List<NetworkManager> list = Collections.synchronizedList(Lists.newArrayList());

   class INetHandlerStatusClientImpl implements INetHandlerStatusClient {
      final Helper1 val$p_pingServer_2_;
      final NetworkManager val$networkmanager;
      final String val$p_pingServer_1_;
      private boolean flag;

      @Override
      public void handleServerInfo(S00PacketServerInfo s00PacketServerInfo) {
         ServerStatusResponse response = s00PacketServerInfo.getResponse();
         if (response.getPlayerCountData() != null) {
            this.val$p_pingServer_2_.text = String.valueOf(response.getPlayerCountData().getOnlinePlayerCount());
            if (ArrayUtils.isNotEmpty(response.getPlayerCountData().getPlayers())) {
               StringBuilder stringBuilder = new StringBuilder();

               for (GameProfile gameProfile : response.getPlayerCountData().getPlayers()) {
                  if (stringBuilder.length() > 0) {
                     stringBuilder.append("\n");
                  }

                  stringBuilder.append(gameProfile.getName());
               }

               if (response.getPlayerCountData().getPlayers().length < response.getPlayerCountData().getOnlinePlayerCount()) {
                  if (stringBuilder.length() > 0) {
                     stringBuilder.append("\n");
                  }

                  stringBuilder.append("... and ")
                     .append(response.getPlayerCountData().getOnlinePlayerCount() - response.getPlayerCountData().getPlayers().length)
                     .append(" more ...");
               }

               this.val$p_pingServer_2_.text2 = stringBuilder.toString();
            }
         } else {
            this.val$p_pingServer_2_.text2 = "";
         }

         this.val$networkmanager.sendPacket(new C01PacketPing(MinecraftUtils.getSystemTime()));
         this.flag = true;
      }

      @Override
      public void handlePong(S01PacketPong s01PacketPong) {
         this.val$networkmanager.closeChannel(new ChatComponentText("Finished"));
      }

      @Override
      public void onDisconnect(IChatComponent iChatComponent) {
         if (!this.flag) {
            OldServerPinger2.logger.error("Can't ping " + this.val$p_pingServer_1_ + ": " + iChatComponent.getUnformattedText());
         }
      }

      INetHandlerStatusClientImpl(Helper1 helper1, NetworkManager networkManager, String text3) {
         this.val$p_pingServer_2_ = helper1;
         this.val$networkmanager = networkManager;
         this.val$p_pingServer_1_ = text3;
         this.flag = false;
      }
   }
}
