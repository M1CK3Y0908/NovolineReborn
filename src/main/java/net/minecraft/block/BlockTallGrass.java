package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTallGrass extends BlockBush implements IGrowable {
   public static final PropertyEnum<BlockTallGrass.EnumType> TYPE = PropertyEnum.create("type", BlockTallGrass.EnumType.class);

   @Override
   public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return state.getValue(TYPE) != BlockTallGrass.EnumType.DEAD_BUSH;
   }

   @Override
   public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
      return worldIn.getBiomeGenForCoords(pos).getGrassColorAtPos(pos);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(TYPE).getMeta();
   }

   @Override
   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
      if (!worldIn.isRemote && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears) {
         player.triggerAchievement(StatList.mineBlockStatArray[Block.getIdFromBlock(this)]);
         spawnAsEntity(worldIn, pos, new ItemStack(Blocks.tallgrass, 1, state.getValue(TYPE).getMeta()));
      } else {
         super.harvestBlock(worldIn, player, pos, state, te);
      }
   }

   @Override
   public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype = BlockDoublePlant.EnumPlantType.GRASS;
      if (state.getValue(TYPE) == BlockTallGrass.EnumType.FERN) {
         blockdoubleplant$enumplanttype = BlockDoublePlant.EnumPlantType.FERN;
      }

      if (Blocks.double_plant.canPlaceBlockAt(worldIn, pos)) {
         Blocks.double_plant.handleWorld(worldIn, pos, blockdoubleplant$enumplanttype);
      }
   }

   @Override
   public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
      for (int index = 1; index < 3; index++) {
         list.add(new ItemStack(itemIn, 1, index));
      }
   }

   @Override
   public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
      return this.canPlaceBlockOn(worldIn.getBlockState(pos.down()).getBlock());
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return rand.nextInt(8) == 0 ? Items.wheat_seeds : null;
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, TYPE);
   }

   @Override
   public boolean isReplaceable(World worldIn, BlockPos pos) {
      return true;
   }

   @Override
   public int getDamageValue(World worldIn, BlockPos pos) {
      IBlockState blockState = worldIn.getBlockState(pos);
      return blockState.getBlock().getMetaFromState(blockState);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(TYPE, BlockTallGrass.EnumType.byMetadata(meta));
   }

   @Override
   public Block.EnumOffsetType getOffsetType() {
      return Block.EnumOffsetType.XYZ;
   }

   @Override
   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      return true;
   }

   protected BlockTallGrass() {
      super(Material.vine);
      this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, BlockTallGrass.EnumType.DEAD_BUSH));
      float f = 0.4F;
      this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.8F, 0.5F + f);
   }

   @Override
   public int quantityDroppedWithBonus(int fortune, Random random) {
      return 1 + random.nextInt(fortune * 2 + 1);
   }

   @Override
   public int getRenderColor(IBlockState state) {
      if (state.getBlock() != this) {
         return super.getRenderColor(state);
      } else {
         return (BlockTallGrass.EnumType)state.getValue(TYPE) == BlockTallGrass.EnumType.DEAD_BUSH ? 16777215 : ColorizerGrass.getGrassColor(0.5, 1.0);
      }
   }

   public enum EnumType implements IStringSerializable {
      DEAD_BUSH(0, "dead_bush"),
      GRASS(1, "tall_grass"),
      FERN(2, "fern");

      private final int meta;
      private final String name;
      private static final BlockTallGrass.EnumType[] META_LOOKUP = new BlockTallGrass.EnumType[values().length];

      @Override
      public String toString() {
         return this.name;
      }

      public int getMeta() {
         return this.meta;
      }

      @Override
      public String getName() {
         return this.name;
      }

      public static BlockTallGrass.EnumType byMetadata(int meta) {
         if (meta < 0 || meta >= META_LOOKUP.length) {
            meta = 0;
         }

         return META_LOOKUP[meta];
      }

      static {
         for (BlockTallGrass.EnumType enumType : values()) {
            META_LOOKUP[enumType.getMeta()] = enumType;
         }
      }

      EnumType(int i, String text) {
         this.meta = i;
         this.name = text;
      }
   }
}
