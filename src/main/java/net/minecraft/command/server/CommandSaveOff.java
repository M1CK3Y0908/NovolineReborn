package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class CommandSaveOff extends CommandBase {
   @Override
   public String getCommandName() {
      return "save-off";
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.save-off.usage";
   }

   private static CommandException createCommandException(CommandException commandException) {
      return commandException;
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      MinecraftServer minecraftServer = MinecraftServer.getServer();
      boolean conditionMet = false;

      for (int index = 0; index < minecraftServer.worldServers.length; index++) {
         if (minecraftServer.worldServers[index] != null) {
            WorldServer worldServer = minecraftServer.worldServers[index];
            if (!worldServer.disableLevelSaving) {
               worldServer.disableLevelSaving = true;
               conditionMet = true;
            }
         }
      }

      if (conditionMet) {
         notifyOperators(sender, this, "commands.save.disabled");
      } else {
         throw new CommandException("commands.save-off.alreadyOff");
      }
   }
}
