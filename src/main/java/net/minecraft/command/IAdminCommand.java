package net.minecraft.command;

public interface IAdminCommand {
   void notifyOperators(ICommandSender iCommandSender, ICommand iCommand, int i, String text, Object... objects);
}
