package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;

public class BlockFire extends Block {
   public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);
   public static final PropertyBool FLIP = PropertyBool.create("flip");
   public static final PropertyBool ALT = PropertyBool.create("alt");
   private final Map<Block, Integer> encouragements = Maps.newIdentityHashMap();
   public static final PropertyBool NORTH = PropertyBool.create("north");
   public static final PropertyBool EAST = PropertyBool.create("east");
   private final Map<Block, Integer> flammabilities = Maps.newIdentityHashMap();
   public static final PropertyBool SOUTH = PropertyBool.create("south");
   public static final PropertyBool WEST = PropertyBool.create("west");
   public static final PropertyInteger UPPER = PropertyInteger.create("upper", 0, 2);

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return null;
   }

   protected boolean canDie(World worldIn, BlockPos pos) {
      return worldIn.isRainingAt(pos)
         || worldIn.isRainingAt(pos.west())
         || worldIn.isRainingAt(pos.east())
         || worldIn.isRainingAt(pos.north())
         || worldIn.isRainingAt(pos.south());
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   private int getEncouragement(Block blockIn) {
      Integer integer = this.encouragements.get(blockIn);
      return integer == null ? 0 : integer;
   }

   @Override
   public boolean requiresUpdates() {
      return false;
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      int xCoordinate = pos.getX();
      int yCoordinate = pos.getY();
      int zCoordinate = pos.getZ();
      if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && !Blocks.fire.canCatchFire(worldIn, pos.down())) {
         boolean flag = (xCoordinate + yCoordinate + zCoordinate & 1) == 1;
         boolean flag2 = (xCoordinate / 2 + yCoordinate / 2 + zCoordinate / 2 & 1) == 1;
         int i = 0;
         if (this.canCatchFire(worldIn, pos.up())) {
            i = flag ? 1 : 2;
         }

         return state.withProperty(NORTH, this.canCatchFire(worldIn, pos.north()))
            .withProperty(EAST, this.canCatchFire(worldIn, pos.east()))
            .withProperty(SOUTH, this.canCatchFire(worldIn, pos.south()))
            .withProperty(WEST, this.canCatchFire(worldIn, pos.west()))
            .withProperty(UPPER, i)
            .withProperty(FLIP, flag2)
            .withProperty(ALT, flag);
      } else {
         return this.getDefaultState();
      }
   }

   private void catchOnFire(World worldIn, BlockPos pos, int chance, Random random, int age) {
      int flammability2 = this.getFlammability(worldIn.getBlockState(pos).getBlock());
      if (random.nextInt(chance) < flammability2) {
         IBlockState blockState = worldIn.getBlockState(pos);
         if (random.nextInt(age + 10) < 5 && !worldIn.isRainingAt(pos)) {
            int i = age + random.nextInt(5) / 4;
            if (i > 15) {
               i = 15;
            }

            worldIn.setBlockState(pos, this.getDefaultState().withProperty(AGE, i), 3);
         } else {
            worldIn.setBlockToAir(pos);
         }

         if (blockState.getBlock() == Blocks.tnt) {
            Blocks.tnt.onBlockDestroyedByPlayer(worldIn, pos, blockState.withProperty(BlockTNT.EXPLODE, true));
         }
      }
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (worldIn.getGameRules().getBoolean("doFireTick")) {
         if (!this.canPlaceBlockAt(worldIn, pos)) {
            worldIn.setBlockToAir(pos);
         }

         Block block = worldIn.getBlockState(pos.down()).getBlock();
         boolean conditionMet = block == Blocks.netherrack;
         if (worldIn.provider instanceof WorldProviderEnd && block == Blocks.bedrock) {
            conditionMet = true;
         }

         if (!conditionMet && worldIn.isRaining() && this.canDie(worldIn, pos)) {
            worldIn.setBlockToAir(pos);
         } else {
            int value = state.getValue(AGE);
            if (value < 15) {
               state = state.withProperty(AGE, value + rand.nextInt(3) / 2);
               worldIn.setBlockState(pos, state, 4);
            }

            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn) + rand.nextInt(10));
            if (!conditionMet) {
               if (!this.canNeighborCatchFire(worldIn, pos)) {
                  if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) || value > 3) {
                     worldIn.setBlockToAir(pos);
                  }

                  return;
               }

               if (!this.canCatchFire(worldIn, pos.down()) && value == 15 && rand.nextInt(4) == 0) {
                  worldIn.setBlockToAir(pos);
                  return;
               }
            }

            boolean isBlockinHighHumidity = worldIn.isBlockinHighHumidity(pos);
            int i = 0;
            if (isBlockinHighHumidity) {
               i = -50;
            }

            this.catchOnFire(worldIn, pos.east(), 300 + i, rand, value);
            this.catchOnFire(worldIn, pos.west(), 300 + i, rand, value);
            this.catchOnFire(worldIn, pos.down(), 250 + i, rand, value);
            this.catchOnFire(worldIn, pos.up(), 250 + i, rand, value);
            this.catchOnFire(worldIn, pos.north(), 300 + i, rand, value);
            this.catchOnFire(worldIn, pos.south(), 300 + i, rand, value);

            for (int nestedIndex = -1; nestedIndex <= 1; nestedIndex++) {
               for (int outerIndex = -1; outerIndex <= 1; outerIndex++) {
                  for (int index = -1; index <= 4; index++) {
                     if (nestedIndex != 0 || index != 0 || outerIndex != 0) {
                        int i2 = 100;
                        if (index > 1) {
                           i2 = 100 + (index - 1) * 100;
                        }

                        BlockPos blockPos = pos.add(nestedIndex, index, outerIndex);
                        int neighborEncouragement = this.getNeighborEncouragement(worldIn, blockPos);
                        if (neighborEncouragement > 0) {
                           int i3 = (neighborEncouragement + 40 + worldIn.getDifficulty().getDifficultyId() * 7) / (value + 30);
                           if (isBlockinHighHumidity) {
                              i3 /= 2;
                           }

                           if (i3 > 0
                              && rand.nextInt(i2) <= i3
                              && (!worldIn.isRaining() || !this.canDie(worldIn, blockPos))) {
                              int i4 = value + rand.nextInt(5) / 4;
                              if (i4 > 15) {
                                 i4 = 15;
                              }

                              worldIn.setBlockState(blockPos, state.withProperty(AGE, i4), 3);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public boolean canCatchFire(IBlockAccess worldIn, BlockPos pos) {
      return this.getEncouragement(worldIn.getBlockState(pos).getBlock()) > 0;
   }

   @Override
   public int tickRate(World worldIn) {
      return 30;
   }

   private int getFlammability(Block blockIn) {
      Integer integer = this.flammabilities.get(blockIn);
      return integer == null ? 0 : integer;
   }

   @Override
   public MapColor getMapColor(IBlockState state) {
      return MapColor.tntColor;
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      if (worldIn.provider.getDimensionId() > 0 || !Blocks.portal.tryPlacePortal(worldIn, pos)) {
         if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && !this.canNeighborCatchFire(worldIn, pos)) {
            worldIn.setBlockToAir(pos);
         } else {
            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn) + worldIn.rand.nextInt(10));
         }
      }
   }

   public static void init() {
      Blocks.fire.setFireInfo(Blocks.planks, 5, 20);
      Blocks.fire.setFireInfo(Blocks.double_wooden_slab, 5, 20);
      Blocks.fire.setFireInfo(Blocks.wooden_slab, 5, 20);
      Blocks.fire.setFireInfo(Blocks.oak_fence_gate, 5, 20);
      Blocks.fire.setFireInfo(Blocks.spruce_fence_gate, 5, 20);
      Blocks.fire.setFireInfo(Blocks.birch_fence_gate, 5, 20);
      Blocks.fire.setFireInfo(Blocks.jungle_fence_gate, 5, 20);
      Blocks.fire.setFireInfo(Blocks.dark_oak_fence_gate, 5, 20);
      Blocks.fire.setFireInfo(Blocks.acacia_fence_gate, 5, 20);
      Blocks.fire.setFireInfo(Blocks.oak_fence, 5, 20);
      Blocks.fire.setFireInfo(Blocks.spruce_fence, 5, 20);
      Blocks.fire.setFireInfo(Blocks.birch_fence, 5, 20);
      Blocks.fire.setFireInfo(Blocks.jungle_fence, 5, 20);
      Blocks.fire.setFireInfo(Blocks.dark_oak_fence, 5, 20);
      Blocks.fire.setFireInfo(Blocks.acacia_fence, 5, 20);
      Blocks.fire.setFireInfo(Blocks.oak_stairs, 5, 20);
      Blocks.fire.setFireInfo(Blocks.birch_stairs, 5, 20);
      Blocks.fire.setFireInfo(Blocks.spruce_stairs, 5, 20);
      Blocks.fire.setFireInfo(Blocks.jungle_stairs, 5, 20);
      Blocks.fire.setFireInfo(Blocks.log, 5, 5);
      Blocks.fire.setFireInfo(Blocks.log2, 5, 5);
      Blocks.fire.setFireInfo(Blocks.leaves, 30, 60);
      Blocks.fire.setFireInfo(Blocks.leaves2, 30, 60);
      Blocks.fire.setFireInfo(Blocks.bookshelf, 30, 20);
      Blocks.fire.setFireInfo(Blocks.tnt, 15, 100);
      Blocks.fire.setFireInfo(Blocks.tallgrass, 60, 100);
      Blocks.fire.setFireInfo(Blocks.double_plant, 60, 100);
      Blocks.fire.setFireInfo(Blocks.yellow_flower, 60, 100);
      Blocks.fire.setFireInfo(Blocks.red_flower, 60, 100);
      Blocks.fire.setFireInfo(Blocks.deadbush, 60, 100);
      Blocks.fire.setFireInfo(Blocks.wool, 30, 60);
      Blocks.fire.setFireInfo(Blocks.vine, 15, 100);
      Blocks.fire.setFireInfo(Blocks.coal_block, 5, 5);
      Blocks.fire.setFireInfo(Blocks.hay_block, 60, 20);
      Blocks.fire.setFireInfo(Blocks.carpet, 60, 20);
   }

   private int getNeighborEncouragement(World worldIn, BlockPos pos) {
      if (!worldIn.isAirBlock(pos)) {
         return 0;
      }

      int i = 0;

      for (EnumFacing enumFacing : EnumFacing.values()) {
         i = Math.max(this.getEncouragement(worldIn.getBlockState(pos.offset(enumFacing)).getBlock()), i);
      }

      return i;
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && !this.canNeighborCatchFire(worldIn, pos)) {
         worldIn.setBlockToAir(pos);
      }
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (rand.nextInt(24) == 0) {
         worldIn.playSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, "fire.fire", 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
      }

      if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && !Blocks.fire.canCatchFire(worldIn, pos.down())) {
         if (Blocks.fire.canCatchFire(worldIn, pos.west())) {
            for (int innerIndex = 0; innerIndex < 2; innerIndex++) {
               double xCoordinate = pos.getX() + rand.nextDouble() * 0.1F;
               double yCoordinate = pos.getY() + rand.nextDouble();
               double zCoordinate = pos.getZ() + rand.nextDouble();
               worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, xCoordinate, yCoordinate, zCoordinate, 0.0, 0.0, 0.0);
            }
         }

         if (Blocks.fire.canCatchFire(worldIn, pos.east())) {
            for (int nestedIndex = 0; nestedIndex < 2; nestedIndex++) {
               double xCoordinate2 = pos.getX() + 1 - rand.nextDouble() * 0.1F;
               double yCoordinate2 = pos.getY() + rand.nextDouble();
               double zCoordinate2 = pos.getZ() + rand.nextDouble();
               worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, xCoordinate2, yCoordinate2, zCoordinate2, 0.0, 0.0, 0.0);
            }
         }

         if (Blocks.fire.canCatchFire(worldIn, pos.north())) {
            for (int outerIndex = 0; outerIndex < 2; outerIndex++) {
               double xCoordinate3 = pos.getX() + rand.nextDouble();
               double yCoordinate3 = pos.getY() + rand.nextDouble();
               double zCoordinate3 = pos.getZ() + rand.nextDouble() * 0.1F;
               worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, xCoordinate3, yCoordinate3, zCoordinate3, 0.0, 0.0, 0.0);
            }
         }

         if (Blocks.fire.canCatchFire(worldIn, pos.south())) {
            for (int index = 0; index < 2; index++) {
               double xCoordinate4 = pos.getX() + rand.nextDouble();
               double yCoordinate4 = pos.getY() + rand.nextDouble();
               double zCoordinate4 = pos.getZ() + 1 - rand.nextDouble() * 0.1F;
               worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, xCoordinate4, yCoordinate4, zCoordinate4, 0.0, 0.0, 0.0);
            }
         }

         if (Blocks.fire.canCatchFire(worldIn, pos.up())) {
            for (int innerIndex2 = 0; innerIndex2 < 2; innerIndex2++) {
               double xCoordinate5 = pos.getX() + rand.nextDouble();
               double yCoordinate5 = pos.getY() + 1 - rand.nextDouble() * 0.1F;
               double zCoordinate5 = pos.getZ() + rand.nextDouble();
               worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, xCoordinate5, yCoordinate5, zCoordinate5, 0.0, 0.0, 0.0);
            }
         }
      } else {
         for (int index2 = 0; index2 < 3; index2++) {
            double xCoordinate6 = pos.getX() + rand.nextDouble();
            double yCoordinate6 = pos.getY() + rand.nextDouble() * 0.5 + 0.5;
            double zCoordinate6 = pos.getZ() + rand.nextDouble();
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, xCoordinate6, yCoordinate6, zCoordinate6, 0.0, 0.0, 0.0);
         }
      }
   }

   private boolean canNeighborCatchFire(World worldIn, BlockPos pos) {
      for (EnumFacing enumFacing : EnumFacing.values()) {
         if (this.canCatchFire(worldIn, pos.offset(enumFacing))) {
            return true;
         }
      }

      return false;
   }

   protected BlockFire() {
      super(Material.fire);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(AGE, 0)
            .withProperty(FLIP, false)
            .withProperty(ALT, false)
            .withProperty(NORTH, false)
            .withProperty(EAST, false)
            .withProperty(SOUTH, false)
            .withProperty(WEST, false)
            .withProperty(UPPER, 0)
      );
      this.setTickRandomly(true);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean isCollidable() {
      return false;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(AGE, meta);
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) || this.canNeighborCatchFire(worldIn, pos);
   }

   @Override
   public int quantityDropped(Random random) {
      return 0;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(AGE);
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, AGE, NORTH, EAST, SOUTH, WEST, UPPER, FLIP, ALT);
   }

   public void setFireInfo(Block blockIn, int encouragement, int flammability) {
      this.encouragements.put(blockIn, encouragement);
      this.flammabilities.put(blockIn, flammability);
   }
}
