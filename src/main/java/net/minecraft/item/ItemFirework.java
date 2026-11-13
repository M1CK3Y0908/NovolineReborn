package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemFirework extends Item {
   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!worldIn.isRemote) {
         EntityFireworkRocket entityFireworkRocket = new EntityFireworkRocket(worldIn, pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ, stack);
         worldIn.spawnEntityInWorld(entityFireworkRocket);
         if (!playerIn.capabilities.isCreativeMode) {
            stack.stackSize--;
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
      if (stack.hasTagCompound()) {
         NBTTagCompound nBTTagCompound = stack.getTagCompound().getCompoundTag("Fireworks");
         if (nBTTagCompound != null) {
            if (nBTTagCompound.hasKey("Flight", 99)) {
               tooltip.add(StatCollector.translateToLocal("item.fireworks.flight") + " " + nBTTagCompound.getByte("Flight"));
            }

            NBTTagList nBTTagList = nBTTagCompound.getTagList("Explosions", 10);
            if (nBTTagList != null && nBTTagList.tagCount() > 0) {
               for (int index = 0; index < nBTTagList.tagCount(); index++) {
                  NBTTagCompound nBTTagCompound2 = nBTTagList.getCompoundTagAt(index);
                  List<String> list = Lists.newArrayList();
                  ItemFireworkCharge.addExplosionInfo(nBTTagCompound2, list);
                  if (list.size() > 0) {
                     for (int innerIndex = 1; innerIndex < list.size(); innerIndex++) {
                        list.set(innerIndex, "  " + list.get(innerIndex));
                     }

                     tooltip.addAll(list);
                  }
               }
            }
         }
      }
   }
}
