package net.minecraft.client.audio;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import io.netty.util.internal.ThreadLocalRandom;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.SoundSystemLogger;
import paulscode.sound.Source;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.libraries.LibraryJavaSound;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

public class SoundManager {
   private int playTime = 0;
   private final Map<String, ISound> playingSounds = HashBiMap.create();
   private SoundManager.SoundSystemStarterThread sndSystem;
   private static final Marker LOG_MARKER = MarkerManager.getMarker("SOUNDS");
   private final Map<ISound, String> invPlayingSounds = ((BiMap)this.playingSounds).inverse();
   private Map<ISound, SoundPoolEntry> playingSoundPoolEntries = Maps.newHashMap();
   private final Multimap<SoundCategory, String> categorySounds = HashMultimap.create();
   private boolean loaded;
   private static final Logger logger = LogManager.getLogger();
   private final List<ITickableSound> tickableSounds = Lists.newArrayList();
   private final Map<ISound, Integer> delayedSounds = Maps.newHashMap();
   private final Map<String, Integer> playingSoundsStopTime = Maps.newHashMap();
   private final SoundHandler sndHandler;
   private final GameSettings options;

   public void pauseAllSounds() {
      for (String text2 : this.playingSounds.keySet()) {
         logger.debug(LOG_MARKER, "Pausing channel {}", new Object[]{text2});
         this.sndSystem.pause(text2);
      }
   }

   public void reloadSoundSystem() {
      this.unloadSoundSystem();
      this.loadSoundSystem();
   }

   public void unloadSoundSystem() {
      if (this.loaded) {
         this.stopAllSounds();
         this.sndSystem.cleanup();
         this.loaded = false;
      }
   }

   public void resumeAllSounds() {
      for (String text2 : this.playingSounds.keySet()) {
         logger.debug(LOG_MARKER, "Resuming channel {}", new Object[]{text2});
         this.sndSystem.play(text2);
      }
   }

   public void stopSound(ISound sound) {
      if (this.loaded) {
         String text = this.invPlayingSounds.get(sound);
         if (text != null) {
            this.sndSystem.stop(text);
         }
      }
   }

   public void updateAllSounds() {
      this.playTime++;

      for (ITickableSound iTickableSound : this.tickableSounds) {
         iTickableSound.update();
         if (iTickableSound.isDonePlaying()) {
            this.stopSound(iTickableSound);
         } else {
            String text2 = this.invPlayingSounds.get(iTickableSound);
            this.sndSystem
               .setVolume(
                  text2,
                  this.getNormalizedVolume(
                     iTickableSound, this.playingSoundPoolEntries.get(iTickableSound), this.sndHandler.getSound(iTickableSound.getSoundLocation()).getSoundCategory()
                  )
               );
            this.sndSystem.setPitch(text2, this.getNormalizedPitch(iTickableSound, this.playingSoundPoolEntries.get(iTickableSound)));
            this.sndSystem.setPosition(text2, iTickableSound.getXPosF(), iTickableSound.getYPosF(), iTickableSound.getZPosF());
         }
      }

      Iterator iterator2 = this.playingSounds.entrySet().iterator();

      while (iterator2.hasNext()) {
         Entry entry2 = (Entry)iterator2.next();
         String text3 = (String)entry2.getKey();
         ISound iSound = (ISound)entry2.getValue();
         if (!this.sndSystem.playing(text3) && this.playingSoundsStopTime.get(text3) <= this.playTime) {
            int repeatDelay = iSound.getRepeatDelay();
            if (iSound.canRepeat() && repeatDelay > 0) {
               this.delayedSounds.put(iSound, this.playTime + repeatDelay);
            }

            iterator2.remove();
            logger.debug(LOG_MARKER, "Removed channel {} because it's not playing anymore", new Object[]{text3});
            this.sndSystem.removeSource(text3);
            this.playingSoundsStopTime.remove(text3);
            this.playingSoundPoolEntries.remove(iSound);

            try {
               this.categorySounds.remove(this.sndHandler.getSound(iSound.getSoundLocation()).getSoundCategory(), text3);
            } catch (RuntimeException runtimeException) {
            }

            if (iSound instanceof ITickableSound) {
               this.tickableSounds.remove(iSound);
            }
         }
      }

      Iterator iterator3 = this.delayedSounds.entrySet().iterator();

      while (iterator3.hasNext()) {
         Entry entry3 = (Entry)iterator3.next();
         if (this.playTime >= (Integer)entry3.getValue()) {
            ISound iSound2 = (ISound)entry3.getKey();
            if (iSound2 instanceof ITickableSound) {
               ((ITickableSound)iSound2).update();
            }

            this.playSound(iSound2);
            iterator3.remove();
         }
      }
   }

   public boolean isSoundPlaying(ISound sound) {
      if (!this.loaded) {
         return false;
      }

      String text = this.invPlayingSounds.get(sound);
      return text == null
         ? false
         : this.sndSystem.playing(text) || this.playingSoundsStopTime.containsKey(text) && this.playingSoundsStopTime.get(text) <= this.playTime;
   }

