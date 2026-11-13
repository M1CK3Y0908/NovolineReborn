package wtf.novoline.event;

import net.minecraft.client.gui.ScaledResolution;

public class RenderGameOverlayEvent extends Event {
   public ScaledResolution scaledResolution;
   public float partialTicks;

   public RenderGameOverlayEvent(ScaledResolution scaledResolution2, float f) {
      this.scaledResolution = scaledResolution2;
      this.partialTicks = f;
   }
}
