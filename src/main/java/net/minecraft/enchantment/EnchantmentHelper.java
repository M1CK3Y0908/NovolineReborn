package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.WeightedRandom;

public class EnchantmentHelper {
   private static final Random enchantmentRand = new Random();
   private static final EnchantmentHelper.ModifierDamage enchantmentModifierDamage = new EnchantmentHelper.ModifierDamage();
   private static final EnchantmentHelper.ModifierLiving enchantmentModifierLiving = new EnchantmentHelper.ModifierLiving();
   private static final EnchantmentHelper.HurtIterator ENCHANTMENT_ITERATOR_HURT = new EnchantmentHelper.HurtIterator();
   private static final EnchantmentHelper.DamageIterator ENCHANTMENT_ITERATOR_DAMAGE = new EnchantmentHelper.DamageIterator();

   public static int getMaxEnchantmentLevel(int enchID, ItemStack[] stacks) {
      if (stacks == null) {
         return 0;
      }

      int i = 0;

      for (ItemStack itemStack : stacks) {
         int enchantmentLevel2 = getEnchantmentLevel(enchID, itemStack);
         if (enchantmentLevel2 > i) {
            i = enchantmentLevel2;
         }
      }

      return i;
   }

   public static int getLuckOfSeaModifier(EntityLivingBase player) {
      return getEnchantmentLevel(Enchantment.luckOfTheSea.effectId, player.getHeldItem());
   }

   public static int getEnchantmentModifierDamage(ItemStack[] stacks, DamageSource source) {
      enchantmentModifierDamage.damageModifier = 0;
      enchantmentModifierDamage.source = source;
      applyEnchantmentModifierArray(enchantmentModifierDamage, stacks);
      if (enchantmentModifierDamage.damageModifier > 25) {
         enchantmentModifierDamage.damageModifier = 25;
      } else if (enchantmentModifierDamage.damageModifier < 0) {
         enchantmentModifierDamage.damageModifier = 0;
      }

      return (enchantmentModifierDamage.damageModifier + 1 >> 1) + enchantmentRand.nextInt((enchantmentModifierDamage.damageModifier >> 1) + 1);
   }

   public static int calcItemStackEnchantability(Random rand, int enchantNum, int power, ItemStack stack) {
      if (stack.getItem().getItemEnchantability() <= 0) {
         return 0;
      }

      if (power > 15) {
         power = 15;
      }

      int i = rand.nextInt(8) + 1 + (power >> 1) + rand.nextInt(power + 1);
      return enchantNum == 0 ? Math.max(i / 3, 1) : (enchantNum == 1 ? i * 2 / 3 + 1 : Math.max(i, power * 2));
   }

   private static void applyEnchantmentModifierArray(EnchantmentHelper.IModifier modifier, ItemStack[] stacks) {
      for (ItemStack itemStack : stacks) {
         applyEnchantmentModifier(modifier, itemStack);
      }
   }

   public static Map<Integer, EnchantmentData> mapEnchantmentData(int i, ItemStack itemStack) {
      Item item2 = itemStack.getItem();
      HashMap hashMap = null;
      int index = itemStack.getItem() == Items.book ? 1 : 0;

      for (Enchantment enchantment2 : Enchantment.enchantmentsBookList) {
         if (enchantment2 != null && (enchantment2.type.canEnchantItem(item2) || index != 0)) {
            for (int minLevel = enchantment2.getMinLevel(); minLevel <= enchantment2.getMaxLevel(); minLevel++) {
               if (i >= enchantment2.getMinEnchantability(minLevel) && i <= enchantment2.getMaxEnchantability(minLevel)) {
                  if (hashMap == null) {
                     hashMap = Maps.newHashMap();
                  }

                  hashMap.put(enchantment2.effectId, new EnchantmentData(enchantment2, minLevel));
               }
            }
         }
      }

      return hashMap;
   }

   public static int getRespiration(Entity player) {
      return getMaxEnchantmentLevel(Enchantment.respiration.effectId, player.getInventory());
   }

   public static ItemStack addRandomEnchantment(Random random, ItemStack itemStack, int i) {
      List list = buildEnchantmentList(random, itemStack, i);
      boolean flag = itemStack.getItem() == Items.book;
      if (flag) {
         itemStack.setItem(Items.enchanted_book);
      }

      if (list != null) {
         for (EnchantmentData enchantmentData : (Iterable<EnchantmentData>) list) {
            if (flag) {
               Items.enchanted_book.addEnchantment(itemStack, enchantmentData);
            } else {
               itemStack.addEnchantment(enchantmentData.enchantmentobj, enchantmentData.enchantmentLevel);
            }
         }
      }

      return itemStack;
   }

   public static int getLootingModifier(EntityLivingBase player) {
      return getEnchantmentLevel(Enchantment.looting.effectId, player.getHeldItem());
   }

   public static int getKnockbackModifier(EntityLivingBase player) {
      return getEnchantmentLevel(Enchantment.knockback.effectId, player.getHeldItem());
   }

