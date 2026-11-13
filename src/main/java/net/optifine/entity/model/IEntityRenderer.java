package net.optifine.entity.model;

import net.minecraft.util.ResourceLocation;

public interface IEntityRenderer {
   Class getEntityClass();

   void setLocationTextureCustom(ResourceLocation resourceLocation);

   void setEntityClass(Class clazz);

   ResourceLocation getLocationTextureCustom();
}
