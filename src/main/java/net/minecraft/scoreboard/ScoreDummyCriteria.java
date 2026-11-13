package net.minecraft.scoreboard;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ScoreDummyCriteria implements IScoreObjectiveCriteria {
   private final String dummyName;

   @Override
   public int setScore(List<EntityPlayer> players) {
      return 0;
   }

   @Override
   public IScoreObjectiveCriteria.EnumRenderType getRenderType() {
      return IScoreObjectiveCriteria.EnumRenderType.INTEGER;
   }

   public ScoreDummyCriteria(String name) {
      this.dummyName = name;
      IScoreObjectiveCriteria.INSTANCES.put(name, this);
   }

   @Override
   public String getName() {
      return this.dummyName;
   }

   @Override
   public boolean isReadOnly() {
      return false;
   }
}
