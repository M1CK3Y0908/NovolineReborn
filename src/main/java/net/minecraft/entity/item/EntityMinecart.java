package net.minecraft.entity.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityMinecart extends Entity implements IWorldNameable {
   private boolean isInReverse;
   private double minecartYaw;
   private String entityName;
   private double velocityY;
   private double velocityX;
   private static final int[][][] matrix = new int[][][]{
      {{0, 0, -1}, {0, 0, 1}},
      {{-1, 0, 0}, {1, 0, 0}},
      {{-1, -1, 0}, {1, 0, 0}},
      {{-1, 0, 0}, {1, -1, 0}},
      {{0, 0, -1}, {0, -1, 1}},
      {{0, -1, -1}, {0, 0, 1}},
      {{0, 0, 1}, {1, 0, 0}},
      {{0, 0, 1}, {-1, 0, 0}},
      {{0, 0, -1}, {-1, 0, 0}},
      {{0, 0, -1}, {1, 0, 0}}
   };
   private double minecartX;
   private double minecartY;
   private double minecartPitch;
   private double velocityZ;
   private int turnProgress;
   private double minecartZ;

   protected void moveAlongTrack(BlockPos blockPos, IBlockState iBlockState) {
      this.fallDistance = 0.0F;
      Vec3 pos = this.getPos(this.posX, this.posY, this.posZ);
      this.posY = blockPos.getY();
      boolean flag = false;
      boolean flag2 = false;
      BlockRailBase blockRailBase = (BlockRailBase)iBlockState.getBlock();
      if (blockRailBase == Blocks.golden_rail) {
         flag = iBlockState.getValue(BlockRailPowered.POWERED);
         flag2 = !flag;
      }

      net.minecraft.block.BlockRailBase.EnumRailDirection value = iBlockState.getValue(blockRailBase.getShapeProperty());
      switch (value) {
         case ASCENDING_EAST:
            this.motionX -= 0.0078125;
            this.posY++;
            break;
         case ASCENDING_WEST:
            this.motionX += 0.0078125;
            this.posY++;
            break;
         case ASCENDING_NORTH:
            this.motionZ += 0.0078125;
            this.posY++;
            break;
         case ASCENDING_SOUTH:
            this.motionZ -= 0.0078125;
            this.posY++;
      }

      int[][] iArrays = matrix[value.getMetadata()];
      double d = iArrays[1][0] - iArrays[0][0];
      double xCoordinate3 = iArrays[1][2] - iArrays[0][2];
      double d2 = Math.sqrt(d * d + xCoordinate3 * xCoordinate3);
      double d3 = this.motionX * d + this.motionZ * xCoordinate3;
      if (d3 < 0.0) {
         d = -d;
         xCoordinate3 = -xCoordinate3;
      }

      double d4 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
      if (d4 > 2.0) {
         d4 = 2.0;
      }

      this.motionX = d4 * d / d2;
      this.motionZ = d4 * xCoordinate3 / d2;
      if (this.riddenByEntity instanceof EntityLivingBase) {
         double moveForward2 = ((EntityLivingBase)this.riddenByEntity).moveForward;
         if (moveForward2 > 0.0) {
            double d5 = -Math.sin(this.riddenByEntity.rotationYaw * (float) Math.PI / 180.0F);
            double d6 = Math.cos(this.riddenByEntity.rotationYaw * (float) Math.PI / 180.0F);
            double d7 = this.motionX * this.motionX + this.motionZ * this.motionZ;
            if (d7 < 0.01) {
               this.motionX += d5 * 0.1;
               this.motionZ += d6 * 0.1;
               flag2 = false;
            }
         }
      }

      if (flag2) {
         double d8 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (d8 < 0.03) {
            this.motionX *= 0.0;
            this.motionY *= 0.0;
            this.motionZ *= 0.0;
         } else {
            this.motionX *= 0.5;
            this.motionY *= 0.0;
            this.motionZ *= 0.5;
         }
      }

      double d9 = blockPos.getX() + 0.5 + iArrays[0][0] * 0.5;
      double d10 = blockPos.getZ() + 0.5 + iArrays[0][2] * 0.5;
      double d11 = blockPos.getX() + 0.5 + iArrays[1][0] * 0.5;
      double d12 = blockPos.getZ() + 0.5 + iArrays[1][2] * 0.5;
      d = d11 - d9;
      xCoordinate3 = d12 - d10;
      double d13;
      if (d == 0.0) {
         this.posX = blockPos.getX() + 0.5;
         d13 = this.posZ - blockPos.getZ();
      } else if (xCoordinate3 == 0.0) {
         this.posZ = blockPos.getZ() + 0.5;
         d13 = this.posX - blockPos.getX();
      } else {
         double d14 = this.posX - d9;
         double d15 = this.posZ - d10;
         d13 = (d14 * d + d15 * xCoordinate3) * 2.0;
      }

      this.posX = d9 + d * d13;
      this.posZ = d10 + xCoordinate3 * d13;
      this.setPosition(this.posX, this.posY, this.posZ);
      double motionX2 = this.motionX;
      double motionZ2 = this.motionZ;
      if (this.riddenByEntity != null) {
         motionX2 *= 0.75;
         motionZ2 *= 0.75;
      }

      double maximumSpeed = this.getMaximumSpeed();
      motionX2 = MathHelper.clamp_double(motionX2, -maximumSpeed, maximumSpeed);
      motionZ2 = MathHelper.clamp_double(motionZ2, -maximumSpeed, maximumSpeed);
      this.moveEntity(motionX2, 0.0, motionZ2);
      if (iArrays[0][1] != 0
         && MathHelper.floor_double(this.posX) - blockPos.getX() == iArrays[0][0]
         && MathHelper.floor_double(this.posZ) - blockPos.getZ() == iArrays[0][2]) {
         this.setPosition(this.posX, this.posY + iArrays[0][1], this.posZ);
      } else if (iArrays[1][1] != 0
         && MathHelper.floor_double(this.posX) - blockPos.getX() == iArrays[1][0]
         && MathHelper.floor_double(this.posZ) - blockPos.getZ() == iArrays[1][2]) {
         this.setPosition(this.posX, this.posY + iArrays[1][1], this.posZ);
      }

      this.applyDrag();
      Vec3 pos2 = this.getPos(this.posX, this.posY, this.posZ);
      if (pos2 != null && pos != null) {
         double d16 = (pos.yCoord - pos2.yCoord) * 0.05;
         d4 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (d4 > 0.0) {
            this.motionX = this.motionX / d4 * (d4 + d16);
            this.motionZ = this.motionZ / d4 * (d4 + d16);
         }

         this.setPosition(this.posX, pos2.yCoord, this.posZ);
      }

      int i = MathHelper.floor_double(this.posX);
      int i2 = MathHelper.floor_double(this.posZ);
      if (i != blockPos.getX() || i2 != blockPos.getZ()) {
         d4 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.motionX = d4 * (i - blockPos.getX());
         this.motionZ = d4 * (i2 - blockPos.getZ());
      }

      if (flag) {
         double d17 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (d17 > 0.01) {
            double d18 = 0.06;
            this.motionX = this.motionX + this.motionX / d17 * d18;
            this.motionZ = this.motionZ + this.motionZ / d17 * d18;
         } else if (value == net.minecraft.block.BlockRailBase.EnumRailDirection.EAST_WEST) {
            if (this.worldObj.getBlockState(blockPos.west()).getBlock().isNormalCube()) {
               this.motionX = 0.02;
            } else if (this.worldObj.getBlockState(blockPos.east()).getBlock().isNormalCube()) {
               this.motionX = -0.02;
            }
         } else if (value == net.minecraft.block.BlockRailBase.EnumRailDirection.NORTH_SOUTH) {
            if (this.worldObj.getBlockState(blockPos.north()).getBlock().isNormalCube()) {
               this.motionZ = 0.02;
            } else if (this.worldObj.getBlockState(blockPos.south()).getBlock().isNormalCube()) {
               this.motionZ = -0.02;
            }
         }
      }
   }

   protected void applyDrag() {
      if (this.riddenByEntity != null) {
         this.motionX *= 0.997F;
         this.motionY *= 0.0;
         this.motionZ *= 0.997F;
      } else {
         this.motionX *= 0.96F;
         this.motionY *= 0.0;
         this.motionZ *= 0.96F;
      }
   }

   public int getRollingDirection() {
      return this.dataWatcher.getWatchableObjectInt(18);
   }

   @Override
   public void setDead() {
      super.setDead();
   }

   @Override
   public void setVelocity(double x, double y, double z) {
      this.velocityX = this.motionX = x;
      this.velocityY = this.motionY = y;
      this.velocityZ = this.motionZ = z;
   }

   @Override
   public void setPosition(double x, double y, double z) {
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      float f = this.width / 2.0F;
      float height2 = this.height;
      this.setEntityBoundingBox(new AxisAlignedBB(x - f, y, z - f, x + f, y + height2, z + f));
   }

   public Vec3 getPosOffset(double d, double d2, double d3, double d4) {
      int i = MathHelper.floor_double(d);
      int i2 = MathHelper.floor_double(d2);
      int i3 = MathHelper.floor_double(d3);
      if (BlockRailBase.isRailBlock(this.worldObj, new BlockPos(i, i2 - 1, i3))) {
         i2--;
      }

      IBlockState blockState2 = this.worldObj.getBlockState(new BlockPos(i, i2, i3));
      if (BlockRailBase.isRailBlock(blockState2)) {
         net.minecraft.block.BlockRailBase.EnumRailDirection value = blockState2.getValue(((BlockRailBase)blockState2.getBlock()).getShapeProperty());
         d2 = i2;
         if (value.isAscending()) {
            d2 = i2 + 1;
         }

         int[][] iArrays = matrix[value.getMetadata()];
         double d5 = iArrays[1][0] - iArrays[0][0];
         double d6 = iArrays[1][2] - iArrays[0][2];
         double d7 = Math.sqrt(d5 * d5 + d6 * d6);
         d5 /= d7;
         d6 /= d7;
         d += d5 * d4;
         d3 += d6 * d4;
         if (iArrays[0][1] != 0 && MathHelper.floor_double(d) - i == iArrays[0][0] && MathHelper.floor_double(d3) - i3 == iArrays[0][2]) {
            d2 += iArrays[0][1];
         } else if (iArrays[1][1] != 0 && MathHelper.floor_double(d) - i == iArrays[1][0] && MathHelper.floor_double(d3) - i3 == iArrays[1][2]) {
            d2 += iArrays[1][1];
         }

         return this.getPos(d, d2, d3);
      } else {
         return null;
      }
   }

   @Override
   public void applyEntityCollision(Entity entityIn) {
      if (!this.worldObj.isRemote && !entityIn.noClip && !this.noClip && entityIn != this.riddenByEntity) {
         if (entityIn instanceof EntityLivingBase
            && !(entityIn instanceof EntityPlayer)
            && !(entityIn instanceof EntityIronGolem)
            && this.getMinecartType() == EntityMinecart.EnumMinecartType.RIDEABLE
            && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.01
            && this.riddenByEntity == null
            && entityIn.ridingEntity == null) {
            entityIn.mountEntity(this);
         }

         double d = entityIn.posX - this.posX;
         double d2 = entityIn.posZ - this.posZ;
         double d3 = d * d + d2 * d2;
         if (d3 >= 1.0E-4F) {
            d3 = MathHelper.sqrt_double(d3);
            d /= d3;
            d2 /= d3;
            double d4 = 1.0 / d3;
            if (d4 > 1.0) {
               d4 = 1.0;
            }

            d *= d4;
            d2 *= d4;
            d *= 0.1F;
            d2 *= 0.1F;
            d *= 1.0F - this.entityCollisionReduction;
            d2 *= 1.0F - this.entityCollisionReduction;
            d *= 0.5;
            d2 *= 0.5;
            if (entityIn instanceof EntityMinecart) {
               double d5 = entityIn.posX - this.posX;
               double d6 = entityIn.posZ - this.posZ;
               Vec3 vec3 = new Vec3(d5, 0.0, d6).normalize();
               Vec3 vec32 = new Vec3(
                     MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F), 0.0, MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F)
                  )
                  .normalize();
               double d7 = Math.abs(vec3.dotProduct(vec32));
               if (d7 < 0.8F) {
                  return;
               }

               double d8 = entityIn.motionX + this.motionX;
               double d9 = entityIn.motionZ + this.motionZ;
               if (((EntityMinecart)entityIn).getMinecartType() == EntityMinecart.EnumMinecartType.FURNACE
                  && this.getMinecartType() != EntityMinecart.EnumMinecartType.FURNACE) {
                  this.motionX *= 0.2F;
                  this.motionZ *= 0.2F;
                  this.addVelocity(entityIn.motionX - d, 0.0, entityIn.motionZ - d2);
                  entityIn.motionX *= 0.95F;
                  entityIn.motionZ *= 0.95F;
               } else if (((EntityMinecart)entityIn).getMinecartType() != EntityMinecart.EnumMinecartType.FURNACE
                  && this.getMinecartType() == EntityMinecart.EnumMinecartType.FURNACE) {
                  entityIn.motionX *= 0.2F;
                  entityIn.motionZ *= 0.2F;
                  entityIn.addVelocity(this.motionX + d, 0.0, this.motionZ + d2);
                  this.motionX *= 0.95F;
                  this.motionZ *= 0.95F;
               } else {
                  d8 /= 2.0;
                  d9 /= 2.0;
                  this.motionX *= 0.2F;
                  this.motionZ *= 0.2F;
                  this.addVelocity(d8 - d, 0.0, d9 - d2);
                  entityIn.motionX *= 0.2F;
                  entityIn.motionZ *= 0.2F;
                  entityIn.addVelocity(d8 + d, 0.0, d9 + d2);
               }
            } else {
               this.addVelocity(-d, 0.0, -d2);
               entityIn.addVelocity(d / 4.0, 0.0, d2 / 4.0);
            }
         }
      }
   }

   @Override
   public void setCustomNameTag(String name) {
      this.entityName = name;
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.worldObj.isRemote || this.isDead) {
         return true;
      }

      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      this.setRollingDirection(-this.getRollingDirection());
      this.setRollingAmplitude(10);
      this.setBeenAttacked();
      this.setDamage(this.getDamage() + amount * 10.0F);
      boolean flag = source.getEntity() instanceof EntityPlayer && ((EntityPlayer)source.getEntity()).capabilities.isCreativeMode;
      if (flag || this.getDamage() > 40.0F) {
         if (this.riddenByEntity != null) {
            this.riddenByEntity.mountEntity((Entity)null);
         }

         if (flag && !this.hasCustomName()) {
            this.setDead();
         } else {
            this.killMinecart(source);
         }
      }

      return true;
   }

   @Override
   public String getName() {
      return this.entityName != null ? this.entityName : super.getName();
   }

   public boolean hasDisplayTile() {
      return this.getDataWatcher().getWatchableObjectByte(22) == 1;
   }

   @Override
   public double getMountedYOffset() {
      return 0.0;
   }

   protected double getMaximumSpeed() {
      return 0.4;
   }

   @Override
   public String getCustomNameTag() {
      return this.entityName;
   }

   public void killMinecart(DamageSource source) {
      this.setDead();
      if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
         ItemStack itemStack = new ItemStack(Items.minecart, 1);
         if (this.entityName != null) {
            itemStack.setStackDisplayName(this.entityName);
         }

         this.entityDropItem(itemStack, 0.0F);
      }
   }

   public int getRollingAmplitude() {
      return this.dataWatcher.getWatchableObjectInt(17);
   }

   public EntityMinecart(World worldIn) {
      super(worldIn);
      this.preventEntitySpawning = true;
      this.setSize(0.98F, 0.7F);
   }

   public int getDefaultDisplayTileOffset() {
      return 6;
   }

   @Override
   public void onUpdate() {
      if (this.getRollingAmplitude() > 0) {
         this.setRollingAmplitude(this.getRollingAmplitude() - 1);
      }

      if (this.getDamage() > 0.0F) {
         this.setDamage(this.getDamage() - 1.0F);
      }

      if (this.posY < -64.0) {
         this.kill();
      }

      if (!this.worldObj.isRemote && this.worldObj instanceof WorldServer) {
         this.worldObj.theProfiler.startSection("portal");
         MinecraftServer minecraftServer = ((WorldServer)this.worldObj).getMinecraftServer();
         int maxInPortalTime = this.getMaxInPortalTime();
         if (this.inPortal) {
            if (minecraftServer.getAllowNether()) {
               if (this.ridingEntity == null && this.portalCounter++ >= maxInPortalTime) {
                  this.portalCounter = maxInPortalTime;
                  this.timeUntilPortal = this.getPortalCooldown();
                  byte b;
                  if (this.worldObj.provider.getDimensionId() == -1) {
                     b = 0;
                  } else {
                     b = -1;
                  }

                  this.travelToDimension(b);
               }

               this.inPortal = false;
            }
         } else {
            if (this.portalCounter > 0) {
               this.portalCounter -= 4;
            }

            if (this.portalCounter < 0) {
               this.portalCounter = 0;
            }
         }

         if (this.timeUntilPortal > 0) {
            this.timeUntilPortal--;
         }

         this.worldObj.theProfiler.endSection();
      }

      if (this.worldObj.isRemote) {
         if (this.turnProgress > 0) {
            double d = this.posX + (this.minecartX - this.posX) / this.turnProgress;
            double d2 = this.posY + (this.minecartY - this.posY) / this.turnProgress;
            double d3 = this.posZ + (this.minecartZ - this.posZ) / this.turnProgress;
            double d4 = MathHelper.wrapAngleTo180_double(this.minecartYaw - this.rotationYaw);
            this.rotationYaw = (float)(this.rotationYaw + d4 / this.turnProgress);
            this.rotationPitch = (float)(this.rotationPitch + (this.minecartPitch - this.rotationPitch) / this.turnProgress);
            this.turnProgress--;
            this.setPosition(d, d2, d3);
            this.setRotation(this.rotationYaw, this.rotationPitch);
         } else {
            this.setPosition(this.posX, this.posY, this.posZ);
            this.setRotation(this.rotationYaw, this.rotationPitch);
         }
      } else {
         this.prevPosX = this.posX;
         this.prevPosY = this.posY;
         this.prevPosZ = this.posZ;
         this.motionY -= 0.04F;
         int i = MathHelper.floor_double(this.posX);
         int i2 = MathHelper.floor_double(this.posY);
         int i3 = MathHelper.floor_double(this.posZ);
         if (BlockRailBase.isRailBlock(this.worldObj, new BlockPos(i, i2 - 1, i3))) {
            i2--;
         }

         BlockPos blockPos = new BlockPos(i, i2, i3);
         IBlockState blockState2 = this.worldObj.getBlockState(blockPos);
         if (BlockRailBase.isRailBlock(blockState2)) {
            this.moveAlongTrack(blockPos, blockState2);
            if (blockState2.getBlock() == Blocks.activator_rail) {
               this.setHasDisplayTile(blockState2.getValue(BlockRailPowered.POWERED));
            }
         } else {
            this.moveDerailedMinecart();
         }

         this.doBlockCollisions();
         this.rotationPitch = 0.0F;
         double d5 = this.prevPosX - this.posX;
         double d6 = this.prevPosZ - this.posZ;
         if (d5 * d5 + d6 * d6 > 0.001) {
            this.rotationYaw = (float)(MathHelper.atan2(d6, d5) * 180.0 / Math.PI);
            if (this.isInReverse) {
               this.rotationYaw += 180.0F;
            }
         }

         double d7 = MathHelper.wrapAngleTo180_float(this.rotationYaw - this.prevRotationYaw);
         if (d7 < -170.0 || d7 >= 170.0) {
            this.rotationYaw += 180.0F;
            this.isInReverse = !this.isInReverse;
         }

         this.setRotation(this.rotationYaw, this.rotationPitch);

         for (Entity entity2 : this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(0.2F, 0.0, 0.2F))) {
            if (entity2 != this.riddenByEntity && entity2.canBePushed() && entity2 instanceof EntityMinecart) {
               entity2.applyEntityCollision(this);
            }
         }

         if (this.riddenByEntity != null && this.riddenByEntity.isDead) {
            if (this.riddenByEntity.ridingEntity == this) {
               this.riddenByEntity.ridingEntity = null;
            }

            this.riddenByEntity = null;
         }

         this.handleWaterMovement();
      }
   }

   @Override
   protected void writeEntityToNBT(NBTTagCompound tagCompound) {
      if (this.hasDisplayTile()) {
         tagCompound.setBoolean("CustomDisplayTile", true);
         IBlockState blockState = this.getDisplayTile();
         ResourceLocation resourceLocation = Block.blockRegistry.getNameForObject(blockState.getBlock());
         tagCompound.setString("DisplayTile", resourceLocation == null ? "" : resourceLocation.toString());
         tagCompound.setInteger("DisplayData", blockState.getBlock().getMetaFromState(blockState));
         tagCompound.setInteger("DisplayOffset", this.getDisplayTileOffset());
      }

      if (this.entityName != null && this.entityName.length() > 0) {
         tagCompound.setString("CustomName", this.entityName);
      }
   }

   public Vec3 getPos(double d, double d2, double d3) {
      int i = MathHelper.floor_double(d);
      int i2 = MathHelper.floor_double(d2);
      int i3 = MathHelper.floor_double(d3);
      if (BlockRailBase.isRailBlock(this.worldObj, new BlockPos(i, i2 - 1, i3))) {
         i2--;
      }

      IBlockState blockState = this.worldObj.getBlockState(new BlockPos(i, i2, i3));
      if (BlockRailBase.isRailBlock(blockState)) {
         net.minecraft.block.BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = blockState.getValue(((BlockRailBase)blockState.getBlock()).getShapeProperty());
         int[][] iArrays = matrix[blockrailbase$enumraildirection.getMetadata()];
         double d4 = i + 0.5 + iArrays[0][0] * 0.5;
         double d5 = i2 + 0.0625 + iArrays[0][1] * 0.5;
         double d6 = i3 + 0.5 + iArrays[0][2] * 0.5;
         double d7 = i + 0.5 + iArrays[1][0] * 0.5;
         double d8 = i2 + 0.0625 + iArrays[1][1] * 0.5;
         double d9 = i3 + 0.5 + iArrays[1][2] * 0.5;
         double d10 = d7 - d4;
         double d11 = (d8 - d5) * 2.0;
         double d12 = d9 - d6;
         double d13;
         if (d10 == 0.0) {
            d13 = d3 - i3;
         } else if (d12 == 0.0) {
            d13 = d - i;
         } else {
            double d14 = d - d4;
            double d15 = d3 - d6;
            d13 = (d14 * d10 + d15 * d12) * 2.0;
         }

         d = d4 + d10 * d13;
         d2 = d5 + d11 * d13;
         d3 = d6 + d12 * d13;
         if (d11 < 0.0) {
            d2++;
         }

         if (d11 > 0.0) {
            d2 += 0.5;
         }

         return new Vec3(d, d2, d3);
      } else {
         return null;
      }
   }

   @Override
   protected void entityInit() {
      this.dataWatcher.addObject(17, new Integer(0));
      this.dataWatcher.addObject(18, new Integer(1));
      this.dataWatcher.addObject(19, new Float(0.0F));
      this.dataWatcher.addObject(20, new Integer(0));
      this.dataWatcher.addObject(21, new Integer(6));
      this.dataWatcher.addObject(22, (byte)0);
   }

   @Override
   public IChatComponent getDisplayName() {
      if (this.hasCustomName()) {
         ChatComponentText chatComponentText = new ChatComponentText(this.entityName);
         chatComponentText.getChatStyle().setChatHoverEvent(this.getHoverEvent());
         chatComponentText.getChatStyle().setInsertion(this.getUniqueID().toString());
         return chatComponentText;
      } else {
         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation(this.getName());
         chatComponentTranslation.getChatStyle().setChatHoverEvent(this.getHoverEvent());
         chatComponentTranslation.getChatStyle().setInsertion(this.getUniqueID().toString());
         return chatComponentTranslation;
      }
   }

   @Override
   public void performHurtAnimation() {
      this.setRollingDirection(-this.getRollingDirection());
      this.setRollingAmplitude(10);
      this.setDamage(this.getDamage() + this.getDamage() * 10.0F);
   }

   @Override
   protected boolean canTriggerWalking() {
      return false;
   }

   public abstract EntityMinecart.EnumMinecartType getMinecartType();

   public float getDamage() {
      return this.dataWatcher.getWatchableObjectFloat(19);
   }

   public IBlockState getDefaultDisplayTile() {
      return Blocks.air.getDefaultState();
   }

   public void setHasDisplayTile(boolean flag) {
   }

   @Override
   public boolean canBeCollidedWith() {
      return !this.isDead;
   }

   public void update() {
      this.getDataWatcher().updateObject(22, (byte)1);
   }

   public void setRollingDirection(int i) {
      this.dataWatcher.updateObject(18, i);
   }

   @Override
   public AxisAlignedBB getCollisionBox(Entity entityIn) {
      return entityIn.canBePushed() ? entityIn.getEntityBoundingBox() : null;
   }

   public void setDisplayTileOffset(int i) {
      this.getDataWatcher().updateObject(21, i);
      this.update();
   }

   public static EntityMinecart getMinecart(World worldIn, double x, double y, double z, EntityMinecart.EnumMinecartType type) {
      switch (type) {
         case CHEST:
            return new EntityMinecartChest(worldIn, x, y, z);
         case FURNACE:
            return new EntityMinecartFurnace(worldIn, x, y, z);
         case TNT:
            return new EntityMinecartTNT(worldIn, x, y, z);
         case SPAWNER:
            return new EntityMinecartMobSpawner(worldIn, x, y, z);
         case HOPPER:
            return new EntityMinecartHopper(worldIn, x, y, z);
         case COMMAND_BLOCK:
            return new EntityMinecartCommandBlock(worldIn, x, y, z);
         default:
            return new EntityMinecartEmpty(worldIn, x, y, z);
      }
   }

   @Override
   public boolean hasCustomName() {
      return this.entityName != null;
   }

   public void setDisplayTile(IBlockState iBlockState) {
      this.getDataWatcher().updateObject(20, Block.getStateId(iBlockState));
      this.update();
   }

   public IBlockState getDisplayTile() {
      return !this.hasDisplayTile() ? this.getDefaultDisplayTile() : Block.getStateById(this.getDataWatcher().getWatchableObjectInt(20));
   }

   public EntityMinecart(World worldIn, double x, double y, double z) {
      this(worldIn);
      this.setPosition(x, y, z);
      this.motionX = 0.0;
      this.motionY = 0.0;
      this.motionZ = 0.0;
      this.prevPosX = x;
      this.prevPosY = y;
      this.prevPosZ = z;
   }

   public void setDamage(float f) {
      this.dataWatcher.updateObject(19, f);
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox() {
      return null;
   }

   @Override
   public boolean canBePushed() {
      return true;
   }

   protected void moveDerailedMinecart() {
      double maximumSpeed = this.getMaximumSpeed();
      this.motionX = MathHelper.clamp_double(this.motionX, -maximumSpeed, maximumSpeed);
      this.motionZ = MathHelper.clamp_double(this.motionZ, -maximumSpeed, maximumSpeed);
      if (this.onGround) {
         this.motionX *= 0.5;
         this.motionY *= 0.5;
         this.motionZ *= 0.5;
      }

      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      if (!this.onGround) {
         this.motionX *= 0.95F;
         this.motionY *= 0.95F;
         this.motionZ *= 0.95F;
      }
   }

   public void setRollingAmplitude(int i) {
      this.dataWatcher.updateObject(17, i);
   }

   public int getDisplayTileOffset() {
      return !this.hasDisplayTile() ? this.getDefaultDisplayTileOffset() : this.getDataWatcher().getWatchableObjectInt(21);
   }

   @Override
   protected void readEntityFromNBT(NBTTagCompound tagCompund) {
      if (tagCompund.getBoolean("CustomDisplayTile")) {
         int integer = tagCompund.getInteger("DisplayData");
         if (tagCompund.hasKey("DisplayTile", 8)) {
            Block block = Block.getBlockFromName(tagCompund.getString("DisplayTile"));
            if (block == null) {
               this.setDisplayTile(Blocks.air.getDefaultState());
            } else {
               this.setDisplayTile(block.getStateFromMeta(integer));
            }
         } else {
            Block block2 = Block.getBlockById(tagCompund.getInteger("DisplayTile"));
            if (block2 == null) {
               this.setDisplayTile(Blocks.air.getDefaultState());
            } else {
               this.setDisplayTile(block2.getStateFromMeta(integer));
            }
         }

         this.setDisplayTileOffset(tagCompund.getInteger("DisplayOffset"));
      }

      if (tagCompund.hasKey("CustomName", 8) && tagCompund.getString("CustomName").length() > 0) {
         this.entityName = tagCompund.getString("CustomName");
      }
   }

   @Override
   public void handleDouble(double d, double d2, double d3, float f, float f2, boolean flag) {
      this.minecartX = d;
      this.minecartY = d2;
      this.minecartZ = d3;
      this.minecartYaw = f;
      this.minecartPitch = f2;
      this.turnProgress = 5;
      this.motionX = this.velocityX;
      this.motionY = this.velocityY;
      this.motionZ = this.velocityZ;
   }

   public enum EnumMinecartType {
      RIDEABLE(0, "MinecartRideable"),
      CHEST(1, "MinecartChest"),
      FURNACE(2, "MinecartFurnace"),
      TNT(3, "MinecartTNT"),
      SPAWNER(4, "MinecartSpawner"),
      HOPPER(5, "MinecartHopper"),
      COMMAND_BLOCK(6, "MinecartCommandBlock");

      private final int networkID;
      private static final Map<Integer, EntityMinecart.EnumMinecartType> ID_LOOKUP = Maps.newHashMap();
      private final String name;

      static {
         for (EntityMinecart.EnumMinecartType enumMinecartType : values()) {
            ID_LOOKUP.put(enumMinecartType.getNetworkID(), enumMinecartType);
         }
      }

      public int getNetworkID() {
         return this.networkID;
      }

      public static EntityMinecart.EnumMinecartType byNetworkID(int id) {
         EntityMinecart.EnumMinecartType entityminecart$enumminecarttype = ID_LOOKUP.get(id);
         return entityminecart$enumminecarttype == null ? RIDEABLE : entityminecart$enumminecarttype;
      }

      EnumMinecartType(int i, String text) {
         this.networkID = i;
         this.name = text;
      }

      public String getName() {
         return this.name;
      }
   }
}
