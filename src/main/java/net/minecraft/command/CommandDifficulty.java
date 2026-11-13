package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.EnumDifficulty;

public class CommandDifficulty extends CommandBase {
   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.difficulty.usage";
   }

   protected EnumDifficulty getDifficultyFromCommand(String text) throws CommandException {
      return text.equalsIgnoreCase("peaceful") || text.equalsIgnoreCase("p")
         ? EnumDifficulty.PEACEFUL
         : (
            text.equalsIgnoreCase("easy") || text.equalsIgnoreCase("e")
               ? EnumDifficulty.EASY
               : (
                  !text.equalsIgnoreCase("normal") && !text.equalsIgnoreCase("n")
                     ? (
                        !text.equalsIgnoreCase("hard") && !text.equalsIgnoreCase("h")
                           ? EnumDifficulty.getDifficultyEnum(parseInt(text, 0, 3))
                           : EnumDifficulty.HARD
                     )
                     : EnumDifficulty.NORMAL
               )
         );
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length <= 0) {
         throw new WrongUsageException("commands.difficulty.usage");
      }

      EnumDifficulty difficulty = this.getDifficultyFromCommand(args[0]);
      MinecraftServer.getServer().setDifficultyForAllWorlds(difficulty);
      notifyOperators(sender, this, "commands.difficulty.success", new ChatComponentTranslation(difficulty.getDifficultyResourceKey()));
   }

   @Override
   public String getCommandName() {
      return "difficulty";
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1 ? getListOfStringsMatchingLastWord(args, "peaceful", "easy", "normal", "hard") : null;
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   private static CommandException createCommandException(CommandException commandException) {
      return commandException;
   }
}
