package net.minecraft.entity.item;

import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityMinecartTNT extends EntityMinecart {
   private int minecartTNTFuse = -1;

   public EntityMinecartTNT(World worldIn) {
      super(worldIn);
   }

   @Override
   protected void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      if (tagCompund.hasKey("TNTFuse", 99)) {
         this.minecartTNTFuse = tagCompund.getInteger("TNTFuse");
      }
   }

   @Override
   public void setHasDisplayTile(boolean flag) {
      if (flag && this.minecartTNTFuse < 0) {
         this.ignite();
      }
   }

   public void ignite() {
      this.minecartTNTFuse = 80;
      if (!this.worldObj.isRemote) {
         this.worldObj.setEntityState(this, (byte)10);
         if (!this.isSilent()) {
            this.worldObj.playSoundAtEntity(this, "game.tnt.primed", 1.0F, 1.0F);
         }
      }
   }

   public EntityMinecartTNT(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (this.minecartTNTFuse > 0) {
         this.minecartTNTFuse--;
         this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5, this.posZ, 0.0, 0.0, 0.0);
      } else if (this.minecartTNTFuse == 0) {
         this.explodeCart(this.motionX * this.motionX + this.motionZ * this.motionZ);
      }

      if (this.isCollidedHorizontally) {
         double d = this.motionX * this.motionX + this.motionZ * this.motionZ;
         if (d >= 0.01F) {
            this.explodeCart(d);
         }
      }
   }

   @Override
   public EntityMinecart.EnumMinecartType getMinecartType() {
      return EntityMinecart.EnumMinecartType.TNT;
   }

   @Override
   public void killMinecart(DamageSource source) {
      super.killMinecart(source);
      double d = this.motionX * this.motionX + this.motionZ * this.motionZ;
      if (!source.isExplosion() && this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
         this.entityDropItem(new ItemStack(Blocks.tnt, 1), 0.0F);
      }

      if (source.isFireDamage() || source.isExplosion() || d >= 0.01F) {
         this.explodeCart(d);
      }
   }

   @Override
   public void handleStatusUpdate(byte id) {
      if (id == 10) {
         this.ignite();
      } else {
         super.handleStatusUpdate(id);
      }
   }

   @Override
   public void fall(float distance, float damageMultiplier) {
      if (distance >= 3.0F) {
         float f = distance / 10.0F;
         this.explodeCart(f * f);
      }

      super.fall(distance, damageMultiplier);
   }

   @Override
   protected void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setInteger("TNTFuse", this.minecartTNTFuse);
   }

   public boolean isIgnited() {
      return this.minecartTNTFuse > -1;
   }

   @Override
   public float getExplosionResistance(Explosion explosionIn, World worldIn, BlockPos pos, IBlockState blockStateIn) {
      return this.isIgnited() && (BlockRailBase.isRailBlock(blockStateIn) || BlockRailBase.isRailBlock(worldIn, pos.up()))
         ? 0.0F
         : super.getExplosionResistance(explosionIn, worldIn, pos, blockStateIn);
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (source.getSourceOfDamage() instanceof EntityArrow entityArrow && entityArrow.isBurning()) {
         this.explodeCart(entityArrow.motionX * entityArrow.motionX + entityArrow.motionY * entityArrow.motionY + entityArrow.motionZ * entityArrow.motionZ);
      }

      return super.attackEntityFrom(source, amount);
   }

   @Override
   public boolean verifyExplosion(Explosion explosionIn, World worldIn, BlockPos pos, IBlockState blockStateIn, float f) {
      return this.isIgnited() && (BlockRailBase.isRailBlock(blockStateIn) || BlockRailBase.isRailBlock(worldIn, pos.up()))
         ? false
         : super.verifyExplosion(explosionIn, worldIn, pos, blockStateIn, f);
   }

   @Override
   public IBlockState getDefaultDisplayTile() {
      return Blocks.tnt.getDefaultState();
   }

   public int getFuseTicks() {
      return this.minecartTNTFuse;
   }

   protected void explodeCart(double d) {
      if (!this.worldObj.isRemote) {
         double d2 = Math.sqrt(d);
         if (d2 > 5.0) {
            d2 = 5.0;
         }

         this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, (float)(4.0 + this.rand.nextDouble() * 1.5 * d2), true);
         this.setDead();
      }
   }
}
