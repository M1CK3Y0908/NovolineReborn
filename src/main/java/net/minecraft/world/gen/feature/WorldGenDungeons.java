package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenDungeons extends WorldGenerator {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final String[] SPAWNERTYPES = new String[]{"Skeleton", "Zombie", "Zombie", "Spider"};
   private static final List<WeightedRandomChestContent> CHESTCONTENT = Lists.newArrayList(
      new WeightedRandomChestContent[]{
         new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 10),
         new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 4, 10),
         new WeightedRandomChestContent(Items.bread, 0, 1, 1, 10),
         new WeightedRandomChestContent(Items.wheat, 0, 1, 4, 10),
         new WeightedRandomChestContent(Items.gunpowder, 0, 1, 4, 10),
         new WeightedRandomChestContent(Items.string, 0, 1, 4, 10),
         new WeightedRandomChestContent(Items.bucket, 0, 1, 1, 10),
         new WeightedRandomChestContent(Items.golden_apple, 0, 1, 1, 1),
         new WeightedRandomChestContent(Items.redstone, 0, 1, 4, 10),
         new WeightedRandomChestContent(Items.record_13, 0, 1, 1, 4),
         new WeightedRandomChestContent(Items.record_cat, 0, 1, 1, 4),
         new WeightedRandomChestContent(Items.name_tag, 0, 1, 1, 10),
         new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 2),
         new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 5),
         new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1)
      }
   );

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int i = rand.nextInt(2) + 2;
      int i2 = -i - 1;
      int i3 = i + 1;
      int i4 = rand.nextInt(2) + 2;
      int i5 = -i4 - 1;
      int i6 = i4 + 1;
      int i7 = 0;

      for (int i8 = i2; i8 <= i3; i8++) {
         for (int i9 = -1; i9 <= 4; i9++) {
            for (int i10 = i5; i10 <= i6; i10++) {
               BlockPos blockPos = position.add(i8, i9, i10);
               Material material2 = worldIn.getBlockState(blockPos).getBlock().getMaterial();
               boolean solid = material2.isSolid();
               if (i9 == -1 && !solid) {
                  return false;
               }

               if (i9 == 4 && !solid) {
                  return false;
               }

               if ((i8 == i2 || i8 == i3 || i10 == i5 || i10 == i6)
                  && i9 == 0
                  && worldIn.isAirBlock(blockPos)
                  && worldIn.isAirBlock(blockPos.up())) {
                  i7++;
               }
            }
         }
      }

      if (i7 >= 1 && i7 <= 5) {
         for (int i11 = i2; i11 <= i3; i11++) {
            for (int i12 = 3; i12 >= -1; i12--) {
               for (int i13 = i5; i13 <= i6; i13++) {
                  BlockPos blockPos2 = position.add(i11, i12, i13);
                  if (i11 != i2 && i12 != -1 && i13 != i5 && i11 != i3 && i12 != 4 && i13 != i6) {
                     if (worldIn.getBlockState(blockPos2).getBlock() != Blocks.chest) {
                        worldIn.setBlockToAir(blockPos2);
                     }
                  } else if (blockPos2.getY() >= 0 && !worldIn.getBlockState(blockPos2.down()).getBlock().getMaterial().isSolid()) {
                     worldIn.setBlockToAir(blockPos2);
                  } else if (worldIn.getBlockState(blockPos2).getBlock().getMaterial().isSolid() && worldIn.getBlockState(blockPos2).getBlock() != Blocks.chest) {
                     if (i12 == -1 && rand.nextInt(4) != 0) {
                        worldIn.setBlockState(blockPos2, Blocks.mossy_cobblestone.getDefaultState(), 2);
                     } else {
                        worldIn.setBlockState(blockPos2, Blocks.cobblestone.getDefaultState(), 2);
                     }
                  }
               }
            }
         }

         for (int i14 = 0; i14 < 2; i14++) {
            for (int i15 = 0; i15 < 3; i15++) {
               int i16 = position.getX() + rand.nextInt(i * 2 + 1) - i;
               int y = position.getY();
               int i17 = position.getZ() + rand.nextInt(i4 * 2 + 1) - i4;
               BlockPos blockPos3 = new BlockPos(i16, y, i17);
               if (worldIn.isAirBlock(blockPos3)) {
                  int i18 = 0;

                  for (EnumFacing enumFacing : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
                     if (worldIn.getBlockState(blockPos3.offset(enumFacing)).getBlock().getMaterial().isSolid()) {
                        i18++;
                     }
                  }

                  if (i18 == 1) {
                     worldIn.setBlockState(blockPos3, Blocks.chest.correctFacing(worldIn, blockPos3, Blocks.chest.getDefaultState()), 2);
                     List list = WeightedRandomChestContent.copyWithAddition(CHESTCONTENT, Items.enchanted_book.getRandom(rand));
                     TileEntity tileEntity = worldIn.getTileEntity(blockPos3);
                     if (tileEntity instanceof TileEntityChest) {
                        WeightedRandomChestContent.generateChestContents(rand, list, (TileEntityChest)tileEntity, 8);
                     }
                     break;
                  }
               }
            }
         }

         worldIn.setBlockState(position, Blocks.mob_spawner.getDefaultState(), 2);
         TileEntity tileEntity2 = worldIn.getTileEntity(position);
         if (tileEntity2 instanceof TileEntityMobSpawner) {
            ((TileEntityMobSpawner)tileEntity2).getSpawnerBaseLogic().setEntityName(this.pickMobSpawner(rand));
         } else {
            LOGGER.error("Failed to fetch mob spawner entity at (" + position.getX() + ", " + position.getY() + ", " + position.getZ() + ")");
         }

         return true;
      } else {
         return false;
      }
   }

   private String pickMobSpawner(Random rand) {
      return SPAWNERTYPES[rand.nextInt(SPAWNERTYPES.length)];
   }
}
