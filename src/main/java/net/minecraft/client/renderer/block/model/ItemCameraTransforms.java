package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.client.renderer.GlStateManager;

public class ItemCameraTransforms {
   public final ItemTransformVec3f thirdPerson;
   public static final ItemCameraTransforms DEFAULT = new ItemCameraTransforms();
   public static float translationOffsetX = 0.0F;
   public static float translationOffsetY = 0.0F;
   public static float translationOffsetZ = 0.0F;
   public final ItemTransformVec3f firstPerson;
   public static float rotationOffsetX = 0.0F;
   public static float rotationOffsetY = 0.0F;
   public final ItemTransformVec3f head;
   public static float rotationOffsetZ = 0.0F;
   public static float scaleOffsetX = 0.0F;
   public static float scaleOffsetY = 0.0F;
   public static float scaleOffsetZ = 0.0F;
   public final ItemTransformVec3f gui;
   public final ItemTransformVec3f ground;
   public final ItemTransformVec3f fixed;

   public ItemCameraTransforms(
      ItemTransformVec3f thirdPersonIn,
      ItemTransformVec3f firstPersonIn,
      ItemTransformVec3f headIn,
      ItemTransformVec3f guiIn,
      ItemTransformVec3f groundIn,
      ItemTransformVec3f fixedIn
   ) {
      this.thirdPerson = thirdPersonIn;
      this.firstPerson = firstPersonIn;
      this.head = headIn;
      this.gui = guiIn;
      this.ground = groundIn;
      this.fixed = fixedIn;
   }

   public void applyTransform(ItemCameraTransforms.TransformType type) {
      ItemTransformVec3f itemTransformVec3f = this.getTransform(type);
      if (itemTransformVec3f != ItemTransformVec3f.DEFAULT) {
         GlStateManager.translate(
            itemTransformVec3f.translation.x + translationOffsetX,
            itemTransformVec3f.translation.y + translationOffsetY,
            itemTransformVec3f.translation.z + translationOffsetZ
         );
         GlStateManager.rotate(itemTransformVec3f.rotation.y + rotationOffsetY, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(itemTransformVec3f.rotation.x + rotationOffsetX, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(itemTransformVec3f.rotation.z + rotationOffsetZ, 0.0F, 0.0F, 1.0F);
         GlStateManager.scale(itemTransformVec3f.scale.x + scaleOffsetX, itemTransformVec3f.scale.y + scaleOffsetY, itemTransformVec3f.scale.z + scaleOffsetZ);
      }
   }

   public ItemTransformVec3f getTransform(ItemCameraTransforms.TransformType type) {
      switch (type) {
         case THIRD_PERSON:
            return this.thirdPerson;
         case FIRST_PERSON:
            return this.firstPerson;
         case HEAD:
            return this.head;
         case GUI:
            return this.gui;
         case GROUND:
            return this.ground;
         case FIXED:
            return this.fixed;
         default:
            return ItemTransformVec3f.DEFAULT;
      }
   }

   private ItemCameraTransforms() {
      this(
         ItemTransformVec3f.DEFAULT,
         ItemTransformVec3f.DEFAULT,
         ItemTransformVec3f.DEFAULT,
         ItemTransformVec3f.DEFAULT,
         ItemTransformVec3f.DEFAULT,
         ItemTransformVec3f.DEFAULT
      );
   }

   public ItemCameraTransforms(ItemCameraTransforms transforms) {
      this.thirdPerson = transforms.thirdPerson;
      this.firstPerson = transforms.firstPerson;
      this.head = transforms.head;
      this.gui = transforms.gui;
      this.ground = transforms.ground;
      this.fixed = transforms.fixed;
   }

   public boolean hasTransform(ItemCameraTransforms.TransformType type) {
      return !this.getTransform(type).equals(ItemTransformVec3f.DEFAULT);
   }

   static class Deserializer implements JsonDeserializer<ItemCameraTransforms> {
      public ItemCameraTransforms deserialize(JsonElement arg1, Type arg2, JsonDeserializationContext arg3) throws JsonParseException {
         JsonObject jsonObject = arg1.getAsJsonObject();
         ItemTransformVec3f itemTransformVec3f = this.getTransformFromJson(arg3, jsonObject, "thirdperson");
         ItemTransformVec3f itemTransformVec3f2 = this.getTransformFromJson(arg3, jsonObject, "firstperson");
         ItemTransformVec3f itemTransformVec3f3 = this.getTransformFromJson(arg3, jsonObject, "head");
         ItemTransformVec3f itemTransformVec3f4 = this.getTransformFromJson(arg3, jsonObject, "gui");
         ItemTransformVec3f itemTransformVec3f5 = this.getTransformFromJson(arg3, jsonObject, "ground");
         ItemTransformVec3f itemTransformVec3f6 = this.getTransformFromJson(arg3, jsonObject, "fixed");
         return new ItemCameraTransforms(
            itemTransformVec3f, itemTransformVec3f2, itemTransformVec3f3, itemTransformVec3f4, itemTransformVec3f5, itemTransformVec3f6
         );
      }

      private ItemTransformVec3f getTransformFromJson(JsonDeserializationContext context, JsonObject jsonObject, String key) {
         return jsonObject.has(key) ? context.deserialize(jsonObject.get(key), ItemTransformVec3f.class) : ItemTransformVec3f.DEFAULT;
      }
   }

   public enum TransformType {
      NONE,
      THIRD_PERSON,
      FIRST_PERSON,
      HEAD,
      GUI,
      GROUND,
      FIXED;
   }
}
