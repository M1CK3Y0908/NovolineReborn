package net.minecraft.entity;

import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public interface IEntityMultiPart {
   boolean attackEntityFromPart(EntityDragonPart entityDragonPart, DamageSource damageSource, float f);

   World getWorld();
}
