package net.minecraft.world;

import java.util.Set;
import java.util.TreeMap;
import net.minecraft.nbt.NBTTagCompound;

public class GameRules {
   private TreeMap<String, GameRules.Value> theGameRules = new TreeMap<>();

   public int getInt(String name) {
      GameRules.Value gamerules$value = this.theGameRules.get(name);
      return gamerules$value != null ? gamerules$value.getInt() : 0;
   }

   public void addGameRule(String key, String value, GameRules.ValueType type) {
      this.theGameRules.put(key, new GameRules.Value(value, type));
   }

   public boolean areSameType(String key, GameRules.ValueType otherValue) {
      GameRules.Value gamerules$value = this.theGameRules.get(key);
      return gamerules$value != null && (gamerules$value.getType() == otherValue || otherValue == GameRules.ValueType.ANY_VALUE);
   }

   public boolean getBoolean(String name) {
      GameRules.Value gamerules$value = this.theGameRules.get(name);
      return gamerules$value != null ? gamerules$value.getBoolean() : false;
   }

   public NBTTagCompound writeToNBT() {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();

      for (String text : this.theGameRules.keySet()) {
         GameRules.Value value2 = this.theGameRules.get(text);
         nbtTagCompound.setString(text, value2.getString());
      }

      return nbtTagCompound;
   }

   public void setOrCreateGameRule(String key, String ruleValue) {
      GameRules.Value gamerules$value = this.theGameRules.get(key);
      if (gamerules$value != null) {
         gamerules$value.setValue(ruleValue);
      } else {
         this.addGameRule(key, ruleValue, GameRules.ValueType.ANY_VALUE);
      }
   }

   public GameRules() {
      this.addGameRule("doFireTick", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("mobGriefing", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("keepInventory", "false", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("doMobSpawning", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("doMobLoot", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("doTileDrops", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("doEntityDrops", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("commandBlockOutput", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("naturalRegeneration", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("doDaylightCycle", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("logAdminCommands", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("showDeathMessages", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("randomTickSpeed", "3", GameRules.ValueType.NUMERICAL_VALUE);
      this.addGameRule("sendCommandFeedback", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("reducedDebugInfo", "false", GameRules.ValueType.BOOLEAN_VALUE);
   }

   public String[] getRules() {
      Set<String> set = this.theGameRules.keySet();
      return set.toArray(new String[set.size()]);
   }

   public boolean hasRule(String name) {
      return this.theGameRules.containsKey(name);
   }

   public String getString(String name) {
      GameRules.Value gamerules$value = this.theGameRules.get(name);
      return gamerules$value != null ? gamerules$value.getString() : "";
   }

   public void readFromNBT(NBTTagCompound nbt) {
      for (String text : nbt.getKeySet()) {
         String string = nbt.getString(text);
         this.setOrCreateGameRule(text, string);
      }
   }

   static class Value {
      private final GameRules.ValueType type;
      private String valueString;
      private double valueDouble;
      private boolean valueBoolean;
      private int valueInteger;

      public GameRules.ValueType getType() {
         return this.type;
      }

      public String getString() {
         return this.valueString;
      }

      public void setValue(String value) {
         this.valueString = value;
         if (value != null) {
            if (value.equals("false")) {
               this.valueBoolean = false;
               return;
            }

            if (value.equals("true")) {
               this.valueBoolean = true;
               return;
            }
         }

         this.valueBoolean = Boolean.parseBoolean(value);
         this.valueInteger = this.valueBoolean ? 1 : 0;

         try {
            this.valueInteger = Integer.parseInt(value);
         } catch (NumberFormatException numberFormatException) {
         }

         try {
            this.valueDouble = Double.parseDouble(value);
         } catch (NumberFormatException numberFormatException2) {
         }
      }

      public int getInt() {
         return this.valueInteger;
      }

      public Value(String value, GameRules.ValueType type) {
         this.type = type;
         this.setValue(value);
      }

      public boolean getBoolean() {
         return this.valueBoolean;
      }
   }

   public enum ValueType {
      ANY_VALUE,
      BOOLEAN_VALUE,
      NUMERICAL_VALUE;
   }
}
