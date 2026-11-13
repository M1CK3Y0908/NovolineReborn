package net.minecraft.item;

import com.google.common.base.Predicates;
import java.util.List;
import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EntitySelectors.ArmoredMob;
import net.minecraft.world.World;

public class ItemArmor extends Item {
   private final ItemArmor.ArmorMaterial material;
   private static final int[] maxDamageArray = new int[]{11, 16, 15, 13};
   public final int armorType;
   public static final String[] EMPTY_SLOT_NAMES = new String[]{
      "minecraft:items/empty_armor_slot_helmet",
      "minecraft:items/empty_armor_slot_chestplate",
      "minecraft:items/empty_armor_slot_leggings",
      "minecraft:items/empty_armor_slot_boots"
   };
   public final int renderIndex;
   public final int damageReduceAmount;
   private static final IBehaviorDispenseItem dispenserBehavior = new BehaviorDefaultDispenseItem() {
      @Override
      protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
         BlockPos blockPos = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
         int xCoordinate = blockPos.getX();
         int yCoordinate = blockPos.getY();
         int zCoordinate = blockPos.getZ();
         AxisAlignedBB axisAlignedBB = new AxisAlignedBB(xCoordinate, yCoordinate, zCoordinate, xCoordinate + 1, yCoordinate + 1, zCoordinate + 1);
         List<EntityLivingBase> list = source.getWorld()
            .getEntitiesWithinAABB(EntityLivingBase.class, axisAlignedBB, Predicates.and(EntitySelectors.NOT_SPECTATING, new ArmoredMob(stack)));
         if (list.size() > 0) {
            EntityLivingBase entityLivingBase = list.get(0);
            int i = entityLivingBase instanceof EntityPlayer ? 1 : 0;
            int armorPosition = EntityLiving.getArmorPosition(stack);
            ItemStack itemStack = stack.copy();
            itemStack.stackSize = 1;
            entityLivingBase.setCurrentItemOrArmor(armorPosition - i, itemStack);
            if (entityLivingBase instanceof EntityLiving) {
               ((EntityLiving)entityLivingBase).setEquipmentDropChance(armorPosition, 2.0F);
            }

            stack.stackSize--;
            return stack;
         } else {
            return super.dispenseStack(source, stack);
         }
      }
   };

   public ItemArmor(ItemArmor.ArmorMaterial material, int renderIndex, int armorType) {
      this.material = material;
      this.armorType = armorType;
      this.renderIndex = renderIndex;
      this.damageReduceAmount = material.getDamageReductionAmount(armorType);
      this.setMaxDamage(material.getDurability(armorType));
      this.maxStackSize = 1;
      this.setCreativeTab(CreativeTabs.tabCombat);
      BlockDispenser.dispenseBehaviorRegistry.putObject(this, dispenserBehavior);
   }

   public void removeColor(ItemStack stack) {
      if (this.material == ItemArmor.ArmorMaterial.LEATHER) {
         NBTTagCompound nBTTagCompound = stack.getTagCompound();
         if (nBTTagCompound != null) {
            NBTTagCompound nBTTagCompound2 = nBTTagCompound.getCompoundTag("display");
            if (nBTTagCompound2.hasKey("color")) {
               nBTTagCompound2.removeTag("color");
            }
         }
      }
   }

   public ItemArmor.ArmorMaterial getArmorMaterial() {
      return this.material;
   }

   @Override
   public int getItemEnchantability() {
      return this.material.getEnchantability();
   }

   public void setColor(ItemStack stack, int color) {
      if (this.material != ItemArmor.ArmorMaterial.LEATHER) {
         throw new UnsupportedOperationException("Can't dye non-leather!");
      }

      NBTTagCompound nBTTagCompound = stack.getTagCompound();
      if (nBTTagCompound == null) {
         nBTTagCompound = new NBTTagCompound();
         stack.setTagCompound(nBTTagCompound);
      }

      NBTTagCompound nBTTagCompound2 = nBTTagCompound.getCompoundTag("display");
      if (!nBTTagCompound.hasKey("display", 10)) {
         nBTTagCompound.setTag("display", nBTTagCompound2);
      }

      nBTTagCompound2.setInteger("color", color);
   }

   public boolean hasColor(ItemStack stack) {
      return this.material != ItemArmor.ArmorMaterial.LEATHER
         ? false
         : (
            !stack.hasTagCompound()
               ? false
               : (!stack.getTagCompound().hasKey("display", 10) ? false : stack.getTagCompound().getCompoundTag("display").hasKey("color", 3))
         );
   }

   @Override
   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      return this.material.getRepairItem() == repair.getItem() ? true : super.getIsRepairable(toRepair, repair);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      int i = EntityLiving.getArmorPosition(itemStackIn) - 1;
      if (playerIn.getCurrentArmor(i) == null) {
         playerIn.setCurrentItemOrArmor(i, itemStackIn.copy());
         itemStackIn.stackSize = 0;
      }

      return itemStackIn;
   }

   @Override
   public int getColorFromItemStack(ItemStack stack, int renderPass) {
      if (renderPass > 0) {
         return 16777215;
      }

      int color2 = this.getColor(stack);
      if (color2 < 0) {
         color2 = 16777215;
      }

      return color2;
   }

   public int getColor(ItemStack stack) {
      if (this.material != ItemArmor.ArmorMaterial.LEATHER) {
         return -1;
      }

      NBTTagCompound nBTTagCompound = stack.getTagCompound();
      if (nBTTagCompound != null) {
         NBTTagCompound nBTTagCompound2 = nBTTagCompound.getCompoundTag("display");
         if (nBTTagCompound2 != null && nBTTagCompound2.hasKey("color", 3)) {
            return nBTTagCompound2.getInteger("color");
         }
      }

      return 10511680;
   }

   public enum ArmorMaterial {
      LEATHER("leather", 5, new int[]{1, 3, 2, 1}, 15),
      CHAIN("chainmail", 15, new int[]{2, 5, 4, 1}, 12),
      IRON("iron", 15, new int[]{2, 6, 5, 2}, 9),
      GOLD("gold", 7, new int[]{2, 5, 3, 1}, 25),
      DIAMOND("diamond", 33, new int[]{3, 8, 6, 3}, 10);

      private final String name;
      private final int maxDamageFactor;
      private final int[] damageReductionAmountArray;
      private final int enchantability;

      ArmorMaterial(String text, int i, int[] iArray, int i2) {
         this.name = text;
         this.maxDamageFactor = i;
         this.damageReductionAmountArray = iArray;
         this.enchantability = i2;
      }

      public int getDurability(int armorType) {
         return ItemArmor.maxDamageArray[armorType] * this.maxDamageFactor;
      }

      public int getDamageReductionAmount(int armorType) {
         return this.damageReductionAmountArray[armorType];
      }

      public int getEnchantability() {
         return this.enchantability;
      }

      public String getName() {
         return this.name;
      }

      public Item getRepairItem() {
         return this == LEATHER
            ? Items.leather
            : (
               this == CHAIN
                  ? Items.iron_ingot
                  : (this == GOLD ? Items.gold_ingot : (this == IRON ? Items.iron_ingot : (this == DIAMOND ? Items.diamond : null)))
            );
      }
   }
}
