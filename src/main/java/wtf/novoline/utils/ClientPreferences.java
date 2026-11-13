package wtf.novoline.utils;

import com.google.gson.annotations.SerializedName;
import wtf.novoline.utils.language.Language.RuRuType;

public class ClientPreferences {
   @SerializedName("language")
   private RuRuType language;
   @SerializedName("shader_id")
   private int shader_id = 0;

   public void setLanguage(RuRuType ruRuType) {
      this.language = ruRuType;
   }

   public RuRuType getLanguage() {
      return this.language;
   }

   public void setShaderId(int i) {
      this.shader_id = i;
   }

   public int getShaderId() {
      return this.shader_id;
   }
}
