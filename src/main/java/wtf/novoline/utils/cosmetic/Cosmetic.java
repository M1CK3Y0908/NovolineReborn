package wtf.novoline.utils.cosmetic;

import com.google.gson.annotations.SerializedName;
import wtf.novoline.enums.CapeType;

public class Cosmetic {
   @SerializedName("type")
   private final CapeType type;
   @SerializedName("name")
   private final String name;

   public String getName() {
      return this.name;
   }

   protected boolean canEqual(Object object) {
      return object instanceof Cosmetic;
   }

   public CapeType getType() {
      return this.type;
   }

   @Override
   public String toString() {
      return "Cosmetic(type=" + this.getType() + ", name=" + this.getName() + ")";
   }

   public Cosmetic(CapeType capeType, String text) {
      this.type = capeType;
      this.name = text;
   }

   @Override
   public int hashCode() {
      CapeType type2 = this.getType();
      int i = 59 + (type2 == null ? 43 : type2.hashCode());
      String name2 = this.getName();
      return i * 59 + (name2 == null ? 43 : name2.hashCode());
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof Cosmetic cosmetic2)) {
         return false;
      } else if (!cosmetic2.canEqual(this)) {
         return false;
      } else {
         CapeType type2 = this.getType();
         CapeType type3 = cosmetic2.getType();
         if (type2 == null ? type3 == null : type2.equals(type3)) {
            String name2 = this.getName();
            String name3 = cosmetic2.getName();
            return name2 == null ? name3 == null : name2.equals(name3);
         } else {
            return false;
         }
      }
   }
}
