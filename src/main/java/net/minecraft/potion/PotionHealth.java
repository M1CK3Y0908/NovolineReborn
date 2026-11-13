package net.minecraft.potion;

import net.minecraft.util.ResourceLocation;

public class PotionHealth extends Potion {
   @Override
   public boolean isReady(int i, int i2) {
      return i >= 1;
   }

   public PotionHealth(int potionID, ResourceLocation location, boolean badEffect, int potionColor) {
      super(potionID, location, badEffect, potionColor);
   }

   @Override
   public boolean isInstant() {
      return true;
   }
}
