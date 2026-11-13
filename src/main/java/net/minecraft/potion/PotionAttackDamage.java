package net.minecraft.potion;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.ResourceLocation;

public class PotionAttackDamage extends Potion {
   @Override
   public double getAttributeModifierAmount(int i, AttributeModifier modifier) {
      return this.id == Potion.weakness.id ? -0.5F * (i + 1) : 1.3 * (i + 1);
   }

   protected PotionAttackDamage(int potionID, ResourceLocation location, boolean badEffect, int potionColor) {
      super(potionID, location, badEffect, potionColor);
   }
}
