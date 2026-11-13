package net.minecraft.world;

public class WorldProviderSurface extends WorldProvider {
   @Override
   public String getInternalNameSuffix() {
      return "";
   }

   @Override
   public String getDimensionName() {
      return "Overworld";
   }
}
