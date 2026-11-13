package net.minecraft.tileentity;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileEntityBrewingStand extends TileEntityLockable implements ITickable, ISidedInventory {
   private Item ingredientID;
   private static final int[] inputSlots = new int[]{3};
   private boolean[] filledSlots;
   private String customName;
   private static final int[] outputSlots = new int[]{0, 1, 2};
   private int brewTime;
   private ItemStack[] brewingItemStacks = new ItemStack[4];

   @Override
   public void openInventory(EntityPlayer entityPlayer) {
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.customName : "container.brewing";
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer player) {
      return this.worldObj.getTileEntity(this.pos) != this
         ? false
         : player.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
   }

   @Override
   public void closeInventory(EntityPlayer entityPlayer) {
   }

   @Override
   public int getSizeInventory() {
      return this.brewingItemStacks.length;
   }

   @Override
   public ItemStack decrStackSize(int index, int count) {
      if (index >= 0 && index < this.brewingItemStacks.length) {
         ItemStack itemStack = this.brewingItemStacks[index];
         this.brewingItemStacks[index] = null;
         return itemStack;
      } else {
         return null;
      }
   }

   @Override
   public void clear() {
      for (int index = 0; index < this.brewingItemStacks.length; index++) {
         this.brewingItemStacks[index] = null;
      }
   }

   @Override
   public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
      return this.isItemValidForSlot(index, itemStackIn);
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      if (index >= 0 && index < this.brewingItemStacks.length) {
         this.brewingItemStacks[index] = stack;
      }
   }

   public boolean[] createFilledSlotsArray() {
      boolean[] booleanArray = new boolean[3];

      for (int index = 0; index < 3; index++) {
         if (this.brewingItemStacks[index] != null) {
            booleanArray[index] = true;
         }
      }

      return booleanArray;
   }

   @Override
   public void update() {
      if (this.brewTime > 0) {
         this.brewTime--;
         if (this.brewTime == 0) {
            this.brewPotions();
            this.markDirty();
         } else if (!this.canBrew()) {
            this.brewTime = 0;
            this.markDirty();
         } else if (this.ingredientID != this.brewingItemStacks[3].getItem()) {
            this.brewTime = 0;
            this.markDirty();
         }
      } else if (this.canBrew()) {
         this.brewTime = 400;
         this.ingredientID = this.brewingItemStacks[3].getItem();
      }

      if (!this.worldObj.isRemote) {
         boolean[] booleanArray = this.createFilledSlotsArray();
         if (!Arrays.equals(booleanArray, this.filledSlots)) {
            this.filledSlots = booleanArray;
            IBlockState blockState = this.worldObj.getBlockState(this.getPos());
            if (!(blockState.getBlock() instanceof BlockBrewingStand)) {
               return;
            }

            for (int index = 0; index < BlockBrewingStand.HAS_BOTTLE.length; index++) {
               blockState = blockState.withProperty(BlockBrewingStand.HAS_BOTTLE[index], booleanArray[index]);
            }

            this.worldObj.setBlockState(this.pos, blockState, 2);
         }
      }
   }

   @Override
   public ItemStack getStackInSlot(int index) {
      return index >= 0 && index < this.brewingItemStacks.length ? this.brewingItemStacks[index] : null;
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      NBTTagList nBTTagList = compound.getTagList("Items", 10);
      this.brewingItemStacks = new ItemStack[this.getSizeInventory()];

      for (int index = 0; index < nBTTagList.tagCount(); index++) {
         NBTTagCompound nBTTagCompound = nBTTagList.getCompoundTagAt(index);
         int byteValue = nBTTagCompound.getByte("Slot");
         if (byteValue >= 0 && byteValue < this.brewingItemStacks.length) {
            this.brewingItemStacks[byteValue] = ItemStack.loadItemStackFromNBT(nBTTagCompound);
         }
      }

      this.brewTime = compound.getShort("BrewTime");
      if (compound.hasKey("CustomName", 8)) {
         this.customName = compound.getString("CustomName");
      }
   }

   @Override
   public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
      return true;
   }

   @Override
   public int getField(int id) {
      switch (id) {
         case 0:
            return this.brewTime;
         default:
            return 0;
      }
   }

   @Override
   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return index == 3 ? stack.getItem().isPotionIngredient(stack) : stack.getItem() == Items.potionitem || stack.getItem() == Items.glass_bottle;
   }

   @Override
   public int getFieldCount() {
      return 1;
   }

   @Override
   public String getGuiID() {
      return "minecraft:brewing_stand";
   }

   @Override
   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return new ContainerBrewingStand(playerInventory, this);
   }

   @Override
   public void setField(int id, int value) {
      switch (id) {
         case 0:
            this.brewTime = value;
      }
   }

   private void brewPotions() {
      if (this.canBrew()) {
         ItemStack itemStack = this.brewingItemStacks[3];

         for (int index = 0; index < 3; index++) {
            if (this.brewingItemStacks[index] != null && this.brewingItemStacks[index].getItem() == Items.potionitem) {
               int metadata = this.brewingItemStacks[index].getMetadata();
               int potionResult = this.getPotionResult(metadata, itemStack);
               List<PotionEffect> list = Items.potionitem.getEffects(metadata);
               List<PotionEffect> list2 = Items.potionitem.getEffects(potionResult);
               if (metadata > 0 && list == list2 || list != null && (list.equals(list2) || list2 == null)) {
                  if (!ItemPotion.isSplash(metadata) && ItemPotion.isSplash(potionResult)) {
                     this.brewingItemStacks[index].setItemDamage(potionResult);
                  }
               } else if (metadata != potionResult) {
                  this.brewingItemStacks[index].setItemDamage(potionResult);
               }
            }
         }

         if (itemStack.getItem().hasContainerItem()) {
            this.brewingItemStacks[3] = new ItemStack(itemStack.getItem().getContainerItem());
         } else {
            this.brewingItemStacks[3].stackSize--;
            if (this.brewingItemStacks[3].stackSize <= 0) {
               this.brewingItemStacks[3] = null;
            }
         }
      }
   }

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      compound.setShort("BrewTime", (short)this.brewTime);
      NBTTagList nbtTagList = new NBTTagList();

      for (int index = 0; index < this.brewingItemStacks.length; index++) {
         if (this.brewingItemStacks[index] != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setByte("Slot", (byte)index);
            this.brewingItemStacks[index].writeToNBT(nbtTagCompound);
            nbtTagList.appendTag(nbtTagCompound);
         }
      }

      compound.setTag("Items", nbtTagList);
      if (this.hasCustomName()) {
         compound.setString("CustomName", this.customName);
      }
   }

   public void setName(String name) {
      this.customName = name;
   }

   @Override
   public int getInventoryStackLimit() {
      return 64;
   }

   @Override
   public ItemStack removeStackFromSlot(int index) {
      if (index >= 0 && index < this.brewingItemStacks.length) {
         ItemStack itemStack = this.brewingItemStacks[index];
         this.brewingItemStacks[index] = null;
         return itemStack;
      } else {
         return null;
      }
   }

   private int getPotionResult(int meta, ItemStack stack) {
      return stack == null
         ? meta
         : (stack.getItem().isPotionIngredient(stack) ? PotionHelper.applyIngredient(meta, stack.getItem().getPotionEffect(stack)) : meta);
   }

   private boolean canBrew() {
      if (this.brewingItemStacks[3] != null && this.brewingItemStacks[3].stackSize > 0) {
         ItemStack itemStack = this.brewingItemStacks[3];
         if (!itemStack.getItem().isPotionIngredient(itemStack)) {
            return false;
         }

         boolean conditionMet = false;

         for (int index = 0; index < 3; index++) {
            if (this.brewingItemStacks[index] != null && this.brewingItemStacks[index].getItem() == Items.potionitem) {
               int metadata = this.brewingItemStacks[index].getMetadata();
               int potionResult = this.getPotionResult(metadata, itemStack);
               if (!ItemPotion.isSplash(metadata) && ItemPotion.isSplash(potionResult)) {
                  conditionMet = true;
                  break;
               }

               List<PotionEffect> list = Items.potionitem.getEffects(metadata);
               List<PotionEffect> list2 = Items.potionitem.getEffects(potionResult);
               if ((metadata <= 0 || list != list2) && (list == null || !list.equals(list2) && list2 != null) && metadata != potionResult) {
                  conditionMet = true;
                  break;
               }
            }
         }

         return conditionMet;
      } else {
         return false;
      }
   }

   @Override
   public boolean hasCustomName() {
      return this.customName != null && this.customName.length() > 0;
   }

   @Override
   public int[] getSlotsForFace(EnumFacing side) {
      return side == EnumFacing.UP ? inputSlots : outputSlots;
   }
}
