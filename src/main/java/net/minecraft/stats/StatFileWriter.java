package net.minecraft.stats;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.TupleIntJsonSerializable;

public class StatFileWriter {
   protected final Map<StatBase, TupleIntJsonSerializable> statsData = Maps.newConcurrentMap();

   public int getDistanceToUnlock(Achievement achievement) {
      if (this.hasAchievementUnlocked(achievement)) {
         return 0;
      }

      int i = 0;

      for (Achievement parentAchievement = achievement.parentAchievement;
         parentAchievement != null && !this.hasAchievementUnlocked(parentAchievement);
         i++
      ) {
         parentAchievement = parentAchievement.parentAchievement;
      }

      return i;
   }

   public boolean canUnlockAchievement(Achievement achievementIn) {
      return achievementIn.parentAchievement == null || this.hasAchievementUnlocked(achievementIn.parentAchievement);
   }

   public int readStat(StatBase stat) {
      TupleIntJsonSerializable tupleIntJsonSerializable = this.statsData.get(stat);
      return tupleIntJsonSerializable == null ? 0 : tupleIntJsonSerializable.getIntegerValue();
   }

   public void increaseStat(EntityPlayer player, StatBase stat, int amount) {
      if (!stat.isAchievement() || this.canUnlockAchievement((Achievement)stat)) {
         this.unlockAchievement(player, stat, this.readStat(stat) + amount);
      }
   }

   public boolean hasAchievementUnlocked(Achievement achievementIn) {
      return this.readStat(achievementIn) > 0;
   }

   public <T extends IJsonSerializable> T getProgress(StatBase stat) {
      TupleIntJsonSerializable tupleIntJsonSerializable = this.statsData.get(stat);
      return tupleIntJsonSerializable != null ? tupleIntJsonSerializable.getJsonSerializableValue() : null;
   }

   public void unlockAchievement(EntityPlayer playerIn, StatBase statIn, int value) {
      TupleIntJsonSerializable tupleIntJsonSerializable = this.statsData.get(statIn);
      if (tupleIntJsonSerializable == null) {
         tupleIntJsonSerializable = new TupleIntJsonSerializable();
         this.statsData.put(statIn, tupleIntJsonSerializable);
      }

      tupleIntJsonSerializable.setIntegerValue(value);
   }

   public <T extends IJsonSerializable> T setProgress(StatBase stat, T progress) {
      TupleIntJsonSerializable tupleIntJsonSerializable = this.statsData.get(stat);
      if (tupleIntJsonSerializable == null) {
         tupleIntJsonSerializable = new TupleIntJsonSerializable();
         this.statsData.put(stat, tupleIntJsonSerializable);
      }

      tupleIntJsonSerializable.setJsonSerializableValue(progress);
      return progress;
   }
}
