package wtf.novoline.utils.account;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Directories;
import wtf.novoline.enums.AccountType;
import wtf.novoline.gson.InstantMillisAdapter;
import wtf.novoline.utils.account.Account.JsonDeserializerImpl;

public class AccountManager extends ArrayList<Account> {
   public static final Path path = Directories.STORAGE_DIR.resolve("alts.novo");
   private String alteningToken;
   private int selectedIndex = -1;
   public static final Gson gson = new GsonBuilder()
      .registerTypeAdapter(Instant.class, new InstantMillisAdapter())
      .registerTypeAdapter(Account.class, new JsonDeserializerImpl())
      .excludeFieldsWithoutExposeAnnotation()
      .create();
   private Account account;

   public Account getAccount() {
      return this.account;
   }

   public List readAccounts() throws IOException {
      try (GZIPInputStream gzipInputStream = new GZIPInputStream(Files.newInputStream(path))) {
         String text = new String(gzipInputStream.readAllBytes(), StandardCharsets.UTF_8);
         JsonObject asJsonObject = JsonParser.parseString(text).getAsJsonObject();
         if (asJsonObject.has("last_alt")) {
            JsonObject asJsonObject2 = asJsonObject.get("last_alt").getAsJsonObject();
            this.account = gson.fromJson(asJsonObject2, Account.class);
         }

         if (asJsonObject.has("altening_token")) {
            this.alteningToken = asJsonObject.get("altening_token").getAsString();
         }

         if (asJsonObject.has("accounts")) {
            TypeToken typeToken = new TypeToken<ArrayList<Account>>() {};
            return gson.fromJson(asJsonObject.get("accounts"), typeToken.getType());
         } else {
            return Collections.emptyList();
         }
      }
   }

   public int getSelectedIndex() {
      return this.selectedIndex;
   }

   public void setIntValue(int i) {
      this.selectedIndex = i;
   }

   public void setAlteningToken(String text) {
      this.alteningToken = text;
   }

   public void setAccount(Account account2) {
      this.account = account2;
   }

   public void save() throws IOException {
      try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(Files.newOutputStream(path))) {
         JsonObject jsonObject = new JsonObject();
         JsonArray jsonArray = new JsonArray();

         for (Account account2 : this) {
            jsonArray.add(gson.toJsonTree(account2));
         }

         jsonObject.add("accounts", jsonArray);
         if (this.account != null) {
            jsonObject.add("last_alt", gson.toJsonTree(this.account));
         }

         if (this.alteningToken != null) {
            jsonObject.addProperty("altening_token", this.alteningToken);
         }

         String text = gson.toJson(jsonObject);
         gzipOutputStream.write(text.getBytes(StandardCharsets.UTF_8));
      }
   }

   public Account createAccount(String text, String text2, AccountType accountType) {
      return switch (accountType) {
         case MOJANG -> new MojangAccount(text, text2);
         case CRACKED -> new CrackedAccount(text);
         case ALTENING -> new AlteningAccount(text);
         default -> throw new IllegalArgumentException("Account type " + accountType + " isn't supported");
      };
   }

   public AccountManager() {
      if (Files.exists(path)) {
         try {
            this.addAll(this.readAccounts());
            Novoline.taggedLogger.debug("Loaded {} accounts", new Object[]{this.size()});
         } catch (Throwable throwable) {
            Novoline.taggedLogger.error(throwable, "Couldn't read accounts");
            Novoline.taggedLogger.error("Accounts file is broken. Backing it up and resetting");

            try {
               Files.move(path, Directories.STORAGE_DIR.resolve("alts.novo.broken"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ioException) {
               Novoline.taggedLogger.error(ioException, "Couldn't backup accounts file");
            }
         }
      }
   }

   public String getAlteningToken() {
      return this.alteningToken;
   }
}
