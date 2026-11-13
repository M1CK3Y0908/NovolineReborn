package net.minecraft.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.util.ResourceLocation;

public class PotionAbsorption extends Potion {
   @Override
   public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBase, BaseAttributeMap baseAttributeMap, int i) {
      entityLivingBase.setAbsorptionAmount(entityLivingBase.getAbsorptionAmount() - 4 * (i + 1));
      super.removeAttributesModifiersFromEntity(entityLivingBase, baseAttributeMap, i);
   }

   protected PotionAbsorption(int potionID, ResourceLocation location, boolean badEffect, int potionColor) {
      super(potionID, location, badEffect, potionColor);
   }

   @Override
   public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBase, BaseAttributeMap baseAttributeMap, int i) {
      entityLivingBase.setAbsorptionAmount(entityLivingBase.getAbsorptionAmount() + 4 * (i + 1));
      super.applyAttributesModifiersToEntity(entityLivingBase, baseAttributeMap, i);
   }
}
