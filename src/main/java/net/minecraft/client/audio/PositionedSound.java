package net.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;

public abstract class PositionedSound implements ISound {
   protected float xPosF;
   protected float volume = 1.0F;
   protected float zPosF;
   protected float pitch = 1.0F;
   protected boolean repeat = false;
   protected int repeatDelay = 0;
   protected ISound.AttenuationType attenuationType = ISound.AttenuationType.LINEAR;
   protected float yPosF;
   protected final ResourceLocation positionedSoundLocation;

   protected PositionedSound(ResourceLocation soundResource) {
      this.positionedSoundLocation = soundResource;
   }

   @Override
   public float getVolume() {
      return this.volume;
   }

   @Override
   public float getPitch() {
      return this.pitch;
   }

   @Override
   public ResourceLocation getSoundLocation() {
      return this.positionedSoundLocation;
   }

   @Override
   public float getZPosF() {
      return this.zPosF;
   }

   @Override
   public boolean canRepeat() {
      return this.repeat;
   }

   @Override
   public ISound.AttenuationType getAttenuationType() {
      return this.attenuationType;
   }

   @Override
   public float getXPosF() {
      return this.xPosF;
   }

   @Override
   public int getRepeatDelay() {
      return this.repeatDelay;
   }

   @Override
   public float getYPosF() {
      return this.yPosF;
   }
}
