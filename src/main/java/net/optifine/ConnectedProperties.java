package net.optifine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.src.Config;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.config.ConnectedParser;
import net.optifine.config.MatchBlock;
import net.optifine.config.Matches;
import net.optifine.config.NbtTagValue;
import net.optifine.config.RangeInt;
import net.optifine.config.RangeListInt;
import net.optifine.util.MathUtils;
import net.optifine.util.TextureUtils;

public class ConnectedProperties {
   public static final int intValue = 2;
   public String name = null;
   public String basePath = null;
   public static final int FACE_WEST = 16;
   public MatchBlock[] matchBlocks = null;
   public int[] metadatas = null;
   public String[] matchTiles = null;
   public int method = 0;
   public String[] tiles = null;
   public static final int METHOD_OVERLAY_FIXED = 12;
   public static final int intValue2 = 2;
   public int connect = 0;
   public int faces = 63;
   public BiomeGenBase[] biomes = null;
   public static final int FACE_EAST = 32;
   public RangeListInt heights = null;
   public static final int intValue3 = 0;
   public int renderPass = 0;
   public static final int intValue4 = 3;
   public static final int intValue5 = 6;
   public boolean innerSeams = false;
   public static final int intValue6 = 128;
   public int[] ctmTileIndexes = null;
   public static final int METHOD_OVERLAY_REPEAT = 14;
   public int width = 0;
   public int height = 0;
   public static final int METHOD_VERTICAL_HORIZONTAL = 9;
   public static final int intValue7 = 128;
   public static final int intValue8 = 3;
   public int[] weights = null;
   public static final int METHOD_OVERLAY_RANDOM = 13;
   public int randomLoops = 0;
   public static final int intValue9 = 0;
   public int symmetry = 1;
   public static final int intValue10 = 1;
   public static final int intValue11 = 8;
   public static final int METHOD_FIXED = 7;
   public static final int intValue12 = 4;
   public static final int METHOD_OVERLAY = 11;
   public boolean linked = false;
   public static final int intValue13 = 6;
   public static final String text = "<default>.png";
   public static final int FACE_SIDES = 60;
   public NbtTagValue nbtName = null;
   public static final int intValue14 = 1;
   public static final int intValue15 = 8;
   public int[] sumWeights = null;
   public static final int intValue16 = 2;
   public static final String text2 = "<skip>.png";
   public static final int METHOD_OVERLAY_CTM = 15;
   public static final int intValue17 = 2;
   public int sumAllWeights = 1;
   public static final int intValue18 = 1;
   public static final int METHOD_CTM_COMPACT = 10;
   public TextureAtlasSprite[] matchTileIcons = null;
   public TextureAtlasSprite[] tileIcons = null;
   public MatchBlock[] connectBlocks = null;
   public String[] connectTiles = null;
   public TextureAtlasSprite[] connectTileIcons = null;
   public static final int intValue19 = 1;
   public static final int FACE_ALL = 63;
   public static final int intValue20 = 128;
   public int tintIndex = -1;
   public IBlockState tintBlockState = Blocks.air.getDefaultState();
   public EnumWorldBlockLayer layer = null;
   public static final int METHOD_REPEAT = 5;
   public static final int intValue21 = 4;

   public static IProperty getProperty(String key, Collection<IProperty> properties) {
      for (IProperty property : properties) {
         if (key.equals(property.getName())) {
            return property;
         }
      }

      return null;
   }

   private boolean isValidRepeat(String path) {
      if (this.tiles == null) {
         Config.warn("Tiles not defined: " + path);
         return false;
      } else if (this.width <= 0) {
         Config.warn("Invalid width: " + path);
         return false;
      } else if (this.height <= 0) {
         Config.warn("Invalid height: " + path);
         return false;
      } else if (this.tiles.length != this.width * this.height) {
         Config.warn("Number of tiles does not equal width x height: " + path);
         return false;
      } else {
         return true;
      }
   }

   public boolean matchesBiome(BiomeGenBase biome) {
      return Matches.biome(biome, this.biomes);
   }

   public boolean matchesBlock(int blockId, int metadata) {
      return !Matches.block(blockId, metadata, this.matchBlocks) ? false : Matches.metadata(metadata, this.metadatas);
   }

   private boolean isValidVerticalHorizontal(String path) {
      if (this.tiles == null) {
         Config.warn("No tiles defined for vertical+horizontal: " + path);
         return false;
      } else if (this.tiles.length != 7) {
         Config.warn("Invalid tiles, must be exactly 7: " + path);
         return false;
      } else {
         return true;
      }
   }

