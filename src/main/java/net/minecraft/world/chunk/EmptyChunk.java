package net.minecraft.world.chunk;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class EmptyChunk extends Chunk {
   @Override
   public <T extends Entity> void getEntitiesOfTypeWithinAAAB(
      Class<? extends T> entityClass, AxisAlignedBB aabb, List<T> listToFill, Predicate<? super T> predicate
   ) {
   }

   @Override
   public void setLightFor(EnumSkyBlock enumSkyBlock, BlockPos pos, int value) {
   }

   @Override
   public int getLightFor(EnumSkyBlock enumSkyBlock, BlockPos pos) {
      return enumSkyBlock.defaultLightValue;
   }

   @Override
   public void addTileEntity(TileEntity tileEntityIn) {
   }

   @Override
   public void getEntitiesWithinAABBForEntity(Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, Predicate<? super Entity> predicate) {
   }

   @Override
   public Block getBlock(BlockPos pos) {
      return Blocks.air;
   }

   @Override
   public int getLightSubtracted(BlockPos pos, int amount) {
      return 0;
   }

   @Override
   public boolean isEmpty() {
      return true;
   }

   @Override
   public boolean isLightInitialized() {
      return false;
   }

   @Override
   public void addEntity(Entity entity2) {
   }

   @Override
   public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType enumCreateEntityType) {
      return null;
   }

   @Override
   public int getBlockLightOpacity(BlockPos pos) {
      return 255;
   }

   @Override
   public void generateSkylightMap() {
   }

   @Override
   public void removeEntity(Entity entity2) {
   }

   @Override
   public boolean getAreLevelsEmpty(int startY, int endY) {
      return true;
   }

   @Override
   public void setChunkModified() {
   }

   @Override
   public int getBlockMetadata(BlockPos pos) {
      return 0;
   }

   public EmptyChunk(World worldIn, int x, int z) {
      super(worldIn, x, z);
   }

   @Override
   public boolean canSeeSky(BlockPos pos) {
      return false;
   }

   @Override
   public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
   }

   @Override
   public int getHeightValue(int x, int z) {
      return 0;
   }

   @Override
   public boolean isAtLocation(int x, int z) {
      return x == this.xPosition && z == this.zPosition;
   }

   @Override
   public void onChunkUnload() {
   }

   @Override
   public void onChunkLoad() {
   }

   @Override
   public void removeEntityAtIndex(Entity entityIn, int i) {
   }

   @Override
   public Random getRandom() {
      return new Random(
         this.getWorld().getSeed()
               + this.xPosition * this.xPosition * 4987142
               + this.xPosition * 5947611
               + this.zPosition * this.zPosition * 4392871L
               + this.zPosition * 389711
            ^ 987234911L
      );
   }

   @Override
   public void generateHeightMap() {
   }

   @Override
   public void removeTileEntity(BlockPos pos) {
   }
}
