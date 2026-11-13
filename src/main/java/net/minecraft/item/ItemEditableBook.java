package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentProcessor;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;

public class ItemEditableBook extends Item {
   public ItemEditableBook() {
      this.setMaxStackSize(1);
   }

   @Override
   public boolean hasEffect(ItemStack stack) {
      return true;
   }

   private void resolveContents(ItemStack stack, EntityPlayer player) {
      if (stack != null && stack.getTagCompound() != null) {
         NBTTagCompound tagCompound = stack.getTagCompound();
         if (!tagCompound.getBoolean("resolved")) {
            tagCompound.setBoolean("resolved", true);
            if (validBookTagContents(tagCompound)) {
               NBTTagList tagList = tagCompound.getTagList("pages", 8);

               for (int i = 0; i < tagList.tagCount(); i++) {
                  String stringTagAt = tagList.getStringTagAt(i);

                  IChatComponent iChatComponent;
                  try {
                     iChatComponent = net.minecraft.util.IChatComponent.Serializer.jsonToComponent(stringTagAt);
                     iChatComponent = ChatComponentProcessor.processComponent(player, iChatComponent, player);
                  } catch (Exception exception) {
                     iChatComponent = new ChatComponentText(stringTagAt);
                  }

                  tagList.set(i, new NBTTagString(net.minecraft.util.IChatComponent.Serializer.componentToJson(iChatComponent)));
               }

               tagCompound.setTag("pages", tagList);
               if (player instanceof EntityPlayerMP && player.getCurrentEquippedItem() == stack) {
                  Slot slotFromInventory = player.openContainer.getSlotFromInventory(player.inventory, player.inventory.currentItem);
                  ((EntityPlayerMP)player).playerNetServerHandler.sendPacket(new S2FPacketSetSlot(0, slotFromInventory.slotNumber, stack));
               }
            }
         }
      }
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      if (!worldIn.isRemote) {
         this.resolveContents(itemStackIn, playerIn);
      }

      playerIn.displayGUIBook(itemStackIn);
      playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      return itemStackIn;
   }

   public static int getGeneration(ItemStack book) {
      return book.getTagCompound().getInteger("generation");
   }

   @Override
   public String getItemStackDisplayName(ItemStack stack) {
      if (stack.hasTagCompound()) {
         String string = stack.getTagCompound().getString("title");
         if (!StringUtils.isNullOrEmpty(string)) {
            return string;
         }
      }

      return super.getItemStackDisplayName(stack);
   }

   public static boolean validBookTagContents(NBTTagCompound nbt) {
      if (!ItemWritableBook.isNBTValid(nbt)) {
         return false;
      }

      if (!nbt.hasKey("title", 8)) {
         return false;
      }

      String text = nbt.getString("title");
      return text != null && text.length() <= 32 ? nbt.hasKey("author", 8) : false;
   }

   @Override
   public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
      if (stack.hasTagCompound()) {
         NBTTagCompound nBTTagCompound = stack.getTagCompound();
         String text = nBTTagCompound.getString("author");
         if (!StringUtils.isNullOrEmpty(text)) {
            tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocalFormatted("book.byAuthor", text));
         }

         tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("book.generation." + nBTTagCompound.getInteger("generation")));
      }
   }
}
