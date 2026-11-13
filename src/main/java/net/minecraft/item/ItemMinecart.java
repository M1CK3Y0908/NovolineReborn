package net.minecraft.item;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemMinecart extends Item {
   private final net.minecraft.entity.item.EntityMinecart.EnumMinecartType minecartType;
   private static final IBehaviorDispenseItem dispenserMinecartBehavior = new BehaviorDefaultDispenseItem() {
      private final BehaviorDefaultDispenseItem behaviourDefaultDispenseItem = new BehaviorDefaultDispenseItem();

      @Override
      public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
         EnumFacing facing = BlockDispenser.getFacing(source.getBlockMetadata());
         World world2 = source.getWorld();
         double d = source.getX() + facing.getFrontOffsetX() * 1.125;
         double d2 = Math.floor(source.getY()) + facing.getFrontOffsetY();
         double d3 = source.getZ() + facing.getFrontOffsetZ() * 1.125;
         BlockPos blockPos = source.getBlockPos().offset(facing);
         IBlockState blockState2 = world2.getBlockState(blockPos);
         net.minecraft.block.BlockRailBase.EnumRailDirection enumRailDirection = blockState2.getBlock() instanceof BlockRailBase
            ? blockState2.getValue(((BlockRailBase)blockState2.getBlock()).getShapeProperty())
            : net.minecraft.block.BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         double d4;
         if (BlockRailBase.isRailBlock(blockState2)) {
            if (enumRailDirection.isAscending()) {
               d4 = 0.6;
            } else {
               d4 = 0.1;
            }
         } else {
            if (blockState2.getBlock().getMaterial() != Material.air || !BlockRailBase.isRailBlock(world2.getBlockState(blockPos.down()))) {
               return this.behaviourDefaultDispenseItem.dispense(source, stack);
            }

            IBlockState blockState3 = world2.getBlockState(blockPos.down());
            net.minecraft.block.BlockRailBase.EnumRailDirection enumRailDirection2 = blockState3.getBlock() instanceof BlockRailBase
               ? blockState3.getValue(((BlockRailBase)blockState3.getBlock()).getShapeProperty())
               : net.minecraft.block.BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            if (facing != EnumFacing.DOWN && enumRailDirection2.isAscending()) {
               d4 = -0.4;
            } else {
               d4 = -0.9;
            }
         }

         EntityMinecart minecart = EntityMinecart.getMinecart(world2, d, d2 + d4, d3, ((ItemMinecart)stack.getItem()).minecartType);
         if (stack.hasDisplayName()) {
            minecart.setCustomNameTag(stack.getDisplayName());
         }

         world2.spawnEntityInWorld(minecart);
         stack.splitStack(1);
         return stack;
      }

      @Override
      protected void playDispenseSound(IBlockSource source) {
         source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
      }
   };

   public ItemMinecart(net.minecraft.entity.item.EntityMinecart.EnumMinecartType type) {
      this.maxStackSize = 1;
      this.minecartType = type;
      this.setCreativeTab(CreativeTabs.tabTransport);
      BlockDispenser.dispenseBehaviorRegistry.putObject(this, dispenserMinecartBehavior);
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      IBlockState blockState = worldIn.getBlockState(pos);
      if (BlockRailBase.isRailBlock(blockState)) {
         if (!worldIn.isRemote) {
            net.minecraft.block.BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = blockState.getBlock() instanceof BlockRailBase
               ? blockState.getValue(((BlockRailBase)blockState.getBlock()).getShapeProperty())
               : net.minecraft.block.BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            double d = 0.0;
            if (blockrailbase$enumraildirection.isAscending()) {
               d = 0.5;
            }

            EntityMinecart entityMinecart = EntityMinecart.getMinecart(
               worldIn, pos.getX() + 0.5, pos.getY() + 0.0625 + d, pos.getZ() + 0.5, this.minecartType
            );
            if (stack.hasDisplayName()) {
               entityMinecart.setCustomNameTag(stack.getDisplayName());
            }

            worldIn.spawnEntityInWorld(entityMinecart);
         }

         stack.stackSize--;
         return true;
      } else {
         return false;
      }
   }
}
