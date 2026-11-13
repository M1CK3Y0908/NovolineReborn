package net.minecraft.entity.monster;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityMagmaCube extends EntitySlime {
   @Override
   protected void alterSquishAmount() {
      this.squishAmount *= 0.9F;
   }

   @Override
   protected String getJumpSound() {
      return this.getSlimeSize() > 1 ? "mob.magmacube.big" : "mob.magmacube.small";
   }

   @Override
   protected void handleJumpLava() {
      this.motionY = 0.22F + this.getSlimeSize() * 0.05F;
      this.isAirBorne = true;
   }

   @Override
   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2F);
   }

   @Override
   protected boolean makesSoundOnLand() {
      return true;
   }

   @Override
   protected int getAttackStrength() {
      return super.getAttackStrength() + 2;
   }

   @Override
   public boolean isNotColliding() {
      return this.worldObj.checkNoEntityCollision(this.getEntityBoundingBox(), this)
         && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty()
         && !this.worldObj.isAnyLiquid(this.getEntityBoundingBox());
   }

   @Override
   protected EntitySlime createInstance() {
      return new EntityMagmaCube(this.worldObj);
   }

   @Override
   public void fall(float distance, float damageMultiplier) {
   }

   @Override
   public boolean isBurning() {
      return false;
   }

   @Override
   public boolean getCanSpawnHere() {
      return this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL;
   }

   @Override
   protected boolean canDamagePlayer() {
      return true;
   }

   public EntityMagmaCube(World worldIn) {
      super(worldIn);
      this.isImmuneToFire = true;
   }

   @Override
   protected EnumParticleTypes getParticleType() {
      return EnumParticleTypes.FLAME;
   }

   @Override
   protected void jump() {
      this.motionY = 0.42F + this.getSlimeSize() * 0.1F;
      this.isAirBorne = true;
   }

   @Override
   protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      Item item = this.getDropItem();
      if (item != null && this.getSlimeSize() > 1) {
         int i = this.rand.nextInt(4) - 2;
         if (lootingModifier > 0) {
            i += this.rand.nextInt(lootingModifier + 1);
         }

         for (int innerIndex = 0; innerIndex < i; innerIndex++) {
            this.getEntityItem(item);
         }
      }
   }

   @Override
   public int getBrightnessForRender(float partialTicks) {
      return 15728880;
   }

   @Override
   protected Item getDropItem() {
      return Items.magma_cream;
   }

   @Override
   protected int getJumpDelay() {
      return super.getJumpDelay() * 4;
   }

   @Override
   public int getTotalArmorValue() {
      return this.getSlimeSize() * 3;
   }

   @Override
   public float getBrightness(float partialTicks) {
      return 1.0F;
   }
}
