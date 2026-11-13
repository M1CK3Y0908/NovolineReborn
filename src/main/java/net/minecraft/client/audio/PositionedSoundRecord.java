package net.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;

public class PositionedSoundRecord extends PositionedSound {
   public static PositionedSoundRecord create(ResourceLocation soundResource, float pitch) {
      return new PositionedSoundRecord(soundResource, 0.25F, pitch, ISound.AttenuationType.NONE, 0.0F, 0.0F, 0.0F);
   }

   public PositionedSoundRecord(ResourceLocation soundResource, float volume, float pitch, float xPosition, float yPosition, float zPosition) {
      this(soundResource, volume, pitch, ISound.AttenuationType.LINEAR, xPosition, yPosition, zPosition);
   }

   private PositionedSoundRecord(ResourceLocation resourceLocation, float f, float f2, ISound.AttenuationType attenuationType2, float f3, float f4, float f5) {
      super(resourceLocation);
      this.volume = f;
      this.pitch = f2;
      this.xPosF = f3;
      this.yPosF = f4;
      this.zPosF = f5;
      this.repeat = false;
      this.repeatDelay = 0;
      this.attenuationType = attenuationType2;
   }

   public static PositionedSoundRecord create(ResourceLocation soundResource) {
      return new PositionedSoundRecord(soundResource, 1.0F, 1.0F, ISound.AttenuationType.NONE, 0.0F, 0.0F, 0.0F);
   }

   public static PositionedSoundRecord create(ResourceLocation soundResource, float xPosition, float yPosition, float zPosition) {
      return new PositionedSoundRecord(soundResource, 4.0F, 1.0F, ISound.AttenuationType.LINEAR, xPosition, yPosition, zPosition);
   }
}
