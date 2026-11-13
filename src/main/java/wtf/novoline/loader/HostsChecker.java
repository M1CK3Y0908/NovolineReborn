package wtf.novoline.loader;

import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import wtf.novoline.enums.ServerType;
import wtf.novoline.utils.hypixel.HypixelState;
import wtf.novoline.support.SneakyThrow;

public class HostsChecker {
   private static final HashMap<String, Integer> cloudflareRanges = new HashMap<>();
   private static final List<HostsChecker.ServerCondition> serverConditions = List.of(
      HostsChecker.ServerCondition.createServerCondition(
         ServerType.HYPIXEL,
         context -> matchesDomain(context.getConnectingHostname(), "hypixel.net")
            || matchesDomain(context.getConnectingHostname(), "hypixel.io")
               && cloudflareRanges.entrySet().stream().anyMatch(entry -> isInPrefix(context.getConnectingIp(), entry.getKey(), entry.getValue()))
               && !isHostsEntryPresent("hypixel.net")
      )
   );

   public static void detectServer(HostsChecker.Context context2) {
      HypixelState.reset();

      for (HostsChecker.ServerCondition serverCondition : serverConditions) {
         if (serverCondition.getCondition().test(context2)) {
            HypixelState.setServerType(serverCondition.getServer());
         }
      }
   }

   static {
      cloudflareRanges.put("209.222.114.0", 23);
      cloudflareRanges.put("173.245.48.0", 20);
      cloudflareRanges.put("103.21.244.0", 22);
      cloudflareRanges.put("103.22.200.0", 22);
      cloudflareRanges.put("103.31.4.0", 22);
      cloudflareRanges.put("141.101.64.0", 18);
      cloudflareRanges.put("108.162.192.0", 20);
      cloudflareRanges.put("190.93.240.0", 20);
      cloudflareRanges.put("188.114.96.0", 20);
      cloudflareRanges.put("197.234.240.0", 22);
      cloudflareRanges.put("198.41.128.0", 17);
      cloudflareRanges.put("162.158.0.0", 15);
      cloudflareRanges.put("104.16.0.0", 12);
      cloudflareRanges.put("172.64.0.0", 13);
      cloudflareRanges.put("131.0.72.0", 22);
      cloudflareRanges.put("2400:cb00::", 32);
      cloudflareRanges.put("2606:4700::", 32);
      cloudflareRanges.put("2803:f800::", 32);
      cloudflareRanges.put("2405:b500::", 32);
      cloudflareRanges.put("2405:8100::", 32);
      cloudflareRanges.put("2a06:98c0::", 29);
      cloudflareRanges.put("2c0f:f248::", 32);
   }

   private static boolean matchesDomain(String text, String text2) {
      return text.equals(text2) || text.equals(text2 + ".") || text.endsWith("." + text2) || text.endsWith("." + text2 + ".");
   }

   private static boolean isInPrefix(String text, String text2, int i) {
      try {
         byte[] address = InetAddress.getByName(text).getAddress();
         byte[] address2 = InetAddress.getByName(text2).getAddress();
         if (address.length != address2.length) {
            return false;
         }

         int i2 = i;
         int i3 = 0;

         while (i2 >= 8) {
            if (address[i3] != address2[i3]) {
               return false;
            }

            i3++;
            i2 -= 8;
         }

         int i4 = 65280 >> i2 & 0xFF;
         return (address[i3] & i4) == (address2[i3] & i4);
      } catch (Throwable throwable) {
         return false;
      }
   }

   private static boolean isHostsEntryPresent(String text) {
      try {
      Path path = Paths.get(System.getenv("windir")).resolve("System32").resolve("drivers").resolve("etc").resolve("hosts");
      return Files.notExists(path)
         || Files.readAllLines(path).stream().filter(string -> !string.startsWith("#")).anyMatch(string -> string.contains(text));
   } catch (Throwable sneakyThrowable) {
         throw SneakyThrow.sneak(sneakyThrowable);
      }
   }

   public static final class Context {
      private final String connectingHostname;
      private final String connectingIp;

