package net.minecraft.profiler;

import com.google.common.collect.Maps;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.util.HttpUtil;

public class PlayerUsageSnooper {
   private final Map<String, Object> snooperStats = Maps.newHashMap();
   private final Map<String, Object> clientStats = Maps.newHashMap();
   private final String uniqueID = UUID.randomUUID().toString();
   private final Timer threadTrigger = new Timer("Snooper Timer", true);
   private final Object syncLock = new Object();
   private final URL serverUrl;
   private int selfCounter;
   private boolean running;
   private final IPlayerUsage playerStatsCollector;
   private final long minecraftStartTimeMillis;

   private void addOSData() {
      this.update();
      this.addClientStat("snooper_token", this.uniqueID);
      this.addStatToSnooper("snooper_token", this.uniqueID);
      this.addStatToSnooper("os_name", System.getProperty("os.name"));
      this.addStatToSnooper("os_version", System.getProperty("os.version"));
      this.addStatToSnooper("os_architecture", System.getProperty("os.arch"));
      this.addStatToSnooper("java_version", System.getProperty("java.version"));
      this.addClientStat("version", "1.8.9");
      this.playerStatsCollector.addServerTypeToSnooper(this);
   }

   public PlayerUsageSnooper(String side, IPlayerUsage playerStatsCollector, long startTime) {
      try {
         this.serverUrl = new URL("http://snoop.minecraft.net/" + side + "?version=2");
      } catch (MalformedURLException malformedURLException) {
         throw new IllegalArgumentException();
      }

      this.playerStatsCollector = playerStatsCollector;
      this.minecraftStartTimeMillis = startTime;
   }

   public String getUniqueID() {
      return this.uniqueID;
   }

   public boolean isSnooperRunning() {
      return this.running;
   }

   public long getMinecraftStartTimeMillis() {
      return this.minecraftStartTimeMillis;
   }

   private void update() {
      List inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
      int i = 0;

      for (String text : (Iterable<String>) inputArguments) {
         if (text.startsWith("-X")) {
            this.addClientStat("jvm_arg[" + i++ + "]", text);
         }
      }

      this.addClientStat("jvm_args", i);
   }

   public void addStatToSnooper(String name, Object value) {
      synchronized (this.syncLock) {
         this.snooperStats.put(name, value);
      }
   }

   public void addClientStat(String name, Object value) {
      synchronized (this.syncLock) {
         this.clientStats.put(name, value);
      }
   }

   public void addMemoryStatsToSnooper() {
      this.addStatToSnooper("memory_total", Runtime.getRuntime().totalMemory());
      this.addStatToSnooper("memory_max", Runtime.getRuntime().maxMemory());
      this.addStatToSnooper("memory_free", Runtime.getRuntime().freeMemory());
      this.addStatToSnooper("cpu_cores", Runtime.getRuntime().availableProcessors());
      this.playerStatsCollector.addServerStatsToSnooper(this);
   }

   public Map<String, String> getCurrentStats() {
      LinkedHashMap linkedHashMap = Maps.newLinkedHashMap();
      synchronized (this.syncLock) {
         this.addMemoryStatsToSnooper();

         for (Entry entry : this.snooperStats.entrySet()) {
            linkedHashMap.put((String)entry.getKey(), entry.getValue().toString());
         }

         for (Entry entry2 : this.clientStats.entrySet()) {
            linkedHashMap.put((String)entry2.getKey(), entry2.getValue().toString());
         }

         return linkedHashMap;
      }
   }

   public void stopSnooper() {
      this.threadTrigger.cancel();
   }

   public void startSnooper() {
      if (!this.running) {
         this.running = true;
         this.addOSData();
         this.threadTrigger.schedule(new TimerTask() {
            @Override
            public void run() {
               if (PlayerUsageSnooper.this.playerStatsCollector.isSnooperEnabled()) {
                  HashMap hashMap;
                  synchronized (PlayerUsageSnooper.this.syncLock) {
                     hashMap = Maps.newHashMap(PlayerUsageSnooper.this.clientStats);
                     if (PlayerUsageSnooper.this.selfCounter == 0) {
                        hashMap.putAll(PlayerUsageSnooper.this.snooperStats);
                     }

                     hashMap.put("snooper_count", PlayerUsageSnooper.this.selfCounter++);
                     hashMap.put("snooper_token", PlayerUsageSnooper.this.uniqueID);
                  }

                  HttpUtil.createString(PlayerUsageSnooper.this.serverUrl, hashMap);
               }
            }
         }, 0L, 900000L);
      }
   }
}
