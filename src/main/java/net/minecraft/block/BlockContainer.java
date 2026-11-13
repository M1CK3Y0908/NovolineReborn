package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class BlockContainer extends Block implements ITileEntityProvider {
   protected BlockContainer(Material materialIn) {
      this(materialIn, materialIn.getMaterialMapColor());
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      super.breakBlock(worldIn, pos, state);
      worldIn.removeTileEntity(pos);
   }

   protected boolean hasInvalidNeighbor(World world2, BlockPos blockPos) {
      return this.isInvalidNeighbor(world2, blockPos, EnumFacing.NORTH)
         || this.isInvalidNeighbor(world2, blockPos, EnumFacing.SOUTH)
         || this.isInvalidNeighbor(world2, blockPos, EnumFacing.WEST)
         || this.isInvalidNeighbor(world2, blockPos, EnumFacing.EAST);
   }

   protected boolean isInvalidNeighbor(World world2, BlockPos blockPos, EnumFacing enumFacing) {
      return world2.getBlockState(blockPos.offset(enumFacing)).getBlock().getMaterial() == Material.cactus;
   }

   protected BlockContainer(Material material2, MapColor mapColor) {
      super(material2, mapColor);
      this.isBlockContainer = true;
   }

   @Override
   public int getRenderType() {
      return -1;
   }

   @Override
   public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam) {
      super.onBlockEventReceived(worldIn, pos, state, eventID, eventParam);
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      return tileEntity == null ? false : tileEntity.receiveClientEvent(eventID, eventParam);
   }
}
