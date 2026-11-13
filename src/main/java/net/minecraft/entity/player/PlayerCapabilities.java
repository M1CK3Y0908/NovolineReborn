package net.minecraft.entity.player;

import net.minecraft.nbt.NBTTagCompound;

public class PlayerCapabilities {
   public boolean allowEdit = true;
   public float flySpeed = 0.05F;
   public float walkSpeed = 0.1F;
   public boolean disableDamage;
   public boolean isFlying;
   public boolean allowFlying;
   public boolean isCreativeMode;

   public void readCapabilitiesFromNBT(NBTTagCompound tagCompound) {
      if (tagCompound.hasKey("abilities", 10)) {
         NBTTagCompound nBTTagCompound = tagCompound.getCompoundTag("abilities");
         this.disableDamage = nBTTagCompound.getBoolean("invulnerable");
         this.isFlying = nBTTagCompound.getBoolean("flying");
         this.allowFlying = nBTTagCompound.getBoolean("mayfly");
         this.isCreativeMode = nBTTagCompound.getBoolean("instabuild");
         if (nBTTagCompound.hasKey("flySpeed", 99)) {
            this.flySpeed = nBTTagCompound.getFloat("flySpeed");
            this.walkSpeed = nBTTagCompound.getFloat("walkSpeed");
         }

         if (nBTTagCompound.hasKey("mayBuild", 1)) {
            this.allowEdit = nBTTagCompound.getBoolean("mayBuild");
         }
      }
   }

   public float getWalkSpeed() {
      return this.walkSpeed;
   }

   public void setFlySpeed(float speed) {
      this.flySpeed = speed;
   }

   public float getFlySpeed() {
      return this.flySpeed;
   }

   public void writeCapabilitiesToNBT(NBTTagCompound tagCompound) {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      nbtTagCompound.setBoolean("invulnerable", this.disableDamage);
      nbtTagCompound.setBoolean("flying", this.isFlying);
      nbtTagCompound.setBoolean("mayfly", this.allowFlying);
      nbtTagCompound.setBoolean("instabuild", this.isCreativeMode);
      nbtTagCompound.setBoolean("mayBuild", this.allowEdit);
      nbtTagCompound.setFloat("flySpeed", this.flySpeed);
      nbtTagCompound.setFloat("walkSpeed", this.walkSpeed);
      tagCompound.setTag("abilities", nbtTagCompound);
   }

   public void setPlayerWalkSpeed(float speed) {
      this.walkSpeed = speed;
   }
}
