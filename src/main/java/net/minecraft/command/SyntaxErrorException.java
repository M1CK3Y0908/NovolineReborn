package net.minecraft.command;

public class SyntaxErrorException extends CommandException {
   public SyntaxErrorException(String message, Object... replacements) {
      super(message, replacements);
   }
}
