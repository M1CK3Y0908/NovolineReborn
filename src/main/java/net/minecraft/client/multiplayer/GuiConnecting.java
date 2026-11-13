package net.minecraft.client.multiplayer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wtf.novoline.Novoline;
import wtf.novoline.event.ServerConnectEvent;

public class GuiConnecting extends GuiScreen {
   private static final AtomicInteger CONNECTION_ID = new AtomicInteger(0);
   private boolean cancel;
   private NetworkManager networkManager;
   private static final Logger logger = LogManager.getLogger();
   private final GuiScreen previousGuiScreen;

   static Minecraft access$000(GuiConnecting guiConnecting) {
      return guiConnecting.mc;
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 0) {
         this.cancel = true;
         if (this.networkManager != null) {
            this.networkManager.closeChannel(new ChatComponentText("Aborted"));
         }

         this.mc.displayGuiScreen(this.previousGuiScreen);
      }
   }

   private void connect(final String ip, final int port) {
      logger.info("Connecting to " + ip + ", " + port);
      (new Thread("Server Connector #" + CONNECTION_ID.incrementAndGet()) {
            @Override
            public void run() {
               InetAddress inetAddress = null;

               try {
                  if (GuiConnecting.this.cancel) {
                     return;
                  }

                  Novoline novoline2 = Novoline.getNovoline();
                  novoline2.setServerData(GuiConnecting.access$100(GuiConnecting.this).getCurrentServerData());
                  inetAddress = InetAddress.getByName(ip);
                  novoline2.getBus().post(new ServerConnectEvent(ip, inetAddress.getHostAddress()));
                  GuiConnecting.this.networkManager = NetworkManager.createNetworkManagerAndConnect(
                     inetAddress, port, GuiConnecting.access$300(GuiConnecting.this).gameSettings.isUsingNativeTransport()
                  );
                  GuiConnecting.this.networkManager
                     .setNetHandler(
                        new NetHandlerLoginClient(
                           GuiConnecting.this.networkManager, GuiConnecting.access$400(GuiConnecting.this), GuiConnecting.this.previousGuiScreen
                        )
                     );
                  GuiConnecting.this.networkManager.sendPacket(new C00Handshake(ip, port, EnumConnectionState.LOGIN));
                  GuiConnecting.this.networkManager.sendPacket(new C00PacketLoginStart(GuiConnecting.access$000(GuiConnecting.this).getSession().getProfile()));
               } catch (UnknownHostException unknownHostException) {
                  if (GuiConnecting.this.cancel) {
                     return;
                  }

                  GuiConnecting.logger.error("Couldn't connect to server", unknownHostException);
                  GuiConnecting.access$200(GuiConnecting.this)
                     .displayGuiScreen(
                        new GuiDisconnected(
                           GuiConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", "Unknown host")
                        )
                     );
               } catch (Exception exception) {
                  if (GuiConnecting.this.cancel) {
                     return;
                  }

                  GuiConnecting.logger.error("Couldn't connect to server", exception);
                  String text = exception.toString();
                  if (inetAddress != null) {
                     String text2 = inetAddress.toString() + ":" + port;
                     text = text.replaceAll(text2, "");
                  }

                  GuiConnecting.access$500(GuiConnecting.this)
                     .displayGuiScreen(
                        new GuiDisconnected(
                           GuiConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", text)
                        )
                     );
               }
            }
         })
         .start();
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")));
   }

   static Minecraft access$100(GuiConnecting guiConnecting) {
      return guiConnecting.mc;
   }

   static Minecraft access$200(GuiConnecting guiConnecting) {
      return guiConnecting.mc;
   }

   public GuiConnecting(GuiScreen guiScreen, Minecraft mcIn, ServerData serverData) {
      this.mc = mcIn;
      this.previousGuiScreen = guiScreen;
      ServerAddress serverAddress = ServerAddress.fromString(serverData.serverIP);
      mcIn.loadWorld((WorldClient)null);
      mcIn.setServerData(serverData);
      this.connect(serverAddress.getIP(), serverAddress.getPort());
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      if (this.networkManager == null) {
         this.drawCenteredString(this.fontRendererObj, I18n.format("connect.connecting"), this.width / 2, this.height / 2 - 50, 16777215);
      } else {
         this.drawCenteredString(this.fontRendererObj, I18n.format("connect.authorizing"), this.width / 2, this.height / 2 - 50, 16777215);
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   static Minecraft access$300(GuiConnecting guiConnecting) {
      return guiConnecting.mc;
   }

   static Minecraft access$400(GuiConnecting guiConnecting) {
      return guiConnecting.mc;
   }

   public GuiConnecting(GuiScreen guiScreen, Minecraft mcIn, String hostName, int port) {
      this.mc = mcIn;
      this.previousGuiScreen = guiScreen;
      mcIn.loadWorld((WorldClient)null);
      this.connect(hostName, port);
   }

   static Minecraft access$500(GuiConnecting guiConnecting) {
      return guiConnecting.mc;
   }

   @Override
   public void updateScreen() {
      if (this.networkManager != null) {
         if (this.networkManager.isChannelOpen()) {
            this.networkManager.processReceivedPackets();
         } else {
            this.networkManager.checkDisconnected();
         }
      }
   }
}
