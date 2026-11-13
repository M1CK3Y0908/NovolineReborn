package wtf.novoline.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager {
   public final String prefix = ".";
   private final List<Command> commands = new ArrayList<>();
   private final Map<String, Command> commandsByAlias = new HashMap<>();
   private final CommandDispatcher commandDispatcher = new CommandDispatcher(this);

   public CommandDispatcher getCommandDispatcher() {
      return this.commandDispatcher;
   }

   public CommandManager() {
      this.register(new ToggleCommand());
      this.register(new HelpCommand());
      this.register(new MacrosCommand());
      this.register(new BindCommand());
      this.register(new ConfigCommand());
      this.register(new ClipCommand());
      this.register(new FriendCommand());
      this.register(new PluginsCommand());
      this.register(new SettingCommand());
      this.register(new PanicCommand());
      this.register(new IrcCommand());
      this.register(new ChatCommand());
   }

   public Map<String, Command> getCommandsByAlias() {
      return this.commandsByAlias;
   }

   public void register(Command command2) {
      this.commands.add(command2);

      for (String text : command2.getAliases()) {
         this.commandsByAlias.put(text, command2);
      }
   }

   public List<Command> getCommands() {
      return this.commands;
   }
}
