package net.minecraft.command;

import java.util.List;
import net.minecraft.util.BlockPos;

public interface ICommand extends Comparable<ICommand> {
   boolean canCommandSenderUseCommand(ICommandSender iCommandSender);

   void processCommand(ICommandSender iCommandSender, String[] texts) throws CommandException;

   boolean isUsernameIndex(String[] texts, int i);

   String getCommandName();

   List<String> getCommandAliases();

   String getCommandUsage(ICommandSender iCommandSender);

   List<String> addTabCompletionOptions(ICommandSender iCommandSender, String[] texts, BlockPos blockPos);
}
