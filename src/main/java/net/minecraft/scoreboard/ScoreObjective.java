package net.minecraft.scoreboard;

public class ScoreObjective {
   private final Scoreboard theScoreboard;
   private final String name;
   private final IScoreObjectiveCriteria objectiveCriteria;
   private String displayName;
   private IScoreObjectiveCriteria.EnumRenderType renderType;

   public void setRenderType(IScoreObjectiveCriteria.EnumRenderType type) {
      this.renderType = type;
      this.theScoreboard.onObjectiveDisplayNameChanged(this);
   }

   public String getName() {
      return this.name;
   }

   public IScoreObjectiveCriteria getCriteria() {
      return this.objectiveCriteria;
   }

   public Scoreboard getScoreboard() {
      return this.theScoreboard;
   }

   public ScoreObjective(Scoreboard theScoreboardIn, String nameIn, IScoreObjectiveCriteria objectiveCriteriaIn) {
      this.theScoreboard = theScoreboardIn;
      this.name = nameIn;
      this.objectiveCriteria = objectiveCriteriaIn;
      this.displayName = nameIn;
      this.renderType = objectiveCriteriaIn.getRenderType();
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public IScoreObjectiveCriteria.EnumRenderType getRenderType() {
      return this.renderType;
   }

   public void setDisplayName(String nameIn) {
      this.displayName = nameIn;
      this.theScoreboard.onObjectiveDisplayNameChanged(this);
   }
}
