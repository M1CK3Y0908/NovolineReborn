package net.optifine.entity.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.ConnectedParser;
import net.optifine.entity.model.anim.ModelUpdater;
import net.optifine.entity.model.anim.ModelVariableUpdater;
import net.optifine.player.PlayerItemParser;
import net.optifine.util.Json;

public class CustomEntityModelParser {
   public static final String MODEL = "model";
   public static final String INVERT_AXIS = "invertAxis";
   public static final String TEXTURE = "texture";
   public static final String BASE_ID = "baseId";
   public static final String ATTACH = "attach";
   public static final String ENTITY_MODEL = "EntityModel";
   public static final String MODELS = "models";
   public static final String TEXTURE_SIZE = "textureSize";
   public static final String TYPE = "type";
   public static final String MIRROR_TEXTURE = "mirrorTexture";
   public static final String PART = "part";
   public static final String COORDINATES = "coordinates";
   public static final String SPRITES = "sprites";
   public static final String TEXTURE_OFFSET = "textureOffset";
   public static final String TRANSLATE = "translate";
   public static final String ROTATE = "rotate";
   public static final String ID = "id";
   public static final String SUBMODELS = "submodels";
   public static final String BOXES = "boxes";
   public static final String SHADOW_SIZE = "shadowSize";
   public static final String SCALE = "scale";
   public static final String USE_PLAYER_TEXTURE = "usePlayerTexture";
   public static final String SUBMODEL = "submodel";
   public static final String ENTITY = "entity";
   public static final String SIZE_ADD = "sizeAdd";
   public static final String TYPE2 = "type";
   public static final String ANIMATIONS = "animations";
   public static final String ENTITY_MODEL_PART = "EntityModelPart";

   public static CustomEntityRenderer parseEntityRender(JsonObject obj, String path) {
      ConnectedParser connectedParser = new ConnectedParser("CustomEntityModels");
      String text = connectedParser.parseName(path);
      String text2 = connectedParser.parseBasePath(path);
      String text3 = Json.getString(obj, "texture");
      int[] intArray = Json.parseIntArray(obj.get("textureSize"), 2);
      float floatValue = Json.getFloat(obj, "shadowSize", -1.0F);
      JsonArray jsonArray = (JsonArray)obj.get("models");
      checkNull(jsonArray, "Missing models");
      HashMap hashMap = new HashMap();
      ArrayList arrayList = new ArrayList();

      for (int index = 0; index < jsonArray.size(); index++) {
         JsonObject jsonObject = (JsonObject)jsonArray.get(index);
         processBaseId(jsonObject, hashMap);
         processExternalModel(jsonObject, hashMap, text2);
         processId(jsonObject, hashMap);
         CustomModelRenderer customModelRenderer = parseCustomModelRenderer(jsonObject, intArray, text2);
         if (customModelRenderer != null) {
            arrayList.add(customModelRenderer);
         }
      }

      CustomModelRenderer[] customModelRenderers = (CustomModelRenderer[]) arrayList.toArray(new CustomModelRenderer[arrayList.size()]);
      ResourceLocation resourceLocation = null;
      if (text3 != null) {
         resourceLocation = getResourceLocation(text2, text3, ".png");
      }

      return new CustomEntityRenderer(text, text2, resourceLocation, customModelRenderers, floatValue);
   }

   private static void copyJsonElements(JsonObject objFrom, JsonObject objTo) {
      for (Entry entry2 : objFrom.entrySet()) {
         if (!((String)entry2.getKey()).equals("id") && !objTo.has((String)entry2.getKey())) {
            objTo.add((String)entry2.getKey(), (JsonElement)entry2.getValue());
         }
      }
   }

   public static JsonObject loadJson(ResourceLocation location) throws IOException, JsonParseException {
      InputStream resourceStream = Config.getResourceStream(location);
      if (resourceStream == null) {
         return null;
      }

      String inputStream = Config.readInputStream(resourceStream, "ASCII");
      resourceStream.close();
      return (JsonObject)new JsonParser().parse(inputStream);
   }

