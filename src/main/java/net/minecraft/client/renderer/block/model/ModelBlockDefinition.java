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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class ModelBlockDefinition {
   static final Gson GSON = new GsonBuilder()
      .registerTypeAdapter(ModelBlockDefinition.class, new ModelBlockDefinition.Deserializer())
      .registerTypeAdapter(ModelBlockDefinition.Variant.class, new ModelBlockDefinition.Variant.Deserializer())
      .create();
   private final Map<String, ModelBlockDefinition.Variants> mapVariants = Maps.newHashMap();

   public ModelBlockDefinition.Variants getVariants(String variantName) {
      ModelBlockDefinition.Variants modelblockdefinition$variants = this.mapVariants.get(variantName);
      if (modelblockdefinition$variants == null) {
         throw new ModelBlockDefinition.MissingVariantException();
      } else {
         return modelblockdefinition$variants;
      }
   }

   public ModelBlockDefinition(List<ModelBlockDefinition> definitions) {
      for (ModelBlockDefinition modelBlockDefinition2 : definitions) {
         this.mapVariants.putAll(modelBlockDefinition2.mapVariants);
      }
   }

   public ModelBlockDefinition(Collection<ModelBlockDefinition.Variants> variants) {
      for (ModelBlockDefinition.Variants variants2 : variants) {
         this.mapVariants.put(variants2.name, variants2);
      }
   }

   private static ModelBlockDefinition.MissingVariantException createMissingVariantException(ModelBlockDefinition.MissingVariantException missingVariantException) {
      return missingVariantException;
   }

   @Override
   public int hashCode() {
      return this.mapVariants.hashCode();
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else {
         return other instanceof ModelBlockDefinition modelBlockDefinition ? this.mapVariants.equals(modelBlockDefinition.mapVariants) : false;
      }
   }

   public static ModelBlockDefinition parseFromReader(Reader reader) {
      return GSON.fromJson(reader, ModelBlockDefinition.class);
   }

   public static class Deserializer implements JsonDeserializer<ModelBlockDefinition> {
      protected ModelBlockDefinition.Variants parseVariants(JsonDeserializationContext context, Entry<String, JsonElement> entry) {
         String key = entry.getKey();
         ArrayList arrayList = Lists.newArrayList();
         JsonElement value = entry.getValue();
         if (value.isJsonArray()) {
            for (JsonElement jsonElement : value.getAsJsonArray()) {
               arrayList.add((ModelBlockDefinition.Variant)context.deserialize(jsonElement, ModelBlockDefinition.Variant.class));
            }
         } else {
            arrayList.add((ModelBlockDefinition.Variant)context.deserialize(value, ModelBlockDefinition.Variant.class));
         }

         return new ModelBlockDefinition.Variants(key, arrayList);
      }

      protected List<ModelBlockDefinition.Variants> parseVariantsList(JsonDeserializationContext context, JsonObject json) {
         JsonObject jsonObject2 = JsonUtils.getJsonObject(json, "variants");
         ArrayList arrayList = Lists.newArrayList();

         for (Entry entry2 : jsonObject2.entrySet()) {
            arrayList.add(this.parseVariants(context, entry2));
         }

         return arrayList;
      }

      public ModelBlockDefinition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonObject = json.getAsJsonObject();
         List<ModelBlockDefinition.Variants> list = this.parseVariantsList(context, jsonObject);
         return new ModelBlockDefinition((Collection<ModelBlockDefinition.Variants>)list);
      }
   }

   public class MissingVariantException extends RuntimeException {
   }

   public static class Variant {
      private final ResourceLocation modelLocation;
      private final ModelRotation modelRotation;
      private final boolean uvLock;
      private final int weight;

      public boolean isUvLocked() {
         return this.uvLock;
      }

      @Override
      public int hashCode() {
         int i = this.modelLocation.hashCode();
         i = 31 * i + (this.modelRotation != null ? this.modelRotation.hashCode() : 0);
         return 31 * i + (this.uvLock ? 1 : 0);
      }

      public ResourceLocation getModelLocation() {
         return this.modelLocation;
      }

      public Variant(ResourceLocation modelLocationIn, ModelRotation modelRotationIn, boolean uvLockIn, int weightIn) {
         this.modelLocation = modelLocationIn;
         this.modelRotation = modelRotationIn;
         this.uvLock = uvLockIn;
         this.weight = weightIn;
      }

      @Override
      public boolean equals(Object other) {
         if (this == other) {
            return true;
         } else {
            return !(other instanceof ModelBlockDefinition.Variant modelblockdefinition$variant)
               ? false
               : this.modelLocation.equals(modelblockdefinition$variant.modelLocation)
                  && this.modelRotation == modelblockdefinition$variant.modelRotation
                  && this.uvLock == modelblockdefinition$variant.uvLock;
         }
      }

      public ModelRotation getRotation() {
         return this.modelRotation;
      }

      public int getWeight() {
         return this.weight;
      }

      public static class Deserializer implements JsonDeserializer<ModelBlockDefinition.Variant> {
         protected int parseWeight(JsonObject json) {
            return JsonUtils.getInt(json, "weight", 1);
         }

         private ResourceLocation makeModelLocation(String modelName) {
            ResourceLocation resourceLocation = new ResourceLocation(modelName);
            return new ResourceLocation(resourceLocation.getResourceDomain(), "block/" + resourceLocation.getResourcePath());
         }

         protected ModelRotation parseRotation(JsonObject json) {
            int intValue = JsonUtils.getInt(json, "x", 0);
            int intValue2 = JsonUtils.getInt(json, "y", 0);
            ModelRotation modelRotation = ModelRotation.getModelRotation(intValue, intValue2);
            if (modelRotation == null) {
               throw new JsonParseException("Invalid BlockModelRotation x: " + intValue + ", y: " + intValue2);
            } else {
               return modelRotation;
            }
         }

         private boolean parseUvLock(JsonObject json) {
            return JsonUtils.getBoolean(json, "uvlock", false);
         }

         protected String parseModel(JsonObject json) {
            return JsonUtils.getString(json, "model");
         }

         public ModelBlockDefinition.Variant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String text = this.parseModel(jsonObject);
            ModelRotation modelRotation = this.parseRotation(jsonObject);
            boolean conditionMet = this.parseUvLock(jsonObject);
            int i = this.parseWeight(jsonObject);
            return new ModelBlockDefinition.Variant(this.makeModelLocation(text), modelRotation, conditionMet, i);
         }
      }
   }

   public static class Variants {
      private final String name;
      private final List<ModelBlockDefinition.Variant> listVariants;

      public List<ModelBlockDefinition.Variant> getVariants() {
         return this.listVariants;
      }

      @Override
      public boolean equals(Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof ModelBlockDefinition.Variants modelblockdefinition$variants)) {
            return false;
         } else {
            return !this.name.equals(modelblockdefinition$variants.name) ? false : this.listVariants.equals(modelblockdefinition$variants.listVariants);
         }
      }

      public Variants(String nameIn, List<ModelBlockDefinition.Variant> listVariantsIn) {
         this.name = nameIn;
         this.listVariants = listVariantsIn;
      }

      @Override
      public int hashCode() {
         int i = this.name.hashCode();
         return 31 * i + this.listVariants.hashCode();
      }
   }
}
