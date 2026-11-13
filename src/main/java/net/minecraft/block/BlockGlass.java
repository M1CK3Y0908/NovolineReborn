package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumWorldBlockLayer;

public class BlockGlass extends BlockBreakable {
   public BlockGlass(Material material2) {
      super(material2);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   protected boolean canSilkHarvest() {
      return true;
   }

   @Override
   public int quantityDropped(Random random) {
      return 0;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }
}
