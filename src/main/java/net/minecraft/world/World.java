package net.minecraft.world;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;
import wtf.novoline.Novoline;
import wtf.novoline.event.SpawnParticleEvent;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.utils.render.RenderUtils;

public abstract class World implements IBlockAccess {
   private int seaLevel = 63;
   public final List<Entity> loadedEntityList = Lists.newArrayList();
   private boolean processingLoadedTiles;
   protected final List<Entity> unloadedEntityList = Lists.newArrayList();
   public final List<TileEntity> loadedTileEntityList = Lists.newArrayList();
   protected float prevRainingStrength;
   public final List<TileEntity> tickableTileEntities = Lists.newArrayList();
   protected float prevThunderingStrength;
   protected boolean scheduledUpdatesAreImmediate;
   private final List<TileEntity> addedTileEntityList = Lists.newArrayList();
   private final List<TileEntity> tileEntitiesToBeRemoved = Lists.newArrayList();
   public final List<EntityPlayer> playerEntities = Lists.newArrayList();
   public final List<Entity> weatherEffects = Lists.newArrayList();
   protected final IntHashMap<Entity> entitiesById = new IntHashMap<>();
   private long cloudColour = 16777215L;
   protected int updateLCG = new Random().nextInt();
   protected final int DIST_HASH_MAGIC = 1013904223;
   public final Random rand = new Random();
   protected VillageCollection villageCollectionObj;
   protected List<IWorldAccess> worldAccesses = Lists.newArrayList();
   private final Calendar theCalendar = Calendar.getInstance();
   protected Scoreboard worldScoreboard = new Scoreboard();
   protected IChunkProvider chunkProvider;
   protected Set<ChunkCoordIntPair> activeChunkSet = Sets.newHashSet();
   private int ambientTickCountdown = this.rand.nextInt(12000);
   protected boolean spawnHostileMobs = true;
   protected boolean spawnPeacefulMobs = true;
   protected float thunderingStrength;
   private int lastLightningBolt;
   int[] lightUpdateBlockList = new int[32768];
   protected boolean findingSpawnPoint;
   protected final ISaveHandler saveHandler;
   public final Profiler theProfiler;
   protected MapStorage mapStorage;
   private int skylightSubtracted;
   protected WorldInfo worldInfo;
   public final WorldProvider provider;
   public final boolean isRemote;
   private final WorldBorder worldBorder;
   protected float rainingStrength;

   public boolean isDaytime() {
      return this.skylightSubtracted < 4;
   }

   public Random setRandomSeed(int i, int i2, int i3) {
      long l = i * 341873128712L + i2 * 132897987541L + this.getWorldInfo().getSeed() + i3;
      this.rand.setSeed(l);
      return this.rand;
   }

   public <T extends Entity> List<T> getEntities(Class<? extends T> entityType, Predicate<? super T> filter) {
      ArrayList arrayList = Lists.newArrayList();

      for (Entity entity2 : this.loadedEntityList) {
         if (entityType.isAssignableFrom(entity2.getClass()) && ((com.google.common.base.Predicate)filter).apply(entity2)) {
            arrayList.add(entity2);
         }
      }

      return arrayList;
   }

   public boolean canBlockSeeSky(BlockPos pos) {
      if (pos.getY() >= this.getSeaLevel()) {
         return this.canSeeSky(pos);
      }

      BlockPos blockPos3 = new BlockPos(pos.getX(), this.getSeaLevel(), pos.getZ());
      if (!this.canSeeSky(blockPos3)) {
         return false;
      }

      for (BlockPos blockPos4 = blockPos3.down(); blockPos4.getY() > pos.getY(); blockPos4 = blockPos4.down()) {
         Block block3 = this.getBlockState(blockPos4).getBlock();
         if (block3.getLightOpacity() > 0 && !block3.getMaterial().isLiquid()) {
            return false;
         }
      }

      return true;
   }

   public EntityPlayer getPlayerEntityByName(String name) {
      for (int index = 0; index < this.playerEntities.size(); index++) {
         EntityPlayer entityPlayer = this.playerEntities.get(index);
         if (name.equals(entityPlayer.getName())) {
            return entityPlayer;
         }
      }

      return null;
   }

   @Override
   public WorldType getWorldType() {
      return this.worldInfo.getTerrainType();
   }

   public Vec3 getSkyColor(Entity entityIn, float partialTicks) {
      float celestialAngle = this.getCelestialAngle(partialTicks);
      float f = MathHelper.cos(celestialAngle * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
      f = MathHelper.clamp_float(f, 0.0F, 1.0F);
      int i = MathHelper.floor_double(entityIn.posX);
      int i2 = MathHelper.floor_double(entityIn.posY);
      int i3 = MathHelper.floor_double(entityIn.posZ);
      BlockPos blockPos3 = new BlockPos(i, i2, i3);
      BiomeGenBase biomeGenForCoords = this.getBiomeGenForCoords(blockPos3);
      float floatTemperature = biomeGenForCoords.getFloatTemperature(blockPos3);
      int skyColorByTemp = biomeGenForCoords.getSkyColorByTemp(floatTemperature);
      float f2 = (skyColorByTemp >> 16 & 0xFF) / 255.0F;
      float f3 = (skyColorByTemp >> 8 & 0xFF) / 255.0F;
      float f4 = (skyColorByTemp & 0xFF) / 255.0F;
      f2 *= f;
      f3 *= f;
      f4 *= f;
      float rainStrength = this.getRainStrength(partialTicks);
      if (rainStrength > 0.0F) {
         float f5 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.6F;
         float f6 = 1.0F - rainStrength * 0.75F;
         f2 = f2 * f6 + f5 * (1.0F - f6);
         f3 = f3 * f6 + f5 * (1.0F - f6);
         f4 = f4 * f6 + f5 * (1.0F - f6);
      }

      float thunderStrength = this.getThunderStrength(partialTicks);
      if (thunderStrength > 0.0F) {
         float f7 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.2F;
         float f8 = 1.0F - thunderStrength * 0.75F;
         f2 = f2 * f8 + f7 * (1.0F - f8);
         f3 = f3 * f8 + f7 * (1.0F - f8);
         f4 = f4 * f8 + f7 * (1.0F - f8);
      }

      if (this.lastLightningBolt > 0) {
         float f9 = this.lastLightningBolt - partialTicks;
         if (f9 > 1.0F) {
            f9 = 1.0F;
         }

         f9 *= 0.45F;
         f2 = f2 * (1.0F - f9) + 0.8F * f9;
         f3 = f3 * (1.0F - f9) + 0.8F * f9;
         f4 = f4 * (1.0F - f9) + 1.0F * f9;
      }

      if (ModuleManager.worldColor.isEnabled() && ModuleManager.worldColor.worldColor.isSelected(1)) {
         float[] fArray = RenderUtils.unpackColorNormalized(ModuleManager.worldColor.skyColor.getColor());
         if (ModuleManager.worldColor.colorType.getCurrentElement() == 1) {
            fArray = RenderUtils.unpackColorNormalized(RenderUtils.indexedHsbColorMirrored(1, 1, 1.0F, 1.0F, 3000L));
         }

         f2 = fArray[0];
         f3 = fArray[1];
         f4 = fArray[2];
      }

      return new Vec3(f2, f3, f4);
   }

   public void updateEntities() {
      this.theProfiler.startSection("entities");
      this.theProfiler.startSection("global");

      for (int index = 0; index < this.weatherEffects.size(); index++) {
         Entity entity = this.weatherEffects.get(index);

         try {
            entity.ticksExisted++;
            entity.onUpdate();
         } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Ticking entity");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("Entity being ticked");
            if (entity == null) {
               crashReportCategory.addCrashSection("Entity", "~~NULL~~");
            } else {
               entity.addEntityCrashInfo(crashReportCategory);
            }

            throw new ReportedException(crashReport);
         }

         if (entity.isDead) {
            this.weatherEffects.remove(index--);
         }
      }

      this.theProfiler.endStartSection("remove");
      this.loadedEntityList.removeAll(this.unloadedEntityList);

      for (int nestedIndex = 0; nestedIndex < this.unloadedEntityList.size(); nestedIndex++) {
         Entity entity3 = this.unloadedEntityList.get(nestedIndex);
         int chunkCoordX2 = entity3.chunkCoordX;
         int chunkCoordZ2 = entity3.chunkCoordZ;
         if (entity3.addedToChunk && this.isChunkLoaded(chunkCoordX2, chunkCoordZ2, true)) {
            this.getChunkFromChunkCoords(chunkCoordX2, chunkCoordZ2).removeEntity(entity3);
         }
      }

      for (int outerIndex = 0; outerIndex < this.unloadedEntityList.size(); outerIndex++) {
         this.onEntityRemoved(this.unloadedEntityList.get(outerIndex));
      }

      this.unloadedEntityList.clear();
      this.theProfiler.endStartSection("regular");

      for (int index2 = 0; index2 < this.loadedEntityList.size(); index2++) {
         Entity entity4 = this.loadedEntityList.get(index2);
         if (entity4.ridingEntity != null) {
            if (!entity4.ridingEntity.isDead && entity4.ridingEntity.riddenByEntity == entity4) {
               continue;
            }

            entity4.ridingEntity.riddenByEntity = null;
            entity4.ridingEntity = null;
         }

         this.theProfiler.startSection("tick");
         if (!entity4.isDead) {
            try {
               this.updateEntity(entity4);
            } catch (Throwable throwable2) {
               CrashReport crashReport2 = CrashReport.makeCrashReport(throwable2, "Ticking entity");
               CrashReportCategory crashReportCategory2 = crashReport2.makeCategory("Entity being ticked");
               entity4.addEntityCrashInfo(crashReportCategory2);
               throw new ReportedException(crashReport2);
            }
         }

         this.theProfiler.endSection();
         this.theProfiler.startSection("remove");
         if (entity4.isDead) {
            int chunkCoordX3 = entity4.chunkCoordX;
            int chunkCoordZ3 = entity4.chunkCoordZ;
            if (entity4.addedToChunk && this.isChunkLoaded(chunkCoordX3, chunkCoordZ3, true)) {
               this.getChunkFromChunkCoords(chunkCoordX3, chunkCoordZ3).removeEntity(entity4);
            }

            this.loadedEntityList.remove(index2--);
            this.onEntityRemoved(entity4);
         }

         this.theProfiler.endSection();
      }

      this.theProfiler.endStartSection("blockEntities");
      this.processingLoadedTiles = true;
      Iterator<TileEntity> iterator = this.tickableTileEntities.iterator();

      while (iterator.hasNext()) {
         TileEntity tileEntity = iterator.next();
         if (!tileEntity.isInvalid() && tileEntity.hasWorldObj()) {
            BlockPos blockPos = tileEntity.getPos();
            if (this.isBlockLoaded(blockPos) && this.worldBorder.contains(blockPos)) {
               try {
                  ((ITickable)tileEntity).update();
               } catch (Throwable throwable3) {
                  CrashReport crashReport3 = CrashReport.makeCrashReport(throwable3, "Ticking block entity");
                  CrashReportCategory crashReportCategory3 = crashReport3.makeCategory("Block entity being ticked");
                  tileEntity.addInfoToCrashReport(crashReportCategory3);
                  throw new ReportedException(crashReport3);
               }
            }
         }

         if (tileEntity.isInvalid()) {
            iterator.remove();
            this.loadedTileEntityList.remove(tileEntity);
            if (this.isBlockLoaded(tileEntity.getPos())) {
               this.getChunkFromBlockCoords(tileEntity.getPos()).removeTileEntity(tileEntity.getPos());
            }
         }
      }

      this.processingLoadedTiles = false;
      if (!this.tileEntitiesToBeRemoved.isEmpty()) {
         this.tickableTileEntities.removeAll(this.tileEntitiesToBeRemoved);
         this.loadedTileEntityList.removeAll(this.tileEntitiesToBeRemoved);
         this.tileEntitiesToBeRemoved.clear();
      }

      this.theProfiler.endStartSection("pendingBlockEntities");
      if (!this.addedTileEntityList.isEmpty()) {
         for (int innerIndex = 0; innerIndex < this.addedTileEntityList.size(); innerIndex++) {
            TileEntity tileEntity2 = this.addedTileEntityList.get(innerIndex);
            if (!tileEntity2.isInvalid()) {
               if (!this.loadedTileEntityList.contains(tileEntity2)) {
                  this.addTileEntity(tileEntity2);
               }

               if (this.isBlockLoaded(tileEntity2.getPos())) {
                  this.getChunkFromBlockCoords(tileEntity2.getPos()).addTileEntity(tileEntity2.getPos(), tileEntity2);
               }

               this.markBlockForUpdate(tileEntity2.getPos());
            }
         }

         this.addedTileEntityList.clear();
      }

      this.theProfiler.endSection();
      this.theProfiler.endSection();
   }

