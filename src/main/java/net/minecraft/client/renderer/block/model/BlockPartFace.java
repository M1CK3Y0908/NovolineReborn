package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;

public class BlockPartFace {
   public final EnumFacing cullFace;
   public final int tintIndex;
   public final String texture;
   public final BlockFaceUV blockFaceUV;
   public static final EnumFacing FACING_DEFAULT = null;

   public BlockPartFace(EnumFacing cullFaceIn, int tintIndexIn, String textureIn, BlockFaceUV blockFaceUVIn) {
      this.cullFace = cullFaceIn;
      this.tintIndex = tintIndexIn;
      this.texture = textureIn;
      this.blockFaceUV = blockFaceUVIn;
   }

   static class Deserializer implements JsonDeserializer<BlockPartFace> {
      private String parseTexture(JsonObject jsonObject2) {
         return JsonUtils.getString(jsonObject2, "texture");
      }

      public BlockPartFace deserialize(JsonElement arg1, Type arg2, JsonDeserializationContext arg3) throws JsonParseException {
         JsonObject jsonObject = arg1.getAsJsonObject();
         EnumFacing facing = this.parseCullFace(jsonObject);
         int i = this.parseTintIndex(jsonObject);
         String text = this.parseTexture(jsonObject);
         BlockFaceUV blockFaceUV = arg3.deserialize(jsonObject, BlockFaceUV.class);
         return new BlockPartFace(facing, i, text, blockFaceUV);
      }

      private EnumFacing parseCullFace(JsonObject jsonObject2) {
         return EnumFacing.byName(JsonUtils.getString(jsonObject2, "cullface", ""));
      }

      protected int parseTintIndex(JsonObject jsonObject2) {
         return JsonUtils.getInt(jsonObject2, "tintindex", -1);
      }
   }
}
