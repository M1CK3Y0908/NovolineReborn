package wtf.novoline.utils.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thealtening.api.switcher.service.AlteningServiceType;
import net.minecraft.util.Session;
import wtf.novoline.enums.AccountType;
import wtf.novoline.lang.AuthException;
import wtf.novoline.utils.auth.YggdrasilSessionFactory;

public class MojangAccount extends Account {
   private transient int cachedHashCode;
   private final YggdrasilSessionFactory yggdrasilSessionFactory = new YggdrasilSessionFactory();
   @SerializedName("username")
   @Expose
   private final String username;
   @SerializedName("password")
   @Expose
   private final String password;

   @Override
   public String toString() {
      return this.username + ":" + this.password;
   }

   @Override
   public int hashCode() {
      if (this.cachedHashCode != 0) {
         return this.cachedHashCode;
      }

      int i = super.hashCode();
      YggdrasilSessionFactory yggdrasilSessionFactory2 = this.yggdrasilSessionFactory;
      i = i * 59 + (yggdrasilSessionFactory2 == null ? 43 : yggdrasilSessionFactory2.hashCode());
      String username2 = this.username;
      i = i * 59 + (username2 == null ? 43 : username2.hashCode());
      String password2 = this.password;
      i = i * 59 + (password2 == null ? 43 : password2.hashCode());
      if (i == 0) {
         i = Integer.MIN_VALUE;
      }

      this.cachedHashCode = i;
      return i;
   }

   public MojangAccount(String text, String text2) {
      super(AccountType.MOJANG);
      this.username = text;
      this.password = text2;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof MojangAccount mojangAccount)) {
         return false;
      } else {
         if (!mojangAccount.canEqual(this)) {
            return false;
         }

         if (!super.equals(object)) {
            return false;
         }

         YggdrasilSessionFactory yggdrasilSessionFactory2 = this.yggdrasilSessionFactory;
         YggdrasilSessionFactory yggdrasilSessionFactory3 = mojangAccount.yggdrasilSessionFactory;
         if (yggdrasilSessionFactory2 == null ? yggdrasilSessionFactory3 == null : yggdrasilSessionFactory2.equals(yggdrasilSessionFactory3)) {
            String username2 = this.username;
            String username3 = mojangAccount.username;
            if (username2 == null ? username3 == null : username2.equals(username3)) {
               String password2 = this.password;
               String password3 = mojangAccount.password;
               return password2 == null ? password3 == null : password2.equals(password3);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Override
   protected boolean canEqual(Object object) {
      return object instanceof MojangAccount;
   }

   @Override
   public Session login() throws AuthException {
      theAlteningAuthentication.updateService(AlteningServiceType.MOJANG);
      return this.yggdrasilSessionFactory.authenticate(this.username, this.password);
   }
}
