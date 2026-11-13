package net.optifine;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.config.ConnectedParser;
import net.optifine.config.MatchBlock;
import net.optifine.config.Matches;
import net.optifine.util.TextureUtils;

public class CustomColormap implements CustomColors.IColorizer {
   public String name = null;
   public static final String GRID = "grid";
   public String basePath = null;
   private static final int FORMAT_FIXED = 2;
   public static final String FORMAT = "format";
   public static final String COLOR = "color";
   private int format = -1;
   private MatchBlock[] matchBlocks = null;
   private String source = null;
   public static final String BLOCKS = "blocks";
   private static final int FORMAT_UNKNOWN = -1;
   private static final int FORMAT_VANILLA = 0;
   public static final String VANILLA = "vanilla";
   private int color = -1;
   private int yVariance = 0;
   private int yOffset = 0;
   public static final String FIXED = "fixed";
   private int width = 0;
   private int height = 0;
   private static final int FORMAT_GRID = 1;
   public static final String Y_VARIANCE = "yVariance";
   public static final String Y_OFFSET = "yOffset";
   private int[] colors = null;
   public static final String SOURCE = "source";
   private float[][] colorsRgb = (float[][])null;
   public static final String[] FORMAT_STRINGS = new String[]{"vanilla", "grid", "fixed"};

   public int getColor(BiomeGenBase biome, BlockPos blockPos) {
      return this.format == 0 ? this.getColorVanilla(biome, blockPos) : (this.format == 1 ? this.getColorGrid(biome, blockPos) : this.color);
   }

   public boolean isValid(String path) {
      if (this.format != 0 && this.format != 1) {
         if (this.format != 2) {
            return false;
         }

         if (this.color < 0) {
            this.color = 16777215;
         }
      } else {
         if (this.source == null) {
            warn("Source not defined: " + path);
            return false;
         }

         this.readColors();
         if (this.colors == null) {
            return false;
         }

         if (this.color < 0) {
            if (this.format == 0) {
               this.color = this.getColor(127, 127);
            }

            if (this.format == 1) {
               this.color = this.getColorGrid(BiomeGenBase.plains, new BlockPos(0, 64, 0));
            }
         }
      }

      return true;
   }

   public int getInt(IBlockAccess iBlockAccess, double d, double d2, double d3) {
      if (this.format == 2) {
         return this.color;
      }

      int i = MathHelper.floor_double(d);
      int i2 = MathHelper.floor_double(d2);
      int i3 = MathHelper.floor_double(d3);
      int i4 = 0;
      int i5 = 0;
      int i6 = 0;
      int i7 = 0;
      BlockPosM blockPosM = new BlockPosM(0, 0, 0);

      for (int i8 = i - 3; i8 <= i + 3; i8++) {
         for (int i9 = i3 - 3; i9 <= i3 + 3; i9++) {
            blockPosM.setXyz(i8, i2, i9);
            int color2 = this.getColor(iBlockAccess, blockPosM);
            i4 += color2 >> 16 & 0xFF;
            i5 += color2 >> 8 & 0xFF;
            i6 += color2 & 0xFF;
            i7++;
         }
      }

      int i10 = i4 / i7;
      int i11 = i5 / i7;
      int i12 = i6 / i7;
      return i10 << 16 | i11 << 8 | i12;
   }

