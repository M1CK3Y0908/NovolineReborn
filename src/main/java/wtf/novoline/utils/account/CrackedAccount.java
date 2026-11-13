package wtf.novoline.utils.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.UUID;
import net.minecraft.util.Session;
import wtf.novoline.enums.AccountType;
import wtf.novoline.lang.AuthException;
import wtf.novoline.utils.language.Language;

public class CrackedAccount extends Account {
   @SerializedName("username")
   @Expose
   private final String username;
   private static final UUID uUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
   private transient int cachedHashCode;

   @Override
   protected boolean canEqual(Object object) {
      return object instanceof CrackedAccount;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof CrackedAccount crackedAccount)) {
         return false;
      } else {
         if (!crackedAccount.canEqual(this)) {
            return false;
         }

         if (!super.equals(object)) {
            return false;
         }

         String username2 = this.username;
         String username3 = crackedAccount.username;
         return username2 == null ? username3 == null : username2.equals(username3);
      }
   }

   public CrackedAccount(String text) {
      super(AccountType.CRACKED);
      this.username = text;
   }

   @Override
   public Session login() throws AuthException {
      return new Session(this.username, uUID.toString(), null, "");
   }

   @Override
   public int hashCode() {
      if (this.cachedHashCode != 0) {
         return this.cachedHashCode;
      }

      int i = super.hashCode();
      String username2 = this.username;
      i = i * 59 + (username2 == null ? 43 : username2.hashCode());
      if (i == 0) {
         i = Integer.MIN_VALUE;
      }

      this.cachedHashCode = i;
      return i;
   }

   @Override
   public String toString() {
      return Language.translate("account_manager.gui.entry.cracked");
   }
}
