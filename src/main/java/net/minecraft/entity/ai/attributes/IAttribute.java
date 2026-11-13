package net.minecraft.entity.ai.attributes;

public interface IAttribute {
   double getDefaultValue();

   String getAttributeUnlocalizedName();

   double clampValue(double d);

   boolean getShouldWatch();

   IAttribute getParentAttribute();
}
