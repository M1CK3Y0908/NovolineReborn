package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityCow extends EntityAnimal {
   @Override
   protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      int i = this.rand.nextInt(3) + this.rand.nextInt(1 + lootingModifier);

      for (int i2 = 0; i2 < i; i2++) {
         this.getEntityItem(Items.leather);
      }

      i = this.rand.nextInt(3) + 1 + this.rand.nextInt(1 + lootingModifier);

      for (int i3 = 0; i3 < i; i3++) {
         if (this.isBurning()) {
            this.getEntityItem(Items.cooked_beef);
         } else {
            this.getEntityItem(Items.beef);
         }
      }
   }

   @Override
   protected String getDeathSound() {
      return "mob.cow.hurt";
   }

   @Override
   protected String getHurtSound() {
      return "mob.cow.hurt";
   }

   @Override
   protected Item getDropItem() {
      return Items.leather;
   }

   @Override
   protected String getLivingSound() {
      return "mob.cow.say";
   }

   @Override
   protected void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound("mob.cow.step", 0.15F, 1.0F);
   }

   @Override
   public boolean interact(EntityPlayer player) {
      ItemStack itemStack = player.inventory.getCurrentItem();
      if (itemStack != null && itemStack.getItem() == Items.bucket && !player.capabilities.isCreativeMode && !this.isChild()) {
         if (itemStack.stackSize-- == 1) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.milk_bucket));
         } else if (!player.inventory.addItemStackToInventory(new ItemStack(Items.milk_bucket))) {
            player.getEntityItem3(new ItemStack(Items.milk_bucket, 1, 0));
         }

         return true;
      } else {
         return super.interact(player);
      }
   }

   @Override
   public float getEyeHeight() {
      return this.height;
   }

   public EntityCow createChild(EntityAgeable ageable) {
      return new EntityCow(this.worldObj);
   }

   @Override
   protected float getSoundVolume() {
      return 0.4F;
   }

   public EntityCow(World worldIn) {
      super(worldIn);
      this.setSize(0.9F, 1.3F);
      ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityAIPanic(this, 2.0));
      this.tasks.addTask(2, new EntityAIMate(this, 1.0));
      this.tasks.addTask(3, new EntityAITempt(this, 1.25, Items.wheat, false));
      this.tasks.addTask(4, new EntityAIFollowParent(this, 1.25));
      this.tasks.addTask(5, new EntityAIWander(this, 1.0));
      this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(7, new EntityAILookIdle(this));
   }

   @Override
   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2F);
   }
}
