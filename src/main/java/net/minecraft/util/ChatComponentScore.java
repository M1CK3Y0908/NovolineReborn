package net.minecraft.util;

import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;

public class ChatComponentScore extends ChatComponentStyle {
   private String value = "";
   private final String name;
   private final String objective;

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else {
         return !(other instanceof ChatComponentScore chatComponentScore)
            ? false
            : this.name.equals(chatComponentScore.name) && this.objective.equals(chatComponentScore.objective) && super.equals(other);
      }
   }

   @Override
   public String getUnformattedTextForChat() {
      MinecraftServer minecraftServer = MinecraftServer.getServer();
      if (minecraftServer != null && minecraftServer.isAnvilFileSet() && StringUtils.isNullOrEmpty(this.value)) {
         Scoreboard scoreboard = minecraftServer.worldServerForDimension(0).getScoreboard();
         ScoreObjective scoreObjective = scoreboard.getObjective(this.objective);
         if (scoreboard.entityHasObjective(this.name, scoreObjective)) {
            Score score = scoreboard.getValueFromObjective(this.name, scoreObjective);
            this.setValue(String.format("%d", score.getScorePoints()));
         } else {
            this.value = "";
         }
      }

      return this.value;
   }

   public String getName() {
      return this.name;
   }

   public ChatComponentScore createCopy() {
      ChatComponentScore chatComponentScore2 = new ChatComponentScore(this.name, this.objective);
      chatComponentScore2.setValue(this.value);
      chatComponentScore2.setChatStyle(this.getChatStyle().createShallowCopy());

      for (IChatComponent iChatComponent : this.getSiblings()) {
         chatComponentScore2.appendSibling(iChatComponent.createCopy());
      }

      return chatComponentScore2;
   }

   public void setValue(String valueIn) {
      this.value = valueIn;
   }

   @Override
   public String toString() {
      return "ScoreComponent{name='" + this.name + "'objective='" + this.objective + "', siblings=" + this.siblings + ", style=" + this.getChatStyle() + "}";
   }

   public String getObjective() {
      return this.objective;
   }

   public ChatComponentScore(String nameIn, String objectiveIn) {
      this.name = nameIn;
      this.objective = objectiveIn;
   }
}
