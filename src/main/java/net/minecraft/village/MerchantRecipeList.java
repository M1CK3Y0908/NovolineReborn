package net.minecraft.village;

import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;

public class MerchantRecipeList extends ArrayList<MerchantRecipe> {
   public MerchantRecipeList(NBTTagCompound compound) {
      this.readRecipiesFromTags(compound);
   }

   public MerchantRecipeList() {
   }

   public void readRecipiesFromTags(NBTTagCompound compound) {
      NBTTagList nBTTagList = compound.getTagList("Recipes", 10);

      for (int index = 0; index < nBTTagList.tagCount(); index++) {
         NBTTagCompound nBTTagCompound = nBTTagList.getCompoundTagAt(index);
         this.add(new MerchantRecipe(nBTTagCompound));
      }
   }

   public void writeToBuf(PacketBuffer buffer) {
      buffer.writeByte((byte)(this.size() & 0xFF));

      for (int index = 0; index < this.size(); index++) {
         MerchantRecipe merchantRecipe = this.get(index);
         buffer.writeItemStackToBuffer(merchantRecipe.getItemToBuy());
         buffer.writeItemStackToBuffer(merchantRecipe.getItemToSell());
         ItemStack itemStack = merchantRecipe.getSecondItemToBuy();
         buffer.writeBoolean(itemStack != null);
         if (itemStack != null) {
            buffer.writeItemStackToBuffer(itemStack);
         }

         buffer.writeBoolean(merchantRecipe.isRecipeDisabled());
         buffer.writeInt(merchantRecipe.getToolUses());
         buffer.writeInt(merchantRecipe.getMaxTradeUses());
      }
   }

   public static MerchantRecipeList readFromBuf(PacketBuffer buffer) throws IOException {
      MerchantRecipeList merchantRecipeList = new MerchantRecipeList();
      int i = buffer.readByte() & 255;

      for (int innerIndex = 0; innerIndex < i; innerIndex++) {
         ItemStack itemStack = buffer.readItemStackFromBuffer();
         ItemStack itemStack2 = buffer.readItemStackFromBuffer();
         ItemStack itemStack3 = null;
         if (buffer.readBoolean()) {
            itemStack3 = buffer.readItemStackFromBuffer();
         }

         boolean conditionMet = buffer.readBoolean();
         int intValue = buffer.readInt();
         int intValue2 = buffer.readInt();
         MerchantRecipe merchantRecipe3 = new MerchantRecipe(itemStack, itemStack3, itemStack2, intValue, intValue2);
         if (conditionMet) {
            merchantRecipe3.compensateToolUses();
         }

         merchantRecipeList.add(merchantRecipe3);
      }

      return merchantRecipeList;
   }

   public NBTTagCompound getRecipiesAsTags() {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      NBTTagList nbtTagList = new NBTTagList();

      for (int index = 0; index < this.size(); index++) {
         MerchantRecipe merchantRecipe = this.get(index);
         nbtTagList.appendTag(merchantRecipe.writeToTags());
      }

      nbtTagCompound.setTag("Recipes", nbtTagList);
      return nbtTagCompound;
   }

   public MerchantRecipe canRecipeBeUsed(ItemStack itemStack4, ItemStack itemStack5, int i) {
      if (i > 0 && i < this.size()) {
         MerchantRecipe merchantRecipe = this.get(i);
         return this.areItemsEqual(itemStack4, merchantRecipe.getItemToBuy())
               && (
                  itemStack5 == null && !merchantRecipe.hasSecondItemToBuy()
                     || merchantRecipe.hasSecondItemToBuy() && this.areItemsEqual(itemStack5, merchantRecipe.getSecondItemToBuy())
               )
               && itemStack4.stackSize >= merchantRecipe.getItemToBuy().stackSize
               && (!merchantRecipe.hasSecondItemToBuy() || itemStack5.stackSize >= merchantRecipe.getSecondItemToBuy().stackSize)
            ? merchantRecipe
            : null;
      }

      for (int index = 0; index < this.size(); index++) {
         MerchantRecipe merchantRecipe2 = this.get(index);
         if (this.areItemsEqual(itemStack4, merchantRecipe2.getItemToBuy())
            && itemStack4.stackSize >= merchantRecipe2.getItemToBuy().stackSize
            && (
               !merchantRecipe2.hasSecondItemToBuy() && itemStack5 == null
                  || merchantRecipe2.hasSecondItemToBuy()
                     && this.areItemsEqual(itemStack5, merchantRecipe2.getSecondItemToBuy())
                     && itemStack5.stackSize >= merchantRecipe2.getSecondItemToBuy().stackSize
            )) {
            return merchantRecipe2;
         }
      }

      return null;
   }

   private boolean areItemsEqual(ItemStack first, ItemStack second) {
      return ItemStack.areItemsEqual(first, second)
         && (!second.hasTagCompound() || first.hasTagCompound() && NBTUtil.areNBTEquals(second.getTagCompound(), first.getTagCompound(), false));
   }
}
