package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

public abstract class BlockLiquid extends Block {
   public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 15);

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, LEVEL);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(LEVEL, meta);
   }

   @Override
   public Vec3 modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3 motion) {
      return motion.add(this.getFlowVector(worldIn, pos));
   }

   public static double getFlowDirection(IBlockAccess worldIn, BlockPos pos, Material materialIn) {
      Vec3 vec3 = getFlowingBlock(materialIn).getFlowVector(worldIn, pos);
      return vec3.xCoord == 0.0 && vec3.zCoord == 0.0 ? -1000.0 : MathHelper.atan2(vec3.zCoord, vec3.xCoord) - (Math.PI / 2);
   }

   @Override
   public int tickRate(World worldIn) {
      return this.blockMaterial == Material.water ? 5 : (this.blockMaterial == Material.lava ? (worldIn.provider.getHasNoSky() ? 10 : 30) : 0);
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return null;
   }

   @Override
   public int quantityDropped(Random random) {
      return 0;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      this.checkForMixing(worldIn, pos, state);
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return null;
   }

   public static float getLiquidHeightPercent(int meta) {
      if (meta >= 8) {
         meta = 0;
      }

      return (meta + 1) / 9.0F;
   }

   public boolean checkForMixing(World worldIn, BlockPos pos, IBlockState state) {
      if (this.blockMaterial == Material.lava) {
         boolean flag = false;

         for (EnumFacing enumFacing : EnumFacing.values()) {
            if (enumFacing != EnumFacing.DOWN && worldIn.getBlockState(pos.offset(enumFacing)).getBlock().getMaterial() == Material.water) {
               flag = true;
               break;
            }
         }

         if (flag) {
            Integer value = state.getValue(LEVEL);
            if (value == 0) {
               worldIn.setBlockState(pos, Blocks.obsidian.getDefaultState());
               this.triggerMixEffects(worldIn, pos);
               return true;
            }

            if (value <= 4) {
               worldIn.setBlockState(pos, Blocks.cobblestone.getDefaultState());
               this.triggerMixEffects(worldIn, pos);
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
      return hitIfLiquid && state.getValue(LEVEL) == 0;
   }

   @Override
   public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      Material material = worldIn.getBlockState(pos).getBlock().getMaterial();
      return material == this.blockMaterial
         ? false
         : (side == EnumFacing.UP ? true : (material == Material.ice ? false : super.isBlockSolid(worldIn, pos, side)));
   }

   protected Vec3 getFlowVector(IBlockAccess worldIn, BlockPos pos) {
      Vec3 vec32 = new Vec3(0.0, 0.0, 0.0);
      int effectiveFlowDecay = this.getEffectiveFlowDecay(worldIn, pos);

      for (EnumFacing enumFacing : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
         BlockPos blockPos = pos.offset(enumFacing);
         int effectiveFlowDecay2 = this.getEffectiveFlowDecay(worldIn, blockPos);
         if (effectiveFlowDecay2 < 0) {
            if (!worldIn.getBlockState(blockPos).getBlock().getMaterial().blocksMovement()) {
               effectiveFlowDecay2 = this.getEffectiveFlowDecay(worldIn, blockPos.down());
               if (effectiveFlowDecay2 >= 0) {
                  int i = effectiveFlowDecay2 - (effectiveFlowDecay - 8);
                  vec32 = vec32.addVector((blockPos.getX() - pos.getX()) * i, (blockPos.getY() - pos.getY()) * i, (blockPos.getZ() - pos.getZ()) * i);
               }
            }
         } else if (effectiveFlowDecay2 >= 0) {
            int i2 = effectiveFlowDecay2 - effectiveFlowDecay;
            vec32 = vec32.addVector((blockPos.getX() - pos.getX()) * i2, (blockPos.getY() - pos.getY()) * i2, (blockPos.getZ() - pos.getZ()) * i2);
         }
      }

      if (worldIn.getBlockState(pos).getValue(LEVEL) >= 8) {
         for (EnumFacing enumFacing2 : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
            BlockPos blockPos2 = pos.offset(enumFacing2);
            if (this.isBlockSolid(worldIn, blockPos2, enumFacing2) || this.isBlockSolid(worldIn, blockPos2.up(), enumFacing2)) {
               vec32 = vec32.normalize().addVector(0.0, -6.0, 0.0);
               break;
            }
         }
      }

      return vec32.normalize();
   }

   protected int getEffectiveFlowDecay(IBlockAccess worldIn, BlockPos pos) {
      int level2 = this.getLevel(worldIn, pos);
      return level2 >= 8 ? 0 : level2;
   }

   protected void triggerMixEffects(World worldIn, BlockPos pos) {
      double xCoordinate = pos.getX();
      double yCoordinate = pos.getY();
      double zCoordinate = pos.getZ();
      worldIn.playSoundEffect(
         xCoordinate + 0.5, yCoordinate + 0.5, zCoordinate + 0.5, "random.fizz", 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F
      );

      for (int index = 0; index < 8; index++) {
         worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, xCoordinate + Math.random(), yCoordinate + 1.2, zCoordinate + Math.random(), 0.0, 0.0, 0.0);
      }
   }

   @Override
   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return this.blockMaterial != Material.lava;
   }

   @Override
   public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
      return this.blockMaterial == Material.water ? BiomeColorHelper.getWaterColorAtPos(worldIn, pos) : 16777215;
   }

   protected int getLevel(IBlockAccess worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos).getBlock().getMaterial() == this.blockMaterial ? worldIn.getBlockState(pos).getValue(LEVEL) : -1;
   }

   @Override
   public int getMixedBrightnessForBlock(IBlockAccess worldIn, BlockPos pos) {
      int combinedLight = worldIn.getCombinedLight(pos, 0);
      int combinedLight2 = worldIn.getCombinedLight(pos.up(), 0);
      int i = combinedLight & 0xFF;
      int i2 = combinedLight2 & 0xFF;
      int i3 = combinedLight >> 16 & 0xFF;
      int i4 = combinedLight2 >> 16 & 0xFF;
      return (i > i2 ? i : i2) | (i3 > i4 ? i3 : i4) << 16;
   }

   protected BlockLiquid(Material materialIn) {
      super(materialIn);
      this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, 0));
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      this.setTickRandomly(true);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(LEVEL);
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      this.checkForMixing(worldIn, pos, state);
   }

   public static BlockStaticLiquid getStaticBlock(Material materialIn) {
      if (materialIn == Material.water) {
         return Blocks.water;
      } else if (materialIn == Material.lava) {
         return Blocks.lava;
      } else {
         throw new IllegalArgumentException("Invalid material");
      }
   }

   public boolean shouldRenderSides(IBlockAccess blockAccess, BlockPos pos) {
      for (int i = -1; i <= 1; i++) {
         for (int i2 = -1; i2 <= 1; i2++) {
            Block block2 = blockAccess.getBlockState(pos.add(i, 0, i2)).getBlock();
            if (block2.getMaterial() != this.blockMaterial && !block2.isFullBlock()) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      return worldIn.getBlockState(pos).getBlock().getMaterial() == this.blockMaterial
         ? false
         : (side == EnumFacing.UP ? true : super.shouldSideBeRendered(worldIn, pos, side));
   }

   public static BlockDynamicLiquid getFlowingBlock(Material materialIn) {
      if (materialIn == Material.water) {
         return Blocks.flowing_water;
      } else if (materialIn == Material.lava) {
         return Blocks.flowing_lava;
      } else {
         throw new IllegalArgumentException("Invalid material");
      }
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      double xCoordinate = pos.getX();
      double yCoordinate = pos.getY();
      double zCoordinate = pos.getZ();
      if (this.blockMaterial == Material.water) {
         int value = state.getValue(LEVEL);
         if (value > 0 && value < 8) {
            if (rand.nextInt(64) == 0) {
               worldIn.playSound(
                  xCoordinate + 0.5,
                  yCoordinate + 0.5,
                  zCoordinate + 0.5,
                  "liquid.water",
                  rand.nextFloat() * 0.25F + 0.75F,
                  rand.nextFloat() * 1.0F + 0.5F,
                  false
               );
            }
         } else if (rand.nextInt(10) == 0) {
            worldIn.spawnParticle(
               EnumParticleTypes.SUSPENDED, xCoordinate + rand.nextFloat(), yCoordinate + rand.nextFloat(), zCoordinate + rand.nextFloat(), 0.0, 0.0, 0.0
            );
         }
      }

      if (this.blockMaterial == Material.lava
         && worldIn.getBlockState(pos.up()).getBlock().getMaterial() == Material.air
         && !worldIn.getBlockState(pos.up()).getBlock().isOpaqueCube()) {
         if (rand.nextInt(100) == 0) {
            double d = xCoordinate + rand.nextFloat();
            double d2 = yCoordinate + this.maxY;
            double d3 = zCoordinate + rand.nextFloat();
            worldIn.spawnParticle(EnumParticleTypes.LAVA, d, d2, d3, 0.0, 0.0, 0.0);
            worldIn.playSound(d, d2, d3, "liquid.lavapop", 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
         }

         if (rand.nextInt(200) == 0) {
            worldIn.playSound(xCoordinate, yCoordinate, zCoordinate, "liquid.lava", 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
         }
      }

      if (rand.nextInt(10) == 0 && World.doesBlockHaveSolidTopSurface(worldIn, pos.down())) {
         Material material = worldIn.getBlockState(pos.down(2)).getBlock().getMaterial();
         if (!material.blocksMovement() && !material.isLiquid()) {
            double d4 = xCoordinate + rand.nextFloat();
            double d5 = yCoordinate - 1.05;
            double d6 = zCoordinate + rand.nextFloat();
            if (this.blockMaterial == Material.water) {
               worldIn.spawnParticle(EnumParticleTypes.DRIP_WATER, d4, d5, d6, 0.0, 0.0, 0.0);
            } else {
               worldIn.spawnParticle(EnumParticleTypes.DRIP_LAVA, d4, d5, d6, 0.0, 0.0, 0.0);
            }
         }
      }
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return this.blockMaterial == Material.water ? EnumWorldBlockLayer.TRANSLUCENT : EnumWorldBlockLayer.SOLID;
   }

   @Override
   public int getRenderType() {
      return 1;
   }
}
