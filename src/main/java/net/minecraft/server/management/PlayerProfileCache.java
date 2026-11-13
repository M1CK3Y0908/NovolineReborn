package net.minecraft.server.management;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.IOUtils;

public class PlayerProfileCache {
   public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
   private final Map<String, PlayerProfileCache.ProfileEntry> usernameToProfileEntryMap = Maps.newHashMap();
   private final Map<UUID, PlayerProfileCache.ProfileEntry> uuidToProfileEntryMap = Maps.newHashMap();
   private final LinkedList<GameProfile> gameProfiles = Lists.newLinkedList();
   private final MinecraftServer mcServer;
   private static final ParameterizedType TYPE = new ParameterizedType() {
      @Override
      public Type getRawType() {
         return List.class;
      }

      @Override
      public Type[] getActualTypeArguments() {
         return new Type[]{PlayerProfileCache.ProfileEntry.class};
      }

      @Override
      public Type getOwnerType() {
         return null;
      }
   };
   private final File usercacheFile;
   protected final Gson gson;

   public PlayerProfileCache(MinecraftServer server, File cacheFile) {
      this.mcServer = server;
      this.usercacheFile = cacheFile;
      GsonBuilder gsonBuilder = new GsonBuilder();
      gsonBuilder.registerTypeHierarchyAdapter(PlayerProfileCache.ProfileEntry.class, new PlayerProfileCache.Serializer());
      this.gson = gsonBuilder.create();
      this.load();
   }

   public String[] getUsernames() {
      List<String> list = Lists.newArrayList(this.usernameToProfileEntryMap.keySet());
      return list.toArray(new String[list.size()]);
   }

   public void load() {
      BufferedReader bufferedReader = null;

      try {
         bufferedReader = Files.newReader(this.usercacheFile, Charsets.UTF_8);
         List list2 = this.gson.fromJson(bufferedReader, TYPE);
         this.usernameToProfileEntryMap.clear();
         this.uuidToProfileEntryMap.clear();
         this.gameProfiles.clear();

         for (PlayerProfileCache.ProfileEntry profileEntry : (Iterable<PlayerProfileCache.ProfileEntry>) Lists.reverse(list2)) {
            if (profileEntry != null) {
               this.addEntry(profileEntry.getGameProfile(), profileEntry.getExpirationDate());
            }
         }
      } catch (FileNotFoundException fileNotFoundException) {
      } catch (JsonParseException jsonParseException) {
      } finally {
         IOUtils.closeQuietly(bufferedReader);
      }
   }

   public GameProfile getProfileByUUID(UUID uUID) {
      PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = this.uuidToProfileEntryMap.get(uUID);
      return playerprofilecache$profileentry == null ? null : playerprofilecache$profileentry.getGameProfile();
   }

   private void addEntry(GameProfile gameProfile, Date expirationDate) {
      UUID id = gameProfile.getId();
      if (expirationDate == null) {
         Calendar instance = Calendar.getInstance();
         instance.setTime(new Date());
         instance.add(2, 1);
         expirationDate = instance.getTime();
      }

      gameProfile.getName().toLowerCase(Locale.ROOT);
      PlayerProfileCache.ProfileEntry profileEntry = new PlayerProfileCache.ProfileEntry(gameProfile, expirationDate);
      if (this.uuidToProfileEntryMap.containsKey(id)) {
         PlayerProfileCache.ProfileEntry profileEntry2 = this.uuidToProfileEntryMap.get(id);
         this.usernameToProfileEntryMap.remove(profileEntry2.getGameProfile().getName().toLowerCase(Locale.ROOT));
         this.gameProfiles.remove(gameProfile);
      }

      this.usernameToProfileEntryMap.put(gameProfile.getName().toLowerCase(Locale.ROOT), profileEntry);
      this.uuidToProfileEntryMap.put(id, profileEntry);
      this.gameProfiles.addFirst(gameProfile);
      this.save();
   }

   public void addEntry(GameProfile gameProfile) {
      this.addEntry(gameProfile, (Date)null);
   }

   public void save() {
      String text3 = this.gson.toJson(this.getList());
      BufferedWriter bufferedWriter = null;

      try {
         bufferedWriter = Files.newWriter(this.usercacheFile, Charsets.UTF_8);
         bufferedWriter.write(text3);
         return;
      } catch (FileNotFoundException fileNotFoundException) {
         return;
      } catch (IOException ioException) {
      } finally {
         IOUtils.closeQuietly(bufferedWriter);
      }
   }

   private PlayerProfileCache.ProfileEntry getByUUID(UUID uUID) {
      PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = this.uuidToProfileEntryMap.get(uUID);
      if (playerprofilecache$profileentry != null) {
         GameProfile gameProfile = playerprofilecache$profileentry.getGameProfile();
         this.gameProfiles.remove(gameProfile);
         this.gameProfiles.addFirst(gameProfile);
      }

      return playerprofilecache$profileentry;
   }

