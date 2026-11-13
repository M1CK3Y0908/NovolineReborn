package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModelBlock {
   protected ModelBlock parent;
   public String name = "";
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<BlockPart> elements;
   private final boolean ambientOcclusion;
   private final boolean gui3d;
   protected final Map<String, String> textures;
   protected ResourceLocation parentLocation;
   static final Gson SERIALIZER = new GsonBuilder()
      .registerTypeAdapter(ModelBlock.class, new ModelBlock.Deserializer())
      .registerTypeAdapter(BlockPart.class, new BlockPart.Deserializer())
      .registerTypeAdapter(BlockPartFace.class, new BlockPartFace.Deserializer())
      .registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer())
      .registerTypeAdapter(ItemTransformVec3f.class, new ItemTransformVec3f.Deserializer())
      .registerTypeAdapter(ItemCameraTransforms.class, new ItemCameraTransforms.Deserializer())
      .create();
   private ItemCameraTransforms cameraTransforms;

   public ItemCameraTransforms getAllTransforms() {
      ItemTransformVec3f itemTransformVec3f = this.getTransform(ItemCameraTransforms.TransformType.THIRD_PERSON);
      ItemTransformVec3f itemTransformVec3f2 = this.getTransform(ItemCameraTransforms.TransformType.FIRST_PERSON);
      ItemTransformVec3f itemTransformVec3f3 = this.getTransform(ItemCameraTransforms.TransformType.HEAD);
      ItemTransformVec3f itemTransformVec3f4 = this.getTransform(ItemCameraTransforms.TransformType.GUI);
      ItemTransformVec3f itemTransformVec3f5 = this.getTransform(ItemCameraTransforms.TransformType.GROUND);
      ItemTransformVec3f itemTransformVec3f6 = this.getTransform(ItemCameraTransforms.TransformType.FIXED);
      return new ItemCameraTransforms(
         itemTransformVec3f, itemTransformVec3f2, itemTransformVec3f3, itemTransformVec3f4, itemTransformVec3f5, itemTransformVec3f6
      );
   }

   public boolean isGui3d() {
      return this.gui3d;
   }

   public List<BlockPart> getElements() {
      return this.hasParent() ? this.parent.getElements() : this.elements;
   }

   public ModelBlock getRootModel() {
      return this.hasParent() ? this.parent.getRootModel() : this;
   }

   private ModelBlock(
      ResourceLocation parentLocationIn,
      List<BlockPart> elementsIn,
      Map<String, String> texturesIn,
      boolean ambientOcclusionIn,
      boolean gui3dIn,
      ItemCameraTransforms cameraTransformsIn
   ) {
      this.elements = elementsIn;
      this.ambientOcclusion = ambientOcclusionIn;
      this.gui3d = gui3dIn;
      this.textures = texturesIn;
      this.parentLocation = parentLocationIn;
      this.cameraTransforms = cameraTransformsIn;
   }

   public static ModelBlock deserialize(String jsonString) {
      return deserialize(new StringReader(jsonString));
   }

   protected ModelBlock(
      ResourceLocation parentLocationIn, Map<String, String> texturesIn, boolean ambientOcclusionIn, boolean gui3dIn, ItemCameraTransforms cameraTransformsIn
   ) {
      this(parentLocationIn, Collections.emptyList(), texturesIn, ambientOcclusionIn, gui3dIn, cameraTransformsIn);
   }

   private ItemTransformVec3f getTransform(ItemCameraTransforms.TransformType type) {
      return this.parent != null && !this.cameraTransforms.hasTransform(type) ? this.parent.getTransform(type) : this.cameraTransforms.getTransform(type);
   }

   public boolean isAmbientOcclusion() {
      return this.hasParent() ? this.parent.isAmbientOcclusion() : this.ambientOcclusion;
   }

   public void getParentFromMap(Map<ResourceLocation, ModelBlock> map2) {
      if (this.parentLocation != null) {
         this.parent = map2.get(this.parentLocation);
      }
   }

   private boolean hasParent() {
      return this.parent != null;
   }

   private boolean startsWithHash(String hash) {
      return hash.charAt(0) == '#';
   }

   public static ModelBlock deserialize(Reader readerIn) {
      return SERIALIZER.fromJson(readerIn, ModelBlock.class);
   }

   private static ModelBlock.LoopException createLoopException(ModelBlock.LoopException loopException) {
      return loopException;
   }

   protected ModelBlock(
      List<BlockPart> elementsIn, Map<String, String> texturesIn, boolean ambientOcclusionIn, boolean gui3dIn, ItemCameraTransforms cameraTransformsIn
   ) {
      this((ResourceLocation)null, elementsIn, texturesIn, ambientOcclusionIn, gui3dIn, cameraTransformsIn);
   }

   public boolean isTexturePresent(String textureName) {
      return !"missingno".equals(this.resolveTextureName(textureName));
   }

   private String resolveTextureName(String textureName, ModelBlock.Bookkeep bookkeep) {
      if (this.startsWithHash(textureName)) {
         if (this == bookkeep.modelExt) {
            LOGGER.warn("Unable to resolve texture due to upward reference: " + textureName + " in " + this.name);
            return "missingno";
         }

         String text = this.textures.get(textureName.substring(1));
         if (text == null && this.hasParent()) {
            text = this.parent.resolveTextureName(textureName, bookkeep);
         }

         bookkeep.modelExt = this;
         if (text != null && this.startsWithHash(text)) {
            text = bookkeep.model.resolveTextureName(text, bookkeep);
         }

         return text != null && !this.startsWithHash(text) ? text : "missingno";
      } else {
         return textureName;
      }
   }

   public String resolveTextureName(String textureName) {
      if (!this.startsWithHash(textureName)) {
         textureName = "#" + textureName;
      }

      return this.resolveTextureName(textureName, new ModelBlock.Bookkeep(this));
   }

   public boolean isResolved() {
      return this.parentLocation == null || this.parent != null && this.parent.isResolved();
   }

   public static void checkModelHierarchy(Map<ResourceLocation, ModelBlock> map2) {
      for (ModelBlock modelBlock2 : map2.values()) {
         try {
            ModelBlock modelBlock3 = modelBlock2.parent;

            for (ModelBlock parent2 = modelBlock3.parent; modelBlock3 != parent2; parent2 = parent2.parent.parent) {
               modelBlock3 = modelBlock3.parent;
            }

            throw new ModelBlock.LoopException();
         } catch (NullPointerException nullPointerException) {
         }
      }
   }

   public ResourceLocation getParentLocation() {
      return this.parentLocation;
   }

   static final class Bookkeep {
      public ModelBlock modelExt;
      public final ModelBlock model;

      private Bookkeep(ModelBlock modelBlock) {
         this.model = modelBlock;
      }
   }

   public static class Deserializer implements JsonDeserializer<ModelBlock> {
      protected List<BlockPart> getModelElements(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject3) {
         ArrayList arrayList = Lists.newArrayList();
         if (jsonObject3.has("elements")) {
            for (JsonElement jsonElement : JsonUtils.getJsonArray(jsonObject3, "elements")) {
               arrayList.add((BlockPart)jsonDeserializationContext.deserialize(jsonElement, BlockPart.class));
            }
         }

         return arrayList;
      }

      private Map<String, String> getTextures(JsonObject jsonObject3) {
         Map<String, String> map = Maps.newHashMap();
         if (jsonObject3.has("textures")) {
            for (Entry<String, JsonElement> entry : jsonObject3.getAsJsonObject("textures").entrySet()) {
               map.put(entry.getKey(), entry.getValue().getAsString());
            }
         }

         return map;
      }

      protected boolean getAmbientOcclusionEnabled(JsonObject jsonObject3) {
         return JsonUtils.getBoolean(jsonObject3, "ambientocclusion", true);
      }

      public ModelBlock deserialize(JsonElement arg1, Type arg2, JsonDeserializationContext arg3) throws JsonParseException {
         JsonObject jsonObject = arg1.getAsJsonObject();
         List<BlockPart> list = this.getModelElements(arg3, jsonObject);
         String text = this.getParent(jsonObject);
         boolean isEmpty = StringUtils.isEmpty(text);
         boolean isEmpty2 = list.isEmpty();
         if (isEmpty2 && isEmpty) {
            throw new JsonParseException("BlockModel requires either elements or parent, found neither");
         }

         if (!isEmpty && !isEmpty2) {
            throw new JsonParseException("BlockModel requires either elements or parent, found both");
         }

         Map<String, String> map = this.getTextures(jsonObject);
         boolean conditionMet = this.getAmbientOcclusionEnabled(jsonObject);
         ItemCameraTransforms itemCameraTransforms = ItemCameraTransforms.DEFAULT;
         if (jsonObject.has("display")) {
            JsonObject jsonObject2 = JsonUtils.getJsonObject(jsonObject, "display");
            itemCameraTransforms = arg3.deserialize(jsonObject2, ItemCameraTransforms.class);
         }

         return isEmpty2
            ? new ModelBlock(new ResourceLocation(text), map, conditionMet, true, itemCameraTransforms)
            : new ModelBlock(list, map, conditionMet, true, itemCameraTransforms);
      }

      private String getParent(JsonObject jsonObject3) {
         return JsonUtils.getString(jsonObject3, "parent", "");
      }
   }

   public static class LoopException extends RuntimeException {
   }
}