   private boolean isValidCtmCompact(String path) {
      if (this.tiles == null) {
         this.tiles = this.parseTileNames("0-4");
      }

      if (this.tiles.length < 5) {
         Config.warn("Invalid tiles, must be at least 5: " + path);
         return false;
      } else {
         return true;
      }
   }

   private static TextureAtlasSprite[] registerIcons(String[] tileNames, TextureMap textureMap, boolean skipTiles, boolean defaultTiles) {
      if (tileNames == null) {
         return null;
      }

      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < tileNames.length; i++) {
         String text3 = tileNames[i];
         ResourceLocation resourceLocation = new ResourceLocation(text3);
         String resourceDomain = resourceLocation.getResourceDomain();
         String resourcePath = resourceLocation.getResourcePath();
         if (!resourcePath.contains("/")) {
            resourcePath = "textures/blocks/" + resourcePath;
         }

         String text4 = resourcePath + ".png";
         if (skipTiles && text4.endsWith("<skip>.png")) {
            arrayList.add(null);
         } else if (defaultTiles && text4.endsWith("<default>.png")) {
            arrayList.add(ConnectedTextures.SPRITE_DEFAULT);
         } else {
            if (!Config.hasResource(new ResourceLocation(resourceDomain, text4))) {
               Config.warn("File not found: " + text4);
            }

            String text5 = resourcePath;
            if (resourcePath.startsWith("textures/")) {
               text5 = resourcePath.substring("textures/".length());
            }

            ResourceLocation resourceLocation2 = new ResourceLocation(resourceDomain, text5);
            TextureAtlasSprite textureAtlasSprite = textureMap.registerSprite(resourceLocation2);
            arrayList.add(textureAtlasSprite);
         }
      }

