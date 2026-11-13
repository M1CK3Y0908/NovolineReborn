package wtf.novoline.command;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import wtf.novoline.Novoline;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.Module.PressType;
import wtf.novoline.utils.language.Language;

public class BindCommand extends Command {
   private static final Novoline clientInstance = Novoline.getNovoline();

   private int parseKeyCode(String text) {
      if (text.startsWith("MOUSE")) {
         String text2 = text.substring("MOUSE".length());
         return text2.isEmpty() ? 0 : -Integer.parseInt(text2) - 1;
      } else {
         return Keyboard.getKeyIndex(text);
      }
   }

   private String getKeyName(int i) {
      return i < 0 ? "MOUSE" + (Math.abs(i) - 1) : Keyboard.getKeyName(i);
   }

   public BindCommand() {
      super("set/unset feature hotkey", "bind", "b", "unbind");
   }

   @Override
   public void execute(String[] texts) {
      if (texts[0].equalsIgnoreCase("bind")) {
         if (texts.length == 2) {
            if (texts[1].equalsIgnoreCase("list")) {
               Map map2 = ModuleManager.modules
                  .stream()
                  .filter(module2 -> module2.getKeyBind() != 0)
                  .collect(Collectors.groupingBy(Module::getKeyBind, Collectors.toList()));
               List list2 = map2.entrySet()
                  .stream()
                  .map(
                     (entry) -> "§b"
                        + this.getKeyName((Integer)((Map.Entry) entry).getKey())
                        + "§7: "
                        + ((List)((Map.Entry) entry).getValue())
                           .stream()
                           .map((module2) -> ((Module) module2).getName() + " (" + ((Module) module2).getPressType().name().charAt(0) + ")")
                           .collect(Collectors.joining(", "))
                  )
                  .toList();
               if (list2.isEmpty()) {
                  this.sendLocalized("command.bind.bindlist_empty");
               } else {
                  list2.forEach(string -> this.sendMessage((String) string));
               }

               return;
            }

            if (texts[1].equalsIgnoreCase("clear")) {
               ModuleManager.modules.forEach(module2 -> module2.setKeyBind(0));
               this.sendLocalized("command.bind.cleared");
               return;
            }
         } else if (texts.length == 3 || texts.length == 4) {
            Module module3 = ModuleManager.modulesByName.get(texts[1].toLowerCase(Locale.ROOT));
            String text = texts[2].toUpperCase(Locale.ROOT);
            int i = this.parseKeyCode(text);
            boolean flag = text.equalsIgnoreCase("none");
            if (module3 == null) {
               this.sendLocalized("command.bind.wrong_feature_name");
               return;
            }

            if (i == 0 && !flag) {
               this.sendLocalized("command.bind.wrong_key");
               return;
            }

            if (texts.length == 4) {
               try {
                  PressType pressType = PressType.valueOf(texts[3].toUpperCase(Locale.ROOT));
                  module3.setPressType(pressType);
               } catch (IllegalArgumentException illegalArgumentException) {
                  this.sendLocalized("command.bind.invalid_press_type", texts[3]);
                  return;
               }
            } else {
               module3.setPressType(PressType.PRESS);
            }

            module3.setKeyBind(i);
            if (flag) {
               this.sendLocalized("command.bind.unbound", module3.getName());
            } else {
               String text2 = module3.getPressType().name().toLowerCase(Locale.ROOT);
               this.sendLocalized("command.bind.bound", module3.getName(), text, Language.translate("command.bind.press_type." + text2));
            }

            return;
         }
      } else if (texts[0].equalsIgnoreCase("unbind") && texts.length == 2) {
         Minecraft.getMinecraft().thePlayer.sendChatMessage(".bind " + texts[1] + " none");
         return;
      }

      this.sendLocalized("command.wrong_arguments");
      this.sendMessage("§c.bind <feature> <key> [toggle/hold]");
      this.sendMessage("§c.bind list");
      this.sendMessage("§c.bind clear");
      this.sendMessage("§c.unbind <feature>");
   }
}
