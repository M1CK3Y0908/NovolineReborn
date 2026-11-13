package net.minecraft.stats;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.TupleIntJsonSerializable;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StatisticsFile extends StatFileWriter {
   private final Set<StatBase> dirtyStats = Sets.newHashSet();
   private int lastStatUpdateTick = -300;
   private boolean hasAchievementDataChanged = false;
   private final MinecraftServer mcServer;
   private final File statsFile;
   private static final Logger logger = LogManager.getLogger();

   public Map<StatBase, TupleIntJsonSerializable> parseJson(String jsonString) {
      JsonElement jsonElement = new JsonParser().parse(jsonString);
      if (!jsonElement.isJsonObject()) {
         return Maps.newHashMap();
      }

      JsonObject asJsonObject = jsonElement.getAsJsonObject();
      HashMap hashMap = Maps.newHashMap();

      for (Entry entry : asJsonObject.entrySet()) {
         StatBase oneShotStat = StatList.getOneShotStat((String)entry.getKey());
         if (oneShotStat != null) {
            TupleIntJsonSerializable tupleIntJsonSerializable = new TupleIntJsonSerializable();
            if (((JsonElement)entry.getValue()).isJsonPrimitive() && ((JsonElement)entry.getValue()).getAsJsonPrimitive().isNumber()) {
               tupleIntJsonSerializable.setIntegerValue(((JsonElement)entry.getValue()).getAsInt());
            } else if (((JsonElement)entry.getValue()).isJsonObject()) {
               JsonObject asJsonObject2 = ((JsonElement)entry.getValue()).getAsJsonObject();
               if (asJsonObject2.has("value") && asJsonObject2.get("value").isJsonPrimitive() && asJsonObject2.get("value").getAsJsonPrimitive().isNumber()) {
                  tupleIntJsonSerializable.setIntegerValue(asJsonObject2.getAsJsonPrimitive("value").getAsInt());
               }

               if (asJsonObject2.has("progress") && oneShotStat.getSerializableClass() != null) {
                  try {
                     Constructor constructor = oneShotStat.getSerializableClass().getConstructor();
                     IJsonSerializable iJsonSerializable = (IJsonSerializable)constructor.newInstance();
                     iJsonSerializable.fromJson(asJsonObject2.get("progress"));
                     tupleIntJsonSerializable.setJsonSerializableValue(iJsonSerializable);
                  } catch (Throwable throwable) {
                     logger.warn("Invalid statistic progress in " + this.statsFile, throwable);
                  }
               }
            }

            hashMap.put(oneShotStat, tupleIntJsonSerializable);
         } else {
            logger.warn("Invalid statistic in " + this.statsFile + ": Don't know what " + (String)entry.getKey() + " is");
         }
      }

      return hashMap;
   }

   public void readStatFile() {
      if (this.statsFile.isFile()) {
         try {
            this.statsData.clear();
            this.statsData.putAll(this.parseJson(FileUtils.readFileToString(this.statsFile)));
         } catch (IOException ioException) {
            logger.error("Couldn't read statistics file " + this.statsFile, ioException);
         } catch (JsonParseException jsonParseException) {
            logger.error("Couldn't parse statistics file " + this.statsFile, jsonParseException);
         }
      }
   }

   public void markAllDirty() {
      for (StatBase statBase : this.statsData.keySet()) {
         this.dirtyStats.add(statBase);
      }
   }

   @Override
   public void unlockAchievement(EntityPlayer playerIn, StatBase statIn, int value) {
      int i = statIn.isAchievement() ? this.readStat(statIn) : 0;
      super.unlockAchievement(playerIn, statIn, value);
      this.dirtyStats.add(statIn);
      if (statIn.isAchievement() && i == 0 && value > 0) {
         this.hasAchievementDataChanged = true;
         if (this.mcServer.isAnnouncingPlayerAchievements()) {
            this.mcServer
               .getConfigurationManager()
               .sendChatMsg(new ChatComponentTranslation("chat.type.achievement", playerIn.getDisplayName(), statIn.createChatComponent()));
         }
      }

      if (statIn.isAchievement() && i > 0 && value == 0) {
         this.hasAchievementDataChanged = true;
         if (this.mcServer.isAnnouncingPlayerAchievements()) {
            this.mcServer
               .getConfigurationManager()
               .sendChatMsg(new ChatComponentTranslation("chat.type.achievement.taken", playerIn.getDisplayName(), statIn.createChatComponent()));
         }
      }
   }

   public boolean hasAchievementDataChanged() {
      return this.hasAchievementDataChanged;
   }

   public StatisticsFile(MinecraftServer serverIn, File statsFileIn) {
      this.mcServer = serverIn;
      this.statsFile = statsFileIn;
   }

   public void sendStats(EntityPlayerMP player) {
      int tickCounter = this.mcServer.getTickCounter();
      HashMap hashMap = Maps.newHashMap();
      if (this.hasAchievementDataChanged || tickCounter - this.lastStatUpdateTick > 300) {
         this.lastStatUpdateTick = tickCounter;

         for (StatBase statBase : this.getDirtyStats()) {
            hashMap.put(statBase, this.readStat(statBase));
         }
      }

      player.playerNetServerHandler.sendPacket(new S37PacketStatistics(hashMap));
   }

   public Set<StatBase> getDirtyStats() {
      Set<StatBase> set = Sets.newHashSet(this.dirtyStats);
      this.dirtyStats.clear();
      this.hasAchievementDataChanged = false;
      return set;
   }

   public void sendAchievements(EntityPlayerMP player) {
      HashMap hashMap = Maps.newHashMap();

      for (Achievement achievement2 : AchievementList.achievementList) {
         if (this.hasAchievementUnlocked(achievement2)) {
            hashMap.put(achievement2, this.readStat(achievement2));
            this.dirtyStats.remove(achievement2);
         }
      }

      player.playerNetServerHandler.sendPacket(new S37PacketStatistics(hashMap));
   }

   public void saveStatFile() {
      try {
         FileUtils.writeStringToFile(this.statsFile, dumpJson(this.statsData));
      } catch (IOException ioException) {
         logger.error("Couldn't save stats", ioException);
      }
   }

   public static String dumpJson(Map<StatBase, TupleIntJsonSerializable> statsData) {
      JsonObject jsonObject = new JsonObject();

      for (Entry entry : statsData.entrySet()) {
         if (((TupleIntJsonSerializable)entry.getValue()).getJsonSerializableValue() != null) {
            JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("value", ((TupleIntJsonSerializable)entry.getValue()).getIntegerValue());

            try {
               jsonObject2.add("progress", ((TupleIntJsonSerializable)entry.getValue()).<IJsonSerializable>getJsonSerializableValue().getSerializableElement());
            } catch (Throwable throwable) {
               logger.warn("Couldn't save statistic " + ((StatBase)entry.getKey()).getStatName() + ": error serializing progress", throwable);
            }

            jsonObject.add(((StatBase)entry.getKey()).statId, jsonObject2);
         } else {
            jsonObject.addProperty(((StatBase)entry.getKey()).statId, ((TupleIntJsonSerializable)entry.getValue()).getIntegerValue());
         }
      }

      return jsonObject.toString();
   }
}
