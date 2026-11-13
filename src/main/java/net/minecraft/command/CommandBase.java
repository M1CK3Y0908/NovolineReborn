package net.minecraft.command;

import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

public abstract class CommandBase implements ICommand {
   private static IAdminCommand theAdmin;
   private static Entity[] entityArray;

   public static double parseDouble(String input, double min) throws NumberInvalidException {
      return parseDouble(input, min, Double.MAX_VALUE);
   }

   public static double parseDouble(double base, String input, int min, int max, boolean centerBlock) throws NumberInvalidException {
      boolean conditionMet = input.startsWith("~");
      if (conditionMet && Double.isNaN(base)) {
         throw new NumberInvalidException("commands.generic.num.invalid", base);
      }

      double d = conditionMet ? base : 0.0;
      if (!conditionMet || input.length() > 1) {
         boolean conditionMet2 = input.contains(".");
         if (conditionMet) {
            input = input.substring(1);
         }

         d += parseDouble(input);
         if (!conditionMet2 && !conditionMet && centerBlock) {
            d += 0.5;
         }
      }

      if (min != 0 || max != 0) {
         if (d < min) {
            throw new NumberInvalidException("commands.generic.double.tooSmall", d, min);
         }

         if (d > max) {
            throw new NumberInvalidException("commands.generic.double.tooBig", d, max);
         }
      }

      return d;
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return false;
   }

   public static IChatComponent getChatComponentFromNthArg(ICommandSender sender, String[] args, int index) throws CommandException {
      return getChatComponentFromNthArg(sender, args, index, false);
   }

   public static String getPlayerName(ICommandSender sender, String query) throws PlayerNotFoundException {
      try {
         return getPlayer(sender, query).getName();
      } catch (PlayerNotFoundException playerNotFoundException) {
         if (PlayerSelector.hasArguments(query)) {
            throw playerNotFoundException;
         } else {
            return query;
         }
      }
   }

   public static String getEntityName(ICommandSender sender, String selector) throws EntityNotFoundException {
      try {
         return getPlayer(sender, selector).getName();
      } catch (PlayerNotFoundException playerNotFoundException) {
         try {
            return getEntity(sender, selector).getUniqueID().toString();
         } catch (EntityNotFoundException entityNotFoundException) {
            if (PlayerSelector.hasArguments(selector)) {
               throw entityNotFoundException;
            } else {
               return selector;
            }
         }
      }
   }

   public int getRequiredPermissionLevel() {
      return 4;
   }

   public static List<String> getListOfStringsMatchingLastWord(String[] args, Collection<?> possibilities) {
      String text2 = args[args.length - 1];
      ArrayList arrayList = Lists.newArrayList();
      if (!possibilities.isEmpty()) {
         for (String text3 : Iterables.transform(possibilities, Functions.toStringFunction())) {
            if (doesStringStartWith(text2, text3)) {
               arrayList.add(text3);
            }
         }

         if (arrayList.isEmpty()) {
            for (Object object : possibilities) {
               if (object instanceof ResourceLocation && doesStringStartWith(text2, ((ResourceLocation)object).getResourcePath())) {
                  arrayList.add(String.valueOf(object));
               }
            }
         }
      }

      return arrayList;
   }

   public static EntityPlayerMP getPlayer(ICommandSender sender, String username) throws PlayerNotFoundException {
      EntityPlayerMP entityPlayerMP = PlayerSelector.matchOnePlayer(sender, username);
      if (entityPlayerMP == null) {
         try {
            entityPlayerMP = MinecraftServer.getServer().getConfigurationManager().getPlayerByUUID(UUID.fromString(username));
         } catch (IllegalArgumentException illegalArgumentException) {
         }
      }

      if (entityPlayerMP == null) {
         entityPlayerMP = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(username);
      }

      if (entityPlayerMP == null) {
         throw new PlayerNotFoundException();
      } else {
         return entityPlayerMP;
      }
   }