   public static int getEfficiencyModifier(EntityLivingBase player) {
      return getEnchantmentLevel(Enchantment.efficiency.effectId, player.getHeldItem());
   }

   public static ItemStack getEnchantedItem(Enchantment enchantment2, EntityLivingBase entityLivingBase) {
      for (ItemStack itemStack : entityLivingBase.getInventory()) {
         if (itemStack != null && getEnchantmentLevel(enchantment2.effectId, itemStack) > 0) {
            return itemStack;
         }
      }

      return null;
   }

   public static int getEnchantmentLevel(int enchID, ItemStack stack) {
      if (stack == null) {
         return 0;
      }

      NBTTagList nBTTagList = stack.getEnchantmentTagList();
      if (nBTTagList == null) {
         return 0;
      }

      for (int index = 0; index < nBTTagList.tagCount(); index++) {
         int shortValue = nBTTagList.getCompoundTagAt(index).getShort("id");
         int shortValue2 = nBTTagList.getCompoundTagAt(index).getShort("lvl");
         if (shortValue == enchID) {
            return shortValue2;
         }
      }

      return 0;
   }

   public static int getFortuneModifier(EntityLivingBase player) {
      return getEnchantmentLevel(Enchantment.fortune.effectId, player.getHeldItem());
   }

   public static boolean getAquaAffinityModifier(EntityLivingBase player) {
      return getMaxEnchantmentLevel(Enchantment.aquaAffinity.effectId, player.getInventory()) > 0;
   }

   public static boolean getSilkTouchModifier(EntityLivingBase player) {
      return getEnchantmentLevel(Enchantment.silkTouch.effectId, player.getHeldItem()) > 0;
   }

   public static void setEnchantments(Map<Integer, Integer> enchMap, ItemStack stack) {
      NBTTagList nbtTagList = new NBTTagList();

      for (int i : enchMap.keySet()) {
         Enchantment enchantment = Enchantment.getEnchantmentById(i);
         if (enchantment != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setShort("id", (short)i);
            nbtTagCompound.setShort("lvl", (short)enchMap.get(i).intValue());
            nbtTagList.appendTag(nbtTagCompound);
            if (stack.getItem() == Items.enchanted_book) {
               Items.enchanted_book.addEnchantment(stack, new EnchantmentData(enchantment, enchMap.get(i)));
            }
         }
      }

      if (nbtTagList.tagCount() > 0) {
         if (stack.getItem() != Items.enchanted_book) {
            stack.setTagInfo("ench", nbtTagList);
         }
      } else if (stack.hasTagCompound()) {
         stack.getTagCompound().removeTag("ench");
      }
   }

   public static void applyThornEnchantments(EntityLivingBase entityLivingBase, Entity entity2) {
      ENCHANTMENT_ITERATOR_HURT.attacker = entity2;
      ENCHANTMENT_ITERATOR_HURT.user = entityLivingBase;
      if (entityLivingBase != null) {
         applyEnchantmentModifierArray(ENCHANTMENT_ITERATOR_HURT, entityLivingBase.getInventory());
      }

      if (entity2 instanceof EntityPlayer) {
         applyEnchantmentModifier(ENCHANTMENT_ITERATOR_HURT, entityLivingBase.getHeldItem());
      }
   }

   private static void applyEnchantmentModifier(EnchantmentHelper.IModifier modifier, ItemStack stack) {
      if (stack != null) {
         NBTTagList nBTTagList = stack.getEnchantmentTagList();
         if (nBTTagList != null) {
            for (int index = 0; index < nBTTagList.tagCount(); index++) {
               int shortValue = nBTTagList.getCompoundTagAt(index).getShort("id");
               int shortValue2 = nBTTagList.getCompoundTagAt(index).getShort("lvl");
               if (Enchantment.getEnchantmentById(shortValue) != null) {
                  modifier.calculateModifier(Enchantment.getEnchantmentById(shortValue), shortValue2);
               }
            }
         }
      }
   }

   public static void applyArthropodEnchantments(EntityLivingBase entityLivingBase, Entity entity2) {
      ENCHANTMENT_ITERATOR_DAMAGE.user = entityLivingBase;
      ENCHANTMENT_ITERATOR_DAMAGE.target = entity2;
      if (entityLivingBase != null) {
         applyEnchantmentModifierArray(ENCHANTMENT_ITERATOR_DAMAGE, entityLivingBase.getInventory());
      }

      if (entityLivingBase instanceof EntityPlayer) {
         applyEnchantmentModifier(ENCHANTMENT_ITERATOR_DAMAGE, entityLivingBase.getHeldItem());
      }
   }

   public static int getDepthStriderModifier(Entity player) {
      return getMaxEnchantmentLevel(Enchantment.depthStrider.effectId, player.getInventory());
   }

