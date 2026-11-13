package net.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;

public interface ISound {
   boolean canRepeat();

   float getZPosF();

   float getVolume();

   int getRepeatDelay();

   ISound.AttenuationType getAttenuationType();

   ResourceLocation getSoundLocation();

   float getXPosF();

   float getYPosF();

   float getPitch();

   enum AttenuationType {
      NONE(0),
      LINEAR(2);

      private final int type;

      AttenuationType(int i) {
         this.type = i;
      }

      public int getTypeInt() {
         return this.type;
      }
   }
}
