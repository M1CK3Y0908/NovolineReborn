package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockVine extends Block {
   public static final PropertyBool UP = PropertyBool.create("up");
   public static final PropertyBool NORTH = PropertyBool.create("north");
   public static final PropertyBool EAST = PropertyBool.create("east");
   public static final PropertyBool SOUTH = PropertyBool.create("south");
   public static final PropertyBool WEST = PropertyBool.create("west");
   public static final PropertyBool[] ALL_FACES = new PropertyBool[]{UP, NORTH, SOUTH, WEST, EAST};

   @Override
   public int getMetaFromState(IBlockState state) {
      int i = 0;
      if (state.getValue(SOUTH)) {
         i = 1;
      }

      if (state.getValue(WEST)) {
         i |= 2;
      }

      if (state.getValue(NORTH)) {
         i |= 4;
      }

      if (state.getValue(EAST)) {
         i |= 8;
      }

      return i;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return null;
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!worldIn.isRemote && !this.recheckGrownSides(worldIn, pos, state)) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }
   }

   @Override
   public int quantityDropped(Random random) {
      return 0;
   }

   private boolean canPlaceOn(Block blockIn) {
      return blockIn.isFullCube() && blockIn.blockMaterial.blocksMovement();
   }

   public static PropertyBool getPropertyFor(EnumFacing side) {
      switch (side) {
         case UP:
            return UP;
         case NORTH:
            return NORTH;
         case SOUTH:
            return SOUTH;
         case EAST:
            return EAST;
         case WEST:
            return WEST;
         default:
            throw new IllegalArgumentException(side + " is an invalid choice");
      }
   }

   @Override
   public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
      switch (side) {
         case UP:
            return this.canPlaceOn(worldIn.getBlockState(pos.up()).getBlock());
         case NORTH:
         case SOUTH:
         case EAST:
         case WEST:
            return this.canPlaceOn(worldIn.getBlockState(pos.offset(side.getOpposite())).getBlock());
         default:
            return false;
      }
   }

   @Override
   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
      if (!worldIn.isRemote && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears) {
         player.triggerAchievement(StatList.mineBlockStatArray[Block.getIdFromBlock(this)]);
         spawnAsEntity(worldIn, pos, new ItemStack(Blocks.vine, 1, 0));
      } else {
         super.harvestBlock(worldIn, player, pos, state, te);
      }
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return state.withProperty(UP, worldIn.getBlockState(pos.up()).getBlock().isBlockNormalCube());
   }

   public static int getNumGrownFaces(IBlockState state) {
      int i = 0;

      for (PropertyBool propertyBool : ALL_FACES) {
         if (state.getValue(propertyBool)) {
            i++;
         }
      }

      return i;
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote && worldIn.rand.nextInt(4) == 0) {
         int i = 5;
         boolean flag = false;

         label182:
         for (int i2 = -4; i2 <= 4; i2++) {
            for (int i3 = -4; i3 <= 4; i3++) {
               for (int i4 = -1; i4 <= 1; i4++) {
                  if (worldIn.getBlockState(pos.add(i2, i4, i3)).getBlock() == this) {
                     if (--i <= 0) {
                        flag = true;
                        break label182;
                     }
                  }
               }
            }
         }

         EnumFacing enumFacing = EnumFacing.random(rand);
         BlockPos blockPos = pos.up();
         if (enumFacing == EnumFacing.UP && pos.getY() < 255 && worldIn.isAirBlock(blockPos)) {
            if (!flag) {
               IBlockState state2 = state;

               for (EnumFacing enumFacing2 : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
                  if (rand.nextBoolean() || !this.canPlaceOn(worldIn.getBlockState(blockPos.offset(enumFacing2)).getBlock())) {
                     state2 = state2.withProperty(getPropertyFor(enumFacing2), false);
                  }
               }

               if (state2.getValue(NORTH) || state2.getValue(EAST) || state2.getValue(SOUTH) || state2.getValue(WEST)) {
                  worldIn.setBlockState(blockPos, state2, 2);
               }
            }
         } else if (!enumFacing.getAxis().isHorizontal() || state.getValue(getPropertyFor(enumFacing))) {
            if (pos.getY() > 1) {
               BlockPos blockPos2 = pos.down();
               IBlockState blockState2 = worldIn.getBlockState(blockPos2);
               Block block2 = blockState2.getBlock();
               if (block2.blockMaterial == Material.air) {
                  IBlockState state3 = state;

                  for (EnumFacing enumFacing3 : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
                     if (rand.nextBoolean()) {
                        state3 = state3.withProperty(getPropertyFor(enumFacing3), false);
                     }
                  }

                  if (state3.getValue(NORTH) || state3.getValue(EAST) || state3.getValue(SOUTH) || state3.getValue(WEST)) {
                     worldIn.setBlockState(blockPos2, state3, 2);
                  }
               } else if (block2 == this) {
                  IBlockState iBlockState = blockState2;

                  for (EnumFacing enumFacing4 : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
                     PropertyBool propertyFor = getPropertyFor(enumFacing4);
                     if (rand.nextBoolean() && state.getValue(propertyFor)) {
                        iBlockState = iBlockState.withProperty(propertyFor, true);
                     }
                  }

                  if (iBlockState.getValue(NORTH) || iBlockState.getValue(EAST) || iBlockState.getValue(SOUTH) || iBlockState.getValue(WEST)) {
                     worldIn.setBlockState(blockPos2, iBlockState, 2);
                  }
               }
            }
         } else if (!flag) {
            BlockPos blockPos3 = pos.offset(enumFacing);
            Block block3 = worldIn.getBlockState(blockPos3).getBlock();
            if (block3.blockMaterial == Material.air) {
               EnumFacing enumFacing5 = enumFacing.rotateY();
               EnumFacing enumFacing6 = enumFacing.rotateYCCW();
               boolean value = state.getValue(getPropertyFor(enumFacing5));
               boolean value2 = state.getValue(getPropertyFor(enumFacing6));
               BlockPos blockPos4 = blockPos3.offset(enumFacing5);
               BlockPos blockPos5 = blockPos3.offset(enumFacing6);
               if (value && this.canPlaceOn(worldIn.getBlockState(blockPos4).getBlock())) {
                  worldIn.setBlockState(blockPos3, this.getDefaultState().withProperty(getPropertyFor(enumFacing5), true), 2);
               } else if (value2 && this.canPlaceOn(worldIn.getBlockState(blockPos5).getBlock())) {
                  worldIn.setBlockState(blockPos3, this.getDefaultState().withProperty(getPropertyFor(enumFacing6), true), 2);
               } else if (value && worldIn.isAirBlock(blockPos4) && this.canPlaceOn(worldIn.getBlockState(pos.offset(enumFacing5)).getBlock())) {
                  worldIn.setBlockState(blockPos4, this.getDefaultState().withProperty(getPropertyFor(enumFacing.getOpposite()), true), 2);
               } else if (value2 && worldIn.isAirBlock(blockPos5) && this.canPlaceOn(worldIn.getBlockState(pos.offset(enumFacing6)).getBlock())) {
                  worldIn.setBlockState(blockPos5, this.getDefaultState().withProperty(getPropertyFor(enumFacing.getOpposite()), true), 2);
               } else if (this.canPlaceOn(worldIn.getBlockState(blockPos3.up()).getBlock())) {
                  worldIn.setBlockState(blockPos3, this.getDefaultState(), 2);
               }
            } else if (block3.blockMaterial.isOpaque() && block3.isFullCube()) {
               worldIn.setBlockState(pos, state.withProperty(getPropertyFor(enumFacing), true), 2);
            }
         }
      }
   }

   @Override
   public void setBlockBoundsForItemRender() {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   public BlockVine() {
      super(Material.vine);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(UP, false)
            .withProperty(NORTH, false)
            .withProperty(EAST, false)
            .withProperty(SOUTH, false)
            .withProperty(WEST, false)
      );
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public boolean isReplaceable(World worldIn, BlockPos pos) {
      return true;
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return null;
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      IBlockState blockState = this.getDefaultState()
         .withProperty(UP, false)
         .withProperty(NORTH, false)
         .withProperty(EAST, false)
         .withProperty(SOUTH, false)
         .withProperty(WEST, false);
      return facing.getAxis().isHorizontal() ? blockState.withProperty(getPropertyFor(facing.getOpposite()), true) : blockState;
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, UP, NORTH, EAST, SOUTH, WEST);
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   private boolean recheckGrownSides(World worldIn, BlockPos pos, IBlockState state) {
      IBlockState state2 = state;

      for (EnumFacing enumFacing : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
         PropertyBool propertyFor = getPropertyFor(enumFacing);
         if (state.getValue(propertyFor) && !this.canPlaceOn(worldIn.getBlockState(pos.offset(enumFacing)).getBlock())) {
            IBlockState blockState2 = worldIn.getBlockState(pos.up());
            if (blockState2.getBlock() != this || !blockState2.getValue(propertyFor)) {
               state = state.withProperty(propertyFor, false);
            }
         }
      }

      if (getNumGrownFaces(state) == 0) {
         return false;
      }

      if (state2 != state) {
         worldIn.setBlockState(pos, state, 2);
      }

      return true;
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      float f = 1.0F;
      float f2 = 1.0F;
      float f3 = 1.0F;
      float f4 = 0.0F;
      float f5 = 0.0F;
      float f6 = 0.0F;
      boolean flag = false;
      if (worldIn.getBlockState(pos).getValue(WEST)) {
         f4 = Math.max(f4, 0.0625F);
         f = 0.0F;
         f2 = 0.0F;
         f5 = 1.0F;
         f3 = 0.0F;
         f6 = 1.0F;
         flag = true;
      }

      if (worldIn.getBlockState(pos).getValue(EAST)) {
         f = Math.min(f, 0.9375F);
         f4 = 1.0F;
         f2 = 0.0F;
         f5 = 1.0F;
         f3 = 0.0F;
         f6 = 1.0F;
         flag = true;
      }

      if (worldIn.getBlockState(pos).getValue(NORTH)) {
         f6 = Math.max(f6, 0.0625F);
         f3 = 0.0F;
         f = 0.0F;
         f4 = 1.0F;
         f2 = 0.0F;
         f5 = 1.0F;
         flag = true;
      }

      if (worldIn.getBlockState(pos).getValue(SOUTH)) {
         f3 = Math.min(f3, 0.9375F);
         f6 = 1.0F;
         f = 0.0F;
         f4 = 1.0F;
         f2 = 0.0F;
         f5 = 1.0F;
         flag = true;
      }

      if (!flag && this.canPlaceOn(worldIn.getBlockState(pos.up()).getBlock())) {
         f2 = Math.min(f2, 0.9375F);
         f5 = 1.0F;
         f = 0.0F;
         f4 = 1.0F;
         f3 = 0.0F;
         f6 = 1.0F;
      }

      this.setBlockBounds(f, f2, f3, f4, f5, f6);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState()
         .withProperty(SOUTH, (meta & 1) > 0)
         .withProperty(WEST, (meta & 2) > 0)
         .withProperty(NORTH, (meta & 4) > 0)
         .withProperty(EAST, (meta & 8) > 0);
   }

   @Override
   public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
      return worldIn.getBiomeGenForCoords(pos).getFoliageColorAtPos(pos);
   }

   @Override
   public int getRenderColor(IBlockState state) {
      return ColorizerFoliage.getFoliageColorBasic();
   }
}
