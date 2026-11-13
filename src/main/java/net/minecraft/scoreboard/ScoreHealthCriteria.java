package net.minecraft.scoreboard;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class ScoreHealthCriteria extends ScoreDummyCriteria {
   @Override
   public boolean isReadOnly() {
      return true;
   }

   @Override
   public IScoreObjectiveCriteria.EnumRenderType getRenderType() {
      return IScoreObjectiveCriteria.EnumRenderType.HEARTS;
   }

   public ScoreHealthCriteria(String name) {
      super(name);
   }

   @Override
   public int setScore(List<EntityPlayer> players) {
      float f = 0.0F;

      for (EntityPlayer entityPlayer : players) {
         f += entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount();
      }

      if (players.size() > 0) {
         f /= players.size();
      }

      return MathHelper.ceiling_float_int(f);
   }
}
