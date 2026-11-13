package net.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;

public abstract class MovingSound extends PositionedSound implements ITickableSound {
   protected boolean donePlaying = false;

   @Override
   public boolean isDonePlaying() {
      return this.donePlaying;
   }

   protected MovingSound(ResourceLocation location) {
      super(location);
   }
}
