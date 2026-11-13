package net.minecraft.command;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandStats extends CommandBase {
   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 1) {
         throw new WrongUsageException("commands.stats.usage");
      }

      boolean flag;
      if (args[0].equals("entity")) {
         flag = false;
      } else {
         if (!args[0].equals("block")) {
            throw new WrongUsageException("commands.stats.usage");
         }

         flag = true;
      }

      int i;
      if (flag) {
         if (args.length < 5) {
            throw new WrongUsageException("commands.stats.block.usage");
         }

         i = 4;
      } else {
         if (args.length < 3) {
            throw new WrongUsageException("commands.stats.entity.usage");
         }

         i = 2;
      }

      String text = args[i++];
      if ("set".equals(text)) {
         if (args.length < i + 3) {
            if (i == 5) {
               throw new WrongUsageException("commands.stats.block.set.usage");
            }

            throw new WrongUsageException("commands.stats.entity.set.usage");
         }
      } else {
         if (!"clear".equals(text)) {
            throw new WrongUsageException("commands.stats.usage");
         }

         if (args.length < i + 1) {
            if (i == 5) {
               throw new WrongUsageException("commands.stats.block.clear.usage");
            }

            throw new WrongUsageException("commands.stats.entity.clear.usage");
         }
      }

      CommandResultStats.Type typeByName = CommandResultStats.Type.getTypeByName(args[i++]);
      if (typeByName == null) {
         throw new CommandException("commands.stats.failed");
      }

      World entityWorld = sender.getEntityWorld();
      CommandResultStats commandResultStats;
      if (flag) {
         BlockPos blockPos = parseBlockPos(sender, args, 1, false);
         TileEntity tileEntity = entityWorld.getTileEntity(blockPos);
         if (tileEntity == null) {
            throw new CommandException("commands.stats.noCompatibleBlock", blockPos.getX(), blockPos.getY(), blockPos.getZ());
         }

         if (tileEntity instanceof TileEntityCommandBlock) {
            commandResultStats = ((TileEntityCommandBlock)tileEntity).getCommandResultStats();
         } else {
            if (!(tileEntity instanceof TileEntitySign)) {
               throw new CommandException("commands.stats.noCompatibleBlock", blockPos.getX(), blockPos.getY(), blockPos.getZ());
            }

            commandResultStats = ((TileEntitySign)tileEntity).getStats();
         }
      } else {
         Entity entity2 = getEntity(sender, args[1]);
         commandResultStats = entity2.getCommandStats();
      }

      if ("set".equals(text)) {
         String text2 = args[i++];
         String text3 = args[i];
         if (text2.length() == 0 || text3.length() == 0) {
            throw new CommandException("commands.stats.failed");
         }

         CommandResultStats.setScoreBoardStat(commandResultStats, typeByName, text2, text3);
         notifyOperators(sender, this, "commands.stats.success", typeByName.getTypeName(), text3, text2);
      } else if ("clear".equals(text)) {
         CommandResultStats.setScoreBoardStat(commandResultStats, typeByName, (String)null, (String)null);
         notifyOperators(sender, this, "commands.stats.cleared", typeByName.getTypeName());
      }

      if (flag) {
         BlockPos blockPos2 = parseBlockPos(sender, args, 1, false);
         TileEntity tileEntity2 = entityWorld.getTileEntity(blockPos2);
         tileEntity2.markDirty();
      }
   }

   protected List<String> getObjectiveNames() {
      Collection scoreObjectives = MinecraftServer.getServer().worldServerForDimension(0).getScoreboard().getScoreObjectives();
      ArrayList arrayList = Lists.newArrayList();

      for (ScoreObjective scoreObjective : (Iterable<ScoreObjective>) scoreObjectives) {
         if (!scoreObjective.getCriteria().isReadOnly()) {
            arrayList.add(scoreObjective.getName());
         }
      }

      return arrayList;
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, "entity", "block")
         : (
            args.length == 2 && args[0].equals("entity")
               ? getListOfStringsMatchingLastWord(args, this.getEntityNames())
               : (
                  args.length >= 2 && args.length <= 4 && args[0].equals("block")
                     ? getCoordinateTabCompletion(args, 1, pos)
                     : (
                        (args.length != 3 || !args[0].equals("entity")) && (args.length != 5 || !args[0].equals("block"))
                           ? (
                              (args.length != 4 || !args[0].equals("entity")) && (args.length != 6 || !args[0].equals("block"))
                                 ? (
                                    args.length == 6 && args[0].equals("entity") || args.length == 8 && args[0].equals("block")
                                       ? getListOfStringsMatchingLastWord(args, this.getObjectiveNames())
                                       : null
                                 )
                                 : getListOfStringsMatchingLastWord(args, CommandResultStats.Type.getTypeNames())
                           )
                           : getListOfStringsMatchingLastWord(args, "set", "clear")
                     )
               )
         );
   }

   @Override
   public String getCommandName() {
      return "stats";
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.stats.usage";
   }

   protected String[] getEntityNames() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return args.length > 0 && args[0].equals("entity") && index == 1;
   }
}
