package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandXP extends CommandBase {
   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.xp.usage";
   }

   protected String[] getAllUsernames() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length <= 0) {
         throw new WrongUsageException("commands.xp.usage");
      }

      String text = args[0];
      boolean flag = text.endsWith("l") || text.endsWith("L");
      if (flag && text.length() > 1) {
         text = text.substring(0, text.length() - 1);
      }

      int i = parseInt(text);
      boolean flag2 = i < 0;
      if (flag2) {
         i *= -1;
      }

      EntityPlayer entityPlayer = args.length > 1 ? getPlayer(sender, args[1]) : getCommandSenderAsPlayer(sender);
      if (flag) {
         sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, entityPlayer.experienceLevel);
         if (flag2) {
            entityPlayer.addExperienceLevel(-i);
            notifyOperators(sender, this, "commands.xp.success.negative.levels", i, entityPlayer.getName());
         } else {
            entityPlayer.addExperienceLevel(i);
            notifyOperators(sender, this, "commands.xp.success.levels", i, entityPlayer.getName());
         }
      } else {
         sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, entityPlayer.experienceTotal);
         if (flag2) {
            throw new CommandException("commands.xp.failure.widthdrawXp");
         }

         entityPlayer.addExperience(i);
         notifyOperators(sender, this, "commands.xp.success", i, entityPlayer.getName());
      }
   }

   @Override
   public String getCommandName() {
      return "xp";
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 1;
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 2 ? getListOfStringsMatchingLastWord(args, this.getAllUsernames()) : null;
   }
}
