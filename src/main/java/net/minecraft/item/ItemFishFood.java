package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemFishFood extends ItemFood {
   private final boolean cooked;

   @Override
   public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
      for (ItemFishFood.FishType fishType : ItemFishFood.FishType.values()) {
         if (!this.cooked || fishType.canCook()) {
            subItems.add(new ItemStack(this, 1, fishType.getMetadata()));
         }
      }
   }

   public ItemFishFood(boolean cooked) {
      super(0, 0.0F, false);
      this.cooked = cooked;
   }

   @Override
   public String getUnlocalizedName(ItemStack stack) {
      ItemFishFood.FishType itemfishfood$fishtype = ItemFishFood.FishType.byItemStack(stack);
      return this.getUnlocalizedName()
         + "."
         + itemfishfood$fishtype.getUnlocalizedName()
         + "."
         + (this.cooked && itemfishfood$fishtype.canCook() ? "cooked" : "raw");
   }

   @Override
   protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
      if (ItemFishFood.FishType.byItemStack(stack) == ItemFishFood.FishType.PUFFERFISH) {
         player.addPotionEffect(new PotionEffect(Potion.poison.id, 1200, 3));
         player.addPotionEffect(new PotionEffect(Potion.hunger.id, 300, 2));
         player.addPotionEffect(new PotionEffect(Potion.confusion.id, 300, 1));
      }

      super.onFoodEaten(stack, worldIn, player);
   }

   @Override
   public String getPotionEffect(ItemStack stack) {
      return ItemFishFood.FishType.byItemStack(stack) == ItemFishFood.FishType.PUFFERFISH ? "+0-1+2+3+13&4-4" : null;
   }

   @Override
   public int getHealAmount(ItemStack stack) {
      ItemFishFood.FishType itemfishfood$fishtype = ItemFishFood.FishType.byItemStack(stack);
      return this.cooked && itemfishfood$fishtype.canCook() ? itemfishfood$fishtype.getCookedHealAmount() : itemfishfood$fishtype.getUncookedHealAmount();
   }

   @Override
   public float getSaturationModifier(ItemStack stack) {
      ItemFishFood.FishType itemfishfood$fishtype = ItemFishFood.FishType.byItemStack(stack);
      return this.cooked && itemfishfood$fishtype.canCook()
         ? itemfishfood$fishtype.getCookedSaturationModifier()
         : itemfishfood$fishtype.getUncookedSaturationModifier();
   }

   public enum FishType {
      COD(0, "cod", 0.1F, 5, 0.6F),
      SALMON(1, "salmon", 0.1F, 6, 0.8F),
      CLOWNFISH(2, "clownfish", 0.1F),
      PUFFERFISH(3, "pufferfish", 0.1F);

      private boolean cookable = false;
      private final int meta;
      private final String unlocalizedName;
      private final int uncookedHealAmount;
      private final float uncookedSaturationModifier;
      private final int cookedHealAmount;
      private static final Map<Integer, ItemFishFood.FishType> META_LOOKUP = Maps.newHashMap();
      private final float cookedSaturationModifier;

      FishType(int i, String text, float f, int i2, float f2) {
         this.meta = i;
         this.unlocalizedName = text;
         this.uncookedHealAmount = 2;
         this.uncookedSaturationModifier = f;
         this.cookedHealAmount = i2;
         this.cookedSaturationModifier = f2;
         this.cookable = true;
      }

      public boolean canCook() {
         return this.cookable;
      }

      public int getCookedHealAmount() {
         return this.cookedHealAmount;
      }

      public float getUncookedSaturationModifier() {
         return this.uncookedSaturationModifier;
      }

      public static ItemFishFood.FishType byMetadata(int meta) {
         ItemFishFood.FishType itemfishfood$fishtype = META_LOOKUP.get(meta);
         return itemfishfood$fishtype == null ? COD : itemfishfood$fishtype;
      }

      public static ItemFishFood.FishType byItemStack(ItemStack stack) {
         return stack.getItem() instanceof ItemFishFood ? byMetadata(stack.getMetadata()) : COD;
      }

      public String getUnlocalizedName() {
         return this.unlocalizedName;
      }

      static {
         for (ItemFishFood.FishType fishType : values()) {
            META_LOOKUP.put(fishType.getMetadata(), fishType);
         }
      }

      public int getMetadata() {
         return this.meta;
      }

      public int getUncookedHealAmount() {
         return this.uncookedHealAmount;
      }

      public float getCookedSaturationModifier() {
         return this.cookedSaturationModifier;
      }

      FishType(int i, String text, float f) {
         this.meta = i;
         this.unlocalizedName = text;
         this.uncookedHealAmount = 1;
         this.uncookedSaturationModifier = f;
         this.cookedHealAmount = 0;
         this.cookedSaturationModifier = 0.0F;
         this.cookable = false;
      }
   }
}
