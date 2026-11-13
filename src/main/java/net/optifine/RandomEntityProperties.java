package net.optifine;

import java.util.ArrayList;
import java.util.Properties;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.ConnectedParser;

public class RandomEntityProperties {
   public String name = null;
   public String basePath = null;
   public ResourceLocation[] resourceLocations = null;
   public RandomEntityRule[] rules = null;

   public RandomEntityProperties(String path, ResourceLocation[] variants) {
      ConnectedParser connectedParser = new ConnectedParser("RandomEntities");
      this.name = connectedParser.parseName(path);
      this.basePath = connectedParser.parseBasePath(path);
      this.resourceLocations = variants;
   }

   public ResourceLocation getTextureLocation(ResourceLocation loc, IRandomEntity randomEntity) {
      if (this.rules != null) {
         for (int index = 0; index < this.rules.length; index++) {
            RandomEntityRule count = this.rules[index];
            if (count.matches(randomEntity)) {
               return count.getTextureLocation(loc, randomEntity.getId());
            }
         }
      }

      if (this.resourceLocations != null) {
         int id = randomEntity.getId();
         int count = id % this.resourceLocations.length;
         return this.resourceLocations[count];
      } else {
         return loc;
      }
   }

   public RandomEntityProperties(Properties props, String path, ResourceLocation baseResLoc) {
      ConnectedParser connectedParser = new ConnectedParser("RandomEntities");
      this.name = connectedParser.parseName(path);
      this.basePath = connectedParser.parseBasePath(path);
      this.rules = this.parseRules(props, path, baseResLoc, connectedParser);
   }

   public boolean isDefault() {
      return this.rules != null ? false : this.resourceLocations == null;
   }

   private RandomEntityRule[] parseRules(Properties props, String pathProps, ResourceLocation baseResLoc, ConnectedParser cp) {
      ArrayList arrayList = new ArrayList();
      int count = props.size();

      for (int innerIndex = 0; innerIndex < count; innerIndex++) {
         int i = innerIndex + 1;
         String propertyValue = props.getProperty("textures." + i);
         if (propertyValue == null) {
            propertyValue = props.getProperty("skins." + i);
         }

         if (propertyValue != null) {
            RandomEntityRule randomEntityRule = new RandomEntityRule(props, pathProps, baseResLoc, i, propertyValue, cp);
            if (randomEntityRule.isValid(pathProps)) {
               arrayList.add(randomEntityRule);
            }
         }
      }

      return (RandomEntityRule[]) arrayList.toArray(new RandomEntityRule[arrayList.size()]);
   }

   public boolean isValid(String path) {
      if (this.resourceLocations == null && this.rules == null) {
         Config.warn("No skins specified: " + path);
         return false;
      }

      if (this.rules != null) {
         for (int index = 0; index < this.rules.length; index++) {
            RandomEntityRule randomEntityRule = this.rules[index];
            if (!randomEntityRule.isValid(path)) {
               return false;
            }
         }
      }

      if (this.resourceLocations != null) {
         for (int innerIndex = 0; innerIndex < this.resourceLocations.length; innerIndex++) {
            ResourceLocation resourceLocation = this.resourceLocations[innerIndex];
            if (!Config.hasResource(resourceLocation)) {
               Config.warn("Texture not found: " + resourceLocation.getResourcePath());
               return false;
            }
         }
      }

      return true;
   }
}
