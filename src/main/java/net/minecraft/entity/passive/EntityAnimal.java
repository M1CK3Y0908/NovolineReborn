package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityAnimal extends EntityAgeable implements IAnimals {
   private EntityPlayer playerInLove;
   protected Block spawnableBlock = Blocks.grass;
   private int inLove;

   public boolean isInLove() {
      return this.inLove > 0;
   }

   @Override
   public boolean interact(EntityPlayer player) {
      ItemStack itemStack = player.inventory.getCurrentItem();
      if (itemStack != null) {
         if (this.isBreedingItem(itemStack) && this.getGrowingAge() == 0 && this.inLove <= 0) {
            this.consumeItemFromStack(player, itemStack);
            this.setInLove(player);
            return true;
         }

         if (this.isChild() && this.isBreedingItem(itemStack)) {
            this.consumeItemFromStack(player, itemStack);
            this.addGrowth((int)(-this.getGrowingAge() / 20 * 0.1F), true);
            return true;
         }
      }

      return super.interact(player);
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      this.inLove = 0;
      return super.attackEntityFrom(source, amount);
   }

   public void resetInLove() {
      this.inLove = 0;
   }

   @Override
   protected int getExperiencePoints(EntityPlayer player) {
      return 1 + this.worldObj.rand.nextInt(3);
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setInteger("InLove", this.inLove);
   }

   @Override
   protected boolean canDespawn() {
      return false;
   }

   @Override
   public float getBlockPathWeight(BlockPos pos) {
      return this.worldObj.getBlockState(pos.down()).getBlock() == Blocks.grass ? 10.0F : this.worldObj.getLightBrightness(pos) - 0.5F;
   }

   public boolean isBreedingItem(ItemStack stack) {
      return stack == null ? false : stack.getItem() == Items.wheat;
   }

   @Override
   protected void updateAITasks() {
      if (this.getGrowingAge() != 0) {
         this.inLove = 0;
      }

      super.updateAITasks();
   }

   @Override
   public int getTalkInterval() {
      return 120;
   }

   @Override
   public boolean getCanSpawnHere() {
      int i = MathHelper.floor_double(this.posX);
      int i2 = MathHelper.floor_double(this.getEntityBoundingBox().minY);
      int i3 = MathHelper.floor_double(this.posZ);
      BlockPos blockPos = new BlockPos(i, i2, i3);
      return this.worldObj.getBlockState(blockPos.down()).getBlock() == this.spawnableBlock && this.worldObj.getLight(blockPos) > 8 && super.getCanSpawnHere();
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.inLove = tagCompund.getInteger("InLove");
   }

   protected void consumeItemFromStack(EntityPlayer player, ItemStack stack) {
      if (!player.capabilities.isCreativeMode) {
         stack.stackSize--;
         if (stack.stackSize <= 0) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
         }
      }
   }

   public boolean canMateWith(EntityAnimal otherAnimal) {
      return otherAnimal == this ? false : (otherAnimal.getClass() != this.getClass() ? false : this.isInLove() && otherAnimal.isInLove());
   }

   public void setInLove(EntityPlayer player) {
      this.inLove = 600;
      this.playerInLove = player;
      this.worldObj.setEntityState(this, (byte)18);
   }

   public EntityPlayer getPlayerInLove() {
      return this.playerInLove;
   }

   public EntityAnimal(World worldIn) {
      super(worldIn);
   }

   @Override
   public void handleStatusUpdate(byte id) {
      if (id == 18) {
         for (int index = 0; index < 7; index++) {
            double d = this.rand.nextGaussian() * 0.02;
            double d2 = this.rand.nextGaussian() * 0.02;
            double d3 = this.rand.nextGaussian() * 0.02;
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.HEART,
                  this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                  this.posY + 0.5 + this.rand.nextFloat() * this.height,
                  this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                  d,
                  d2,
                  d3
               );
         }
      } else {
         super.handleStatusUpdate(id);
      }
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.getGrowingAge() != 0) {
         this.inLove = 0;
      }

      if (this.inLove > 0) {
         this.inLove--;
         if (this.inLove % 10 == 0) {
            double d = this.rand.nextGaussian() * 0.02;
            double d2 = this.rand.nextGaussian() * 0.02;
            double d3 = this.rand.nextGaussian() * 0.02;
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.HEART,
                  this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                  this.posY + 0.5 + this.rand.nextFloat() * this.height,
                  this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                  d,
                  d2,
                  d3
               );
         }
      }
   }
}
