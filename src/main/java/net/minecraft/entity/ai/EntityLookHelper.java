package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class EntityLookHelper {
   private boolean isLooking;
   private double posZ;
   private EntityLiving entity;
   private float deltaLookPitch;
   private double posY;
   private double posX;
   private float deltaLookYaw;

   public EntityLookHelper(EntityLiving entitylivingIn) {
      this.entity = entitylivingIn;
   }

   public double getLookPosX() {
      return this.posX;
   }

   private float updateRotation(float f, float f2, float f3) {
      float f4 = MathHelper.wrapAngleTo180_float(f2 - f);
      if (f4 > f3) {
         f4 = f3;
      }

      if (f4 < -f3) {
         f4 = -f3;
      }

      return f + f4;
   }

   public void setLookPosition(double x, double y, double z, float deltaYaw, float deltaPitch) {
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      this.deltaLookYaw = deltaYaw;
      this.deltaLookPitch = deltaPitch;
      this.isLooking = true;
   }

   public boolean getIsLooking() {
      return this.isLooking;
   }

   public void onUpdateLook() {
      this.entity.rotationPitch = 0.0F;
      if (this.isLooking) {
         this.isLooking = false;
         double xCoordinate = this.posX - this.entity.posX;
         double yCoordinate = this.posY - (this.entity.posY + this.entity.getEyeHeight());
         double zCoordinate = this.posZ - this.entity.posZ;
         double d = MathHelper.sqrt_double(xCoordinate * xCoordinate + zCoordinate * zCoordinate);
         float f = (float)(MathHelper.atan2(zCoordinate, xCoordinate) * 180.0 / Math.PI) - 90.0F;
         float f2 = (float)(-(MathHelper.atan2(yCoordinate, d) * 180.0 / Math.PI));
         this.entity.rotationPitch = this.updateRotation(this.entity.rotationPitch, f2, this.deltaLookPitch);
         this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, f, this.deltaLookYaw);
      } else {
         this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, this.entity.renderYawOffset, 10.0F);
      }

      float f3 = MathHelper.wrapAngleTo180_float(this.entity.rotationYawHead - this.entity.renderYawOffset);
      if (!this.entity.getNavigator().noPath()) {
         if (f3 < -75.0F) {
            this.entity.rotationYawHead = this.entity.renderYawOffset - 75.0F;
         }

         if (f3 > 75.0F) {
            this.entity.rotationYawHead = this.entity.renderYawOffset + 75.0F;
         }
      }
   }

   public double getLookPosY() {
      return this.posY;
   }

   public double getLookPosZ() {
      return this.posZ;
   }

   public void setLookPositionWithEntity(Entity entityIn, float deltaYaw, float deltaPitch) {
      this.posX = entityIn.posX;
      if (entityIn instanceof EntityLivingBase) {
         this.posY = entityIn.posY + entityIn.getEyeHeight();
      } else {
         this.posY = (entityIn.getEntityBoundingBox().minY + entityIn.getEntityBoundingBox().maxY) / 2.0;
      }

      this.posZ = entityIn.posZ;
      this.deltaLookYaw = deltaYaw;
      this.deltaLookPitch = deltaPitch;
      this.isLooking = true;
   }
}
