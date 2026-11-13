package net.minecraft.entity.ai.attributes;

import java.util.Collection;
import java.util.UUID;

public interface IAttributeInstance {
   Collection getModifiers();

   double getBaseValue();

   void removeModifier(AttributeModifier attributeModifier);

   Collection<AttributeModifier> getModifiersByOperation(int i);

   IAttribute getAttribute();

   boolean hasModifier(AttributeModifier attributeModifier);

   double getAttributeValue();

   void applyModifier(AttributeModifier attributeModifier);

   void setBaseValue(double d);

   AttributeModifier getModifier(UUID uuid);

   void removeAllModifiers();
}
