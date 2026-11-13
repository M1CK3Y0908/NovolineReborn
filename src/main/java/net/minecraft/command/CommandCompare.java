package net.minecraft.command;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class CommandCompare extends CommandBase {
   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandName() {
      return "testforblocks";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 9) {
         throw new WrongUsageException("commands.compare.usage");
      }

      sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
      BlockPos blockPos = parseBlockPos(sender, args, 0, false);
      BlockPos blockPos2 = parseBlockPos(sender, args, 3, false);
      BlockPos blockPos3 = parseBlockPos(sender, args, 6, false);
      StructureBoundingBox structureBoundingBox = new StructureBoundingBox(blockPos, blockPos2);
      StructureBoundingBox structureBoundingBox2 = new StructureBoundingBox(blockPos3, blockPos3.add(structureBoundingBox.getLength()));
      int i = structureBoundingBox.getXSize() * structureBoundingBox.getYSize() * structureBoundingBox.getZSize();
      if (i > 524288) {
         throw new CommandException("commands.compare.tooManyBlocks", i, 524288);
      }

      if (structureBoundingBox.minY >= 0 && structureBoundingBox.maxY < 256 && structureBoundingBox2.minY >= 0 && structureBoundingBox2.maxY < 256) {
         World entityWorld = sender.getEntityWorld();
         if (entityWorld.isAreaLoaded(structureBoundingBox) && entityWorld.isAreaLoaded(structureBoundingBox2)) {
            boolean flag = false;
            if (args.length > 9 && args[9].equals("masked")) {
               flag = true;
            }

            i = 0;
            BlockPos blockPos4 = new BlockPos(structureBoundingBox2.minX - structureBoundingBox.minX, structureBoundingBox2.minY - structureBoundingBox.minY, structureBoundingBox2.minZ - structureBoundingBox.minZ);
            net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();
            net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos2 = new net.minecraft.util.BlockPos.MutableBlockPos();

            for (int minZ2 = structureBoundingBox.minZ; minZ2 <= structureBoundingBox.maxZ; minZ2++) {
               for (int minY2 = structureBoundingBox.minY; minY2 <= structureBoundingBox.maxY; minY2++) {
                  for (int minX2 = structureBoundingBox.minX; minX2 <= structureBoundingBox.maxX; minX2++) {
                     mutableBlockPos.set(minX2, minY2, minZ2);
                     mutableBlockPos2.set(minX2 + blockPos4.getX(), minY2 + blockPos4.getY(), minZ2 + blockPos4.getZ());
                     boolean flag2 = false;
                     IBlockState blockState = entityWorld.getBlockState(mutableBlockPos);
                     if (!flag || blockState.getBlock() != Blocks.air) {
                        if (blockState == entityWorld.getBlockState(mutableBlockPos2)) {
                           TileEntity tileEntity = entityWorld.getTileEntity(mutableBlockPos);
                           TileEntity tileEntity2 = entityWorld.getTileEntity(mutableBlockPos2);
                           if (tileEntity != null && tileEntity2 != null) {
                              NBTTagCompound nbtTagCompound = new NBTTagCompound();
                              tileEntity.writeToNBT(nbtTagCompound);
                              nbtTagCompound.removeTag("x");
                              nbtTagCompound.removeTag("y");
                              nbtTagCompound.removeTag("z");
                              NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
                              tileEntity2.writeToNBT(nbtTagCompound2);
                              nbtTagCompound2.removeTag("x");
                              nbtTagCompound2.removeTag("y");
                              nbtTagCompound2.removeTag("z");
                              if (!nbtTagCompound.equals(nbtTagCompound2)) {
                                 flag2 = true;
                              }
                           } else if (tileEntity != null) {
                              flag2 = true;
                           }
                        } else {
                           flag2 = true;
                        }

                        i++;
                        if (flag2) {
                           throw new CommandException("commands.compare.failed");
                        }
                     }
                  }
               }
            }

            sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, i);
            notifyOperators(sender, this, "commands.compare.success", i);
         } else {
            throw new CommandException("commands.compare.outOfWorld");
         }
      } else {
         throw new CommandException("commands.compare.outOfWorld");
      }
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
                     : (args.length == 10 ? getListOfStringsMatchingLastWord(args, "masked", "all") : null)
               )
         );
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.compare.usage";
   }
}
