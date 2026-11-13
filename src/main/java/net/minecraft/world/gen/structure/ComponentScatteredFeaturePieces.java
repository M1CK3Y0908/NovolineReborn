package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.block.BlockFlowerPot.EnumFlowerType;
import net.minecraft.block.BlockSandStone.EnumType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureComponent.BlockSelector;

public class ComponentScatteredFeaturePieces {
   public static void registerScatteredFeaturePieces() {
      MapGenStructureIO.registerStructureComponent(ComponentScatteredFeaturePieces.DesertPyramid.class, "TeDP");
      MapGenStructureIO.registerStructureComponent(ComponentScatteredFeaturePieces.JunglePyramid.class, "TeJP");
      MapGenStructureIO.registerStructureComponent(ComponentScatteredFeaturePieces.SwampHut.class, "TeSH");
   }

   public static class DesertPyramid extends ComponentScatteredFeaturePieces.Feature {
      private static final List<WeightedRandomChestContent> itemsToGenerateInTemple = Lists.newArrayList(
         new WeightedRandomChestContent[]{
            new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 3),
            new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10),
            new WeightedRandomChestContent(Items.gold_ingot, 0, 2, 7, 15),
            new WeightedRandomChestContent(Items.emerald, 0, 1, 3, 2),
            new WeightedRandomChestContent(Items.bone, 0, 4, 6, 20),
            new WeightedRandomChestContent(Items.rotten_flesh, 0, 3, 7, 16),
            new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 3),
            new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1),
            new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 1),
            new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1)
         }
      );
      private boolean[] hasPlacedChest = new boolean[4];

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            0,
            -4,
            0,
            this.scatteredFeatureSizeX - 1,
            0,
            this.scatteredFeatureSizeZ - 1,
            Blocks.sandstone.getDefaultState(),
            Blocks.sandstone.getDefaultState(),
            false
         );

         for (int i = 1; i <= 9; i++) {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               i,
               i,
               i,
               this.scatteredFeatureSizeX - 1 - i,
               i,
               this.scatteredFeatureSizeZ - 1 - i,
               Blocks.sandstone.getDefaultState(),
               Blocks.sandstone.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               i + 1,
               i,
               i + 1,
               this.scatteredFeatureSizeX - 2 - i,
               i,
               this.scatteredFeatureSizeZ - 2 - i,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
         }

         for (int i2 = 0; i2 < this.scatteredFeatureSizeX; i2++) {
            for (int i3 = 0; i3 < this.scatteredFeatureSizeZ; i3++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.sandstone.getDefaultState(), i2, -5, i3, structureBoundingBoxIn);
            }
         }

         int metadataWithOffset = this.getMetadataWithOffset(Blocks.sandstone_stairs, 3);
         int metadataWithOffset2 = this.getMetadataWithOffset(Blocks.sandstone_stairs, 2);
         int metadataWithOffset3 = this.getMetadataWithOffset(Blocks.sandstone_stairs, 0);
         int metadataWithOffset4 = this.getMetadataWithOffset(Blocks.sandstone_stairs, 1);
         int i4 = ~EnumDyeColor.ORANGE.getDyeDamage() & 15;
         int i5 = ~EnumDyeColor.BLUE.getDyeDamage() & 15;
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 9, 4, Blocks.sandstone.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 10, 1, 3, 10, 3, Blocks.sandstone.getDefaultState(), Blocks.sandstone.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.sandstone_stairs.getStateFromMeta(metadataWithOffset), 2, 10, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone_stairs.getStateFromMeta(metadataWithOffset2), 2, 10, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone_stairs.getStateFromMeta(metadataWithOffset3), 0, 10, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone_stairs.getStateFromMeta(metadataWithOffset4), 4, 10, 2, structureBoundingBoxIn);
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.scatteredFeatureSizeX - 5,
            0,
            0,
            this.scatteredFeatureSizeX - 1,
            9,
            4,
            Blocks.sandstone.getDefaultState(),
            Blocks.air.getDefaultState(),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.scatteredFeatureSizeX - 4,
            10,
            1,
            this.scatteredFeatureSizeX - 2,
            10,
            3,
            Blocks.sandstone.getDefaultState(),
            Blocks.sandstone.getDefaultState(),
            false
         );
         this.setBlockState(worldIn, Blocks.sandstone_stairs.getStateFromMeta(metadataWithOffset), this.scatteredFeatureSizeX - 3, 10, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone_stairs.getStateFromMeta(metadataWithOffset2), this.scatteredFeatureSizeX - 3, 10, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone_stairs.getStateFromMeta(metadataWithOffset3), this.scatteredFeatureSizeX - 5, 10, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone_stairs.getStateFromMeta(metadataWithOffset4), this.scatteredFeatureSizeX - 1, 10, 2, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 0, 0, 12, 4, 4, Blocks.sandstone.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 1, 0, 11, 3, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), 9, 1, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), 9, 2, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), 9, 3, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), 10, 3, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), 11, 3, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), 11, 2, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), 11, 1, 1, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 1, 8, 3, 3, Blocks.sandstone.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 2, 8, 2, 2, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 1, 1, 16, 3, 3, Blocks.sandstone.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 1, 2, 16, 2, 2, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            5,
            4,
            5,
            this.scatteredFeatureSizeX - 6,
            4,
            this.scatteredFeatureSizeZ - 6,
            Blocks.sandstone.getDefaultState(),
            Blocks.sandstone.getDefaultState(),
            false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 4, 9, 11, 4, 11, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            8,
            1,
            8,
            8,
            3,
            8,
            Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()),
            Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            12,
            1,
            8,
            12,
            3,
            8,
            Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()),
            Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            8,
            1,
            12,
            8,
            3,
            12,
            Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()),
            Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            12,
            1,
            12,
            12,
            3,
            12,
            Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()),
            Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()),
            false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 5, 4, 4, 11, Blocks.sandstone.getDefaultState(), Blocks.sandstone.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.scatteredFeatureSizeX - 5,
            1,
            5,
            this.scatteredFeatureSizeX - 2,
            4,
            11,
            Blocks.sandstone.getDefaultState(),
            Blocks.sandstone.getDefaultState(),
            false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 7, 9, 6, 7, 11, Blocks.sandstone.getDefaultState(), Blocks.sandstone.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.scatteredFeatureSizeX - 7,
            7,
            9,
            this.scatteredFeatureSizeX - 7,
            7,
            11,
            Blocks.sandstone.getDefaultState(),
            Blocks.sandstone.getDefaultState(),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            5,
            5,
            9,
            5,
            7,
            11,
            Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()),
            Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.scatteredFeatureSizeX - 6,
            5,
            9,
            this.scatteredFeatureSizeX - 6,
            7,
            11,
            Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()),
            Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()),
            false
         );
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 5, 5, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 5, 6, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 6, 6, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), this.scatteredFeatureSizeX - 6, 5, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), this.scatteredFeatureSizeX - 6, 6, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), this.scatteredFeatureSizeX - 7, 6, 10, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 4, 4, 2, 6, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.scatteredFeatureSizeX - 3,
            4,
            4,
            this.scatteredFeatureSizeX - 3,
            6,
            4,
            Blocks.air.getDefaultState(),
            Blocks.air.getDefaultState(),
            false
         );
         this.setBlockState(worldIn, Blocks.sandstone_stairs.getStateFromMeta(metadataWithOffset), 2, 4, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone_stairs.getStateFromMeta(metadataWithOffset), 2, 3, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone_stairs.getStateFromMeta(metadataWithOffset), this.scatteredFeatureSizeX - 3, 4, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone_stairs.getStateFromMeta(metadataWithOffset), this.scatteredFeatureSizeX - 3, 3, 4, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 3, 2, 2, 3, Blocks.sandstone.getDefaultState(), Blocks.sandstone.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.scatteredFeatureSizeX - 3,
            1,
            3,
            this.scatteredFeatureSizeX - 2,
            2,
            3,
            Blocks.sandstone.getDefaultState(),
            Blocks.sandstone.getDefaultState(),
            false
         );
         this.setBlockState(worldIn, Blocks.sandstone_stairs.getDefaultState(), 1, 1, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone_stairs.getDefaultState(), this.scatteredFeatureSizeX - 2, 1, 2, structureBoundingBoxIn);
         this.setBlockState(
            worldIn, Blocks.stone_slab.getStateFromMeta(net.minecraft.block.BlockStoneSlab.EnumType.SAND.getMetadata()), 1, 2, 2, structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.stone_slab.getStateFromMeta(net.minecraft.block.BlockStoneSlab.EnumType.SAND.getMetadata()),
            this.scatteredFeatureSizeX - 2,
            2,
            2,
            structureBoundingBoxIn
         );
         this.setBlockState(worldIn, Blocks.sandstone_stairs.getStateFromMeta(metadataWithOffset4), 2, 1, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone_stairs.getStateFromMeta(metadataWithOffset3), this.scatteredFeatureSizeX - 3, 1, 2, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 3, 5, 4, 3, 18, Blocks.sandstone.getDefaultState(), Blocks.sandstone.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.scatteredFeatureSizeX - 5,
            3,
            5,
            this.scatteredFeatureSizeX - 5,
            3,
            17,
            Blocks.sandstone.getDefaultState(),
            Blocks.sandstone.getDefaultState(),
            false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 5, 4, 2, 16, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.scatteredFeatureSizeX - 6,
            1,
            5,
            this.scatteredFeatureSizeX - 5,
            2,
            16,
            Blocks.air.getDefaultState(),
            Blocks.air.getDefaultState(),
            false
         );

         for (int i6 = 5; i6 <= 17; i6 += 2) {
            this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), 4, 1, i6, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.CHISELED.getMetadata()), 4, 2, i6, structureBoundingBoxIn);
            this.setBlockState(
               worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), this.scatteredFeatureSizeX - 5, 1, i6, structureBoundingBoxIn
            );
            this.setBlockState(
               worldIn, Blocks.sandstone.getStateFromMeta(EnumType.CHISELED.getMetadata()), this.scatteredFeatureSizeX - 5, 2, i6, structureBoundingBoxIn
            );
         }

         this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), 10, 0, 7, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), 10, 0, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), 9, 0, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), 11, 0, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), 8, 0, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), 12, 0, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), 7, 0, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), 13, 0, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), 9, 0, 11, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), 11, 0, 11, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), 10, 0, 12, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), 10, 0, 13, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i5), 10, 0, 10, structureBoundingBoxIn);

         for (int i7 = 0; i7 <= this.scatteredFeatureSizeX - 1; i7 += this.scatteredFeatureSizeX - 1) {
            this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), i7, 2, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), i7, 2, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), i7, 2, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), i7, 3, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), i7, 3, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), i7, 3, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), i7, 4, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.CHISELED.getMetadata()), i7, 4, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), i7, 4, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), i7, 5, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), i7, 5, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), i7, 5, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), i7, 6, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.CHISELED.getMetadata()), i7, 6, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), i7, 6, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), i7, 7, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), i7, 7, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), i7, 7, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), i7, 8, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), i7, 8, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), i7, 8, 3, structureBoundingBoxIn);
         }

         for (int i8 = 2; i8 <= this.scatteredFeatureSizeX - 3; i8 += this.scatteredFeatureSizeX - 3 - 2) {
            this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), i8 - 1, 2, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), i8, 2, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), i8 + 1, 2, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), i8 - 1, 3, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), i8, 3, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), i8 + 1, 3, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), i8 - 1, 4, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.CHISELED.getMetadata()), i8, 4, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), i8 + 1, 4, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), i8 - 1, 5, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), i8, 5, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), i8 + 1, 5, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), i8 - 1, 6, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.CHISELED.getMetadata()), i8, 6, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), i8 + 1, 6, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), i8 - 1, 7, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), i8, 7, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), i8 + 1, 7, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), i8 - 1, 8, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), i8, 8, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), i8 + 1, 8, 0, structureBoundingBoxIn);
         }

         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            8,
            4,
            0,
            12,
            6,
            0,
            Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()),
            Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()),
            false
         );
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 8, 6, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 12, 6, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), 9, 5, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.CHISELED.getMetadata()), 10, 5, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stained_hardened_clay.getStateFromMeta(i4), 11, 5, 0, structureBoundingBoxIn);
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            8,
            -14,
            8,
            12,
            -11,
            12,
            Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()),
            Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            8,
            -10,
            8,
            12,
            -10,
            12,
            Blocks.sandstone.getStateFromMeta(EnumType.CHISELED.getMetadata()),
            Blocks.sandstone.getStateFromMeta(EnumType.CHISELED.getMetadata()),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            8,
            -9,
            8,
            12,
            -9,
            12,
            Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()),
            Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()),
            false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, -8, 8, 12, -1, 12, Blocks.sandstone.getDefaultState(), Blocks.sandstone.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, -11, 9, 11, -1, 11, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.stone_pressure_plate.getDefaultState(), 10, -11, 10, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, -13, 9, 11, -13, 11, Blocks.tnt.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 8, -11, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 8, -10, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.CHISELED.getMetadata()), 7, -10, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), 7, -11, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 12, -11, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 12, -10, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.CHISELED.getMetadata()), 13, -10, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), 13, -11, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 10, -11, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 10, -10, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.CHISELED.getMetadata()), 10, -10, 7, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), 10, -11, 7, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 10, -11, 12, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 10, -10, 12, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.CHISELED.getMetadata()), 10, -10, 13, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata()), 10, -11, 13, structureBoundingBoxIn);

         for (EnumFacing enumFacing : Plane.HORIZONTAL) {
            if (!this.hasPlacedChest[enumFacing.getHorizontalIndex()]) {
               int i9 = enumFacing.getFrontOffsetX() * 2;
               int i10 = enumFacing.getFrontOffsetZ() * 2;
               this.hasPlacedChest[enumFacing.getHorizontalIndex()] = this.generateChestContents(
                  worldIn,
                  structureBoundingBoxIn,
                  randomIn,
                  10 + i9,
                  -11,
                  10 + i10,
                  WeightedRandomChestContent.copyWithAddition(itemsToGenerateInTemple, Items.enchanted_book.getRandom(randomIn)),
                  2 + randomIn.nextInt(5)
               );
            }
         }

         return true;
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("hasPlacedChest0", this.hasPlacedChest[0]);
         tagCompound.setBoolean("hasPlacedChest1", this.hasPlacedChest[1]);
         tagCompound.setBoolean("hasPlacedChest2", this.hasPlacedChest[2]);
         tagCompound.setBoolean("hasPlacedChest3", this.hasPlacedChest[3]);
      }

      public DesertPyramid(Random random2, int i, int i2) {
         super(random2, i, i2, 21, 15, 21);
      }

      public DesertPyramid() {
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.hasPlacedChest[0] = tagCompound.getBoolean("hasPlacedChest0");
         this.hasPlacedChest[1] = tagCompound.getBoolean("hasPlacedChest1");
         this.hasPlacedChest[2] = tagCompound.getBoolean("hasPlacedChest2");
         this.hasPlacedChest[3] = tagCompound.getBoolean("hasPlacedChest3");
      }
   }

   abstract static class Feature extends StructureComponent {
      protected int scatteredFeatureSizeX;
      protected int scatteredFeatureSizeZ;
      protected int averageGroundLevel = -1;
      protected int scatteredFeatureSizeY;

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
         this.scatteredFeatureSizeX = tagCompound.getInteger("Width");
         this.scatteredFeatureSizeY = tagCompound.getInteger("Height");
         this.scatteredFeatureSizeZ = tagCompound.getInteger("Depth");
         this.averageGroundLevel = tagCompound.getInteger("HPos");
      }

      protected boolean checkWorld(World world2, StructureBoundingBox structureBoundingBox) {
         if (this.averageGroundLevel >= 0) {
            return true;
         }

         int i = 0;
         int i2 = 0;
         MutableBlockPos mutableBlockPos = new MutableBlockPos();

         for (int minZ2 = this.boundingBox.minZ; minZ2 <= this.boundingBox.maxZ; minZ2++) {
            for (int minX2 = this.boundingBox.minX; minX2 <= this.boundingBox.maxX; minX2++) {
               mutableBlockPos.set(minX2, 64, minZ2);
               if (structureBoundingBox.isVecInside(mutableBlockPos)) {
                  i += Math.max(world2.getTopSolidOrLiquidBlock(mutableBlockPos).getY(), world2.provider.getAverageGroundLevel());
                  i2++;
               }
            }
         }

         if (i2 == 0) {
            return false;
         }

         this.averageGroundLevel = i / i2;
         this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.minY + 0, 0);
         return true;
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         tagCompound.setInteger("Width", this.scatteredFeatureSizeX);
         tagCompound.setInteger("Height", this.scatteredFeatureSizeY);
         tagCompound.setInteger("Depth", this.scatteredFeatureSizeZ);
         tagCompound.setInteger("HPos", this.averageGroundLevel);
      }

      public Feature() {
      }

      protected Feature(Random random2, int i, int i2, int i3, int i4, int i5) {
         super(0);
         this.scatteredFeatureSizeX = i3;
         this.scatteredFeatureSizeY = i4;
         this.scatteredFeatureSizeZ = i5;
         this.coordBaseMode = Plane.HORIZONTAL.random(random2);
         switch (this.coordBaseMode) {
            case NORTH:
            case SOUTH:
               this.boundingBox = new StructureBoundingBox(i, 64, i2, i + i3 - 1, 64 + i4 - 1, i2 + i5 - 1);
               break;
            default:
               this.boundingBox = new StructureBoundingBox(i, 64, i2, i + i5 - 1, 64 + i4 - 1, i2 + i3 - 1);
         }
      }
   }

   public static class JunglePyramid extends ComponentScatteredFeaturePieces.Feature {
      private boolean placedTrap1;
      private static final List<WeightedRandomChestContent> junglePyramidMainChestLoot = Lists.newArrayList(
         new WeightedRandomChestContent[]{
            new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 3),
            new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10),
            new WeightedRandomChestContent(Items.gold_ingot, 0, 2, 7, 15),
            new WeightedRandomChestContent(Items.emerald, 0, 1, 3, 2),
            new WeightedRandomChestContent(Items.bone, 0, 4, 6, 20),
            new WeightedRandomChestContent(Items.rotten_flesh, 0, 3, 7, 16),
            new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 3),
            new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1),
            new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 1),
            new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1)
         }
      );
      private static final List<WeightedRandomChestContent> junglePyramidArrowLoot = Lists.newArrayList(
         new WeightedRandomChestContent[]{new WeightedRandomChestContent(Items.arrow, 0, 2, 7, 30)}
      );
      private boolean placedTrap2;
      private static ComponentScatteredFeaturePieces.JunglePyramid.Stones junglePyramidsRandomScatteredStones = new ComponentScatteredFeaturePieces.JunglePyramid.Stones();
      private boolean placedHiddenChest;
      private boolean placedMainChest;

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.placedMainChest = tagCompound.getBoolean("placedMainChest");
         this.placedHiddenChest = tagCompound.getBoolean("placedHiddenChest");
         this.placedTrap1 = tagCompound.getBoolean("placedTrap1");
         this.placedTrap2 = tagCompound.getBoolean("placedTrap2");
      }

      public JunglePyramid() {
      }

      public JunglePyramid(Random random2, int i, int i2) {
         super(random2, i, i2, 12, 10, 15);
      }

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("placedMainChest", this.placedMainChest);
         tagCompound.setBoolean("placedHiddenChest", this.placedHiddenChest);
         tagCompound.setBoolean("placedTrap1", this.placedTrap1);
         tagCompound.setBoolean("placedTrap2", this.placedTrap2);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (!this.checkWorld(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         int metadataWithOffset = this.getMetadataWithOffset(Blocks.stone_stairs, 3);
         int metadataWithOffset2 = this.getMetadataWithOffset(Blocks.stone_stairs, 2);
         int metadataWithOffset3 = this.getMetadataWithOffset(Blocks.stone_stairs, 0);
         int metadataWithOffset4 = this.getMetadataWithOffset(Blocks.stone_stairs, 1);
         this.fillWithRandomizedBlocks(
            worldIn,
            structureBoundingBoxIn,
            0,
            -4,
            0,
            this.scatteredFeatureSizeX - 1,
            0,
            this.scatteredFeatureSizeZ - 1,
            false,
            randomIn,
            junglePyramidsRandomScatteredStones
         );
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 1, 2, 9, 2, 2, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 1, 12, 9, 2, 12, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 1, 3, 2, 2, 11, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 9, 1, 3, 9, 2, 11, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 3, 1, 10, 6, 1, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 3, 13, 10, 6, 13, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 3, 2, 1, 6, 12, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 10, 3, 2, 10, 6, 12, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 3, 2, 9, 3, 12, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 6, 2, 9, 6, 12, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 3, 7, 3, 8, 7, 11, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 8, 4, 7, 8, 10, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithAir(worldIn, structureBoundingBoxIn, 3, 1, 3, 8, 2, 11);
         this.fillWithAir(worldIn, structureBoundingBoxIn, 4, 3, 6, 7, 3, 9);
         this.fillWithAir(worldIn, structureBoundingBoxIn, 2, 4, 2, 9, 5, 12);
         this.fillWithAir(worldIn, structureBoundingBoxIn, 4, 6, 5, 7, 6, 9);
         this.fillWithAir(worldIn, structureBoundingBoxIn, 5, 7, 6, 6, 7, 8);
         this.fillWithAir(worldIn, structureBoundingBoxIn, 5, 1, 2, 6, 2, 2);
         this.fillWithAir(worldIn, structureBoundingBoxIn, 5, 2, 12, 6, 2, 12);
         this.fillWithAir(worldIn, structureBoundingBoxIn, 5, 5, 1, 6, 5, 1);
         this.fillWithAir(worldIn, structureBoundingBoxIn, 5, 5, 13, 6, 5, 13);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 1, 5, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 10, 5, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 1, 5, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 10, 5, 9, structureBoundingBoxIn);

         for (int index = 0; index <= 14; index += 14) {
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 4, index, 2, 5, index, false, randomIn, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 4, index, 4, 5, index, false, randomIn, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 7, 4, index, 7, 5, index, false, randomIn, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 9, 4, index, 9, 5, index, false, randomIn, junglePyramidsRandomScatteredStones);
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 5, 6, 0, 6, 6, 0, false, randomIn, junglePyramidsRandomScatteredStones);

         for (int nestedIndex = 0; nestedIndex <= 11; nestedIndex += 11) {
            for (int innerIndex = 2; innerIndex <= 12; innerIndex += 2) {
               this.fillWithRandomizedBlocks(
                  worldIn, structureBoundingBoxIn, nestedIndex, 4, innerIndex, nestedIndex, 5, innerIndex, false, randomIn, junglePyramidsRandomScatteredStones
               );
            }

            this.fillWithRandomizedBlocks(
               worldIn, structureBoundingBoxIn, nestedIndex, 6, 5, nestedIndex, 6, 5, false, randomIn, junglePyramidsRandomScatteredStones
            );
            this.fillWithRandomizedBlocks(
               worldIn, structureBoundingBoxIn, nestedIndex, 6, 9, nestedIndex, 6, 9, false, randomIn, junglePyramidsRandomScatteredStones
            );
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 7, 2, 2, 9, 2, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 9, 7, 2, 9, 9, 2, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 7, 12, 2, 9, 12, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 9, 7, 12, 9, 9, 12, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 9, 4, 4, 9, 4, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 7, 9, 4, 7, 9, 4, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 9, 10, 4, 9, 10, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 7, 9, 10, 7, 9, 10, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 5, 9, 7, 6, 9, 7, false, randomIn, junglePyramidsRandomScatteredStones);
         this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(metadataWithOffset), 5, 9, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(metadataWithOffset), 6, 9, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(metadataWithOffset2), 5, 9, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(metadataWithOffset2), 6, 9, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(metadataWithOffset), 4, 0, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(metadataWithOffset), 5, 0, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(metadataWithOffset), 6, 0, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(metadataWithOffset), 7, 0, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(metadataWithOffset), 4, 1, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(metadataWithOffset), 4, 2, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(metadataWithOffset), 4, 3, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(metadataWithOffset), 7, 1, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(metadataWithOffset), 7, 2, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(metadataWithOffset), 7, 3, 10, structureBoundingBoxIn);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 9, 4, 1, 9, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 7, 1, 9, 7, 1, 9, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 10, 7, 2, 10, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 5, 4, 5, 6, 4, 5, false, randomIn, junglePyramidsRandomScatteredStones);
         this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(metadataWithOffset3), 4, 4, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(metadataWithOffset4), 7, 4, 5, structureBoundingBoxIn);

         for (int outerIndex = 0; outerIndex < 4; outerIndex++) {
            this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(metadataWithOffset2), 5, 0 - outerIndex, 6 + outerIndex, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(metadataWithOffset2), 6, 0 - outerIndex, 6 + outerIndex, structureBoundingBoxIn);
            this.fillWithAir(worldIn, structureBoundingBoxIn, 5, 0 - outerIndex, 7 + outerIndex, 6, 0 - outerIndex, 9 + outerIndex);
         }

         this.fillWithAir(worldIn, structureBoundingBoxIn, 1, -3, 12, 10, -1, 13);
         this.fillWithAir(worldIn, structureBoundingBoxIn, 1, -3, 1, 3, -1, 13);
         this.fillWithAir(worldIn, structureBoundingBoxIn, 1, -3, 1, 9, -1, 5);

         for (int index2 = 1; index2 <= 13; index2 += 2) {
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, -3, index2, 1, -2, index2, false, randomIn, junglePyramidsRandomScatteredStones);
         }

         for (int index3 = 2; index3 <= 12; index3 += 2) {
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, -1, index3, 3, -1, index3, false, randomIn, junglePyramidsRandomScatteredStones);
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, -2, 1, 5, -2, 1, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 7, -2, 1, 9, -2, 1, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 6, -3, 1, 6, -3, 1, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 6, -1, 1, 6, -1, 1, false, randomIn, junglePyramidsRandomScatteredStones);
         this.setBlockState(
            worldIn,
            Blocks.tripwire_hook
               .getStateFromMeta(this.getMetadataWithOffset(Blocks.tripwire_hook, EnumFacing.EAST.getHorizontalIndex()))
               .withProperty(BlockTripWireHook.ATTACHED, true),
            1,
            -3,
            8,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.tripwire_hook
               .getStateFromMeta(this.getMetadataWithOffset(Blocks.tripwire_hook, EnumFacing.WEST.getHorizontalIndex()))
               .withProperty(BlockTripWireHook.ATTACHED, true),
            4,
            -3,
            8,
            structureBoundingBoxIn
         );
         this.setBlockState(worldIn, Blocks.tripwire.getDefaultState().withProperty(BlockTripWire.ATTACHED, true), 2, -3, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.tripwire.getDefaultState().withProperty(BlockTripWire.ATTACHED, true), 3, -3, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.redstone_wire.getDefaultState(), 5, -3, 7, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.redstone_wire.getDefaultState(), 5, -3, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.redstone_wire.getDefaultState(), 5, -3, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.redstone_wire.getDefaultState(), 5, -3, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.redstone_wire.getDefaultState(), 5, -3, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.redstone_wire.getDefaultState(), 5, -3, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.redstone_wire.getDefaultState(), 5, -3, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.redstone_wire.getDefaultState(), 4, -3, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.mossy_cobblestone.getDefaultState(), 3, -3, 1, structureBoundingBoxIn);
         if (!this.placedTrap1) {
            this.placedTrap1 = this.checkWorld2(worldIn, structureBoundingBoxIn, randomIn, 3, 1, EnumFacing.NORTH.getIndex(), junglePyramidArrowLoot);
         }

         this.setBlockState(worldIn, Blocks.vine.getStateFromMeta(15), 3, -2, 2, structureBoundingBoxIn);
         this.setBlockState(
            worldIn,
            Blocks.tripwire_hook
               .getStateFromMeta(this.getMetadataWithOffset(Blocks.tripwire_hook, EnumFacing.NORTH.getHorizontalIndex()))
               .withProperty(BlockTripWireHook.ATTACHED, true),
            7,
            -3,
            1,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.tripwire_hook
               .getStateFromMeta(this.getMetadataWithOffset(Blocks.tripwire_hook, EnumFacing.SOUTH.getHorizontalIndex()))
               .withProperty(BlockTripWireHook.ATTACHED, true),
            7,
            -3,
            5,
            structureBoundingBoxIn
         );
         this.setBlockState(worldIn, Blocks.tripwire.getDefaultState().withProperty(BlockTripWire.ATTACHED, true), 7, -3, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.tripwire.getDefaultState().withProperty(BlockTripWire.ATTACHED, true), 7, -3, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.tripwire.getDefaultState().withProperty(BlockTripWire.ATTACHED, true), 7, -3, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.redstone_wire.getDefaultState(), 8, -3, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.redstone_wire.getDefaultState(), 9, -3, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.redstone_wire.getDefaultState(), 9, -3, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.mossy_cobblestone.getDefaultState(), 9, -3, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.redstone_wire.getDefaultState(), 9, -2, 4, structureBoundingBoxIn);
         if (!this.placedTrap2) {
            this.placedTrap2 = this.checkWorld2(worldIn, structureBoundingBoxIn, randomIn, 9, 3, EnumFacing.WEST.getIndex(), junglePyramidArrowLoot);
         }

         this.setBlockState(worldIn, Blocks.vine.getStateFromMeta(15), 8, -1, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.vine.getStateFromMeta(15), 8, -2, 3, structureBoundingBoxIn);
         if (!this.placedMainChest) {
            this.placedMainChest = this.generateChestContents(
               worldIn,
               structureBoundingBoxIn,
               randomIn,
               8,
               -3,
               3,
               WeightedRandomChestContent.copyWithAddition(junglePyramidMainChestLoot, Items.enchanted_book.getRandom(randomIn)),
               2 + randomIn.nextInt(5)
            );
         }

         this.setBlockState(worldIn, Blocks.mossy_cobblestone.getDefaultState(), 9, -3, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.mossy_cobblestone.getDefaultState(), 8, -3, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.mossy_cobblestone.getDefaultState(), 4, -3, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.mossy_cobblestone.getDefaultState(), 5, -2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.mossy_cobblestone.getDefaultState(), 5, -1, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.mossy_cobblestone.getDefaultState(), 6, -3, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.mossy_cobblestone.getDefaultState(), 7, -2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.mossy_cobblestone.getDefaultState(), 7, -1, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.mossy_cobblestone.getDefaultState(), 8, -3, 5, structureBoundingBoxIn);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 9, -1, 1, 9, -1, 5, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithAir(worldIn, structureBoundingBoxIn, 8, -3, 8, 10, -1, 10);
         this.setBlockState(worldIn, Blocks.stonebrick.getStateFromMeta(BlockStoneBrick.CHISELED_META), 8, -2, 11, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getStateFromMeta(BlockStoneBrick.CHISELED_META), 9, -2, 11, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getStateFromMeta(BlockStoneBrick.CHISELED_META), 10, -2, 11, structureBoundingBoxIn);
         this.setBlockState(
            worldIn,
            Blocks.lever
               .getStateFromMeta(BlockLever.getMetadataForFacing(EnumFacing.getFront(this.getMetadataWithOffset(Blocks.lever, EnumFacing.NORTH.getIndex())))),
            8,
            -2,
            12,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.lever
               .getStateFromMeta(BlockLever.getMetadataForFacing(EnumFacing.getFront(this.getMetadataWithOffset(Blocks.lever, EnumFacing.NORTH.getIndex())))),
            9,
            -2,
            12,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.lever
               .getStateFromMeta(BlockLever.getMetadataForFacing(EnumFacing.getFront(this.getMetadataWithOffset(Blocks.lever, EnumFacing.NORTH.getIndex())))),
            10,
            -2,
            12,
            structureBoundingBoxIn
         );
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 8, -3, 8, 8, -3, 10, false, randomIn, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 10, -3, 8, 10, -3, 10, false, randomIn, junglePyramidsRandomScatteredStones);
         this.setBlockState(worldIn, Blocks.mossy_cobblestone.getDefaultState(), 10, -2, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.redstone_wire.getDefaultState(), 8, -2, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.redstone_wire.getDefaultState(), 8, -2, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.redstone_wire.getDefaultState(), 10, -1, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.sticky_piston.getStateFromMeta(EnumFacing.UP.getIndex()), 9, -2, 8, structureBoundingBoxIn);
         this.setBlockState(
            worldIn,
            Blocks.sticky_piston.getStateFromMeta(this.getMetadataWithOffset(Blocks.sticky_piston, EnumFacing.WEST.getIndex())),
            10,
            -2,
            8,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.sticky_piston.getStateFromMeta(this.getMetadataWithOffset(Blocks.sticky_piston, EnumFacing.WEST.getIndex())),
            10,
            -1,
            8,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.unpowered_repeater.getStateFromMeta(this.getMetadataWithOffset(Blocks.unpowered_repeater, EnumFacing.NORTH.getHorizontalIndex())),
            10,
            -2,
            10,
            structureBoundingBoxIn
         );
         if (!this.placedHiddenChest) {
            this.placedHiddenChest = this.generateChestContents(
               worldIn,
               structureBoundingBoxIn,
               randomIn,
               9,
               -3,
               10,
               WeightedRandomChestContent.copyWithAddition(junglePyramidMainChestLoot, Items.enchanted_book.getRandom(randomIn)),
               2 + randomIn.nextInt(5)
            );
         }

         return true;
      }

      static class Stones extends BlockSelector {
         @Override
         public void handleRandom(Random random2, boolean flag) {
            if (random2.nextFloat() < 0.4F) {
               this.blockstate = Blocks.cobblestone.getDefaultState();
            } else {
               this.blockstate = Blocks.mossy_cobblestone.getDefaultState();
            }
         }

         private Stones() {
         }
      }
   }

   public static class SwampHut extends ComponentScatteredFeaturePieces.Feature {
      private boolean hasWitch;

      @Override
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Witch", this.hasWitch);
      }

      public SwampHut() {
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (!this.checkWorld(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            1,
            1,
            1,
            5,
            1,
            7,
            Blocks.planks.getStateFromMeta(net.minecraft.block.BlockPlanks.EnumType.SPRUCE.getMetadata()),
            Blocks.planks.getStateFromMeta(net.minecraft.block.BlockPlanks.EnumType.SPRUCE.getMetadata()),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            1,
            4,
            2,
            5,
            4,
            7,
            Blocks.planks.getStateFromMeta(net.minecraft.block.BlockPlanks.EnumType.SPRUCE.getMetadata()),
            Blocks.planks.getStateFromMeta(net.minecraft.block.BlockPlanks.EnumType.SPRUCE.getMetadata()),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            2,
            1,
            0,
            4,
            1,
            0,
            Blocks.planks.getStateFromMeta(net.minecraft.block.BlockPlanks.EnumType.SPRUCE.getMetadata()),
            Blocks.planks.getStateFromMeta(net.minecraft.block.BlockPlanks.EnumType.SPRUCE.getMetadata()),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            2,
            2,
            2,
            3,
            3,
            2,
            Blocks.planks.getStateFromMeta(net.minecraft.block.BlockPlanks.EnumType.SPRUCE.getMetadata()),
            Blocks.planks.getStateFromMeta(net.minecraft.block.BlockPlanks.EnumType.SPRUCE.getMetadata()),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            1,
            2,
            3,
            1,
            3,
            6,
            Blocks.planks.getStateFromMeta(net.minecraft.block.BlockPlanks.EnumType.SPRUCE.getMetadata()),
            Blocks.planks.getStateFromMeta(net.minecraft.block.BlockPlanks.EnumType.SPRUCE.getMetadata()),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            5,
            2,
            3,
            5,
            3,
            6,
            Blocks.planks.getStateFromMeta(net.minecraft.block.BlockPlanks.EnumType.SPRUCE.getMetadata()),
            Blocks.planks.getStateFromMeta(net.minecraft.block.BlockPlanks.EnumType.SPRUCE.getMetadata()),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            2,
            2,
            7,
            4,
            3,
            7,
            Blocks.planks.getStateFromMeta(net.minecraft.block.BlockPlanks.EnumType.SPRUCE.getMetadata()),
            Blocks.planks.getStateFromMeta(net.minecraft.block.BlockPlanks.EnumType.SPRUCE.getMetadata()),
            false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 2, 1, 3, 2, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 0, 2, 5, 3, 2, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 7, 1, 3, 7, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 0, 7, 5, 3, 7, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 2, 3, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 3, 3, 7, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 1, 3, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 5, 3, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 5, 3, 5, structureBoundingBoxIn);
         this.setBlockState(
            worldIn, Blocks.flower_pot.getDefaultState().withProperty(BlockFlowerPot.CONTENTS, EnumFlowerType.MUSHROOM_RED), 1, 3, 5, structureBoundingBoxIn
         );
         this.setBlockState(worldIn, Blocks.crafting_table.getDefaultState(), 3, 2, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cauldron.getDefaultState(), 4, 2, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 1, 2, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 5, 2, 1, structureBoundingBoxIn);
         int metadataWithOffset = this.getMetadataWithOffset(Blocks.oak_stairs, 3);
         int metadataWithOffset2 = this.getMetadataWithOffset(Blocks.oak_stairs, 1);
         int metadataWithOffset3 = this.getMetadataWithOffset(Blocks.oak_stairs, 0);
         int metadataWithOffset4 = this.getMetadataWithOffset(Blocks.oak_stairs, 2);
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            0,
            4,
            1,
            6,
            4,
            1,
            Blocks.spruce_stairs.getStateFromMeta(metadataWithOffset),
            Blocks.spruce_stairs.getStateFromMeta(metadataWithOffset),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            0,
            4,
            2,
            0,
            4,
            7,
            Blocks.spruce_stairs.getStateFromMeta(metadataWithOffset3),
            Blocks.spruce_stairs.getStateFromMeta(metadataWithOffset3),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            6,
            4,
            2,
            6,
            4,
            7,
            Blocks.spruce_stairs.getStateFromMeta(metadataWithOffset2),
            Blocks.spruce_stairs.getStateFromMeta(metadataWithOffset2),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            0,
            4,
            8,
            6,
            4,
            8,
            Blocks.spruce_stairs.getStateFromMeta(metadataWithOffset4),
            Blocks.spruce_stairs.getStateFromMeta(metadataWithOffset4),
            false
         );

         for (int index = 2; index <= 7; index += 5) {
            for (int innerIndex = 1; innerIndex <= 5; innerIndex += 4) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.log.getDefaultState(), innerIndex, -1, index, structureBoundingBoxIn);
            }
         }

         if (!this.hasWitch) {
            int xWithOffset = this.getXWithOffset(2, 5);
            int yWithOffset = this.getYWithOffset(2);
            int zWithOffset = this.getZWithOffset(2, 5);
            if (structureBoundingBoxIn.isVecInside(new BlockPos(xWithOffset, yWithOffset, zWithOffset))) {
               this.hasWitch = true;
               EntityWitch entityWitch = new EntityWitch(worldIn);
               entityWitch.setLocationAndAngles(xWithOffset + 0.5, yWithOffset, zWithOffset + 0.5, 0.0F, 0.0F);
               entityWitch.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(xWithOffset, yWithOffset, zWithOffset)), (IEntityLivingData)null);
               worldIn.spawnEntityInWorld(entityWitch);
            }
         }

         return true;
      }

      @Override
      protected void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.hasWitch = tagCompound.getBoolean("Witch");
      }

      public SwampHut(Random random2, int i, int i2) {
         super(random2, i, i2, 7, 7, 9);
      }
   }
}
