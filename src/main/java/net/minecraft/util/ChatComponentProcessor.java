package net.minecraft.util;

import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.Entity;

public class ChatComponentProcessor {
   private static CommandException createCommandException(CommandException commandException) {
      return commandException;
   }

   public static IChatComponent processComponent(ICommandSender commandSender, IChatComponent component, Entity entityIn) throws CommandException {
      IChatComponent iChatComponent;
      if (component instanceof ChatComponentScore chatComponentScore) {
         String name = chatComponentScore.getName();
         if (PlayerSelector.hasArguments(name)) {
            List list = PlayerSelector.matchEntities(commandSender, name, Entity.class);
            if (list.size() != 1) {
               throw new EntityNotFoundException();
            }

            name = ((Entity)list.get(0)).getName();
         }

         iChatComponent = entityIn != null && name.equals("*")
            ? new ChatComponentScore(entityIn.getName(), chatComponentScore.getObjective())
            : new ChatComponentScore(name, chatComponentScore.getObjective());
         ((ChatComponentScore)iChatComponent).setValue(chatComponentScore.getUnformattedTextForChat());
      } else if (component instanceof ChatComponentSelector) {
         String selector = ((ChatComponentSelector)component).getSelector();
         iChatComponent = PlayerSelector.matchEntitiesToChatComponent(commandSender, selector);
         if (iChatComponent == null) {
            iChatComponent = new ChatComponentText("");
         }
      } else if (component instanceof ChatComponentText) {
         iChatComponent = new ChatComponentText(((ChatComponentText)component).getText());
      } else {
         if (!(component instanceof ChatComponentTranslation)) {
            return component;
         }

         Object[] formatArgs = ((ChatComponentTranslation)component).getFormatArgs();

         for (int i = 0; i < formatArgs.length; i++) {
            Object object = formatArgs[i];
            if (object instanceof IChatComponent) {
               formatArgs[i] = processComponent(commandSender, (IChatComponent)object, entityIn);
            }
         }

         iChatComponent = new ChatComponentTranslation(((ChatComponentTranslation)component).getKey(), formatArgs);
      }

      ChatStyle chatStyle = component.getChatStyle();
      if (chatStyle != null) {
         iChatComponent.setChatStyle(chatStyle.createShallowCopy());
      }

      for (IChatComponent iChatComponent2 : component.getSiblings()) {
         iChatComponent.appendSibling(processComponent(commandSender, iChatComponent2, entityIn));
      }

      return iChatComponent;
   }
}
