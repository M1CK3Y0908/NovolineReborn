package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockSandStone.EnumType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

public class StructureVillagePieces {
   private static int getTotalWeight(List<StructureVillagePieces.PieceWeight> pieces) {
      int i = 0;
      int i2 = 0;

      for (StructureVillagePieces.PieceWeight pieceWeight : pieces) {
         if (pieceWeight.villagePiecesLimit > 0 && pieceWeight.villagePiecesSpawned < pieceWeight.villagePiecesLimit) {
            i = 1;
         }

         i2 += pieceWeight.villagePieceWeight;
      }

      return i != 0 ? i2 : -1;
   }

   private static StructureComponent addVillageStructure(
      StructureVillagePieces.Start start, List<StructureComponent> components, Random rand, int x, int y, int z, EnumFacing facing, int type
   ) {
      if (type > 50) {
         return null;
      }

      if (Math.abs(x - start.getBoundingBox().minX) <= 112 && Math.abs(z - start.getBoundingBox().minZ) <= 112) {
         StructureComponent structureComponent = addVillageComponent(start, components, rand, x, y, z, facing, type + 1);
         if (structureComponent != null) {
            int i = (structureComponent.boundingBox.minX + structureComponent.boundingBox.maxX) / 2;
            int i2 = (structureComponent.boundingBox.minZ + structureComponent.boundingBox.maxZ) / 2;
            int i3 = structureComponent.boundingBox.maxX - structureComponent.boundingBox.minX;
            int i4 = structureComponent.boundingBox.maxZ - structureComponent.boundingBox.minZ;
            int i5 = i3 > i4 ? i3 : i4;
            if (start.getWorldChunkManager().areBiomesViable(i, i2, i5 / 2 + 4, MapGenVillage.villageSpawnBiomes)) {
               components.add(structureComponent);
               start.villageComponents.add(structureComponent);
               return structureComponent;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private static StructureVillagePieces.Village createVillagePiece(
      StructureVillagePieces.Start start,
      StructureVillagePieces.PieceWeight weight,
      List<StructureComponent> components,
      Random rand,
      int x,
      int y,
      int z,
      EnumFacing facing,
      int type
   ) {
      Class<? extends StructureVillagePieces.Village> oclass = weight.villagePieceClass;
      StructureVillagePieces.Village village = null;
      if (oclass == StructureVillagePieces.House4Garden.class) {
         village = StructureVillagePieces.House4Garden.createComponent(start, components, rand, x, y, z, facing, type);
      } else if (oclass == StructureVillagePieces.VillageChild2.class) {
         village = StructureVillagePieces.VillageChild2.createVillageChild2(start, components, rand, x, y, z, facing, type);
      } else if (oclass == StructureVillagePieces.VillageChild3.class) {
         village = StructureVillagePieces.VillageChild3.createVillageChild3(start, components, rand, x, y, z, facing, type);
      } else if (oclass == StructureVillagePieces.WoodHut.class) {
         village = StructureVillagePieces.WoodHut.createComponent(start, components, rand, x, y, z, facing, type);
      } else if (oclass == StructureVillagePieces.VillageChild.class) {
         village = StructureVillagePieces.VillageChild.createVillageChild(start, components, rand, x, y, z, facing, type);
      } else if (oclass == StructureVillagePieces.Field1.class) {
         village = StructureVillagePieces.Field1.createComponent(start, components, rand, x, y, z, facing, type);
      } else if (oclass == StructureVillagePieces.Field2.class) {
         village = StructureVillagePieces.Field2.createComponent(start, components, rand, x, y, z, facing, type);
      } else if (oclass == StructureVillagePieces.House2.class) {
         village = StructureVillagePieces.House2.createComponent(start, components, rand, x, y, z, facing, type);
      } else if (oclass == StructureVillagePieces.House3.class) {
         village = StructureVillagePieces.House3.createComponent(start, components, rand, x, y, z, facing, type);
      }

      return village;
   }

   private static StructureVillagePieces.Village addVillageComponent(
      StructureVillagePieces.Start start, List<StructureComponent> components, Random rand, int x, int y, int z, EnumFacing facing, int type
   ) {
      int totalWeight = getTotalWeight(start.structureVillageWeightedPieceList);
      if (totalWeight <= 0) {
         return null;
      }

      int i = 0;

      while (i < 5) {
         i++;
         int i2 = rand.nextInt(totalWeight);

         for (StructureVillagePieces.PieceWeight pieceWeight : start.structureVillageWeightedPieceList) {
            i2 -= pieceWeight.villagePieceWeight;
            if (i2 < 0) {
               if (!pieceWeight.isActive() || pieceWeight == start.structVillagePieceWeight && start.structureVillageWeightedPieceList.size() > 1) {
                  break;
               }

               StructureVillagePieces.Village villagePiece = createVillagePiece(start, pieceWeight, components, rand, x, y, z, facing, type);
               if (villagePiece != null) {
                  pieceWeight.villagePiecesSpawned++;
                  start.structVillagePieceWeight = pieceWeight;
                  if (!pieceWeight.canSpawnMoreVillagePieces()) {
                     start.structureVillageWeightedPieceList.remove(pieceWeight);
                  }

                  return villagePiece;
               }
            }
         }
      }

      StructureBoundingBox boundingBox2 = StructureVillagePieces.Torch.createBoundingBox(start, components, rand, x, y, z, facing);
      return boundingBox2 != null ? new StructureVillagePieces.Torch(start, type, rand, boundingBox2, facing) : null;
   }

   public static List<StructureVillagePieces.PieceWeight> getStructureVillageWeightedPieceList(Random random, int size) {
      List<StructureVillagePieces.PieceWeight> list = Lists.newArrayList();
      list.add(
         new StructureVillagePieces.PieceWeight(
            StructureVillagePieces.House4Garden.class, 4, MathHelper.getRandomIntegerInRange(random, 2 + size, 4 + size * 2)
         )
      );
      list.add(
         new StructureVillagePieces.PieceWeight(StructureVillagePieces.VillageChild2.class, 20, MathHelper.getRandomIntegerInRange(random, 0 + size, 1 + size))
      );
      list.add(
         new StructureVillagePieces.PieceWeight(StructureVillagePieces.VillageChild3.class, 20, MathHelper.getRandomIntegerInRange(random, 0 + size, 2 + size))
      );
      list.add(
         new StructureVillagePieces.PieceWeight(StructureVillagePieces.WoodHut.class, 3, MathHelper.getRandomIntegerInRange(random, 2 + size, 5 + size * 3))
      );
      list.add(
         new StructureVillagePieces.PieceWeight(StructureVillagePieces.VillageChild.class, 15, MathHelper.getRandomIntegerInRange(random, 0 + size, 2 + size))
      );
      list.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Field1.class, 3, MathHelper.getRandomIntegerInRange(random, 1 + size, 4 + size)));
      list.add(
         new StructureVillagePieces.PieceWeight(StructureVillagePieces.Field2.class, 3, MathHelper.getRandomIntegerInRange(random, 2 + size, 4 + size * 2))
      );
      list.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House2.class, 15, MathHelper.getRandomIntegerInRange(random, 0, 1 + size)));
      list.add(
         new StructureVillagePieces.PieceWeight(StructureVillagePieces.House3.class, 8, MathHelper.getRandomIntegerInRange(random, 0 + size, 3 + size * 2))
      );
      Iterator<StructureVillagePieces.PieceWeight> iterator = list.iterator();

      while (iterator.hasNext()) {
         if (iterator.next().villagePiecesLimit == 0) {
            iterator.remove();
         }
      }

      return list;
   }

