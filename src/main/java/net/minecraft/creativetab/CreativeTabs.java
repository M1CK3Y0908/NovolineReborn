package net.minecraft.creativetab;

import java.util.List;
import net.minecraft.block.BlockDoublePlant.EnumPlantType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class CreativeTabs {
   public static final CreativeTabs[] creativeTabArray = new CreativeTabs[12];
   private String theTexture = "items.png";
   public static final CreativeTabs tabBlock = new CreativeTabs(0, "buildingBlocks") {
      @Override
      public Item getTabIconItem() {
         return Item.getItemFromBlock(Blocks.brick_block);
      }
   };
   private boolean hasScrollbar = true;
   public static final CreativeTabs tabDecorations = new CreativeTabs(1, "decorations") {
      @Override
      public Item getTabIconItem() {
         return Item.getItemFromBlock(Blocks.double_plant);
      }

      @Override
      public int getIconItemDamage() {
         return EnumPlantType.PAEONIA.getMeta();
      }
   };
   public static final CreativeTabs tabRedstone = new CreativeTabs(2, "redstone") {
      @Override
      public Item getTabIconItem() {
         return Items.redstone;
      }
   };
   public static final CreativeTabs tabTransport = new CreativeTabs(3, "transportation") {
      @Override
      public Item getTabIconItem() {
         return Item.getItemFromBlock(Blocks.golden_rail);
      }
   };
   public static final CreativeTabs tabMisc = (new CreativeTabs(4, "misc") {
      @Override
      public Item getTabIconItem() {
         return Items.lava_bucket;
      }
   }).setRelevantEnchantmentTypes(EnumEnchantmentType.ALL);
   private boolean drawTitle = true;
   public static final CreativeTabs tabAllSearch = (new CreativeTabs(5, "search") {
      @Override
      public Item getTabIconItem() {
         return Items.compass;
      }
   }).setBackgroundImageName("item_search.png");
   private EnumEnchantmentType[] enchantmentTypes;
   private final int tabIndex;
   public static final CreativeTabs tabFood = new CreativeTabs(6, "food") {
      @Override
      public Item getTabIconItem() {
         return Items.apple;
      }
   };
   public static final CreativeTabs tabTools = (new CreativeTabs(7, "tools") {
      @Override
      public Item getTabIconItem() {
         return Items.iron_axe;
      }
   }).setRelevantEnchantmentTypes(EnumEnchantmentType.DIGGER, EnumEnchantmentType.FISHING_ROD, EnumEnchantmentType.BREAKABLE);
   public static final CreativeTabs tabCombat = (new CreativeTabs(8, "combat") {
         @Override
         public Item getTabIconItem() {
            return Items.golden_sword;
         }
      })
      .setRelevantEnchantmentTypes(
         EnumEnchantmentType.ARMOR,
         EnumEnchantmentType.ARMOR_FEET,
         EnumEnchantmentType.ARMOR_HEAD,
         EnumEnchantmentType.ARMOR_LEGS,
         EnumEnchantmentType.ARMOR_TORSO,
         EnumEnchantmentType.BOW,
         EnumEnchantmentType.WEAPON
      );
   private ItemStack iconItemStack;
   public static final CreativeTabs tabBrewing = new CreativeTabs(9, "brewing") {
      @Override
      public Item getTabIconItem() {
         return Items.potionitem;
      }
   };
   private final String tabLabel;
   public static final CreativeTabs tabMaterials = new CreativeTabs(10, "materials") {
      @Override
      public Item getTabIconItem() {
         return Items.stick;
      }
   };
   public static final CreativeTabs tabInventory = (new CreativeTabs(11, "inventory") {
      @Override
      public Item getTabIconItem() {
         return Item.getItemFromBlock(Blocks.chest);
      }
   }).setBackgroundImageName("inventory.png").setNoScrollbar().setNoTitle();

   public String getTranslatedTabLabel() {
      return "itemGroup." + this.getTabLabel();
   }

   public void displayAllRelevantItems(List<ItemStack> items) {
      for (Item item2 : Item.itemRegistry) {
         if (item2 != null && item2.getCreativeTab() == this) {
            item2.getSubItems(item2, this, items);
         }
      }

      if (this.getRelevantEnchantmentTypes() != null) {
         this.addEnchantmentBooksToList(items, this.getRelevantEnchantmentTypes());
      }
   }

   public abstract Item getTabIconItem();

   public ItemStack getIconItemStack() {
      if (this.iconItemStack == null) {
         this.iconItemStack = new ItemStack(this.getTabIconItem(), 1, this.getIconItemDamage());
      }

      return this.iconItemStack;
   }

   public boolean hasRelevantEnchantmentType(EnumEnchantmentType enchantmentType) {
      if (this.enchantmentTypes == null) {
         return false;
      }

      EnumEnchantmentType[] enchantmentTypes2 = this.enchantmentTypes;
      int length2 = enchantmentTypes2.length;

      for (int i = 0; i < length2; i++) {
         if (enchantmentTypes2[i] == enchantmentType) {
            return true;
         }
      }

      return false;
   }

   public CreativeTabs setNoTitle() {
      this.drawTitle = false;
      return this;
   }

   public CreativeTabs(int index, String label) {
      this.tabIndex = index;
      this.tabLabel = label;
      creativeTabArray[index] = this;
   }

   public int getIconItemDamage() {
      return 0;
   }

   public CreativeTabs setBackgroundImageName(String texture) {
      this.theTexture = texture;
      return this;
   }

   public boolean isTabInFirstRow() {
      return this.tabIndex < 6;
   }

   public CreativeTabs setRelevantEnchantmentTypes(EnumEnchantmentType... types) {
      this.enchantmentTypes = types;
      return this;
   }

   public boolean drawInForegroundOfTab() {
      return this.drawTitle;
   }

   public EnumEnchantmentType[] getRelevantEnchantmentTypes() {
      return this.enchantmentTypes;
   }

   public CreativeTabs setNoScrollbar() {
      this.hasScrollbar = false;
      return this;
   }

   public String getTabLabel() {
      return this.tabLabel;
   }

   public void addEnchantmentBooksToList(List<ItemStack> itemList, EnumEnchantmentType... enchantmentType) {
      for (Enchantment enchantment2 : Enchantment.enchantmentsBookList) {
         if (enchantment2 != null && enchantment2.type != null) {
            boolean flag = false;

            for (int i = 0; i < enchantmentType.length && !flag; i++) {
               if (enchantment2.type == enchantmentType[i]) {
                  flag = true;
               }
            }

            if (flag) {
               itemList.add(Items.enchanted_book.getEnchantedItemStack(new EnchantmentData(enchantment2, enchantment2.getMaxLevel())));
            }
         }
      }
   }

   public String getBackgroundImageName() {
      return this.theTexture;
   }

   public int getTabColumn() {
      return this.tabIndex % 6;
   }

   public boolean shouldHidePlayerInventory() {
      return this.hasScrollbar;
   }

   public int getTabIndex() {
      return this.tabIndex;
   }
}
