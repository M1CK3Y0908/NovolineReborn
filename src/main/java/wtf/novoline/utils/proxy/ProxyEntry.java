package wtf.novoline.utils.proxy;

import com.google.gson.annotations.SerializedName;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import java.net.InetSocketAddress;

public class ProxyEntry {
   @SerializedName("proxy_type")
   private final ProxyEntry.Socks4Type proxyType;
   @SerializedName("address")
   private final String address;
   @SerializedName("port")
   private final int port;
   @SerializedName("authentication")
   private final ProxyEntry.Authentication authentication;

   @Override
   public int hashCode() {
      int i = 59 + this.getPort();
      ProxyEntry.Socks4Type proxyType2 = this.getProxyType();
      i = i * 59 + (proxyType2 == null ? 43 : proxyType2.hashCode());
      String address2 = this.getAddress();
      i = i * 59 + (address2 == null ? 43 : address2.hashCode());
      ProxyEntry.Authentication authentication2 = this.getAuthentication();
      return i * 59 + (authentication2 == null ? 43 : authentication2.hashCode());
   }

   public ProxyHandler getProxyHandler() {
      return switch (this.proxyType) {
         case SOCKS_4 -> new Socks4ProxyHandler(new InetSocketAddress(this.address, this.port));
         case SOCKS_5 -> {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(this.address, this.port);
            yield this.authentication != null
               ? new Socks5ProxyHandler(inetSocketAddress, this.authentication.getUsername(), this.authentication.getPassword())
               : new Socks5ProxyHandler(inetSocketAddress);
         }
      };
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof ProxyEntry proxyEntry)) {
         return false;
      } else {
         if (!proxyEntry.canEqual(this)) {
            return false;
         }

         if (this.getPort() != proxyEntry.getPort()) {
            return false;
         }

         ProxyEntry.Socks4Type proxyType2 = this.getProxyType();
         ProxyEntry.Socks4Type proxyType3 = proxyEntry.getProxyType();
         if (proxyType2 == null ? proxyType3 == null : proxyType2.equals(proxyType3)) {
            String address2 = this.getAddress();
            String address3 = proxyEntry.getAddress();
            if (address2 == null ? address3 == null : address2.equals(address3)) {
               ProxyEntry.Authentication authentication2 = this.getAuthentication();
               ProxyEntry.Authentication authentication3 = proxyEntry.getAuthentication();
               return authentication2 == null ? authentication3 == null : authentication2.equals(authentication3);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Override
   public String toString() {
      return "Proxy(proxyType="
         + this.getProxyType()
         + ", address="
         + this.getAddress()
         + ", port="
         + this.getPort()
         + ", authentication="
         + this.getAuthentication()
         + ")";
   }

   protected boolean canEqual(Object object) {
      return object instanceof ProxyEntry;
   }

   public ProxyEntry.Authentication getAuthentication() {
      return this.authentication;
   }

   public int getPort() {
      return this.port;
   }

   public ProxyEntry.Socks4Type getProxyType() {
      return this.proxyType;
   }

   public static ProxyEntry.ProxyBuilder builder() {
      return new ProxyEntry.ProxyBuilder();
   }

   public String getAddress() {
      return this.address;
   }

   ProxyEntry(ProxyEntry.Socks4Type socks4Type, String text, int i, ProxyEntry.Authentication authentication2) {
      this.proxyType = socks4Type;
      this.address = text;
      this.port = i;
      this.authentication = authentication2;
   }

   public static final class Authentication {
      @SerializedName("username")
      private final String username;
      @SerializedName("password")
      private final String password;

      @Override
      public String toString() {
         return "Proxy.Authentication(username=" + this.getUsername() + ", password=" + this.getPassword() + ")";
      }

      public static ProxyEntry.Authentication createAuthentication(String text, String text2) {
         return new ProxyEntry.Authentication(text, text2);
      }

      public String getPassword() {
         return this.password;
      }

      private Authentication(String text, String text2) {
         this.username = text;
         this.password = text2;
      }

      @Override
      public int hashCode() {
         String username2 = this.getUsername();
         int i = 59 + (username2 == null ? 43 : username2.hashCode());
         String password2 = this.getPassword();
         return i * 59 + (password2 == null ? 43 : password2.hashCode());
      }

      public String getUsername() {
         return this.username;
      }

      @Override
      public boolean equals(Object object) {
         if (object == this) {
            return true;
         } else if (!(object instanceof ProxyEntry.Authentication authentication2)) {
            return false;
         } else {
            String username2 = this.getUsername();
            String username3 = authentication2.getUsername();
            if (username2 == null ? username3 == null : username2.equals(username3)) {
               String password2 = this.getPassword();
               String password3 = authentication2.getPassword();
               return password2 == null ? password3 == null : password2.equals(password3);
            } else {
               return false;
            }
         }
      }
   }

   public static class ProxyBuilder {
      private ProxyEntry.Authentication authentication;
      private String address;
      private ProxyEntry.Socks4Type proxyType;
      private int port;

      public ProxyEntry.ProxyBuilder address(String text) {
         this.address = text;
         return this;
      }

      public ProxyEntry.ProxyBuilder port(int i) {
         this.port = i;
         return this;
      }

      public ProxyEntry build() {
         return new ProxyEntry(this.proxyType, this.address, this.port, this.authentication);
      }

      public ProxyEntry.ProxyBuilder proxyType(ProxyEntry.Socks4Type socks4Type) {
         this.proxyType = socks4Type;
         return this;
      }

      ProxyBuilder() {
      }

      @Override
      public String toString() {
         return "Proxy.ProxyBuilder(proxyType="
            + this.proxyType
            + ", address="
            + this.address
            + ", port="
            + this.port
            + ", authentication="
            + this.authentication
            + ")";
      }

      public ProxyEntry.ProxyBuilder authentication(ProxyEntry.Authentication authentication2) {
         this.authentication = authentication2;
         return this;
      }
   }

   public enum Socks4Type {
      SOCKS_4,
      SOCKS_5;
   }
}