   public static List<EnchantmentData> buildEnchantmentList(Random randomIn, ItemStack itemStackIn, int i) {
      int itemEnchantability = itemStackIn.getItem().getItemEnchantability();
      if (itemEnchantability <= 0) {
         return null;
      }

      itemEnchantability /= 2;
      itemEnchantability = 1 + randomIn.nextInt((itemEnchantability >> 1) + 1) + randomIn.nextInt((itemEnchantability >> 1) + 1);
      int i2 = itemEnchantability + i;
      float f = (randomIn.nextFloat() + randomIn.nextFloat() - 1.0F) * 0.15F;
      int i3 = (int)(i2 * (1.0F + f) + 0.5F);
      if (i3 < 1) {
         i3 = 1;
      }

      ArrayList arrayList = null;
      Map map2 = mapEnchantmentData(i3, itemStackIn);
      if (map2 != null && !map2.isEmpty()) {
         EnchantmentData enchantmentData = (EnchantmentData) WeightedRandom.getRandomItem(randomIn, map2.values());
         if (enchantmentData != null) {
            arrayList = Lists.newArrayList();
            arrayList.add(enchantmentData);

            for (int i4 = i3; randomIn.nextInt(50) <= i4; i4 >>= 1) {
               Iterator iterator2 = map2.keySet().iterator();

               while (iterator2.hasNext()) {
                  Integer integer = (Integer)iterator2.next();
                  boolean flag = true;
                  Iterator iterator3 = arrayList.iterator();

                  while (iterator3.hasNext()) {
                     if (!((EnchantmentData)iterator3.next()).enchantmentobj.canApplyTogether(Enchantment.getEnchantmentById(integer))) {
                        flag = false;
                        break;
                     }
                  }

                  if (!flag) {
                     iterator2.remove();
                  }
               }

               if (!map2.isEmpty()) {
                  EnchantmentData enchantmentData2 = (EnchantmentData) WeightedRandom.getRandomItem(randomIn, map2.values());
                  arrayList.add(enchantmentData2);
               }
            }
         }
      }

      return arrayList;
   }

   public static float getModifierForCreature(ItemStack itemStack, EnumCreatureAttribute enumCreatureAttribute) {
      enchantmentModifierLiving.livingModifier = 0.0F;
      enchantmentModifierLiving.entityLiving = enumCreatureAttribute;
      applyEnchantmentModifier(enchantmentModifierLiving, itemStack);
      return enchantmentModifierLiving.livingModifier;
   }

   public static int getLureModifier(EntityLivingBase player) {
      return getEnchantmentLevel(Enchantment.lure.effectId, player.getHeldItem());
   }

   public static int getFireAspectModifier(EntityLivingBase player) {
      return getEnchantmentLevel(Enchantment.fireAspect.effectId, player.getHeldItem());
   }

   public static Map<Integer, Integer> getEnchantments(ItemStack stack) {
      Map<Integer, Integer> map = Maps.newLinkedHashMap();
      NBTTagList nBTTagList = stack.getItem() == Items.enchanted_book ? Items.enchanted_book.getEnchantments(stack) : stack.getEnchantmentTagList();
      if (nBTTagList != null) {
         for (int index = 0; index < nBTTagList.tagCount(); index++) {
            int shortValue = nBTTagList.getCompoundTagAt(index).getShort("id");
            int shortValue2 = nBTTagList.getCompoundTagAt(index).getShort("lvl");
            map.put(shortValue, shortValue2);
         }
      }

      return map;
   }

   static final class DamageIterator implements EnchantmentHelper.IModifier {
      public EntityLivingBase user;
      public Entity target;

      private DamageIterator() {
      }

      @Override
      public void calculateModifier(Enchantment enchantmentIn, int enchantmentLevel) {
         enchantmentIn.onEntityDamaged(this.user, this.target, enchantmentLevel);
      }
   }

   static final class HurtIterator implements EnchantmentHelper.IModifier {
      public EntityLivingBase user;
      public Entity attacker;

      private HurtIterator() {
      }

      @Override
      public void calculateModifier(Enchantment enchantmentIn, int enchantmentLevel) {
         enchantmentIn.onUserHurt(this.user, this.attacker, enchantmentLevel);
      }
   }

   interface IModifier {
      void calculateModifier(Enchantment enchantment2, int i);
   }

   static final class ModifierDamage implements EnchantmentHelper.IModifier {
      public int damageModifier;
      public DamageSource source;

      @Override
      public void calculateModifier(Enchantment enchantmentIn, int enchantmentLevel) {
         this.damageModifier = this.damageModifier + enchantmentIn.calcModifierDamage(enchantmentLevel, this.source);
      }

      private ModifierDamage() {
      }
   }

   static final class ModifierLiving implements EnchantmentHelper.IModifier {
      public float livingModifier;
      public EnumCreatureAttribute entityLiving;

      @Override
      public void calculateModifier(Enchantment enchantmentIn, int enchantmentLevel) {
         this.livingModifier = this.livingModifier + enchantmentIn.calcDamageByCreature(enchantmentLevel, this.entityLiving);
      }

      private ModifierLiving() {
      }
   }
}
