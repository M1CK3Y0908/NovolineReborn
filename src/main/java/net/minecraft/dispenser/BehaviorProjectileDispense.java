package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class BehaviorProjectileDispense extends BehaviorDefaultDispenseItem {
   @Override
   protected void playDispenseSound(IBlockSource source) {
      source.getWorld().playAuxSFX(1002, source.getBlockPos(), 0);
   }

   protected abstract IProjectile getProjectileEntity(World world2, IPosition iPosition);

   @Override
   public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
      World world = source.getWorld();
      IPosition position = BlockDispenser.getDispensePosition(source);
      EnumFacing facing = BlockDispenser.getFacing(source.getBlockMetadata());
      IProjectile projectile = this.getProjectileEntity(world, position);
      projectile.setThrowableHeading(
         facing.getFrontOffsetX(), facing.getFrontOffsetY() + 0.1F, facing.getFrontOffsetZ(), this.getProjectileVelocity(), this.getProjectileInaccuracy()
      );
      world.spawnEntityInWorld((Entity)projectile);
      stack.splitStack(1);
      return stack;
   }

   protected float getProjectileVelocity() {
      return 1.1F;
   }

   protected float getProjectileInaccuracy() {
      return 6.0F;
   }
}
