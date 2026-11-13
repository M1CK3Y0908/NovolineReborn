package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandTestFor extends CommandBase {
   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 1) {
         throw new WrongUsageException("commands.testfor.usage");
      }

      Entity entity = getEntity(sender, args[0]);
      NBTTagCompound nBTTagCompound = null;
      if (args.length >= 2) {
         try {
            nBTTagCompound = JsonToNBT.getTagFromJson(buildString(args, 1));
         } catch (NBTException nbtException) {
            throw new CommandException("commands.testfor.tagError", nbtException.getMessage());
         }
      }

      if (nBTTagCompound != null) {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         entity.writeToNBT(nbtTagCompound);
         if (!NBTUtil.areNBTEquals(nBTTagCompound, nbtTagCompound, true)) {
            throw new CommandException("commands.testfor.failure", entity.getName());
         }
      }

      notifyOperators(sender, this, "commands.testfor.success", entity.getName());
   }

   @Override
   public String getCommandName() {
      return "testfor";
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.testfor.usage";
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }
}
