package net.minecraft.entity.boss;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityDragon extends EntityLiving implements IBossDisplayData, IEntityMultiPart, IMob {
   public EntityEnderCrystal healingEnderCrystal;
   public double[][] ringBuffer = new double[64][3];
   public double targetX;
   public boolean forceNewTarget;
   public int ringBufferIndex = -1;
   public double targetZ;
   public int deathTicks;
   public EntityDragonPart dragonPartHead;
   public EntityDragonPart dragonPartBody;
   public float prevAnimTime;
   public float animTime;
   public EntityDragonPart dragonPartTail1;
   public EntityDragonPart dragonPartTail2;
   public boolean slowed;
   public EntityDragonPart dragonPartTail3;
   private Entity target;
   public EntityDragonPart dragonPartWing1;
   public EntityDragonPart dragonPartWing2;
   public EntityDragonPart[] dragonPartArray;
   public double targetY;

   private void setNewTarget() {
      this.forceNewTarget = false;
      ArrayList arrayList = Lists.newArrayList(this.worldObj.playerEntities);
      Iterator iterator2 = arrayList.iterator();

      while (iterator2.hasNext()) {
         if (((EntityPlayer)iterator2.next()).isSpectator()) {
            iterator2.remove();
         }
      }

      if (this.rand.nextInt(2) == 0 && !arrayList.isEmpty()) {
         this.target = (Entity)arrayList.get(this.rand.nextInt(arrayList.size()));
      } else {
         double d;
         double d2;
         double d3;
         do {
            this.targetX = 0.0;
            this.targetY = 70.0F + this.rand.nextFloat() * 50.0F;
            this.targetZ = 0.0;
            this.targetX = this.targetX + (this.rand.nextFloat() * 120.0F - 60.0F);
            this.targetZ = this.targetZ + (this.rand.nextFloat() * 120.0F - 60.0F);
            d = this.posX - this.targetX;
            d2 = this.posY - this.targetY;
            d3 = this.posZ - this.targetZ;
         } while (!(d * d + d2 * d2 + d3 * d3 > 100.0));

         this.target = null;
      }
   }

   @Override
   protected void entityInit() {
      super.entityInit();
   }

   @Override
   protected void onDeathUpdate() {
      this.deathTicks++;
      if (this.deathTicks >= 180 && this.deathTicks <= 200) {
         float f = (this.rand.nextFloat() - 0.5F) * 8.0F;
         float f2 = (this.rand.nextFloat() - 0.5F) * 4.0F;
         float f3 = (this.rand.nextFloat() - 0.5F) * 8.0F;
         this.worldObj
            .spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.posX + f, this.posY + 2.0 + f2, this.posZ + f3, 0.0, 0.0, 0.0);
      }

      boolean conditionMet = this.worldObj.getGameRules().getBoolean("doMobLoot");
      if (!this.worldObj.isRemote) {
         if (this.deathTicks > 150 && this.deathTicks % 5 == 0 && conditionMet) {
            int i = 1000;

            while (i > 0) {
               int xpSplit = EntityXPOrb.getXPSplit(i);
               i -= xpSplit;
               this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, xpSplit));
            }
         }

         if (this.deathTicks == 1) {
            this.worldObj.playBroadcastSound(1018, new BlockPos(this), 0);
         }
      }

      this.moveEntity(0.0, 0.1F, 0.0);
      this.renderYawOffset = this.rotationYaw += 20.0F;
      if (this.deathTicks == 200 && !this.worldObj.isRemote) {
         if (conditionMet) {
            int i2 = 2000;

            while (i2 > 0) {
               int xpSplit2 = EntityXPOrb.getXPSplit(i2);
               i2 -= xpSplit2;
               this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, xpSplit2));
            }
         }

         this.generatePortal(new BlockPos(this.posX, 64.0, this.posZ));
         this.setDead();
      }
   }

   @Override
   public void onLivingUpdate() {
      if (this.worldObj.isRemote) {
         float f = MathHelper.cos(this.animTime * (float) Math.PI * 2.0F);
         float f2 = MathHelper.cos(this.prevAnimTime * (float) Math.PI * 2.0F);
         if (f2 <= -0.3F && f >= -0.3F && !this.isSilent()) {
            this.worldObj.playSound(this.posX, this.posY, this.posZ, "mob.enderdragon.wings", 5.0F, 0.8F + this.rand.nextFloat() * 0.3F, false);
         }
      }

      this.prevAnimTime = this.animTime;
      if (this.getHealth() <= 0.0F) {
         float f3 = (this.rand.nextFloat() - 0.5F) * 8.0F;
         float f4 = (this.rand.nextFloat() - 0.5F) * 4.0F;
         float f5 = (this.rand.nextFloat() - 0.5F) * 8.0F;
         this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX + f3, this.posY + 2.0 + f4, this.posZ + f5, 0.0, 0.0, 0.0);
      } else {
         this.updateDragonEnderCrystal();
         float f6 = 0.2F / (MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 10.0F + 1.0F);
         f6 *= (float)Math.pow(2.0, this.motionY);
         if (this.slowed) {
            this.animTime += f6 * 0.5F;
         } else {
            this.animTime += f6;
         }

         this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw);
         if (this.isAIDisabled()) {
            this.animTime = 0.5F;
         } else {
            if (this.ringBufferIndex < 0) {
               for (int i = 0; i < this.ringBuffer.length; i++) {
                  this.ringBuffer[i][0] = this.rotationYaw;
                  this.ringBuffer[i][1] = this.posY;
               }
            }

            if (++this.ringBufferIndex == this.ringBuffer.length) {
               this.ringBufferIndex = 0;
            }

            this.ringBuffer[this.ringBufferIndex][0] = this.rotationYaw;
            this.ringBuffer[this.ringBufferIndex][1] = this.posY;
            if (this.worldObj.isRemote) {
               if (this.newPosRotationIncrements > 0) {
                  double d = this.posX + (this.newPosX - this.posX) / this.newPosRotationIncrements;
                  double d2 = this.posY + (this.newPosY - this.posY) / this.newPosRotationIncrements;
                  double d3 = this.posZ + (this.newPosZ - this.posZ) / this.newPosRotationIncrements;
                  double d4 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - this.rotationYaw);
                  this.rotationYaw = (float)(this.rotationYaw + d4 / this.newPosRotationIncrements);
                  this.rotationPitch = (float)(this.rotationPitch + (this.newRotationPitch - this.rotationPitch) / this.newPosRotationIncrements);
                  this.newPosRotationIncrements--;
                  this.setPosition(d, d2, d3);
                  this.setRotation(this.rotationYaw, this.rotationPitch);
               }
            } else {
               double d5 = this.targetX - this.posX;
               double d6 = this.targetY - this.posY;
               double d7 = this.targetZ - this.posZ;
               double d8 = d5 * d5 + d6 * d6 + d7 * d7;
               if (this.target != null) {
                  this.targetX = this.target.posX;
                  this.targetZ = this.target.posZ;
                  double d9 = this.targetX - this.posX;
                  double d10 = this.targetZ - this.posZ;
                  double d11 = Math.sqrt(d9 * d9 + d10 * d10);
                  double d12 = 0.4F + d11 / 80.0 - 1.0;
                  if (d12 > 10.0) {
                     d12 = 10.0;
                  }

                  this.targetY = this.target.getEntityBoundingBox().minY + d12;
               } else {
                  this.targetX = this.targetX + this.rand.nextGaussian() * 2.0;
                  this.targetZ = this.targetZ + this.rand.nextGaussian() * 2.0;
               }

               if (this.forceNewTarget || d8 < 100.0 || d8 > 22500.0 || this.isCollidedHorizontally || this.isCollidedVertically) {
                  this.setNewTarget();
               }

               d6 /= MathHelper.sqrt_double(d5 * d5 + d7 * d7);
               float f7 = 0.6F;
               d6 = MathHelper.clamp_double(d6, -f7, f7);
               this.motionY += d6 * 0.1F;
               this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw);
               double d13 = 180.0 - MathHelper.atan2(d5, d7) * 180.0 / Math.PI;
               double d14 = MathHelper.wrapAngleTo180_double(d13 - this.rotationYaw);
               if (d14 > 50.0) {
                  d14 = 50.0;
               }

               if (d14 < -50.0) {
                  d14 = -50.0;
               }

               Vec3 vec3 = new Vec3(this.targetX - this.posX, this.targetY - this.posY, this.targetZ - this.posZ).normalize();
               double d15 = -MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F);
               Vec3 vec32 = new Vec3(MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F), this.motionY, d15).normalize();
               float f8 = ((float)vec32.dotProduct(vec3) + 0.5F) / 1.5F;
               if (f8 < 0.0F) {
                  f8 = 0.0F;
               }

               this.randomYawVelocity *= 0.8F;
               float f9 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0F + 1.0F;
               double d16 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0 + 1.0;
               if (d16 > 40.0) {
                  d16 = 40.0;
               }

               this.randomYawVelocity = (float)(this.randomYawVelocity + d14 * (0.7F / d16 / f9));
               this.rotationYaw = this.rotationYaw + this.randomYawVelocity * 0.1F;
               float f10 = (float)(2.0 / (d16 + 1.0));
               float f11 = 0.06F;
               this.moveFlying(0.0F, -1.0F, f11 * (f8 * f10 + (1.0F - f10)));
               if (this.slowed) {
                  this.moveEntity(this.motionX * 0.8F, this.motionY * 0.8F, this.motionZ * 0.8F);
               } else {
                  this.moveEntity(this.motionX, this.motionY, this.motionZ);
               }

               float f12 = ((float)new Vec3(this.motionX, this.motionY, this.motionZ).normalize().dotProduct(vec32) + 1.0F) / 2.0F;
               f12 = 0.8F + 0.15F * f12;
               this.motionX *= f12;
               this.motionZ *= f12;
               this.motionY *= 0.91F;
            }

            this.renderYawOffset = this.rotationYaw;
            this.dragonPartHead.width = this.dragonPartHead.height = 3.0F;
            this.dragonPartTail1.width = this.dragonPartTail1.height = 2.0F;
            this.dragonPartTail2.width = this.dragonPartTail2.height = 2.0F;
            this.dragonPartTail3.width = this.dragonPartTail3.height = 2.0F;
            this.dragonPartBody.height = 3.0F;
            this.dragonPartBody.width = 5.0F;
            this.dragonPartWing1.height = 2.0F;
            this.dragonPartWing1.width = 4.0F;
            this.dragonPartWing2.height = 3.0F;
            this.dragonPartWing2.width = 4.0F;
            float f13 = (float)(this.getMovementOffsets(5, 1.0F)[1] - this.getMovementOffsets(10, 1.0F)[1]) * 10.0F / 180.0F * (float) Math.PI;
            float f14 = MathHelper.cos(f13);
            float f15 = -MathHelper.sin(f13);
            float f16 = this.rotationYaw * (float) Math.PI / 180.0F;
            float f17 = MathHelper.sin(f16);
            float f18 = MathHelper.cos(f16);
            this.dragonPartBody.onUpdate();
            this.dragonPartBody.setLocationAndAngles(this.posX + f17 * 0.5F, this.posY, this.posZ - f18 * 0.5F, 0.0F, 0.0F);
            this.dragonPartWing1.onUpdate();
            this.dragonPartWing1.setLocationAndAngles(this.posX + f18 * 4.5F, this.posY + 2.0, this.posZ + f17 * 4.5F, 0.0F, 0.0F);
            this.dragonPartWing2.onUpdate();
            this.dragonPartWing2.setLocationAndAngles(this.posX - f18 * 4.5F, this.posY + 2.0, this.posZ - f17 * 4.5F, 0.0F, 0.0F);
            if (!this.worldObj.isRemote && this.hurtTime == 0) {
               this.collideWithEntities(
                  this.worldObj
                     .getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing1.getEntityBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0))
               );
               this.collideWithEntities(
                  this.worldObj
                     .getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing2.getEntityBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0))
               );
               this.attackEntitiesInList(
                  this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartHead.getEntityBoundingBox().expand(1.0, 1.0, 1.0))
               );
            }

            double[] movementOffsets = this.getMovementOffsets(5, 1.0F);
            double[] movementOffsets2 = this.getMovementOffsets(0, 1.0F);
            float f19 = MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F - this.randomYawVelocity * 0.01F);
            float f20 = MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F - this.randomYawVelocity * 0.01F);
            this.dragonPartHead.onUpdate();
            this.dragonPartHead
               .setLocationAndAngles(
                  this.posX + f19 * 5.5F * f14, this.posY + (movementOffsets2[1] - movementOffsets[1]) * 1.0 + f15 * 5.5F, this.posZ - f20 * 5.5F * f14, 0.0F, 0.0F
               );

            for (int i2 = 0; i2 < 3; i2++) {
               EntityDragonPart entityDragonPart = null;
               if (i2 == 0) {
                  entityDragonPart = this.dragonPartTail1;
               }

               if (i2 == 1) {
                  entityDragonPart = this.dragonPartTail2;
               }

               if (i2 == 2) {
                  entityDragonPart = this.dragonPartTail3;
               }

               double[] movementOffsets3 = this.getMovementOffsets(12 + i2 * 2, 1.0F);
               float f21 = this.rotationYaw * (float) Math.PI / 180.0F + this.simplifyAngle(movementOffsets3[0] - movementOffsets[0]) * (float) Math.PI / 180.0F * 1.0F;
               float f22 = MathHelper.sin(f21);
               float f23 = MathHelper.cos(f21);
               float f24 = 1.5F;
               float f25 = (i2 + 1) * 2.0F;
               entityDragonPart.onUpdate();
               entityDragonPart.setLocationAndAngles(
                  this.posX - (f17 * f24 + f22 * f25) * f14,
                  this.posY + (movementOffsets3[1] - movementOffsets[1]) * 1.0 - (f25 + f24) * f15 + 1.5,
                  this.posZ + (f18 * f24 + f23 * f25) * f14,
                  0.0F,
                  0.0F
               );
            }

            if (!this.worldObj.isRemote) {
               this.slowed = this.destroyBlocksInAABB(this.dragonPartHead.getEntityBoundingBox())
                  | this.destroyBlocksInAABB(this.dragonPartBody.getEntityBoundingBox());
            }
         }
      }
   }

   @Override
   public World getWorld() {
      return this.worldObj;
   }

   private void collideWithEntities(List<Entity> list) {
      double d = (this.dragonPartBody.getEntityBoundingBox().minX + this.dragonPartBody.getEntityBoundingBox().maxX) / 2.0;
      double xCoordinate = (this.dragonPartBody.getEntityBoundingBox().minZ + this.dragonPartBody.getEntityBoundingBox().maxZ) / 2.0;

      for (Entity entity2 : list) {
         if (entity2 instanceof EntityLivingBase) {
            double d2 = entity2.posX - d;
            double d3 = entity2.posZ - xCoordinate;
            double d4 = d2 * d2 + d3 * d3;
            entity2.addVelocity(d2 / d4 * 4.0, 0.2F, d3 / d4 * 4.0);
         }
      }
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (source instanceof EntityDamageSource && ((EntityDamageSource)source).getIsThornsDamage()) {
         this.attackDragonFrom(source, amount);
      }

      return false;
   }

   @Override
   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(200.0);
   }

   @Override
   protected void despawnEntity() {
   }

   @Override
   protected String getHurtSound() {
      return "mob.enderdragon.hit";
   }

   @Override
   protected float getSoundVolume() {
      return 5.0F;
   }

   private float simplifyAngle(double d) {
      return (float)MathHelper.wrapAngleTo180_double(d);
   }

   @Override
   protected String getLivingSound() {
      return "mob.enderdragon.growl";
   }

   public double[] getMovementOffsets(int i, float f) {
      if (this.getHealth() <= 0.0F) {
         f = 0.0F;
      }

      f = 1.0F - f;
      int i2 = this.ringBufferIndex - i * 1 & 63;
      int i3 = this.ringBufferIndex - i * 1 - 1 & 63;
      double[] doubleArray = new double[3];
      double d = this.ringBuffer[i2][0];
      double d2 = MathHelper.wrapAngleTo180_double(this.ringBuffer[i3][0] - d);
      doubleArray[0] = d + d2 * f;
      d = this.ringBuffer[i2][1];
      d2 = this.ringBuffer[i3][1] - d;
      doubleArray[1] = d + d2 * f;
      doubleArray[2] = this.ringBuffer[i2][2] + (this.ringBuffer[i3][2] - this.ringBuffer[i2][2]) * f;
      return doubleArray;
   }

   protected boolean attackDragonFrom(DamageSource source, float amount) {
      return super.attackEntityFrom(source, amount);
   }

   @Override
   public Entity[] getParts() {
      return this.dragonPartArray;
   }

   private void generatePortal(BlockPos pos) {
      for (int i = -1; i <= 32; i++) {
         for (int i2 = -4; i2 <= 4; i2++) {
            for (int i3 = -4; i3 <= 4; i3++) {
               double d = i2 * i2 + i3 * i3;
               if (d <= 12.25) {
                  BlockPos blockPos = pos.add(i2, i, i3);
                  if (i < 0) {
                     if (d <= 6.25) {
                        this.worldObj.setBlockState(blockPos, Blocks.bedrock.getDefaultState());
                     }
                  } else if (i > 0) {
                     this.worldObj.setBlockState(blockPos, Blocks.air.getDefaultState());
                  } else if (d > 6.25) {
                     this.worldObj.setBlockState(blockPos, Blocks.bedrock.getDefaultState());
                  } else {
                     this.worldObj.setBlockState(blockPos, Blocks.end_portal.getDefaultState());
                  }
               }
            }
         }
      }

      this.worldObj.setBlockState(pos, Blocks.bedrock.getDefaultState());
      this.worldObj.setBlockState(pos.up(), Blocks.bedrock.getDefaultState());
      BlockPos blockPos2 = pos.up(2);
      this.worldObj.setBlockState(blockPos2, Blocks.bedrock.getDefaultState());
      this.worldObj.setBlockState(blockPos2.west(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.EAST));
      this.worldObj.setBlockState(blockPos2.east(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.WEST));
      this.worldObj.setBlockState(blockPos2.north(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.SOUTH));
      this.worldObj.setBlockState(blockPos2.south(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.NORTH));
      this.worldObj.setBlockState(pos.up(3), Blocks.bedrock.getDefaultState());
      this.worldObj.setBlockState(pos.up(4), Blocks.dragon_egg.getDefaultState());
   }

   public EntityDragon(World worldIn) {
      super(worldIn);
      this.dragonPartArray = new EntityDragonPart[]{
         this.dragonPartHead = new EntityDragonPart(this, "head", 6.0F, 6.0F),
         this.dragonPartBody = new EntityDragonPart(this, "body", 8.0F, 8.0F),
         this.dragonPartTail1 = new EntityDragonPart(this, "tail", 4.0F, 4.0F),
         this.dragonPartTail2 = new EntityDragonPart(this, "tail", 4.0F, 4.0F),
         this.dragonPartTail3 = new EntityDragonPart(this, "tail", 4.0F, 4.0F),
         this.dragonPartWing1 = new EntityDragonPart(this, "wing", 4.0F, 4.0F),
         this.dragonPartWing2 = new EntityDragonPart(this, "wing", 4.0F, 4.0F)
      };
      this.setHealth(this.getMaxHealth());
      this.setSize(16.0F, 8.0F);
      this.noClip = true;
      this.isImmuneToFire = true;
      this.targetY = 100.0;
      this.ignoreFrustumCheck = true;
   }

   private void updateDragonEnderCrystal() {
      if (this.healingEnderCrystal != null) {
         if (this.healingEnderCrystal.isDead) {
            if (!this.worldObj.isRemote) {
               this.attackEntityFromPart(this.dragonPartHead, DamageSource.setExplosionSource((Explosion)null), 10.0F);
            }

            this.healingEnderCrystal = null;
         } else if (this.ticksExisted % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
            this.setHealth(this.getHealth() + 1.0F);
         }
      }

      if (this.rand.nextInt(10) == 0) {
         float f = 32.0F;
         List entitiesWithinAABB = this.worldObj.getEntitiesWithinAABB(EntityEnderCrystal.class, this.getEntityBoundingBox().expand(f, f, f));
         EntityEnderCrystal entityEnderCrystal = null;
         double d = Double.MAX_VALUE;

         for (EntityEnderCrystal entityEnderCrystal2 : (Iterable<EntityEnderCrystal>) entitiesWithinAABB) {
            double distanceSqToEntity = entityEnderCrystal2.getDistanceSqToEntity(this);
            if (distanceSqToEntity < d) {
               d = distanceSqToEntity;
               entityEnderCrystal = entityEnderCrystal2;
            }
         }

         this.healingEnderCrystal = entityEnderCrystal;
      }
   }

   @Override
   public void onKillCommand() {
      this.setDead();
   }

   @Override
   public boolean attackEntityFromPart(EntityDragonPart dragonPart, DamageSource source, float f) {
      if (dragonPart != this.dragonPartHead) {
         f = f / 4.0F + 1.0F;
      }

      float f2 = this.rotationYaw * (float) Math.PI / 180.0F;
      float f3 = MathHelper.sin(f2);
      float f4 = MathHelper.cos(f2);
      this.targetX = this.posX + f3 * 5.0F + (this.rand.nextFloat() - 0.5F) * 2.0F;
      this.targetY = this.posY + this.rand.nextFloat() * 3.0F + 1.0;
      this.targetZ = this.posZ - f4 * 5.0F + (this.rand.nextFloat() - 0.5F) * 2.0F;
      this.target = null;
      if (source.getEntity() instanceof EntityPlayer || source.isExplosion()) {
         this.attackDragonFrom(source, f);
      }

      return true;
   }

   private boolean destroyBlocksInAABB(AxisAlignedBB axisAlignedBB) {
      int i = MathHelper.floor_double(axisAlignedBB.minX);
      int i2 = MathHelper.floor_double(axisAlignedBB.minY);
      int i3 = MathHelper.floor_double(axisAlignedBB.minZ);
      int i4 = MathHelper.floor_double(axisAlignedBB.maxX);
      int i5 = MathHelper.floor_double(axisAlignedBB.maxY);
      int i6 = MathHelper.floor_double(axisAlignedBB.maxZ);
      boolean conditionMet = false;
      boolean conditionMet2 = false;

      for (int nestedIndex = i; nestedIndex <= i4; nestedIndex++) {
         for (int outerIndex = i2; outerIndex <= i5; outerIndex++) {
            for (int index = i3; index <= i6; index++) {
               BlockPos blockPos = new BlockPos(nestedIndex, outerIndex, index);
               Block block = this.worldObj.getBlockState(blockPos).getBlock();
               if (block.getMaterial() != Material.air) {
                  if (block != Blocks.barrier
                     && block != Blocks.obsidian
                     && block != Blocks.end_stone
                     && block != Blocks.bedrock
                     && block != Blocks.command_block
                     && this.worldObj.getGameRules().getBoolean("mobGriefing")) {
                     conditionMet2 = this.worldObj.setBlockToAir(blockPos) || conditionMet2;
                  } else {
                     conditionMet = true;
                  }
               }
            }
         }
      }

      if (conditionMet2) {
         double d = axisAlignedBB.minX + (axisAlignedBB.maxX - axisAlignedBB.minX) * this.rand.nextFloat();
         double d2 = axisAlignedBB.minY + (axisAlignedBB.maxY - axisAlignedBB.minY) * this.rand.nextFloat();
         double d3 = axisAlignedBB.minZ + (axisAlignedBB.maxZ - axisAlignedBB.minZ) * this.rand.nextFloat();
         this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, d, d2, d3, 0.0, 0.0, 0.0);
      }

      return conditionMet;
   }

   @Override
   public boolean canBeCollidedWith() {
      return false;
   }

   private void attackEntitiesInList(List<Entity> list) {
      for (int index = 0; index < list.size(); index++) {
         Entity entity = list.get(index);
         if (entity instanceof EntityLivingBase) {
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 10.0F);
            this.applyEnchantments(this, entity);
         }
      }
   }
}
