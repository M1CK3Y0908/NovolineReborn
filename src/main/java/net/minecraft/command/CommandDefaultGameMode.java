package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;

public class CommandDefaultGameMode extends CommandGameMode {
   @Override
   public String getCommandName() {
      return "defaultgamemode";
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.defaultgamemode.usage";
   }

   protected void setGameType(net.minecraft.world.WorldSettings.GameType gameMode) {
      MinecraftServer server2 = MinecraftServer.getServer();
      server2.setGameType(gameMode);
      if (server2.getForceGamemode()) {
         for (EntityPlayerMP entityPlayerMP : MinecraftServer.getServer().getConfigurationManager().getPlayerList()) {
            entityPlayerMP.setGameType(gameMode);
            entityPlayerMP.fallDistance = 0.0F;
         }
      }
   }

   private static CommandException createCommandException(CommandException commandException) {
      return commandException;
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length <= 0) {
         throw new WrongUsageException("commands.defaultgamemode.usage");
      }

      net.minecraft.world.WorldSettings.GameType worldsettings$gametype = this.getGameModeFromCommand(sender, args[0]);
      this.setGameType(worldsettings$gametype);
      notifyOperators(sender, this, "commands.defaultgamemode.success", new ChatComponentTranslation("gameMode." + worldsettings$gametype.getName()));
   }
}
