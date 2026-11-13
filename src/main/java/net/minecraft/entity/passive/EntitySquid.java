package net.minecraft.entity.passive;

import net.minecraft.block.material.Material;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySquid extends EntityWaterMob {
   public float squidPitch;
   public float lastTentacleAngle;
   private float tentacleMoveAmplifier;
   private float randomMotionVecY;
   public float prevSquidPitch;
   private float randomMotionVecZ;
   public float squidYaw;
   public float tentacleAngle;
   public float squidRotation;
   public float prevSquidRotation;
   private float randomMotionVecX;
   private float rotationVelocity;
   private float randomMotionSpeed;
   public float prevSquidYaw;

   @Override
   protected float getSoundVolume() {
      return 0.4F;
   }

   @Override
   protected String getHurtSound() {
      return null;
   }

   @Override
   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0);
   }

   @Override
   protected String getDeathSound() {
      return null;
   }

   @Override
   public void handleStatusUpdate(byte id) {
      if (id == 19) {
         this.squidRotation = 0.0F;
      } else {
         super.handleStatusUpdate(id);
      }
   }

   public void setRandomMotionVec(float randomMotionVecXIn, float randomMotionVecYIn, float randomMotionVecZIn) {
      this.randomMotionVecX = randomMotionVecXIn;
      this.randomMotionVecY = randomMotionVecYIn;
      this.randomMotionVecZ = randomMotionVecZIn;
   }

   @Override
   protected String getLivingSound() {
      return null;
   }

   @Override
   protected Item getDropItem() {
      return null;
   }

   @Override
   public boolean getCanSpawnHere() {
      return this.posY > 45.0 && this.posY < this.worldObj.getSeaLevel() && super.getCanSpawnHere();
   }

   public EntitySquid(World worldIn) {
      super(worldIn);
      this.setSize(0.95F, 0.95F);
      this.rand.setSeed(1 + this.getEntityId());
      this.rotationVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
      this.tasks.addTask(0, new EntitySquid.AIMoveRandom(this));
   }

   @Override
   protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      int i = this.rand.nextInt(3 + lootingModifier) + 1;

      for (int innerIndex = 0; innerIndex < i; innerIndex++) {
         this.entityDropItem(new ItemStack(Items.dye, 1, EnumDyeColor.BLACK.getDyeDamage()), 0.0F);
      }
   }

   @Override
   public boolean isInWater() {
      return this.worldObj.handleMaterialAcceleration(this.getEntityBoundingBox().expand(0.0, -0.6F, 0.0), Material.water, this);
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.prevSquidPitch = this.squidPitch;
      this.prevSquidYaw = this.squidYaw;
      this.prevSquidRotation = this.squidRotation;
      this.lastTentacleAngle = this.tentacleAngle;
      this.squidRotation = this.squidRotation + this.rotationVelocity;
      if (this.squidRotation > Math.PI * 2) {
         if (this.worldObj.isRemote) {
            this.squidRotation = (float) (Math.PI * 2);
         } else {
            this.squidRotation = (float)(this.squidRotation - (Math.PI * 2));
            if (this.rand.nextInt(10) == 0) {
               this.rotationVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
            }

            this.worldObj.setEntityState(this, (byte)19);
         }
      }

      if (this.inWater) {
         if (this.squidRotation < (float) Math.PI) {
            float f = this.squidRotation / (float) Math.PI;
            this.tentacleAngle = MathHelper.sin(f * f * (float) Math.PI) * (float) Math.PI * 0.25F;
            if (f > 0.75) {
               this.randomMotionSpeed = 1.0F;
               this.tentacleMoveAmplifier = 1.0F;
            } else {
               this.tentacleMoveAmplifier *= 0.8F;
            }
         } else {
            this.tentacleAngle = 0.0F;
            this.randomMotionSpeed *= 0.9F;
            this.tentacleMoveAmplifier *= 0.99F;
         }

         if (!this.worldObj.isRemote) {
            this.motionX = this.randomMotionVecX * this.randomMotionSpeed;
            this.motionY = this.randomMotionVecY * this.randomMotionSpeed;
            this.motionZ = this.randomMotionVecZ * this.randomMotionSpeed;
         }

         float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.renderYawOffset = this.renderYawOffset
            + (-((float)MathHelper.atan2(this.motionX, this.motionZ)) * 180.0F / (float) Math.PI - this.renderYawOffset) * 0.1F;
         this.rotationYaw = this.renderYawOffset;
         this.squidYaw = (float)(this.squidYaw + Math.PI * this.tentacleMoveAmplifier * 1.5);
         this.squidPitch = this.squidPitch + (-((float)MathHelper.atan2(f2, this.motionY)) * 180.0F / (float) Math.PI - this.squidPitch) * 0.1F;
      } else {
         this.tentacleAngle = MathHelper.abs(MathHelper.sin(this.squidRotation)) * (float) Math.PI * 0.25F;
         if (!this.worldObj.isRemote) {
            this.motionX = 0.0;
            this.motionY -= 0.08;
            this.motionY *= 0.98F;
            this.motionZ = 0.0;
         }

         this.squidPitch = (float)(this.squidPitch + (-90.0F - this.squidPitch) * 0.02);
      }
   }

   @Override
   public float getEyeHeight() {
      return this.height * 0.5F;
   }

   @Override
   public void moveEntityWithHeading(float strafe, float forward) {
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
   }

   @Override
   protected boolean canTriggerWalking() {
      return false;
   }

   public boolean hasRandomMotion() {
      return this.randomMotionVecX != 0.0F || this.randomMotionVecY != 0.0F || this.randomMotionVecZ != 0.0F;
   }

   static class AIMoveRandom extends EntityAIBase {
      private EntitySquid squid;

      @Override
      public boolean shouldExecute() {
         return true;
      }

      public AIMoveRandom(EntitySquid entitySquid) {
         this.squid = entitySquid;
      }

      @Override
      public void updateTask() {
         if (this.squid.getAge() > 100) {
            this.squid.setRandomMotionVec(0.0F, 0.0F, 0.0F);
         } else if (this.squid.getRNG().nextInt(50) == 0 || !this.squid.inWater || !this.squid.hasRandomMotion()) {
            float f = this.squid.getRNG().nextFloat() * (float) Math.PI * 2.0F;
            float f2 = MathHelper.cos(f) * 0.2F;
            float f3 = -0.1F + this.squid.getRNG().nextFloat() * 0.2F;
            float f4 = MathHelper.sin(f) * 0.2F;
            this.squid.setRandomMotionVec(f2, f3, f4);
         }
      }
   }
}
