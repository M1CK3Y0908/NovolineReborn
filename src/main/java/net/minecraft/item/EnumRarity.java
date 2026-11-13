package net.minecraft.item;

import net.minecraft.util.EnumChatFormatting;

public enum EnumRarity {
   COMMON(EnumChatFormatting.WHITE, "Common"),
   UNCOMMON(EnumChatFormatting.YELLOW, "Uncommon"),
   RARE(EnumChatFormatting.AQUA, "Rare"),
   EPIC(EnumChatFormatting.LIGHT_PURPLE, "Epic");

   public final EnumChatFormatting rarityColor;
   public final String rarityName;

   EnumRarity(EnumChatFormatting enumChatFormatting, String text) {
      this.rarityColor = enumChatFormatting;
      this.rarityName = text;
   }
}
