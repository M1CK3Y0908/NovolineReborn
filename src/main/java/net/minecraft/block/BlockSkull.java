package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockStateHelper;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StatCollector;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSkull extends BlockContainer {
   private BlockPattern witherBasePattern;
   public static final PropertyDirection FACING = PropertyDirection.create("facing");
   private BlockPattern witherPattern;
   public static final PropertyBool NODROP = PropertyBool.create("nodrop");
   private static final Predicate<BlockWorldState> IS_WITHER_SKELETON = new Predicate<BlockWorldState>() {
      public boolean apply(BlockWorldState arg1) {
         return arg1.getBlockState() != null
            && arg1.getBlockState().getBlock() == Blocks.skull
            && arg1.getTileEntity() instanceof TileEntitySkull
            && ((TileEntitySkull)arg1.getTileEntity()).getSkullType() == 1;
      }
   };

   @Override
   public int getMetaFromState(IBlockState state) {
      int i = 0 | state.getValue(FACING).getIndex();
      if (state.getValue(NODROP)) {
         i |= 8;
      }

      return i;
   }

   @Override
   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal("tile.skull.skeleton.name");
   }

   public void checkWitherSpawn(World worldIn, BlockPos pos, TileEntitySkull te) {
      if (te.getSkullType() == 1 && pos.getY() >= 2 && worldIn.getDifficulty() != EnumDifficulty.PEACEFUL && !worldIn.isRemote) {
         BlockPattern witherPattern2 = this.getWitherPattern();
         net.minecraft.block.state.pattern.BlockPattern.PatternHelper patternHelper = witherPattern2.match(worldIn, pos);
         if (patternHelper != null) {
            for (int i = 0; i < 3; i++) {
               BlockWorldState blockWorldState = patternHelper.translateOffset(i, 0, 0);
               worldIn.setBlockState(blockWorldState.getPos(), blockWorldState.getBlockState().withProperty(NODROP, true), 2);
            }

            for (int i2 = 0; i2 < witherPattern2.getPalmLength(); i2++) {
               for (int i3 = 0; i3 < witherPattern2.getThumbLength(); i3++) {
                  BlockWorldState blockWorldState2 = patternHelper.translateOffset(i2, i3, 0);
                  worldIn.setBlockState(blockWorldState2.getPos(), Blocks.air.getDefaultState(), 2);
               }
            }

            BlockPos pos2 = patternHelper.translateOffset(1, 0, 0).getPos();
            EntityWither entityWither = new EntityWither(worldIn);
            BlockPos pos3 = patternHelper.translateOffset(1, 2, 0).getPos();
            entityWither.setLocationAndAngles(
               pos3.getX() + 0.5, pos3.getY() + 0.55, pos3.getZ() + 0.5, patternHelper.getFinger().getAxis() == net.minecraft.util.EnumFacing.Axis.X ? 0.0F : 90.0F, 0.0F
            );
            entityWither.renderYawOffset = patternHelper.getFinger().getAxis() == net.minecraft.util.EnumFacing.Axis.X ? 0.0F : 90.0F;
            entityWither.setWitherInvulnerability();

            for (EntityPlayer entityPlayer : worldIn.getEntitiesWithinAABB(EntityPlayer.class, entityWither.getEntityBoundingBox().expand(50.0, 50.0, 50.0))) {
               entityPlayer.triggerAchievement(AchievementList.spawnWither);
            }

            worldIn.spawnEntityInWorld(entityWither);

            for (int i4 = 0; i4 < 120; i4++) {
               worldIn.spawnParticle(
                  EnumParticleTypes.SNOWBALL,
                  pos2.getX() + worldIn.rand.nextDouble(),
                  pos2.getY() - 2 + worldIn.rand.nextDouble() * 3.9,
                  pos2.getZ() + worldIn.rand.nextDouble(),
                  0.0,
                  0.0,
                  0.0
               );
            }

            for (int i5 = 0; i5 < witherPattern2.getPalmLength(); i5++) {
               for (int i6 = 0; i6 < witherPattern2.getThumbLength(); i6++) {
                  BlockWorldState blockWorldState3 = patternHelper.translateOffset(i5, i6, 0);
                  worldIn.notifyNeighborsRespectDebug(blockWorldState3.getPos(), Blocks.air);
               }
            }
         }
      }
   }

   protected BlockPattern getWitherPattern() {
      if (this.witherPattern == null) {
         this.witherPattern = FactoryBlockPattern.start()
            .aisle("^^^", "###", "~#~")
            .where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.soul_sand)))
            .where('^', IS_WITHER_SKELETON)
            .where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air)))
            .build();
      }

      return this.witherPattern;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public int getDamageValue(World worldIn, BlockPos pos) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      return tileEntity instanceof TileEntitySkull ? ((TileEntitySkull)tileEntity).getSkullType() : super.getDamageValue(worldIn, pos);
   }

   protected BlockSkull() {
      super(Material.circuits);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(NODROP, false));
      this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
   }

   @Override
   public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      if (player.capabilities.isCreativeMode) {
         state = state.withProperty(NODROP, true);
         worldIn.setBlockState(pos, state, 4);
      }

      super.onBlockHarvested(worldIn, pos, state, player);
   }

   public boolean canDispenserPlace(World worldIn, BlockPos pos, ItemStack stack) {
      return stack.getMetadata() == 1 && pos.getY() >= 2 && worldIn.getDifficulty() != EnumDifficulty.PEACEFUL && !worldIn.isRemote
         ? this.getWitherBasePattern().match(worldIn, pos) != null
         : false;
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, FACING, NODROP);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7)).withProperty(NODROP, (meta & 8) > 0);
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      if (!worldIn.isRemote) {
         if (!state.getValue(NODROP) && worldIn.getTileEntity(pos) instanceof TileEntitySkull tileEntitySkull) {
            ItemStack itemStack = new ItemStack(Items.skull, 1, this.getDamageValue(worldIn, pos));
            if (tileEntitySkull.getSkullType() == 3 && tileEntitySkull.getPlayerProfile() != null) {
               itemStack.setTagCompound(new NBTTagCompound());
               NBTTagCompound nbtTagCompound = new NBTTagCompound();
               NBTUtil.writeGameProfile(nbtTagCompound, tileEntitySkull.getPlayerProfile());
               itemStack.getTagCompound().setTag("SkullOwner", nbtTagCompound);
            }

            spawnAsEntity(worldIn, pos, itemStack);
         }

         super.breakBlock(worldIn, pos, state);
      }
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      switch ((EnumFacing)worldIn.getBlockState(pos).getValue(FACING)) {
         case UP:
         default:
            this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
            break;
         case NORTH:
            this.setBlockBounds(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F);
            break;
         case SOUTH:
            this.setBlockBounds(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F);
            break;
         case WEST:
            this.setBlockBounds(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
            break;
         case EAST:
            this.setBlockBounds(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F);
      }
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.getCollisionBoundingBox(worldIn, pos, state);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   protected BlockPattern getWitherBasePattern() {
      if (this.witherBasePattern == null) {
         this.witherBasePattern = FactoryBlockPattern.start()
            .aisle("   ", "###", "~#~")
            .where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.soul_sand)))
            .where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air)))
            .build();
      }

      return this.witherBasePattern;
   }

   @Override
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntitySkull();
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(NODROP, false);
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return Items.skull;
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.skull;
   }
}
