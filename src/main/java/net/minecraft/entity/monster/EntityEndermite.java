package net.minecraft.entity.monster;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityEndermite extends EntityMob {
   private int lifetime = 0;
   private boolean playerSpawned = false;

   @Override
   protected boolean isValidLightLevel() {
      return true;
   }

   @Override
   protected String getDeathSound() {
      return "mob.silverfish.kill";
   }

   public boolean isSpawnedByPlayer() {
      return this.playerSpawned;
   }

   public void update() {
      this.playerSpawned = true;
   }

   public EntityEndermite(World worldIn) {
      super(worldIn);
      this.experienceValue = 3;
      this.setSize(0.4F, 0.3F);
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0, false));
      this.tasks.addTask(3, new EntityAIWander(this, 1.0));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.worldObj.isRemote) {
         for (int index = 0; index < 2; index++) {
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.PORTAL,
                  this.posX + (this.rand.nextDouble() - 0.5) * this.width,
                  this.posY + this.rand.nextDouble() * this.height,
                  this.posZ + (this.rand.nextDouble() - 0.5) * this.width,
                  (this.rand.nextDouble() - 0.5) * 2.0,
                  -this.rand.nextDouble(),
                  (this.rand.nextDouble() - 0.5) * 2.0
               );
         }
      } else {
         if (!this.isNoDespawnRequired()) {
            this.lifetime++;
         }

         if (this.lifetime >= 2400) {
            this.setDead();
         }
      }
   }

   @Override
   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(2.0);
   }

   @Override
   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.ARTHROPOD;
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setInteger("Lifetime", this.lifetime);
      tagCompound.setBoolean("PlayerSpawned", this.playerSpawned);
   }

   @Override
   protected String getLivingSound() {
      return "mob.silverfish.say";
   }

   @Override
   public void onUpdate() {
      this.renderYawOffset = this.rotationYaw;
      super.onUpdate();
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.lifetime = tagCompund.getInteger("Lifetime");
      this.playerSpawned = tagCompund.getBoolean("PlayerSpawned");
   }

   @Override
   public float getEyeHeight() {
      return 0.1F;
   }

   @Override
   protected Item getDropItem() {
      return null;
   }

   @Override
   protected boolean canTriggerWalking() {
      return false;
   }

   @Override
   protected String getHurtSound() {
      return "mob.silverfish.hit";
   }

   @Override
   public boolean getCanSpawnHere() {
      return super.getCanSpawnHere() ? this.worldObj.getClosestPlayerToEntity(this, 5.0) == null : false;
   }

   @Override
   protected void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound("mob.silverfish.step", 0.15F, 1.0F);
   }
}
