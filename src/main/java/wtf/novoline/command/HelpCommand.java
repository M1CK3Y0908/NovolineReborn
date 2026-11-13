package wtf.novoline.command;

import wtf.novoline.Novoline;

public class HelpCommand extends Command {
   @Override
   public void execute(String[] texts) {
      Novoline.getNovoline().getCommandManager().getCommands().forEach(command2 -> Novoline.getNovoline().sendChatMessage(command2.toString()));
   }

   public HelpCommand() {
      super("displays information about commands", "help", "h");
   }
}
