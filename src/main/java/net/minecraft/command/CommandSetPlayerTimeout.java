package net.minecraft.command;

import net.minecraft.server.MinecraftServer;

public class CommandSetPlayerTimeout extends CommandBase {
   private static CommandException createCommandException(CommandException commandException) {
      return commandException;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.setidletimeout.usage";
   }

   @Override
   public String getCommandName() {
      return "setidletimeout";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length != 1) {
         throw new WrongUsageException("commands.setidletimeout.usage");
      }

      int i = parseInt(args[0], 0);
      MinecraftServer.getServer().setPlayerIdleTimeout(i);
      notifyOperators(sender, this, "commands.setidletimeout.success", i);
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }
}
