package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockStoneSlab.EnumType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureComponent.BlockSelector;

public class StructureStrongholdPieces {
   private static Class<? extends StructureStrongholdPieces.Stronghold> strongComponentType;
   static int totalWeight;
   private static final StructureStrongholdPieces.PieceWeight[] pieceWeightArray = new StructureStrongholdPieces.PieceWeight[]{
      new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.Straight.class, 40, 0),
      new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.Prison.class, 5, 5),
      new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.LeftTurn.class, 20, 0),
      new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.RightTurn.class, 20, 0),
      new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.RoomCrossing.class, 10, 6),
      new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.StairsStraight.class, 5, 5),
      new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.Stairs.class, 5, 5),
      new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.Crossing.class, 5, 4),
      new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.ChestCorridor.class, 5, 4),
      new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.Library.class, 10, 2) {
         @Override
         public boolean canSpawnMoreStructuresOfType(int i) {
            return super.canSpawnMoreStructuresOfType(i) && i > 4;
         }
      },
      new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.PortalRoom.class, 20, 1) {
         @Override
         public boolean canSpawnMoreStructuresOfType(int i2) {
            return super.canSpawnMoreStructuresOfType(i2) && i2 > 5;
         }
      }
   };
   private static List<StructureStrongholdPieces.PieceWeight> structurePieceList;
   private static final StructureStrongholdPieces.Stones strongholdStones = new StructureStrongholdPieces.Stones();

   private static StructureComponent generateAndAddPiece(
      StructureStrongholdPieces.Stairs2 stairs2,
      List<StructureComponent> list,
      Random random2,
      int i,
      int i2,
      int i3,
      EnumFacing enumFacing,
      int i4
   ) {
      if (i4 > 50) {
         return null;
      }

      if (Math.abs(i - stairs2.getBoundingBox().minX) <= 112 && Math.abs(i3 - stairs2.getBoundingBox().minZ) <= 112) {
         StructureComponent structureComponent = generatePiece(stairs2, list, random2, i, i2, i3, enumFacing, i4 + 1);
         if (structureComponent != null) {
            list.add(structureComponent);
            stairs2.pendingChildren.add(structureComponent);
         }

         return structureComponent;
      } else {
         return null;
      }
   }

   public static void registerStrongholdPieces() {
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.ChestCorridor.class, "SHCC");
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.Corridor.class, "SHFC");
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.Crossing.class, "SH5C");
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.LeftTurn.class, "SHLT");
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.Library.class, "SHLi");
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.PortalRoom.class, "SHPR");
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.Prison.class, "SHPH");
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.RightTurn.class, "SHRT");
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.RoomCrossing.class, "SHRC");
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.Stairs.class, "SHSD");
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.Stairs2.class, "SHStart");
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.Straight.class, "SHS");
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.StairsStraight.class, "SHSSD");
   }

   private static StructureStrongholdPieces.Stronghold createStrongholdPiece(
      Class<? extends StructureStrongholdPieces.Stronghold> clazz,
      List<StructureComponent> list,
      Random random2,
      int i,
      int i2,
      int i3,
      EnumFacing enumFacing,
      int i4
   ) {
      StructureStrongholdPieces.Stronghold structurestrongholdpieces$stronghold = null;
      if (clazz == StructureStrongholdPieces.Straight.class) {
         structurestrongholdpieces$stronghold = StructureStrongholdPieces.Straight.createPiece(list, random2, i, i2, i3, enumFacing, i4);
      } else if (clazz == StructureStrongholdPieces.Prison.class) {
         structurestrongholdpieces$stronghold = StructureStrongholdPieces.Prison.createPiece(list, random2, i, i2, i3, enumFacing, i4);
      } else if (clazz == StructureStrongholdPieces.LeftTurn.class) {
         structurestrongholdpieces$stronghold = StructureStrongholdPieces.LeftTurn.createPiece(list, random2, i, i2, i3, enumFacing, i4);
      } else if (clazz == StructureStrongholdPieces.RightTurn.class) {
         structurestrongholdpieces$stronghold = StructureStrongholdPieces.RightTurn.createPiece(list, random2, i, i2, i3, enumFacing, i4);
      } else if (clazz == StructureStrongholdPieces.RoomCrossing.class) {
         structurestrongholdpieces$stronghold = StructureStrongholdPieces.RoomCrossing.createPiece(list, random2, i, i2, i3, enumFacing, i4);
      } else if (clazz == StructureStrongholdPieces.StairsStraight.class) {
         structurestrongholdpieces$stronghold = StructureStrongholdPieces.StairsStraight.createPiece(list, random2, i, i2, i3, enumFacing, i4);
      } else if (clazz == StructureStrongholdPieces.Stairs.class) {
         structurestrongholdpieces$stronghold = StructureStrongholdPieces.Stairs.createPiece(list, random2, i, i2, i3, enumFacing, i4);
      } else if (clazz == StructureStrongholdPieces.Crossing.class) {
         structurestrongholdpieces$stronghold = StructureStrongholdPieces.Crossing.createPiece(list, random2, i, i2, i3, enumFacing, i4);
      } else if (clazz == StructureStrongholdPieces.ChestCorridor.class) {
         structurestrongholdpieces$stronghold = StructureStrongholdPieces.ChestCorridor.createPiece(list, random2, i, i2, i3, enumFacing, i4);
      } else if (clazz == StructureStrongholdPieces.Library.class) {
         structurestrongholdpieces$stronghold = StructureStrongholdPieces.Library.createPiece(list, random2, i, i2, i3, enumFacing, i4);
      } else if (clazz == StructureStrongholdPieces.PortalRoom.class) {
         structurestrongholdpieces$stronghold = StructureStrongholdPieces.PortalRoom.createPiece(list, random2, i, i2, i3, enumFacing, i4);
      }

      return structurestrongholdpieces$stronghold;
   }

   public static void prepareStructurePieces() {
      structurePieceList = Lists.newArrayList();

      for (StructureStrongholdPieces.PieceWeight pieceWeight2 : pieceWeightArray) {
         pieceWeight2.instancesSpawned = 0;
         structurePieceList.add(pieceWeight2);
      }

      strongComponentType = null;
   }

   private static boolean canAddStructurePieces() {
      boolean flag = false;
      totalWeight = 0;

      for (StructureStrongholdPieces.PieceWeight pieceWeight2 : structurePieceList) {
         if (pieceWeight2.instancesLimit > 0 && pieceWeight2.instancesSpawned < pieceWeight2.instancesLimit) {
            flag = true;
         }

         totalWeight = totalWeight + pieceWeight2.pieceWeight;
      }

      return flag;
   }

   private static StructureStrongholdPieces.Stronghold generatePiece(
      StructureStrongholdPieces.Stairs2 stairs2,
      List<StructureComponent> list,
      Random random2,
      int i,
      int i2,
      int i3,
      EnumFacing enumFacing,
      int i4
   ) {
      if (!canAddStructurePieces()) {
         return null;
      }

      if (strongComponentType != null) {
         StructureStrongholdPieces.Stronghold strongholdPiece = createStrongholdPiece(strongComponentType, list, random2, i, i2, i3, enumFacing, i4);
         strongComponentType = null;
         if (strongholdPiece != null) {
            return strongholdPiece;
         }
      }

      int i5 = 0;

      while (i5 < 5) {
         i5++;
         int i6 = random2.nextInt(totalWeight);

         for (StructureStrongholdPieces.PieceWeight pieceWeight2 : structurePieceList) {
            i6 -= pieceWeight2.pieceWeight;
            if (i6 < 0) {
               if (!pieceWeight2.canSpawnMoreStructuresOfType(i4) || pieceWeight2 == stairs2.strongholdPieceWeight) {
                  break;
               }

               StructureStrongholdPieces.Stronghold strongholdPiece2 = createStrongholdPiece(pieceWeight2.pieceClass, list, random2, i, i2, i3, enumFacing, i4);
               if (strongholdPiece2 != null) {
                  pieceWeight2.instancesSpawned++;
                  stairs2.strongholdPieceWeight = pieceWeight2;
                  if (!pieceWeight2.canSpawnMoreStructures()) {
                     structurePieceList.remove(pieceWeight2);
                  }

                  return strongholdPiece2;
               }
            }
         }
      }

      StructureBoundingBox corridorBoundingBox = StructureStrongholdPieces.Corridor.findCorridorBoundingBox(list, random2, i, i2, i3, enumFacing);
      return corridorBoundingBox != null && corridorBoundingBox.minY > 1 ? new StructureStrongholdPieces.Corridor(i4, random2, corridorBoundingBox, enumFacing) : null;
   }

   public static class ChestCorridor extends StructureStrongholdPieces.Stronghold {
      private boolean hasMadeChest;
      private static final List<WeightedRandomChestContent> strongholdChestContents = Lists.newArrayList(
         new WeightedRandomChestContent[]{
            new WeightedRandomChestContent(Items.ender_pearl, 0, 1, 1, 10),
            new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 3),
            new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10),
            new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 5),
            new WeightedRandomChestContent(Items.redstone, 0, 4, 9, 5),
            new WeightedRandomChestContent(Items.bread, 0, 1, 3, 15),
            new WeightedRandomChestContent(Items.apple, 0, 1, 3, 15),
            new WeightedRandomChestContent(Items.iron_pickaxe, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.iron_sword, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.iron_chestplate, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.iron_helmet, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.iron_leggings, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.iron_boots, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.golden_apple, 0, 1, 1, 1),
            new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 1),
            new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1),
            new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 1),
            new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1)
         }
      );

      public ChestCorridor() {
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getStructureComponent3((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1);
      }

      public ChestCorridor(int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.entryDoor = this.getRandomDoor(random2);
         this.boundingBox = structureBoundingBox2;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 4, 6, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 1, 1, 0);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 6);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 2, 3, 1, 4, Blocks.stonebrick.getDefaultState(), Blocks.stonebrick.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(EnumType.SMOOTHBRICK.getMetadata()), 3, 1, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(EnumType.SMOOTHBRICK.getMetadata()), 3, 1, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(EnumType.SMOOTHBRICK.getMetadata()), 3, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(EnumType.SMOOTHBRICK.getMetadata()), 3, 2, 4, structureBoundingBoxIn);

         for (int index = 2; index <= 4; index++) {
            this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(EnumType.SMOOTHBRICK.getMetadata()), 2, 1, index, structureBoundingBoxIn);
         }

         if (!this.hasMadeChest
            && structureBoundingBoxIn.isVecInside(new BlockPos(this.getXWithOffset(3, 3), this.getYWithOffset(2), this.getZWithOffset(3, 3)))) {
            this.hasMadeChest = true;
            this.generateChestContents(
               worldIn,
               structureBoundingBoxIn,
               randomIn,
               3,
               2,
               3,
               WeightedRandomChestContent.copyWithAddition(strongholdChestContents, Items.enchanted_book.getRandom(randomIn)),
               2 + randomIn.nextInt(2)
            );
         }

         return true;
      }

      public static StructureStrongholdPieces.ChestCorridor createPiece(
         List<StructureComponent> list, Random random2, int i, int i2, int i3, EnumFacing enumFacing, int i4
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -1, -1, 5, 5, 7, enumFacing);
         return canStrongholdGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureStrongholdPieces.ChestCorridor(i4, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Chest", this.hasMadeChest);
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.hasMadeChest = tagCompound.getBoolean("Chest");
      }
   }

   public static class Corridor extends StructureStrongholdPieces.Stronghold {
      private int corridorLength;

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         for (int index = 0; index < this.corridorLength; index++) {
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 0, 0, index, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 1, 0, index, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 2, 0, index, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3, 0, index, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 4, 0, index, structureBoundingBoxIn);

            for (int innerIndex = 1; innerIndex <= 3; innerIndex++) {
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 0, innerIndex, index, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.air.getDefaultState(), 1, innerIndex, index, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.air.getDefaultState(), 2, innerIndex, index, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.air.getDefaultState(), 3, innerIndex, index, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 4, innerIndex, index, structureBoundingBoxIn);
            }

            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 0, 4, index, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 1, 4, index, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 2, 4, index, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3, 4, index, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 4, 4, index, structureBoundingBoxIn);
         }

         return true;
      }

      public Corridor() {
      }

      public Corridor(int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox2;
         this.corridorLength = enumFacing != EnumFacing.NORTH && enumFacing != EnumFacing.SOUTH ? structureBoundingBox2.getXSize() : structureBoundingBox2.getZSize();
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setInteger("Steps", this.corridorLength);
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.corridorLength = tagCompound.getInteger("Steps");
      }

      public static StructureBoundingBox findCorridorBoundingBox(
         List<StructureComponent> list, Random random2, int i, int i2, int i3, EnumFacing enumFacing
      ) {
         StructureBoundingBox structureBoundingBox2 = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -1, -1, 5, 5, 4, enumFacing);
         StructureComponent intersecting = StructureComponent.findIntersecting(list, structureBoundingBox2);
         if (intersecting == null) {
            return null;
         }

         if (intersecting.getBoundingBox().minY == structureBoundingBox2.minY) {
            for (int i4 = 3; i4 >= 1; i4--) {
               structureBoundingBox2 = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -1, -1, 5, 5, i4 - 1, enumFacing);
               if (!intersecting.getBoundingBox().intersectsWith(structureBoundingBox2)) {
                  return StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -1, -1, 5, 5, i4, enumFacing);
               }
            }
         }

         return null;
      }
   }

   public static class Crossing extends StructureStrongholdPieces.Stronghold {
      private boolean leftHigh;
      private boolean leftLow;
      private boolean rightHigh;
      private boolean rightLow;

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 9, 8, 10, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 4, 3, 0);
         if (this.leftLow) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 1, 0, 5, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         if (this.rightLow) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 3, 1, 9, 5, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         if (this.leftHigh) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 7, 0, 7, 9, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         if (this.rightHigh) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 5, 7, 9, 7, 9, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 10, 7, 3, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 2, 1, 8, 2, 6, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 5, 4, 4, 9, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 8, 1, 5, 8, 4, 9, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 4, 7, 3, 4, 9, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 3, 5, 3, 3, 6, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 4, 3, 3, 4, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 6, 3, 4, 6, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 5, 1, 7, 7, 1, 8, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 9, 7, 1, 9, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 2, 7, 7, 2, 7, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 5, 7, 4, 5, 9, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 5, 7, 8, 5, 9, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 5, 7, 7, 5, 9, Blocks.double_stone_slab.getDefaultState(), Blocks.double_stone_slab.getDefaultState(), false
         );
         this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 6, 5, 6, structureBoundingBoxIn);
         return true;
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("leftLow", this.leftLow);
         tagCompound.setBoolean("leftHigh", this.leftHigh);
         tagCompound.setBoolean("rightLow", this.rightLow);
         tagCompound.setBoolean("rightHigh", this.rightHigh);
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         int i = 3;
         int i2 = 5;
         if (this.coordBaseMode == EnumFacing.WEST || this.coordBaseMode == EnumFacing.NORTH) {
            i = 5;
            i2 = 3;
         }

         this.getStructureComponent3((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 5);
         if (this.leftLow) {
            this.getStructureComponent2((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, i, 1);
         }

         if (this.leftHigh) {
            this.getStructureComponent2((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, i2, 7);
         }

         if (this.rightLow) {
            this.getStructureComponent((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, i, 1);
         }

         if (this.rightHigh) {
            this.getStructureComponent((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, i2, 7);
         }
      }

      public static StructureStrongholdPieces.Crossing createPiece(
         List<StructureComponent> list, Random random2, int i, int i2, int i3, EnumFacing enumFacing, int i4
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -4, -3, 10, 9, 11, enumFacing);
         return canStrongholdGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureStrongholdPieces.Crossing(i4, random2, structureBoundingBox, enumFacing)
            : null;
      }

      public Crossing() {
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.leftLow = tagCompound.getBoolean("leftLow");
         this.leftHigh = tagCompound.getBoolean("leftHigh");
         this.rightLow = tagCompound.getBoolean("rightLow");
         this.rightHigh = tagCompound.getBoolean("rightHigh");
      }

      public Crossing(int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.entryDoor = this.getRandomDoor(random2);
         this.boundingBox = structureBoundingBox2;
         this.leftLow = random2.nextBoolean();
         this.leftHigh = random2.nextBoolean();
         this.rightLow = random2.nextBoolean();
         this.rightHigh = random2.nextInt(3) > 0;
      }
   }

   public static class LeftTurn extends StructureStrongholdPieces.Stronghold {
      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.EAST) {
            this.getStructureComponent((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
         } else {
            this.getStructureComponent2((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
         }
      }

      public LeftTurn() {
      }

      public LeftTurn(int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.entryDoor = this.getRandomDoor(random2);
         this.boundingBox = structureBoundingBox2;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 4, 4, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 1, 1, 0);
         if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.EAST) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 1, 4, 3, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         } else {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 3, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         return true;
      }

      public static StructureStrongholdPieces.LeftTurn createPiece(
         List<StructureComponent> list, Random random2, int i, int i2, int i3, EnumFacing enumFacing, int i4
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -1, -1, 5, 5, 5, enumFacing);
         return canStrongholdGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureStrongholdPieces.LeftTurn(i4, random2, structureBoundingBox, enumFacing)
            : null;
      }
   }

   public static class Library extends StructureStrongholdPieces.Stronghold {
      private static final List<WeightedRandomChestContent> strongholdLibraryChestContents = Lists.newArrayList(
         new WeightedRandomChestContent[]{
            new WeightedRandomChestContent(Items.book, 0, 1, 3, 20),
            new WeightedRandomChestContent(Items.paper, 0, 2, 7, 20),
            new WeightedRandomChestContent(Items.map, 0, 1, 1, 1),
            new WeightedRandomChestContent(Items.compass, 0, 1, 1, 1)
         }
      );
      private boolean isLargeRoom;

      public static StructureStrongholdPieces.Library createPiece(
         List<StructureComponent> list, Random random2, int i, int i2, int i3, EnumFacing enumFacing, int i4
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -4, -1, 14, 11, 15, enumFacing);
         if (!canStrongholdGoDeeper(structureBoundingBox) || StructureComponent.findIntersecting(list, structureBoundingBox) != null) {
            structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -4, -1, 14, 6, 15, enumFacing);
            if (!canStrongholdGoDeeper(structureBoundingBox) || StructureComponent.findIntersecting(list, structureBoundingBox) != null) {
               return null;
            }
         }

         return new StructureStrongholdPieces.Library(i4, random2, structureBoundingBox, enumFacing);
      }

      public Library(int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.entryDoor = this.getRandomDoor(random2);
         this.boundingBox = structureBoundingBox2;
         this.isLargeRoom = structureBoundingBox2.getYSize() > 6;
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Tall", this.isLargeRoom);
      }

      public Library() {
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.isLargeRoom = tagCompound.getBoolean("Tall");
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         byte b = 11;
         if (!this.isLargeRoom) {
            b = 6;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 13, b - 1, 14, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 4, 1, 0);
         this.handleWorld3(worldIn, structureBoundingBoxIn, randomIn, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.web.getDefaultState(), Blocks.web.getDefaultState());

         for (int i = 1; i <= 13; i++) {
            if ((i - 1) % 4 == 0) {
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 1, 1, i, 1, 4, i, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false
               );
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 12, 1, i, 12, 4, i, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false
               );
               this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 2, 3, i, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 11, 3, i, structureBoundingBoxIn);
               if (this.isLargeRoom) {
                  this.fillWithBlocks(
                     worldIn, structureBoundingBoxIn, 1, 6, i, 1, 9, i, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false
                  );
                  this.fillWithBlocks(
                     worldIn, structureBoundingBoxIn, 12, 6, i, 12, 9, i, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false
                  );
               }
            } else {
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 1, 1, i, 1, 4, i, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false
               );
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 12, 1, i, 12, 4, i, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false
               );
               if (this.isLargeRoom) {
                  this.fillWithBlocks(
                     worldIn, structureBoundingBoxIn, 1, 6, i, 1, 9, i, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false
                  );
                  this.fillWithBlocks(
                     worldIn,
                     structureBoundingBoxIn,
                     12,
                     6,
                     i,
                     12,
                     9,
                     i,
                     Blocks.bookshelf.getDefaultState(),
                     Blocks.bookshelf.getDefaultState(),
                     false
                  );
               }
            }
         }

         for (int i2 = 3; i2 < 12; i2 += 2) {
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 3, 1, i2, 4, 3, i2, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false
            );
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 6, 1, i2, 7, 3, i2, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false
            );
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 9, 1, i2, 10, 3, i2, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false
            );
         }

         if (this.isLargeRoom) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 1, 3, 5, 13, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 5, 1, 12, 5, 13, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 5, 1, 9, 5, 2, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 5, 12, 9, 5, 13, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
            this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 9, 5, 11, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 8, 5, 11, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 9, 5, 10, structureBoundingBoxIn);
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 3, 6, 2, 3, 6, 12, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false
            );
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 10, 6, 2, 10, 6, 10, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false
            );
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 4, 6, 2, 9, 6, 2, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false
            );
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 4, 6, 12, 8, 6, 12, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false
            );
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 9, 6, 11, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 8, 6, 11, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 9, 6, 10, structureBoundingBoxIn);
            int metadataWithOffset = this.getMetadataWithOffset(Blocks.ladder, 3);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(metadataWithOffset), 10, 1, 13, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(metadataWithOffset), 10, 2, 13, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(metadataWithOffset), 10, 3, 13, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(metadataWithOffset), 10, 4, 13, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(metadataWithOffset), 10, 5, 13, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(metadataWithOffset), 10, 6, 13, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(metadataWithOffset), 10, 7, 13, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 6, 9, 7, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 7, 9, 7, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 6, 8, 7, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 7, 8, 7, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 6, 7, 7, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 7, 7, 7, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 5, 7, 7, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 8, 7, 7, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 6, 7, 6, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 6, 7, 8, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 7, 7, 6, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 7, 7, 8, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 5, 8, 7, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 8, 8, 7, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 6, 8, 6, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 6, 8, 8, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 7, 8, 6, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 7, 8, 8, structureBoundingBoxIn);
         }

         this.generateChestContents(
            worldIn,
            structureBoundingBoxIn,
            randomIn,
            3,
            3,
            5,
            WeightedRandomChestContent.copyWithAddition(strongholdLibraryChestContents, Items.enchanted_book.getWeightedRandomChestContent(randomIn, 5, 2)),
            1 + randomIn.nextInt(4)
         );
         if (this.isLargeRoom) {
            this.setBlockState(worldIn, Blocks.air.getDefaultState(), 12, 9, 1, structureBoundingBoxIn);
            this.generateChestContents(
               worldIn,
               structureBoundingBoxIn,
               randomIn,
               12,
               8,
               1,
               WeightedRandomChestContent.copyWithAddition(strongholdLibraryChestContents, Items.enchanted_book.getWeightedRandomChestContent(randomIn, 5, 2)),
               1 + randomIn.nextInt(4)
            );
         }

         return true;
      }
   }

   static class PieceWeight {
      public Class<? extends StructureStrongholdPieces.Stronghold> pieceClass;
      public final int pieceWeight;
      public int instancesSpawned;
      public int instancesLimit;

      public boolean canSpawnMoreStructures() {
         return this.instancesLimit == 0 || this.instancesSpawned < this.instancesLimit;
      }

      public boolean canSpawnMoreStructuresOfType(int i) {
         return this.instancesLimit == 0 || this.instancesSpawned < this.instancesLimit;
      }

      public PieceWeight(Class<? extends StructureStrongholdPieces.Stronghold> clazz, int i, int i2) {
         this.pieceClass = clazz;
         this.pieceWeight = i;
         this.instancesLimit = i2;
      }
   }

   public static class PortalRoom extends StructureStrongholdPieces.Stronghold {
      private boolean hasSpawner;

      public PortalRoom(int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox2;
      }

      public static StructureStrongholdPieces.PortalRoom createPiece(
         List<StructureComponent> list, Random random2, int i, int i2, int i3, EnumFacing enumFacing, int i4
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -4, -1, 11, 8, 16, enumFacing);
         return canStrongholdGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureStrongholdPieces.PortalRoom(i4, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 10, 7, 15, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, StructureStrongholdPieces.Stronghold.Door.GRATES, 4, 1, 0);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 6, 1, 1, 6, 14, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 9, 6, 1, 9, 6, 14, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 6, 1, 8, 6, 2, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 6, 14, 8, 6, 14, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 2, 1, 4, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 8, 1, 1, 9, 1, 4, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 1, 1, 1, 1, 3, Blocks.flowing_lava.getDefaultState(), Blocks.flowing_lava.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 9, 1, 1, 9, 1, 3, Blocks.flowing_lava.getDefaultState(), Blocks.flowing_lava.getDefaultState(), false
         );
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 3, 1, 8, 7, 1, 12, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 1, 9, 6, 1, 11, Blocks.flowing_lava.getDefaultState(), Blocks.flowing_lava.getDefaultState(), false
         );

         for (int i = 3; i < 14; i += 2) {
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 0, 3, i, 0, 4, i, Blocks.iron_bars.getDefaultState(), Blocks.iron_bars.getDefaultState(), false
            );
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 10, 3, i, 10, 4, i, Blocks.iron_bars.getDefaultState(), Blocks.iron_bars.getDefaultState(), false
            );
         }

         for (int innerIndex = 2; innerIndex < 9; innerIndex += 2) {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               innerIndex,
               3,
               15,
               innerIndex,
               4,
               15,
               Blocks.iron_bars.getDefaultState(),
               Blocks.iron_bars.getDefaultState(),
               false
            );
         }

         int nestedIndex = this.getMetadataWithOffset(Blocks.stone_brick_stairs, 3);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 5, 6, 1, 7, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 2, 6, 6, 2, 7, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 3, 7, 6, 3, 7, false, randomIn, StructureStrongholdPieces.strongholdStones);

         for (int i2 = 4; i2 <= 6; i2++) {
            this.setBlockState(worldIn, Blocks.stone_brick_stairs.getStateFromMeta(nestedIndex), i2, 1, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stone_brick_stairs.getStateFromMeta(nestedIndex), i2, 2, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stone_brick_stairs.getStateFromMeta(nestedIndex), i2, 3, 6, structureBoundingBoxIn);
         }

         int nestedIndex2 = EnumFacing.NORTH.getHorizontalIndex();
         int horizontalIndex = EnumFacing.SOUTH.getHorizontalIndex();
         int horizontalIndex2 = EnumFacing.EAST.getHorizontalIndex();
         int horizontalIndex3 = EnumFacing.WEST.getHorizontalIndex();
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case SOUTH:
                  nestedIndex2 = EnumFacing.SOUTH.getHorizontalIndex();
                  horizontalIndex = EnumFacing.NORTH.getHorizontalIndex();
                  break;
               case WEST:
                  nestedIndex2 = EnumFacing.WEST.getHorizontalIndex();
                  horizontalIndex = EnumFacing.EAST.getHorizontalIndex();
                  horizontalIndex2 = EnumFacing.SOUTH.getHorizontalIndex();
                  horizontalIndex3 = EnumFacing.NORTH.getHorizontalIndex();
                  break;
               case EAST:
                  nestedIndex2 = EnumFacing.EAST.getHorizontalIndex();
                  horizontalIndex = EnumFacing.WEST.getHorizontalIndex();
                  horizontalIndex2 = EnumFacing.SOUTH.getHorizontalIndex();
                  horizontalIndex3 = EnumFacing.NORTH.getHorizontalIndex();
            }
         }

         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(nestedIndex2).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            4,
            3,
            8,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(nestedIndex2).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            5,
            3,
            8,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(nestedIndex2).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            6,
            3,
            8,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(horizontalIndex).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            4,
            3,
            12,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(horizontalIndex).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            5,
            3,
            12,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(horizontalIndex).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            6,
            3,
            12,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(horizontalIndex2).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            3,
            3,
            9,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(horizontalIndex2).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            3,
            3,
            10,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(horizontalIndex2).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            3,
            3,
            11,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(horizontalIndex3).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            7,
            3,
            9,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(horizontalIndex3).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            7,
            3,
            10,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(horizontalIndex3).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            7,
            3,
            11,
            structureBoundingBoxIn
         );
         if (!this.hasSpawner) {
            int yWithOffset = this.getYWithOffset(3);
            BlockPos blockPos = new BlockPos(this.getXWithOffset(5, 6), yWithOffset, this.getZWithOffset(5, 6));
            if (structureBoundingBoxIn.isVecInside(blockPos)) {
               this.hasSpawner = true;
               worldIn.setBlockState(blockPos, Blocks.mob_spawner.getDefaultState(), 2);
               TileEntity tileEntity = worldIn.getTileEntity(blockPos);
               if (tileEntity instanceof TileEntityMobSpawner) {
                  ((TileEntityMobSpawner)tileEntity).getSpawnerBaseLogic().setEntityName("Silverfish");
               }
            }
         }

         return true;
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.hasSpawner = tagCompound.getBoolean("Mob");
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         if (componentIn != null) {
            ((StructureStrongholdPieces.Stairs2)componentIn).strongholdPortalRoom = this;
         }
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Mob", this.hasSpawner);
      }

      public PortalRoom() {
      }
   }

   public static class Prison extends StructureStrongholdPieces.Stronghold {
      public Prison(int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.entryDoor = this.getRandomDoor(random2);
         this.boundingBox = structureBoundingBox2;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getStructureComponent3((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1);
      }

      public Prison() {
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 8, 4, 10, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 1, 1, 0);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 10, 3, 3, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 1, 4, 3, 1, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 3, 4, 3, 3, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 7, 4, 3, 7, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 9, 4, 3, 9, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 4, 4, 3, 6, Blocks.iron_bars.getDefaultState(), Blocks.iron_bars.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 5, 7, 3, 5, Blocks.iron_bars.getDefaultState(), Blocks.iron_bars.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), 4, 3, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), 4, 3, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.iron_door.getStateFromMeta(this.getMetadataWithOffset(Blocks.iron_door, 3)), 4, 1, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.iron_door.getStateFromMeta(this.getMetadataWithOffset(Blocks.iron_door, 3) + 8), 4, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.iron_door.getStateFromMeta(this.getMetadataWithOffset(Blocks.iron_door, 3)), 4, 1, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.iron_door.getStateFromMeta(this.getMetadataWithOffset(Blocks.iron_door, 3) + 8), 4, 2, 8, structureBoundingBoxIn);
         return true;
      }

      public static StructureStrongholdPieces.Prison createPiece(
         List<StructureComponent> list, Random random2, int i, int i2, int i3, EnumFacing enumFacing, int i4
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -1, -1, 9, 5, 11, enumFacing);
         return canStrongholdGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureStrongholdPieces.Prison(i4, random2, structureBoundingBox, enumFacing)
            : null;
      }
   }

   public static class RightTurn extends StructureStrongholdPieces.LeftTurn {
      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.EAST) {
            this.getStructureComponent2((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
         } else {
            this.getStructureComponent((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
         }
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 4, 4, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 1, 1, 0);
         if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.EAST) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 3, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         } else {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 1, 4, 3, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         return true;
      }
   }

   public static class RoomCrossing extends StructureStrongholdPieces.Stronghold {
      private static final List<WeightedRandomChestContent> strongholdRoomCrossingChestContents = Lists.newArrayList(
         new WeightedRandomChestContent[]{
            new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10),
            new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 5),
            new WeightedRandomChestContent(Items.redstone, 0, 4, 9, 5),
            new WeightedRandomChestContent(Items.coal, 0, 3, 8, 10),
            new WeightedRandomChestContent(Items.bread, 0, 1, 3, 15),
            new WeightedRandomChestContent(Items.apple, 0, 1, 3, 15),
            new WeightedRandomChestContent(Items.iron_pickaxe, 0, 1, 1, 1)
         }
      );
      protected int roomType;

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getStructureComponent3((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 4);
         this.getStructureComponent2((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 4);
         this.getStructureComponent((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 4);
      }

      public static StructureStrongholdPieces.RoomCrossing createPiece(
         List<StructureComponent> list, Random random2, int i, int i2, int i3, EnumFacing enumFacing, int i4
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -4, -1, 11, 7, 11, enumFacing);
         return canStrongholdGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureStrongholdPieces.RoomCrossing(i4, random2, structureBoundingBox, enumFacing)
            : null;
      }

      public RoomCrossing() {
      }

      public RoomCrossing(int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.entryDoor = this.getRandomDoor(random2);
         this.boundingBox = structureBoundingBox2;
         this.roomType = random2.nextInt(5);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 10, 6, 10, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 4, 1, 0);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 10, 6, 3, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 4, 0, 3, 6, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 1, 4, 10, 3, 6, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         switch (this.roomType) {
            case 0:
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 5, 1, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 5, 2, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 5, 3, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 4, 3, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 6, 3, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 5, 3, 4, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 5, 3, 6, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stone_slab.getDefaultState(), 4, 1, 4, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stone_slab.getDefaultState(), 4, 1, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stone_slab.getDefaultState(), 4, 1, 6, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stone_slab.getDefaultState(), 6, 1, 4, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stone_slab.getDefaultState(), 6, 1, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stone_slab.getDefaultState(), 6, 1, 6, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stone_slab.getDefaultState(), 5, 1, 4, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stone_slab.getDefaultState(), 5, 1, 6, structureBoundingBoxIn);
               break;
            case 1:
               for (int index = 0; index < 5; index++) {
                  this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3, 1, 3 + index, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 7, 1, 3 + index, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3 + index, 1, 3, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3 + index, 1, 7, structureBoundingBoxIn);
               }

               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 5, 1, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 5, 2, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 5, 3, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.flowing_water.getDefaultState(), 5, 4, 5, structureBoundingBoxIn);
               break;
            case 2:
               for (int index2 = 1; index2 <= 9; index2++) {
                  this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 1, 3, index2, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 9, 3, index2, structureBoundingBoxIn);
               }

               for (int innerIndex = 1; innerIndex <= 9; innerIndex++) {
                  this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), innerIndex, 3, 1, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), innerIndex, 3, 9, structureBoundingBoxIn);
               }

               this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 5, 1, 4, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 5, 1, 6, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 5, 3, 4, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 5, 3, 6, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 4, 1, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 6, 1, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 4, 3, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 6, 3, 5, structureBoundingBoxIn);

               for (int nestedIndex = 1; nestedIndex <= 3; nestedIndex++) {
                  this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 4, nestedIndex, 4, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 6, nestedIndex, 4, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 4, nestedIndex, 6, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 6, nestedIndex, 6, structureBoundingBoxIn);
               }

               this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 5, 3, 5, structureBoundingBoxIn);

               for (int outerIndex = 2; outerIndex <= 8; outerIndex++) {
                  this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 2, 3, outerIndex, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 3, 3, outerIndex, structureBoundingBoxIn);
                  if (outerIndex <= 3 || outerIndex >= 7) {
                     this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 4, 3, outerIndex, structureBoundingBoxIn);
                     this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 5, 3, outerIndex, structureBoundingBoxIn);
                     this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 6, 3, outerIndex, structureBoundingBoxIn);
                  }

                  this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 7, 3, outerIndex, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 8, 3, outerIndex, structureBoundingBoxIn);
               }

               this.setBlockState(
                  worldIn,
                  Blocks.ladder.getStateFromMeta(this.getMetadataWithOffset(Blocks.ladder, EnumFacing.WEST.getIndex())),
                  9,
                  1,
                  3,
                  structureBoundingBoxIn
               );
               this.setBlockState(
                  worldIn,
                  Blocks.ladder.getStateFromMeta(this.getMetadataWithOffset(Blocks.ladder, EnumFacing.WEST.getIndex())),
                  9,
                  2,
                  3,
                  structureBoundingBoxIn
               );
               this.setBlockState(
                  worldIn,
                  Blocks.ladder.getStateFromMeta(this.getMetadataWithOffset(Blocks.ladder, EnumFacing.WEST.getIndex())),
                  9,
                  3,
                  3,
                  structureBoundingBoxIn
               );
               this.generateChestContents(
                  worldIn,
                  structureBoundingBoxIn,
                  randomIn,
                  3,
                  4,
                  8,
                  WeightedRandomChestContent.copyWithAddition(strongholdRoomCrossingChestContents, Items.enchanted_book.getRandom(randomIn)),
                  1 + randomIn.nextInt(4)
               );
         }

         return true;
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setInteger("Type", this.roomType);
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.roomType = tagCompound.getInteger("Type");
      }
   }

   public static class Stairs extends StructureStrongholdPieces.Stronghold {
      private boolean source;

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         if (this.source) {
            StructureStrongholdPieces.strongComponentType = StructureStrongholdPieces.Crossing.class;
         }

         this.getStructureComponent3((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1);
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Source", this.source);
      }

      public static StructureStrongholdPieces.Stairs createPiece(
         List<StructureComponent> list, Random random2, int i, int i2, int i3, EnumFacing enumFacing, int i4
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -1, -7, 5, 11, 5, enumFacing);
         return canStrongholdGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureStrongholdPieces.Stairs(i4, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.source = tagCompound.getBoolean("Source");
      }

      public Stairs() {
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 10, 4, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 1, 7, 0);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 4);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 2, 6, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 1, 5, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(EnumType.STONE.getMetadata()), 1, 6, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 1, 5, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 1, 4, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(EnumType.STONE.getMetadata()), 1, 5, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 2, 4, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3, 3, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(EnumType.STONE.getMetadata()), 3, 4, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3, 3, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3, 2, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(EnumType.STONE.getMetadata()), 3, 3, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 2, 2, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 1, 1, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(EnumType.STONE.getMetadata()), 1, 2, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 1, 1, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(EnumType.STONE.getMetadata()), 1, 1, 3, structureBoundingBoxIn);
         return true;
      }

      public Stairs(int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(i);
         this.source = false;
         this.coordBaseMode = enumFacing;
         this.entryDoor = this.getRandomDoor(random2);
         this.boundingBox = structureBoundingBox2;
      }

      public Stairs(int i, Random random2, int i2, int i3) {
         super(i);
         this.source = true;
         this.coordBaseMode = Plane.HORIZONTAL.random(random2);
         this.entryDoor = StructureStrongholdPieces.Stronghold.Door.OPENING;
         switch (this.coordBaseMode) {
            case SOUTH:
            case NORTH:
               this.boundingBox = new StructureBoundingBox(i2, 64, i3, i2 + 5 - 1, 74, i3 + 5 - 1);
               break;
            default:
               this.boundingBox = new StructureBoundingBox(i2, 64, i3, i2 + 5 - 1, 74, i3 + 5 - 1);
         }
      }
   }

   public static class Stairs2 extends StructureStrongholdPieces.Stairs {
      public StructureStrongholdPieces.PortalRoom strongholdPortalRoom;
      public StructureStrongholdPieces.PieceWeight strongholdPieceWeight;
      public List<StructureComponent> pendingChildren = Lists.newArrayList();

      public Stairs2(Random random2, int i, int i2) {
         super(0, random2, i, i2);
      }

      public Stairs2() {
      }

      @Override
      public BlockPos getBoundingBoxCenter() {
         return this.strongholdPortalRoom != null ? this.strongholdPortalRoom.getBoundingBoxCenter() : super.getBoundingBoxCenter();
      }
   }

   public static class StairsStraight extends StructureStrongholdPieces.Stronghold {
      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getStructureComponent3((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1);
      }

      public StairsStraight() {
      }

      public static StructureStrongholdPieces.StairsStraight createPiece(
         List<StructureComponent> list, Random random2, int i, int i2, int i3, EnumFacing enumFacing, int i4
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -1, -7, 5, 11, 8, enumFacing);
         return canStrongholdGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureStrongholdPieces.StairsStraight(i4, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 10, 7, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 1, 7, 0);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 7);
         int metadataWithOffset = this.getMetadataWithOffset(Blocks.stone_stairs, 2);

         for (int innerIndex = 0; innerIndex < 6; innerIndex++) {
            this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(metadataWithOffset), 1, 6 - innerIndex, 1 + innerIndex, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(metadataWithOffset), 2, 6 - innerIndex, 1 + innerIndex, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(metadataWithOffset), 3, 6 - innerIndex, 1 + innerIndex, structureBoundingBoxIn);
            if (innerIndex < 5) {
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 1, 5 - innerIndex, 1 + innerIndex, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 2, 5 - innerIndex, 1 + innerIndex, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3, 5 - innerIndex, 1 + innerIndex, structureBoundingBoxIn);
            }
         }

         return true;
      }

      public StairsStraight(int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.entryDoor = this.getRandomDoor(random2);
         this.boundingBox = structureBoundingBox2;
      }
   }

   static class Stones extends BlockSelector {
      private Stones() {
      }

      @Override
      public void handleRandom(Random random2, boolean flag) {
         if (flag) {
            float f = random2.nextFloat();
            if (f < 0.2F) {
               this.blockstate = Blocks.stonebrick.getStateFromMeta(BlockStoneBrick.CRACKED_META);
            } else if (f < 0.5F) {
               this.blockstate = Blocks.stonebrick.getStateFromMeta(BlockStoneBrick.MOSSY_META);
            } else if (f < 0.55F) {
               this.blockstate = Blocks.monster_egg.getStateFromMeta(net.minecraft.block.BlockSilverfish.EnumType.STONEBRICK.getMetadata());
            } else {
               this.blockstate = Blocks.stonebrick.getDefaultState();
            }
         } else {
            this.blockstate = Blocks.air.getDefaultState();
         }
      }
   }

   public static class Straight extends StructureStrongholdPieces.Stronghold {
      private boolean expandsX;
      private boolean expandsZ;

      public static StructureStrongholdPieces.Straight createPiece(
         List<StructureComponent> list, Random random2, int i, int i2, int i3, EnumFacing enumFacing, int i4
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -1, -1, 5, 5, 7, enumFacing);
         return canStrongholdGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureStrongholdPieces.Straight(i4, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.expandsX = tagCompound.getBoolean("Left");
         this.expandsZ = tagCompound.getBoolean("Right");
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getStructureComponent3((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1);
         if (this.expandsX) {
            this.getStructureComponent2((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 2);
         }

         if (this.expandsZ) {
            this.getStructureComponent((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 2);
         }
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 4, 6, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 1, 1, 0);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 6);
         this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 1, 2, 1, Blocks.torch.getDefaultState());
         this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 3, 2, 1, Blocks.torch.getDefaultState());
         this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 1, 2, 5, Blocks.torch.getDefaultState());
         this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 3, 2, 5, Blocks.torch.getDefaultState());
         if (this.expandsX) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 2, 0, 3, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         if (this.expandsZ) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 2, 4, 3, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         return true;
      }

      public Straight(int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.entryDoor = this.getRandomDoor(random2);
         this.boundingBox = structureBoundingBox2;
         this.expandsX = random2.nextInt(2) == 0;
         this.expandsZ = random2.nextInt(2) == 0;
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Left", this.expandsX);
         tagCompound.setBoolean("Right", this.expandsZ);
      }

      public Straight() {
      }
   }

   abstract static class Stronghold extends StructureComponent {
      protected StructureStrongholdPieces.Stronghold.Door entryDoor = StructureStrongholdPieces.Stronghold.Door.OPENING;

      protected StructureComponent getStructureComponent2(StructureStrongholdPieces.Stairs2 stairs2, List list, Random random2, int i, int i2) {
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case SOUTH:
                  return StructureStrongholdPieces.generateAndAddPiece(
                     stairs2,
                     list,
                     random2,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY + i,
                     this.boundingBox.minZ + i2,
                     EnumFacing.WEST,
                     this.getComponentType()
                  );
               case WEST:
                  return StructureStrongholdPieces.generateAndAddPiece(
                     stairs2,
                     list,
                     random2,
                     this.boundingBox.minX + i2,
                     this.boundingBox.minY + i,
                     this.boundingBox.minZ - 1,
                     EnumFacing.NORTH,
                     this.getComponentType()
                  );
               case EAST:
                  return StructureStrongholdPieces.generateAndAddPiece(
                     stairs2,
                     list,
                     random2,
                     this.boundingBox.minX + i2,
                     this.boundingBox.minY + i,
                     this.boundingBox.minZ - 1,
                     EnumFacing.NORTH,
                     this.getComponentType()
                  );
               case NORTH:
                  return StructureStrongholdPieces.generateAndAddPiece(
                     stairs2,
                     list,
                     random2,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY + i,
                     this.boundingBox.minZ + i2,
                     EnumFacing.WEST,
                     this.getComponentType()
                  );
            }
         }

         return null;
      }

      protected void placeDoor(
         World worldIn, Random random2, StructureBoundingBox structureBoundingBox2, StructureStrongholdPieces.Stronghold.Door door, int i, int i2, int i3
      ) {
         switch (door) {
            case OPENING:
            default:
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBox2,
                  i,
                  i2,
                  i3,
                  i + 3 - 1,
                  i2 + 3 - 1,
                  i3,
                  Blocks.air.getDefaultState(),
                  Blocks.air.getDefaultState(),
                  false
               );
               break;
            case WOOD_DOOR:
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i, i2, i3, structureBoundingBox2);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i, i2 + 1, i3, structureBoundingBox2);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i, i2 + 2, i3, structureBoundingBox2);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i + 1, i2 + 2, i3, structureBoundingBox2);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i + 2, i2 + 2, i3, structureBoundingBox2);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i + 2, i2 + 1, i3, structureBoundingBox2);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i + 2, i2, i3, structureBoundingBox2);
               this.setBlockState(worldIn, Blocks.oak_door.getDefaultState(), i + 1, i2, i3, structureBoundingBox2);
               this.setBlockState(worldIn, Blocks.oak_door.getStateFromMeta(8), i + 1, i2 + 1, i3, structureBoundingBox2);
               break;
            case GRATES:
               this.setBlockState(worldIn, Blocks.air.getDefaultState(), i + 1, i2, i3, structureBoundingBox2);
               this.setBlockState(worldIn, Blocks.air.getDefaultState(), i + 1, i2 + 1, i3, structureBoundingBox2);
               this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), i, i2, i3, structureBoundingBox2);
               this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), i, i2 + 1, i3, structureBoundingBox2);
               this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), i, i2 + 2, i3, structureBoundingBox2);
               this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), i + 1, i2 + 2, i3, structureBoundingBox2);
               this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), i + 2, i2 + 2, i3, structureBoundingBox2);
               this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), i + 2, i2 + 1, i3, structureBoundingBox2);
               this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), i + 2, i2, i3, structureBoundingBox2);
               break;
            case IRON_DOOR:
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i, i2, i3, structureBoundingBox2);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i, i2 + 1, i3, structureBoundingBox2);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i, i2 + 2, i3, structureBoundingBox2);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i + 1, i2 + 2, i3, structureBoundingBox2);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i + 2, i2 + 2, i3, structureBoundingBox2);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i + 2, i2 + 1, i3, structureBoundingBox2);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i + 2, i2, i3, structureBoundingBox2);
               this.setBlockState(worldIn, Blocks.iron_door.getDefaultState(), i + 1, i2, i3, structureBoundingBox2);
               this.setBlockState(worldIn, Blocks.iron_door.getStateFromMeta(8), i + 1, i2 + 1, i3, structureBoundingBox2);
               this.setBlockState(
                  worldIn, Blocks.stone_button.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_button, 4)), i + 2, i2 + 1, i3 + 1, structureBoundingBox2
               );
               this.setBlockState(
                  worldIn, Blocks.stone_button.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_button, 3)), i + 2, i2 + 1, i3 - 1, structureBoundingBox2
               );
         }
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
         this.entryDoor = StructureStrongholdPieces.Stronghold.Door.valueOf(tagCompound.getString("EntryDoor"));
      }

      protected static boolean canStrongholdGoDeeper(StructureBoundingBox structureBoundingBox2) {
         return structureBoundingBox2 != null && structureBoundingBox2.minY > 10;
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         tagCompound.setString("EntryDoor", this.entryDoor.name());
      }

      protected StructureComponent getStructureComponent(StructureStrongholdPieces.Stairs2 stairs2, List list, Random random2, int i, int i2) {
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case SOUTH:
                  return StructureStrongholdPieces.generateAndAddPiece(
                     stairs2,
                     list,
                     random2,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY + i,
                     this.boundingBox.minZ + i2,
                     EnumFacing.EAST,
                     this.getComponentType()
                  );
               case WEST:
                  return StructureStrongholdPieces.generateAndAddPiece(
                     stairs2,
                     list,
                     random2,
                     this.boundingBox.minX + i2,
                     this.boundingBox.minY + i,
                     this.boundingBox.maxZ + 1,
                     EnumFacing.SOUTH,
                     this.getComponentType()
                  );
               case EAST:
                  return StructureStrongholdPieces.generateAndAddPiece(
                     stairs2,
                     list,
                     random2,
                     this.boundingBox.minX + i2,
                     this.boundingBox.minY + i,
                     this.boundingBox.maxZ + 1,
                     EnumFacing.SOUTH,
                     this.getComponentType()
                  );
               case NORTH:
                  return StructureStrongholdPieces.generateAndAddPiece(
                     stairs2,
                     list,
                     random2,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY + i,
                     this.boundingBox.minZ + i2,
                     EnumFacing.EAST,
                     this.getComponentType()
                  );
            }
         }

         return null;
      }

      protected StructureStrongholdPieces.Stronghold.Door getRandomDoor(Random random2) {
         switch (random2.nextInt(5)) {
            case 0:
            case 1:
            default:
               return StructureStrongholdPieces.Stronghold.Door.OPENING;
            case 2:
               return StructureStrongholdPieces.Stronghold.Door.WOOD_DOOR;
            case 3:
               return StructureStrongholdPieces.Stronghold.Door.GRATES;
            case 4:
               return StructureStrongholdPieces.Stronghold.Door.IRON_DOOR;
         }
      }

      protected Stronghold(int i) {
         super(i);
      }

      protected StructureComponent getStructureComponent3(StructureStrongholdPieces.Stairs2 stairs2, List list, Random random2, int i) {
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case SOUTH:
                  return StructureStrongholdPieces.generateAndAddPiece(
                     stairs2,
                     list,
                     random2,
                     this.boundingBox.minX + i,
                     this.boundingBox.minY + 1,
                     this.boundingBox.maxZ + 1,
                     this.coordBaseMode,
                     this.getComponentType()
                  );
               case WEST:
                  return StructureStrongholdPieces.generateAndAddPiece(
                     stairs2,
                     list,
                     random2,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY + 1,
                     this.boundingBox.minZ + i,
                     this.coordBaseMode,
                     this.getComponentType()
                  );
               case EAST:
                  return StructureStrongholdPieces.generateAndAddPiece(
                     stairs2,
                     list,
                     random2,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY + 1,
                     this.boundingBox.minZ + i,
                     this.coordBaseMode,
                     this.getComponentType()
                  );
               case NORTH:
                  return StructureStrongholdPieces.generateAndAddPiece(
                     stairs2,
                     list,
                     random2,
                     this.boundingBox.minX + i,
                     this.boundingBox.minY + 1,
                     this.boundingBox.minZ - 1,
                     this.coordBaseMode,
                     this.getComponentType()
                  );
            }
         }

         return null;
      }

      public Stronghold() {
      }

      public enum Door {
         OPENING,
         WOOD_DOOR,
         GRATES,
         IRON_DOOR;
      }
   }
}
