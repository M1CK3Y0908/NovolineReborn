package net.minecraft.client.stream;

import net.minecraft.entity.EntityLivingBase;

public class MetadataCombat extends Metadata {
   public MetadataCombat(EntityLivingBase entityLivingBase, EntityLivingBase entityLivingBase2) {
      super("player_combat");
      this.handleString("player", entityLivingBase.getName());
      if (entityLivingBase2 != null) {
         this.handleString("primary_opponent", entityLivingBase2.getName());
      }

      if (entityLivingBase2 != null) {
         this.setText2("Combat between " + entityLivingBase.getName() + " and " + entityLivingBase2.getName());
      } else {
         this.setText2("Combat between " + entityLivingBase.getName() + " and others");
      }
   }
}
