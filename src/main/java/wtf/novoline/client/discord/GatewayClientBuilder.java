package wtf.novoline.client.discord;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import wtf.novoline.enums.UserListType;

public class GatewayClientBuilder {
   private String salt;
   private Map<String, String> headers = new HashMap<>();
   private ClientProperties clientProperties;
   private Set<UserListType> userLists = Collections.emptySet();
   private Set<GatewayEventListener> listeners = new HashSet<>();
   private final URI uRI;

   public Map<String, String> getHeaders() {
      return this.headers;
   }

   public GatewayClientBuilder clientProperties(ClientProperties clientProperties2) {
      this.clientProperties = clientProperties2;
      return this;
   }

   public URI getURI() {
      return this.uRI;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof GatewayClientBuilder gatewayClientBuilder)) {
         return false;
      } else {
         if (!gatewayClientBuilder.canEqual(this)) {
            return false;
         }

         URI uri2 = this.getURI();
         URI uri3 = gatewayClientBuilder.getURI();
         if (uri2 == null ? uri3 == null : uri2.equals(uri3)) {
            Map headers2 = this.getHeaders();
            Map headers3 = gatewayClientBuilder.getHeaders();
            if (headers2 == null ? headers3 == null : headers2.equals(headers3)) {
               GatewayClientBuilder gatewayClientBuilder2 = this.userLists();
               GatewayClientBuilder gatewayClientBuilder3 = gatewayClientBuilder.userLists();
               if (gatewayClientBuilder2 == null ? gatewayClientBuilder3 == null : gatewayClientBuilder2.equals(gatewayClientBuilder3)) {
                  Set listeners2 = this.getListeners();
                  Set listeners3 = gatewayClientBuilder.getListeners();
                  if (listeners2 == null ? listeners3 == null : listeners2.equals(listeners3)) {
                     ClientProperties clientProperties2 = this.getClientProperties();
                     ClientProperties clientProperties3 = gatewayClientBuilder.getClientProperties();
                     if (clientProperties2 == null ? clientProperties3 == null : clientProperties2.equals(clientProperties3)) {
                        String salt2 = this.getSalt();
                        String salt3 = gatewayClientBuilder.getSalt();
                        return salt2 == null ? salt3 == null : salt2.equals(salt3);
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   private void requireNonNull(Object object, String text) {
      if (object == null) {
         throw new IllegalArgumentException(text + " may not be null");
      }
   }

   public GatewayClientBuilder(URI uri2) {
      this.uRI = uri2;
   }

   public GatewayClientBuilder userLists(UserListType... userListTypes) {
      this.userLists = Arrays.stream(userListTypes).collect(Collectors.toSet());
      return this;
   }

   public GatewayClientBuilder bearerToken(String text) {
      return this.header("Authorization", "Bearer " + text);
   }

   public GatewayClientBuilder listener(GatewayEventListener gatewayEventListener) {
      this.listeners.add(gatewayEventListener);
      return this;
   }

   public GatewayClientBuilder headers(Map<String, String> map) {
      this.headers = map;
      return this;
   }

   public ClientProperties getClientProperties() {
      return this.clientProperties;
   }

   @Override
   public int hashCode() {
      URI uri2 = this.getURI();
      int i = 59 + (uri2 == null ? 43 : uri2.hashCode());
      Map headers2 = this.getHeaders();
      i = i * 59 + (headers2 == null ? 43 : headers2.hashCode());
      GatewayClientBuilder gatewayClientBuilder = this.userLists();
      i = i * 59 + (gatewayClientBuilder == null ? 43 : gatewayClientBuilder.hashCode());
      Set listeners2 = this.getListeners();
      i = i * 59 + (listeners2 == null ? 43 : listeners2.hashCode());
      ClientProperties clientProperties2 = this.getClientProperties();
      i = i * 59 + (clientProperties2 == null ? 43 : clientProperties2.hashCode());
      String salt2 = this.getSalt();
      return i * 59 + (salt2 == null ? 43 : salt2.hashCode());
   }

   public GatewayClientBuilder salt(String text) {
      this.salt = text;
      return this;
   }

   public GatewayClientBuilder header(String text, String text2) {
      this.headers.put(text, text2);
      return this;
   }

   @Override
   public String toString() {
      return "GatewayClientBuilder(uri="
         + this.getURI()
         + ", headers="
         + this.getHeaders()
         + ", intents="
         + this.userLists()
         + ", eventListeners="
         + this.getListeners()
         + ", clientProperties="
         + this.getClientProperties()
         + ", ignSalt="
         + this.getSalt()
         + ")";
   }

   public GatewayClientBuilder listeners(Set<GatewayEventListener> set) {
      this.listeners = set;
      return this;
   }

   protected boolean canEqual(Object object) {
      return object instanceof GatewayClientBuilder;
   }

   public String getSalt() {
      return this.salt;
   }

   public Set<GatewayEventListener> getListeners() {
      return this.listeners;
   }

   public GatewayClient getGatewayClient() {
      this.requireNonNull(this.uRI, "gateway uri");
      this.requireNonNull(this.clientProperties, "client properties");
      this.requireNonNull(this.salt, "ign salt");
      GatewayClient gatewayClient = new GatewayClient(this.uRI, this.headers, this.clientProperties, this.userLists, this.salt);
      gatewayClient.getGatewayEventDispatcher().addAll(this.listeners);
      return gatewayClient;
   }
}
