package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.BlockSilverfish.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntitySilverfish extends EntityMob {
   private EntitySilverfish.AISummonSilverfish summonSilverfish;

   @Override
   protected void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound("mob.silverfish.step", 0.15F, 1.0F);
   }

   @Override
   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0);
   }

   @Override
   public float getBlockPathWeight(BlockPos pos) {
      return this.worldObj.getBlockState(pos.down()).getBlock() == Blocks.stone ? 10.0F : super.getBlockPathWeight(pos);
   }

   @Override
   protected String getHurtSound() {
      return "mob.silverfish.hit";
   }

   @Override
   protected String getDeathSound() {
      return "mob.silverfish.kill";
   }

   @Override
   protected boolean isValidLightLevel() {
      return true;
   }

   @Override
   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.ARTHROPOD;
   }

   @Override
   public void onUpdate() {
      this.renderYawOffset = this.rotationYaw;
      super.onUpdate();
   }

   public EntitySilverfish(World worldIn) {
      super(worldIn);
      this.setSize(0.4F, 0.3F);
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(3, this.summonSilverfish = new EntitySilverfish.AISummonSilverfish(this));
      this.tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0, false));
      this.tasks.addTask(5, new EntitySilverfish.AIHideInStone(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
   }

   @Override
   public float getEyeHeight() {
      return 0.1F;
   }

   @Override
   public boolean getCanSpawnHere() {
      return super.getCanSpawnHere() ? this.worldObj.getClosestPlayerToEntity(this, 5.0) == null : false;
   }

   @Override
   public double getYOffset() {
      return 0.2;
   }

   @Override
   protected String getLivingSound() {
      return "mob.silverfish.say";
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
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if (source instanceof EntityDamageSource || source == DamageSource.magic) {
         this.summonSilverfish.startSummoningSilverfish();
      }

      return super.attackEntityFrom(source, amount);
   }

   static class AIHideInStone extends EntityAIWander {
      private final EntitySilverfish silverfish;
      private boolean hidingInBlock;
      private EnumFacing facing;

      @Override
      public void startExecuting() {
         if (!this.hidingInBlock) {
            super.startExecuting();
         } else {
            World world = this.silverfish.worldObj;
            BlockPos blockPos = new BlockPos(this.silverfish.posX, this.silverfish.posY + 0.5, this.silverfish.posZ).offset(this.facing);
            IBlockState blockState = world.getBlockState(blockPos);
            if (BlockSilverfish.canContainSilverfish(blockState)) {
               world.setBlockState(blockPos, Blocks.monster_egg.getDefaultState().withProperty(BlockSilverfish.VARIANT, EnumType.forModelBlock(blockState)), 3);
               this.silverfish.spawnExplosionParticle();
               this.silverfish.setDead();
            }
         }
      }

      public AIHideInStone(EntitySilverfish silverfishIn) {
         super(silverfishIn, 1.0, 10);
         this.silverfish = silverfishIn;
         this.setMutexBits(1);
      }

      @Override
      public boolean shouldExecute() {
         if (this.silverfish.getAttackTarget() != null) {
            return false;
         }

         if (!this.silverfish.getNavigator().noPath()) {
            return false;
         }

         Random rng = this.silverfish.getRNG();
         if (rng.nextInt(10) == 0) {
            this.facing = EnumFacing.random(rng);
            BlockPos blockPos3 = new BlockPos(this.silverfish.posX, this.silverfish.posY + 0.5, this.silverfish.posZ).offset(this.facing);
            if (BlockSilverfish.canContainSilverfish(this.silverfish.worldObj.getBlockState(blockPos3))) {
               this.hidingInBlock = true;
               return true;
            }
         }

         this.hidingInBlock = false;
         return super.shouldExecute();
      }

      @Override
      public boolean continueExecuting() {
         return this.hidingInBlock ? false : super.continueExecuting();
      }
   }

   static class AISummonSilverfish extends EntityAIBase {
      private int summonCooldown;
      private EntitySilverfish silverfish;

      @Override
      public boolean shouldExecute() {
         return this.summonCooldown > 0;
      }

      @Override
      public void updateTask() {
         this.summonCooldown--;
         if (this.summonCooldown <= 0) {
            World world = this.silverfish.worldObj;
            Random random = this.silverfish.getRNG();
            BlockPos blockPos3 = new BlockPos(this.silverfish);

            for (int index = 0; index <= 5 && index >= -5; index = index <= 0 ? 1 - index : 0 - index) {
               for (int innerIndex = 0; innerIndex <= 10 && innerIndex >= -10; innerIndex = innerIndex <= 0 ? 1 - innerIndex : 0 - innerIndex) {
                  for (int nestedIndex = 0; nestedIndex <= 10 && nestedIndex >= -10; nestedIndex = nestedIndex <= 0 ? 1 - nestedIndex : 0 - nestedIndex) {
                     BlockPos blockPos2 = blockPos3.add(innerIndex, index, nestedIndex);
                     IBlockState blockState = world.getBlockState(blockPos2);
                     if (blockState.getBlock() == Blocks.monster_egg) {
                        if (world.getGameRules().getBoolean("mobGriefing")) {
                           world.destroyBlock(blockPos2, true);
                        } else {
                           world.setBlockState(blockPos2, blockState.getValue(BlockSilverfish.VARIANT).getModelBlock(), 3);
                        }

                        if (random.nextBoolean()) {
                           return;
                        }
                     }
                  }
               }
            }
         }
      }

      public void startSummoningSilverfish() {
         if (this.summonCooldown == 0) {
            this.summonCooldown = 20;
         }
      }

      public AISummonSilverfish(EntitySilverfish silverfishIn) {
         this.silverfish = silverfishIn;
      }
   }
}