   private static void processId(JsonObject elem, Map mapModelJsons) {
      String text = Json.getString(elem, "id");
      if (text != null) {
         if (text.length() < 1) {
            Config.warn("Empty model ID: " + text);
         } else if (mapModelJsons.containsKey(text)) {
            Config.warn("Duplicate model ID: " + text);
         } else {
            mapModelJsons.put(text, elem);
         }
      }
   }

   public static CustomModelRenderer parseCustomModelRenderer(JsonObject elem, int[] textureSize, String basePath) {
      String text = Json.getString(elem, "part");
      checkNull(text, "Model part not specified, missing \"replace\" or \"attachTo\".");
      boolean conditionMet = Json.getBoolean(elem, "attach", false);
      CustomEntityModel customEntityModel = new CustomEntityModel();
      if (textureSize != null) {
         customEntityModel.textureWidth = textureSize[0];
         customEntityModel.textureHeight = textureSize[1];
      }

      ModelUpdater modelUpdater = null;
      JsonArray jsonArray = (JsonArray)elem.get("animations");
      if (jsonArray != null) {
         ArrayList modelRenderer = new ArrayList();

         for (int index = 0; index < jsonArray.size(); index++) {
            for (Entry<String, JsonElement> entry : ((JsonObject)jsonArray.get(index)).entrySet()) {
               String text2 = entry.getKey();
               String text3 = entry.getValue().getAsString();
               ModelVariableUpdater modelVariableUpdater = new ModelVariableUpdater(text2, text3);
               modelRenderer.add(modelVariableUpdater);
            }
         }

         if (modelRenderer.size() > 0) {
            ModelVariableUpdater[] modelVariableUpdaters = (ModelVariableUpdater[]) modelRenderer.toArray(new ModelVariableUpdater[modelRenderer.size()]);
            modelUpdater = new ModelUpdater(modelVariableUpdaters);
         }
      }

      ModelRenderer modelRenderer = PlayerItemParser.parseModelRenderer(elem, customEntityModel, textureSize, basePath);
      return new CustomModelRenderer(text, conditionMet, modelRenderer, modelUpdater);
   }

   public static ResourceLocation getResourceLocation(String basePath, String path, String extension) {
      if (!path.endsWith(extension)) {
         path = path + extension;
      }

      if (!path.contains("/")) {
         path = basePath + "/" + path;
      } else if (path.startsWith("./")) {
         path = basePath + "/" + path.substring(2);
      } else if (path.startsWith("~/")) {
         path = "optifine/" + path.substring(2);
      }

      return new ResourceLocation(path);
   }

   private static void processBaseId(JsonObject elem, Map mapModelJsons) {
      String text = Json.getString(elem, "baseId");
      if (text != null) {
         JsonObject jsonObject = (JsonObject)mapModelJsons.get(text);
         if (jsonObject == null) {
            Config.warn("BaseID not found: " + text);
         } else {
            copyJsonElements(jsonObject, elem);
         }
      }
   }

   private static void checkNull(Object obj, String msg) {
      if (obj == null) {
         throw new JsonParseException(msg);
      }
   }

   private static void processExternalModel(JsonObject elem, Map mapModelJsons, String basePath) {
      String text = Json.getString(elem, "model");
      if (text != null) {
         ResourceLocation resourceLocation = getResourceLocation(basePath, text, ".jpm");

         try {
            JsonObject jsonObject = loadJson(resourceLocation);
            if (jsonObject == null) {
               Config.warn("Model not found: " + resourceLocation);
               return;
            }

            copyJsonElements(jsonObject, elem);
         } catch (IOException ioException) {
            Config.error(ioException.getClass().getName() + ": " + ioException.getMessage());
         } catch (JsonParseException jsonParseException) {
            Config.error(jsonParseException.getClass().getName() + ": " + jsonParseException.getMessage());
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }
}
