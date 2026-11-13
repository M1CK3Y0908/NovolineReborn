package net.minecraft.entity.item;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityXPOrb extends Entity {
   private EntityPlayer closestPlayer;
   private int xpTargetColor;
   public int delayBeforeCanPickup;
   public int xpOrbAge;
   public int xpColor;
   private int xpOrbHealth = 5;
   private int xpValue;

   @Override
   public boolean canAttackWithItem() {
      return false;
   }

   @Override
   public void onCollideWithPlayer(EntityPlayer entityIn) {
      if (!this.worldObj.isRemote && this.delayBeforeCanPickup == 0 && entityIn.xpCooldown == 0) {
         entityIn.xpCooldown = 2;
         this.worldObj.playSoundAtEntity(entityIn, "random.orb", 0.1F, 0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
         entityIn.onItemPickup(this, 1);
         entityIn.addExperience(this.xpValue);
         this.setDead();
      }
   }

   public int getTextureByXP() {
      return this.xpValue >= 2477
         ? 10
         : (
            this.xpValue >= 1237
               ? 9
               : (
                  this.xpValue >= 617
                     ? 8
                     : (
                        this.xpValue >= 307
                           ? 7
                           : (
                              this.xpValue >= 149
                                 ? 6
                                 : (
                                    this.xpValue >= 73
                                       ? 5
                                       : (this.xpValue >= 37 ? 4 : (this.xpValue >= 17 ? 3 : (this.xpValue >= 7 ? 2 : (this.xpValue >= 3 ? 1 : 0))))
                                 )
                           )
                     )
               )
         );
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (this.delayBeforeCanPickup > 0) {
         this.delayBeforeCanPickup--;
      }

      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.motionY -= 0.03F;
      if (this.worldObj.getBlockState(new BlockPos(this)).getBlock().getMaterial() == Material.lava) {
         this.motionY = 0.2F;
         this.motionX = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
         this.motionZ = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
         this.playSound("random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
      }

      this.pushOutOfBlocks(this.posX, (this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0, this.posZ);
      double d = 8.0;
      if (this.xpTargetColor < this.xpColor - 20 + this.getEntityId() % 100) {
         if (this.closestPlayer == null || this.closestPlayer.getDistanceSqToEntity(this) > d * d) {
            this.closestPlayer = this.worldObj.getClosestPlayerToEntity(this, d);
         }

         this.xpTargetColor = this.xpColor;
      }

      if (this.closestPlayer != null && this.closestPlayer.isSpectator()) {
         this.closestPlayer = null;
      }

      if (this.closestPlayer != null) {
         double d2 = (this.closestPlayer.posX - this.posX) / d;
         double d3 = (this.closestPlayer.posY + this.closestPlayer.getEyeHeight() - this.posY) / d;
         double d4 = (this.closestPlayer.posZ - this.posZ) / d;
         double d5 = Math.sqrt(d2 * d2 + d3 * d3 + d4 * d4);
         double d6 = 1.0 - d5;
         if (d6 > 0.0) {
            d6 *= d6;
            this.motionX += d2 / d5 * d6 * 0.1;
            this.motionY += d3 / d5 * d6 * 0.1;
            this.motionZ += d4 / d5 * d6 * 0.1;
         }
      }

      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      float f = 0.98F;
      if (this.onGround) {
         f = this.worldObj
               .getBlockState(
                  new BlockPos(
                     MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ)
                  )
               )
               .getBlock()
               .slipperiness
            * 0.98F;
      }

      this.motionX *= f;
      this.motionY *= 0.98F;
      this.motionZ *= f;
      if (this.onGround) {
         this.motionY *= -0.9F;
      }

      this.xpColor++;
      this.xpOrbAge++;
      if (this.xpOrbAge >= 6000) {
         this.setDead();
      }
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      this.setBeenAttacked();
      this.xpOrbHealth = (int)(this.xpOrbHealth - amount);
      if (this.xpOrbHealth <= 0) {
         this.setDead();
      }

      return false;
   }

   @Override
   public boolean handleWaterMovement() {
      return this.worldObj.handleMaterialAcceleration(this.getEntityBoundingBox(), Material.water, this);
   }

   @Override
   protected boolean canTriggerWalking() {
      return false;
   }

   @Override
   public int getBrightnessForRender(float partialTicks) {
      float f = 0.5F;
      f = MathHelper.clamp_float(f, 0.0F, 1.0F);
      int brightnessForRender = super.getBrightnessForRender(partialTicks);
      int i = brightnessForRender & 0xFF;
      int i2 = brightnessForRender >> 16 & 0xFF;
      i += (int)(f * 15.0F * 16.0F);
      if (i > 240) {
         i = 240;
      }

      return i | i2 << 16;
   }

   public EntityXPOrb(World worldIn) {
      super(worldIn);
      this.setSize(0.25F, 0.25F);
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      tagCompound.setShort("Health", (byte)this.xpOrbHealth);
      tagCompound.setShort("Age", (short)this.xpOrbAge);
      tagCompound.setShort("Value", (short)this.xpValue);
   }

   public static int getXPSplit(int expValue) {
      return expValue >= 2477
         ? 2477
         : (
            expValue >= 1237
               ? 1237
               : (
                  expValue >= 617
                     ? 617
                     : (
                        expValue >= 307
                           ? 307
                           : (
                              expValue >= 149
                                 ? 149
                                 : (expValue >= 73 ? 73 : (expValue >= 37 ? 37 : (expValue >= 17 ? 17 : (expValue >= 7 ? 7 : (expValue >= 3 ? 3 : 1)))))
                           )
                     )
               )
         );
   }

   @Override
   protected void entityInit() {
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      this.xpOrbHealth = tagCompund.getShort("Health") & 255;
      this.xpOrbAge = tagCompund.getShort("Age");
      this.xpValue = tagCompund.getShort("Value");
   }

   public EntityXPOrb(World worldIn, double x, double y, double z, int expValue) {
      super(worldIn);
      this.setSize(0.5F, 0.5F);
      this.setPosition(x, y, z);
      this.rotationYaw = (float)(Math.random() * 360.0);
      this.motionX = (float)(Math.random() * 0.2F - 0.1F) * 2.0F;
      this.motionY = (float)(Math.random() * 0.2) * 2.0F;
      this.motionZ = (float)(Math.random() * 0.2F - 0.1F) * 2.0F;
      this.xpValue = expValue;
   }

   public int getXpValue() {
      return this.xpValue;
   }

   @Override
   protected void setTimeout() {
      this.attackEntityFrom(DamageSource.inFire, 1);
   }
}
