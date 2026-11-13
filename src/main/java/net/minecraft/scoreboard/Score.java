package net.minecraft.scoreboard;

import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class Score {
   private int scorePoints;
   private final Scoreboard theScoreboard;
   private boolean locked;
   public static final Comparator<Score> scoreComparator = new Comparator<Score>() {
      public int compare(Score left, Score right) {
         return left.getScorePoints() > right.getScorePoints()
            ? 1
            : (left.getScorePoints() < right.getScorePoints() ? -1 : right.getPlayerName().compareToIgnoreCase(left.getPlayerName()));
      }
   };
   private final ScoreObjective theScoreObjective;
   private final String scorePlayerName;
   private boolean forceUpdate;

   public String getPlayerName() {
      return this.scorePlayerName;
   }

   public ScoreObjective getObjective() {
      return this.theScoreObjective;
   }

   public void setLocked(boolean locked) {
      this.locked = locked;
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setScoreFromPlayers(List<EntityPlayer> players) {
      this.setScorePoints(this.theScoreObjective.getCriteria().setScore(players));
   }

   public void incrementScore() {
      if (this.theScoreObjective.getCriteria().isReadOnly()) {
         throw new IllegalStateException("Cannot modify read-only score");
      }

      this.increseScore(1);
   }

   public int getScorePoints() {
      return this.scorePoints;
   }

   public void increseScore(int amount) {
      if (this.theScoreObjective.getCriteria().isReadOnly()) {
         throw new IllegalStateException("Cannot modify read-only score");
      }

      this.setScorePoints(this.getScorePoints() + amount);
   }

   public void setScorePoints(int points) {
      int scorePoints2 = this.scorePoints;
      this.scorePoints = points;
      if (scorePoints2 != points || this.forceUpdate) {
         this.forceUpdate = false;
         this.getScoreScoreboard().onScoreUpdated(this);
      }
   }

   public Score(Scoreboard theScoreboardIn, ScoreObjective theScoreObjectiveIn, String scorePlayerNameIn) {
      this.theScoreboard = theScoreboardIn;
      this.theScoreObjective = theScoreObjectiveIn;
      this.scorePlayerName = scorePlayerNameIn;
      this.forceUpdate = true;
   }

   public Scoreboard getScoreScoreboard() {
      return this.theScoreboard;
   }

   public void decreaseScore(int amount) {
      if (this.theScoreObjective.getCriteria().isReadOnly()) {
         throw new IllegalStateException("Cannot modify read-only score");
      }

      this.setScorePoints(this.getScorePoints() - amount);
   }
}
