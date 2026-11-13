package wtf.novoline.module.hud;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HudManager {
   private final List<HudElement> elements = new java.util.ArrayList<>();
   private final Map<String, HudElement> elementsByName = new HashMap<>();
   private final HudRenderer hudRenderer = new HudRenderer(this);
   private final HudInputHandler hudInputHandler = new HudInputHandler(this);

   public HudInputHandler getHudInputHandler() {
      return this.hudInputHandler;
   }

   public HudElement getHudElement(String text) {
      for (HudElement hudElement : this.elements) {
         if (hudElement.getName().equals(text)) {
            return hudElement;
         }
      }

      return null;
   }

   public HudManager() {
      this.register(new WatermarkElement());
      this.register(new ArrayListElement());
      this.register(new TargetHudElement());
      this.register(new HypixelStatsElement());
      this.register(new BlockCounterElement());
   }

   public void register(HudElement hudElement) {
      if (this.elementsByName.put(hudElement.getName(), hudElement) != null) {
         throw new RuntimeException("Item name duplication in OverlayRegistry");
      }

      this.elements.add(hudElement);
   }

   public HudRenderer getHudRenderer() {
      return this.hudRenderer;
   }

   public List<HudElement> getElements() {
      return this.elements;
   }
}
