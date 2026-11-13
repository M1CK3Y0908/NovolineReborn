package wtf.novoline.utils.config;

import com.google.gson.JsonObject;
import wtf.novoline.Novoline;
import wtf.novoline.utils.ClientPreferences;

public class PreferencesSection extends ConfigSection {
   @Override
   public JsonObject serialize() {
      return gson.toJsonTree(Novoline.getNovoline().getClientPreferences()).getAsJsonObject();
   }

   @Override
   public void deserialize(JsonObject jsonObject) {
      ClientPreferences clientPreferences = gson.fromJson(jsonObject, ClientPreferences.class);
      Novoline.getNovoline().setClientPreferences(clientPreferences);
   }

   public PreferencesSection() {
      super("prefs");
   }
}
