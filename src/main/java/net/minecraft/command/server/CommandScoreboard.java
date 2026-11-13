package net.minecraft.command.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class CommandScoreboard extends CommandBase {
   protected Scoreboard getScoreboard() {
      return MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.scoreboard.usage";
   }

   protected void handleICommandSender6(ICommandSender iCommandSender, String[] texts) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      String text = texts[2];
      int objectiveDisplaySlotNumber = Scoreboard.getObjectiveDisplaySlotNumber(text);
      ScoreObjective scoreObjective2 = null;
      if (texts.length == 4) {
         scoreObjective2 = this.getObjective(texts[3], false);
      }

      if (objectiveDisplaySlotNumber < 0) {
         throw new CommandException("commands.scoreboard.objectives.setdisplay.invalidSlot", text);
      }

      scoreboard2.setObjectiveInDisplaySlot(objectiveDisplaySlotNumber, scoreObjective2);
      if (scoreObjective2 != null) {
         notifyOperators(iCommandSender, this, "commands.scoreboard.objectives.setdisplay.successSet", Scoreboard.getObjectiveDisplaySlot(objectiveDisplaySlotNumber), scoreObjective2.getName());
      } else {
         notifyOperators(iCommandSender, this, "commands.scoreboard.objectives.setdisplay.successCleared", Scoreboard.getObjectiveDisplaySlot(objectiveDisplaySlotNumber));
      }
   }

   private boolean processWildcardCommand(ICommandSender sender, String[] args) throws CommandException {
      int i = -1;

      for (int i2 = 0; i2 < args.length; i2++) {
         if (this.isUsernameIndex(args, i2) && "*".equals(args[i2])) {
            if (i >= 0) {
               throw new CommandException("commands.scoreboard.noMultiWildcard");
            }

            i = i2;
         }
      }

      if (i < 0) {
         return false;
      }

      ArrayList arrayList = Lists.newArrayList(this.getScoreboard().getObjectiveNames());
      String text = args[i];
      ArrayList arrayList2 = Lists.newArrayList();

      for (String text2 : (Iterable<String>) arrayList) {
         args[i] = text2;

         try {
            this.processCommand(sender, args);
            arrayList2.add(text2);
         } catch (CommandException commandException) {
            ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation(commandException.getMessage(), commandException.getErrorObjects());
            chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            sender.addChatMessage(chatComponentTranslation);
         }
      }

      args[i] = text;
      sender.setCommandStat(net.minecraft.command.CommandResultStats.Type.AFFECTED_ENTITIES, arrayList2.size());
      if (arrayList2.size() == 0) {
         throw new WrongUsageException("commands.scoreboard.allMatchesFailed");
      } else {
         return true;
      }
   }

   protected void handleICommandSender3(ICommandSender iCommandSender, String[] texts) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      String entityName = getEntityName(iCommandSender, texts[2]);
      ScoreObjective objective = this.getObjective(texts[3], true);
      String text = texts[4];
      String entityName2 = getEntityName(iCommandSender, texts[5]);
      ScoreObjective objective2 = this.getObjective(texts[6], false);
      if (entityName.length() > 40) {
         throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", entityName, 40);
      }

      if (entityName2.length() > 40) {
         throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", entityName2, 40);
      }

      Score valueFromObjective = scoreboard2.getValueFromObjective(entityName, objective);
      if (!scoreboard2.entityHasObjective(entityName2, objective2)) {
         throw new CommandException("commands.scoreboard.players.operation.notFound", objective2.getName(), entityName2);
      }

      Score valueFromObjective2 = scoreboard2.getValueFromObjective(entityName2, objective2);
      if (text.equals("+=")) {
         valueFromObjective.setScorePoints(valueFromObjective.getScorePoints() + valueFromObjective2.getScorePoints());
      } else if (text.equals("-=")) {
         valueFromObjective.setScorePoints(valueFromObjective.getScorePoints() - valueFromObjective2.getScorePoints());
      } else if (text.equals("*=")) {
         valueFromObjective.setScorePoints(valueFromObjective.getScorePoints() * valueFromObjective2.getScorePoints());
      } else if (text.equals("/=")) {
         if (valueFromObjective2.getScorePoints() != 0) {
            valueFromObjective.setScorePoints(valueFromObjective.getScorePoints() / valueFromObjective2.getScorePoints());
         }
      } else if (text.equals("%=")) {
         if (valueFromObjective2.getScorePoints() != 0) {
            valueFromObjective.setScorePoints(valueFromObjective.getScorePoints() % valueFromObjective2.getScorePoints());
         }
      } else if (text.equals("=")) {
         valueFromObjective.setScorePoints(valueFromObjective2.getScorePoints());
      } else if (text.equals("<")) {
         valueFromObjective.setScorePoints(Math.min(valueFromObjective.getScorePoints(), valueFromObjective2.getScorePoints()));
      } else if (text.equals(">")) {
         valueFromObjective.setScorePoints(Math.max(valueFromObjective.getScorePoints(), valueFromObjective2.getScorePoints()));
      } else {
         if (!text.equals("><")) {
            throw new CommandException("commands.scoreboard.players.operation.invalidOperation", text);
         }

         int scorePoints = valueFromObjective.getScorePoints();
         valueFromObjective.setScorePoints(valueFromObjective2.getScorePoints());
         valueFromObjective2.setScorePoints(scorePoints);
      }

      notifyOperators(iCommandSender, this, "commands.scoreboard.players.operation.success");
   }

   protected void handleICommandSender9(ICommandSender iCommandSender, String[] texts) throws CommandException {
      String text = texts[1];
      String entityName = getEntityName(iCommandSender, texts[2]);
      if (entityName.length() > 40) {
         throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", entityName, 40);
      }

      ScoreObjective objective = this.getObjective(texts[3], true);
      int i = text.equalsIgnoreCase("set") ? parseInt(texts[4]) : parseInt(texts[4], 0);
      if (texts.length > 5) {
         Entity entity2 = getEntity(iCommandSender, texts[2]);

         try {
            NBTTagCompound tagFromJson = JsonToNBT.getTagFromJson(buildString(texts, 5));
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            entity2.writeToNBT(nbtTagCompound);
            if (!NBTUtil.areNBTEquals(tagFromJson, nbtTagCompound, true)) {
               throw new CommandException("commands.scoreboard.players.set.tagMismatch", entityName);
            }
         } catch (NBTException nbtException) {
            throw new CommandException("commands.scoreboard.players.set.tagError", nbtException.getMessage());
         }
      }

      Scoreboard scoreboard2 = this.getScoreboard();
      Score valueFromObjective = scoreboard2.getValueFromObjective(entityName, objective);
      if (text.equalsIgnoreCase("set")) {
         valueFromObjective.setScorePoints(i);
      } else if (text.equalsIgnoreCase("add")) {
         valueFromObjective.increseScore(i);
      } else {
         valueFromObjective.decreaseScore(i);
      }

      notifyOperators(iCommandSender, this, "commands.scoreboard.players.set.success", objective.getName(), entityName, valueFromObjective.getScorePoints());
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      if (args.length == 1) {
         return getListOfStringsMatchingLastWord(args, "objectives", "players", "teams");
      }

      if (args[0].equalsIgnoreCase("objectives")) {
         if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, "list", "add", "remove", "setdisplay");
         }

         if (args[1].equalsIgnoreCase("add")) {
            if (args.length == 4) {
               Set<String> set = IScoreObjectiveCriteria.INSTANCES.keySet();
               return getListOfStringsMatchingLastWord(args, set);
            }
         } else if (args[1].equalsIgnoreCase("remove")) {
            if (args.length == 3) {
               return getListOfStringsMatchingLastWord(args, this.getObjectiveNames(false));
            }
         } else if (args[1].equalsIgnoreCase("setdisplay")) {
            if (args.length == 3) {
               return getListOfStringsMatchingLastWord(args, Scoreboard.getDisplaySlotStrings());
            }

            if (args.length == 4) {
               return getListOfStringsMatchingLastWord(args, this.getObjectiveNames(false));
            }
         }
      } else if (args[0].equalsIgnoreCase("players")) {
         if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, "set", "add", "remove", "reset", "list", "enable", "test", "operation");
         }

         if (!args[1].equalsIgnoreCase("set") && !args[1].equalsIgnoreCase("add") && !args[1].equalsIgnoreCase("remove") && !args[1].equalsIgnoreCase("reset")) {
            if (args[1].equalsIgnoreCase("enable")) {
               if (args.length == 3) {
                  return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
               }

               if (args.length == 4) {
                  return getListOfStringsMatchingLastWord(args, this.getTriggerObjectiveNames());
               }
            } else if (!args[1].equalsIgnoreCase("list") && !args[1].equalsIgnoreCase("test")) {
               if (args[1].equalsIgnoreCase("operation")) {
                  if (args.length == 3) {
                     return getListOfStringsMatchingLastWord(args, this.getScoreboard().getObjectiveNames());
                  }

                  if (args.length == 4) {
                     return getListOfStringsMatchingLastWord(args, this.getObjectiveNames(true));
                  }

                  if (args.length == 5) {
                     return getListOfStringsMatchingLastWord(args, "+=", "-=", "*=", "/=", "%=", "=", "<", ">", "><");
                  }

                  if (args.length == 6) {
                     return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
                  }

                  if (args.length == 7) {
                     return getListOfStringsMatchingLastWord(args, this.getObjectiveNames(false));
                  }
               }
            } else {
               if (args.length == 3) {
                  return getListOfStringsMatchingLastWord(args, this.getScoreboard().getObjectiveNames());
               }

               if (args.length == 4 && args[1].equalsIgnoreCase("test")) {
                  return getListOfStringsMatchingLastWord(args, this.getObjectiveNames(false));
               }
            }
         } else {
            if (args.length == 3) {
               return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
            }

            if (args.length == 4) {
               return getListOfStringsMatchingLastWord(args, this.getObjectiveNames(true));
            }
         }
      } else if (args[0].equalsIgnoreCase("teams")) {
         if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, "add", "remove", "join", "leave", "empty", "list", "option");
         }

         if (args[1].equalsIgnoreCase("join")) {
            if (args.length == 3) {
               return getListOfStringsMatchingLastWord(args, this.getScoreboard().getTeamNames());
            }

            if (args.length >= 4) {
               return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
            }
         } else {
            if (args[1].equalsIgnoreCase("leave")) {
               return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
            }

            if (!args[1].equalsIgnoreCase("empty") && !args[1].equalsIgnoreCase("list") && !args[1].equalsIgnoreCase("remove")) {
               if (args[1].equalsIgnoreCase("option")) {
                  if (args.length == 3) {
                     return getListOfStringsMatchingLastWord(args, this.getScoreboard().getTeamNames());
                  }

                  if (args.length == 4) {
                     return getListOfStringsMatchingLastWord(
                        args, "color", "friendlyfire", "seeFriendlyInvisibles", "nametagVisibility", "deathMessageVisibility"
                     );
                  }

                  if (args.length == 5) {
                     if (args[3].equalsIgnoreCase("color")) {
                        return getListOfStringsMatchingLastWord(args, EnumChatFormatting.getCollection());
                     }

                     if (args[3].equalsIgnoreCase("nametagVisibility") || args[3].equalsIgnoreCase("deathMessageVisibility")) {
                        return getListOfStringsMatchingLastWord(args, net.minecraft.scoreboard.Team.EnumVisible.getNames());
                     }

                     if (args[3].equalsIgnoreCase("friendlyfire") || args[3].equalsIgnoreCase("seeFriendlyInvisibles")) {
                        return getListOfStringsMatchingLastWord(args, "true", "false");
                     }
                  }
               }
            } else if (args.length == 3) {
               return getListOfStringsMatchingLastWord(args, this.getScoreboard().getTeamNames());
            }
         }
      }

      return null;
   }

   protected void handleICommandSender7(ICommandSender iCommandSender, String[] texts) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      ScorePlayerTeam team = this.getTeam(texts[2]);
      if (team != null) {
         scoreboard2.removeTeam(team);
         notifyOperators(iCommandSender, this, "commands.scoreboard.teams.remove.success", team.getRegisteredName());
      }
   }

   protected ScoreObjective getObjective(String name, boolean edit) throws CommandException {
      ScoreObjective objective = this.getScoreboard().getObjective(name);
      if (objective == null) {
         throw new CommandException("commands.scoreboard.objectiveNotFound", name);
      } else if (edit && objective.getCriteria().isReadOnly()) {
         throw new CommandException("commands.scoreboard.objectiveReadOnly", name);
      } else {
         return objective;
      }
   }

   @Override
   public String getCommandName() {
      return "scoreboard";
   }

   protected void handleICommandSender5(ICommandSender iCommandSender, String[] texts) throws CommandException {
      String text = texts[2];
      Scoreboard scoreboard2 = this.getScoreboard();
      if (scoreboard2.getTeam(text) != null) {
         throw new CommandException("commands.scoreboard.teams.add.alreadyExists", text);
      }

      if (text.length() > 16) {
         throw new SyntaxErrorException("commands.scoreboard.teams.add.tooLong", text, 16);
      }

      if (text.length() == 0) {
         throw new WrongUsageException("commands.scoreboard.teams.add.usage");
      }

      if (texts.length > 3) {
         String unformattedText = getChatComponentFromNthArg(iCommandSender, texts, 3).getUnformattedText();
         if (unformattedText.length() > 32) {
            throw new SyntaxErrorException("commands.scoreboard.teams.add.displayTooLong", unformattedText, 32);
         }

         if (unformattedText.length() > 0) {
            scoreboard2.createTeam(text).setTeamName(unformattedText);
         } else {
            scoreboard2.createTeam(text);
         }
      } else {
         scoreboard2.createTeam(text);
      }

      notifyOperators(iCommandSender, this, "commands.scoreboard.teams.add.success", text);
   }

   protected ScorePlayerTeam getTeam(String name) throws CommandException {
      ScorePlayerTeam team = this.getScoreboard().getTeam(name);
      if (team == null) {
         throw new CommandException("commands.scoreboard.teamNotFound", name);
      } else {
         return team;
      }
   }

   protected void joinTeam(ICommandSender sender, String[] args, int index) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      index++;
      String text = args[2];
      HashSet hashSet = Sets.newHashSet();
      HashSet hashSet2 = Sets.newHashSet();
      if (sender instanceof EntityPlayer && 3 == args.length) {
         String name2 = getCommandSenderAsPlayer(sender).getName();
         if (scoreboard2.addPlayerToTeam(name2, text)) {
            hashSet.add(name2);
         } else {
            hashSet2.add(name2);
         }
      } else {
         while (index < args.length) {
            String text2 = args[index++];
            if (text2.startsWith("@")) {
               for (Entity entity2 : getEntities(sender, text2)) {
                  String entityName = getEntityName(sender, entity2.getUniqueID().toString());
                  if (scoreboard2.addPlayerToTeam(entityName, text)) {
                     hashSet.add(entityName);
                  } else {
                     hashSet2.add(entityName);
                  }
               }
            } else {
               String entityName2 = getEntityName(sender, text2);
               if (scoreboard2.addPlayerToTeam(entityName2, text)) {
                  hashSet.add(entityName2);
               } else {
                  hashSet2.add(entityName2);
               }
            }
         }
      }

      if (!hashSet.isEmpty()) {
         sender.setCommandStat(net.minecraft.command.CommandResultStats.Type.AFFECTED_ENTITIES, hashSet.size());
         notifyOperators(sender, this, "commands.scoreboard.teams.join.success", hashSet.size(), text, joinNiceString(hashSet.toArray(new String[hashSet.size()])));
      }

      if (!hashSet2.isEmpty()) {
         throw new CommandException("commands.scoreboard.teams.join.failure", hashSet2.size(), text, joinNiceString(hashSet2.toArray(new String[hashSet2.size()])));
      }
   }

   protected void handleICommandSender4(ICommandSender iCommandSender, String[] texts) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      ScorePlayerTeam team = this.getTeam(texts[2]);
      if (team != null) {
         ArrayList arrayList = Lists.newArrayList(team.getMembershipCollection());
         iCommandSender.setCommandStat(net.minecraft.command.CommandResultStats.Type.AFFECTED_ENTITIES, arrayList.size());
         if (arrayList.isEmpty()) {
            throw new CommandException("commands.scoreboard.teams.empty.alreadyEmpty", team.getRegisteredName());
         }

         for (String text : (Iterable<String>) arrayList) {
            scoreboard2.removePlayerFromTeam(text, team);
         }

         notifyOperators(iCommandSender, this, "commands.scoreboard.teams.empty.success", arrayList.size(), team.getRegisteredName());
      }
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (!this.processWildcardCommand(sender, args)) {
         if (args.length < 1) {
            throw new WrongUsageException("commands.scoreboard.usage");
         }

         if (args[0].equalsIgnoreCase("objectives")) {
            if (args.length == 1) {
               throw new WrongUsageException("commands.scoreboard.objectives.usage");
            }

            if (args[1].equalsIgnoreCase("list")) {
               this.listObjectives(sender);
            } else if (args[1].equalsIgnoreCase("add")) {
               if (args.length < 4) {
                  throw new WrongUsageException("commands.scoreboard.objectives.add.usage");
               }

               this.handleICommandSender11(sender, args);
            } else if (args[1].equalsIgnoreCase("remove")) {
               if (args.length != 3) {
                  throw new WrongUsageException("commands.scoreboard.objectives.remove.usage");
               }

               this.removeObjective(sender, args[2]);
            } else {
               if (!args[1].equalsIgnoreCase("setdisplay")) {
                  throw new WrongUsageException("commands.scoreboard.objectives.usage");
               }

               if (args.length != 3 && args.length != 4) {
                  throw new WrongUsageException("commands.scoreboard.objectives.setdisplay.usage");
               }

               this.handleICommandSender6(sender, args);
            }
         } else if (args[0].equalsIgnoreCase("players")) {
            if (args.length == 1) {
               throw new WrongUsageException("commands.scoreboard.players.usage");
            }

            if (args[1].equalsIgnoreCase("list")) {
               if (args.length > 3) {
                  throw new WrongUsageException("commands.scoreboard.players.list.usage");
               }

               this.handleICommandSender10(sender, args);
            } else if (args[1].equalsIgnoreCase("add")) {
               if (args.length < 5) {
                  throw new WrongUsageException("commands.scoreboard.players.add.usage");
               }

               this.handleICommandSender9(sender, args);
            } else if (args[1].equalsIgnoreCase("remove")) {
               if (args.length < 5) {
                  throw new WrongUsageException("commands.scoreboard.players.remove.usage");
               }

               this.handleICommandSender9(sender, args);
            } else if (args[1].equalsIgnoreCase("set")) {
               if (args.length < 5) {
                  throw new WrongUsageException("commands.scoreboard.players.set.usage");
               }

               this.handleICommandSender9(sender, args);
            } else if (args[1].equalsIgnoreCase("reset")) {
               if (args.length != 3 && args.length != 4) {
                  throw new WrongUsageException("commands.scoreboard.players.reset.usage");
               }

               this.handleICommandSender13(sender, args);
            } else if (args[1].equalsIgnoreCase("enable")) {
               if (args.length != 4) {
                  throw new WrongUsageException("commands.scoreboard.players.enable.usage");
               }

               this.handleICommandSender(sender, args);
            } else if (args[1].equalsIgnoreCase("test")) {
               if (args.length != 5 && args.length != 6) {
                  throw new WrongUsageException("commands.scoreboard.players.test.usage");
               }

               this.handleICommandSender12(sender, args);
            } else {
               if (!args[1].equalsIgnoreCase("operation")) {
                  throw new WrongUsageException("commands.scoreboard.players.usage");
               }

               if (args.length != 7) {
                  throw new WrongUsageException("commands.scoreboard.players.operation.usage");
               }

               this.handleICommandSender3(sender, args);
            }
         } else {
            if (!args[0].equalsIgnoreCase("teams")) {
               throw new WrongUsageException("commands.scoreboard.usage");
            }

            if (args.length == 1) {
               throw new WrongUsageException("commands.scoreboard.teams.usage");
            }

            if (args[1].equalsIgnoreCase("list")) {
               if (args.length > 3) {
                  throw new WrongUsageException("commands.scoreboard.teams.list.usage");
               }

               this.handleICommandSender2(sender, args);
            } else if (args[1].equalsIgnoreCase("add")) {
               if (args.length < 3) {
                  throw new WrongUsageException("commands.scoreboard.teams.add.usage");
               }

               this.handleICommandSender5(sender, args);
            } else if (args[1].equalsIgnoreCase("remove")) {
               if (args.length != 3) {
                  throw new WrongUsageException("commands.scoreboard.teams.remove.usage");
               }

               this.handleICommandSender7(sender, args);
            } else if (args[1].equalsIgnoreCase("empty")) {
               if (args.length != 3) {
                  throw new WrongUsageException("commands.scoreboard.teams.empty.usage");
               }

               this.handleICommandSender4(sender, args);
            } else if (args[1].equalsIgnoreCase("join")) {
               if (args.length < 4 && (args.length != 3 || !(sender instanceof EntityPlayer))) {
                  throw new WrongUsageException("commands.scoreboard.teams.join.usage");
               }

               this.joinTeam(sender, args, 2);
            } else if (args[1].equalsIgnoreCase("leave")) {
               if (args.length < 3 && !(sender instanceof EntityPlayer)) {
                  throw new WrongUsageException("commands.scoreboard.teams.leave.usage");
               }

               this.leaveTeam(sender, args, 2);
            } else {
               if (!args[1].equalsIgnoreCase("option")) {
                  throw new WrongUsageException("commands.scoreboard.teams.usage");
               }

               if (args.length != 4 && args.length != 5) {
                  throw new WrongUsageException("commands.scoreboard.teams.option.usage");
               }

               this.handleICommandSender8(sender, args);
            }
         }
      }
   }

   protected void handleICommandSender8(ICommandSender iCommandSender, String[] texts) throws CommandException {
      ScorePlayerTeam team = this.getTeam(texts[2]);
      if (team != null) {
         String text = texts[3].toLowerCase();
         if (!text.equalsIgnoreCase("color")
            && !text.equalsIgnoreCase("friendlyfire")
            && !text.equalsIgnoreCase("seeFriendlyInvisibles")
            && !text.equalsIgnoreCase("nametagVisibility")
            && !text.equalsIgnoreCase("deathMessageVisibility")) {
            throw new WrongUsageException("commands.scoreboard.teams.option.usage");
         }

         if (texts.length == 4) {
            if (text.equalsIgnoreCase("color")) {
               throw new WrongUsageException(
                  "commands.scoreboard.teams.option.noValue", text, joinNiceStringFromCollection(EnumChatFormatting.getCollection())
               );
            }

            if (!text.equalsIgnoreCase("friendlyfire") && !text.equalsIgnoreCase("seeFriendlyInvisibles")) {
               if (!text.equalsIgnoreCase("nametagVisibility") && !text.equalsIgnoreCase("deathMessageVisibility")) {
                  throw new WrongUsageException("commands.scoreboard.teams.option.usage");
               }

               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", text, joinNiceString(net.minecraft.scoreboard.Team.EnumVisible.getNames()));
            }

            throw new WrongUsageException("commands.scoreboard.teams.option.noValue", text, joinNiceStringFromCollection(Arrays.asList("true", "false")));
         }

         String text2 = texts[4];
         if (text.equalsIgnoreCase("color")) {
            EnumChatFormatting valueByName = EnumChatFormatting.getValueByName(text2);
            if (valueByName == null || valueByName.isFancyStyling()) {
               throw new WrongUsageException(
                  "commands.scoreboard.teams.option.noValue", text, joinNiceStringFromCollection(EnumChatFormatting.getCollection())
               );
            }

            team.setChatFormat(valueByName);
            team.setNamePrefix(valueByName.toString());
            team.setNameSuffix(EnumChatFormatting.RESET.toString());
         } else if (text.equalsIgnoreCase("friendlyfire")) {
            if (!text2.equalsIgnoreCase("true") && !text2.equalsIgnoreCase("false")) {
               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", text, joinNiceStringFromCollection(Arrays.asList("true", "false")));
            }

            team.setAllowFriendlyFire(text2.equalsIgnoreCase("true"));
         } else if (text.equalsIgnoreCase("seeFriendlyInvisibles")) {
            if (!text2.equalsIgnoreCase("true") && !text2.equalsIgnoreCase("false")) {
               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", text, joinNiceStringFromCollection(Arrays.asList("true", "false")));
            }

            team.setSeeFriendlyInvisiblesEnabled(text2.equalsIgnoreCase("true"));
         } else if (text.equalsIgnoreCase("nametagVisibility")) {
            net.minecraft.scoreboard.Team.EnumVisible enumVisible = net.minecraft.scoreboard.Team.EnumVisible.byInternalName(text2);
            if (enumVisible == null) {
               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", text, joinNiceString(net.minecraft.scoreboard.Team.EnumVisible.getNames()));
            }

            team.setNameTagVisibility(enumVisible);
         } else if (text.equalsIgnoreCase("deathMessageVisibility")) {
            net.minecraft.scoreboard.Team.EnumVisible enumVisible2 = net.minecraft.scoreboard.Team.EnumVisible.byInternalName(text2);
            if (enumVisible2 == null) {
               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", text, joinNiceString(net.minecraft.scoreboard.Team.EnumVisible.getNames()));
            }

            team.setDeathMessageVisibility(enumVisible2);
         }

         notifyOperators(iCommandSender, this, "commands.scoreboard.teams.option.success", text, team.getRegisteredName(), text2);
      }
   }

   protected void listObjectives(ICommandSender sender) throws CommandException {
      Collection scoreObjectives = this.getScoreboard().getScoreObjectives();
      if (scoreObjectives.size() <= 0) {
         throw new CommandException("commands.scoreboard.objectives.list.empty");
      }

      ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.scoreboard.objectives.list.count", scoreObjectives.size());
      chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
      sender.addChatMessage(chatComponentTranslation);

      for (ScoreObjective scoreObjective : (Iterable<ScoreObjective>) scoreObjectives) {
         sender.addChatMessage(
            new ChatComponentTranslation(
               "commands.scoreboard.objectives.list.entry", scoreObjective.getName(), scoreObjective.getDisplayName(), scoreObjective.getCriteria().getName()
            )
         );
      }
   }

   protected List<String> getTriggerObjectiveNames() {
      Collection scoreObjectives = this.getScoreboard().getScoreObjectives();
      ArrayList arrayList = Lists.newArrayList();

      for (ScoreObjective scoreObjective2 : (Iterable<ScoreObjective>) scoreObjectives) {
         if (scoreObjective2.getCriteria() == IScoreObjectiveCriteria.TRIGGER) {
            arrayList.add(scoreObjective2.getName());
         }
      }

      return arrayList;
   }

   protected void handleICommandSender13(ICommandSender iCommandSender, String[] texts) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      String entityName = getEntityName(iCommandSender, texts[2]);
      if (texts.length > 3) {
         ScoreObjective objective = this.getObjective(texts[3], false);
         scoreboard2.removeObjectiveFromEntity(entityName, objective);
         notifyOperators(iCommandSender, this, "commands.scoreboard.players.resetscore.success", objective.getName(), entityName);
      } else {
         scoreboard2.removeObjectiveFromEntity(entityName, (ScoreObjective)null);
         notifyOperators(iCommandSender, this, "commands.scoreboard.players.reset.success", entityName);
      }
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return !args[0].equalsIgnoreCase("players")
         ? (args[0].equalsIgnoreCase("teams") ? index == 2 : false)
         : (args.length > 1 && args[1].equalsIgnoreCase("operation") ? index == 2 || index == 5 : index == 2);
   }

   protected void leaveTeam(ICommandSender sender, String[] args, int index) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      HashSet hashSet = Sets.newHashSet();
      HashSet hashSet2 = Sets.newHashSet();
      if (sender instanceof EntityPlayer && 2 == args.length) {
         String name2 = getCommandSenderAsPlayer(sender).getName();
         if (scoreboard2.removePlayerFromTeams(name2)) {
            hashSet.add(name2);
         } else {
            hashSet2.add(name2);
         }
      } else {
         while (index < args.length) {
            String text = args[index++];
            if (text.startsWith("@")) {
               for (Entity entity2 : getEntities(sender, text)) {
                  String entityName = getEntityName(sender, entity2.getUniqueID().toString());
                  if (scoreboard2.removePlayerFromTeams(entityName)) {
                     hashSet.add(entityName);
                  } else {
                     hashSet2.add(entityName);
                  }
               }
            } else {
               String entityName2 = getEntityName(sender, text);
               if (scoreboard2.removePlayerFromTeams(entityName2)) {
                  hashSet.add(entityName2);
               } else {
                  hashSet2.add(entityName2);
               }
            }
         }
      }

      if (!hashSet.isEmpty()) {
         sender.setCommandStat(net.minecraft.command.CommandResultStats.Type.AFFECTED_ENTITIES, hashSet.size());
         notifyOperators(sender, this, "commands.scoreboard.teams.leave.success", hashSet.size(), joinNiceString(hashSet.toArray(new String[hashSet.size()])));
      }

      if (!hashSet2.isEmpty()) {
         throw new CommandException("commands.scoreboard.teams.leave.failure", hashSet2.size(), joinNiceString(hashSet2.toArray(new String[hashSet2.size()])));
      }
   }

   protected void handleICommandSender2(ICommandSender iCommandSender, String[] texts) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      if (texts.length > 2) {
         ScorePlayerTeam team = this.getTeam(texts[2]);
         if (team == null) {
            return;
         }

         Collection membershipCollection = team.getMembershipCollection();
         iCommandSender.setCommandStat(net.minecraft.command.CommandResultStats.Type.QUERY_RESULT, membershipCollection.size());
         if (membershipCollection.size() <= 0) {
            throw new CommandException("commands.scoreboard.teams.list.player.empty", team.getRegisteredName());
         }

         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.scoreboard.teams.list.player.count", membershipCollection.size(), team.getRegisteredName());
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
         iCommandSender.addChatMessage(chatComponentTranslation);
         iCommandSender.addChatMessage(new ChatComponentText(joinNiceString(membershipCollection.toArray())));
      } else {
         Collection teams2 = scoreboard2.getTeams();
         iCommandSender.setCommandStat(net.minecraft.command.CommandResultStats.Type.QUERY_RESULT, teams2.size());
         if (teams2.size() <= 0) {
            throw new CommandException("commands.scoreboard.teams.list.empty");
         }

         ChatComponentTranslation chatComponentTranslation2 = new ChatComponentTranslation("commands.scoreboard.teams.list.count", teams2.size());
         chatComponentTranslation2.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
         iCommandSender.addChatMessage(chatComponentTranslation2);

         for (ScorePlayerTeam scorePlayerTeam : (Iterable<ScorePlayerTeam>) teams2) {
            iCommandSender.addChatMessage(
               new ChatComponentTranslation(
                  "commands.scoreboard.teams.list.entry", scorePlayerTeam.getRegisteredName(), scorePlayerTeam.getTeamName(), scorePlayerTeam.getMembershipCollection().size()
               )
            );
         }
      }
   }

   protected void handleICommandSender11(ICommandSender iCommandSender, String[] texts) throws CommandException {
      String text = texts[2];
      String text2 = texts[3];
      Scoreboard scoreboard2 = this.getScoreboard();
      IScoreObjectiveCriteria iScoreObjectiveCriteria = IScoreObjectiveCriteria.INSTANCES.get(text2);
      if (iScoreObjectiveCriteria == null) {
         throw new WrongUsageException("commands.scoreboard.objectives.add.wrongType", text2);
      }

      if (scoreboard2.getObjective(text) != null) {
         throw new CommandException("commands.scoreboard.objectives.add.alreadyExists", text);
      }

      if (text.length() > 16) {
         throw new SyntaxErrorException("commands.scoreboard.objectives.add.tooLong", text, 16);
      }

      if (text.length() == 0) {
         throw new WrongUsageException("commands.scoreboard.objectives.add.usage");
      }

      if (texts.length > 4) {
         String unformattedText = getChatComponentFromNthArg(iCommandSender, texts, 4).getUnformattedText();
         if (unformattedText.length() > 32) {
            throw new SyntaxErrorException("commands.scoreboard.objectives.add.displayTooLong", unformattedText, 32);
         }

         if (unformattedText.length() > 0) {
            scoreboard2.addScoreObjective(text, iScoreObjectiveCriteria).setDisplayName(unformattedText);
         } else {
            scoreboard2.addScoreObjective(text, iScoreObjectiveCriteria);
         }
      } else {
         scoreboard2.addScoreObjective(text, iScoreObjectiveCriteria);
      }

      notifyOperators(iCommandSender, this, "commands.scoreboard.objectives.add.success", text);
   }

   protected void handleICommandSender(ICommandSender iCommandSender, String[] texts) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      String playerName = getPlayerName(iCommandSender, texts[2]);
      if (playerName.length() > 40) {
         throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", playerName, 40);
      }

      ScoreObjective objective = this.getObjective(texts[3], false);
      if (objective.getCriteria() != IScoreObjectiveCriteria.TRIGGER) {
         throw new CommandException("commands.scoreboard.players.enable.noTrigger", objective.getName());
      }

      scoreboard2.getValueFromObjective(playerName, objective).setLocked(false);
      notifyOperators(iCommandSender, this, "commands.scoreboard.players.enable.success", objective.getName(), playerName);
   }

   protected void handleICommandSender10(ICommandSender iCommandSender, String[] texts) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      if (texts.length > 2) {
         String entityName = getEntityName(iCommandSender, texts[2]);
         Map objectivesForEntity = scoreboard2.getObjectivesForEntity(entityName);
         iCommandSender.setCommandStat(net.minecraft.command.CommandResultStats.Type.QUERY_RESULT, objectivesForEntity.size());
         if (objectivesForEntity.size() <= 0) {
            throw new CommandException("commands.scoreboard.players.list.player.empty", entityName);
         }

         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.scoreboard.players.list.player.count", objectivesForEntity.size(), entityName);
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
         iCommandSender.addChatMessage(chatComponentTranslation);

         for (Score score : (Iterable<Score>) objectivesForEntity.values()) {
            iCommandSender.addChatMessage(
               new ChatComponentTranslation(
                  "commands.scoreboard.players.list.player.entry",
                  score.getScorePoints(),
                  score.getObjective().getDisplayName(),
                  score.getObjective().getName()
               )
            );
         }
      } else {
         Collection objectiveNames = scoreboard2.getObjectiveNames();
         iCommandSender.setCommandStat(net.minecraft.command.CommandResultStats.Type.QUERY_RESULT, objectiveNames.size());
         if (objectiveNames.size() <= 0) {
            throw new CommandException("commands.scoreboard.players.list.empty");
         }

         ChatComponentTranslation chatComponentTranslation2 = new ChatComponentTranslation("commands.scoreboard.players.list.count", objectiveNames.size());
         chatComponentTranslation2.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
         iCommandSender.addChatMessage(chatComponentTranslation2);
         iCommandSender.addChatMessage(new ChatComponentText(joinNiceString(objectiveNames.toArray())));
      }
   }

   protected List<String> getObjectiveNames(boolean writableOnly) {
      Collection scoreObjectives = this.getScoreboard().getScoreObjectives();
      ArrayList arrayList = Lists.newArrayList();

      for (ScoreObjective scoreObjective2 : (Iterable<ScoreObjective>) scoreObjectives) {
         if (!writableOnly || !scoreObjective2.getCriteria().isReadOnly()) {
            arrayList.add(scoreObjective2.getName());
         }
      }

      return arrayList;
   }

   protected void removeObjective(ICommandSender sender, String objectiveName) throws CommandException {
      Scoreboard scoreboard = this.getScoreboard();
      ScoreObjective scoreObjective = this.getObjective(objectiveName, false);
      scoreboard.removeObjective(scoreObjective);
      notifyOperators(sender, this, "commands.scoreboard.objectives.remove.success", objectiveName);
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   protected void handleICommandSender12(ICommandSender iCommandSender, String[] texts) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      String entityName = getEntityName(iCommandSender, texts[2]);
      if (entityName.length() > 40) {
         throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", entityName, 40);
      }

      ScoreObjective objective = this.getObjective(texts[3], false);
      if (!scoreboard2.entityHasObjective(entityName, objective)) {
         throw new CommandException("commands.scoreboard.players.test.notFound", objective.getName(), entityName);
      }

      int i = texts[4].equals("*") ? Integer.MIN_VALUE : parseInt(texts[4]);
      int i2 = 5 < texts.length && !texts[5].equals("*") ? parseInt(texts[5], i) : Integer.MAX_VALUE;
      Score valueFromObjective = scoreboard2.getValueFromObjective(entityName, objective);
      if (valueFromObjective.getScorePoints() >= i && valueFromObjective.getScorePoints() <= i2) {
         notifyOperators(iCommandSender, this, "commands.scoreboard.players.test.success", valueFromObjective.getScorePoints(), i, i2);
      } else {
         throw new CommandException("commands.scoreboard.players.test.failed", valueFromObjective.getScorePoints(), i, i2);
      }
   }
}
