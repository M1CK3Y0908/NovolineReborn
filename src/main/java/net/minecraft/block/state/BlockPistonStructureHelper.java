package net.minecraft.block.state;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockPistonStructureHelper {
   private final List<BlockPos> toMove = Lists.newArrayList();
   private final List<BlockPos> toDestroy = Lists.newArrayList();
   private final World world;
   private final BlockPos pistonPos;
   private final EnumFacing moveDirection;
   private final BlockPos blockToMove;

   public List<BlockPos> getBlocksToDestroy() {
      return this.toDestroy;
   }

   private void reorderBlocks(int tailCount, int splitIndex) {
      List<BlockPos> list = Lists.newArrayList();
      List<BlockPos> list3 = Lists.newArrayList();
      List<BlockPos> list4 = Lists.newArrayList();
      list.addAll(this.toMove.subList(0, splitIndex));
      list3.addAll(this.toMove.subList(this.toMove.size() - tailCount, this.toMove.size()));
      list4.addAll(this.toMove.subList(splitIndex, this.toMove.size() - tailCount));
      this.toMove.clear();
      this.toMove.addAll(list);
      this.toMove.addAll(list3);
      this.toMove.addAll(list4);
   }

   public boolean canMove() {
      this.toMove.clear();
      this.toDestroy.clear();
      Block block = this.world.getBlockState(this.blockToMove).getBlock();
      if (!BlockPistonBase.canPush(block, this.world, this.blockToMove, this.moveDirection, false)) {
         if (block.getMobilityFlag() != 1) {
            return false;
         }

         this.toDestroy.add(this.blockToMove);
         return true;
      } else {
         if (!this.canMoveBlock(this.blockToMove)) {
            return false;
         }

         for (int index = 0; index < this.toMove.size(); index++) {
            BlockPos blockPos = this.toMove.get(index);
            if (this.world.getBlockState(blockPos).getBlock() == Blocks.slime_block && !this.canMoveSlimeBlock(blockPos)) {
               return false;
            }
         }

         return true;
      }
   }

   public BlockPistonStructureHelper(World worldIn, BlockPos posIn, EnumFacing pistonFacing, boolean extending) {
      this.world = worldIn;
      this.pistonPos = posIn;
      if (extending) {
         this.moveDirection = pistonFacing;
         this.blockToMove = posIn.offset(pistonFacing);
      } else {
         this.moveDirection = pistonFacing.getOpposite();
         this.blockToMove = posIn.offset(pistonFacing, 2);
      }
   }

   private boolean canMoveSlimeBlock(BlockPos position) {
      for (EnumFacing enumFacing : EnumFacing.values()) {
         if (enumFacing.getAxis() != this.moveDirection.getAxis() && !this.canMoveBlock(position.offset(enumFacing))) {
            return false;
         }
      }

      return true;
   }

   private boolean canMoveBlock(BlockPos origin) {
      Block block2 = this.world.getBlockState(origin).getBlock();
      if (block2.getMaterial() == Material.air) {
         return true;
      }

      if (!BlockPistonBase.canPush(block2, this.world, origin, this.moveDirection, false)) {
         return true;
      }

      if (origin.equals(this.pistonPos)) {
         return true;
      }

      if (this.toMove.contains(origin)) {
         return true;
      }

      int i = 1;
      if (1 + this.toMove.size() > 12) {
         return false;
      }

      while (block2 == Blocks.slime_block) {
         BlockPos blockPos2 = origin.offset(this.moveDirection.getOpposite(), i);
         block2 = this.world.getBlockState(blockPos2).getBlock();
         if (block2.getMaterial() == Material.air
            || !BlockPistonBase.canPush(block2, this.world, blockPos2, this.moveDirection, false)
            || blockPos2.equals(this.pistonPos)) {
            break;
         }

         if (++i + this.toMove.size() > 12) {
            return false;
         }
      }

      int i2 = 0;

      for (int i3 = i - 1; i3 >= 0; i3--) {
         this.toMove.add(origin.offset(this.moveDirection.getOpposite(), i3));
         i2++;
      }

      int i4 = 1;

      while (true) {
         BlockPos blockPos3 = origin.offset(this.moveDirection, i4);
         int i5 = this.toMove.indexOf(blockPos3);
         if (i5 > -1) {
            this.reorderBlocks(i2, i5);

            for (int i6 = 0; i6 <= i5 + i2; i6++) {
               BlockPos blockPos4 = this.toMove.get(i6);
               if (this.world.getBlockState(blockPos4).getBlock() == Blocks.slime_block && !this.canMoveSlimeBlock(blockPos4)) {
                  return false;
               }
            }

            return true;
         }

         block2 = this.world.getBlockState(blockPos3).getBlock();
         if (block2.getMaterial() == Material.air) {
            return true;
         }

         if (!BlockPistonBase.canPush(block2, this.world, blockPos3, this.moveDirection, true) || blockPos3.equals(this.pistonPos)) {
            return false;
         }

         if (block2.getMobilityFlag() == 1) {
            this.toDestroy.add(blockPos3);
            return true;
         }

         if (this.toMove.size() >= 12) {
            return false;
         }

         this.toMove.add(blockPos3);
         i2++;
         i4++;
      }
   }

   public List<BlockPos> getBlocksToMove() {
      return this.toMove;
   }
}
