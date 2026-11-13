package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class CommandListPlayers extends CommandBase {
   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      int currentPlayerCount = MinecraftServer.getServer().getCurrentPlayerCount();
      sender.addChatMessage(new ChatComponentTranslation("commands.players.list", currentPlayerCount, MinecraftServer.getServer().getMaxPlayers()));
      sender.addChatMessage(
         new ChatComponentText(MinecraftServer.getServer().getConfigurationManager().getPlayerListString(args.length > 0 && "uuids".equalsIgnoreCase(args[0])))
      );
      sender.setCommandStat(net.minecraft.command.CommandResultStats.Type.QUERY_RESULT, currentPlayerCount);
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 0;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.players.usage";
   }

   private static CommandException createCommandException(CommandException commandException) {
      return commandException;
   }

   @Override
   public String getCommandName() {
      return "list";
   }
}
