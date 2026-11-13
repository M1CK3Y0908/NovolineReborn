package net.optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockStem;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.config.ConnectedParser;
import net.optifine.config.MatchBlock;
import net.optifine.reflect.Reflector;
import net.optifine.render.RenderEnv;
import net.optifine.util.EntityUtils;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;
import net.optifine.util.StrUtils;
import net.optifine.util.TextureUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class CustomColors {
   private static String paletteFormatDefault = "vanilla";
   private static CustomColormap waterColors = null;
   private static CustomColormap foliagePineColors = null;
   private static CustomColormap foliageBirchColors = null;
   private static CustomColormap swampFoliageColors = null;
   private static CustomColormap swampGrassColors = null;
   private static CustomColormap[] colorsBlockColormaps = null;
   private static CustomColormap[][] blockColormaps = (CustomColormap[][])null;
   private static CustomColormap skyColors = null;
   private static CustomColorFader skyColorFader = new CustomColorFader();
   private static CustomColormap fogColors = null;
   private static CustomColorFader fogColorFader = new CustomColorFader();
   private static CustomColormap underwaterColors = null;
   private static CustomColorFader underwaterColorFader = new CustomColorFader();
   private static CustomColormap underlavaColors = null;
   private static CustomColorFader underlavaColorFader = new CustomColorFader();
   private static LightMapPack[] lightMapPacks = null;
   private static int lightmapMinDimensionId = 0;
   private static CustomColormap redstoneColors = null;
   private static CustomColormap xpOrbColors = null;
   private static int xpOrbTime = -1;
   private static CustomColormap durabilityColors = null;
   private static CustomColormap stemColors = null;
   private static CustomColormap stemMelonColors = null;
   private static CustomColormap stemPumpkinColors = null;
   private static CustomColormap myceliumParticleColors = null;
   private static boolean useDefaultGrassFoliageColors = true;
   private static int particleWaterColor = -1;
   private static int particlePortalColor = -1;
   private static int lilyPadColor = -1;
   private static int expBarTextColor = -1;
   private static int bossTextColor = -1;
   private static int signTextColor = -1;
   private static Vec3 fogColorNether = null;
   private static Vec3 fogColorEnd = null;
   private static Vec3 skyColorEnd = null;
   private static int[] spawnEggPrimaryColors = null;
   private static int[] spawnEggSecondaryColors = null;
   private static float[][] wolfCollarColors = (float[][])null;
   private static float[][] sheepColors = (float[][])null;
   private static int[] textColors = null;
   private static int[] mapColorsOriginal = null;
   private static int[] potionColors = null;
   private static final IBlockState BLOCK_STATE_DIRT = Blocks.dirt.getDefaultState();
   private static final IBlockState BLOCK_STATE_WATER = Blocks.water.getDefaultState();
   public static Random random = new Random();
   private static final CustomColors.IColorizer COLORIZER_GRASS = new CustomColors.IColorizer() {
      @Override
      public boolean isColorConstant() {
         return false;
      }

      @Override
      public int getColor(IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos) {
         BiomeGenBase biomeGenBase = CustomColors.getColorBiome(blockAccess, blockPos);
         return CustomColors.swampGrassColors != null && biomeGenBase == BiomeGenBase.swampland
            ? CustomColors.swampGrassColors.getColor(biomeGenBase, blockPos)
            : biomeGenBase.getGrassColorAtPos(blockPos);
      }
   };
   private static final CustomColors.IColorizer COLORIZER_FOLIAGE = new CustomColors.IColorizer() {
      @Override
      public int getColor(IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos) {
         BiomeGenBase biomeGenBase = CustomColors.getColorBiome(blockAccess, blockPos);
         return CustomColors.swampFoliageColors != null && biomeGenBase == BiomeGenBase.swampland
            ? CustomColors.swampFoliageColors.getColor(biomeGenBase, blockPos)
            : biomeGenBase.getFoliageColorAtPos(blockPos);
      }

      @Override
      public boolean isColorConstant() {
         return false;
      }
   };
   private static final CustomColors.IColorizer COLORIZER_FOLIAGE_PINE = new CustomColors.IColorizer() {
      @Override
      public boolean isColorConstant() {
         return CustomColors.foliagePineColors == null;
      }

      @Override
      public int getColor(IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos) {
         return CustomColors.foliagePineColors != null
            ? CustomColors.foliagePineColors.getColor(blockAccess, blockPos)
            : ColorizerFoliage.getFoliageColorPine();
      }
   };
   private static final CustomColors.IColorizer COLORIZER_FOLIAGE_BIRCH = new CustomColors.IColorizer() {
      @Override
      public int getColor(IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos) {
         return CustomColors.foliageBirchColors != null
            ? CustomColors.foliageBirchColors.getColor(blockAccess, blockPos)
            : ColorizerFoliage.getFoliageColorBirch();
      }

      @Override
      public boolean isColorConstant() {
         return CustomColors.foliageBirchColors == null;
      }
   };
   private static final CustomColors.IColorizer COLORIZER_WATER = new CustomColors.IColorizer() {
      @Override
      public int getColor(IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos) {
         BiomeGenBase biomeGenBase = CustomColors.getColorBiome(blockAccess, blockPos);
         return CustomColors.waterColors != null
            ? CustomColors.waterColors.getColor(biomeGenBase, blockPos)
            : (Reflector.reflectorMethod18.isActive() ? Reflector.createInt3(biomeGenBase, Reflector.reflectorMethod18) : biomeGenBase.waterColorMultiplier);
      }

      @Override
      public boolean isColorConstant() {
         return false;
      }
   };

   private static void addToList(CustomColormap cm, List lists, int id) {
      while (id >= lists.size()) {
         lists.add(null);
      }

      List list = (List)lists.get(id);
      if (list == null) {
         list = new ArrayList();
         lists.set(id, list);
      }

      list.add(cm);
   }

   public static int getFluidColor(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, RenderEnv renderEnv) {
      Block block = blockState.getBlock();
      CustomColors.IColorizer customcolors$icolorizer = getBlockColormap(blockState);
      if (customcolors$icolorizer == null && blockState.getBlock().getMaterial() == Material.water) {
         customcolors$icolorizer = COLORIZER_WATER;
      }

      return customcolors$icolorizer == null
         ? block.colorMultiplier(blockAccess, blockPos, 0)
         : (
            Config.isSmoothBiomes() && !customcolors$icolorizer.isColorConstant()
               ? getSmoothColorMultiplier(blockState, blockAccess, blockPos, customcolors$icolorizer, renderEnv.getColorizerBlockPosM())
               : customcolors$icolorizer.getColor(blockState, blockAccess, blockPos)
         );
   }

   private static Vec3 getFogColorEnd(Vec3 col) {
      return fogColorEnd == null ? col : fogColorEnd;
   }

   private static int getMapColorIndex(String name) {
      return name == null
         ? -1
         : (
            name.equals("air")
               ? MapColor.airColor.colorIndex
               : (
                  name.equals("grass")
                     ? MapColor.grassColor.colorIndex
                     : (
                        name.equals("sand")
                           ? MapColor.sandColor.colorIndex
                           : (
                              name.equals("cloth")
                                 ? MapColor.clothColor.colorIndex
                                 : (
                                    name.equals("tnt")
                                       ? MapColor.tntColor.colorIndex
                                       : (
                                          name.equals("ice")
                                             ? MapColor.iceColor.colorIndex
                                             : (
                                                name.equals("iron")
                                                   ? MapColor.ironColor.colorIndex
                                                   : (
                                                      name.equals("foliage")
                                                         ? MapColor.foliageColor.colorIndex
                                                         : (
                                                            name.equals("clay")
                                                               ? MapColor.clayColor.colorIndex
                                                               : (
                                                                  name.equals("dirt")
                                                                     ? MapColor.dirtColor.colorIndex
                                                                     : (
                                                                        name.equals("stone")
                                                                           ? MapColor.stoneColor.colorIndex
                                                                           : (
                                                                              name.equals("water")
                                                                                 ? MapColor.waterColor.colorIndex
                                                                                 : (
                                                                                    name.equals("wood")
                                                                                       ? MapColor.woodColor.colorIndex
                                                                                       : (
                                                                                          name.equals("quartz")
                                                                                             ? MapColor.quartzColor.colorIndex
                                                                                             : (
                                                                                                name.equals("gold")
                                                                                                   ? MapColor.goldColor.colorIndex
                                                                                                   : (
                                                                                                      name.equals("diamond")
                                                                                                         ? MapColor.diamondColor.colorIndex
                                                                                                         : (
                                                                                                            name.equals("lapis")
                                                                                                               ? MapColor.lapisColor.colorIndex
                                                                                                               : (
                                                                                                                  name.equals("emerald")
                                                                                                                     ? MapColor.emeraldColor.colorIndex
                                                                                                                     : (
                                                                                                                        name.equals("podzol")
                                                                                                                           ? MapColor.obsidianColor.colorIndex
                                                                                                                           : (
                                                                                                                              name.equals("netherrack")
                                                                                                                                 ? MapColor.netherrackColor.colorIndex
                                                                                                                                 : (
                                                                                                                                    name.equals("snow")
                                                                                                                                          || name.equals(
                                                                                                                                             "white"
                                                                                                                                          )
                                                                                                                                       ? MapColor.snowColor.colorIndex
                                                                                                                                       : (
                                                                                                                                          name.equals("adobe")
                                                                                                                                                || name.equals(
                                                                                                                                                   "orange"
                                                                                                                                                )
                                                                                                                                             ? MapColor.adobeColor
                                                                                                                                                .colorIndex
                                                                                                                                             : (
                                                                                                                                                name.equals(
                                                                                                                                                      "magenta"
                                                                                                                                                   )
                                                                                                                                                   ? MapColor.magentaColor
                                                                                                                                                      .colorIndex
                                                                                                                                                   : (
                                                                                                                                                      !name.equals(
                                                                                                                                                               "light_blue"
                                                                                                                                                            )
                                                                                                                                                            && !name.equals(
                                                                                                                                                               "lightBlue"
                                                                                                                                                            )
                                                                                                                                                         ? (
                                                                                                                                                            name.equals(
                                                                                                                                                                  "yellow"
                                                                                                                                                               )
                                                                                                                                                               ? MapColor.yellowColor
                                                                                                                                                                  .colorIndex
                                                                                                                                                               : (
                                                                                                                                                                  name.equals(
                                                                                                                                                                        "lime"
                                                                                                                                                                     )
                                                                                                                                                                     ? MapColor.limeColor
                                                                                                                                                                        .colorIndex
                                                                                                                                                                     : (
                                                                                                                                                                        name.equals(
                                                                                                                                                                              "pink"
                                                                                                                                                                           )
                                                                                                                                                                           ? MapColor.pinkColor
                                                                                                                                                                              .colorIndex
                                                                                                                                                                           : (
                                                                                                                                                                              name.equals(
                                                                                                                                                                                    "gray"
                                                                                                                                                                                 )
                                                                                                                                                                                 ? MapColor.grayColor
                                                                                                                                                                                    .colorIndex
                                                                                                                                                                                 : (
                                                                                                                                                                                    name.equals(
                                                                                                                                                                                          "silver"
                                                                                                                                                                                       )
                                                                                                                                                                                       ? MapColor.silverColor
                                                                                                                                                                                          .colorIndex
                                                                                                                                                                                       : (
                                                                                                                                                                                          name.equals(
                                                                                                                                                                                                "cyan"
                                                                                                                                                                                             )
                                                                                                                                                                                             ? MapColor.cyanColor
                                                                                                                                                                                                .colorIndex
                                                                                                                                                                                             : (
                                                                                                                                                                                                name.equals(
                                                                                                                                                                                                      "purple"
                                                                                                                                                                                                   )
                                                                                                                                                                                                   ? MapColor.purpleColor
                                                                                                                                                                                                      .colorIndex
                                                                                                                                                                                                   : (
                                                                                                                                                                                                      name.equals(
                                                                                                                                                                                                            "blue"
                                                                                                                                                                                                         )
                                                                                                                                                                                                         ? MapColor.blueColor
                                                                                                                                                                                                            .colorIndex
                                                                                                                                                                                                         : (
                                                                                                                                                                                                            name.equals(
                                                                                                                                                                                                                  "brown"
                                                                                                                                                                                                               )
                                                                                                                                                                                                               ? MapColor.brownColor
                                                                                                                                                                                                                  .colorIndex
                                                                                                                                                                                                               : (
                                                                                                                                                                                                                  name.equals(
                                                                                                                                                                                                                        "green"
                                                                                                                                                                                                                     )
                                                                                                                                                                                                                     ? MapColor.greenColor
                                                                                                                                                                                                                        .colorIndex
                                                                                                                                                                                                                     : (
                                                                                                                                                                                                                        name.equals(
                                                                                                                                                                                                                              "red"
                                                                                                                                                                                                                           )
                                                                                                                                                                                                                           ? MapColor.redColor
                                                                                                                                                                                                                              .colorIndex
                                                                                                                                                                                                                           : (
                                                                                                                                                                                                                              name.equals(
                                                                                                                                                                                                                                    "black"
                                                                                                                                                                                                                                 )
                                                                                                                                                                                                                                 ? MapColor.blackColor
                                                                                                                                                                                                                                    .colorIndex
                                                                                                                                                                                                                                 : -1
                                                                                                                                                                                                                           )
                                                                                                                                                                                                                     )
                                                                                                                                                                                                               )
                                                                                                                                                                                                         )
                                                                                                                                                                                                   )
                                                                                                                                                                                             )
                                                                                                                                                                                       )
                                                                                                                                                                                 )
                                                                                                                                                                           )
                                                                                                                                                                     )
                                                                                                                                                               )
                                                                                                                                                         )
                                                                                                                                                         : MapColor.lightBlueColor
                                                                                                                                                            .colorIndex
                                                                                                                                                   )
                                                                                                                                             )
                                                                                                                                       )
                                                                                                                                 )
                                                                                                                           )
                                                                                                                     )
                                                                                                               )
                                                                                                         )
                                                                                                   )
                                                                                             )
                                                                                       )
                                                                                 )
                                                                           )
                                                                     )
                                                               )
                                                         )
                                                   )
                                             )
                                       )
                                 )
                           )
                     )
               )
         );
   }

   public static void updateUseDefaultGrassFoliageColors() {
      useDefaultGrassFoliageColors = foliageBirchColors == null
         && foliagePineColors == null
         && swampGrassColors == null
         && swampFoliageColors == null
         && Config.isSwampColors()
         && Config.isSmoothBiomes();
   }

   private static int createInt(IBlockState iBlockState) {
      if (!(iBlockState.getBlock() instanceof BlockRedstoneWire)) {
         return 15;
      }

      Comparable value2 = iBlockState.getValue(BlockRedstoneWire.POWER);
      return !(value2 instanceof Integer) ? 15 : (Integer)value2;
   }

   private static int getPotionId(String name) {
      if (name.equals("potion.water")) {
         return 0;
      }

      Potion[] apotion = Potion.potionTypes;

      for (int index = 0; index < apotion.length; index++) {
         Potion potion2 = apotion[index];
         if (potion2 != null && potion2.getName().equals(name)) {
            return potion2.getId();
         }
      }

      return -1;
   }

   public static void updateMyceliumFX(EntityFX fx) {
      if (myceliumParticleColors != null) {
         int colorRandom = myceliumParticleColors.getColorRandom();
         int i = colorRandom >> 16 & 0xFF;
         int i2 = colorRandom >> 8 & 0xFF;
         int i3 = colorRandom & 0xFF;
         float f = i / 255.0F;
         float f2 = i2 / 255.0F;
         float f3 = i3 / 255.0F;
         fx.setRBGColorF(f, f2, f3);
      }
   }

   private static int getStemColorMultiplier(Block blockStem, IBlockAccess blockAccess, BlockPos blockPos, RenderEnv renderEnv) {
      CustomColormap customColormap = stemColors;
      if (blockStem == Blocks.pumpkin_stem && stemPumpkinColors != null) {
         customColormap = stemPumpkinColors;
      }

      if (blockStem == Blocks.melon_stem && stemMelonColors != null) {
         customColormap = stemMelonColors;
      }

      if (customColormap == null) {
         return -1;
      }

      int metadata = renderEnv.getMetadata();
      return customColormap.getColor(metadata);
   }

   public static int getDurabilityColor(int dur255) {
      if (durabilityColors == null) {
         return -1;
      }

      int i = dur255 * durabilityColors.getLength() / 255;
      return durabilityColors.getColor(i);
   }

   public static boolean updateLightmap(World world, float torchFlickerX, int[] lmColors, boolean nightvision, float partialTicks) {
      if (world == null) {
         return false;
      } else if (lightMapPacks == null) {
         return false;
      } else {
         int i = world.provider.getDimensionId() - lightmapMinDimensionId;
         if (i >= 0 && i < lightMapPacks.length) {
            LightMapPack lightMapPack = lightMapPacks[i];
            return lightMapPack == null ? false : lightMapPack.updateLightmap(world, torchFlickerX, lmColors, nightvision, partialTicks);
         } else {
            return false;
         }
      }
   }

   public static Vec3 getUnderlavaColor(IBlockAccess blockAccess, double x, double y, double z) {
      return getUnderFluidColor(blockAccess, x, y, z, underlavaColors, underlavaColorFader);
   }

   private static Pair<LightMapPack[], Integer> parseLightMapPacks() {
      String[] texts = ResUtils.collectFiles("mcpatcher/lightmap/world", ".png");
      HashMap hashMap = new HashMap();

      for (int i = 0; i < texts.length; i++) {
         String text5 = texts[i];
         String text6 = StrUtils.removePrefixSuffix(text5, "mcpatcher/lightmap/world", ".png");
         int i2 = Config.parseInt(text6, Integer.MIN_VALUE);
         if (i2 == Integer.MIN_VALUE) {
            warn("Invalid dimension ID: " + text6 + ", path: " + text5);
         } else {
            hashMap.put(i2, text5);
         }
      }

      Set set2 = hashMap.keySet();
      Integer[] integers = (Integer[]) set2.toArray(new Integer[set2.size()]);
      Arrays.sort(integers);
      if (integers.length <= 0) {
         return new ImmutablePair<>(null, 0);
      }

      int i3 = integers[0];
      int i4 = integers[integers.length - 1];
      CustomColormap[] customColormaps = new CustomColormap[i4 - i3 + 1];

      for (int i5 = 0; i5 < integers.length; i5++) {
         Integer integer = integers[i5];
         String text7 = (String)hashMap.get(integer);
         CustomColormap customColors = getCustomColors(text7, -1, -1);
         if (customColors != null) {
            if (customColors.getWidth() < 16) {
               warn("Invalid lightmap width: " + customColors.getWidth() + ", path: " + text7);
            } else {
               int i6 = integer - i3;
               customColormaps[i6] = customColors;
            }
         }
      }

      LightMapPack[] lightMapPacks2 = new LightMapPack[customColormaps.length];

      for (int i7 = 0; i7 < customColormaps.length; i7++) {
         CustomColormap customColormap2 = customColormaps[i7];
         if (customColormap2 != null) {
            String name2 = customColormap2.name;
            String basePath2 = customColormap2.basePath;
            CustomColormap customColors2 = getCustomColors(basePath2 + "/" + name2 + "_rain.png", -1, -1);
            CustomColormap customColors3 = getCustomColors(basePath2 + "/" + name2 + "_thunder.png", -1, -1);
            LightMap lightMap = new LightMap(customColormap2);
            LightMap lightMap2 = customColors2 != null ? new LightMap(customColors2) : null;
            LightMap lightMap3 = customColors3 != null ? new LightMap(customColors3) : null;
            LightMapPack lightMapPack = new LightMapPack(lightMap, lightMap2, lightMap3);
            lightMapPacks2[i7] = lightMapPack;
         }
      }

      return new ImmutablePair<>(lightMapPacks2, i3);
   }

   public static void updatePortalFX(EntityFX fx) {
      if (particlePortalColor >= 0) {
         int particlePortalColor2 = particlePortalColor;
         int i = particlePortalColor2 >> 16 & 0xFF;
         int i2 = particlePortalColor2 >> 8 & 0xFF;
         int i3 = particlePortalColor2 & 0xFF;
         float f = i / 255.0F;
         float f2 = i2 / 255.0F;
         float f3 = i3 / 255.0F;
         fx.setRBGColorF(f, f2, f3);
      }
   }

   private static float[][] readDyeColors(Properties props, String fileName, String prefix, String logName) {
      EnumDyeColor[] aenumdyecolor = EnumDyeColor.values();
      HashMap hashMap = new HashMap();

      for (int index = 0; index < aenumdyecolor.length; index++) {
         EnumDyeColor enumDyeColor = aenumdyecolor[index];
         hashMap.put(enumDyeColor.getName(), enumDyeColor);
      }

      float[][] floatArray1 = new float[aenumdyecolor.length][];
      int i = 0;

      for (String text : (Iterable<String>) (Iterable<String>) (Set) (props.keySet())) {
         String propertyValue = props.getProperty(text);
         if (text.startsWith(prefix)) {
            String text2 = StrUtils.removePrefix(text, prefix);
            if (text2.equals("lightBlue")) {
               text2 = "light_blue";
            }

            EnumDyeColor dyeColor2 = (EnumDyeColor)hashMap.get(text2);
            int i2 = parseColor(propertyValue);
            if (dyeColor2 != null && i2 >= 0) {
               float[] fArray = new float[]{(i2 >> 16 & 0xFF) / 255.0F, (i2 >> 8 & 0xFF) / 255.0F, (i2 & 0xFF) / 255.0F};
               floatArray1[dyeColor2.ordinal()] = fArray;
               i++;
            } else {
               warn("Invalid color: " + text + " = " + propertyValue);
            }
         }
      }

      if (i <= 0) {
         return (float[][])null;
      }

      dbg(logName + " colors: " + i);
      return floatArray1;
   }

   private static Vec3 getFogColorNether(Vec3 col) {
      return fogColorNether == null ? col : fogColorNether;
   }

   public static CustomColormap getCustomColors(String pathImage, int width, int height) {
      try {
         if (!Config.hasResource(new ResourceLocation(pathImage))) {
            return null;
         }

         dbg("Colormap " + pathImage);
         PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
         String text5 = StrUtils.replaceSuffix(pathImage, ".png", ".properties");
         ResourceLocation resourceLocation = new ResourceLocation(text5);
         if (Config.hasResource(resourceLocation)) {
            InputStream resourceStream = Config.getResourceStream(resourceLocation);
            propertiesOrdered.load(resourceStream);
            resourceStream.close();
            dbg("Colormap properties: " + text5);
         } else {
            propertiesOrdered.put("format", paletteFormatDefault);
            propertiesOrdered.put("source", pathImage);
            text5 = pathImage;
         }

         CustomColormap customColormap2 = new CustomColormap(propertiesOrdered, text5, width, height, paletteFormatDefault);
         return !customColormap2.isValid(text5) ? null : customColormap2;
      } catch (Exception exception) {
         exception.printStackTrace();
         return null;
      }
   }

   private static void setMapColors(int[] colors) {
      if (colors != null) {
         MapColor[] amapcolor = MapColor.mapColorArray;
         boolean conditionMet = false;

         for (int index = 0; index < amapcolor.length && index < colors.length; index++) {
            MapColor mapColor = amapcolor[index];
            if (mapColor != null) {
               int i = colors[index];
               if (i >= 0 && mapColor.colorValue != i) {
                  mapColor.colorValue = i;
                  conditionMet = true;
               }
            }
         }

         if (conditionMet) {
            Minecraft.getMinecraft().getTextureManager().reloadBannerTextures();
         }
      }
   }

   private static CustomColormap[][] createArray(String[] texts, CustomColormap[] customColormaps) {
      String[] texts2 = ResUtils.collectFiles(texts, new String[]{".properties"});
      Arrays.sort(texts2);
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < texts2.length; i++) {
         String text5 = texts2[i];
         dbg("Block colormap: " + text5);

         try {
            InputStream resourceStream = Config.getResourceStream(new ResourceLocation("minecraft", text5));
            if (resourceStream == null) {
               warn("File not found: " + text5);
            } else {
               PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
               propertiesOrdered.load(resourceStream);
               resourceStream.close();
               CustomColormap customColormap2 = new CustomColormap(propertiesOrdered, text5, 256, 256, paletteFormatDefault);
               if (customColormap2.isValid(text5) && customColormap2.isValidMatchBlocks(text5)) {
                  addToBlockList(customColormap2, arrayList);
               }
            }
         } catch (FileNotFoundException fileNotFoundException) {
            warn("File not found: " + text5);
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }

      if (customColormaps != null) {
         for (int i2 = 0; i2 < customColormaps.length; i2++) {
            CustomColormap customColormap3 = customColormaps[i2];
            addToBlockList(customColormap3, arrayList);
         }
      }

      return arrayList.size() <= 0 ? (CustomColormap[][])null : blockListToArray(arrayList);
   }

   private static void addToBlockList(CustomColormap cm, List blockList) {
      int[] intArray = cm.getMatchBlockIds();
      if (intArray != null && intArray.length > 0) {
         for (int index = 0; index < intArray.length; index++) {
            int i = intArray[index];
            if (i < 0) {
               warn("Invalid block ID: " + i);
            } else {
               addToList(cm, blockList, i);
            }
         }
      } else {
         warn("No match blocks: " + Config.arrayToString(intArray));
      }
   }

   private static Vec3 getSkyColorEnd(Vec3 col) {
      return skyColorEnd == null ? col : skyColorEnd;
   }

   public static Vec3 getSkyColor(Vec3 skyColor3d, IBlockAccess blockAccess, double x, double y, double z) {
      if (skyColors == null) {
         return skyColor3d;
      }

      int intValue = skyColors.getInt(blockAccess, x, y, z);
      int i = intValue >> 16 & 0xFF;
      int i2 = intValue >> 8 & 0xFF;
      int i3 = intValue & 0xFF;
      float f = i / 255.0F;
      float f2 = i2 / 255.0F;
      float f3 = i3 / 255.0F;
      float f4 = (float)skyColor3d.xCoord / 0.5F;
      float f5 = (float)skyColor3d.yCoord / 0.66275F;
      float f6 = (float)skyColor3d.zCoord;
      f *= f4;
      f2 *= f5;
      f3 *= f6;
      return skyColorFader.getColor(f, f2, f3);
   }

   private static String getValidProperty(String fileName, String key, String[] validValues, String valDef) {
      try {
         InputStream resourceStream = Config.getResourceStream(new ResourceLocation(fileName));
         if (resourceStream == null) {
            return valDef;
         } else {
            PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
            propertiesOrdered.load(resourceStream);
            resourceStream.close();
            String property = propertiesOrdered.getProperty(key);
            if (property == null) {
               return valDef;
            } else if (!Arrays.asList(validValues).contains(property)) {
               warn("Invalid value: " + key + "=" + property);
               warn("Expected values: " + Config.arrayToString(validValues));
               return valDef;
            } else {
               dbg(key + "=" + property);
               return property;
            }
         }
      } catch (FileNotFoundException fileNotFoundException) {
         return valDef;
      } catch (IOException ioException) {
         ioException.printStackTrace();
         return valDef;
      }
   }

   protected static BiomeGenBase getColorBiome(IBlockAccess blockAccess, BlockPos blockPos) {
      BiomeGenBase biomeGenBase = blockAccess.getBiomeGenForCoords(blockPos);
      if (biomeGenBase == BiomeGenBase.swampland && !Config.isSwampColors()) {
         biomeGenBase = BiomeGenBase.plains;
      }

      return biomeGenBase;
   }

   private static void warn(String str) {
      Config.warn("CustomColors: " + str);
   }

   private static CustomColormap getCustomColors(String basePath, String[] paths, int width, int height) {
      for (int i = 0; i < paths.length; i++) {
         String text5 = paths[i];
         text5 = basePath + text5;
         CustomColormap customColors = getCustomColors(text5, width, height);
         if (customColors != null) {
            return customColors;
         }
      }

      return null;
   }

   private static int readColor(Properties props, String name) {
      String property = props.getProperty(name);
      if (property == null) {
         return -1;
      } else {
         property = property.trim();
         int i = parseColor(property);
         if (i < 0) {
            warn("Invalid color: " + name + " = " + property);
            return i;
         } else {
            dbg(name + " = " + property);
            return i;
         }
      }
   }

   private static int parseColor(String str) {
      if (str == null) {
         return -1;
      }

      str = str.trim();

      try {
         return Integer.parseInt(str, 16) & 16777215;
      } catch (NumberFormatException numberFormatException) {
         return -1;
      }
   }

   public static Vec3 getUnderFluidColor(
      IBlockAccess blockAccess, double x, double y, double z, CustomColormap underFluidColors, CustomColorFader underFluidColorFader
   ) {
      if (underFluidColors == null) {
         return null;
      }

      int intValue = underFluidColors.getInt(blockAccess, x, y, z);
      int i = intValue >> 16 & 0xFF;
      int i2 = intValue >> 8 & 0xFF;
      int i3 = intValue & 0xFF;
      float f = i / 255.0F;
      float f2 = i2 / 255.0F;
      float f3 = i3 / 255.0F;
      return underFluidColorFader.getColor(f, f2, f3);
   }

   public static int getXpOrbColor(float timer) {
      if (xpOrbColors == null) {
         return -1;
      }

      int i = (int)Math.round((MathHelper.sin(timer) + 1.0F) * (xpOrbColors.getLength() - 1) / 2.0);
      return xpOrbColors.getColor(i);
   }

   public static float[] getWolfCollarColors(EnumDyeColor dye, float[] colors) {
      return getDyeColors(dye, wolfCollarColors, colors);
   }

   public static int getInt2() {
      return expBarTextColor < 0 ? 8453920 : expBarTextColor;
   }

   private static int[] readMapColors(Properties props, String fileName, String prefix, String logName) {
      int[] iArray = new int[MapColor.mapColorArray.length];
      Arrays.fill(iArray, -1);
      int i = 0;

      for (String text5 : (Iterable<String>) (Iterable<String>) (Set) (props.keySet())) {
         String property = props.getProperty(text5);
         if (text5.startsWith(prefix)) {
            int mapColorIndex = getMapColorIndex(StrUtils.removePrefix(text5, prefix));
            int i2 = parseColor(property);
            if (mapColorIndex >= 0 && mapColorIndex < iArray.length && i2 >= 0) {
               iArray[mapColorIndex] = i2;
               i++;
            } else {
               warn("Invalid color: " + text5 + " = " + property);
            }
         }
      }

      if (i <= 0) {
         return null;
      }

      dbg(logName + " colors: " + i);
      return iArray;
   }

   public static float getXpOrbTimer(float timer) {
      if (xpOrbTime <= 0) {
         return timer;
      }

      float f = 628.0F / xpOrbTime;
      return timer * f;
   }

   private static int getSmoothColorMultiplier(
      IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos, CustomColors.IColorizer colorizer, BlockPosM blockPosM
   ) {
      int i = 0;
      int i2 = 0;
      int i3 = 0;
      int xCoordinate = blockPos.getX();
      int yCoordinate = blockPos.getY();
      int zCoordinate = blockPos.getZ();
      BlockPosM blockPosM2 = blockPosM;

      for (int nestedIndex = xCoordinate - 1; nestedIndex <= xCoordinate + 1; nestedIndex++) {
         for (int outerIndex = zCoordinate - 1; outerIndex <= zCoordinate + 1; outerIndex++) {
            blockPosM2.setXyz(nestedIndex, yCoordinate, outerIndex);
            int color2 = colorizer.getColor(blockState, blockAccess, blockPosM2);
            i += color2 >> 16 & 0xFF;
            i2 += color2 >> 8 & 0xFF;
            i3 += color2 & 0xFF;
         }
      }

      int i4 = i / 9;
      int i5 = i2 / 9;
      int i6 = i3 / 9;
      return i4 << 16 | i5 << 8 | i6;
   }

   private static void dbg(String str) {
      Config.dbg("CustomColors: " + str);
   }

   private static int getSpawnEggColor(ItemMonsterPlacer item, ItemStack itemStack, int layer, int color) {
      int metadata = itemStack.getMetadata();
      int[] intArray = layer == 0 ? spawnEggPrimaryColors : spawnEggSecondaryColors;
      if (intArray == null) {
         return color;
      } else if (metadata >= 0 && metadata < intArray.length) {
         int i = intArray[metadata];
         return i < 0 ? color : i;
      } else {
         return color;
      }
   }

   public static int getColorMultiplier(BakedQuad quad, IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos, RenderEnv renderEnv) {
      Block block2 = blockState.getBlock();
      IBlockState blockState2 = renderEnv.getBlockState();
      if (blockColormaps != null) {
         if (!quad.hasTintIndex()) {
            if (block2 == Blocks.grass) {
               blockState2 = BLOCK_STATE_DIRT;
            }

            if (block2 == Blocks.redstone_wire) {
               return -1;
            }
         }

         if (block2 == Blocks.double_plant && renderEnv.getMetadata() >= 8) {
            blockPos = blockPos.down();
            blockState2 = blockAccess.getBlockState(blockPos);
         }

         CustomColormap blockColormap = getBlockColormap(blockState2);
         if (blockColormap != null) {
            if (Config.isSmoothBiomes() && !blockColormap.isColorConstant()) {
               return getSmoothColorMultiplier(blockState, blockAccess, blockPos, blockColormap, renderEnv.getColorizerBlockPosM());
            }

            return blockColormap.getColor(blockAccess, blockPos);
         }
      }

      if (!quad.hasTintIndex()) {
         return -1;
      }

      if (block2 == Blocks.waterlily) {
         return getLilypadColorMultiplier(blockAccess, blockPos);
      }

      if (block2 == Blocks.redstone_wire) {
         return getRedstoneColor(renderEnv.getBlockState());
      }

      if (block2 instanceof BlockStem) {
         return getStemColorMultiplier(block2, blockAccess, blockPos, renderEnv);
      }

      if (useDefaultGrassFoliageColors) {
         return -1;
      }

      int metadata = renderEnv.getMetadata();
      CustomColors.IColorizer iColorizer;
      if (block2 == Blocks.grass || block2 == Blocks.tallgrass || block2 == Blocks.double_plant) {
         iColorizer = COLORIZER_GRASS;
      } else if (block2 == Blocks.double_plant) {
         iColorizer = COLORIZER_GRASS;
         if (metadata >= 8) {
            blockPos = blockPos.down();
         }
      } else if (block2 == Blocks.leaves) {
         switch (metadata & 3) {
            case 0:
               iColorizer = COLORIZER_FOLIAGE;
               break;
            case 1:
               iColorizer = COLORIZER_FOLIAGE_PINE;
               break;
            case 2:
               iColorizer = COLORIZER_FOLIAGE_BIRCH;
               break;
            default:
               iColorizer = COLORIZER_FOLIAGE;
         }
      } else if (block2 == Blocks.leaves2) {
         iColorizer = COLORIZER_FOLIAGE;
      } else {
         if (block2 != Blocks.vine) {
            return -1;
         }

         iColorizer = COLORIZER_FOLIAGE;
      }

      return Config.isSmoothBiomes() && !iColorizer.isColorConstant()
         ? getSmoothColorMultiplier(blockState, blockAccess, blockPos, iColorizer, renderEnv.getColorizerBlockPosM())
         : iColorizer.getColor(blockState2, blockAccess, blockPos);
   }

   private static CustomColormap[] readCustomColormaps(Properties props, String fileName) {
      ArrayList arrayList = new ArrayList();
      HashMap hashMap = new HashMap();

      for (Object propertyKey : props.keySet()) {
         String text = (String)propertyKey;
         String propertyValue = props.getProperty(text);
         if (text.startsWith("palette.block.")) {
            hashMap.put(text, propertyValue);
         }
      }

      String[] texts = (String[]) hashMap.keySet().toArray(new String[hashMap.size()]);

      for (int innerIndex = 0; innerIndex < texts.length; innerIndex++) {
         String text5 = texts[innerIndex];
         String propertyValue2 = props.getProperty(text5);
         dbg("Block palette: " + text5 + " = " + propertyValue2);
         String text3 = text5.substring("palette.block.".length());
         String text4 = TextureUtils.getBasePath(fileName);
         text3 = TextureUtils.fixResourcePath(text3, text4);
         CustomColormap customColors = getCustomColors(text3, 256, 256);
         if (customColors == null) {
            warn("Colormap not found: " + text3);
         } else {
            MatchBlock[] amatchblock = new ConnectedParser("CustomColors").parseMatchBlocks(propertyValue2);
            if (amatchblock != null && amatchblock.length > 0) {
               for (int index = 0; index < amatchblock.length; index++) {
                  MatchBlock matchBlock = amatchblock[index];
                  customColors.addMatchBlock(matchBlock);
               }

               arrayList.add(customColors);
            } else {
               warn("Invalid match blocks: " + propertyValue2);
            }
         }
      }

      return arrayList.size() <= 0 ? null : (CustomColormap[]) arrayList.toArray(new CustomColormap[arrayList.size()]);
   }

   private static int getLilypadColorMultiplier(IBlockAccess blockAccess, BlockPos blockPos) {
      return lilyPadColor < 0 ? Blocks.waterlily.colorMultiplier(blockAccess, blockPos) : lilyPadColor;
   }

   private static int[] readTextColors(Properties props, String fileName, String prefix, String logName) {
      int[] iArray = new int[32];
      Arrays.fill(iArray, -1);
      int i = 0;

      for (String text5 : (Iterable<String>) (Iterable<String>) (Set) (props.keySet())) {
         String property = props.getProperty(text5);
         if (text5.startsWith(prefix)) {
            int i2 = Config.parseInt(StrUtils.removePrefix(text5, prefix), -1);
            int i3 = parseColor(property);
            if (i2 >= 0 && i2 < iArray.length && i3 >= 0) {
               iArray[i2] = i3;
               i++;
            } else {
               warn("Invalid color: " + text5 + " = " + property);
            }
         }
      }

      if (i <= 0) {
         return null;
      }

      dbg(logName + " colors: " + i);
      return iArray;
   }

   private static CustomColormap getBlockColormap(IBlockState blockState) {
      if (blockColormaps == null) {
         return null;
      } else if (!(blockState instanceof BlockStateBase blockStateBase)) {
         return null;
      } else {
         int blockId = blockStateBase.getBlockId();
         if (blockId >= 0 && blockId < blockColormaps.length) {
            CustomColormap[] customColormaps = blockColormaps[blockId];
            if (customColormaps == null) {
               return null;
            }

            for (int innerIndex = 0; innerIndex < customColormaps.length; innerIndex++) {
               CustomColormap customColormap2 = customColormaps[innerIndex];
               if (customColormap2.matchesBlock(blockStateBase)) {
                  return customColormap2;
               }
            }

            return null;
         } else {
            return null;
         }
      }
   }

   public static int getInt() {
      return signTextColor < 0 ? 0 : signTextColor;
   }

   public static int getTextColor(int index, int color) {
      if (textColors == null) {
         return color;
      } else if (index >= 0 && index < textColors.length) {
         int i = textColors[index];
         return i < 0 ? color : i;
      } else {
         return color;
      }
   }

   private static Vec3 readColorVec3(Properties props, String name) {
      int color2 = readColor(props, name);
      if (color2 < 0) {
         return null;
      }

      int i = color2 >> 16 & 0xFF;
      int i2 = color2 >> 8 & 0xFF;
      int i3 = color2 & 0xFF;
      float f = i / 255.0F;
      float f2 = i2 / 255.0F;
      float f3 = i3 / 255.0F;
      return new Vec3(f, f2, f3);
   }

   public static int getPotionColor(int potionId, int color) {
      if (potionColors == null) {
         return color;
      } else if (potionId >= 0 && potionId < potionColors.length) {
         int i = potionColors[potionId];
         return i < 0 ? color : i;
      } else {
         return color;
      }
   }

   private static int[] readPotionColors(Properties props, String fileName, String prefix, String logName) {
      int[] intArray = new int[Potion.potionTypes.length];
      Arrays.fill(intArray, -1);
      int i = 0;

      for (String text : (Iterable<String>) (Iterable<String>) (Set) (props.keySet())) {
         String propertyValue = props.getProperty(text);
         if (text.startsWith(prefix)) {
            int potionId2 = getPotionId(text);
            int i2 = parseColor(propertyValue);
            if (potionId2 >= 0 && potionId2 < intArray.length && i2 >= 0) {
               intArray[potionId2] = i2;
               i++;
            } else {
               warn("Invalid color: " + text + " = " + propertyValue);
            }
         }
      }

      if (i <= 0) {
         return null;
      }

      dbg(logName + " colors: " + i);
      return intArray;
   }

   public static void update() {
      paletteFormatDefault = "vanilla";
      waterColors = null;
      foliageBirchColors = null;
      foliagePineColors = null;
      swampGrassColors = null;
      swampFoliageColors = null;
      skyColors = null;
      fogColors = null;
      underwaterColors = null;
      underlavaColors = null;
      redstoneColors = null;
      xpOrbColors = null;
      xpOrbTime = -1;
      durabilityColors = null;
      stemColors = null;
      myceliumParticleColors = null;
      lightMapPacks = null;
      particleWaterColor = -1;
      particlePortalColor = -1;
      lilyPadColor = -1;
      expBarTextColor = -1;
      bossTextColor = -1;
      signTextColor = -1;
      fogColorNether = null;
      fogColorEnd = null;
      skyColorEnd = null;
      colorsBlockColormaps = null;
      blockColormaps = (CustomColormap[][])null;
      useDefaultGrassFoliageColors = true;
      spawnEggPrimaryColors = null;
      spawnEggSecondaryColors = null;
      wolfCollarColors = (float[][])null;
      sheepColors = (float[][])null;
      textColors = null;
      setMapColors(mapColorsOriginal);
      potionColors = null;
      paletteFormatDefault = getValidProperty("mcpatcher/color.properties", "palette.format", CustomColormap.FORMAT_STRINGS, "vanilla");
      String[] texts = new String[]{"water.png", "watercolorX.png"};
      waterColors = getCustomColors("mcpatcher/colormap/", texts, 256, 256);
      updateUseDefaultGrassFoliageColors();
      if (Config.isCustomColors()) {
         String[] texts2 = new String[]{"pine.png", "pinecolor.png"};
         foliagePineColors = getCustomColors("mcpatcher/colormap/", texts2, 256, 256);
         String[] texts3 = new String[]{"birch.png", "birchcolor.png"};
         foliageBirchColors = getCustomColors("mcpatcher/colormap/", texts3, 256, 256);
         String[] texts4 = new String[]{"swampgrass.png", "swampgrasscolor.png"};
         swampGrassColors = getCustomColors("mcpatcher/colormap/", texts4, 256, 256);
         String[] texts5 = new String[]{"swampfoliage.png", "swampfoliagecolor.png"};
         swampFoliageColors = getCustomColors("mcpatcher/colormap/", texts5, 256, 256);
         String[] texts6 = new String[]{"sky0.png", "skycolor0.png"};
         skyColors = getCustomColors("mcpatcher/colormap/", texts6, 256, 256);
         String[] texts7 = new String[]{"fog0.png", "fogcolor0.png"};
         fogColors = getCustomColors("mcpatcher/colormap/", texts7, 256, 256);
         String[] texts8 = new String[]{"underwater.png", "underwatercolor.png"};
         underwaterColors = getCustomColors("mcpatcher/colormap/", texts8, 256, 256);
         String[] texts9 = new String[]{"underlava.png", "underlavacolor.png"};
         underlavaColors = getCustomColors("mcpatcher/colormap/", texts9, 256, 256);
         String[] texts10 = new String[]{"redstone.png", "redstonecolor.png"};
         redstoneColors = getCustomColors("mcpatcher/colormap/", texts10, 16, 1);
         xpOrbColors = getCustomColors("mcpatcher/colormap/xporb.png", -1, -1);
         durabilityColors = getCustomColors("mcpatcher/colormap/durability.png", -1, -1);
         String[] texts11 = new String[]{"stem.png", "stemcolor.png"};
         stemColors = getCustomColors("mcpatcher/colormap/", texts11, 8, 1);
         stemPumpkinColors = getCustomColors("mcpatcher/colormap/pumpkinstem.png", 8, 1);
         stemMelonColors = getCustomColors("mcpatcher/colormap/melonstem.png", 8, 1);
         String[] texts12 = new String[]{"myceliumparticle.png", "myceliumparticlecolor.png"};
         myceliumParticleColors = getCustomColors("mcpatcher/colormap/", texts12, -1, -1);
         Pair pair = parseLightMapPacks();
         lightMapPacks = (LightMapPack[])pair.getLeft();
         lightmapMinDimensionId = (Integer)pair.getRight();
         readColorProperties("mcpatcher/color.properties");
         blockColormaps = createArray(new String[]{"mcpatcher/colormap/custom/", "mcpatcher/colormap/blocks/"}, colorsBlockColormaps);
         updateUseDefaultGrassFoliageColors();
      }
   }

   public static Vec3 getWorldFogColor(Vec3 fogVec, World world, Entity renderViewEntity, float partialTicks) {
      switch (world.provider.getDimensionId()) {
         case -1:
            fogVec = getFogColorNether(fogVec);
            break;
         case 0:
            Minecraft minecraft2 = Minecraft.getMinecraft();
            fogVec = getFogColor(fogVec, minecraft2.theWorld, renderViewEntity.posX, renderViewEntity.posY + 1.0, renderViewEntity.posZ);
            break;
         case 1:
            fogVec = getFogColorEnd(fogVec);
      }

      return fogVec;
   }

   public static void updateWaterFX(EntityFX fx, IBlockAccess blockAccess, double x, double y, double z, RenderEnv renderEnv) {
      if (waterColors != null || blockColormaps != null || particleWaterColor >= 0) {
         BlockPos blockPos2 = new BlockPos(x, y, z);
         renderEnv.reset(BLOCK_STATE_WATER, blockPos2);
         int fluidColor = getFluidColor(blockAccess, BLOCK_STATE_WATER, blockPos2, renderEnv);
         int i = fluidColor >> 16 & 0xFF;
         int i2 = fluidColor >> 8 & 0xFF;
         int i3 = fluidColor & 0xFF;
         float f = i / 255.0F;
         float f2 = i2 / 255.0F;
         float f3 = i3 / 255.0F;
         if (particleWaterColor >= 0) {
            int i4 = particleWaterColor >> 16 & 0xFF;
            int i5 = particleWaterColor >> 8 & 0xFF;
            int i6 = particleWaterColor & 0xFF;
            f *= i4 / 255.0F;
            f2 *= i5 / 255.0F;
            f3 *= i6 / 255.0F;
         }

         fx.setRBGColorF(f, f2, f3);
      }
   }

   private static int readColor(Properties props, String[] names) {
      for (int index = 0; index < names.length; index++) {
         String text5 = names[index];
         int color2 = readColor(props, text5);
         if (color2 >= 0) {
            return color2;
         }
      }

      return -1;
   }

   private static float[] getDyeColors(EnumDyeColor dye, float[][] dyeColors, float[] colors) {
      if (dyeColors == null) {
         return colors;
      }

      if (dye == null) {
         return colors;
      }

      float[] fArray = dyeColors[dye.ordinal()];
      return fArray == null ? colors : fArray;
   }

   private static int getRedstoneColor(IBlockState blockState) {
      if (redstoneColors == null) {
         return -1;
      }

      int intValue = createInt(blockState);
      return redstoneColors.getColor(intValue);
   }

   private static void readColorProperties(String fileName) {
      try {
         InputStream resourceStream = Config.getResourceStream(new ResourceLocation(fileName));
         if (resourceStream == null) {
            return;
         }

         dbg("Loading " + fileName);
         PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
         propertiesOrdered.load(resourceStream);
         resourceStream.close();
         particleWaterColor = readColor(propertiesOrdered, new String[]{"particle.water", "drop.water"});
         particlePortalColor = readColor(propertiesOrdered, "particle.portal");
         lilyPadColor = readColor(propertiesOrdered, "lilypad");
         expBarTextColor = readColor(propertiesOrdered, "text.xpbar");
         bossTextColor = readColor(propertiesOrdered, "text.boss");
         signTextColor = readColor(propertiesOrdered, "text.sign");
         fogColorNether = readColorVec3(propertiesOrdered, "fog.nether");
         fogColorEnd = readColorVec3(propertiesOrdered, "fog.end");
         skyColorEnd = readColorVec3(propertiesOrdered, "sky.end");
         colorsBlockColormaps = readCustomColormaps(propertiesOrdered, fileName);
         spawnEggPrimaryColors = readSpawnEggColors(propertiesOrdered, fileName, "egg.shell.", "Spawn egg shell");
         spawnEggSecondaryColors = readSpawnEggColors(propertiesOrdered, fileName, "egg.spots.", "Spawn egg spot");
         wolfCollarColors = readDyeColors(propertiesOrdered, fileName, "collar.", "Wolf collar");
         sheepColors = readDyeColors(propertiesOrdered, fileName, "sheep.", "Sheep");
         textColors = readTextColors(propertiesOrdered, fileName, "text.code.", "Text");
         int[] mapColors = readMapColors(propertiesOrdered, fileName, "map.", "Map");
         if (mapColors != null) {
            if (mapColorsOriginal == null) {
               mapColorsOriginal = getMapColors();
            }

            setMapColors(mapColors);
         }

         potionColors = readPotionColors(propertiesOrdered, fileName, "potion.", "Potion");
         xpOrbTime = Config.parseInt(propertiesOrdered.getProperty("xporb.time"), -1);
      } catch (FileNotFoundException fileNotFoundException) {
         return;
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   public static Vec3 getWorldSkyColor(Vec3 skyVec, World world, Entity renderViewEntity, float partialTicks) {
      switch (world.provider.getDimensionId()) {
         case 0:
            Minecraft minecraft2 = Minecraft.getMinecraft();
            skyVec = getSkyColor(skyVec, minecraft2.theWorld, renderViewEntity.posX, renderViewEntity.posY + 1.0, renderViewEntity.posZ);
            break;
         case 1:
            skyVec = getSkyColorEnd(skyVec);
      }

      return skyVec;
   }

   public static float[] getSheepColors(EnumDyeColor dye, float[] colors) {
      return getDyeColors(dye, sheepColors, colors);
   }

   private static int[] readSpawnEggColors(Properties props, String fileName, String prefix, String logName) {
      ArrayList arrayList = new ArrayList();
      Set set = props.keySet();
      int i = 0;

      for (Object outerIndex : set) {
         String propertyValue = (String)outerIndex;
         String text2 = props.getProperty(propertyValue);
         if (propertyValue.startsWith(prefix)) {
            int entityIdByName = EntityUtils.getEntityIdByName(StrUtils.removePrefix(propertyValue, prefix));
            if (entityIdByName < 0) {
               warn("Invalid spawn egg name: " + propertyValue);
            } else {
               int i2 = parseColor(text2);
               if (i2 < 0) {
                  warn("Invalid spawn egg color: " + propertyValue + " = " + text2);
               } else {
                  while (arrayList.size() <= entityIdByName) {
                     arrayList.add(-1);
                  }

                  arrayList.set(entityIdByName, i2);
                  i++;
               }
            }
         }
      }

      if (i <= 0) {
         return null;
      }

      dbg(logName + " colors: " + i);
      int[] intArray = new int[arrayList.size()];

      for (int outerIndex = 0; outerIndex < intArray.length; outerIndex++) {
         intArray[outerIndex] = (Integer)arrayList.get(outerIndex);
      }

      return intArray;
   }

   public static int getColorFromItemStack(ItemStack itemStack, int layer, int color) {
      if (itemStack == null) {
         return color;
      }

      Item item = itemStack.getItem();
      return item == null ? color : (item instanceof ItemMonsterPlacer ? getSpawnEggColor((ItemMonsterPlacer)item, itemStack, layer, color) : color);
   }

   public static Vec3 getUnderwaterColor(IBlockAccess blockAccess, double x, double y, double z) {
      return getUnderFluidColor(blockAccess, x, y, z, underwaterColors, underwaterColorFader);
   }

   private static Vec3 getFogColor(Vec3 fogColor3d, IBlockAccess blockAccess, double x, double y, double z) {
      if (fogColors == null) {
         return fogColor3d;
      }

      int intValue = fogColors.getInt(blockAccess, x, y, z);
      int i = intValue >> 16 & 0xFF;
      int i2 = intValue >> 8 & 0xFF;
      int i3 = intValue & 0xFF;
      float f = i / 255.0F;
      float f2 = i2 / 255.0F;
      float f3 = i3 / 255.0F;
      float f4 = (float)fogColor3d.xCoord / 0.753F;
      float f5 = (float)fogColor3d.yCoord / 0.8471F;
      float f6 = (float)fogColor3d.zCoord;
      f *= f4;
      f2 *= f5;
      f3 *= f6;
      return fogColorFader.getColor(f, f2, f3);
   }

   private static CustomColormap[][] blockListToArray(List lists) {
      CustomColormap[][] acustomcolormap = new CustomColormap[lists.size()][];

      for (int index = 0; index < lists.size(); index++) {
         List list = (List)lists.get(index);
         if (list != null) {
            CustomColormap[] customColormaps = (CustomColormap[]) list.toArray(new CustomColormap[list.size()]);
            acustomcolormap[index] = customColormaps;
         }
      }

      return acustomcolormap;
   }

   private static int[] getMapColors() {
      MapColor[] amapcolor = MapColor.mapColorArray;
      int[] intArray = new int[amapcolor.length];
      Arrays.fill(intArray, -1);

      for (int index = 0; index < amapcolor.length && index < intArray.length; index++) {
         MapColor mapColor = amapcolor[index];
         if (mapColor != null) {
            intArray[index] = mapColor.colorValue;
         }
      }

      return intArray;
   }

   public static void updateReddustFX(EntityFX fx, IBlockAccess blockAccess, double x, double y, double z) {
      if (redstoneColors != null) {
         int intValue = createInt(blockAccess.getBlockState(new BlockPos(x, y, z)));
         int color2 = redstoneColors.getColor(intValue);
         int i = color2 >> 16 & 0xFF;
         int i2 = color2 >> 8 & 0xFF;
         int i3 = color2 & 0xFF;
         float f = i / 255.0F;
         float f2 = i2 / 255.0F;
         float f3 = i3 / 255.0F;
         fx.setRBGColorF(f, f2, f3);
      }
   }

   public interface IColorizer {
      int getColor(IBlockState iBlockState, IBlockAccess iBlockAccess, BlockPos blockPos2);

      boolean isColorConstant();
   }
}
