package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

public class BlockAnvil extends BlockFalling {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", net.minecraft.util.EnumFacing.Plane.HORIZONTAL);
   public static final PropertyInteger DAMAGE = PropertyInteger.create("damage", 0, 2);

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, FACING, DAMAGE);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 3)).withProperty(DAMAGE, (meta & 15) >> 2);
   }

   @Override
   public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
      list.add(new ItemStack(itemIn, 1, 0));
      list.add(new ItemStack(itemIn, 1, 1));
      list.add(new ItemStack(itemIn, 1, 2));
   }

   @Override
   public IBlockState getStateForEntityRender(IBlockState state) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!worldIn.isRemote) {
         playerIn.displayGui(new BlockAnvil.Anvil(worldIn, pos));
      }

      return true;
   }

   @Override
   protected void onStartFalling(EntityFallingBlock fallingEntity) {
      fallingEntity.update();
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      EnumFacing facing2 = placer.getHorizontalFacing().rotateY();
      return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(FACING, facing2).withProperty(DAMAGE, meta >> 2);
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      return true;
   }

   protected BlockAnvil() {
      super(Material.anvil);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(DAMAGE, 0));
      this.setLightOpacity(0);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public int damageDropped(IBlockState state) {
      return state.getValue(DAMAGE);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      if (worldIn.getBlockState(pos).getValue(FACING).getAxis() == net.minecraft.util.EnumFacing.Axis.X) {
         this.setBlockBounds(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F);
      } else {
         this.setBlockBounds(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
      }
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return 0 | state.getValue(FACING).getHorizontalIndex() | state.getValue(DAMAGE) << 2;
   }

   @Override
   public void onEndFalling(World worldIn, BlockPos pos) {
      worldIn.playAuxSFX(1022, pos, 0);
   }

   public static class Anvil implements IInteractionObject {
      private final World world;
      private final BlockPos position;

      @Override
      public boolean hasCustomName() {
         return false;
      }

      @Override
      public String getGuiID() {
         return "minecraft:anvil";
      }

      @Override
      public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
         return new ContainerRepair(playerInventory, this.world, this.position, playerIn);
      }

      @Override
      public String getName() {
         return "anvil";
      }

      @Override
      public IChatComponent getDisplayName() {
         return new ChatComponentTranslation(Blocks.anvil.getUnlocalizedName() + ".name");
      }

      public Anvil(World worldIn, BlockPos pos) {
         this.world = worldIn;
         this.position = pos;
      }
   }
}