   public SoundManager(SoundHandler soundHandler, GameSettings gameSettings) {
      this.sndHandler = soundHandler;
      this.options = gameSettings;

      try {
         SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
         SoundSystemConfig.addLibrary(LibraryJavaSound.class);
         SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
      } catch (SoundSystemException soundSystemException) {
         logger.error(LOG_MARKER, "Error linking with the LibraryJavaSound plug-in", soundSystemException);
      }
   }

   public void playDelayedSound(ISound sound, int delay) {
      this.delayedSounds.put(sound, this.playTime + delay);
   }

   public void setListener(EntityPlayer player, float partialTicks) {
      if (this.loaded && player != null) {
         float f = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
         float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;
         double xCoordinate = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
         double yCoordinate = player.prevPosY + (player.posY - player.prevPosY) * partialTicks + player.getEyeHeight();
         double zCoordinate = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;
         float f3 = MathHelper.cos((f2 + 90.0F) * (float) (Math.PI / 180.0));
         float f4 = MathHelper.sin((f2 + 90.0F) * (float) (Math.PI / 180.0));
         float f5 = MathHelper.cos(-f * (float) (Math.PI / 180.0));
         float f6 = MathHelper.sin(-f * (float) (Math.PI / 180.0));
         float f7 = MathHelper.cos((-f + 90.0F) * (float) (Math.PI / 180.0));
         float f8 = MathHelper.sin((-f + 90.0F) * (float) (Math.PI / 180.0));
         float f9 = f3 * f5;
         float f10 = f4 * f5;
         float f11 = f3 * f7;
         float f12 = f4 * f7;
         this.sndSystem.setListenerPosition((float)xCoordinate, (float)yCoordinate, (float)zCoordinate);
         this.sndSystem.setListenerOrientation(f9, f6, f10, f11, f8, f12);
      }
   }

   private synchronized void loadSoundSystem() {
      if (!this.loaded) {
         try {
            new Thread(new Runnable() {
               @Override
               public void run() {
                  SoundSystemConfig.setLogger(new SoundSystemLogger() {
                     public void message(String arg1, int arg2) {
                        if (!arg1.isEmpty()) {
                           SoundManager.logger.info(arg1);
                        }
                     }

                     public void errorMessage(String arg1, String arg2, int arg3) {
                        if (!arg2.isEmpty()) {
                           SoundManager.logger.error("Error in class '" + arg1 + "'");
                           SoundManager.logger.error(arg2);
                        }
                     }

                     public void importantMessage(String arg1, int arg2) {
                        if (!arg1.isEmpty()) {
                           SoundManager.logger.warn(arg1);
                        }
                     }
                  });
                  SoundManager.this.sndSystem = SoundManager.this.new SoundSystemStarterThread();
                  SoundManager.this.loaded = true;
                  SoundManager.this.sndSystem.setMasterVolume(SoundManager.this.options.getSoundLevel(SoundCategory.MASTER));
                  SoundManager.logger.info(SoundManager.LOG_MARKER, "Sound engine started");
               }
            }, "Sound Library Loader").start();
         } catch (RuntimeException runtimeException) {
            logger.error(LOG_MARKER, "Error starting SoundSystem. Turning off sounds & music", runtimeException);
            this.options.setSoundLevel(SoundCategory.MASTER, 0.0F);
            this.options.saveOptions();
         }
      }
   }

   public void stopAllSounds() {
      if (this.loaded) {
         for (String text2 : this.playingSounds.keySet()) {
            this.sndSystem.stop(text2);
         }

         this.playingSounds.clear();
         this.delayedSounds.clear();
         this.tickableSounds.clear();
         this.categorySounds.clear();
         this.playingSoundPoolEntries.clear();
         this.playingSoundsStopTime.clear();
      }
   }

   public void setSoundCategoryVolume(SoundCategory category, float volume) {
      if (this.loaded) {
         if (category == SoundCategory.MASTER) {
            this.sndSystem.setMasterVolume(volume);
         } else {
            for (String text2 : this.categorySounds.get(category)) {
               ISound iSound = this.playingSounds.get(text2);
               float normalizedVolume = this.getNormalizedVolume(iSound, this.playingSoundPoolEntries.get(iSound), category);
               if (normalizedVolume <= 0.0F) {
                  this.stopSound(iSound);
               } else {
                  this.sndSystem.setVolume(text2, normalizedVolume);
               }
            }
         }
      }
   }

   private float getNormalizedPitch(ISound sound, SoundPoolEntry entry) {
      return (float)MathHelper.clamp_double(sound.getPitch() * entry.getPitch(), 0.5, 2.0);
   }

