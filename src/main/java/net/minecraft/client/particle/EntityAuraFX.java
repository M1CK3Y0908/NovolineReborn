package net.minecraft.client.particle;

import net.minecraft.world.World;
import wtf.novoline.particle.EntityFXChild9;

public class EntityAuraFX extends EntityFXChild9 {
   protected EntityAuraFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double speedIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, speedIn, 2.5F);
   }

   public static class IParticleFactoryImpl implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(World world2, double d, double d2, double d3, double d4, double d5, double d6, int... iArray) {
         return new EntityAuraFX(world2, d, d2, d3, d4, d5, d6);
      }
   }
}
