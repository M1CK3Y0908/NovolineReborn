package wtf.novoline.command;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import wtf.novoline.Novoline;
import wtf.novoline.utils.language.Language;

public final class CommandDispatcher {
   private final CommandManager commandManager;

   public CommandDispatcher(CommandManager commandManager2) {
      this.commandManager = commandManager2;
   }

   public void handle(String text) {
      Novoline novoline2 = Novoline.getNovoline();
      Map commandsByAlias = this.commandManager.getCommandsByAlias();
      String[] texts = text.replace("\\s{2,}", " ").substring(1).split(" ");
      String text2 = texts[0].toLowerCase(Locale.ROOT);
      if (!texts[0].isEmpty()) {
         Command command2 = (Command)commandsByAlias.get(text2);
         if (command2 != null) {
            try {
               command2.execute(texts);
            } catch (Throwable throwable) {
               novoline2.sendChatMessage(Language.translate("command.error"));
               Novoline.taggedLogger.error(throwable, "Unable to execute command {}", command2.getAliases()[0]);
            }

            return;
         }
      }

      String text3 = (String) commandsByAlias.keySet().stream().filter((string) -> ((String) string).startsWith(text2)).collect(Collectors.joining(", "));
      novoline2.sendChatMessage(Language.translate("command.wrong_command"));
      if (text3.length() > 0) {
         novoline2.sendChatMessage(Language.translate("command.similar_commands", text3));
      }
   }
}
