package wtf.novoline.utils.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import net.minecraft.util.Session;
import wtf.novoline.enums.AccountType;
import wtf.novoline.lang.AccessRevokedException;
import wtf.novoline.lang.AuthException;
import wtf.novoline.util.ExchangeCodeResponse;
import wtf.novoline.util.MCProfileResponse;
import wtf.novoline.util.XBLAuthenticationResponse;
import wtf.novoline.utils.language.Language;

public class MicrosoftAccount extends Account {
   private final MicrosoftAuthenticator microsoftAuthenticator = new MicrosoftAuthenticator();
   private transient int cachedHashCode;
   @SerializedName("access_token")
   @Expose
   private String access_token;
   @SerializedName("refresh_token")
   @Expose
   private String refresh_token;
   @SerializedName("valid_due")
   @Expose
   private Instant valid_due;

   public void closeAuthenticator() {
      this.microsoftAuthenticator.shutdown();
   }

   public MicrosoftAccount() {
      this(null, null, null);
   }

   public MicrosoftAccount(String text, String text2, Instant instant) {
      super(AccountType.MICROSOFT);
      this.access_token = text;
      this.refresh_token = text2;
      this.valid_due = instant;
   }

   @Override
   public String toString() {
      return Language.translate("account_manager.gui.entry.microsoft");
   }

   public boolean hasTokens() {
      return this.access_token != null && this.refresh_token != null && this.valid_due != null;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof MicrosoftAccount microsoftAccount)) {
         return false;
      } else {
         if (!microsoftAccount.canEqual(this)) {
            return false;
         }

         if (!super.equals(object)) {
            return false;
         }

         MicrosoftAuthenticator microsoftAuthenticator2 = this.microsoftAuthenticator;
         MicrosoftAuthenticator microsoftAuthenticator3 = microsoftAccount.microsoftAuthenticator;
         if (microsoftAuthenticator2 == null ? microsoftAuthenticator3 == null : microsoftAuthenticator2.equals(microsoftAuthenticator3)) {
            String text = this.access_token;
            String text2 = microsoftAccount.access_token;
            if (text == null ? text2 == null : text.equals(text2)) {
               String text3 = this.refresh_token;
               String text4 = microsoftAccount.refresh_token;
               if (text3 == null ? text4 == null : text3.equals(text4)) {
                  Instant instant = this.valid_due;
                  Instant instant2 = microsoftAccount.valid_due;
                  return instant == null ? instant2 == null : instant.equals(instant2);
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

   private void storeTokens(ExchangeCodeResponse exchangeCodeResponse) {
      this.access_token = exchangeCodeResponse.getAccessToken();
      this.refresh_token = exchangeCodeResponse.getRefreshToken();
      this.valid_due = Instant.now().plusSeconds(exchangeCodeResponse.getExpiresIn());
   }

   @Override
   public int hashCode() {
      if (this.cachedHashCode != 0) {
         return this.cachedHashCode;
      }

      int i = super.hashCode();
      MicrosoftAuthenticator microsoftAuthenticator2 = this.microsoftAuthenticator;
      i = i * 59 + (microsoftAuthenticator2 == null ? 43 : microsoftAuthenticator2.hashCode());
      String text = this.access_token;
      i = i * 59 + (text == null ? 43 : text.hashCode());
      String text2 = this.refresh_token;
      i = i * 59 + (text2 == null ? 43 : text2.hashCode());
      Instant instant = this.valid_due;
      i = i * 59 + (instant == null ? 43 : instant.hashCode());
      if (i == 0) {
         i = Integer.MIN_VALUE;
      }

      this.cachedHashCode = i;
      return i;
   }

   @Override
   protected boolean canEqual(Object object) {
      return object instanceof MicrosoftAccount;
   }

   @Override
   public Session login() throws AuthException {
      if (!this.hasTokens()) {
         throw new AuthException("Attempted to authenticate an account which isn't set up");
      }

      if (Instant.now().isAfter(this.valid_due)) {
         try {
            ExchangeCodeResponse exchangeCodeResponse = this.microsoftAuthenticator.getExchangeCodeResponse(this.refresh_token);
            this.storeTokens(exchangeCodeResponse);
         } catch (IOException | InterruptedException exception) {
            throw new AccessRevokedException();
         }
      }

      try {
         XBLAuthenticationResponse xblAuthenticationResponse = this.microsoftAuthenticator.authenticateXboxLive(this.access_token);
         XBLAuthenticationResponse xblAuthenticationResponse2 = this.microsoftAuthenticator.getXBLAuthenticationResponse(xblAuthenticationResponse.getToken());
         MicrosoftOAuthToken microsoftOAuthToken = this.microsoftAuthenticator.getMicrosoftOAuthToken(xblAuthenticationResponse2.getDisplayClaims().getXui().get(0).getUhs(), xblAuthenticationResponse2.getToken());
         MCProfileResponse mcProfileResponse = this.microsoftAuthenticator.getMCProfileResponse(microsoftOAuthToken.getTokenType(), microsoftOAuthToken.getAccessToken());
         return new Session(mcProfileResponse.getName(), mcProfileResponse.getId(), microsoftOAuthToken.getAccessToken(), "msa");
      } catch (IOException | InterruptedException exception2) {
         throw new AuthException(exception2);
      }
   }

   public CompletableFuture<Void> authenticateWithMicrosoft() {
      return CompletableFuture.runAsync(() -> {
         try {
            String text = (String)this.microsoftAuthenticator.getCompletableFuture().get();
            ExchangeCodeResponse exchangeCodeResponse = this.microsoftAuthenticator.exchangeCode(text);
            this.storeTokens(exchangeCodeResponse);
         } catch (IOException | InterruptedException | ExecutionException exception) {
            throw new RuntimeException(exception);
         }
      });
   }
}
