package net.minecraft.block;

import com.google.common.base.Predicate;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockStateHelper;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockPumpkin extends BlockDirectional {
   private BlockPattern snowmanBasePattern;
   private BlockPattern snowmanPattern;
   private BlockPattern golemBasePattern;
   private static final Predicate<IBlockState> PUMPKIN_PREDICATE = new Predicate<IBlockState>() {
      public boolean apply(IBlockState arg1) {
         return arg1 != null && (arg1.getBlock() == Blocks.pumpkin || arg1.getBlock() == Blocks.lit_pumpkin);
      }
   };
   private BlockPattern golemPattern;

   protected BlockPumpkin() {
      super(Material.gourd, MapColor.adobeColor);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
   }

   protected BlockPattern getGolemBasePattern() {
      if (this.golemBasePattern == null) {
         this.golemBasePattern = FactoryBlockPattern.start()
            .aisle("~ ~", "###", "~#~")
            .where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.iron_block)))
            .where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air)))
            .build();
      }

      return this.golemBasePattern;
   }

   protected BlockPattern getGolemPattern() {
      if (this.golemPattern == null) {
         this.golemPattern = FactoryBlockPattern.start()
            .aisle("~^~", "###", "~#~")
            .where('^', BlockWorldState.hasState(PUMPKIN_PREDICATE))
            .where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.iron_block)))
            .where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air)))
            .build();
      }

      return this.golemPattern;
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, FACING);
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos).getBlock().blockMaterial.isReplaceable() && World.doesBlockHaveSolidTopSurface(worldIn, pos.down());
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      super.onBlockAdded(worldIn, pos, state);
      this.trySpawnGolem(worldIn, pos);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(FACING).getHorizontalIndex();
   }

   private void trySpawnGolem(World worldIn, BlockPos pos) {
      net.minecraft.block.state.pattern.BlockPattern.PatternHelper patternHelper;
      if ((patternHelper = this.getSnowmanPattern().match(worldIn, pos)) != null) {
         for (int i = 0; i < this.getSnowmanPattern().getThumbLength(); i++) {
            BlockWorldState blockWorldState = patternHelper.translateOffset(0, i, 0);
            worldIn.setBlockState(blockWorldState.getPos(), Blocks.air.getDefaultState(), 2);
         }

         EntitySnowman entitySnowman = new EntitySnowman(worldIn);
         BlockPos pos2 = patternHelper.translateOffset(0, 2, 0).getPos();
         entitySnowman.setLocationAndAngles(pos2.getX() + 0.5, pos2.getY() + 0.05, pos2.getZ() + 0.5, 0.0F, 0.0F);
         worldIn.spawnEntityInWorld(entitySnowman);

         for (int i2 = 0; i2 < 120; i2++) {
            worldIn.spawnParticle(
               EnumParticleTypes.SNOW_SHOVEL,
               pos2.getX() + worldIn.rand.nextDouble(),
               pos2.getY() + worldIn.rand.nextDouble() * 2.5,
               pos2.getZ() + worldIn.rand.nextDouble(),
               0.0,
               0.0,
               0.0
            );
         }

         for (int i3 = 0; i3 < this.getSnowmanPattern().getThumbLength(); i3++) {
            BlockWorldState blockWorldState2 = patternHelper.translateOffset(0, i3, 0);
            worldIn.notifyNeighborsRespectDebug(blockWorldState2.getPos(), Blocks.air);
         }
      } else if ((patternHelper = this.getGolemPattern().match(worldIn, pos)) != null) {
         for (int i4 = 0; i4 < this.getGolemPattern().getPalmLength(); i4++) {
            for (int i5 = 0; i5 < this.getGolemPattern().getThumbLength(); i5++) {
               worldIn.setBlockState(patternHelper.translateOffset(i4, i5, 0).getPos(), Blocks.air.getDefaultState(), 2);
            }
         }

         BlockPos pos3 = patternHelper.translateOffset(1, 2, 0).getPos();
         EntityIronGolem entityIronGolem = new EntityIronGolem(worldIn);
         entityIronGolem.setPlayerCreated(true);
         entityIronGolem.setLocationAndAngles(pos3.getX() + 0.5, pos3.getY() + 0.05, pos3.getZ() + 0.5, 0.0F, 0.0F);
         worldIn.spawnEntityInWorld(entityIronGolem);

         for (int i6 = 0; i6 < 120; i6++) {
            worldIn.spawnParticle(
               EnumParticleTypes.SNOWBALL,
               pos3.getX() + worldIn.rand.nextDouble(),
               pos3.getY() + worldIn.rand.nextDouble() * 3.9,
               pos3.getZ() + worldIn.rand.nextDouble(),
               0.0,
               0.0,
               0.0
            );
         }

         for (int i7 = 0; i7 < this.getGolemPattern().getPalmLength(); i7++) {
            for (int i8 = 0; i8 < this.getGolemPattern().getThumbLength(); i8++) {
               BlockWorldState blockWorldState3 = patternHelper.translateOffset(i7, i8, 0);
               worldIn.notifyNeighborsRespectDebug(blockWorldState3.getPos(), Blocks.air);
            }
         }
      }
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
   }

   protected BlockPattern getSnowmanBasePattern() {
      if (this.snowmanBasePattern == null) {
         this.snowmanBasePattern = FactoryBlockPattern.start()
            .aisle(" ", "#", "#")
            .where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.snow)))
            .build();
      }

      return this.snowmanBasePattern;
   }

   protected BlockPattern getSnowmanPattern() {
      if (this.snowmanPattern == null) {
         this.snowmanPattern = FactoryBlockPattern.start()
            .aisle("^", "#", "#")
            .where('^', BlockWorldState.hasState(PUMPKIN_PREDICATE))
            .where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.snow)))
            .build();
      }

      return this.snowmanPattern;
   }

   public boolean canDispenserPlace(World worldIn, BlockPos pos) {
      return this.getSnowmanBasePattern().match(worldIn, pos) != null || this.getGolemBasePattern().match(worldIn, pos) != null;
   }
}