   public static void registerVillagePieces() {
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.VillageChild3.class, "ViBH");
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.Field1.class, "ViDF");
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.Field2.class, "ViF");
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.Torch.class, "ViL");
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.VillageChild.class, "ViPH");
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.House4Garden.class, "ViSH");
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.WoodHut.class, "ViSmH");
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.VillageChild2.class, "ViST");
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.House2.class, "ViS");
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.Start.class, "ViStart");
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.Path.class, "ViSR");
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.House3.class, "ViTRH");
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.Well.class, "ViW");
   }

   private static StructureComponent addVillagePath(
      StructureVillagePieces.Start start, List<StructureComponent> components, Random rand, int x, int y, int z, EnumFacing facing, int type
   ) {
      if (type > 3 + start.terrainType) {
         return null;
      }

      if (Math.abs(x - start.getBoundingBox().minX) <= 112 && Math.abs(z - start.getBoundingBox().minZ) <= 112) {
         StructureBoundingBox structureBoundingBox = StructureVillagePieces.Path.createBoundingBox(start, components, rand, x, y, z, facing);
         if (structureBoundingBox != null && structureBoundingBox.minY > 10) {
            StructureVillagePieces.Path path = new StructureVillagePieces.Path(start, type, rand, structureBoundingBox, facing);
            int i = (path.boundingBox.minX + path.boundingBox.maxX) / 2;
            int i2 = (path.boundingBox.minZ + path.boundingBox.maxZ) / 2;
            int i3 = path.boundingBox.maxX - path.boundingBox.minX;
            int i4 = path.boundingBox.maxZ - path.boundingBox.minZ;
            int i5 = i3 > i4 ? i3 : i4;
            if (start.getWorldChunkManager().areBiomesViable(i, i2, i5 / 2 + 4, MapGenVillage.villageSpawnBiomes)) {
               components.add(path);
               start.villageRoads.add(path);
               return path;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static class Field1 extends StructureVillagePieces.Village {
      private Block cropTypeA;
      private Block cropTypeB;
      private Block cropTypeC;
      private Block cropTypeD;

      public Field1(StructureVillagePieces.Start start, int type, Random rand, StructureBoundingBox boundingBox, EnumFacing facing) {
         super(start, type);
         this.coordBaseMode = facing;
         this.boundingBox = boundingBox;
         this.cropTypeA = this.getRandomCrop(rand);
         this.cropTypeB = this.getRandomCrop(rand);
         this.cropTypeC = this.getRandomCrop(rand);
         this.cropTypeD = this.getRandomCrop(rand);
      }

      public Field1() {
      }

      public static StructureVillagePieces.Field1 createComponent(
         StructureVillagePieces.Start start, List<StructureComponent> components, Random rand, int x, int y, int z, EnumFacing facing, int type
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(x, y, z, 0, 0, 13, 4, 9, facing);
         return canVillageGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(components, structureBoundingBox) == null
            ? new StructureVillagePieces.Field1(start, type, rand, structureBoundingBox, facing)
            : null;
      }

      private Block getRandomCrop(Random rand) {
         switch (rand.nextInt(5)) {
            case 0:
               return Blocks.carrots;
            case 1:
               return Blocks.potatoes;
            default:
               return Blocks.wheat;
         }
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setInteger("CA", Block.blockRegistry.getIDForObject(this.cropTypeA));
         tagCompound.setInteger("CB", Block.blockRegistry.getIDForObject(this.cropTypeB));
         tagCompound.setInteger("CC", Block.blockRegistry.getIDForObject(this.cropTypeC));
         tagCompound.setInteger("CD", Block.blockRegistry.getIDForObject(this.cropTypeD));
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.averageGroundLevel < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 4 - 1, 0);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 12, 4, 8, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 1, 2, 0, 7, Blocks.farmland.getDefaultState(), Blocks.farmland.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 0, 1, 5, 0, 7, Blocks.farmland.getDefaultState(), Blocks.farmland.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 0, 1, 8, 0, 7, Blocks.farmland.getDefaultState(), Blocks.farmland.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 0, 1, 11, 0, 7, Blocks.farmland.getDefaultState(), Blocks.farmland.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 0, 0, 8, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 0, 0, 6, 0, 8, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 0, 0, 12, 0, 8, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 0, 11, 0, 0, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 8, 11, 0, 8, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 0, 1, 3, 0, 7, Blocks.water.getDefaultState(), Blocks.water.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 0, 1, 9, 0, 7, Blocks.water.getDefaultState(), Blocks.water.getDefaultState(), false);

         for (int index = 1; index <= 7; index++) {
            this.setBlockState(
               worldIn, this.cropTypeA.getStateFromMeta(MathHelper.getRandomIntegerInRange(randomIn, 2, 7)), 1, 1, index, structureBoundingBoxIn
            );
            this.setBlockState(
               worldIn, this.cropTypeA.getStateFromMeta(MathHelper.getRandomIntegerInRange(randomIn, 2, 7)), 2, 1, index, structureBoundingBoxIn
            );
            this.setBlockState(
               worldIn, this.cropTypeB.getStateFromMeta(MathHelper.getRandomIntegerInRange(randomIn, 2, 7)), 4, 1, index, structureBoundingBoxIn
            );
            this.setBlockState(
               worldIn, this.cropTypeB.getStateFromMeta(MathHelper.getRandomIntegerInRange(randomIn, 2, 7)), 5, 1, index, structureBoundingBoxIn
            );
            this.setBlockState(
               worldIn, this.cropTypeC.getStateFromMeta(MathHelper.getRandomIntegerInRange(randomIn, 2, 7)), 7, 1, index, structureBoundingBoxIn
            );
            this.setBlockState(
               worldIn, this.cropTypeC.getStateFromMeta(MathHelper.getRandomIntegerInRange(randomIn, 2, 7)), 8, 1, index, structureBoundingBoxIn
            );
            this.setBlockState(
               worldIn, this.cropTypeD.getStateFromMeta(MathHelper.getRandomIntegerInRange(randomIn, 2, 7)), 10, 1, index, structureBoundingBoxIn
            );
            this.setBlockState(
               worldIn, this.cropTypeD.getStateFromMeta(MathHelper.getRandomIntegerInRange(randomIn, 2, 7)), 11, 1, index, structureBoundingBoxIn
            );
         }

         for (int nestedIndex = 0; nestedIndex < 9; nestedIndex++) {
            for (int innerIndex = 0; innerIndex < 13; innerIndex++) {
               this.clearCurrentPositionBlocksUpwards(worldIn, innerIndex, 4, nestedIndex, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.dirt.getDefaultState(), innerIndex, -1, nestedIndex, structureBoundingBoxIn);
            }
         }

         return true;
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.cropTypeA = Block.getBlockById(tagCompound.getInteger("CA"));
         this.cropTypeB = Block.getBlockById(tagCompound.getInteger("CB"));
         this.cropTypeC = Block.getBlockById(tagCompound.getInteger("CC"));
         this.cropTypeD = Block.getBlockById(tagCompound.getInteger("CD"));
      }
   }

   public static class Field2 extends StructureVillagePieces.Village {
      private Block cropTypeA;
      private Block cropTypeB;

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.averageGroundLevel < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 4 - 1, 0);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 6, 4, 8, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 1, 2, 0, 7, Blocks.farmland.getDefaultState(), Blocks.farmland.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 0, 1, 5, 0, 7, Blocks.farmland.getDefaultState(), Blocks.farmland.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 0, 0, 8, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 0, 0, 6, 0, 8, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 0, 5, 0, 0, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 8, 5, 0, 8, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 0, 1, 3, 0, 7, Blocks.water.getDefaultState(), Blocks.water.getDefaultState(), false);

         for (int index = 1; index <= 7; index++) {
            this.setBlockState(
               worldIn, this.cropTypeA.getStateFromMeta(MathHelper.getRandomIntegerInRange(randomIn, 2, 7)), 1, 1, index, structureBoundingBoxIn
            );
            this.setBlockState(
               worldIn, this.cropTypeA.getStateFromMeta(MathHelper.getRandomIntegerInRange(randomIn, 2, 7)), 2, 1, index, structureBoundingBoxIn
            );
            this.setBlockState(
               worldIn, this.cropTypeB.getStateFromMeta(MathHelper.getRandomIntegerInRange(randomIn, 2, 7)), 4, 1, index, structureBoundingBoxIn
            );
            this.setBlockState(
               worldIn, this.cropTypeB.getStateFromMeta(MathHelper.getRandomIntegerInRange(randomIn, 2, 7)), 5, 1, index, structureBoundingBoxIn
            );
         }

         for (int nestedIndex = 0; nestedIndex < 9; nestedIndex++) {
            for (int innerIndex = 0; innerIndex < 7; innerIndex++) {
               this.clearCurrentPositionBlocksUpwards(worldIn, innerIndex, 4, nestedIndex, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.dirt.getDefaultState(), innerIndex, -1, nestedIndex, structureBoundingBoxIn);
            }
         }

         return true;
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setInteger("CA", Block.blockRegistry.getIDForObject(this.cropTypeA));
         tagCompound.setInteger("CB", Block.blockRegistry.getIDForObject(this.cropTypeB));
      }

      private Block getRandomCrop(Random rand) {
         switch (rand.nextInt(5)) {
            case 0:
               return Blocks.carrots;
            case 1:
               return Blocks.potatoes;
            default:
               return Blocks.wheat;
         }
      }

      public Field2() {
      }

      public Field2(StructureVillagePieces.Start start, int type, Random rand, StructureBoundingBox boundingBox, EnumFacing facing) {
         super(start, type);
         this.coordBaseMode = facing;
         this.boundingBox = boundingBox;
         this.cropTypeA = this.getRandomCrop(rand);
         this.cropTypeB = this.getRandomCrop(rand);
      }

      public static StructureVillagePieces.Field2 createComponent(
         StructureVillagePieces.Start start, List<StructureComponent> components, Random rand, int x, int y, int z, EnumFacing facing, int type
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(x, y, z, 0, 0, 7, 4, 9, facing);
         return canVillageGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(components, structureBoundingBox) == null
            ? new StructureVillagePieces.Field2(start, type, rand, structureBoundingBox, facing)
            : null;
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.cropTypeA = Block.getBlockById(tagCompound.getInteger("CA"));
         this.cropTypeB = Block.getBlockById(tagCompound.getInteger("CB"));
      }
   }

   public static class House2 extends StructureVillagePieces.Village {
      private static final List<WeightedRandomChestContent> villageBlacksmithChestContents = Lists.newArrayList(
         new WeightedRandomChestContent[]{
            new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 3),
            new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10),
            new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 5),
            new WeightedRandomChestContent(Items.bread, 0, 1, 3, 15),
            new WeightedRandomChestContent(Items.apple, 0, 1, 3, 15),
            new WeightedRandomChestContent(Items.iron_pickaxe, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.iron_sword, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.iron_chestplate, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.iron_helmet, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.iron_leggings, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.iron_boots, 0, 1, 1, 5),
            new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.obsidian), 0, 3, 7, 5),
            new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.sapling), 0, 3, 7, 5),
            new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 3),
            new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1),
            new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 1),
            new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1)
         }
      );
      private boolean hasMadeChest;

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.hasMadeChest = tagCompound.getBoolean("Chest");
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.averageGroundLevel < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 6 - 1, 0);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 9, 4, 6, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 9, 0, 6, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 4, 0, 9, 4, 6, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 0, 9, 5, 6, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 1, 8, 5, 5, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 2, 3, 0, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 4, 0, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 0, 3, 4, 0, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 6, 0, 4, 6, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 3, 3, 1, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 2, 3, 3, 2, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 3, 5, 3, 3, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 3, 5, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 6, 5, 3, 6, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 0, 5, 3, 0, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 1, 0, 9, 3, 0, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 1, 4, 9, 4, 6, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.setBlockState(worldIn, Blocks.flowing_lava.getDefaultState(), 7, 1, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.flowing_lava.getDefaultState(), 8, 1, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), 9, 2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), 9, 2, 4, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 4, 8, 2, 5, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 6, 1, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.furnace.getDefaultState(), 6, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.furnace.getDefaultState(), 6, 3, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.double_stone_slab.getDefaultState(), 8, 1, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 2, 2, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 4, 2, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 2, 1, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.wooden_pressure_plate.getDefaultState(), 2, 2, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 1, 1, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.oak_stairs, 3)), 2, 1, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.oak_stairs, 1)), 1, 1, 4, structureBoundingBoxIn);
         if (!this.hasMadeChest
            && structureBoundingBoxIn.isVecInside(new BlockPos(this.getXWithOffset(5, 5), this.getYWithOffset(1), this.getZWithOffset(5, 5)))) {
            this.hasMadeChest = true;
            this.generateChestContents(worldIn, structureBoundingBoxIn, randomIn, 5, 1, 5, villageBlacksmithChestContents, 3 + randomIn.nextInt(6));
         }

         for (int index = 6; index <= 8; index++) {
            if (this.getBlockStateFromPos(worldIn, index, 0, -1, structureBoundingBoxIn).getBlock().getMaterial() == Material.air
               && this.getBlockStateFromPos(worldIn, index, -1, -1, structureBoundingBoxIn).getBlock().getMaterial() != Material.air) {
               this.setBlockState(
                  worldIn, Blocks.stone_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_stairs, 3)), index, 0, -1, structureBoundingBoxIn
               );
            }
         }

         for (int nestedIndex = 0; nestedIndex < 7; nestedIndex++) {
            for (int innerIndex = 0; innerIndex < 10; innerIndex++) {
               this.clearCurrentPositionBlocksUpwards(worldIn, innerIndex, 6, nestedIndex, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.cobblestone.getDefaultState(), innerIndex, -1, nestedIndex, structureBoundingBoxIn);
            }
         }

         this.handleWorld(worldIn, structureBoundingBoxIn, 7, 1, 1);
         return true;
      }

      public static StructureVillagePieces.House2 createComponent(
         StructureVillagePieces.Start start, List<StructureComponent> components, Random rand, int x, int y, int z, EnumFacing facing, int type
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(x, y, z, 0, 0, 10, 6, 7, facing);
         return canVillageGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(components, structureBoundingBox) == null
            ? new StructureVillagePieces.House2(start, type, rand, structureBoundingBox, facing)
            : null;
      }

      @Override
      protected int getVillagerType(int population, int profession) {
         return 3;
      }

      public House2(StructureVillagePieces.Start start, int type, Random rand, StructureBoundingBox boundingBox, EnumFacing facing) {
         super(start, type);
         this.coordBaseMode = facing;
         this.boundingBox = boundingBox;
      }

      public House2() {
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Chest", this.hasMadeChest);
      }
   }

   public static class House3 extends StructureVillagePieces.Village {
      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.averageGroundLevel < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 7 - 1, 0);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 7, 4, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 6, 8, 4, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 0, 5, 8, 0, 10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 1, 7, 0, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 0, 3, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, 0, 0, 8, 3, 10, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 0, 0, 7, 2, 0, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 0, 5, 2, 1, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 0, 6, 2, 3, 10, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 3, 0, 10, 7, 3, 10, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 7, 3, 0, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 5, 2, 3, 5, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 1, 8, 4, 1, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 4, 3, 4, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 2, 8, 5, 3, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 0, 4, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 0, 4, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 8, 4, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 8, 4, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 8, 4, 4, structureBoundingBoxIn);
         int metadataWithOffset = this.getMetadataWithOffset(Blocks.oak_stairs, 3);
         int metadataWithOffset2 = this.getMetadataWithOffset(Blocks.oak_stairs, 2);

         for (int nestedIndex = -1; nestedIndex <= 2; nestedIndex++) {
            for (int outerIndex = 0; outerIndex <= 8; outerIndex++) {
               this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset), outerIndex, 4 + nestedIndex, nestedIndex, structureBoundingBoxIn);
               if ((nestedIndex > -1 || outerIndex <= 1) && (nestedIndex > 0 || outerIndex <= 3) && (nestedIndex > 1 || outerIndex <= 4 || outerIndex >= 6)) {
                  this.setBlockState(
                     worldIn, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset2), outerIndex, 4 + nestedIndex, 5 - nestedIndex, structureBoundingBoxIn
                  );
               }
            }
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 4, 5, 3, 4, 10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 4, 2, 7, 4, 10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 5, 4, 4, 5, 10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 5, 4, 6, 5, 10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 6, 3, 5, 6, 10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         int metadataWithOffset3 = this.getMetadataWithOffset(Blocks.oak_stairs, 0);

         for (int outerIndex2 = 4; outerIndex2 >= 1; outerIndex2--) {
            this.setBlockState(worldIn, Blocks.planks.getDefaultState(), outerIndex2, 2 + outerIndex2, 7 - outerIndex2, structureBoundingBoxIn);

            for (int index = 8 - outerIndex2; index <= 10; index++) {
               this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset3), outerIndex2, 2 + outerIndex2, index, structureBoundingBoxIn);
            }
         }

         int metadataWithOffset4 = this.getMetadataWithOffset(Blocks.oak_stairs, 1);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 6, 6, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 7, 5, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset4), 6, 6, 4, structureBoundingBoxIn);

         for (int index2 = 6; index2 <= 8; index2++) {
            for (int innerIndex = 5; innerIndex <= 10; innerIndex++) {
               this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset4), index2, 12 - index2, innerIndex, structureBoundingBoxIn);
            }
         }

         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 0, 2, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 0, 2, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 4, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 5, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 6, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 8, 2, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 8, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 8, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 8, 2, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 8, 2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 8, 2, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 8, 2, 7, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 8, 2, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 8, 2, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 2, 2, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 2, 2, 7, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 2, 2, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 2, 2, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 4, 4, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 5, 4, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 6, 4, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 5, 5, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 2, 1, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 2, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode), 2, 3, 1, structureBoundingBoxIn);
         this.handleWorld2(worldIn, structureBoundingBoxIn, randomIn, 2, 0, EnumFacing.getHorizontal(this.getMetadataWithOffset(Blocks.oak_door, 1)));
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, -1, 3, 2, -1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         if (this.getBlockStateFromPos(worldIn, 2, 0, -1, structureBoundingBoxIn).getBlock().getMaterial() == Material.air
            && this.getBlockStateFromPos(worldIn, 2, -1, -1, structureBoundingBoxIn).getBlock().getMaterial() != Material.air) {
            this.setBlockState(
               worldIn, Blocks.stone_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_stairs, 3)), 2, 0, -1, structureBoundingBoxIn
            );
         }

         for (int index3 = 0; index3 < 5; index3++) {
            for (int index4 = 0; index4 < 9; index4++) {
               this.clearCurrentPositionBlocksUpwards(worldIn, index4, 7, index3, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.cobblestone.getDefaultState(), index4, -1, index3, structureBoundingBoxIn);
            }
         }

         for (int index5 = 5; index5 < 11; index5++) {
            for (int index6 = 2; index6 < 9; index6++) {
               this.clearCurrentPositionBlocksUpwards(worldIn, index6, 7, index5, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.cobblestone.getDefaultState(), index6, -1, index5, structureBoundingBoxIn);
            }
         }

         this.handleWorld(worldIn, structureBoundingBoxIn, 4, 2, 2);
         return true;
      }

      public House3() {
      }

      public House3(StructureVillagePieces.Start start, int type, Random rand, StructureBoundingBox boundingBox, EnumFacing facing) {
         super(start, type);
         this.coordBaseMode = facing;
         this.boundingBox = boundingBox;
      }

      public static StructureVillagePieces.House3 createComponent(
         StructureVillagePieces.Start start, List<StructureComponent> components, Random rand, int x, int y, int z, EnumFacing facing, int type
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(x, y, z, 0, 0, 9, 7, 12, facing);
         return canVillageGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(components, structureBoundingBox) == null
            ? new StructureVillagePieces.House3(start, type, rand, structureBoundingBox, facing)
            : null;
      }
   }

   public static class House4Garden extends StructureVillagePieces.Village {
      private boolean isRoofAccessible;

      public House4Garden(StructureVillagePieces.Start start, int i, Random rand, StructureBoundingBox structureBoundingBox2, EnumFacing facing) {
         super(start, i);
         this.coordBaseMode = facing;
         this.boundingBox = structureBoundingBox2;
         this.isRoofAccessible = rand.nextBoolean();
      }

      public static StructureVillagePieces.House4Garden createComponent(
         StructureVillagePieces.Start start, List<StructureComponent> components, Random rand, int x, int y, int z, EnumFacing facing, int type
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(x, y, z, 0, 0, 5, 6, 5, facing);
         return StructureComponent.findIntersecting(components, structureBoundingBox) != null
            ? null
            : new StructureVillagePieces.House4Garden(start, type, rand, structureBoundingBox, facing);
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.isRoofAccessible = tagCompound.getBoolean("Terrace");
      }

      public House4Garden() {
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.averageGroundLevel < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 6 - 1, 0);
         }

         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 0, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 0, 4, 4, 4, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 1, 3, 4, 3, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 0, 1, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 0, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 0, 3, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 4, 1, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 4, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 4, 3, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 0, 1, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 0, 2, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 0, 3, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 4, 1, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 4, 2, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 4, 3, 4, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 3, 3, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 1, 4, 3, 3, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 4, 3, 3, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 2, 2, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 4, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 1, 1, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 1, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 1, 3, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 2, 3, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 3, 3, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 3, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 3, 1, 0, structureBoundingBoxIn);
         if (this.getBlockStateFromPos(worldIn, 2, 0, -1, structureBoundingBoxIn).getBlock().getMaterial() == Material.air
            && this.getBlockStateFromPos(worldIn, 2, -1, -1, structureBoundingBoxIn).getBlock().getMaterial() != Material.air) {
            this.setBlockState(
               worldIn, Blocks.stone_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_stairs, 3)), 2, 0, -1, structureBoundingBoxIn
            );
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 3, 3, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         if (this.isRoofAccessible) {
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 0, 5, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 1, 5, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 2, 5, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 3, 5, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 4, 5, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 0, 5, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 1, 5, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 2, 5, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 3, 5, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 4, 5, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 4, 5, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 4, 5, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 4, 5, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 0, 5, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 0, 5, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 0, 5, 3, structureBoundingBoxIn);
         }

         if (this.isRoofAccessible) {
            int metadataWithOffset = this.getMetadataWithOffset(Blocks.ladder, 3);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(metadataWithOffset), 3, 1, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(metadataWithOffset), 3, 2, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(metadataWithOffset), 3, 3, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(metadataWithOffset), 3, 4, 3, structureBoundingBoxIn);
         }

         this.setBlockState(worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode), 2, 3, 1, structureBoundingBoxIn);

         for (int nestedIndex = 0; nestedIndex < 5; nestedIndex++) {
            for (int innerIndex = 0; innerIndex < 5; innerIndex++) {
               this.clearCurrentPositionBlocksUpwards(worldIn, innerIndex, 6, nestedIndex, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.cobblestone.getDefaultState(), innerIndex, -1, nestedIndex, structureBoundingBoxIn);
            }
         }

         this.handleWorld(worldIn, structureBoundingBoxIn, 1, 2, 1);
         return true;
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Terrace", this.isRoofAccessible);
      }
   }

   public static class Path extends StructureVillagePieces.Road {
      private int length;

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         boolean conditionMet = false;

         for (int index = rand.nextInt(5); index < this.length - 8; index += 2 + rand.nextInt(5)) {
            StructureComponent structureComponent = this.getStructureComponent((StructureVillagePieces.Start)componentIn, listIn, rand, index);
            if (structureComponent != null) {
               index += Math.max(structureComponent.boundingBox.getXSize(), structureComponent.boundingBox.getZSize());
               conditionMet = true;
            }
         }

         for (int innerIndex = rand.nextInt(5); innerIndex < this.length - 8; innerIndex += 2 + rand.nextInt(5)) {
            StructureComponent structureComponent2 = this.getStructureComponent2((StructureVillagePieces.Start)componentIn, listIn, rand, innerIndex);
            if (structureComponent2 != null) {
               innerIndex += Math.max(structureComponent2.boundingBox.getXSize(), structureComponent2.boundingBox.getZSize());
               conditionMet = true;
            }
         }

         if (conditionMet && rand.nextInt(3) > 0 && this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case NORTH:
                  StructureVillagePieces.addVillagePath(
                     (StructureVillagePieces.Start)componentIn,
                     listIn,
                     rand,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY,
                     this.boundingBox.minZ,
                     EnumFacing.WEST,
                     this.getComponentType()
                  );
                  break;
               case SOUTH:
                  StructureVillagePieces.addVillagePath(
                     (StructureVillagePieces.Start)componentIn,
                     listIn,
                     rand,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY,
                     this.boundingBox.maxZ - 2,
                     EnumFacing.WEST,
                     this.getComponentType()
                  );
                  break;
               case WEST:
                  StructureVillagePieces.addVillagePath(
                     (StructureVillagePieces.Start)componentIn,
                     listIn,
                     rand,
                     this.boundingBox.minX,
                     this.boundingBox.minY,
                     this.boundingBox.minZ - 1,
                     EnumFacing.NORTH,
                     this.getComponentType()
                  );
                  break;
               case EAST:
                  StructureVillagePieces.addVillagePath(
                     (StructureVillagePieces.Start)componentIn,
                     listIn,
                     rand,
                     this.boundingBox.maxX - 2,
                     this.boundingBox.minY,
                     this.boundingBox.minZ - 1,
                     EnumFacing.NORTH,
                     this.getComponentType()
                  );
            }
         }

         if (conditionMet && rand.nextInt(3) > 0 && this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case NORTH:
                  StructureVillagePieces.addVillagePath(
                     (StructureVillagePieces.Start)componentIn,
                     listIn,
                     rand,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY,
                     this.boundingBox.minZ,
                     EnumFacing.EAST,
                     this.getComponentType()
                  );
                  break;
               case SOUTH:
                  StructureVillagePieces.addVillagePath(
                     (StructureVillagePieces.Start)componentIn,
                     listIn,
                     rand,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY,
                     this.boundingBox.maxZ - 2,
                     EnumFacing.EAST,
                     this.getComponentType()
                  );
                  break;
               case WEST:
                  StructureVillagePieces.addVillagePath(
                     (StructureVillagePieces.Start)componentIn,
                     listIn,
                     rand,
                     this.boundingBox.minX,
                     this.boundingBox.minY,
                     this.boundingBox.maxZ + 1,
                     EnumFacing.SOUTH,
                     this.getComponentType()
                  );
                  break;
               case EAST:
                  StructureVillagePieces.addVillagePath(
                     (StructureVillagePieces.Start)componentIn,
                     listIn,
                     rand,
                     this.boundingBox.maxX - 2,
                     this.boundingBox.minY,
                     this.boundingBox.maxZ + 1,
                     EnumFacing.SOUTH,
                     this.getComponentType()
                  );
            }
         }
      }

      public Path(StructureVillagePieces.Start start, int i, Random rand, StructureBoundingBox structureBoundingBox2, EnumFacing facing) {
         super(start, i);
         this.coordBaseMode = facing;
         this.boundingBox = structureBoundingBox2;
         this.length = Math.max(structureBoundingBox2.getXSize(), structureBoundingBox2.getZSize());
      }

      public static StructureBoundingBox createBoundingBox(
         StructureVillagePieces.Start start, List<StructureComponent> components, Random rand, int x, int y, int z, EnumFacing facing
      ) {
         for (int index = 7 * MathHelper.getRandomIntegerInRange(rand, 3, 5); index >= 7; index -= 7) {
            StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(x, y, z, 0, 0, 3, 3, index, facing);
            if (StructureComponent.findIntersecting(components, structureBoundingBox) == null) {
               return structureBoundingBox;
            }
         }

         return null;
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setInteger("Length", this.length);
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.length = tagCompound.getInteger("Length");
      }

      public Path() {
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         IBlockState biomeBlockState = this.getBiomeBlockState(Blocks.gravel.getDefaultState());
         IBlockState biomeBlockState2 = this.getBiomeBlockState(Blocks.cobblestone.getDefaultState());

         for (int minX2 = this.boundingBox.minX; minX2 <= this.boundingBox.maxX; minX2++) {
            for (int minZ2 = this.boundingBox.minZ; minZ2 <= this.boundingBox.maxZ; minZ2++) {
               BlockPos blockPos = new BlockPos(minX2, 64, minZ2);
               if (structureBoundingBoxIn.isVecInside(blockPos)) {
                  blockPos = worldIn.getTopSolidOrLiquidBlock(blockPos).down();
                  worldIn.setBlockState(blockPos, biomeBlockState, 2);
                  worldIn.setBlockState(blockPos.down(), biomeBlockState2, 2);
               }
            }
         }

         return true;
      }
   }

   public static class PieceWeight {
      public int villagePiecesSpawned;
      public Class<? extends StructureVillagePieces.Village> villagePieceClass;
      public final int villagePieceWeight;
      public int villagePiecesLimit;

      public PieceWeight(Class<? extends StructureVillagePieces.Village> clazz, int i, int i2) {
         this.villagePieceClass = clazz;
         this.villagePieceWeight = i;
         this.villagePiecesLimit = i2;
      }

      public boolean canSpawnMoreVillagePieces() {
         return this.villagePiecesLimit == 0 || this.villagePiecesSpawned < this.villagePiecesLimit;
      }

      public boolean isActive() {
         return this.villagePiecesLimit == 0 || this.villagePiecesSpawned < this.villagePiecesLimit;
      }
   }

   public abstract static class Road extends StructureVillagePieces.Village {
      public Road() {
      }

      protected Road(StructureVillagePieces.Start start, int type) {
         super(start, type);
      }
   }

   public static class Start extends StructureVillagePieces.Well {
      public int terrainType;
      public List<StructureVillagePieces.PieceWeight> structureVillageWeightedPieceList;
      public List<StructureComponent> villageComponents = Lists.newArrayList();
      public StructureVillagePieces.PieceWeight structVillagePieceWeight;
      public boolean inDesert;
      public WorldChunkManager worldChunkMngr;
      public List<StructureComponent> villageRoads = Lists.newArrayList();

      public WorldChunkManager getWorldChunkManager() {
         return this.worldChunkMngr;
      }

      public Start() {
      }

      public Start(WorldChunkManager worldChunkManager, Random random2, int i, int i2, List list2, int i3) {
         super((StructureVillagePieces.Start)null, 0, random2, i, i2);
         this.worldChunkMngr = worldChunkManager;
         this.structureVillageWeightedPieceList = list2;
         this.terrainType = i3;
         BiomeGenBase biomeGenerator = worldChunkManager.getBiomeGenerator(new BlockPos(i, 0, i2), BiomeGenBase.DEFAULT_BIOME);
         this.inDesert = biomeGenerator == BiomeGenBase.desert || biomeGenerator == BiomeGenBase.desertHills;
         this.setDesertMaterials(this.inDesert);
      }
   }

   public static class Torch extends StructureVillagePieces.Village {
      public static StructureBoundingBox createBoundingBox(
         StructureVillagePieces.Start start, List<StructureComponent> components, Random rand, int x, int y, int z, EnumFacing facing
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(x, y, z, 0, 0, 3, 4, 2, facing);
         return StructureComponent.findIntersecting(components, structureBoundingBox) != null ? null : structureBoundingBox;
      }

      public Torch(StructureVillagePieces.Start start, int i, Random rand, StructureBoundingBox structureBoundingBox2, EnumFacing facing) {
         super(start, i);
         this.coordBaseMode = facing;
         this.boundingBox = structureBoundingBox2;
      }

      public Torch() {
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.averageGroundLevel < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 4 - 1, 0);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 2, 3, 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 1, 0, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 1, 1, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 1, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.wool.getStateFromMeta(EnumDyeColor.WHITE.getDyeDamage()), 1, 3, 0, structureBoundingBoxIn);
         boolean flag = this.coordBaseMode == EnumFacing.EAST || this.coordBaseMode == EnumFacing.NORTH;
         this.setBlockState(
            worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode.rotateY()), flag ? 2 : 0, 3, 0, structureBoundingBoxIn
         );
         this.setBlockState(worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode), 1, 3, 1, structureBoundingBoxIn);
         this.setBlockState(
            worldIn,
            Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode.rotateYCCW()),
            flag ? 0 : 2,
            3,
            0,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode.getOpposite()), 1, 3, -1, structureBoundingBoxIn
         );
         return true;
      }
   }

   abstract static class Village extends StructureComponent {
      protected int averageGroundLevel = -1;
      private boolean isDesertVillage;
      private int villagersSpawned;

      protected Village(StructureVillagePieces.Start start, int type) {
         super(type);
         if (start != null) {
            this.isDesertVillage = start.inDesert;
         }
      }

      @Override
      protected void replaceAirAndLiquidDownwards(World worldIn, IBlockState blockstateIn, int x, int y, int z, StructureBoundingBox boundingboxIn) {
         IBlockState blockState = this.getBiomeBlockState(blockstateIn);
         super.replaceAirAndLiquidDownwards(worldIn, blockState, x, -1, z, boundingboxIn);
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
         this.averageGroundLevel = tagCompound.getInteger("HPos");
         this.villagersSpawned = tagCompound.getInteger("VCount");
         this.isDesertVillage = tagCompound.getBoolean("Desert");
      }

      public Village() {
      }

      protected int getVillagerType(int population, int profession) {
         return profession;
      }

      protected IBlockState getBiomeBlockState(IBlockState state) {
         if (this.isDesertVillage) {
            if (state.getBlock() == Blocks.log || state.getBlock() == Blocks.log2) {
               return Blocks.sandstone.getDefaultState();
            }

            if (state.getBlock() == Blocks.cobblestone) {
               return Blocks.sandstone.getStateFromMeta(EnumType.DEFAULT.getMetadata());
            }

            if (state.getBlock() == Blocks.planks) {
               return Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata());
            }

            if (state.getBlock() == Blocks.oak_stairs) {
               return Blocks.sandstone_stairs.getDefaultState().withProperty(BlockStairs.FACING, state.getValue(BlockStairs.FACING));
            }

            if (state.getBlock() == Blocks.stone_stairs) {
               return Blocks.sandstone_stairs.getDefaultState().withProperty(BlockStairs.FACING, state.getValue(BlockStairs.FACING));
            }

            if (state.getBlock() == Blocks.gravel) {
               return Blocks.sandstone.getDefaultState();
            }
         }

         return state;
      }

      protected static boolean canVillageGoDeeper(StructureBoundingBox boundingBox) {
         return boundingBox != null && boundingBox.minY > 10;
      }

      @Override
      protected void fillWithBlocks(
         World worldIn,
         StructureBoundingBox boundingboxIn,
         int xMin,
         int yMin,
         int zMin,
         int xMax,
         int yMax,
         int zMax,
         IBlockState boundaryBlockState,
         IBlockState insideBlockState,
         boolean existingOnly
      ) {
         IBlockState blockState = this.getBiomeBlockState(boundaryBlockState);
         IBlockState blockState2 = this.getBiomeBlockState(insideBlockState);
         super.fillWithBlocks(worldIn, boundingboxIn, xMin, yMin, zMin, xMax, yMax, zMax, blockState, blockState2, false);
      }

      protected void handleWorld(World world2, StructureBoundingBox structureBoundingBox2, int i, int i2, int i3) {
         if (this.villagersSpawned < i3) {
            for (int villagersSpawned2 = this.villagersSpawned; villagersSpawned2 < i3; villagersSpawned2++) {
               int xWithOffset = this.getXWithOffset(i + villagersSpawned2, i2);
               int yWithOffset = this.getYWithOffset(1);
               int zWithOffset = this.getZWithOffset(i + villagersSpawned2, i2);
               if (!structureBoundingBox2.isVecInside(new BlockPos(xWithOffset, yWithOffset, zWithOffset))) {
                  break;
               }

               this.villagersSpawned++;
               EntityVillager entityVillager = new EntityVillager(world2);
               entityVillager.setLocationAndAngles(xWithOffset + 0.5, yWithOffset, zWithOffset + 0.5, 0.0F, 0.0F);
               entityVillager.onInitialSpawn(world2.getDifficultyForLocation(new BlockPos(entityVillager)), (IEntityLivingData)null);
               entityVillager.setProfession(this.getVillagerType(villagersSpawned2, entityVillager.getProfession()));
               world2.spawnEntityInWorld(entityVillager);
            }
         }
      }

      protected void setDesertMaterials(boolean isDesert) {
         this.isDesertVillage = isDesert;
      }

      @Override
      protected void setBlockState(World worldIn, IBlockState blockstateIn, int x, int y, int z, StructureBoundingBox boundingboxIn) {
         IBlockState blockState = this.getBiomeBlockState(blockstateIn);
         super.setBlockState(worldIn, blockState, x, y, z, boundingboxIn);
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         tagCompound.setInteger("HPos", this.averageGroundLevel);
         tagCompound.setInteger("VCount", this.villagersSpawned);
         tagCompound.setBoolean("Desert", this.isDesertVillage);
      }

      protected StructureComponent getStructureComponent2(StructureVillagePieces.Start start2, List list2, Random random2, int i) {
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case NORTH:
                  return StructureVillagePieces.addVillageStructure(
                     start2,
                     list2,
                     random2,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY + 0,
                     this.boundingBox.minZ + i,
                     EnumFacing.EAST,
                     this.getComponentType()
                  );
               case SOUTH:
                  return StructureVillagePieces.addVillageStructure(
                     start2,
                     list2,
                     random2,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY + 0,
                     this.boundingBox.minZ + i,
                     EnumFacing.EAST,
                     this.getComponentType()
                  );
               case WEST:
                  return StructureVillagePieces.addVillageStructure(
                     start2,
                     list2,
                     random2,
                     this.boundingBox.minX + i,
                     this.boundingBox.minY + 0,
                     this.boundingBox.maxZ + 1,
                     EnumFacing.SOUTH,
                     this.getComponentType()
                  );
               case EAST:
                  return StructureVillagePieces.addVillageStructure(
                     start2,
                     list2,
                     random2,
                     this.boundingBox.minX + i,
                     this.boundingBox.minY + 0,
                     this.boundingBox.maxZ + 1,
                     EnumFacing.SOUTH,
                     this.getComponentType()
                  );
            }
         }

         return null;
      }

      protected StructureComponent getStructureComponent(StructureVillagePieces.Start start2, List list2, Random random2, int i) {
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case NORTH:
                  return StructureVillagePieces.addVillageStructure(
                     start2,
                     list2,
                     random2,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY + 0,
                     this.boundingBox.minZ + i,
                     EnumFacing.WEST,
                     this.getComponentType()
                  );
               case SOUTH:
                  return StructureVillagePieces.addVillageStructure(
                     start2,
                     list2,
                     random2,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY + 0,
                     this.boundingBox.minZ + i,
                     EnumFacing.WEST,
                     this.getComponentType()
                  );
               case WEST:
                  return StructureVillagePieces.addVillageStructure(
                     start2,
                     list2,
                     random2,
                     this.boundingBox.minX + i,
                     this.boundingBox.minY + 0,
                     this.boundingBox.minZ - 1,
                     EnumFacing.NORTH,
                     this.getComponentType()
                  );
               case EAST:
                  return StructureVillagePieces.addVillageStructure(
                     start2,
                     list2,
                     random2,
                     this.boundingBox.minX + i,
                     this.boundingBox.minY + 0,
                     this.boundingBox.minZ - 1,
                     EnumFacing.NORTH,
                     this.getComponentType()
                  );
            }
         }

         return null;
      }

      protected int getAverageGroundLevel(World worldIn, StructureBoundingBox boundingBox) {
         int i = 0;
         int i2 = 0;
         MutableBlockPos mutableBlockPos = new MutableBlockPos();

         for (int nestedIndex = this.boundingBox.minZ; nestedIndex <= this.boundingBox.maxZ; nestedIndex++) {
            for (int outerIndex = this.boundingBox.minX; outerIndex <= this.boundingBox.maxX; outerIndex++) {
               mutableBlockPos.set(outerIndex, 64, nestedIndex);
               if (boundingBox.isVecInside(mutableBlockPos)) {
                  i += Math.max(worldIn.getTopSolidOrLiquidBlock(mutableBlockPos).getY(), worldIn.provider.getAverageGroundLevel());
                  i2++;
               }
            }
         }

         return i2 == 0 ? -1 : i / i2;
      }
   }

   public static class VillageChild extends StructureVillagePieces.Village {
      @Override
      protected int getVillagerType(int i, int i2) {
         return i == 0 ? 4 : super.getVillagerType(i, i2);
      }

      public VillageChild(StructureVillagePieces.Start start2, int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(start2, i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox2;
      }

      public static StructureVillagePieces.VillageChild createVillageChild(
         StructureVillagePieces.Start start2, List list2, Random random2, int i, int i2, int i3, EnumFacing enumFacing, int i4
      ) {
         StructureBoundingBox structureBoundingBox2 = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, 0, 0, 9, 7, 11, enumFacing);
         return canVillageGoDeeper(structureBoundingBox2) && StructureComponent.findIntersecting(list2, structureBoundingBox2) == null
            ? new StructureVillagePieces.VillageChild(start2, i4, random2, structureBoundingBox2, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World world2, Random random2, StructureBoundingBox structureBoundingBox2) {
         if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(world2, structureBoundingBox2);
            if (this.averageGroundLevel < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 7 - 1, 0);
         }

         this.fillWithBlocks(world2, structureBoundingBox2, 1, 1, 1, 7, 4, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 2, 1, 6, 8, 4, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 2, 0, 6, 8, 0, 10, Blocks.dirt.getDefaultState(), Blocks.dirt.getDefaultState(), false);
         this.setBlockState(world2, Blocks.cobblestone.getDefaultState(), 6, 0, 6, structureBoundingBox2);
         this.fillWithBlocks(world2, structureBoundingBox2, 2, 1, 6, 2, 1, 10, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 8, 1, 6, 8, 1, 10, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 3, 1, 10, 7, 1, 10, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 1, 0, 1, 7, 0, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 0, 0, 0, 3, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 8, 0, 0, 8, 3, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 1, 0, 0, 7, 1, 0, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 1, 0, 5, 7, 1, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 1, 2, 0, 7, 3, 0, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 1, 2, 5, 7, 3, 5, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 4, 1, 8, 4, 1, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 4, 4, 8, 4, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 5, 2, 8, 5, 3, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.setBlockState(world2, Blocks.planks.getDefaultState(), 0, 4, 2, structureBoundingBox2);
         this.setBlockState(world2, Blocks.planks.getDefaultState(), 0, 4, 3, structureBoundingBox2);
         this.setBlockState(world2, Blocks.planks.getDefaultState(), 8, 4, 2, structureBoundingBox2);
         this.setBlockState(world2, Blocks.planks.getDefaultState(), 8, 4, 3, structureBoundingBox2);
         int metadataWithOffset = this.getMetadataWithOffset(Blocks.oak_stairs, 3);
         int metadataWithOffset2 = this.getMetadataWithOffset(Blocks.oak_stairs, 2);

         for (int i = -1; i <= 2; i++) {
            for (int i2 = 0; i2 <= 8; i2++) {
               this.setBlockState(world2, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset), i2, 4 + i, i, structureBoundingBox2);
               this.setBlockState(world2, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset2), i2, 4 + i, 5 - i, structureBoundingBox2);
            }
         }

         this.setBlockState(world2, Blocks.log.getDefaultState(), 0, 2, 1, structureBoundingBox2);
         this.setBlockState(world2, Blocks.log.getDefaultState(), 0, 2, 4, structureBoundingBox2);
         this.setBlockState(world2, Blocks.log.getDefaultState(), 8, 2, 1, structureBoundingBox2);
         this.setBlockState(world2, Blocks.log.getDefaultState(), 8, 2, 4, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 0, 2, 2, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 0, 2, 3, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 8, 2, 2, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 8, 2, 3, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 2, 2, 5, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 3, 2, 5, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 5, 2, 0, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 6, 2, 5, structureBoundingBox2);
         this.setBlockState(world2, Blocks.oak_fence.getDefaultState(), 2, 1, 3, structureBoundingBox2);
         this.setBlockState(world2, Blocks.wooden_pressure_plate.getDefaultState(), 2, 2, 3, structureBoundingBox2);
         this.setBlockState(world2, Blocks.planks.getDefaultState(), 1, 1, 4, structureBoundingBox2);
         this.setBlockState(world2, Blocks.oak_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.oak_stairs, 3)), 2, 1, 4, structureBoundingBox2);
         this.setBlockState(world2, Blocks.oak_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.oak_stairs, 1)), 1, 1, 3, structureBoundingBox2);
         this.fillWithBlocks(world2, structureBoundingBox2, 5, 0, 1, 7, 0, 3, Blocks.double_stone_slab.getDefaultState(), Blocks.double_stone_slab.getDefaultState(), false);
         this.setBlockState(world2, Blocks.double_stone_slab.getDefaultState(), 6, 1, 1, structureBoundingBox2);
         this.setBlockState(world2, Blocks.double_stone_slab.getDefaultState(), 6, 1, 2, structureBoundingBox2);
         this.setBlockState(world2, Blocks.air.getDefaultState(), 2, 1, 0, structureBoundingBox2);
         this.setBlockState(world2, Blocks.air.getDefaultState(), 2, 2, 0, structureBoundingBox2);
         this.setBlockState(world2, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode), 2, 3, 1, structureBoundingBox2);
         this.handleWorld2(world2, structureBoundingBox2, random2, 2, 0, EnumFacing.getHorizontal(this.getMetadataWithOffset(Blocks.oak_door, 1)));
         if (this.getBlockStateFromPos(world2, 2, 0, -1, structureBoundingBox2).getBlock().getMaterial() == Material.air
            && this.getBlockStateFromPos(world2, 2, -1, -1, structureBoundingBox2).getBlock().getMaterial() != Material.air) {
            this.setBlockState(world2, Blocks.stone_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_stairs, 3)), 2, 0, -1, structureBoundingBox2);
         }

         this.setBlockState(world2, Blocks.air.getDefaultState(), 6, 1, 5, structureBoundingBox2);
         this.setBlockState(world2, Blocks.air.getDefaultState(), 6, 2, 5, structureBoundingBox2);
         this.setBlockState(world2, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode.getOpposite()), 6, 3, 4, structureBoundingBox2);
         this.handleWorld2(world2, structureBoundingBox2, random2, 6, 5, EnumFacing.getHorizontal(this.getMetadataWithOffset(Blocks.oak_door, 1)));

         for (int i3 = 0; i3 < 5; i3++) {
            for (int i4 = 0; i4 < 9; i4++) {
               this.clearCurrentPositionBlocksUpwards(world2, i4, 7, i3, structureBoundingBox2);
               this.replaceAirAndLiquidDownwards(world2, Blocks.cobblestone.getDefaultState(), i4, -1, i3, structureBoundingBox2);
            }
         }

         this.handleWorld(world2, structureBoundingBox2, 4, 2, 2);
         return true;
      }

      public VillageChild() {
      }
   }

   public static class VillageChild2 extends StructureVillagePieces.Village {
      @Override
      protected int getVillagerType(int i, int i2) {
         return 2;
      }

      public VillageChild2(StructureVillagePieces.Start start2, int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(start2, i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox2;
      }

      public VillageChild2() {
      }

      public static StructureVillagePieces.VillageChild2 createVillageChild2(
         StructureVillagePieces.Start start2, List list2, Random random2, int i, int i2, int i3, EnumFacing enumFacing, int i4
      ) {
         StructureBoundingBox structureBoundingBox2 = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, 0, 0, 5, 12, 9, enumFacing);
         return canVillageGoDeeper(structureBoundingBox2) && StructureComponent.findIntersecting(list2, structureBoundingBox2) == null
            ? new StructureVillagePieces.VillageChild2(start2, i4, random2, structureBoundingBox2, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World world2, Random random2, StructureBoundingBox structureBoundingBox2) {
         if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(world2, structureBoundingBox2);
            if (this.averageGroundLevel < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 12 - 1, 0);
         }

         this.fillWithBlocks(world2, structureBoundingBox2, 1, 1, 1, 3, 3, 7, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 1, 5, 1, 3, 9, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 1, 0, 0, 3, 0, 8, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 1, 1, 0, 3, 10, 0, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 1, 1, 0, 10, 3, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 4, 1, 1, 4, 10, 3, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 0, 4, 0, 4, 7, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 4, 0, 4, 4, 4, 7, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 1, 1, 8, 3, 4, 8, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 1, 5, 4, 3, 10, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 1, 5, 5, 3, 5, 7, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 9, 0, 4, 9, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 4, 0, 4, 4, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.setBlockState(world2, Blocks.cobblestone.getDefaultState(), 0, 11, 2, structureBoundingBox2);
         this.setBlockState(world2, Blocks.cobblestone.getDefaultState(), 4, 11, 2, structureBoundingBox2);
         this.setBlockState(world2, Blocks.cobblestone.getDefaultState(), 2, 11, 0, structureBoundingBox2);
         this.setBlockState(world2, Blocks.cobblestone.getDefaultState(), 2, 11, 4, structureBoundingBox2);
         this.setBlockState(world2, Blocks.cobblestone.getDefaultState(), 1, 1, 6, structureBoundingBox2);
         this.setBlockState(world2, Blocks.cobblestone.getDefaultState(), 1, 1, 7, structureBoundingBox2);
         this.setBlockState(world2, Blocks.cobblestone.getDefaultState(), 2, 1, 7, structureBoundingBox2);
         this.setBlockState(world2, Blocks.cobblestone.getDefaultState(), 3, 1, 6, structureBoundingBox2);
         this.setBlockState(world2, Blocks.cobblestone.getDefaultState(), 3, 1, 7, structureBoundingBox2);
         this.setBlockState(world2, Blocks.stone_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_stairs, 3)), 1, 1, 5, structureBoundingBox2);
         this.setBlockState(world2, Blocks.stone_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_stairs, 3)), 2, 1, 6, structureBoundingBox2);
         this.setBlockState(world2, Blocks.stone_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_stairs, 3)), 3, 1, 5, structureBoundingBox2);
         this.setBlockState(world2, Blocks.stone_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_stairs, 1)), 1, 2, 7, structureBoundingBox2);
         this.setBlockState(world2, Blocks.stone_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_stairs, 0)), 3, 2, 7, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 0, 2, 2, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 0, 3, 2, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 4, 2, 2, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 4, 3, 2, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 0, 6, 2, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 0, 7, 2, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 4, 6, 2, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 4, 7, 2, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 2, 6, 0, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 2, 7, 0, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 2, 6, 4, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 2, 7, 4, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 0, 3, 6, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 4, 3, 6, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 2, 3, 8, structureBoundingBox2);
         this.setBlockState(world2, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode.getOpposite()), 2, 4, 7, structureBoundingBox2);
         this.setBlockState(world2, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode.rotateY()), 1, 4, 6, structureBoundingBox2);
         this.setBlockState(world2, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode.rotateYCCW()), 3, 4, 6, structureBoundingBox2);
         this.setBlockState(world2, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode), 2, 4, 5, structureBoundingBox2);
         int metadataWithOffset = this.getMetadataWithOffset(Blocks.ladder, 4);

         for (int i = 1; i <= 9; i++) {
            this.setBlockState(world2, Blocks.ladder.getStateFromMeta(metadataWithOffset), 3, i, 3, structureBoundingBox2);
         }

         this.setBlockState(world2, Blocks.air.getDefaultState(), 2, 1, 0, structureBoundingBox2);
         this.setBlockState(world2, Blocks.air.getDefaultState(), 2, 2, 0, structureBoundingBox2);
         this.handleWorld2(world2, structureBoundingBox2, random2, 2, 0, EnumFacing.getHorizontal(this.getMetadataWithOffset(Blocks.oak_door, 1)));
         if (this.getBlockStateFromPos(world2, 2, 0, -1, structureBoundingBox2).getBlock().getMaterial() == Material.air
            && this.getBlockStateFromPos(world2, 2, -1, -1, structureBoundingBox2).getBlock().getMaterial() != Material.air) {
            this.setBlockState(world2, Blocks.stone_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_stairs, 3)), 2, 0, -1, structureBoundingBox2);
         }

         for (int i2 = 0; i2 < 9; i2++) {
            for (int i3 = 0; i3 < 5; i3++) {
               this.clearCurrentPositionBlocksUpwards(world2, i3, 12, i2, structureBoundingBox2);
               this.replaceAirAndLiquidDownwards(world2, Blocks.cobblestone.getDefaultState(), i3, -1, i2, structureBoundingBox2);
            }
         }

         this.handleWorld(world2, structureBoundingBox2, 2, 2, 1);
         return true;
      }
   }

   public static class VillageChild3 extends StructureVillagePieces.Village {
      @Override
      public boolean addComponentParts(World world2, Random random2, StructureBoundingBox structureBoundingBox2) {
         if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(world2, structureBoundingBox2);
            if (this.averageGroundLevel < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 9 - 1, 0);
         }

         this.fillWithBlocks(world2, structureBoundingBox2, 1, 1, 1, 7, 5, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 0, 0, 8, 0, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 5, 0, 8, 5, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 6, 1, 8, 6, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 7, 2, 8, 7, 3, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         int metadataWithOffset = this.getMetadataWithOffset(Blocks.oak_stairs, 3);
         int metadataWithOffset2 = this.getMetadataWithOffset(Blocks.oak_stairs, 2);

         for (int i = -1; i <= 2; i++) {
            for (int i2 = 0; i2 <= 8; i2++) {
               this.setBlockState(world2, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset), i2, 6 + i, i, structureBoundingBox2);
               this.setBlockState(world2, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset2), i2, 6 + i, 5 - i, structureBoundingBox2);
            }
         }

         this.fillWithBlocks(world2, structureBoundingBox2, 0, 1, 0, 0, 1, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 1, 1, 5, 8, 1, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 8, 1, 0, 8, 1, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 2, 1, 0, 7, 1, 0, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 2, 0, 0, 4, 0, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 2, 5, 0, 4, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 8, 2, 5, 8, 4, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 8, 2, 0, 8, 4, 0, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 2, 1, 0, 4, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 1, 2, 5, 7, 4, 5, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 8, 2, 1, 8, 4, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 1, 2, 0, 7, 4, 0, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 4, 2, 0, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 5, 2, 0, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 6, 2, 0, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 4, 3, 0, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 5, 3, 0, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 6, 3, 0, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 0, 2, 2, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 0, 2, 3, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 0, 3, 2, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 0, 3, 3, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 8, 2, 2, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 8, 2, 3, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 8, 3, 2, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 8, 3, 3, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 2, 2, 5, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 3, 2, 5, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 5, 2, 5, structureBoundingBox2);
         this.setBlockState(world2, Blocks.glass_pane.getDefaultState(), 6, 2, 5, structureBoundingBox2);
         this.fillWithBlocks(world2, structureBoundingBox2, 1, 4, 1, 7, 4, 1, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 1, 4, 4, 7, 4, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 1, 3, 4, 7, 3, 4, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false);
         this.setBlockState(world2, Blocks.planks.getDefaultState(), 7, 1, 4, structureBoundingBox2);
         this.setBlockState(world2, Blocks.oak_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.oak_stairs, 0)), 7, 1, 3, structureBoundingBox2);
         int metadataWithOffset3 = this.getMetadataWithOffset(Blocks.oak_stairs, 3);
         this.setBlockState(world2, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset3), 6, 1, 4, structureBoundingBox2);
         this.setBlockState(world2, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset3), 5, 1, 4, structureBoundingBox2);
         this.setBlockState(world2, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset3), 4, 1, 4, structureBoundingBox2);
         this.setBlockState(world2, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset3), 3, 1, 4, structureBoundingBox2);
         this.setBlockState(world2, Blocks.oak_fence.getDefaultState(), 6, 1, 3, structureBoundingBox2);
         this.setBlockState(world2, Blocks.wooden_pressure_plate.getDefaultState(), 6, 2, 3, structureBoundingBox2);
         this.setBlockState(world2, Blocks.oak_fence.getDefaultState(), 4, 1, 3, structureBoundingBox2);
         this.setBlockState(world2, Blocks.wooden_pressure_plate.getDefaultState(), 4, 2, 3, structureBoundingBox2);
         this.setBlockState(world2, Blocks.crafting_table.getDefaultState(), 7, 1, 1, structureBoundingBox2);
         this.setBlockState(world2, Blocks.air.getDefaultState(), 1, 1, 0, structureBoundingBox2);
         this.setBlockState(world2, Blocks.air.getDefaultState(), 1, 2, 0, structureBoundingBox2);
         this.handleWorld2(world2, structureBoundingBox2, random2, 1, 0, EnumFacing.getHorizontal(this.getMetadataWithOffset(Blocks.oak_door, 1)));
         if (this.getBlockStateFromPos(world2, 1, 0, -1, structureBoundingBox2).getBlock().getMaterial() == Material.air
            && this.getBlockStateFromPos(world2, 1, -1, -1, structureBoundingBox2).getBlock().getMaterial() != Material.air) {
            this.setBlockState(world2, Blocks.stone_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_stairs, 3)), 1, 0, -1, structureBoundingBox2);
         }

         for (int i3 = 0; i3 < 6; i3++) {
            for (int i4 = 0; i4 < 9; i4++) {
               this.clearCurrentPositionBlocksUpwards(world2, i4, 9, i3, structureBoundingBox2);
               this.replaceAirAndLiquidDownwards(world2, Blocks.cobblestone.getDefaultState(), i4, -1, i3, structureBoundingBox2);
            }
         }

         this.handleWorld(world2, structureBoundingBox2, 2, 2, 1);
         return true;
      }

      public VillageChild3(StructureVillagePieces.Start start2, int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(start2, i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox2;
      }

      public VillageChild3() {
      }

      public static StructureVillagePieces.VillageChild3 createVillageChild3(
         StructureVillagePieces.Start start2, List list2, Random random2, int i, int i2, int i3, EnumFacing enumFacing, int i4
      ) {
         StructureBoundingBox structureBoundingBox2 = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, 0, 0, 9, 9, 6, enumFacing);
         return canVillageGoDeeper(structureBoundingBox2) && StructureComponent.findIntersecting(list2, structureBoundingBox2) == null
            ? new StructureVillagePieces.VillageChild3(start2, i4, random2, structureBoundingBox2, enumFacing)
            : null;
      }

      @Override
      protected int getVillagerType(int i, int i2) {
         return 1;
      }
   }

   public static class Well extends StructureVillagePieces.Village {
      public Well() {
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.averageGroundLevel < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 3, 0);
         }

         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 0, 1, 4, 12, 4, Blocks.cobblestone.getDefaultState(), Blocks.flowing_water.getDefaultState(), false
         );
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 2, 12, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 3, 12, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 2, 12, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 3, 12, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 1, 13, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 1, 14, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 4, 13, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 4, 14, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 1, 13, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 1, 14, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 4, 13, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 4, 14, 4, structureBoundingBoxIn);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 15, 1, 4, 15, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );

         for (int index = 0; index <= 5; index++) {
            for (int innerIndex = 0; innerIndex <= 5; innerIndex++) {
               if (innerIndex == 0 || innerIndex == 5 || index == 0 || index == 5) {
                  this.setBlockState(worldIn, Blocks.gravel.getDefaultState(), innerIndex, 11, index, structureBoundingBoxIn);
                  this.clearCurrentPositionBlocksUpwards(worldIn, innerIndex, 12, index, structureBoundingBoxIn);
               }
            }
         }

         return true;
      }

      public Well(StructureVillagePieces.Start start, int i, Random rand, int i2, int i3) {
         super(start, i);
         this.coordBaseMode = Plane.HORIZONTAL.random(rand);
         switch (this.coordBaseMode) {
            case NORTH:
            case SOUTH:
               this.boundingBox = new StructureBoundingBox(i2, 64, i3, i2 + 6 - 1, 78, i3 + 6 - 1);
               break;
            default:
               this.boundingBox = new StructureBoundingBox(i2, 64, i3, i2 + 6 - 1, 78, i3 + 6 - 1);
         }
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         StructureVillagePieces.addVillagePath(
            (StructureVillagePieces.Start)componentIn,
            listIn,
            rand,
            this.boundingBox.minX - 1,
            this.boundingBox.maxY - 4,
            this.boundingBox.minZ + 1,
            EnumFacing.WEST,
            this.getComponentType()
         );
         StructureVillagePieces.addVillagePath(
            (StructureVillagePieces.Start)componentIn,
            listIn,
            rand,
            this.boundingBox.maxX + 1,
            this.boundingBox.maxY - 4,
            this.boundingBox.minZ + 1,
            EnumFacing.EAST,
            this.getComponentType()
         );
         StructureVillagePieces.addVillagePath(
            (StructureVillagePieces.Start)componentIn,
            listIn,
            rand,
            this.boundingBox.minX + 1,
            this.boundingBox.maxY - 4,
            this.boundingBox.minZ - 1,
            EnumFacing.NORTH,
            this.getComponentType()
         );
         StructureVillagePieces.addVillagePath(
            (StructureVillagePieces.Start)componentIn,
            listIn,
            rand,
            this.boundingBox.minX + 1,
            this.boundingBox.maxY - 4,
            this.boundingBox.maxZ + 1,
            EnumFacing.SOUTH,
            this.getComponentType()
         );
      }
   }

   public static class WoodHut extends StructureVillagePieces.Village {
      private boolean isTallHouse;
      private int tablePosition;

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setInteger("T", this.tablePosition);
         tagCompound.setBoolean("C", this.isTallHouse);
      }

      public static StructureVillagePieces.WoodHut createComponent(
         StructureVillagePieces.Start start, List<StructureComponent> components, Random rand, int x, int y, int z, EnumFacing facing, int type
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(x, y, z, 0, 0, 4, 6, 5, facing);
         return canVillageGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(components, structureBoundingBox) == null
            ? new StructureVillagePieces.WoodHut(start, type, rand, structureBoundingBox, facing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.averageGroundLevel < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 6 - 1, 0);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 3, 5, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 3, 0, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 1, 2, 0, 3, Blocks.dirt.getDefaultState(), Blocks.dirt.getDefaultState(), false);
         if (this.isTallHouse) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 1, 2, 4, 3, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         } else {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 1, 2, 5, 3, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         }

         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 1, 4, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 2, 4, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 1, 4, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 2, 4, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 0, 4, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 0, 4, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 0, 4, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 3, 4, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 3, 4, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 3, 4, 3, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 3, 0, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 0, 3, 3, 0, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 4, 0, 3, 4, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 4, 3, 3, 4, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 3, 3, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 1, 3, 3, 3, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 2, 3, 0, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 4, 2, 3, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 3, 2, 2, structureBoundingBoxIn);
         if (this.tablePosition > 0) {
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), this.tablePosition, 1, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.wooden_pressure_plate.getDefaultState(), this.tablePosition, 2, 3, structureBoundingBoxIn);
         }

         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 1, 1, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 1, 2, 0, structureBoundingBoxIn);
         this.handleWorld2(worldIn, structureBoundingBoxIn, randomIn, 1, 0, EnumFacing.getHorizontal(this.getMetadataWithOffset(Blocks.oak_door, 1)));
         if (this.getBlockStateFromPos(worldIn, 1, 0, -1, structureBoundingBoxIn).getBlock().getMaterial() == Material.air
            && this.getBlockStateFromPos(worldIn, 1, -1, -1, structureBoundingBoxIn).getBlock().getMaterial() != Material.air) {
            this.setBlockState(
               worldIn, Blocks.stone_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_stairs, 3)), 1, 0, -1, structureBoundingBoxIn
            );
         }

         for (int index = 0; index < 5; index++) {
            for (int innerIndex = 0; innerIndex < 4; innerIndex++) {
               this.clearCurrentPositionBlocksUpwards(worldIn, innerIndex, 6, index, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.cobblestone.getDefaultState(), innerIndex, -1, index, structureBoundingBoxIn);
            }
         }

         this.handleWorld(worldIn, structureBoundingBoxIn, 1, 2, 1);
         return true;
      }

      public WoodHut(StructureVillagePieces.Start start, int i, Random rand, StructureBoundingBox structureBoundingBox2, EnumFacing facing) {
         super(start, i);
         this.coordBaseMode = facing;
         this.boundingBox = structureBoundingBox2;
         this.isTallHouse = rand.nextBoolean();
         this.tablePosition = rand.nextInt(3);
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.tablePosition = tagCompound.getInteger("T");
         this.isTallHouse = tagCompound.getBoolean("C");
      }

      public WoodHut() {
      }
   }
}
