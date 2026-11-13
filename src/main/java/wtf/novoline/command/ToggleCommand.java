package wtf.novoline.command;

import java.util.Locale;
import wtf.novoline.Novoline;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.utils.language.Language;

public class ToggleCommand extends Command {
   private void printUsage() {
      Novoline.getNovoline().sendChatMessage(Language.translate("command.wrong_arguments"));
      Novoline.getNovoline().sendChatMessage("§c.toggle <feature>");
      Novoline.getNovoline().sendChatMessage("§c.toggle <feature> <on/off>");
   }

   private Module getModule(String text) {
      return ModuleManager.modulesByName.get(text);
   }

   @Override
   public void execute(String[] texts) {
      if (texts.length < 2) {
         this.printUsage();
      } else {
         String text = texts[1];
         Module module2 = this.getModule(text.toLowerCase(Locale.ROOT));
         if (module2 != null) {
            text = module2.getName();
            switch (texts.length) {
               case 2:
                  module2.toggle();
                  Novoline.getNovoline()
                     .sendChatMessage(
                        String.format(module2.isEnabled() ? Language.translate("command.toggle.on") : Language.translate("command.toggle.off"), text)
                     );
                  break;
               case 3:
                  String text2 = texts[2].toLowerCase();
                  int i = text2.equals("off") ? 0 : (text2.equals("on") ? 1 : 2);
                  if (i != 2) {
                     module2.setEnabled(i != 0);
                     Novoline.getNovoline()
                        .sendChatMessage(
                           String.format(module2.isEnabled() ? Language.translate("command.toggle.on") : Language.translate("command.toggle.off"), text)
                        );
                     break;
                  }
               default:
                  this.printUsage();
            }
         } else {
            Novoline.getNovoline().sendChatMessage(String.format(Language.translate("command.toggle.not_found"), text));
         }
      }
   }

   public ToggleCommand() {
      super("enables/disables feature", "toggle", "t");
   }
}
