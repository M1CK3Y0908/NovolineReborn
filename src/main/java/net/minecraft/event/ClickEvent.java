package net.minecraft.event;

import com.google.common.collect.Maps;
import java.util.Map;

public class ClickEvent {
   private final ClickEvent.Action action;
   private final String value;

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      }

      if (other != null && this.getClass() == other.getClass()) {
         ClickEvent clickEvent = (ClickEvent)other;
         if (this.action != clickEvent.action) {
            return false;
         }

         if (this.value != null) {
            if (!this.value.equals(clickEvent.value)) {
               return false;
            }
         } else if (clickEvent.value != null) {
            return false;
         }

         return true;
      } else {
         return false;
      }
   }

   public ClickEvent.Action getAction() {
      return this.action;
   }

   public String getValue() {
      return this.value;
   }

   @Override
   public String toString() {
      return "ClickEvent{action=" + this.action + ", value='" + this.value + "'}";
   }

   @Override
   public int hashCode() {
      int i = this.action.hashCode();
      return 31 * i + (this.value != null ? this.value.hashCode() : 0);
   }

   public ClickEvent(ClickEvent.Action actionIn, String valueIn) {
      this.action = actionIn;
      this.value = valueIn;
   }

   public enum Action {
      OPEN_URL("open_url", true),
      OPEN_FILE("open_file", false),
      RUN_COMMAND("run_command", true),
      TWITCH_USER_INFO("twitch_user_info", false),
      SUGGEST_COMMAND("suggest_command", true),
      CHANGE_PAGE("change_page", true);

      private final String canonicalName;
      private final boolean allowedInChat;
      private static final Map<String, ClickEvent.Action> nameMapping = Maps.newHashMap();

      static {
         for (ClickEvent.Action action2 : values()) {
            nameMapping.put(action2.getCanonicalName(), action2);
         }
      }

      public String getCanonicalName() {
         return this.canonicalName;
      }

      public static ClickEvent.Action getValueByCanonicalName(String canonicalNameIn) {
         return nameMapping.get(canonicalNameIn);
      }

      Action(String text, boolean flag) {
         this.canonicalName = text;
         this.allowedInChat = flag;
      }

      public boolean shouldAllowInChat() {
         return this.allowedInChat;
      }
   }
}
