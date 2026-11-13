package net.minecraft.command;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandTrigger extends CommandBase {
   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 3) {
         throw new WrongUsageException("commands.trigger.usage");
      } else if (!(sender instanceof EntityPlayerMP) && !(sender.getCommandSenderEntity() instanceof EntityPlayerMP)) {
         throw new CommandException("commands.trigger.invalidPlayer");
      } else {
         EntityPlayerMP entityPlayerMP = sender instanceof EntityPlayerMP ? (EntityPlayerMP)sender : (EntityPlayerMP)sender.getCommandSenderEntity();
         Scoreboard scoreboard2 = MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();
         ScoreObjective objective = scoreboard2.getObjective(args[0]);
         if (objective != null && objective.getCriteria() == IScoreObjectiveCriteria.TRIGGER) {
            int i = parseInt(args[2]);
            if (!scoreboard2.entityHasObjective(entityPlayerMP.getName(), objective)) {
               throw new CommandException("commands.trigger.invalidObjective", args[0]);
            }

            Score valueFromObjective = scoreboard2.getValueFromObjective(entityPlayerMP.getName(), objective);
            if (valueFromObjective.isLocked()) {
               throw new CommandException("commands.trigger.disabled", args[0]);
            }

            if ("set".equals(args[1])) {
               valueFromObjective.setScorePoints(i);
            } else {
               if (!"add".equals(args[1])) {
                  throw new CommandException("commands.trigger.invalidMode", args[1]);
               }

               valueFromObjective.increseScore(i);
            }

            valueFromObjective.setLocked(true);
            if (entityPlayerMP.theItemInWorldManager.isCreative()) {
               notifyOperators(sender, this, "commands.trigger.success", args[0], args[1], args[2]);
            }
         } else {
            throw new CommandException("commands.trigger.invalidObjective", args[0]);
         }
      }
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 0;
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      if (args.length == 1) {
         Scoreboard scoreboard2 = MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();
         ArrayList arrayList = Lists.newArrayList();

         for (ScoreObjective scoreObjective : scoreboard2.getScoreObjectives()) {
            if (scoreObjective.getCriteria() == IScoreObjectiveCriteria.TRIGGER) {
               arrayList.add(scoreObjective.getName());
            }
         }

         return getListOfStringsMatchingLastWord(args, (String[]) arrayList.toArray(new String[arrayList.size()]));
      } else {
         return args.length == 2 ? getListOfStringsMatchingLastWord(args, "add", "set") : null;
      }
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.trigger.usage";
   }

   @Override
   public String getCommandName() {
      return "trigger";
   }
}
