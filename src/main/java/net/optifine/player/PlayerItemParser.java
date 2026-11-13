package net.optifine.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import net.optifine.entity.model.CustomEntityModelParser;
import net.optifine.util.Json;

public class PlayerItemParser {
   public static final String PLAYER_ITEM = "PlayerItem";
   public static final String INVERT_AXIS = "invertAxis";
   public static final String ROTATE = "rotate";
   public static final String COORDINATES = "coordinates";
   public static final String UV_UP = "uvUp";
   public static final String UV_DOWN = "uvDown";
   public static final String TEXTURE_OFFSET = "textureOffset";
   public static final String SIZE_ADD = "sizeAdd";
   public static final String TEXTURE_SIZE = "textureSize";
   public static final String SUBMODELS = "submodels";
   public static final String TEXTURE = "texture";
   public static final String UV_FRONT = "uvFront";
   public static final String SPRITES = "sprites";
   public static final String TYPE = "type";
   public static final String MODEL_BOX = "ModelBox";
   public static final String USE_PLAYER_TEXTURE = "usePlayerTexture";
   public static final String SCALE = "scale";
   public static final String UV_NORTH = "uvNorth";
   public static final String ATTACH_TO = "attachTo";
   public static final String UV_LEFT = "uvLeft";
   public static final String TRANSLATE = "translate";
   public static final String BOXES = "boxes";
   public static final String TEXTURE_SIZE2 = "textureSize";
   public static final String MIRROR_TEXTURE = "mirrorTexture";
   public static final String BASE_ID = "baseId";
   public static final String UV_EAST = "uvEast";
   public static final String TYPE2 = "type";
   public static final String SUBMODEL = "submodel";
   public static final String UV_BACK = "uvBack";
   public static final String ID = "id";
   public static final String UV_RIGHT = "uvRight";
   public static final String UV_SOUTH = "uvSouth";
   public static final String MODELS = "models";
   public static final String UV_WEST = "uvWest";
   private static JsonParser jsonParser = new JsonParser();

   public static PlayerItemRenderer parseItemRenderer(JsonObject elem, Dimension textureDim) {
      String string = Json.getString(elem, "type");
      if (!Config.equals(string, "ModelBox")) {
         Config.warn("Unknown model type: " + string);
         return null;
      } else {
         int i = parseAttachModel(Json.getString(elem, "attachTo"));
         ModelPlayerItem modelPlayerItem = new ModelPlayerItem();
         modelPlayerItem.textureWidth = textureDim.width;
         modelPlayerItem.textureHeight = textureDim.height;
         ModelRenderer modelRenderer = parseModelRenderer(elem, modelPlayerItem, (int[])null, (String)null);
         return new PlayerItemRenderer(i, modelRenderer);
      }
   }

   private static int[][] parseFaceUvs(JsonObject box) {
      int[][] iArrays = new int[][]{
         Json.parseIntArray(box.get("uvDown"), 4),
         Json.parseIntArray(box.get("uvUp"), 4),
         Json.parseIntArray(box.get("uvNorth"), 4),
         Json.parseIntArray(box.get("uvSouth"), 4),
         Json.parseIntArray(box.get("uvWest"), 4),
         Json.parseIntArray(box.get("uvEast"), 4)
      };
      if (iArrays[2] == null) {
         iArrays[2] = Json.parseIntArray(box.get("uvFront"), 4);
      }

      if (iArrays[3] == null) {
         iArrays[3] = Json.parseIntArray(box.get("uvBack"), 4);
      }

      if (iArrays[4] == null) {
         iArrays[4] = Json.parseIntArray(box.get("uvLeft"), 4);
      }

      if (iArrays[5] == null) {
         iArrays[5] = Json.parseIntArray(box.get("uvRight"), 4);
      }

      boolean conditionMet = false;

      for (int index = 0; index < iArrays.length; index++) {
         if (iArrays[index] != null) {
            conditionMet = true;
         }
      }

      return !conditionMet ? (int[][])null : iArrays;
   }

