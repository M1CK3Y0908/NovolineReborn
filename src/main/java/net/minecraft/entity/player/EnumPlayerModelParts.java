package net.minecraft.entity.player;

import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public enum EnumPlayerModelParts {
   CAPE(0, "cape"),
   JACKET(1, "jacket"),
   LEFT_SLEEVE(2, "left_sleeve"),
   RIGHT_SLEEVE(3, "right_sleeve"),
   LEFT_PANTS_LEG(4, "left_pants_leg"),
   RIGHT_PANTS_LEG(5, "right_pants_leg"),
   HAT(6, "hat");

   private final int partId;
   private final int partMask;
   private final String partName;
   private final IChatComponent nameComponent;

   public String getPartName() {
      return this.partName;
   }

   public int getPartId() {
      return this.partId;
   }

   public int getPartMask() {
      return this.partMask;
   }

   EnumPlayerModelParts(int i, String text) {
      this.partId = i;
      this.partMask = 1 << i;
      this.partName = text;
      this.nameComponent = new ChatComponentTranslation("options.modelPart." + text);
   }

   public IChatComponent getNameComponent() {
      return this.nameComponent;
   }
}
