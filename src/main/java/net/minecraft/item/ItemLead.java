package net.minecraft.item;

import net.minecraft.block.BlockFence;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemLead extends Item {
   public static boolean attachToFence(EntityPlayer player, World worldIn, BlockPos fence) {
      EntityLeashKnot knotForPosition = EntityLeashKnot.getKnotForPosition(worldIn, fence);
      boolean flag = false;
      double d = 7.0;
      int x = fence.getX();
      int y = fence.getY();
      int z = fence.getZ();

      for (EntityLiving entityLiving : worldIn.getEntitiesWithinAABB(
         EntityLiving.class, new AxisAlignedBB(x - d, y - d, z - d, x + d, y + d, z + d)
      )) {
         if (entityLiving.getLeashed() && entityLiving.getLeashedToEntity() == player) {
            if (knotForPosition == null) {
               knotForPosition = EntityLeashKnot.createKnot(worldIn, fence);
            }

            entityLiving.setLeashedToEntity(knotForPosition, true);
            flag = true;
         }
      }

      return flag;
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.getBlockState(pos).getBlock() instanceof BlockFence) {
         if (worldIn.isRemote) {
            return true;
         }

         attachToFence(playerIn, worldIn, pos);
         return true;
      } else {
         return false;
      }
   }

   public ItemLead() {
      this.setCreativeTab(CreativeTabs.tabTools);
   }
}