   public static PlayerItemModel parseItemModel(JsonObject obj) {
      String string = Json.getString(obj, "type");
      if (!Config.equals(string, "PlayerItem")) {
         throw new JsonParseException("Unknown model type: " + string);
      }

      int[] iArray = Json.parseIntArray(obj.get("textureSize"), 2);
      checkNull(iArray, "Missing texture size");
      Dimension dimension = new Dimension(iArray[0], iArray[1]);
      boolean booleanValue = Json.getBoolean(obj, "usePlayerTexture", false);
      JsonArray jsonArray = (JsonArray)obj.get("models");
      checkNull(jsonArray, "Missing elements");
      HashMap hashMap = new HashMap();
      ArrayList arrayList = new ArrayList();
      new ArrayList();

      for (int i = 0; i < jsonArray.size(); i++) {
         JsonObject jsonObject = (JsonObject)jsonArray.get(i);
         String string2 = Json.getString(jsonObject, "baseId");
         if (string2 != null) {
            JsonObject jsonObject2 = (JsonObject)hashMap.get(string2);
            if (jsonObject2 == null) {
               Config.warn("BaseID not found: " + string2);
               continue;
            }

            for (Entry entry : jsonObject2.entrySet()) {
               if (!jsonObject.has((String)entry.getKey())) {
                  jsonObject.add((String)entry.getKey(), (JsonElement)entry.getValue());
               }
            }
         }

         String string3 = Json.getString(jsonObject, "id");
         if (string3 != null) {
            if (!hashMap.containsKey(string3)) {
               hashMap.put(string3, jsonObject);
            } else {
               Config.warn("Duplicate model ID: " + string3);
            }
         }

         PlayerItemRenderer playerItemRenderer = parseItemRenderer(jsonObject, dimension);
         if (playerItemRenderer != null) {
            arrayList.add(playerItemRenderer);
         }
      }

      PlayerItemRenderer[] playerItemRenderers = (PlayerItemRenderer[]) arrayList.toArray(new PlayerItemRenderer[arrayList.size()]);
      return new PlayerItemModel(dimension, booleanValue, playerItemRenderers);
   }

