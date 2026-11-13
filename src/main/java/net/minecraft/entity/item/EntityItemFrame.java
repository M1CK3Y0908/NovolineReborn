package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntityItemFrame extends EntityHanging {
   private float itemDropChance = 1.0F;

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      NBTTagCompound nBTTagCompound = tagCompund.getCompoundTag("Item");
      if (nBTTagCompound != null && !nBTTagCompound.hasNoTags()) {
         this.setDisplayedItemWithUpdate(ItemStack.loadItemStackFromNBT(nBTTagCompound), false);
         this.setItemRotation(tagCompund.getByte("ItemRotation"), false);
         if (tagCompund.hasKey("ItemDropChance", 99)) {
            this.itemDropChance = tagCompund.getFloat("ItemDropChance");
         }

         if (tagCompund.hasKey("Direction")) {
            this.setItemRotation(this.getRotation() * 2, false);
         }
      }

      super.readEntityFromNBT(tagCompund);
   }

   @Override
   public int getHeightPixels() {
      return 12;
   }

   public void setItemRotation(int rotation) {
      this.setItemRotation(rotation, true);
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if (!source.isExplosion() && this.getDisplayedItem() != null) {
         if (!this.worldObj.isRemote) {
            this.dropItemOrSelf(source.getEntity(), false);
            this.setDisplayedItem((ItemStack)null);
         }

         return true;
      } else {
         return super.attackEntityFrom(source, amount);
      }
   }

   private void setDisplayedItemWithUpdate(ItemStack stack, boolean updateComparator) {
      if (stack != null) {
         stack = stack.copy();
         stack.stackSize = 1;
         stack.setItemFrame(this);
      }

      this.getDataWatcher().updateObject(8, stack);
      this.getDataWatcher().setObjectWatched(8);
      if (updateComparator && this.hangingPosition != null) {
         this.worldObj.updateComparatorOutputLevel(this.hangingPosition, Blocks.air);
      }
   }

   @Override
   public int getWidthPixels() {
      return 12;
   }

   public void setDisplayedItem(ItemStack stack) {
      this.setDisplayedItemWithUpdate(stack, true);
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      if (this.getDisplayedItem() != null) {
         tagCompound.setTag("Item", this.getDisplayedItem().writeToNBT(new NBTTagCompound()));
         tagCompound.setByte("ItemRotation", (byte)this.getRotation());
         tagCompound.setFloat("ItemDropChance", this.itemDropChance);
      }

      super.writeEntityToNBT(tagCompound);
   }

   public EntityItemFrame(World worldIn, BlockPos hangingPosition, EnumFacing facing) {
      super(worldIn, hangingPosition);
      this.updateFacingWithBoundingBox(facing);
   }

   @Override
   public boolean isInRangeToRenderDist(double distance) {
      double d = 16.0;
      d = d * 64.0 * this.renderDistanceWeight;
      return distance < d * d;
   }

   public EntityItemFrame(World worldIn) {
      super(worldIn);
   }

   public void dropItemOrSelf(Entity brokenEntity, boolean dropFrame) {
      if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
         ItemStack itemStack = this.getDisplayedItem();
         if (brokenEntity instanceof EntityPlayer && ((EntityPlayer)brokenEntity).capabilities.isCreativeMode) {
            this.removeFrameFromMap(itemStack);
            return;
         }

         if (dropFrame) {
            this.entityDropItem(new ItemStack(Items.item_frame), 0.0F);
         }

         if (itemStack != null && this.rand.nextFloat() < this.itemDropChance) {
            itemStack = itemStack.copy();
            this.removeFrameFromMap(itemStack);
            this.entityDropItem(itemStack, 0.0F);
         }
      }
   }

   @Override
   public boolean interactFirst(EntityPlayer playerIn) {
      if (this.getDisplayedItem() == null) {
         ItemStack itemStack = playerIn.getHeldItem();
         if (itemStack != null && !this.worldObj.isRemote) {
            this.setDisplayedItem(itemStack);
            if (!playerIn.capabilities.isCreativeMode && --itemStack.stackSize <= 0) {
               playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, (ItemStack)null);
            }
         }
      } else if (!this.worldObj.isRemote) {
         this.setItemRotation(this.getRotation() + 1);
      }

      return true;
   }

   public int getAnalogOutput() {
      return this.getDisplayedItem() == null ? 0 : this.getRotation() % 8 + 1;
   }

   public int getRotation() {
      return this.getDataWatcher().getWatchableObjectByte(9);
   }

   @Override
   public float getCollisionBorderSize() {
      return 0.0F;
   }

   @Override
   public void onBroken(Entity brokenEntity) {
      this.dropItemOrSelf(brokenEntity, true);
   }

   private void removeFrameFromMap(ItemStack stack) {
      if (stack != null) {
         if (stack.getItem() == Items.filled_map) {
            ((ItemMap)stack.getItem()).getMapData(stack, this.worldObj).mapDecorations.remove("frame-" + this.getEntityId());
         }

         stack.setItemFrame((EntityItemFrame)null);
      }
   }

   @Override
   protected void entityInit() {
      this.getDataWatcher().handleInt(8);
      this.getDataWatcher().addObject(9, (byte)0);
   }

   public ItemStack getDisplayedItem() {
      return this.getDataWatcher().getWatchableObjectItemStack(8);
   }

   private void setItemRotation(int rotation, boolean updateComparator) {
      this.getDataWatcher().updateObject(9, (byte)(rotation % 8));
      if (updateComparator && this.hangingPosition != null) {
         this.worldObj.updateComparatorOutputLevel(this.hangingPosition, Blocks.air);
      }
   }
}
