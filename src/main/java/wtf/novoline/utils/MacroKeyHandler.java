package wtf.novoline.utils;

public class MacroKeyHandler {
   private final MacroManager macroManager;

   public MacroKeyHandler(MacroManager macroManager2) {
      this.macroManager = macroManager2;
   }

   public void handleKeyPress(int i) {
      for (ChatMacro chatMacro : this.macroManager.getMacros()) {
         if (chatMacro.getKeyCode() == i) {
            chatMacro.send();
         }
      }
   }
}