   public WorldSavedData loadItemData(Class<? extends WorldSavedData> clazz, String dataID) {
      return this.mapStorage.loadData(clazz, dataID);
   }

   public static boolean doesBlockHaveSolidTopSurface(IBlockAccess blockAccess, BlockPos pos) {
      IBlockState blockState = blockAccess.getBlockState(pos);
      Block block = blockState.getBlock();
      return block.getMaterial().isOpaque() && block.isFullCube()
         ? true
         : (
            block instanceof BlockStairs
               ? blockState.getValue(BlockStairs.HALF) == net.minecraft.block.BlockStairs.EnumHalf.TOP
               : (
                  block instanceof BlockSlab
                     ? blockState.getValue(BlockSlab.HALF) == net.minecraft.block.BlockSlab.EnumBlockHalf.TOP
                     : (block instanceof BlockHopper ? true : (block instanceof BlockSnow ? blockState.getValue(BlockSnow.LAYERS) == 7 : false))
               )
         );
   }

   @Override
   public boolean extendedLevelsInChunkCache() {
      return false;
   }

   public void updateEntityWithOptionalForce(Entity entityIn, boolean forceUpdate) {
      int i = MathHelper.floor_double(entityIn.posX);
      int i2 = MathHelper.floor_double(entityIn.posZ);
      if (!forceUpdate || this.isAreaLoaded(i - 32, 0, i2 - 32, i + 32, 0, i2 + 32, true)) {
         entityIn.lastTickPosX = entityIn.posX;
         entityIn.lastTickPosY = entityIn.posY;
         entityIn.lastTickPosZ = entityIn.posZ;
         entityIn.prevRotationYaw = entityIn.rotationYaw;
         entityIn.prevRotationPitch = entityIn.rotationPitch;
         if (forceUpdate && entityIn.addedToChunk) {
            entityIn.ticksExisted++;
            if (entityIn.ridingEntity != null) {
               entityIn.updateRidden();
            } else {
               entityIn.onUpdate();
            }
         }

         this.theProfiler.startSection("chunkCheck");
         if (Double.isNaN(entityIn.posX) || Double.isInfinite(entityIn.posX)) {
            entityIn.posX = entityIn.lastTickPosX;
         }

         if (Double.isNaN(entityIn.posY) || Double.isInfinite(entityIn.posY)) {
            entityIn.posY = entityIn.lastTickPosY;
         }

         if (Double.isNaN(entityIn.posZ) || Double.isInfinite(entityIn.posZ)) {
            entityIn.posZ = entityIn.lastTickPosZ;
         }

         if (Double.isNaN(entityIn.rotationPitch) || Double.isInfinite(entityIn.rotationPitch)) {
            entityIn.rotationPitch = entityIn.prevRotationPitch;
         }

         if (Double.isNaN(entityIn.rotationYaw) || Double.isInfinite(entityIn.rotationYaw)) {
            entityIn.rotationYaw = entityIn.prevRotationYaw;
         }

         int i3 = MathHelper.floor_double(entityIn.posX / 16.0);
         int i4 = MathHelper.floor_double(entityIn.posY / 16.0);
         int i5 = MathHelper.floor_double(entityIn.posZ / 16.0);
         if (!entityIn.addedToChunk || entityIn.chunkCoordX != i3 || entityIn.chunkCoordY != i4 || entityIn.chunkCoordZ != i5) {
            if (entityIn.addedToChunk && this.isChunkLoaded(entityIn.chunkCoordX, entityIn.chunkCoordZ, true)) {
               this.getChunkFromChunkCoords(entityIn.chunkCoordX, entityIn.chunkCoordZ).removeEntityAtIndex(entityIn, entityIn.chunkCoordY);
            }

            if (this.isChunkLoaded(i3, i5, true)) {
               entityIn.addedToChunk = true;
               this.getChunkFromChunkCoords(i3, i5).addEntity(entityIn);
            } else {
               entityIn.addedToChunk = false;
            }
         }

         this.theProfiler.endSection();
         if (forceUpdate && entityIn.addedToChunk && entityIn.riddenByEntity != null) {
            if (!entityIn.riddenByEntity.isDead && entityIn.riddenByEntity.ridingEntity == entityIn) {
               this.updateEntity(entityIn.riddenByEntity);
            } else {
               entityIn.riddenByEntity.ridingEntity = null;
               entityIn.riddenByEntity = null;
            }
         }
      }
   }

   public void scheduleBlockUpdate(BlockPos pos, Block blockIn, int delay, int priority) {
   }

   public void unloadEntities(Collection<Entity> entityCollection) {
      this.unloadedEntityList.addAll(entityCollection);
   }

   public EnumDifficulty getDifficulty() {
      return this.getWorldInfo().getDifficulty();
   }

   public void updateComparatorOutputLevel(BlockPos pos, Block blockIn) {
      for (EnumFacing enumFacing : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
         BlockPos blockPos3 = pos.offset(enumFacing);
         if (this.isBlockLoaded(blockPos3)) {
            IBlockState blockState2 = this.getBlockState(blockPos3);
            if (Blocks.unpowered_comparator.isAssociated(blockState2.getBlock())) {
               blockState2.getBlock().onNeighborBlockChange(this, blockPos3, blockState2, blockIn);
            } else if (blockState2.getBlock().isNormalCube()) {
               blockPos3 = blockPos3.offset(enumFacing);
               blockState2 = this.getBlockState(blockPos3);
               if (Blocks.unpowered_comparator.isAssociated(blockState2.getBlock())) {
                  blockState2.getBlock().onNeighborBlockChange(this, blockPos3, blockState2, blockIn);
               }
            }
         }
      }
   }

   public boolean checkLightFor(EnumSkyBlock lightType, BlockPos pos) {
      if (!this.isAreaLoaded(pos, 17, false)) {
         return false;
      }

      int i = 0;
      int i2 = 0;
      this.theProfiler.startSection("getBrightness");
      int lightFor = this.getLightFor(lightType, pos);
      int rawLight = this.getRawLight(pos, lightType);
      int x3 = pos.getX();
      int y3 = pos.getY();
      int z3 = pos.getZ();
      if (rawLight > lightFor) {
         i2++;
         this.lightUpdateBlockList[0] = 133152;
      } else if (rawLight < lightFor) {
         i2++;
         this.lightUpdateBlockList[0] = 133152 | lightFor << 18;

         while (i < i2) {
            int i3 = this.lightUpdateBlockList[i++];
            int i4 = (i3 & 63) - 32 + x3;
            int i5 = (i3 >> 6 & 63) - 32 + y3;
            int i6 = (i3 >> 12 & 63) - 32 + z3;
            int i7 = i3 >> 18 & 15;
            BlockPos blockPos3 = new BlockPos(i4, i5, i6);
            int lightFor2 = this.getLightFor(lightType, blockPos3);
            if (lightFor2 == i7) {
               this.setLightFor(lightType, blockPos3, 0);
               if (i7 > 0) {
                  int i8 = MathHelper.abs_int(i4 - x3);
                  int i9 = MathHelper.abs_int(i5 - y3);
                  int i10 = MathHelper.abs_int(i6 - z3);
                  if (i8 + i9 + i10 < 17) {
                     net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

                     for (EnumFacing enumFacing : EnumFacing.values()) {
                        int i11 = i4 + enumFacing.getFrontOffsetX();
                        int i12 = i5 + enumFacing.getFrontOffsetY();
                        int i13 = i6 + enumFacing.getFrontOffsetZ();
                        mutableBlockPos.set(i11, i12, i13);
                        int i14 = Math.max(1, this.getBlockState(mutableBlockPos).getBlock().getLightOpacity());
                        lightFor2 = this.getLightFor(lightType, mutableBlockPos);
                        if (lightFor2 == i7 - i14 && i2 < this.lightUpdateBlockList.length) {
                           this.lightUpdateBlockList[i2++] = i11 - x3 + 32
                              | i12 - y3 + 32 << 6
                              | i13 - z3 + 32 << 12
                              | i7 - i14 << 18;
                        }
                     }
                  }
               }
            }
         }

         i = 0;
      }

      this.theProfiler.endSection();
      this.theProfiler.startSection("checkedPosition < toCheckCount");

      while (i < i2) {
         int i15 = this.lightUpdateBlockList[i++];
         int i16 = (i15 & 63) - 32 + x3;
         int i17 = (i15 >> 6 & 63) - 32 + y3;
         int i18 = (i15 >> 12 & 63) - 32 + z3;
         BlockPos blockPos4 = new BlockPos(i16, i17, i18);
         int lightFor3 = this.getLightFor(lightType, blockPos4);
         int rawLight2 = this.getRawLight(blockPos4, lightType);
         if (rawLight2 != lightFor3) {
            this.setLightFor(lightType, blockPos4, rawLight2);
            if (rawLight2 > lightFor3) {
               int i19 = Math.abs(i16 - x3);
               int i20 = Math.abs(i17 - y3);
               int i21 = Math.abs(i18 - z3);
               boolean flag = i2 < this.lightUpdateBlockList.length - 6;
               if (i19 + i20 + i21 < 17 && flag) {
                  if (this.getLightFor(lightType, blockPos4.west()) < rawLight2) {
                     this.lightUpdateBlockList[i2++] = i16 - 1 - x3 + 32 + (i17 - y3 + 32 << 6) + (i18 - z3 + 32 << 12);
                  }

                  if (this.getLightFor(lightType, blockPos4.east()) < rawLight2) {
                     this.lightUpdateBlockList[i2++] = i16 + 1 - x3 + 32 + (i17 - y3 + 32 << 6) + (i18 - z3 + 32 << 12);
                  }

                  if (this.getLightFor(lightType, blockPos4.down()) < rawLight2) {
                     this.lightUpdateBlockList[i2++] = i16 - x3 + 32 + (i17 - 1 - y3 + 32 << 6) + (i18 - z3 + 32 << 12);
                  }

                  if (this.getLightFor(lightType, blockPos4.up()) < rawLight2) {
                     this.lightUpdateBlockList[i2++] = i16 - x3 + 32 + (i17 + 1 - y3 + 32 << 6) + (i18 - z3 + 32 << 12);
                  }

                  if (this.getLightFor(lightType, blockPos4.north()) < rawLight2) {
                     this.lightUpdateBlockList[i2++] = i16 - x3 + 32 + (i17 - y3 + 32 << 6) + (i18 - 1 - z3 + 32 << 12);
                  }

                  if (this.getLightFor(lightType, blockPos4.south()) < rawLight2) {
                     this.lightUpdateBlockList[i2++] = i16 - x3 + 32 + (i17 - y3 + 32 << 6) + (i18 + 1 - z3 + 32 << 12);
                  }
               }
            }
         }
      }

      this.theProfiler.endSection();
      return true;
   }

   public void addWorldAccess(IWorldAccess worldAccess) {
      this.worldAccesses.add(worldAccess);
   }

   public int getLight(BlockPos pos, boolean checkNeighbors) {
      if (pos.getX() < -30000000 || pos.getZ() < -30000000 || pos.getX() >= 30000000 || pos.getZ() >= 30000000) {
         return 15;
      }

      if (checkNeighbors && this.getBlockState(pos).getBlock().getUseNeighborBrightness()) {
         int light = this.getLight(pos.up(), false);
         int light2 = this.getLight(pos.east(), false);
         int light3 = this.getLight(pos.west(), false);
         int light4 = this.getLight(pos.south(), false);
         int light5 = this.getLight(pos.north(), false);
         if (light2 > light) {
            light = light2;
         }

         if (light3 > light) {
            light = light3;
         }

         if (light4 > light) {
            light = light4;
         }

         if (light5 > light) {
            light = light5;
         }

         return light;
      } else {
         if (pos.getY() < 0) {
            return 0;
         }

         if (pos.getY() >= 256) {
            pos = new BlockPos(pos.getX(), 255, pos.getZ());
         }

         Chunk chunk = this.getChunkFromBlockCoords(pos);
         return chunk.getLightSubtracted(pos, this.skylightSubtracted);
      }
   }

   public void makeFireworks(double x, double y, double z, double motionX, double motionY, double motionZ, NBTTagCompound compund) {
   }

   public int getSeaLevel() {
      return this.seaLevel;
   }

   public WorldBorder getWorldBorder() {
      return this.worldBorder;
   }

   public Explosion newExplosion(Entity entityIn, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking) {
      Explosion explosion = new Explosion(this, entityIn, x, y, z, strength, isFlaming, isSmoking);
      explosion.doExplosionA();
      explosion.doExplosionB(true);
      return explosion;
   }

   public boolean isSidePowered(BlockPos pos, EnumFacing side) {
      return this.getRedstonePower(pos, side) > 0;
   }

