package net.minecraft.scoreboard;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldSavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScoreboardSaveData extends WorldSavedData {
   private NBTTagCompound delayedInitNbt;
   private Scoreboard theScoreboard;
   private static final Logger logger = LogManager.getLogger();

   public void setScoreboard(Scoreboard scoreboardIn) {
      this.theScoreboard = scoreboardIn;
      if (this.delayedInitNbt != null) {
         this.readFromNBT(this.delayedInitNbt);
      }
   }

   protected void writeDisplaySlotsToNBT(NBTTagCompound nbt) {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      boolean conditionMet = false;

      for (int index = 0; index < 19; index++) {
         ScoreObjective scoreObjective = this.theScoreboard.getObjectiveInDisplaySlot(index);
         if (scoreObjective != null) {
            nbtTagCompound.setString("slot_" + index, scoreObjective.getName());
            conditionMet = true;
         }
      }

      if (conditionMet) {
         nbt.setTag("DisplaySlots", nbtTagCompound);
      }
   }

   protected void readObjectives(NBTTagList nbt) {
      for (int index = 0; index < nbt.tagCount(); index++) {
         NBTTagCompound nBTTagCompound = nbt.getCompoundTagAt(index);
         IScoreObjectiveCriteria scoreObjectiveCriteria = IScoreObjectiveCriteria.INSTANCES.get(nBTTagCompound.getString("CriteriaName"));
         if (scoreObjectiveCriteria != null) {
            String text = nBTTagCompound.getString("Name");
            if (text.length() > 16) {
               text = text.substring(0, 16);
            }

            ScoreObjective scoreObjective = this.theScoreboard.addScoreObjective(text, scoreObjectiveCriteria);
            scoreObjective.setDisplayName(nBTTagCompound.getString("DisplayName"));
            scoreObjective.setRenderType(IScoreObjectiveCriteria.EnumRenderType.getType(nBTTagCompound.getString("RenderType")));
         }
      }
   }

   protected void readDisplayConfig(NBTTagCompound nbt) {
      for (int index = 0; index < 19; index++) {
         if (nbt.hasKey("slot_" + index, 8)) {
            String text = nbt.getString("slot_" + index);
            ScoreObjective scoreObjective = this.theScoreboard.getObjective(text);
            this.theScoreboard.setObjectiveInDisplaySlot(index, scoreObjective);
         }
      }
   }

   protected NBTTagList writeTeamsToNBT() {
      NBTTagList nbtTagList = new NBTTagList();

      for (ScorePlayerTeam scorePlayerTeam2 : this.theScoreboard.getTeams()) {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         nbtTagCompound.setString("Name", scorePlayerTeam2.getRegisteredName());
         nbtTagCompound.setString("DisplayName", scorePlayerTeam2.getTeamName());
         if (scorePlayerTeam2.getChatFormat().getColorIndex() >= 0) {
            nbtTagCompound.setString("TeamColor", scorePlayerTeam2.getChatFormat().getFriendlyName());
         }

         nbtTagCompound.setString("Prefix", scorePlayerTeam2.getColorPrefix());
         nbtTagCompound.setString("Suffix", scorePlayerTeam2.getColorSuffix());
         nbtTagCompound.setBoolean("AllowFriendlyFire", scorePlayerTeam2.getAllowFriendlyFire());
         nbtTagCompound.setBoolean("SeeFriendlyInvisibles", scorePlayerTeam2.getSeeFriendlyInvisiblesEnabled());
         nbtTagCompound.setString("NameTagVisibility", scorePlayerTeam2.getNameTagVisibility().internalName);
         nbtTagCompound.setString("DeathMessageVisibility", scorePlayerTeam2.getDeathMessageVisibility().internalName);
         NBTTagList nbtTagList2 = new NBTTagList();

         for (String text3 : scorePlayerTeam2.getMembershipCollection()) {
            nbtTagList2.appendTag(new NBTTagString(text3));
         }

         nbtTagCompound.setTag("Players", nbtTagList2);
         nbtTagList.appendTag(nbtTagCompound);
      }

      return nbtTagList;
   }

   protected void readPlayers(ScorePlayerTeam team, NBTTagList players) {
      for (int index = 0; index < players.tagCount(); index++) {
         this.theScoreboard.addPlayerToTeam(players.getStringTagAt(index), team.getRegisteredName());
      }
   }

   protected NBTTagList scoresToNbt() {
      NBTTagList nbtTagList = new NBTTagList();

      for (Score score2 : this.theScoreboard.getScores()) {
         if (score2.getObjective() != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setString("Name", score2.getPlayerName());
            nbtTagCompound.setString("Objective", score2.getObjective().getName());
            nbtTagCompound.setInteger("Score", score2.getScorePoints());
            nbtTagCompound.setBoolean("Locked", score2.isLocked());
            nbtTagList.appendTag(nbtTagCompound);
         }
      }

      return nbtTagList;
   }

   public ScoreboardSaveData(String name) {
      super(name);
   }

   @Override
   public void readFromNBT(NBTTagCompound nbt) {
      if (this.theScoreboard == null) {
         this.delayedInitNbt = nbt;
      } else {
         this.readObjectives(nbt.getTagList("Objectives", 10));
         this.readScores(nbt.getTagList("PlayerScores", 10));
         if (nbt.hasKey("DisplaySlots", 10)) {
            this.readDisplayConfig(nbt.getCompoundTag("DisplaySlots"));
         }

         if (nbt.hasKey("Teams", 9)) {
            this.readTeams(nbt.getTagList("Teams", 10));
         }
      }
   }

   public ScoreboardSaveData() {
      this("scoreboard");
   }

   @Override
   public void writeToNBT(NBTTagCompound nbt) {
      if (this.theScoreboard == null) {
         logger.warn("Tried to save scoreboard without having a scoreboard...");
      } else {
         nbt.setTag("Objectives", this.objectivesToNbt());
         nbt.setTag("PlayerScores", this.scoresToNbt());
         nbt.setTag("Teams", this.writeTeamsToNBT());
         this.writeDisplaySlotsToNBT(nbt);
      }
   }

   protected NBTTagList objectivesToNbt() {
      NBTTagList nbtTagList = new NBTTagList();

      for (ScoreObjective scoreObjective2 : this.theScoreboard.getScoreObjectives()) {
         if (scoreObjective2.getCriteria() != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setString("Name", scoreObjective2.getName());
            nbtTagCompound.setString("CriteriaName", scoreObjective2.getCriteria().getName());
            nbtTagCompound.setString("DisplayName", scoreObjective2.getDisplayName());
            nbtTagCompound.setString("RenderType", scoreObjective2.getRenderType().getName());
            nbtTagList.appendTag(nbtTagCompound);
         }
      }

      return nbtTagList;
   }

   protected void readScores(NBTTagList nbt) {
      for (int index = 0; index < nbt.tagCount(); index++) {
         NBTTagCompound nBTTagCompound = nbt.getCompoundTagAt(index);
         ScoreObjective scoreObjective = this.theScoreboard.getObjective(nBTTagCompound.getString("Objective"));
         String text = nBTTagCompound.getString("Name");
         if (text.length() > 40) {
            text = text.substring(0, 40);
         }

         Score score = this.theScoreboard.getValueFromObjective(text, scoreObjective);
         score.setScorePoints(nBTTagCompound.getInteger("Score"));
         if (nBTTagCompound.hasKey("Locked")) {
            score.setLocked(nBTTagCompound.getBoolean("Locked"));
         }
      }
   }

   protected void readTeams(NBTTagList nbt) {
      for (int index = 0; index < nbt.tagCount(); index++) {
         NBTTagCompound nBTTagCompound = nbt.getCompoundTagAt(index);
         String text = nBTTagCompound.getString("Name");
         if (text.length() > 16) {
            text = text.substring(0, 16);
         }

         ScorePlayerTeam scorePlayerTeam = this.theScoreboard.createTeam(text);
         String text2 = nBTTagCompound.getString("DisplayName");
         if (text2.length() > 32) {
            text2 = text2.substring(0, 32);
         }

         scorePlayerTeam.setTeamName(text2);
         if (nBTTagCompound.hasKey("TeamColor", 8)) {
            scorePlayerTeam.setChatFormat(EnumChatFormatting.getValueByName(nBTTagCompound.getString("TeamColor")));
         }

         scorePlayerTeam.setNamePrefix(nBTTagCompound.getString("Prefix"));
         scorePlayerTeam.setNameSuffix(nBTTagCompound.getString("Suffix"));
         if (nBTTagCompound.hasKey("AllowFriendlyFire", 99)) {
            scorePlayerTeam.setAllowFriendlyFire(nBTTagCompound.getBoolean("AllowFriendlyFire"));
         }

         if (nBTTagCompound.hasKey("SeeFriendlyInvisibles", 99)) {
            scorePlayerTeam.setSeeFriendlyInvisiblesEnabled(nBTTagCompound.getBoolean("SeeFriendlyInvisibles"));
         }

         if (nBTTagCompound.hasKey("NameTagVisibility", 8)) {
            Team.EnumVisible team$enumvisible = Team.EnumVisible.byInternalName(nBTTagCompound.getString("NameTagVisibility"));
            if (team$enumvisible != null) {
               scorePlayerTeam.setNameTagVisibility(team$enumvisible);
            }
         }

         if (nBTTagCompound.hasKey("DeathMessageVisibility", 8)) {
            Team.EnumVisible team$enumvisible1 = Team.EnumVisible.byInternalName(nBTTagCompound.getString("DeathMessageVisibility"));
            if (team$enumvisible1 != null) {
               scorePlayerTeam.setDeathMessageVisibility(team$enumvisible1);
            }
         }

         this.readPlayers(scorePlayerTeam, nBTTagCompound.getTagList("Players", 8));
      }
   }
}
