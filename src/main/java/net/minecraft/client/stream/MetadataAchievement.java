package net.minecraft.client.stream;

import net.minecraft.stats.Achievement;

public class MetadataAchievement extends Metadata {
   public MetadataAchievement(Achievement achievement2) {
      super("achievement");
      this.handleString("achievement_id", achievement2.statId);
      this.handleString("achievement_name", achievement2.getStatName().getUnformattedText());
      this.handleString("achievement_description", achievement2.getDescription());
      this.setText2("Achievement '" + achievement2.getStatName().getUnformattedText() + "' obtained!");
   }
}
