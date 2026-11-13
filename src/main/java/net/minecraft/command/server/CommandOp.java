package net.minecraft.command.server;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandOp extends CommandBase {
   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length == 1 && args[0].length() > 0) {
         MinecraftServer minecraftServer = MinecraftServer.getServer();
         GameProfile gameProfile = minecraftServer.getPlayerProfileCache().getGameProfileForUsername(args[0]);
         if (gameProfile == null) {
            throw new CommandException("commands.op.failed", args[0]);
         }

         minecraftServer.getConfigurationManager().addOp(gameProfile);
         notifyOperators(sender, this, "commands.op.success", args[0]);
      } else {
         throw new WrongUsageException("commands.op.usage");
      }
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public String getCommandName() {
      return "op";
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.op.usage";
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      if (args.length == 1) {
         String text = args[args.length - 1];
         ArrayList arrayList = Lists.newArrayList();

         for (GameProfile gameProfile2 : MinecraftServer.getServer().getGameProfiles()) {
            if (!MinecraftServer.getServer().getConfigurationManager().canSendCommands(gameProfile2) && doesStringStartWith(text, gameProfile2.getName())) {
               arrayList.add(gameProfile2.getName());
            }
         }

         return arrayList;
      } else {
         return null;
      }
   }
}