   @Override
   public int getStrongPower(BlockPos pos, EnumFacing direction) {
      IBlockState blockState = this.getBlockState(pos);
      return blockState.getBlock().getStrongPower(this, pos, blockState, direction);
   }

   protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
      return this.chunkProvider.chunkExists(x, z) && (allowEmpty || !this.chunkProvider.provideChunk(x, z).isEmpty());
   }

   protected void playMoodSoundAndCheckLight(int i, int i2, Chunk chunkIn) {
      this.theProfiler.endStartSection("moodSound");
      if (this.ambientTickCountdown == 0 && !this.isRemote) {
         this.updateLCG = this.updateLCG * 3 + 1013904223;
         int i3 = this.updateLCG >> 2;
         int i4 = i3 & 15;
         int i5 = i3 >> 8 & 15;
         int i6 = i3 >> 16 & 0xFF;
         BlockPos blockPos3 = new BlockPos(i4, i6, i5);
         Block block3 = chunkIn.getBlock(blockPos3);
         i4 += i;
         i5 += i2;
         if (block3.getMaterial() == Material.air && this.getLight(blockPos3) <= this.rand.nextInt(8) && this.getLightFor(EnumSkyBlock.SKY, blockPos3) <= 0) {
            EntityPlayer closestPlayer = this.getClosestPlayer(i4 + 0.5, i6 + 0.5, i5 + 0.5, 8.0);
            if (closestPlayer != null && closestPlayer.getDistanceSq(i4 + 0.5, i6 + 0.5, i5 + 0.5) > 4.0) {
               this.playSoundEffect(i4 + 0.5, i6 + 0.5, i5 + 0.5, "ambient.cave.cave", 0.7F, 0.8F + this.rand.nextFloat() * 0.2F);
               this.ambientTickCountdown = this.rand.nextInt(12000) + 6000;
            }
         }
      }

      this.theProfiler.endStartSection("checkLight");
      chunkIn.enqueueRelightChecks();
   }

   public float getLightBrightness(BlockPos pos) {
      return this.provider.getLightBrightnessTable()[this.getLightFromNeighbors(pos)];
   }

   public void playAuxSFX(int i, BlockPos pos, int i2) {
      this.playAuxSFXAtEntity((EntityPlayer)null, i, pos, i2);
   }

   public void setSeaLevel(int i) {
      this.seaLevel = i;
   }

   public int getUniqueDataId(String key) {
      return this.mapStorage.getUniqueDataId(key);
   }

   public MovingObjectPosition rayTraceBlocks(Vec3 vec32, Vec3 vec34) {
      return this.rayTraceBlocks(vec32, vec34, false, false, false);
   }

   public boolean handleMaterialAcceleration(AxisAlignedBB bb, Material material, Entity entityIn) {
      int y = MathHelper.floor_double(bb.minX);
      int i = MathHelper.floor_double(bb.maxX + 1.0);
      int i2 = MathHelper.floor_double(bb.minY);
      int i3 = MathHelper.floor_double(bb.maxY + 1.0);
      int i4 = MathHelper.floor_double(bb.minZ);
      int i5 = MathHelper.floor_double(bb.maxZ + 1.0);
      if (!this.isAreaLoaded(y, i2, i4, i, i3, i5, true)) {
         return false;
      }

      boolean flag = false;
      Vec3 vec32 = new Vec3(0.0, 0.0, 0.0);
      net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

      for (int y3 = y; y3 < i; y3++) {
         for (int i6 = i2; i6 < i3; i6++) {
            for (int i7 = i4; i7 < i5; i7++) {
               mutableBlockPos.set(y3, i6, i7);
               IBlockState blockState2 = this.getBlockState(mutableBlockPos);
               Block block3 = blockState2.getBlock();
               if (block3.getMaterial() == material) {
                  double d2 = i6 + 1 - BlockLiquid.getLiquidHeightPercent(blockState2.getValue(BlockLiquid.LEVEL));
                  if (i3 >= d2) {
                     flag = true;
                     vec32 = block3.modifyAcceleration(this, mutableBlockPos, entityIn, vec32);
                  }
               }
            }
         }
      }

      if (vec32.lengthVector() > 0.0 && entityIn.isPushedByWater()) {
         vec32 = vec32.normalize();
         double d3 = 0.014;
         entityIn.motionX = entityIn.motionX + vec32.xCoord * d3;
         entityIn.motionY = entityIn.motionY + vec32.yCoord * d3;
         entityIn.motionZ = entityIn.motionZ + vec32.zCoord * d3;
      }

      return flag;
   }

   public boolean isAreaLoaded(BlockPos center, int radius) {
      return this.isAreaLoaded(center, radius, true);
   }

   public boolean setBlockToAir(BlockPos pos) {
      return this.setBlockState(pos, Blocks.air.getDefaultState(), 3);
   }

   public void notifyNeighborsOfStateChange(BlockPos pos, Block blockType) {
      this.notifyBlockOfStateChange(pos.west(), blockType);
      this.notifyBlockOfStateChange(pos.east(), blockType);
      this.notifyBlockOfStateChange(pos.down(), blockType);
      this.notifyBlockOfStateChange(pos.up(), blockType);
      this.notifyBlockOfStateChange(pos.north(), blockType);
      this.notifyBlockOfStateChange(pos.south(), blockType);
   }

   public void notifyBlockOfStateChange(BlockPos pos, final Block blockIn) {
      if (!this.isRemote) {
         IBlockState blockState2 = this.getBlockState(pos);

         try {
            blockState2.getBlock().onNeighborBlockChange(this, pos, blockState2, blockIn);
         } catch (Throwable throwable) {
            CrashReport crashReport4 = CrashReport.makeCrashReport(throwable, "Exception while updating neighbours");
            CrashReportCategory crashReportCategory4 = crashReport4.makeCategory("Block being updated");
            crashReportCategory4.addCrashSectionCallable(
               "Source block type",
               new Callable<String>() {
                  public String call() throws Exception {
                     try {
                        return String.format(
                           "ID #%d (%s // %s)", Block.getIdFromBlock(blockIn), blockIn.getUnlocalizedName(), blockIn.getClass().getCanonicalName()
                        );
                     } catch (Throwable throwable2) {
                        return "ID #" + Block.getIdFromBlock(blockIn);
                     }
                  }
               }
            );
            CrashReportCategory.addBlockInfo(crashReportCategory4, pos, blockState2);
            throw new ReportedException(crashReport4);
         }
      }
   }

   public boolean isBlockModifiable(EntityPlayer player, BlockPos pos) {
      return true;
   }

   protected void onEntityRemoved(Entity entityIn) {
      for (int index = 0; index < this.worldAccesses.size(); index++) {
         this.worldAccesses.get(index).onEntityRemoved(entityIn);
      }
   }

   public WorldChunkManager getWorldChunkManager() {
      return this.provider.getWorldChunkManager();
   }

   public Chunk getChunkFromChunkCoords(int chunkX, int chunkZ) {
      return this.chunkProvider.provideChunk(chunkX, chunkZ);
   }

   public boolean canBlockBePlaced(Block blockIn, BlockPos pos, boolean flag, EnumFacing side, Entity entityIn, ItemStack itemStackIn) {
      Block block = this.getBlockState(pos).getBlock();
      AxisAlignedBB axisAlignedBB = flag ? null : blockIn.getCollisionBoundingBox(this, pos, blockIn.getDefaultState());
      return axisAlignedBB != null && !this.checkNoEntityCollision(axisAlignedBB, entityIn)
         ? false
         : (
            block.getMaterial() == Material.circuits && blockIn == Blocks.anvil
               ? true
               : block.getMaterial().isReplaceable() && blockIn.canReplace(this, pos, side, itemStackIn)
         );
   }

   public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> clazz, AxisAlignedBB aabb, Predicate<? super T> filter) {
      int i = MathHelper.floor_double((aabb.minX - 2.0) / 16.0);
      int i2 = MathHelper.floor_double((aabb.maxX + 2.0) / 16.0);
      int i3 = MathHelper.floor_double((aabb.minZ - 2.0) / 16.0);
      int i4 = MathHelper.floor_double((aabb.maxZ + 2.0) / 16.0);
      List<T> list = Lists.newArrayList();

      for (int index = i; index <= i2; index++) {
         for (int innerIndex = i3; innerIndex <= i4; innerIndex++) {
            if (this.isChunkLoaded(index, innerIndex, true)) {
               this.getChunkFromChunkCoords(index, innerIndex).getEntitiesOfTypeWithinAAAB(clazz, aabb, list, filter);
            }
         }
      }

      return list;
   }

   public boolean addWeatherEffect(Entity entityIn) {
      this.weatherEffects.add(entityIn);
      return true;
   }

   public long getTotalWorldTime() {
      return this.worldInfo.getWorldTotalTime();
   }

   public float getCurrentMoonPhaseFactor() {
      return WorldProvider.moonPhaseFactors[this.provider.getMoonPhase(this.worldInfo.getWorldTime())];
   }

   public int getActualHeight() {
      return this.provider.getHasNoSky() ? 128 : 256;
   }

   public boolean isRainingAt(BlockPos strikePosition) {
      if (!this.isRaining()) {
         return false;
      }

      if (!this.canSeeSky(strikePosition)) {
         return false;
      }

      if (this.getPrecipitationHeight(strikePosition).getY() > strikePosition.getY()) {
         return false;
      }

      BiomeGenBase biomeGenBase = this.getBiomeGenForCoords(strikePosition);
      return biomeGenBase.getEnableSnow() ? false : (this.canSnowAt(strikePosition, false) ? false : biomeGenBase.canRain());
   }

   public Explosion createExplosion(Entity entityIn, double x, double y, double z, float strength, boolean isSmoking) {
      return this.newExplosion(entityIn, x, y, z, strength, false, isSmoking);
   }

   public boolean isInsideBorder(WorldBorder worldBorderIn, Entity entityIn) {
      double d2 = worldBorderIn.minX();
      double d3 = worldBorderIn.minZ();
      double d4 = worldBorderIn.maxX();
      double d5 = worldBorderIn.maxZ();
      if (entityIn.isOutsideBorder()) {
         d2++;
         d3++;
         d4--;
         d5--;
      } else {
         d2--;
         d3--;
         d4++;
         d5++;
      }

      return entityIn.posX > d2 && entityIn.posX < d4 && entityIn.posZ > d3 && entityIn.posZ < d5;
   }

   protected void onEntityAdded(Entity entityIn) {
      for (int index = 0; index < this.worldAccesses.size(); index++) {
         this.worldAccesses.get(index).onEntityAdded(entityIn);
      }
   }

   public void spawnParticle(
      EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... iArray
   ) {
      this.spawnParticle(particleType.getParticleID(), particleType.getShouldIgnoreRange(), xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, iArray);
   }

   public BlockPos getHeight(BlockPos pos) {
      int i;
      if (pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000) {
         if (this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4, true)) {
            i = this.getChunkFromChunkCoords(pos.getX() >> 4, pos.getZ() >> 4).getHeightValue(pos.getX() & 15, pos.getZ() & 15);
         } else {
            i = 0;
         }
      } else {
         i = this.getSeaLevel() + 1;
      }

      return new BlockPos(pos.getX(), i, pos.getZ());
   }

   public boolean isFindingSpawnPoint() {
      return this.findingSpawnPoint;
   }

   public String getProviderName() {
      return this.chunkProvider.makeString();
   }

   public boolean isBlockLoaded(BlockPos pos) {
      return this.isBlockLoaded(pos, true);
   }

   public void spawnParticle(
      EnumParticleTypes particleType,
      boolean flag,
      double xCoord,
      double yCoord,
      double zCoord,
      double xOffset,
      double yOffset,
      double zOffset,
      int... iArray
   ) {
      this.spawnParticle(particleType.getParticleID(), particleType.getShouldIgnoreRange() | flag, xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, iArray);
   }

   public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
      for (int index = 0; index < this.worldAccesses.size(); index++) {
         this.worldAccesses.get(index).markBlockRangeForRenderUpdate(x1, y1, z1, x2, y2, z2);
      }
   }

   public void playSoundEffect(double x, double y, double z, String soundName, float volume, float pitch) {
      for (int index = 0; index < this.worldAccesses.size(); index++) {
         this.worldAccesses.get(index).playSound(soundName, x, y, z, volume, pitch);
      }
   }

   public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> classEntity, AxisAlignedBB bb) {
      return this.getEntitiesWithinAABB(classEntity, bb, EntitySelectors.NOT_SPECTATING);
   }

   public MapStorage getMapStorage() {
      return this.mapStorage;
   }

   public boolean canBlockFreezeWater(BlockPos pos) {
      return this.canBlockFreeze(pos, false);
   }

   protected World(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client) {
      this.saveHandler = saveHandlerIn;
      this.theProfiler = profilerIn;
      this.worldInfo = info;
      this.provider = providerIn;
      this.isRemote = client;
      this.worldBorder = providerIn.getWorldBorder();
   }

   public void playSoundToNearExcept(EntityPlayer player, String name, float volume, float pitch) {
      for (int index = 0; index < this.worldAccesses.size(); index++) {
         this.worldAccesses.get(index).playSoundToNearExcept(player, name, player.posX, player.posY, player.posZ, volume, pitch);
      }
   }

   public void notifyNeighborsRespectDebug(BlockPos pos, Block blockType) {
      if (this.worldInfo.getTerrainType() != WorldType.DEBUG_WORLD) {
         this.notifyNeighborsOfStateChange(pos, blockType);
      }
   }

   public Vec3 getCloudColour(float partialTicks) {
      float celestialAngle = this.getCelestialAngle(partialTicks);
      float f = MathHelper.cos(celestialAngle * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
      f = MathHelper.clamp_float(f, 0.0F, 1.0F);
      float f2 = (float)(this.cloudColour >> 16 & 255L) / 255.0F;
      float f3 = (float)(this.cloudColour >> 8 & 255L) / 255.0F;
      float f4 = (float)(this.cloudColour & 255L) / 255.0F;
      float rainStrength = this.getRainStrength(partialTicks);
      if (rainStrength > 0.0F) {
         float f5 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.6F;
         float f6 = 1.0F - rainStrength * 0.95F;
         f2 = f2 * f6 + f5 * (1.0F - f6);
         f3 = f3 * f6 + f5 * (1.0F - f6);
         f4 = f4 * f6 + f5 * (1.0F - f6);
      }

      f2 *= f * 0.9F + 0.1F;
      f3 *= f * 0.9F + 0.1F;
      f4 *= f * 0.85F + 0.15F;
      float thunderStrength = this.getThunderStrength(partialTicks);
      if (thunderStrength > 0.0F) {
         float f7 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.2F;
         float f8 = 1.0F - thunderStrength * 0.95F;
         f2 = f2 * f8 + f7 * (1.0F - f8);
         f3 = f3 * f8 + f7 * (1.0F - f8);
         f4 = f4 * f8 + f7 * (1.0F - f8);
      }

      return new Vec3(f2, f3, f4);
   }

   private boolean isWater(BlockPos pos) {
      return this.getBlockState(pos).getBlock().getMaterial() == Material.water;
   }

   public void setWorldTime(long time) {
      this.worldInfo.setWorldTime(time);
   }

   public void removeEntity(Entity entityIn) {
      if (entityIn.riddenByEntity != null) {
         entityIn.riddenByEntity.mountEntity((Entity)null);
      }

      if (entityIn.ridingEntity != null) {
         entityIn.mountEntity((Entity)null);
      }

      entityIn.setDead();
      if (entityIn instanceof EntityPlayer) {
         this.playerEntities.remove(entityIn);
         this.updateAllPlayersSleepingFlag();
         this.onEntityRemoved(entityIn);
      }
   }

   public MovingObjectPosition rayTraceBlocks(
      Vec3 vec3, Vec3 vec33, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock
   ) {
      if (Double.isNaN(vec3.xCoord) || Double.isNaN(vec3.yCoord) || Double.isNaN(vec3.zCoord)) {
         return null;
      }

      if (!Double.isNaN(vec33.xCoord) && !Double.isNaN(vec33.yCoord) && !Double.isNaN(vec33.zCoord)) {
         int i = MathHelper.floor_double(vec33.xCoord);
         int i2 = MathHelper.floor_double(vec33.yCoord);
         int i3 = MathHelper.floor_double(vec33.zCoord);
         int i4 = MathHelper.floor_double(vec3.xCoord);
         int i5 = MathHelper.floor_double(vec3.yCoord);
         int i6 = MathHelper.floor_double(vec3.zCoord);
         BlockPos blockPos3 = new BlockPos(i4, i5, i6);
         IBlockState blockState2 = this.getBlockState(blockPos3);
         Block block3 = blockState2.getBlock();
         if ((!ignoreBlockWithoutBoundingBox || block3.getCollisionBoundingBox(this, blockPos3, blockState2) != null) && block3.canCollideCheck(blockState2, stopOnLiquid)) {
            MovingObjectPosition movingObjectPosition = block3.collisionRayTrace(this, blockPos3, vec3, vec33);
            if (movingObjectPosition != null) {
               return movingObjectPosition;
            }
         }

         MovingObjectPosition movingObjectPosition2 = null;
         int i7 = 200;

         while (i7-- >= 0) {
            if (Double.isNaN(vec3.xCoord) || Double.isNaN(vec3.yCoord) || Double.isNaN(vec3.zCoord)) {
               return null;
            }

            if (i4 == i && i5 == i2 && i6 == i3) {
               return returnLastUncollidableBlock ? movingObjectPosition2 : null;
            }

            boolean flag = true;
            boolean flag2 = true;
            boolean flag3 = true;
            double d2 = 999.0;
            double d3 = 999.0;
            double d4 = 999.0;
            if (i > i4) {
               d2 = i4 + 1.0;
            } else if (i < i4) {
               d2 = i4 + 0.0;
            } else {
               flag = false;
            }

            if (i2 > i5) {
               d3 = i5 + 1.0;
            } else if (i2 < i5) {
               d3 = i5 + 0.0;
            } else {
               flag2 = false;
            }

            if (i3 > i6) {
               d4 = i6 + 1.0;
            } else if (i3 < i6) {
               d4 = i6 + 0.0;
            } else {
               flag3 = false;
            }

            double d5 = 999.0;
            double d6 = 999.0;
            double d7 = 999.0;
            double d8 = vec33.xCoord - vec3.xCoord;
            double d9 = vec33.yCoord - vec3.yCoord;
            double d10 = vec33.zCoord - vec3.zCoord;
            if (flag) {
               d5 = (d2 - vec3.xCoord) / d8;
            }

            if (flag2) {
               d6 = (d3 - vec3.yCoord) / d9;
            }

            if (flag3) {
               d7 = (d4 - vec3.zCoord) / d10;
            }

            if (d5 == -0.0) {
               d5 = -1.0E-4;
            }

            if (d6 == -0.0) {
               d6 = -1.0E-4;
            }

            if (d7 == -0.0) {
               d7 = -1.0E-4;
            }

            EnumFacing enumFacing;
            if (d5 < d6 && d5 < d7) {
               enumFacing = i > i4 ? EnumFacing.WEST : EnumFacing.EAST;
               vec3 = new Vec3(d2, vec3.yCoord + d9 * d5, vec3.zCoord + d10 * d5);
            } else if (d6 < d7) {
               enumFacing = i2 > i5 ? EnumFacing.DOWN : EnumFacing.UP;
               vec3 = new Vec3(vec3.xCoord + d8 * d6, d3, vec3.zCoord + d10 * d6);
            } else {
               enumFacing = i3 > i6 ? EnumFacing.NORTH : EnumFacing.SOUTH;
               vec3 = new Vec3(vec3.xCoord + d8 * d7, vec3.yCoord + d9 * d7, d4);
            }

            i4 = MathHelper.floor_double(vec3.xCoord) - (enumFacing == EnumFacing.EAST ? 1 : 0);
            i5 = MathHelper.floor_double(vec3.yCoord) - (enumFacing == EnumFacing.UP ? 1 : 0);
            i6 = MathHelper.floor_double(vec3.zCoord) - (enumFacing == EnumFacing.SOUTH ? 1 : 0);
            blockPos3 = new BlockPos(i4, i5, i6);
            IBlockState blockState3 = this.getBlockState(blockPos3);
            Block block4 = blockState3.getBlock();
            if (!ignoreBlockWithoutBoundingBox || block4.getCollisionBoundingBox(this, blockPos3, blockState3) != null) {
               if (block4.canCollideCheck(blockState3, stopOnLiquid)) {
                  MovingObjectPosition movingObjectPosition3 = block4.collisionRayTrace(this, blockPos3, vec3, vec33);
                  if (movingObjectPosition3 != null) {
                     return movingObjectPosition3;
                  }
               } else {
                  movingObjectPosition2 = new MovingObjectPosition(net.minecraft.util.MovingObjectPosition.MovingObjectType.MISS, vec3, enumFacing, blockPos3);
               }
            }
         }

         return returnLastUncollidableBlock ? movingObjectPosition2 : null;
      } else {
         return null;
      }
   }

   public void playRecord(BlockPos pos, String name) {
      for (int index = 0; index < this.worldAccesses.size(); index++) {
         this.worldAccesses.get(index).playRecord(name, pos);
      }
   }

   public void scheduleUpdate(BlockPos pos, Block blockIn, int delay) {
   }

   @Override
   public TileEntity getTileEntity(BlockPos pos) {
      if (!this.isValid(pos)) {
         return null;
      }

      TileEntity tileEntity = null;
      if (this.processingLoadedTiles) {
         for (int index = 0; index < this.addedTileEntityList.size(); index++) {
            TileEntity tileEntity2 = this.addedTileEntityList.get(index);
            if (!tileEntity2.isInvalid() && tileEntity2.getPos().equals(pos)) {
               tileEntity = tileEntity2;
               break;
            }
         }
      }

      if (tileEntity == null) {
         tileEntity = this.getChunkFromBlockCoords(pos).getTileEntity(pos, net.minecraft.world.chunk.Chunk.EnumCreateEntityType.IMMEDIATE);
      }

      if (tileEntity == null) {
         for (int innerIndex = 0; innerIndex < this.addedTileEntityList.size(); innerIndex++) {
            TileEntity tileEntity3 = this.addedTileEntityList.get(innerIndex);
            if (!tileEntity3.isInvalid() && tileEntity3.getPos().equals(pos)) {
               tileEntity = tileEntity3;
               break;
            }
         }
      }

      return tileEntity;
   }

   public boolean canBlockFreezeNoWater(BlockPos pos) {
      return this.canBlockFreeze(pos, true);
   }

   public void markBlockRangeForRenderUpdate(BlockPos rangeMin, BlockPos rangeMax) {
      this.markBlockRangeForRenderUpdate(rangeMin.getX(), rangeMin.getY(), rangeMin.getZ(), rangeMax.getX(), rangeMax.getY(), rangeMax.getZ());
   }

   public Scoreboard getScoreboard() {
      return this.worldScoreboard;
   }

   public boolean isAreaLoaded(BlockPos center, int radius, boolean allowEmpty) {
      return this.isAreaLoaded(
         center.getX() - radius,
         center.getY() - radius,
         center.getZ() - radius,
         center.getX() + radius,
         center.getY() + radius,
         center.getZ() + radius,
         allowEmpty
      );
   }

   public boolean checkBlockPos(BlockPos blockPos3, BlockPos blockPos4) {
      return this.isAreaLoaded(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ(), blockPos4.getX(), blockPos4.getY(), blockPos4.getZ(), true);
   }

   public void playAuxSFXAtEntity(EntityPlayer player, int sfxType, BlockPos pos, int i) {
      try {
         for (int index = 0; index < this.worldAccesses.size(); index++) {
            this.worldAccesses.get(index).playAuxSFX(player, sfxType, pos, i);
         }
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Playing level event");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Level event being played");
         crashReportCategory.addCrashSection("Block coordinates", CrashReportCategory.getCoordinateInfo(pos));
         crashReportCategory.addCrashSection("Event source", player);
         crashReportCategory.addCrashSection("Event type", sfxType);
         crashReportCategory.addCrashSection("Event data", i);
         throw new ReportedException(crashReport);
      }
   }

   public int getLight(BlockPos pos) {
      if (pos.getY() < 0) {
         return 0;
      }

      if (pos.getY() >= 256) {
         pos = new BlockPos(pos.getX(), 255, pos.getZ());
      }

      return this.getChunkFromBlockCoords(pos).getLightSubtracted(pos, 0);
   }

   public void setRainStrength(float strength) {
      this.prevRainingStrength = strength;
      this.rainingStrength = strength;
   }

   public void setInitialSpawnLocation() {
      this.setSpawnPoint(new BlockPos(8, 64, 8));
   }

   public boolean isBlockNormalCube(BlockPos pos, boolean _default) {
      if (!this.isValid(pos)) {
         return _default;
      }

      if (this.chunkProvider.provideChunk(pos).isEmpty()) {
         return _default;
      }

      Block block3 = this.getBlockState(pos).getBlock();
      return block3.getMaterial().isOpaque() && block3.isFullCube();
   }

   public void addTileEntities(Collection<TileEntity> tileEntityCollection) {
      if (this.processingLoadedTiles) {
         this.addedTileEntityList.addAll(tileEntityCollection);
      } else {
         for (TileEntity tileEntity4 : tileEntityCollection) {
            this.loadedTileEntityList.add(tileEntity4);
            if (tileEntity4 instanceof ITickable) {
               this.tickableTileEntities.add(tileEntity4);
            }
         }
      }
   }

   public boolean isMaterialInBB(AxisAlignedBB bb, Material materialIn) {
      int i = MathHelper.floor_double(bb.minX);
      int i2 = MathHelper.floor_double(bb.maxX + 1.0);
      int i3 = MathHelper.floor_double(bb.minY);
      int i4 = MathHelper.floor_double(bb.maxY + 1.0);
      int i5 = MathHelper.floor_double(bb.minZ);
      int i6 = MathHelper.floor_double(bb.maxZ + 1.0);
      net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

      for (int nestedIndex = i; nestedIndex < i2; nestedIndex++) {
         for (int outerIndex = i3; outerIndex < i4; outerIndex++) {
            for (int index = i5; index < i6; index++) {
               if (this.getBlockState(mutableBlockPos.set(nestedIndex, outerIndex, index)).getBlock().getMaterial() == materialIn) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public List getList(StructureBoundingBox structureBoundingBox) {
      return null;
   }

   public List getList3(Chunk chunk2) {
      return null;
   }

   public void updateEntity(Entity ent) {
      this.updateEntityWithOptionalForce(ent, true);
   }

   public boolean isBlockLoaded(BlockPos pos, boolean allowEmpty) {
      return !this.isValid(pos) ? false : this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4, allowEmpty);
   }

   public void loadEntities(Collection<Entity> entityCollection) {
      this.loadedEntityList.addAll(entityCollection);

      for (Entity entity2 : entityCollection) {
         this.onEntityAdded(entity2);
      }
   }

   public boolean checkLight(BlockPos pos) {
      boolean flag = false;
      if (!this.provider.getHasNoSky()) {
         flag = false | this.checkLightFor(EnumSkyBlock.SKY, pos);
      }

      return flag | this.checkLightFor(EnumSkyBlock.BLOCK, pos);
   }

   public void setSpawnPoint(BlockPos pos) {
      this.worldInfo.setSpawn(pos);
   }

   public BlockPos getTopSolidOrLiquidBlock(BlockPos pos) {
      Chunk chunk = this.getChunkFromBlockCoords(pos);
      BlockPos blockPos3 = new BlockPos(pos.getX(), chunk.getTopFilledSegment() + 16, pos.getZ());

      while (blockPos3.getY() >= 0) {
         BlockPos blockPos2 = blockPos3.down();
         Material material = chunk.getBlock(blockPos2).getMaterial();
         if (material.blocksMovement() && material != Material.leaves) {
            break;
         }

         blockPos3 = blockPos2;
      }

      return blockPos3;
   }

   public EntityPlayer getClosestPlayer(double x, double y, double z, double distance) {
      double d2 = -1.0;
      EntityPlayer entityPlayer3 = null;

      for (int index = 0; index < this.playerEntities.size(); index++) {
         EntityPlayer entityPlayer2 = this.playerEntities.get(index);
         if (EntitySelectors.NOT_SPECTATING.apply(entityPlayer2)) {
            double distanceSq = entityPlayer2.getDistanceSq(x, y, z);
            if ((distance < 0.0 || distanceSq < distance * distance) && (d2 == -1.0 || distanceSq < d2)) {
               d2 = distanceSq;
               entityPlayer3 = entityPlayer2;
            }
         }
      }

      return entityPlayer3;
   }

   private int getRawLight(BlockPos pos, EnumSkyBlock lightType) {
      if (lightType == EnumSkyBlock.SKY && this.canSeeSky(pos)) {
         return 15;
      }

      Block block3 = this.getBlockState(pos).getBlock();
      int i = lightType == EnumSkyBlock.SKY ? 0 : block3.getLightValue();
      int lightOpacity = block3.getLightOpacity();
      if (lightOpacity >= 15 && block3.getLightValue() > 0) {
         lightOpacity = 1;
      }

      if (lightOpacity < 1) {
         lightOpacity = 1;
      }

      if (lightOpacity >= 15) {
         return 0;
      }

      if (i >= 14) {
         return i;
      }

      for (EnumFacing enumFacing : EnumFacing.values()) {
         BlockPos blockPos3 = pos.offset(enumFacing);
         int i2 = this.getLightFor(lightType, blockPos3) - lightOpacity;
         if (i2 > i) {
            i = i2;
         }

         if (i >= 14) {
            return i;
         }
      }

      return i;
   }

   public <T extends Entity> List<T> getPlayers(Class<? extends T> playerType, Predicate<? super T> filter) {
      ArrayList arrayList = Lists.newArrayList();

      for (Entity entity2 : this.playerEntities) {
         if (playerType.isAssignableFrom(entity2.getClass()) && ((com.google.common.base.Predicate)filter).apply(entity2)) {
            arrayList.add(entity2);
         }
      }

      return arrayList;
   }

   public int getChunksLowestHorizon(int x, int z) {
      if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
         return !this.isChunkLoaded(x >> 4, z >> 4, true) ? 0 : this.getChunkFromChunkCoords(x >> 4, z >> 4).getLowestHeight();
      } else {
         return this.getSeaLevel() + 1;
      }
   }

   public float getThunderStrength(float delta) {
      return (this.prevThunderingStrength + (this.thunderingStrength - this.prevThunderingStrength) * delta) * this.getRainStrength(delta);
   }

   public long getWorldTime() {
      return this.worldInfo.getWorldTime();
   }

   public BlockPos getPrecipitationHeight(BlockPos pos) {
      return this.getChunkFromBlockCoords(pos).getPrecipitationHeight(pos);
   }

   public void playSoundAtEntity(Entity entityIn, String name, float volume, float pitch) {
      for (int index = 0; index < this.worldAccesses.size(); index++) {
         this.worldAccesses.get(index).playSound(name, entityIn.posX, entityIn.posY, entityIn.posZ, volume, pitch);
      }
   }

   public int getMoonPhase() {
      return this.provider.getMoonPhase(this.worldInfo.getWorldTime());
   }

   public void initialize(WorldSettings settings) {
      this.worldInfo.update();
   }

   public CrashReportCategory addWorldInfoToCrashReport(CrashReport report) {
      CrashReportCategory crashReportCategory = report.getCrashReportCategory("Affected level");
      crashReportCategory.addCrashSection("Level name", this.worldInfo == null ? "????" : this.worldInfo.getWorldName());
      crashReportCategory.addCrashSectionCallable("All players", new Callable<String>() {
         public String call() {
            return World.this.playerEntities.size() + " total; " + World.this.playerEntities.toString();
         }
      });
      crashReportCategory.addCrashSectionCallable("Chunk stats", new Callable<String>() {
         public String call() {
            return World.this.chunkProvider.makeString();
         }
      });

      try {
         this.worldInfo.addToCrashReport(crashReportCategory);
      } catch (Throwable throwable) {
         crashReportCategory.addCrashSectionThrowable("Level Data Unobtainable", throwable);
      }

      return crashReportCategory;
   }

   public void removeWorldAccess(IWorldAccess worldAccess) {
      this.worldAccesses.remove(worldAccess);
   }

   public void removePlayerEntityDangerously(Entity entityIn) {
      entityIn.setDead();
      if (entityIn instanceof EntityPlayer) {
         this.playerEntities.remove(entityIn);
         this.updateAllPlayersSleepingFlag();
      }

      int chunkCoordX2 = entityIn.chunkCoordX;
      int chunkCoordZ2 = entityIn.chunkCoordZ;
      if (entityIn.addedToChunk && this.isChunkLoaded(chunkCoordX2, chunkCoordZ2, true)) {
         this.getChunkFromChunkCoords(chunkCoordX2, chunkCoordZ2).removeEntity(entityIn);
      }

      this.loadedEntityList.remove(entityIn);
      this.onEntityRemoved(entityIn);
   }

   public boolean isAreaLoaded(BlockPos from, BlockPos to) {
      return this.checkBlockPos(from, to);
   }

   private boolean isAreaLoaded(int xStart, int yStart, int zStart, int xEnd, int yEnd, int zEnd, boolean allowEmpty) {
      if (yEnd >= 0 && yStart < 256) {
         xStart >>= 4;
         zStart >>= 4;
         xEnd >>= 4;
         zEnd >>= 4;

         for (int index = xStart; index <= xEnd; index++) {
            for (int innerIndex = zStart; innerIndex <= zEnd; innerIndex++) {
               if (!this.isChunkLoaded(index, innerIndex, allowEmpty)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean isAirBlock(BlockPos pos) {
      return this.getBlockState(pos).getBlock().getMaterial() == Material.air;
   }

   public Calendar getCurrentDate() {
      if (this.getTotalWorldTime() % 600L == 0L) {
         this.theCalendar.setTimeInMillis(MinecraftServer.getCurrentTimeMillis());
      }

      return this.theCalendar;
   }

   public void setTotalWorldTime(long worldTime) {
      this.worldInfo.setWorldTotalTime(worldTime);
   }

   public int countEntities(Class<?> entityType) {
      int i = 0;

      for (Entity entity2 : this.loadedEntityList) {
         if ((!(entity2 instanceof EntityLiving) || !((EntityLiving)entity2).isNoDespawnRequired()) && entityType.isAssignableFrom(entity2.getClass())) {
            i++;
         }
      }

      return i;
   }

   public boolean isAABBInMaterial(AxisAlignedBB bb, Material materialIn) {
      int i = MathHelper.floor_double(bb.minX);
      int nestedIndex = MathHelper.floor_double(bb.maxX + 1.0);
      int outerIndex = MathHelper.floor_double(bb.minY);
      int index = MathHelper.floor_double(bb.maxY + 1.0);
      int i2 = MathHelper.floor_double(bb.minZ);
      int i3 = MathHelper.floor_double(bb.maxZ + 1.0);
      net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

      for (int i4 = i; i4 < nestedIndex; i4++) {
         for (int outerIndex2 = outerIndex; outerIndex2 < index; outerIndex2++) {
            for (int i5 = i2; i5 < i3; i5++) {
               IBlockState blockState2 = this.getBlockState(mutableBlockPos.set(i4, outerIndex2, i5));
               if (blockState2.getBlock().getMaterial() == materialIn) {
                  int value = blockState2.getValue(BlockLiquid.LEVEL);
                  double d2 = outerIndex2 + 1;
                  if (value < 8) {
                     d2 = outerIndex2 + 1 - value / 8.0;
                  }

                  if (d2 >= bb.minY) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public void updateAllPlayersSleepingFlag() {
   }

   public List<AxisAlignedBB> getCollidingBoundingBoxes(Entity entityIn, AxisAlignedBB bb) {
      ArrayList arrayList = Lists.newArrayList();
      int i = MathHelper.floor_double(bb.minX);
      int i2 = MathHelper.floor_double(bb.maxX + 1.0);
      int i3 = MathHelper.floor_double(bb.minY);
      int i4 = MathHelper.floor_double(bb.maxY + 1.0);
      int i5 = MathHelper.floor_double(bb.minZ);
      int i6 = MathHelper.floor_double(bb.maxZ + 1.0);
      WorldBorder worldBorder2 = this.getWorldBorder();
      boolean outsideBorder = entityIn.isOutsideBorder();
      boolean insideBorder = this.isInsideBorder(worldBorder2, entityIn);
      IBlockState defaultState = Blocks.stone.getDefaultState();
      net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

      for (int i7 = i; i7 < i2; i7++) {
         for (int i8 = i5; i8 < i6; i8++) {
            if (this.isBlockLoaded(mutableBlockPos.set(i7, 64, i8))) {
               for (int i9 = i3 - 1; i9 < i4; i9++) {
                  mutableBlockPos.set(i7, i9, i8);
                  if (outsideBorder && insideBorder) {
                     entityIn.setOutsideBorder(false);
                  } else if (!outsideBorder && !insideBorder) {
                     entityIn.setOutsideBorder(true);
                  }

                  IBlockState iBlockState = defaultState;
                  if (worldBorder2.contains(mutableBlockPos) || !insideBorder) {
                     iBlockState = this.getBlockState(mutableBlockPos);
                  }

                  iBlockState.getBlock().addCollisionBoxesToList(this, mutableBlockPos, iBlockState, bb, arrayList, entityIn);
               }
            }
         }
      }

      double d2 = 0.25;
      List entitiesWithinAABBExcludingEntity = this.getEntitiesWithinAABBExcludingEntity(entityIn, bb.expand(d2, d2, d2));

      for (int i10 = 0; i10 < entitiesWithinAABBExcludingEntity.size(); i10++) {
         if (entityIn.riddenByEntity != entitiesWithinAABBExcludingEntity && entityIn.ridingEntity != entitiesWithinAABBExcludingEntity) {
            AxisAlignedBB collisionBoundingBox = ((Entity)entitiesWithinAABBExcludingEntity.get(i10)).getCollisionBoundingBox();
            if (collisionBoundingBox != null && collisionBoundingBox.intersectsWith(bb)) {
               arrayList.add(collisionBoundingBox);
            }

            collisionBoundingBox = entityIn.getCollisionBox((Entity)entitiesWithinAABBExcludingEntity.get(i10));
            if (collisionBoundingBox != null && collisionBoundingBox.intersectsWith(bb)) {
               arrayList.add(collisionBoundingBox);
            }
         }
      }

      return arrayList;
   }

   public ISaveHandler getSaveHandler() {
      return this.saveHandler;
   }

   public long getSeed() {
      return this.worldInfo.getSeed();
   }

   public List<Entity> getLoadedEntityList() {
      return this.loadedEntityList;
   }

   public void checkSessionLock() throws MinecraftException {
      this.saveHandler.checkSessionLock();
   }

   public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
      for (int i = 0; i < this.worldAccesses.size(); i++) {
         this.worldAccesses.get(i).sendBlockBreakProgress(breakerId, pos, progress);
      }
   }

   public void tick() {
      this.updateWeather();
   }

   public int getLightFromNeighbors(BlockPos pos) {
      return this.getLight(pos, true);
   }

   public void setAllowedSpawnTypes(boolean hostile, boolean peaceful) {
      this.spawnHostileMobs = hostile;
      this.spawnPeacefulMobs = peaceful;
   }

   public void setEntityState(Entity entityIn, byte state) {
   }

   public String getDebugLoadedEntities() {
      return "All: " + this.loadedEntityList.size();
   }

   public GameRules getGameRules() {
      return this.worldInfo.getGameRulesInstance();
   }

   public boolean isSpawnChunk(int x, int z) {
      BlockPos spawnPoint = this.getSpawnPoint();
      int i = x * 16 + 8 - spawnPoint.getX();
      int i2 = z * 16 + 8 - spawnPoint.getZ();
      return i >= -128 && i <= 128 && i2 >= -128 && i2 <= 128;
   }

   public boolean isBlockPowered(BlockPos pos) {
      return this.getRedstonePower(pos.down(), EnumFacing.DOWN) > 0
         ? true
         : (
            this.getRedstonePower(pos.up(), EnumFacing.UP) > 0
               ? true
               : (
                  this.getRedstonePower(pos.north(), EnumFacing.NORTH) > 0
                     ? true
                     : (
                        this.getRedstonePower(pos.south(), EnumFacing.SOUTH) > 0
                           ? true
                           : (this.getRedstonePower(pos.west(), EnumFacing.WEST) > 0 ? true : this.getRedstonePower(pos.east(), EnumFacing.EAST) > 0)
                     )
               )
         );
   }

   public MovingObjectPosition rayTraceBlocks(Vec3 start, Vec3 end, boolean stopOnLiquid) {
      return this.rayTraceBlocks(start, end, stopOnLiquid, false, false);
   }

   public boolean addTileEntity(TileEntity tile) {
      boolean conditionMet = this.loadedTileEntityList.add(tile);
      if (conditionMet && tile instanceof ITickable) {
         this.tickableTileEntities.add(tile);
      }

      return conditionMet;
   }

   public boolean isBlockTickPending(BlockPos pos, Block blockType) {
      return false;
   }

   public boolean canBlockFreeze(BlockPos pos, boolean noWaterAdj) {
      float floatTemperature = this.getBiomeGenForCoords(pos).getFloatTemperature(pos);
      if (floatTemperature > 0.15F) {
         return false;
      }

      if (pos.getY() >= 0 && pos.getY() < 256 && this.getLightFor(EnumSkyBlock.BLOCK, pos) < 10) {
         IBlockState blockState2 = this.getBlockState(pos);
         Block block3 = blockState2.getBlock();
         if ((block3 == Blocks.water || block3 == Blocks.flowing_water) && blockState2.getValue(BlockLiquid.LEVEL) == 0) {
            if (!noWaterAdj) {
               return true;
            }

            if (!this.isWater(pos.west()) || !this.isWater(pos.east()) || !this.isWater(pos.north()) || !this.isWater(pos.south())) {
               return true;
            }
         }
      }

      return false;
   }

   public int calculateSkylightSubtracted(float f) {
      float celestialAngle = this.getCelestialAngle(f);
      float f2 = 1.0F - (MathHelper.cos(celestialAngle * (float) Math.PI * 2.0F) * 2.0F + 0.5F);
      f2 = MathHelper.clamp_float(f2, 0.0F, 1.0F);
      f2 = 1.0F - f2;
      f2 = (float)(f2 * (1.0 - this.getRainStrength(f) * 5.0F / 16.0));
      f2 = (float)(f2 * (1.0 - this.getThunderStrength(f) * 5.0F / 16.0));
      f2 = 1.0F - f2;
      return (int)(f2 * 11.0F);
   }

   public Entity getEntityByID(int id) {
      return this.entitiesById.lookup(id);
   }

   public boolean isBlockFullCube(BlockPos pos) {
      IBlockState blockState = this.getBlockState(pos);
      AxisAlignedBB axisAlignedBB = blockState.getBlock().getCollisionBoundingBox(this, pos, blockState);
      return axisAlignedBB != null && axisAlignedBB.getAverageEdgeLength() >= 1.0;
   }

   public WorldInfo getWorldInfo() {
      return this.worldInfo;
   }

   @Override
   public int getCombinedLight(BlockPos pos, int lightValue) {
      int lightFromNeighborsFor = this.getLightFromNeighborsFor(EnumSkyBlock.SKY, pos);
      int lightFromNeighborsFor2 = this.getLightFromNeighborsFor(EnumSkyBlock.BLOCK, pos);
      if (lightFromNeighborsFor2 < lightValue) {
         lightFromNeighborsFor2 = lightValue;
      }

      return lightFromNeighborsFor << 20 | lightFromNeighborsFor2 << 4;
   }

   private void spawnParticle(
      int particleID, boolean flag, double xCood, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... iArray
   ) {
      for (int i = 0; i < this.worldAccesses.size(); i++) {
         SpawnParticleEvent spawnParticleEvent = new SpawnParticleEvent(particleID);
         Novoline.getNovoline().getBus().post(spawnParticleEvent);
         if (spawnParticleEvent.isCancelled()) {
            return;
         }

         this.worldAccesses.get(i).spawnParticle(particleID, flag, xCood, yCoord, zCoord, xOffset, yOffset, zOffset, iArray);
      }
   }

   public List<Entity> getEntitiesInAABBexcluding(Entity entityIn, AxisAlignedBB boundingBox, Predicate<? super Entity> predicate) {
      List<Entity> list = Lists.newArrayList();
      int i = MathHelper.floor_double((boundingBox.minX - 2.0) / 16.0);
      int i2 = MathHelper.floor_double((boundingBox.maxX + 2.0) / 16.0);
      int i3 = MathHelper.floor_double((boundingBox.minZ - 2.0) / 16.0);
      int i4 = MathHelper.floor_double((boundingBox.maxZ + 2.0) / 16.0);

      for (int index = i; index <= i2; index++) {
         for (int innerIndex = i3; innerIndex <= i4; innerIndex++) {
            if (this.isChunkLoaded(index, innerIndex, true)) {
               this.getChunkFromChunkCoords(index, innerIndex).getEntitiesWithinAABBForEntity(entityIn, boundingBox, list, predicate);
            }
         }
      }

      return list;
   }

   public void setTileEntity(BlockPos pos, TileEntity tileEntityIn) {
      if (tileEntityIn != null && !tileEntityIn.isInvalid()) {
         if (this.processingLoadedTiles) {
            tileEntityIn.setPos(pos);
            Iterator<TileEntity> iterator = this.addedTileEntityList.iterator();

            while (iterator.hasNext()) {
               TileEntity tileEntity = iterator.next();
               if (tileEntity.getPos().equals(pos)) {
                  tileEntity.invalidate();
                  iterator.remove();
               }
            }

            this.addedTileEntityList.add(tileEntityIn);
         } else {
            this.addTileEntity(tileEntityIn);
            this.getChunkFromBlockCoords(pos).addTileEntity(pos, tileEntityIn);
         }
      }
   }

   public void forceBlockUpdateTick(Block blockType, BlockPos pos, Random random) {
      this.scheduledUpdatesAreImmediate = true;
      blockType.updateTick(this, pos, this.getBlockState(pos), random);
      this.scheduledUpdatesAreImmediate = false;
   }

   public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
      if (!this.isValid(pos)) {
         return false;
      }

      if (!this.isRemote && this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD) {
         return false;
      }

      Chunk chunk = this.getChunkFromBlockCoords(pos);
      Block block = newState.getBlock();
      IBlockState blockState = chunk.setBlockState(pos, newState);
      if (blockState == null) {
         return false;
      }

      Block block2 = blockState.getBlock();
      if (block.getLightOpacity() != block2.getLightOpacity() || block.getLightValue() != block2.getLightValue()) {
         this.theProfiler.startSection("checkLight");
         this.checkLight(pos);
         this.theProfiler.endSection();
      }

      if ((flags & 2) != 0 && (!this.isRemote || (flags & 4) == 0) && chunk.isPopulated()) {
         this.markBlockForUpdate(pos);
      }

      if (!this.isRemote && (flags & 1) != 0) {
         this.notifyNeighborsRespectDebug(pos, blockState.getBlock());
         if (block.hasComparatorInputOverride()) {
            this.updateComparatorOutputLevel(pos, block);
         }
      }

      return true;
   }

   public void markBlockForUpdate(BlockPos pos) {
      for (int index = 0; index < this.worldAccesses.size(); index++) {
         this.worldAccesses.get(index).markBlockForUpdate(pos);
      }
   }

   public void calculateInitialSkylight() {
      int i = this.calculateSkylightSubtracted(1.0F);
      if (i != this.skylightSubtracted) {
         this.skylightSubtracted = i;
      }
   }

   public void setThunderStrength(float strength) {
      this.prevThunderingStrength = strength;
      this.thunderingStrength = strength;
   }

   public IChunkProvider getChunkProvider() {
      return this.chunkProvider;
   }

   public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) {
      if (this.isBlockLoaded(pos)) {
         this.getChunkFromBlockCoords(pos).setChunkModified();
      }
   }

   public Block getGroundAboveSeaLevel(BlockPos pos) {
      BlockPos blockPos = new BlockPos(pos.getX(), this.getSeaLevel(), pos.getZ());

      while (!this.isAirBlock(blockPos.up())) {
         blockPos = blockPos.up();
      }

      return this.getBlockState(blockPos).getBlock();
   }

   public boolean isAreaLoaded(StructureBoundingBox box) {
      return this.checkStructureBoundingBox(box);
   }

   public boolean checkBlockCollision(AxisAlignedBB bb) {
      int i = MathHelper.floor_double(bb.minX);
      int i2 = MathHelper.floor_double(bb.maxX);
      int i3 = MathHelper.floor_double(bb.minY);
      int i4 = MathHelper.floor_double(bb.maxY);
      int nestedIndex = MathHelper.floor_double(bb.minZ);
      int outerIndex = MathHelper.floor_double(bb.maxZ);
      net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

      for (int i5 = i; i5 <= i2; i5++) {
         for (int i6 = i3; i6 <= i4; i6++) {
            for (int nestedIndex2 = nestedIndex; nestedIndex2 <= outerIndex; nestedIndex2++) {
               if (this.getBlockState(mutableBlockPos.set(i5, i6, nestedIndex2)).getBlock().getMaterial() != Material.air) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public int getHeight() {
      return 256;
   }

   public <T extends Entity> T findNearestEntityWithinAABB(Class<? extends T> entityType, AxisAlignedBB aabb, T closestTo) {
      List<T> list = this.getEntitiesWithinAABB(entityType, aabb);
      Entity entity2 = null;
      double d2 = Double.MAX_VALUE;

      for (int index = 0; index < list.size(); index++) {
         T t2 = (T)list.get(index);
         if (t2 != closestTo && EntitySelectors.NOT_SPECTATING.apply(t2)) {
            double distanceSqToEntity = closestTo.getDistanceSqToEntity(t2);
            if (distanceSqToEntity <= d2) {
               entity2 = t2;
               d2 = distanceSqToEntity;
            }
         }
      }

      return (T)entity2;
   }

   public int getLightFor(EnumSkyBlock type, BlockPos pos) {
      if (pos.getY() < 0) {
         pos = new BlockPos(pos.getX(), 0, pos.getZ());
      }

      if (!this.isValid(pos)) {
         return type.defaultLightValue;
      } else {
         return !this.isBlockLoaded(pos) ? type.defaultLightValue : this.getChunkFromBlockCoords(pos).getLightFor(type, pos);
      }
   }

   protected void updateBlocks() {
      this.setActivePlayerChunksAndCheckLight();
   }

   public void notifyNeighborsOfStateExcept(BlockPos pos, Block blockType, EnumFacing skipSide) {
      if (skipSide != EnumFacing.WEST) {
         this.notifyBlockOfStateChange(pos.west(), blockType);
      }

      if (skipSide != EnumFacing.EAST) {
         this.notifyBlockOfStateChange(pos.east(), blockType);
      }

      if (skipSide != EnumFacing.DOWN) {
         this.notifyBlockOfStateChange(pos.down(), blockType);
      }

      if (skipSide != EnumFacing.UP) {
         this.notifyBlockOfStateChange(pos.up(), blockType);
      }

      if (skipSide != EnumFacing.NORTH) {
         this.notifyBlockOfStateChange(pos.north(), blockType);
      }

      if (skipSide != EnumFacing.SOUTH) {
         this.notifyBlockOfStateChange(pos.south(), blockType);
      }
   }

   public boolean canSnowAt(BlockPos pos, boolean checkLight) {
      float floatTemperature = this.getBiomeGenForCoords(pos).getFloatTemperature(pos);
      if (floatTemperature > 0.15F) {
         return false;
      } else {
         return !checkLight
            ? true
            : pos.getY() >= 0
               && pos.getY() < 256
               && this.getLightFor(EnumSkyBlock.BLOCK, pos) < 10
               && this.getBlockState(pos).getBlock().getMaterial() == Material.air
               && Blocks.snow_layer.canPlaceBlockAt(this, pos);
      }
   }

   public float getCelestialAngle(float partialTicks) {
      return this.provider.calculateCelestialAngle(this.worldInfo.getWorldTime(), partialTicks);
   }

   public List<Entity> getEntitiesWithinAABBExcludingEntity(Entity entityIn, AxisAlignedBB bb) {
      return this.getEntitiesInAABBexcluding(entityIn, bb, EntitySelectors.NOT_SPECTATING);
   }

   public int getRedstonePower(BlockPos pos, EnumFacing facing) {
      IBlockState blockState = this.getBlockState(pos);
      Block block = blockState.getBlock();
      return block.isNormalCube() ? this.getStrongPower(pos) : block.getWeakPower(this, pos, blockState, facing);
   }

   public void setSkylightSubtracted(int newSkylightSubtracted) {
      this.skylightSubtracted = newSkylightSubtracted;
   }

   public float getCelestialAngleRadians(float partialTicks) {
      float celestialAngle = this.getCelestialAngle(partialTicks);
      return celestialAngle * (float) Math.PI * 2.0F;
   }

   public boolean destroyBlock(BlockPos pos, boolean dropBlock) {
      IBlockState blockState2 = this.getBlockState(pos);
      Block block3 = blockState2.getBlock();
      if (block3.getMaterial() == Material.air) {
         return false;
      }

      this.playAuxSFX(2001, pos, Block.getStateId(blockState2));
      if (dropBlock) {
         block3.dropBlockAsItem(this, pos, blockState2, 0);
      }

      return this.setBlockState(pos, Blocks.air.getDefaultState(), 3);
   }

   public DifficultyInstance getDifficultyForLocation(BlockPos pos) {
      long l = 0L;
      float f = 0.0F;
      if (this.isBlockLoaded(pos)) {
         f = this.getCurrentMoonPhaseFactor();
         l = this.getChunkFromBlockCoords(pos).getInhabitedTime();
      }

      return new DifficultyInstance(this.getDifficulty(), this.getWorldTime(), l, f);
   }

   public boolean isAnyPlayerWithinRangeAt(double x, double y, double z, double range) {
      for (int index = 0; index < this.playerEntities.size(); index++) {
         EntityPlayer entityPlayer = this.playerEntities.get(index);
         if (EntitySelectors.NOT_SPECTATING.apply(entityPlayer)) {
            double distanceSq = entityPlayer.getDistanceSq(x, y, z);
            if (range < 0.0 || distanceSq < range * range) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean extinguishFire(EntityPlayer player, BlockPos pos, EnumFacing side) {
      pos = pos.offset(side);
      if (this.getBlockState(pos).getBlock() == Blocks.fire) {
         this.playAuxSFXAtEntity(player, 1004, pos, 0);
         this.setBlockToAir(pos);
         return true;
      } else {
         return false;
      }
   }

   public float getStarBrightness(float partialTicks) {
      float celestialAngle = this.getCelestialAngle(partialTicks);
      float f = 1.0F - (MathHelper.cos(celestialAngle * (float) Math.PI * 2.0F) * 2.0F + 0.25F);
      f = MathHelper.clamp_float(f, 0.0F, 1.0F);
      return f * f * 0.5F;
   }

   public VillageCollection getVillageCollection() {
      return this.villageCollectionObj;
   }

   public int getLastLightningBolt() {
      return this.lastLightningBolt;
   }

   public boolean isThundering() {
      return this.getThunderStrength(1.0F) > 0.9;
   }

   public void updateBlockTick(BlockPos pos, Block blockIn, int delay, int priority) {
   }

   public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam) {
      blockIn.onBlockEventReceived(this, pos, this.getBlockState(pos), eventID, eventParam);
   }

   @Override
   public BiomeGenBase getBiomeGenForCoords(final BlockPos pos) {
      if (this.isBlockLoaded(pos)) {
         Chunk chunkFromBlockCoords = this.getChunkFromBlockCoords(pos);

         try {
            return chunkFromBlockCoords.getBiome(pos, this.provider.getWorldChunkManager());
         } catch (Throwable throwable) {
            CrashReport crashReport4 = CrashReport.makeCrashReport(throwable, "Getting biome");
            crashReport4.makeCategory("Coordinates of biome request").addCrashSectionCallable("Location", new Callable<String>() {
               public String call() throws Exception {
                  return CrashReportCategory.getCoordinateInfo(pos);
               }
            });
            throw new ReportedException(crashReport4);
         }
      } else {
         return this.provider.getWorldChunkManager().getBiomeGenerator(pos, BiomeGenBase.plains);
      }
   }

   public boolean isBlockinHighHumidity(BlockPos pos) {
      return this.getBiomeGenForCoords(pos).isHighHumidity();
   }

   protected abstract int getRenderDistanceChunks();

   public int getLightFromNeighborsFor(EnumSkyBlock type, BlockPos pos) {
      if (this.provider.getHasNoSky() && type == EnumSkyBlock.SKY) {
         return 0;
      }

      if (pos.getY() < 0) {
         pos = new BlockPos(pos.getX(), 0, pos.getZ());
      }

      if (!this.isValid(pos)) {
         return type.defaultLightValue;
      }

      if (!this.isBlockLoaded(pos)) {
         return type.defaultLightValue;
      }

      if (this.getBlockState(pos).getBlock().getUseNeighborBrightness()) {
         int lightFor = this.getLightFor(type, pos.up());
         int lightFor2 = this.getLightFor(type, pos.east());
         int lightFor3 = this.getLightFor(type, pos.west());
         int lightFor4 = this.getLightFor(type, pos.south());
         int lightFor5 = this.getLightFor(type, pos.north());
         if (lightFor2 > lightFor) {
            lightFor = lightFor2;
         }

         if (lightFor3 > lightFor) {
            lightFor = lightFor3;
         }

         if (lightFor4 > lightFor) {
            lightFor = lightFor4;
         }

         if (lightFor5 > lightFor) {
            lightFor = lightFor5;
         }

         return lightFor;
      } else {
         Chunk chunk = this.getChunkFromBlockCoords(pos);
         return chunk.getLightFor(type, pos);
      }
   }

   public boolean isRaining() {
      return this.getRainStrength(1.0F) > 0.2;
   }

   protected void calculateInitialWeather() {
      if (this.worldInfo.isRaining()) {
         this.rainingStrength = 1.0F;
         if (this.worldInfo.isThundering()) {
            this.thunderingStrength = 1.0F;
         }
      }
   }

   public float getBlockDensity(Vec3 vec, AxisAlignedBB bb) {
      double d2 = 1.0 / ((bb.maxX - bb.minX) * 2.0 + 1.0);
      double d3 = 1.0 / ((bb.maxY - bb.minY) * 2.0 + 1.0);
      double d4 = 1.0 / ((bb.maxZ - bb.minZ) * 2.0 + 1.0);
      double d5 = (1.0 - Math.floor(1.0 / d2) * d2) / 2.0;
      double d6 = (1.0 - Math.floor(1.0 / d4) * d4) / 2.0;
      if (d2 >= 0.0 && d3 >= 0.0 && d4 >= 0.0) {
         int i = 0;
         int i2 = 0;

         for (float f = 0.0F; f <= 1.0F; f = (float)(f + d2)) {
            for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float)(f2 + d3)) {
               for (float f3 = 0.0F; f3 <= 1.0F; f3 = (float)(f3 + d4)) {
                  double d7 = bb.minX + (bb.maxX - bb.minX) * f;
                  double d8 = bb.minY + (bb.maxY - bb.minY) * f2;
                  double d9 = bb.minZ + (bb.maxZ - bb.minZ) * f3;
                  if (this.rayTraceBlocks(new Vec3(d7 + d5, d8, d9 + d6), vec) == null) {
                     i++;
                  }

                  i2++;
               }
            }
         }

         return (float)i / i2;
      } else {
         return 0.0F;
      }
   }

   public int getSkylightSubtracted() {
      return this.skylightSubtracted;
   }

   public void setItemData(String dataID, WorldSavedData worldSavedDataIn) {
      this.mapStorage.setData(dataID, worldSavedDataIn);
   }

   public BlockPos getSpawnPoint() {
      BlockPos blockPos = new BlockPos(this.worldInfo.getSpawnX(), this.worldInfo.getSpawnY(), this.worldInfo.getSpawnZ());
      if (!this.getWorldBorder().contains(blockPos)) {
         blockPos = this.getHeight(new BlockPos(this.getWorldBorder().getCenterX(), 0.0, this.getWorldBorder().getCenterZ()));
      }

      return blockPos;
   }

   public int getStrongPower(BlockPos pos) {
      int i = Math.max(0, this.getStrongPower(pos.down(), EnumFacing.DOWN));
      if (i >= 15) {
         return i;
      }

      i = Math.max(i, this.getStrongPower(pos.up(), EnumFacing.UP));
      if (i >= 15) {
         return i;
      }

      i = Math.max(i, this.getStrongPower(pos.north(), EnumFacing.NORTH));
      if (i >= 15) {
         return i;
      }

      i = Math.max(i, this.getStrongPower(pos.south(), EnumFacing.SOUTH));
      if (i >= 15) {
         return i;
      }

      i = Math.max(i, this.getStrongPower(pos.west(), EnumFacing.WEST));
      if (i >= 15) {
         return i;
      }

      i = Math.max(i, this.getStrongPower(pos.east(), EnumFacing.EAST));
      return i >= 15 ? i : i;
   }

   private boolean isValid(BlockPos pos) {
      return pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000 && pos.getY() >= 0 && pos.getY() < 256;
   }

   public boolean isFlammableWithin(AxisAlignedBB bb) {
      int i = MathHelper.floor_double(bb.minX);
      int i2 = MathHelper.floor_double(bb.maxX + 1.0);
      int i3 = MathHelper.floor_double(bb.minY);
      int i4 = MathHelper.floor_double(bb.maxY + 1.0);
      int i5 = MathHelper.floor_double(bb.minZ);
      int i6 = MathHelper.floor_double(bb.maxZ + 1.0);
      if (this.isAreaLoaded(i, i3, i5, i2, i4, i6, true)) {
         net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

         for (int nestedIndex = i; nestedIndex < i2; nestedIndex++) {
            for (int outerIndex = i3; outerIndex < i4; outerIndex++) {
               for (int index = i5; index < i6; index++) {
                  Block block = this.getBlockState(mutableBlockPos.set(nestedIndex, outerIndex, index)).getBlock();
                  if (block == Blocks.fire || block == Blocks.flowing_lava || block == Blocks.lava) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public boolean checkNoEntityCollision(AxisAlignedBB bb, Entity entityIn) {
      List<Entity> list = this.getEntitiesWithinAABBExcludingEntity((Entity)null, bb);

      for (int index = 0; index < list.size(); index++) {
         Entity entity = list.get(index);
         if (!entity.isDead
            && entity.preventEntitySpawning
            && entity != entityIn
            && (entityIn == null || entityIn.ridingEntity != entity && entityIn.riddenByEntity != entity)) {
            return false;
         }
      }

      return true;
   }

   public Vec3 getFogColor(float partialTicks) {
      float celestialAngle = this.getCelestialAngle(partialTicks);
      return this.provider.getFogColor(celestialAngle, partialTicks);
   }

   public boolean checkNoEntityCollision(AxisAlignedBB bb) {
      return this.checkNoEntityCollision(bb, (Entity)null);
   }

   public float getSunBrightness(float f) {
      float celestialAngle = this.getCelestialAngle(f);
      float f2 = 1.0F - (MathHelper.cos(celestialAngle * (float) Math.PI * 2.0F) * 2.0F + 0.2F);
      f2 = MathHelper.clamp_float(f2, 0.0F, 1.0F);
      f2 = 1.0F - f2;
      f2 = (float)(f2 * (1.0 - this.getRainStrength(f) * 5.0F / 16.0));
      f2 = (float)(f2 * (1.0 - this.getThunderStrength(f) * 5.0F / 16.0));
      return f2 * 0.8F + 0.2F;
   }

   public double getHorizon() {
      return this.worldInfo.getTerrainType() == WorldType.FLAT ? 0.0 : 63.0;
   }

   public void playSound(double x, double y, double z, String soundName, float volume, float pitch, boolean distanceDelay) {
   }

   public List<AxisAlignedBB> getCollisionBoxes(AxisAlignedBB bb) {
      ArrayList arrayList = Lists.newArrayList();
      int nestedIndex = MathHelper.floor_double(bb.minX);
      int outerIndex = MathHelper.floor_double(bb.maxX + 1.0);
      int index = MathHelper.floor_double(bb.minY);
      int i = MathHelper.floor_double(bb.maxY + 1.0);
      int i2 = MathHelper.floor_double(bb.minZ);
      int i3 = MathHelper.floor_double(bb.maxZ + 1.0);
      net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

      for (int nestedIndex2 = nestedIndex; nestedIndex2 < outerIndex; nestedIndex2++) {
         for (int i4 = i2; i4 < i3; i4++) {
            if (this.isBlockLoaded(mutableBlockPos.set(nestedIndex2, 64, i4))) {
               for (int i5 = index - 1; i5 < i; i5++) {
                  mutableBlockPos.set(nestedIndex2, i5, i4);
                  IBlockState iBlockState;
                  if (nestedIndex2 >= -30000000 && nestedIndex2 < 30000000 && i4 >= -30000000 && i4 < 30000000) {
                     iBlockState = this.getBlockState(mutableBlockPos);
                  } else {
                     iBlockState = Blocks.bedrock.getDefaultState();
                  }

                  iBlockState.getBlock().addCollisionBoxesToList(this, mutableBlockPos, iBlockState, bb, arrayList, (Entity)null);
               }
            }
         }
      }

      return arrayList;
   }

   public void removeTileEntity(BlockPos pos) {
      TileEntity tileEntity = this.getTileEntity(pos);
      if (tileEntity != null && this.processingLoadedTiles) {
         tileEntity.invalidate();
         this.addedTileEntityList.remove(tileEntity);
      } else {
         if (tileEntity != null) {
            this.addedTileEntityList.remove(tileEntity);
            this.loadedTileEntityList.remove(tileEntity);
            this.tickableTileEntities.remove(tileEntity);
         }

         this.getChunkFromBlockCoords(pos).removeTileEntity(pos);
      }
   }

   public void setLightFor(EnumSkyBlock type, BlockPos pos, int lightValue) {
      if (this.isValid(pos) && this.isBlockLoaded(pos)) {
         this.getChunkFromBlockCoords(pos).setLightFor(type, pos, lightValue);
         this.notifyLightSet(pos);
      }
   }

   public BlockPos getStrongholdPos(String name, BlockPos pos) {
      return this.getChunkProvider().getStrongholdGen(this, name, pos);
   }

   public boolean checkStructureBoundingBox(StructureBoundingBox structureBoundingBox) {
      return this.isAreaLoaded(structureBoundingBox.minX, structureBoundingBox.minY, structureBoundingBox.minZ, structureBoundingBox.maxX, structureBoundingBox.maxY, structureBoundingBox.maxZ, true);
   }

   public Chunk getChunkFromBlockCoords(BlockPos pos) {
      return this.getChunkFromChunkCoords(pos.getX() >> 4, pos.getZ() >> 4);
   }

   public boolean canSeeSky(BlockPos pos) {
      return this.getChunkFromBlockCoords(pos).canSeeSky(pos);
   }

   public void joinEntityInSurroundings(Entity entityIn) {
      int i = MathHelper.floor_double(entityIn.posX / 16.0);
      int i2 = MathHelper.floor_double(entityIn.posZ / 16.0);

      for (int i3 = i - 2; i3 <= i + 2; i3++) {
         for (int i4 = i2 - 2; i4 <= i2 + 2; i4++) {
            this.getChunkFromChunkCoords(i3, i4);
         }
      }

      if (!this.loadedEntityList.contains(entityIn)) {
         this.loadedEntityList.add(entityIn);
      }
   }

   public void playBroadcastSound(int i, BlockPos pos, int i2) {
      for (int index = 0; index < this.worldAccesses.size(); index++) {
         this.worldAccesses.get(index).broadcastSound(i, pos, i2);
      }
   }

   public EntityPlayer getClosestPlayerToEntity(Entity entityIn, double distance) {
      return this.getClosestPlayer(entityIn.posX, entityIn.posY, entityIn.posZ, distance);
   }

   public void markTileEntityForRemoval(TileEntity tileEntityIn) {
      this.tileEntitiesToBeRemoved.add(tileEntityIn);
   }

   public boolean setBlockState(BlockPos pos, IBlockState state) {
      return this.setBlockState(pos, state, 3);
   }

   public void setLastLightningBolt(int lastLightningBoltIn) {
      this.lastLightningBolt = lastLightningBoltIn;
   }

   public void markBlocksDirtyVertical(int x1, int z1, int x2, int z2) {
      if (x2 > z2) {
         int i = z2;
         z2 = x2;
         x2 = i;
      }

      if (!this.provider.getHasNoSky()) {
         for (int innerIndex = x2; innerIndex <= z2; innerIndex++) {
            this.checkLightFor(EnumSkyBlock.SKY, new BlockPos(x1, innerIndex, z1));
         }
      }

      this.markBlockRangeForRenderUpdate(x1, x2, z1, x1, z2, z1);
   }

   public boolean isAnyLiquid(AxisAlignedBB bb) {
      int i = MathHelper.floor_double(bb.minX);
      int i2 = MathHelper.floor_double(bb.maxX);
      int i3 = MathHelper.floor_double(bb.minY);
      int i4 = MathHelper.floor_double(bb.maxY);
      int nestedIndex = MathHelper.floor_double(bb.minZ);
      int outerIndex = MathHelper.floor_double(bb.maxZ);
      net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

      for (int i5 = i; i5 <= i2; i5++) {
         for (int i6 = i3; i6 <= i4; i6++) {
            for (int nestedIndex2 = nestedIndex; nestedIndex2 <= outerIndex; nestedIndex2++) {
               if (this.getBlockState(mutableBlockPos.set(i5, i6, nestedIndex2)).getBlock().getMaterial().isLiquid()) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public void notifyLightSet(BlockPos pos) {
      for (int index = 0; index < this.worldAccesses.size(); index++) {
         this.worldAccesses.get(index).notifyLightSet(pos);
      }
   }

   @Override
   public IBlockState getBlockState(BlockPos pos) {
      return !this.isValid(pos) ? Blocks.air.getDefaultState() : this.getChunkFromBlockCoords(pos).getBlockState(pos);
   }

   public float getRainStrength(float delta) {
      return this.prevRainingStrength + (this.rainingStrength - this.prevRainingStrength) * delta;
   }

   public int isBlockIndirectlyGettingPowered(BlockPos pos) {
      int i = 0;

      for (EnumFacing enumFacing : EnumFacing.values()) {
         int redstonePower = this.getRedstonePower(pos.offset(enumFacing), enumFacing);
         if (redstonePower >= 15) {
            return 15;
         }

         if (redstonePower > i) {
            i = redstonePower;
         }
      }

      return i;
   }

   protected void setActivePlayerChunksAndCheckLight() {
      this.activeChunkSet.clear();
      this.theProfiler.startSection("buildList");

      for (int index = 0; index < this.playerEntities.size(); index++) {
         EntityPlayer entityPlayer = this.playerEntities.get(index);
         int i = MathHelper.floor_double(entityPlayer.posX / 16.0);
         int i2 = MathHelper.floor_double(entityPlayer.posZ / 16.0);
         int renderDistanceChunks = this.getRenderDistanceChunks();

         for (int index2 = -renderDistanceChunks; index2 <= renderDistanceChunks; index2++) {
            for (int innerIndex = -renderDistanceChunks; innerIndex <= renderDistanceChunks; innerIndex++) {
               this.activeChunkSet.add(new ChunkCoordIntPair(index2 + i, innerIndex + i2));
            }
         }
      }

      this.theProfiler.endSection();
      if (this.ambientTickCountdown > 0) {
         this.ambientTickCountdown--;
      }

      this.theProfiler.startSection("playerCheckLight");
      if (!this.playerEntities.isEmpty()) {
         int count = this.rand.nextInt(this.playerEntities.size());
         EntityPlayer entityPlayer2 = this.playerEntities.get(count);
         int i3 = MathHelper.floor_double(entityPlayer2.posX) + this.rand.nextInt(11) - 5;
         int i4 = MathHelper.floor_double(entityPlayer2.posY) + this.rand.nextInt(11) - 5;
         int i5 = MathHelper.floor_double(entityPlayer2.posZ) + this.rand.nextInt(11) - 5;
         this.checkLight(new BlockPos(i3, i4, i5));
      }

      this.theProfiler.endSection();
   }

   public boolean spawnEntityInWorld(Entity entityIn) {
      int i = MathHelper.floor_double(entityIn.posX / 16.0);
      int i2 = MathHelper.floor_double(entityIn.posZ / 16.0);
      boolean conditionMet = entityIn.forceSpawn;
      if (entityIn instanceof EntityPlayer) {
         conditionMet = true;
      }

      if (!conditionMet && !this.isChunkLoaded(i, i2, true)) {
         return false;
      }

      if (entityIn instanceof EntityPlayer entityPlayer) {
         this.playerEntities.add(entityPlayer);
         this.updateAllPlayersSleepingFlag();
      }

      this.getChunkFromChunkCoords(i, i2).addEntity(entityIn);
      this.loadedEntityList.add(entityIn);
      this.onEntityAdded(entityIn);
      return true;
   }

   public EntityPlayer getPlayerEntityByUUID(UUID uUID) {
      for (int index = 0; index < this.playerEntities.size(); index++) {
         EntityPlayer entityPlayer = this.playerEntities.get(index);
         if (entityPlayer != null && uUID.equals(entityPlayer.getUniqueID())) {
            return entityPlayer;
         }
      }

      return null;
   }

   protected void updateWeather() {
      if (!this.provider.getHasNoSky() && !this.isRemote) {
         int cleanWeatherTime = this.worldInfo.getCleanWeatherTime();
         if (cleanWeatherTime > 0) {
            this.worldInfo.setCleanWeatherTime(--cleanWeatherTime);
            this.worldInfo.setThunderTime(this.worldInfo.isThundering() ? 1 : 2);
            this.worldInfo.setRainTime(this.worldInfo.isRaining() ? 1 : 2);
         }

         int thunderTime = this.worldInfo.getThunderTime();
         if (thunderTime <= 0) {
            if (this.worldInfo.isThundering()) {
               this.worldInfo.setThunderTime(this.rand.nextInt(12000) + 3600);
            } else {
               this.worldInfo.setThunderTime(this.rand.nextInt(168000) + 12000);
            }
         } else {
            this.worldInfo.setThunderTime(--thunderTime);
            if (thunderTime <= 0) {
               this.worldInfo.setThundering(!this.worldInfo.isThundering());
            }
         }

         this.prevThunderingStrength = this.thunderingStrength;
         if (this.worldInfo.isThundering()) {
            this.thunderingStrength = (float)(this.thunderingStrength + 0.01);
         } else {
            this.thunderingStrength = (float)(this.thunderingStrength - 0.01);
         }

         this.thunderingStrength = MathHelper.clamp_float(this.thunderingStrength, 0.0F, 1.0F);
         int rainTime = this.worldInfo.getRainTime();
         if (rainTime <= 0) {
            if (this.worldInfo.isRaining()) {
               this.worldInfo.setRainTime(this.rand.nextInt(12000) + 12000);
            } else {
               this.worldInfo.setRainTime(this.rand.nextInt(168000) + 12000);
            }
         } else {
            this.worldInfo.setRainTime(--rainTime);
            if (rainTime <= 0) {
               this.worldInfo.setRaining(!this.worldInfo.isRaining());
            }
         }

         this.prevRainingStrength = this.rainingStrength;
         if (this.worldInfo.isRaining()) {
            this.rainingStrength = (float)(this.rainingStrength + 0.01);
         } else {
            this.rainingStrength = (float)(this.rainingStrength - 0.01);
         }

         this.rainingStrength = MathHelper.clamp_float(this.rainingStrength, 0.0F, 1.0F);
      }
   }
}
