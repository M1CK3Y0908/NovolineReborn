package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.world.World;

public class EntityMinecartHopper extends EntityMinecartContainer implements IHopper {
   private boolean isBlocked = true;
   private int transferTicker = -1;
   private BlockPos originPosition = BlockPos.ORIGIN;

   @Override
   public World getWorld() {
      return this.worldObj;
   }

   public void setTransferTicker(int transferTickerIn) {
      this.transferTicker = transferTickerIn;
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (!this.worldObj.isRemote && this.isEntityAlive() && this.getBlocked()) {
         if (new BlockPos(this).equals(this.originPosition)) {
            this.transferTicker--;
         } else {
            this.setTransferTicker(0);
         }

         if (!this.canTransfer()) {
            this.setTransferTicker(0);
            if (this.captureDroppedItems()) {
               this.setTransferTicker(4);
               this.markDirty();
            }
         }
      }
   }

   public boolean captureDroppedItems() {
      if (TileEntityHopper.captureDroppedItems(this)) {
         return true;
      }

      List<EntityItem> list = this.worldObj
         .getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().expand(0.25, 0.0, 0.25), EntitySelectors.selectAnything);
      if (list.size() > 0) {
         TileEntityHopper.putDropInInventoryAllSlots(this, list.get(0));
      }

      return false;
   }

   @Override
   public int getDefaultDisplayTileOffset() {
      return 1;
   }

   @Override
   public int getSizeInventory() {
      return 5;
   }

   public boolean canTransfer() {
      return this.transferTicker > 0;
   }

   public EntityMinecartHopper(World worldIn) {
      super(worldIn);
   }

   @Override
   protected void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.transferTicker = tagCompund.getInteger("TransferCooldown");
   }

   public EntityMinecartHopper(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
   }

   @Override
   public boolean interactFirst(EntityPlayer playerIn) {
      if (!this.worldObj.isRemote) {
         playerIn.displayGUIChest(this);
      }

      return true;
   }

   @Override
   public double getZPos() {
      return this.posZ;
   }

   @Override
   public void killMinecart(DamageSource source) {
      super.killMinecart(source);
      if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
         this.getEntityItem2(Item.getItemFromBlock(Blocks.hopper), 0.0F);
      }
   }

   @Override
   public IBlockState getDefaultDisplayTile() {
      return Blocks.hopper.getDefaultState();
   }

   @Override
   public double getXPos() {
      return this.posX;
   }

   @Override
   public void setHasDisplayTile(boolean flag) {
      boolean flag2 = !flag;
      if (flag2 != this.getBlocked()) {
         this.setBlocked(flag2);
      }
   }

   public boolean getBlocked() {
      return this.isBlocked;
   }

   @Override
   public double getYPos() {
      return this.posY + 0.5;
   }

   @Override
   public String getGuiID() {
      return "minecraft:hopper";
   }

   public void setBlocked(boolean blocked) {
      this.isBlocked = blocked;
   }

   @Override
   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return new ContainerHopper(playerInventory, this, playerIn);
   }

   @Override
   public EntityMinecart.EnumMinecartType getMinecartType() {
      return EntityMinecart.EnumMinecartType.HOPPER;
   }

   @Override
   protected void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setInteger("TransferCooldown", this.transferTicker);
   }
}
