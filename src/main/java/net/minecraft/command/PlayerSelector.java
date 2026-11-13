package net.minecraft.command;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class PlayerSelector {
   private static final Pattern tokenPattern = Pattern.compile("^@([pare])(?:\\[([\\w=,!-]*)\\])?$");
   private static final Pattern intListPattern = Pattern.compile("\\G([-!]?[\\w-]*)(?:$|,)");
   private static final Pattern keyValueListPattern = Pattern.compile("\\G(\\w+)=([-!]?[\\w-]*)(?:$|,)");
   private static final Set<String> WORLD_BINDING_ARGS = Sets.newHashSet(new String[]{"x", "y", "z", "dx", "dy", "dz", "rm", "r"});

   private static boolean hasWorldLimitedArgument(Map<String, String> params) {
      for (String text2 : WORLD_BINDING_ARGS) {
         if (params.containsKey(text2)) {
            return true;
         }
      }

      return false;
   }

   private static List<World> getWorlds(ICommandSender sender, Map<String, String> argumentMap) {
      List<World> list = Lists.newArrayList();
      if (hasWorldLimitedArgument(argumentMap)) {
         list.add(sender.getEntityWorld());
      } else {
         Collections.addAll(list, MinecraftServer.getServer().worldServers);
      }

      return list;
   }

   public static EntityPlayerMP matchOnePlayer(ICommandSender sender, String token) {
      return matchOneEntity(sender, token, EntityPlayerMP.class);
   }

   private static List<Predicate<Entity>> getScorePredicates(Map<String, String> params) {
      List<Predicate<Entity>> list = Lists.newArrayList();
      final Map<String, Integer> map = getScoreMap(params);
      if (map != null && map.size() > 0) {
         list.add(new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               Scoreboard scoreboard2 = MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();

               for (Entry entry : map.entrySet()) {
                  String text2 = (String)entry.getKey();
                  boolean flag = false;
                  if (text2.endsWith("_min") && text2.length() > 4) {
                     flag = true;
                     text2 = text2.substring(0, text2.length() - 4);
                  }

                  ScoreObjective objective = scoreboard2.getObjective(text2);
                  if (objective == null) {
                     return false;
                  }

                  String text3 = entity instanceof EntityPlayerMP ? entity.getName() : entity.getUniqueID().toString();
                  if (!scoreboard2.entityHasObjective(text3, objective)) {
                     return false;
                  }

                  int scorePoints = scoreboard2.getValueFromObjective(text3, objective).getScorePoints();
                  if (scorePoints < (Integer)entry.getValue() && flag) {
                     return false;
                  }

                  if (scorePoints > (Integer)entry.getValue() && !flag) {
                     return false;
                  }
               }

               return true;
            }
         });
      }

      return list;
   }

   public static <T extends Entity> List<T> matchEntities(ICommandSender sender, String token, Class<? extends T> targetClass) {
      Matcher matcher3 = tokenPattern.matcher(token);
      if (matcher3.matches() && sender.canCommandSenderUseCommand(1, "@")) {
         Map argumentMap2 = getArgumentMap(matcher3.group(2));
         if (!isEntityTypeValid(sender, argumentMap2)) {
            return Collections.emptyList();
         }

         String text2 = matcher3.group(1);
         BlockPos blockPosFromArguments = getBlockPosFromArguments(argumentMap2, sender.getPosition());
         List worlds = getWorlds(sender, argumentMap2);
         ArrayList arrayList = Lists.newArrayList();

         for (World world2 : (Iterable<World>) worlds) {
            if (world2 != null) {
               ArrayList arrayList2 = Lists.newArrayList();
               arrayList2.addAll(getEntityTypePredicates(argumentMap2, text2));
               arrayList2.addAll(getXpLevelPredicates(argumentMap2));
               arrayList2.addAll(getGamemodePredicates(argumentMap2));
               arrayList2.addAll(getTeamPredicates(argumentMap2));
               arrayList2.addAll(getScorePredicates(argumentMap2));
               arrayList2.addAll(getNamePredicates(argumentMap2));
               arrayList2.addAll(getRadiusPredicates(argumentMap2, blockPosFromArguments));
               arrayList2.addAll(getRotationsPredicates(argumentMap2));
               arrayList.addAll(filterResults(argumentMap2, targetClass, arrayList2, text2, world2, blockPosFromArguments));
            }
         }

         return reduceResults(arrayList, argumentMap2, sender, targetClass, text2, blockPosFromArguments);
      } else {
         return Collections.emptyList();
      }
   }

   public static int normalizeAngle(int angle) {
      angle %= 360;
      if (angle >= 160) {
         angle -= 360;
      }

      if (angle < 0) {
         angle += 360;
      }

      return angle;
   }

   private static <T extends Entity> List<T> reduceResults(
      List<T> entities, Map<String, String> params, ICommandSender sender, Class<? extends T> targetClass, String selectorType, final BlockPos position
   ) {
      int i = !selectorType.equals("a") && !selectorType.equals("e") ? 1 : 0;
      int i2 = parseIntWithDefault(params, "c", i);
      if (!selectorType.equals("p") && !selectorType.equals("a") && !selectorType.equals("e")) {
         if (selectorType.equals("r")) {
            Collections.shuffle(entities);
         }
      } else if (position != null) {
         Collections.sort(entities, new Comparator<Entity>() {
            public int compare(Entity firstEntity, Entity secondEntity) {
               return ComparisonChain.start().compare(firstEntity.getDistanceSq(position), secondEntity.getDistanceSq(position)).result();
            }
         });
      }

      Entity commandSenderEntity = sender.getCommandSenderEntity();
      if (commandSenderEntity != null && targetClass.isAssignableFrom(commandSenderEntity.getClass()) && i2 == 1 && entities.contains(commandSenderEntity) && !"r".equals(selectorType)) {
         entities = Lists.newArrayList((T)commandSenderEntity);
      }

      if (i2 != 0) {
         if (i2 < 0) {
            Collections.reverse(entities);
         }

         entities = entities.subList(0, Math.min(Math.abs(i2), entities.size()));
      }

      return entities;
   }

   private static AxisAlignedBB getAABB(BlockPos position, int xSize, int ySize, int zSize) {
      boolean flag = xSize < 0;
      boolean flag2 = ySize < 0;
      boolean flag3 = zSize < 0;
      int xCoordinate = position.getX() + (flag ? xSize : 0);
      int yCoordinate = position.getY() + (flag2 ? ySize : 0);
      int zCoordinate = position.getZ() + (flag3 ? zSize : 0);
      int xCoordinate2 = position.getX() + (flag ? 0 : xSize) + 1;
      int yCoordinate2 = position.getY() + (flag2 ? 0 : ySize) + 1;
      int zCoordinate2 = position.getZ() + (flag3 ? 0 : zSize) + 1;
      return new AxisAlignedBB(xCoordinate, yCoordinate, zCoordinate, xCoordinate2, yCoordinate2, zCoordinate2);
   }

   private static <T extends Entity> boolean isEntityTypeValid(ICommandSender commandSender, Map<String, String> params) {
      String argument = getArgument(params, "type");
      argument = argument != null && argument.startsWith("!") ? argument.substring(1) : argument;
      if (argument != null && !EntityList.isStringValidEntityName(argument)) {
         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.generic.entity.invalidType", argument);
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
         commandSender.addChatMessage(chatComponentTranslation);
         return false;
      } else {
         return true;
      }
   }

   public static Map<String, Integer> getScoreMap(Map<String, String> params) {
      HashMap hashMap = Maps.newHashMap();

      for (String text2 : params.keySet()) {
         if (text2.startsWith("score_") && text2.length() > "score_".length()) {
            hashMap.put(text2.substring("score_".length()), MathHelper.parseIntWithDefault(params.get(text2), 1));
         }
      }

      return hashMap;
   }

   public static <T extends Entity> T matchOneEntity(ICommandSender sender, String token, Class<? extends T> targetClass) {
      List<T> list = matchEntities(sender, token, targetClass);
      return list.size() == 1 ? list.get(0) : null;
   }

   public static IChatComponent matchEntitiesToChatComponent(ICommandSender sender, String token) {
      List list2 = matchEntities(sender, token, Entity.class);
      if (list2.isEmpty()) {
         return null;
      }

      ArrayList arrayList = Lists.newArrayList();

      for (Entity entity2 : (Iterable<Entity>) list2) {
         arrayList.add(entity2.getDisplayName());
      }

      return CommandBase.join(arrayList);
   }

   public static boolean hasArguments(String token) {
      return tokenPattern.matcher(token).matches();
   }

   private static List<Predicate<Entity>> getXpLevelPredicates(Map<String, String> params) {
      List<Predicate<Entity>> list = Lists.newArrayList();
      final int i = parseIntWithDefault(params, "lm", -1);
      final int i2 = parseIntWithDefault(params, "l", -1);
      if (i > -1 || i2 > -1) {
         list.add(
            new Predicate<Entity>() {
               public boolean apply(Entity entity) {
                  return !(entity instanceof EntityPlayerMP entityPlayerMP)
                     ? false
                     : (i <= -1 || entityPlayerMP.experienceLevel >= i)
                        && (i2 <= -1 || entityPlayerMP.experienceLevel <= i2);
               }
            }
         );
      }

      return list;
   }

   private static String getArgument(Map<String, String> params, String key) {
      return params.get(key);
   }

   private static List<Predicate<Entity>> getRadiusPredicates(Map<String, String> params, final BlockPos position) {
      List<Predicate<Entity>> list = Lists.newArrayList();
      final int i = parseIntWithDefault(params, "rm", -1);
      final int i2 = parseIntWithDefault(params, "r", -1);
      if (position != null && (i >= 0 || i2 >= 0)) {
         final int i3 = i * i;
         final int i4 = i2 * i2;
         list.add(new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               int intValuex = (int)entity.getDistanceSqToCenter(position);
               return (i < 0 || intValuex >= i3) && (i2 < 0 || intValuex <= i4);
            }
         });
      }

      return list;
   }

   private static List<Predicate<Entity>> getTeamPredicates(Map<String, String> params) {
      List<Predicate<Entity>> list = Lists.newArrayList();
      String text = getArgument(params, "team");
      final boolean flag = text != null && text.startsWith("!");
      if (flag) {
         text = text.substring(1);
      }

      if (text != null) {
         final String text2 = text;
         list.add(new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               if (!(entity instanceof EntityLivingBase)) {
                  return false;
               }

               Team team2 = ((EntityLivingBase)entity).getTeam();
               return (team2 == null ? "" : team2.getRegisteredName()).equals(text2) != flag;
            }
         });
      }

      return list;
   }

   private static List<Predicate<Entity>> getNamePredicates(Map<String, String> params) {
      List<Predicate<Entity>> list = Lists.newArrayList();
      String text = getArgument(params, "name");
      final boolean flag = text != null && text.startsWith("!");
      if (flag) {
         text = text.substring(1);
      }

      if (text != null) {
         final String text2 = text;
         list.add(new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               return entity.getName().equals(text2) != flag;
            }
         });
      }

      return list;
   }

   private static BlockPos getBlockPosFromArguments(Map<String, String> params, BlockPos defaultPosition) {
      return new BlockPos(
         parseIntWithDefault(params, "x", defaultPosition.getX()),
         parseIntWithDefault(params, "y", defaultPosition.getY()),
         parseIntWithDefault(params, "z", defaultPosition.getZ())
      );
   }

   private static Map<String, String> getArgumentMap(String argumentString) {
      Map<String, String> map = Maps.newHashMap();
      if (argumentString == null) {
         return map;
      }

      int i = 0;
      int i2 = -1;

      for (Matcher matcher = intListPattern.matcher(argumentString); matcher.find(); i2 = matcher.end()) {
         String text = null;
         switch (i++) {
            case 0:
               text = "x";
               break;
            case 1:
               text = "y";
               break;
            case 2:
               text = "z";
               break;
            case 3:
               text = "r";
         }

         if (text != null && matcher.group(1).length() > 0) {
            map.put(text, matcher.group(1));
         }
      }

      if (i2 < argumentString.length()) {
         Matcher matcher2 = keyValueListPattern.matcher(i2 == -1 ? argumentString : argumentString.substring(i2));

         while (matcher2.find()) {
            map.put(matcher2.group(1), matcher2.group(2));
         }
      }

      return map;
   }

   private static int parseIntWithDefault(Map<String, String> params, String key, int defaultValue) {
      return params.containsKey(key) ? MathHelper.parseIntWithDefault(params.get(key), defaultValue) : defaultValue;
   }

   private static <T extends Entity> List<T> filterResults(
      Map<String, String> params, Class<? extends T> entityClass, List<Predicate<Entity>> inputList, String type, World worldIn, BlockPos position
   ) {
      ArrayList arrayList = Lists.newArrayList();
      String argument = getArgument(params, "type");
      argument = argument != null && argument.startsWith("!") ? argument.substring(1) : argument;
      boolean flag = !type.equals("e");
      boolean flag2 = type.equals("r") && argument != null;
      int i = parseIntWithDefault(params, "dx", 0);
      int i2 = parseIntWithDefault(params, "dy", 0);
      int i3 = parseIntWithDefault(params, "dz", 0);
      int i4 = parseIntWithDefault(params, "r", -1);
      Predicate predicate = Predicates.and(inputList);
      Predicate predicate2 = Predicates.and(EntitySelectors.selectAnything, predicate);
      if (position != null) {
         int size2 = worldIn.playerEntities.size();
         int size3 = worldIn.loadedEntityList.size();
         boolean flag3 = size2 < size3 / 16;
         if (params.containsKey("dx") || params.containsKey("dy") || params.containsKey("dz")) {
            final AxisAlignedBB aabb = getAABB(position, i, i2, i3);
            if (flag && flag3 && !flag2) {
               Predicate predicate3 = new Predicate<Entity>() {
                  public boolean apply(Entity entity) {
                     return entity.posX >= aabb.minX && entity.posY >= aabb.minY && entity.posZ >= aabb.minZ
                        ? entity.posX < aabb.maxX && entity.posY < aabb.maxY && entity.posZ < aabb.maxZ
                        : false;
                  }
               };
               arrayList.addAll(worldIn.getPlayers(entityClass, Predicates.and(predicate2, predicate3)));
            } else {
               arrayList.addAll(worldIn.getEntitiesWithinAABB(entityClass, aabb, predicate2));
            }
         } else if (i4 >= 0) {
            AxisAlignedBB axisAlignedBB = new AxisAlignedBB(
               position.getX() - i4,
               position.getY() - i4,
               position.getZ() - i4,
               position.getX() + i4 + 1,
               position.getY() + i4 + 1,
               position.getZ() + i4 + 1
            );
            if (flag && flag3 && !flag2) {
               arrayList.addAll(worldIn.getPlayers(entityClass, predicate2));
            } else {
               arrayList.addAll(worldIn.getEntitiesWithinAABB(entityClass, axisAlignedBB, predicate2));
            }
         } else if (type.equals("a")) {
            arrayList.addAll(worldIn.getPlayers(entityClass, predicate));
         } else if (type.equals("p") || type.equals("r") && !flag2) {
            arrayList.addAll(worldIn.getPlayers(entityClass, predicate2));
         } else {
            arrayList.addAll(worldIn.getEntities(entityClass, predicate2));
         }
      } else if (type.equals("a")) {
         arrayList.addAll(worldIn.getPlayers(entityClass, predicate));
      } else if (type.equals("p") || type.equals("r") && !flag2) {
         arrayList.addAll(worldIn.getPlayers(entityClass, predicate2));
      } else {
         arrayList.addAll(worldIn.getEntities(entityClass, predicate2));
      }

      return arrayList;
   }

   private static List<Predicate<Entity>> getEntityTypePredicates(Map<String, String> params, String selectorType) {
      List<Predicate<Entity>> list = Lists.newArrayList();
      String text = getArgument(params, "type");
      final boolean flag = text != null && text.startsWith("!");
      if (flag) {
         text = text.substring(1);
      }

      boolean flag2 = !selectorType.equals("e");
      boolean flag3 = selectorType.equals("r") && text != null;
      if ((text == null || !selectorType.equals("e")) && !flag3) {
         if (flag2) {
            list.add(new Predicate<Entity>() {
               public boolean apply(Entity entity) {
                  return entity instanceof EntityPlayer;
               }
            });
         }
      } else {
         final String text2 = text;
         list.add(new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               return EntityList.isStringEntityName(entity, text2) != flag;
            }
         });
      }

      return list;
   }

   private static List<Predicate<Entity>> getRotationsPredicates(Map<String, String> params) {
      List<Predicate<Entity>> list = Lists.newArrayList();
      if (params.containsKey("rym") || params.containsKey("ry")) {
         final int i = normalizeAngle(parseIntWithDefault(params, "rym", 0));
         final int i2 = normalizeAngle(parseIntWithDefault(params, "ry", 359));
         list.add(new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               int i3 = PlayerSelector.normalizeAngle((int)Math.floor(entity.rotationYaw));
               return i > i2 ? i3 >= i || i3 <= i2 : i3 >= i && i3 <= i2;
            }
         });
      }

      if (params.containsKey("rxm") || params.containsKey("rx")) {
         final int i4 = normalizeAngle(parseIntWithDefault(params, "rxm", 0));
         final int i5 = normalizeAngle(parseIntWithDefault(params, "rx", 359));
         list.add(
            new Predicate<Entity>() {
               public boolean apply(Entity entity) {
                  int i6 = PlayerSelector.normalizeAngle((int)Math.floor(entity.rotationPitch));
                  return i4 > i5
                     ? i6 >= i4 || i6 <= i5
                     : i6 >= i4 && i6 <= i5;
               }
            }
         );
      }

      return list;
   }

   public static boolean matchesMultiplePlayers(String token) {
      Matcher matcher = tokenPattern.matcher(token);
      if (!matcher.matches()) {
         return false;
      }

      Map<String, String> map = getArgumentMap(matcher.group(2));
      String text = matcher.group(1);
      int i = !"a".equals(text) && !"e".equals(text) ? 1 : 0;
      return parseIntWithDefault(map, "c", i) != 1;
   }

   private static List<Predicate<Entity>> getGamemodePredicates(Map<String, String> params) {
      List<Predicate<Entity>> list = Lists.newArrayList();
      final int i = parseIntWithDefault(params, "m", net.minecraft.world.WorldSettings.GameType.NOT_SET.getID());
      if (i != net.minecraft.world.WorldSettings.GameType.NOT_SET.getID()) {
         list.add(new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               return !(entity instanceof EntityPlayerMP) ? false : ((EntityPlayerMP)entity).theItemInWorldManager.getGameType().getID() == i;
            }
         });
      }

      return list;
   }
}
