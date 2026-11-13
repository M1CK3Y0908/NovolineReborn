package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class ItemEmptyMap extends ItemMapBase {
   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      ItemStack itemStack = new ItemStack(Items.filled_map, 1, worldIn.getUniqueDataId("map"));
      String text = "map_" + itemStack.getMetadata();
      MapData mapData = new MapData(text);
      worldIn.setItemData(text, mapData);
      mapData.scale = 0;
      mapData.calculateMapCenter(playerIn.posX, playerIn.posZ, mapData.scale);
      mapData.dimension = (byte)worldIn.provider.getDimensionId();
      mapData.markDirty();
      itemStackIn.stackSize--;
      if (itemStackIn.stackSize <= 0) {
         return itemStack;
      }

      if (!playerIn.inventory.addItemStackToInventory(itemStack.copy())) {
         playerIn.getEntityItem3(itemStack);
      }

      playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      return itemStackIn;
   }

   protected ItemEmptyMap() {
      this.setCreativeTab(CreativeTabs.tabMisc);
   }
}
