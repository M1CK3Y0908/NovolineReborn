package net.minecraft.client.resources;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourceIndex {
   private static final Logger logger = LogManager.getLogger();
   private final Map<String, File> resourceMap = Maps.newHashMap();

   public Map<String, File> getResourceMap() {
      return this.resourceMap;
   }

   public ResourceIndex(File file2, String text) {
      if (text != null) {
         File file3 = new File(file2, "objects");
         File file4 = new File(file2, "indexes/" + text + ".json");
         BufferedReader bufferedReader = null;

         try {
            bufferedReader = Files.newReader(file4, Charsets.UTF_8);
            JsonObject jsonObject = JsonUtils.getJsonObject(new JsonParser().parse(bufferedReader).getAsJsonObject(), "objects", (JsonObject)null);
            if (jsonObject != null) {
               for (Entry entry : jsonObject.entrySet()) {
                  JsonObject jsonObject2 = (JsonObject)entry.getValue();
                  String[] texts = ((String)entry.getKey()).split("/", 2);
                  String text2 = texts.length == 1 ? texts[0] : texts[0] + ":" + texts[1];
                  String string = JsonUtils.getString(jsonObject2, "hash");
                  File file5 = new File(file3, string.substring(0, 2) + "/" + string);
                  this.resourceMap.put(text2, file5);
               }
            }
         } catch (JsonParseException jsonParseException) {
            logger.error("Unable to parse resource index file: " + file4);
         } catch (FileNotFoundException fileNotFoundException) {
            logger.error("Can't find the resource index file: " + file4);
         } finally {
            IOUtils.closeQuietly(bufferedReader);
         }
      }
   }
}
