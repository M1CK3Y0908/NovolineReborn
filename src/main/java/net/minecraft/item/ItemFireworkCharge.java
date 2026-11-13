package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.StatCollector;

public class ItemFireworkCharge extends Item {
   public static NBTBase getExplosionTag(ItemStack stack, String key) {
      if (stack.hasTagCompound()) {
         NBTTagCompound nBTTagCompound = stack.getTagCompound().getCompoundTag("Explosion");
         if (nBTTagCompound != null) {
            return nBTTagCompound.getTag(key);
         }
      }

      return null;
   }

   @Override
   public int getColorFromItemStack(ItemStack stack, int renderPass) {
      if (renderPass != 1) {
         return super.getColorFromItemStack(stack, renderPass);
      }

      NBTBase explosionTag = getExplosionTag(stack, "Colors");
      if (!(explosionTag instanceof NBTTagIntArray)) {
         return 9079434;
      }

      int[] intArray = ((NBTTagIntArray)explosionTag).getIntArray();
      if (intArray.length == 1) {
         return intArray[0];
      }

      int i = 0;
      int i2 = 0;
      int i3 = 0;

      for (int i4 : intArray) {
         i += (i4 & 0xFF0000) >> 16;
         i2 += (i4 & 0xFF00) >> 8;
         i3 += (i4 & 0xFF) >> 0;
      }

      i /= intArray.length;
      i2 /= intArray.length;
      i3 /= intArray.length;
      return i << 16 | i2 << 8 | i3;
   }

   @Override
   public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
      if (stack.hasTagCompound()) {
         NBTTagCompound nBTTagCompound = stack.getTagCompound().getCompoundTag("Explosion");
         if (nBTTagCompound != null) {
            addExplosionInfo(nBTTagCompound, tooltip);
         }
      }
   }

   public static void addExplosionInfo(NBTTagCompound nbt, List<String> tooltip) {
      byte byteValue = nbt.getByte("Type");
      if (byteValue >= 0 && byteValue <= 4) {
         tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.type." + byteValue).trim());
      } else {
         tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.type").trim());
      }

      int[] intArray = nbt.getIntArray("Colors");
      if (intArray.length > 0) {
         boolean flag = true;
         String text = "";

         for (int i : intArray) {
            if (!flag) {
               text = text + ", ";
            }

            flag = false;
            boolean flag2 = false;

            for (int i2 = 0; i2 < ItemDye.dyeColors.length; i2++) {
               if (i == ItemDye.dyeColors[i2]) {
                  flag2 = true;
                  text = text + StatCollector.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(i2).getUnlocalizedName());
                  break;
               }
            }

            if (!flag2) {
               text = text + StatCollector.translateToLocal("item.fireworksCharge.customColor");
            }
         }

         tooltip.add(text);
      }

      int[] intArray2 = nbt.getIntArray("FadeColors");
      if (intArray2.length > 0) {
         boolean flag3 = true;
         String text2 = StatCollector.translateToLocal("item.fireworksCharge.fadeTo") + " ";

         for (int i3 : intArray2) {
            if (!flag3) {
               text2 = text2 + ", ";
            }

            flag3 = false;
            boolean flag4 = false;

            for (int i4 = 0; i4 < 16; i4++) {
               if (i3 == ItemDye.dyeColors[i4]) {
                  flag4 = true;
                  text2 = text2 + StatCollector.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(i4).getUnlocalizedName());
                  break;
               }
            }

            if (!flag4) {
               text2 = text2 + StatCollector.translateToLocal("item.fireworksCharge.customColor");
            }
         }

         tooltip.add(text2);
      }

      boolean booleanValue = nbt.getBoolean("Trail");
      if (booleanValue) {
         tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.trail"));
      }

      boolean booleanValue2 = nbt.getBoolean("Flicker");
      if (booleanValue2) {
         tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.flicker"));
      }
   }
}
