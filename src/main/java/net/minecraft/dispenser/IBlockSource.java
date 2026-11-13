package net.minecraft.dispenser;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

public interface IBlockSource extends ILocatableSource {
   int getBlockMetadata();

   <T extends TileEntity> T getBlockTileEntity();

   BlockPos getBlockPos();

   @Override
   double getX();

   @Override
   double getZ();

   @Override
   double getY();
}
