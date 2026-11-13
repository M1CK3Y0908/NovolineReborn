package net.minecraft.command;

public class NumberInvalidException extends CommandException {
   public NumberInvalidException(String message, Object... replacements) {
      super(message, replacements);
   }
}
