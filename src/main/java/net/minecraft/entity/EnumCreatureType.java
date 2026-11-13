package net.minecraft.entity;

import net.minecraft.block.material.Material;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.passive.IAnimals;

public enum EnumCreatureType {
   MONSTER(IMob.class, 70, Material.air, false, false),
   CREATURE(EntityAnimal.class, 10, Material.air, true, true),
   AMBIENT(EntityAmbientCreature.class, 15, Material.air, true, false),
   WATER_CREATURE(EntityWaterMob.class, 5, Material.water, true, false);

   private final Class<? extends IAnimals> creatureClass;
   private final int maxNumberOfCreature;
   private final Material creatureMaterial;
   private final boolean isPeacefulCreature;
   private final boolean isAnimal;

   public boolean getPeacefulCreature() {
      return this.isPeacefulCreature;
   }

   public Class<? extends IAnimals> getCreatureClass() {
      return this.creatureClass;
   }

   public boolean getAnimal() {
      return this.isAnimal;
   }

   public int getMaxNumberOfCreature() {
      return this.maxNumberOfCreature;
   }

   EnumCreatureType(Class<? extends IAnimals> clazz, int i, Material material2, boolean flag, boolean flag2) {
      this.creatureClass = clazz;
      this.maxNumberOfCreature = i;
      this.creatureMaterial = material2;
      this.isPeacefulCreature = flag;
      this.isAnimal = flag2;
   }
}
