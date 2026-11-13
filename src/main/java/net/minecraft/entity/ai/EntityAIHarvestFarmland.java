package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityAIHarvestFarmland extends EntityAIMoveToBlock {
   private boolean needsFood;
   private int cropAction;
   private boolean hasFarmItem;
   private final EntityVillager theVillager;

   @Override
   public void resetTask() {
      super.resetTask();
   }

   @Override
   public void updateTask() {
      super.updateTask();
      this.theVillager
         .getLookHelper()
         .setLookPosition(
            this.destinationBlock.getX() + 0.5,
            this.destinationBlock.getY() + 1,
            this.destinationBlock.getZ() + 0.5,
            10.0F,
            this.theVillager.getVerticalFaceSpeed()
         );
      if (this.getIsAboveDestination()) {
         World world = this.theVillager.worldObj;
         BlockPos blockPos = this.destinationBlock.up();
         IBlockState blockState = world.getBlockState(blockPos);
         Block block = blockState.getBlock();
         if (this.cropAction == 0 && block instanceof BlockCrops && blockState.getValue(BlockCrops.AGE) == 7) {
            world.destroyBlock(blockPos, true);
         } else if (this.cropAction == 1 && block == Blocks.air) {
            InventoryBasic inventoryBasic = this.theVillager.getVillagerInventory();

            for (int index = 0; index < inventoryBasic.getSizeInventory(); index++) {
               ItemStack itemStack = inventoryBasic.getStackInSlot(index);
               boolean conditionMet = false;
               if (itemStack != null) {
                  if (itemStack.getItem() == Items.wheat_seeds) {
                     world.setBlockState(blockPos, Blocks.wheat.getDefaultState(), 3);
                     conditionMet = true;
                  } else if (itemStack.getItem() == Items.potato) {
                     world.setBlockState(blockPos, Blocks.potatoes.getDefaultState(), 3);
                     conditionMet = true;
                  } else if (itemStack.getItem() == Items.carrot) {
                     world.setBlockState(blockPos, Blocks.carrots.getDefaultState(), 3);
                     conditionMet = true;
                  }
               }

               if (conditionMet) {
                  itemStack.stackSize--;
                  if (itemStack.stackSize <= 0) {
                     inventoryBasic.setInventorySlotContents(index, (ItemStack)null);
                  }
                  break;
               }
            }
         }

         this.cropAction = -1;
         this.runDelay = 10;
      }
   }

   @Override
   public void startExecuting() {
      super.startExecuting();
   }

   @Override
   public boolean shouldExecute() {
      if (this.runDelay <= 0) {
         if (!this.theVillager.worldObj.getGameRules().getBoolean("mobGriefing")) {
            return false;
         }

         this.cropAction = -1;
         this.hasFarmItem = this.theVillager.isFarmItemInInventory();
         this.needsFood = this.theVillager.needsFood();
      }

      return super.shouldExecute();
   }

   @Override
   protected boolean shouldMoveTo(World worldIn, BlockPos pos) {
      Block block2 = worldIn.getBlockState(pos).getBlock();
      if (block2 == Blocks.farmland) {
         pos = pos.up();
         IBlockState blockState2 = worldIn.getBlockState(pos);
         block2 = blockState2.getBlock();
         if (block2 instanceof BlockCrops && blockState2.getValue(BlockCrops.AGE) == 7 && this.needsFood && (this.cropAction == 0 || this.cropAction < 0)) {
            this.cropAction = 0;
            return true;
         }

         if (block2 == Blocks.air && this.hasFarmItem && (this.cropAction == 1 || this.cropAction < 0)) {
            this.cropAction = 1;
            return true;
         }
      }

      return false;
   }

   public EntityAIHarvestFarmland(EntityVillager theVillagerIn, double speedIn) {
      super(theVillagerIn, speedIn, 16);
      this.theVillager = theVillagerIn;
   }

   @Override
   public boolean continueExecuting() {
      return this.cropAction >= 0 && super.continueExecuting();
   }
}
