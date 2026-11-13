package net.minecraft.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class Explosion {
   private final Random explosionRNG = new Random();
   private final List<BlockPos> affectedBlockPositions = Lists.newArrayList();
   private final Map<EntityPlayer, Vec3> playerKnockbackMap = Maps.newHashMap();
   private final World worldObj;
   private final Entity exploder;
   private final float explosionSize;
   private final double explosionX;
   private final double explosionY;
   private final double explosionZ;
   private final boolean isFlaming;
   private final boolean isSmoking;

   public Explosion(World worldIn, Entity entityIn, double x, double y, double z, float size, List<BlockPos> affectedPositions) {
      this(worldIn, entityIn, x, y, z, size, false, true, affectedPositions);
   }

   public void clearAffectedBlockPositions() {
      this.affectedBlockPositions.clear();
   }

   public void doExplosionA() {
      HashSet hashSet = Sets.newHashSet();

      for (int i = 0; i < 16; i++) {
         for (int i2 = 0; i2 < 16; i2++) {
            for (int i3 = 0; i3 < 16; i3++) {
               if (i == 0 || i == 15 || i2 == 0 || i2 == 15 || i3 == 0 || i3 == 15) {
                  double d = i / 15.0F * 2.0F - 1.0F;
                  double d2 = i2 / 15.0F * 2.0F - 1.0F;
                  double d3 = i3 / 15.0F * 2.0F - 1.0F;
                  double d4 = Math.sqrt(d * d + d2 * d2 + d3 * d3);
                  d /= d4;
                  d2 /= d4;
                  d3 /= d4;
                  float f = this.explosionSize * (0.7F + this.worldObj.rand.nextFloat() * 0.6F);
                  double explosionX2 = this.explosionX;
                  double explosionY2 = this.explosionY;
                  double explosionZ2 = this.explosionZ;

                  while (f > 0.0F) {
                     BlockPos blockPos = new BlockPos(explosionX2, explosionY2, explosionZ2);
                     IBlockState blockState = this.worldObj.getBlockState(blockPos);
                     if (blockState.getBlock().getMaterial() != Material.air) {
                        float f2 = this.exploder != null
                           ? this.exploder.getExplosionResistance(this, this.worldObj, blockPos, blockState)
                           : blockState.getBlock().getExplosionResistance((Entity)null);
                        f -= (f2 + 0.3F) * 0.3F;
                     }

                     if (f > 0.0F && (this.exploder == null || this.exploder.verifyExplosion(this, this.worldObj, blockPos, blockState, f))) {
                        hashSet.add(blockPos);
                     }

                     explosionX2 += d * 0.3F;
                     explosionY2 += d2 * 0.3F;
                     explosionZ2 += d3 * 0.3F;
                     f -= 0.22500001F;
                  }
               }
            }
         }
      }

      this.affectedBlockPositions.addAll(hashSet);
      float f3 = this.explosionSize * 2.0F;
      int i4 = MathHelper.floor_double(this.explosionX - f3 - 1.0);
      int i5 = MathHelper.floor_double(this.explosionX + f3 + 1.0);
      int i6 = MathHelper.floor_double(this.explosionY - f3 - 1.0);
      int i7 = MathHelper.floor_double(this.explosionY + f3 + 1.0);
      int i8 = MathHelper.floor_double(this.explosionZ - f3 - 1.0);
      int i9 = MathHelper.floor_double(this.explosionZ + f3 + 1.0);
      List entitiesWithinAABBExcludingEntity = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.exploder, new AxisAlignedBB(i4, i6, i8, i5, i7, i9));
      Vec3 vec3 = new Vec3(this.explosionX, this.explosionY, this.explosionZ);

      for (int i10 = 0; i10 < entitiesWithinAABBExcludingEntity.size(); i10++) {
         Entity entity2 = (Entity)entitiesWithinAABBExcludingEntity.get(i10);
         if (!entity2.isImmuneToExplosions()) {
            double d5 = entity2.getDistance(this.explosionX, this.explosionY, this.explosionZ) / f3;
            if (d5 <= 1.0) {
               double d6 = entity2.posX - this.explosionX;
               double d7 = entity2.posY + entity2.getEyeHeight() - this.explosionY;
               double d8 = entity2.posZ - this.explosionZ;
               double d9 = MathHelper.sqrt_double(d6 * d6 + d7 * d7 + d8 * d8);
               if (d9 != 0.0) {
                  d6 /= d9;
                  d7 /= d9;
                  d8 /= d9;
                  double blockDensity = this.worldObj.getBlockDensity(vec3, entity2.getEntityBoundingBox());
                  double d10 = (1.0 - d5) * blockDensity;
                  entity2.attackEntityFrom(DamageSource.setExplosionSource(this), (int)((d10 * d10 + d10) / 2.0 * 8.0 * f3 + 1.0));
                  double blastDamageReduction = EnchantmentProtection.getBlastDamageReduction(entity2, d10);
                  entity2.motionX += d6 * blastDamageReduction;
                  entity2.motionY += d7 * blastDamageReduction;
                  entity2.motionZ += d8 * blastDamageReduction;
                  if (entity2 instanceof EntityPlayer && !((EntityPlayer)entity2).capabilities.disableDamage) {
                     this.playerKnockbackMap.put((EntityPlayer)entity2, new Vec3(d6 * d10, d7 * d10, d8 * d10));
                  }
               }
            }
         }
      }
   }

   public List<BlockPos> getAffectedBlockPositions() {
      return this.affectedBlockPositions;
   }

   public void doExplosionB(boolean spawnParticles) {
      this.worldObj
         .playSoundEffect(
            this.explosionX,
            this.explosionY,
            this.explosionZ,
            "random.explode",
            4.0F,
            (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F
         );
      if (this.explosionSize >= 2.0F && this.isSmoking) {
         this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.explosionX, this.explosionY, this.explosionZ, 1.0, 0.0, 0.0);
      } else {
         this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.explosionX, this.explosionY, this.explosionZ, 1.0, 0.0, 0.0);
      }

      if (this.isSmoking) {
         for (BlockPos blockPos : this.affectedBlockPositions) {
            Block block2 = this.worldObj.getBlockState(blockPos).getBlock();
            if (spawnParticles) {
               double d = blockPos.getX() + this.worldObj.rand.nextFloat();
               double d2 = blockPos.getY() + this.worldObj.rand.nextFloat();
               double d3 = blockPos.getZ() + this.worldObj.rand.nextFloat();
               double d4 = d - this.explosionX;
               double d5 = d2 - this.explosionY;
               double d6 = d3 - this.explosionZ;
               double d7 = MathHelper.sqrt_double(d4 * d4 + d5 * d5 + d6 * d6);
               d4 /= d7;
               d5 /= d7;
               d6 /= d7;
               double d8 = 0.5 / (d7 / this.explosionSize + 0.1);
               d8 *= this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F;
               d4 *= d8;
               d5 *= d8;
               d6 *= d8;
               this.worldObj
                  .spawnParticle(
                     EnumParticleTypes.EXPLOSION_NORMAL,
                     (d + this.explosionX * 1.0) / 2.0,
                     (d2 + this.explosionY * 1.0) / 2.0,
                     (d3 + this.explosionZ * 1.0) / 2.0,
                     d4,
                     d5,
                     d6
                  );
               this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d, d2, d3, d4, d5, d6);
            }

            if (block2.getMaterial() != Material.air) {
               if (block2.canDropFromExplosion(this)) {
                  block2.dropBlockAsItemWithChance(this.worldObj, blockPos, this.worldObj.getBlockState(blockPos), 1.0F / this.explosionSize, 0);
               }

               this.worldObj.setBlockState(blockPos, Blocks.air.getDefaultState(), 3);
               block2.onBlockDestroyedByExplosion(this.worldObj, blockPos, this);
            }
         }
      }

      if (this.isFlaming) {
         for (BlockPos blockPos2 : this.affectedBlockPositions) {
            if (this.worldObj.getBlockState(blockPos2).getBlock().getMaterial() == Material.air
               && this.worldObj.getBlockState(blockPos2.down()).getBlock().isFullBlock()
               && this.explosionRNG.nextInt(3) == 0) {
               this.worldObj.setBlockState(blockPos2, Blocks.fire.getDefaultState());
            }
         }
      }
   }

   public Map<EntityPlayer, Vec3> getPlayerKnockbackMap() {
      return this.playerKnockbackMap;
   }

   public Explosion(
      World worldIn, Entity entityIn, double x, double y, double z, float size, boolean flaming, boolean smoking, List<BlockPos> affectedPositions
   ) {
      this(worldIn, entityIn, x, y, z, size, flaming, smoking);
      this.affectedBlockPositions.addAll(affectedPositions);
   }

   public EntityLivingBase getExplosivePlacedBy() {
      return this.exploder == null
         ? null
         : (
            this.exploder instanceof EntityTNTPrimed
               ? ((EntityTNTPrimed)this.exploder).getTntPlacedBy()
               : (this.exploder instanceof EntityLivingBase ? (EntityLivingBase)this.exploder : null)
         );
   }

   public Explosion(World worldIn, Entity entityIn, double x, double y, double z, float size, boolean flaming, boolean smoking) {
      this.worldObj = worldIn;
      this.exploder = entityIn;
      this.explosionSize = size;
      this.explosionX = x;
      this.explosionY = y;
      this.explosionZ = z;
      this.isFlaming = flaming;
      this.isSmoking = smoking;
   }
}