   private static URL getURLForSoundResource(final ResourceLocation location) {
      String text2 = String.format("%s:%s:%s", "mcsounddomain", location.getResourceDomain(), location.getResourcePath());
      URLStreamHandler urlStreamHandler = new URLStreamHandler() {
         @Override
         protected URLConnection openConnection(URL arg1) {
            return new URLConnection(arg1) {
               @Override
               public InputStream getInputStream() throws IOException {
                  return Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream();
               }

               @Override
               public void connect() throws IOException {
               }
            };
         }
      };

      try {
         return new URL((URL)null, text2, urlStreamHandler);
      } catch (MalformedURLException malformedURLException) {
         throw new Error("TODO: Sanely handle url exception! :D");
      }
   }

   private float getNormalizedVolume(ISound sound, SoundPoolEntry entry, SoundCategory category) {
      return (float)MathHelper.clamp_double(sound.getVolume() * entry.getVolume(), 0.0, 1.0) * this.getSoundCategoryVolume(category);
   }

   private float getSoundCategoryVolume(SoundCategory category) {
      return category != null && category != SoundCategory.MASTER ? this.options.getSoundLevel(category) : 1.0F;
   }

   public void playSound(ISound sound) {
      if (this.loaded) {
         if (this.sndSystem.getMasterVolume() <= 0.0F) {
            logger.debug(LOG_MARKER, "Skipped playing soundEvent: {}, master volume was zero", new Object[]{sound.getSoundLocation()});
         } else {
            SoundEventAccessorComposite soundEventAccessorComposite = this.sndHandler.getSound(sound.getSoundLocation());
            if (soundEventAccessorComposite != null) {
               SoundPoolEntry soundPoolEntry = soundEventAccessorComposite.cloneEntry();
               if (soundPoolEntry == SoundHandler.missing_sound) {
                  logger.warn(LOG_MARKER, "Unable to play empty soundEvent: {}", new Object[]{soundEventAccessorComposite.getSoundEventLocation()});
               } else {
                  float volume2 = sound.getVolume();
                  float f = 16.0F;
                  if (volume2 > 1.0F) {
                     f *= volume2;
                  }

                  SoundCategory soundCategory = soundEventAccessorComposite.getSoundCategory();
                  float normalizedVolume = this.getNormalizedVolume(sound, soundPoolEntry, soundCategory);
                  double normalizedPitch = this.getNormalizedPitch(sound, soundPoolEntry);
                  ResourceLocation resourceLocation = soundPoolEntry.getSoundPoolEntryLocation();
                  if (normalizedVolume == 0.0F) {
                     logger.debug(LOG_MARKER, "Skipped playing sound {}, volume was zero.", new Object[]{resourceLocation});
                  } else {
                     boolean flag = sound.canRepeat() && sound.getRepeatDelay() == 0;
                     String text = MathHelper.getRandomUuid(ThreadLocalRandom.current()).toString();
                     if (soundPoolEntry.isStreamingSound()) {
                        this.sndSystem
                           .newStreamingSource(
                              false,
                              text,
                              getURLForSoundResource(resourceLocation),
                              resourceLocation.toString(),
                              flag,
                              sound.getXPosF(),
                              sound.getYPosF(),
                              sound.getZPosF(),
                              sound.getAttenuationType().getTypeInt(),
                              f
                           );
                     } else {
                        this.sndSystem
                           .newSource(
                              false,
                              text,
                              getURLForSoundResource(resourceLocation),
                              resourceLocation.toString(),
                              flag,
                              sound.getXPosF(),
                              sound.getYPosF(),
                              sound.getZPosF(),
                              sound.getAttenuationType().getTypeInt(),
                              f
                           );
                     }

                     logger.debug(
                        LOG_MARKER,
                        "Playing sound {} for event {} as channel {}",
                        soundPoolEntry.getSoundPoolEntryLocation(),
                        soundEventAccessorComposite.getSoundEventLocation(),
                        text
                     );
                     this.sndSystem.setPitch(text, (float)normalizedPitch);
                     this.sndSystem.setVolume(text, normalizedVolume);
                     this.sndSystem.play(text);
                     this.playingSoundsStopTime.put(text, this.playTime + 20);
                     this.playingSounds.put(text, sound);
                     this.playingSoundPoolEntries.put(sound, soundPoolEntry);
                     if (soundCategory != SoundCategory.MASTER) {
                        this.categorySounds.put(soundCategory, text);
                     }

                     if (sound instanceof ITickableSound) {
                        this.tickableSounds.add((ITickableSound)sound);
                     }
                  }
               }
            }
         }
      }
   }

   class SoundSystemStarterThread extends SoundSystem {
      public boolean playing(String arg1) {
         synchronized (SoundSystemConfig.THREAD_SYNC) {
            if (this.soundLibrary == null) {
               return false;
            }

            Source source = (Source)this.soundLibrary.getSources().get(arg1);
            return source == null ? false : source.playing() || source.paused() || source.preLoad;
         }
      }

      private SoundSystemStarterThread() {
      }
   }
}
