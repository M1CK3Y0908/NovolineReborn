package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandStop extends CommandBase {
   @Override
   public String getCommandName() {
      return "stop";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (MinecraftServer.getServer().worldServers != null) {
         notifyOperators(sender, this, "commands.stop.start");
      }

      MinecraftServer.getServer().initiateShutdown();
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.stop.usage";
   }

   private static CommandException createCommandException(CommandException commandException) {
      return commandException;
   }
}
