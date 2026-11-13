package wtf.novoline.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Directories;
import wtf.novoline.enums.RelationType;

public class RelationManager extends LinkedHashMap<String, RelationType> {
   public static final Gson gson = new Gson();
   public static final Type type = new RelationManager.TypeTokenChild().getType();
   public static final Path path = Directories.STORAGE_DIR.resolve("relations.novo");

   public void save() throws IOException {
      String text = gson.toJson(this, type);
      Files.writeString(path, text);
   }

   public void load() throws IOException {
      String string = Files.readString(path);
      this.putAll(gson.fromJson(string, type));
   }

   public RelationManager() {
      try {
         if (Files.exists(path)) {
            this.load();
         }
      } catch (Throwable throwable) {
         Novoline.taggedLogger.error(throwable, "Relations file is broken, deleting it");

         try {
            Files.deleteIfExists(path);
         } catch (IOException ioException) {
         }
      }
   }

   static class TypeTokenChild extends TypeToken<Map<String, RelationType>> {
   }
}
