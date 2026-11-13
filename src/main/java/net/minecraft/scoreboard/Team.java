package net.minecraft.scoreboard;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;

public abstract class Team {
   public abstract Collection<String> getMembershipCollection();

   public abstract Team.EnumVisible getNameTagVisibility();

   public abstract boolean getAllowFriendlyFire();

   public abstract Team.EnumVisible getDeathMessageVisibility();

   public abstract String getRegisteredName();

   public abstract boolean getSeeFriendlyInvisiblesEnabled();

   public boolean isSameTeam(Team other) {
      return other == null ? false : this == other;
   }

   public abstract String formatString(String text);

   public enum EnumVisible {
      ALWAYS("always", 0),
      NEVER("never", 1),
      HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
      HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

      public final String internalName;
      public final int id;
      private static Map<String, Team.EnumVisible> nameMapping = Maps.newHashMap();

      public static String[] getNames() {
         return nameMapping.keySet().toArray(new String[nameMapping.size()]);
      }

      public static Team.EnumVisible byInternalName(String internalName) {
         return nameMapping.get(internalName);
      }

      static {
         for (Team.EnumVisible enumVisible : values()) {
            nameMapping.put(enumVisible.internalName, enumVisible);
         }
      }

      EnumVisible(String text, int i) {
         this.internalName = text;
         this.id = i;
      }
   }
}
