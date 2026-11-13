package wtf.novoline.module;

import java.util.Arrays;
import net.minecraft.util.ResourceLocation;

public enum Category {
   COMBAT("Combat", new ResourceLocation("novoline", "images/clickgui/combat.png")),
   PLAYER("Player", new ResourceLocation("novoline", "images/clickgui/player.png")),
   MOVEMENT("Movement", new ResourceLocation("novoline", "images/clickgui/movement.png")),
   VISUAL("Visual", new ResourceLocation("novoline", "images/clickgui/visual.png")),
   WORLD("World", new ResourceLocation("novoline", "images/clickgui/world.png")),
   MISC("Misc", new ResourceLocation("novoline", "images/clickgui/misc.png")),
   HUD("Display", new ResourceLocation("novoline", "images/clickgui/hud.png")),
   SCRIPTS("Scripts", new ResourceLocation("novoline", "images/clickgui/scripts.png"));

   private final String displayName;
   private final ResourceLocation icon;

   public String getDisplayName() {
      return this.displayName;
   }

   public ResourceLocation getIcon() {
      return this.icon;
   }

   public static Category byName(String text) {
      return Arrays.stream(values()).filter(category -> category.getDisplayName().equalsIgnoreCase(text)).findFirst().orElse(null);
   }

   Category(String text, ResourceLocation resourceLocation) {
      this.displayName = text;
      this.icon = resourceLocation;
   }
}
