package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.server.MinecraftServer;

public class ServerScoreboard extends Scoreboard {
   private final Set<ScoreObjective> trackedObjectives = Sets.newHashSet();
   private ScoreboardSaveData scoreboardSaveData;
   private final MinecraftServer scoreboardMCServer;

   @Override
   public void broadcastTeamRemoved(ScorePlayerTeam scorePlayerTeam2) {
      super.broadcastTeamRemoved(scorePlayerTeam2);
      this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(scorePlayerTeam2, 1));
      this.markSaveDataDirty();
   }

   @Override
   public void sendTeamUpdate(ScorePlayerTeam scorePlayerTeam2) {
      super.sendTeamUpdate(scorePlayerTeam2);
      this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(scorePlayerTeam2, 2));
      this.markSaveDataDirty();
   }

   protected void markSaveDataDirty() {
      if (this.scoreboardSaveData != null) {
         this.scoreboardSaveData.markDirty();
      }
   }

   @Override
   public void setObjectiveInDisplaySlot(int slot, ScoreObjective objective) {
      ScoreObjective scoreObjective = this.getObjectiveInDisplaySlot(slot);
      super.setObjectiveInDisplaySlot(slot, objective);
      if (scoreObjective != objective && scoreObjective != null) {
         if (this.getObjectiveDisplaySlotCount(scoreObjective) > 0) {
            this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3DPacketDisplayScoreboard(slot, objective));
         } else {
            this.sendDisplaySlotRemovalPackets(scoreObjective);
         }
      }

      if (objective != null) {
         if (this.trackedObjectives.contains(objective)) {
            this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3DPacketDisplayScoreboard(slot, objective));
         } else {
            this.startTrackingObjective(objective);
         }
      }

      this.markSaveDataDirty();
   }

   @Override
   public boolean addPlayerToTeam(String player, String newTeam) {
      if (super.addPlayerToTeam(player, newTeam)) {
         ScorePlayerTeam scorePlayerTeam = this.getTeam(newTeam);
         this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(scorePlayerTeam, Arrays.asList(player), 3));
         this.markSaveDataDirty();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void broadcastTeamCreated(ScorePlayerTeam scorePlayerTeam2) {
      super.broadcastTeamCreated(scorePlayerTeam2);
      this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(scorePlayerTeam2, 0));
      this.markSaveDataDirty();
   }

   public List<Packet> getCreatePackets(ScoreObjective objective) {
      ArrayList arrayList = Lists.newArrayList();
      arrayList.add(new S3BPacketScoreboardObjective(objective, 0));

      for (int i = 0; i < 19; i++) {
         if (this.getObjectiveInDisplaySlot(i) == objective) {
            arrayList.add(new S3DPacketDisplayScoreboard(i, objective));
         }
      }

      for (Score score2 : this.getSortedScores(objective)) {
         arrayList.add(new S3CPacketUpdateScore(score2));
      }

      return arrayList;
   }

   public ServerScoreboard(MinecraftServer mcServer) {
      this.scoreboardMCServer = mcServer;
   }

   @Override
   public void onEntityScoreRemoved(String entityName) {
      super.onEntityScoreRemoved(entityName);
      this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3CPacketUpdateScore(entityName));
      this.markSaveDataDirty();
   }

   @Override
   public void onObjectiveDisplayNameChanged(ScoreObjective objective) {
      super.onObjectiveDisplayNameChanged(objective);
      if (this.trackedObjectives.contains(objective)) {
         this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3BPacketScoreboardObjective(objective, 2));
      }

      this.markSaveDataDirty();
   }

   @Override
   public void onEntityScoreRemoved(String entityName, ScoreObjective objective) {
      super.onEntityScoreRemoved(entityName, objective);
      this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3CPacketUpdateScore(entityName, objective));
      this.markSaveDataDirty();
   }

   public List<Packet> getDestroyPackets(ScoreObjective objective) {
      List<Packet> list = Lists.newArrayList();
      list.add(new S3BPacketScoreboardObjective(objective, 1));

      for (int index = 0; index < 19; index++) {
         if (this.getObjectiveInDisplaySlot(index) == objective) {
            list.add(new S3DPacketDisplayScoreboard(index, objective));
         }
      }

      return list;
   }

   @Override
   public void onScoreObjectiveAdded(ScoreObjective scoreObjectiveIn) {
      super.onScoreObjectiveAdded(scoreObjectiveIn);
      this.markSaveDataDirty();
   }

   @Override
   public void removePlayerFromTeam(String player, ScorePlayerTeam team) {
      super.removePlayerFromTeam(player, team);
      this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(team, Arrays.asList(player), 4));
      this.markSaveDataDirty();
   }

   public int getObjectiveDisplaySlotCount(ScoreObjective objective) {
      int i = 0;

      for (int innerIndex = 0; innerIndex < 19; innerIndex++) {
         if (this.getObjectiveInDisplaySlot(innerIndex) == objective) {
            i++;
         }
      }

      return i;
   }

   public void setScoreboardSaveData(ScoreboardSaveData saveData) {
      this.scoreboardSaveData = saveData;
   }

   @Override
   public void onScoreObjectiveRemoved(ScoreObjective objective) {
      super.onScoreObjectiveRemoved(objective);
      if (this.trackedObjectives.contains(objective)) {
         this.sendDisplaySlotRemovalPackets(objective);
      }

      this.markSaveDataDirty();
   }

   public void sendDisplaySlotRemovalPackets(ScoreObjective objective) {
      List destroyPackets = this.getDestroyPackets(objective);

      for (EntityPlayerMP entityPlayerMP : this.scoreboardMCServer.getConfigurationManager().getPlayerList()) {
         for (Packet packet : (Iterable<Packet>) destroyPackets) {
            entityPlayerMP.playerNetServerHandler.sendPacket(packet);
         }
      }

      this.trackedObjectives.remove(objective);
   }

   public void startTrackingObjective(ScoreObjective objective) {
      List createPackets = this.getCreatePackets(objective);

      for (EntityPlayerMP entityPlayerMP : this.scoreboardMCServer.getConfigurationManager().getPlayerList()) {
         for (Packet packet : (Iterable<Packet>) createPackets) {
            entityPlayerMP.playerNetServerHandler.sendPacket(packet);
         }
      }

      this.trackedObjectives.add(objective);
   }

   @Override
   public void onScoreUpdated(Score score) {
      super.onScoreUpdated(score);
      if (this.trackedObjectives.contains(score.getObjective())) {
         this.scoreboardMCServer.getConfigurationManager().sendPacketToAllPlayers(new S3CPacketUpdateScore(score));
      }

      this.markSaveDataDirty();
   }
}
