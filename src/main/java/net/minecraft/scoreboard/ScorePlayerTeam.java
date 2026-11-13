package net.minecraft.scoreboard;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import net.minecraft.util.EnumChatFormatting;

public class ScorePlayerTeam extends Team {
   private final Set<String> membershipSet = Sets.newHashSet();
   private String namePrefixSPT = "";
   private String colorSuffix = "";
   private boolean allowFriendlyFire = true;
   private boolean canSeeFriendlyInvisibles = true;
   private Team.EnumVisible nameTagVisibility = Team.EnumVisible.ALWAYS;
   private Team.EnumVisible deathMessageVisibility = Team.EnumVisible.ALWAYS;
   private EnumChatFormatting chatFormat = EnumChatFormatting.RESET;
   private final Scoreboard theScoreboard;
   private final String registeredName;
   private String teamNameSPT;

   public static String formatPlayerName(Team team, String playerName) {
      return team == null ? playerName : team.formatString(playerName);
   }

   public void setNameSuffix(String suffix) {
      this.colorSuffix = suffix;
      this.theScoreboard.sendTeamUpdate(this);
   }

   @Override
   public String formatString(String input) {
      return this.getColorPrefix() + input + this.getColorSuffix();
   }

   @Override
   public boolean getAllowFriendlyFire() {
      return this.allowFriendlyFire;
   }

   public void setDeathMessageVisibility(Team.EnumVisible visibility) {
      this.deathMessageVisibility = visibility;
      this.theScoreboard.sendTeamUpdate(this);
   }

   public void setAllowFriendlyFire(boolean friendlyFire) {
      this.allowFriendlyFire = friendlyFire;
      this.theScoreboard.sendTeamUpdate(this);
   }

   @Override
   public Team.EnumVisible getDeathMessageVisibility() {
      return this.deathMessageVisibility;
   }

   public ScorePlayerTeam(Scoreboard theScoreboardIn, String name) {
      this.theScoreboard = theScoreboardIn;
      this.registeredName = name;
      this.teamNameSPT = name;
   }

   @Override
   public boolean getSeeFriendlyInvisiblesEnabled() {
      return this.canSeeFriendlyInvisibles;
   }

   public int getFriendlyFlags() {
      int i = 0;
      if (this.getAllowFriendlyFire()) {
         i = 1;
      }

      if (this.getSeeFriendlyInvisiblesEnabled()) {
         i |= 2;
      }

      return i;
   }

   @Override
   public String getRegisteredName() {
      return this.registeredName;
   }

   public String getColorPrefix() {
      return this.namePrefixSPT;
   }

   public EnumChatFormatting getChatFormat() {
      return this.chatFormat;
   }

   @Override
   public Collection<String> getMembershipCollection() {
      return this.membershipSet;
   }

   public String getColorSuffix() {
      return this.colorSuffix;
   }

   @Override
   public Team.EnumVisible getNameTagVisibility() {
      return this.nameTagVisibility;
   }

   public void setNamePrefix(String prefix) {
      if (prefix == null) {
         throw new IllegalArgumentException("Prefix cannot be null");
      }

      this.namePrefixSPT = prefix;
      this.theScoreboard.sendTeamUpdate(this);
   }

   public void setSeeFriendlyInvisiblesEnabled(boolean friendlyInvisibles) {
      this.canSeeFriendlyInvisibles = friendlyInvisibles;
      this.theScoreboard.sendTeamUpdate(this);
   }

   public void setNameTagVisibility(Team.EnumVisible visibility) {
      this.nameTagVisibility = visibility;
      this.theScoreboard.sendTeamUpdate(this);
   }

   public void setTeamName(String name) {
      if (name == null) {
         throw new IllegalArgumentException("Name cannot be null");
      }

      this.teamNameSPT = name;
      this.theScoreboard.sendTeamUpdate(this);
   }

   public void setChatFormat(EnumChatFormatting format) {
      this.chatFormat = format;
   }

   public String getTeamName() {
      return this.teamNameSPT;
   }

   public void setFriendlyFlags(int flags) {
      this.setAllowFriendlyFire((flags & 1) > 0);
      this.setSeeFriendlyInvisiblesEnabled((flags & 2) > 0);
   }
}
