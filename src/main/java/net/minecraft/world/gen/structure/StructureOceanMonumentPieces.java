package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class StructureOceanMonumentPieces {
   public static void registerOceanMonumentPieces() {
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.MonumentBuilding.class, "OMB");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.MonumentCoreRoom.class, "OMCR");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.DoubleXRoom.class, "OMDXR");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.DoubleXYRoom.class, "OMDXYR");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.DoubleYRoom.class, "OMDYR");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.DoubleYZRoom.class, "OMDYZR");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.DoubleZRoom.class, "OMDZR");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.EntryRoom.class, "OMEntry");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.Penthouse.class, "OMPenthouse");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.SimpleRoom.class, "OMSimple");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.SimpleTopRoom.class, "OMSimpleT");
   }

   public static class DoubleXRoom extends StructureOceanMonumentPieces.Piece {
      public DoubleXRoom() {
      }

      public DoubleXRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition2, Random random) {
         super(1, enumFacing, roomDefinition2, 2, 1, 1);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         StructureOceanMonumentPieces.RoomDefinition roomDefinition2 = this.roomDefinition.adjacentRooms[EnumFacing.EAST.getIndex()];
         StructureOceanMonumentPieces.RoomDefinition structureoceanmonumentpieces$roomdefinition1 = this.roomDefinition;
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(worldIn, structureBoundingBoxIn, 8, 0, roomDefinition2.connections[EnumFacing.DOWN.getIndex()]);
            this.generateDefaultFloor(
               worldIn, structureBoundingBoxIn, 0, 0, structureoceanmonumentpieces$roomdefinition1.connections[EnumFacing.DOWN.getIndex()]
            );
         }

         if (structureoceanmonumentpieces$roomdefinition1.adjacentRooms[EnumFacing.UP.getIndex()] == null) {
            this.fillIfWater(worldIn, structureBoundingBoxIn, 1, 4, 1, 7, 4, 6, BASE_PRISMARINE);
         }

         if (roomDefinition2.adjacentRooms[EnumFacing.UP.getIndex()] == null) {
            this.fillIfWater(worldIn, structureBoundingBoxIn, 8, 4, 1, 14, 4, 6, BASE_PRISMARINE);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 0, 0, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 15, 3, 0, 15, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 0, 15, 3, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 7, 14, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 2, 7, BASE_PRISMARINE, BASE_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 15, 2, 0, 15, 2, 7, BASE_PRISMARINE, BASE_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 15, 2, 0, BASE_PRISMARINE, BASE_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 7, 14, 2, 7, BASE_PRISMARINE, BASE_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 15, 1, 0, 15, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 15, 1, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 7, 14, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 0, 10, 1, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 2, 0, 9, 2, 3, BASE_PRISMARINE, BASE_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 3, 0, 10, 3, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.setBlockState(worldIn, SEA_LANTERN, 6, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, SEA_LANTERN, 9, 2, 3, structureBoundingBoxIn);
         if (structureoceanmonumentpieces$roomdefinition1.connections[EnumFacing.SOUTH.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 2, 0);
         }

         if (structureoceanmonumentpieces$roomdefinition1.connections[EnumFacing.NORTH.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 3, 1, 7, 4, 2, 7);
         }

         if (structureoceanmonumentpieces$roomdefinition1.connections[EnumFacing.WEST.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 0, 1, 3, 0, 2, 4);
         }

         if (roomDefinition2.connections[EnumFacing.SOUTH.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 11, 1, 0, 12, 2, 0);
         }

         if (roomDefinition2.connections[EnumFacing.NORTH.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 11, 1, 7, 12, 2, 7);
         }

         if (roomDefinition2.connections[EnumFacing.EAST.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 15, 1, 3, 15, 2, 4);
         }

         return true;
      }
   }

   public static class DoubleXYRoom extends StructureOceanMonumentPieces.Piece {
      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         StructureOceanMonumentPieces.RoomDefinition roomDefinition2 = this.roomDefinition.adjacentRooms[EnumFacing.EAST.getIndex()];
         StructureOceanMonumentPieces.RoomDefinition structureoceanmonumentpieces$roomdefinition1 = this.roomDefinition;
         StructureOceanMonumentPieces.RoomDefinition roomDefinition3 = structureoceanmonumentpieces$roomdefinition1.adjacentRooms[EnumFacing.UP.getIndex()];
         StructureOceanMonumentPieces.RoomDefinition roomDefinition4 = roomDefinition2.adjacentRooms[EnumFacing.UP.getIndex()];
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(worldIn, structureBoundingBoxIn, 8, 0, roomDefinition2.connections[EnumFacing.DOWN.getIndex()]);
            this.generateDefaultFloor(
               worldIn, structureBoundingBoxIn, 0, 0, structureoceanmonumentpieces$roomdefinition1.connections[EnumFacing.DOWN.getIndex()]
            );
         }

         if (roomDefinition3.adjacentRooms[EnumFacing.UP.getIndex()] == null) {
            this.fillIfWater(worldIn, structureBoundingBoxIn, 1, 8, 1, 7, 8, 6, BASE_PRISMARINE);
         }

         if (roomDefinition4.adjacentRooms[EnumFacing.UP.getIndex()] == null) {
            this.fillIfWater(worldIn, structureBoundingBoxIn, 8, 8, 1, 14, 8, 6, BASE_PRISMARINE);
         }

         for (int index = 1; index <= 7; index++) {
            IBlockState blockState = BRICKS_PRISMARINE;
            if (index == 2 || index == 6) {
               blockState = BASE_PRISMARINE;
            }

            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, index, 0, 0, index, 7, blockState, blockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 15, index, 0, 15, index, 7, blockState, blockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, index, 0, 15, index, 0, blockState, blockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, index, 7, 14, index, 7, blockState, blockState, false);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 3, 2, 7, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 2, 4, 7, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 5, 4, 7, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 13, 1, 3, 13, 7, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 11, 1, 2, 12, 7, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 11, 1, 5, 12, 7, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 3, 5, 3, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 1, 3, 10, 3, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 7, 2, 10, 7, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 5, 2, 5, 7, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 5, 2, 10, 7, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 5, 5, 5, 7, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 5, 5, 10, 7, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.setBlockState(worldIn, BRICKS_PRISMARINE, 6, 6, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, BRICKS_PRISMARINE, 9, 6, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, BRICKS_PRISMARINE, 6, 6, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, BRICKS_PRISMARINE, 9, 6, 5, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 4, 3, 6, 4, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 4, 3, 10, 4, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.setBlockState(worldIn, SEA_LANTERN, 5, 4, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, SEA_LANTERN, 5, 4, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, SEA_LANTERN, 10, 4, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, SEA_LANTERN, 10, 4, 5, structureBoundingBoxIn);
         if (structureoceanmonumentpieces$roomdefinition1.connections[EnumFacing.SOUTH.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 2, 0);
         }

         if (structureoceanmonumentpieces$roomdefinition1.connections[EnumFacing.NORTH.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 3, 1, 7, 4, 2, 7);
         }

         if (structureoceanmonumentpieces$roomdefinition1.connections[EnumFacing.WEST.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 0, 1, 3, 0, 2, 4);
         }

         if (roomDefinition2.connections[EnumFacing.SOUTH.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 11, 1, 0, 12, 2, 0);
         }

         if (roomDefinition2.connections[EnumFacing.NORTH.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 11, 1, 7, 12, 2, 7);
         }

         if (roomDefinition2.connections[EnumFacing.EAST.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 15, 1, 3, 15, 2, 4);
         }

         if (roomDefinition3.connections[EnumFacing.SOUTH.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 3, 5, 0, 4, 6, 0);
         }

         if (roomDefinition3.connections[EnumFacing.NORTH.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 3, 5, 7, 4, 6, 7);
         }

         if (roomDefinition3.connections[EnumFacing.WEST.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 0, 5, 3, 0, 6, 4);
         }

         if (roomDefinition4.connections[EnumFacing.SOUTH.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 11, 5, 0, 12, 6, 0);
         }

         if (roomDefinition4.connections[EnumFacing.NORTH.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 11, 5, 7, 12, 6, 7);
         }

         if (roomDefinition4.connections[EnumFacing.EAST.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 15, 5, 3, 15, 6, 4);
         }

         return true;
      }

      public DoubleXYRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition2, Random random) {
         super(1, enumFacing, roomDefinition2, 2, 2, 1);
      }

      public DoubleXYRoom() {
      }
   }

   public static class DoubleYRoom extends StructureOceanMonumentPieces.Piece {
      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(worldIn, structureBoundingBoxIn, 0, 0, this.roomDefinition.connections[EnumFacing.DOWN.getIndex()]);
         }

         StructureOceanMonumentPieces.RoomDefinition roomDefinition2 = this.roomDefinition.adjacentRooms[EnumFacing.UP.getIndex()];
         if (roomDefinition2.adjacentRooms[EnumFacing.UP.getIndex()] == null) {
            this.fillIfWater(worldIn, structureBoundingBoxIn, 1, 8, 1, 6, 8, 6, BASE_PRISMARINE);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 0, 0, 4, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 4, 0, 7, 4, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 0, 6, 4, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 7, 6, 4, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 4, 1, 2, 4, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 2, 1, 4, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 4, 1, 5, 4, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 4, 2, 6, 4, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 4, 5, 2, 4, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 5, 1, 4, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 4, 5, 5, 4, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 4, 5, 6, 4, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         StructureOceanMonumentPieces.RoomDefinition roomDefinition3 = this.roomDefinition;

         for (int i = 1; i <= 5; i += 4) {
            if (roomDefinition3.connections[EnumFacing.SOUTH.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, i, 0, 2, i + 2, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, i, 0, 5, i + 2, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, i + 2, 0, 4, i + 2, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            } else {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, i, 0, 7, i + 2, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, i + 1, 0, 7, i + 1, 0, BASE_PRISMARINE, BASE_PRISMARINE, false);
            }

            if (roomDefinition3.connections[EnumFacing.NORTH.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, i, 7, 2, i + 2, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, i, 7, 5, i + 2, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, i + 2, 7, 4, i + 2, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            } else {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, i, 7, 7, i + 2, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, i + 1, 7, 7, i + 1, 7, BASE_PRISMARINE, BASE_PRISMARINE, false);
            }

            if (roomDefinition3.connections[EnumFacing.WEST.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, i, 2, 0, i + 2, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, i, 5, 0, i + 2, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, i + 2, 3, 0, i + 2, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            } else {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, i, 0, 0, i + 2, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, i + 1, 0, 0, i + 1, 7, BASE_PRISMARINE, BASE_PRISMARINE, false);
            }

            if (roomDefinition3.connections[EnumFacing.EAST.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, i, 2, 7, i + 2, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, i, 5, 7, i + 2, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, i + 2, 3, 7, i + 2, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            } else {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, i, 0, 7, i + 2, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, i + 1, 0, 7, i + 1, 7, BASE_PRISMARINE, BASE_PRISMARINE, false);
            }

            roomDefinition3 = roomDefinition2;
         }

         return true;
      }

      public DoubleYRoom() {
      }

      public DoubleYRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition2, Random random) {
         super(1, enumFacing, roomDefinition2, 1, 2, 1);
      }
   }

   public static class DoubleYZRoom extends StructureOceanMonumentPieces.Piece {
      public DoubleYZRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition2, Random random) {
         super(1, enumFacing, roomDefinition2, 1, 2, 2);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         StructureOceanMonumentPieces.RoomDefinition roomDefinition2 = this.roomDefinition.adjacentRooms[EnumFacing.NORTH.getIndex()];
         StructureOceanMonumentPieces.RoomDefinition structureoceanmonumentpieces$roomdefinition1 = this.roomDefinition;
         StructureOceanMonumentPieces.RoomDefinition roomDefinition3 = roomDefinition2.adjacentRooms[EnumFacing.UP.getIndex()];
         StructureOceanMonumentPieces.RoomDefinition roomDefinition4 = structureoceanmonumentpieces$roomdefinition1.adjacentRooms[EnumFacing.UP.getIndex()];
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(worldIn, structureBoundingBoxIn, 0, 8, roomDefinition2.connections[EnumFacing.DOWN.getIndex()]);
            this.generateDefaultFloor(
               worldIn, structureBoundingBoxIn, 0, 0, structureoceanmonumentpieces$roomdefinition1.connections[EnumFacing.DOWN.getIndex()]
            );
         }

         if (roomDefinition4.adjacentRooms[EnumFacing.UP.getIndex()] == null) {
            this.fillIfWater(worldIn, structureBoundingBoxIn, 1, 8, 1, 6, 8, 7, BASE_PRISMARINE);
         }

         if (roomDefinition3.adjacentRooms[EnumFacing.UP.getIndex()] == null) {
            this.fillIfWater(worldIn, structureBoundingBoxIn, 1, 8, 8, 6, 8, 14, BASE_PRISMARINE);
         }

         for (int index = 1; index <= 7; index++) {
            IBlockState blockState = BRICKS_PRISMARINE;
            if (index == 2 || index == 6) {
               blockState = BASE_PRISMARINE;
            }

            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, index, 0, 0, index, 15, blockState, blockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, index, 0, 7, index, 15, blockState, blockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, index, 0, 6, index, 0, blockState, blockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, index, 15, 6, index, 15, blockState, blockState, false);
         }

         for (int innerIndex = 1; innerIndex <= 7; innerIndex++) {
            IBlockState blockState2 = DARK_PRISMARINE;
            if (innerIndex == 2 || innerIndex == 6) {
               blockState2 = SEA_LANTERN;
            }

            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, innerIndex, 7, 4, innerIndex, 8, blockState2, blockState2, false);
         }

         if (structureoceanmonumentpieces$roomdefinition1.connections[EnumFacing.SOUTH.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 2, 0);
         }

         if (structureoceanmonumentpieces$roomdefinition1.connections[EnumFacing.EAST.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 7, 1, 3, 7, 2, 4);
         }

         if (structureoceanmonumentpieces$roomdefinition1.connections[EnumFacing.WEST.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 0, 1, 3, 0, 2, 4);
         }

         if (roomDefinition2.connections[EnumFacing.NORTH.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 3, 1, 15, 4, 2, 15);
         }

         if (roomDefinition2.connections[EnumFacing.WEST.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 0, 1, 11, 0, 2, 12);
         }

         if (roomDefinition2.connections[EnumFacing.EAST.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 7, 1, 11, 7, 2, 12);
         }

         if (roomDefinition4.connections[EnumFacing.SOUTH.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 3, 5, 0, 4, 6, 0);
         }

         if (roomDefinition4.connections[EnumFacing.EAST.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 7, 5, 3, 7, 6, 4);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 4, 2, 6, 4, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 2, 6, 3, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 5, 6, 3, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         }

         if (roomDefinition4.connections[EnumFacing.WEST.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 0, 5, 3, 0, 6, 4);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 2, 2, 4, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 2, 1, 3, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 5, 1, 3, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         }

         if (roomDefinition3.connections[EnumFacing.NORTH.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 3, 5, 15, 4, 6, 15);
         }

         if (roomDefinition3.connections[EnumFacing.WEST.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 0, 5, 11, 0, 6, 12);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 10, 2, 4, 13, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 10, 1, 3, 10, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 13, 1, 3, 13, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         }

         if (roomDefinition3.connections[EnumFacing.EAST.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 7, 5, 11, 7, 6, 12);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 4, 10, 6, 4, 13, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 10, 6, 3, 10, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 13, 6, 3, 13, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         }

         return true;
      }

      public DoubleYZRoom() {
      }
   }

   public static class DoubleZRoom extends StructureOceanMonumentPieces.Piece {
      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         StructureOceanMonumentPieces.RoomDefinition roomDefinition2 = this.roomDefinition.adjacentRooms[EnumFacing.NORTH.getIndex()];
         StructureOceanMonumentPieces.RoomDefinition structureoceanmonumentpieces$roomdefinition1 = this.roomDefinition;
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(worldIn, structureBoundingBoxIn, 0, 8, roomDefinition2.connections[EnumFacing.DOWN.getIndex()]);
            this.generateDefaultFloor(
               worldIn, structureBoundingBoxIn, 0, 0, structureoceanmonumentpieces$roomdefinition1.connections[EnumFacing.DOWN.getIndex()]
            );
         }

         if (structureoceanmonumentpieces$roomdefinition1.adjacentRooms[EnumFacing.UP.getIndex()] == null) {
            this.fillIfWater(worldIn, structureBoundingBoxIn, 1, 4, 1, 6, 4, 7, BASE_PRISMARINE);
         }

         if (roomDefinition2.adjacentRooms[EnumFacing.UP.getIndex()] == null) {
            this.fillIfWater(worldIn, structureBoundingBoxIn, 1, 4, 8, 6, 4, 14, BASE_PRISMARINE);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 0, 0, 3, 15, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 3, 0, 7, 3, 15, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 0, 7, 3, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 15, 6, 3, 15, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 2, 15, BASE_PRISMARINE, BASE_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 0, 7, 2, 15, BASE_PRISMARINE, BASE_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 7, 2, 0, BASE_PRISMARINE, BASE_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 15, 6, 2, 15, BASE_PRISMARINE, BASE_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 1, 15, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 0, 7, 1, 15, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 7, 1, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 15, 6, 1, 15, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 1, 1, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 1, 6, 1, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 1, 1, 3, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 3, 1, 6, 3, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 13, 1, 1, 14, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 13, 6, 1, 14, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 13, 1, 3, 14, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 3, 13, 6, 3, 14, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 6, 2, 3, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 6, 5, 3, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 9, 2, 3, 9, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 9, 5, 3, 9, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 2, 6, 4, 2, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 2, 9, 4, 2, 9, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 2, 7, 2, 2, 8, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 2, 7, 5, 2, 8, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.setBlockState(worldIn, SEA_LANTERN, 2, 2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, SEA_LANTERN, 5, 2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, SEA_LANTERN, 2, 2, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, SEA_LANTERN, 5, 2, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, BRICKS_PRISMARINE, 2, 3, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, BRICKS_PRISMARINE, 5, 3, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, BRICKS_PRISMARINE, 2, 3, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, BRICKS_PRISMARINE, 5, 3, 10, structureBoundingBoxIn);
         if (structureoceanmonumentpieces$roomdefinition1.connections[EnumFacing.SOUTH.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 2, 0);
         }

         if (structureoceanmonumentpieces$roomdefinition1.connections[EnumFacing.EAST.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 7, 1, 3, 7, 2, 4);
         }

         if (structureoceanmonumentpieces$roomdefinition1.connections[EnumFacing.WEST.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 0, 1, 3, 0, 2, 4);
         }

         if (roomDefinition2.connections[EnumFacing.NORTH.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 3, 1, 15, 4, 2, 15);
         }

         if (roomDefinition2.connections[EnumFacing.WEST.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 0, 1, 11, 0, 2, 12);
         }

         if (roomDefinition2.connections[EnumFacing.EAST.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 7, 1, 11, 7, 2, 12);
         }

         return true;
      }

      public DoubleZRoom() {
      }

      public DoubleZRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition2, Random random) {
         super(1, enumFacing, roomDefinition2, 1, 1, 2);
      }
   }

   public static class EntryRoom extends StructureOceanMonumentPieces.Piece {
      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 0, 2, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 3, 0, 7, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 1, 2, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 2, 0, 7, 2, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 0, 7, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 7, 7, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 2, 3, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 0, 6, 3, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         if (this.roomDefinition.connections[EnumFacing.NORTH.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 3, 1, 7, 4, 2, 7);
         }

         if (this.roomDefinition.connections[EnumFacing.WEST.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 0, 1, 3, 1, 2, 4);
         }

         if (this.roomDefinition.connections[EnumFacing.EAST.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 6, 1, 3, 7, 2, 4);
         }

         return true;
      }

      public EntryRoom() {
      }

      public EntryRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition2) {
         super(1, enumFacing, roomDefinition2, 1, 1, 1);
      }
   }

   static class FitSimpleRoomHelper implements StructureOceanMonumentPieces.MonumentRoomFitHelper {
      @Override
      public boolean canFit(StructureOceanMonumentPieces.RoomDefinition roomDefinition2) {
         return true;
      }

      @Override
      public StructureOceanMonumentPieces.Piece createPiece(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition2, Random random) {
         roomDefinition2.claimed = true;
         return new StructureOceanMonumentPieces.SimpleRoom(enumFacing, roomDefinition2, random);
      }

      private FitSimpleRoomHelper() {
      }
   }

   static class FitSimpleRoomTopHelper implements StructureOceanMonumentPieces.MonumentRoomFitHelper {
      private FitSimpleRoomTopHelper() {
      }

      @Override
      public StructureOceanMonumentPieces.Piece createPiece(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition2, Random random) {
         roomDefinition2.claimed = true;
         return new StructureOceanMonumentPieces.SimpleTopRoom(enumFacing, roomDefinition2, random);
      }

      @Override
      public boolean canFit(StructureOceanMonumentPieces.RoomDefinition roomDefinition2) {
         return !roomDefinition2.connections[EnumFacing.WEST.getIndex()]
            && !roomDefinition2.connections[EnumFacing.EAST.getIndex()]
            && !roomDefinition2.connections[EnumFacing.NORTH.getIndex()]
            && !roomDefinition2.connections[EnumFacing.SOUTH.getIndex()]
            && !roomDefinition2.connections[EnumFacing.UP.getIndex()];
      }
   }

   public static class MonumentBuilding extends StructureOceanMonumentPieces.Piece {
      private StructureOceanMonumentPieces.RoomDefinition entryRoomDefinition;
      private StructureOceanMonumentPieces.RoomDefinition coreRoomDefinition;
      private List<StructureOceanMonumentPieces.Piece> childPieces = Lists.newArrayList();

      private void generateEntranceArch(World worldIn, Random random, StructureBoundingBox structureBoundingBox) {
         if (this.chunkIntersects(structureBoundingBox, 22, 5, 35, 17)) {
            this.handleWorld(worldIn, structureBoundingBox, 25, 0, 0, 32, 8, 20);

            for (int index = 0; index < 4; index++) {
               this.fillWithBlocks(worldIn, structureBoundingBox, 24, 2, 5 + index * 4, 24, 4, 5 + index * 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBox, 22, 4, 5 + index * 4, 23, 4, 5 + index * 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.setBlockState(worldIn, BRICKS_PRISMARINE, 25, 5, 5 + index * 4, structureBoundingBox);
               this.setBlockState(worldIn, BRICKS_PRISMARINE, 26, 6, 5 + index * 4, structureBoundingBox);
               this.setBlockState(worldIn, SEA_LANTERN, 26, 5, 5 + index * 4, structureBoundingBox);
               this.fillWithBlocks(worldIn, structureBoundingBox, 33, 2, 5 + index * 4, 33, 4, 5 + index * 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBox, 34, 4, 5 + index * 4, 35, 4, 5 + index * 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.setBlockState(worldIn, BRICKS_PRISMARINE, 32, 5, 5 + index * 4, structureBoundingBox);
               this.setBlockState(worldIn, BRICKS_PRISMARINE, 31, 6, 5 + index * 4, structureBoundingBox);
               this.setBlockState(worldIn, SEA_LANTERN, 31, 5, 5 + index * 4, structureBoundingBox);
               this.fillWithBlocks(worldIn, structureBoundingBox, 27, 6, 5 + index * 4, 30, 6, 5 + index * 4, BASE_PRISMARINE, BASE_PRISMARINE, false);
            }
         }
      }

      private void generateMiddleWall(World worldIn, Random random, StructureBoundingBox structureBoundingBox) {
         if (this.chunkIntersects(structureBoundingBox, 7, 21, 13, 50)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 7, 0, 21, 13, 0, 50, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.handleWorld(worldIn, structureBoundingBox, 7, 1, 21, 13, 10, 50);
            this.fillWithBlocks(worldIn, structureBoundingBox, 11, 8, 21, 13, 8, 53, BASE_PRISMARINE, BASE_PRISMARINE, false);

            for (int index = 0; index < 4; index++) {
               this.fillWithBlocks(worldIn, structureBoundingBox, index + 7, index + 5, 21, index + 7, index + 5, 54, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            for (int innerIndex = 21; innerIndex <= 45; innerIndex += 3) {
               this.setBlockState(worldIn, ACCENT_PRISMARINE, 12, 9, innerIndex, structureBoundingBox);
            }
         }

         if (this.chunkIntersects(structureBoundingBox, 44, 21, 50, 54)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 44, 0, 21, 50, 0, 50, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.handleWorld(worldIn, structureBoundingBox, 44, 1, 21, 50, 10, 50);
            this.fillWithBlocks(worldIn, structureBoundingBox, 44, 8, 21, 46, 8, 53, BASE_PRISMARINE, BASE_PRISMARINE, false);

            for (int nestedIndex = 0; nestedIndex < 4; nestedIndex++) {
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, 50 - nestedIndex, nestedIndex + 5, 21, 50 - nestedIndex, nestedIndex + 5, 54, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false
               );
            }

            for (int outerIndex = 21; outerIndex <= 45; outerIndex += 3) {
               this.setBlockState(worldIn, ACCENT_PRISMARINE, 45, 9, outerIndex, structureBoundingBox);
            }
         }

         if (this.chunkIntersects(structureBoundingBox, 8, 44, 49, 54)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 14, 0, 44, 43, 0, 50, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.handleWorld(worldIn, structureBoundingBox, 14, 1, 44, 43, 10, 50);

            for (int index2 = 12; index2 <= 45; index2 += 3) {
               this.setBlockState(worldIn, ACCENT_PRISMARINE, index2, 9, 45, structureBoundingBox);
               this.setBlockState(worldIn, ACCENT_PRISMARINE, index2, 9, 52, structureBoundingBox);
               if (index2 == 12 || index2 == 18 || index2 == 24 || index2 == 33 || index2 == 39 || index2 == 45) {
                  this.setBlockState(worldIn, ACCENT_PRISMARINE, index2, 9, 47, structureBoundingBox);
                  this.setBlockState(worldIn, ACCENT_PRISMARINE, index2, 9, 50, structureBoundingBox);
                  this.setBlockState(worldIn, ACCENT_PRISMARINE, index2, 10, 45, structureBoundingBox);
                  this.setBlockState(worldIn, ACCENT_PRISMARINE, index2, 10, 46, structureBoundingBox);
                  this.setBlockState(worldIn, ACCENT_PRISMARINE, index2, 10, 51, structureBoundingBox);
                  this.setBlockState(worldIn, ACCENT_PRISMARINE, index2, 10, 52, structureBoundingBox);
                  this.setBlockState(worldIn, ACCENT_PRISMARINE, index2, 11, 47, structureBoundingBox);
                  this.setBlockState(worldIn, ACCENT_PRISMARINE, index2, 11, 50, structureBoundingBox);
                  this.setBlockState(worldIn, ACCENT_PRISMARINE, index2, 12, 48, structureBoundingBox);
                  this.setBlockState(worldIn, ACCENT_PRISMARINE, index2, 12, 49, structureBoundingBox);
               }
            }

            for (int innerIndex2 = 0; innerIndex2 < 3; innerIndex2++) {
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, 8 + innerIndex2, 5 + innerIndex2, 54, 49 - innerIndex2, 5 + innerIndex2, 54, BASE_PRISMARINE, BASE_PRISMARINE, false
               );
            }

            this.fillWithBlocks(worldIn, structureBoundingBox, 11, 8, 54, 46, 8, 54, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 14, 8, 44, 43, 8, 53, BASE_PRISMARINE, BASE_PRISMARINE, false);
         }
      }

      private void generateEntranceWall(World worldIn, Random random, StructureBoundingBox structureBoundingBox) {
         if (this.chunkIntersects(structureBoundingBox, 15, 20, 42, 21)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 15, 0, 21, 42, 0, 21, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.handleWorld(worldIn, structureBoundingBox, 26, 1, 21, 31, 3, 21);
            this.fillWithBlocks(worldIn, structureBoundingBox, 21, 12, 21, 36, 12, 21, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 17, 11, 21, 40, 11, 21, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 16, 10, 21, 41, 10, 21, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 15, 7, 21, 42, 9, 21, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 16, 6, 21, 41, 6, 21, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 17, 5, 21, 40, 5, 21, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 21, 4, 21, 36, 4, 21, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 22, 3, 21, 26, 3, 21, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 31, 3, 21, 35, 3, 21, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 23, 2, 21, 25, 2, 21, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 32, 2, 21, 34, 2, 21, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 28, 4, 20, 29, 4, 21, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.setBlockState(worldIn, BRICKS_PRISMARINE, 27, 3, 21, structureBoundingBox);
            this.setBlockState(worldIn, BRICKS_PRISMARINE, 30, 3, 21, structureBoundingBox);
            this.setBlockState(worldIn, BRICKS_PRISMARINE, 26, 2, 21, structureBoundingBox);
            this.setBlockState(worldIn, BRICKS_PRISMARINE, 31, 2, 21, structureBoundingBox);
            this.setBlockState(worldIn, BRICKS_PRISMARINE, 25, 1, 21, structureBoundingBox);
            this.setBlockState(worldIn, BRICKS_PRISMARINE, 32, 1, 21, structureBoundingBox);

            for (int index = 0; index < 7; index++) {
               this.setBlockState(worldIn, DARK_PRISMARINE, 28 - index, 6 + index, 21, structureBoundingBox);
               this.setBlockState(worldIn, DARK_PRISMARINE, 29 + index, 6 + index, 21, structureBoundingBox);
            }

            for (int innerIndex = 0; innerIndex < 4; innerIndex++) {
               this.setBlockState(worldIn, DARK_PRISMARINE, 28 - innerIndex, 9 + innerIndex, 21, structureBoundingBox);
               this.setBlockState(worldIn, DARK_PRISMARINE, 29 + innerIndex, 9 + innerIndex, 21, structureBoundingBox);
            }

            this.setBlockState(worldIn, DARK_PRISMARINE, 28, 12, 21, structureBoundingBox);
            this.setBlockState(worldIn, DARK_PRISMARINE, 29, 12, 21, structureBoundingBox);

            for (int nestedIndex = 0; nestedIndex < 3; nestedIndex++) {
               this.setBlockState(worldIn, DARK_PRISMARINE, 22 - nestedIndex * 2, 8, 21, structureBoundingBox);
               this.setBlockState(worldIn, DARK_PRISMARINE, 22 - nestedIndex * 2, 9, 21, structureBoundingBox);
               this.setBlockState(worldIn, DARK_PRISMARINE, 35 + nestedIndex * 2, 8, 21, structureBoundingBox);
               this.setBlockState(worldIn, DARK_PRISMARINE, 35 + nestedIndex * 2, 9, 21, structureBoundingBox);
            }

            this.handleWorld(worldIn, structureBoundingBox, 15, 13, 21, 42, 15, 21);
            this.handleWorld(worldIn, structureBoundingBox, 15, 1, 21, 15, 6, 21);
            this.handleWorld(worldIn, structureBoundingBox, 16, 1, 21, 16, 5, 21);
            this.handleWorld(worldIn, structureBoundingBox, 17, 1, 21, 20, 4, 21);
            this.handleWorld(worldIn, structureBoundingBox, 21, 1, 21, 21, 3, 21);
            this.handleWorld(worldIn, structureBoundingBox, 22, 1, 21, 22, 2, 21);
            this.handleWorld(worldIn, structureBoundingBox, 23, 1, 21, 24, 1, 21);
            this.handleWorld(worldIn, structureBoundingBox, 42, 1, 21, 42, 6, 21);
            this.handleWorld(worldIn, structureBoundingBox, 41, 1, 21, 41, 5, 21);
            this.handleWorld(worldIn, structureBoundingBox, 37, 1, 21, 40, 4, 21);
            this.handleWorld(worldIn, structureBoundingBox, 36, 1, 21, 36, 3, 21);
            this.handleWorld(worldIn, structureBoundingBox, 33, 1, 21, 34, 1, 21);
            this.handleWorld(worldIn, structureBoundingBox, 35, 1, 21, 35, 2, 21);
         }
      }

      private void generateUpperWall(World worldIn, Random random, StructureBoundingBox structureBoundingBox) {
         if (this.chunkIntersects(structureBoundingBox, 14, 21, 20, 43)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 14, 0, 21, 20, 0, 43, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.handleWorld(worldIn, structureBoundingBox, 14, 1, 22, 20, 14, 43);
            this.fillWithBlocks(worldIn, structureBoundingBox, 18, 12, 22, 20, 12, 39, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 18, 12, 21, 20, 12, 21, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);

            for (int index = 0; index < 4; index++) {
               this.fillWithBlocks(worldIn, structureBoundingBox, index + 14, index + 9, 21, index + 14, index + 9, 43 - index, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            for (int innerIndex = 23; innerIndex <= 39; innerIndex += 3) {
               this.setBlockState(worldIn, ACCENT_PRISMARINE, 19, 13, innerIndex, structureBoundingBox);
            }
         }

         if (this.chunkIntersects(structureBoundingBox, 37, 21, 43, 43)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 37, 0, 21, 43, 0, 43, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.handleWorld(worldIn, structureBoundingBox, 37, 1, 22, 43, 14, 43);
            this.fillWithBlocks(worldIn, structureBoundingBox, 37, 12, 22, 39, 12, 39, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 37, 12, 21, 39, 12, 21, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);

            for (int nestedIndex = 0; nestedIndex < 4; nestedIndex++) {
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBox,
                  43 - nestedIndex,
                  nestedIndex + 9,
                  21,
                  43 - nestedIndex,
                  nestedIndex + 9,
                  43 - nestedIndex,
                  BRICKS_PRISMARINE,
                  BRICKS_PRISMARINE,
                  false
               );
            }

            for (int outerIndex = 23; outerIndex <= 39; outerIndex += 3) {
               this.setBlockState(worldIn, ACCENT_PRISMARINE, 38, 13, outerIndex, structureBoundingBox);
            }
         }

         if (this.chunkIntersects(structureBoundingBox, 15, 37, 42, 43)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 21, 0, 37, 36, 0, 43, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.handleWorld(worldIn, structureBoundingBox, 21, 1, 37, 36, 14, 43);
            this.fillWithBlocks(worldIn, structureBoundingBox, 21, 12, 37, 36, 12, 39, BASE_PRISMARINE, BASE_PRISMARINE, false);

            for (int index2 = 0; index2 < 4; index2++) {
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, 15 + index2, index2 + 9, 43 - index2, 42 - index2, index2 + 9, 43 - index2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false
               );
            }

            for (int innerIndex2 = 21; innerIndex2 <= 36; innerIndex2 += 3) {
               this.setBlockState(worldIn, ACCENT_PRISMARINE, innerIndex2, 13, 38, structureBoundingBox);
            }
         }
      }

      public MonumentBuilding(Random random, int i, int i2, EnumFacing enumFacing) {
         super(0);
         this.coordBaseMode = enumFacing;
         switch (this.coordBaseMode) {
            case NORTH:
            case SOUTH:
               this.boundingBox = new StructureBoundingBox(i, 39, i2, i + 58 - 1, 61, i2 + 58 - 1);
               break;
            default:
               this.boundingBox = new StructureBoundingBox(i, 39, i2, i + 58 - 1, 61, i2 + 58 - 1);
         }

         List list = this.generateRoomGraph(random);
         this.entryRoomDefinition.claimed = true;
         this.childPieces.add(new StructureOceanMonumentPieces.EntryRoom(this.coordBaseMode, this.entryRoomDefinition));
         this.childPieces.add(new StructureOceanMonumentPieces.MonumentCoreRoom(this.coordBaseMode, this.coreRoomDefinition, random));
         ArrayList arrayList = Lists.newArrayList();
         arrayList.add(new StructureOceanMonumentPieces.XYDoubleRoomFitHelper());
         arrayList.add(new StructureOceanMonumentPieces.YZDoubleRoomFitHelper());
         arrayList.add(new StructureOceanMonumentPieces.ZDoubleRoomFitHelper());
         arrayList.add(new StructureOceanMonumentPieces.XDoubleRoomFitHelper());
         arrayList.add(new StructureOceanMonumentPieces.YDoubleRoomFitHelper());
         arrayList.add(new StructureOceanMonumentPieces.FitSimpleRoomTopHelper());
         arrayList.add(new StructureOceanMonumentPieces.FitSimpleRoomHelper());

         for (StructureOceanMonumentPieces.RoomDefinition roomDefinition2 : (Iterable<StructureOceanMonumentPieces.RoomDefinition>) list) {
            if (!roomDefinition2.claimed && !roomDefinition2.isSpecialRoom()) {
               for (StructureOceanMonumentPieces.MonumentRoomFitHelper monumentRoomFitHelper : (Iterable<StructureOceanMonumentPieces.MonumentRoomFitHelper>) arrayList) {
                  if (monumentRoomFitHelper.canFit(roomDefinition2)) {
                     this.childPieces.add(monumentRoomFitHelper.createPiece(this.coordBaseMode, roomDefinition2, random));
                     break;
                  }
               }
            }
         }

         int minY2 = this.boundingBox.minY;
         int xWithOffset = this.getXWithOffset(9, 22);
         int zWithOffset = this.getZWithOffset(9, 22);

         for (StructureOceanMonumentPieces.Piece piece : this.childPieces) {
            piece.getBoundingBox().offset(xWithOffset, minY2, zWithOffset);
         }

         StructureBoundingBox structureBoundingBox = StructureBoundingBox.fromCorners(
            this.getXWithOffset(1, 1),
            this.getYWithOffset(1),
            this.getZWithOffset(1, 1),
            this.getXWithOffset(23, 21),
            this.getYWithOffset(8),
            this.getZWithOffset(23, 21)
         );
         StructureBoundingBox structureBoundingBox2 = StructureBoundingBox.fromCorners(
            this.getXWithOffset(34, 1),
            this.getYWithOffset(1),
            this.getZWithOffset(34, 1),
            this.getXWithOffset(56, 21),
            this.getYWithOffset(8),
            this.getZWithOffset(56, 21)
         );
         StructureBoundingBox structureBoundingBox3 = StructureBoundingBox.fromCorners(
            this.getXWithOffset(22, 22),
            this.getYWithOffset(13),
            this.getZWithOffset(22, 22),
            this.getXWithOffset(35, 35),
            this.getYWithOffset(17),
            this.getZWithOffset(35, 35)
         );
         int i3 = random.nextInt();
         this.childPieces.add(new StructureOceanMonumentPieces.WingRoom(this.coordBaseMode, structureBoundingBox, i3++));
         this.childPieces.add(new StructureOceanMonumentPieces.WingRoom(this.coordBaseMode, structureBoundingBox2, i3));
         this.childPieces.add(new StructureOceanMonumentPieces.Penthouse(this.coordBaseMode, structureBoundingBox3));
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         int i = Math.max(worldIn.getSeaLevel(), 64) - this.boundingBox.minY;
         this.handleWorld(worldIn, structureBoundingBoxIn, 0, 0, 0, 58, i, 58);
         this.generateWing(false, 0, worldIn, randomIn, structureBoundingBoxIn);
         this.generateWing(true, 33, worldIn, randomIn, structureBoundingBoxIn);
         this.generateEntranceArch(worldIn, randomIn, structureBoundingBoxIn);
         this.generateEntranceWall(worldIn, randomIn, structureBoundingBoxIn);
         this.generateRoofPiece(worldIn, randomIn, structureBoundingBoxIn);
         this.generateLowerWall(worldIn, randomIn, structureBoundingBoxIn);
         this.generateMiddleWall(worldIn, randomIn, structureBoundingBoxIn);
         this.generateUpperWall(worldIn, randomIn, structureBoundingBoxIn);

         for (int i2 = 0; i2 < 7; i2++) {
            int i3 = 0;

            while (i3 < 7) {
               if (i3 == 0 && i2 == 3) {
                  i3 = 6;
               }

               int i4 = i2 * 9;
               int i5 = i3 * 9;

               for (int i6 = 0; i6 < 4; i6++) {
                  for (int i7 = 0; i7 < 4; i7++) {
                     this.setBlockState(worldIn, BRICKS_PRISMARINE, i4 + i6, 0, i5 + i7, structureBoundingBoxIn);
                     this.replaceAirAndLiquidDownwards(worldIn, BRICKS_PRISMARINE, i4 + i6, -1, i5 + i7, structureBoundingBoxIn);
                  }
               }

               if (i2 != 0 && i2 != 6) {
                  i3 += 6;
               } else {
                  i3++;
               }
            }
         }

         for (int i8 = 0; i8 < 5; i8++) {
            this.handleWorld(worldIn, structureBoundingBoxIn, -1 - i8, 0 + i8 * 2, -1 - i8, -1 - i8, 23, 58 + i8);
            this.handleWorld(worldIn, structureBoundingBoxIn, 58 + i8, 0 + i8 * 2, -1 - i8, 58 + i8, 23, 58 + i8);
            this.handleWorld(worldIn, structureBoundingBoxIn, 0 - i8, 0 + i8 * 2, -1 - i8, 57 + i8, 23, -1 - i8);
            this.handleWorld(worldIn, structureBoundingBoxIn, 0 - i8, 0 + i8 * 2, 58 + i8, 57 + i8, 23, 58 + i8);
         }

         for (StructureOceanMonumentPieces.Piece piece : this.childPieces) {
            if (piece.getBoundingBox().intersectsWith(structureBoundingBoxIn)) {
               piece.addComponentParts(worldIn, randomIn, structureBoundingBoxIn);
            }
         }

         return true;
      }

      public MonumentBuilding() {
      }

      private void generateLowerWall(World worldIn, Random random, StructureBoundingBox structureBoundingBox) {
         if (this.chunkIntersects(structureBoundingBox, 0, 21, 6, 58)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 0, 0, 21, 6, 0, 57, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.handleWorld(worldIn, structureBoundingBox, 0, 1, 21, 6, 7, 57);
            this.fillWithBlocks(worldIn, structureBoundingBox, 4, 4, 21, 6, 4, 53, BASE_PRISMARINE, BASE_PRISMARINE, false);

            for (int index = 0; index < 4; index++) {
               this.fillWithBlocks(worldIn, structureBoundingBox, index, index + 1, 21, index, index + 1, 57 - index, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            for (int innerIndex = 23; innerIndex < 53; innerIndex += 3) {
               this.setBlockState(worldIn, ACCENT_PRISMARINE, 5, 5, innerIndex, structureBoundingBox);
            }

            this.setBlockState(worldIn, ACCENT_PRISMARINE, 5, 5, 52, structureBoundingBox);

            for (int nestedIndex = 0; nestedIndex < 4; nestedIndex++) {
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBox,
                  nestedIndex,
                  nestedIndex + 1,
                  21,
                  nestedIndex,
                  nestedIndex + 1,
                  57 - nestedIndex,
                  BRICKS_PRISMARINE,
                  BRICKS_PRISMARINE,
                  false
               );
            }

            this.fillWithBlocks(worldIn, structureBoundingBox, 4, 1, 52, 6, 3, 52, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 5, 1, 51, 5, 3, 53, BASE_PRISMARINE, BASE_PRISMARINE, false);
         }

         if (this.chunkIntersects(structureBoundingBox, 51, 21, 58, 58)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 51, 0, 21, 57, 0, 57, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.handleWorld(worldIn, structureBoundingBox, 51, 1, 21, 57, 7, 57);
            this.fillWithBlocks(worldIn, structureBoundingBox, 51, 4, 21, 53, 4, 53, BASE_PRISMARINE, BASE_PRISMARINE, false);

            for (int outerIndex = 0; outerIndex < 4; outerIndex++) {
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBox,
                  57 - outerIndex,
                  outerIndex + 1,
                  21,
                  57 - outerIndex,
                  outerIndex + 1,
                  57 - outerIndex,
                  BRICKS_PRISMARINE,
                  BRICKS_PRISMARINE,
                  false
               );
            }

            for (int index2 = 23; index2 < 53; index2 += 3) {
               this.setBlockState(worldIn, ACCENT_PRISMARINE, 52, 5, index2, structureBoundingBox);
            }

            this.setBlockState(worldIn, ACCENT_PRISMARINE, 52, 5, 52, structureBoundingBox);
            this.fillWithBlocks(worldIn, structureBoundingBox, 51, 1, 52, 53, 3, 52, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 52, 1, 51, 52, 3, 53, BASE_PRISMARINE, BASE_PRISMARINE, false);
         }

         if (this.chunkIntersects(structureBoundingBox, 0, 51, 57, 57)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 7, 0, 51, 50, 0, 57, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.handleWorld(worldIn, structureBoundingBox, 7, 1, 51, 50, 10, 57);

            for (int innerIndex2 = 0; innerIndex2 < 4; innerIndex2++) {
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBox,
                  innerIndex2 + 1,
                  innerIndex2 + 1,
                  57 - innerIndex2,
                  56 - innerIndex2,
                  innerIndex2 + 1,
                  57 - innerIndex2,
                  BRICKS_PRISMARINE,
                  BRICKS_PRISMARINE,
                  false
               );
            }
         }
      }

      private void generateWing(boolean flag, int i, World worldIn, Random random, StructureBoundingBox structureBoundingBox) {
         if (this.chunkIntersects(structureBoundingBox, i, 0, i + 23, 20)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 0, 0, 0, i + 24, 0, 20, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.handleWorld(worldIn, structureBoundingBox, i + 0, 1, 0, i + 24, 10, 20);

            for (int i2 = 0; i2 < 4; i2++) {
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, i + i2, i2 + 1, i2, i + i2, i2 + 1, 20, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false
               );
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBox,
                  i + i2 + 7,
                  i2 + 5,
                  i2 + 7,
                  i + i2 + 7,
                  i2 + 5,
                  20,
                  BRICKS_PRISMARINE,
                  BRICKS_PRISMARINE,
                  false
               );
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBox,
                  i + 17 - i2,
                  i2 + 5,
                  i2 + 7,
                  i + 17 - i2,
                  i2 + 5,
                  20,
                  BRICKS_PRISMARINE,
                  BRICKS_PRISMARINE,
                  false
               );
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, i + 24 - i2, i2 + 1, i2, i + 24 - i2, i2 + 1, 20, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false
               );
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBox,
                  i + i2 + 1,
                  i2 + 1,
                  i2,
                  i + 23 - i2,
                  i2 + 1,
                  i2,
                  BRICKS_PRISMARINE,
                  BRICKS_PRISMARINE,
                  false
               );
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBox,
                  i + i2 + 8,
                  i2 + 5,
                  i2 + 7,
                  i + 16 - i2,
                  i2 + 5,
                  i2 + 7,
                  BRICKS_PRISMARINE,
                  BRICKS_PRISMARINE,
                  false
               );
            }

            this.fillWithBlocks(worldIn, structureBoundingBox, i + 4, 4, 4, i + 6, 4, 20, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 7, 4, 4, i + 17, 4, 6, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 18, 4, 4, i + 20, 4, 20, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 11, 8, 11, i + 13, 8, 20, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.setBlockState(worldIn, ACCENT_PRISMARINE, i + 12, 9, 12, structureBoundingBox);
            this.setBlockState(worldIn, ACCENT_PRISMARINE, i + 12, 9, 15, structureBoundingBox);
            this.setBlockState(worldIn, ACCENT_PRISMARINE, i + 12, 9, 18, structureBoundingBox);
            int i3 = flag ? i + 19 : i + 5;
            int i4 = flag ? i + 5 : i + 19;

            for (int i5 = 20; i5 >= 5; i5 -= 3) {
               this.setBlockState(worldIn, ACCENT_PRISMARINE, i3, 5, i5, structureBoundingBox);
            }

            for (int i6 = 19; i6 >= 7; i6 -= 3) {
               this.setBlockState(worldIn, ACCENT_PRISMARINE, i4, 5, i6, structureBoundingBox);
            }

            for (int i7 = 0; i7 < 4; i7++) {
               int i8 = flag ? i + (24 - (17 - i7 * 3)) : i + 17 - i7 * 3;
               this.setBlockState(worldIn, ACCENT_PRISMARINE, i8, 5, 5, structureBoundingBox);
            }

            this.setBlockState(worldIn, ACCENT_PRISMARINE, i4, 5, 5, structureBoundingBox);
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 11, 1, 12, i + 13, 7, 12, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 12, 1, 11, i + 12, 7, 13, BASE_PRISMARINE, BASE_PRISMARINE, false);
         }
      }

      private void generateRoofPiece(World worldIn, Random random, StructureBoundingBox structureBoundingBox) {
         if (this.chunkIntersects(structureBoundingBox, 21, 21, 36, 36)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 21, 0, 22, 36, 0, 36, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.handleWorld(worldIn, structureBoundingBox, 21, 1, 22, 36, 23, 36);

            for (int index = 0; index < 4; index++) {
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, 21 + index, 13 + index, 21 + index, 36 - index, 13 + index, 21 + index, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false
               );
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, 21 + index, 13 + index, 36 - index, 36 - index, 13 + index, 36 - index, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false
               );
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, 21 + index, 13 + index, 22 + index, 21 + index, 13 + index, 35 - index, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false
               );
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, 36 - index, 13 + index, 22 + index, 36 - index, 13 + index, 35 - index, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false
               );
            }

            this.fillWithBlocks(worldIn, structureBoundingBox, 25, 16, 25, 32, 16, 32, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 25, 17, 25, 25, 19, 25, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 32, 17, 25, 32, 19, 25, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 25, 17, 32, 25, 19, 32, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 32, 17, 32, 32, 19, 32, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.setBlockState(worldIn, BRICKS_PRISMARINE, 26, 20, 26, structureBoundingBox);
            this.setBlockState(worldIn, BRICKS_PRISMARINE, 27, 21, 27, structureBoundingBox);
            this.setBlockState(worldIn, SEA_LANTERN, 27, 20, 27, structureBoundingBox);
            this.setBlockState(worldIn, BRICKS_PRISMARINE, 26, 20, 31, structureBoundingBox);
            this.setBlockState(worldIn, BRICKS_PRISMARINE, 27, 21, 30, structureBoundingBox);
            this.setBlockState(worldIn, SEA_LANTERN, 27, 20, 30, structureBoundingBox);
            this.setBlockState(worldIn, BRICKS_PRISMARINE, 31, 20, 31, structureBoundingBox);
            this.setBlockState(worldIn, BRICKS_PRISMARINE, 30, 21, 30, structureBoundingBox);
            this.setBlockState(worldIn, SEA_LANTERN, 30, 20, 30, structureBoundingBox);
            this.setBlockState(worldIn, BRICKS_PRISMARINE, 31, 20, 26, structureBoundingBox);
            this.setBlockState(worldIn, BRICKS_PRISMARINE, 30, 21, 27, structureBoundingBox);
            this.setBlockState(worldIn, SEA_LANTERN, 30, 20, 27, structureBoundingBox);
            this.fillWithBlocks(worldIn, structureBoundingBox, 28, 21, 27, 29, 21, 27, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 27, 21, 28, 27, 21, 29, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 28, 21, 30, 29, 21, 30, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 30, 21, 28, 30, 21, 29, BASE_PRISMARINE, BASE_PRISMARINE, false);
         }
      }

      private List<StructureOceanMonumentPieces.RoomDefinition> generateRoomGraph(Random random) {
         StructureOceanMonumentPieces.RoomDefinition[] roomDefinitions = new StructureOceanMonumentPieces.RoomDefinition[75];

         for (int i = 0; i < 5; i++) {
            for (int i2 = 0; i2 < 4; i2++) {
               int roomIndex = getRoomIndex(i, 0, i2);
               roomDefinitions[roomIndex] = new StructureOceanMonumentPieces.RoomDefinition(roomIndex);
            }
         }

         for (int i3 = 0; i3 < 5; i3++) {
            for (int i4 = 0; i4 < 4; i4++) {
               int roomIndex2 = getRoomIndex(i3, 1, i4);
               roomDefinitions[roomIndex2] = new StructureOceanMonumentPieces.RoomDefinition(roomIndex2);
            }
         }

         for (int i5 = 1; i5 < 4; i5++) {
            for (int i6 = 0; i6 < 2; i6++) {
               int roomIndex3 = getRoomIndex(i5, 2, i6);
               roomDefinitions[roomIndex3] = new StructureOceanMonumentPieces.RoomDefinition(roomIndex3);
            }
         }

         this.entryRoomDefinition = roomDefinitions[ENTRY_ROOM_INDEX];

         for (int i7 = 0; i7 < 5; i7++) {
            for (int i8 = 0; i8 < 5; i8++) {
               for (int i9 = 0; i9 < 3; i9++) {
                  int roomIndex4 = getRoomIndex(i7, i9, i8);
                  if (roomDefinitions[roomIndex4] != null) {
                     for (EnumFacing enumFacing : EnumFacing.values()) {
                        int i10 = i7 + enumFacing.getFrontOffsetX();
                        int i11 = i9 + enumFacing.getFrontOffsetY();
                        int i12 = i8 + enumFacing.getFrontOffsetZ();
                        if (i10 >= 0 && i10 < 5 && i12 >= 0 && i12 < 5 && i11 >= 0 && i11 < 3) {
                           int roomIndex5 = getRoomIndex(i10, i11, i12);
                           if (roomDefinitions[roomIndex5] != null) {
                              if (i12 != i8) {
                                 roomDefinitions[roomIndex4].connectRooms(enumFacing.getOpposite(), roomDefinitions[roomIndex5]);
                              } else {
                                 roomDefinitions[roomIndex4].connectRooms(enumFacing, roomDefinitions[roomIndex5]);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         StructureOceanMonumentPieces.RoomDefinition roomDefinition2;
         roomDefinitions[PENTHOUSE_ROOM_INDEX].connectRooms(EnumFacing.UP, roomDefinition2 = new StructureOceanMonumentPieces.RoomDefinition(1003));
         StructureOceanMonumentPieces.RoomDefinition roomDefinition3;
         roomDefinitions[LEFT_WING_ROOM_INDEX].connectRooms(EnumFacing.SOUTH, roomDefinition3 = new StructureOceanMonumentPieces.RoomDefinition(1001));
         StructureOceanMonumentPieces.RoomDefinition roomDefinition4;
         roomDefinitions[RIGHT_WING_ROOM_INDEX].connectRooms(EnumFacing.SOUTH, roomDefinition4 = new StructureOceanMonumentPieces.RoomDefinition(1002));
         roomDefinition2.claimed = true;
         roomDefinition3.claimed = true;
         roomDefinition4.claimed = true;
         this.entryRoomDefinition.sourceRoom = true;
         this.coreRoomDefinition = roomDefinitions[getRoomIndex(random.nextInt(4), 0, 2)];
         this.coreRoomDefinition.claimed = true;
         this.coreRoomDefinition.adjacentRooms[EnumFacing.EAST.getIndex()].claimed = true;
         this.coreRoomDefinition.adjacentRooms[EnumFacing.NORTH.getIndex()].claimed = true;
         this.coreRoomDefinition.adjacentRooms[EnumFacing.EAST.getIndex()].adjacentRooms[EnumFacing.NORTH.getIndex()].claimed = true;
         this.coreRoomDefinition.adjacentRooms[EnumFacing.UP.getIndex()].claimed = true;
         this.coreRoomDefinition.adjacentRooms[EnumFacing.EAST.getIndex()].adjacentRooms[EnumFacing.UP.getIndex()].claimed = true;
         this.coreRoomDefinition.adjacentRooms[EnumFacing.NORTH.getIndex()].adjacentRooms[EnumFacing.UP.getIndex()].claimed = true;
         this.coreRoomDefinition.adjacentRooms[EnumFacing.EAST.getIndex()].adjacentRooms[EnumFacing.NORTH.getIndex()].adjacentRooms[EnumFacing.UP.getIndex()].claimed = true;
         ArrayList arrayList = Lists.newArrayList();

         for (StructureOceanMonumentPieces.RoomDefinition roomDefinition5 : roomDefinitions) {
            if (roomDefinition5 != null) {
               roomDefinition5.updateOpenings();
               arrayList.add(roomDefinition5);
            }
         }

         roomDefinition2.updateOpenings();
         Collections.shuffle(arrayList, random);
         int i13 = 1;

         for (StructureOceanMonumentPieces.RoomDefinition roomDefinition6 : (Iterable<StructureOceanMonumentPieces.RoomDefinition>) arrayList) {
            int i14 = 0;
            int i15 = 0;

            while (i14 < 2 && i15 < 5) {
               i15++;
               int i16 = random.nextInt(6);
               if (roomDefinition6.connections[i16]) {
                  int index3 = EnumFacing.getFront(i16).getOpposite().getIndex();
                  roomDefinition6.connections[i16] = false;
                  roomDefinition6.adjacentRooms[i16].connections[index3] = false;
                  if (roomDefinition6.findSourcePath(i13++) && roomDefinition6.adjacentRooms[i16].findSourcePath(i13++)) {
                     i14++;
                  } else {
                     roomDefinition6.connections[i16] = true;
                     roomDefinition6.adjacentRooms[i16].connections[index3] = true;
                  }
               }
            }
         }

         arrayList.add(roomDefinition2);
         arrayList.add(roomDefinition3);
         arrayList.add(roomDefinition4);
         return arrayList;
      }
   }

   public static class MonumentCoreRoom extends StructureOceanMonumentPieces.Piece {
      public MonumentCoreRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition2, Random random) {
         super(1, enumFacing, roomDefinition2, 2, 2, 2);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillIfWater(worldIn, structureBoundingBoxIn, 1, 8, 0, 14, 8, 14, BASE_PRISMARINE);
         IBlockState iBlockState = BRICKS_PRISMARINE;
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 7, 0, 0, 7, 15, iBlockState, iBlockState, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 15, 7, 0, 15, 7, 15, iBlockState, iBlockState, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 7, 0, 15, 7, 0, iBlockState, iBlockState, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 7, 15, 14, 7, 15, iBlockState, iBlockState, false);

         for (int i = 1; i <= 6; i++) {
            iBlockState = BRICKS_PRISMARINE;
            if (i == 2 || i == 6) {
               iBlockState = BASE_PRISMARINE;
            }

            for (int i2 = 0; i2 <= 15; i2 += 15) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, i2, i, 0, i2, i, 1, iBlockState, iBlockState, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, i2, i, 6, i2, i, 9, iBlockState, iBlockState, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, i2, i, 14, i2, i, 15, iBlockState, iBlockState, false);
            }

            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, i, 0, 1, i, 0, iBlockState, iBlockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, i, 0, 9, i, 0, iBlockState, iBlockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 14, i, 0, 14, i, 0, iBlockState, iBlockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, i, 15, 14, i, 15, iBlockState, iBlockState, false);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 3, 6, 9, 6, 9, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 4, 7, 8, 5, 8, Blocks.gold_block.getDefaultState(), Blocks.gold_block.getDefaultState(), false);

         for (int i3 = 3; i3 <= 6; i3 += 3) {
            for (int i4 = 6; i4 <= 9; i4 += 3) {
               this.setBlockState(worldIn, SEA_LANTERN, i4, i3, 6, structureBoundingBoxIn);
               this.setBlockState(worldIn, SEA_LANTERN, i4, i3, 9, structureBoundingBoxIn);
            }
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 6, 5, 2, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 9, 5, 2, 9, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 1, 6, 10, 2, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 1, 9, 10, 2, 9, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 5, 6, 2, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 1, 5, 9, 2, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 10, 6, 2, 10, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 1, 10, 9, 2, 10, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 2, 5, 5, 6, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 2, 10, 5, 6, 10, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 2, 5, 10, 6, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 2, 10, 10, 6, 10, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 7, 1, 5, 7, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 7, 1, 10, 7, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 7, 9, 5, 7, 14, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 7, 9, 10, 7, 14, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 7, 5, 6, 7, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 7, 10, 6, 7, 10, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 7, 5, 14, 7, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 7, 10, 14, 7, 10, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 2, 2, 1, 3, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 2, 3, 1, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 13, 1, 2, 13, 1, 3, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 1, 2, 12, 1, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 12, 2, 1, 13, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 13, 3, 1, 13, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 13, 1, 12, 13, 1, 13, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 1, 13, 12, 1, 13, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         return true;
      }

      public MonumentCoreRoom() {
      }
   }

   interface MonumentRoomFitHelper {
      boolean canFit(StructureOceanMonumentPieces.RoomDefinition roomDefinition2);

      StructureOceanMonumentPieces.Piece createPiece(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition2, Random random);
   }

   public static class Penthouse extends StructureOceanMonumentPieces.Piece {
      public Penthouse(EnumFacing enumFacing, StructureBoundingBox structureBoundingBox) {
         super(enumFacing, structureBoundingBox);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, -1, 2, 11, -1, 11, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, -1, 0, 1, -1, 11, BASE_PRISMARINE, BASE_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, -1, 0, 13, -1, 11, BASE_PRISMARINE, BASE_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, -1, 0, 11, -1, 1, BASE_PRISMARINE, BASE_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, -1, 12, 11, -1, 13, BASE_PRISMARINE, BASE_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 0, 0, 13, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 13, 0, 0, 13, 0, 13, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 0, 12, 0, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 13, 12, 0, 13, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);

         for (int index = 2; index <= 11; index += 3) {
            this.setBlockState(worldIn, SEA_LANTERN, 0, 0, index, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 13, 0, index, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, index, 0, 0, structureBoundingBoxIn);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 0, 3, 4, 0, 9, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 0, 3, 11, 0, 9, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 0, 9, 9, 0, 11, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.setBlockState(worldIn, BRICKS_PRISMARINE, 5, 0, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, BRICKS_PRISMARINE, 8, 0, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, BRICKS_PRISMARINE, 10, 0, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, BRICKS_PRISMARINE, 3, 0, 10, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 0, 3, 3, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 0, 3, 10, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 0, 10, 7, 0, 10, DARK_PRISMARINE, DARK_PRISMARINE, false);
         int i = 3;

         for (int innerIndex = 0; innerIndex < 2; innerIndex++) {
            for (int nestedIndex = 2; nestedIndex <= 8; nestedIndex += 3) {
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, i, 0, nestedIndex, i, 2, nestedIndex, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false
               );
            }

            i = 10;
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 0, 10, 5, 2, 10, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 0, 10, 8, 2, 10, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, -1, 7, 7, -1, 8, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.handleWorld(worldIn, structureBoundingBoxIn, 6, -1, 3, 7, -1, 4);
         this.spawnElderGuardian(worldIn, structureBoundingBoxIn, 6, 1, 6);
         return true;
      }

      public Penthouse() {
      }
   }

   public abstract static class Piece extends StructureComponent {
      protected static final IBlockState BASE_PRISMARINE = Blocks.prismarine.getStateFromMeta(BlockPrismarine.ROUGH_META);
      protected static final IBlockState BRICKS_PRISMARINE = Blocks.prismarine.getStateFromMeta(BlockPrismarine.BRICKS_META);
      protected static final IBlockState DARK_PRISMARINE = Blocks.prismarine.getStateFromMeta(BlockPrismarine.DARK_META);
      protected static final IBlockState ACCENT_PRISMARINE = BRICKS_PRISMARINE;
      protected static final IBlockState SEA_LANTERN = Blocks.sea_lantern.getDefaultState();
      protected static final IBlockState WATER_BLOCK = Blocks.water.getDefaultState();
      protected static final int ENTRY_ROOM_INDEX = getRoomIndex(2, 0, 0);
      protected static final int PENTHOUSE_ROOM_INDEX = getRoomIndex(2, 2, 0);
      protected static final int LEFT_WING_ROOM_INDEX = getRoomIndex(0, 1, 0);
      protected static final int RIGHT_WING_ROOM_INDEX = getRoomIndex(4, 1, 0);
      protected StructureOceanMonumentPieces.RoomDefinition roomDefinition;

      protected void handleWorld(World world2, StructureBoundingBox structureBoundingBox, int i, int i2, int i3, int i4, int i5, int i6) {
         for (int i7 = i2; i7 <= i5; i7++) {
            for (int i8 = i; i8 <= i4; i8++) {
               for (int i9 = i3; i9 <= i6; i9++) {
                  if (this.getYWithOffset(i7) >= world2.getSeaLevel()) {
                     this.setBlockState(world2, Blocks.air.getDefaultState(), i8, i7, i9, structureBoundingBox);
                  } else {
                     this.setBlockState(world2, WATER_BLOCK, i8, i7, i9, structureBoundingBox);
                  }
               }
            }
         }
      }

      protected boolean spawnElderGuardian(World worldIn, StructureBoundingBox structureBoundingBox, int i, int i2, int i3) {
         int xWithOffset = this.getXWithOffset(i, i3);
         int yWithOffset = this.getYWithOffset(i2);
         int zWithOffset = this.getZWithOffset(i, i3);
         if (structureBoundingBox.isVecInside(new BlockPos(xWithOffset, yWithOffset, zWithOffset))) {
            EntityGuardian entityGuardian = new EntityGuardian(worldIn);
            entityGuardian.setElder(true);
            entityGuardian.heal(entityGuardian.getMaxHealth());
            entityGuardian.setLocationAndAngles(xWithOffset + 0.5, yWithOffset, zWithOffset + 0.5, 0.0F, 0.0F);
            entityGuardian.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entityGuardian)), (IEntityLivingData)null);
            worldIn.spawnEntityInWorld(entityGuardian);
            return true;
         } else {
            return false;
         }
      }

      public Piece() {
         super(0);
      }

      protected void generateDefaultFloor(World worldIn, StructureBoundingBox structureBoundingBox, int i, int i2, boolean flag) {
         if (flag) {
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 0, 0, i2 + 0, i + 2, 0, i2 + 8 - 1, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 5, 0, i2 + 0, i + 8 - 1, 0, i2 + 8 - 1, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 3, 0, i2 + 0, i + 4, 0, i2 + 2, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 3, 0, i2 + 5, i + 4, 0, i2 + 8 - 1, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 3, 0, i2 + 2, i + 4, 0, i2 + 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 3, 0, i2 + 5, i + 4, 0, i2 + 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 2, 0, i2 + 3, i + 2, 0, i2 + 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 5, 0, i2 + 3, i + 5, 0, i2 + 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         } else {
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 0, 0, i2 + 0, i + 8 - 1, 0, i2 + 8 - 1, BASE_PRISMARINE, BASE_PRISMARINE, false);
         }
      }

      protected boolean chunkIntersects(StructureBoundingBox structureBoundingBox, int i, int i2, int i3, int i4) {
         int xWithOffset = this.getXWithOffset(i, i2);
         int zWithOffset = this.getZWithOffset(i, i2);
         int xWithOffset2 = this.getXWithOffset(i3, i4);
         int zWithOffset2 = this.getZWithOffset(i3, i4);
         return structureBoundingBox.intersectsWith(
            Math.min(xWithOffset, xWithOffset2),
            Math.min(zWithOffset, zWithOffset2),
            Math.max(xWithOffset, xWithOffset2),
            Math.max(zWithOffset, zWithOffset2)
         );
      }

      public Piece(EnumFacing enumFacing, StructureBoundingBox structureBoundingBox) {
         super(1);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound nbtTagCompound) {
      }

      protected Piece(int i, EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition2, int i2, int i3, int i4) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.roomDefinition = roomDefinition2;
         int index3 = roomDefinition2.index;
         int i5 = index3 % 5;
         int i6 = index3 / 5 % 5;
         int i7 = index3 / 25;
         if (enumFacing != EnumFacing.NORTH && enumFacing != EnumFacing.SOUTH) {
            this.boundingBox = new StructureBoundingBox(0, 0, 0, i4 * 8 - 1, i3 * 4 - 1, i2 * 8 - 1);
         } else {
            this.boundingBox = new StructureBoundingBox(0, 0, 0, i2 * 8 - 1, i3 * 4 - 1, i4 * 8 - 1);
         }

         switch (enumFacing) {
            case NORTH:
               this.boundingBox.offset(i5 * 8, i7 * 4, -(i6 + i4) * 8 + 1);
               break;
            case SOUTH:
               this.boundingBox.offset(i5 * 8, i7 * 4, i6 * 8);
               break;
            case WEST:
               this.boundingBox.offset(-(i6 + i4) * 8 + 1, i7 * 4, i5 * 8);
               break;
            default:
               this.boundingBox.offset(i6 * 8, i7 * 4, i5 * 8);
         }
      }

      public Piece(int i) {
         super(i);
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound nbtTagCompound) {
      }

      protected void fillIfWater(
         World worldIn, StructureBoundingBox structureBoundingBox, int i, int i2, int i3, int i4, int i5, int i6, IBlockState iBlockState
      ) {
         for (int index = i2; index <= i5; index++) {
            for (int innerIndex = i; innerIndex <= i4; innerIndex++) {
               for (int nestedIndex = i3; nestedIndex <= i6; nestedIndex++) {
                  if (this.getBlockStateFromPos(worldIn, innerIndex, index, nestedIndex, structureBoundingBox) == WATER_BLOCK) {
                     this.setBlockState(worldIn, iBlockState, innerIndex, index, nestedIndex, structureBoundingBox);
                  }
               }
            }
         }
      }

      protected static final int getRoomIndex(int i, int i2, int i3) {
         return i2 * 25 + i3 * 5 + i;
      }
   }

   static class RoomDefinition {
      StructureOceanMonumentPieces.RoomDefinition[] adjacentRooms = new StructureOceanMonumentPieces.RoomDefinition[6];
      boolean[] connections = new boolean[6];
      int scanIndex;
      boolean sourceRoom;
      boolean claimed;
      int index;

      public void connectRooms(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition2) {
         this.adjacentRooms[enumFacing.getIndex()] = roomDefinition2;
         roomDefinition2.adjacentRooms[enumFacing.getOpposite().getIndex()] = this;
      }

      public boolean isSpecialRoom() {
         return this.index >= 75;
      }

      public boolean findSourcePath(int i) {
         if (this.sourceRoom) {
            return true;
         }

         this.scanIndex = i;

         for (int index = 0; index < 6; index++) {
            if (this.adjacentRooms[index] != null
               && this.connections[index]
               && this.adjacentRooms[index].scanIndex != i
               && this.adjacentRooms[index].findSourcePath(i)) {
               return true;
            }
         }

         return false;
      }

      public RoomDefinition(int i) {
         this.index = i;
      }

      public void updateOpenings() {
         for (int index = 0; index < 6; index++) {
            this.connections[index] = this.adjacentRooms[index] != null;
         }
      }

      public int getOpenCount() {
         int i = 0;

         for (int innerIndex = 0; innerIndex < 6; innerIndex++) {
            if (this.connections[innerIndex]) {
               i++;
            }
         }

         return i;
      }
   }

   public static class SimpleRoom extends StructureOceanMonumentPieces.Piece {
      private int roomType;

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(worldIn, structureBoundingBoxIn, 0, 0, this.roomDefinition.connections[EnumFacing.DOWN.getIndex()]);
         }

         if (this.roomDefinition.adjacentRooms[EnumFacing.UP.getIndex()] == null) {
            this.fillIfWater(worldIn, structureBoundingBoxIn, 1, 4, 1, 6, 4, 6, BASE_PRISMARINE);
         }

         boolean flag = this.roomType != 0
            && randomIn.nextBoolean()
            && !this.roomDefinition.connections[EnumFacing.DOWN.getIndex()]
            && !this.roomDefinition.connections[EnumFacing.UP.getIndex()]
            && this.roomDefinition.getOpenCount() > 1;
         if (this.roomType == 0) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 2, 1, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 0, 2, 3, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 2, 2, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 2, 2, 0, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.setBlockState(worldIn, SEA_LANTERN, 1, 2, 1, structureBoundingBoxIn);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 0, 7, 1, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 3, 0, 7, 3, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 0, 7, 2, 2, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 2, 0, 6, 2, 0, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.setBlockState(worldIn, SEA_LANTERN, 6, 2, 1, structureBoundingBoxIn);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 5, 2, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 5, 2, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 5, 0, 2, 7, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 7, 2, 2, 7, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.setBlockState(worldIn, SEA_LANTERN, 1, 2, 6, structureBoundingBoxIn);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 5, 7, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 3, 5, 7, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 5, 7, 2, 7, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 2, 7, 6, 2, 7, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.setBlockState(worldIn, SEA_LANTERN, 6, 2, 6, structureBoundingBoxIn);
            if (this.roomDefinition.connections[EnumFacing.SOUTH.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 3, 0, 4, 3, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            } else {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 3, 0, 4, 3, 1, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 2, 0, 4, 2, 0, BASE_PRISMARINE, BASE_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 1, 1, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            if (this.roomDefinition.connections[EnumFacing.NORTH.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 3, 7, 4, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            } else {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 3, 6, 4, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 2, 7, 4, 2, 7, BASE_PRISMARINE, BASE_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 6, 4, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            if (this.roomDefinition.connections[EnumFacing.WEST.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 3, 0, 3, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            } else {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 3, 1, 3, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 3, 0, 2, 4, BASE_PRISMARINE, BASE_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 3, 1, 1, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            if (this.roomDefinition.connections[EnumFacing.EAST.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 3, 3, 7, 3, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            } else {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 3, 3, 7, 3, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 3, 7, 2, 4, BASE_PRISMARINE, BASE_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 3, 7, 1, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }
         } else if (this.roomType == 1) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 2, 2, 3, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 5, 2, 3, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 5, 5, 3, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 2, 5, 3, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.setBlockState(worldIn, SEA_LANTERN, 2, 2, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 2, 2, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 5, 2, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 5, 2, 2, structureBoundingBoxIn);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 1, 3, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 3, 1, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 7, 1, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 6, 0, 3, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 7, 7, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 6, 7, 3, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 0, 7, 3, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 1, 7, 3, 1, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.setBlockState(worldIn, BASE_PRISMARINE, 1, 2, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, BASE_PRISMARINE, 0, 2, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, BASE_PRISMARINE, 1, 2, 7, structureBoundingBoxIn);
            this.setBlockState(worldIn, BASE_PRISMARINE, 0, 2, 6, structureBoundingBoxIn);
            this.setBlockState(worldIn, BASE_PRISMARINE, 6, 2, 7, structureBoundingBoxIn);
            this.setBlockState(worldIn, BASE_PRISMARINE, 7, 2, 6, structureBoundingBoxIn);
            this.setBlockState(worldIn, BASE_PRISMARINE, 6, 2, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, BASE_PRISMARINE, 7, 2, 1, structureBoundingBoxIn);
            if (!this.roomDefinition.connections[EnumFacing.SOUTH.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 0, 6, 3, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 6, 2, 0, BASE_PRISMARINE, BASE_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 6, 1, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            if (!this.roomDefinition.connections[EnumFacing.NORTH.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 7, 6, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 7, 6, 2, 7, BASE_PRISMARINE, BASE_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 7, 6, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            if (!this.roomDefinition.connections[EnumFacing.WEST.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 1, 0, 3, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 1, 0, 2, 6, BASE_PRISMARINE, BASE_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 1, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            if (!this.roomDefinition.connections[EnumFacing.EAST.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 3, 1, 7, 3, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 1, 7, 2, 6, BASE_PRISMARINE, BASE_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 1, 7, 1, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }
         } else if (this.roomType == 2) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 0, 7, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 6, 1, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 7, 6, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 0, 7, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 6, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 7, 6, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 0, 0, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 3, 0, 7, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 0, 6, 3, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 7, 6, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 3, 0, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 3, 7, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 7, 4, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            if (this.roomDefinition.connections[EnumFacing.SOUTH.getIndex()]) {
               this.handleWorld(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 2, 0);
            }

            if (this.roomDefinition.connections[EnumFacing.NORTH.getIndex()]) {
               this.handleWorld(worldIn, structureBoundingBoxIn, 3, 1, 7, 4, 2, 7);
            }

            if (this.roomDefinition.connections[EnumFacing.WEST.getIndex()]) {
               this.handleWorld(worldIn, structureBoundingBoxIn, 0, 1, 3, 0, 2, 4);
            }

            if (this.roomDefinition.connections[EnumFacing.EAST.getIndex()]) {
               this.handleWorld(worldIn, structureBoundingBoxIn, 7, 1, 3, 7, 2, 4);
            }
         }

         if (flag) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 3, 4, 1, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 2, 3, 4, 2, 4, BASE_PRISMARINE, BASE_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 3, 3, 4, 3, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         }

         return true;
      }

      public SimpleRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition2, Random random) {
         super(1, enumFacing, roomDefinition2, 1, 1, 1);
         this.roomType = random.nextInt(3);
      }

      public SimpleRoom() {
      }
   }

   public static class SimpleTopRoom extends StructureOceanMonumentPieces.Piece {
      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(worldIn, structureBoundingBoxIn, 0, 0, this.roomDefinition.connections[EnumFacing.DOWN.getIndex()]);
         }

         if (this.roomDefinition.adjacentRooms[EnumFacing.UP.getIndex()] == null) {
            this.fillIfWater(worldIn, structureBoundingBoxIn, 1, 4, 1, 6, 4, 6, BASE_PRISMARINE);
         }

         for (int index = 1; index <= 6; index++) {
            for (int innerIndex = 1; innerIndex <= 6; innerIndex++) {
               if (randomIn.nextInt(3) != 0) {
                  int i = 2 + (randomIn.nextInt(4) == 0 ? 0 : 1);
                  this.fillWithBlocks(
                     worldIn,
                     structureBoundingBoxIn,
                     index,
                     i,
                     innerIndex,
                     index,
                     3,
                     innerIndex,
                     Blocks.sponge.getStateFromMeta(1),
                     Blocks.sponge.getStateFromMeta(1),
                     false
                  );
               }
            }
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 0, 7, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 6, 1, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 7, 6, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 0, 7, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 6, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 7, 6, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 0, 0, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 3, 0, 7, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 0, 6, 3, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 7, 6, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 3, 0, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 3, 7, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 7, 4, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
         if (this.roomDefinition.connections[EnumFacing.SOUTH.getIndex()]) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 2, 0);
         }

         return true;
      }

      public SimpleTopRoom() {
      }

      public SimpleTopRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition2, Random random) {
         super(1, enumFacing, roomDefinition2, 1, 1, 1);
      }
   }

   public static class WingRoom extends StructureOceanMonumentPieces.Piece {
      private int wingType;

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.wingType == 0) {
            for (int index = 0; index < 4; index++) {
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 10 - index, 3 - index, 20 - index, 12 + index, 3 - index, 20, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false
               );
            }

            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 0, 6, 15, 0, 16, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 0, 6, 6, 3, 20, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 16, 0, 6, 16, 3, 20, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 7, 7, 1, 20, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 15, 1, 7, 15, 1, 20, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 6, 9, 3, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 13, 1, 6, 15, 3, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 1, 7, 9, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 13, 1, 7, 14, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 0, 5, 13, 0, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 0, 7, 12, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 0, 10, 8, 0, 12, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 14, 0, 10, 14, 0, 12, DARK_PRISMARINE, DARK_PRISMARINE, false);

            for (int index2 = 18; index2 >= 7; index2 -= 3) {
               this.setBlockState(worldIn, SEA_LANTERN, 6, 3, index2, structureBoundingBoxIn);
               this.setBlockState(worldIn, SEA_LANTERN, 16, 3, index2, structureBoundingBoxIn);
            }

            this.setBlockState(worldIn, SEA_LANTERN, 10, 0, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 12, 0, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 10, 0, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 12, 0, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 8, 3, 6, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 14, 3, 6, structureBoundingBoxIn);
            this.setBlockState(worldIn, BRICKS_PRISMARINE, 4, 2, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 4, 1, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, BRICKS_PRISMARINE, 4, 0, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, BRICKS_PRISMARINE, 18, 2, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 18, 1, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, BRICKS_PRISMARINE, 18, 0, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, BRICKS_PRISMARINE, 4, 2, 18, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 4, 1, 18, structureBoundingBoxIn);
            this.setBlockState(worldIn, BRICKS_PRISMARINE, 4, 0, 18, structureBoundingBoxIn);
            this.setBlockState(worldIn, BRICKS_PRISMARINE, 18, 2, 18, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 18, 1, 18, structureBoundingBoxIn);
            this.setBlockState(worldIn, BRICKS_PRISMARINE, 18, 0, 18, structureBoundingBoxIn);
            this.setBlockState(worldIn, BRICKS_PRISMARINE, 9, 7, 20, structureBoundingBoxIn);
            this.setBlockState(worldIn, BRICKS_PRISMARINE, 13, 7, 20, structureBoundingBoxIn);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 0, 21, 7, 4, 21, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 15, 0, 21, 16, 4, 21, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.spawnElderGuardian(worldIn, structureBoundingBoxIn, 11, 2, 16);
         } else if (this.wingType == 1) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 3, 18, 13, 3, 20, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 0, 18, 9, 2, 18, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 13, 0, 18, 13, 2, 18, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            int i = 9;

            for (int i2 = 0; i2 < 2; i2++) {
               this.setBlockState(worldIn, BRICKS_PRISMARINE, i, 6, 20, structureBoundingBoxIn);
               this.setBlockState(worldIn, SEA_LANTERN, i, 5, 20, structureBoundingBoxIn);
               this.setBlockState(worldIn, BRICKS_PRISMARINE, i, 4, 20, structureBoundingBoxIn);
               i = 13;
            }

            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 3, 7, 15, 3, 14, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            i = 10;

            for (int outerIndex = 0; outerIndex < 2; outerIndex++) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, i, 0, 10, i, 6, 10, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, i, 0, 12, i, 6, 12, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.setBlockState(worldIn, SEA_LANTERN, i, 0, 10, structureBoundingBoxIn);
               this.setBlockState(worldIn, SEA_LANTERN, i, 0, 12, structureBoundingBoxIn);
               this.setBlockState(worldIn, SEA_LANTERN, i, 4, 10, structureBoundingBoxIn);
               this.setBlockState(worldIn, SEA_LANTERN, i, 4, 12, structureBoundingBoxIn);
               i = 12;
            }

            i = 8;

            for (int outerIndex2 = 0; outerIndex2 < 2; outerIndex2++) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, i, 0, 7, i, 2, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, i, 0, 14, i, 2, 14, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               i = 14;
            }

            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 3, 8, 8, 3, 13, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 14, 3, 8, 14, 3, 13, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.spawnElderGuardian(worldIn, structureBoundingBoxIn, 11, 5, 13);
         }

         return true;
      }

      public WingRoom(EnumFacing enumFacing, StructureBoundingBox structureBoundingBox, int i) {
         super(enumFacing, structureBoundingBox);
         this.wingType = i & 1;
      }

      public WingRoom() {
      }
   }

   static class XDoubleRoomFitHelper implements StructureOceanMonumentPieces.MonumentRoomFitHelper {
      @Override
      public StructureOceanMonumentPieces.Piece createPiece(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition2, Random random) {
         roomDefinition2.claimed = true;
         roomDefinition2.adjacentRooms[EnumFacing.EAST.getIndex()].claimed = true;
         return new StructureOceanMonumentPieces.DoubleXRoom(enumFacing, roomDefinition2, random);
      }

      @Override
      public boolean canFit(StructureOceanMonumentPieces.RoomDefinition roomDefinition2) {
         return roomDefinition2.connections[EnumFacing.EAST.getIndex()] && !roomDefinition2.adjacentRooms[EnumFacing.EAST.getIndex()].claimed;
      }

      private XDoubleRoomFitHelper() {
      }
   }

   static class XYDoubleRoomFitHelper implements StructureOceanMonumentPieces.MonumentRoomFitHelper {
      @Override
      public StructureOceanMonumentPieces.Piece createPiece(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition2, Random random) {
         roomDefinition2.claimed = true;
         roomDefinition2.adjacentRooms[EnumFacing.EAST.getIndex()].claimed = true;
         roomDefinition2.adjacentRooms[EnumFacing.UP.getIndex()].claimed = true;
         roomDefinition2.adjacentRooms[EnumFacing.EAST.getIndex()].adjacentRooms[EnumFacing.UP.getIndex()].claimed = true;
         return new StructureOceanMonumentPieces.DoubleXYRoom(enumFacing, roomDefinition2, random);
      }

      private XYDoubleRoomFitHelper() {
      }

      @Override
      public boolean canFit(StructureOceanMonumentPieces.RoomDefinition roomDefinition2) {
         if (roomDefinition2.connections[EnumFacing.EAST.getIndex()]
            && !roomDefinition2.adjacentRooms[EnumFacing.EAST.getIndex()].claimed
            && roomDefinition2.connections[EnumFacing.UP.getIndex()]
            && !roomDefinition2.adjacentRooms[EnumFacing.UP.getIndex()].claimed) {
            StructureOceanMonumentPieces.RoomDefinition roomDefinition3 = roomDefinition2.adjacentRooms[EnumFacing.EAST.getIndex()];
            return roomDefinition3.connections[EnumFacing.UP.getIndex()] && !roomDefinition3.adjacentRooms[EnumFacing.UP.getIndex()].claimed;
         } else {
            return false;
         }
      }
   }

   static class YDoubleRoomFitHelper implements StructureOceanMonumentPieces.MonumentRoomFitHelper {
      @Override
      public StructureOceanMonumentPieces.Piece createPiece(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition2, Random random) {
         roomDefinition2.claimed = true;
         roomDefinition2.adjacentRooms[EnumFacing.UP.getIndex()].claimed = true;
         return new StructureOceanMonumentPieces.DoubleYRoom(enumFacing, roomDefinition2, random);
      }

      private YDoubleRoomFitHelper() {
      }

      @Override
      public boolean canFit(StructureOceanMonumentPieces.RoomDefinition roomDefinition2) {
         return roomDefinition2.connections[EnumFacing.UP.getIndex()] && !roomDefinition2.adjacentRooms[EnumFacing.UP.getIndex()].claimed;
      }
   }

   static class YZDoubleRoomFitHelper implements StructureOceanMonumentPieces.MonumentRoomFitHelper {
      @Override
      public boolean canFit(StructureOceanMonumentPieces.RoomDefinition roomDefinition2) {
         if (roomDefinition2.connections[EnumFacing.NORTH.getIndex()]
            && !roomDefinition2.adjacentRooms[EnumFacing.NORTH.getIndex()].claimed
            && roomDefinition2.connections[EnumFacing.UP.getIndex()]
            && !roomDefinition2.adjacentRooms[EnumFacing.UP.getIndex()].claimed) {
            StructureOceanMonumentPieces.RoomDefinition roomDefinition3 = roomDefinition2.adjacentRooms[EnumFacing.NORTH.getIndex()];
            return roomDefinition3.connections[EnumFacing.UP.getIndex()] && !roomDefinition3.adjacentRooms[EnumFacing.UP.getIndex()].claimed;
         } else {
            return false;
         }
      }

      private YZDoubleRoomFitHelper() {
      }

      @Override
      public StructureOceanMonumentPieces.Piece createPiece(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition2, Random random) {
         roomDefinition2.claimed = true;
         roomDefinition2.adjacentRooms[EnumFacing.NORTH.getIndex()].claimed = true;
         roomDefinition2.adjacentRooms[EnumFacing.UP.getIndex()].claimed = true;
         roomDefinition2.adjacentRooms[EnumFacing.NORTH.getIndex()].adjacentRooms[EnumFacing.UP.getIndex()].claimed = true;
         return new StructureOceanMonumentPieces.DoubleYZRoom(enumFacing, roomDefinition2, random);
      }
   }

   static class ZDoubleRoomFitHelper implements StructureOceanMonumentPieces.MonumentRoomFitHelper {
      private ZDoubleRoomFitHelper() {
      }

      @Override
      public boolean canFit(StructureOceanMonumentPieces.RoomDefinition roomDefinition2) {
         return roomDefinition2.connections[EnumFacing.NORTH.getIndex()] && !roomDefinition2.adjacentRooms[EnumFacing.NORTH.getIndex()].claimed;
      }

      @Override
      public StructureOceanMonumentPieces.Piece createPiece(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition2, Random random) {
         StructureOceanMonumentPieces.RoomDefinition roomDefinition3 = roomDefinition2;
         if (!roomDefinition2.connections[EnumFacing.NORTH.getIndex()] || roomDefinition2.adjacentRooms[EnumFacing.NORTH.getIndex()].claimed) {
            roomDefinition3 = roomDefinition2.adjacentRooms[EnumFacing.SOUTH.getIndex()];
         }

         roomDefinition3.claimed = true;
         roomDefinition3.adjacentRooms[EnumFacing.NORTH.getIndex()].claimed = true;
         return new StructureOceanMonumentPieces.DoubleZRoom(enumFacing, roomDefinition3, random);
      }
   }
}
