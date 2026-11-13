package wtf.novoline.utils.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import wtf.novoline.Novoline;
import wtf.novoline.utils.ChatMacro;
import wtf.novoline.utils.MacroManager;

public class MacrosSection extends ConfigSection {
   private final MacroManager macroManager = Novoline.getNovoline().getMacroManager();

   public MacrosSection() {
      super("macros");
   }

   @Override
   public JsonObject serialize() {
      JsonObject jsonObject = new JsonObject();

      for (ChatMacro chatMacro : this.macroManager.getMacros()) {
         JsonObject jsonObject2 = new JsonObject();
         jsonObject2.addProperty("hotkey", chatMacro.getKeyCode());
         jsonObject2.addProperty("message", chatMacro.getMessage());
         jsonObject.add(chatMacro.getName(), jsonObject2);
      }

      return jsonObject;
   }

   @Override
   public void deserialize(JsonObject jsonObject) {
      this.macroManager.getMacros().clear();
      if (jsonObject.size() > 0) {
         for (java.util.Map.Entry entry : jsonObject.entrySet()) {
            JsonObject asJsonObject = ((JsonElement)entry.getValue()).getAsJsonObject();
            this.macroManager.addMacro(new ChatMacro((String)entry.getKey(), asJsonObject.get("hotkey").getAsInt(), asJsonObject.get("msg").getAsString()));
         }
      }
   }
}
