package wtf.novoline.utils.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thealtening.api.switcher.service.AlteningServiceType;
import net.minecraft.util.Session;
import wtf.novoline.enums.AccountType;
import wtf.novoline.lang.AuthException;
import wtf.novoline.utils.auth.YggdrasilSessionFactory;

public class AlteningAccount extends Account {
   private final YggdrasilSessionFactory yggdrasilSessionFactory = new YggdrasilSessionFactory();
   private transient int cachedHashCode;
   @SerializedName("code")
   @Expose
   private final String code;

   @Override
   protected boolean canEqual(Object object) {
      return object instanceof AlteningAccount;
   }

   @Override
   public String toString() {
      return this.code;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof AlteningAccount alteningAccount)) {
         return false;
      } else if (!alteningAccount.canEqual(this)) {
         return false;
      } else if (!super.equals(object)) {
         return false;
      } else {
         YggdrasilSessionFactory yggdrasilSessionFactory2 = this.yggdrasilSessionFactory;
         YggdrasilSessionFactory yggdrasilSessionFactory3 = alteningAccount.yggdrasilSessionFactory;
         if (yggdrasilSessionFactory2 == null ? yggdrasilSessionFactory3 == null : yggdrasilSessionFactory2.equals(yggdrasilSessionFactory3)) {
            String code2 = this.code;
            String code3 = alteningAccount.code;
            return code2 == null ? code3 == null : code2.equals(code3);
         } else {
            return false;
         }
      }
   }

   public AlteningAccount(String text) {
      super(AccountType.ALTENING);
      this.code = text;
   }

   @Override
   public Session login() throws AuthException {
      theAlteningAuthentication.updateService(AlteningServiceType.THEALTENING);
      return this.yggdrasilSessionFactory.authenticate(this.code, "1");
   }

   @Override
   public int hashCode() {
      if (this.cachedHashCode != 0) {
         return this.cachedHashCode;
      }

      int i = super.hashCode();
      YggdrasilSessionFactory yggdrasilSessionFactory2 = this.yggdrasilSessionFactory;
      i = i * 59 + (yggdrasilSessionFactory2 == null ? 43 : yggdrasilSessionFactory2.hashCode());
      String code2 = this.code;
      i = i * 59 + (code2 == null ? 43 : code2.hashCode());
      if (i == 0) {
         i = Integer.MIN_VALUE;
      }

      this.cachedHashCode = i;
      return i;
   }
}