      return (TextureAtlasSprite[]) arrayList.toArray(new TextureAtlasSprite[arrayList.size()]);
   }

   private String[] parseMatchTiles(String str) {
      if (str == null) {
         return null;
      }

      String[] texts = Config.tokenize(str, " ");

      for (int i = 0; i < texts.length; i++) {
         String text3 = texts[i];
         if (text3.endsWith(".png")) {
            text3 = text3.substring(0, text3.length() - 4);
         }

         text3 = TextureUtils.fixResourcePath(text3, this.basePath);
         texts[i] = text3;
      }

      return texts;
   }

   private MatchBlock[] detectMatchBlocks() {
      int[] intArray = this.detectMatchBlockIds();
      if (intArray == null) {
         return null;
      }

      MatchBlock[] amatchblock = new MatchBlock[intArray.length];

      for (int index = 0; index < amatchblock.length; index++) {
         amatchblock[index] = new MatchBlock(intArray[index]);
      }

      return amatchblock;
   }

   private static boolean isMethodOverlay(int method) {
      switch (method) {
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
            return true;
         default:
            return false;
      }
   }

   private boolean isValidCtm(String path) {
      if (this.tiles == null) {
         this.tiles = this.parseTileNames("0-11 16-27 32-43 48-58");
      }

      if (this.tiles.length < 47) {
         Config.warn("Invalid tiles, must be at least 47: " + path);
         return false;
      } else {
         return true;
      }
   }

   private static int parseFace(String str) {
      str = str.toLowerCase();
      if (str.equals("bottom") || str.equals("down")) {
         return 1;
      }

      if (str.equals("top") || str.equals("up")) {
         return 2;
      }

      if (str.equals("north")) {
         return 4;
      }

      if (str.equals("south")) {
         return 8;
      }

      if (str.equals("east")) {
         return 32;
      }

      if (str.equals("west")) {
         return 16;
      }

      if (str.equals("sides")) {
         return 60;
      }

      if (str.equals("all")) {
         return 63;
      }

      Config.warn("Unknown face: " + str);
      return 128;
   }

   private String[] parseTileNames(String str) {
      if (str == null) {
         return null;
      }

      ArrayList arrayList = new ArrayList();
      String[] texts = Config.tokenize(str, " ,");

      for (int i = 0; i < texts.length; i++) {
         String text3 = texts[i];
         if (text3.contains("-")) {
            String[] texts2 = Config.tokenize(text3, "-");
            if (texts2.length == 2) {
               int i2 = Config.parseInt(texts2[0], -1);
               int i3 = Config.parseInt(texts2[1], -1);
               if (i2 >= 0 && i3 >= 0) {
                  if (i2 > i3) {
                     Config.warn("Invalid interval: " + text3 + ", when parsing: " + str);
                     continue;
                  }

                  for (int i4 = i2; i4 <= i3; i4++) {
                     arrayList.add(String.valueOf(i4));
                  }
                  continue;
               }
            }
         }

         arrayList.add(text3);
      }

      String[] texts3 = (String[]) arrayList.toArray(new String[arrayList.size()]);

      for (int i5 = 0; i5 < texts3.length; i5++) {
         String text4 = texts3[i5];
         text4 = TextureUtils.fixResourcePath(text4, this.basePath);
         if (!text4.startsWith(this.basePath) && !text4.startsWith("textures/") && !text4.startsWith("mcpatcher/")) {
            text4 = this.basePath + "/" + text4;
         }

         if (text4.endsWith(".png")) {
            text4 = text4.substring(0, text4.length() - 4);
         }

         if (text4.startsWith("/")) {
            text4 = text4.substring(1);
         }

         texts3[i5] = text4;
      }

      return texts3;
   }

   private static TextureAtlasSprite getIcon(String iconName) {
      TextureMap textureMapBlocks = Minecraft.getMinecraft().getTextureMapBlocks();
      TextureAtlasSprite spriteSafe = textureMapBlocks.getSpriteSafe(iconName);
      return spriteSafe != null ? spriteSafe : textureMapBlocks.getSpriteSafe("blocks/" + iconName);
   }

   private int[] parseCtmTileIndexes(Properties props) {
      if (this.tiles == null) {
         return null;
      }

      HashMap hashMap = new HashMap();

      for (Object object : props.keySet()) {
         if (object instanceof String text3 && text3.startsWith("ctm.")) {
            String text4 = text3.substring("ctm.".length());
            String property2 = props.getProperty(text3);
            if (property2 != null) {
               property2 = property2.trim();
               int i = Config.parseInt(text4, -1);
               if (i >= 0 && i <= 46) {
                  int i2 = Config.parseInt(property2, -1);
                  if (i2 >= 0 && i2 < this.tiles.length) {
                     hashMap.put(i, i2);
                  } else {
                     Config.warn("Invalid CTM tile index: " + property2);
                  }
               } else {
                  Config.warn("Invalid CTM index: " + text4);
               }
            }
         }
      }

      if (hashMap.isEmpty()) {
         return null;
      }

      int[] iArray = new int[47];

      for (int i3 = 0; i3 < iArray.length; i3++) {
         iArray[i3] = -1;
         if (hashMap.containsKey(i3)) {
            iArray[i3] = (Integer)hashMap.get(i3);
         }
      }

      return iArray;
   }

   private String[] detectMatchTiles() {
      return getIcon(this.name) == null ? null : new String[]{this.name};
   }

   private boolean isValidRandom(String path) {
      if (this.tiles != null && this.tiles.length > 0) {
         if (this.weights != null) {
            if (this.weights.length > this.tiles.length) {
               Config.warn("More weights defined than tiles, trimming weights: " + path);
               int[] intArray = new int[this.tiles.length];
               System.arraycopy(this.weights, 0, intArray, 0, intArray.length);
               this.weights = intArray;
            }

            if (this.weights.length < this.tiles.length) {
               Config.warn("Less weights defined than tiles, expanding weights: " + path);
               int[] intArray1 = new int[this.tiles.length];
               System.arraycopy(this.weights, 0, intArray1, 0, this.weights.length);
               int average = MathUtils.getAverage(this.weights);

               for (int innerIndex = this.weights.length; innerIndex < intArray1.length; innerIndex++) {
                  intArray1[innerIndex] = average;
               }

               this.weights = intArray1;
            }

            this.sumWeights = new int[this.weights.length];
            int i = 0;

            for (int outerIndex = 0; outerIndex < this.weights.length; outerIndex++) {
               i += this.weights[outerIndex];
               this.sumWeights[outerIndex] = i;
            }

            this.sumAllWeights = i;
            if (this.sumAllWeights <= 0) {
               Config.warn("Invalid sum of all weights: " + i);
               this.sumAllWeights = 1;
            }
         }

         if (this.randomLoops >= 0 && this.randomLoops <= 9) {
            return true;
         }

         Config.warn("Invalid randomLoops: " + this.randomLoops);
         return false;
      } else {
         Config.warn("Tiles not defined: " + path);
         return false;
      }
   }

   private static int parseFaces(String str) {
      if (str == null) {
         return 63;
      }

      String[] texts = Config.tokenize(str, " ,");
      int i = 0;

      for (int i2 = 0; i2 < texts.length; i2++) {
         int i3 = parseFace(texts[i2]);
         i |= i3;
      }

      return i;
   }

   private boolean isValidOverlayFixed(String path) {
      if (!this.isValidFixed(path)) {
         return false;
      }

      if (this.layer != null && this.layer != EnumWorldBlockLayer.SOLID) {
         return true;
      }

      Config.warn("Invalid overlay layer: " + this.layer);
      return false;
   }

   private boolean isValidTop(String path) {
      if (this.tiles == null) {
         this.tiles = this.parseTileNames("66");
      }

      if (this.tiles.length != 1) {
         Config.warn("Invalid tiles, must be exactly 1: " + path);
         return false;
      } else {
         return true;
      }
   }

   public ConnectedProperties(Properties props, String path) {
      ConnectedParser connectedParser = new ConnectedParser("ConnectedTextures");
      this.name = connectedParser.parseName(path);
      this.basePath = connectedParser.parseBasePath(path);
      this.matchBlocks = connectedParser.parseMatchBlocks(props.getProperty("matchBlocks"));
      this.metadatas = connectedParser.parseIntList(props.getProperty("metadata"));
      this.matchTiles = this.parseMatchTiles(props.getProperty("matchTiles"));
      this.method = parseMethod(props.getProperty("method"));
      this.tiles = this.parseTileNames(props.getProperty("tiles"));
      this.connect = parseConnect(props.getProperty("connect"));
      this.faces = parseFaces(props.getProperty("faces"));
      this.biomes = connectedParser.parseBiomes(props.getProperty("biomes"));
      this.heights = connectedParser.parseRangeListInt(props.getProperty("heights"));
      if (this.heights == null) {
         int i = connectedParser.parseInt(props.getProperty("minHeight"), -1);
         int i2 = connectedParser.parseInt(props.getProperty("maxHeight"), 1024);
         if (i != -1 || i2 != 1024) {
            this.heights = new RangeListInt(new RangeInt(i, i2));
         }
      }

      this.renderPass = connectedParser.parseInt(props.getProperty("renderPass"), -1);
      this.innerSeams = connectedParser.checkString(props.getProperty("innerSeams"));
      this.ctmTileIndexes = this.parseCtmTileIndexes(props);
      this.width = connectedParser.parseInt(props.getProperty("width"), -1);
      this.height = connectedParser.parseInt(props.getProperty("height"), -1);
      this.weights = connectedParser.parseIntList(props.getProperty("weights"));
      this.randomLoops = connectedParser.parseInt(props.getProperty("randomLoops"), 0);
      this.symmetry = parseSymmetry(props.getProperty("symmetry"));
      this.linked = connectedParser.checkString(props.getProperty("linked"));
      this.nbtName = connectedParser.parseNbtTagValue("name", props.getProperty("name"));
      this.connectBlocks = connectedParser.parseMatchBlocks(props.getProperty("connectBlocks"));
      this.connectTiles = this.parseMatchTiles(props.getProperty("connectTiles"));
      this.tintIndex = connectedParser.parseInt(props.getProperty("tintIndex"), -1);
      this.tintBlockState = connectedParser.parseBlockState(props.getProperty("tintBlock"), Blocks.air.getDefaultState());
      this.layer = connectedParser.parseBlockRenderLayer(props.getProperty("layer"), EnumWorldBlockLayer.CUTOUT_MIPPED);
   }

   private static int parseMethod(String str) {
      if (str == null) {
         return 1;
      }

      str = str.trim();
      if (str.equals("ctm") || str.equals("glass")) {
         return 1;
      }

      if (str.equals("ctm_compact")) {
         return 10;
      }

      if (str.equals("horizontal") || str.equals("bookshelf")) {
         return 2;
      }

      if (str.equals("vertical")) {
         return 6;
      }

      if (str.equals("top")) {
         return 3;
      }

      if (str.equals("random")) {
         return 4;
      }

      if (str.equals("repeat")) {
         return 5;
      }

      if (str.equals("fixed")) {
         return 7;
      }

      if (str.equals("horizontal+vertical") || str.equals("h+v")) {
         return 8;
      }

      if (str.equals("vertical+horizontal") || str.equals("v+h")) {
         return 9;
      }

      if (str.equals("overlay")) {
         return 11;
      }

      if (str.equals("overlay_fixed")) {
         return 12;
      }

      if (str.equals("overlay_random")) {
         return 13;
      }

      if (str.equals("overlay_repeat")) {
         return 14;
      }

      if (str.equals("overlay_ctm")) {
         return 15;
      }

      Config.warn("Unknown method: " + str);
      return 0;
   }

   private boolean isValidVertical(String path) {
      if (this.tiles == null) {
         Config.warn("No tiles defined for vertical: " + path);
         return false;
      } else if (this.tiles.length != 4) {
         Config.warn("Invalid tiles, must be exactly 4: " + path);
         return false;
      } else {
         return true;
      }
   }

   public boolean matchesBlockId(int blockId) {
      return Matches.blockId(blockId, this.matchBlocks);
   }

   private boolean isValidHorizontal(String path) {
      if (this.tiles == null) {
         this.tiles = this.parseTileNames("12-15");
      }

      if (this.tiles.length != 4) {
         Config.warn("Invalid tiles, must be exactly 4: " + path);
         return false;
      } else {
         return true;
      }
   }

   private boolean isValidOverlayRandom(String path) {
      if (!this.isValidRandom(path)) {
         return false;
      }

      if (this.layer != null && this.layer != EnumWorldBlockLayer.SOLID) {
         return true;
      }

      Config.warn("Invalid overlay layer: " + this.layer);
      return false;
   }

   @Override
   public String toString() {
      return "CTM name: "
         + this.name
         + ", basePath: "
         + this.basePath
         + ", matchBlocks: "
         + Config.arrayToString(this.matchBlocks)
         + ", matchTiles: "
         + Config.arrayToString(this.matchTiles);
   }

   private boolean isValidOverlayRepeat(String path) {
      if (!this.isValidRepeat(path)) {
         return false;
      }

      if (this.layer != null && this.layer != EnumWorldBlockLayer.SOLID) {
         return true;
      }

      Config.warn("Invalid overlay layer: " + this.layer);
      return false;
   }

   private static String parseBasePath(String path) {
      int i = path.lastIndexOf(47);
      return i < 0 ? "" : path.substring(0, i);
   }

   public int getMetadataMax() {
      int max2 = this.getMax(this.metadatas, -1);
      if (this.matchBlocks != null) {
         for (int innerIndex = 0; innerIndex < this.matchBlocks.length; innerIndex++) {
            MatchBlock matchBlock = this.matchBlocks[innerIndex];
            max2 = this.getMax(matchBlock.getMetadatas(), max2);
         }
      }

      return max2;
   }

   private static int parseConnect(String str) {
      if (str == null) {
         return 0;
      }

      str = str.trim();
      if (str.equals("block")) {
         return 1;
      }

      if (str.equals("tile")) {
         return 2;
      }

      if (str.equals("material")) {
         return 3;
      }

      Config.warn("Unknown connect: " + str);
      return 128;
   }

   private int detectConnect() {
      return this.matchBlocks != null ? 1 : (this.matchTiles != null ? 2 : 128);
   }

   private boolean isValidOverlay(String path) {
      if (this.tiles == null) {
         this.tiles = this.parseTileNames("0-16");
      }

      if (this.tiles.length < 17) {
         Config.warn("Invalid tiles, must be at least 17: " + path);
         return false;
      }

      if (this.layer != null && this.layer != EnumWorldBlockLayer.SOLID) {
         return true;
      }

      Config.warn("Invalid overlay layer: " + this.layer);
      return false;
   }

   private static int parseSymmetry(String str) {
      if (str == null) {
         return 1;
      }

      str = str.trim();
      if (str.equals("opposite")) {
         return 2;
      }

      if (str.equals("all")) {
         return 6;
      }

      Config.warn("Unknown symmetry: " + str);
      return 1;
   }

   private int[] detectMatchBlockIds() {
      if (!this.name.startsWith("block")) {
         return null;
      }

      int count = "block".length();

      int i;
      for (i = count; i < this.name.length(); i++) {
         char c0 = this.name.charAt(i);
         if (c0 < '0' || c0 > '9') {
            break;
         }
      }

      if (i == count) {
         return null;
      }

      String text = this.name.substring(count, i);
      int i2 = Config.parseInt(text, -1);
      return i2 < 0 ? null : new int[]{i2};
   }

   private boolean isValidHorizontalVertical(String path) {
      if (this.tiles == null) {
         Config.warn("No tiles defined for horizontal+vertical: " + path);
         return false;
      } else if (this.tiles.length != 7) {
         Config.warn("Invalid tiles, must be exactly 7: " + path);
         return false;
      } else {
         return true;
      }
   }

   public boolean matchesIcon(TextureAtlasSprite icon) {
      return Matches.sprite(icon, this.matchTileIcons);
   }

   private boolean isValidFixed(String path) {
      if (this.tiles == null) {
         Config.warn("Tiles not defined: " + path);
         return false;
      } else if (this.tiles.length != 1) {
         Config.warn("Number of tiles should be 1 for method: fixed.");
         return false;
      } else {
         return true;
      }
   }

   private boolean isValidOverlayCtm(String path) {
      if (!this.isValidCtm(path)) {
         return false;
      }

      if (this.layer != null && this.layer != EnumWorldBlockLayer.SOLID) {
         return true;
      }

      Config.warn("Invalid overlay layer: " + this.layer);
      return false;
   }

   public void updateIcons(TextureMap textureMap) {
      if (this.matchTiles != null) {
         this.matchTileIcons = registerIcons(this.matchTiles, textureMap, false, false);
      }

      if (this.connectTiles != null) {
         this.connectTileIcons = registerIcons(this.connectTiles, textureMap, false, false);
      }

      if (this.tiles != null) {
         this.tileIcons = registerIcons(this.tiles, textureMap, true, !isMethodOverlay(this.method));
      }
   }

   private static String parseName(String path) {
      String text = path;
      int i = path.lastIndexOf(47);
      if (i >= 0) {
         text = path.substring(i + 1);
      }

      int i2 = text.lastIndexOf(46);
      if (i2 >= 0) {
         text = text.substring(0, i2);
      }

      return text;
   }

   public boolean isValid(String path) {
      if (this.name == null || this.name.length() <= 0) {
         Config.warn("No name found: " + path);
         return false;
      }

      if (this.basePath == null) {
         Config.warn("No base path found: " + path);
         return false;
      }

      if (this.matchBlocks == null) {
         this.matchBlocks = this.detectMatchBlocks();
      }

      if (this.matchTiles == null && this.matchBlocks == null) {
         this.matchTiles = this.detectMatchTiles();
      }

      if (this.matchBlocks == null && this.matchTiles == null) {
         Config.warn("No matchBlocks or matchTiles specified: " + path);
         return false;
      }

      if (this.method == 0) {
         Config.warn("No method: " + path);
         return false;
      }

      if (this.tiles != null && this.tiles.length > 0) {
         if (this.connect == 0) {
            this.connect = this.detectConnect();
         }

         if (this.connect == 128) {
            Config.warn("Invalid connect in: " + path);
            return false;
         }

         if (this.renderPass > 0) {
            Config.warn("Render pass not supported: " + this.renderPass);
            return false;
         }

         if ((this.faces & 128) != 0) {
            Config.warn("Invalid faces in: " + path);
            return false;
         }

         if ((this.symmetry & 128) != 0) {
            Config.warn("Invalid symmetry in: " + path);
            return false;
         }

         switch (this.method) {
            case 1:
               return this.isValidCtm(path);
            case 2:
               return this.isValidHorizontal(path);
            case 3:
               return this.isValidTop(path);
            case 4:
               return this.isValidRandom(path);
            case 5:
               return this.isValidRepeat(path);
            case 6:
               return this.isValidVertical(path);
            case 7:
               return this.isValidFixed(path);
            case 8:
               return this.isValidHorizontalVertical(path);
            case 9:
               return this.isValidVerticalHorizontal(path);
            case 10:
               return this.isValidCtmCompact(path);
            case 11:
               return this.isValidOverlay(path);
            case 12:
               return this.isValidOverlayFixed(path);
            case 13:
               return this.isValidOverlayRandom(path);
            case 14:
               return this.isValidOverlayRepeat(path);
            case 15:
               return this.isValidOverlayCtm(path);
            default:
               Config.warn("Unknown method: " + path);
               return false;
         }
      } else {
         Config.warn("No tiles specified: " + path);
         return false;
      }
   }

   private int getMax(int[] mds, int max) {
      if (mds == null) {
         return max;
      }

      for (int index = 0; index < mds.length; index++) {
         int i = mds[index];
         if (i > max) {
            max = i;
         }
      }

      return max;
   }
}
