package net.minecraft.item;

import net.minecraft.block.Block;

public class ItemPiston extends ItemBlock {
   @Override
   public int getMetadata(int damage) {
      return 7;
   }

   public ItemPiston(Block block) {
      super(block);
   }
}
