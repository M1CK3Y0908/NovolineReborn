package wtf.novoline.command;

import java.util.Objects;
import wtf.novoline.Novoline;
import wtf.novoline.utils.config.ConfigManager;
import wtf.novoline.utils.language.Language;

public abstract class Command {
   protected final Novoline novoline = Novoline.getNovoline();
   private final String description;
   private final String[] aliases;

   public abstract void execute(String[] texts);

   public String[] getAliases() {
      return this.aliases;
   }

   protected ConfigManager getConfigManager() {
      return this.novoline.getConfigRegistry().getConfigManager();
   }

   protected void sendLocalized(String text, Object... objects) {
      this.sendMessage(Language.translate(text, objects));
   }

   public String getDescription() {
      return this.description;
   }

   protected void sendMessage(String... texts) {
      Novoline.getNovoline().sendChatMessage(String.join(" ", texts));
   }

   public Command(String text, String... texts) {
      this.description = text;
      this.aliases = texts;
   }

   @Override
   public String toString() {
      Objects.requireNonNull(Novoline.getNovoline().getCommandManager());
      return "." + this.aliases[0] + " - " + this.description;
   }
}
