package net.minecraft.command;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class CommandClone extends CommandBase {
   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 9) {
         throw new WrongUsageException("commands.clone.usage");
      }

      sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
      BlockPos blockPos = parseBlockPos(sender, args, 0, false);
      BlockPos blockPos2 = parseBlockPos(sender, args, 3, false);
      BlockPos blockPos3 = parseBlockPos(sender, args, 6, false);
      StructureBoundingBox structureBoundingBox = new StructureBoundingBox(blockPos, blockPos2);
      StructureBoundingBox structureBoundingBox2 = new StructureBoundingBox(blockPos3, blockPos3.add(structureBoundingBox.getLength()));
      int i = structureBoundingBox.getXSize() * structureBoundingBox.getYSize() * structureBoundingBox.getZSize();
      if (i > 32768) {
         throw new CommandException("commands.clone.tooManyBlocks", i, 32768);
      }

      boolean flag = false;
      Block block2 = null;
      int i2 = -1;
      if ((args.length < 11 || !args[10].equals("force") && !args[10].equals("move")) && structureBoundingBox.intersectsWith(structureBoundingBox2)) {
         throw new CommandException("commands.clone.noOverlap");
      }

      if (args.length >= 11 && args[10].equals("move")) {
         flag = true;
      }

      if (structureBoundingBox.minY >= 0 && structureBoundingBox.maxY < 256 && structureBoundingBox2.minY >= 0 && structureBoundingBox2.maxY < 256) {
         World entityWorld = sender.getEntityWorld();
         if (entityWorld.isAreaLoaded(structureBoundingBox) && entityWorld.isAreaLoaded(structureBoundingBox2)) {
            boolean flag2 = false;
            if (args.length >= 10) {
               if (args[9].equals("masked")) {
                  flag2 = true;
               } else if (args[9].equals("filtered")) {
                  if (args.length < 12) {
                     throw new WrongUsageException("commands.clone.usage");
                  }

                  block2 = getBlockByText(sender, args[11]);
                  if (args.length >= 13) {
                     i2 = parseInt(args[12], 0, 15);
                  }
               }
            }

            ArrayList arrayList = Lists.newArrayList();
            ArrayList arrayList2 = Lists.newArrayList();
            ArrayList arrayList3 = Lists.newArrayList();
            LinkedList linkedList = Lists.newLinkedList();
            BlockPos blockPos4 = new BlockPos(structureBoundingBox2.minX - structureBoundingBox.minX, structureBoundingBox2.minY - structureBoundingBox.minY, structureBoundingBox2.minZ - structureBoundingBox.minZ);

            for (int minZ2 = structureBoundingBox.minZ; minZ2 <= structureBoundingBox.maxZ; minZ2++) {
               for (int minY2 = structureBoundingBox.minY; minY2 <= structureBoundingBox.maxY; minY2++) {
                  for (int minX2 = structureBoundingBox.minX; minX2 <= structureBoundingBox.maxX; minX2++) {
                     BlockPos blockPos5 = new BlockPos(minX2, minY2, minZ2);
                     BlockPos blockPos6 = blockPos5.add(blockPos4);
                     IBlockState blockState2 = entityWorld.getBlockState(blockPos5);
                     if ((!flag2 || blockState2.getBlock() != Blocks.air)
                        && (block2 == null || blockState2.getBlock() == block2 && (i2 < 0 || blockState2.getBlock().getMetaFromState(blockState2) == i2))) {
                        TileEntity tileEntity = entityWorld.getTileEntity(blockPos5);
                        if (tileEntity != null) {
                           NBTTagCompound nbtTagCompound = new NBTTagCompound();
                           tileEntity.writeToNBT(nbtTagCompound);
                           arrayList2.add(new CommandClone.StaticCloneData(blockPos6, blockState2, nbtTagCompound));
                           linkedList.addLast(blockPos5);
                        } else if (!blockState2.getBlock().isFullBlock() && !blockState2.getBlock().isFullCube()) {
                           arrayList3.add(new CommandClone.StaticCloneData(blockPos6, blockState2, (NBTTagCompound)null));
                           linkedList.addFirst(blockPos5);
                        } else {
                           arrayList.add(new CommandClone.StaticCloneData(blockPos6, blockState2, (NBTTagCompound)null));
                           linkedList.addLast(blockPos5);
                        }
                     }
                  }
               }
            }

            if (flag) {
               for (BlockPos blockPos7 : (Iterable<BlockPos>) linkedList) {
                  TileEntity tileEntity2 = entityWorld.getTileEntity(blockPos7);
                  if (tileEntity2 instanceof IInventory) {
                     ((IInventory)tileEntity2).clear();
                  }

                  entityWorld.setBlockState(blockPos7, Blocks.barrier.getDefaultState(), 2);
               }

               for (BlockPos blockPos8 : (Iterable<BlockPos>) linkedList) {
                  entityWorld.setBlockState(blockPos8, Blocks.air.getDefaultState(), 3);
               }
            }

            ArrayList arrayList4 = Lists.newArrayList();
            arrayList4.addAll(arrayList);
            arrayList4.addAll(arrayList2);
            arrayList4.addAll(arrayList3);
            List list = Lists.reverse(arrayList4);

            for (CommandClone.StaticCloneData staticCloneData : (Iterable<CommandClone.StaticCloneData>) list) {
               TileEntity tileEntity3 = entityWorld.getTileEntity(staticCloneData.pos);
               if (tileEntity3 instanceof IInventory) {
                  ((IInventory)tileEntity3).clear();
               }

               entityWorld.setBlockState(staticCloneData.pos, Blocks.barrier.getDefaultState(), 2);
            }

            i = 0;

            for (CommandClone.StaticCloneData staticCloneData2 : (Iterable<CommandClone.StaticCloneData>) arrayList4) {
               if (entityWorld.setBlockState(staticCloneData2.pos, staticCloneData2.blockState, 2)) {
                  i++;
               }
            }

            for (CommandClone.StaticCloneData staticCloneData3 : (Iterable<CommandClone.StaticCloneData>) arrayList2) {
               TileEntity tileEntity4 = entityWorld.getTileEntity(staticCloneData3.pos);
               if (staticCloneData3.compound != null && tileEntity4 != null) {
                  staticCloneData3.compound.setInteger("x", staticCloneData3.pos.getX());
                  staticCloneData3.compound.setInteger("y", staticCloneData3.pos.getY());
                  staticCloneData3.compound.setInteger("z", staticCloneData3.pos.getZ());
                  tileEntity4.readFromNBT(staticCloneData3.compound);
                  tileEntity4.markDirty();
               }

               entityWorld.setBlockState(staticCloneData3.pos, staticCloneData3.blockState, 2);
            }

            for (CommandClone.StaticCloneData staticCloneData4 : (Iterable<CommandClone.StaticCloneData>) list) {
               entityWorld.notifyNeighborsRespectDebug(staticCloneData4.pos, staticCloneData4.blockState.getBlock());
            }

            List list2 = entityWorld.getList(structureBoundingBox);
            if (list2 != null) {
               for (NextTickListEntry nextTickListEntry : (Iterable<NextTickListEntry>) list2) {
                  if (structureBoundingBox.isVecInside(nextTickListEntry.position)) {
                     BlockPos blockPos9 = nextTickListEntry.position.add(blockPos4);
                     entityWorld.scheduleBlockUpdate(blockPos9, nextTickListEntry.getBlock(), (int)(nextTickListEntry.scheduledTime - entityWorld.getWorldInfo().getWorldTotalTime()), nextTickListEntry.priority);
                  }
               }
            }

            if (i <= 0) {
               throw new CommandException("commands.clone.failed");
            }

            sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, i);
            notifyOperators(sender, this, "commands.clone.success", i);
         } else {
            throw new CommandException("commands.clone.outOfWorld");
         }
      } else {
         throw new CommandException("commands.clone.outOfWorld");
      }
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length > 0 && args.length <= 3
         ? getCoordinateTabCompletion(args, 0, pos)
         : (
            args.length > 3 && args.length <= 6
               ? getCoordinateTabCompletion(args, 3, pos)
               : (
                  args.length > 6 && args.length <= 9
                     ? getCoordinateTabCompletion(args, 6, pos)
                     : (
                        args.length == 10
                           ? getListOfStringsMatchingLastWord(args, "replace", "masked", "filtered")
                           : (
                              args.length == 11
                                 ? getListOfStringsMatchingLastWord(args, "normal", "force", "move")
                                 : (
                                    args.length == 12 && "filtered".equals(args[9])
                                       ? getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys())
                                       : null
                                 )
                           )
                     )
               )
         );
   }

   @Override
   public String getCommandName() {
      return "clone";
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.clone.usage";
   }

   static class StaticCloneData {
      public final BlockPos pos;
      public final IBlockState blockState;
      public final NBTTagCompound compound;

      public StaticCloneData(BlockPos posIn, IBlockState stateIn, NBTTagCompound compoundIn) {
         this.pos = posIn;
         this.blockState = stateIn;
         this.compound = compoundIn;
      }
   }
}
