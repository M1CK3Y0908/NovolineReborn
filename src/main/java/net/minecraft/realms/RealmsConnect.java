package net.minecraft.realms;

import java.net.InetAddress;
import java.net.UnknownHostException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.CommandSuggestions;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wtf.novoline.utils.MinecraftUtils;

public class RealmsConnect {
   private NetworkManager networkManager;
   private volatile boolean flag = false;
   private final CommandSuggestions commandSuggestions;
   private static final Logger logger = LogManager.getLogger();

   public void update() {
      this.flag = true;
   }

   public RealmsConnect(CommandSuggestions commandSuggestions2) {
      this.commandSuggestions = commandSuggestions2;
   }

   class ThreadChild extends Thread {
      final String val$p_connect_1_;
      final int val$p_connect_2_;

      @Override
      public void run() {
         InetAddress inetAddress = null;

         try {
            inetAddress = InetAddress.getByName(this.val$p_connect_1_);
            if (RealmsConnect.this.flag) {
               return;
            }

            RealmsConnect.this.networkManager = NetworkManager.createNetworkManagerAndConnect(
               inetAddress, this.val$p_connect_2_, Minecraft.getMinecraft().gameSettings.isUsingNativeTransport()
            );
            if (RealmsConnect.this.flag) {
               return;
            }

            RealmsConnect.this.networkManager
               .setNetHandler(
                  new NetHandlerLoginClient(
                     RealmsConnect.this.networkManager, Minecraft.getMinecraft(), RealmsConnect.this.commandSuggestions.getGuiScreenRealmsProxy()
                  )
               );
            if (RealmsConnect.this.flag) {
               return;
            }

            RealmsConnect.this.networkManager.sendPacket(new C00Handshake(this.val$p_connect_1_, this.val$p_connect_2_, EnumConnectionState.LOGIN));
            if (RealmsConnect.this.flag) {
               return;
            }

            RealmsConnect.this.networkManager.sendPacket(new C00PacketLoginStart(Minecraft.getMinecraft().getSession().getProfile()));
         } catch (UnknownHostException unknownHostException) {
            MinecraftUtils.clearResourcePack();
            if (RealmsConnect.this.flag) {
               return;
            }

            RealmsConnect.logger.error("Couldn't connect to world", unknownHostException);
            Minecraft.getMinecraft().getResourcePackRepository().clearResourcePack();
            MinecraftUtils.displayScreen(
               new DisconnectedRealmsScreen(
                  RealmsConnect.this.commandSuggestions,
                  "connect.failed",
                  new ChatComponentTranslation("disconnect.genericReason", "Unknown host '" + this.val$p_connect_1_ + "'")
               )
            );
         } catch (Exception exception) {
            MinecraftUtils.clearResourcePack();
            if (RealmsConnect.this.flag) {
               return;
            }

            RealmsConnect.logger.error("Couldn't connect to world", exception);
            String text = exception.toString();
            if (inetAddress != null) {
               String text2 = inetAddress.toString() + ":" + this.val$p_connect_2_;
               text = text.replaceAll(text2, "");
            }

            MinecraftUtils.displayScreen(
               new DisconnectedRealmsScreen(
                  RealmsConnect.this.commandSuggestions, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", text)
               )
            );
         }
      }

      ThreadChild(String text, String text2, int i) {
         super(text);
         this.val$p_connect_1_ = text2;
         this.val$p_connect_2_ = i;
      }
   }
}
