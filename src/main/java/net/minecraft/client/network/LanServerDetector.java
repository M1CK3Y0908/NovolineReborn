package net.minecraft.client.network;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ThreadLanServerPing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LanServerDetector {
   private static final AtomicInteger THREAD_ID = new AtomicInteger(0);
   private static final Logger logger = LogManager.getLogger();

   public static class LanServer {
      private String lanServerMotd;
      private String lanServerIpPort;
      private long timeLastSeen;

      public void updateLastSeen() {
         this.timeLastSeen = Minecraft.getSystemTime();
      }

      public String getServerIpPort() {
         return this.lanServerIpPort;
      }

      public LanServer(String motd, String address) {
         this.lanServerMotd = motd;
         this.lanServerIpPort = address;
         this.timeLastSeen = Minecraft.getSystemTime();
      }

      public String getServerMotd() {
         return this.lanServerMotd;
      }
   }

   public static class LanServerList {
      boolean wasUpdated;
      private List<LanServerDetector.LanServer> listOfLanServers = Lists.newArrayList();

      public synchronized List<LanServerDetector.LanServer> getLanServers() {
         return Collections.unmodifiableList(this.listOfLanServers);
      }

      public synchronized boolean getWasUpdated() {
         return this.wasUpdated;
      }

      public synchronized void setWasNotUpdated() {
         this.wasUpdated = false;
      }

      public synchronized void addServer(String pingResponse, InetAddress address) {
         String motdFromPingResponse = ThreadLanServerPing.getMotdFromPingResponse(pingResponse);
         String adFromPingResponse = ThreadLanServerPing.getAdFromPingResponse(pingResponse);
         if (adFromPingResponse != null) {
            adFromPingResponse = address.getHostAddress() + ":" + adFromPingResponse;
            boolean flag = false;

            for (LanServerDetector.LanServer lanServer : this.listOfLanServers) {
               if (lanServer.getServerIpPort().equals(adFromPingResponse)) {
                  lanServer.updateLastSeen();
                  flag = true;
                  break;
               }
            }

            if (!flag) {
               this.listOfLanServers.add(new LanServerDetector.LanServer(motdFromPingResponse, adFromPingResponse));
               this.wasUpdated = true;
            }
         }
      }
   }

   public static class ThreadLanServerFind extends Thread {
      private final LanServerDetector.LanServerList localServerList;
      private final MulticastSocket socket;
      private final InetAddress broadcastAddress;

      @Override
      public void run() {
         byte[] bArray = new byte[1024];

         while (!this.isInterrupted()) {
            DatagramPacket datagramPacket = new DatagramPacket(bArray, bArray.length);

            try {
               this.socket.receive(datagramPacket);
            } catch (SocketTimeoutException socketTimeoutException) {
               continue;
            } catch (IOException ioException) {
               LanServerDetector.logger.error("Couldn't ping server", ioException);
               break;
            }

            String text = new String(datagramPacket.getData(), datagramPacket.getOffset(), datagramPacket.getLength());
            LanServerDetector.logger.debug(datagramPacket.getAddress() + ": " + text);
            this.localServerList.addServer(text, datagramPacket.getAddress());
         }

         try {
            this.socket.leaveGroup(this.broadcastAddress);
         } catch (IOException ioException2) {
         }

         this.socket.close();
      }

      public ThreadLanServerFind(LanServerDetector.LanServerList localServerListIn) throws IOException {
         super("LanServerDetector #" + LanServerDetector.THREAD_ID.incrementAndGet());
         this.localServerList = localServerListIn;
         this.setDaemon(true);
         this.socket = new MulticastSocket(4445);
         this.broadcastAddress = InetAddress.getByName("224.0.2.60");
         this.socket.setSoTimeout(5000);
         this.socket.joinGroup(this.broadcastAddress);
      }
   }
}
