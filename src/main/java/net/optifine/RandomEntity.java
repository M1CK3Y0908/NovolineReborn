package net.optifine;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;

public class RandomEntity implements IRandomEntity {
   private Entity entity;

   @Override
   public int getMaxHealth() {
      return !(this.entity instanceof EntityLiving) ? 0 : (int)((EntityLiving)this.entity).getMaxHealth();
   }

   public void setEntity(Entity entity) {
      this.entity = entity;
   }

   @Override
   public int getId() {
      long leastSignificantBits = this.entity.getUniqueID().getLeastSignificantBits();
      return (int)(leastSignificantBits & 2147483647L);
   }

   public Entity getEntity() {
      return this.entity;
   }

   @Override
   public String getName() {
      return this.entity.hasCustomName() ? this.entity.getCustomNameTag() : null;
   }

   @Override
   public BiomeGenBase getSpawnBiome() {
      return this.entity.getDataWatcher().spawnBiome;
   }

   @Override
   public int getHealth() {
      return !(this.entity instanceof EntityLiving) ? 0 : (int)((EntityLiving)this.entity).getHealth();
   }

   @Override
   public BlockPos getSpawnPosition() {
      return this.entity.getDataWatcher().spawnPosition;
   }
}
