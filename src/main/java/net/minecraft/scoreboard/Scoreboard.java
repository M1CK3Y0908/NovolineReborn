package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

public class Scoreboard {
   private final Map<String, ScoreObjective> scoreObjectives = Maps.newHashMap();
   private final Map<IScoreObjectiveCriteria, List<ScoreObjective>> scoreObjectiveCriterias = Maps.newHashMap();
   private final Map<String, Map<ScoreObjective, Score>> entitiesScoreObjectives = Maps.newHashMap();
   private static String[] displaySlots = null;
   private final ScoreObjective[] objectiveDisplaySlots = new ScoreObjective[19];
   private final Map<String, ScorePlayerTeam> teams = Maps.newHashMap();
   private final Map<String, ScorePlayerTeam> teamMemberships = Maps.newHashMap();

   public Collection<Score> getSortedScores(ScoreObjective objective) {
      List<Score> list = Lists.newArrayList();
      Iterator iterator2 = this.entitiesScoreObjectives.values().iterator();

      while (iterator2.hasNext()) {
         Score score = (Score)((Map)iterator2.next()).get(objective);
         if (score != null) {
            list.add(score);
         }
      }

      Collections.sort(list, Score.scoreComparator);
      return list;
   }

   public void broadcastTeamCreated(ScorePlayerTeam playerTeam) {
   }

   public void removeObjective(ScoreObjective objective) {
      this.scoreObjectives.remove(objective.getName());

      for (int index = 0; index < 19; index++) {
         if (this.getObjectiveInDisplaySlot(index) == objective) {
            this.setObjectiveInDisplaySlot(index, (ScoreObjective)null);
         }
      }

      List<ScoreObjective> list = this.scoreObjectiveCriterias.get(objective.getCriteria());
      if (list != null) {
         list.remove(objective);
      }

      Iterator iterator2 = this.entitiesScoreObjectives.values().iterator();

      while (iterator2.hasNext()) {
         ((Map)iterator2.next()).remove(objective);
      }

      this.onScoreObjectiveRemoved(objective);
   }

   public static String getObjectiveDisplaySlot(int slot) {
      switch (slot) {
         case 0:
            return "list";
         case 1:
            return "sidebar";
         case 2:
            return "belowName";
         default:
            if (slot >= 3 && slot <= 18) {
               EnumChatFormatting chatFormatting = EnumChatFormatting.getByColorIndex(slot - 3);
               if (chatFormatting != null && chatFormatting != EnumChatFormatting.RESET) {
                  return "sidebar.team." + chatFormatting.getFriendlyName();
               }
            }

            return null;
      }
   }

   public Collection<ScoreObjective> getObjectivesFromCriteria(IScoreObjectiveCriteria criteria) {
      Collection<ScoreObjective> collection = this.scoreObjectiveCriterias.get(criteria);
      return collection == null ? Lists.newArrayList() : Lists.newArrayList(collection);
   }

   public ScoreObjective getObjectiveInDisplaySlot(int slot) {
      return this.objectiveDisplaySlots[slot];
   }

   public Collection<String> getTeamNames() {
      return this.teams.keySet();
   }

   public void onScoreObjectiveAdded(ScoreObjective scoreObjectiveIn) {
   }

   public void removeTeam(ScorePlayerTeam team) {
      this.teams.remove(team.getRegisteredName());

      for (String text2 : team.getMembershipCollection()) {
         this.teamMemberships.remove(text2);
      }

      this.broadcastTeamRemoved(team);
   }

   public void removeObjectiveFromEntity(String name, ScoreObjective objective) {
      if (objective == null) {
         Map map2 = this.entitiesScoreObjectives.remove(name);
         if (map2 != null) {
            this.onEntityScoreRemoved(name);
         }
      } else {
         Map map3 = this.entitiesScoreObjectives.get(name);
         if (map3 != null) {
            Score score2 = (Score)map3.remove(objective);
            if (map3.size() < 1) {
               if ((Map)this.entitiesScoreObjectives.remove(name) != null) {
                  this.onEntityScoreRemoved(name);
               }
            } else if (score2 != null) {
               this.onEntityScoreRemoved(name, objective);
            }
         }
      }
   }

   public ScorePlayerTeam getPlayersTeam(String player) {
      return this.teamMemberships.get(player);
   }

   public void onScoreObjectiveRemoved(ScoreObjective objective) {
   }

   public void removeEntity(Entity entity) {
      if (entity != null && !(entity instanceof EntityPlayer) && !entity.isEntityAlive()) {
         String text = entity.getUniqueID().toString();
         this.removeObjectiveFromEntity(text, (ScoreObjective)null);
         this.removePlayerFromTeams(text);
      }
   }

   public static int getObjectiveDisplaySlotNumber(String slotName) {
      if (slotName.equalsIgnoreCase("list")) {
         return 0;
      }

      if (slotName.equalsIgnoreCase("sidebar")) {
         return 1;
      }

      if (slotName.equalsIgnoreCase("belowName")) {
         return 2;
      }

      if (slotName.startsWith("sidebar.team.")) {
         EnumChatFormatting valueByName = EnumChatFormatting.getValueByName(slotName.substring("sidebar.team.".length()));
         if (valueByName != null && valueByName.getColorIndex() >= 0) {
            return valueByName.getColorIndex() + 3;
         }
      }

      return -1;
   }

