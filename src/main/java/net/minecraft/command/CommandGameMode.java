package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldSettings;

public class CommandGameMode extends CommandBase {
   protected net.minecraft.world.WorldSettings.GameType getGameModeFromCommand(ICommandSender iCommandSender, String text) throws CommandException {
      return text.equalsIgnoreCase(net.minecraft.world.WorldSettings.GameType.SURVIVAL.getName()) || text.equalsIgnoreCase("s")
         ? net.minecraft.world.WorldSettings.GameType.SURVIVAL
         : (
            text.equalsIgnoreCase(net.minecraft.world.WorldSettings.GameType.CREATIVE.getName()) || text.equalsIgnoreCase("c")
               ? net.minecraft.world.WorldSettings.GameType.CREATIVE
               : (
                  text.equalsIgnoreCase(net.minecraft.world.WorldSettings.GameType.ADVENTURE.getName()) || text.equalsIgnoreCase("a")
                     ? net.minecraft.world.WorldSettings.GameType.ADVENTURE
                     : (
                        !text.equalsIgnoreCase(net.minecraft.world.WorldSettings.GameType.SPECTATOR.getName()) && !text.equalsIgnoreCase("sp")
                           ? WorldSettings.getGameTypeById(parseInt(text, 0, net.minecraft.world.WorldSettings.GameType.values().length - 2))
                           : net.minecraft.world.WorldSettings.GameType.SPECTATOR
                     )
               )
         );
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.gamemode.usage";
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 1;
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length <= 0) {
         throw new WrongUsageException("commands.gamemode.usage");
      }

      net.minecraft.world.WorldSettings.GameType worldsettings$gametype = this.getGameModeFromCommand(sender, args[0]);
      EntityPlayer entityPlayer = args.length >= 2 ? getPlayer(sender, args[1]) : getCommandSenderAsPlayer(sender);
      entityPlayer.setGameType(worldsettings$gametype);
      entityPlayer.fallDistance = 0.0F;
      if (sender.getEntityWorld().getGameRules().getBoolean("sendCommandFeedback")) {
         entityPlayer.addChatMessage(new ChatComponentTranslation("gameMode.changed"));
      }

      ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("gameMode." + worldsettings$gametype.getName());
      if (entityPlayer != sender) {
         notifyOperators(sender, this, 1, "commands.gamemode.success.other", entityPlayer.getName(), chatComponentTranslation);
      } else {
         notifyOperators(sender, this, 1, "commands.gamemode.success.self", chatComponentTranslation);
      }
   }

   @Override
   public String getCommandName() {
      return "gamemode";
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, "survival", "creative", "adventure", "spectator")
         : (args.length == 2 ? getListOfStringsMatchingLastWord(args, this.getListOfPlayerUsernames()) : null);
   }

   protected String[] getListOfPlayerUsernames() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }
}
