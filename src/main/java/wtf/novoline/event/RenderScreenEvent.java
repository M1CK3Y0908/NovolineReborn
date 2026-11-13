package wtf.novoline.event;

import net.minecraft.client.gui.ScaledResolution;

public class RenderScreenEvent extends Event {
   public ScaledResolution scaledResolution;
   public float partialTicks;

   public RenderScreenEvent(ScaledResolution scaledResolution2, float f) {
      this.scaledResolution = scaledResolution2;
      this.partialTicks = f;
   }
}
