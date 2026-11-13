package wtf.novoline.utils.cosmetic;

import com.google.gson.annotations.SerializedName;
import wtf.novoline.enums.CapeType;

public class CapeCosmetic extends Cosmetic {
   public CapeCosmetic() {
      super((CapeType) null, (String) null);
   }

   @SerializedName("texHash")
   private String texHash;

   public String getTexHash() {
      return this.texHash;
   }
}
