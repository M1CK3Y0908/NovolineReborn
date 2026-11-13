package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemPotion extends Item {
   private static final Map<List<PotionEffect>, Integer> SUB_ITEMS_CACHE = Maps.newLinkedHashMap();
   private Map<Integer, List<PotionEffect>> effectCache = Maps.newHashMap();

   public boolean isEffectInstant(int meta) {
      List effects2 = this.getEffects(meta);
      if (effects2 != null && !effects2.isEmpty()) {
         for (PotionEffect potionEffect : (Iterable<PotionEffect>) effects2) {
            if (Potion.potionTypes[potionEffect.getPotionID()].isInstant()) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public String getItemStackDisplayName(ItemStack stack) {
      if (stack.getMetadata() == 0) {
         return StatCollector.translateToLocal("item.emptyPotion.name").trim();
      }

      String text = "";
      if (isSplash(stack.getMetadata())) {
         text = StatCollector.translateToLocal("potion.prefix.grenade").trim() + " ";
      }

      List effects2 = Items.potionitem.getEffects(stack);
      if (effects2 != null && !effects2.isEmpty()) {
         String effectName = ((PotionEffect)effects2.get(0)).getEffectName();
         effectName = effectName + ".postfix";
         return text + StatCollector.translateToLocal(effectName).trim();
      } else {
         String potionPrefix = PotionHelper.getPotionPrefix(stack.getMetadata());
         return StatCollector.translateToLocal(potionPrefix).trim() + " " + super.getItemStackDisplayName(stack);
      }
   }

   public int getColorFromDamage(int meta) {
      return PotionHelper.createInt2(meta);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      if (isSplash(itemStackIn.getMetadata())) {
         if (!playerIn.capabilities.isCreativeMode) {
            itemStackIn.stackSize--;
         }

         worldIn.playSoundAtEntity(playerIn, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
         if (!worldIn.isRemote) {
            worldIn.spawnEntityInWorld(new EntityPotion(worldIn, playerIn, itemStackIn));
         }

         playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
         return itemStackIn;
      } else {
         playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
         return itemStackIn;
      }
   }

   public ItemPotion() {
      this.setMaxStackSize(1);
      this.getItem();
      this.setMaxDamage(0);
      this.setCreativeTab(CreativeTabs.tabBrewing);
   }

   @Override
   public int getMaxItemUseDuration(ItemStack stack) {
      return 32;
   }

   @Override
   public int getColorFromItemStack(ItemStack stack, int renderPass) {
      return renderPass > 0 ? 16777215 : this.getColorFromDamage(stack.getMetadata());
   }

   @Override
   public boolean hasEffect(ItemStack stack) {
      List<PotionEffect> list = this.getEffects(stack);
      return list != null && !list.isEmpty();
   }

   @Override
   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.DRINK;
   }

   public static boolean isSplash(int meta) {
      return (meta & 16384) != 0;
   }

   public List<PotionEffect> getEffects(ItemStack stack) {
      if (stack.hasTagCompound() && stack.getTagCompound().hasKey("CustomPotionEffects", 9)) {
         ArrayList arrayList = Lists.newArrayList();
         NBTTagList tagList = stack.getTagCompound().getTagList("CustomPotionEffects", 10);

         for (int i = 0; i < tagList.tagCount(); i++) {
            PotionEffect customPotionEffectFromNBT = PotionEffect.readCustomPotionEffectFromNBT(tagList.getCompoundTagAt(i));
            if (customPotionEffectFromNBT != null) {
               arrayList.add(customPotionEffectFromNBT);
            }
         }

         return arrayList;
      } else {
         List list2 = this.effectCache.get(stack.getMetadata());
         if (list2 == null) {
            list2 = PotionHelper.createList(stack.getMetadata());
            this.effectCache.put(stack.getMetadata(), list2);
         }

         return list2;
      }
   }

   @Override
   public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn) {
      if (!playerIn.capabilities.isCreativeMode) {
         stack.stackSize--;
      }

      if (!worldIn.isRemote) {
         List effects2 = this.getEffects(stack);
         if (effects2 != null) {
            for (PotionEffect potionEffect : (Iterable<PotionEffect>) effects2) {
               playerIn.addPotionEffect(new PotionEffect(potionEffect));
            }
         }
      }

      playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      if (!playerIn.capabilities.isCreativeMode) {
         if (stack.stackSize <= 0) {
            return new ItemStack(Items.glass_bottle);
         }

         playerIn.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
      }

      return stack;
   }

   @Override
   public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
      super.getSubItems(itemIn, tab, subItems);
      if (SUB_ITEMS_CACHE.isEmpty()) {
         for (int i = 0; i <= 15; i++) {
            for (int i2 = 0; i2 <= 1; i2++) {
               int i3;
               if (i2 == 0) {
                  i3 = i | 8192;
               } else {
                  i3 = i | 16384;
               }

               for (int i4 = 0; i4 <= 2; i4++) {
                  int i5 = i3;
                  if (i4 != 0) {
                     if (i4 == 1) {
                        i5 = i3 | 32;
                     } else if (i4 == 2) {
                        i5 = i3 | 64;
                     }
                  }

                  List list2 = PotionHelper.createList(i5);
                  if (list2 != null && !list2.isEmpty()) {
                     SUB_ITEMS_CACHE.put(list2, i5);
                  }
               }
            }
         }
      }

      for (int i6 : SUB_ITEMS_CACHE.values()) {
         subItems.add(new ItemStack(itemIn, 1, i6));
      }
   }

   @Override
   public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
      if (stack.getMetadata() != 0) {
         List effects2 = Items.potionitem.getEffects(stack);
         HashMultimap hashMultimap = HashMultimap.create();
         if (effects2 != null && !effects2.isEmpty()) {
            for (PotionEffect potionEffect : (Iterable<PotionEffect>) effects2) {
               String text = StatCollector.translateToLocal(potionEffect.getEffectName()).trim();
               Potion potion2 = Potion.potionTypes[potionEffect.getPotionID()];
               Map attributeModifierMap = potion2.getAttributeModifierMap();
               if (attributeModifierMap != null && attributeModifierMap.size() > 0) {
                  for (Entry entry : (Iterable<Map.Entry>) attributeModifierMap.entrySet()) {
                     AttributeModifier attributeModifier = (AttributeModifier)entry.getValue();
                     AttributeModifier attributeModifier2 = new AttributeModifier(
                        attributeModifier.getName(), potion2.getAttributeModifierAmount(potionEffect.getAmplifier(), attributeModifier), attributeModifier.getOperation()
                     );
                     hashMultimap.put(((IAttribute)entry.getKey()).getAttributeUnlocalizedName(), attributeModifier2);
                  }
               }

               if (potionEffect.getAmplifier() > 0) {
                  text = text + " " + StatCollector.translateToLocal("potion.potency." + potionEffect.getAmplifier()).trim();
               }

               if (potionEffect.getDuration() > 20) {
                  text = text + " (" + Potion.getDurationString(potionEffect) + ")";
               }

               if (potion2.isBadEffect()) {
                  tooltip.add(EnumChatFormatting.RED + text);
               } else {
                  tooltip.add(EnumChatFormatting.GRAY + text);
               }
            }
         } else {
            String text2 = StatCollector.translateToLocal("potion.empty").trim();
            tooltip.add(EnumChatFormatting.GRAY + text2);
         }

         if (!hashMultimap.isEmpty()) {
            tooltip.add("");
            tooltip.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("potion.effects.whenDrank"));

            for (Entry entry2 : (Iterable<Map.Entry>) hashMultimap.entries()) {
               AttributeModifier attributeModifier3 = (AttributeModifier)entry2.getValue();
               double amount = attributeModifier3.getAmount();
               double d;
               if (attributeModifier3.getOperation() != 1 && attributeModifier3.getOperation() != 2) {
                  d = attributeModifier3.getAmount();
               } else {
                  d = attributeModifier3.getAmount() * 100.0;
               }

               if (amount > 0.0) {
                  tooltip.add(
                     EnumChatFormatting.BLUE
                        + StatCollector.translateToLocalFormatted(
                           "attribute.modifier.plus." + attributeModifier3.getOperation(),
                           ItemStack.DECIMALFORMAT.format(d),
                           StatCollector.translateToLocal("attribute.name." + (String)entry2.getKey())
                        )
                  );
               } else if (amount < 0.0) {
                  d *= -1.0;
                  tooltip.add(
                     EnumChatFormatting.RED
                        + StatCollector.translateToLocalFormatted(
                           "attribute.modifier.take." + attributeModifier3.getOperation(),
                           ItemStack.DECIMALFORMAT.format(d),
                           StatCollector.translateToLocal("attribute.name." + (String)entry2.getKey())
                        )
                  );
               }
            }
         }
      }
   }

   public List<PotionEffect> getEffects(int meta) {
      List<PotionEffect> list = this.effectCache.get(meta);
      if (list == null) {
         list = PotionHelper.createList(meta);
         this.effectCache.put(meta, list);
      }

      return list;
   }
}