   public static ModelRenderer parseModelRenderer(JsonObject elem, ModelBase modelBase, int[] parentTextureSize, String basePath) {
      ModelRenderer modelRenderer = new ModelRenderer(modelBase);
      String string = Json.getString(elem, "id");
      modelRenderer.setId(string);
      float floatValue = Json.getFloat(elem, "scale", 1.0F);
      modelRenderer.scaleX = floatValue;
      modelRenderer.scaleY = floatValue;
      modelRenderer.scaleZ = floatValue;
      String string2 = Json.getString(elem, "texture");
      if (string2 != null) {
         modelRenderer.setTextureLocation(CustomEntityModelParser.getResourceLocation(basePath, string2, ".png"));
      }

      int[] iArray = Json.parseIntArray(elem.get("textureSize"), 2);
      if (iArray == null) {
         iArray = parentTextureSize;
      }

      if (iArray != null) {
         modelRenderer.setTextureSize(iArray[0], iArray[1]);
      }

      String text = Json.getString(elem, "invertAxis", "").toLowerCase();
      boolean flag = text.contains("x");
      boolean flag2 = text.contains("y");
      boolean flag3 = text.contains("z");
      float[] fArray = Json.parseFloatArray(elem.get("translate"), 3, new float[3]);
      if (flag) {
         fArray[0] = -fArray[0];
      }

      if (flag2) {
         fArray[1] = -fArray[1];
      }

      if (flag3) {
         fArray[2] = -fArray[2];
      }

      float[] fArray2 = Json.parseFloatArray(elem.get("rotate"), 3, new float[3]);

      for (int i = 0; i < fArray2.length; i++) {
         fArray2[i] = fArray2[i] / 180.0F * MathHelper.PI;
      }

      if (flag) {
         fArray2[0] = -fArray2[0];
      }

      if (flag2) {
         fArray2[1] = -fArray2[1];
      }

      if (flag3) {
         fArray2[2] = -fArray2[2];
      }

      modelRenderer.setRotationPoint(fArray[0], fArray[1], fArray[2]);
      modelRenderer.rotateAngleX = fArray2[0];
      modelRenderer.rotateAngleY = fArray2[1];
      modelRenderer.rotateAngleZ = fArray2[2];
      String text2 = Json.getString(elem, "mirrorTexture", "").toLowerCase();
      boolean flag4 = text2.contains("u");
      boolean flag5 = text2.contains("v");
      if (flag4) {
         modelRenderer.mirror = true;
      }

      if (flag5) {
         modelRenderer.mirrorV = true;
      }

      JsonArray asJsonArray = elem.getAsJsonArray("boxes");
      if (asJsonArray != null) {
         for (int i2 = 0; i2 < asJsonArray.size(); i2++) {
            JsonObject asJsonObject = asJsonArray.get(i2).getAsJsonObject();
            int[] iArray2 = Json.parseIntArray(asJsonObject.get("textureOffset"), 2);
            int[][] iArrays = parseFaceUvs(asJsonObject);
            if (iArray2 == null && iArrays == null) {
               throw new JsonParseException("Texture offset not specified");
            }

            float[] array = Json.createArray(asJsonObject.get("coordinates"));
            if (array == null) {
               throw new JsonParseException("Coordinates not specified");
            }

            if (flag) {
               array[0] = -array[0] - array[3];
            }

            if (flag2) {
               array[1] = -array[1] - array[4];
            }

            if (flag3) {
               array[2] = -array[2] - array[5];
            }

            float floatValue2 = Json.getFloat(asJsonObject, "sizeAdd", 0.0F);
            if (iArrays != null) {
               modelRenderer.addBox(iArrays, array[0], array[1], array[2], array[3], array[4], array[5], floatValue2);
            } else {
               modelRenderer.setTextureOffset(iArray2[0], iArray2[1]);
               modelRenderer.addBox(array[0], array[1], array[2], (int)array[3], (int)array[4], (int)array[5], floatValue2);
            }
         }
      }

      JsonArray asJsonArray2 = elem.getAsJsonArray("sprites");
      if (asJsonArray2 != null) {
         for (int i3 = 0; i3 < asJsonArray2.size(); i3++) {
            JsonObject asJsonObject2 = asJsonArray2.get(i3).getAsJsonObject();
            int[] iArray3 = Json.parseIntArray(asJsonObject2.get("textureOffset"), 2);
            if (iArray3 == null) {
               throw new JsonParseException("Texture offset not specified");
            }

            float[] array2 = Json.createArray(asJsonObject2.get("coordinates"));
            if (array2 == null) {
               throw new JsonParseException("Coordinates not specified");
            }

            if (flag) {
               array2[0] = -array2[0] - array2[3];
            }

            if (flag2) {
               array2[1] = -array2[1] - array2[4];
            }

            if (flag3) {
               array2[2] = -array2[2] - array2[5];
            }

            float floatValue3 = Json.getFloat(asJsonObject2, "sizeAdd", 0.0F);
            modelRenderer.setTextureOffset(iArray3[0], iArray3[1]);
            modelRenderer.addSprite(array2[0], array2[1], array2[2], (int)array2[3], (int)array2[4], (int)array2[5], floatValue3);
         }
      }

      JsonObject jsonObject = (JsonObject)elem.get("submodel");
      if (jsonObject != null) {
         ModelRenderer modelRenderer2 = parseModelRenderer(jsonObject, modelBase, iArray, basePath);
         modelRenderer.addChild(modelRenderer2);
      }

      JsonArray jsonArray = (JsonArray)elem.get("submodels");
      if (jsonArray != null) {
         for (int i4 = 0; i4 < jsonArray.size(); i4++) {
            JsonObject jsonObject2 = (JsonObject)jsonArray.get(i4);
            ModelRenderer modelRenderer3 = parseModelRenderer(jsonObject2, modelBase, iArray, basePath);
            if (modelRenderer3.getId() != null && modelRenderer.getChild(modelRenderer3.getId()) != null) {
               Config.warn("Duplicate model ID: " + modelRenderer3.getId());
            }

            modelRenderer.addChild(modelRenderer3);
         }
      }

      return modelRenderer;
   }

   private static void checkNull(Object obj, String msg) {
      if (obj == null) {
         throw new JsonParseException(msg);
      }
   }

   private static int parseAttachModel(String attachModelStr) {
      if (attachModelStr == null) {
         return 0;
      }

      if (attachModelStr.equals("body")) {
         return 0;
      }

      if (attachModelStr.equals("head")) {
         return 1;
      }

      if (attachModelStr.equals("leftArm")) {
         return 2;
      }

      if (attachModelStr.equals("rightArm")) {
         return 3;
      }

      if (attachModelStr.equals("leftLeg")) {
         return 4;
      }

      if (attachModelStr.equals("rightLeg")) {
         return 5;
      }

      if (attachModelStr.equals("cape")) {
         return 6;
      }

      Config.warn("Unknown attachModel: " + attachModelStr);
      return 0;
   }
}
