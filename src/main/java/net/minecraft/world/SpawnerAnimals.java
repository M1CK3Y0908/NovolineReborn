package net.minecraft.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.optifine.BlockPosM;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;

public final class SpawnerAnimals {
   private final Set<ChunkCoordIntPair> eligibleChunksForSpawning = Sets.newHashSet();
   private int countChunkPos;
   private static final int MOB_COUNT_DIV = (int)Math.pow(17.0, 2.0);
   private Map<Class, EntityLiving> map = new HashMap<>();
   private int intValue = Integer.MAX_VALUE;
   private int intValue2 = Integer.MAX_VALUE;

   public int findChunksForSpawning(WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean flag) {
      if (!spawnHostileMobs && !spawnPeacefulMobs) {
         return 0;
      }

      boolean flag2 = true;
      EntityPlayer entityPlayer = null;
      if (worldServerIn.playerEntities.size() == 1) {
         entityPlayer = worldServerIn.playerEntities.get(0);
         if (this.eligibleChunksForSpawning.size() > 0 && entityPlayer != null && entityPlayer.chunkCoordX == this.intValue && entityPlayer.chunkCoordZ == this.intValue2) {
            flag2 = false;
         }
      }

      if (flag2) {
         this.eligibleChunksForSpawning.clear();
         int i = 0;

         for (EntityPlayer entityPlayer2 : worldServerIn.playerEntities) {
            if (!entityPlayer2.isSpectator()) {
               int i2 = MathHelper.floor_double(entityPlayer2.posX / 16.0);
               int i3 = MathHelper.floor_double(entityPlayer2.posZ / 16.0);

               for (int i4 = -8; i4 <= 8; i4++) {
                  for (int i5 = -8; i5 <= 8; i5++) {
                     boolean flag3 = i4 == -8 || i4 == 8 || i5 == -8 || i5 == 8;
                     ChunkCoordIntPair chunkCoordIntPair = new ChunkCoordIntPair(i4 + i2, i5 + i3);
                     if (!this.eligibleChunksForSpawning.contains(chunkCoordIntPair)) {
                        i++;
                        if (!flag3 && worldServerIn.getWorldBorder().contains(chunkCoordIntPair)) {
                           this.eligibleChunksForSpawning.add(chunkCoordIntPair);
                        }
                     }
                  }
               }
            }
         }

         this.countChunkPos = i;
         if (entityPlayer != null) {
            this.intValue = entityPlayer.chunkCoordX;
            this.intValue2 = entityPlayer.chunkCoordZ;
         }
      }

      int i6 = 0;
      BlockPos spawnPoint = worldServerIn.getSpawnPoint();
      BlockPosM blockPosM = new BlockPosM(0, 0, 0);
      new net.minecraft.util.BlockPos.MutableBlockPos();

      for (EnumCreatureType enumCreatureType : EnumCreatureType.values()) {
         if ((!enumCreatureType.getPeacefulCreature() || spawnPeacefulMobs) && (enumCreatureType.getPeacefulCreature() || spawnHostileMobs) && (!enumCreatureType.getAnimal() || flag)) {
            int i7 = Reflector.reflectorMethod87.isActive()
               ? Reflector.createInt3(worldServerIn, Reflector.reflectorMethod87, enumCreatureType, true)
               : worldServerIn.countEntities(enumCreatureType.getCreatureClass());
            int i8 = enumCreatureType.getMaxNumberOfCreature() * this.countChunkPos / MOB_COUNT_DIV;
            if (i7 <= i8) {
               Collection eligibleChunksForSpawning2 = this.eligibleChunksForSpawning;
               if (Reflector.reflectorClass22.isActive()) {
                  ArrayList arrayList = Lists.newArrayList(eligibleChunksForSpawning2);
                  Collections.shuffle(arrayList);
                  eligibleChunksForSpawning2 = arrayList;
               }

               label166:
               for (ChunkCoordIntPair chunkCoordIntPair2 : (Iterable<ChunkCoordIntPair>) eligibleChunksForSpawning2) {
                  BlockPosM randomChunkPosition = getRandomChunkPosition(worldServerIn, chunkCoordIntPair2.chunkXPos, chunkCoordIntPair2.chunkZPos, blockPosM);
                  int x = randomChunkPosition.getX();
                  int y = randomChunkPosition.getY();
                  int z = randomChunkPosition.getZ();
                  if (!worldServerIn.getBlockState(randomChunkPosition).getBlock().isNormalCube()) {
                     int i9 = 0;

                     for (int i10 = 0; i10 < 3; i10++) {
                        int i11 = x;
                        int i12 = y;
                        int i13 = z;
                        net.minecraft.world.biome.BiomeGenBase.SpawnListEntry spawnListEntry = null;
                        IEntityLivingData iEntityLivingData = null;

                        for (int i14 = 0; i14 < 4; i14++) {
                           i11 += worldServerIn.rand.nextInt(6) - worldServerIn.rand.nextInt(6);
                           i12 += worldServerIn.rand.nextInt(1) - worldServerIn.rand.nextInt(1);
                           i13 += worldServerIn.rand.nextInt(6) - worldServerIn.rand.nextInt(6);
                           BlockPos blockPos = new BlockPos(i11, i12, i13);
                           float f = i11 + 0.5F;
                           float f2 = i13 + 0.5F;
                           if (!worldServerIn.isAnyPlayerWithinRangeAt(f, i12, f2, 24.0) && spawnPoint.distanceSq(f, i12, f2) >= 576.0) {
                              if (spawnListEntry == null) {
                                 spawnListEntry = worldServerIn.getSpawnListEntryForTypeAt(enumCreatureType, blockPos);
                                 if (spawnListEntry == null) {
                                    break;
                                 }
                              }

                              if (worldServerIn.canCreatureTypeSpawnHere(enumCreatureType, spawnListEntry, blockPos)
                                 && canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.getPlacementForEntity(spawnListEntry.entityClass), worldServerIn, blockPos)
                                 )
                               {
                                 EntityLiving entityLiving;
                                 try {
                                    entityLiving = this.map.get(spawnListEntry.entityClass);
                                    if (entityLiving == null) {
                                       entityLiving = spawnListEntry.entityClass.getConstructor(World.class).newInstance(worldServerIn);
                                       this.map.put(spawnListEntry.entityClass, entityLiving);
                                    }
                                 } catch (Exception exception) {
                                    exception.printStackTrace();
                                    return i6;
                                 }

                                 entityLiving.setLocationAndAngles(f, i12, f2, worldServerIn.rand.nextFloat() * 360.0F, 0.0F);
                                 if (Reflector.reflectorMethod38.isActive()
                                    ? ReflectorForge.checkEntityLiving2(entityLiving, worldServerIn, f, i12, f2)
                                    : entityLiving.getCanSpawnHere() && entityLiving.isNotColliding()) {
                                    this.map.remove(spawnListEntry.entityClass);
                                    if (!ReflectorForge.checkEntityLiving(entityLiving, worldServerIn, f, i12, f2)) {
                                       iEntityLivingData = entityLiving.onInitialSpawn(worldServerIn.getDifficultyForLocation(new BlockPos(entityLiving)), iEntityLivingData);
                                    }

                                    if (entityLiving.isNotColliding()) {
                                       i9++;
                                       worldServerIn.spawnEntityInWorld(entityLiving);
                                    }

                                    int i15 = Reflector.reflectorMethod40.isActive()
                                       ? Reflector.createInt2(Reflector.reflectorMethod40, entityLiving)
                                       : entityLiving.getMaxSpawnedInChunk();
                                    if (i9 >= i15) {
                                       continue label166;
                                    }
                                 }

                                 i6 += i9;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return i6;
   }

   public static boolean canCreatureTypeSpawnAtLocation(net.minecraft.entity.EntityLiving.SpawnPlacementType spawnPlacementTypeIn, World worldIn, BlockPos pos) {
      if (!worldIn.getWorldBorder().contains(pos)) {
         return false;
      }

      if (spawnPlacementTypeIn == null) {
         return false;
      }

      Block block2 = worldIn.getBlockState(pos).getBlock();
      if (spawnPlacementTypeIn == net.minecraft.entity.EntityLiving.SpawnPlacementType.IN_WATER) {
         return block2.getMaterial().isLiquid()
            && worldIn.getBlockState(pos.down()).getBlock().getMaterial().isLiquid()
            && !worldIn.getBlockState(pos.up()).getBlock().isNormalCube();
      }

      BlockPos blockPos = pos.down();
      IBlockState blockState = worldIn.getBlockState(blockPos);
      if (Reflector.reflectorMethod21.isActive()
         ? !Reflector.checkObject(blockState.getBlock(), Reflector.reflectorMethod21, worldIn, blockPos, spawnPlacementTypeIn)
         : !World.doesBlockHaveSolidTopSurface(worldIn, blockPos)) {
         return false;
      }

      Block block3 = worldIn.getBlockState(blockPos).getBlock();
      return block3 != Blocks.bedrock
         && block3 != Blocks.barrier
         && !block2.isNormalCube()
         && !block2.getMaterial().isLiquid()
         && !worldIn.getBlockState(pos.up()).getBlock().isNormalCube();
   }

   public static void handleWorld(World world2, BiomeGenBase biomeGenBase, int i, int i2, Random random) {
      List spawnableList = biomeGenBase.getSpawnableList(EnumCreatureType.CREATURE);
      if (!spawnableList.isEmpty()) {
         while (random.nextFloat() < biomeGenBase.getSpawningChance()) {
            net.minecraft.world.biome.BiomeGenBase.SpawnListEntry spawnListEntry = (BiomeGenBase.SpawnListEntry) WeightedRandom.getRandomItem(world2.rand, spawnableList);
            int i3 = spawnListEntry.minGroupCount + random.nextInt(1 + spawnListEntry.maxGroupCount - spawnListEntry.minGroupCount);
            IEntityLivingData iEntityLivingData = null;
            int i4 = i + random.nextInt(16);
            int i5 = i2 + random.nextInt(16);
            int i6 = i4;
            int i7 = i5;

            for (int i8 = 0; i8 < i3; i8++) {
               boolean flag = false;

               for (int i9 = 0; !flag && i9 < 4; i9++) {
                  BlockPos topSolidOrLiquidBlock = world2.getTopSolidOrLiquidBlock(new BlockPos(i4, 0, i5));
                  if (canCreatureTypeSpawnAtLocation(net.minecraft.entity.EntityLiving.SpawnPlacementType.ON_GROUND, world2, topSolidOrLiquidBlock)) {
                     EntityLiving entityLiving;
                     try {
                        entityLiving = spawnListEntry.entityClass.getConstructor(World.class).newInstance(world2);
                     } catch (Exception exception) {
                        exception.printStackTrace();
                        continue;
                     }

                     if (Reflector.reflectorMethod38.isActive()
                        && Reflector.createObject2(Reflector.reflectorMethod38, entityLiving, world2, i4 + 0.5F, topSolidOrLiquidBlock.getY(), i5 + 0.5F)
                           == ReflectorForge.object2) {
                        continue;
                     }

                     entityLiving.setLocationAndAngles(i4 + 0.5F, topSolidOrLiquidBlock.getY(), i5 + 0.5F, random.nextFloat() * 360.0F, 0.0F);
                     world2.spawnEntityInWorld(entityLiving);
                     iEntityLivingData = entityLiving.onInitialSpawn(world2.getDifficultyForLocation(new BlockPos(entityLiving)), iEntityLivingData);
                     flag = true;
                  }

                  i4 += random.nextInt(5) - random.nextInt(5);

                  for (i5 += random.nextInt(5) - random.nextInt(5);
                     i4 < i || i4 >= i + 16 || i5 < i2 || i5 >= i2 + 16;
                     i5 = i7 + random.nextInt(5) - random.nextInt(5)
                  ) {
                     i4 = i6 + random.nextInt(5) - random.nextInt(5);
                  }
               }
            }
         }
      }
   }

   private static BlockPosM getRandomChunkPosition(World arg0, int arg1, int arg2, BlockPosM arg3) {
      Chunk chunk = arg0.getChunkFromChunkCoords(arg1, arg2);
      int i = arg1 * 16 + arg0.rand.nextInt(16);
      int i2 = arg2 * 16 + arg0.rand.nextInt(16);
      int i3 = MathHelper.roundUp(chunk.getHeightValue(i & 15, i2 & 15) + 1, 16);
      int i4 = arg0.rand.nextInt(i3 > 0 ? i3 : chunk.getTopFilledSegment() + 16 - 1);
      arg3.setXyz(i, i4, i2);
      return arg3;
   }
}
