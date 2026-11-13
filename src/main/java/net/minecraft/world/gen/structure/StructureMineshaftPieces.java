package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class StructureMineshaftPieces {
   private static final List<WeightedRandomChestContent> CHEST_CONTENT_WEIGHT_LIST = Lists.newArrayList(
      new WeightedRandomChestContent[]{
         new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10),
         new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 5),
         new WeightedRandomChestContent(Items.redstone, 0, 4, 9, 5),
         new WeightedRandomChestContent(Items.dye, EnumDyeColor.BLUE.getDyeDamage(), 4, 9, 5),
         new WeightedRandomChestContent(Items.diamond, 0, 1, 2, 3),
         new WeightedRandomChestContent(Items.coal, 0, 3, 8, 10),
         new WeightedRandomChestContent(Items.bread, 0, 1, 3, 15),
         new WeightedRandomChestContent(Items.iron_pickaxe, 0, 1, 1, 1),
         new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.rail), 0, 4, 8, 1),
         new WeightedRandomChestContent(Items.melon_seeds, 0, 2, 4, 10),
         new WeightedRandomChestContent(Items.pumpkin_seeds, 0, 2, 4, 10),
         new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 3),
         new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1)
      }
   );

   private static StructureComponent generateAndAddPiece(
      StructureComponent componentIn, List<StructureComponent> listIn, Random rand, int x, int y, int z, EnumFacing facing, int type
   ) {
      if (type > 8) {
         return null;
      }

      if (Math.abs(x - componentIn.getBoundingBox().minX) <= 80 && Math.abs(z - componentIn.getBoundingBox().minZ) <= 80) {
         StructureComponent structureComponent = createRandomShaftPiece(listIn, rand, x, y, z, facing, type + 1);
         if (structureComponent != null) {
            listIn.add(structureComponent);
            structureComponent.buildComponent(componentIn, listIn, rand);
         }

         return structureComponent;
      } else {
         return null;
      }
   }

   private static StructureComponent createRandomShaftPiece(List<StructureComponent> listIn, Random rand, int x, int y, int z, EnumFacing facing, int type) {
      int i = rand.nextInt(100);
      if (i >= 80) {
         StructureBoundingBox structureBoundingBox = StructureMineshaftPieces.Cross.findCrossingBoundingBox(listIn, rand, x, y, z, facing);
         if (structureBoundingBox != null) {
            return new StructureMineshaftPieces.Cross(type, rand, structureBoundingBox, facing);
         }
      } else if (i >= 70) {
         StructureBoundingBox structureBoundingBox2 = StructureMineshaftPieces.Stairs.findStairsBoundingBox(listIn, rand, x, y, z, facing);
         if (structureBoundingBox2 != null) {
            return new StructureMineshaftPieces.Stairs(type, rand, structureBoundingBox2, facing);
         }
      } else {
         StructureBoundingBox structureBoundingBox3 = StructureMineshaftPieces.Corridor.findCorridorBoundingBox(listIn, rand, x, y, z, facing);
         if (structureBoundingBox3 != null) {
            return new StructureMineshaftPieces.Corridor(type, rand, structureBoundingBox3, facing);
         }
      }

      return null;
   }

   public static void registerStructurePieces() {
      MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Corridor.class, "MSCorridor");
      MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Cross.class, "MSCrossing");
      MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Room.class, "MSRoom");
      MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Stairs.class, "MSStairs");
   }

   public static class Corridor extends StructureComponent {
      private boolean hasRails;
      private boolean spawnerPlaced;
      private boolean hasSpiders;
      private int sectionCount;

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         int componentType = this.getComponentType();
         int i = rand.nextInt(4);
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case NORTH:
                  if (i <= 1) {
                     StructureMineshaftPieces.generateAndAddPiece(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.minX,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.minZ - 1,
                        this.coordBaseMode,
                        componentType
                     );
                  } else if (i == 2) {
                     StructureMineshaftPieces.generateAndAddPiece(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.minX - 1,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.minZ,
                        EnumFacing.WEST,
                        componentType
                     );
                  } else {
                     StructureMineshaftPieces.generateAndAddPiece(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.maxX + 1,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.minZ,
                        EnumFacing.EAST,
                        componentType
                     );
                  }
                  break;
               case SOUTH:
                  if (i <= 1) {
                     StructureMineshaftPieces.generateAndAddPiece(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.minX,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.maxZ + 1,
                        this.coordBaseMode,
                        componentType
                     );
                  } else if (i == 2) {
                     StructureMineshaftPieces.generateAndAddPiece(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.minX - 1,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.maxZ - 3,
                        EnumFacing.WEST,
                        componentType
                     );
                  } else {
                     StructureMineshaftPieces.generateAndAddPiece(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.maxX + 1,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.maxZ - 3,
                        EnumFacing.EAST,
                        componentType
                     );
                  }
                  break;
               case WEST:
                  if (i <= 1) {
                     StructureMineshaftPieces.generateAndAddPiece(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.minX - 1,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.minZ,
                        this.coordBaseMode,
                        componentType
                     );
                  } else if (i == 2) {
                     StructureMineshaftPieces.generateAndAddPiece(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.minX,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.minZ - 1,
                        EnumFacing.NORTH,
                        componentType
                     );
                  } else {
                     StructureMineshaftPieces.generateAndAddPiece(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.minX,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.maxZ + 1,
                        EnumFacing.SOUTH,
                        componentType
                     );
                  }
                  break;
               case EAST:
                  if (i <= 1) {
                     StructureMineshaftPieces.generateAndAddPiece(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.maxX + 1,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.minZ,
                        this.coordBaseMode,
                        componentType
                     );
                  } else if (i == 2) {
                     StructureMineshaftPieces.generateAndAddPiece(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.maxX - 3,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.minZ - 1,
                        EnumFacing.NORTH,
                        componentType
                     );
                  } else {
                     StructureMineshaftPieces.generateAndAddPiece(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.maxX - 3,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.maxZ + 1,
                        EnumFacing.SOUTH,
                        componentType
                     );
                  }
            }
         }

         if (componentType < 8) {
            if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.SOUTH) {
               for (int index = this.boundingBox.minX + 3; index + 3 <= this.boundingBox.maxX; index += 5) {
                  int i2 = rand.nextInt(5);
                  if (i2 == 0) {
                     StructureMineshaftPieces.generateAndAddPiece(
                        componentIn, listIn, rand, index, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, componentType + 1
                     );
                  } else if (i2 == 1) {
                     StructureMineshaftPieces.generateAndAddPiece(
                        componentIn, listIn, rand, index, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, componentType + 1
                     );
                  }
               }
            } else {
               for (int nestedIndex = this.boundingBox.minZ + 3; nestedIndex + 3 <= this.boundingBox.maxZ; nestedIndex += 5) {
                  int i3 = rand.nextInt(5);
                  if (i3 == 0) {
                     StructureMineshaftPieces.generateAndAddPiece(
                        componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, nestedIndex, EnumFacing.WEST, componentType + 1
                     );
                  } else if (i3 == 1) {
                     StructureMineshaftPieces.generateAndAddPiece(
                        componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, nestedIndex, EnumFacing.EAST, componentType + 1
                     );
                  }
               }
            }
         }
      }

      public Corridor(int type, Random rand, StructureBoundingBox structurebb, EnumFacing facing) {
         super(type);
         this.coordBaseMode = facing;
         this.boundingBox = structurebb;
         this.hasRails = rand.nextInt(3) == 0;
         this.hasSpiders = !this.hasRails && rand.nextInt(23) == 0;
         if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.SOUTH) {
            this.sectionCount = structurebb.getXSize() / 5;
         } else {
            this.sectionCount = structurebb.getZSize() / 5;
         }
      }

      @Override
      protected boolean generateChestContents(
         World worldIn, StructureBoundingBox boundingBoxIn, Random rand, int x, int y, int z, List<WeightedRandomChestContent> listIn, int max
      ) {
         BlockPos blockPos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(0), this.getZWithOffset(x, z));
         if (boundingBoxIn.isVecInside(blockPos) && worldIn.getBlockState(blockPos).getBlock().getMaterial() == Material.air) {
            int i = rand.nextBoolean() ? 1 : 0;
            worldIn.setBlockState(blockPos, Blocks.rail.getStateFromMeta(this.getMetadataWithOffset(Blocks.rail, i)), 2);
            EntityMinecartChest entityMinecartChest = new EntityMinecartChest(worldIn, blockPos.getX() + 0.5F, blockPos.getY() + 0.5F, blockPos.getZ() + 0.5F);
            WeightedRandomChestContent.generateChestContents(rand, listIn, entityMinecartChest, max);
            worldIn.spawnEntityInWorld(entityMinecartChest);
            return true;
         } else {
            return false;
         }
      }

      public static StructureBoundingBox findCorridorBoundingBox(List<StructureComponent> list, Random rand, int x, int y, int z, EnumFacing facing) {
         StructureBoundingBox structureBoundingBox4 = new StructureBoundingBox(x, y, z, x, y + 2, z);

         int i;
         for (i = rand.nextInt(3) + 2; i > 0; i--) {
            int i2 = i * 5;
            switch (facing) {
               case NORTH:
                  structureBoundingBox4.maxX = x + 2;
                  structureBoundingBox4.minZ = z - (i2 - 1);
                  break;
               case SOUTH:
                  structureBoundingBox4.maxX = x + 2;
                  structureBoundingBox4.maxZ = z + (i2 - 1);
                  break;
               case WEST:
                  structureBoundingBox4.minX = x - (i2 - 1);
                  structureBoundingBox4.maxZ = z + 2;
                  break;
               case EAST:
                  structureBoundingBox4.maxX = x + (i2 - 1);
                  structureBoundingBox4.maxZ = z + 2;
            }

            if (StructureComponent.findIntersecting(list, structureBoundingBox4) == null) {
               break;
            }
         }

         return i > 0 ? structureBoundingBox4 : null;
      }

      public Corridor() {
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         tagCompound.setBoolean("hr", this.hasRails);
         tagCompound.setBoolean("sc", this.hasSpiders);
         tagCompound.setBoolean("hps", this.spawnerPlaced);
         tagCompound.setInteger("Num", this.sectionCount);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         int i = this.sectionCount * 5 - 1;
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 2, 1, i, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.handleWorld3(worldIn, structureBoundingBoxIn, randomIn, 0.8F, 0, 2, 0, 2, 2, i, Blocks.air.getDefaultState(), Blocks.air.getDefaultState());
         if (this.hasSpiders) {
            this.handleWorld3(
               worldIn, structureBoundingBoxIn, randomIn, 0.6F, 0, 0, 0, 2, 1, i, Blocks.web.getDefaultState(), Blocks.air.getDefaultState()
            );
         }

         for (int i2 = 0; i2 < this.sectionCount; i2++) {
            int i3 = 2 + i2 * 5;
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 0, 0, i3, 0, 1, i3, Blocks.oak_fence.getDefaultState(), Blocks.air.getDefaultState(), false
            );
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 2, 0, i3, 2, 1, i3, Blocks.oak_fence.getDefaultState(), Blocks.air.getDefaultState(), false
            );
            if (randomIn.nextInt(4) == 0) {
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 0, 2, i3, 0, 2, i3, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false
               );
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 2, 2, i3, 2, 2, i3, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false
               );
            } else {
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 0, 2, i3, 2, 2, i3, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false
               );
            }

            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 0, 2, i3 - 1, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 2, 2, i3 - 1, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 0, 2, i3 + 1, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 2, 2, i3 + 1, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 0, 2, i3 - 2, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 2, 2, i3 - 2, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 0, 2, i3 + 2, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 2, 2, i3 + 2, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 1, 2, i3 - 1, Blocks.torch.getStateFromMeta(EnumFacing.UP.getIndex()));
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 1, 2, i3 + 1, Blocks.torch.getStateFromMeta(EnumFacing.UP.getIndex()));
            if (randomIn.nextInt(100) == 0) {
               this.generateChestContents(
                  worldIn,
                  structureBoundingBoxIn,
                  randomIn,
                  2,
                  0,
                  i3 - 1,
                  WeightedRandomChestContent.copyWithAddition(StructureMineshaftPieces.CHEST_CONTENT_WEIGHT_LIST, Items.enchanted_book.getRandom(randomIn)),
                  3 + randomIn.nextInt(4)
               );
            }

            if (randomIn.nextInt(100) == 0) {
               this.generateChestContents(
                  worldIn,
                  structureBoundingBoxIn,
                  randomIn,
                  0,
                  0,
                  i3 + 1,
                  WeightedRandomChestContent.copyWithAddition(StructureMineshaftPieces.CHEST_CONTENT_WEIGHT_LIST, Items.enchanted_book.getRandom(randomIn)),
                  3 + randomIn.nextInt(4)
               );
            }

            if (this.hasSpiders && !this.spawnerPlaced) {
               int yWithOffset = this.getYWithOffset(0);
               int i4 = i3 - 1 + randomIn.nextInt(3);
               int xWithOffset = this.getXWithOffset(1, i4);
               i4 = this.getZWithOffset(1, i4);
               BlockPos blockPos = new BlockPos(xWithOffset, yWithOffset, i4);
               if (structureBoundingBoxIn.isVecInside(blockPos)) {
                  this.spawnerPlaced = true;
                  worldIn.setBlockState(blockPos, Blocks.mob_spawner.getDefaultState(), 2);
                  TileEntity tileEntity = worldIn.getTileEntity(blockPos);
                  if (tileEntity instanceof TileEntityMobSpawner) {
                     ((TileEntityMobSpawner)tileEntity).getSpawnerBaseLogic().setEntityName("CaveSpider");
                  }
               }
            }
         }

         for (int i5 = 0; i5 <= 2; i5++) {
            for (int i6 = 0; i6 <= i; i6++) {
               IBlockState blockStateFromPos = this.getBlockStateFromPos(worldIn, i5, -1, i6, structureBoundingBoxIn);
               if (blockStateFromPos.getBlock().getMaterial() == Material.air) {
                  this.setBlockState(worldIn, Blocks.planks.getDefaultState(), i5, -1, i6, structureBoundingBoxIn);
               }
            }
         }

         if (this.hasRails) {
            for (int i7 = 0; i7 <= i; i7++) {
               IBlockState blockStateFromPos2 = this.getBlockStateFromPos(worldIn, 1, -1, i7, structureBoundingBoxIn);
               if (blockStateFromPos2.getBlock().getMaterial() != Material.air && blockStateFromPos2.getBlock().isFullBlock()) {
                  this.randomlyPlaceBlock(
                     worldIn, structureBoundingBoxIn, randomIn, 0.7F, 1, 0, i7, Blocks.rail.getStateFromMeta(this.getMetadataWithOffset(Blocks.rail, 0))
                  );
               }
            }
         }

         return true;
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
         this.hasRails = tagCompound.getBoolean("hr");
         this.hasSpiders = tagCompound.getBoolean("sc");
         this.spawnerPlaced = tagCompound.getBoolean("hps");
         this.sectionCount = tagCompound.getInteger("Num");
      }
   }

   public static class Cross extends StructureComponent {
      private EnumFacing corridorDirection;
      private boolean isMultipleFloors;

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
         this.isMultipleFloors = tagCompound.getBoolean("tf");
         this.corridorDirection = EnumFacing.getHorizontal(tagCompound.getInteger("D"));
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         if (this.isMultipleFloors) {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               this.boundingBox.minX + 1,
               this.boundingBox.minY,
               this.boundingBox.minZ,
               this.boundingBox.maxX - 1,
               this.boundingBox.minY + 3 - 1,
               this.boundingBox.maxZ,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               this.boundingBox.minX,
               this.boundingBox.minY,
               this.boundingBox.minZ + 1,
               this.boundingBox.maxX,
               this.boundingBox.minY + 3 - 1,
               this.boundingBox.maxZ - 1,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               this.boundingBox.minX + 1,
               this.boundingBox.maxY - 2,
               this.boundingBox.minZ,
               this.boundingBox.maxX - 1,
               this.boundingBox.maxY,
               this.boundingBox.maxZ,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               this.boundingBox.minX,
               this.boundingBox.maxY - 2,
               this.boundingBox.minZ + 1,
               this.boundingBox.maxX,
               this.boundingBox.maxY,
               this.boundingBox.maxZ - 1,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               this.boundingBox.minX + 1,
               this.boundingBox.minY + 3,
               this.boundingBox.minZ + 1,
               this.boundingBox.maxX - 1,
               this.boundingBox.minY + 3,
               this.boundingBox.maxZ - 1,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
         } else {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               this.boundingBox.minX + 1,
               this.boundingBox.minY,
               this.boundingBox.minZ,
               this.boundingBox.maxX - 1,
               this.boundingBox.maxY,
               this.boundingBox.maxZ,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               this.boundingBox.minX,
               this.boundingBox.minY,
               this.boundingBox.minZ + 1,
               this.boundingBox.maxX,
               this.boundingBox.maxY,
               this.boundingBox.maxZ - 1,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
         }

         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.boundingBox.minX + 1,
            this.boundingBox.minY,
            this.boundingBox.minZ + 1,
            this.boundingBox.minX + 1,
            this.boundingBox.maxY,
            this.boundingBox.minZ + 1,
            Blocks.planks.getDefaultState(),
            Blocks.air.getDefaultState(),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.boundingBox.minX + 1,
            this.boundingBox.minY,
            this.boundingBox.maxZ - 1,
            this.boundingBox.minX + 1,
            this.boundingBox.maxY,
            this.boundingBox.maxZ - 1,
            Blocks.planks.getDefaultState(),
            Blocks.air.getDefaultState(),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.boundingBox.maxX - 1,
            this.boundingBox.minY,
            this.boundingBox.minZ + 1,
            this.boundingBox.maxX - 1,
            this.boundingBox.maxY,
            this.boundingBox.minZ + 1,
            Blocks.planks.getDefaultState(),
            Blocks.air.getDefaultState(),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.boundingBox.maxX - 1,
            this.boundingBox.minY,
            this.boundingBox.maxZ - 1,
            this.boundingBox.maxX - 1,
            this.boundingBox.maxY,
            this.boundingBox.maxZ - 1,
            Blocks.planks.getDefaultState(),
            Blocks.air.getDefaultState(),
            false
         );

         for (int index = this.boundingBox.minX; index <= this.boundingBox.maxX; index++) {
            for (int innerIndex = this.boundingBox.minZ; innerIndex <= this.boundingBox.maxZ; innerIndex++) {
               if (this.getBlockStateFromPos(worldIn, index, this.boundingBox.minY - 1, innerIndex, structureBoundingBoxIn).getBlock().getMaterial()
                  == Material.air) {
                  this.setBlockState(worldIn, Blocks.planks.getDefaultState(), index, this.boundingBox.minY - 1, innerIndex, structureBoundingBoxIn);
               }
            }
         }

         return true;
      }

      public Cross(int type, Random rand, StructureBoundingBox structurebb, EnumFacing facing) {
         super(type);
         this.corridorDirection = facing;
         this.boundingBox = structurebb;
         this.isMultipleFloors = structurebb.getYSize() > 3;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         int componentType = this.getComponentType();
         switch (this.corridorDirection) {
            case NORTH:
               StructureMineshaftPieces.generateAndAddPiece(
                  componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, componentType
               );
               StructureMineshaftPieces.generateAndAddPiece(
                  componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, componentType
               );
               StructureMineshaftPieces.generateAndAddPiece(
                  componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, componentType
               );
               break;
            case SOUTH:
               StructureMineshaftPieces.generateAndAddPiece(
                  componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, componentType
               );
               StructureMineshaftPieces.generateAndAddPiece(
                  componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, componentType
               );
               StructureMineshaftPieces.generateAndAddPiece(
                  componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, componentType
               );
               break;
            case WEST:
               StructureMineshaftPieces.generateAndAddPiece(
                  componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, componentType
               );
               StructureMineshaftPieces.generateAndAddPiece(
                  componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, componentType
               );
               StructureMineshaftPieces.generateAndAddPiece(
                  componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, componentType
               );
               break;
            case EAST:
               StructureMineshaftPieces.generateAndAddPiece(
                  componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, componentType
               );
               StructureMineshaftPieces.generateAndAddPiece(
                  componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, componentType
               );
               StructureMineshaftPieces.generateAndAddPiece(
                  componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, componentType
               );
         }

         if (this.isMultipleFloors) {
            if (rand.nextBoolean()) {
               StructureMineshaftPieces.generateAndAddPiece(
                  componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ - 1, EnumFacing.NORTH, componentType
               );
            }

            if (rand.nextBoolean()) {
               StructureMineshaftPieces.generateAndAddPiece(
                  componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, EnumFacing.WEST, componentType
               );
            }

            if (rand.nextBoolean()) {
               StructureMineshaftPieces.generateAndAddPiece(
                  componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, EnumFacing.EAST, componentType
               );
            }

            if (rand.nextBoolean()) {
               StructureMineshaftPieces.generateAndAddPiece(
                  componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, componentType
               );
            }
         }
      }

      public Cross() {
      }

      public static StructureBoundingBox findCrossingBoundingBox(List<StructureComponent> listIn, Random rand, int x, int y, int z, EnumFacing facing) {
         StructureBoundingBox structureBoundingBox4 = new StructureBoundingBox(x, y, z, x, y + 2, z);
         if (rand.nextInt(4) == 0) {
            structureBoundingBox4.maxY += 4;
         }

         switch (facing) {
            case NORTH:
               structureBoundingBox4.minX = x - 1;
               structureBoundingBox4.maxX = x + 3;
               structureBoundingBox4.minZ = z - 4;
               break;
            case SOUTH:
               structureBoundingBox4.minX = x - 1;
               structureBoundingBox4.maxX = x + 3;
               structureBoundingBox4.maxZ = z + 4;
               break;
            case WEST:
               structureBoundingBox4.minX = x - 4;
               structureBoundingBox4.minZ = z - 1;
               structureBoundingBox4.maxZ = z + 3;
               break;
            case EAST:
               structureBoundingBox4.maxX = x + 4;
               structureBoundingBox4.minZ = z - 1;
               structureBoundingBox4.maxZ = z + 3;
         }

         return StructureComponent.findIntersecting(listIn, structureBoundingBox4) != null ? null : structureBoundingBox4;
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         tagCompound.setBoolean("tf", this.isMultipleFloors);
         tagCompound.setInteger("D", this.corridorDirection.getHorizontalIndex());
      }
   }

   public static class Room extends StructureComponent {
      private List<StructureBoundingBox> roomsLinkedToTheRoom = Lists.newLinkedList();

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         int componentType = this.getComponentType();
         int i = this.boundingBox.getYSize() - 3 - 1;
         if (i <= 0) {
            i = 1;
         }

         int i2 = 0;

         while (i2 < this.boundingBox.getXSize()) {
            i2 += rand.nextInt(this.boundingBox.getXSize());
            if (i2 + 3 > this.boundingBox.getXSize()) {
               break;
            }

            StructureComponent structureComponent2 = StructureMineshaftPieces.generateAndAddPiece(
               componentIn,
               listIn,
               rand,
               this.boundingBox.minX + i2,
               this.boundingBox.minY + rand.nextInt(i) + 1,
               this.boundingBox.minZ - 1,
               EnumFacing.NORTH,
               componentType
            );
            if (structureComponent2 != null) {
               StructureBoundingBox boundingBox2 = structureComponent2.getBoundingBox();
               this.roomsLinkedToTheRoom
                  .add(new StructureBoundingBox(boundingBox2.minX, boundingBox2.minY, this.boundingBox.minZ, boundingBox2.maxX, boundingBox2.maxY, this.boundingBox.minZ + 1));
            }

            i2 += 4;
         }

         i2 = 0;

         while (i2 < this.boundingBox.getXSize()) {
            i2 += rand.nextInt(this.boundingBox.getXSize());
            if (i2 + 3 > this.boundingBox.getXSize()) {
               break;
            }

            StructureComponent structureComponent3 = StructureMineshaftPieces.generateAndAddPiece(
               componentIn,
               listIn,
               rand,
               this.boundingBox.minX + i2,
               this.boundingBox.minY + rand.nextInt(i) + 1,
               this.boundingBox.maxZ + 1,
               EnumFacing.SOUTH,
               componentType
            );
            if (structureComponent3 != null) {
               StructureBoundingBox boundingBox3 = structureComponent3.getBoundingBox();
               this.roomsLinkedToTheRoom
                  .add(new StructureBoundingBox(boundingBox3.minX, boundingBox3.minY, this.boundingBox.maxZ - 1, boundingBox3.maxX, boundingBox3.maxY, this.boundingBox.maxZ));
            }

            i2 += 4;
         }

         i2 = 0;

         while (i2 < this.boundingBox.getZSize()) {
            i2 += rand.nextInt(this.boundingBox.getZSize());
            if (i2 + 3 > this.boundingBox.getZSize()) {
               break;
            }

            StructureComponent structureComponent4 = StructureMineshaftPieces.generateAndAddPiece(
               componentIn,
               listIn,
               rand,
               this.boundingBox.minX - 1,
               this.boundingBox.minY + rand.nextInt(i) + 1,
               this.boundingBox.minZ + i2,
               EnumFacing.WEST,
               componentType
            );
            if (structureComponent4 != null) {
               StructureBoundingBox boundingBox4 = structureComponent4.getBoundingBox();
               this.roomsLinkedToTheRoom
                  .add(new StructureBoundingBox(this.boundingBox.minX, boundingBox4.minY, boundingBox4.minZ, this.boundingBox.minX + 1, boundingBox4.maxY, boundingBox4.maxZ));
            }

            i2 += 4;
         }

         i2 = 0;

         while (i2 < this.boundingBox.getZSize()) {
            i2 += rand.nextInt(this.boundingBox.getZSize());
            if (i2 + 3 > this.boundingBox.getZSize()) {
               break;
            }

            StructureComponent structureComponent5 = StructureMineshaftPieces.generateAndAddPiece(
               componentIn,
               listIn,
               rand,
               this.boundingBox.maxX + 1,
               this.boundingBox.minY + rand.nextInt(i) + 1,
               this.boundingBox.minZ + i2,
               EnumFacing.EAST,
               componentType
            );
            if (structureComponent5 != null) {
               StructureBoundingBox boundingBox5 = structureComponent5.getBoundingBox();
               this.roomsLinkedToTheRoom
                  .add(new StructureBoundingBox(this.boundingBox.maxX - 1, boundingBox5.minY, boundingBox5.minZ, this.boundingBox.maxX, boundingBox5.maxY, boundingBox5.maxZ));
            }

            i2 += 4;
         }
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
         NBTTagList nBTTagList = tagCompound.getTagList("Entrances", 11);

         for (int index = 0; index < nBTTagList.tagCount(); index++) {
            this.roomsLinkedToTheRoom.add(new StructureBoundingBox(nBTTagList.getIntArrayAt(index)));
         }
      }

      public Room() {
      }

      @Override
      public void handleInt(int i) {
         super.handleInt(i);
         Iterator iterator2 = this.roomsLinkedToTheRoom.iterator();

         while (iterator2.hasNext()) {
            ((StructureBoundingBox)iterator2.next()).offset(0, i, 0);
         }
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.boundingBox.minX,
            this.boundingBox.minY,
            this.boundingBox.minZ,
            this.boundingBox.maxX,
            this.boundingBox.minY,
            this.boundingBox.maxZ,
            Blocks.dirt.getDefaultState(),
            Blocks.air.getDefaultState(),
            true
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.boundingBox.minX,
            this.boundingBox.minY + 1,
            this.boundingBox.minZ,
            this.boundingBox.maxX,
            Math.min(this.boundingBox.minY + 3, this.boundingBox.maxY),
            this.boundingBox.maxZ,
            Blocks.air.getDefaultState(),
            Blocks.air.getDefaultState(),
            false
         );

         for (StructureBoundingBox structureBoundingBox4 : this.roomsLinkedToTheRoom) {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               structureBoundingBox4.minX,
               structureBoundingBox4.maxY - 2,
               structureBoundingBox4.minZ,
               structureBoundingBox4.maxX,
               structureBoundingBox4.maxY,
               structureBoundingBox4.maxZ,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
         }

         this.handleWorld4(
            worldIn,
            structureBoundingBoxIn,
            this.boundingBox.minX,
            this.boundingBox.minY + 4,
            this.boundingBox.minZ,
            this.boundingBox.maxX,
            this.boundingBox.maxY,
            this.boundingBox.maxZ,
            Blocks.air.getDefaultState()
         );
         return true;
      }

      public Room(int type, Random rand, int x, int z) {
         super(type);
         this.boundingBox = new StructureBoundingBox(x, 50, z, x + 7 + rand.nextInt(6), 54 + rand.nextInt(6), z + 7 + rand.nextInt(6));
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         NBTTagList nbtTagList = new NBTTagList();

         for (StructureBoundingBox structureBoundingBox4 : this.roomsLinkedToTheRoom) {
            nbtTagList.appendTag(structureBoundingBox4.toNBTTagIntArray());
         }

         tagCompound.setTag("Entrances", nbtTagList);
      }
   }

   public static class Stairs extends StructureComponent {
      public Stairs() {
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound nbtTagCompound) {
      }

      public Stairs(int type, Random rand, StructureBoundingBox structurebb, EnumFacing facing) {
         super(type);
         this.coordBaseMode = facing;
         this.boundingBox = structurebb;
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound nbtTagCompound) {
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 0, 2, 7, 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 7, 2, 2, 8, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);

         for (int index = 0; index < 5; index++) {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               0,
               5 - index - (index < 4 ? 1 : 0),
               2 + index,
               2,
               7 - index,
               2 + index,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
         }

         return true;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         int componentType = this.getComponentType();
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case NORTH:
                  StructureMineshaftPieces.generateAndAddPiece(
                     componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, componentType
                  );
                  break;
               case SOUTH:
                  StructureMineshaftPieces.generateAndAddPiece(
                     componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, componentType
                  );
                  break;
               case WEST:
                  StructureMineshaftPieces.generateAndAddPiece(
                     componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, EnumFacing.WEST, componentType
                  );
                  break;
               case EAST:
                  StructureMineshaftPieces.generateAndAddPiece(
                     componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, EnumFacing.EAST, componentType
                  );
            }
         }
      }

      public static StructureBoundingBox findStairsBoundingBox(List<StructureComponent> listIn, Random rand, int x, int y, int z, EnumFacing facing) {
         StructureBoundingBox structureBoundingBox4 = new StructureBoundingBox(x, y - 5, z, x, y + 2, z);
         switch (facing) {
            case NORTH:
               structureBoundingBox4.maxX = x + 2;
               structureBoundingBox4.minZ = z - 8;
               break;
            case SOUTH:
               structureBoundingBox4.maxX = x + 2;
               structureBoundingBox4.maxZ = z + 8;
               break;
            case WEST:
               structureBoundingBox4.minX = x - 8;
               structureBoundingBox4.maxZ = z + 2;
               break;
            case EAST:
               structureBoundingBox4.maxX = x + 8;
               structureBoundingBox4.maxZ = z + 2;
         }

         return StructureComponent.findIntersecting(listIn, structureBoundingBox4) != null ? null : structureBoundingBox4;
      }
   }
}
