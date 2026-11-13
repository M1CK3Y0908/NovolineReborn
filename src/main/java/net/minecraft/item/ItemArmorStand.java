package net.minecraft.item;

import java.util.Random;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Rotations;
import net.minecraft.world.World;

public class ItemArmorStand extends Item {
   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (side == EnumFacing.DOWN) {
         return false;
      }

      BlockPos blockPos = worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos) ? pos : pos.offset(side);
      if (!playerIn.canPlayerEdit(blockPos, side, stack)) {
         return false;
      }

      BlockPos blockPos2 = blockPos.up();
      if ((!worldIn.isAirBlock(blockPos) && !worldIn.getBlockState(blockPos).getBlock().isReplaceable(worldIn, blockPos))
         | (!worldIn.isAirBlock(blockPos2) && !worldIn.getBlockState(blockPos2).getBlock().isReplaceable(worldIn, blockPos2))) {
         return false;
      }

      double x = blockPos.getX();
      double y = blockPos.getY();
      double z = blockPos.getZ();
      if (worldIn.getEntitiesWithinAABBExcludingEntity((Entity)null, AxisAlignedBB.fromBounds(x, y, z, x + 1.0, y + 2.0, z + 1.0))
            .size()
         > 0) {
         return false;
      }

      if (!worldIn.isRemote) {
         worldIn.setBlockToAir(blockPos);
         worldIn.setBlockToAir(blockPos2);
         EntityArmorStand entityArmorStand = new EntityArmorStand(worldIn, x + 0.5, y, z + 0.5);
         float f = MathHelper.floor_float((MathHelper.wrapAngleTo180_float(playerIn.rotationYaw - 180.0F) + 22.5F) / 45.0F) * 45.0F;
         entityArmorStand.setLocationAndAngles(x + 0.5, y, z + 0.5, f, 0.0F);
         this.applyRandomRotations(entityArmorStand, worldIn.rand);
         NBTTagCompound tagCompound = stack.getTagCompound();
         if (tagCompound != null && tagCompound.hasKey("EntityTag", 10)) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            entityArmorStand.writeToNBTOptional(nbtTagCompound);
            nbtTagCompound.merge(tagCompound.getCompoundTag("EntityTag"));
            entityArmorStand.readFromNBT(nbtTagCompound);
         }

         worldIn.spawnEntityInWorld(entityArmorStand);
      }

      stack.stackSize--;
      return true;
   }

   public ItemArmorStand() {
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   private void applyRandomRotations(EntityArmorStand armorStand, Random rand) {
      Rotations headRotation = armorStand.getHeadRotation();
      float f = rand.nextFloat() * 5.0F;
      float f2 = rand.nextFloat() * 20.0F - 10.0F;
      Rotations rotations = new Rotations(headRotation.getX() + f, headRotation.getY() + f2, headRotation.getZ());
      armorStand.setHeadRotation(rotations);
      headRotation = armorStand.getBodyRotation();
      f = rand.nextFloat() * 10.0F - 5.0F;
      rotations = new Rotations(headRotation.getX(), headRotation.getY() + f, headRotation.getZ());
      armorStand.setBodyRotation(rotations);
   }
}
