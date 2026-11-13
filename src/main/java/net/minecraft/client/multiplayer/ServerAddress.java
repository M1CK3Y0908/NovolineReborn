package net.minecraft.client.multiplayer;

import java.net.IDN;
import java.util.Hashtable;
import javax.naming.directory.InitialDirContext;

public class ServerAddress {
   private final String ipAddress;
   private final int serverPort;

   public int getPort() {
      return this.serverPort;
   }

   public String getIP() {
      return IDN.toASCII(this.ipAddress);
   }

   public static ServerAddress fromString(String text) {
      if (text == null) {
         return null;
      }

      String[] texts = text.split(":");
      if (text.startsWith("[")) {
         int i = text.indexOf("]");
         if (i > 0) {
            String text2 = text.substring(1, i);
            String text3 = text.substring(i + 1).trim();
            if (text3.startsWith(":") && text3.length() > 0) {
               text3 = text3.substring(1);
               texts = new String[]{text2, text3};
            } else {
               texts = new String[]{text2};
            }
         }
      }

      if (texts.length > 2) {
         texts = new String[]{text};
      }

      String text4 = texts[0];
      int i2 = texts.length > 1 ? createInt(texts[1]) : 25565;
      if (i2 == 25565) {
         String[] serverAddress = getServerAddress(text4);
         text4 = serverAddress[0];
         i2 = createInt(serverAddress[1]);
      }

      return new ServerAddress(text4, i2);
   }

   private static int createInt(String text) {
      try {
         return Integer.parseInt(text.trim());
      } catch (Exception exception) {
         return 25565;
      }
   }

   private ServerAddress(String address, int port) {
      this.ipAddress = address;
      this.serverPort = port;
   }

   private static String[] getServerAddress(String text) {
      try {
         Class.forName("com.sun.jndi.dns.DnsContextFactory");
         Hashtable hashtable = new Hashtable();
         hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
         hashtable.put("java.naming.provider.url", "dns:");
         hashtable.put("com.sun.jndi.dns.timeout.retries", "1");
         String[] stringArray = new InitialDirContext(hashtable)
            .getAttributes("_minecraft._tcp." + text, new String[]{"SRV"})
            .get("srv")
            .get()
            .toString()
            .split(" ", 4);
         return new String[]{stringArray[3], stringArray[2]};
      } catch (Throwable throwable) {
         return new String[]{text, Integer.toString(25565)};
      }
   }
}
