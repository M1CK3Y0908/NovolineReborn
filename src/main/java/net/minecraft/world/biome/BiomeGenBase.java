package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom.Item;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenDoublePlant;
import net.minecraft.world.gen.feature.WorldGenSwamp;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BiomeGenBase {
   public IBlockState topBlock = Blocks.grass.getDefaultState();
   public IBlockState fillerBlock = Blocks.dirt.getDefaultState();
   private static final Logger logger = LogManager.getLogger();
   protected static final BiomeGenBase.Height height_Default = new BiomeGenBase.Height(0.1F, 0.2F);
   protected static final BiomeGenBase.Height height_ShallowWaters = new BiomeGenBase.Height(-0.5F, 0.0F);
   protected static final BiomeGenBase.Height height_Oceans = new BiomeGenBase.Height(-1.0F, 0.1F);
   protected static final BiomeGenBase.Height height_DeepOceans = new BiomeGenBase.Height(-1.8F, 0.1F);
   protected static final BiomeGenBase.Height height_LowPlains = new BiomeGenBase.Height(0.125F, 0.05F);
   protected static final BiomeGenBase.Height height_MidPlains = new BiomeGenBase.Height(0.2F, 0.2F);
   protected static final BiomeGenBase.Height height_LowHills = new BiomeGenBase.Height(0.45F, 0.3F);
   public int fillerBlockMetadata = 5169201;
   protected static final BiomeGenBase.Height height_HighPlateaus = new BiomeGenBase.Height(1.5F, 0.025F);
   protected static final BiomeGenBase.Height height_MidHills = new BiomeGenBase.Height(1.0F, 0.5F);
   protected static final BiomeGenBase.Height height_Shores = new BiomeGenBase.Height(0.0F, 0.025F);
   protected static final BiomeGenBase.Height height_RockyWaters = new BiomeGenBase.Height(0.1F, 0.8F);
   protected static final BiomeGenBase.Height height_LowIslands = new BiomeGenBase.Height(0.2F, 0.3F);
   protected static final BiomeGenBase.Height height_PartiallySubmerged = new BiomeGenBase.Height(-0.2F, 0.1F);
   private static final BiomeGenBase[] biomeList = new BiomeGenBase[256];
   public float minHeight = height_Default.rootHeight;
   public static final Set<BiomeGenBase> explorationBiomesList = Sets.newHashSet();
   public float maxHeight = height_Default.variation;
   public float temperature = 0.5F;
   public static final Map<String, BiomeGenBase> BIOME_ID_MAP = Maps.newHashMap();
   public static final BiomeGenBase ocean = new BiomeGenOcean(0).setColor(112).setBiomeName("Ocean").setHeight(height_Oceans);
   public static final BiomeGenBase plains = new BiomeGenPlains(1).setColor(9286496).setBiomeName("Plains");
   public static final BiomeGenBase desert = new BiomeGenDesert(2)
      .setColor(16421912)
      .setBiomeName("Desert")
      .setDisableRain()
      .setTemperatureRainfall(2.0F, 0.0F)
      .setHeight(height_LowPlains);
   public static final BiomeGenBase extremeHills = new BiomeGenHills(3, false)
      .setColor(6316128)
      .setBiomeName("Extreme Hills")
      .setHeight(height_MidHills)
      .setTemperatureRainfall(0.2F, 0.3F);
   public static final BiomeGenBase forest = new BiomeGenForest(4, 0).setColor(353825).setBiomeName("Forest");
   public static final BiomeGenBase taiga = new BiomeGenTaiga(5, 0)
      .setColor(747097)
      .setBiomeName("Taiga")
      .setFillerBlockMetadata(5159473)
      .setTemperatureRainfall(0.25F, 0.8F)
      .setHeight(height_MidPlains);
   public static final BiomeGenBase swampland = new BiomeGenSwamp(6)
      .setColor(522674)
      .setBiomeName("Swampland")
      .setFillerBlockMetadata(9154376)
      .setHeight(height_PartiallySubmerged)
      .setTemperatureRainfall(0.8F, 0.9F);
   public static final BiomeGenBase river = new BiomeGenRiver(7).setColor(255).setBiomeName("River").setHeight(height_ShallowWaters);
   protected boolean enableSnow;
   public float rainfall = 0.5F;
   public static final BiomeGenBase hell = new BiomeGenHell(8).setColor(16711680).setBiomeName("Hell").setDisableRain().setTemperatureRainfall(2.0F, 0.0F);
   public static final BiomeGenBase sky = new BiomeGenEnd(9).setColor(8421631).setBiomeName("The End").setDisableRain();
   public static final BiomeGenBase frozenOcean = new BiomeGenOcean(10)
      .setColor(9474208)
      .setBiomeName("FrozenOcean")
      .setEnableSnow()
      .setHeight(height_Oceans)
      .setTemperatureRainfall(0.0F, 0.5F);
   public int waterColorMultiplier = 16777215;
   public static final BiomeGenBase frozenRiver = new BiomeGenRiver(11)
      .setColor(10526975)
      .setBiomeName("FrozenRiver")
      .setEnableSnow()
      .setHeight(height_ShallowWaters)
      .setTemperatureRainfall(0.0F, 0.5F);
   protected List<BiomeGenBase.SpawnListEntry> spawnableMonsterList = Lists.newArrayList();
   public static final BiomeGenBase icePlains = new BiomeGenSnow(12, false)
      .setColor(16777215)
      .setBiomeName("Ice Plains")
      .setEnableSnow()
      .setTemperatureRainfall(0.0F, 0.5F)
      .setHeight(height_LowPlains);
   public static final BiomeGenBase iceMountains = new BiomeGenSnow(13, false)
      .setColor(10526880)
      .setBiomeName("Ice Mountains")
      .setEnableSnow()
      .setHeight(height_LowHills)
      .setTemperatureRainfall(0.0F, 0.5F);
   public static final BiomeGenBase mushroomIsland = new BiomeGenMushroomIsland(14)
      .setColor(16711935)
      .setBiomeName("MushroomIsland")
      .setTemperatureRainfall(0.9F, 1.0F)
      .setHeight(height_LowIslands);
   public static final BiomeGenBase mushroomIslandShore = new BiomeGenMushroomIsland(15)
      .setColor(10486015)
      .setBiomeName("MushroomIslandShore")
      .setTemperatureRainfall(0.9F, 1.0F)
      .setHeight(height_Shores);
   public static final BiomeGenBase beach = new BiomeGenBeach(16)
      .setColor(16440917)
      .setBiomeName("Beach")
      .setTemperatureRainfall(0.8F, 0.4F)
      .setHeight(height_Shores);
   public static final BiomeGenBase desertHills = new BiomeGenDesert(17)
      .setColor(13786898)
      .setBiomeName("DesertHills")
      .setDisableRain()
      .setTemperatureRainfall(2.0F, 0.0F)
      .setHeight(height_LowHills);
   public static final BiomeGenBase forestHills = new BiomeGenForest(18, 0).setColor(2250012).setBiomeName("ForestHills").setHeight(height_LowHills);
   protected List<BiomeGenBase.SpawnListEntry> spawnableCreatureList = Lists.newArrayList();
   public static final BiomeGenBase taigaHills = new BiomeGenTaiga(19, 0)
      .setColor(1456435)
      .setBiomeName("TaigaHills")
      .setFillerBlockMetadata(5159473)
      .setTemperatureRainfall(0.25F, 0.8F)
      .setHeight(height_LowHills);
   public static final BiomeGenBase extremeHillsEdge = new BiomeGenHills(20, true)
      .setColor(7501978)
      .setBiomeName("Extreme Hills Edge")
      .setHeight(height_MidHills.attenuate())
      .setTemperatureRainfall(0.2F, 0.3F);
   public int color;
   public static final BiomeGenBase jungle = new BiomeGenJungle(21, false)
      .setColor(5470985)
      .setBiomeName("Jungle")
      .setFillerBlockMetadata(5470985)
      .setTemperatureRainfall(0.95F, 0.9F);
   public static final BiomeGenBase jungleHills = new BiomeGenJungle(22, false)
      .setColor(2900485)
      .setBiomeName("JungleHills")
      .setFillerBlockMetadata(5470985)
      .setTemperatureRainfall(0.95F, 0.9F)
      .setHeight(height_LowHills);
   public static final BiomeGenBase jungleEdge = new BiomeGenJungle(23, true)
      .setColor(6458135)
      .setBiomeName("JungleEdge")
      .setFillerBlockMetadata(5470985)
      .setTemperatureRainfall(0.95F, 0.8F);
   public int mapColor;
   public static final BiomeGenBase deepOcean = new BiomeGenOcean(24).setColor(48).setBiomeName("Deep Ocean").setHeight(height_DeepOceans);
   public static final BiomeGenBase stoneBeach = new BiomeGenStoneBeach(25)
      .setColor(10658436)
      .setBiomeName("Stone Beach")
      .setTemperatureRainfall(0.2F, 0.3F)
      .setHeight(height_RockyWaters);
   protected List<BiomeGenBase.SpawnListEntry> spawnableWaterCreatureList = Lists.newArrayList();
   protected List<BiomeGenBase.SpawnListEntry> spawnableCaveCreatureList = Lists.newArrayList();
   public static final BiomeGenBase coldBeach = new BiomeGenBeach(26)
      .setColor(16445632)
      .setBiomeName("Cold Beach")
      .setTemperatureRainfall(0.05F, 0.3F)
      .setHeight(height_Shores)
      .setEnableSnow();
   protected boolean enableRain = true;
   public static final BiomeGenBase birchForest = new BiomeGenForest(27, 2).setBiomeName("Birch Forest").setColor(3175492);
   protected WorldGenTrees worldGeneratorTrees = new WorldGenTrees(false);
   protected WorldGenBigTree worldGeneratorBigTree = new WorldGenBigTree(false);
   public static final BiomeGenBase birchForestHills = new BiomeGenForest(28, 2)
      .setBiomeName("Birch Forest Hills")
      .setColor(2055986)
      .setHeight(height_LowHills);
   public static final BiomeGenBase roofedForest = new BiomeGenForest(29, 3).setColor(4215066).setBiomeName("Roofed Forest");
   public static final BiomeGenBase coldTaiga = new BiomeGenTaiga(30, 0)
      .setColor(3233098)
      .setBiomeName("Cold Taiga")
      .setFillerBlockMetadata(5159473)
      .setEnableSnow()
      .setTemperatureRainfall(-0.5F, 0.4F)
      .setHeight(height_MidPlains)
      .getBiomeGenBase();
   public static final BiomeGenBase coldTaigaHills = new BiomeGenTaiga(31, 0)
      .setColor(2375478)
      .setBiomeName("Cold Taiga Hills")
      .setFillerBlockMetadata(5159473)
      .setEnableSnow()
      .setTemperatureRainfall(-0.5F, 0.4F)
      .setHeight(height_LowHills)
      .getBiomeGenBase();
   public String biomeName;
   public static final BiomeGenBase megaTaiga = new BiomeGenTaiga(32, 1)
      .setColor(5858897)
      .setBiomeName("Mega Taiga")
      .setFillerBlockMetadata(5159473)
      .setTemperatureRainfall(0.3F, 0.8F)
      .setHeight(height_MidPlains);
   public static final BiomeGenBase megaTaigaHills = new BiomeGenTaiga(33, 1)
      .setColor(4542270)
      .setBiomeName("Mega Taiga Hills")
      .setFillerBlockMetadata(5159473)
      .setTemperatureRainfall(0.3F, 0.8F)
      .setHeight(height_LowHills);
   public static final BiomeGenBase extremeHillsPlus = new BiomeGenHills(34, true)
      .setColor(5271632)
      .setBiomeName("Extreme Hills+")
      .setHeight(height_MidHills)
      .setTemperatureRainfall(0.2F, 0.3F);
   public static final BiomeGenBase savanna = new BiomeGenSavanna(35)
      .setColor(12431967)
      .setBiomeName("Savanna")
      .setTemperatureRainfall(1.2F, 0.0F)
      .setDisableRain()
      .setHeight(height_LowPlains);
   protected WorldGenSwamp worldGeneratorSwamp = new WorldGenSwamp();
   public static final BiomeGenBase savannaPlateau = new BiomeGenSavanna(36)
      .setColor(10984804)
      .setBiomeName("Savanna Plateau")
      .setTemperatureRainfall(1.0F, 0.0F)
      .setDisableRain()
      .setHeight(height_HighPlateaus);
   public final int biomeID;
   public static final BiomeGenBase mesa = new BiomeGenMesa(37, false, false).setColor(14238997).setBiomeName("Mesa");
   public static final BiomeGenBase mesaPlateau_F = new BiomeGenMesa(38, false, true)
      .setColor(11573093)
      .setBiomeName("Mesa Plateau F")
      .setHeight(height_HighPlateaus);
   public static final BiomeGenBase mesaPlateau = new BiomeGenMesa(39, false, false)
      .setColor(13274213)
      .setBiomeName("Mesa Plateau")
      .setHeight(height_HighPlateaus);
   public static final BiomeGenBase DEFAULT_BIOME = ocean;
   protected static final NoiseGeneratorPerlin temperatureNoise;
   protected static final NoiseGeneratorPerlin GRASS_COLOR_NOISE;
   public BiomeDecorator theBiomeDecorator;
   protected static final WorldGenDoublePlant DOUBLE_PLANT_GENERATOR;

   protected BiomeGenBase setFillerBlockMetadata(int meta) {
      this.fillerBlockMetadata = meta;
      return this;
   }

   public WorldGenerator getRandomWorldGenForGrass(Random rand) {
      return new WorldGenTallGrass(net.minecraft.block.BlockTallGrass.EnumType.GRASS);
   }

   public final float getFloatTemperature(BlockPos pos) {
      if (pos.getY() > 64) {
         float f = (float)(temperatureNoise.getValue(pos.getX() * 1.0 / 8.0, pos.getZ() * 1.0 / 8.0) * 4.0);
         return this.temperature - (f + pos.getY() - 64.0F) * 0.05F / 30.0F;
      } else {
         return this.temperature;
      }
   }

   public boolean isSnowyBiome() {
      return this.enableSnow;
   }

   public static BiomeGenBase getBiomeFromBiomeList(int biomeId, BiomeGenBase biome) {
      if (biomeId >= 0 && biomeId <= biomeList.length) {
         BiomeGenBase biomeGenBase = biomeList[biomeId];
         return biomeGenBase == null ? biome : biomeGenBase;
      } else {
         logger.warn("Biome ID is out of bounds: " + biomeId + ", defaulting to 0 (Ocean)");
         return ocean;
      }
   }

   public List<BiomeGenBase.SpawnListEntry> getSpawnableList(EnumCreatureType creatureType) {
      switch (creatureType) {
         case MONSTER:
            return this.spawnableMonsterList;
         case CREATURE:
            return this.spawnableCreatureList;
         case WATER_CREATURE:
            return this.spawnableWaterCreatureList;
         case AMBIENT:
            return this.spawnableCaveCreatureList;
         default:
            return Collections.emptyList();
      }
   }

   protected BiomeGenBase setBiomeColor(int colorIn, boolean darkenMapColor) {
      this.color = colorIn;
      if (darkenMapColor) {
         this.mapColor = (colorIn & 16711422) >> 1;
      } else {
         this.mapColor = colorIn;
      }

      return this;
   }

   public void decorate(World worldIn, Random rand, BlockPos pos) {
      this.theBiomeDecorator.decorate(worldIn, rand, this, pos);
   }

   public int getSkyColorByTemp(float temperature) {
      temperature /= 3.0F;
      temperature = MathHelper.clamp_float(temperature, -1.0F, 1.0F);
      return MathHelper.hsvToRGB(0.62222224F - temperature * 0.05F, 0.5F + temperature * 0.1F, 1.0F);
   }

   protected BiomeGenBase createMutation() {
      return this.createMutatedBiome(this.biomeID + 128);
   }

   protected BiomeGenBase createMutatedBiome(int id) {
      return new BiomeGenMutated(id, this);
   }

   protected BiomeGenBase setColor(int colorIn) {
      this.setBiomeColor(colorIn, false);
      return this;
   }

   public static BiomeGenBase getBiome(int id) {
      return getBiomeFromBiomeList(id, (BiomeGenBase)null);
   }

   public boolean canRain() {
      return this.isSnowyBiome() ? false : this.enableRain;
   }

   public static BiomeGenBase[] getBiomeGenArray() {
      return biomeList;
   }

   public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
      this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
   }

   public final float getFloatRainfall() {
      return this.rainfall;
   }

   protected BiomeGenBase setBiomeName(String name) {
      this.biomeName = name;
      return this;
   }

   static {
      plains.createMutation();
      desert.createMutation();
      forest.createMutation();
      taiga.createMutation();
      swampland.createMutation();
      icePlains.createMutation();
      jungle.createMutation();
      jungleEdge.createMutation();
      coldTaiga.createMutation();
      savanna.createMutation();
      savannaPlateau.createMutation();
      mesa.createMutation();
      mesaPlateau_F.createMutation();
      mesaPlateau.createMutation();
      birchForest.createMutation();
      birchForestHills.createMutation();
      roofedForest.createMutation();
      megaTaiga.createMutation();
      extremeHills.createMutation();
      extremeHillsPlus.createMutation();
      megaTaiga.createMutatedBiome(megaTaigaHills.biomeID + 128).setBiomeName("Redwood Taiga Hills M");

      for (BiomeGenBase biomeGenBase : biomeList) {
         if (biomeGenBase != null) {
            if (BIOME_ID_MAP.containsKey(biomeGenBase.biomeName)) {
               throw new Error("Biome \"" + biomeGenBase.biomeName + "\" is defined as both ID " + BIOME_ID_MAP.get(biomeGenBase.biomeName).biomeID + " and " + biomeGenBase.biomeID);
            }

            BIOME_ID_MAP.put(biomeGenBase.biomeName, biomeGenBase);
            if (biomeGenBase.biomeID < 128) {
               explorationBiomesList.add(biomeGenBase);
            }
         }
      }

      explorationBiomesList.remove(hell);
      explorationBiomesList.remove(sky);
      explorationBiomesList.remove(frozenOcean);
      explorationBiomesList.remove(extremeHillsEdge);
      temperatureNoise = new NoiseGeneratorPerlin(new Random(1234L), 1);
      GRASS_COLOR_NOISE = new NoiseGeneratorPerlin(new Random(2345L), 1);
      DOUBLE_PLANT_GENERATOR = new WorldGenDoublePlant();
   }

   protected BiomeGenBase getBiomeGenBase() {
      this.mapColor = 16777215;
      return this;
   }

   public Class<? extends BiomeGenBase> getBiomeClass() {
      return (Class<? extends BiomeGenBase>)this.getClass();
   }

   protected BiomeGenBase setEnableSnow() {
      this.enableSnow = true;
      return this;
   }

   protected BiomeGenBase setDisableRain() {
      this.enableRain = false;
      return this;
   }

   public final void generateBiomeTerrain(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
      int seaLevel = worldIn.getSeaLevel();
      IBlockState blockState = this.topBlock;
      IBlockState blockState2 = this.fillerBlock;
      int i = -1;
      int i2 = (int)(noiseVal / 3.0 + 3.0 + rand.nextDouble() * 0.25);
      int i3 = x & 15;
      int i4 = z & 15;
      net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

      for (int innerIndex = 255; innerIndex >= 0; innerIndex--) {
         if (innerIndex <= rand.nextInt(5)) {
            chunkPrimerIn.setBlockState(i4, innerIndex, i3, Blocks.bedrock.getDefaultState());
         } else {
            IBlockState blockState3 = chunkPrimerIn.getBlockState(i4, innerIndex, i3);
            if (blockState3.getBlock().getMaterial() == Material.air) {
               i = -1;
            } else if (blockState3.getBlock() == Blocks.stone) {
               if (i == -1) {
                  if (i2 <= 0) {
                     blockState = null;
                     blockState2 = Blocks.stone.getDefaultState();
                  } else if (innerIndex >= seaLevel - 4 && innerIndex <= seaLevel + 1) {
                     blockState = this.topBlock;
                     blockState2 = this.fillerBlock;
                  }

                  if (innerIndex < seaLevel && (blockState == null || blockState.getBlock().getMaterial() == Material.air)) {
                     if (this.getFloatTemperature(mutableBlockPos.set(x, innerIndex, z)) < 0.15F) {
                        blockState = Blocks.ice.getDefaultState();
                     } else {
                        blockState = Blocks.water.getDefaultState();
                     }
                  }

                  i = i2;
                  if (innerIndex >= seaLevel - 1) {
                     chunkPrimerIn.setBlockState(i4, innerIndex, i3, blockState);
                  } else if (innerIndex < seaLevel - 7 - i2) {
                     blockState = null;
                     blockState2 = Blocks.stone.getDefaultState();
                     chunkPrimerIn.setBlockState(i4, innerIndex, i3, Blocks.gravel.getDefaultState());
                  } else {
                     chunkPrimerIn.setBlockState(i4, innerIndex, i3, blockState2);
                  }
               } else if (i > 0) {
                  i--;
                  chunkPrimerIn.setBlockState(i4, innerIndex, i3, blockState2);
                  if (i == 0 && blockState2.getBlock() == Blocks.sand) {
                     i = rand.nextInt(4) + Math.max(0, innerIndex - 63);
                     blockState2 = blockState2.getValue(BlockSand.VARIANT) == net.minecraft.block.BlockSand.EnumType.RED_SAND
                        ? Blocks.red_sandstone.getDefaultState()
                        : Blocks.sandstone.getDefaultState();
                  }
               }
            }
         }
      }
   }

   public WorldGenAbstractTree genBigTreeChance(Random rand) {
      return rand.nextInt(10) == 0 ? this.worldGeneratorBigTree : this.worldGeneratorTrees;
   }

   public int getFoliageColorAtPos(BlockPos pos) {
      double d = MathHelper.clamp_float(this.getFloatTemperature(pos), 0.0F, 1.0F);
      double d2 = MathHelper.clamp_float(this.getFloatRainfall(), 0.0F, 1.0F);
      return ColorizerFoliage.getFoliageColor(d, d2);
   }

   public boolean isHighHumidity() {
      return this.rainfall > 0.85F;
   }

   public int getGrassColorAtPos(BlockPos pos) {
      double d = MathHelper.clamp_float(this.getFloatTemperature(pos), 0.0F, 1.0F);
      double d2 = MathHelper.clamp_float(this.getFloatRainfall(), 0.0F, 1.0F);
      return ColorizerGrass.getGrassColor(d, d2);
   }

   protected BiomeGenBase setTemperatureRainfall(float temperatureIn, float rainfallIn) {
      if (temperatureIn > 0.1F && temperatureIn < 0.2F) {
         throw new IllegalArgumentException("Please avoid temperatures in the range 0.1 - 0.2 because of snow");
      }

      this.temperature = temperatureIn;
      this.rainfall = rainfallIn;
      return this;
   }

   protected BiomeDecorator createBiomeDecorator() {
      return new BiomeDecorator();
   }

   public boolean getEnableSnow() {
      return this.isSnowyBiome();
   }

   protected BiomeGenBase(int id) {
      this.biomeID = id;
      biomeList[id] = this;
      this.theBiomeDecorator = this.createBiomeDecorator();
      this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntitySheep.class, 12, 4, 4));
      this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityRabbit.class, 10, 3, 3));
      this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityPig.class, 10, 4, 4));
      this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityChicken.class, 10, 4, 4));
      this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityCow.class, 8, 4, 4));
      this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySpider.class, 100, 4, 4));
      this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityZombie.class, 100, 4, 4));
      this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySkeleton.class, 100, 4, 4));
      this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityCreeper.class, 100, 4, 4));
      this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySlime.class, 100, 4, 4));
      this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityEnderman.class, 10, 1, 4));
      this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityWitch.class, 5, 1, 1));
      this.spawnableWaterCreatureList.add(new BiomeGenBase.SpawnListEntry(EntitySquid.class, 10, 4, 4));
      this.spawnableCaveCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityBat.class, 10, 8, 8));
   }

   public float getSpawningChance() {
      return 0.1F;
   }

   public boolean isEqualTo(BiomeGenBase biome) {
      return biome == this ? true : (biome == null ? false : this.getBiomeClass() == biome.getBiomeClass());
   }

   public BiomeGenBase.TempCategory getTempCategory() {
      return this.temperature < 0.2
         ? BiomeGenBase.TempCategory.COLD
         : (this.temperature < 1.0 ? BiomeGenBase.TempCategory.MEDIUM : BiomeGenBase.TempCategory.WARM);
   }

   public net.minecraft.block.BlockFlower.EnumFlowerType pickRandomFlower(Random rand, BlockPos pos) {
      return rand.nextInt(3) > 0 ? net.minecraft.block.BlockFlower.EnumFlowerType.DANDELION : net.minecraft.block.BlockFlower.EnumFlowerType.POPPY;
   }

   public final int getIntRainfall() {
      return (int)(this.rainfall * 65536.0F);
   }

   protected final BiomeGenBase setHeight(BiomeGenBase.Height heights) {
      this.minHeight = heights.rootHeight;
      this.maxHeight = heights.variation;
      return this;
   }

   public static class Height {
      public float rootHeight;
      public float variation;

      public BiomeGenBase.Height attenuate() {
         return new BiomeGenBase.Height(this.rootHeight * 0.8F, this.variation * 0.6F);
      }

      public Height(float rootHeightIn, float variationIn) {
         this.rootHeight = rootHeightIn;
         this.variation = variationIn;
      }
   }

   public static class SpawnListEntry extends Item {
      public Class<? extends EntityLiving> entityClass;
      public int minGroupCount;
      public int maxGroupCount;

      public SpawnListEntry(Class<? extends EntityLiving> entityclassIn, int weight, int groupCountMin, int groupCountMax) {
         super(weight);
         this.entityClass = entityclassIn;
         this.minGroupCount = groupCountMin;
         this.maxGroupCount = groupCountMax;
      }

      @Override
      public String toString() {
         return this.entityClass.getSimpleName() + "*(" + this.minGroupCount + "-" + this.maxGroupCount + "):" + this.itemWeight;
      }
   }

   public enum TempCategory {
      OCEAN,
      COLD,
      MEDIUM,
      WARM;
   }
}
