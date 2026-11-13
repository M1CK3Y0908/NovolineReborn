package net.minecraft.command;

public class EntityNotFoundException extends CommandException {
   public EntityNotFoundException(String text, Object... objects) {
      super(text, objects);
   }

   public EntityNotFoundException() {
      this("commands.generic.entity.notFound");
   }
}
