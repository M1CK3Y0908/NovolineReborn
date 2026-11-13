package net.minecraft.client.multiplayer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadLanServerPing extends Thread {
   private boolean isStopping = true;
   private final String motd;
   private final String address;
   private static final AtomicInteger threadNumber = new AtomicInteger(0);
   private final DatagramSocket socket;
   private static final Logger logger = LogManager.getLogger();

   @Override
   public void run() {
      byte[] bytes = getPingResponse(this.motd, this.address).getBytes();

      while (!this.isInterrupted() && this.isStopping) {
         try {
            InetAddress byName = InetAddress.getByName("224.0.2.60");
            DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, byName, 4445);
            this.socket.send(datagramPacket);
         } catch (IOException ioException) {
            logger.warn("LanServerPinger: " + ioException.getMessage());
            break;
         }

         try {
            sleep(1500L);
         } catch (InterruptedException interruptedException) {
         }
      }
   }

   public static String getPingResponse(String motd, String address) {
      return "[MOTD]" + motd + "[/MOTD][AD]" + address + "[/AD]";
   }

   public static String getAdFromPingResponse(String response) {
      int i = response.indexOf("[/MOTD]");
      if (i < 0) {
         return null;
      }

      if (response.indexOf("[/MOTD]", i + "[/MOTD]".length()) >= 0) {
         return null;
      }

      int count2 = response.indexOf("[AD]", i + "[/MOTD]".length());
      if (count2 < 0) {
         return null;
      }

      int count3 = response.indexOf("[/AD]", count2 + "[AD]".length());
      return count3 < count2 ? null : response.substring(count2 + "[AD]".length(), count3);
   }

   public ThreadLanServerPing(String motdIn, String addressIn) throws IOException {
      super("LanServerPinger #" + threadNumber.incrementAndGet());
      this.motd = motdIn;
      this.address = addressIn;
      this.setDaemon(true);
      this.socket = new DatagramSocket();
   }

   @Override
   public void interrupt() {
      super.interrupt();
      this.isStopping = false;
   }

   public static String getMotdFromPingResponse(String response) {
      int i = response.indexOf("[MOTD]");
      if (i < 0) {
         return "missing no";
      }

      int count = response.indexOf("[/MOTD]", i + "[MOTD]".length());
      return count < i ? "missing no" : response.substring(i + "[MOTD]".length(), count);
   }
}
