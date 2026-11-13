package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BehaviorDefaultDispenseItem implements IBehaviorDispenseItem {
   private int getWorldEventDataFrom(EnumFacing facingIn) {
      return facingIn.getFrontOffsetX() + 1 + (facingIn.getFrontOffsetZ() + 1) * 3;
   }

   public static void handleWorld(World world2, ItemStack itemStack2, EnumFacing enumFacing, IPosition iPosition) {
      double x = iPosition.getX();
      double y = iPosition.getY();
      double z = iPosition.getZ();
      if (enumFacing.getAxis() == net.minecraft.util.EnumFacing.Axis.Y) {
         y -= 0.125;
      } else {
         y -= 0.15625;
      }

      EntityItem entityItem = new EntityItem(world2, x, y, z, itemStack2);
      double d = world2.rand.nextDouble() * 0.1 + 0.2;
      entityItem.motionX = enumFacing.getFrontOffsetX() * d;
      entityItem.motionY = 0.2F;
      entityItem.motionZ = enumFacing.getFrontOffsetZ() * d;
      entityItem.motionX = entityItem.motionX + world2.rand.nextGaussian() * 0.0075F * 6;
      entityItem.motionY = entityItem.motionY + world2.rand.nextGaussian() * 0.0075F * 6;
      entityItem.motionZ = entityItem.motionZ + world2.rand.nextGaussian() * 0.0075F * 6;
      world2.spawnEntityInWorld(entityItem);
   }

   protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
      EnumFacing facing = BlockDispenser.getFacing(source.getBlockMetadata());
      IPosition position = BlockDispenser.getDispensePosition(source);
      ItemStack itemStack = stack.splitStack(1);
      handleWorld(source.getWorld(), itemStack, facing, position);
      return stack;
   }

   @Override
   public final ItemStack dispense(IBlockSource source, ItemStack stack) {
      ItemStack itemStack = this.dispenseStack(source, stack);
      this.playDispenseSound(source);
      this.spawnDispenseParticles(source, BlockDispenser.getFacing(source.getBlockMetadata()));
      return itemStack;
   }

   protected void playDispenseSound(IBlockSource source) {
      source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
   }

   protected void spawnDispenseParticles(IBlockSource source, EnumFacing facingIn) {
      source.getWorld().playAuxSFX(2000, source.getBlockPos(), this.getWorldEventDataFrom(facingIn));
   }
}
