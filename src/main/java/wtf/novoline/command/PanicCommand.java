package wtf.novoline.command;

import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;

public class PanicCommand extends Command {
   @Override
   public void execute(String[] texts) {
      ModuleManager.modules.forEach(Module::disable);
   }

   public PanicCommand() {
      super("Disables all features", "panic", "p");
   }
}
