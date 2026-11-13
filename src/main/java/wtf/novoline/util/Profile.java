package wtf.novoline.util;

import com.google.gson.annotations.SerializedName;
import java.util.Collection;
import wtf.novoline.utils.cosmetic.Cosmetic;

public class Profile {
   @SerializedName("cosmetics")
   private Collection<Cosmetic> cosmetics;
   @SerializedName("userId")
   private long userId;

   @Override
   public int hashCode() {
      long userId2 = this.getUserId();
      int i = 59 + (int)(userId2 >>> 32 ^ userId2);
      Collection cosmetics2 = this.getCosmetics();
      return i * 59 + (cosmetics2 == null ? 43 : cosmetics2.hashCode());
   }

   public long getUserId() {
      return this.userId;
   }

   public void setCosmetics(Collection<Cosmetic> collection) {
      this.cosmetics = collection;
   }

   @Override
   public String toString() {
      Collection cosmetics2 = this.getCosmetics();
      long userId2 = this.getUserId();
      return "Profile(userId=" + userId2 + ", cosmetics=" + cosmetics2 + ")";
   }

   public Collection<Cosmetic> getCosmetics() {
      return this.cosmetics;
   }

   protected boolean canEqual(Object object) {
      return object instanceof Profile;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof Profile profile)) {
         return false;
      } else {
         if (!profile.canEqual(this)) {
            return false;
         }

         if (this.getUserId() != profile.getUserId()) {
            return false;
         }

         Collection cosmetics2 = this.getCosmetics();
         Collection cosmetics3 = profile.getCosmetics();
         return cosmetics2 == null ? cosmetics3 == null : cosmetics2.equals(cosmetics3);
      }
   }
}
