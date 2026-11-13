package net.minecraft.entity.projectile;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomFishable;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityFishHook extends Entity {
   private static final List<WeightedRandomFishable> JUNK = Arrays.asList(
      new WeightedRandomFishable(new ItemStack(Items.leather_boots), 10).setMaxDamagePercent(0.9F),
      new WeightedRandomFishable(new ItemStack(Items.leather), 10),
      new WeightedRandomFishable(new ItemStack(Items.bone), 10),
      new WeightedRandomFishable(new ItemStack(Items.potionitem), 10),
      new WeightedRandomFishable(new ItemStack(Items.string), 5),
      new WeightedRandomFishable(new ItemStack(Items.fishing_rod), 2).setMaxDamagePercent(0.9F),
      new WeightedRandomFishable(new ItemStack(Items.bowl), 10),
      new WeightedRandomFishable(new ItemStack(Items.stick), 5),
      new WeightedRandomFishable(new ItemStack(Items.dye, 10, EnumDyeColor.BLACK.getDyeDamage()), 1),
      new WeightedRandomFishable(new ItemStack(Blocks.tripwire_hook), 10),
      new WeightedRandomFishable(new ItemStack(Items.rotten_flesh), 10)
   );
   private int ticksCatchableDelay;
   private int ticksInGround;
   private static final List<WeightedRandomFishable> TREASURE = Arrays.asList(
      new WeightedRandomFishable(new ItemStack(Blocks.waterlily), 1),
      new WeightedRandomFishable(new ItemStack(Items.name_tag), 1),
      new WeightedRandomFishable(new ItemStack(Items.saddle), 1),
      new WeightedRandomFishable(new ItemStack(Items.bow), 1).setMaxDamagePercent(0.25F).setEnchantable(),
      new WeightedRandomFishable(new ItemStack(Items.fishing_rod), 1).setMaxDamagePercent(0.25F).setEnchantable(),
      new WeightedRandomFishable(new ItemStack(Items.book), 1).setEnchantable()
   );
   private int ticksCaughtDelay;
   private int ticksInAir;
   private int xTile = -1;
   private int yTile = -1;
   private int zTile = -1;
   private int ticksCatchable;
   private static final List<WeightedRandomFishable> FISH = Arrays.asList(
      new WeightedRandomFishable(new ItemStack(Items.fish, 1, net.minecraft.item.ItemFishFood.FishType.COD.getMetadata()), 60),
      new WeightedRandomFishable(new ItemStack(Items.fish, 1, net.minecraft.item.ItemFishFood.FishType.SALMON.getMetadata()), 25),
      new WeightedRandomFishable(new ItemStack(Items.fish, 1, net.minecraft.item.ItemFishFood.FishType.CLOWNFISH.getMetadata()), 2),
      new WeightedRandomFishable(new ItemStack(Items.fish, 1, net.minecraft.item.ItemFishFood.FishType.PUFFERFISH.getMetadata()), 13)
   );
   public int shake;
   private double clientMotionX;
   private double fishZ;
   private double fishX;
   private float fishApproachAngle;
   private double clientMotionZ;
   private Block inTile;
   private double fishPitch;
   private double clientMotionY;
   private int fishPosRotationIncrements;
   public Entity caughtEntity;
   public EntityPlayer angler;
   private double fishYaw;
   private boolean inGround;
   private double fishY;

   @Override
   public void setDead() {
      super.setDead();
      if (this.angler != null) {
         this.angler.fishEntity = null;
      }
   }

   public EntityFishHook(World worldIn) {
      super(worldIn);
      this.setSize(0.25F, 0.25F);
      this.ignoreFrustumCheck = true;
   }

   public EntityFishHook(World worldIn, double x, double y, double z, EntityPlayer anglerIn) {
      this(worldIn);
      this.setPosition(x, y, z);
      this.ignoreFrustumCheck = true;
      this.angler = anglerIn;
      anglerIn.fishEntity = this;
   }

   @Override
   public void setVelocity(double x, double y, double z) {
      this.clientMotionX = this.motionX = x;
      this.clientMotionY = this.motionY = y;
      this.clientMotionZ = this.motionZ = z;
   }

   public static List<WeightedRandomFishable> getFishLoot() {
      return FISH;
   }

   public int handleHookRetraction() {
      if (this.worldObj.isRemote) {
         return 0;
      }

      int i = 0;
      if (this.caughtEntity != null) {
         double xCoordinate = this.angler.posX - this.posX;
         double yCoordinate = this.angler.posY - this.posY;
         double zCoordinate = this.angler.posZ - this.posZ;
         double d = MathHelper.sqrt_double(xCoordinate * xCoordinate + yCoordinate * yCoordinate + zCoordinate * zCoordinate);
         double d2 = 0.1;
         this.caughtEntity.motionX += xCoordinate * d2;
         this.caughtEntity.motionY = this.caughtEntity.motionY + (yCoordinate * d2 + MathHelper.sqrt_double(d) * 0.08);
         this.caughtEntity.motionZ += zCoordinate * d2;
         i = 3;
      } else if (this.ticksCatchable > 0) {
         EntityItem entityItem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, this.getFishingResult());
         double xCoordinate2 = this.angler.posX - this.posX;
         double yCoordinate2 = this.angler.posY - this.posY;
         double zCoordinate2 = this.angler.posZ - this.posZ;
         double d3 = MathHelper.sqrt_double(xCoordinate2 * xCoordinate2 + yCoordinate2 * yCoordinate2 + zCoordinate2 * zCoordinate2);
         double d4 = 0.1;
         entityItem.motionX = xCoordinate2 * d4;
         entityItem.motionY = yCoordinate2 * d4 + MathHelper.sqrt_double(d3) * 0.08;
         entityItem.motionZ = zCoordinate2 * d4;
         this.worldObj.spawnEntityInWorld(entityItem);
         this.angler
            .worldObj
            .spawnEntityInWorld(
               new EntityXPOrb(this.angler.worldObj, this.angler.posX, this.angler.posY + 0.5, this.angler.posZ + 0.5, this.rand.nextInt(6) + 1)
            );
         i = 1;
      }

      if (this.inGround) {
         i = 2;
      }

      this.setDead();
      this.angler.fishEntity = null;
      return i;
   }

   public void handleHookCasting(double d, double d2, double d3, float f, float f2) {
      float f3 = MathHelper.sqrt_double(d * d + d2 * d2 + d3 * d3);
      d /= f3;
      d2 /= f3;
      d3 /= f3;
      d += this.rand.nextGaussian() * 0.0075F * f2;
      d2 += this.rand.nextGaussian() * 0.0075F * f2;
      d3 += this.rand.nextGaussian() * 0.0075F * f2;
      d *= f;
      d2 *= f;
      d3 *= f;
      this.motionX = d;
      this.motionY = d2;
      this.motionZ = d3;
      float f4 = MathHelper.sqrt_double(d * d + d3 * d3);
      this.prevRotationYaw = this.rotationYaw = (float)(MathHelper.atan2(d, d3) * 180.0 / Math.PI);
      this.prevRotationPitch = this.rotationPitch = (float)(MathHelper.atan2(d2, f4) * 180.0 / Math.PI);
      this.ticksInGround = 0;
   }

   public EntityFishHook(World worldIn, EntityPlayer fishingPlayer) {
      super(worldIn);
      this.ignoreFrustumCheck = true;
      this.angler = fishingPlayer;
      this.angler.fishEntity = this;
      this.setSize(0.25F, 0.25F);
      this.setLocationAndAngles(
         fishingPlayer.posX, fishingPlayer.posY + fishingPlayer.getEyeHeight(), fishingPlayer.posZ, fishingPlayer.rotationYaw, fishingPlayer.rotationPitch
      );
      this.posX = this.posX - MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
      this.posY -= 0.1F;
      this.posZ = this.posZ - MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
      this.setPosition(this.posX, this.posY, this.posZ);
      float f = 0.4F;
      this.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f;
      this.motionZ = MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f;
      this.motionY = -MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI) * f;
      this.handleHookCasting(this.motionX, this.motionY, this.motionZ, 1.5F, 1.0F);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      this.xTile = tagCompund.getShort("xTile");
      this.yTile = tagCompund.getShort("yTile");
      this.zTile = tagCompund.getShort("zTile");
      if (tagCompund.hasKey("inTile", 8)) {
         this.inTile = Block.getBlockFromName(tagCompund.getString("inTile"));
      } else {
         this.inTile = Block.getBlockById(tagCompund.getByte("inTile") & 255);
      }

      this.shake = tagCompund.getByte("shake") & 255;
      this.inGround = tagCompund.getByte("inGround") == 1;
   }

   @Override
   public boolean isInRangeToRenderDist(double distance) {
      double d = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0;
      if (Double.isNaN(d)) {
         d = 4.0;
      }

      d *= 64.0;
      return distance < d * d;
   }

   @Override
   public void handleDouble(double d, double d2, double d3, float f, float f2, boolean flag) {
      this.fishX = d;
      this.fishY = d2;
      this.fishZ = d3;
      this.fishYaw = f;
      this.fishPitch = f2;
      this.fishPosRotationIncrements = 3;
      this.motionX = this.clientMotionX;
      this.motionY = this.clientMotionY;
      this.motionZ = this.clientMotionZ;
   }

   private ItemStack getFishingResult() {
      float f = this.worldObj.rand.nextFloat();
      int luckOfSeaModifier = EnchantmentHelper.getLuckOfSeaModifier(this.angler);
      int lureModifier = EnchantmentHelper.getLureModifier(this.angler);
      float f2 = 0.1F - luckOfSeaModifier * 0.025F - lureModifier * 0.01F;
      float f3 = 0.05F + luckOfSeaModifier * 0.01F - lureModifier * 0.01F;
      f2 = MathHelper.clamp_float(f2, 0.0F, 1.0F);
      f3 = MathHelper.clamp_float(f3, 0.0F, 1.0F);
      if (f < f2) {
         this.angler.triggerAchievement(StatList.junkFishedStat);
         return WeightedRandom.getRandomItem(this.rand, JUNK).getItemStack(this.rand);
      } else {
         f -= f2;
         if (f < f3) {
            this.angler.triggerAchievement(StatList.treasureFishedStat);
            return WeightedRandom.getRandomItem(this.rand, TREASURE).getItemStack(this.rand);
         } else {
            this.angler.triggerAchievement(StatList.fishCaughtStat);
            return WeightedRandom.getRandomItem(this.rand, FISH).getItemStack(this.rand);
         }
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      tagCompound.setShort("xTile", (short)this.xTile);
      tagCompound.setShort("yTile", (short)this.yTile);
      tagCompound.setShort("zTile", (short)this.zTile);
      ResourceLocation resourceLocation = Block.blockRegistry.getNameForObject(this.inTile);
      tagCompound.setString("inTile", resourceLocation == null ? "" : resourceLocation.toString());
      tagCompound.setByte("shake", (byte)this.shake);
      tagCompound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (this.fishPosRotationIncrements > 0) {
         double d = this.posX + (this.fishX - this.posX) / this.fishPosRotationIncrements;
         double d2 = this.posY + (this.fishY - this.posY) / this.fishPosRotationIncrements;
         double d3 = this.posZ + (this.fishZ - this.posZ) / this.fishPosRotationIncrements;
         double d4 = MathHelper.wrapAngleTo180_double(this.fishYaw - this.rotationYaw);
         this.rotationYaw = (float)(this.rotationYaw + d4 / this.fishPosRotationIncrements);
         this.rotationPitch = (float)(this.rotationPitch + (this.fishPitch - this.rotationPitch) / this.fishPosRotationIncrements);
         this.fishPosRotationIncrements--;
         this.setPosition(d, d2, d3);
         this.setRotation(this.rotationYaw, this.rotationPitch);
      } else {
         if (!this.worldObj.isRemote) {
            ItemStack currentEquippedItem = this.angler.getCurrentEquippedItem();
            if (this.angler.isDead
               || !this.angler.isEntityAlive()
               || currentEquippedItem == null
               || currentEquippedItem.getItem() != Items.fishing_rod
               || this.getDistanceSqToEntity(this.angler) > 1024.0) {
               this.setDead();
               this.angler.fishEntity = null;
               return;
            }

            if (this.caughtEntity != null) {
               if (!this.caughtEntity.isDead) {
                  this.posX = this.caughtEntity.posX;
                  double height2 = this.caughtEntity.height;
                  this.posY = this.caughtEntity.getEntityBoundingBox().minY + height2 * 0.8;
                  this.posZ = this.caughtEntity.posZ;
                  return;
               }

               this.caughtEntity = null;
            }
         }

         if (this.shake > 0) {
            this.shake--;
         }

         if (this.inGround) {
            if (this.worldObj.getBlockState(new BlockPos(this.xTile, this.yTile, this.zTile)).getBlock() == this.inTile) {
               this.ticksInGround++;
               if (this.ticksInGround == 1200) {
                  this.setDead();
               }

               return;
            }

            this.inGround = false;
            this.motionX = this.motionX * (this.rand.nextFloat() * 0.2F);
            this.motionY = this.motionY * (this.rand.nextFloat() * 0.2F);
            this.motionZ = this.motionZ * (this.rand.nextFloat() * 0.2F);
            this.ticksInGround = 0;
            this.ticksInAir = 0;
         } else {
            this.ticksInAir++;
         }

         Vec3 vec3 = new Vec3(this.posX, this.posY, this.posZ);
         Vec3 vec32 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         MovingObjectPosition movingObjectPosition = this.worldObj.rayTraceBlocks(vec3, vec32);
         vec3 = new Vec3(this.posX, this.posY, this.posZ);
         vec32 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         if (movingObjectPosition != null) {
            vec32 = new Vec3(movingObjectPosition.hitVec.xCoord, movingObjectPosition.hitVec.yCoord, movingObjectPosition.hitVec.zCoord);
         }

         Entity entity2 = null;
         List entitiesWithinAABBExcludingEntity = this.worldObj
            .getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
         double d5 = 0.0;

         for (int i = 0; i < entitiesWithinAABBExcludingEntity.size(); i++) {
            Entity entity3 = (Entity)entitiesWithinAABBExcludingEntity.get(i);
            if (entity3.canBeCollidedWith() && (entity3 != this.angler || this.ticksInAir >= 5)) {
               float f = 0.3F;
               AxisAlignedBB axisAlignedBB = entity3.getEntityBoundingBox().expand(f, f, f);
               MovingObjectPosition movingObjectPosition2 = axisAlignedBB.calculateIntercept(vec3, vec32);
               if (movingObjectPosition2 != null) {
                  double d6 = vec3.squareDistanceTo(movingObjectPosition2.hitVec);
                  if (d6 < d5 || d5 == 0.0) {
                     entity2 = entity3;
                     d5 = d6;
                  }
               }
            }
         }

         if (entity2 != null) {
            movingObjectPosition = new MovingObjectPosition(entity2);
         }

         if (movingObjectPosition != null) {
            if (movingObjectPosition.entityHit != null) {
               if (movingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.angler), 0.0F)) {
                  this.caughtEntity = movingObjectPosition.entityHit;
               }
            } else {
               this.inGround = true;
            }
         }

         if (!this.inGround) {
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
            this.rotationPitch = (float)(MathHelper.atan2(this.motionY, f2) * 180.0 / Math.PI);

            while (this.rotationPitch - this.prevRotationPitch < -180.0F) {
               this.prevRotationPitch -= 360.0F;
            }

            while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
               this.prevRotationPitch += 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
               this.prevRotationYaw -= 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
               this.prevRotationYaw += 360.0F;
            }

            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
            float f3 = 0.92F;
            if (this.onGround || this.isCollidedHorizontally) {
               f3 = 0.5F;
            }

            double d7 = 0.0;

            for (int i2 = 0; i2 < 5; i2++) {
               AxisAlignedBB entityBoundingBox = this.getEntityBoundingBox();
               double d8 = entityBoundingBox.maxY - entityBoundingBox.minY;
               double d9 = entityBoundingBox.minY + d8 * i2 / 5;
               double d10 = entityBoundingBox.minY + d8 * (i2 + 1) / 5;
               AxisAlignedBB axisAlignedBB2 = new AxisAlignedBB(entityBoundingBox.minX, d9, entityBoundingBox.minZ, entityBoundingBox.maxX, d10, entityBoundingBox.maxZ);
               if (this.worldObj.isAABBInMaterial(axisAlignedBB2, Material.water)) {
                  d7 += 1.0 / 5;
               }
            }

            if (!this.worldObj.isRemote && d7 > 0.0) {
               WorldServer worldServer = (WorldServer)this.worldObj;
               int i3 = 1;
               BlockPos blockPos = new BlockPos(this).up();
               if (this.rand.nextFloat() < 0.25F && this.worldObj.isRainingAt(blockPos)) {
                  i3 = 2;
               }

               if (this.rand.nextFloat() < 0.5F && !this.worldObj.canSeeSky(blockPos)) {
                  i3--;
               }

               if (this.ticksCatchable > 0) {
                  this.ticksCatchable--;
                  if (this.ticksCatchable <= 0) {
                     this.ticksCaughtDelay = 0;
                     this.ticksCatchableDelay = 0;
                  }
               } else if (this.ticksCatchableDelay > 0) {
                  this.ticksCatchableDelay -= i3;
                  if (this.ticksCatchableDelay <= 0) {
                     this.motionY -= 0.2F;
                     this.playSound("random.splash", 0.25F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                     float f4 = MathHelper.floor_double(this.getEntityBoundingBox().minY);
                     worldServer.spawnParticle(
                        EnumParticleTypes.WATER_BUBBLE, this.posX, f4 + 1.0F, this.posZ, (int)(1.0F + this.width * 20.0F), this.width, 0.0, this.width, 0.2F
                     );
                     worldServer.spawnParticle(
                        EnumParticleTypes.WATER_WAKE, this.posX, f4 + 1.0F, this.posZ, (int)(1.0F + this.width * 20.0F), this.width, 0.0, this.width, 0.2F
                     );
                     this.ticksCatchable = MathHelper.getRandomIntegerInRange(this.rand, 10, 30);
                  } else {
                     this.fishApproachAngle = (float)(this.fishApproachAngle + this.rand.nextGaussian() * 4.0);
                     float f5 = this.fishApproachAngle * (float) (Math.PI / 180.0);
                     float f6 = MathHelper.sin(f5);
                     float f7 = MathHelper.cos(f5);
                     double d11 = this.posX + f6 * this.ticksCatchableDelay * 0.1F;
                     double d12 = MathHelper.floor_double(this.getEntityBoundingBox().minY) + 1.0F;
                     double d13 = this.posZ + f7 * this.ticksCatchableDelay * 0.1F;
                     Block block2 = worldServer.getBlockState(new BlockPos((int)d11, (int)d12 - 1, (int)d13)).getBlock();
                     if (block2 == Blocks.water || block2 == Blocks.flowing_water) {
                        if (this.rand.nextFloat() < 0.15F) {
                           worldServer.spawnParticle(EnumParticleTypes.WATER_BUBBLE, d11, d12 - 0.1F, d13, 1, f6, 0.1, f7, 0.0);
                        }

                        float f8 = f6 * 0.04F;
                        float f9 = f7 * 0.04F;
                        worldServer.spawnParticle(EnumParticleTypes.WATER_WAKE, d11, d12, d13, 0, f9, 0.01, -f8, 1.0);
                        worldServer.spawnParticle(EnumParticleTypes.WATER_WAKE, d11, d12, d13, 0, -f9, 0.01, f8, 1.0);
                     }
                  }
               } else if (this.ticksCaughtDelay > 0) {
                  this.ticksCaughtDelay -= i3;
                  float f10 = 0.15F;
                  if (this.ticksCaughtDelay < 20) {
                     f10 = (float)(f10 + (20 - this.ticksCaughtDelay) * 0.05);
                  } else if (this.ticksCaughtDelay < 40) {
                     f10 = (float)(f10 + (40 - this.ticksCaughtDelay) * 0.02);
                  } else if (this.ticksCaughtDelay < 60) {
                     f10 = (float)(f10 + (60 - this.ticksCaughtDelay) * 0.01);
                  }

                  if (this.rand.nextFloat() < f10) {
                     float f11 = MathHelper.randomFloatClamp(this.rand, 0.0F, 360.0F) * (float) (Math.PI / 180.0);
                     float f12 = MathHelper.randomFloatClamp(this.rand, 25.0F, 60.0F);
                     double d14 = this.posX + MathHelper.sin(f11) * f12 * 0.1F;
                     double d15 = MathHelper.floor_double(this.getEntityBoundingBox().minY) + 1.0F;
                     double d16 = this.posZ + MathHelper.cos(f11) * f12 * 0.1F;
                     Block block3 = worldServer.getBlockState(new BlockPos((int)d14, (int)d15 - 1, (int)d16)).getBlock();
                     if (block3 == Blocks.water || block3 == Blocks.flowing_water) {
                        worldServer.spawnParticle(EnumParticleTypes.WATER_SPLASH, d14, d15, d16, 2 + this.rand.nextInt(2), 0.1F, 0.0, 0.1F, 0.0);
                     }
                  }

                  if (this.ticksCaughtDelay <= 0) {
                     this.fishApproachAngle = MathHelper.randomFloatClamp(this.rand, 0.0F, 360.0F);
                     this.ticksCatchableDelay = MathHelper.getRandomIntegerInRange(this.rand, 20, 80);
                  }
               } else {
                  this.ticksCaughtDelay = MathHelper.getRandomIntegerInRange(this.rand, 100, 900);
                  this.ticksCaughtDelay = this.ticksCaughtDelay - EnchantmentHelper.getLureModifier(this.angler) * 20 * 5;
               }

               if (this.ticksCatchable > 0) {
                  this.motionY = this.motionY - this.rand.nextFloat() * this.rand.nextFloat() * this.rand.nextFloat() * 0.2;
               }
            }

            double d17 = d7 * 2.0 - 1.0;
            this.motionY += 0.04F * d17;
            if (d7 > 0.0) {
               f3 = (float)(f3 * 0.9);
               this.motionY *= 0.8;
            }

            this.motionX *= f3;
            this.motionY *= f3;
            this.motionZ *= f3;
            this.setPosition(this.posX, this.posY, this.posZ);
         }
      }
   }

   @Override
   protected void entityInit() {
   }
}
