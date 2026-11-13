package wtf.novoline.command;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import wtf.novoline.Novoline;
import wtf.novoline.module.misc.AbstractMessageModule;

public class MessagesCommand extends Command {
   private final AbstractMessageModule abstractMessageModule;

   public MessagesCommand(AbstractMessageModule abstractMessageModule2, String text, String... texts) {
      super(text, texts);
      this.abstractMessageModule = abstractMessageModule2;
   }

   public void printUsage() {
      this.sendLocalized("command.gen_messages.usage_ph");
      this.sendLocalized("command.gen_messages.usage_ph_1");
      this.sendLocalized("command.gen_messages.usage_ph_2");
      this.sendLocalized("command.gen_messages.usage_ph_3");
      this.sendLocalized("command.gen_messages.usage_ph_4");
   }

   @Override
   public void execute(String[] texts) {
      String text = this.getAliases()[0];
      List messages = this.abstractMessageModule.getMessages();
      if (texts.length > 1) {
         switch (texts[1]) {
            case "list":
               this.sendLocalized("command.gen_messages.list");

               for (int i = 0; i < messages.size(); i++) {
                  this.sendLocalized("command.gen_messages.list_entry", i, messages.get(i));
               }

               return;
            case "clear":
               messages.clear();
               this.sendLocalized("command.gen_messages.cleared");
               return;
            case "browse":
               try {
                  Path path = this.abstractMessageModule.getPath();
                  File file2 = path.getParent().toFile();
                  Desktop.getDesktop().open(file2);
               } catch (IOException ioException) {
                  Novoline.taggedLogger.error(ioException, "Couldn't open folder");
               }

               return;
            case "add":
               if (texts.length >= 3) {
                  String[] texts2 = Arrays.copyOfRange(texts, 2, texts.length);
                  String text2 = String.join(" ", texts2);
                  messages.add(text2);
                  this.sendLocalized("command.gen_messages.added");
                  return;
               }
               break;
            case "remove":
               if (texts.length == 3) {
                  int i2 = Integer.parseInt(texts[2]);
                  if (i2 >= 0 && i2 < messages.size()) {
                     messages.remove(i2);
                     this.sendLocalized("command.gen_messages.removed");
                  } else {
                     this.sendLocalized("command.gen_messages.remove_out_of_bounds", text);
                  }

                  return;
               }
         }
      }

      this.sendLocalized("command.wrong_arguments");
      this.sendLocalized("command.gen_messages.usage_1", text);
      this.sendLocalized("command.gen_messages.usage_2", text);
      this.sendLocalized("command.gen_messages.usage_3", text);
      this.sendLocalized("command.gen_messages.usage_4", text);
      this.sendLocalized("command.gen_messages.usage_5", text);
      this.printUsage();
   }
}
