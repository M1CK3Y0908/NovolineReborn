package wtf.novoline.utils;

import java.util.ArrayList;
import java.util.List;

public class MacroManager {
   private final List<ChatMacro> macros = new ArrayList<>();
   private final MacroKeyHandler macroKeyHandler = new MacroKeyHandler(this);

   public MacroKeyHandler getMacroKeyHandler() {
      return this.macroKeyHandler;
   }

   public void addMacro(ChatMacro chatMacro) {
      this.macros.add(chatMacro);
   }

   public List<ChatMacro> getMacros() {
      return this.macros;
   }
}
