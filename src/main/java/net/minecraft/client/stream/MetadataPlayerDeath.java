package net.minecraft.client.stream;

import net.minecraft.entity.EntityLivingBase;

public class MetadataPlayerDeath extends Metadata {
   public MetadataPlayerDeath(EntityLivingBase entityLivingBase, EntityLivingBase entityLivingBase2) {
      super("player_death");
      if (entityLivingBase != null) {
         this.handleString("player", entityLivingBase.getName());
      }

      if (entityLivingBase2 != null) {
         this.handleString("killer", entityLivingBase2.getName());
      }
   }
}
