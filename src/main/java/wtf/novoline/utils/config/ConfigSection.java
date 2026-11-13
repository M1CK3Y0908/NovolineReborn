package wtf.novoline.utils.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public abstract class ConfigSection {
   private final String name;
   protected static final Gson gson = new GsonBuilder().create();

   public String getName() {
      return this.name;
   }

   public ConfigSection(String text) {
      this.name = text;
   }

   public abstract JsonObject serialize();

   public abstract void deserialize(JsonObject jsonObject);
}
