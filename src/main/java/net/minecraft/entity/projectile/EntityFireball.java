package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public abstract class EntityFireball extends Entity {
   private int ticksInAir;
   private int ticksAlive;
   public double accelerationZ;
   private Block inTile;
   public double accelerationX;
   private int xTile = -1;
   public double accelerationY;
   private int yTile = -1;
   private boolean inGround;
   private int zTile = -1;
   public EntityLivingBase shootingEntity;

   @Override
   protected void entityInit() {
   }

   public EntityFireball(World worldIn) {
      super(worldIn);
      this.setSize(1.0F, 1.0F);
   }

   public EntityFireball(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
      super(worldIn);
      this.setSize(1.0F, 1.0F);
      this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
      this.setPosition(x, y, z);
      double d = MathHelper.sqrt_double(accelX * accelX + accelY * accelY + accelZ * accelZ);
      this.accelerationX = accelX / d * 0.1;
      this.accelerationY = accelY / d * 0.1;
      this.accelerationZ = accelZ / d * 0.1;
   }

   public EntityFireball(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
      super(worldIn);
      this.shootingEntity = shooter;
      this.setSize(1.0F, 1.0F);
      this.setLocationAndAngles(shooter.posX, shooter.posY, shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
      this.setPosition(this.posX, this.posY, this.posZ);
      this.motionX = this.motionY = this.motionZ = 0.0;
      accelX += this.rand.nextGaussian() * 0.4;
      accelY += this.rand.nextGaussian() * 0.4;
      accelZ += this.rand.nextGaussian() * 0.4;
      double d = MathHelper.sqrt_double(accelX * accelX + accelY * accelY + accelZ * accelZ);
      this.accelerationX = accelX / d * 0.1;
      this.accelerationY = accelY / d * 0.1;
      this.accelerationZ = accelZ / d * 0.1;
   }

   protected abstract void onImpact(MovingObjectPosition movingObjectPosition);

   @Override
   public void onUpdate() {
      if (this.worldObj.isRemote || (this.shootingEntity == null || !this.shootingEntity.isDead) && this.worldObj.isBlockLoaded(new BlockPos(this))) {
         super.onUpdate();
         this.setFire(1);
         if (this.inGround) {
            if (this.worldObj.getBlockState(new BlockPos(this.xTile, this.yTile, this.zTile)).getBlock() == this.inTile) {
               this.ticksAlive++;
               if (this.ticksAlive == 600) {
                  this.setDead();
               }

               return;
            }

            this.inGround = false;
            this.motionX = this.motionX * (this.rand.nextFloat() * 0.2F);
            this.motionY = this.motionY * (this.rand.nextFloat() * 0.2F);
            this.motionZ = this.motionZ * (this.rand.nextFloat() * 0.2F);
            this.ticksAlive = 0;
            this.ticksInAir = 0;
         } else {
            this.ticksInAir++;
         }

         Vec3 vec32 = new Vec3(this.posX, this.posY, this.posZ);
         Vec3 vec33 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         MovingObjectPosition movingObjectPosition = this.worldObj.rayTraceBlocks(vec32, vec33);
         vec32 = new Vec3(this.posX, this.posY, this.posZ);
         vec33 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         if (movingObjectPosition != null) {
            vec33 = new Vec3(movingObjectPosition.hitVec.xCoord, movingObjectPosition.hitVec.yCoord, movingObjectPosition.hitVec.zCoord);
         }

         Entity entity2 = null;
         List entitiesWithinAABBExcludingEntity = this.worldObj
            .getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
         double d = 0.0;

         for (int i = 0; i < entitiesWithinAABBExcludingEntity.size(); i++) {
            Entity entity3 = (Entity)entitiesWithinAABBExcludingEntity.get(i);
            if (entity3.canBeCollidedWith() && (!entity3.isEntityEqual(this.shootingEntity) || this.ticksInAir >= 25)) {
               float f = 0.3F;
               AxisAlignedBB axisAlignedBB = entity3.getEntityBoundingBox().expand(f, f, f);
               MovingObjectPosition movingObjectPosition2 = axisAlignedBB.calculateIntercept(vec32, vec33);
               if (movingObjectPosition2 != null) {
                  double d2 = vec32.squareDistanceTo(movingObjectPosition2.hitVec);
                  if (d2 < d || d == 0.0) {
                     entity2 = entity3;
                     d = d2;
                  }
               }
            }
         }

         if (entity2 != null) {
            movingObjectPosition = new MovingObjectPosition(entity2);
         }

         if (movingObjectPosition != null) {
            this.onImpact(movingObjectPosition);
         }

         this.posX = this.posX + this.motionX;
         this.posY = this.posY + this.motionY;
         this.posZ = this.posZ + this.motionZ;
         float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.rotationYaw = (float)(MathHelper.atan2(this.motionZ, this.motionX) * 180.0 / Math.PI) + 90.0F;
         this.rotationPitch = (float)(MathHelper.atan2(f2, this.motionY) * 180.0 / Math.PI) - 90.0F;

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
         float motionFactor = this.getMotionFactor();
         if (this.isInWater()) {
            for (int i2 = 0; i2 < 4; i2++) {
               float f3 = 0.25F;
               this.worldObj
                  .spawnParticle(
                     EnumParticleTypes.WATER_BUBBLE,
                     this.posX - this.motionX * f3,
                     this.posY - this.motionY * f3,
                     this.posZ - this.motionZ * f3,
                     this.motionX,
                     this.motionY,
                     this.motionZ
                  );
            }

            motionFactor = 0.8F;
         }

         this.motionX = this.motionX + this.accelerationX;
         this.motionY = this.motionY + this.accelerationY;
         this.motionZ = this.motionZ + this.accelerationZ;
         this.motionX *= motionFactor;
         this.motionY *= motionFactor;
         this.motionZ *= motionFactor;
         this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5, this.posZ, 0.0, 0.0, 0.0);
         this.setPosition(this.posX, this.posY, this.posZ);
      } else {
         this.setDead();
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      tagCompound.setShort("xTile", (short)this.xTile);
      tagCompound.setShort("yTile", (short)this.yTile);
      tagCompound.setShort("zTile", (short)this.zTile);
      ResourceLocation resourceLocation = Block.blockRegistry.getNameForObject(this.inTile);
      tagCompound.setString("inTile", resourceLocation == null ? "" : resourceLocation.toString());
      tagCompound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
      tagCompound.setTag("direction", this.newDoubleNBTList(this.motionX, this.motionY, this.motionZ));
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      this.setBeenAttacked();
      if (source.getEntity() != null) {
         Vec3 vec3 = source.getEntity().getLookVec();
         if (vec3 != null) {
            this.motionX = vec3.xCoord;
            this.motionY = vec3.yCoord;
            this.motionZ = vec3.zCoord;
            this.accelerationX = this.motionX * 0.1;
            this.accelerationY = this.motionY * 0.1;
            this.accelerationZ = this.motionZ * 0.1;
         }

         if (source.getEntity() instanceof EntityLivingBase) {
            this.shootingEntity = (EntityLivingBase)source.getEntity();
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean canBeCollidedWith() {
      return true;
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

      this.inGround = tagCompund.getByte("inGround") == 1;
      if (tagCompund.hasKey("direction", 9)) {
         NBTTagList nBTTagList = tagCompund.getTagList("direction", 6);
         this.motionX = nBTTagList.getDoubleAt(0);
         this.motionY = nBTTagList.getDoubleAt(1);
         this.motionZ = nBTTagList.getDoubleAt(2);
      } else {
         this.setDead();
      }
   }

   @Override
   public int getBrightnessForRender(float partialTicks) {
      return 15728880;
   }

   protected float getMotionFactor() {
      return 0.95F;
   }

   @Override
   public float getCollisionBorderSize() {
      return 1.0F;
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
}
