package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

public class BlockDoublePlant extends BlockBush implements IGrowable {
   public static final PropertyEnum<BlockDoublePlant.EnumPlantType> VARIANT = PropertyEnum.create("variant", BlockDoublePlant.EnumPlantType.class);
   public static final PropertyEnum<BlockDoublePlant.EnumBlockHalf> HALF = PropertyEnum.create("half", BlockDoublePlant.EnumBlockHalf.class);
   public static final PropertyEnum<EnumFacing> FACING = BlockDirectional.FACING;

   @Override
   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      return true;
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER ? 8 | state.getValue(FACING).getHorizontalIndex() : state.getValue(VARIANT).getMeta();
   }

   @Override
   public Block.EnumOffsetType getOffsetType() {
      return Block.EnumOffsetType.XZ;
   }

   @Override
   protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
      if (!this.canBlockStay(worldIn, pos, state)) {
         boolean flag = state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER;
         BlockPos blockPos = flag ? pos : pos.up();
         BlockPos blockPos2 = flag ? pos.down() : pos;
         Block block = flag ? this : worldIn.getBlockState(blockPos).getBlock();
         Block block2 = flag ? worldIn.getBlockState(blockPos2).getBlock() : this;
         if (block == this) {
            worldIn.setBlockState(blockPos, Blocks.air.getDefaultState(), 2);
         }

         if (block2 == this) {
            worldIn.setBlockState(blockPos2, Blocks.air.getDefaultState(), 3);
            if (!flag) {
               this.dropBlockAsItem(worldIn, blockPos2, state, 0);
            }
         }
      }
   }

   @Override
   public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
      for (BlockDoublePlant.EnumPlantType enumPlantType : BlockDoublePlant.EnumPlantType.values()) {
         list.add(new ItemStack(itemIn, 1, enumPlantType.getMeta()));
      }
   }

   public BlockDoublePlant() {
      super(Material.vine);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(VARIANT, BlockDoublePlant.EnumPlantType.SUNFLOWER)
            .withProperty(HALF, BlockDoublePlant.EnumBlockHalf.LOWER)
            .withProperty(FACING, EnumFacing.NORTH)
      );
      this.setHardness(0.0F);
      this.setStepSound(soundTypeGrass);
      this.setUnlocalizedName("doublePlant");
   }

   private boolean onHarvest(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype = state.getValue(VARIANT);
      if (blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.FERN && blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.GRASS) {
         return false;
      }

      player.triggerAchievement(StatList.mineBlockStatArray[Block.getIdFromBlock(this)]);
      int meta2 = (blockdoubleplant$enumplanttype == BlockDoublePlant.EnumPlantType.GRASS ? BlockTallGrass.EnumType.GRASS : BlockTallGrass.EnumType.FERN)
         .getMeta();
      spawnAsEntity(worldIn, pos, new ItemStack(Blocks.tallgrass, 2, meta2));
      return true;
   }

   @Override
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      worldIn.setBlockState(pos.up(), this.getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER), 2);
   }

   @Override
   public int getDamageValue(World worldIn, BlockPos pos) {
      return this.getVariant(worldIn, pos).getMeta();
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      if (state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER) {
         return null;
      }

      BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype = state.getValue(VARIANT);
      return blockdoubleplant$enumplanttype == BlockDoublePlant.EnumPlantType.FERN
         ? null
         : (
            blockdoubleplant$enumplanttype == BlockDoublePlant.EnumPlantType.GRASS
               ? (rand.nextInt(8) == 0 ? Items.wheat_seeds : null)
               : Item.getItemFromBlock(this)
         );
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return (meta & 8) > 0
         ? this.getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER)
         : this.getDefaultState()
            .withProperty(HALF, BlockDoublePlant.EnumBlockHalf.LOWER)
            .withProperty(VARIANT, BlockDoublePlant.EnumPlantType.byMetadata(meta & 7));
   }

   @Override
   public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
      if (state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER) {
         return worldIn.getBlockState(pos.down()).getBlock() == this;
      }

      IBlockState blockState = worldIn.getBlockState(pos.up());
      return blockState.getBlock() == this && super.canBlockStay(worldIn, pos, blockState);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return super.canPlaceBlockAt(worldIn, pos) && worldIn.isAirBlock(pos.up());
   }

   public BlockDoublePlant.EnumPlantType getVariant(IBlockAccess worldIn, BlockPos pos) {
      IBlockState blockState2 = worldIn.getBlockState(pos);
      if (blockState2.getBlock() == this) {
         blockState2 = this.getActualState(blockState2, worldIn, pos);
         return blockState2.getValue(VARIANT);
      } else {
         return BlockDoublePlant.EnumPlantType.FERN;
      }
   }

   @Override
   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
      if (worldIn.isRemote
         || player.getCurrentEquippedItem() == null
         || player.getCurrentEquippedItem().getItem() != Items.shears
         || state.getValue(HALF) != BlockDoublePlant.EnumBlockHalf.LOWER
         || !this.onHarvest(worldIn, pos, state, player)) {
         super.harvestBlock(worldIn, player, pos, state, te);
      }
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      if (state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER) {
         IBlockState blockState = worldIn.getBlockState(pos.down());
         if (blockState.getBlock() == this) {
            state = state.withProperty(VARIANT, blockState.getValue(VARIANT));
         }
      }

      return state;
   }

   public void handleWorld(World world2, BlockPos blockPos3, BlockDoublePlant.EnumPlantType enumPlantType) {
      world2.setBlockState(blockPos3, this.getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.LOWER).withProperty(VARIANT, enumPlantType), 2);
      world2.setBlockState(blockPos3.up(), this.getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER), 2);
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, HALF, VARIANT, FACING);
   }

   @Override
   public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      if (state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER) {
         if (worldIn.getBlockState(pos.down()).getBlock() == this) {
            if (!player.capabilities.isCreativeMode) {
               IBlockState blockState = worldIn.getBlockState(pos.down());
               BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype = blockState.getValue(VARIANT);
               if (blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.FERN
                  && blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.GRASS) {
                  worldIn.destroyBlock(pos.down(), true);
               } else if (!worldIn.isRemote) {
                  if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears) {
                     this.onHarvest(worldIn, pos, blockState, player);
                     worldIn.setBlockToAir(pos.down());
                  } else {
                     worldIn.destroyBlock(pos.down(), true);
                  }
               } else {
                  worldIn.setBlockToAir(pos.down());
               }
            } else {
               worldIn.setBlockToAir(pos.down());
            }
         }
      } else if (player.capabilities.isCreativeMode && worldIn.getBlockState(pos.up()).getBlock() == this) {
         worldIn.setBlockState(pos.up(), Blocks.air.getDefaultState(), 2);
      }

      super.onBlockHarvested(worldIn, pos, state, player);
   }

   @Override
   public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype = this.getVariant(worldIn, pos);
      return blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.GRASS && blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.FERN;
   }

   @Override
   public int damageDropped(IBlockState state) {
      return state.getValue(HALF) != BlockDoublePlant.EnumBlockHalf.UPPER && state.getValue(VARIANT) != BlockDoublePlant.EnumPlantType.GRASS
         ? state.getValue(VARIANT).getMeta()
         : 0;
   }

   @Override
   public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      spawnAsEntity(worldIn, pos, new ItemStack(this, 1, this.getVariant(worldIn, pos).getMeta()));
   }

   @Override
   public boolean isReplaceable(World worldIn, BlockPos pos) {
      IBlockState blockState = worldIn.getBlockState(pos);
      if (blockState.getBlock() != this) {
         return true;
      }

      BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype = this.getActualState(blockState, worldIn, pos).getValue(VARIANT);
      return blockdoubleplant$enumplanttype == BlockDoublePlant.EnumPlantType.FERN || blockdoubleplant$enumplanttype == BlockDoublePlant.EnumPlantType.GRASS;
   }

   @Override
   public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
      BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype = this.getVariant(worldIn, pos);
      return blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.GRASS && blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.FERN
         ? 16777215
         : BiomeColorHelper.getGrassColorAtPos(worldIn, pos);
   }

   public enum EnumBlockHalf implements IStringSerializable {
      UPPER,
      LOWER;

      @Override
      public String toString() {
         return this.getName();
      }

      @Override
      public String getName() {
         return this == UPPER ? "upper" : "lower";
      }
   }

   public enum EnumPlantType implements IStringSerializable {
      SUNFLOWER(0, "sunflower"),
      SYRINGA(1, "syringa"),
      GRASS(2, "double_grass", "grass"),
      FERN(3, "double_fern", "fern"),
      ROSE(4, "double_rose", "rose"),
      PAEONIA(5, "paeonia");

      private final int meta;
      private final String name;
      private final String unlocalizedName;
      private static final BlockDoublePlant.EnumPlantType[] META_LOOKUP = new BlockDoublePlant.EnumPlantType[values().length];

      public int getMeta() {
         return this.meta;
      }

      public static BlockDoublePlant.EnumPlantType byMetadata(int meta) {
         if (meta < 0 || meta >= META_LOOKUP.length) {
            meta = 0;
         }

         return META_LOOKUP[meta];
      }

      public String getUnlocalizedName() {
         return this.unlocalizedName;
      }

      @Override
      public String toString() {
         return this.name;
      }

      EnumPlantType(int i, String text, String text2) {
         this.meta = i;
         this.name = text;
         this.unlocalizedName = text2;
      }

      static {
         for (BlockDoublePlant.EnumPlantType enumPlantType : values()) {
            META_LOOKUP[enumPlantType.getMeta()] = enumPlantType;
         }
      }

      @Override
      public String getName() {
         return this.name;
      }

      EnumPlantType(int i, String text) {
         this(i, text, text);
      }
   }
}
