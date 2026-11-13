package net.minecraft.command;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class CommandResultStats {
   private static final int NUM_RESULT_TYPES = CommandResultStats.Type.values().length;
   private static final String[] STRING_RESULT_TYPES = new String[NUM_RESULT_TYPES];
   private String[] entitiesID = STRING_RESULT_TYPES;
   private String[] objectives = STRING_RESULT_TYPES;

   public void writeStatsToNBT(NBTTagCompound tagcompound) {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();

      for (CommandResultStats.Type type2 : CommandResultStats.Type.values()) {
         String text = this.entitiesID[type2.getTypeID()];
         String text2 = this.objectives[type2.getTypeID()];
         if (text != null && text2 != null) {
            nbtTagCompound.setString(type2.getTypeName() + "Name", text);
            nbtTagCompound.setString(type2.getTypeName() + "Objective", text2);
         }
      }

      if (!nbtTagCompound.hasNoTags()) {
         tagcompound.setTag("CommandStats", nbtTagCompound);
      }
   }

   private static void removeScoreBoardStat(CommandResultStats resultStatsIn, CommandResultStats.Type resultTypeIn) {
      if (resultStatsIn.entitiesID != STRING_RESULT_TYPES && resultStatsIn.objectives != STRING_RESULT_TYPES) {
         resultStatsIn.entitiesID[resultTypeIn.getTypeID()] = null;
         resultStatsIn.objectives[resultTypeIn.getTypeID()] = null;
         boolean flag = true;

         for (CommandResultStats.Type type2 : CommandResultStats.Type.values()) {
            if (resultStatsIn.entitiesID[type2.getTypeID()] != null && resultStatsIn.objectives[type2.getTypeID()] != null) {
               flag = false;
               break;
            }
         }

         if (flag) {
            resultStatsIn.entitiesID = STRING_RESULT_TYPES;
            resultStatsIn.objectives = STRING_RESULT_TYPES;
         }
      }
   }

   public void setCommandStatScore(final ICommandSender sender, CommandResultStats.Type resultTypeIn, int scorePoint) {
      String text = this.entitiesID[resultTypeIn.getTypeID()];
      if (text != null) {
         ICommandSender iCommandSender = new ICommandSender() {
            @Override
            public boolean sendCommandFeedback() {
               return sender.sendCommandFeedback();
            }

            @Override
            public BlockPos getPosition() {
               return sender.getPosition();
            }

            @Override
            public IChatComponent getDisplayName() {
               return sender.getDisplayName();
            }

            @Override
            public String getName() {
               return sender.getName();
            }

            @Override
            public Vec3 getPositionVector() {
               return sender.getPositionVector();
            }

            @Override
            public void addChatMessage(IChatComponent component) {
               sender.addChatMessage(component);
            }

            @Override
            public void setCommandStat(CommandResultStats.Type type, int amount) {
               sender.setCommandStat(type, amount);
            }

            @Override
            public World getEntityWorld() {
               return sender.getEntityWorld();
            }

            @Override
            public Entity getCommandSenderEntity() {
               return sender.getCommandSenderEntity();
            }

            @Override
            public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
               return true;
            }
         };

         String text2;
         try {
            text2 = CommandBase.getEntityName(iCommandSender, text);
         } catch (EntityNotFoundException entityNotFoundException) {
            return;
         }

         String text3 = this.objectives[resultTypeIn.getTypeID()];
         if (text3 != null) {
            Scoreboard scoreboard2 = sender.getEntityWorld().getScoreboard();
            ScoreObjective objective = scoreboard2.getObjective(text3);
            if (objective != null && scoreboard2.entityHasObjective(text2, objective)) {
               scoreboard2.getValueFromObjective(text2, objective).setScorePoints(scorePoint);
            }
         }
      }
   }

   public void addAllStats(CommandResultStats resultStatsIn) {
      for (CommandResultStats.Type type2 : CommandResultStats.Type.values()) {
         setScoreBoardStat(this, type2, resultStatsIn.entitiesID[type2.getTypeID()], resultStatsIn.objectives[type2.getTypeID()]);
      }
   }

   public static void setScoreBoardStat(CommandResultStats stats, CommandResultStats.Type resultType, String entityID, String objectiveName) {
      if (entityID != null && entityID.length() != 0 && objectiveName != null && objectiveName.length() != 0) {
         if (stats.entitiesID == STRING_RESULT_TYPES || stats.objectives == STRING_RESULT_TYPES) {
            stats.entitiesID = new String[NUM_RESULT_TYPES];
            stats.objectives = new String[NUM_RESULT_TYPES];
         }

         stats.entitiesID[resultType.getTypeID()] = entityID;
         stats.objectives[resultType.getTypeID()] = objectiveName;
      } else {
         removeScoreBoardStat(stats, resultType);
      }
   }

   public void readStatsFromNBT(NBTTagCompound tagcompound) {
      if (tagcompound.hasKey("CommandStats", 10)) {
         NBTTagCompound compoundTag = tagcompound.getCompoundTag("CommandStats");

         for (CommandResultStats.Type type2 : CommandResultStats.Type.values()) {
            String text = type2.getTypeName() + "Name";
            String text2 = type2.getTypeName() + "Objective";
            if (compoundTag.hasKey(text, 8) && compoundTag.hasKey(text2, 8)) {
               String string = compoundTag.getString(text);
               String string2 = compoundTag.getString(text2);
               setScoreBoardStat(this, type2, string, string2);
            }
         }
      }
   }

   public enum Type {
      SUCCESS_COUNT(0, "SuccessCount"),
      AFFECTED_BLOCKS(1, "AffectedBlocks"),
      AFFECTED_ENTITIES(2, "AffectedEntities"),
      AFFECTED_ITEMS(3, "AffectedItems"),
      QUERY_RESULT(4, "QueryResult");

      final int typeID;
      final String typeName;

      public String getTypeName() {
         return this.typeName;
      }

      public static String[] getTypeNames() {
         String[] texts = new String[values().length];
         int i = 0;

         for (CommandResultStats.Type type2 : values()) {
            texts[i++] = type2.getTypeName();
         }

         return texts;
      }

      public int getTypeID() {
         return this.typeID;
      }

      Type(int i, String text) {
         this.typeID = i;
         this.typeName = text;
      }

      public static CommandResultStats.Type getTypeByName(String name) {
         for (CommandResultStats.Type type2 : values()) {
            if (type2.getTypeName().equals(name)) {
               return type2;
            }
         }

         return null;
      }
   }
}
