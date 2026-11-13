package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockBookshelf extends Block {
   @Override
   public int quantityDropped(Random random) {
      return 3;
   }

   @Override
   public Item getItemDropped(IBlockState iBlockState, Random random, int i) {
      return Items.book;
   }

   public BlockBookshelf() {
      super(Material.wood);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }
}
