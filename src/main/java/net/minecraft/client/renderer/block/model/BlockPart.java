package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.MathHelper;
import net.minecraft.util.EnumFacing.Axis;
import org.lwjgl.util.vector.Vector3f;

public class BlockPart {
   public final Vector3f positionFrom;
   public final Vector3f positionTo;
   public final Map<EnumFacing, BlockPartFace> mapFaces;
   public final BlockPartRotation partRotation;
   public final boolean shade;

   public BlockPart(
      Vector3f positionFromIn, Vector3f positionToIn, Map<EnumFacing, BlockPartFace> mapFacesIn, BlockPartRotation partRotationIn, boolean shadeIn
   ) {
      this.positionFrom = positionFromIn;
      this.positionTo = positionToIn;
      this.mapFaces = mapFacesIn;
      this.partRotation = partRotationIn;
      this.shade = shadeIn;
      this.setDefaultUvs();
   }

   private void setDefaultUvs() {
      for (Entry entry2 : this.mapFaces.entrySet()) {
         float[] faceUvs = this.getFaceUvs((EnumFacing)entry2.getKey());
         ((BlockPartFace)entry2.getValue()).blockFaceUV.setUvs(faceUvs);
      }
   }

   private float[] getFaceUvs(EnumFacing enumFacing) {
      return switch (enumFacing) {
         case DOWN, UP -> new float[]{this.positionFrom.x, this.positionFrom.z, this.positionTo.x, this.positionTo.z};
         case NORTH, SOUTH -> new float[]{this.positionFrom.x, 16.0F - this.positionTo.y, this.positionTo.x, 16.0F - this.positionFrom.y};
         case WEST, EAST -> new float[]{this.positionFrom.z, 16.0F - this.positionTo.y, this.positionTo.z, 16.0F - this.positionFrom.y};
         default -> throw new NullPointerException();
      };
   }

   static class Deserializer implements JsonDeserializer<BlockPart> {
      private Axis parseAxis(JsonObject jsonObject2) {
         String text = JsonUtils.getString(jsonObject2, "axis");
         Axis enumfacing$axis = Axis.byName(text.toLowerCase());
         if (enumfacing$axis == null) {
            throw new JsonParseException("Invalid rotation axis: " + text);
         } else {
            return enumfacing$axis;
         }
      }

      private float parseAngle(JsonObject jsonObject2) {
         float floatValue = JsonUtils.getFloat(jsonObject2, "angle");
         if (floatValue != 0.0F && MathHelper.abs(floatValue) != 22.5F && MathHelper.abs(floatValue) != 45.0F) {
            throw new JsonParseException("Invalid rotation " + floatValue + " found, only -45/-22.5/0/22.5/45 allowed");
         } else {
            return floatValue;
         }
      }

      public BlockPart deserialize(JsonElement arg1, Type arg2, JsonDeserializationContext arg3) throws JsonParseException {
         JsonObject jsonObject = arg1.getAsJsonObject();
         Vector3f vector3f = this.parsePositionFrom(jsonObject);
         Vector3f vector3f2 = this.parsePositionTo(jsonObject);
         BlockPartRotation blockPartRotation = this.parseRotation(jsonObject);
         Map<EnumFacing, BlockPartFace> map = this.parseFacesCheck(arg3, jsonObject);
         if (jsonObject.has("shade") && !JsonUtils.isBoolean(jsonObject, "shade")) {
            throw new JsonParseException("Expected shade to be a Boolean");
         }

         boolean conditionMet = JsonUtils.getBoolean(jsonObject, "shade", true);
         return new BlockPart(vector3f, vector3f2, map, blockPartRotation, conditionMet);
      }

      private Vector3f parsePosition(JsonObject jsonObject2, String text2) {
         JsonArray jsonArray = JsonUtils.getJsonArray(jsonObject2, text2);
         if (jsonArray.size() != 3) {
            throw new JsonParseException("Expected 3 " + text2 + " values, found: " + jsonArray.size());
         }

         float[] floatArray = new float[3];

         for (int index = 0; index < floatArray.length; index++) {
            floatArray[index] = JsonUtils.getFloat(jsonArray.get(index), text2 + "[" + index + "]");
         }

         return new Vector3f(floatArray[0], floatArray[1], floatArray[2]);
      }

      private Map<EnumFacing, BlockPartFace> parseFaces(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject2) {
         Map<EnumFacing, BlockPartFace> map = Maps.newEnumMap(EnumFacing.class);

         for (Entry<String, JsonElement> entry : JsonUtils.getJsonObject(jsonObject2, "faces").entrySet()) {
            EnumFacing facing = this.parseEnumFacing(entry.getKey());
            map.put(facing, jsonDeserializationContext.deserialize(entry.getValue(), BlockPartFace.class));
         }

         return map;
      }

      private Map<EnumFacing, BlockPartFace> parseFacesCheck(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject2) {
         Map<EnumFacing, BlockPartFace> map = this.parseFaces(jsonDeserializationContext, jsonObject2);
         if (map.isEmpty()) {
            throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
         } else {
            return map;
         }
      }

      private Vector3f parsePositionTo(JsonObject jsonObject2) {
         Vector3f vector3f = this.parsePosition(jsonObject2, "to");
         if (vector3f.x >= -16.0F && vector3f.y >= -16.0F && vector3f.z >= -16.0F && vector3f.x <= 32.0F && vector3f.y <= 32.0F && vector3f.z <= 32.0F) {
            return vector3f;
         } else {
            throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + vector3f);
         }
      }

      private Vector3f parsePositionFrom(JsonObject jsonObject2) {
         Vector3f vector3f = this.parsePosition(jsonObject2, "from");
         if (vector3f.x >= -16.0F && vector3f.y >= -16.0F && vector3f.z >= -16.0F && vector3f.x <= 32.0F && vector3f.y <= 32.0F && vector3f.z <= 32.0F) {
            return vector3f;
         } else {
            throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + vector3f);
         }
      }

      private BlockPartRotation parseRotation(JsonObject jsonObject2) {
         BlockPartRotation blockPartRotation2 = null;
         if (jsonObject2.has("rotation")) {
            JsonObject jsonObject = JsonUtils.getJsonObject(jsonObject2, "rotation");
            Vector3f vector3f = this.parsePosition(jsonObject, "origin");
            vector3f.scale(0.0625F);
            Axis enumfacing$axis = this.parseAxis(jsonObject);
            float f = this.parseAngle(jsonObject);
            boolean conditionMet = JsonUtils.getBoolean(jsonObject, "rescale", false);
            blockPartRotation2 = new BlockPartRotation(vector3f, enumfacing$axis, f, conditionMet);
         }

         return blockPartRotation2;
      }

      private EnumFacing parseEnumFacing(String name) {
         EnumFacing facing = EnumFacing.byName(name);
         if (facing == null) {
            throw new JsonParseException("Unknown facing: " + name);
         } else {
            return facing;
         }
      }
   }
}
