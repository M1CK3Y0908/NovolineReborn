package net.minecraft.block;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockPressurePlate extends BlockBasePressurePlate {
   public static final PropertyBool POWERED = PropertyBool.create("powered");
   private final BlockPressurePlate.Sensitivity sensitivity;

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(POWERED, meta == 1);
   }

   protected BlockPressurePlate(Material materialIn, BlockPressurePlate.Sensitivity sensitivityIn) {
      super(materialIn);
      this.setDefaultState(this.blockState.getBaseState().withProperty(POWERED, false));
      this.sensitivity = sensitivityIn;
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(POWERED) ? 1 : 0;
   }

   @Override
   protected int getRedstoneStrength(IBlockState state) {
      return state.getValue(POWERED) ? 15 : 0;
   }

   @Override
   protected IBlockState setRedstoneStrength(IBlockState state, int strength) {
      return state.withProperty(POWERED, strength > 0);
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, POWERED);
   }

   @Override
   protected int computeRedstoneStrength(World worldIn, BlockPos pos) {
      AxisAlignedBB sensitiveAABB = this.getSensitiveAABB(pos);
      List list;
      switch (this.sensitivity) {
         case EVERYTHING:
            list = worldIn.getEntitiesWithinAABBExcludingEntity((Entity)null, sensitiveAABB);
            break;
         case MOBS:
            list = worldIn.getEntitiesWithinAABB(EntityLivingBase.class, sensitiveAABB);
            break;
         default:
            return 0;
      }

      if (!list.isEmpty()) {
         Iterator iterator2 = list.iterator();

         while (iterator2.hasNext()) {
            if (!((Entity)iterator2.next()).doesEntityNotTriggerPressurePlate()) {
               return 15;
            }
         }
      }

      return 0;
   }

   public enum Sensitivity {
      EVERYTHING,
      MOBS;
   }
}
