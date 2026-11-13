package net.minecraft.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

public class CommandHelp extends CommandBase {
   @Override
   public String getCommandName() {
      return "help";
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.help.usage";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 0;
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      List sortedPossibleCommands = this.getSortedPossibleCommands(sender);
      int i = (sortedPossibleCommands.size() - 1) / 7;

      int i2;
      try {
         i2 = args.length == 0 ? 0 : parseInt(args[0], 1, i + 1) - 1;
      } catch (NumberInvalidException numberInvalidException) {
         Map commands2 = this.getCommands();
         ICommand iCommand = (ICommand)commands2.get(args[0]);
         if (iCommand != null) {
            throw new WrongUsageException(iCommand.getCommandUsage(sender));
         }

         if (MathHelper.parseIntWithDefault(args[0], -1) != -1) {
            throw numberInvalidException;
         }

         throw new CommandNotFoundException();
      }

      int i3 = Math.min((i2 + 1) * 7, sortedPossibleCommands.size());
      ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.help.header", i2 + 1, i + 1);
      chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
      sender.addChatMessage(chatComponentTranslation);

      for (int i4 = i2 * 7; i4 < i3; i4++) {
         ICommand iCommand2 = (ICommand)sortedPossibleCommands.get(i4);
         ChatComponentTranslation chatComponentTranslation2 = new ChatComponentTranslation(iCommand2.getCommandUsage(sender));
         chatComponentTranslation2.getChatStyle().setChatClickEvent(new ClickEvent(net.minecraft.event.ClickEvent.Action.SUGGEST_COMMAND, "/" + iCommand2.getCommandName() + " "));
         sender.addChatMessage(chatComponentTranslation2);
      }

      if (i2 == 0 && sender instanceof EntityPlayer) {
         ChatComponentTranslation chatComponentTranslation3 = new ChatComponentTranslation("commands.help.footer");
         chatComponentTranslation3.getChatStyle().setColor(EnumChatFormatting.GREEN);
         sender.addChatMessage(chatComponentTranslation3);
      }
   }

   private static NumberInvalidException createNumberInvalidException(NumberInvalidException numberInvalidException) {
      return numberInvalidException;
   }

   @Override
   public List<String> getCommandAliases() {
      return Arrays.asList("?");
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      if (args.length == 1) {
         Set<String> set = this.getCommands().keySet();
         return getListOfStringsMatchingLastWord(args, set.toArray(new String[set.size()]));
      } else {
         return null;
      }
   }

   protected List<ICommand> getSortedPossibleCommands(ICommandSender iCommandSender) {
      List<ICommand> list = MinecraftServer.getServer().getCommandManager().getPossibleCommands(iCommandSender);
      Collections.sort(list);
      return list;
   }

   protected Map<String, ICommand> getCommands() {
      return MinecraftServer.getServer().getCommandManager().getCommands();
   }
}
