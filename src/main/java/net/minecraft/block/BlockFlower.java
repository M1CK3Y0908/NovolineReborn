package net.minecraft.block;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public abstract class BlockFlower extends BlockBush {
   protected PropertyEnum<BlockFlower.EnumFlowerType> type;

   public abstract BlockFlower.EnumFlowerColor getBlockType();

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(this.getTypeProperty(), BlockFlower.EnumFlowerType.getType(this.getBlockType(), meta));
   }

   @Override
   public int getMetaFromState(IBlockState iBlockState) {
      return iBlockState.getValue(this.getTypeProperty()).getMeta();
   }

   @Override
   public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
      for (BlockFlower.EnumFlowerType enumFlowerType : BlockFlower.EnumFlowerType.getTypes(this.getBlockType())) {
         list.add(new ItemStack(itemIn, 1, enumFlowerType.getMeta()));
      }
   }

   protected BlockFlower() {
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(
               this.getTypeProperty(),
               this.getBlockType() == BlockFlower.EnumFlowerColor.RED ? BlockFlower.EnumFlowerType.POPPY : BlockFlower.EnumFlowerType.DANDELION
            )
      );
   }

   @Override
   public int damageDropped(IBlockState iBlockState) {
      return iBlockState.getValue(this.getTypeProperty()).getMeta();
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, this.getTypeProperty());
   }

   @Override
   public Block.EnumOffsetType getOffsetType() {
      return Block.EnumOffsetType.XZ;
   }

   public IProperty<BlockFlower.EnumFlowerType> getTypeProperty() {
      if (this.type == null) {
         this.type = PropertyEnum.create("type", BlockFlower.EnumFlowerType.class, new Predicate<BlockFlower.EnumFlowerType>() {
            public boolean apply(BlockFlower.EnumFlowerType arg1) {
               return arg1.getBlockType() == BlockFlower.this.getBlockType();
            }
         });
      }

      return this.type;
   }

   public enum EnumFlowerColor {
      YELLOW,
      RED;

      public BlockFlower getBlock() {
         return this == YELLOW ? Blocks.yellow_flower : Blocks.red_flower;
      }
   }

   public enum EnumFlowerType implements IStringSerializable {
      DANDELION(BlockFlower.EnumFlowerColor.YELLOW, 0, "dandelion"),
      POPPY(BlockFlower.EnumFlowerColor.RED, 0, "poppy"),
      BLUE_ORCHID(BlockFlower.EnumFlowerColor.RED, 1, "blue_orchid", "blueOrchid"),
      ALLIUM(BlockFlower.EnumFlowerColor.RED, 2, "allium"),
      HOUSTONIA(BlockFlower.EnumFlowerColor.RED, 3, "houstonia"),
      RED_TULIP(BlockFlower.EnumFlowerColor.RED, 4, "red_tulip", "tulipRed"),
      ORANGE_TULIP(BlockFlower.EnumFlowerColor.RED, 5, "orange_tulip", "tulipOrange"),
      WHITE_TULIP(BlockFlower.EnumFlowerColor.RED, 6, "white_tulip", "tulipWhite"),
      PINK_TULIP(BlockFlower.EnumFlowerColor.RED, 7, "pink_tulip", "tulipPink"),
      OXEYE_DAISY(BlockFlower.EnumFlowerColor.RED, 8, "oxeye_daisy", "oxeyeDaisy");

      private final BlockFlower.EnumFlowerColor blockType;
      private final int meta;
      private final String name;
      private static final BlockFlower.EnumFlowerType[][] TYPES_FOR_BLOCK = new BlockFlower.EnumFlowerType[BlockFlower.EnumFlowerColor.values().length][];
      private final String unlocalizedName;

      public String getUnlocalizedName() {
         return this.unlocalizedName;
      }

      public int getMeta() {
         return this.meta;
      }

      public static BlockFlower.EnumFlowerType getType(BlockFlower.EnumFlowerColor blockType, int meta) {
         BlockFlower.EnumFlowerType[] enumFlowerTypes = TYPES_FOR_BLOCK[blockType.ordinal()];
         if (meta < 0 || meta >= enumFlowerTypes.length) {
            meta = 0;
         }

         return enumFlowerTypes[meta];
      }

      EnumFlowerType(BlockFlower.EnumFlowerColor enumFlowerColor, int i, String text) {
         this(enumFlowerColor, i, text, text);
      }

      EnumFlowerType(BlockFlower.EnumFlowerColor enumFlowerColor, int i, String text, String text2) {
         this.blockType = enumFlowerColor;
         this.meta = i;
         this.name = text;
         this.unlocalizedName = text2;
      }

      @Override
      public String getName() {
         return this.name;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static BlockFlower.EnumFlowerType[] getTypes(BlockFlower.EnumFlowerColor flowerColor) {
         return TYPES_FOR_BLOCK[flowerColor.ordinal()];
      }

      public BlockFlower.EnumFlowerColor getBlockType() {
         return this.blockType;
      }

      static {
         for (final BlockFlower.EnumFlowerColor enumFlowerColor : BlockFlower.EnumFlowerColor.values()) {
            Collection collection = Collections2.filter(Lists.newArrayList(values()), new Predicate<BlockFlower.EnumFlowerType>() {
               public boolean apply(BlockFlower.EnumFlowerType arg1) {
                  return arg1.getBlockType() == enumFlowerColor;
               }
            });
            TYPES_FOR_BLOCK[enumFlowerColor.ordinal()] = (BlockFlower.EnumFlowerType[]) collection.toArray(new BlockFlower.EnumFlowerType[collection.size()]);
         }
      }
   }
}
