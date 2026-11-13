package wtf.novoline.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class InventoryUtils {
   private static final Minecraft minecraft = Minecraft.getMinecraft();

   public static int findBestArmorSlot(int i) {
      int i2 = -1;
      int i3 = -1;

      for (int i4 = 0; i4 < minecraft.thePlayer.inventory.getSizeInventory(); i4++) {
         ItemStack stackInSlot = minecraft.thePlayer.inventory.getStackInSlot(i4);
         if (stackInSlot != null) {
            Item item2 = stackInSlot.getItem();
            if (item2 instanceof ItemArmor && ((ItemArmor)item2).armorType == i) {
               int armorScore = ItemUtils.getArmorScore(stackInSlot);
               if (armorScore >= i3) {
                  i3 = armorScore;
                  i2 = i4;
               }
            }
         }
      }

      return i2;
   }

   public static int findBlockSlot() {
      return findSlotWithBlocks(false);
   }

   public static int findEmptyHotbarSlot() {
      for (int i = 0; i < 9; i++) {
         ItemStack itemStack = minecraft.thePlayer.inventory.mainInventory[i];
         if (itemStack == null || itemStack.getItem() == null) {
            return i;
         }
      }

      return -1;
   }

   public static int findSlotWithBlocks(boolean flag) {
      int i = -1;
      int i2 = -1;

      for (int i3 = 0; i3 < minecraft.thePlayer.inventory.mainInventory.length; i3++) {
         ItemStack itemStack = minecraft.thePlayer.inventory.mainInventory[i3];
         if (itemStack != null) {
            Item item2 = itemStack.getItem();
            if (item2 instanceof ItemBlock && ((ItemBlock)item2).getBlock().isFullCube()) {
               if (!flag) {
                  return i3;
               }

               if (i2 < itemStack.stackSize) {
                  i2 = itemStack.stackSize;
                  i = i3;
               }
            }
         }
      }

      return i;
   }

   public static int findBestItemSlot(Class clazz) {
      int i = -1;
      int i2 = -1;

      for (int i3 = 0; i3 < minecraft.thePlayer.inventory.getSizeInventory(); i3++) {
         ItemStack stackInSlot = minecraft.thePlayer.inventory.getStackInSlot(i3);
         if (stackInSlot != null && stackInSlot.getItem() != null && stackInSlot.getItem().getClass().equals(clazz)) {
            int toolScore = ItemUtils.getToolScore(stackInSlot);
            if (toolScore > i2) {
               i2 = toolScore;
               i = i3;
            }
         }
      }

      return i;
   }

   public static int findSlotOfClass(Class clazz, boolean flag) {
      if (flag) {
         for (int i = 0; i < 9; i++) {
            ItemStack itemStack = minecraft.thePlayer.inventory.mainInventory[i % 9];
            if (itemStack != null && itemStack.getItem() != null && clazz.isInstance(itemStack.getItem())) {
               return i;
            }
         }
      } else {
         for (int i2 = 0; i2 < minecraft.thePlayer.inventory.mainInventory.length; i2++) {
            ItemStack itemStack2 = minecraft.thePlayer.inventory.mainInventory[i2];
            if (itemStack2 != null && itemStack2.getItem() != null && clazz.isInstance(itemStack2.getItem())) {
               return i2;
            }
         }
      }

      return -1;
   }
}
