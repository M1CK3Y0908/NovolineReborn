package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonBase extends Block {
   public static final PropertyDirection FACING = PropertyDirection.create("facing");
   private final boolean isSticky;
   public static final PropertyBool EXTENDED = PropertyBool.create("extended");

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, getFacingFromEntity(worldIn, pos, placer)).withProperty(EXTENDED, false);
   }

   public static EnumFacing getFacing(int meta) {
      int i = meta & 7;
      return i > 5 ? null : EnumFacing.getFront(i);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   public static EnumFacing getFacingFromEntity(World worldIn, BlockPos clickedBlock, EntityLivingBase entityIn) {
      if (MathHelper.abs((float)entityIn.posX - clickedBlock.getX()) < 2.0F && MathHelper.abs((float)entityIn.posZ - clickedBlock.getZ()) < 2.0F) {
         double yCoordinate = entityIn.posY + entityIn.getEyeHeight();
         if (yCoordinate - clickedBlock.getY() > 2.0) {
            return EnumFacing.UP;
         }

         if (clickedBlock.getY() - yCoordinate > 0.0) {
            return EnumFacing.DOWN;
         }
      }

      return entityIn.getHorizontalFacing().getOpposite();
   }

   private boolean doMove(World worldIn, BlockPos pos, EnumFacing direction, boolean extending) {
      if (!extending) {
         worldIn.setBlockToAir(pos.offset(direction));
      }

      BlockPistonStructureHelper blockPistonStructureHelper = new BlockPistonStructureHelper(worldIn, pos, direction, extending);
      List blocksToMove = blockPistonStructureHelper.getBlocksToMove();
      List blocksToDestroy = blockPistonStructureHelper.getBlocksToDestroy();
      if (!blockPistonStructureHelper.canMove()) {
         return false;
      }

      int i = blocksToMove.size() + blocksToDestroy.size();
      Block[] blocks = new Block[i];
      EnumFacing enumFacing = extending ? direction : direction.getOpposite();

      for (int i2 = blocksToDestroy.size() - 1; i2 >= 0; i2--) {
         BlockPos blockPos2 = (BlockPos)blocksToDestroy.get(i2);
         Block block2 = worldIn.getBlockState(blockPos2).getBlock();
         block2.dropBlockAsItem(worldIn, blockPos2, worldIn.getBlockState(blockPos2), 0);
         worldIn.setBlockToAir(blockPos2);
         blocks[--i] = block2;
      }

      for (int i3 = blocksToMove.size() - 1; i3 >= 0; i3--) {
         BlockPos blockPos3 = (BlockPos)blocksToMove.get(i3);
         IBlockState blockState2 = worldIn.getBlockState(blockPos3);
         Block block3 = blockState2.getBlock();
         block3.getMetaFromState(blockState2);
         worldIn.setBlockToAir(blockPos3);
         blockPos3 = blockPos3.offset(enumFacing);
         worldIn.setBlockState(blockPos3, Blocks.piston_extension.getDefaultState().withProperty(FACING, direction), 4);
         worldIn.setTileEntity(blockPos3, BlockPistonMoving.newTileEntity(blockState2, direction, extending, false));
         blocks[--i] = block3;
      }

      BlockPos blockPos4 = pos.offset(direction);
      if (extending) {
         BlockPistonExtension.EnumPistonType enumPistonType = this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT;
         IBlockState iBlockState = Blocks.piston_head
            .getDefaultState()
            .withProperty(BlockPistonExtension.FACING, direction)
            .withProperty(BlockPistonExtension.TYPE, enumPistonType);
         IBlockState iBlockState2 = Blocks.piston_extension
            .getDefaultState()
            .withProperty(BlockPistonMoving.FACING, direction)
            .withProperty(BlockPistonMoving.TYPE, this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
         worldIn.setBlockState(blockPos4, iBlockState2, 4);
         worldIn.setTileEntity(blockPos4, BlockPistonMoving.newTileEntity(iBlockState, direction, true, false));
      }

      for (int i4 = blocksToDestroy.size() - 1; i4 >= 0; i4--) {
         worldIn.notifyNeighborsOfStateChange((BlockPos)blocksToDestroy.get(i4), blocks[i++]);
      }

      for (int i5 = blocksToMove.size() - 1; i5 >= 0; i5--) {
         worldIn.notifyNeighborsOfStateChange((BlockPos)blocksToMove.get(i5), blocks[i++]);
      }

      if (extending) {
         worldIn.notifyNeighborsOfStateChange(blockPos4, Blocks.piston_head);
         worldIn.notifyNeighborsOfStateChange(pos, this);
      }

      return true;
   }

   @Override
   public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int i = 0 | state.getValue(FACING).getIndex();
      if (state.getValue(EXTENDED)) {
         i |= 8;
      }

      return i;
   }

   @Override
   public IBlockState getStateForEntityRender(IBlockState state) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.UP);
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.getCollisionBoundingBox(worldIn, pos, state);
   }

   private void checkForMove(World worldIn, BlockPos pos, IBlockState state) {
      EnumFacing facing = state.getValue(FACING);
      boolean shouldBeExtended = this.shouldBeExtended(worldIn, pos, facing);
      if (shouldBeExtended && !state.getValue(EXTENDED)) {
         if (new BlockPistonStructureHelper(worldIn, pos, facing, true).canMove()) {
            worldIn.addBlockEvent(pos, this, 0, facing.getIndex());
         }
      } else if (!shouldBeExtended && state.getValue(EXTENDED)) {
         worldIn.setBlockState(pos, state.withProperty(EXTENDED, false), 2);
         worldIn.addBlockEvent(pos, this, 1, facing.getIndex());
      }
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, FACING, EXTENDED);
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      if (!worldIn.isRemote && worldIn.getTileEntity(pos) == null) {
         this.checkForMove(worldIn, pos, state);
      }
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(FACING, getFacing(meta)).withProperty(EXTENDED, (meta & 8) > 0);
   }

   @Override
   public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam) {
      EnumFacing facing = state.getValue(FACING);
      if (!worldIn.isRemote) {
         boolean shouldBeExtended = this.shouldBeExtended(worldIn, pos, facing);
         if (shouldBeExtended && eventID == 1) {
            worldIn.setBlockState(pos, state.withProperty(EXTENDED, true), 2);
            return false;
         }

         if (!shouldBeExtended && eventID == 0) {
            return false;
         }
      }

      if (eventID == 0) {
         if (!this.doMove(worldIn, pos, facing, true)) {
            return false;
         }

         worldIn.setBlockState(pos, state.withProperty(EXTENDED, true), 2);
         worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, "tile.piston.out", 0.5F, worldIn.rand.nextFloat() * 0.25F + 0.6F);
      } else if (eventID == 1) {
         TileEntity tileEntity = worldIn.getTileEntity(pos.offset(facing));
         if (tileEntity instanceof TileEntityPiston) {
            ((TileEntityPiston)tileEntity).clearPistonTileEntity();
         }

         worldIn.setBlockState(
            pos,
            Blocks.piston_extension
               .getDefaultState()
               .withProperty(BlockPistonMoving.FACING, facing)
               .withProperty(BlockPistonMoving.TYPE, this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT),
            3
         );
         worldIn.setTileEntity(pos, BlockPistonMoving.newTileEntity(this.getStateFromMeta(eventParam), facing, false, true));
         if (this.isSticky) {
            BlockPos blockPos = pos.add(facing.getFrontOffsetX() * 2, facing.getFrontOffsetY() * 2, facing.getFrontOffsetZ() * 2);
            Block block = worldIn.getBlockState(blockPos).getBlock();
            boolean conditionMet = false;
            if (block == Blocks.piston_extension
               && worldIn.getTileEntity(blockPos) instanceof TileEntityPiston tileEntityPiston
               && tileEntityPiston.getFacing() == facing
               && tileEntityPiston.isExtending()) {
               tileEntityPiston.clearPistonTileEntity();
               conditionMet = true;
            }

            if (!conditionMet
               && block.getMaterial() != Material.air
               && canPush(block, worldIn, blockPos, facing.getOpposite(), false)
               && (block.getMobilityFlag() == 0 || block == Blocks.piston || block == Blocks.sticky_piston)) {
               this.doMove(worldIn, pos, facing, false);
            }
         } else {
            worldIn.setBlockToAir(pos.offset(facing));
         }

         worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, "tile.piston.in", 0.5F, worldIn.rand.nextFloat() * 0.15F + 0.6F);
      }

      return true;
   }

   @Override
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      worldIn.setBlockState(pos, state.withProperty(FACING, getFacingFromEntity(worldIn, pos, placer)), 2);
      if (!worldIn.isRemote) {
         this.checkForMove(worldIn, pos, state);
      }
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      IBlockState blockState2 = worldIn.getBlockState(pos);
      if (blockState2.getBlock() == this && blockState2.getValue(EXTENDED)) {
         EnumFacing value = blockState2.getValue(FACING);
         if (value != null) {
            switch (value) {
               case DOWN:
                  this.setBlockBounds(0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F);
                  break;
               case UP:
                  this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
                  break;
               case NORTH:
                  this.setBlockBounds(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F);
                  break;
               case SOUTH:
                  this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F);
                  break;
               case WEST:
                  this.setBlockBounds(0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                  break;
               case EAST:
                  this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F);
            }
         }
      } else {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public void setBlockBoundsForItemRender() {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   public static boolean canPush(Block blockIn, World worldIn, BlockPos pos, EnumFacing direction, boolean allowDestroy) {
      if (blockIn == Blocks.obsidian) {
         return false;
      }

      if (!worldIn.getWorldBorder().contains(pos)) {
         return false;
      }

      if (pos.getY() >= 0 && (direction != EnumFacing.DOWN || pos.getY() != 0)) {
         if (pos.getY() <= worldIn.getHeight() - 1 && (direction != EnumFacing.UP || pos.getY() != worldIn.getHeight() - 1)) {
            if (blockIn != Blocks.piston && blockIn != Blocks.sticky_piston) {
               if (blockIn.getBlockHardness(worldIn, pos) == -1.0F) {
                  return false;
               }

               if (blockIn.getMobilityFlag() == 2) {
                  return false;
               }

               if (blockIn.getMobilityFlag() == 1) {
                  if (!allowDestroy) {
                     return false;
                  }

                  return true;
               }
            } else if (worldIn.getBlockState(pos).getValue(EXTENDED)) {
               return false;
            }

            return !(blockIn instanceof ITileEntityProvider);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!worldIn.isRemote) {
         this.checkForMove(worldIn, pos, state);
      }
   }

   public BlockPistonBase(boolean isSticky) {
      super(Material.piston);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(EXTENDED, false));
      this.isSticky = isSticky;
      this.setStepSound(soundTypePiston);
      this.setHardness(0.5F);
      this.setCreativeTab(CreativeTabs.tabRedstone);
   }

   private boolean shouldBeExtended(World worldIn, BlockPos pos, EnumFacing facing) {
      for (EnumFacing enumFacing : EnumFacing.values()) {
         if (enumFacing != facing && worldIn.isSidePowered(pos.offset(enumFacing), enumFacing)) {
            return true;
         }
      }

      if (worldIn.isSidePowered(pos, EnumFacing.DOWN)) {
         return true;
      }

      BlockPos blockPos2 = pos.up();

      for (EnumFacing enumFacing2 : EnumFacing.values()) {
         if (enumFacing2 != EnumFacing.DOWN && worldIn.isSidePowered(blockPos2.offset(enumFacing2), enumFacing2)) {
            return true;
         }
      }

      return false;
   }
}
