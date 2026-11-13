package net.minecraft.command;

import java.util.List;
import java.util.Map;
import net.minecraft.util.BlockPos;

public interface ICommandManager {
   Map<String, ICommand> getCommands();

   List<ICommand> getPossibleCommands(ICommandSender iCommandSender);

   int executeCommand(ICommandSender iCommandSender, String text);

   List<String> getTabCompletionOptions(ICommandSender iCommandSender, String text, BlockPos blockPos);
}
