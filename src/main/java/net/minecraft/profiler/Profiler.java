package net.minecraft.profiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.src.Config;
import net.optifine.Lagometer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Profiler {
   private final List<String> sectionList = Lists.newArrayList();
   private static final String TICK = "tick";
   private static final String DISPLAY = "display";
   private static final String RENDER = "render";
   public boolean profilingEnabled;
   private static final String PRE_RENDER_ERRORS = "preRenderErrors";
   private final List<Long> timestampList = Lists.newArrayList();
   private String profilingSection = "";
   private static final String SCHEDULED_EXECUTABLES = "scheduledExecutables";
   private final Map<String, Long> profilingMap = Maps.newHashMap();
   public boolean profilerGlobalEnabled = true;
   private static final Logger logger = LogManager.getLogger();
   private boolean profilerLocalEnabled = this.profilerGlobalEnabled;
   private static final int HASH_SCHEDULED_EXECUTABLES = "scheduledExecutables".hashCode();
   private static final int HASH_TICK = "tick".hashCode();
   private static final int HASH_PRE_RENDER_ERRORS = "preRenderErrors".hashCode();
   private static final int HASH_RENDER = "render".hashCode();
   private static final int HASH_DISPLAY = "display".hashCode();

   public List<Profiler.Result> getProfilingData(String profilerName) {
      if (!this.profilingEnabled) {
         return null;
      }

      long l = this.profilingMap.containsKey("root") ? this.profilingMap.get("root") : 0L;
      long l2 = this.profilingMap.containsKey(profilerName) ? this.profilingMap.get(profilerName) : -1L;
      ArrayList arrayList = Lists.newArrayList();
      if (profilerName.length() > 0) {
         profilerName = profilerName + ".";
      }

      long l3 = 0L;

      for (String text : this.profilingMap.keySet()) {
         if (text.length() > profilerName.length() && text.startsWith(profilerName) && text.indexOf(".", profilerName.length() + 1) < 0) {
            l3 += this.profilingMap.get(text);
         }
      }

      float f = (float)l3;
      if (l3 < l2) {
         l3 = l2;
      }

      if (l < l3) {
         l = l3;
      }

      for (String text2 : this.profilingMap.keySet()) {
         if (text2.length() > profilerName.length() && text2.startsWith(profilerName) && text2.indexOf(".", profilerName.length() + 1) < 0) {
            long l4 = this.profilingMap.get(text2);
            double d = l4 * 100.0 / l3;
            double d2 = l4 * 100.0 / l;
            String text3 = text2.substring(profilerName.length());
            arrayList.add(new Profiler.Result(text3, d, d2));
         }
      }

      for (String text4 : this.profilingMap.keySet()) {
         this.profilingMap.put(text4, this.profilingMap.get(text4) * 950L / 1000L);
      }

      if ((float)l3 > f) {
         arrayList.add(new Profiler.Result("unspecified", ((float)l3 - f) * 100.0 / l3, ((float)l3 - f) * 100.0 / l));
      }

      Collections.sort(arrayList);
      arrayList.add(0, new Profiler.Result(profilerName, 100.0, l3 * 100.0 / l));
      return arrayList;
   }

   public String getNameOfLastSection() {
      return this.sectionList.size() == 0 ? "[UNKNOWN]" : this.sectionList.get(this.sectionList.size() - 1);
   }

   public void endStartSection(String name) {
      if (this.profilerLocalEnabled) {
         this.endSection();
         this.startSection(name);
      }
   }

   public void startSection(String name) {
      if (Lagometer.isActive()) {
         int i = name.hashCode();
         if (i == HASH_SCHEDULED_EXECUTABLES && name.equals("scheduledExecutables")) {
            Lagometer.timerScheduledExecutables.start();
         } else if (i == HASH_TICK && name.equals("tick") && Config.isMinecraftThread()) {
            Lagometer.timerScheduledExecutables.end();
            Lagometer.timerTick.start();
         } else if (i == HASH_PRE_RENDER_ERRORS && name.equals("preRenderErrors")) {
            Lagometer.timerTick.end();
         }
      }

      if (this.profilerLocalEnabled && this.profilingEnabled) {
         if (this.profilingSection.length() > 0) {
            this.profilingSection = this.profilingSection + ".";
         }

         this.profilingSection = this.profilingSection + name;
         this.sectionList.add(this.profilingSection);
         this.timestampList.add(System.nanoTime());
      }
   }

   public void endSection() {
      if (this.profilerLocalEnabled && this.profilingEnabled) {
         long l = System.nanoTime();
         long l2 = this.timestampList.remove(this.timestampList.size() - 1);
         this.sectionList.remove(this.sectionList.size() - 1);
         long l3 = l - l2;
         if (this.profilingMap.containsKey(this.profilingSection)) {
            this.profilingMap.put(this.profilingSection, this.profilingMap.get(this.profilingSection) + l3);
         } else {
            this.profilingMap.put(this.profilingSection, l3);
         }

         if (l3 > 100000000L) {
            double d = l3 / 1000000.0;
            logger.warn("Something's taking too long! '" + this.profilingSection + "' took aprox " + d + " ms");
         }

         this.profilingSection = !this.sectionList.isEmpty() ? this.sectionList.get(this.sectionList.size() - 1) : "";
      }
   }

   public void clearProfiling() {
      this.profilingMap.clear();
      this.profilingSection = "";
      this.sectionList.clear();
      this.profilerLocalEnabled = this.profilerGlobalEnabled;
   }

   public static final class Result implements Comparable<Profiler.Result> {
      public String sectionName;
      public double usePercentage;
      public double totalUsePercentage;

      public Result(String sectionName, double usePercentage, double totalUsePercentage) {
         this.sectionName = sectionName;
         this.usePercentage = usePercentage;
         this.totalUsePercentage = totalUsePercentage;
      }

      public int compareTo(Profiler.Result other) {
         return other.usePercentage < this.usePercentage ? -1 : (other.usePercentage > this.usePercentage ? 1 : other.sectionName.compareTo(this.sectionName));
      }

      public int getColor() {
         return (this.sectionName.hashCode() & 11184810) + 4473924;
      }
   }
}
