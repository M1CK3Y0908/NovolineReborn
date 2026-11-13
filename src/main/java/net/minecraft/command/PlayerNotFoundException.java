package net.minecraft.command;

public class PlayerNotFoundException extends CommandException {
   public PlayerNotFoundException(String message, Object... replacements) {
      super(message, replacements);
   }

   public PlayerNotFoundException() {
      this("commands.generic.player.notFound");
   }
}
