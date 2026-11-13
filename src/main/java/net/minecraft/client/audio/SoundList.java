package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.util.List;

public class SoundList {
   private SoundCategory category;
   private final List<SoundList.SoundEntry> soundList = Lists.newArrayList();
   private boolean replaceExisting;

   public void setSoundCategory(SoundCategory soundCat) {
      this.category = soundCat;
   }

   public boolean canReplaceExisting() {
      return this.replaceExisting;
   }

   public List<SoundList.SoundEntry> getSoundList() {
      return this.soundList;
   }

   public void setReplaceExisting(boolean flag) {
      this.replaceExisting = flag;
   }

   public SoundCategory getSoundCategory() {
      return this.category;
   }

   public static class SoundEntry {
      private String name;
      private float volume = 1.0F;
      private float pitch = 1.0F;
      private int weight = 1;
      private SoundList.SoundEntry.Type type = SoundList.SoundEntry.Type.FILE;
      private boolean streaming = false;

      public void setSoundEntryWeight(int weightIn) {
         this.weight = weightIn;
      }

      public int getSoundEntryWeight() {
         return this.weight;
      }

      public String getSoundEntryName() {
         return this.name;
      }

      public void setSoundEntryName(String nameIn) {
         this.name = nameIn;
      }

      public float getSoundEntryPitch() {
         return this.pitch;
      }

      public void setSoundEntryVolume(float volumeIn) {
         this.volume = volumeIn;
      }

      public void setStreaming(boolean isStreaming) {
         this.streaming = isStreaming;
      }

      public boolean isStreaming() {
         return this.streaming;
      }

      public void setSoundEntryPitch(float pitchIn) {
         this.pitch = pitchIn;
      }

      public float getSoundEntryVolume() {
         return this.volume;
      }

      public void setSoundEntryType(SoundList.SoundEntry.Type typeIn) {
         this.type = typeIn;
      }

      public SoundList.SoundEntry.Type getSoundEntryType() {
         return this.type;
      }

      public enum Type {
         FILE("file"),
         SOUND_EVENT("event");

         private final String name;

         Type(String text) {
            this.name = text;
         }

         public static SoundList.SoundEntry.Type getType(String nameIn) {
            for (SoundList.SoundEntry.Type type2 : values()) {
               if (type2.name.equals(nameIn)) {
                  return type2;
               }
            }

            return null;
         }
      }
   }
}
