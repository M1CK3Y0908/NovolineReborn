package net.minecraft.entity;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import wtf.novoline.Novoline;
import wtf.novoline.event.MoveEntityEvent;
import wtf.novoline.event.MoveEntityEvent2;
import wtf.novoline.event.MoveFlyingEvent;
import wtf.novoline.module.ModuleManager;

public abstract class Entity implements ICommandSender {
   public double prevPosZ;
   private double entityRiderYawDelta;
   public int serverPosZ;
   public float rotationPitch;
   public double motionZ;
   public boolean isCollidedVertically;
   public float fallDistance;
   public boolean addedToChunk;
   public float stepHeight;
   public int chunkCoordZ;
   public double motionX;
   protected Vec3 lastPortalVec;
   public boolean preventEntitySpawning;
   private int entityId;
   public boolean forceSpawn;
   public int chunkCoordY;
   public int ticksExisted;
   public double lastTickPosX;
   public float entityCollisionReduction;
   public boolean velocityChanged;
   public float distanceWalkedOnStepModified;
   public double prevPosX;
   public double renderDistanceWeight;
   public boolean ignoreFrustumCheck;
   public float distanceWalkedModified;
   private AxisAlignedBB boundingBox;
   public int serverPosX;
   private double entityRiderPitchDelta;
   protected boolean inWater;
   public boolean isCollided;
   public double posX;
   public int hurtResistantTime;
   public boolean noClip;
   public float width;
   protected EnumFacing teleportDirection;
   public float height;
   protected boolean isImmuneToFire;
   public boolean isCollidedHorizontally;
   public int serverPosY;
   public double prevPosY;
   public boolean isAirBorne;
   public float prevRotationPitch;
   public float prevRotationYaw;
   private int nextStepDistance;
   public int chunkCoordX;
   protected Random rand;
   protected BlockPos lastPortalPos;
   public int timeUntilPortal;
   private static int nextEntityID;
   public boolean isDead;
   private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   private int fire;
   public int fireResistance;
   public double lastTickPosY;
   protected boolean isInWeb;
   protected boolean firstUpdate;
   protected boolean inPortal;
   protected UUID entityUniqueID;
   public double posZ;
   protected int portalCounter;
   public Entity riddenByEntity;
   public float rotationYaw;
   private final CommandResultStats cmdResultStats;
   private boolean isOutsideBorder;
   public float prevDistanceWalkedModified;
   public World worldObj;
   public double lastTickPosZ;
   public int dimension;
   public Entity ridingEntity;
   private boolean invulnerable;
   protected DataWatcher dataWatcher;
   public boolean onGround;
   public double posY;
   public double motionY;

   @Override
   public boolean equals(Object arg1) {
      return arg1 instanceof Entity ? ((Entity)arg1).entityId == this.entityId : false;
   }

   public float getRotationYawHead() {
      return 0.0F;
   }

