package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlockNewLog extends BlockLog {
   public static final PropertyEnum<BlockPlanks.EnumType> VARIANT = PropertyEnum.create(
      "variant", BlockPlanks.EnumType.class, new Predicate<BlockPlanks.EnumType>() {
         public boolean apply(BlockPlanks.EnumType arg1) {
            return arg1.getMetadata() >= 4;
         }
      }
   );

   @Override
   public int getMetaFromState(IBlockState state) {
      int i = 0 | state.getValue(VARIANT).getMetadata() - 4;
      switch ((BlockLog.EnumAxis)state.getValue(LOG_AXIS)) {
         case X:
            i |= 4;
            break;
         case Z:
            i |= 8;
            break;
         case NONE:
            i |= 12;
      }

      return i;
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, VARIANT, LOG_AXIS);
   }

   @Override
   public int damageDropped(IBlockState state) {
      return state.getValue(VARIANT).getMetadata() - 4;
   }

   @Override
   public MapColor getMapColor(IBlockState state) {
      BlockPlanks.EnumType blockplanks$enumtype = state.getValue(VARIANT);
      switch ((BlockLog.EnumAxis)state.getValue(LOG_AXIS)) {
         case X:
         case Z:
         case NONE:
         default:
            switch (blockplanks$enumtype) {
               case ACACIA:
               default:
                  return MapColor.stoneColor;
               case DARK_OAK:
                  return BlockPlanks.EnumType.DARK_OAK.getMapColor();
            }
         case Y:
            return blockplanks$enumtype.getMapColor();
      }
   }

   @Override
   public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
      list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.ACACIA.getMetadata() - 4));
      list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.DARK_OAK.getMetadata() - 4));
   }

   public BlockNewLog() {
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockPlanks.EnumType.ACACIA).withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
   }

   @Override
   protected ItemStack createStackedBlock(IBlockState state) {
      return new ItemStack(Item.getItemFromBlock(this), 1, state.getValue(VARIANT).getMetadata() - 4);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      IBlockState iBlockState = this.getDefaultState().withProperty(VARIANT, BlockPlanks.EnumType.byMetadata((meta & 3) + 4));

      return switch (meta & 12) {
         case 0 -> iBlockState.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y);
         case 4 -> iBlockState.withProperty(LOG_AXIS, BlockLog.EnumAxis.X);
         case 8 -> iBlockState.withProperty(LOG_AXIS, BlockLog.EnumAxis.Z);
         default -> iBlockState.withProperty(LOG_AXIS, BlockLog.EnumAxis.NONE);
      };
   }
}
