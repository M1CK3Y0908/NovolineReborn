package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileEntityChest extends TileEntityLockable implements ITickable, IInventory {
   public TileEntityChest adjacentChestZPos;
   public boolean adjacentChestChecked;
   public int numPlayersUsing;
   private int ticksSinceSync;
   public TileEntityChest adjacentChestXPos;
   public TileEntityChest adjacentChestXNeg;
   public TileEntityChest adjacentChestZNeg;
   private String customName;
   private ItemStack[] chestContents = new ItemStack[27];
   public float lidAngle;
   private int cachedChestType;
   public float prevLidAngle;

   @Override
   public void clear() {
      for (int index = 0; index < this.chestContents.length; index++) {
         this.chestContents[index] = null;
      }
   }

   private boolean isChestAt(BlockPos posIn) {
      if (this.worldObj == null) {
         return false;
      }

      Block block = this.worldObj.getBlockState(posIn).getBlock();
      return block instanceof BlockChest && ((BlockChest)block).chestType == this.getChestType();
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer player) {
      return this.worldObj.getTileEntity(this.pos) != this
         ? false
         : player.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
   }

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      NBTTagList nbtTagList = new NBTTagList();

      for (int index = 0; index < this.chestContents.length; index++) {
         if (this.chestContents[index] != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setByte("Slot", (byte)index);
            this.chestContents[index].writeToNBT(nbtTagCompound);
            nbtTagList.appendTag(nbtTagCompound);
         }
      }

      compound.setTag("Items", nbtTagList);
      if (this.hasCustomName()) {
         compound.setString("CustomName", this.customName);
      }
   }

   @Override
   public void closeInventory(EntityPlayer player) {
      if (!player.isSpectator() && this.getBlockType() instanceof BlockChest) {
         this.numPlayersUsing--;
         this.worldObj.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
         this.worldObj.notifyNeighborsOfStateChange(this.pos, this.getBlockType());
         this.worldObj.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType());
      }
   }

   @Override
   public ItemStack removeStackFromSlot(int index) {
      if (this.chestContents[index] != null) {
         ItemStack itemStack = this.chestContents[index];
         this.chestContents[index] = null;
         return itemStack;
      } else {
         return null;
      }
   }

   @Override
   public void update() {
      this.checkForAdjacentChests();
      int x = this.pos.getX();
      int y = this.pos.getY();
      int z = this.pos.getZ();
      this.ticksSinceSync++;
      if (!this.worldObj.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + x + y + z) % 200 == 0) {
         this.numPlayersUsing = 0;
         float f = 5.0F;

         for (EntityPlayer entityPlayer : this.worldObj
            .getEntitiesWithinAABB(
               EntityPlayer.class, new AxisAlignedBB(x - f, y - f, z - f, x + 1 + f, y + 1 + f, z + 1 + f)
            )) {
            if (entityPlayer.openContainer instanceof ContainerChest) {
               IInventory lowerChestInventory = ((ContainerChest)entityPlayer.openContainer).getLowerChestInventory();
               if (lowerChestInventory == this || lowerChestInventory instanceof InventoryLargeChest && ((InventoryLargeChest)lowerChestInventory).isPartOfLargeChest(this)) {
                  this.numPlayersUsing++;
               }
            }
         }
      }

      this.prevLidAngle = this.lidAngle;
      float f2 = 0.1F;
      if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
         double d = x + 0.5;
         double d2 = z + 0.5;
         if (this.adjacentChestZPos != null) {
            d2 += 0.5;
         }

         if (this.adjacentChestXPos != null) {
            d += 0.5;
         }

         this.worldObj.playSoundEffect(d, y + 0.5, d2, "random.chestopen", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
      }

      if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) {
         float lidAngle2 = this.lidAngle;
         if (this.numPlayersUsing > 0) {
            this.lidAngle += f2;
         } else {
            this.lidAngle -= f2;
         }

         if (this.lidAngle > 1.0F) {
            this.lidAngle = 1.0F;
         }

         float f3 = 0.5F;
         if (this.lidAngle < f3 && lidAngle2 >= f3 && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
            double d3 = x + 0.5;
            double d4 = z + 0.5;
            if (this.adjacentChestZPos != null) {
               d4 += 0.5;
            }

            if (this.adjacentChestXPos != null) {
               d3 += 0.5;
            }

            this.worldObj.playSoundEffect(d3, y + 0.5, d4, "random.chestclosed", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
         }

         if (this.lidAngle < 0.0F) {
            this.lidAngle = 0.0F;
         }
      }
   }

   @Override
   public void openInventory(EntityPlayer player) {
      if (!player.isSpectator()) {
         if (this.numPlayersUsing < 0) {
            this.numPlayersUsing = 0;
         }

         this.numPlayersUsing++;
         this.worldObj.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
         this.worldObj.notifyNeighborsOfStateChange(this.pos, this.getBlockType());
         this.worldObj.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType());
      }
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.customName : "container.chest";
   }

   @Override
   public boolean hasCustomName() {
      return this.customName != null && this.customName.length() > 0;
   }

   @Override
   public int getInventoryStackLimit() {
      return 64;
   }

   public void setCustomName(String name) {
      this.customName = name;
   }

   protected TileEntityChest getAdjacentChest(EnumFacing side) {
      BlockPos blockPos = this.pos.offset(side);
      if (this.isChestAt(blockPos) && this.worldObj.getTileEntity(blockPos) instanceof TileEntityChest tileEntityChest) {
         tileEntityChest.checkNeighborForInvalidation(this, side.getOpposite());
         return tileEntityChest;
      } else {
         return null;
      }
   }

   @Override
   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return true;
   }

   @Override
   public void setField(int id, int value) {
   }

   @Override
   public boolean receiveClientEvent(int id, int type) {
      if (id == 1) {
         this.numPlayersUsing = type;
         return true;
      } else {
         return super.receiveClientEvent(id, type);
      }
   }

   @Override
   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return new ContainerChest(playerInventory, this, playerIn);
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      NBTTagList nBTTagList = compound.getTagList("Items", 10);
      this.chestContents = new ItemStack[this.getSizeInventory()];
      if (compound.hasKey("CustomName", 8)) {
         this.customName = compound.getString("CustomName");
      }

      for (int index = 0; index < nBTTagList.tagCount(); index++) {
         NBTTagCompound nBTTagCompound = nBTTagList.getCompoundTagAt(index);
         int i = nBTTagCompound.getByte("Slot") & 255;
         if (i >= 0 && i < this.chestContents.length) {
            this.chestContents[i] = ItemStack.loadItemStackFromNBT(nBTTagCompound);
         }
      }
   }

   @Override
   public void invalidate() {
      super.invalidate();
      this.updateContainingBlockInfo();
      this.checkForAdjacentChests();
   }

   public TileEntityChest(int chestType) {
      this.cachedChestType = chestType;
   }

   public void checkForAdjacentChests() {
      if (!this.adjacentChestChecked) {
         this.adjacentChestChecked = true;
         this.adjacentChestXNeg = this.getAdjacentChest(EnumFacing.WEST);
         this.adjacentChestXPos = this.getAdjacentChest(EnumFacing.EAST);
         this.adjacentChestZNeg = this.getAdjacentChest(EnumFacing.NORTH);
         this.adjacentChestZPos = this.getAdjacentChest(EnumFacing.SOUTH);
      }
   }

   @Override
   public void updateContainingBlockInfo() {
      super.updateContainingBlockInfo();
      this.adjacentChestChecked = false;
   }

   @Override
   public ItemStack getStackInSlot(int index) {
      return this.chestContents[index];
   }

   @Override
   public int getFieldCount() {
      return 0;
   }

   public TileEntityChest() {
      this.cachedChestType = -1;
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      this.chestContents[index] = stack;
      if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
         stack.stackSize = this.getInventoryStackLimit();
      }

      this.markDirty();
   }

   @Override
   public String getGuiID() {
      return "minecraft:chest";
   }

   @Override
   public int getSizeInventory() {
      return 27;
   }

   private void checkNeighborForInvalidation(TileEntityChest chestTe, EnumFacing side) {
      if (chestTe.isInvalid()) {
         this.adjacentChestChecked = false;
      } else if (this.adjacentChestChecked) {
         switch (side) {
            case NORTH:
               if (this.adjacentChestZNeg != chestTe) {
                  this.adjacentChestChecked = false;
               }
               break;
            case SOUTH:
               if (this.adjacentChestZPos != chestTe) {
                  this.adjacentChestChecked = false;
               }
               break;
            case EAST:
               if (this.adjacentChestXPos != chestTe) {
                  this.adjacentChestChecked = false;
               }
               break;
            case WEST:
               if (this.adjacentChestXNeg != chestTe) {
                  this.adjacentChestChecked = false;
               }
         }
      }
   }

   public int getChestType() {
      if (this.cachedChestType == -1) {
         if (this.worldObj == null || !(this.getBlockType() instanceof BlockChest)) {
            return 0;
         }

         this.cachedChestType = ((BlockChest)this.getBlockType()).chestType;
      }

      return this.cachedChestType;
   }

   @Override
   public int getField(int id) {
      return 0;
   }

   @Override
   public ItemStack decrStackSize(int index, int count) {
      if (this.chestContents[index] != null) {
         if (this.chestContents[index].stackSize <= count) {
            ItemStack itemStack = this.chestContents[index];
            this.chestContents[index] = null;
            this.markDirty();
            return itemStack;
         }

         ItemStack itemStack2 = this.chestContents[index].splitStack(count);
         if (this.chestContents[index].stackSize == 0) {
            this.chestContents[index] = null;
         }

         this.markDirty();
         return itemStack2;
      } else {
         return null;
      }
   }
}
