package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class TileEntityBanner extends TileEntity {
   private String patternResourceLocation;
   private List<TileEntityBanner.EnumBannerPattern> patternList;
   private int baseColor;
   private List<EnumDyeColor> colorList;
   private NBTTagList patterns;
   private boolean hasBannerData;

   public static int getBaseColor(ItemStack stack) {
      NBTTagCompound nBTTagCompound = stack.getSubCompound("BlockEntityTag", false);
      return nBTTagCompound != null && nBTTagCompound.hasKey("Base") ? nBTTagCompound.getInteger("Base") : stack.getMetadata();
   }

   public String getPatternResourceLocation() {
      this.initializeBannerData();
      return this.patternResourceLocation;
   }

   private void initializeBannerData() {
      if (this.patternList == null || this.colorList == null || this.patternResourceLocation == null) {
         if (!this.hasBannerData) {
            this.patternResourceLocation = "";
         } else {
            this.patternList = Lists.newArrayList();
            this.colorList = Lists.newArrayList();
            this.patternList.add(TileEntityBanner.EnumBannerPattern.BASE);
            this.colorList.add(EnumDyeColor.byDyeDamage(this.baseColor));
            this.patternResourceLocation = "b" + this.baseColor;
            if (this.patterns != null) {
               for (int index = 0; index < this.patterns.tagCount(); index++) {
                  NBTTagCompound nBTTagCompound = this.patterns.getCompoundTagAt(index);
                  TileEntityBanner.EnumBannerPattern tileentitybanner$enumbannerpattern = TileEntityBanner.EnumBannerPattern.getPatternByID(
                     nBTTagCompound.getString("Pattern")
                  );
                  if (tileentitybanner$enumbannerpattern != null) {
                     this.patternList.add(tileentitybanner$enumbannerpattern);
                     int integer = nBTTagCompound.getInteger("Color");
                     this.colorList.add(EnumDyeColor.byDyeDamage(integer));
                     this.patternResourceLocation = this.patternResourceLocation + tileentitybanner$enumbannerpattern.getPatternID() + integer;
                  }
               }
            }
         }
      }
   }

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      setBaseColorAndPatterns(compound, this.baseColor, this.patterns);
   }

   @Override
   public Packet getDescriptionPacket() {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      this.writeToNBT(nbtTagCompound);
      return new S35PacketUpdateTileEntity(this.pos, 6, nbtTagCompound);
   }

   public static int getPatterns(ItemStack stack) {
      NBTTagCompound nBTTagCompound = stack.getSubCompound("BlockEntityTag", false);
      return nBTTagCompound != null && nBTTagCompound.hasKey("Patterns") ? nBTTagCompound.getTagList("Patterns", 10).tagCount() : 0;
   }

   public void setItemValues(ItemStack stack) {
      this.patterns = null;
      if (stack.hasTagCompound() && stack.getTagCompound().hasKey("BlockEntityTag", 10)) {
         NBTTagCompound nBTTagCompound = stack.getTagCompound().getCompoundTag("BlockEntityTag");
         if (nBTTagCompound.hasKey("Patterns")) {
            this.patterns = (NBTTagList)nBTTagCompound.getTagList("Patterns", 10).copy();
         }

         if (nBTTagCompound.hasKey("Base", 99)) {
            this.baseColor = nBTTagCompound.getInteger("Base");
         } else {
            this.baseColor = stack.getMetadata() & 15;
         }
      } else {
         this.baseColor = stack.getMetadata() & 15;
      }

      this.patternList = null;
      this.colorList = null;
      this.patternResourceLocation = "";
      this.hasBannerData = true;
   }

   public List<TileEntityBanner.EnumBannerPattern> getPatternList() {
      this.initializeBannerData();
      return this.patternList;
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.baseColor = compound.getInteger("Base");
      this.patterns = compound.getTagList("Patterns", 10);
      this.patternList = null;
      this.colorList = null;
      this.patternResourceLocation = null;
      this.hasBannerData = true;
   }

   public static void removeBannerData(ItemStack stack) {
      NBTTagCompound nBTTagCompound = stack.getSubCompound("BlockEntityTag", false);
      if (nBTTagCompound != null && nBTTagCompound.hasKey("Patterns", 9)) {
         NBTTagList nBTTagList = nBTTagCompound.getTagList("Patterns", 10);
         if (nBTTagList.tagCount() > 0) {
            nBTTagList.removeTag(nBTTagList.tagCount() - 1);
            if (nBTTagList.hasNoTags()) {
               stack.getTagCompound().removeTag("BlockEntityTag");
               if (stack.getTagCompound().hasNoTags()) {
                  stack.setTagCompound((NBTTagCompound)null);
               }
            }
         }
      }
   }

   public int getBaseColor() {
      return this.baseColor;
   }

   public NBTTagList getPatterns() {
      return this.patterns;
   }

   public static void setBaseColorAndPatterns(NBTTagCompound compound, int baseColorIn, NBTTagList patternsIn) {
      compound.setInteger("Base", baseColorIn);
      if (patternsIn != null) {
         compound.setTag("Patterns", patternsIn);
      }
   }

   public List<EnumDyeColor> getColorList() {
      this.initializeBannerData();
      return this.colorList;
   }

   public enum EnumBannerPattern {
      BASE("base", "b"),
      SQUARE_BOTTOM_LEFT("square_bottom_left", "bl", "   ", "   ", "#  "),
      SQUARE_BOTTOM_RIGHT("square_bottom_right", "br", "   ", "   ", "  #"),
      SQUARE_TOP_LEFT("square_top_left", "tl", "#  ", "   ", "   "),
      SQUARE_TOP_RIGHT("square_top_right", "tr", "  #", "   ", "   "),
      STRIPE_BOTTOM("stripe_bottom", "bs", "   ", "   ", "###"),
      STRIPE_TOP("stripe_top", "ts", "###", "   ", "   "),
      STRIPE_LEFT("stripe_left", "ls", "#  ", "#  ", "#  "),
      STRIPE_RIGHT("stripe_right", "rs", "  #", "  #", "  #"),
      STRIPE_CENTER("stripe_center", "cs", " # ", " # ", " # "),
      STRIPE_MIDDLE("stripe_middle", "ms", "   ", "###", "   "),
      STRIPE_DOWNRIGHT("stripe_downright", "drs", "#  ", " # ", "  #"),
      STRIPE_DOWNLEFT("stripe_downleft", "dls", "  #", " # ", "#  "),
      STRIPE_SMALL("small_stripes", "ss", "# #", "# #", "   "),
      CROSS("cross", "cr", "# #", " # ", "# #"),
      STRAIGHT_CROSS("straight_cross", "sc", " # ", "###", " # "),
      TRIANGLE_BOTTOM("triangle_bottom", "bt", "   ", " # ", "# #"),
      TRIANGLE_TOP("triangle_top", "tt", "# #", " # ", "   "),
      TRIANGLES_BOTTOM("triangles_bottom", "bts", "   ", "# #", " # "),
      TRIANGLES_TOP("triangles_top", "tts", " # ", "# #", "   "),
      DIAGONAL_LEFT("diagonal_left", "ld", "## ", "#  ", "   "),
      DIAGONAL_RIGHT("diagonal_up_right", "rd", "   ", "  #", " ##"),
      DIAGONAL_LEFT_MIRROR("diagonal_up_left", "lud", "   ", "#  ", "## "),
      DIAGONAL_RIGHT_MIRROR("diagonal_right", "rud", " ##", "  #", "   "),
      CIRCLE_MIDDLE("circle", "mc", "   ", " # ", "   "),
      RHOMBUS_MIDDLE("rhombus", "mr", " # ", "# #", " # "),
      HALF_VERTICAL("half_vertical", "vh", "## ", "## ", "## "),
      HALF_HORIZONTAL("half_horizontal", "hh", "###", "###", "   "),
      HALF_VERTICAL_MIRROR("half_vertical_right", "vhr", " ##", " ##", " ##"),
      HALF_HORIZONTAL_MIRROR("half_horizontal_bottom", "hhb", "   ", "###", "###"),
      BORDER("border", "bo", "###", "# #", "###"),
      CURLY_BORDER("curly_border", "cbo", new ItemStack(Blocks.vine)),
      CREEPER("creeper", "cre", new ItemStack(Items.skull, 1, 4)),
      GRADIENT("gradient", "gra", "# #", " # ", " # "),
      GRADIENT_UP("gradient_up", "gru", " # ", " # ", "# #"),
      BRICKS("bricks", "bri", new ItemStack(Blocks.brick_block)),
      SKULL("skull", "sku", new ItemStack(Items.skull, 1, 1)),
      FLOWER("flower", "flo", new ItemStack(Blocks.red_flower, 1, EnumFlowerType.OXEYE_DAISY.getMeta())),
      MOJANG("mojang", "moj", new ItemStack(Items.golden_apple, 1, 1));

      private String[] craftingLayers = new String[3];
      private String patternName;
      private String patternID;
      private ItemStack patternCraftingStack;

      public String[] getCraftingLayers() {
         return this.craftingLayers;
      }

      public String getPatternID() {
         return this.patternID;
      }

      public String getPatternName() {
         return this.patternName;
      }

      public static TileEntityBanner.EnumBannerPattern getPatternByID(String id) {
         for (TileEntityBanner.EnumBannerPattern enumBannerPattern : values()) {
            if (enumBannerPattern.patternID.equals(id)) {
               return enumBannerPattern;
            }
         }

         return null;
      }

      public boolean hasCraftingStack() {
         return this.patternCraftingStack != null;
      }

      EnumBannerPattern(String text, String text2, ItemStack itemStack) {
         this(text, text2);
         this.patternCraftingStack = itemStack;
      }

      EnumBannerPattern(String text, String text2, String text3, String text4, String text5) {
         this(text, text2);
         this.craftingLayers[0] = text3;
         this.craftingLayers[1] = text4;
         this.craftingLayers[2] = text5;
      }

      EnumBannerPattern(String text, String text2) {
         this.patternName = text;
         this.patternID = text2;
      }

      public boolean hasValidCrafting() {
         return this.patternCraftingStack != null || this.craftingLayers[0] != null;
      }

      public ItemStack getCraftingStack() {
         return this.patternCraftingStack;
      }
   }
}
