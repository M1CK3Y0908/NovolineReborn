package net.minecraft.stats;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.event.HoverEvent;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IJsonSerializable;

public class StatBase {
   private static NumberFormat numberFormat = NumberFormat.getIntegerInstance(Locale.US);
   public final String statId;
   private final IChatComponent statName;
   public static IStatType simpleStatType = new IStatType() {
      @Override
      public String format(int number) {
         return StatBase.numberFormat.format(number);
      }
   };
   private static DecimalFormat decimalFormat = new DecimalFormat("########0.00");
   public boolean isIndependent;
   private Class<? extends IJsonSerializable> serializableClass;
   private final IStatType type;
   public static IStatType timeStatType = new IStatType() {
      @Override
      public String format(int number) {
         double d2 = number / 20.0;
         double d3 = d2 / 60.0;
         double d4 = d3 / 60.0;
         double d5 = d4 / 24.0;
         double d6 = d5 / 365.0;
         return d6 > 0.5
            ? StatBase.decimalFormat.format(d6) + " y"
            : (
               d5 > 0.5
                  ? StatBase.decimalFormat.format(d5) + " d"
                  : (
                     d4 > 0.5
                        ? StatBase.decimalFormat.format(d4) + " h"
                        : (d3 > 0.5 ? StatBase.decimalFormat.format(d3) + " m" : d2 + " s")
                  )
            );
      }
   };
   public static IStatType distanceStatType = new IStatType() {
      @Override
      public String format(int number) {
         double d2 = number / 100.0;
         double d3 = d2 / 1000.0;
         return d3 > 0.5
            ? StatBase.decimalFormat.format(d3) + " km"
            : (d2 > 0.5 ? StatBase.decimalFormat.format(d2) + " m" : number + " cm");
      }
   };
   private final IScoreObjectiveCriteria objectiveCriteria;
   public static IStatType damageStatType = new IStatType() {
      @Override
      public String format(int number) {
         return StatBase.decimalFormat.format(number * 0.1);
      }
   };

   public Class<? extends IJsonSerializable> getSerializableClass() {
      return this.serializableClass;
   }

   public StatBase initIndependentStat() {
      this.isIndependent = true;
      return this;
   }

   public boolean isAchievement() {
      return false;
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other != null && this.getClass() == other.getClass()) {
         StatBase statBase = (StatBase)other;
         return this.statId.equals(statBase.statId);
      } else {
         return false;
      }
   }

   public IScoreObjectiveCriteria getCriteria() {
      return this.objectiveCriteria;
   }

   @Override
   public int hashCode() {
      return this.statId.hashCode();
   }

   public StatBase setSerializableClass(Class<? extends IJsonSerializable> serializableClass) {
      this.serializableClass = serializableClass;
      return this;
   }

   @Override
   public String toString() {
      return "Stat{id="
         + this.statId
         + ", nameId="
         + this.statName
         + ", awardLocallyOnly="
         + this.isIndependent
         + ", formatter="
         + this.type
         + ", objectiveCriteria="
         + this.objectiveCriteria
         + "}";
   }

   public String format(int number) {
      return this.type.format(number);
   }

   public StatBase registerStat() {
      if (StatList.oneShotStats.containsKey(this.statId)) {
         throw new RuntimeException(
            "Duplicate stat id: \"" + StatList.oneShotStats.get(this.statId).statName + "\" and \"" + this.statName + "\" at id " + this.statId
         );
      }

      StatList.allStats.add(this);
      StatList.oneShotStats.put(this.statId, this);
      return this;
   }

   public IChatComponent getStatName() {
      IChatComponent chatComponent = this.statName.createCopy();
      chatComponent.getChatStyle().setColor(EnumChatFormatting.GRAY);
      chatComponent.getChatStyle().setChatHoverEvent(new HoverEvent(net.minecraft.event.HoverEvent.Action.SHOW_ACHIEVEMENT, new ChatComponentText(this.statId)));
      return chatComponent;
   }

   public StatBase(String statIdIn, IChatComponent statNameIn) {
      this(statIdIn, statNameIn, simpleStatType);
   }

   public IChatComponent createChatComponent() {
      IChatComponent chatComponent = this.getStatName();
      IChatComponent chatComponent2 = new ChatComponentText("[").appendSibling(chatComponent).appendText("]");
      chatComponent2.setChatStyle(chatComponent.getChatStyle());
      return chatComponent2;
   }

   public StatBase(String statIdIn, IChatComponent statNameIn, IStatType typeIn) {
      this.statId = statIdIn;
      this.statName = statNameIn;
      this.type = typeIn;
      this.objectiveCriteria = new ObjectiveStat(this);
      IScoreObjectiveCriteria.INSTANCES.put(this.objectiveCriteria.getName(), this.objectiveCriteria);
   }
}
