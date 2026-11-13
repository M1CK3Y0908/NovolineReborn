package net.minecraft.tileentity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockHopper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TileEntityHopper extends TileEntityLockable implements IHopper, ITickable {
   private String customName;
   private ItemStack[] inventory = new ItemStack[5];
   private int transferCooldown = -1;

   @Override
   public double getYPos() {
      return this.pos.getY() + 0.5;
   }

   @Override
   public ItemStack decrStackSize(int index, int count) {
      if (this.inventory[index] != null) {
         if (this.inventory[index].stackSize <= count) {
            ItemStack itemStack4 = this.inventory[index];
            this.inventory[index] = null;
            return itemStack4;
         }

         ItemStack itemStack2 = this.inventory[index].splitStack(count);
         if (this.inventory[index].stackSize == 0) {
            this.inventory[index] = null;
         }

         return itemStack2;
      } else {
         return null;
      }
   }

   @Override
   public void setField(int id, int value) {
   }

   @Override
   public String getGuiID() {
      return "minecraft:hopper";
   }

   @Override
   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return new ContainerHopper(playerInventory, this, playerIn);
   }

   private static boolean canCombine(ItemStack stack1, ItemStack stack2) {
      return stack1.getItem() != stack2.getItem()
         ? false
         : (
            stack1.getMetadata() != stack2.getMetadata()
               ? false
               : (stack1.stackSize > stack1.getMaxStackSize() ? false : ItemStack.areItemStackTagsEqual(stack1, stack2))
         );
   }

   @Override
   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean isOnTransferCooldown() {
      return this.transferCooldown > 0;
   }

   @Override
   public void clear() {
      for (int index = 0; index < this.inventory.length; index++) {
         this.inventory[index] = null;
      }
   }

   private static ItemStack insertStack(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side) {
      ItemStack itemStack = inventoryIn.getStackInSlot(index);
      if (canInsertItemInSlot(inventoryIn, stack, index, side)) {
         boolean conditionMet = false;
         if (itemStack == null) {
            inventoryIn.setInventorySlotContents(index, stack);
            stack = null;
            conditionMet = true;
         } else if (canCombine(itemStack, stack)) {
            int i = stack.getMaxStackSize() - itemStack.stackSize;
            int i2 = Math.min(stack.stackSize, i);
            stack.stackSize -= i2;
            itemStack.stackSize += i2;
            conditionMet = i2 > 0;
         }

         if (conditionMet) {
            if (inventoryIn instanceof TileEntityHopper tileEntityHopper) {
               if (tileEntityHopper.mayTransfer()) {
                  tileEntityHopper.setTransferCooldown(8);
               }

               inventoryIn.markDirty();
            }

            inventoryIn.markDirty();
         }
      }

      return stack;
   }

   public boolean mayTransfer() {
      return this.transferCooldown <= 1;
   }

   @Override
   public double getZPos() {
      return this.pos.getZ() + 0.5;
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.customName : "container.hopper";
   }

   @Override
   public void markDirty() {
      super.markDirty();
   }

   public static IInventory getInventoryAtPosition(World worldIn, double x, double y, double z) {
      IInventory inventory = null;
      int i = MathHelper.floor_double(x);
      int i2 = MathHelper.floor_double(y);
      int i3 = MathHelper.floor_double(z);
      BlockPos blockPos = new BlockPos(i, i2, i3);
      Block block = worldIn.getBlockState(blockPos).getBlock();
      if (block.hasTileEntity()) {
         TileEntity tileEntity = worldIn.getTileEntity(blockPos);
         if (tileEntity instanceof IInventory) {
            inventory = (IInventory)tileEntity;
            if (inventory instanceof TileEntityChest && block instanceof BlockChest) {
               inventory = ((BlockChest)block).getLockableContainer(worldIn, blockPos);
            }
         }
      }

      if (inventory == null) {
         List<Entity> list = worldIn.getEntitiesInAABBexcluding(
            (Entity)null, new AxisAlignedBB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntitySelectors.selectInventories
         );
         if (list.size() > 0) {
            inventory = (IInventory)list.get(worldIn.rand.nextInt(list.size()));
         }
      }

      return inventory;
   }

   public static List<EntityItem> getCaptureItems(World worldIn, double x, double y, double z) {
      return worldIn.getEntitiesWithinAABB(
         EntityItem.class, new AxisAlignedBB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntitySelectors.selectAnything
      );
   }

   @Override
   public ItemStack getStackInSlot(int index) {
      return this.inventory[index];
   }

   @Override
   public int getFieldCount() {
      return 0;
   }

   private static boolean pullItemFromSlot(IHopper hopper, IInventory inventoryIn, int index, EnumFacing direction) {
      ItemStack itemStack = inventoryIn.getStackInSlot(index);
      if (itemStack != null && canExtractItemFromSlot(inventoryIn, itemStack, index, direction)) {
         ItemStack itemStack2 = itemStack.copy();
         ItemStack itemStack3 = putStackInInventoryAllSlots(hopper, inventoryIn.decrStackSize(index, 1), (EnumFacing)null);
         if (itemStack3 == null || itemStack3.stackSize == 0) {
            inventoryIn.markDirty();
            return true;
         }

         inventoryIn.setInventorySlotContents(index, itemStack2);
      }

      return false;
   }

   private boolean isEmpty() {
      ItemStack[] inventory2 = this.inventory;
      int length2 = inventory2.length;

      for (int i = 0; i < length2; i++) {
         if (inventory2[i] != null) {
            return false;
         }
      }

      return true;
   }

   public void setCustomName(String customNameIn) {
      this.customName = customNameIn;
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer player) {
      return this.worldObj.getTileEntity(this.pos) != this
         ? false
         : player.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
   }

   public static ItemStack putStackInInventoryAllSlots(IInventory inventoryIn, ItemStack stack, EnumFacing side) {
      if (inventoryIn instanceof ISidedInventory && side != null) {
         ISidedInventory sidedInventory = (ISidedInventory)inventoryIn;
         int[] intArray = sidedInventory.getSlotsForFace(side);

         for (int nestedIndex = 0; nestedIndex < intArray.length && stack != null && stack.stackSize > 0; nestedIndex++) {
            stack = insertStack(inventoryIn, stack, intArray[nestedIndex], side);
         }
      } else {
         int sizeInventory = inventoryIn.getSizeInventory();

         for (int innerIndex = 0; innerIndex < sizeInventory && stack != null && stack.stackSize > 0; innerIndex++) {
            stack = insertStack(inventoryIn, stack, innerIndex, side);
         }
      }

      if (stack != null && stack.stackSize == 0) {
         stack = null;
      }

      return stack;
   }

   @Override
   public ItemStack removeStackFromSlot(int index) {
      if (this.inventory[index] != null) {
         ItemStack itemStack4 = this.inventory[index];
         this.inventory[index] = null;
         return itemStack4;
      } else {
         return null;
      }
   }

   private boolean isInventoryFull(IInventory inventoryIn, EnumFacing side) {
      if (inventoryIn instanceof ISidedInventory sidedInventory) {
         int[] intArray = sidedInventory.getSlotsForFace(side);

         for (int nestedIndex = 0; nestedIndex < intArray.length; nestedIndex++) {
            ItemStack itemStack = sidedInventory.getStackInSlot(intArray[nestedIndex]);
            if (itemStack == null || itemStack.stackSize != itemStack.getMaxStackSize()) {
               return false;
            }
         }
      } else {
         int sizeInventory = inventoryIn.getSizeInventory();

         for (int innerIndex = 0; innerIndex < sizeInventory; innerIndex++) {
            ItemStack itemStack2 = inventoryIn.getStackInSlot(innerIndex);
            if (itemStack2 == null || itemStack2.stackSize != itemStack2.getMaxStackSize()) {
               return false;
            }
         }
      }

      return true;
   }

   private static boolean canInsertItemInSlot(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side) {
      return !inventoryIn.isItemValidForSlot(index, stack)
         ? false
         : !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory)inventoryIn).canInsertItem(index, stack, side);
   }

   public void setTransferCooldown(int ticks) {
      this.transferCooldown = ticks;
   }

   @Override
   public double getXPos() {
      return this.pos.getX() + 0.5;
   }

   @Override
   public void openInventory(EntityPlayer entityPlayer) {
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      NBTTagList nBTTagList = compound.getTagList("Items", 10);
      this.inventory = new ItemStack[this.getSizeInventory()];
      if (compound.hasKey("CustomName", 8)) {
         this.customName = compound.getString("CustomName");
      }

      this.transferCooldown = compound.getInteger("TransferCooldown");

      for (int index = 0; index < nBTTagList.tagCount(); index++) {
         NBTTagCompound nBTTagCompound = nBTTagList.getCompoundTagAt(index);
         int byteValue = nBTTagCompound.getByte("Slot");
         if (byteValue >= 0 && byteValue < this.inventory.length) {
            this.inventory[byteValue] = ItemStack.loadItemStackFromNBT(nBTTagCompound);
         }
      }
   }

   @Override
   public boolean hasCustomName() {
      return this.customName != null && this.customName.length() > 0;
   }

   private static boolean canExtractItemFromSlot(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side) {
      return !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory)inventoryIn).canExtractItem(index, stack, side);
   }

   private boolean isFull() {
      for (ItemStack itemStack4 : this.inventory) {
         if (itemStack4 == null || itemStack4.stackSize != itemStack4.getMaxStackSize()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      NBTTagList nbtTagList = new NBTTagList();

      for (int index = 0; index < this.inventory.length; index++) {
         if (this.inventory[index] != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setByte("Slot", (byte)index);
            this.inventory[index].writeToNBT(nbtTagCompound);
            nbtTagList.appendTag(nbtTagCompound);
         }
      }

      compound.setTag("Items", nbtTagList);
      compound.setInteger("TransferCooldown", this.transferCooldown);
      if (this.hasCustomName()) {
         compound.setString("CustomName", this.customName);
      }
   }

   @Override
   public void closeInventory(EntityPlayer entityPlayer) {
   }

   @Override
   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return true;
   }

   private boolean transferItemsOut() {
      IInventory inventory = this.getInventoryForHopperTransfer();
      if (inventory == null) {
         return false;
      }

      EnumFacing facing = BlockHopper.getFacing(this.getBlockMetadata()).getOpposite();
      if (this.isInventoryFull(inventory, facing)) {
         return false;
      }

      for (int index = 0; index < this.getSizeInventory(); index++) {
         if (this.getStackInSlot(index) != null) {
            ItemStack itemStack = this.getStackInSlot(index).copy();
            ItemStack itemStack2 = putStackInInventoryAllSlots(inventory, this.decrStackSize(index, 1), facing);
            if (itemStack2 == null || itemStack2.stackSize == 0) {
               inventory.markDirty();
               return true;
            }

            this.setInventorySlotContents(index, itemStack);
         }
      }

      return false;
   }

   @Override
   public int getSizeInventory() {
      return this.inventory.length;
   }

   public static boolean captureDroppedItems(IHopper iHopper) {
      IInventory hopperInventory = getHopperInventory(iHopper);
      if (hopperInventory != null) {
         EnumFacing enumFacing = EnumFacing.DOWN;
         if (isInventoryEmpty(hopperInventory, enumFacing)) {
            return false;
         }

         if (hopperInventory instanceof ISidedInventory iSidedInventory) {
            int[] slotsForFace = iSidedInventory.getSlotsForFace(enumFacing);

            for (int i = 0; i < slotsForFace.length; i++) {
               if (pullItemFromSlot(iHopper, hopperInventory, slotsForFace[i], enumFacing)) {
                  return true;
               }
            }
         } else {
            int sizeInventory = hopperInventory.getSizeInventory();

            for (int i2 = 0; i2 < sizeInventory; i2++) {
               if (pullItemFromSlot(iHopper, hopperInventory, i2, enumFacing)) {
                  return true;
               }
            }
         }
      } else {
         for (EntityItem entityItem : getCaptureItems(iHopper.getWorld(), iHopper.getXPos(), iHopper.getYPos() + 1.0, iHopper.getZPos())) {
            if (putDropInInventoryAllSlots(iHopper, entityItem)) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      this.inventory[index] = stack;
      if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
         stack.stackSize = this.getInventoryStackLimit();
      }
   }

   public static IInventory getHopperInventory(IHopper hopper) {
      return getInventoryAtPosition(hopper.getWorld(), hopper.getXPos(), hopper.getYPos() + 1.0, hopper.getZPos());
   }

   private static boolean isInventoryEmpty(IInventory inventoryIn, EnumFacing side) {
      if (inventoryIn instanceof ISidedInventory sidedInventory) {
         int[] intArray = sidedInventory.getSlotsForFace(side);

         for (int index = 0; index < intArray.length; index++) {
            if (sidedInventory.getStackInSlot(intArray[index]) != null) {
               return false;
            }
         }
      } else {
         int sizeInventory = inventoryIn.getSizeInventory();

         for (int nestedIndex = 0; nestedIndex < sizeInventory; nestedIndex++) {
            if (inventoryIn.getStackInSlot(nestedIndex) != null) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public int getField(int id) {
      return 0;
   }

   public static boolean putDropInInventoryAllSlots(IInventory iInventory, EntityItem itemIn) {
      boolean conditionMet = false;
      if (itemIn == null) {
         return false;
      }

      ItemStack itemStack = itemIn.getEntityItem().copy();
      ItemStack itemStack2 = putStackInInventoryAllSlots(iInventory, itemStack, (EnumFacing)null);
      if (itemStack2 != null && itemStack2.stackSize != 0) {
         itemIn.setEntityItemStack(itemStack2);
      } else {
         conditionMet = true;
         itemIn.setDead();
      }

      return conditionMet;
   }

   public boolean updateHopper() {
      if (this.worldObj != null && !this.worldObj.isRemote) {
         if (!this.isOnTransferCooldown() && BlockHopper.isEnabled(this.getBlockMetadata())) {
            boolean conditionMet = false;
            if (!this.isEmpty()) {
               conditionMet = this.transferItemsOut();
            }

            if (!this.isFull()) {
               conditionMet = captureDroppedItems(this) || conditionMet;
            }

            if (conditionMet) {
               this.setTransferCooldown(8);
               this.markDirty();
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private IInventory getInventoryForHopperTransfer() {
      EnumFacing facing = BlockHopper.getFacing(this.getBlockMetadata());
      return getInventoryAtPosition(
         this.getWorld(), this.pos.getX() + facing.getFrontOffsetX(), this.pos.getY() + facing.getFrontOffsetY(), this.pos.getZ() + facing.getFrontOffsetZ()
      );
   }

   @Override
   public void update() {
      if (this.worldObj != null && !this.worldObj.isRemote) {
         this.transferCooldown--;
         if (!this.isOnTransferCooldown()) {
            this.setTransferCooldown(0);
            this.updateHopper();
         }
      }
   }
}
