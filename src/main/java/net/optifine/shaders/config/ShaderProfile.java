package net.optifine.shaders.config;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ShaderProfile {
   private String name = null;
   private Map<String, String> mapOptionValues = new LinkedHashMap<>();
   private Set<String> disabledPrograms = new LinkedHashSet<>();

   public Collection<String> getDisabledPrograms() {
      return new LinkedHashSet<>(this.disabledPrograms);
   }

   public String getName() {
      return this.name;
   }

   public String[] getOptions() {
      Set<String> set = this.mapOptionValues.keySet();
      return set.toArray(new String[set.size()]);
   }

   public boolean isProgramDisabled(String program) {
      return this.disabledPrograms.contains(program);
   }

   public void addOptionValues(ShaderProfile prof) {
      if (prof != null) {
         this.mapOptionValues.putAll(prof.mapOptionValues);
      }
   }

   public void addDisabledProgram(String program) {
      this.disabledPrograms.add(program);
   }

   public void removeDisabledProgram(String program) {
      this.disabledPrograms.remove(program);
   }

   public void addDisabledPrograms(Collection<String> programs) {
      this.disabledPrograms.addAll(programs);
   }

   public String getValue(String key) {
      return this.mapOptionValues.get(key);
   }

   public void addOptionValue(String option, String value) {
      this.mapOptionValues.put(option, value);
   }

   public ShaderProfile(String name) {
      this.name = name;
   }
}
