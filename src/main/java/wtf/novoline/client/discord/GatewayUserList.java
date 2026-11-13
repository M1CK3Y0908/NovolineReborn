package wtf.novoline.client.discord;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class GatewayUserList extends ArrayList<GatewayUser> {
   private final Map<String, GatewayUser> usersByName = new WeakHashMap<>();
   private final String salt;
   private GatewayUser gatewayUser;
   private final MessageDigest messageDigest;

   public GatewayPresence findPresenceBySid(String text) {
      return this.stream()
         .map(GatewayUser::getPresence)
         .filter(Objects::nonNull)
         .flatMap(Collection::stream)
         .filter(gatewayPresence -> gatewayPresence.getSid().equals(text))
         .findFirst()
         .orElse(null);
   }

   private GatewayUserList(String text) {
      this.salt = text;

      try {
         this.messageDigest = MessageDigest.getInstance("SHA-256");
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
         throw new RuntimeException(noSuchAlgorithmException);
      }
   }

   public GatewayUser findById(long l) {
      return this.stream().filter(gatewayUser2 -> gatewayUser2.getId() == l).findFirst().orElse(null);
   }

   public GatewayUser getByName(String text) {
      return this.usersByName.computeIfAbsent(text, this::findByPresenceIgn0);
   }

   public void setGatewayUser(GatewayUser gatewayUser3) {
      this.gatewayUser = gatewayUser3;
   }

   public static GatewayUserList createGatewayUserList(String text) {
      return new GatewayUserList(text);
   }

   private GatewayUser findByPresenceIgn0(String text) {
      return this.stream()
         .filter(
            gatewayUser2 -> gatewayUser2.getPresence()
               .stream()
               .filter(Objects::nonNull)
               .anyMatch(gatewayPresence -> gatewayPresence.getIgn() != null && gatewayPresence.getIgn().equals(this.hashName(text)))
         )
         .findFirst()
         .orElse(null);
   }

   public GatewayUser getGatewayUser() {
      return this.gatewayUser;
   }

   public GatewayUser findByUsername(String text) {
      return this.stream().filter(gatewayUser2 -> gatewayUser2.getUsername().equals(text)).findFirst().orElse(null);
   }

   private String hashName(String text) {
      byte[] bytes = (text + this.salt).getBytes(StandardCharsets.UTF_8);
      byte[] bArray = this.messageDigest.digest(bytes);
      return new BigInteger(1, bArray).toString(16);
   }

   public void removeById(long l) {
      this.removeIf(gatewayUser2 -> gatewayUser2.getId() == l);
   }
}
