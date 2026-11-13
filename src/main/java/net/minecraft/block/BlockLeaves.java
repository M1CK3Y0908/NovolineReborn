package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

public abstract class BlockLeaves extends BlockLeavesBase {
   public static final PropertyBool DECAYABLE = PropertyBool.create("decayable");
   public static final PropertyBool CHECK_DECAY = PropertyBool.create("check_decay");
   int[] surroundings;
   protected boolean isTransparent;
   protected int iconIndex;

   @Override
   public int quantityDropped(Random random) {
      return random.nextInt(20) == 0 ? 1 : 0;
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote && state.getValue(CHECK_DECAY) && state.getValue(DECAYABLE)) {
         int x = pos.getX();
         int y = pos.getY();
         int z = pos.getZ();
         if (this.surroundings == null) {
            this.surroundings = new int[32768];
         }

         if (worldIn.isAreaLoaded(new BlockPos(x - 5, y - 5, z - 5), new BlockPos(x + 5, y + 5, z + 5))) {
            net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

            for (int i = -4; i <= 4; i++) {
               for (int i2 = -4; i2 <= 4; i2++) {
                  for (int i3 = -4; i3 <= 4; i3++) {
                     Block block2 = worldIn.getBlockState(mutableBlockPos.set(x + i, y + i2, z + i3)).getBlock();
                     if (block2 != Blocks.log && block2 != Blocks.log2) {
                        if (block2.getMaterial() == Material.leaves) {
                           this.surroundings[(i + 16) * 1024 + (i2 + 16) * 32 + i3 + 16] = -2;
                        } else {
                           this.surroundings[(i + 16) * 1024 + (i2 + 16) * 32 + i3 + 16] = -1;
                        }
                     } else {
                        this.surroundings[(i + 16) * 1024 + (i2 + 16) * 32 + i3 + 16] = 0;
                     }
                  }
               }
            }

            for (int i4 = 1; i4 <= 4; i4++) {
               for (int i5 = -4; i5 <= 4; i5++) {
                  for (int i6 = -4; i6 <= 4; i6++) {
                     for (int i7 = -4; i7 <= 4; i7++) {
                        if (this.surroundings[(i5 + 16) * 1024 + (i6 + 16) * 32 + i7 + 16] == i4 - 1) {
                           if (this.surroundings[(i5 + 16 - 1) * 1024 + (i6 + 16) * 32 + i7 + 16] == -2) {
                              this.surroundings[(i5 + 16 - 1) * 1024 + (i6 + 16) * 32 + i7 + 16] = i4;
                           }

                           if (this.surroundings[(i5 + 16 + 1) * 1024 + (i6 + 16) * 32 + i7 + 16] == -2) {
                              this.surroundings[(i5 + 16 + 1) * 1024 + (i6 + 16) * 32 + i7 + 16] = i4;
                           }

                           if (this.surroundings[(i5 + 16) * 1024 + (i6 + 16 - 1) * 32 + i7 + 16] == -2) {
                              this.surroundings[(i5 + 16) * 1024 + (i6 + 16 - 1) * 32 + i7 + 16] = i4;
                           }

                           if (this.surroundings[(i5 + 16) * 1024 + (i6 + 16 + 1) * 32 + i7 + 16] == -2) {
                              this.surroundings[(i5 + 16) * 1024 + (i6 + 16 + 1) * 32 + i7 + 16] = i4;
                           }

                           if (this.surroundings[(i5 + 16) * 1024 + (i6 + 16) * 32 + (i7 + 16 - 1)] == -2) {
                              this.surroundings[(i5 + 16) * 1024 + (i6 + 16) * 32 + (i7 + 16 - 1)] = i4;
                           }

                           if (this.surroundings[(i5 + 16) * 1024 + (i6 + 16) * 32 + i7 + 16 + 1] == -2) {
                              this.surroundings[(i5 + 16) * 1024 + (i6 + 16) * 32 + i7 + 16 + 1] = i4;
                           }
                        }
                     }
                  }
               }
            }
         }

         int i8 = this.surroundings[16912];
         if (i8 >= 0) {
            worldIn.setBlockState(pos, state.withProperty(CHECK_DECAY, false), 4);
         } else {
            this.destroy(worldIn, pos);
         }
      }
   }

   public void setGraphicsLevel(boolean fancy) {
      this.isTransparent = fancy;
      this.fancyGraphics = fancy;
      this.iconIndex = fancy ? 0 : 1;
   }

   private void destroy(World worldIn, BlockPos pos) {
      this.dropBlockAsItem(worldIn, pos, worldIn.getBlockState(pos), 0);
      worldIn.setBlockToAir(pos);
   }

   public abstract BlockPlanks.EnumType getWoodType(int i);

   public BlockLeaves() {
      super(Material.leaves);
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabDecorations);
      this.setHardness(0.2F);
      this.setLightOpacity(1);
      this.setStepSound(soundTypeGrass);
   }

   @Override
   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
      if (!worldIn.isRemote) {
         int saplingDropChance = this.getSaplingDropChance(state);
         if (fortune > 0) {
            saplingDropChance -= 2 << fortune;
            if (saplingDropChance < 10) {
               saplingDropChance = 10;
            }
         }

         if (worldIn.rand.nextInt(saplingDropChance) == 0) {
            Item itemDropped = this.getItemDropped(state, worldIn.rand, fortune);
            spawnAsEntity(worldIn, pos, new ItemStack(itemDropped, 1, this.damageDropped(state)));
         }

         saplingDropChance = 200;
         if (fortune > 0) {
            saplingDropChance = 200 - (10 << fortune);
            if (saplingDropChance < 40) {
               saplingDropChance = 40;
            }
         }

         this.dropApple(worldIn, pos, state, saplingDropChance);
      }
   }

   @Override
   public int getRenderColor(IBlockState state) {
      return ColorizerFoliage.getFoliageColorBasic();
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      if (worldIn.isAreaLoaded(new BlockPos(x - 2, y - 2, z - 2), new BlockPos(x + 2, y + 2, z + 2))) {
         for (int i = -1; i <= 1; i++) {
            for (int i2 = -1; i2 <= 1; i2++) {
               for (int i3 = -1; i3 <= 1; i3++) {
                  BlockPos blockPos = pos.add(i, i2, i3);
                  IBlockState blockState = worldIn.getBlockState(blockPos);
                  if (blockState.getBlock().getMaterial() == Material.leaves && !blockState.getValue(CHECK_DECAY)) {
                     worldIn.setBlockState(blockPos, blockState.withProperty(CHECK_DECAY, true), 4);
                  }
               }
            }
         }
      }
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return this.isTransparent ? EnumWorldBlockLayer.CUTOUT_MIPPED : EnumWorldBlockLayer.SOLID;
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Item.getItemFromBlock(Blocks.sapling);
   }

   protected int getSaplingDropChance(IBlockState state) {
      return 20;
   }

   @Override
   public boolean isVisuallyOpaque() {
      return false;
   }

   @Override
   public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
      return BiomeColorHelper.getFoliageColorAtPos(worldIn, pos);
   }

   protected void dropApple(World worldIn, BlockPos pos, IBlockState state, int chance) {
   }

   @Override
   public boolean isOpaqueCube() {
      return !this.fancyGraphics;
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (worldIn.isRainingAt(pos.up()) && !World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && rand.nextInt(15) == 1) {
         double xCoordinate = pos.getX() + rand.nextFloat();
         double yCoordinate = pos.getY() - 0.05;
         double zCoordinate = pos.getZ() + rand.nextFloat();
         worldIn.spawnParticle(EnumParticleTypes.DRIP_WATER, xCoordinate, yCoordinate, zCoordinate, 0.0, 0.0, 0.0);
      }
   }
}