   public boolean addPlayerToTeam(String player, String newTeam) {
      if (player.length() > 40) {
         throw new IllegalArgumentException("The player name '" + player + "' is too long!");
      }

      if (!this.teams.containsKey(newTeam)) {
         return false;
      }

      ScorePlayerTeam scorePlayerTeam = this.getTeam(newTeam);
      if (this.getPlayersTeam(player) != null) {
         this.removePlayerFromTeams(player);
      }

      this.teamMemberships.put(player, scorePlayerTeam);
      scorePlayerTeam.getMembershipCollection().add(player);
      return true;
   }

   public void onObjectiveDisplayNameChanged(ScoreObjective objective) {
   }

   public void removePlayerFromTeam(String player, ScorePlayerTeam team) {
      if (this.getPlayersTeam(player) != team) {
         throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + team.getRegisteredName() + "'.");
      }

      this.teamMemberships.remove(player);
      team.getMembershipCollection().remove(player);
   }

   public ScorePlayerTeam getTeam(String teamName) {
      return this.teams.get(teamName);
   }

   public void onEntityScoreRemoved(String entityName, ScoreObjective objective) {
   }

   public boolean removePlayerFromTeams(String player) {
      ScorePlayerTeam scorePlayerTeam = this.getPlayersTeam(player);
      if (scorePlayerTeam != null) {
         this.removePlayerFromTeam(player, scorePlayerTeam);
         return true;
      } else {
         return false;
      }
   }

   public ScoreObjective getObjective(String name) {
      return this.scoreObjectives.get(name);
   }

   public Collection<ScorePlayerTeam> getTeams() {
      return this.teams.values();
   }

   public ScorePlayerTeam createTeam(String name) {
      if (name.length() > 16) {
         throw new IllegalArgumentException("The team name '" + name + "' is too long!");
      }

      ScorePlayerTeam team2 = this.getTeam(name);
      if (team2 != null) {
         throw new IllegalArgumentException("A team with the name '" + name + "' already exists!");
      }

      team2 = new ScorePlayerTeam(this, name);
      this.teams.put(name, team2);
      this.broadcastTeamCreated(team2);
      return team2;
   }

   public void onEntityScoreRemoved(String entityName) {
   }

   public ScoreObjective addScoreObjective(String name, IScoreObjectiveCriteria criteria) {
      if (name.length() > 16) {
         throw new IllegalArgumentException("The objective name '" + name + "' is too long!");
      }

      ScoreObjective objective2 = this.getObjective(name);
      if (objective2 != null) {
         throw new IllegalArgumentException("An objective with the name '" + name + "' already exists!");
      }

      objective2 = new ScoreObjective(this, name, criteria);
      List list2 = this.scoreObjectiveCriterias.get(criteria);
      if (list2 == null) {
         list2 = Lists.newArrayList();
         this.scoreObjectiveCriterias.put(criteria, list2);
      }

      list2.add(objective2);
      this.scoreObjectives.put(name, objective2);
      this.onScoreObjectiveAdded(objective2);
      return objective2;
   }

   public Collection<ScoreObjective> getScoreObjectives() {
      return this.scoreObjectives.values();
   }

   public static String[] getDisplaySlotStrings() {
      if (displaySlots == null) {
         displaySlots = new String[19];

         for (int index = 0; index < 19; index++) {
            displaySlots[index] = getObjectiveDisplaySlot(index);
         }
      }

      return displaySlots;
   }

   public void broadcastTeamRemoved(ScorePlayerTeam playerTeam) {
   }

   public boolean entityHasObjective(String name, ScoreObjective objective) {
      Map<ScoreObjective, Score> map = this.entitiesScoreObjectives.get(name);
      return map == null ? false : map.get(objective) != null;
   }

   public Map<ScoreObjective, Score> getObjectivesForEntity(String name) {
      Map<ScoreObjective, Score> map = this.entitiesScoreObjectives.get(name);
      if (map == null) {
         map = Maps.newHashMap();
      }

      return map;
   }

   public void setObjectiveInDisplaySlot(int slot, ScoreObjective objective) {
      this.objectiveDisplaySlots[slot] = objective;
   }

   public Collection<Score> getScores() {
      Collection collection2 = this.entitiesScoreObjectives.values();
      ArrayList arrayList = Lists.newArrayList();

      for (Map map2 : (Iterable<Map>) collection2) {
         arrayList.addAll(map2.values());
      }

      return arrayList;
   }

   public Collection<String> getObjectiveNames() {
      return this.entitiesScoreObjectives.keySet();
   }

   public Score getValueFromObjective(String name, ScoreObjective objective) {
      if (name.length() > 40) {
         throw new IllegalArgumentException("The player name '" + name + "' is too long!");
      }

      Map<ScoreObjective, Score> map = this.entitiesScoreObjectives.get(name);
      if (map == null) {
         map = Maps.newHashMap();
         this.entitiesScoreObjectives.put(name, map);
      }

      Score score = map.get(objective);
      if (score == null) {
         score = new Score(this, objective, name);
         map.put(objective, score);
      }

      return score;
   }

   public void onScoreUpdated(Score score) {
   }

   public void sendTeamUpdate(ScorePlayerTeam playerTeam) {
   }
}
