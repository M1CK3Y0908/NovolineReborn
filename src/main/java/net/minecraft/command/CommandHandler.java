package net.minecraft.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandHandler implements ICommandManager {
   private final Map<String, ICommand> commandMap = Maps.newHashMap();
   private final Set<ICommand> commandSet = Sets.newHashSet();
   private static final Logger logger = LogManager.getLogger();

   @Override
   public List<String> getTabCompletionOptions(ICommandSender sender, String input, BlockPos pos) {
      String[] texts = input.split(" ", -1);
      String text = texts[0];
      if (texts.length == 1) {
         ArrayList arrayList = Lists.newArrayList();

         for (Entry entry : this.commandMap.entrySet()) {
            if (CommandBase.doesStringStartWith(text, (String)entry.getKey()) && ((ICommand)entry.getValue()).canCommandSenderUseCommand(sender)) {
               arrayList.add((String)entry.getKey());
            }
         }

         return arrayList;
      } else {
         if (texts.length > 1) {
            ICommand iCommand = this.commandMap.get(text);
            if (iCommand != null && iCommand.canCommandSenderUseCommand(sender)) {
               return iCommand.addTabCompletionOptions(sender, dropFirstString(texts), pos);
            }
         }

         return null;
      }
   }

   public ICommand registerCommand(ICommand command) {
      this.commandMap.put(command.getCommandName(), command);
      this.commandSet.add(command);

      for (String text : command.getCommandAliases()) {
         ICommand iCommand = this.commandMap.get(text);
         if (iCommand == null || !iCommand.getCommandName().equals(text)) {
            this.commandMap.put(text, command);
         }
      }

      return command;
   }

   private int getUsernameIndex(ICommand command, String[] args) {
      if (command == null) {
         return -1;
      }

      for (int index = 0; index < args.length; index++) {
         if (command.isUsernameIndex(args, index) && PlayerSelector.matchesMultiplePlayers(args[index])) {
            return index;
         }
      }

      return -1;
   }

   @Override
   public int executeCommand(ICommandSender sender, String rawCommand) {
      rawCommand = rawCommand.trim();
      if (rawCommand.startsWith("/")) {
         rawCommand = rawCommand.substring(1);
      }

      String[] texts = rawCommand.split(" ");
      String text = texts[0];
      texts = dropFirstString(texts);
      ICommand iCommand = this.commandMap.get(text);
      int usernameIndex = this.getUsernameIndex(iCommand, texts);
      int i = 0;
      if (iCommand == null) {
         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.generic.notFound");
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
         sender.addChatMessage(chatComponentTranslation);
      } else if (iCommand.canCommandSenderUseCommand(sender)) {
         if (usernameIndex > -1) {
            List list = PlayerSelector.matchEntities(sender, texts[usernameIndex], Entity.class);
            String text2 = texts[usernameIndex];
            sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, list.size());

            for (Entity entity2 : (Iterable<Entity>) list) {
               texts[usernameIndex] = entity2.getUniqueID().toString();
               if (this.tryExecute(sender, texts, iCommand, rawCommand)) {
                  i++;
               }
            }

            texts[usernameIndex] = text2;
         } else {
            sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, 1);
            if (this.tryExecute(sender, texts, iCommand, rawCommand)) {
               i++;
            }
         }
      } else {
         ChatComponentTranslation chatComponentTranslation2 = new ChatComponentTranslation("commands.generic.permission");
         chatComponentTranslation2.getChatStyle().setColor(EnumChatFormatting.RED);
         sender.addChatMessage(chatComponentTranslation2);
      }

      sender.setCommandStat(CommandResultStats.Type.SUCCESS_COUNT, i);
      return i;
   }

   @Override
   public List<ICommand> getPossibleCommands(ICommandSender sender) {
      ArrayList arrayList = Lists.newArrayList();

      for (ICommand iCommand : this.commandSet) {
         if (iCommand.canCommandSenderUseCommand(sender)) {
            arrayList.add(iCommand);
         }
      }

      return arrayList;
   }

   private static String[] dropFirstString(String[] input) {
      String[] stringArray = new String[input.length - 1];
      System.arraycopy(input, 1, stringArray, 0, input.length - 1);
      return stringArray;
   }

   protected boolean tryExecute(ICommandSender sender, String[] args, ICommand command, String input) {
      try {
         command.processCommand(sender, args);
         return true;
      } catch (WrongUsageException wrongUsageException) {
         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation(
            "commands.generic.usage", new ChatComponentTranslation(wrongUsageException.getMessage(), wrongUsageException.getErrorObjects())
         );
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
         sender.addChatMessage(chatComponentTranslation);
      } catch (CommandException commandException) {
         ChatComponentTranslation chatComponentTranslation2 = new ChatComponentTranslation(commandException.getMessage(), commandException.getErrorObjects());
         chatComponentTranslation2.getChatStyle().setColor(EnumChatFormatting.RED);
         sender.addChatMessage(chatComponentTranslation2);
      } catch (Throwable throwable) {
         ChatComponentTranslation chatComponentTranslation3 = new ChatComponentTranslation("commands.generic.exception");
         chatComponentTranslation3.getChatStyle().setColor(EnumChatFormatting.RED);
         sender.addChatMessage(chatComponentTranslation3);
         logger.warn("Couldn't process command: '" + input + "'");
      }

      return false;
   }

   @Override
   public Map<String, ICommand> getCommands() {
      return this.commandMap;
   }
}
