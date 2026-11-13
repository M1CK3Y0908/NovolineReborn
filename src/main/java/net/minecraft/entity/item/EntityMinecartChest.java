package net.minecraft.entity.item;

import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntityMinecartChest extends EntityMinecartContainer {
   @Override
   public EntityMinecart.EnumMinecartType getMinecartType() {
      return EntityMinecart.EnumMinecartType.CHEST;
   }

   @Override
   public IBlockState getDefaultDisplayTile() {
      return Blocks.chest.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.NORTH);
   }

   @Override
   public String getGuiID() {
      return "minecraft:chest";
   }

   @Override
   public int getSizeInventory() {
      return 27;
   }

   @Override
   public int getDefaultDisplayTileOffset() {
      return 8;
   }

   @Override
   public void killMinecart(DamageSource source) {
      super.killMinecart(source);
      if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
         this.getEntityItem2(Item.getItemFromBlock(Blocks.chest), 0.0F);
      }
   }

   @Override
   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return new ContainerChest(playerInventory, this, playerIn);
   }

   public EntityMinecartChest(World worldIn) {
      super(worldIn);
   }

   public EntityMinecartChest(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
   }
}