   public void readFromNBT(NBTTagCompound tagCompund) {
      try {
         NBTTagList nBTTagList = tagCompund.getTagList("Pos", 6);
         NBTTagList nBTTagList2 = tagCompund.getTagList("Motion", 6);
         NBTTagList nBTTagList3 = tagCompund.getTagList("Rotation", 5);
         this.motionX = nBTTagList2.getDoubleAt(0);
         this.motionY = nBTTagList2.getDoubleAt(1);
         this.motionZ = nBTTagList2.getDoubleAt(2);
         if (Math.abs(this.motionX) > 10.0) {
            this.motionX = 0.0;
         }

         if (Math.abs(this.motionY) > 10.0) {
            this.motionY = 0.0;
         }

         if (Math.abs(this.motionZ) > 10.0) {
            this.motionZ = 0.0;
         }

         this.prevPosX = this.lastTickPosX = this.posX = nBTTagList.getDoubleAt(0);
         this.prevPosY = this.lastTickPosY = this.posY = nBTTagList.getDoubleAt(1);
         this.prevPosZ = this.lastTickPosZ = this.posZ = nBTTagList.getDoubleAt(2);
         this.prevRotationYaw = this.rotationYaw = nBTTagList3.getFloatAt(0);
         this.prevRotationPitch = this.rotationPitch = nBTTagList3.getFloatAt(1);
         this.setRotationYawHead(this.rotationYaw);
         this.setRenderYawOffset(this.rotationYaw);
         this.fallDistance = tagCompund.getFloat("FallDistance");
         this.fire = tagCompund.getShort("Fire");
         this.setAir(tagCompund.getShort("Air"));
         this.onGround = tagCompund.getBoolean("OnGround");
         this.dimension = tagCompund.getInteger("Dimension");
         this.invulnerable = tagCompund.getBoolean("Invulnerable");
         this.timeUntilPortal = tagCompund.getInteger("PortalCooldown");
         if (tagCompund.hasKey("UUIDMost", 4) && tagCompund.hasKey("UUIDLeast", 4)) {
            this.entityUniqueID = new UUID(tagCompund.getLong("UUIDMost"), tagCompund.getLong("UUIDLeast"));
         } else if (tagCompund.hasKey("UUID", 8)) {
            this.entityUniqueID = UUID.fromString(tagCompund.getString("UUID"));
         }

         this.setPosition(this.posX, this.posY, this.posZ);
         this.setRotation(this.rotationYaw, this.rotationPitch);
         if (tagCompund.hasKey("CustomName", 8) && tagCompund.getString("CustomName").length() > 0) {
            this.setCustomNameTag(tagCompund.getString("CustomName"));
         }

         this.setAlwaysRenderNameTag(tagCompund.getBoolean("CustomNameVisible"));
         this.cmdResultStats.readStatsFromNBT(tagCompund);
         this.setSilent(tagCompund.getBoolean("Silent"));
         this.readEntityFromNBT(tagCompund);
         if (this.shouldSetPosAfterLoading()) {
            this.setPosition(this.posX, this.posY, this.posZ);
         }
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Loading entity NBT");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Entity being loaded");
         this.addEntityCrashInfo(crashReportCategory);
         throw new ReportedException(crashReport);
      }
   }

   public void setEntityId(int id) {
      this.entityId = id;
   }

   public void setCommandStats(Entity entityIn) {
      this.cmdResultStats.addAllStats(entityIn.getCommandStats());
   }

   public NBTTagCompound getNBTTagCompound() {
      return null;
   }

   public void updateRiderPosition() {
      if (this.riddenByEntity != null) {
         this.riddenByEntity.setPosition(this.posX, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ);
      }
   }

   public int getPortalCooldown() {
      return 300;
   }

   public boolean isPushedByWater() {
      return true;
   }

   public void onDataWatcherUpdate(int dataID) {
   }

   @Override
   public String toString() {
      return String.format(
         "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]",
         this.getClass().getSimpleName(),
         this.getName(),
         this.entityId,
         this.worldObj == null ? "~NULL~" : this.worldObj.getWorldInfo().getWorldName(),
         this.posX,
         this.posY,
         this.posZ
      );
   }

   public AxisAlignedBB getEntityBoundingBox() {
      return this.boundingBox;
   }

   public Entity[] getParts() {
      return null;
   }

   public boolean isInvisibleToPlayer(EntityPlayer player) {
      if (ModuleManager.seeInvisibles.isEnabled()) {
         return false;
      } else {
         return player.isSpectator() ? false : this.isInvisible();
      }
   }

   public void handleStatusUpdate(byte id) {
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      this.setBeenAttacked();
      return false;
   }

   public boolean verifyExplosion(Explosion explosionIn, World worldIn, BlockPos pos, IBlockState blockStateIn, float f2) {
      return true;
   }

   public void clientUpdateEntityNBT(NBTTagCompound compound) {
   }

   public void updateRidden() {
      if (this.ridingEntity.isDead) {
         this.ridingEntity = null;
      } else {
         this.motionX = 0.0;
         this.motionY = 0.0;
         this.motionZ = 0.0;
         this.onUpdate();
         if (this.ridingEntity != null) {
            this.ridingEntity.updateRiderPosition();
            this.entityRiderYawDelta = this.entityRiderYawDelta + (this.ridingEntity.rotationYaw - this.ridingEntity.prevRotationYaw);
            this.entityRiderPitchDelta = this.entityRiderPitchDelta + (this.ridingEntity.rotationPitch - this.ridingEntity.prevRotationPitch);

            while (this.entityRiderYawDelta >= 180.0) {
               this.entityRiderYawDelta -= 360.0;
            }

            while (this.entityRiderYawDelta < -180.0) {
               this.entityRiderYawDelta += 360.0;
            }

            while (this.entityRiderPitchDelta >= 180.0) {
               this.entityRiderPitchDelta -= 360.0;
            }

            while (this.entityRiderPitchDelta < -180.0) {
               this.entityRiderPitchDelta += 360.0;
            }

            double d2 = this.entityRiderYawDelta * 0.5;
            double d3 = this.entityRiderPitchDelta * 0.5;
            float f2 = 10.0F;
            if (d2 > f2) {
               d2 = f2;
            }

            if (d2 < -f2) {
               d2 = -f2;
            }

            if (d3 > f2) {
               d3 = f2;
            }

            if (d3 < -f2) {
               d3 = -f2;
            }

            this.entityRiderYawDelta -= d2;
            this.entityRiderPitchDelta -= d3;
         }
      }
   }

   protected void resetHeight() {
      float f2 = MathHelper.sqrt_double(this.motionX * this.motionX * 0.2F + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.2F) * 0.2F;
      if (f2 > 1.0F) {
         f2 = 1.0F;
      }

      this.playSound(this.getSplashSound(), f2, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
      float f3 = MathHelper.floor_double(this.getEntityBoundingBox().minY);

      for (int index = 0; index < 1.0F + this.width * 20.0F; index++) {
         float f4 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         float f5 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         this.worldObj
            .spawnParticle(
               EnumParticleTypes.WATER_BUBBLE,
               this.posX + f4,
               f3 + 1.0F,
               this.posZ + f5,
               this.motionX,
               this.motionY - this.rand.nextFloat() * 0.2F,
               this.motionZ
            );
      }

      for (int innerIndex = 0; innerIndex < 1.0F + this.width * 20.0F; innerIndex++) {
         float f6 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         float f7 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         this.worldObj
            .spawnParticle(
               EnumParticleTypes.WATER_SPLASH, this.posX + f6, f3 + 1.0F, this.posZ + f7, this.motionX, this.motionY, this.motionZ
            );
      }
   }

   public EnumFacing getHorizontalFacing() {
      return EnumFacing.getHorizontal(MathHelper.floor_double(this.rotationYaw * 4.0F / 360.0F + 0.5) & 3);
   }

   protected abstract void entityInit();

   public void handleDouble(double d2, double d3, double d4, float f2, float f3, boolean flag) {
      this.setPosition(d2, d3, d4);
      this.setRotation(f2, f3);
      List collidingBoundingBoxes = this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().contract(0.03125, 0.0, 0.03125));
      if (!collidingBoundingBoxes.isEmpty()) {
         double d5 = 0.0;

         for (AxisAlignedBB axisAlignedBB2 : (Iterable<AxisAlignedBB>) collidingBoundingBoxes) {
            if (axisAlignedBB2.maxY > d5) {
               d5 = axisAlignedBB2.maxY;
            }
         }

         d3 += d5 - this.getEntityBoundingBox().minY;
         this.setPosition(d2, d3, d4);
      }
   }

   public EntityItem entityDropItem(ItemStack itemStackIn, float offsetY) {
      if (itemStackIn.stackSize != 0 && itemStackIn.getItem() != null) {
         EntityItem entityItem = new EntityItem(this.worldObj, this.posX, this.posY + offsetY, this.posZ, itemStackIn);
         entityItem.setDefaultPickupDelay();
         this.worldObj.spawnEntityInWorld(entityItem);
         return entityItem;
      } else {
         return null;
      }
   }

   public void setSneaking(boolean sneaking) {
      this.setFlag(1, sneaking);
   }

   @Override
   public String getName() {
      if (this.hasCustomName()) {
         return this.getCustomNameTag();
      }

      String text = EntityList.getEntityString(this);
      if (text == null) {
         text = "generic";
      }

      return StatCollector.translateToLocal("entity." + text + ".name");
   }

   public boolean getAlwaysRenderNameTagForRender() {
      return this.getAlwaysRenderNameTag();
   }

   public boolean isEntityEqual(Entity entityIn) {
      return this == entityIn;
   }

   public boolean isIsInWeb() {
      return this.isInWeb;
   }

   public void moveFlying(float strafe, float forward, float friction) {
      MoveFlyingEvent moveFlyingEvent = new MoveFlyingEvent();
      moveFlyingEvent.yaw = this.rotationYaw;
      if (this == Minecraft.getMinecraft().thePlayer) {
         moveFlyingEvent.strafe = strafe;
         moveFlyingEvent.forward = forward;
         moveFlyingEvent.friction = friction;
         Novoline.getNovoline().getBus().post(moveFlyingEvent);
         if (moveFlyingEvent.isCancelled()) {
            return;
         }
      }

      float f2 = strafe * strafe + forward * forward;
      if (f2 >= 1.0E-4F) {
         f2 = MathHelper.sqrt_float(f2);
         if (f2 < 1.0F) {
            f2 = 1.0F;
         }

         f2 = friction / f2;
         strafe *= f2;
         forward *= f2;
         float f3 = MathHelper.sin(moveFlyingEvent.yaw * (float) Math.PI / 180.0F);
         float f4 = MathHelper.cos(moveFlyingEvent.yaw * (float) Math.PI / 180.0F);
         this.motionX += strafe * f4 - forward * f3;
         this.motionZ += forward * f4 + strafe * f3;
      }
   }

   public CommandResultStats getCommandStats() {
      return this.cmdResultStats;
   }

   public void moveEntity(double x, double y, double z) {
      if (this.noClip) {
         this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, y, z));
         this.resetPositionToBB();
      } else {
         this.worldObj.theProfiler.startSection("move");
         double posX2 = this.posX;
         double posY2 = this.posY;
         double posZ2 = this.posZ;
         if (this.isInWeb) {
            this.isInWeb = false;
            x *= 0.25;
            y *= 0.05F;
            z *= 0.25;
            this.motionX = 0.0;
            this.motionY = 0.0;
            this.motionZ = 0.0;
         }

         double x2 = x;
         double y2 = y;
         double z2 = z;
         boolean flag = this.onGround && this.isSneaking() && this instanceof EntityPlayer;
         if (this == Minecraft.getMinecraft().thePlayer) {
            MoveEntityEvent moveEntityEvent = new MoveEntityEvent();
            Novoline.getNovoline().getBus().post(moveEntityEvent);
            if (moveEntityEvent.isCancelled()) {
               flag = true;
            }
         }

         if (flag) {
            double d2 = 0.05;

            while (x != 0.0 && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().offset(x, -1.0, 0.0)).isEmpty()) {
               if (x < d2 && x >= -d2) {
                  x = 0.0;
               } else if (x > 0.0) {
                  x -= d2;
               } else {
                  x += d2;
               }

               x2 = x;
            }

            for (; z != 0.0 && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().offset(0.0, -1.0, z)).isEmpty(); z2 = z) {
               if (z < d2 && z >= -d2) {
                  z = 0.0;
               } else if (z > 0.0) {
                  z -= d2;
               } else {
                  z += d2;
               }
            }

            for (; x != 0.0 && z != 0.0 && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().offset(x, -1.0, z)).isEmpty(); z2 = z) {
               if (x < d2 && x >= -d2) {
                  x = 0.0;
               } else if (x > 0.0) {
                  x -= d2;
               } else {
                  x += d2;
               }

               x2 = x;
               if (z < d2 && z >= -d2) {
                  z = 0.0;
               } else if (z > 0.0) {
                  z -= d2;
               } else {
                  z += d2;
               }
            }
         }

         List collidingBoundingBoxes = this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().addCoord(x, y, z));
         AxisAlignedBB entityBoundingBox = this.getEntityBoundingBox();

         for (AxisAlignedBB axisAlignedBB2 : (Iterable<AxisAlignedBB>) collidingBoundingBoxes) {
            y = axisAlignedBB2.calculateYOffset(this.getEntityBoundingBox(), y);
         }

         this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, y, 0.0));
         boolean flag2 = this.onGround || y2 != y && y2 < 0.0;

         for (AxisAlignedBB axisAlignedBB3 : (Iterable<AxisAlignedBB>) collidingBoundingBoxes) {
            x = axisAlignedBB3.calculateXOffset(this.getEntityBoundingBox(), x);
         }

         this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, 0.0, 0.0));

         for (AxisAlignedBB axisAlignedBB4 : (Iterable<AxisAlignedBB>) collidingBoundingBoxes) {
            z = axisAlignedBB4.calculateZOffset(this.getEntityBoundingBox(), z);
         }

         this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, 0.0, z));
         if (this.stepHeight > 0.0F && flag2 && (x2 != x || z2 != z)) {
            double x3 = x;
            double y3 = y;
            double z3 = z;
            AxisAlignedBB entityBoundingBox2 = this.getEntityBoundingBox();
            this.setEntityBoundingBox(entityBoundingBox);
            y = this.stepHeight;
            List collidingBoundingBoxes2 = this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().addCoord(x2, y, z2));
            AxisAlignedBB entityBoundingBox3 = this.getEntityBoundingBox();
            AxisAlignedBB axisAlignedBB5 = entityBoundingBox3.addCoord(x2, 0.0, z2);
            double y4 = y;
            Iterator iterator2 = collidingBoundingBoxes2.iterator();

            while (iterator2.hasNext()) {
               y4 = ((AxisAlignedBB)iterator2.next()).calculateYOffset(axisAlignedBB5, y4);
            }

            entityBoundingBox3 = entityBoundingBox3.offset(0.0, y4, 0.0);
            double d3 = x2;
            Iterator iterator3 = collidingBoundingBoxes2.iterator();

            while (iterator3.hasNext()) {
               d3 = ((AxisAlignedBB)iterator3.next()).calculateXOffset(entityBoundingBox3, d3);
            }

            entityBoundingBox3 = entityBoundingBox3.offset(d3, 0.0, 0.0);
            double d4 = z2;

            for (AxisAlignedBB axisAlignedBB6 : (Iterable<AxisAlignedBB>) collidingBoundingBoxes2) {
               d4 = axisAlignedBB6.calculateZOffset(entityBoundingBox3, d4);
            }

            entityBoundingBox3 = entityBoundingBox3.offset(0.0, 0.0, d4);
            AxisAlignedBB entityBoundingBox4 = this.getEntityBoundingBox();
            double y5 = y;
            Iterator iterator4 = collidingBoundingBoxes2.iterator();

            while (iterator4.hasNext()) {
               y5 = ((AxisAlignedBB)iterator4.next()).calculateYOffset(entityBoundingBox4, y5);
            }

            entityBoundingBox4 = entityBoundingBox4.offset(0.0, y5, 0.0);
            double d5 = x2;
            Iterator iterator5 = collidingBoundingBoxes2.iterator();

            while (iterator5.hasNext()) {
               d5 = ((AxisAlignedBB)iterator5.next()).calculateXOffset(entityBoundingBox4, d5);
            }

            entityBoundingBox4 = entityBoundingBox4.offset(d5, 0.0, 0.0);
            double d6 = z2;
            Iterator iterator6 = collidingBoundingBoxes2.iterator();

            while (iterator6.hasNext()) {
               d6 = ((AxisAlignedBB)iterator6.next()).calculateZOffset(entityBoundingBox4, d6);
            }

            entityBoundingBox4 = entityBoundingBox4.offset(0.0, 0.0, d6);
            double d7 = d3 * d3 + d4 * d4;
            double d8 = d5 * d5 + d6 * d6;
            if (d7 > d8) {
               x = d3;
               z = d4;
               y = -y4;
               this.setEntityBoundingBox(entityBoundingBox3);
            } else {
               x = d5;
               z = d6;
               y = -y5;
               this.setEntityBoundingBox(entityBoundingBox4);
            }

            Iterator iterator7 = collidingBoundingBoxes2.iterator();

            while (iterator7.hasNext()) {
               y = ((AxisAlignedBB)iterator7.next()).calculateYOffset(this.getEntityBoundingBox(), y);
            }

            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, y, 0.0));
            if (x3 * x3 + z3 * z3 >= x * x + z * z) {
               x = x3;
               y = y3;
               z = z3;
               this.setEntityBoundingBox(entityBoundingBox2);
            } else {
               Novoline.getNovoline().getBus().post(new MoveEntityEvent2());
            }
         }

         this.worldObj.theProfiler.endSection();
         this.worldObj.theProfiler.startSection("rest");
         this.resetPositionToBB();
         this.isCollidedHorizontally = x2 != x || z2 != z;
         this.isCollidedVertically = y2 != y;
         this.onGround = this.isCollidedVertically && y2 < 0.0;
         this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
         int i = MathHelper.floor_double(this.posX);
         int i2 = MathHelper.floor_double(this.posY - 0.2F);
         int i3 = MathHelper.floor_double(this.posZ);
         BlockPos blockPos2 = new BlockPos(i, i2, i3);
         Block block2 = this.worldObj.getBlockState(blockPos2).getBlock();
         if (block2.getMaterial() == Material.air) {
            Block block3 = this.worldObj.getBlockState(blockPos2.down()).getBlock();
            if (block3 instanceof BlockFence || block3 instanceof BlockWall || block3 instanceof BlockFenceGate) {
               block2 = block3;
               blockPos2 = blockPos2.down();
            }
         }

         this.updateFallState(y, this.onGround, block2, blockPos2);
         if (x2 != x) {
            this.motionX = 0.0;
         }

         if (z2 != z) {
            this.motionZ = 0.0;
         }

         if (y2 != y) {
            block2.onLanded(this.worldObj, this);
         }

         if (this.canTriggerWalking() && !flag && this.ridingEntity == null) {
            double d9 = this.posX - posX2;
            double d10 = this.posY - posY2;
            double d11 = this.posZ - posZ2;
            if (block2 != Blocks.ladder) {
               d10 = 0.0;
            }

            if (block2 != null && this.onGround) {
               block2.onEntityCollidedWithBlock(this.worldObj, blockPos2, this);
            }

            this.distanceWalkedModified = (float)(this.distanceWalkedModified + MathHelper.sqrt_double(d9 * d9 + d11 * d11) * 0.6);
            this.distanceWalkedOnStepModified = (float)(
               this.distanceWalkedOnStepModified + MathHelper.sqrt_double(d9 * d9 + d10 * d10 + d11 * d11) * 0.6
            );
            if (this.distanceWalkedOnStepModified > this.nextStepDistance && block2.getMaterial() != Material.air) {
               this.nextStepDistance = (int)this.distanceWalkedOnStepModified + 1;
               if (this.isInWater()) {
                  float f2 = MathHelper.sqrt_double(this.motionX * this.motionX * 0.2F + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.2F)
                     * 0.35F;
                  if (f2 > 1.0F) {
                     f2 = 1.0F;
                  }

                  this.playSound(this.getSwimSound(), f2, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
               }

               this.playStepSound(blockPos2, block2);
            }
         }

         try {
            this.doBlockCollisions();
         } catch (Throwable throwable) {
            CrashReport crashReport2 = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
            CrashReportCategory crashReportCategory2 = crashReport2.makeCategory("Entity being checked for collision");
            this.addEntityCrashInfo(crashReportCategory2);
            throw new ReportedException(crashReport2);
         }

         boolean wet = this.isWet();
         if (this.worldObj.isFlammableWithin(this.getEntityBoundingBox().contract(0.001, 0.001, 0.001))) {
            this.setTimeout();
            if (!wet) {
               this.fire++;
               if (this.fire == 0) {
                  this.setFire(8);
               }
            }
         } else if (this.fire <= 0) {
            this.fire = -this.fireResistance;
         }

         if (wet && this.fire > 0) {
            this.playSound("random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
            this.fire = -this.fireResistance;
         }

         this.worldObj.theProfiler.endSection();
      }
   }

   public boolean isOffsetPositionInLiquid(double x, double y, double z) {
      AxisAlignedBB axisAlignedBB = this.getEntityBoundingBox().offset(x, y, z);
      return this.isLiquidPresentInAABB(axisAlignedBB);
   }

   public int getMaxFallHeight() {
      return 3;
   }

   @Override
   public boolean sendCommandFeedback() {
      return false;
   }

   protected abstract void writeEntityToNBT(NBTTagCompound nbtTagCompound);

   public double getDistanceSq(BlockPos pos) {
      return pos.distanceSq(this.posX, this.posY, this.posZ);
   }

   public void setDead() {
      this.isDead = true;
   }

   public String getCustomNameTag() {
      return this.dataWatcher.getWatchableObjectString(2);
   }

   public void setCurrentItemOrArmor(int slotIn, ItemStack stack) {
   }

   private boolean isLiquidPresentInAABB(AxisAlignedBB bb) {
      return this.worldObj.getCollidingBoundingBoxes(this, bb).isEmpty() && !this.worldObj.isAnyLiquid(bb);
   }

   public boolean isBurning() {
      boolean flag = this.worldObj != null && this.worldObj.isRemote;
      return !this.isImmuneToFire && (this.fire > 0 || flag && this.getFlag(0));
   }

   @Override
   public World getEntityWorld() {
      return this.worldObj;
   }

   public void fall(float distance, float damageMultiplier) {
      if (this.riddenByEntity != null) {
         this.riddenByEntity.fall(distance, damageMultiplier);
      }
   }

   protected void playStepSound(BlockPos pos, Block blockIn) {
      net.minecraft.block.Block.SoundType stepSound2 = blockIn.stepSound;
      if (this.worldObj.getBlockState(pos.up()).getBlock() == Blocks.snow_layer) {
         stepSound2 = Blocks.snow_layer.stepSound;
         this.playSound(stepSound2.getStepSound(), stepSound2.getVolume() * 0.15F, stepSound2.getFrequency());
      } else if (!blockIn.getMaterial().isLiquid()) {
         this.playSound(stepSound2.getStepSound(), stepSound2.getVolume() * 0.15F, stepSound2.getFrequency());
      }
   }

   public void setAlwaysRenderNameTag(boolean alwaysRenderNameTag) {
      this.dataWatcher.updateObject(3, (byte)(alwaysRenderNameTag ? 1 : 0));
   }

   public boolean isSpectatedByPlayer(EntityPlayerMP player) {
      return true;
   }

   public boolean isSprinting() {
      return this.getFlag(3);
   }

   public boolean isOutsideBorder() {
      return this.isOutsideBorder;
   }

   public boolean isRiding() {
      return this.ridingEntity != null;
   }

   public float getEyeHeight() {
      return this.height * 0.85F;
   }

   public double getDistanceSq(double x, double y, double z) {
      double xCoordinate = this.posX - x;
      double yCoordinate = this.posY - y;
      double zCoordinate = this.posZ - z;
      return xCoordinate * xCoordinate + yCoordinate * yCoordinate + zCoordinate * zCoordinate;
   }

   private void resetPositionToBB() {
      this.posX = (this.getEntityBoundingBox().minX + this.getEntityBoundingBox().maxX) / 2.0;
      this.posY = this.getEntityBoundingBox().minY;
      this.posZ = (this.getEntityBoundingBox().minZ + this.getEntityBoundingBox().maxZ) / 2.0;
   }

   public EnumFacing getTeleportDirection() {
      return this.teleportDirection;
   }

   @Override
   public IChatComponent getDisplayName() {
      ChatComponentText chatComponentText = new ChatComponentText(this.getName());
      chatComponentText.getChatStyle().setChatHoverEvent(this.getHoverEvent());
      chatComponentText.getChatStyle().setInsertion(this.getUniqueID().toString());
      return chatComponentText;
   }

   public boolean isEntityInsideOpaqueBlock() {
      if (this.noClip) {
         return false;
      }

      net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

      for (int index = 0; index < 8; index++) {
         int i = MathHelper.floor_double(this.posY + ((index >> 0) % 2 - 0.5F) * 0.1F + this.getEyeHeight());
         int i2 = MathHelper.floor_double(this.posX + ((index >> 1) % 2 - 0.5F) * this.width * 0.8F);
         int i3 = MathHelper.floor_double(this.posZ + ((index >> 2) % 2 - 0.5F) * this.width * 0.8F);
         if (mutableBlockPos.getX() != i2 || mutableBlockPos.getY() != i || mutableBlockPos.getZ() != i3) {
            mutableBlockPos.set(i2, i, i3);
            if (this.worldObj.getBlockState(mutableBlockPos).getBlock().isVisuallyOpaque()) {
               return true;
            }
         }
      }

      return false;
   }

   public MovingObjectPosition rayTrace(double blockReachDistance, float partialTicks) {
      Vec3 positionEyes = this.getPositionEyes(partialTicks);
      Vec3 look = this.getLook(partialTicks);
      Vec3 vec3 = positionEyes.addVector(look.xCoord * blockReachDistance, look.yCoord * blockReachDistance, look.zCoord * blockReachDistance);
      return this.worldObj.rayTraceBlocks(positionEyes, vec3, false, false, true);
   }

   @Override
   public void setCommandStat(net.minecraft.command.CommandResultStats.Type type, int amount) {
      this.cmdResultStats.setCommandStatScore(this, type, amount);
   }

   protected final String getEntityString() {
      return EntityList.getEntityString(this);
   }

   public final boolean isImmuneToFire() {
      return this.isImmuneToFire;
   }

   public Vec3 getLook(float partialTicks) {
      if (partialTicks == 1.0F) {
         return this.getVectorForRotation(this.rotationPitch, this.rotationYaw);
      }

      float f2 = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * partialTicks;
      float f3 = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * partialTicks;
      return this.getVectorForRotation(f2, f3);
   }

   public boolean isSilent() {
      return this.dataWatcher.getWatchableObjectByte(4) == 1;
   }

   protected boolean canTriggerWalking() {
      return true;
   }

   public void setSprinting(boolean sprinting) {
      this.setFlag(3, sprinting);
   }

   public void setRenderYawOffset(float offset) {
   }

   public void setRotationYawHead(float rotation) {
   }

   public void setSilent(boolean isSilent) {
      this.dataWatcher.updateObject(4, (byte)(isSilent ? 1 : 0));
   }

   public void setVelocity(double x, double y, double z) {
      this.motionX = x;
      this.motionY = y;
      this.motionZ = z;
   }

   public boolean writeToNBTOptional(NBTTagCompound tagCompund) {
      String text = this.getEntityString();
      if (!this.isDead && text != null && this.riddenByEntity == null) {
         tagCompund.setString("id", text);
         this.writeToNBT(tagCompund);
         return true;
      } else {
         return false;
      }
   }

   public AxisAlignedBB getCollisionBox(Entity entityIn) {
      return null;
   }

   protected String getSplashSound() {
      return "game.neutral.swim.splash";
   }

   protected boolean getFlag(int conditionMet) {
      return (this.dataWatcher.getWatchableObjectByte(0) & 1 << conditionMet) != 0;
   }

   public void moveToBlockPosAndAngles(BlockPos pos, float rotationYawIn, float rotationPitchIn) {
      this.setLocationAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, rotationYawIn, rotationPitchIn);
   }

   public boolean isEntityInvulnerable(DamageSource source) {
      return this.invulnerable && source != DamageSource.outOfWorld && !source.isCreativePlayer();
   }

   protected NBTTagList newDoubleNBTList(double... numbers) {
      NBTTagList nbtTagList = new NBTTagList();

      for (double d2 : numbers) {
         nbtTagList.appendTag(new NBTTagDouble(d2));
      }

      return nbtTagList;
   }

   protected HoverEvent getHoverEvent() {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      String text = EntityList.getEntityString(this);
      nbtTagCompound.setString("id", this.getUniqueID().toString());
      if (text != null) {
         nbtTagCompound.setString("type", text);
      }

      nbtTagCompound.setString("name", this.getName());
      return new HoverEvent(net.minecraft.event.HoverEvent.Action.SHOW_ENTITY, new ChatComponentText(nbtTagCompound.toString()));
   }

   public void setInWeb() {
      this.isInWeb = true;
      this.fallDistance = 0.0F;
   }

   @Override
   public BlockPos getPosition() {
      return new BlockPos(this.posX, this.posY + 0.5, this.posZ);
   }

   protected void setRotation(float yaw, float pitch) {
      this.rotationYaw = yaw % 360.0F;
      this.rotationPitch = pitch % 360.0F;
   }

   public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
      this.lastTickPosX = this.prevPosX = this.posX = x;
      this.lastTickPosY = this.prevPosY = this.posY = y;
      this.lastTickPosZ = this.prevPosZ = this.posZ = z;
      this.rotationYaw = yaw;
      this.rotationPitch = pitch;
      this.setPosition(this.posX, this.posY, this.posZ);
   }

   public void mountEntity(Entity entityIn) {
      this.entityRiderPitchDelta = 0.0;
      this.entityRiderYawDelta = 0.0;
      if (entityIn == null) {
         if (this.ridingEntity != null) {
            this.setLocationAndAngles(
               this.ridingEntity.posX,
               this.ridingEntity.getEntityBoundingBox().minY + this.ridingEntity.height,
               this.ridingEntity.posZ,
               this.rotationYaw,
               this.rotationPitch
            );
            this.ridingEntity.riddenByEntity = null;
         }

         this.ridingEntity = null;
      } else {
         if (this.ridingEntity != null) {
            this.ridingEntity.riddenByEntity = null;
         }

         if (entityIn != null) {
            for (Entity entity = entityIn.ridingEntity; entity != null; entity = entity.ridingEntity) {
               if (entity == this) {
                  return;
               }
            }
         }

         this.ridingEntity = entityIn;
         entityIn.riddenByEntity = this;
      }
   }

   public EntityItem getEntityItem(Item item2) {
      return this.getEntityItem2(item2, 0.0F);
   }

   public void onKillEntity(EntityLivingBase entityLivingIn) {
   }

   public AxisAlignedBB getCollisionBoundingBox() {
      return null;
   }

   public float getCollisionBorderSize() {
      return 0.1F;
   }

   public void extinguish() {
      this.fire = 0;
   }

   public boolean isImmuneToExplosions() {
      return false;
   }

   public int getMaxInPortalTime() {
      return 0;
   }

   public float getDistanceToEntity(Entity entityIn) {
      float f2 = (float)(this.posX - entityIn.posX);
      float f3 = (float)(this.posY - entityIn.posY);
      float f4 = (float)(this.posZ - entityIn.posZ);
      return MathHelper.sqrt_float(f2 * f2 + f3 * f3 + f4 * f4);
   }

   public void onUpdate() {
      this.onEntityUpdate();
   }

   public void setPortal(BlockPos pos) {
      if (this.timeUntilPortal > 0) {
         this.timeUntilPortal = this.getPortalCooldown();
      } else {
         if (!this.worldObj.isRemote && !pos.equals(this.lastPortalPos)) {
            this.lastPortalPos = pos;
            net.minecraft.block.state.pattern.BlockPattern.PatternHelper portalPattern = Blocks.portal.getPortalPattern(this.worldObj, pos);
            double d2 = portalPattern.getFinger().getAxis() == net.minecraft.util.EnumFacing.Axis.X ? portalPattern.getPos().getZ() : portalPattern.getPos().getX();
            double d3 = portalPattern.getFinger().getAxis() == net.minecraft.util.EnumFacing.Axis.X ? this.posZ : this.posX;
            d3 = Math.abs(
               MathHelper.inverseLerp(
                  d3 - (portalPattern.getFinger().rotateY().getAxisDirection() == net.minecraft.util.EnumFacing.AxisDirection.NEGATIVE ? 1 : 0), d2, d2 - portalPattern.getPalmLength()
               )
            );
            double d4 = MathHelper.inverseLerp(this.posY - 1.0, portalPattern.getPos().getY(), portalPattern.getPos().getY() - portalPattern.getThumbLength());
            this.lastPortalVec = new Vec3(d3, d4, 0.0);
            this.teleportDirection = portalPattern.getFinger();
         }

         this.inPortal = true;
      }
   }

   public void playSound(String name, float volume, float pitch) {
      if (!this.isSilent()) {
         this.worldObj.playSoundAtEntity(this, name, volume, pitch);
      }
   }

   public void onStruckByLightning(EntityLightningBolt lightningBolt) {
      this.attackEntityFrom(DamageSource.lightningBolt, 5.0F);
      this.fire++;
      if (this.fire == 0) {
         this.setFire(8);
      }
   }

   public EntityItem getEntityItem2(Item item2, float f2) {
      return this.entityDropItem(new ItemStack(item2, 1, 0), f2);
   }

   @Override
   public Entity getCommandSenderEntity() {
      return this;
   }

   protected void setOnFireFromLava() {
      if (!this.isImmuneToFire) {
         this.attackEntityFrom(DamageSource.lava, 4.0F);
         this.setFire(15);
      }
   }

   public boolean isSneaking() {
      return this.getFlag(1);
   }

   public boolean isInLava() {
      return this.worldObj.isMaterialInBB(this.getEntityBoundingBox().expand(-0.1F, -0.4F, -0.1F), Material.lava);
   }

   public int getAir() {
      return this.dataWatcher.getWatchableObjectShort(1);
   }

   public boolean hasCustomName() {
      return this.dataWatcher.getWatchableObjectString(2).length() > 0;
   }

   public int getEntityId() {
      return this.entityId;
   }

   public void spawnRunningParticles() {
      if (this.isSprinting() && !this.isInWater()) {
         this.createRunningParticles();
      }
   }

   public double getDistance(double x, double y, double z) {
      double xCoordinate = this.posX - x;
      double yCoordinate = this.posY - y;
      double zCoordinate = this.posZ - z;
      return MathHelper.sqrt_double(xCoordinate * xCoordinate + yCoordinate * yCoordinate + zCoordinate * zCoordinate);
   }

   public void writeToNBT(NBTTagCompound tagCompund) {
      try {
         tagCompund.setTag("Pos", this.newDoubleNBTList(this.posX, this.posY, this.posZ));
         tagCompund.setTag("Motion", this.newDoubleNBTList(this.motionX, this.motionY, this.motionZ));
         tagCompund.setTag("Rotation", this.newFloatNBTList(this.rotationYaw, this.rotationPitch));
         tagCompund.setFloat("FallDistance", this.fallDistance);
         tagCompund.setShort("Fire", (short)this.fire);
         tagCompund.setShort("Air", (short)this.getAir());
         tagCompund.setBoolean("OnGround", this.onGround);
         tagCompund.setInteger("Dimension", this.dimension);
         tagCompund.setBoolean("Invulnerable", this.invulnerable);
         tagCompund.setInteger("PortalCooldown", this.timeUntilPortal);
         tagCompund.setLong("UUIDMost", this.getUniqueID().getMostSignificantBits());
         tagCompund.setLong("UUIDLeast", this.getUniqueID().getLeastSignificantBits());
         if (this.getCustomNameTag() != null && this.getCustomNameTag().length() > 0) {
            tagCompund.setString("CustomName", this.getCustomNameTag());
            tagCompund.setBoolean("CustomNameVisible", this.getAlwaysRenderNameTag());
         }

         this.cmdResultStats.writeStatsToNBT(tagCompund);
         if (this.isSilent()) {
            tagCompund.setBoolean("Silent", this.isSilent());
         }

         this.writeEntityToNBT(tagCompund);
         if (this.ridingEntity != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            if (this.ridingEntity.writeMountToNBT(nbtTagCompound)) {
               tagCompund.setTag("Riding", nbtTagCompound);
            }
         }
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Saving entity NBT");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Entity being saved");
         this.addEntityCrashInfo(crashReportCategory);
         throw new ReportedException(crashReport);
      }
   }

   public void setAngles(float yaw, float pitch) {
      float rotationPitch2 = this.rotationPitch;
      float rotationYaw2 = this.rotationYaw;
      this.rotationYaw = (float)(this.rotationYaw + yaw * 0.15);
      this.rotationPitch = (float)(this.rotationPitch - pitch * 0.15);
      this.rotationPitch = MathHelper.clamp_float(this.rotationPitch, -90.0F, 90.0F);
      this.prevRotationPitch = this.prevRotationPitch + (this.rotationPitch - rotationPitch2);
      this.prevRotationYaw = this.prevRotationYaw + (this.rotationYaw - rotationYaw2);
   }

   public boolean isInRangeToRenderDist(double distance) {
      double averageEdgeLength = this.getEntityBoundingBox().getAverageEdgeLength();
      if (Double.isNaN(averageEdgeLength)) {
         averageEdgeLength = 1.0;
      }

      averageEdgeLength = averageEdgeLength * 64.0 * this.renderDistanceWeight;
      return distance < averageEdgeLength * averageEdgeLength;
   }

   public boolean isWet() {
      return this.inWater
         || this.worldObj.isRainingAt(new BlockPos(this.posX, this.posY, this.posZ))
         || this.worldObj.isRainingAt(new BlockPos(this.posX, this.posY + this.height, this.posZ));
   }

   public void setEntityBoundingBox(AxisAlignedBB bb) {
      this.boundingBox = bb;
   }

   public Entity(World worldIn) {
      this.entityId = nextEntityID++;
      this.renderDistanceWeight = 1.0;
      this.boundingBox = ZERO_AABB;
      this.width = 0.6F;
      this.height = 1.8F;
      this.nextStepDistance = 1;
      this.rand = new Random();
      this.fireResistance = 1;
      this.firstUpdate = true;
      this.entityUniqueID = MathHelper.getRandomUuid(this.rand);
      this.cmdResultStats = new CommandResultStats();
      this.worldObj = worldIn;
      this.setPosition(0.0, 0.0, 0.0);
      if (worldIn != null) {
         this.dimension = worldIn.provider.getDimensionId();
      }

      this.dataWatcher = new DataWatcher(this);
      this.dataWatcher.addObject(0, (byte)0);
      this.dataWatcher.addObject(1, (short)300);
      this.dataWatcher.addObject(3, (byte)0);
      this.dataWatcher.addObject(2, "");
      this.dataWatcher.addObject(4, (byte)0);
      this.entityInit();
   }

   public Vec3 getLastPortalVec() {
      return this.lastPortalVec;
   }

   public void onEntityUpdate() {
      this.worldObj.theProfiler.startSection("entityBaseTick");
      if (this.ridingEntity != null && this.ridingEntity.isDead) {
         this.ridingEntity = null;
      }

      this.prevDistanceWalkedModified = this.distanceWalkedModified;
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.prevRotationPitch = this.rotationPitch;
      this.prevRotationYaw = this.rotationYaw;
      if (!this.worldObj.isRemote && this.worldObj instanceof WorldServer) {
         this.worldObj.theProfiler.startSection("portal");
         MinecraftServer minecraftServer2 = ((WorldServer)this.worldObj).getMinecraftServer();
         int maxInPortalTime = this.getMaxInPortalTime();
         if (this.inPortal) {
            if (minecraftServer2.getAllowNether()) {
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

      this.spawnRunningParticles();
      this.handleWaterMovement();
      if (this.worldObj.isRemote) {
         this.fire = 0;
      } else if (this.fire > 0) {
         if (this.isImmuneToFire) {
            this.fire -= 4;
            if (this.fire < 0) {
               this.fire = 0;
            }
         } else {
            if (this.fire % 20 == 0) {
               this.attackEntityFrom(DamageSource.onFire, 1.0F);
            }

            this.fire--;
         }
      }

      if (this.isInLava()) {
         this.setOnFireFromLava();
         this.fallDistance *= 0.5F;
      }

      if (this.posY < -64.0) {
         this.kill();
      }

      if (!this.worldObj.isRemote) {
         this.setFlag(0, this.fire > 0);
      }

      this.firstUpdate = false;
      this.worldObj.theProfiler.endSection();
   }

   public double getDistanceSqToEntity(Entity entityIn) {
      double xCoordinate = this.posX - entityIn.posX;
      double yCoordinate = this.posY - entityIn.posY;
      double zCoordinate = this.posZ - entityIn.posZ;
      return xCoordinate * xCoordinate + yCoordinate * yCoordinate + zCoordinate * zCoordinate;
   }

   protected void doBlockCollisions() {
      BlockPos blockPos2 = new BlockPos(
         this.getEntityBoundingBox().minX + 0.001, this.getEntityBoundingBox().minY + 0.001, this.getEntityBoundingBox().minZ + 0.001
      );
      BlockPos blockPos3 = new BlockPos(
         this.getEntityBoundingBox().maxX - 0.001, this.getEntityBoundingBox().maxY - 0.001, this.getEntityBoundingBox().maxZ - 0.001
      );
      if (this.worldObj.isAreaLoaded(blockPos2, blockPos3)) {
         for (int x2 = blockPos2.getX(); x2 <= blockPos3.getX(); x2++) {
            for (int y2 = blockPos2.getY(); y2 <= blockPos3.getY(); y2++) {
               for (int z2 = blockPos2.getZ(); z2 <= blockPos3.getZ(); z2++) {
                  BlockPos blockPos4 = new BlockPos(x2, y2, z2);
                  IBlockState blockState = this.worldObj.getBlockState(blockPos4);

                  try {
                     blockState.getBlock().onEntityCollidedWithBlock(this.worldObj, blockPos4, blockState, this);
                  } catch (Throwable throwable) {
                     CrashReport crashReport2 = CrashReport.makeCrashReport(throwable, "Colliding entity with block");
                     CrashReportCategory.addBlockInfo(crashReport2.makeCategory("Block being collided with"), blockPos4, blockState);
                     throw new ReportedException(crashReport2);
                  }
               }
            }
         }
      }
   }

   public void setPositionAndUpdate(double x, double y, double z) {
      this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
   }

   public boolean writeMountToNBT(NBTTagCompound tagCompund) {
      String text = this.getEntityString();
      if (!this.isDead && text != null) {
         tagCompund.setString("id", text);
         this.writeToNBT(tagCompund);
         return true;
      } else {
         return false;
      }
   }

   protected void setSize(float width, float height) {
      if (width != this.width || height != this.height) {
         float width2 = this.width;
         this.width = width;
         this.height = height;
         this.setEntityBoundingBox(
            new AxisAlignedBB(
               this.getEntityBoundingBox().minX,
               this.getEntityBoundingBox().minY,
               this.getEntityBoundingBox().minZ,
               this.getEntityBoundingBox().minX + this.width,
               this.getEntityBoundingBox().minY + this.height,
               this.getEntityBoundingBox().minZ + this.width
            )
         );
         if (this.width > width2 && !this.firstUpdate && !this.worldObj.isRemote) {
            this.moveEntity(width2 - this.width, 0.0, width2 - this.width);
         }
      }
   }

   public boolean canBePushed() {
      return false;
   }

   public Vec3 getLookVec() {
      return null;
   }

   @Override
   public void addChatMessage(IChatComponent component) {
   }

   public ItemStack[] getInventory() {
      return null;
   }

   public boolean handleWaterMovement() {
      if (this.worldObj.handleMaterialAcceleration(this.getEntityBoundingBox().expand(0.0, -0.4F, 0.0).contract(0.001, 0.001, 0.001), Material.water, this)) {
         if (!this.inWater && !this.firstUpdate) {
            this.resetHeight();
         }

         this.fallDistance = 0.0F;
         this.inWater = true;
         this.fire = 0;
      } else {
         this.inWater = false;
      }

      return this.inWater;
   }

   protected void applyEnchantments(EntityLivingBase entityLivingBaseIn, Entity entityIn) {
      if (entityIn instanceof EntityLivingBase) {
         EnchantmentHelper.applyThornEnchantments((EntityLivingBase)entityIn, entityLivingBaseIn);
      }

      EnchantmentHelper.applyArthropodEnchantments(entityLivingBaseIn, entityIn);
   }

   public void applyEntityCollision(Entity entityIn) {
      if (entityIn.riddenByEntity != this && entityIn.ridingEntity != this && !entityIn.noClip && !this.noClip) {
         double d2 = entityIn.posX - this.posX;
         double d3 = entityIn.posZ - this.posZ;
         double d4 = MathHelper.abs_max(d2, d3);
         if (d4 >= 0.01F) {
            d4 = MathHelper.sqrt_double(d4);
            d2 /= d4;
            d3 /= d4;
            double d5 = 1.0 / d4;
            if (d5 > 1.0) {
               d5 = 1.0;
            }

            d2 *= d5;
            d3 *= d5;
            d2 *= 0.05F;
            d3 *= 0.05F;
            d2 *= 1.0F - this.entityCollisionReduction;
            d3 *= 1.0F - this.entityCollisionReduction;
            if (this.riddenByEntity == null) {
               this.addVelocity(-d2, 0.0, -d3);
            }

            if (entityIn.riddenByEntity == null) {
               entityIn.addVelocity(d2, 0.0, d3);
            }
         }
      }
   }

   public boolean canRenderOnFire() {
      return this.isBurning();
   }

   protected String getSwimSound() {
      return "game.neutral.swim";
   }

   protected abstract void readEntityFromNBT(NBTTagCompound nbtTagCompound);

   public boolean isInsideOfMaterial(Material materialIn) {
      double d2 = this.posY + this.getEyeHeight();
      BlockPos blockPos2 = new BlockPos(this.posX, d2, this.posZ);
      IBlockState blockState = this.worldObj.getBlockState(blockPos2);
      if (blockState.getBlock().getMaterial() == materialIn) {
         float f2 = BlockLiquid.getLiquidHeightPercent(blockState.getBlock().getMetaFromState(blockState)) - 0.11111111F;
         float f3 = blockPos2.getY() + 1 - f2;
         boolean flag = d2 < f3;
         return !flag && this instanceof EntityPlayer ? false : flag;
      } else {
         return false;
      }
   }

   public boolean doesEntityNotTriggerPressurePlate() {
      return false;
   }

   public boolean hitByEntity(Entity entityIn) {
      return false;
   }

   public Vec3 getPositionEyes(float partialTicks) {
      if (partialTicks == 1.0F) {
         return new Vec3(this.posX, this.posY + this.getEyeHeight(), this.posZ);
      }

      double xCoordinate = this.prevPosX + (this.posX - this.prevPosX) * partialTicks;
      double yCoordinate = this.prevPosY + (this.posY - this.prevPosY) * partialTicks + this.getEyeHeight();
      double zCoordinate = this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks;
      return new Vec3(xCoordinate, yCoordinate, zCoordinate);
   }

   protected NBTTagList newFloatNBTList(float... numbers) {
      NBTTagList nbtTagList = new NBTTagList();

      for (float f2 : numbers) {
         nbtTagList.appendTag(new NBTTagFloat(f2));
      }

      return nbtTagList;
   }

   public boolean isInWater() {
      return this.inWater;
   }

   public boolean isInvisible() {
      return this.getFlag(5);
   }

   public void setFire(int seconds) {
      int i = seconds * 20;
      i = EnchantmentProtection.getFireTimeForEntity(this, i);
      if (this.fire < i) {
         this.fire = i;
      }
   }

   public void setAir(int air) {
      this.dataWatcher.updateObject(1, (short)air);
   }

   public boolean interactAt(EntityPlayer player, Vec3 targetVec3) {
      return false;
   }

   protected final Vec3 getVectorForRotation(float pitch, float yaw) {
      float f2 = MathHelper.cos(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f3 = MathHelper.sin(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f4 = -MathHelper.cos(-pitch * (float) (Math.PI / 180.0));
      float f5 = MathHelper.sin(-pitch * (float) (Math.PI / 180.0));
      return new Vec3(f3 * f4, f5, f2 * f4);
   }

   @Override
   public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
      return true;
   }

   public double getMountedYOffset() {
      return this.height * 0.75;
   }

   public void copyLocationAndAnglesFrom(Entity entityIn) {
      this.setLocationAndAngles(entityIn.posX, entityIn.posY, entityIn.posZ, entityIn.rotationYaw, entityIn.rotationPitch);
   }

   protected boolean shouldSetPosAfterLoading() {
      return true;
   }

   public void setInvisible(boolean invisible) {
      this.setFlag(5, invisible);
   }

   public boolean isEntityAlive() {
      return !this.isDead;
   }

   public boolean getAlwaysRenderNameTag() {
      return this.dataWatcher.getWatchableObjectByte(3) == 1;
   }

   public void onKillCommand() {
      this.setDead();
   }

   public void setWorld(World worldIn) {
      this.worldObj = worldIn;
   }

   protected void createRunningParticles() {
      int i = MathHelper.floor_double(this.posX);
      int i2 = MathHelper.floor_double(this.posY - 0.2F);
      int i3 = MathHelper.floor_double(this.posZ);
      BlockPos blockPos2 = new BlockPos(i, i2, i3);
      IBlockState blockState = this.worldObj.getBlockState(blockPos2);
      if (blockState.getBlock().getRenderType() != -1) {
         this.worldObj
            .spawnParticle(
               EnumParticleTypes.BLOCK_CRACK,
               this.posX + (this.rand.nextFloat() - 0.5) * this.width,
               this.getEntityBoundingBox().minY + 0.1,
               this.posZ + (this.rand.nextFloat() - 0.5) * this.width,
               -this.motionX * 4.0,
               1.5,
               -this.motionZ * 4.0,
               Block.getStateId(blockState)
            );
      }
   }

   public boolean canAttackWithItem() {
      return true;
   }

   public void addToPlayerScore(Entity entityIn, int amount) {
   }

   protected void setFlag(int conditionMet, boolean set) {
      byte b0 = this.dataWatcher.getWatchableObjectByte(0);
      if (set) {
         this.dataWatcher.updateObject(0, (byte)(b0 | 1 << conditionMet));
      } else {
         this.dataWatcher.updateObject(0, (byte)(b0 & ~(1 << conditionMet)));
      }
   }

   @Override
   public Vec3 getPositionVector() {
      return new Vec3(this.posX, this.posY, this.posZ);
   }

   public void performHurtAnimation() {
   }

   public DataWatcher getDataWatcher() {
      return this.dataWatcher;
   }

   public void onChunkLoad() {
   }

   protected void kill() {
      this.setDead();
   }

   public void setEating(boolean eating) {
      this.setFlag(4, eating);
   }

   public void setOutsideBorder(boolean outsideBorder) {
      this.isOutsideBorder = outsideBorder;
   }

   public void setCustomNameTag(String name) {
      this.dataWatcher.updateObject(2, name);
   }

   protected void setBeenAttacked() {
      this.velocityChanged = true;
   }

   public void onCollideWithPlayer(EntityPlayer entityIn) {
   }

   public double getYOffset() {
      return 0.0;
   }

   protected void preparePlayerToSpawn() {
      if (this.worldObj != null) {
         while (true) {
            if (this.posY > 0.0 && this.posY < 256.0) {
               this.setPosition(this.posX, this.posY, this.posZ);
               if (!this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty()) {
                  this.posY++;
                  continue;
               }
            }

            this.motionX = this.motionY = this.motionZ = 0.0;
            this.rotationPitch = 0.0F;
            break;
         }
      }
   }

   public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
      return false;
   }

   protected boolean pushOutOfBlocks(double x, double y, double z) {
      BlockPos blockPos2 = new BlockPos(x, y, z);
      double d2 = x - blockPos2.getX();
      double d3 = y - blockPos2.getY();
      double d4 = z - blockPos2.getZ();
      if (this.worldObj.getCollisionBoxes(this.getEntityBoundingBox()).isEmpty() && !this.worldObj.isBlockFullCube(blockPos2)) {
         return false;
      }

      byte b = 3;
      double d5 = 9999.0;
      if (!this.worldObj.isBlockFullCube(blockPos2.west()) && d2 < d5) {
         d5 = d2;
         b = 0;
      }

      if (!this.worldObj.isBlockFullCube(blockPos2.east()) && 1.0 - d2 < d5) {
         d5 = 1.0 - d2;
         b = 1;
      }

      if (!this.worldObj.isBlockFullCube(blockPos2.up()) && 1.0 - d3 < d5) {
         d5 = 1.0 - d3;
         b = 3;
      }

      if (!this.worldObj.isBlockFullCube(blockPos2.north()) && d4 < d5) {
         d5 = d4;
         b = 4;
      }

      if (!this.worldObj.isBlockFullCube(blockPos2.south()) && 1.0 - d4 < d5) {
         b = 5;
      }

      float f2 = this.rand.nextFloat() * 0.2F + 0.1F;
      if (b == 0) {
         this.motionX = -f2;
      }

      if (b == 1) {
         this.motionX = f2;
      }

      if (b == 3) {
         this.motionY = f2;
      }

      if (b == 4) {
         this.motionZ = -f2;
      }

      if (b == 5) {
         this.motionZ = f2;
      }

      return true;
   }

   protected void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos) {
      if (onGroundIn) {
         if (this.fallDistance > 0.0F) {
            if (blockIn != null) {
               blockIn.onFallenUpon(this.worldObj, pos, this, this.fallDistance);
            } else {
               this.fall(this.fallDistance, 1.0F);
            }

            this.fallDistance = 0.0F;
         }
      } else if (y < 0.0) {
         this.fallDistance = (float)(this.fallDistance - y);
      }
   }

   public float getBrightness(float partialTicks) {
      BlockPos blockPos2 = new BlockPos(this.posX, this.posY + this.getEyeHeight(), this.posZ);
      return this.worldObj.isBlockLoaded(blockPos2) ? this.worldObj.getLightBrightness(blockPos2) : 0.0F;
   }

   @Override
   public int hashCode() {
      return this.entityId;
   }

   protected void setTimeout() {
      if (!this.isImmuneToFire) {
         this.attackEntityFrom(DamageSource.inFire, 1);
      }
   }

   public boolean isEating() {
      return this.getFlag(4);
   }

   public boolean interactFirst(EntityPlayer playerIn) {
      return false;
   }

   public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
      this.prevPosX = this.posX = x;
      this.prevPosY = this.posY = y;
      this.prevPosZ = this.posZ = z;
      this.prevRotationYaw = this.rotationYaw = yaw;
      this.prevRotationPitch = this.rotationPitch = pitch;
      double d2 = this.prevRotationYaw - yaw;
      if (d2 < -180.0) {
         this.prevRotationYaw += 360.0F;
      }

      if (d2 >= 180.0) {
         this.prevRotationYaw -= 360.0F;
      }

      this.setPosition(this.posX, this.posY, this.posZ);
      this.setRotation(yaw, pitch);
   }

   public void addVelocity(double x, double y, double z) {
      this.motionX += x;
      this.motionY += y;
      this.motionZ += z;
      this.isAirBorne = true;
   }

   public UUID getUniqueID() {
      return this.entityUniqueID;
   }

   public void setPosition(double x, double y, double z) {
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      float f2 = this.width / 2.0F;
      float height2 = this.height;
      this.setEntityBoundingBox(new AxisAlignedBB(x - f2, y, z - f2, x + f2, y + height2, z + f2));
   }

   public void addEntityCrashInfo(CrashReportCategory category) {
      category.addCrashSectionCallable("Entity Type", new Callable<String>() {
         public String call() throws Exception {
            return EntityList.getEntityString(Entity.this) + " (" + Entity.this.getClass().getCanonicalName() + ")";
         }
      });
      category.addCrashSection("Entity ID", this.entityId);
      category.addCrashSectionCallable("Entity Name", new Callable<String>() {
         public String call() throws Exception {
            return Entity.this.getName();
         }
      });
      category.addCrashSection("Entity's Exact location", String.format("%.2f, %.2f, %.2f", this.posX, this.posY, this.posZ));
      category.addCrashSection(
         "Entity's Block location",
         CrashReportCategory.getCoordinateInfo(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
      );
      category.addCrashSection("Entity's Momentum", String.format("%.2f, %.2f, %.2f", this.motionX, this.motionY, this.motionZ));
      category.addCrashSectionCallable("Entity's Rider", new Callable<String>() {
         public String call() throws Exception {
            return Entity.this.riddenByEntity.toString();
         }
      });
      category.addCrashSectionCallable("Entity's Vehicle", new Callable<String>() {
         public String call() throws Exception {
            return Entity.this.ridingEntity.toString();
         }
      });
   }

   public float getExplosionResistance(Explosion explosionIn, World worldIn, BlockPos pos, IBlockState blockStateIn) {
      return blockStateIn.getBlock().getExplosionResistance(this);
   }

   public void copyDataFromOld(Entity entityIn) {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      entityIn.writeToNBT(nbtTagCompound);
      this.readFromNBT(nbtTagCompound);
      this.timeUntilPortal = entityIn.timeUntilPortal;
      this.lastPortalPos = entityIn.lastPortalPos;
      this.lastPortalVec = entityIn.lastPortalVec;
      this.teleportDirection = entityIn.teleportDirection;
   }

   public int getBrightnessForRender(float partialTicks) {
      BlockPos blockPos2 = new BlockPos(this.posX, this.posY + this.getEyeHeight(), this.posZ);
      return this.worldObj.isBlockLoaded(blockPos2) ? this.worldObj.getCombinedLight(blockPos2, 0) : 0;
   }

   public double getDistanceSqToCenter(BlockPos pos) {
      return pos.distanceSqToCenter(this.posX, this.posY, this.posZ);
   }

   public void travelToDimension(int dimensionId) {
      if (!this.worldObj.isRemote && !this.isDead) {
         this.worldObj.theProfiler.startSection("changeDimension");
         MinecraftServer minecraftServer = MinecraftServer.getServer();
         int dimension2 = this.dimension;
         WorldServer worldServer = minecraftServer.worldServerForDimension(dimension2);
         WorldServer worldServer2 = minecraftServer.worldServerForDimension(dimensionId);
         this.dimension = dimensionId;
         if (dimension2 == 1 && dimensionId == 1) {
            worldServer2 = minecraftServer.worldServerForDimension(0);
            this.dimension = 0;
         }

         this.worldObj.removeEntity(this);
         this.isDead = false;
         this.worldObj.theProfiler.startSection("reposition");
         minecraftServer.getConfigurationManager().transferEntityToWorld(this, dimension2, worldServer, worldServer2);
         this.worldObj.theProfiler.endStartSection("reloading");
         Entity entity = EntityList.createEntityByName(EntityList.getEntityString(this), worldServer2);
         if (entity != null) {
            entity.copyDataFromOld(this);
            if (dimension2 == 1 && dimensionId == 1) {
               BlockPos blockPos = this.worldObj.getTopSolidOrLiquidBlock(worldServer2.getSpawnPoint());
               entity.moveToBlockPosAndAngles(blockPos, entity.rotationYaw, entity.rotationPitch);
            }

            worldServer2.spawnEntityInWorld(entity);
         }

         this.isDead = true;
         this.worldObj.theProfiler.endSection();
         worldServer.resetUpdateEntityTick();
         worldServer2.resetUpdateEntityTick();
         this.worldObj.theProfiler.endSection();
      }
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean isInRangeToRender3d(double x, double y, double z) {
      double xCoordinate = this.posX - x;
      double yCoordinate = this.posY - y;
      double zCoordinate = this.posZ - z;
      double d2 = xCoordinate * xCoordinate + yCoordinate * yCoordinate + zCoordinate * zCoordinate;
      return this.isInRangeToRenderDist(d2);
   }
}
