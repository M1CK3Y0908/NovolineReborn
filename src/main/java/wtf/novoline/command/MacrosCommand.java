package wtf.novoline.command;

import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;
import org.lwjgl.input.Keyboard;
import wtf.novoline.Novoline;
import wtf.novoline.utils.ChatMacro;
import wtf.novoline.utils.MacroManager;
import wtf.novoline.utils.language.Language;

public class MacrosCommand extends Command {
   private final Novoline clientInstance = Novoline.getNovoline();

   public MacrosCommand() {
      super("create/delete chat macros", "macros");
   }

   @Override
   public void execute(String[] texts) {
      MacroManager macroManager = this.clientInstance.getMacroManager();
      if (texts.length == 2) {
         if (texts[1].equalsIgnoreCase("list")) {
            List macros2 = macroManager.getMacros();
            if (macros2.isEmpty()) {
               this.clientInstance.sendChatMessage("Macros list is empty!");
            } else {
               this.clientInstance.sendChatMessage("Macros count: " + macros2.size());

               for (ChatMacro chatMacro2 : (Iterable<ChatMacro>) macros2) {
                  this.clientInstance
                     .sendChatMessage(
                        String.format(
                           "§3%s §b[%s]§7: %s", chatMacro2.getName(), Keyboard.getKeyName(chatMacro2.getKeyCode()).toUpperCase(Locale.ROOT), chatMacro2.getMessage()
                        )
                     );
               }
            }

            return;
         }

         if (texts[1].equalsIgnoreCase("clear")) {
            macroManager.getMacros().clear();
            this.clientInstance.sendChatMessage("Macros list was cleaned!");
            return;
         }
      } else {
         if (texts.length == 3 && texts[1].equalsIgnoreCase("remove")) {
            ChatMacro chatMacro3 = macroManager.getMacros().stream().filter(chatMacro -> chatMacro.getName().equalsIgnoreCase(texts[2])).findFirst().orElse(null);
            if (chatMacro3 != null) {
               macroManager.getMacros().remove(chatMacro3);
               this.clientInstance.sendChatMessage(String.format("Macros §b%s§7 was removed!", chatMacro3.getName()));
            } else {
               this.clientInstance.sendChatMessage(String.format("§7Macros with name '%s' not found!", texts[2]));
            }

            return;
         }

         if (texts.length >= 5 && texts[1].equalsIgnoreCase("add")) {
            String text = texts[2];

            int i;
            try {
               i = Integer.parseInt(texts[3]);
            } catch (Exception exception) {
               i = Keyboard.getKeyIndex(texts[3].toUpperCase());
            }

            if (i == 0) {
               this.clientInstance.sendChatMessage("§cInvalid key: '" + texts[3] + "'");
               return;
            }

            ChatMacro chatMacro4 = macroManager.getMacros().stream().filter(chatMacro -> chatMacro.getName().equalsIgnoreCase(text)).findFirst().orElse(null);
            if (chatMacro4 == null) {
               StringJoiner stringJoiner = new StringJoiner(" ");
               stringJoiner.add(texts[4]);

               for (int i2 = 5; i2 < texts.length; i2++) {
                  stringJoiner.add(texts[i2]);
               }

               macroManager.addMacro(new ChatMacro(text, i, stringJoiner.toString()));
               this.clientInstance.sendChatMessage("Macros was added!");
            } else {
               this.clientInstance.sendChatMessage(String.format("Macros with name '§b%s§7' already exists!", text));
            }

            return;
         }
      }

      this.clientInstance.sendChatMessage(Language.translate("command.wrong_arguments"));
      this.clientInstance.sendChatMessage("§c.macros add <name> <key> <chat msg>");
      this.clientInstance.sendChatMessage("§c.macros remove <name>");
      this.clientInstance.sendChatMessage("§c.macros list");
   }
}
