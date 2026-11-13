package wtf.novoline.utils.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.world.XRay;

public class XRaySection extends ConfigSection {
   private final XRay xRay;

   public XRaySection() {
      super("xray");
      this.xRay = ModuleManager.xRay;
   }

   @Override
   public void deserialize(JsonObject jsonObject) {
      this.xRay.visibleBlocks.clear();
      JsonArray asJsonArray = jsonObject.getAsJsonArray("blocks");
      if (asJsonArray != null) {
         for (JsonElement jsonElement : asJsonArray) {
            this.xRay.visibleBlocks.add(Block.getBlockById(jsonElement.getAsInt()));
         }
      }
   }

   @Override
   public JsonObject serialize() {
      JsonObject jsonObject = new JsonObject();
      JsonArray jsonArray = new JsonArray();

      for (Block block2 : this.xRay.visibleBlocks) {
         jsonArray.add(Block.getIdFromBlock(block2));
      }

      jsonObject.add("blocks", jsonArray);
      return jsonObject;
   }
}
