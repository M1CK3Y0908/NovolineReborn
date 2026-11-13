package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class BlockBrewingStand extends BlockContainer {
   public static final PropertyBool[] HAS_BOTTLE = new PropertyBool[]{
      PropertyBool.create("has_bottle_0"), PropertyBool.create("has_bottle_1"), PropertyBool.create("has_bottle_2")
   };

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      double xCoordinate = pos.getX() + 0.4F + rand.nextFloat() * 0.2F;
      double yCoordinate = pos.getY() + 0.7F + rand.nextFloat() * 0.3F;
      double zCoordinate = pos.getZ() + 0.4F + rand.nextFloat() * 0.2F;
      worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, xCoordinate, yCoordinate, zCoordinate, 0.0, 0.0, 0.0);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int i = 0;

      for (int innerIndex = 0; innerIndex < 3; innerIndex++) {
         if (state.getValue(HAS_BOTTLE[innerIndex])) {
            i |= 1 << innerIndex;
         }
      }

      return i;
   }

   @Override
   public int getRenderType() {
      return 3;
   }

   @Override
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      if (stack.hasDisplayName()) {
         TileEntity tileEntity = worldIn.getTileEntity(pos);
         if (tileEntity instanceof TileEntityBrewingStand) {
            ((TileEntityBrewingStand)tileEntity).setName(stack.getDisplayName());
         }
      }
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      }

      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof TileEntityBrewingStand) {
         playerIn.displayGUIChest((TileEntityBrewingStand)tileEntity);
         playerIn.triggerAchievement(StatList.brewingStandInteractionStat);
      }

      return true;
   }

   @Override
   public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
      this.setBlockBounds(0.4375F, 0.0F, 0.4375F, 0.5625F, 0.875F, 0.5625F);
      super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
      this.setBlockBoundsForItemRender();
      super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
   }

   @Override
   public int getComparatorInputOverride(World worldIn, BlockPos pos) {
      return Container.calcRedstone(worldIn.getTileEntity(pos));
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal("item.brewingStand.name");
   }

   public BlockBrewingStand() {
      super(Material.iron);
      this.setDefaultState(
         this.blockState.getBaseState().withProperty(HAS_BOTTLE[0], false).withProperty(HAS_BOTTLE[1], false).withProperty(HAS_BOTTLE[2], false)
      );
   }

   @Override
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityBrewingStand();
   }

   @Override
   public boolean hasComparatorInputOverride() {
      return true;
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, HAS_BOTTLE[0], HAS_BOTTLE[1], HAS_BOTTLE[2]);
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.brewing_stand;
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return Items.brewing_stand;
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof TileEntityBrewingStand) {
         InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityBrewingStand)tileEntity);
      }

      super.breakBlock(worldIn, pos, state);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      IBlockState blockState = this.getDefaultState();

      for (int index = 0; index < 3; index++) {
         blockState = blockState.withProperty(HAS_BOTTLE[index], (meta & 1 << index) > 0);
      }

      return blockState;
   }

   @Override
   public void setBlockBoundsForItemRender() {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
   }
}
