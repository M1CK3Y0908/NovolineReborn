package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemSnowball extends Item {
   @Override
   public ItemStack onItemRightClick(ItemStack itemStack, World world2, EntityPlayer entityPlayer) {
      if (!entityPlayer.capabilities.isCreativeMode) {
         itemStack.stackSize--;
      }

      world2.playSoundAtEntity(entityPlayer, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
      if (!world2.isRemote) {
         world2.spawnEntityInWorld(new EntitySnowball(world2, entityPlayer));
      }

      entityPlayer.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      return itemStack;
   }

   public ItemSnowball() {
      this.maxStackSize = 16;
      this.setCreativeTab(CreativeTabs.tabMisc);
   }
}
