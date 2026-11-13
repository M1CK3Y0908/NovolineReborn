package net.minecraft.entity.monster;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySnowman extends EntityGolem implements IRangedAttackMob {
   @Override
   public void attackEntityWithRangedAttack(EntityLivingBase target, float f) {
      EntitySnowball entitySnowball = new EntitySnowball(this.worldObj, this);
      double yCoordinate = target.posY + target.getEyeHeight() - 1.1F;
      double xCoordinate = target.posX - this.posX;
      double yCoordinate2 = yCoordinate - entitySnowball.posY;
      double zCoordinate = target.posZ - this.posZ;
      float f2 = MathHelper.sqrt_double(xCoordinate * xCoordinate + zCoordinate * zCoordinate) * 0.2F;
      entitySnowball.setThrowableHeading(xCoordinate, yCoordinate2 + f2, zCoordinate, 1.6F, 12.0F);
      this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
      this.worldObj.spawnEntityInWorld(entitySnowball);
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.worldObj.isRemote) {
         int i = MathHelper.floor_double(this.posX);
         int i2 = MathHelper.floor_double(this.posY);
         int i3 = MathHelper.floor_double(this.posZ);
         if (this.isWet()) {
            this.attackEntityFrom(DamageSource.drown, 1.0F);
         }

         if (this.worldObj.getBiomeGenForCoords(new BlockPos(i, 0, i3)).getFloatTemperature(new BlockPos(i, i2, i3)) > 1.0F) {
            this.attackEntityFrom(DamageSource.onFire, 1.0F);
         }

         for (int i4 = 0; i4 < 4; i4++) {
            i = MathHelper.floor_double(this.posX + (i4 % 2 * 2 - 1) * 0.25F);
            i2 = MathHelper.floor_double(this.posY);
            i3 = MathHelper.floor_double(this.posZ + (i4 / 2 % 2 * 2 - 1) * 0.25F);
            BlockPos blockPos = new BlockPos(i, i2, i3);
            if (this.worldObj.getBlockState(blockPos).getBlock().getMaterial() == Material.air
               && this.worldObj.getBiomeGenForCoords(new BlockPos(i, 0, i3)).getFloatTemperature(blockPos) < 0.8F
               && Blocks.snow_layer.canPlaceBlockAt(this.worldObj, blockPos)) {
               this.worldObj.setBlockState(blockPos, Blocks.snow_layer.getDefaultState());
            }
         }
      }
   }

   @Override
   public float getEyeHeight() {
      return 1.7F;
   }

   @Override
   protected Item getDropItem() {
      return Items.snowball;
   }

   @Override
   protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      int i = this.rand.nextInt(16);

      for (int innerIndex = 0; innerIndex < i; innerIndex++) {
         this.getEntityItem(Items.snowball);
      }
   }

   public EntitySnowman(World worldIn) {
      super(worldIn);
      this.setSize(0.7F, 1.9F);
      ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
      this.tasks.addTask(1, new EntityAIArrowAttack(this, 1.25, 20, 10.0F));
      this.tasks.addTask(2, new EntityAIWander(this, 1.0));
      this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(4, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityLiving.class, 10, true, false, IMob.mobSelector));
   }

   @Override
   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(4.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2F);
   }
}