      public String getConnectingHostname() {
         return this.connectingHostname;
      }

      Context(String text, String text2) {
         this.connectingHostname = text;
         this.connectingIp = text2;
      }

      @Override
      public int hashCode() {
         String connectingHostname2 = this.getConnectingHostname();
         int i = 59 + (connectingHostname2 == null ? 43 : connectingHostname2.hashCode());
         String connectingIp2 = this.getConnectingIp();
         return i * 59 + (connectingIp2 == null ? 43 : connectingIp2.hashCode());
      }

      public String getConnectingIp() {
         return this.connectingIp;
      }

      @Override
      public String toString() {
         return "ServerChecks.Context(connectingHostname=" + this.getConnectingHostname() + ", connectingIp=" + this.getConnectingIp() + ")";
      }

      public static HostsChecker.ContextBuilder builder() {
         return new HostsChecker.ContextBuilder();
      }

      @Override
      public boolean equals(Object object) {
         if (object == this) {
            return true;
         } else if (!(object instanceof HostsChecker.Context context2)) {
            return false;
         } else {
            String connectingHostname2 = this.getConnectingHostname();
            String connectingHostname3 = context2.getConnectingHostname();
            if (connectingHostname2 == null ? connectingHostname3 == null : connectingHostname2.equals(connectingHostname3)) {
               String connectingIp2 = this.getConnectingIp();
               String connectingIp3 = context2.getConnectingIp();
               return connectingIp2 == null ? connectingIp3 == null : connectingIp2.equals(connectingIp3);
            } else {
               return false;
            }
         }
      }
   }

   public static class ContextBuilder {
      private String connectingIp;
      private String connectingHostname;

      @Override
      public String toString() {
         return "ServerChecks.Context.ContextBuilder(connectingHostname=" + this.connectingHostname + ", connectingIp=" + this.connectingIp + ")";
      }

      public HostsChecker.ContextBuilder connectingIp(String text) {
         this.connectingIp = text;
         return this;
      }

      ContextBuilder() {
      }

      public HostsChecker.ContextBuilder connectingHostname(String text) {
         this.connectingHostname = text;
         return this;
      }

      public HostsChecker.Context getContext() {
         return new HostsChecker.Context(this.connectingHostname, this.connectingIp);
      }
   }

   public static class ServerCondition {
      private final ServerType server;
      private final Predicate<HostsChecker.Context> condition;

      private ServerCondition(ServerType serverType, Predicate<HostsChecker.Context> predicate) {
         this.server = serverType;
         this.condition = predicate;
      }

      @Override
      public boolean equals(Object object) {
         if (object == this) {
            return true;
         } else if (!(object instanceof HostsChecker.ServerCondition serverCondition)) {
            return false;
         } else if (!serverCondition.canEqual(this)) {
            return false;
         } else {
            ServerType server2 = this.getServer();
            ServerType server3 = serverCondition.getServer();
            if (server2 == null ? server3 == null : server2.equals(server3)) {
               Predicate condition2 = this.getCondition();
               Predicate condition3 = serverCondition.getCondition();
               return condition2 == null ? condition3 == null : condition2.equals(condition3);
            } else {
               return false;
            }
         }
      }

      public Predicate<HostsChecker.Context> getCondition() {
         return this.condition;
      }

      @Override
      public String toString() {
         return "ServerChecks.ServerCondition(server=" + this.getServer() + ", condition=" + this.getCondition() + ")";
      }

      public static HostsChecker.ServerCondition createServerCondition(ServerType serverType, Predicate<HostsChecker.Context> predicate) {
         return new HostsChecker.ServerCondition(serverType, predicate);
      }

      @Override
      public int hashCode() {
         ServerType server2 = this.getServer();
         int i = 59 + (server2 == null ? 43 : server2.hashCode());
         Predicate condition2 = this.getCondition();
         return i * 59 + (condition2 == null ? 43 : condition2.hashCode());
      }

      public ServerType getServer() {
         return this.server;
      }

      protected boolean canEqual(Object object) {
         return object instanceof HostsChecker.ServerCondition;
      }
   }
}
