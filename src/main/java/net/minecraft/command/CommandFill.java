package net.minecraft.command;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandFill extends CommandBase {
   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.fill.usage";
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length > 0 && args.length <= 3
         ? getCoordinateTabCompletion(args, 0, pos)
         : (
            args.length > 3 && args.length <= 6
               ? getCoordinateTabCompletion(args, 3, pos)
               : (
                  args.length == 7
                     ? getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys())
                     : (
                        args.length == 9
                           ? getListOfStringsMatchingLastWord(args, "replace", "destroy", "keep", "hollow", "outline")
                           : (args.length == 10 && "replace".equals(args[8]) ? getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys()) : null)
                     )
               )
         );
   }

   @Override
   public String getCommandName() {
      return "fill";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 7) {
         throw new WrongUsageException("commands.fill.usage");
      }

      sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
      BlockPos blockPos = parseBlockPos(sender, args, 0, false);
      BlockPos blockPos2 = parseBlockPos(sender, args, 3, false);
      Block blockByText = CommandBase.getBlockByText(sender, args[6]);
      int i = 0;
      if (args.length >= 8) {
         i = parseInt(args[7], 0, 15);
      }

      BlockPos blockPos3 = new BlockPos(Math.min(blockPos.getX(), blockPos2.getX()), Math.min(blockPos.getY(), blockPos2.getY()), Math.min(blockPos.getZ(), blockPos2.getZ()));
      BlockPos blockPos4 = new BlockPos(Math.max(blockPos.getX(), blockPos2.getX()), Math.max(blockPos.getY(), blockPos2.getY()), Math.max(blockPos.getZ(), blockPos2.getZ()));
      int i2 = (blockPos4.getX() - blockPos3.getX() + 1) * (blockPos4.getY() - blockPos3.getY() + 1) * (blockPos4.getZ() - blockPos3.getZ() + 1);
      if (i2 > 32768) {
         throw new CommandException("commands.fill.tooManyBlocks", i2, 32768);
      }

      if (blockPos3.getY() >= 0 && blockPos4.getY() < 256) {
         World entityWorld = sender.getEntityWorld();

         for (int z2 = blockPos3.getZ(); z2 < blockPos4.getZ() + 16; z2 += 16) {
            for (int x2 = blockPos3.getX(); x2 < blockPos4.getX() + 16; x2 += 16) {
               if (!entityWorld.isBlockLoaded(new BlockPos(x2, blockPos4.getY() - blockPos3.getY(), z2))) {
                  throw new CommandException("commands.fill.outOfWorld");
               }
            }
         }

         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         boolean flag = false;
         if (args.length >= 10 && blockByText.hasTileEntity()) {
            String unformattedText = getChatComponentFromNthArg(sender, args, 9).getUnformattedText();

            try {
               nbtTagCompound = JsonToNBT.getTagFromJson(unformattedText);
               flag = true;
            } catch (NBTException nbtException) {
               throw new CommandException("commands.fill.tagError", nbtException.getMessage());
            }
         }

         ArrayList arrayList = Lists.newArrayList();
         i2 = 0;

         for (int z3 = blockPos3.getZ(); z3 <= blockPos4.getZ(); z3++) {
            for (int y2 = blockPos3.getY(); y2 <= blockPos4.getY(); y2++) {
               for (int x3 = blockPos3.getX(); x3 <= blockPos4.getX(); x3++) {
                  BlockPos blockPos5 = new BlockPos(x3, y2, z3);
                  if (args.length >= 9) {
                     if (!args[8].equals("outline") && !args[8].equals("hollow")) {
                        if (args[8].equals("destroy")) {
                           entityWorld.destroyBlock(blockPos5, true);
                        } else if (args[8].equals("keep")) {
                           if (!entityWorld.isAirBlock(blockPos5)) {
                              continue;
                           }
                        } else if (args[8].equals("replace") && !blockByText.hasTileEntity()) {
                           if (args.length > 9) {
                              Block blockByText2 = CommandBase.getBlockByText(sender, args[9]);
                              if (entityWorld.getBlockState(blockPos5).getBlock() != blockByText2) {
                                 continue;
                              }
                           }

                           if (args.length > 10) {
                              int i3 = CommandBase.parseInt(args[10]);
                              IBlockState blockState = entityWorld.getBlockState(blockPos5);
                              if (blockState.getBlock().getMetaFromState(blockState) != i3) {
                                 continue;
                              }
                           }
                        }
                     } else if (x3 != blockPos3.getX()
                        && x3 != blockPos4.getX()
                        && y2 != blockPos3.getY()
                        && y2 != blockPos4.getY()
                        && z3 != blockPos3.getZ()
                        && z3 != blockPos4.getZ()) {
                        if (args[8].equals("hollow")) {
                           entityWorld.setBlockState(blockPos5, Blocks.air.getDefaultState(), 2);
                           arrayList.add(blockPos5);
                        }
                        continue;
                     }
                  }

                  TileEntity tileEntity = entityWorld.getTileEntity(blockPos5);
                  if (tileEntity != null) {
                     if (tileEntity instanceof IInventory) {
                        ((IInventory)tileEntity).clear();
                     }

                     entityWorld.setBlockState(blockPos5, Blocks.barrier.getDefaultState(), blockByText == Blocks.barrier ? 2 : 4);
                  }

                  IBlockState stateFromMeta = blockByText.getStateFromMeta(i);
                  if (entityWorld.setBlockState(blockPos5, stateFromMeta, 2)) {
                     arrayList.add(blockPos5);
                     i2++;
                     if (flag) {
                        TileEntity tileEntity2 = entityWorld.getTileEntity(blockPos5);
                        if (tileEntity2 != null) {
                           nbtTagCompound.setInteger("x", blockPos5.getX());
                           nbtTagCompound.setInteger("y", blockPos5.getY());
                           nbtTagCompound.setInteger("z", blockPos5.getZ());
                           tileEntity2.readFromNBT(nbtTagCompound);
                        }
                     }
                  }
               }
            }
         }

         for (BlockPos blockPos6 : (Iterable<BlockPos>) arrayList) {
            Block block2 = entityWorld.getBlockState(blockPos6).getBlock();
            entityWorld.notifyNeighborsRespectDebug(blockPos6, block2);
         }

         if (i2 <= 0) {
            throw new CommandException("commands.fill.failed");
         }

         sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, i2);
         notifyOperators(sender, this, "commands.fill.success", i2);
      } else {
         throw new CommandException("commands.fill.outOfWorld");
      }
   }
}
