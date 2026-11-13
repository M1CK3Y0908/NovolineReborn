package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;

public class CommandSaveAll extends CommandBase {
   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.save.usage";
   }

   private static MinecraftException createMinecraftException(MinecraftException minecraftException) {
      return minecraftException;
   }

   @Override
   public String getCommandName() {
      return "save-all";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      MinecraftServer minecraftServer = MinecraftServer.getServer();
      sender.addChatMessage(new ChatComponentTranslation("commands.save.start"));
      if (minecraftServer.getConfigurationManager() != null) {
         minecraftServer.getConfigurationManager().saveAllPlayerData();
      }

      try {
         for (int index = 0; index < minecraftServer.worldServers.length; index++) {
            if (minecraftServer.worldServers[index] != null) {
               WorldServer worldServer = minecraftServer.worldServers[index];
               boolean conditionMet = worldServer.disableLevelSaving;
               worldServer.disableLevelSaving = false;
               worldServer.handleIProgressUpdate((IProgressUpdate)null);
               worldServer.disableLevelSaving = conditionMet;
            }
         }

         if (args.length > 0 && "flush".equals(args[0])) {
            sender.addChatMessage(new ChatComponentTranslation("commands.save.flushStart"));

            for (int innerIndex = 0; innerIndex < minecraftServer.worldServers.length; innerIndex++) {
               if (minecraftServer.worldServers[innerIndex] != null) {
                  WorldServer worldServer2 = minecraftServer.worldServers[innerIndex];
                  boolean conditionMet2 = worldServer2.disableLevelSaving;
                  worldServer2.disableLevelSaving = false;
                  worldServer2.saveChunkData();
                  worldServer2.disableLevelSaving = conditionMet2;
               }
            }

            sender.addChatMessage(new ChatComponentTranslation("commands.save.flushEnd"));
         }
      } catch (MinecraftException minecraftException) {
         notifyOperators(sender, this, "commands.save.failed", minecraftException.getMessage());
         return;
      }

      notifyOperators(sender, this, "commands.save.success");
   }
}
