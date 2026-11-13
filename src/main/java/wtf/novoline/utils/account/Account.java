package wtf.novoline.utils.account;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thealtening.api.switcher.TheAlteningAuthentication;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.util.Session;
import org.apache.commons.lang3.RandomStringUtils;
import wtf.novoline.enums.AccountType;
import wtf.novoline.lang.AuthException;
import wtf.novoline.utils.hypixel.HypixelProfile;
import wtf.novoline.support.SneakyThrow;

public abstract class Account {
   @SerializedName("id")
   @Expose
   private String accountId = RandomStringUtils.randomAlphanumeric(32);
   private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
   private transient int cachedHashCode;
   @SerializedName("metadata")
   @Expose
   private HypixelProfile metadata;
   @SerializedName("type")
   @Expose
   private final AccountType type;
   protected static final TheAlteningAuthentication theAlteningAuthentication = TheAlteningAuthentication.mojang();

   public abstract Session login() throws AuthException;

   public String getAccountId() {
      return this.accountId;
   }

   public synchronized HypixelProfile getHypixelProfile() {
      if (this.metadata == null) {
         this.metadata = new HypixelProfile();
      }

      return this.metadata;
   }

   protected Account(AccountType accountType) {
      this.type = accountType;
   }

   @Override
   public String toString() {
      return "Account(accountId=" + this.getAccountId() + ", type=" + this.getType() + ", metadata=" + this.getHypixelProfile() + ")";
   }

   public void setMetadata(HypixelProfile hypixelProfile) {
      this.metadata = hypixelProfile;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof Account account2)) {
         return false;
      } else {
         if (!account2.canEqual(this)) {
            return false;
         }

         String accountId2 = this.getAccountId();
         String accountId3 = account2.getAccountId();
         return accountId2 == null ? accountId3 == null : accountId2.equals(accountId3);
      }
   }

   private Session getSession() {
      try {
      return this.login();
   } catch (Throwable sneakyThrowable) {
         throw SneakyThrow.sneak(sneakyThrowable);
      }
   }

   public CompletableFuture<Session> getCompletableFuture() {
      return CompletableFuture.supplyAsync(this::getSession, executorService);
   }

   @Override
   public int hashCode() {
      if (this.cachedHashCode != 0) {
         return this.cachedHashCode;
      }

      String accountId2 = this.getAccountId();
      int i = 59 + (accountId2 == null ? 43 : accountId2.hashCode());
      if (i == 0) {
         i = Integer.MIN_VALUE;
      }

      this.cachedHashCode = i;
      return i;
   }

   protected boolean canEqual(Object object) {
      return object instanceof Account;
   }

   public void setAccountId(String text) {
      this.accountId = text;
   }

   public AccountType getType() {
      return this.type;
   }

   public static final class JsonDeserializerImpl implements JsonDeserializer<Account> {
      public Account deserialize(JsonElement jsonElement, Type type2, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject asJsonObject = jsonElement.getAsJsonObject();
         AccountType accountType = AccountType.valueOf(asJsonObject.get("type").getAsString());

         Account account2 = switch (accountType) {
            case ALTENING -> new AlteningAccount(asJsonObject.get("code").getAsString());
            case MOJANG -> new MojangAccount(asJsonObject.get("username").getAsString(), asJsonObject.get("password").getAsString());
            case CRACKED -> new CrackedAccount(asJsonObject.get("username").getAsString());
            case MICROSOFT -> new MicrosoftAccount(
               asJsonObject.get("access_token").getAsString(), asJsonObject.get("refresh_token").getAsString(), Instant.ofEpochMilli(asJsonObject.get("valid_due").getAsLong())
            );
         };
         if (asJsonObject.has("id")) {
            account2.setAccountId(asJsonObject.get("id").getAsString());
         }

         if (asJsonObject.has("metadata")) {
            HypixelProfile hypixelProfile = AccountManager.gson.fromJson(asJsonObject.get("metadata"), HypixelProfile.class);
            account2.setMetadata(hypixelProfile);
         }

         return account2;
      }
   }
}
