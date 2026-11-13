package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.util.ResourceLocation;

public class SoundEventAccessorComposite implements ISoundEventAccessor<SoundPoolEntry> {
   private final List<ISoundEventAccessor<SoundPoolEntry>> soundPool = Lists.newArrayList();
   private final Random rnd = new Random();
   private final ResourceLocation soundLocation;
   private double eventVolume;
   private double eventPitch;
   private final SoundCategory category;

   public void addSoundToEventPool(ISoundEventAccessor<SoundPoolEntry> sound) {
      this.soundPool.add(sound);
   }

   public SoundPoolEntry cloneEntry() {
      int weight = this.getWeight();
      if (!this.soundPool.isEmpty() && weight != 0) {
         int i = this.rnd.nextInt(weight);

         for (ISoundEventAccessor iSoundEventAccessor : this.soundPool) {
            i -= iSoundEventAccessor.getWeight();
            if (i < 0) {
               SoundPoolEntry soundPoolEntry = (SoundPoolEntry)iSoundEventAccessor.cloneEntry();
               soundPoolEntry.setPitch(soundPoolEntry.getPitch() * this.eventPitch);
               soundPoolEntry.setVolume(soundPoolEntry.getVolume() * this.eventVolume);
               return soundPoolEntry;
            }
         }

         return SoundHandler.missing_sound;
      } else {
         return SoundHandler.missing_sound;
      }
   }

   public SoundCategory getSoundCategory() {
      return this.category;
   }

   public ResourceLocation getSoundEventLocation() {
      return this.soundLocation;
   }

   @Override
   public int getWeight() {
      int i = 0;

      for (ISoundEventAccessor iSoundEventAccessor : this.soundPool) {
         i += iSoundEventAccessor.getWeight();
      }

      return i;
   }

   public SoundEventAccessorComposite(ResourceLocation soundLocation, double pitch, double volume, SoundCategory category) {
      this.soundLocation = soundLocation;
      this.eventVolume = volume;
      this.eventPitch = pitch;
      this.category = category;
   }
}
