package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.WeightedRandomChestContent;

public class ItemEnchantedBook extends Item {
   @Override
   public boolean isItemTool(ItemStack itemStack) {
      return false;
   }

   public void addEnchantment(ItemStack stack, EnchantmentData enchantment) {
      NBTTagList nBTTagList = this.getEnchantments(stack);
      boolean conditionMet = true;

      for (int index = 0; index < nBTTagList.tagCount(); index++) {
         NBTTagCompound nBTTagCompound = nBTTagList.getCompoundTagAt(index);
         if (nBTTagCompound.getShort("id") == enchantment.enchantmentobj.effectId) {
            if (nBTTagCompound.getShort("lvl") < enchantment.enchantmentLevel) {
               nBTTagCompound.setShort("lvl", (short)enchantment.enchantmentLevel);
            }

            conditionMet = false;
            break;
         }
      }

      if (conditionMet) {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         nbtTagCompound.setShort("id", (short)enchantment.enchantmentobj.effectId);
         nbtTagCompound.setShort("lvl", (short)enchantment.enchantmentLevel);
         nBTTagList.appendTag(nbtTagCompound);
      }

      if (!stack.hasTagCompound()) {
         stack.setTagCompound(new NBTTagCompound());
      }

      stack.getTagCompound().setTag("StoredEnchantments", nBTTagList);
   }

   @Override
   public boolean hasEffect(ItemStack itemStack) {
      return true;
   }

   public void getAll(Enchantment enchantment, List<ItemStack> list) {
      for (int index = enchantment.getMinLevel(); index <= enchantment.getMaxLevel(); index++) {
         list.add(this.getEnchantedItemStack(new EnchantmentData(enchantment, index)));
      }
   }

   @Override
   public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
      super.addInformation(stack, playerIn, tooltip, advanced);
      NBTTagList nBTTagList = this.getEnchantments(stack);
      if (nBTTagList != null) {
         for (int index = 0; index < nBTTagList.tagCount(); index++) {
            int shortValue = nBTTagList.getCompoundTagAt(index).getShort("id");
            int shortValue2 = nBTTagList.getCompoundTagAt(index).getShort("lvl");
            if (Enchantment.getEnchantmentById(shortValue) != null) {
               tooltip.add(Enchantment.getEnchantmentById(shortValue).getTranslatedName(shortValue2));
            }
         }
      }
   }

   public ItemStack getEnchantedItemStack(EnchantmentData data) {
      ItemStack itemStack = new ItemStack(this);
      this.addEnchantment(itemStack, data);
      return itemStack;
   }

   @Override
   public EnumRarity getRarity(ItemStack stack) {
      return this.getEnchantments(stack).tagCount() > 0 ? EnumRarity.UNCOMMON : super.getRarity(stack);
   }

   public NBTTagList getEnchantments(ItemStack stack) {
      NBTTagCompound nBTTagCompound = stack.getTagCompound();
      return nBTTagCompound != null && nBTTagCompound.hasKey("StoredEnchantments", 9)
         ? (NBTTagList)nBTTagCompound.getTag("StoredEnchantments")
         : new NBTTagList();
   }

   public WeightedRandomChestContent getWeightedRandomChestContent(Random random, int i, int i2) {
      ItemStack itemStack = new ItemStack(Items.book, 1, 0);
      EnchantmentHelper.addRandomEnchantment(random, itemStack, 30);
      return new WeightedRandomChestContent(itemStack, i, i2);
   }

   public WeightedRandomChestContent getRandom(Random rand) {
      return this.getWeightedRandomChestContent(rand, 1, 1);
   }
}
