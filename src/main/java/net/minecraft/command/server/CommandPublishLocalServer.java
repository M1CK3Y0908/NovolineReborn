package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandPublishLocalServer extends CommandBase {
   private static CommandException createCommandException(CommandException commandException) {
      return commandException;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.publish.usage";
   }

   @Override
   public String getCommandName() {
      return "publish";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      String text = MinecraftServer.getServer().shareToLAN(net.minecraft.world.WorldSettings.GameType.SURVIVAL, false);
      if (text != null) {
         notifyOperators(sender, this, "commands.publish.started", text);
      } else {
         notifyOperators(sender, this, "commands.publish.failed");
      }
   }
}
