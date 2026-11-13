package net.minecraft.world.gen;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

public class FlatLayerInfo {
   private int layerMinimumY;
   private int layerCount = 1;
   private final int version;
   private IBlockState layerMaterial;

   public int getLayerCount() {
      return this.layerCount;
   }

   public FlatLayerInfo(int version, int height, Block block, int metadata) {
      this(version, height, block);
      this.layerMaterial = block.getStateFromMeta(metadata);
   }

   public IBlockState getLayerMaterial() {
      return this.layerMaterial;
   }

   @Override
   public String toString() {
      String text;
      if (this.version >= 3) {
         ResourceLocation nameForObject = Block.blockRegistry.getNameForObject(this.getLayerMaterialBlock());
         text = nameForObject == null ? "null" : nameForObject.toString();
         if (this.layerCount > 1) {
            text = this.layerCount + "*" + text;
         }
      } else {
         text = Integer.toString(Block.getIdFromBlock(this.getLayerMaterialBlock()));
         if (this.layerCount > 1) {
            text = this.layerCount + "x" + text;
         }
      }

      int fillBlockMeta = this.getFillBlockMeta();
      if (fillBlockMeta > 0) {
         text = text + ":" + fillBlockMeta;
      }

      return text;
   }

   private int getFillBlockMeta() {
      return this.layerMaterial.getBlock().getMetaFromState(this.layerMaterial);
   }

   public int getMinY() {
      return this.layerMinimumY;
   }

   public FlatLayerInfo(int height, Block block) {
      this(3, height, block);
   }

   public void setMinY(int minY) {
      this.layerMinimumY = minY;
   }

   public FlatLayerInfo(int version, int height, Block layerMaterialIn) {
      this.version = version;
      this.layerCount = height;
      this.layerMaterial = layerMaterialIn.getDefaultState();
   }

   private Block getLayerMaterialBlock() {
      return this.layerMaterial.getBlock();
   }
}
