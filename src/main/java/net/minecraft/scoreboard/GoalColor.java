package net.minecraft.scoreboard;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

public class GoalColor implements IScoreObjectiveCriteria {
   private final String goalName;

   @Override
   public int setScore(List<EntityPlayer> players) {
      return 0;
   }

   @Override
   public String getName() {
      return this.goalName;
   }

   @Override
   public IScoreObjectiveCriteria.EnumRenderType getRenderType() {
      return IScoreObjectiveCriteria.EnumRenderType.INTEGER;
   }

   public GoalColor(String baseName, EnumChatFormatting color) {
      this.goalName = baseName + color.getFriendlyName();
      IScoreObjectiveCriteria.INSTANCES.put(this.goalName, this);
   }

   @Override
   public boolean isReadOnly() {
      return false;
   }
}
