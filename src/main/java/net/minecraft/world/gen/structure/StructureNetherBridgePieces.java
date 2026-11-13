package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.world.World;

public class StructureNetherBridgePieces {
   private static final StructureNetherBridgePieces.PieceWeight[] primaryComponents = new StructureNetherBridgePieces.PieceWeight[]{
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.PieceChild3.class, 30, 0, true),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Crossing3.class, 10, 4),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.PieceChild6.class, 10, 4),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Stairs.class, 10, 3),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Throne.class, 5, 2),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Entrance.class, 5, 1)
   };
   private static final StructureNetherBridgePieces.PieceWeight[] secondaryComponents = new StructureNetherBridgePieces.PieceWeight[]{
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor5.class, 25, 0, true),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Crossing2.class, 15, 5),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.PieceChild.class, 5, 10),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.PieceChild2.class, 5, 10),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.PieceChild4.class, 10, 3, true),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.PieceChild5.class, 7, 2),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.NetherStalkRoom.class, 5, 2)
   };

   private static StructureNetherBridgePieces.Piece createPiece(
      StructureNetherBridgePieces.PieceWeight pieceWeight2,
      List<StructureComponent> list2,
      Random random2,
      int i,
      int i2,
      int i3,
      EnumFacing enumFacing,
      int i4
   ) {
      Class<? extends StructureNetherBridgePieces.Piece> oclass = pieceWeight2.weightClass;
      StructureNetherBridgePieces.Piece structurenetherbridgepieces$piece = null;
      if (oclass == StructureNetherBridgePieces.PieceChild3.class) {
         structurenetherbridgepieces$piece = StructureNetherBridgePieces.PieceChild3.createPieceChild3(list2, random2, i, i2, i3, enumFacing, i4);
      } else if (oclass == StructureNetherBridgePieces.Crossing3.class) {
         structurenetherbridgepieces$piece = StructureNetherBridgePieces.Crossing3.createPiece(list2, random2, i, i2, i3, enumFacing, i4);
      } else if (oclass == StructureNetherBridgePieces.PieceChild6.class) {
         structurenetherbridgepieces$piece = StructureNetherBridgePieces.PieceChild6.createPieceChild6(list2, random2, i, i2, i3, enumFacing, i4);
      } else if (oclass == StructureNetherBridgePieces.Stairs.class) {
         structurenetherbridgepieces$piece = StructureNetherBridgePieces.Stairs.createPiece(list2, random2, i, i2, i3, i4, enumFacing);
      } else if (oclass == StructureNetherBridgePieces.Throne.class) {
         structurenetherbridgepieces$piece = StructureNetherBridgePieces.Throne.createPiece(list2, random2, i, i2, i3, i4, enumFacing);
      } else if (oclass == StructureNetherBridgePieces.Entrance.class) {
         structurenetherbridgepieces$piece = StructureNetherBridgePieces.Entrance.createPiece(list2, random2, i, i2, i3, enumFacing, i4);
      } else if (oclass == StructureNetherBridgePieces.Corridor5.class) {
         structurenetherbridgepieces$piece = StructureNetherBridgePieces.Corridor5.createPiece(list2, random2, i, i2, i3, enumFacing, i4);
      } else if (oclass == StructureNetherBridgePieces.PieceChild.class) {
         structurenetherbridgepieces$piece = StructureNetherBridgePieces.PieceChild.createPieceChild(list2, random2, i, i2, i3, enumFacing, i4);
      } else if (oclass == StructureNetherBridgePieces.PieceChild2.class) {
         structurenetherbridgepieces$piece = StructureNetherBridgePieces.PieceChild2.createPieceChild2(list2, random2, i, i2, i3, enumFacing, i4);
      } else if (oclass == StructureNetherBridgePieces.PieceChild4.class) {
         structurenetherbridgepieces$piece = StructureNetherBridgePieces.PieceChild4.createPieceChild4(list2, random2, i, i2, i3, enumFacing, i4);
      } else if (oclass == StructureNetherBridgePieces.PieceChild5.class) {
         structurenetherbridgepieces$piece = StructureNetherBridgePieces.PieceChild5.createPieceChild5(list2, random2, i, i2, i3, enumFacing, i4);
      } else if (oclass == StructureNetherBridgePieces.Crossing2.class) {
         structurenetherbridgepieces$piece = StructureNetherBridgePieces.Crossing2.createPiece(list2, random2, i, i2, i3, enumFacing, i4);
      } else if (oclass == StructureNetherBridgePieces.NetherStalkRoom.class) {
         structurenetherbridgepieces$piece = StructureNetherBridgePieces.NetherStalkRoom.createPiece(list2, random2, i, i2, i3, enumFacing, i4);
      }

      return structurenetherbridgepieces$piece;
   }

   public static void registerNetherFortressPieces() {
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Crossing3.class, "NeBCr");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.End.class, "NeBEF");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.PieceChild3.class, "NeBS");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.PieceChild4.class, "NeCCS");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.PieceChild5.class, "NeCTB");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Entrance.class, "NeCE");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Crossing2.class, "NeSCSC");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.PieceChild2.class, "NeSCLT");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Corridor5.class, "NeSC");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.PieceChild.class, "NeSCRT");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.NetherStalkRoom.class, "NeCSR");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Throne.class, "NeMT");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.PieceChild6.class, "NeRC");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Stairs.class, "NeSR");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Start.class, "NeStart");
   }

   public static class Corridor5 extends StructureNetherBridgePieces.Piece {
      public Corridor5() {
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 1, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 4, 5, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 5, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 2, 0, 4, 5, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 1, 0, 4, 1, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 3, 0, 4, 3, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 3, 1, 4, 4, 1, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 3, 3, 4, 4, 3, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 6, 0, 4, 6, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int index = 0; index <= 4; index++) {
            for (int innerIndex = 0; innerIndex <= 4; innerIndex++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), index, -1, innerIndex, structureBoundingBoxIn);
            }
         }

         return true;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentNormal((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 1, 0, true);
      }

      public static StructureNetherBridgePieces.Corridor5 createPiece(
         List<StructureComponent> list2, Random random2, int i, int i2, int i3, EnumFacing enumFacing, int i4
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -1, 0, 5, 7, 5, enumFacing);
         return isAboveGround(structureBoundingBox) && StructureComponent.findIntersecting(list2, structureBoundingBox) == null
            ? new StructureNetherBridgePieces.Corridor5(i4, random2, structureBoundingBox, enumFacing)
            : null;
      }

      public Corridor5(int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox2;
      }
   }

   public static class Crossing2 extends StructureNetherBridgePieces.Piece {
      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentNormal((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 1, 0, true);
         this.getNextComponentX((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 0, 1, true);
         this.getNextComponentZ((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 0, 1, true);
      }

      public Crossing2(int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox2;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 1, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 4, 5, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 5, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 2, 0, 4, 5, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 4, 0, 5, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 2, 4, 4, 5, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 6, 0, 4, 6, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int index = 0; index <= 4; index++) {
            for (int innerIndex = 0; innerIndex <= 4; innerIndex++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), index, -1, innerIndex, structureBoundingBoxIn);
            }
         }

         return true;
      }

      public Crossing2() {
      }

      public static StructureNetherBridgePieces.Crossing2 createPiece(
         List<StructureComponent> list2, Random random2, int i, int i2, int i3, EnumFacing enumFacing, int i4
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -1, 0, 5, 7, 5, enumFacing);
         return isAboveGround(structureBoundingBox) && StructureComponent.findIntersecting(list2, structureBoundingBox) == null
            ? new StructureNetherBridgePieces.Crossing2(i4, random2, structureBoundingBox, enumFacing)
            : null;
      }
   }

   public static class Crossing3 extends StructureNetherBridgePieces.Piece {
      public Crossing3() {
      }

      protected Crossing3(Random random2, int i, int i2) {
         super(0);
         this.coordBaseMode = Plane.HORIZONTAL.random(random2);
         switch (this.coordBaseMode) {
            case NORTH:
            case SOUTH:
               this.boundingBox = new StructureBoundingBox(i, 64, i2, i + 19 - 1, 73, i2 + 19 - 1);
               break;
            default:
               this.boundingBox = new StructureBoundingBox(i, 64, i2, i + 19 - 1, 73, i2 + 19 - 1);
         }
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentNormal((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 8, 3, false);
         this.getNextComponentX((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 3, 8, false);
         this.getNextComponentZ((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 3, 8, false);
      }

      public static StructureNetherBridgePieces.Crossing3 createPiece(
         List<StructureComponent> list2, Random random2, int i, int i2, int i3, EnumFacing enumFacing, int i4
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -8, -3, 19, 10, 19, enumFacing);
         return isAboveGround(structureBoundingBox) && StructureComponent.findIntersecting(list2, structureBoundingBox) == null
            ? new StructureNetherBridgePieces.Crossing3(i4, random2, structureBoundingBox, enumFacing)
            : null;
      }

      public Crossing3(int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox2;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 3, 0, 11, 4, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 7, 18, 4, 11, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 5, 0, 10, 7, 18, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 8, 18, 7, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 5, 0, 7, 5, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 5, 11, 7, 5, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 11, 5, 0, 11, 5, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 11, 5, 11, 11, 5, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 5, 7, 7, 5, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 11, 5, 7, 18, 5, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 5, 11, 7, 5, 11, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 11, 5, 11, 18, 5, 11, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 2, 0, 11, 2, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 2, 13, 11, 2, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 0, 0, 11, 1, 3, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 0, 15, 11, 1, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int index = 7; index <= 11; index++) {
            for (int innerIndex = 0; innerIndex <= 2; innerIndex++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), index, -1, innerIndex, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), index, -1, 18 - innerIndex, structureBoundingBoxIn);
            }
         }

         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 7, 5, 2, 11, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 13, 2, 7, 18, 2, 11, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 7, 3, 1, 11, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 15, 0, 7, 18, 1, 11, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int nestedIndex = 0; nestedIndex <= 2; nestedIndex++) {
            for (int outerIndex = 7; outerIndex <= 11; outerIndex++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), nestedIndex, -1, outerIndex, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), 18 - nestedIndex, -1, outerIndex, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   public static class End extends StructureNetherBridgePieces.Piece {
      private int fillSeed;

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         Random random2 = new Random(this.fillSeed);

         for (int i = 0; i <= 4; i++) {
            for (int i2 = 3; i2 <= 4; i2++) {
               int i3 = random2.nextInt(8);
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBoxIn,
                  i,
                  i2,
                  0,
                  i,
                  i2,
                  i3,
                  Blocks.nether_brick.getDefaultState(),
                  Blocks.nether_brick.getDefaultState(),
                  false
               );
            }
         }

         int i4 = random2.nextInt(8);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 5, 0, 0, 5, i4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         i4 = random2.nextInt(8);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 5, 0, 4, 5, i4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int i5 = 0; i5 <= 4; i5++) {
            int i6 = random2.nextInt(5);
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               i5,
               2,
               0,
               i5,
               2,
               i6,
               Blocks.nether_brick.getDefaultState(),
               Blocks.nether_brick.getDefaultState(),
               false
            );
         }

         for (int i7 = 0; i7 <= 4; i7++) {
            for (int i8 = 0; i8 <= 1; i8++) {
               int i9 = random2.nextInt(3);
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBoxIn,
                  i7,
                  i8,
                  0,
                  i7,
                  i8,
                  i9,
                  Blocks.nether_brick.getDefaultState(),
                  Blocks.nether_brick.getDefaultState(),
                  false
               );
            }
         }

         return true;
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setInteger("Seed", this.fillSeed);
      }

      public static StructureNetherBridgePieces.End createPiece(
         List<StructureComponent> list2, Random random2, int i, int i2, int i3, EnumFacing enumFacing, int i4
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -1, -3, 5, 10, 8, enumFacing);
         return isAboveGround(structureBoundingBox) && StructureComponent.findIntersecting(list2, structureBoundingBox) == null
            ? new StructureNetherBridgePieces.End(i4, random2, structureBoundingBox, enumFacing)
            : null;
      }

      public End(int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox2;
         this.fillSeed = random2.nextInt();
      }

      public End() {
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.fillSeed = tagCompound.getInteger("Seed");
      }
   }

   public static class Entrance extends StructureNetherBridgePieces.Piece {
      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 0, 12, 4, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 0, 12, 13, 12, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 5, 0, 1, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 11, 5, 0, 12, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 11, 4, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, 5, 11, 10, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 9, 11, 7, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 0, 4, 12, 1, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, 5, 0, 10, 12, 1, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 9, 0, 7, 12, 1, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 11, 2, 10, 12, 10, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 8, 0, 7, 8, 0, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );

         for (int index = 1; index <= 11; index += 2) {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               index,
               10,
               0,
               index,
               11,
               0,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               index,
               10,
               12,
               index,
               11,
               12,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               0,
               10,
               index,
               0,
               11,
               index,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               12,
               10,
               index,
               12,
               11,
               index,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), index, 13, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), index, 13, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), 0, 13, index, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), 12, 13, index, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), index + 1, 13, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), index + 1, 13, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 0, 13, index + 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 12, 13, index + 1, structureBoundingBoxIn);
         }

         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 0, 13, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 0, 13, 12, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 0, 13, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 12, 13, 0, structureBoundingBoxIn);

         for (int nestedIndex = 3; nestedIndex <= 9; nestedIndex += 2) {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               1,
               7,
               nestedIndex,
               1,
               8,
               nestedIndex,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               11,
               7,
               nestedIndex,
               11,
               8,
               nestedIndex,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
         }

         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 2, 0, 8, 2, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 4, 12, 2, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 0, 0, 8, 1, 3, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 0, 9, 8, 1, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 4, 3, 1, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 9, 0, 4, 12, 1, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int outerIndex = 4; outerIndex <= 8; outerIndex++) {
            for (int innerIndex = 0; innerIndex <= 2; innerIndex++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), outerIndex, -1, innerIndex, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), outerIndex, -1, 12 - innerIndex, structureBoundingBoxIn);
            }
         }

         for (int index2 = 0; index2 <= 2; index2++) {
            for (int innerIndex2 = 4; innerIndex2 <= 8; innerIndex2++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), index2, -1, innerIndex2, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), 12 - index2, -1, innerIndex2, structureBoundingBoxIn);
            }
         }

         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 5, 5, 7, 5, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 6, 6, 4, 6, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), 6, 0, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.flowing_lava.getDefaultState(), 6, 5, 6, structureBoundingBoxIn);
         BlockPos blockPos = new BlockPos(this.getXWithOffset(6, 6), this.getYWithOffset(5), this.getZWithOffset(6, 6));
         if (structureBoundingBoxIn.isVecInside(blockPos)) {
            worldIn.forceBlockUpdateTick(Blocks.flowing_lava, blockPos, randomIn);
         }

         return true;
      }

      public Entrance(int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox2;
      }

      public Entrance() {
      }

      public static StructureNetherBridgePieces.Entrance createPiece(
         List<StructureComponent> list2, Random random2, int i, int i2, int i3, EnumFacing enumFacing, int i4
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -5, -3, 13, 14, 13, enumFacing);
         return isAboveGround(structureBoundingBox) && StructureComponent.findIntersecting(list2, structureBoundingBox) == null
            ? new StructureNetherBridgePieces.Entrance(i4, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentNormal((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 5, 3, true);
      }
   }

   public static class NetherStalkRoom extends StructureNetherBridgePieces.Piece {
      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 0, 12, 4, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 0, 12, 13, 12, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 5, 0, 1, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 11, 5, 0, 12, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 11, 4, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, 5, 11, 10, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 9, 11, 7, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 0, 4, 12, 1, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, 5, 0, 10, 12, 1, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 9, 0, 7, 12, 1, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 11, 2, 10, 12, 10, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int index = 1; index <= 11; index += 2) {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               index,
               10,
               0,
               index,
               11,
               0,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               index,
               10,
               12,
               index,
               11,
               12,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               0,
               10,
               index,
               0,
               11,
               index,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               12,
               10,
               index,
               12,
               11,
               index,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), index, 13, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), index, 13, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), 0, 13, index, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), 12, 13, index, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), index + 1, 13, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), index + 1, 13, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 0, 13, index + 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 12, 13, index + 1, structureBoundingBoxIn);
         }

         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 0, 13, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 0, 13, 12, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 0, 13, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 12, 13, 0, structureBoundingBoxIn);

         for (int innerIndex = 3; innerIndex <= 9; innerIndex += 2) {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               1,
               7,
               innerIndex,
               1,
               8,
               innerIndex,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               11,
               7,
               innerIndex,
               11,
               8,
               innerIndex,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
         }

         int metadataWithOffset = this.getMetadataWithOffset(Blocks.nether_brick_stairs, 3);

         for (int innerIndex2 = 0; innerIndex2 <= 6; innerIndex2++) {
            int i = innerIndex2 + 4;

            for (int outerIndex = 5; outerIndex <= 7; outerIndex++) {
               this.setBlockState(
                  worldIn, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset), outerIndex, 5 + innerIndex2, i, structureBoundingBoxIn
               );
            }

            if (i >= 5 && i <= 8) {
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBoxIn,
                  5,
                  5,
                  i,
                  7,
                  innerIndex2 + 4,
                  i,
                  Blocks.nether_brick.getDefaultState(),
                  Blocks.nether_brick.getDefaultState(),
                  false
               );
            } else if (i >= 9 && i <= 10) {
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBoxIn,
                  5,
                  8,
                  i,
                  7,
                  innerIndex2 + 4,
                  i,
                  Blocks.nether_brick.getDefaultState(),
                  Blocks.nether_brick.getDefaultState(),
                  false
               );
            }

            if (innerIndex2 >= 1) {
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBoxIn,
                  5,
                  6 + innerIndex2,
                  i,
                  7,
                  9 + innerIndex2,
                  i,
                  Blocks.air.getDefaultState(),
                  Blocks.air.getDefaultState(),
                  false
               );
            }
         }

         for (int outerIndex2 = 5; outerIndex2 <= 7; outerIndex2++) {
            this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset), outerIndex2, 12, 11, structureBoundingBoxIn);
         }

         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 6, 7, 5, 7, 7, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 6, 7, 7, 7, 7, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 13, 12, 7, 13, 12, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 2, 3, 5, 3, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 9, 3, 5, 10, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 4, 2, 5, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 9, 5, 2, 10, 5, 3, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 9, 5, 9, 10, 5, 10, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 10, 5, 4, 10, 5, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         int metadataWithOffset2 = this.getMetadataWithOffset(Blocks.nether_brick_stairs, 0);
         int metadataWithOffset3 = this.getMetadataWithOffset(Blocks.nether_brick_stairs, 1);
         this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset3), 4, 5, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset3), 4, 5, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset3), 4, 5, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset3), 4, 5, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset2), 8, 5, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset2), 8, 5, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset2), 8, 5, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset2), 8, 5, 10, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 4, 4, 4, 4, 8, Blocks.soul_sand.getDefaultState(), Blocks.soul_sand.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 4, 4, 9, 4, 8, Blocks.soul_sand.getDefaultState(), Blocks.soul_sand.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 3, 5, 4, 4, 5, 8, Blocks.nether_wart.getDefaultState(), Blocks.nether_wart.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, 5, 4, 9, 5, 8, Blocks.nether_wart.getDefaultState(), Blocks.nether_wart.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 2, 0, 8, 2, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 4, 12, 2, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 0, 0, 8, 1, 3, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 0, 9, 8, 1, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 4, 3, 1, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 9, 0, 4, 12, 1, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int index2 = 4; index2 <= 8; index2++) {
            for (int index3 = 0; index3 <= 2; index3++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), index2, -1, index3, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), index2, -1, 12 - index3, structureBoundingBoxIn);
            }
         }

         for (int index4 = 0; index4 <= 2; index4++) {
            for (int index5 = 4; index5 <= 8; index5++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), index4, -1, index5, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), 12 - index4, -1, index5, structureBoundingBoxIn);
            }
         }

         return true;
      }

      public NetherStalkRoom() {
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentNormal((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 5, 3, true);
         this.getNextComponentNormal((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 5, 11, true);
      }

      public static StructureNetherBridgePieces.NetherStalkRoom createPiece(
         List<StructureComponent> list2, Random random2, int i, int i2, int i3, EnumFacing enumFacing, int i4
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -5, -3, 13, 14, 13, enumFacing);
         return isAboveGround(structureBoundingBox) && StructureComponent.findIntersecting(list2, structureBoundingBox) == null
            ? new StructureNetherBridgePieces.NetherStalkRoom(i4, random2, structureBoundingBox, enumFacing)
            : null;
      }

      public NetherStalkRoom(int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox2;
      }
   }

   abstract static class Piece extends StructureComponent {
      protected static final List<WeightedRandomChestContent> fortressChestContents = Lists.newArrayList(
         new WeightedRandomChestContent[]{
            new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 5),
            new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 5),
            new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 15),
            new WeightedRandomChestContent(Items.golden_sword, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.golden_chestplate, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.flint_and_steel, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.nether_wart, 0, 3, 7, 5),
            new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 10),
            new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 8),
            new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 3),
            new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.obsidian), 0, 2, 4, 2)
         }
      );

      protected StructureComponent getNextComponentX(
         StructureNetherBridgePieces.Start start, List<StructureComponent> list2, Random random2, int i, int i2, boolean flag2
      ) {
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case NORTH:
                  return this.generateAndAddPiece(
                     start,
                     list2,
                     random2,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY + i,
                     this.boundingBox.minZ + i2,
                     EnumFacing.WEST,
                     this.getComponentType(),
                     flag2
                  );
               case SOUTH:
                  return this.generateAndAddPiece(
                     start,
                     list2,
                     random2,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY + i,
                     this.boundingBox.minZ + i2,
                     EnumFacing.WEST,
                     this.getComponentType(),
                     flag2
                  );
               case WEST:
                  return this.generateAndAddPiece(
                     start,
                     list2,
                     random2,
                     this.boundingBox.minX + i2,
                     this.boundingBox.minY + i,
                     this.boundingBox.minZ - 1,
                     EnumFacing.NORTH,
                     this.getComponentType(),
                     flag2
                  );
               case EAST:
                  return this.generateAndAddPiece(
                     start,
                     list2,
                     random2,
                     this.boundingBox.minX + i2,
                     this.boundingBox.minY + i,
                     this.boundingBox.minZ - 1,
                     EnumFacing.NORTH,
                     this.getComponentType(),
                     flag2
                  );
            }
         }

         return null;
      }

      protected StructureComponent getNextComponentZ(
         StructureNetherBridgePieces.Start start, List<StructureComponent> list2, Random random2, int i, int i2, boolean flag2
      ) {
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case NORTH:
                  return this.generateAndAddPiece(
                     start,
                     list2,
                     random2,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY + i,
                     this.boundingBox.minZ + i2,
                     EnumFacing.EAST,
                     this.getComponentType(),
                     flag2
                  );
               case SOUTH:
                  return this.generateAndAddPiece(
                     start,
                     list2,
                     random2,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY + i,
                     this.boundingBox.minZ + i2,
                     EnumFacing.EAST,
                     this.getComponentType(),
                     flag2
                  );
               case WEST:
                  return this.generateAndAddPiece(
                     start,
                     list2,
                     random2,
                     this.boundingBox.minX + i2,
                     this.boundingBox.minY + i,
                     this.boundingBox.maxZ + 1,
                     EnumFacing.SOUTH,
                     this.getComponentType(),
                     flag2
                  );
               case EAST:
                  return this.generateAndAddPiece(
                     start,
                     list2,
                     random2,
                     this.boundingBox.minX + i2,
                     this.boundingBox.minY + i,
                     this.boundingBox.maxZ + 1,
                     EnumFacing.SOUTH,
                     this.getComponentType(),
                     flag2
                  );
            }
         }

         return null;
      }

      protected static boolean isAboveGround(StructureBoundingBox structureBoundingBox2) {
         return structureBoundingBox2 != null && structureBoundingBox2.minY > 10;
      }

      private int getTotalWeight(List<StructureNetherBridgePieces.PieceWeight> list2) {
         boolean flag2 = false;
         int i = 0;

         for (StructureNetherBridgePieces.PieceWeight pieceWeight2 : list2) {
            if (pieceWeight2.instancesLimit > 0 && pieceWeight2.instancesSpawned < pieceWeight2.instancesLimit) {
               flag2 = true;
            }

            i += pieceWeight2.pieceWeight;
         }

         return flag2 ? i : -1;
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
      }

      public Piece() {
      }

      protected StructureComponent getNextComponentNormal(
         StructureNetherBridgePieces.Start start, List<StructureComponent> list2, Random random2, int i, int i2, boolean flag2
      ) {
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case NORTH:
                  return this.generateAndAddPiece(
                     start,
                     list2,
                     random2,
                     this.boundingBox.minX + i,
                     this.boundingBox.minY + i2,
                     this.boundingBox.minZ - 1,
                     this.coordBaseMode,
                     this.getComponentType(),
                     flag2
                  );
               case SOUTH:
                  return this.generateAndAddPiece(
                     start,
                     list2,
                     random2,
                     this.boundingBox.minX + i,
                     this.boundingBox.minY + i2,
                     this.boundingBox.maxZ + 1,
                     this.coordBaseMode,
                     this.getComponentType(),
                     flag2
                  );
               case WEST:
                  return this.generateAndAddPiece(
                     start,
                     list2,
                     random2,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY + i2,
                     this.boundingBox.minZ + i,
                     this.coordBaseMode,
                     this.getComponentType(),
                     flag2
                  );
               case EAST:
                  return this.generateAndAddPiece(
                     start,
                     list2,
                     random2,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY + i2,
                     this.boundingBox.minZ + i,
                     this.coordBaseMode,
                     this.getComponentType(),
                     flag2
                  );
            }
         }

         return null;
      }

      protected Piece(int i) {
         super(i);
      }

      private StructureComponent generateAndAddPiece(
         StructureNetherBridgePieces.Start start,
         List<StructureComponent> list2,
         Random random2,
         int i,
         int i2,
         int i3,
         EnumFacing enumFacing,
         int i4,
         boolean flag2
      ) {
         if (Math.abs(i - start.getBoundingBox().minX) <= 112 && Math.abs(i3 - start.getBoundingBox().minZ) <= 112) {
            List<StructureNetherBridgePieces.PieceWeight> list = start.primaryWeights;
            if (flag2) {
               list = start.secondaryWeights;
            }

            StructureComponent structureComponent = this.generatePiece(start, list, list2, random2, i, i2, i3, enumFacing, i4 + 1);
            if (structureComponent != null) {
               list2.add(structureComponent);
               start.pendingChildren.add(structureComponent);
            }

            return structureComponent;
         } else {
            return StructureNetherBridgePieces.End.createPiece(list2, random2, i, i2, i3, enumFacing, i4);
         }
      }

      private StructureNetherBridgePieces.Piece generatePiece(
         StructureNetherBridgePieces.Start start,
         List<StructureNetherBridgePieces.PieceWeight> list2,
         List<StructureComponent> list3,
         Random random2,
         int i,
         int i2,
         int i3,
         EnumFacing enumFacing,
         int i4
      ) {
         int totalWeight = this.getTotalWeight(list2);
         boolean flag2 = totalWeight > 0 && i4 <= 30;
         int i5 = 0;

         while (i5 < 5 && flag2) {
            i5++;
            int i6 = random2.nextInt(totalWeight);

            for (StructureNetherBridgePieces.PieceWeight pieceWeight2 : list2) {
               i6 -= pieceWeight2.pieceWeight;
               if (i6 < 0) {
                  if (!pieceWeight2.isActive() || pieceWeight2 == start.theNetherBridgePieceWeight && !pieceWeight2.allowInRow) {
                     break;
                  }

                  StructureNetherBridgePieces.Piece piece = StructureNetherBridgePieces.createPiece(
                     pieceWeight2, list3, random2, i, i2, i3, enumFacing, i4
                  );
                  if (piece != null) {
                     pieceWeight2.instancesSpawned++;
                     start.theNetherBridgePieceWeight = pieceWeight2;
                     if (!pieceWeight2.canSpawnMoreStructures()) {
                        list2.remove(pieceWeight2);
                     }

                     return piece;
                  }
               }
            }
         }

         return StructureNetherBridgePieces.End.createPiece(list3, random2, i, i2, i3, enumFacing, i4);
      }
   }

   public static class PieceChild extends StructureNetherBridgePieces.Piece {
      private boolean flag;

      @Override
      protected void writeStructureToNBT(NBTTagCompound nbtTagCompound) {
         super.writeStructureToNBT(nbtTagCompound);
         nbtTagCompound.setBoolean("Chest", this.flag);
      }

      public static StructureNetherBridgePieces.PieceChild createPieceChild(List list2, Random random2, int i, int i2, int i3, EnumFacing enumFacing, int i4) {
         StructureBoundingBox structureBoundingBox2 = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -1, 0, 5, 7, 5, enumFacing);
         return isAboveGround(structureBoundingBox2) && StructureComponent.findIntersecting(list2, structureBoundingBox2) == null
            ? new StructureNetherBridgePieces.PieceChild(i4, random2, structureBoundingBox2, enumFacing)
            : null;
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound nbtTagCompound) {
         super.readStructureFromNBT(nbtTagCompound);
         this.flag = nbtTagCompound.getBoolean("Chest");
      }

      @Override
      public void buildComponent(StructureComponent structureComponent2, List list2, Random random2) {
         this.getNextComponentZ((StructureNetherBridgePieces.Start)structureComponent2, list2, random2, 0, 1, true);
      }

      @Override
      public boolean addComponentParts(World world2, Random random2, StructureBoundingBox structureBoundingBox2) {
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 0, 0, 4, 1, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 2, 0, 4, 5, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 2, 0, 0, 5, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 3, 1, 0, 4, 1, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 3, 3, 0, 4, 3, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 4, 2, 0, 4, 5, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 1, 2, 4, 4, 5, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 1, 3, 4, 1, 4, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 3, 3, 4, 3, 4, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         if (this.flag && structureBoundingBox2.isVecInside(new BlockPos(this.getXWithOffset(1, 3), this.getYWithOffset(2), this.getZWithOffset(1, 3)))) {
            this.flag = false;
            this.generateChestContents(world2, structureBoundingBox2, random2, 1, 2, 3, fortressChestContents, 2 + random2.nextInt(4));
         }

         this.fillWithBlocks(world2, structureBoundingBox2, 0, 6, 0, 4, 6, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);

         for (int i = 0; i <= 4; i++) {
            for (int i2 = 0; i2 <= 4; i2++) {
               this.replaceAirAndLiquidDownwards(world2, Blocks.nether_brick.getDefaultState(), i, -1, i2, structureBoundingBox2);
            }
         }

         return true;
      }

      public PieceChild(int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox2;
         this.flag = random2.nextInt(3) == 0;
      }

      public PieceChild() {
      }
   }

   public static class PieceChild2 extends StructureNetherBridgePieces.Piece {
      private boolean flag;

      @Override
      public boolean addComponentParts(World world2, Random random2, StructureBoundingBox structureBoundingBox2) {
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 0, 0, 4, 1, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 2, 0, 4, 5, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 4, 2, 0, 4, 5, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 4, 3, 1, 4, 4, 1, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 4, 3, 3, 4, 4, 3, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 2, 0, 0, 5, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 2, 4, 3, 5, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 1, 3, 4, 1, 4, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 3, 3, 4, 3, 4, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         if (this.flag && structureBoundingBox2.isVecInside(new BlockPos(this.getXWithOffset(3, 3), this.getYWithOffset(2), this.getZWithOffset(3, 3)))) {
            this.flag = false;
            this.generateChestContents(world2, structureBoundingBox2, random2, 3, 2, 3, fortressChestContents, 2 + random2.nextInt(4));
         }

         this.fillWithBlocks(world2, structureBoundingBox2, 0, 6, 0, 4, 6, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);

         for (int i = 0; i <= 4; i++) {
            for (int i2 = 0; i2 <= 4; i2++) {
               this.replaceAirAndLiquidDownwards(world2, Blocks.nether_brick.getDefaultState(), i, -1, i2, structureBoundingBox2);
            }
         }

         return true;
      }

      public static StructureNetherBridgePieces.PieceChild2 createPieceChild2(List list2, Random random2, int i, int i2, int i3, EnumFacing enumFacing, int i4) {
         StructureBoundingBox structureBoundingBox2 = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -1, 0, 5, 7, 5, enumFacing);
         return isAboveGround(structureBoundingBox2) && StructureComponent.findIntersecting(list2, structureBoundingBox2) == null
            ? new StructureNetherBridgePieces.PieceChild2(i4, random2, structureBoundingBox2, enumFacing)
            : null;
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound nbtTagCompound) {
         super.writeStructureToNBT(nbtTagCompound);
         nbtTagCompound.setBoolean("Chest", this.flag);
      }

      public PieceChild2(int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox2;
         this.flag = random2.nextInt(3) == 0;
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound nbtTagCompound) {
         super.readStructureFromNBT(nbtTagCompound);
         this.flag = nbtTagCompound.getBoolean("Chest");
      }

      @Override
      public void buildComponent(StructureComponent structureComponent2, List list2, Random random2) {
         this.getNextComponentX((StructureNetherBridgePieces.Start)structureComponent2, list2, random2, 0, 1, true);
      }

      public PieceChild2() {
      }
   }

   public static class PieceChild3 extends StructureNetherBridgePieces.Piece {
      public PieceChild3(int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox2;
      }

      public static StructureNetherBridgePieces.PieceChild3 createPieceChild3(List list2, Random random2, int i, int i2, int i3, EnumFacing enumFacing, int i4) {
         StructureBoundingBox structureBoundingBox2 = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -1, -3, 5, 10, 19, enumFacing);
         return isAboveGround(structureBoundingBox2) && StructureComponent.findIntersecting(list2, structureBoundingBox2) == null
            ? new StructureNetherBridgePieces.PieceChild3(i4, random2, structureBoundingBox2, enumFacing)
            : null;
      }

      @Override
      public void buildComponent(StructureComponent structureComponent2, List list2, Random random2) {
         this.getNextComponentNormal((StructureNetherBridgePieces.Start)structureComponent2, list2, random2, 1, 3, false);
      }

      @Override
      public boolean addComponentParts(World world2, Random random2, StructureBoundingBox structureBoundingBox2) {
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 3, 0, 4, 4, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 1, 5, 0, 3, 7, 18, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 5, 0, 0, 5, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 4, 5, 0, 4, 5, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 2, 0, 4, 2, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 2, 13, 4, 2, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 0, 0, 4, 1, 3, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 0, 15, 4, 1, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);

         for (int i = 0; i <= 4; i++) {
            for (int i2 = 0; i2 <= 2; i2++) {
               this.replaceAirAndLiquidDownwards(world2, Blocks.nether_brick.getDefaultState(), i, -1, i2, structureBoundingBox2);
               this.replaceAirAndLiquidDownwards(world2, Blocks.nether_brick.getDefaultState(), i, -1, 18 - i2, structureBoundingBox2);
            }
         }

         this.fillWithBlocks(world2, structureBoundingBox2, 0, 1, 1, 0, 4, 1, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 3, 4, 0, 4, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 3, 14, 0, 4, 14, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 1, 17, 0, 4, 17, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 4, 1, 1, 4, 4, 1, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 4, 3, 4, 4, 4, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 4, 3, 14, 4, 4, 14, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 4, 1, 17, 4, 4, 17, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);
         return true;
      }

      public PieceChild3() {
      }
   }

   public static class PieceChild4 extends StructureNetherBridgePieces.Piece {
      @Override
      public boolean addComponentParts(World world2, Random random2, StructureBoundingBox structureBoundingBox2) {
         int metadataWithOffset = this.getMetadataWithOffset(Blocks.nether_brick_stairs, 2);

         for (int i = 0; i <= 9; i++) {
            int i2 = Math.max(1, 7 - i);
            int i3 = Math.min(Math.max(i2 + 5, 14 - i), 13);
            int i4 = i;
            this.fillWithBlocks(world2, structureBoundingBox2, 0, 0, i, 4, i2, i, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
            this.fillWithBlocks(world2, structureBoundingBox2, 1, i2 + 1, i, 3, i3 - 1, i, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
            if (i <= 6) {
               this.setBlockState(world2, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset), 1, i2 + 1, i, structureBoundingBox2);
               this.setBlockState(world2, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset), 2, i2 + 1, i, structureBoundingBox2);
               this.setBlockState(world2, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset), 3, i2 + 1, i, structureBoundingBox2);
            }

            this.fillWithBlocks(
               world2, structureBoundingBox2, 0, i3, i, 4, i3, i, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
            );
            this.fillWithBlocks(
               world2, structureBoundingBox2, 0, i2 + 1, i, 0, i3 - 1, i, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
            );
            this.fillWithBlocks(
               world2, structureBoundingBox2, 4, i2 + 1, i, 4, i3 - 1, i, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
            );
            if ((i & 1) == 0) {
               this.fillWithBlocks(
                  world2,
                  structureBoundingBox2,
                  0,
                  i2 + 2,
                  i,
                  0,
                  i2 + 3,
                  i,
                  Blocks.nether_brick_fence.getDefaultState(),
                  Blocks.nether_brick_fence.getDefaultState(),
                  false
               );
               this.fillWithBlocks(
                  world2,
                  structureBoundingBox2,
                  4,
                  i2 + 2,
                  i,
                  4,
                  i2 + 3,
                  i,
                  Blocks.nether_brick_fence.getDefaultState(),
                  Blocks.nether_brick_fence.getDefaultState(),
                  false
               );
            }

            for (int i5 = 0; i5 <= 4; i5++) {
               this.replaceAirAndLiquidDownwards(world2, Blocks.nether_brick.getDefaultState(), i5, -1, i4, structureBoundingBox2);
            }
         }

         return true;
      }

      public static StructureNetherBridgePieces.PieceChild4 createPieceChild4(List list2, Random random2, int i, int i2, int i3, EnumFacing enumFacing, int i4) {
         StructureBoundingBox structureBoundingBox2 = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -1, -7, 5, 14, 10, enumFacing);
         return isAboveGround(structureBoundingBox2) && StructureComponent.findIntersecting(list2, structureBoundingBox2) == null
            ? new StructureNetherBridgePieces.PieceChild4(i4, random2, structureBoundingBox2, enumFacing)
            : null;
      }

      public PieceChild4() {
      }

      @Override
      public void buildComponent(StructureComponent structureComponent2, List list2, Random random2) {
         this.getNextComponentNormal((StructureNetherBridgePieces.Start)structureComponent2, list2, random2, 1, 0, true);
      }

      public PieceChild4(int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox2;
      }
   }

   public static class PieceChild5 extends StructureNetherBridgePieces.Piece {
      public PieceChild5(int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox2;
      }

      @Override
      public void buildComponent(StructureComponent structureComponent2, List list2, Random random2) {
         byte b = 1;
         if (this.coordBaseMode == EnumFacing.WEST || this.coordBaseMode == EnumFacing.NORTH) {
            b = 5;
         }

         StructureNetherBridgePieces.Start start = (StructureNetherBridgePieces.Start)structureComponent2;
         boolean flag2 = random2.nextInt(8) > 0;
         byte b2 = b;
         this.getNextComponentX(start, list2, random2, 0, b2, flag2);
         start = (StructureNetherBridgePieces.Start)structureComponent2;
         flag2 = random2.nextInt(8) > 0;
         b2 = b;
         this.getNextComponentZ(start, list2, random2, 0, b2, flag2);
      }

      public static StructureNetherBridgePieces.PieceChild5 createPieceChild5(List list2, Random random2, int i, int i2, int i3, EnumFacing enumFacing, int i4) {
         StructureBoundingBox structureBoundingBox2 = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -3, 0, 9, 7, 9, enumFacing);
         return isAboveGround(structureBoundingBox2) && StructureComponent.findIntersecting(list2, structureBoundingBox2) == null
            ? new StructureNetherBridgePieces.PieceChild5(i4, random2, structureBoundingBox2, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World world2, Random random2, StructureBoundingBox structureBoundingBox2) {
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 0, 0, 8, 1, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 2, 0, 8, 5, 8, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 6, 0, 8, 6, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 2, 0, 2, 5, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 6, 2, 0, 8, 5, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 1, 3, 0, 1, 4, 0, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 7, 3, 0, 7, 4, 0, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 2, 4, 8, 2, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 1, 1, 4, 2, 2, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 6, 1, 4, 7, 2, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 3, 8, 8, 3, 8, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 3, 6, 0, 3, 7, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 8, 3, 6, 8, 3, 7, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 3, 4, 0, 5, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 8, 3, 4, 8, 5, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 1, 3, 5, 2, 5, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 6, 3, 5, 7, 5, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 1, 4, 5, 1, 5, 5, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 7, 4, 5, 7, 5, 5, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);

         for (int i = 0; i <= 5; i++) {
            for (int i2 = 0; i2 <= 8; i2++) {
               this.replaceAirAndLiquidDownwards(world2, Blocks.nether_brick.getDefaultState(), i2, -1, i, structureBoundingBox2);
            }
         }

         return true;
      }

      public PieceChild5() {
      }
   }

   public static class PieceChild6 extends StructureNetherBridgePieces.Piece {
      @Override
      public void buildComponent(StructureComponent structureComponent2, List list2, Random random2) {
         this.getNextComponentNormal((StructureNetherBridgePieces.Start)structureComponent2, list2, random2, 2, 0, false);
         this.getNextComponentX((StructureNetherBridgePieces.Start)structureComponent2, list2, random2, 0, 2, false);
         this.getNextComponentZ((StructureNetherBridgePieces.Start)structureComponent2, list2, random2, 0, 2, false);
      }

      @Override
      public boolean addComponentParts(World world2, Random random2, StructureBoundingBox structureBoundingBox2) {
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 0, 0, 6, 1, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 2, 0, 6, 7, 6, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 2, 0, 1, 6, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 2, 6, 1, 6, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 5, 2, 0, 6, 6, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 5, 2, 6, 6, 6, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 2, 0, 0, 6, 1, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 2, 5, 0, 6, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 6, 2, 0, 6, 6, 1, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 6, 2, 5, 6, 6, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 2, 6, 0, 4, 6, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 2, 5, 0, 4, 5, 0, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 2, 6, 6, 4, 6, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 2, 5, 6, 4, 5, 6, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 6, 2, 0, 6, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 0, 5, 2, 0, 5, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 6, 6, 2, 6, 6, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
         this.fillWithBlocks(world2, structureBoundingBox2, 6, 5, 2, 6, 5, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);

         for (int i = 0; i <= 6; i++) {
            for (int i2 = 0; i2 <= 6; i2++) {
               this.replaceAirAndLiquidDownwards(world2, Blocks.nether_brick.getDefaultState(), i, -1, i2, structureBoundingBox2);
            }
         }

         return true;
      }

      public static StructureNetherBridgePieces.PieceChild6 createPieceChild6(List list2, Random random2, int i, int i2, int i3, EnumFacing enumFacing, int i4) {
         StructureBoundingBox structureBoundingBox2 = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -2, 0, 7, 9, 7, enumFacing);
         return isAboveGround(structureBoundingBox2) && StructureComponent.findIntersecting(list2, structureBoundingBox2) == null
            ? new StructureNetherBridgePieces.PieceChild6(i4, random2, structureBoundingBox2, enumFacing)
            : null;
      }

      public PieceChild6() {
      }

      public PieceChild6(int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox2;
      }
   }

   static class PieceWeight {
      public int instancesSpawned;
      public Class<? extends StructureNetherBridgePieces.Piece> weightClass;
      public final int pieceWeight;
      public int instancesLimit;
      public boolean allowInRow;

      public PieceWeight(Class<? extends StructureNetherBridgePieces.Piece> clazz, int i, int i2, boolean flag2) {
         this.weightClass = clazz;
         this.pieceWeight = i;
         this.instancesLimit = i2;
         this.allowInRow = flag2;
      }

      public boolean isActive() {
         return this.instancesLimit == 0 || this.instancesSpawned < this.instancesLimit;
      }

      public PieceWeight(Class<? extends StructureNetherBridgePieces.Piece> clazz, int i, int i2) {
         this(clazz, i, i2, false);
      }

      public boolean canSpawnMoreStructures() {
         return this.instancesLimit == 0 || this.instancesSpawned < this.instancesLimit;
      }
   }

   public static class Stairs extends StructureNetherBridgePieces.Piece {
      public Stairs() {
      }

      public static StructureNetherBridgePieces.Stairs createPiece(
         List<StructureComponent> list2, Random random2, int i, int i2, int i3, int i4, EnumFacing enumFacing
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -2, 0, 7, 11, 7, enumFacing);
         return isAboveGround(structureBoundingBox) && StructureComponent.findIntersecting(list2, structureBoundingBox) == null
            ? new StructureNetherBridgePieces.Stairs(i4, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentZ((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 6, 2, false);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 6, 1, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 6, 10, 6, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 0, 1, 8, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 2, 0, 6, 8, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 1, 0, 8, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 2, 1, 6, 8, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 2, 6, 5, 8, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 2, 0, 5, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 3, 2, 6, 5, 2, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 3, 4, 6, 5, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), 5, 2, 5, structureBoundingBoxIn);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 2, 5, 4, 3, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 3, 2, 5, 3, 4, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 2, 5, 2, 5, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 2, 5, 1, 6, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 7, 1, 5, 7, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 8, 2, 6, 8, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 6, 0, 4, 8, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 0, 4, 5, 0, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );

         for (int index = 0; index <= 6; index++) {
            for (int innerIndex = 0; innerIndex <= 6; innerIndex++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), index, -1, innerIndex, structureBoundingBoxIn);
            }
         }

         return true;
      }

      public Stairs(int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox2;
      }
   }

   public static class Start extends StructureNetherBridgePieces.Crossing3 {
      public List<StructureComponent> pendingChildren = Lists.newArrayList();
      public List<StructureNetherBridgePieces.PieceWeight> primaryWeights;
      public List<StructureNetherBridgePieces.PieceWeight> secondaryWeights;
      public StructureNetherBridgePieces.PieceWeight theNetherBridgePieceWeight;

      public Start(Random random2, int i, int i2) {
         super(random2, i, i2);
         this.primaryWeights = Lists.newArrayList();

         for (StructureNetherBridgePieces.PieceWeight pieceWeight2 : StructureNetherBridgePieces.primaryComponents) {
            pieceWeight2.instancesSpawned = 0;
            this.primaryWeights.add(pieceWeight2);
         }

         this.secondaryWeights = Lists.newArrayList();

         for (StructureNetherBridgePieces.PieceWeight pieceWeight3 : StructureNetherBridgePieces.secondaryComponents) {
            pieceWeight3.instancesSpawned = 0;
            this.secondaryWeights.add(pieceWeight3);
         }
      }

      public Start() {
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound nbtTagCompound) {
         super.writeStructureToNBT(nbtTagCompound);
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound nbtTagCompound) {
         super.readStructureFromNBT(nbtTagCompound);
      }
   }

   public static class Throne extends StructureNetherBridgePieces.Piece {
      private boolean hasSpawner;

      public Throne() {
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Mob", this.hasSpawner);
      }

      public static StructureNetherBridgePieces.Throne createPiece(
         List<StructureComponent> list2, Random random2, int i, int i2, int i3, int i4, EnumFacing enumFacing
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.createStructureBoundingBox(i, i2, i3, -2, 0, 7, 8, 9, enumFacing);
         return isAboveGround(structureBoundingBox) && StructureComponent.findIntersecting(list2, structureBoundingBox) == null
            ? new StructureNetherBridgePieces.Throne(i4, random2, structureBoundingBox, enumFacing)
            : null;
      }

      public Throne(int i, Random random2, StructureBoundingBox structureBoundingBox2, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox2;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 6, 7, 7, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 0, 0, 5, 1, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 2, 1, 5, 2, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 3, 2, 5, 3, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 4, 3, 5, 4, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 2, 0, 1, 4, 2, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 2, 0, 5, 4, 2, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 5, 2, 1, 5, 3, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 5, 2, 5, 5, 3, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 5, 3, 0, 5, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 5, 3, 6, 5, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 5, 8, 5, 5, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 1, 6, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 5, 6, 3, structureBoundingBoxIn);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 6, 3, 0, 6, 8, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 6, 3, 6, 6, 8, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 6, 8, 5, 7, 8, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 8, 8, 4, 8, 8, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         if (!this.hasSpawner) {
            BlockPos index = new BlockPos(this.getXWithOffset(3, 5), this.getYWithOffset(5), this.getZWithOffset(3, 5));
            if (structureBoundingBoxIn.isVecInside(index)) {
               this.hasSpawner = true;
               worldIn.setBlockState(index, Blocks.mob_spawner.getDefaultState(), 2);
               TileEntity tileEntity = worldIn.getTileEntity(index);
               if (tileEntity instanceof TileEntityMobSpawner) {
                  ((TileEntityMobSpawner)tileEntity).getSpawnerBaseLogic().setEntityName("Blaze");
               }
            }
         }

         for (int index = 0; index <= 6; index++) {
            for (int innerIndex = 0; innerIndex <= 6; innerIndex++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), index, -1, innerIndex, structureBoundingBoxIn);
            }
         }

         return true;
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.hasSpawner = tagCompound.getBoolean("Mob");
      }
   }
}
