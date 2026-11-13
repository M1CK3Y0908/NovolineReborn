package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityComparator;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneComparator extends BlockRedstoneDiode implements ITileEntityProvider {
   public static final PropertyBool POWERED = PropertyBool.create("powered");
   public static final PropertyEnum<BlockRedstoneComparator.Mode> MODE = PropertyEnum.create("mode", BlockRedstoneComparator.Mode.class);

   @Override
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityComparator();
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int i = 0 | state.getValue(FACING).getHorizontalIndex();
      if (state.getValue(POWERED)) {
         i |= 8;
      }

      if (state.getValue(MODE) == BlockRedstoneComparator.Mode.SUBTRACT) {
         i |= 4;
      }

      return i;
   }

   @Override
   protected boolean isPowered(IBlockState state) {
      return this.isRepeaterPowered || state.getValue(POWERED);
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal("item.comparator.name");
   }

   @Override
   protected int calculateInputStrength(World worldIn, BlockPos pos, IBlockState state) {
      int i = super.calculateInputStrength(worldIn, pos, state);
      EnumFacing value = state.getValue(FACING);
      BlockPos blockPos = pos.offset(value);
      Block block2 = worldIn.getBlockState(blockPos).getBlock();
      if (block2.hasComparatorInputOverride()) {
         i = block2.getComparatorInputOverride(worldIn, blockPos);
      } else if (i < 15 && block2.isNormalCube()) {
         blockPos = blockPos.offset(value);
         block2 = worldIn.getBlockState(blockPos).getBlock();
         if (block2.hasComparatorInputOverride()) {
            i = block2.getComparatorInputOverride(worldIn, blockPos);
         } else if (block2.getMaterial() == Material.air) {
            EntityItemFrame itemFrame = this.findItemFrame(worldIn, value, blockPos);
            if (itemFrame != null) {
               i = itemFrame.getAnalogOutput();
            }
         }
      }

      return i;
   }

   @Override
   protected boolean shouldBePowered(World worldIn, BlockPos pos, IBlockState state) {
      int i = this.calculateInputStrength(worldIn, pos, state);
      if (i >= 15) {
         return true;
      }

      if (i == 0) {
         return false;
      }

      int powerOnSides = this.getPowerOnSides(worldIn, pos, state);
      return powerOnSides == 0 ? true : i >= powerOnSides;
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!playerIn.capabilities.allowEdit) {
         return false;
      }

      state = state.cycleProperty(MODE);
      double d = pos.getX() + 0.5;
      double d2 = pos.getY() + 0.5;
      double d3 = pos.getZ() + 0.5;
      float f = state.getValue(MODE) == BlockRedstoneComparator.Mode.SUBTRACT ? 0.55F : 0.5F;
      float f2 = 0.3F;
      worldIn.playSoundEffect(d, d2, d3, "random.click", f2, f);
      worldIn.setBlockState(pos, state, 2);
      this.onStateChange(worldIn, pos, state);
      return true;
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      super.onBlockAdded(worldIn, pos, state);
      worldIn.setTileEntity(pos, this.createNewTileEntity(worldIn, 0));
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, FACING, MODE, POWERED);
   }

   private int calculateOutput(World worldIn, BlockPos pos, IBlockState state) {
      return state.getValue(MODE) == BlockRedstoneComparator.Mode.SUBTRACT
         ? Math.max(this.calculateInputStrength(worldIn, pos, state) - this.getPowerOnSides(worldIn, pos, state), 0)
         : this.calculateInputStrength(worldIn, pos, state);
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      super.breakBlock(worldIn, pos, state);
      worldIn.removeTileEntity(pos);
      this.notifyNeighbors(worldIn, pos, state);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState()
         .withProperty(FACING, EnumFacing.getHorizontal(meta))
         .withProperty(POWERED, (meta & 8) > 0)
         .withProperty(MODE, (meta & 4) > 0 ? BlockRedstoneComparator.Mode.SUBTRACT : BlockRedstoneComparator.Mode.COMPARE);
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState()
         .withProperty(FACING, placer.getHorizontalFacing().getOpposite())
         .withProperty(POWERED, false)
         .withProperty(MODE, BlockRedstoneComparator.Mode.COMPARE);
   }

   @Override
   public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam) {
      super.onBlockEventReceived(worldIn, pos, state, eventID, eventParam);
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      return tileEntity == null ? false : tileEntity.receiveClientEvent(eventID, eventParam);
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.comparator;
   }

   @Override
   protected IBlockState getPoweredState(IBlockState unpoweredState) {
      Boolean obool = unpoweredState.getValue(POWERED);
      BlockRedstoneComparator.Mode blockredstonecomparator$mode = unpoweredState.getValue(MODE);
      EnumFacing facing = unpoweredState.getValue(FACING);
      return Blocks.powered_comparator
         .getDefaultState()
         .withProperty(FACING, facing)
         .withProperty(POWERED, obool)
         .withProperty(MODE, blockredstonecomparator$mode);
   }

   @Override
   protected void updateState(World worldIn, BlockPos pos, IBlockState state) {
      if (!worldIn.isBlockTickPending(pos, this)) {
         int i = this.calculateOutput(worldIn, pos, state);
         TileEntity tileEntity = worldIn.getTileEntity(pos);
         int i2 = tileEntity instanceof TileEntityComparator ? ((TileEntityComparator)tileEntity).getOutputSignal() : 0;
         if (i != i2 || this.isPowered(state) != this.shouldBePowered(worldIn, pos, state)) {
            if (this.isFacingTowardsRepeater(worldIn, pos, state)) {
               worldIn.updateBlockTick(pos, this, 2, -1);
            } else {
               worldIn.updateBlockTick(pos, this, 2, 0);
            }
         }
      }
   }

   @Override
   protected int getDelay(IBlockState state) {
      return 2;
   }

   @Override
   protected IBlockState getUnpoweredState(IBlockState poweredState) {
      Boolean obool = poweredState.getValue(POWERED);
      BlockRedstoneComparator.Mode blockredstonecomparator$mode = poweredState.getValue(MODE);
      EnumFacing facing = poweredState.getValue(FACING);
      return Blocks.unpowered_comparator
         .getDefaultState()
         .withProperty(FACING, facing)
         .withProperty(POWERED, obool)
         .withProperty(MODE, blockredstonecomparator$mode);
   }

   private EntityItemFrame findItemFrame(World worldIn, final EnumFacing facing, BlockPos pos) {
      List<EntityItemFrame> list = worldIn.getEntitiesWithinAABB(
         EntityItemFrame.class,
         new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1),
         new Predicate<Entity>() {
            public boolean apply(Entity arg1) {
               return arg1 != null && arg1.getHorizontalFacing() == facing;
            }
         }
      );
      return list.size() == 1 ? list.get(0) : null;
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return Items.comparator;
   }

   public BlockRedstoneComparator(boolean powered) {
      super(powered);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(POWERED, false)
            .withProperty(MODE, BlockRedstoneComparator.Mode.COMPARE)
      );
      this.isBlockContainer = true;
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (this.isRepeaterPowered) {
         worldIn.setBlockState(pos, this.getUnpoweredState(state).withProperty(POWERED, true), 4);
      }

      this.onStateChange(worldIn, pos, state);
   }

   @Override
   protected int getActiveSignal(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      return tileEntity instanceof TileEntityComparator ? ((TileEntityComparator)tileEntity).getOutputSignal() : 0;
   }

   private void onStateChange(World worldIn, BlockPos pos, IBlockState state) {
      int i = this.calculateOutput(worldIn, pos, state);
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      int i2 = 0;
      if (tileEntity instanceof TileEntityComparator tileEntityComparator) {
         i2 = tileEntityComparator.getOutputSignal();
         tileEntityComparator.setOutputSignal(i);
      }

      if (i2 != i || state.getValue(MODE) == BlockRedstoneComparator.Mode.COMPARE) {
         boolean shouldBePowered = this.shouldBePowered(worldIn, pos, state);
         boolean isPowered = this.isPowered(state);
         if (isPowered && !shouldBePowered) {
            worldIn.setBlockState(pos, state.withProperty(POWERED, false), 2);
         } else if (!isPowered && shouldBePowered) {
            worldIn.setBlockState(pos, state.withProperty(POWERED, true), 2);
         }

         this.notifyNeighbors(worldIn, pos, state);
      }
   }

   public enum Mode implements IStringSerializable {
      COMPARE("compare"),
      SUBTRACT("subtract");

      private final String name;

      @Override
      public String getName() {
         return this.name;
      }

      Mode(String text) {
         this.name = text;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
