package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SoundHandler implements IResourceManagerReloadListener, ITickable {
   private static final Logger logger = LogManager.getLogger();
   private final SoundRegistry sndRegistry = new SoundRegistry();
   private static final Gson GSON = new GsonBuilder().registerTypeAdapter(SoundList.class, new SoundListSerializer()).create();
   private final IResourceManager mcResourceManager;
   private static final ParameterizedType TYPE = new ParameterizedType() {
      @Override
      public Type getRawType() {
         return Map.class;
      }

      @Override
      public Type getOwnerType() {
         return null;
      }

      @Override
      public Type[] getActualTypeArguments() {
         return new Type[]{String.class, SoundList.class};
      }
   };
   public static final SoundPoolEntry missing_sound = new SoundPoolEntry(new ResourceLocation("meta:missing_sound"), 0.0, 0.0, false);
   private final SoundManager sndManager;

   public void resumeSounds() {
      this.sndManager.resumeAllSounds();
   }

   public void setSoundLevel(SoundCategory category, float volume) {
      if (category == SoundCategory.MASTER && volume <= 0.0F) {
         this.stopSounds();
      }

      this.sndManager.setSoundCategoryVolume(category, volume);
   }

   public boolean isSoundPlaying(ISound sound) {
      return this.sndManager.isSoundPlaying(sound);
   }

   public SoundEventAccessorComposite getRandomSoundFromCategories(SoundCategory... categories) {
      ArrayList arrayList = Lists.newArrayList();

      for (ResourceLocation resourceLocation : this.sndRegistry.getKeys()) {
         SoundEventAccessorComposite object = this.sndRegistry.getObject(resourceLocation);
         if (ArrayUtils.contains(categories, object.getSoundCategory())) {
            arrayList.add(object);
         }
      }

      return arrayList.isEmpty() ? null : (SoundEventAccessorComposite)arrayList.get(new Random().nextInt(arrayList.size()));
   }

   public void playDelayedSound(ISound sound, int delay) {
      this.sndManager.playDelayedSound(sound, delay);
   }

   protected Map<String, SoundList> getSoundMap(InputStream stream) {
      Map map;
      try {
         map = GSON.fromJson(new InputStreamReader(stream), TYPE);
      } finally {
         IOUtils.closeQuietly(stream);
      }

      return map;
   }

   private void loadSoundResource(ResourceLocation location, SoundList sounds) {
      boolean flag = !this.sndRegistry.containsKey(location);
      SoundEventAccessorComposite soundEventAccessorComposite2;
      if (!flag && !sounds.canReplaceExisting()) {
         soundEventAccessorComposite2 = this.sndRegistry.getObject(location);
      } else {
         if (!flag) {
            logger.debug("Replaced sound event location {}", new Object[]{location});
         }

         soundEventAccessorComposite2 = new SoundEventAccessorComposite(location, 1.0, 1.0, sounds.getSoundCategory());
         this.sndRegistry.registerSound(soundEventAccessorComposite2);
      }

      for (final SoundList.SoundEntry soundEntry : sounds.getSoundList()) {
         String soundEntryName = soundEntry.getSoundEntryName();
         ResourceLocation resourceLocation = new ResourceLocation(soundEntryName);
         final String text = soundEntryName.contains(":") ? resourceLocation.getResourceDomain() : location.getResourceDomain();
         ISoundEventAccessor iSoundEventAccessor;
         switch (soundEntry.getSoundEntryType()) {
            case FILE:
               ResourceLocation resourceLocation2 = new ResourceLocation(text, "sounds/" + resourceLocation.getResourcePath() + ".ogg");
               InputStream inputStream = null;

               try {
                  inputStream = this.mcResourceManager.getResource(resourceLocation2).getInputStream();
               } catch (FileNotFoundException fileNotFoundException) {
                  logger.warn("File {} does not exist, cannot add it to event {}", resourceLocation2, location);
                  continue;
               } catch (IOException ioException) {
                  logger.warn("Could not load sound file " + resourceLocation2 + ", cannot add it to event " + location, ioException);
                  continue;
               } finally {
                  IOUtils.closeQuietly(inputStream);
               }

               iSoundEventAccessor = new SoundEventAccessor(
                  new SoundPoolEntry(resourceLocation2, soundEntry.getSoundEntryPitch(), soundEntry.getSoundEntryVolume(), soundEntry.isStreaming()), soundEntry.getSoundEntryWeight()
               );
               break;
            case SOUND_EVENT:
               iSoundEventAccessor = new ISoundEventAccessor<SoundPoolEntry>() {
                  final ResourceLocation soundEventLocation = new ResourceLocation(text, soundEntry.getSoundEntryName());

                  @Override
                  public int getWeight() {
                     SoundEventAccessorComposite soundEventAccessorComposite = SoundHandler.this.sndRegistry.getObject(this.soundEventLocation);
                     return soundEventAccessorComposite == null ? 0 : soundEventAccessorComposite.getWeight();
                  }

                  public SoundPoolEntry cloneEntry() {
                     SoundEventAccessorComposite soundEventAccessorComposite = SoundHandler.this.sndRegistry.getObject(this.soundEventLocation);
                     return soundEventAccessorComposite == null ? SoundHandler.missing_sound : soundEventAccessorComposite.cloneEntry();
                  }
               };
               break;
            default:
               throw new IllegalStateException("IN YOU FACE");
         }

         soundEventAccessorComposite2.addSoundToEventPool(iSoundEventAccessor);
      }
   }

   public void unloadSounds() {
      this.sndManager.unloadSoundSystem();
   }

   public void playSound(ISound sound) {
      this.sndManager.playSound(sound);
   }

   public void setListener(EntityPlayer player, float f) {
      this.sndManager.setListener(player, f);
   }

   @Override
   public void update() {
      this.sndManager.updateAllSounds();
   }

   @Override
   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.sndManager.reloadSoundSystem();
      this.sndRegistry.clearMap();

      for (String text : resourceManager.getResourceDomains()) {
         try {
            for (IResource iResource : resourceManager.getAllResources(new ResourceLocation(text, "sounds.json"))) {
               try {
                  Map soundMap = this.getSoundMap(iResource.getInputStream());

                  for (Entry entry : (Iterable<Map.Entry>) soundMap.entrySet()) {
                     this.loadSoundResource(new ResourceLocation(text, (String)entry.getKey()), (SoundList)entry.getValue());
                  }
               } catch (RuntimeException runtimeException) {
                  logger.warn("Invalid sounds.json", runtimeException);
               }
            }
         } catch (IOException ioException) {
         }
      }
   }

   public void stopSound(ISound iSound) {
      this.sndManager.stopSound(iSound);
   }

   public void stopSounds() {
      this.sndManager.stopAllSounds();
   }

   public void pauseSounds() {
      this.sndManager.pauseAllSounds();
   }

   public SoundEventAccessorComposite getSound(ResourceLocation location) {
      return this.sndRegistry.getObject(location);
   }

   public SoundHandler(IResourceManager manager, GameSettings gameSettingsIn) {
      this.mcResourceManager = manager;
      this.sndManager = new SoundManager(this, gameSettingsIn);
   }
}