   public static void notifyOperators(ICommandSender sender, ICommand command, int flags, String msgFormat, Object... msgParams) {
      if (theAdmin != null) {
         theAdmin.notifyOperators(sender, command, flags, msgFormat, msgParams);
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return null;
   }

   public static List<String> getListOfStringsMatchingLastWord(String[] args, String... possibilities) {
      return getListOfStringsMatchingLastWord(args, Arrays.asList(possibilities));
   }

   public static <T extends Entity> T getEntity(ICommandSender commandSender, String selector, Class<? extends T> entityClass) throws EntityNotFoundException {
      Entity entity2 = PlayerSelector.matchOneEntity(commandSender, selector, entityClass);
      MinecraftServer server2 = MinecraftServer.getServer();
      if (entity2 == null) {
         entity2 = server2.getConfigurationManager().getPlayerByUsername(selector);
      }

      if (entity2 == null) {
         try {
            UUID uuid = UUID.fromString(selector);
            entity2 = server2.getEntityFromUuid(uuid);
            if (entity2 == null) {
               entity2 = server2.getConfigurationManager().getPlayerByUUID(uuid);
            }
         } catch (IllegalArgumentException illegalArgumentException) {
            throw new EntityNotFoundException("commands.generic.entity.invalidUuid");
         }
      }

      if (entity2 != null && entityClass.isAssignableFrom(entity2.getClass())) {
         return (T)entity2;
      } else {
         throw new EntityNotFoundException();
      }
   }

   @Override
   public boolean canCommandSenderUseCommand(ICommandSender sender) {
      return sender.canCommandSenderUseCommand(this.getRequiredPermissionLevel(), this.getCommandName());
   }

   static {
      handleArray(new Entity[1]);
   }

   public static IChatComponent getChatComponentFromNthArg(ICommandSender sender, String[] args, int index, boolean parseSelectors) throws PlayerNotFoundException {
      ChatComponentText chatComponentText = new ChatComponentText("");

      for (int index2 = index; index2 < args.length; index2++) {
         if (index2 > index) {
            chatComponentText.appendText(" ");
         }

         IChatComponent iChatComponent = new ChatComponentText(args[index2]);
         if (parseSelectors) {
            IChatComponent chatComponent3 = PlayerSelector.matchEntitiesToChatComponent(sender, args[index2]);
            if (chatComponent3 == null) {
               if (PlayerSelector.hasArguments(args[index2])) {
                  throw new PlayerNotFoundException();
               }
            } else {
               iChatComponent = chatComponent3;
            }
         }

         chatComponentText.appendSibling(iChatComponent);
      }

      return chatComponentText;
   }

   public static long parseLong(String input) throws NumberInvalidException {
      try {
         return Long.parseLong(input);
      } catch (NumberFormatException numberFormatException) {
         throw new NumberInvalidException("commands.generic.num.invalid", input);
      }
   }

   public static double parseDouble(String input) throws NumberInvalidException {
      try {
         double d = Double.parseDouble(input);
         if (!Doubles.isFinite(d)) {
            throw new NumberInvalidException("commands.generic.num.invalid", input);
         } else {
            return d;
         }
      } catch (NumberFormatException numberFormatException) {
         throw new NumberInvalidException("commands.generic.num.invalid", input);
      }
   }

   public static IChatComponent join(List<IChatComponent> components) {
      ChatComponentText chatComponentText = new ChatComponentText("");

      for (int index = 0; index < components.size(); index++) {
         if (index > 0) {
            if (index == components.size() - 1) {
               chatComponentText.appendText(" and ");
            } else if (index > 0) {
               chatComponentText.appendText(", ");
            }
         }

         chatComponentText.appendSibling(components.get(index));
      }

      return chatComponentText;
   }

   public static CommandBase.CoordinateArg parseCoordinate(double base, String input, int min, int max, boolean centerBlock) throws NumberInvalidException {
      boolean conditionMet = input.startsWith("~");
      if (conditionMet && Double.isNaN(base)) {
         throw new NumberInvalidException("commands.generic.num.invalid", base);
      }

      double d = 0.0;
      if (!conditionMet || input.length() > 1) {
         boolean conditionMet2 = input.contains(".");
         if (conditionMet) {
            input = input.substring(1);
         }

         d += parseDouble(input);
         if (!conditionMet2 && !conditionMet && centerBlock) {
            d += 0.5;
         }
      }

      if (min != 0 || max != 0) {
         if (d < min) {
            throw new NumberInvalidException("commands.generic.double.tooSmall", d, min);
         }

         if (d > max) {
            throw new NumberInvalidException("commands.generic.double.tooBig", d, max);
         }
      }

      return new CommandBase.CoordinateArg(d + (conditionMet ? base : 0.0), d, conditionMet);
   }

   public static double parseDouble(double base, String input, boolean centerBlock) throws NumberInvalidException {
      return parseDouble(base, input, -30000000, 30000000, centerBlock);
   }

   public static CommandBase.CoordinateArg parseCoordinate(double base, String input, boolean centerBlock) throws NumberInvalidException {
      return parseCoordinate(base, input, -30000000, 30000000, centerBlock);
   }

   public static Block getBlockByText(ICommandSender sender, String id) throws NumberInvalidException {
      ResourceLocation resourceLocation = new ResourceLocation(id);
      if (!Block.blockRegistry.containsKey(resourceLocation)) {
         throw new NumberInvalidException("commands.give.block.notFound", resourceLocation);
      } else {
         Block block = Block.blockRegistry.getObject(resourceLocation);
         if (block == null) {
            throw new NumberInvalidException("commands.give.block.notFound", resourceLocation);
         } else {
            return block;
         }
      }
   }

   public static boolean doesStringStartWith(String original, String region) {
      return region.regionMatches(true, 0, original, 0, original.length());
   }

   public static Entity[] getEntityArray() {
      return entityArray;
   }

   public static Item getItemByText(ICommandSender sender, String id) throws NumberInvalidException {
      ResourceLocation resourceLocation = new ResourceLocation(id);
      Item item = Item.itemRegistry.getObject(resourceLocation);
      if (item == null) {
         throw new NumberInvalidException("commands.give.item.notFound", resourceLocation);
      } else {
         return item;
      }
   }

   public static String joinNiceStringFromCollection(Collection<String> strings) {
      return joinNiceString(strings.toArray(new String[strings.size()]));
   }

   @Override
   public List<String> getCommandAliases() {
      return Collections.emptyList();
   }

   public static int parseInt(String input, int min, int max) throws NumberInvalidException {
      int i = parseInt(input);
      if (i < min) {
         throw new NumberInvalidException("commands.generic.num.tooSmall", i, min);
      } else if (i > max) {
         throw new NumberInvalidException("commands.generic.num.tooBig", i, max);
      } else {
         return i;
      }
   }

   public static boolean parseBoolean(String input) throws CommandException {
      if (input.equals("true") || input.equals("1")) {
         return true;
      } else if (!input.equals("false") && !input.equals("0")) {
         throw new CommandException("commands.generic.boolean.invalid", input);
      } else {
         return false;
      }
   }

   public static void handleArray(Entity[] entitys) {
      entityArray = entitys;
   }

   public static List<Entity> getEntities(ICommandSender sender, String selector) throws EntityNotFoundException {
      return PlayerSelector.hasArguments(selector)
         ? PlayerSelector.matchEntities(sender, selector, Entity.class)
         : Lists.newArrayList(new Entity[]{getEntity(sender, selector)});
   }

   public static int parseInt(String input) throws NumberInvalidException {
      try {
         return Integer.parseInt(input);
      } catch (NumberFormatException numberFormatException) {
         throw new NumberInvalidException("commands.generic.num.invalid", input);
      }
   }

   public static List<String> getCoordinateXZTabCompletion(String[] args, int index, BlockPos pos) {
      if (pos == null) {
         return null;
      }

      int i = args.length - 1;
      String text2;
      if (i == index) {
         text2 = Integer.toString(pos.getX());
      } else {
         if (i != index + 1) {
            return null;
         }

         text2 = Integer.toString(pos.getZ());
      }

      return Lists.newArrayList(new String[]{text2});
   }

   public static EntityPlayerMP getCommandSenderAsPlayer(ICommandSender sender) throws PlayerNotFoundException {
      if (sender instanceof EntityPlayerMP) {
         return (EntityPlayerMP)sender;
      } else {
         throw new PlayerNotFoundException("You must specify which player you wish to perform this action on.");
      }
   }

   public static List<String> getCoordinateTabCompletion(String[] args, int index, BlockPos pos) {
      if (pos == null) {
         return null;
      }

      int i = args.length - 1;
      String text2;
      if (i == index) {
         text2 = Integer.toString(pos.getX());
      } else if (i == index + 1) {
         text2 = Integer.toString(pos.getY());
      } else {
         if (i != index + 2) {
            return null;
         }

         text2 = Integer.toString(pos.getZ());
      }

      return Lists.newArrayList(new String[]{text2});
   }

   public static BlockPos parseBlockPos(ICommandSender sender, String[] args, int startIndex, boolean centerBlock) throws NumberInvalidException {
      BlockPos blockPos = sender.getPosition();
      return new BlockPos(
         parseDouble(blockPos.getX(), args[startIndex], -30000000, 30000000, centerBlock),
         parseDouble(blockPos.getY(), args[startIndex + 1], 0, 256, false),
         parseDouble(blockPos.getZ(), args[startIndex + 2], -30000000, 30000000, centerBlock)
      );
   }

   public static int parseInt(String input, int min) throws NumberInvalidException {
      return parseInt(input, min, Integer.MAX_VALUE);
   }

   public int compareTo(ICommand other) {
      return this.getCommandName().compareTo(other.getCommandName());
   }

   public static double parseDouble(String input, double min, double max) throws NumberInvalidException {
      double d = parseDouble(input);
      if (d < min) {
         throw new NumberInvalidException("commands.generic.double.tooSmall", d, min);
      } else if (d > max) {
         throw new NumberInvalidException("commands.generic.double.tooBig", d, max);
      } else {
         return d;
      }
   }

   public static Entity getEntity(ICommandSender sender, String selector) throws EntityNotFoundException {
      return getEntity(sender, selector, Entity.class);
   }

   public static long createLong(String text2) throws NumberInvalidException {
      long l = parseLong(text2);
      if (l < 0L) {
         throw new NumberInvalidException("commands.generic.num.tooSmall", l, 0L);
      } else if (l > 9223372036854775L) {
         throw new NumberInvalidException("commands.generic.num.tooBig", l, 9223372036854775L);
      } else {
         return l;
      }
   }

   public static void notifyOperators(ICommandSender sender, ICommand command, String msgFormat, Object... msgParams) {
      notifyOperators(sender, command, 0, msgFormat, msgParams);
   }

   public static void setAdminCommander(IAdminCommand command) {
      theAdmin = command;
   }

   public static String joinNiceString(Object[] elements) {
      StringBuilder stringBuilder = new StringBuilder();

      for (int index = 0; index < elements.length; index++) {
         String text = elements[index].toString();
         if (index > 0) {
            if (index == elements.length - 1) {
               stringBuilder.append(" and ");
            } else {
               stringBuilder.append(", ");
            }
         }

         stringBuilder.append(text);
      }

      return stringBuilder.toString();
   }

   public static String buildString(String[] args, int startPos) {
      StringBuilder stringBuilder = new StringBuilder();

      for (int index = startPos; index < args.length; index++) {
         if (index > startPos) {
            stringBuilder.append(" ");
         }

         String text2 = args[index];
         stringBuilder.append(text2);
      }

      return stringBuilder.toString();
   }

   public static class CoordinateArg {
      private final double result;
      private final double amount;
      private final boolean relative;

      public double getAmount() {
         return this.amount;
      }

      protected CoordinateArg(double result, double amount, boolean relative) {
         this.result = result;
         this.amount = amount;
         this.relative = relative;
      }

      public boolean isRelative() {
         return this.relative;
      }

      public double getResult() {
         return this.result;
      }
   }
}