   private void readColors() {
      try {
         this.colors = null;
         if (this.source == null) {
            return;
         }

         String text = this.source + ".png";
         InputStream resourceStream = Config.getResourceStream(new ResourceLocation(text));
         if (resourceStream == null) {
            return;
         }

         BufferedImage bufferedImage = TextureUtil.readBufferedImage(resourceStream);
         if (bufferedImage == null) {
            return;
         }

         int width2 = bufferedImage.getWidth();
         int height2 = bufferedImage.getHeight();
         boolean flag = this.width < 0 || this.width == width2;
         boolean flag2 = this.height < 0 || this.height == height2;
         if (!flag || !flag2) {
            dbg("Non-standard palette size: " + width2 + "x" + height2 + ", should be: " + this.width + "x" + this.height + ", path: " + text);
         }

         this.width = width2;
         this.height = height2;
         if (this.width <= 0 || this.height <= 0) {
            warn("Invalid palette size: " + width2 + "x" + height2 + ", path: " + text);
            return;
         }

         this.colors = new int[width2 * height2];
         bufferedImage.getRGB(0, 0, width2, height2, this.colors, 0, width2);
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   @Override
   public boolean isColorConstant() {
      return this.format == 2;
   }

   private static float[][] toRgb(int[] cols) {
      float[][] floatArray = new float[cols.length][3];

      for (int index = 0; index < cols.length; index++) {
         int i = cols[index];
         float f = (i >> 16 & 0xFF) / 255.0F;
         float f2 = (i >> 8 & 0xFF) / 255.0F;
         float f3 = (i & 0xFF) / 255.0F;
         float[] fArray = floatArray[index];
         fArray[0] = f;
         fArray[1] = f2;
         fArray[2] = f3;
      }

      return floatArray;
   }

   public int getColor(int index) {
      index = Config.limit(index, 0, this.colors.length - 1);
      return this.colors[index] & 16777215;
   }

   public int[] getMatchBlockIds() {
      if (this.matchBlocks == null) {
         return null;
      }

      HashSet hashSet = new HashSet();

      for (int index = 0; index < this.matchBlocks.length; index++) {
         MatchBlock intArray = this.matchBlocks[index];
         if (intArray.getBlockId() >= 0) {
            hashSet.add(intArray.getBlockId());
         }
      }

      Integer[] integers = (Integer[]) hashSet.toArray(new Integer[hashSet.size()]);
      int[] intArray = new int[integers.length];

      for (int innerIndex = 0; innerIndex < integers.length; innerIndex++) {
         intArray[innerIndex] = integers[innerIndex];
      }

      return intArray;
   }

   @Override
   public String toString() {
      return this.basePath + "/" + this.name + ", blocks: " + Config.arrayToString(this.matchBlocks) + ", source: " + this.source;
   }

   public void addMatchBlock(MatchBlock mb) {
      if (this.matchBlocks == null) {
         this.matchBlocks = new MatchBlock[0];
      }

      this.matchBlocks = (MatchBlock[])Config.addObjectToArray(this.matchBlocks, mb);
   }

   public boolean isValidMatchBlocks(String path) {
      if (this.matchBlocks == null) {
         this.matchBlocks = this.detectMatchBlocks();
         if (this.matchBlocks == null) {
            warn("Match blocks not defined: " + path);
            return false;
         }
      }

      return true;
   }

   private static void warn(String str) {
      Config.warn("CustomColors: " + str);
   }

   public float[][] getColorsRgb() {
      if (this.colorsRgb == null) {
         this.colorsRgb = toRgb(this.colors);
      }

      return this.colorsRgb;
   }

   private MatchBlock[] detectMatchBlocks() {
      Block blockFromName = Block.getBlockFromName(this.name);
      if (blockFromName != null) {
         return new MatchBlock[]{new MatchBlock(Block.getIdFromBlock(blockFromName))};
      }

      Matcher matcher2 = Pattern.compile("^block([0-9]+).*$").matcher(this.name);
      if (matcher2.matches()) {
         int i = Config.parseInt(matcher2.group(1), -1);
         if (i >= 0) {
            return new MatchBlock[]{new MatchBlock(i)};
         }
      }

      MatchBlock[] matchBlocks2 = new ConnectedParser("Colormap").parseMatchBlock(this.name);
      return matchBlocks2 != null ? matchBlocks2 : null;
   }

   public boolean matchesBlock(BlockStateBase blockState) {
      return Matches.block(blockState, this.matchBlocks);
   }

   public int getWidth() {
      return this.width;
   }

   @Override
   public int getColor(IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos) {
      return this.getColor(blockAccess, blockPos);
   }

   public int getLength() {
      return this.format == 2 ? 1 : this.colors.length;
   }

   private static String parseTexture(String texStr, String path, String basePath) {
      if (texStr != null) {
         texStr = texStr.trim();
         if (texStr.endsWith(".png")) {
            texStr = texStr.substring(0, texStr.length() - ".png".length());
         }

         return fixTextureName(texStr, basePath);
      } else {
         String path2 = path;
         int i = path.lastIndexOf(47);
         if (i >= 0) {
            path2 = path.substring(i + 1);
         }

         int i2 = path2.lastIndexOf(46);
         if (i2 >= 0) {
            path2 = path2.substring(0, i2);
         }

         return fixTextureName(path2, basePath);
      }
   }

   private static void dbg(String str) {
      Config.dbg("CustomColors: " + str);
   }

   private int parseFormat(String str) {
      if (str == null) {
         return 0;
      }

      str = str.trim();
      if (str.equals("vanilla")) {
         return 0;
      }

      if (str.equals("grid")) {
         return 1;
      }

      if (str.equals("fixed")) {
         return 2;
      }

      warn("Unknown format: " + str);
      return -1;
   }

   private static String fixTextureName(String iconName, String basePath) {
      iconName = TextureUtils.fixResourcePath(iconName, basePath);
      if (!iconName.startsWith(basePath) && !iconName.startsWith("textures/") && !iconName.startsWith("mcpatcher/")) {
         iconName = basePath + "/" + iconName;
      }

      if (iconName.endsWith(".png")) {
         iconName = iconName.substring(0, iconName.length() - 4);
      }

      if (iconName.startsWith("textures/blocks/")) {
         iconName = iconName.substring("textures/blocks/".length());
      }

      if (iconName.startsWith("/")) {
         iconName = iconName.substring(1);
      }

      return iconName;
   }

   public int getColor(IBlockAccess blockAccess, BlockPos blockPos) {
      BiomeGenBase biomeGenBase = CustomColors.getColorBiome(blockAccess, blockPos);
      return this.getColor(biomeGenBase, blockPos);
   }

   private int getColorGrid(BiomeGenBase biome, BlockPos blockPos) {
      int biomeID2 = biome.biomeID;
      int i = blockPos.getY() - this.yOffset;
      if (this.yVariance > 0) {
         int i2 = Config.intHash(blockPos.getX() << 16 + blockPos.getZ());
         int i3 = this.yVariance * 2 + 1;
         int i4 = (i2 & 0xFF) % i3 - this.yVariance;
         i += i4;
      }

      return this.getColor(biomeID2, i);
   }

   private int getColorVanilla(BiomeGenBase biome, BlockPos blockPos) {
      double d = MathHelper.clamp_float(biome.getFloatTemperature(blockPos), 0.0F, 1.0F);
      double d2 = MathHelper.clamp_float(biome.getFloatRainfall(), 0.0F, 1.0F);
      d2 *= d;
      int i = (int)((1.0 - d) * (this.width - 1));
      int i2 = (int)((1.0 - d2) * (this.height - 1));
      return this.getColor(i, i2);
   }

   public CustomColormap(Properties props, String path, int width, int height, String formatDefault) {
      ConnectedParser connectedParser = new ConnectedParser("Colormap");
      this.name = connectedParser.parseName(path);
      this.basePath = connectedParser.parseBasePath(path);
      this.format = this.parseFormat(props.getProperty("format", formatDefault));
      this.matchBlocks = connectedParser.parseMatchBlocks(props.getProperty("blocks"));
      this.source = parseTexture(props.getProperty("source"), path, this.basePath);
      this.color = ConnectedParser.createInt(props.getProperty("color"));
      this.yVariance = connectedParser.parseInt(props.getProperty("yVariance"), 0);
      this.yOffset = connectedParser.parseInt(props.getProperty("yOffset"), 0);
      this.width = width;
      this.height = height;
   }

   public int getHeight() {
      return this.height;
   }

   public int getColor(int cx, int cy) {
      cx = Config.limit(cx, 0, this.width - 1);
      cy = Config.limit(cy, 0, this.height - 1);
      return this.colors[cy * this.width + cx] & 16777215;
   }

   public int getColorRandom() {
      if (this.format == 2) {
         return this.color;
      }

      int count = CustomColors.random.nextInt(this.colors.length);
      return this.colors[count];
   }
}
