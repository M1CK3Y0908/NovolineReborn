package wtf.novoline.utils.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Directories;

public class ConfigManager {
   public static final String NOVO = ".novo";
   private final ConfigRegistry configRegistry;
   private static final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

   public ConfigManager(ConfigRegistry configRegistry2) {
      this.configRegistry = configRegistry2;
   }

   public List readLocalInfo(Path path) {
      try {
         JsonObject asJsonObject = JsonParser.parseString(Files.readString(path)).getAsJsonObject().getAsJsonObject("local-info");
         if (asJsonObject != null) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(asJsonObject.get("creator").getAsString());
            arrayList.add(asJsonObject.get("short-description").getAsString());
            arrayList.add(asJsonObject.get("created-time").getAsString());
            return arrayList;
         } else {
            return null;
         }
      } catch (Exception exception) {
         exception.printStackTrace();
         return null;
      }
   }

   public String readCloudResourceId(Path path) {
      try {
         JsonObject asJsonObject = JsonParser.parseString(Files.readString(path)).getAsJsonObject().getAsJsonObject("cloud-info");
         return asJsonObject != null ? asJsonObject.get("resourceId").getAsString() : null;
      } catch (Exception exception) {
         exception.printStackTrace();
         return null;
      }
   }

   public void writeLocalInfo(Path path, String text, String text2, String text3) {
      try {
         JsonObject asJsonObject = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("creator", text);
         jsonObject.addProperty("short-description", text2);
         jsonObject.addProperty("created-time", text3);
         asJsonObject.add("local-info", jsonObject);

         try {
            Files.writeString(path, gson.toJson(asJsonObject));
         } catch (IOException ioException) {
            ioException.printStackTrace();
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public void saveConfig(String text) {
      this.save(false, text, this.configRegistry.getSections().toArray(new ConfigSection[0]));
   }

   public void writeCloudInfo(Path path, String text) {
      try {
         JsonObject asJsonObject = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("resourceId", text);
         asJsonObject.add("cloud-info", jsonObject);

         try {
            Files.writeString(path, gson.toJson(asJsonObject));
         } catch (IOException ioException) {
            ioException.printStackTrace();
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public void load(boolean flag, String text, ConfigSection... configSections) {
      Path path = (flag ? Directories.STORAGE_DIR : Directories.CONFIGS_DIR).resolve(text + ".novo");

      try {
         JsonObject jsonObject = gson.fromJson(Files.readString(path), JsonObject.class);

         for (ConfigSection configSection : configSections) {
            JsonObject asJsonObject = jsonObject.getAsJsonObject(configSection.getName());
            if (asJsonObject != null) {
               configSection.deserialize(asJsonObject);
            }
         }
      } catch (Throwable throwable) {
         Novoline.taggedLogger.error(throwable, "Config not found or broken!");
      }
   }

   public void loadGlobal() {
      this.load(
         true,
         "global",
         ConfigRegistry.featuresWithHotkeys,
         ConfigRegistry.macrosSection,
         ConfigRegistry.overlaySection,
         ConfigRegistry.xRaySection,
         ConfigRegistry.preferencesSection
      );
   }

   public void saveGlobal() {
      this.save(
         true,
         "global",
         ConfigRegistry.featuresWithHotkeys,
         ConfigRegistry.macrosSection,
         ConfigRegistry.overlaySection,
         ConfigRegistry.xRaySection,
         ConfigRegistry.preferencesSection
      );
   }

   public void loadConfig(String text) {
      this.load(false, text, this.configRegistry.getSections().toArray(new ConfigSection[0]));
   }

   public void save(boolean flag, String text, ConfigSection... configSections) {
      Path path = (flag ? Directories.STORAGE_DIR : Directories.CONFIGS_DIR).resolve(text + ".novo");
      JsonObject jsonObject = new JsonObject();

      for (ConfigSection configSection : configSections) {
         JsonObject jsonObject2 = configSection.serialize();
         if (jsonObject2 != null) {
            jsonObject.add(configSection.getName(), jsonObject2);
         }
      }

      try {
         Files.writeString(path, gson.toJson(jsonObject));
      } catch (IOException ioException) {
         Novoline.taggedLogger.error(ioException, "Can't save config file!");
      }
   }
}
