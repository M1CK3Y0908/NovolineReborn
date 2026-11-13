package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockPressurePlateWeighted extends BlockBasePressurePlate {
   private final int maxWeight;
   public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(POWER, meta);
   }

   @Override
   protected int getRedstoneStrength(IBlockState iBlockState) {
      return iBlockState.getValue(POWER);
   }

   protected BlockPressurePlateWeighted(Material material2) {
      this(material2, 150, material2.getMaterialMapColor());
   }

   @Override
   public int getMetaFromState(IBlockState iBlockState) {
      return iBlockState.getValue(POWER);
   }

   @Override
   public int tickRate(World worldIn) {
      return 10;
   }

   protected BlockPressurePlateWeighted(Material materialIn, int maxWeight, MapColor mapColorIn) {
      super(materialIn, mapColorIn);
      this.setDefaultState(this.blockState.getBaseState().withProperty(POWER, 0));
      this.maxWeight = maxWeight;
   }

   @Override
   protected int computeRedstoneStrength(World worldIn, BlockPos pos) {
      int count = Math.min(worldIn.getEntitiesWithinAABB(Entity.class, this.getSensitiveAABB(pos)).size(), this.maxWeight);
      if (count > 0) {
         float f = (float)Math.min(this.maxWeight, count) / this.maxWeight;
         return MathHelper.ceiling_float_int(f * 15.0F);
      } else {
         return 0;
      }
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, POWER);
   }

   @Override
   protected IBlockState setRedstoneStrength(IBlockState state, int strength) {
      return state.withProperty(POWER, strength);
   }
}
