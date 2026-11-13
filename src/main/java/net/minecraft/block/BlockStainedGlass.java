package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

public class BlockStainedGlass extends BlockBreakable {
   public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class);

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, COLOR);
   }

   @Override
   public MapColor getMapColor(IBlockState state) {
      return state.getValue(COLOR).getMapColor();
   }

   @Override
   public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
      for (EnumDyeColor enumDyeColor : EnumDyeColor.values()) {
         list.add(new ItemStack(itemIn, 1, enumDyeColor.getMetadata()));
      }
   }

   @Override
   public int getMetaFromState(IBlockState iBlockState) {
      return iBlockState.getValue(COLOR).getMetadata();
   }

   @Override
   public int damageDropped(IBlockState iBlockState) {
      return iBlockState.getValue(COLOR).getMetadata();
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(COLOR, EnumDyeColor.byMetadata(meta));
   }

   public BlockStainedGlass(Material materialIn) {
      super(materialIn);
      this.setDefaultState(this.blockState.getBaseState().withProperty(COLOR, EnumDyeColor.WHITE));
      this.setCreativeTab(CreativeTabs.tabBlock);
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
   public void breakBlock(World world2, BlockPos blockPos, IBlockState iBlockState) {
      if (!world2.isRemote) {
         BlockBeacon.updateColorAsync(world2, blockPos);
      }
   }

   @Override
   public void onBlockAdded(World world2, BlockPos blockPos, IBlockState iBlockState) {
      if (!world2.isRemote) {
         BlockBeacon.updateColorAsync(world2, blockPos);
      }
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.TRANSLUCENT;
   }
}
