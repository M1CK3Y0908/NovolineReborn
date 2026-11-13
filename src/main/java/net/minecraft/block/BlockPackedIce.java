package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockPackedIce extends Block {
   @Override
   public int quantityDropped(Random random) {
      return 0;
   }

   public BlockPackedIce() {
      super(Material.packedIce);
      this.slipperiness = 0.98F;
      this.setCreativeTab(CreativeTabs.tabBlock);
   }
}
