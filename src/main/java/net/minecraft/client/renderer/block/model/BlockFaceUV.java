package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;

public class BlockFaceUV {
   public float[] uvs;
   public final int rotation;

   private int getVertexRotated(int vertexIndex) {
      return (vertexIndex + this.rotation / 90) % 4;
   }

   public float getV(int vertexIndex) {
      if (this.uvs == null) {
         throw new NullPointerException("uvs");
      }

      int vertexRotated = this.getVertexRotated(vertexIndex);
      return vertexRotated != 0 && vertexRotated != 3 ? this.uvs[3] : this.uvs[1];
   }

   public int getVertexRotatedReverse(int vertexIndex) {
      return (vertexIndex + (4 - this.rotation / 90)) % 4;
   }

   public void setUvs(float[] uvsIn) {
      if (this.uvs == null) {
         this.uvs = uvsIn;
      }
   }

   public BlockFaceUV(float[] uvsIn, int rotationIn) {
      this.uvs = uvsIn;
      this.rotation = rotationIn;
   }

   public float getU(int vertexIndex) {
      if (this.uvs == null) {
         throw new NullPointerException("uvs");
      }

      int vertexRotated = this.getVertexRotated(vertexIndex);
      return vertexRotated != 0 && vertexRotated != 1 ? this.uvs[2] : this.uvs[0];
   }

   static class Deserializer implements JsonDeserializer<BlockFaceUV> {
      protected int parseRotation(JsonObject jsonObject) {
         int intValue = JsonUtils.getInt(jsonObject, "rotation", 0);
         if (intValue >= 0 && intValue % 90 == 0 && intValue / 90 <= 3) {
            return intValue;
         } else {
            throw new JsonParseException("Invalid rotation " + intValue + " found, only 0/90/180/270 allowed");
         }
      }

      private float[] parseUV(JsonObject jsonObject) {
         if (!jsonObject.has("uv")) {
            return null;
         }

         JsonArray jsonArray = JsonUtils.getJsonArray(jsonObject, "uv");
         if (jsonArray.size() != 4) {
            throw new JsonParseException("Expected 4 uv values, found: " + jsonArray.size());
         }

         float[] floatArray = new float[4];

         for (int index = 0; index < floatArray.length; index++) {
            floatArray[index] = JsonUtils.getFloat(jsonArray.get(index), "uv[" + index + "]");
         }

         return floatArray;
      }

      public BlockFaceUV deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonObject = json.getAsJsonObject();
         float[] floatArray = this.parseUV(jsonObject);
         int i = this.parseRotation(jsonObject);
         return new BlockFaceUV(floatArray, i);
      }
   }
}