   private List getList() {
      ArrayList arrayList = Lists.newArrayList();

      for (GameProfile gameProfile3 : Lists.newArrayList(Iterators.limit(this.gameProfiles.iterator(), 1000))) {
         PlayerProfileCache.ProfileEntry byUUID = this.getByUUID(gameProfile3.getId());
         if (byUUID != null) {
            arrayList.add(byUUID);
         }
      }

      return arrayList;
   }

   private static GameProfile getGameProfile(MinecraftServer server, String username) {
      final GameProfile[] agameprofile = new GameProfile[1];
      ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback() {
         @Override
         public void onProfileLookupSucceeded(GameProfile arg1) {
            agameprofile[0] = arg1;
         }

         @Override
         public void onProfileLookupFailed(GameProfile arg1, Exception arg2) {
            agameprofile[0] = null;
         }
      };
      server.getGameProfileRepository().findProfilesByNames(new String[]{username}, Agent.MINECRAFT, profileLookupCallback);
      if (!server.isServerInOnlineMode() && agameprofile[0] == null) {
         UUID uUID = EntityPlayer.getUUID(new GameProfile((UUID)null, username));
         GameProfile gameProfile3 = new GameProfile(uUID, username);
         profileLookupCallback.onProfileLookupSucceeded(gameProfile3);
      }

      return agameprofile[0];
   }

   public GameProfile getGameProfileForUsername(String username) {
      String text = username.toLowerCase(Locale.ROOT);
      PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = this.usernameToProfileEntryMap.get(text);
      if (playerprofilecache$profileentry != null && new Date().getTime() >= playerprofilecache$profileentry.expirationDate.getTime()) {
         this.uuidToProfileEntryMap.remove(playerprofilecache$profileentry.getGameProfile().getId());
         this.usernameToProfileEntryMap.remove(playerprofilecache$profileentry.getGameProfile().getName().toLowerCase(Locale.ROOT));
         this.gameProfiles.remove(playerprofilecache$profileentry.getGameProfile());
         playerprofilecache$profileentry = null;
      }

      if (playerprofilecache$profileentry != null) {
         GameProfile gameProfile = playerprofilecache$profileentry.getGameProfile();
         this.gameProfiles.remove(gameProfile);
         this.gameProfiles.addFirst(gameProfile);
      } else {
         GameProfile gameProfile2 = getGameProfile(this.mcServer, text);
         if (gameProfile2 != null) {
            this.addEntry(gameProfile2);
            playerprofilecache$profileentry = this.usernameToProfileEntryMap.get(text);
         }
      }

      this.save();
      return playerprofilecache$profileentry == null ? null : playerprofilecache$profileentry.getGameProfile();
   }

   class ProfileEntry {
      private final GameProfile gameProfile;
      private final Date expirationDate;

      public Date getExpirationDate() {
         return this.expirationDate;
      }

      private ProfileEntry(GameProfile gameProfileIn, Date expirationDateIn) {
         this.gameProfile = gameProfileIn;
         this.expirationDate = expirationDateIn;
      }

      public GameProfile getGameProfile() {
         return this.gameProfile;
      }
   }

   class Serializer implements JsonDeserializer<PlayerProfileCache.ProfileEntry>, JsonSerializer<PlayerProfileCache.ProfileEntry> {
      public JsonElement serialize(PlayerProfileCache.ProfileEntry arg1, Type arg2, JsonSerializationContext arg3) {
         JsonObject jsonObject2 = new JsonObject();
         jsonObject2.addProperty("name", arg1.getGameProfile().getName());
         UUID uUID = arg1.getGameProfile().getId();
         jsonObject2.addProperty("uuid", uUID == null ? "" : uUID.toString());
         jsonObject2.addProperty("expiresOn", PlayerProfileCache.dateFormat.format(arg1.getExpirationDate()));
         return jsonObject2;
      }

      public PlayerProfileCache.ProfileEntry deserialize(JsonElement arg1, Type arg2, JsonDeserializationContext arg3) throws JsonParseException {
         if (arg1.isJsonObject()) {
            JsonObject jsonObject = arg1.getAsJsonObject();
            JsonElement jsonElement = jsonObject.get("name");
            JsonElement jsonElement2 = jsonObject.get("uuid");
            JsonElement jsonElement3 = jsonObject.get("expiresOn");
            if (jsonElement != null && jsonElement2 != null) {
               String text = jsonElement2.getAsString();
               String text2 = jsonElement.getAsString();
               Date date = null;
               if (jsonElement3 != null) {
                  try {
                     date = PlayerProfileCache.dateFormat.parse(jsonElement3.getAsString());
                  } catch (ParseException parseException) {
                     date = null;
                  }
               }

               if (text2 != null && text != null) {
                  UUID uuid2;
                  try {
                     uuid2 = UUID.fromString(text);
                  } catch (Throwable throwable) {
                     return null;
                  }

                  return PlayerProfileCache.this.new ProfileEntry(new GameProfile(uuid2, text2), date);
               } else {
                  return null;
               }
            } else {
               return null;
            }
         } else {
            return null;
         }
      }

      private Serializer() {
      }
   }
}
