package wtf.novoline.particle;

import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityFXChild8 extends EntityFX {
   protected EntityFXChild8(World world2, double d, double d2, double d3, double d4, double d5, double d6) {
      super(world2, d, d2, d3, d4, d5, d6);
      this.particleRed = 1.0F;
      this.particleGreen = 1.0F;
      this.particleBlue = 1.0F;
      this.setParticleTextureIndex(32);
      this.setSize(0.02F, 0.02F);
      this.particleScale = this.particleScale * (this.rand.nextFloat() * 0.6F + 0.2F);
      this.motionX = d4 * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
      this.motionY = d5 * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
      this.motionZ = d6 * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
      this.particleMaxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.motionY += 0.002;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.85F;
      this.motionY *= 0.85F;
      this.motionZ *= 0.85F;
      if (this.worldObj.getBlockState(new BlockPos(this)).getBlock().getMaterial() != Material.water) {
         this.setDead();
      }

      if (this.particleMaxAge-- <= 0) {
         this.setDead();
      }
   }

   public static class IParticleFactoryImpl implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(World world2, double d, double d2, double d3, double d4, double d5, double d6, int... iArray) {
         return new EntityFXChild8(world2, d, d2, d3, d4, d5, d6);
      }
   }
}
