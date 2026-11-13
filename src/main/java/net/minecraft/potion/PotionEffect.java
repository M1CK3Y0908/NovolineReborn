package net.minecraft.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PotionEffect {
   private boolean isPotionDurationMax;
   private static final Logger LOGGER = LogManager.getLogger();
   private boolean isSplashPotion;
   private int potionID;
   private int duration;
   private int amplifier;
   private boolean isAmbient;
   private boolean showParticles;

   public NBTTagCompound writeCustomPotionEffectToNBT(NBTTagCompound nbt) {
      nbt.setByte("Id", (byte)this.getPotionID());
      nbt.setByte("Amplifier", (byte)this.getAmplifier());
      nbt.setInteger("Duration", this.getDuration());
      nbt.setBoolean("Ambient", this.getIsAmbient());
      nbt.setBoolean("ShowParticles", this.getIsShowParticles());
      return nbt;
   }

   public void combine(PotionEffect other) {
      if (this.potionID != other.potionID) {
         LOGGER.warn("This method should only be called for matching effects!");
      }

      if (other.amplifier > this.amplifier) {
         this.amplifier = other.amplifier;
         this.duration = other.duration;
      } else if (other.amplifier == this.amplifier && this.duration < other.duration) {
         this.duration = other.duration;
      } else if (!other.isAmbient && this.isAmbient) {
         this.isAmbient = other.isAmbient;
      }

      this.showParticles = other.showParticles;
   }

   public int getPotionID() {
      return this.potionID;
   }

   public boolean onUpdate(EntityLivingBase entityIn) {
      if (this.duration > 0) {
         if (Potion.potionTypes[this.potionID].isReady(this.duration, this.amplifier)) {
            this.performEffect(entityIn);
         }

         this.deincrementDuration();
      }

      return this.duration > 0;
   }

   public void setPotionDurationMax(boolean maxDuration) {
      this.isPotionDurationMax = maxDuration;
   }

   public PotionEffect(int id, int effectDuration, int effectAmplifier) {
      this(id, effectDuration, effectAmplifier, false, true);
   }

   public boolean getIsAmbient() {
      return this.isAmbient;
   }

   @Override
   public boolean equals(Object arg1) {
      return !(arg1 instanceof PotionEffect potionEffect)
         ? false
         : this.potionID == potionEffect.potionID
            && this.amplifier == potionEffect.amplifier
            && this.duration == potionEffect.duration
            && this.isSplashPotion == potionEffect.isSplashPotion
            && this.isAmbient == potionEffect.isAmbient;
   }

   private int deincrementDuration() {
      return --this.duration;
   }

   @Override
   public int hashCode() {
      return this.potionID;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public PotionEffect(int id, int effectDuration, int effectAmplifier, boolean ambient, boolean showParticles) {
      this.potionID = id;
      this.duration = effectDuration;
      this.amplifier = effectAmplifier;
      this.isAmbient = ambient;
      this.showParticles = showParticles;
   }

   public boolean isIsSplashPotion() {
      return this.isSplashPotion;
   }

   public int getDuration() {
      return this.duration;
   }

   public PotionEffect(PotionEffect other) {
      this.potionID = other.potionID;
      this.duration = other.duration;
      this.amplifier = other.amplifier;
      this.isAmbient = other.isAmbient;
      this.showParticles = other.showParticles;
   }

   public boolean getIsPotionDurationMax() {
      return this.isPotionDurationMax;
   }

   public void update() {
      this.isSplashPotion = true;
   }

   @Override
   public String toString() {
      String text;
      if (this.getAmplifier() > 0) {
         text = this.getEffectName() + " x " + (this.getAmplifier() + 1) + ", Duration: " + this.getDuration();
      } else {
         text = this.getEffectName() + ", Duration: " + this.getDuration();
      }

      if (this.isSplashPotion) {
         text = text + ", Splash: true";
      }

      if (!this.showParticles) {
         text = text + ", Particles: false";
      }

      return Potion.potionTypes[this.potionID].isUsable() ? "(" + text + ")" : text;
   }

   public PotionEffect(int id, int effectDuration) {
      this(id, effectDuration, 0);
   }

   public String getEffectName() {
      return Potion.potionTypes[this.potionID].getName();
   }

   public boolean getIsShowParticles() {
      return this.showParticles;
   }

   public static PotionEffect readCustomPotionEffectFromNBT(NBTTagCompound nbt) {
      int byteValue = nbt.getByte("Id");
      if (byteValue >= 0 && byteValue < Potion.potionTypes.length && Potion.potionTypes[byteValue] != null) {
         int byteValue2 = nbt.getByte("Amplifier");
         int integer = nbt.getInteger("Duration");
         boolean conditionMet = nbt.getBoolean("Ambient");
         boolean conditionMet2 = true;
         if (nbt.hasKey("ShowParticles", 1)) {
            conditionMet2 = nbt.getBoolean("ShowParticles");
         }

         return new PotionEffect(byteValue, integer, byteValue2, conditionMet, conditionMet2);
      } else {
         return null;
      }
   }

   public void performEffect(EntityLivingBase entityIn) {
      if (this.duration > 0) {
         Potion.potionTypes[this.potionID].performEffect(entityIn, this.amplifier);
      }
   }
}
