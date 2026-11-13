package net.minecraft.event;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.IChatComponent;

public class HoverEvent {
   private final HoverEvent.Action action;
   private final IChatComponent value;

   public HoverEvent.Action getAction() {
      return this.action;
   }

   @Override
   public String toString() {
      return "HoverEvent{action=" + this.action + ", value='" + this.value + "'}";
   }

   @Override
   public int hashCode() {
      int i = this.action.hashCode();
      return 31 * i + (this.value != null ? this.value.hashCode() : 0);
   }

   public HoverEvent(HoverEvent.Action actionIn, IChatComponent valueIn) {
      this.action = actionIn;
      this.value = valueIn;
   }

   public IChatComponent getValue() {
      return this.value;
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      }

      if (other != null && this.getClass() == other.getClass()) {
         HoverEvent hoverEvent = (HoverEvent)other;
         if (this.action != hoverEvent.action) {
            return false;
         }

         if (this.value != null) {
            if (!this.value.equals(hoverEvent.value)) {
               return false;
            }
         } else if (hoverEvent.value != null) {
            return false;
         }

         return true;
      } else {
         return false;
      }
   }

   public enum Action {
      SHOW_TEXT("show_text"),
      SHOW_ACHIEVEMENT("show_achievement"),
      SHOW_ITEM("show_item"),
      SHOW_ENTITY("show_entity");

      private final String canonicalName;
      private static final Map<String, HoverEvent.Action> nameMapping = Maps.newHashMap();
      private final boolean allowedInChat;

      Action(String text) {
         this.canonicalName = text;
         this.allowedInChat = true;
      }

      public static HoverEvent.Action getValueByCanonicalName(String canonicalNameIn) {
         return nameMapping.get(canonicalNameIn);
      }

      public boolean shouldAllowInChat() {
         return this.allowedInChat;
      }

      public String getCanonicalName() {
         return this.canonicalName;
      }

      static {
         for (HoverEvent.Action action2 : values()) {
            nameMapping.put(action2.getCanonicalName(), action2);
         }
      }
   }
}
