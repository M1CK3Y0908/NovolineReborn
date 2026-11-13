package net.optifine.config;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.src.Config;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.ConnectedProperties;
import net.optifine.util.EntityUtils;

public class ConnectedParser {
   public static final VillagerProfession[] PROFESSIONS_INVALID = new VillagerProfession[0];
   private String context = null;
   public static final EnumDyeColor[] DYE_COLORS_INVALID = new EnumDyeColor[0];
   private static final INameGetter<Enum> NAME_GETTER_ENUM = new ConnectedParser.INameGetterImpl();
   private static final INameGetter<EnumDyeColor> NAME_GETTER_DYE_COLOR = new ConnectedParser.INameGetterImpl2();

   public void warn(String str) {
      Config.warn(this.context + ": " + str);
   }

   public String parseBasePath(String path) {
      int i = path.lastIndexOf(47);
      return i < 0 ? "" : path.substring(0, i);
   }

   public int[] parseItems(String str) {
      str = str.trim();
      TreeSet treeSet = new TreeSet();
      String[] stringArray = Config.tokenize(str, " ");

      for (int index = 0; index < stringArray.length; index++) {
         String intArray = stringArray[index];
         ResourceLocation resourceLocation = new ResourceLocation(intArray);
         Item item = Item.itemRegistry.getObject(resourceLocation);
         if (item == null) {
            this.warn("Item not found: " + intArray);
         } else {
            int idFromItem = Item.getIdFromItem(item);
            if (idFromItem < 0) {
               this.warn("Item has no ID: " + item + ", name: " + intArray);
            } else {
               treeSet.add(new Integer(idFromItem));
            }
         }
      }

      Integer[] integers = (Integer[]) treeSet.toArray(new Integer[treeSet.size()]);
      return Config.toPrimitive(integers);
   }

   public int[] parseEntities(String str) {
      str = str.trim();
      TreeSet treeSet = new TreeSet();
      String[] stringArray = Config.tokenize(str, " ");

      for (int index = 0; index < stringArray.length; index++) {
         String intArray = stringArray[index];
         int entityIdByName = EntityUtils.getEntityIdByName(intArray);
         if (entityIdByName < 0) {
            this.warn("Entity not found: " + intArray);
         } else {
            treeSet.add(new Integer(entityIdByName));
         }
      }

      Integer[] integers = (Integer[]) treeSet.toArray(new Integer[treeSet.size()]);
      return Config.toPrimitive(integers);
   }

   public boolean isFullBlockName(String[] parts) {
      if (parts.length < 2) {
         return false;
      }

      String text3 = parts[1];
      return text3.length() < 1 ? false : (this.startsWithDigit(text3) ? false : !text3.contains("="));
   }

   public <T> T parseObject(String str, T[] objs, INameGetter nameGetter, String property) {
      if (str == null) {
         return null;
      }

      String text = str.toLowerCase().trim();

      for (int index = 0; index < objs.length; index++) {
         Object object = objs[index];
         String text2 = nameGetter.getString(object);
         if (text2 != null && text2.toLowerCase().equals(text)) {
            return (T)object;
         }
      }

      this.warn("Invalid " + property + ": " + str);
      return null;
   }

   public BiomeGenBase findBiome(String biomeName) {
      biomeName = biomeName.toLowerCase();
      if (biomeName.equals("nether")) {
         return BiomeGenBase.hell;
      }

      BiomeGenBase[] biomeGenArray = BiomeGenBase.getBiomeGenArray();

      for (int i = 0; i < biomeGenArray.length; i++) {
         BiomeGenBase biomeGenBase2 = biomeGenArray[i];
         if (biomeGenBase2 != null && biomeGenBase2.biomeName.replace(" ", "").toLowerCase().equals(biomeName)) {
            return biomeGenBase2;
         }
      }

      return null;
   }

   public Block[] parseBlockPart(String domain, String blockPart) {
      if (this.startsWithDigit(blockPart)) {
         int[] intArray = this.parseIntList(blockPart);
         if (intArray == null) {
            return null;
         }

         Block[] ablock1 = new Block[intArray.length];

         for (int innerIndex = 0; innerIndex < intArray.length; innerIndex++) {
            int i = intArray[innerIndex];
            Block block2 = Block.getBlockById(i);
            if (block2 == null) {
               this.warn("Block not found for id: " + i);
               return null;
            }

            ablock1[innerIndex] = block2;
         }

         return ablock1;
      } else {
         String intArray = domain + ":" + blockPart;
         Block block = Block.getBlockFromName(intArray);
         if (block == null) {
            this.warn("Block not found for name: " + intArray);
            return null;
         } else {
            return new Block[]{block};
         }
      }
   }

   public EnumDyeColor[] parseDyeColors(String str, String property, EnumDyeColor[] errValue) {
      return this.parseObjects(str, EnumDyeColor.values(), NAME_GETTER_DYE_COLOR, property, errValue);
   }

   public Weather[] parseWeather(String str, String property, Weather[] errValue) {
      return this.parseObjects(str, Weather.values(), NAME_GETTER_ENUM, property, errValue);
   }

   private RangeInt parseRangeInt(String str) {
      if (str == null) {
         return null;
      }

      if (str.indexOf(45) >= 0) {
         String[] stringArray = Config.tokenize(str, "-");
         if (stringArray.length != 2) {
            this.warn("Invalid range: " + str);
            return null;
         }

         int i = Config.parseInt(stringArray[0], -1);
         int i2 = Config.parseInt(stringArray[1], -1);
         if (i >= 0 && i2 >= 0) {
            return new RangeInt(i, i2);
         }

         this.warn("Invalid range: " + str);
         return null;
      } else {
         int i3 = Config.parseInt(str, -1);
         if (i3 < 0) {
            this.warn("Invalid integer: " + str);
            return null;
         } else {
            return new RangeInt(i3, i3);
         }
      }
   }

   private static int parseProfessionId(String str) {
      int i = Config.parseInt(str, -1);
      return i >= 0
         ? i
         : (
            str.equals("farmer")
               ? 0
               : (
                  str.equals("librarian")
                     ? 1
                     : (str.equals("priest") ? 2 : (str.equals("blacksmith") ? 3 : (str.equals("butcher") ? 4 : (str.equals("nitwit") ? 5 : -1))))
               )
         );
   }

   public int[] parseIntList(String str) {
      if (str == null) {
         return null;
      }

      ArrayList arrayList = new ArrayList();
      String[] stringArray = Config.tokenize(str, " ,");

      for (int index = 0; index < stringArray.length; index++) {
         String innerIndex = stringArray[index];
         if (innerIndex.contains("-")) {
            String[] stringArray1 = Config.tokenize(innerIndex, "-");
            if (stringArray1.length != 2) {
               this.warn("Invalid interval: " + innerIndex + ", when parsing: " + str);
            } else {
               int i = Config.parseInt(stringArray1[0], -1);
               int i2 = Config.parseInt(stringArray1[1], -1);
               if (i >= 0 && i2 >= 0 && i <= i2) {
                  for (int index2 = i; index2 <= i2; index2++) {
                     arrayList.add(index2);
                  }
               } else {
                  this.warn("Invalid interval: " + innerIndex + ", when parsing: " + str);
               }
            }
         } else {
            int i3 = Config.parseInt(innerIndex, -1);
            if (i3 < 0) {
               this.warn("Invalid number: " + innerIndex + ", when parsing: " + str);
            } else {
               arrayList.add(i3);
            }
         }
      }

      int[] intArray = new int[arrayList.size()];

      for (int innerIndex = 0; innerIndex < intArray.length; innerIndex++) {
         intArray[innerIndex] = (Integer)arrayList.get(innerIndex);
      }

      return intArray;
   }

   public boolean matchState(IBlockState bs, Map<IProperty, List<Comparable>> mapPropValues) {
      for (IProperty iProperty : mapPropValues.keySet()) {
         List list = mapPropValues.get(iProperty);
         Comparable value2 = bs.getValue(iProperty);
         if (value2 == null) {
            return false;
         }

         if (!list.contains(value2)) {
            return false;
         }
      }

      return true;
   }

   public Enum[] parseEnums(String str, Enum[] enums, String property, Enum[] errValue) {
      return this.parseObjects(str, enums, NAME_GETTER_ENUM, property, errValue);
   }

   public BiomeGenBase[] parseBiomes(String str) {
      if (str == null) {
         return null;
      }

      str = str.trim();
      boolean conditionMet = false;
      if (str.startsWith("!")) {
         conditionMet = true;
         str = str.substring(1);
      }

      String[] stringArray = Config.tokenize(str, " ");
      ArrayList arrayList = new ArrayList();

      for (int index = 0; index < stringArray.length; index++) {
         String text3 = stringArray[index];
         BiomeGenBase biomeGenBase = this.findBiome(text3);
         if (biomeGenBase == null) {
            this.warn("Biome not found: " + text3);
         } else {
            arrayList.add(biomeGenBase);
         }
      }

      if (conditionMet) {
         ArrayList arrayList2 = new ArrayList<>(Arrays.asList(BiomeGenBase.getBiomeGenArray()));
         arrayList2.removeAll(arrayList);
         arrayList = arrayList2;
      }

      return (BiomeGenBase[]) arrayList.toArray(new BiomeGenBase[arrayList.size()]);
   }

   private static Object getValueName(Comparable obj) {
      return obj instanceof IStringSerializable ? ((IStringSerializable)obj).getName() : obj.toString();
   }

   public int[] parseBlockMetadatas(Block block, String[] params) {
      if (params.length <= 0) {
         return null;
      }

      String text3 = params[0];
      if (this.startsWithDigit(text3)) {
         return this.parseIntList(text3);
      }

      Collection propertyNames = block.getDefaultState().getPropertyNames();
      HashMap hashMap = new HashMap();

      for (int i = 0; i < params.length; i++) {
         String text4 = params[i];
         if (text4.length() > 0) {
            String[] texts = Config.tokenize(text4, "=");
            if (texts.length != 2) {
               this.warn("Invalid block property: " + text4);
               return null;
            }

            String text5 = texts[0];
            String text6 = texts[1];
            IProperty property2 = ConnectedProperties.getProperty(text5, propertyNames);
            if (property2 == null) {
               this.warn("Property not found: " + text5 + ", block: " + block);
               return null;
            }

            List list = (List)hashMap.get(text5);
            if (list == null) {
               list = new ArrayList();
               hashMap.put(property2, list);
            }

            String[] texts2 = Config.tokenize(text6, ",");

            for (int i2 = 0; i2 < texts2.length; i2++) {
               String text7 = texts2[i2];
               Comparable comparable2 = parsePropertyValue(property2, text7);
               if (comparable2 == null) {
                  this.warn("Property value not found: " + text7 + ", property: " + text5 + ", block: " + block);
                  return null;
               }

               list.add(comparable2);
            }
         }
      }

      if (hashMap.isEmpty()) {
         return null;
      }

      ArrayList arrayList = new ArrayList();

      for (int i3 = 0; i3 < 16; i3++) {
         int i4 = i3;

         try {
            IBlockState stateFromMeta = this.getStateFromMeta(block, i4);
            if (this.matchState(stateFromMeta, hashMap)) {
               arrayList.add(i4);
            }
         } catch (IllegalArgumentException illegalArgumentException) {
         }
      }

      if (arrayList.size() == 16) {
         return null;
      }

      int[] iArray = new int[arrayList.size()];

      for (int i5 = 0; i5 < iArray.length; i5++) {
         iArray[i5] = (Integer)arrayList.get(i5);
      }

      return iArray;
   }

   public int parseInt(String str, int defVal) {
      if (str == null) {
         return defVal;
      } else {
         str = str.trim();
         int i = Config.parseInt(str, -1);
         if (i < 0) {
            this.warn("Invalid number: " + str);
            return defVal;
         } else {
            return i;
         }
      }
   }

   public MatchBlock[] parseMatchBlock(String blockStr) {
      if (blockStr == null) {
         return null;
      }

      blockStr = blockStr.trim();
      if (blockStr.length() <= 0) {
         return null;
      }

      String[] stringArray = Config.tokenize(blockStr, ":");
      String message;
      int i;
      if (stringArray.length > 1 && this.isFullBlockName(stringArray)) {
         message = stringArray[0];
         i = 1;
      } else {
         message = "minecraft";
         i = 0;
      }

      String text3 = stringArray[i];
      String[] texts = Arrays.copyOfRange(stringArray, i + 1, stringArray.length);
      Block[] ablock = this.parseBlockPart(message, text3);
      if (ablock == null) {
         return null;
      }

      MatchBlock[] amatchblock = new MatchBlock[ablock.length];

      for (int innerIndex = 0; innerIndex < ablock.length; innerIndex++) {
         Block block3 = ablock[innerIndex];
         int idFromBlock = Block.getIdFromBlock(block3);
         int[] intArray = null;
         if (texts.length > 0) {
            intArray = this.parseBlockMetadatas(block3, texts);
            if (intArray == null) {
               return null;
            }
         }

         MatchBlock matchBlock = new MatchBlock(idFromBlock, intArray);
         amatchblock[innerIndex] = matchBlock;
      }

      return amatchblock;
   }

   public static Comparable parseValue(String str, Class<?> cls) {
      return (Comparable)(
         cls == String.class
            ? str
            : (
               cls == Boolean.class
                  ? Boolean.valueOf(str)
                  : cls == Float.class
                     ? Float.valueOf(str).floatValue()
                     : (
                        cls == Double.class
                           ? Double.valueOf(str)
                           : (cls == Integer.class ? Integer.valueOf(str).intValue() : cls == Long.class ? Long.valueOf(str) : null)
                     )
            )
      );
   }

   public static int createInt(String text3) {
      if (text3 == null) {
         return -1;
      }

      text3 = text3.trim();

      try {
         return Integer.parseInt(text3, 16) & 16777215;
      } catch (NumberFormatException numberFormatException) {
         return -1;
      }
   }

   public IBlockState parseBlockState(String str, IBlockState def) {
      MatchBlock[] matchBlocks = this.parseMatchBlock(str);
      if (matchBlocks == null) {
         return def;
      } else {
         return matchBlocks.length != 1 ? def : Block.getBlockById(matchBlocks[0].getBlockId()).getDefaultState();
      }
   }

   public boolean startsWithDigit(String str) {
      if (str == null) {
         return false;
      } else {
         return str.length() < 1 ? false : Character.isDigit(str.charAt(0));
      }
   }

   public static int parseColor4(String str, int defVal) {
      if (str == null) {
         return defVal;
      }

      str = str.trim();

      try {
         return (int)(Long.parseLong(str, 16) & -1L);
      } catch (NumberFormatException numberFormatException) {
         return defVal;
      }
   }

   private VillagerProfession parseProfession(String str) {
      str = str.toLowerCase();
      String[] stringArray = Config.tokenize(str, ":");
      if (stringArray.length > 2) {
         return null;
      }

      String text3 = stringArray[0];
      String text4 = null;
      if (stringArray.length > 1) {
         text4 = stringArray[1];
      }

      int i = parseProfessionId(text3);
      if (i < 0) {
         return null;
      }

      int[] intArray = null;
      if (text4 != null) {
         intArray = parseCareerIds(i, text4);
         if (intArray == null) {
            return null;
         }
      }

      return new VillagerProfession(i, intArray);
   }

   public Boolean parseBooleanObject(String str) {
      if (str == null) {
         return null;
      }

      String text = str.toLowerCase().trim();
      if (text.equals("true")) {
         return Boolean.TRUE;
      }

      if (text.equals("false")) {
         return Boolean.FALSE;
      }

      this.warn("Invalid boolean: " + str);
      return null;
   }

   public ConnectedParser(String context) {
      this.context = context;
   }

   private IBlockState getStateFromMeta(Block block, int md) {
      try {
         IBlockState stateFromMeta = block.getStateFromMeta(md);
         if (block == Blocks.double_plant && md > 7) {
            IBlockState stateFromMeta2 = block.getStateFromMeta(md & 7);
            stateFromMeta = stateFromMeta.withProperty(BlockDoublePlant.VARIANT, stateFromMeta2.getValue(BlockDoublePlant.VARIANT));
         }

         return stateFromMeta;
      } catch (IllegalArgumentException illegalArgumentException) {
         return block.getDefaultState();
      }
   }

   public EnumWorldBlockLayer parseBlockRenderLayer(String str, EnumWorldBlockLayer def) {
      if (str == null) {
         return def;
      }

      str = str.toLowerCase().trim();
      EnumWorldBlockLayer[] worldBlockLayerArray = EnumWorldBlockLayer.values();

      for (int index = 0; index < worldBlockLayerArray.length; index++) {
         EnumWorldBlockLayer enumWorldBlockLayer = worldBlockLayerArray[index];
         if (str.equals(enumWorldBlockLayer.name().toLowerCase())) {
            return enumWorldBlockLayer;
         }
      }

      return def;
   }

   private static int[] parseCareerIds(int prof, String str) {
      HashSet hashSet = new HashSet();
      String[] stringArray = Config.tokenize(str, ",");

      for (int index = 0; index < stringArray.length; index++) {
         String intArray = stringArray[index];
         int i = parseCareerId(prof, intArray);
         if (i < 0) {
            return null;
         }

         hashSet.add(i);
      }

      Integer[] integers = (Integer[]) hashSet.toArray(new Integer[hashSet.size()]);
      int[] intArray = new int[integers.length];

      for (int nestedIndex = 0; nestedIndex < intArray.length; nestedIndex++) {
         intArray[nestedIndex] = integers[nestedIndex];
      }

      return intArray;
   }

   public <T> T[] parseObjects(String str, T[] objs, INameGetter nameGetter, String property, T[] errValue) {
      if (str == null) {
         return null;
      }

      str = str.toLowerCase().trim();
      String[] stringArray = Config.tokenize(str, " ");
      Object[] objects = (Object[])Array.newInstance(objs.getClass().getComponentType(), stringArray.length);

      for (int index = 0; index < stringArray.length; index++) {
         String text3 = stringArray[index];
         T t = this.parseObject(text3, objs, nameGetter, property);
         if (t == null) {
            return errValue;
         }

         objects[index] = t;
      }

      return (T[])objects;
   }

   public NbtTagValue parseNbtTagValue(String path, String value) {
      return path != null && value != null ? new NbtTagValue(path, value) : null;
   }

   public static Comparable getPropertyValue(String value, Collection propertyValues) {
      for (Comparable comparable : (Iterable<Comparable>) propertyValues) {
         if (getValueName(comparable).equals(value)) {
            return comparable;
         }
      }

      return null;
   }

   private static int parseCareerId(int prof, String str) {
      int i = Config.parseInt(str, -1);
      if (i >= 0) {
         return i;
      }

      if (prof == 0) {
         if (str.equals("farmer")) {
            return 1;
         }

         if (str.equals("fisherman")) {
            return 2;
         }

         if (str.equals("shepherd")) {
            return 3;
         }

         if (str.equals("fletcher")) {
            return 4;
         }
      }

      if (prof == 1) {
         if (str.equals("librarian")) {
            return 1;
         }

         if (str.equals("cartographer")) {
            return 2;
         }
      }

      if (prof == 2 && str.equals("cleric")) {
         return 1;
      }

      if (prof == 3) {
         if (str.equals("armor")) {
            return 1;
         }

         if (str.equals("weapon")) {
            return 2;
         }

         if (str.equals("tool")) {
            return 3;
         }
      }

      if (prof == 4) {
         if (str.equals("butcher")) {
            return 1;
         }

         if (str.equals("leather")) {
            return 2;
         }
      }

      return prof == 5 && str.equals("nitwit") ? 1 : -1;
   }

   public boolean checkString(String text3) {
      if (text3 == null) {
         return false;
      }

      String text4 = text3.toLowerCase().trim();
      if (text4.equals("true")) {
         return true;
      }

      if (text4.equals("false")) {
         return false;
      }

      this.warn("Invalid boolean: " + text3);
      return false;
   }

   public String parseName(String path) {
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

   public Enum parseEnum(String str, Enum[] enums, String property) {
      return this.parseObject(str, enums, NAME_GETTER_ENUM, property);
   }

   public static Comparable parsePropertyValue(IProperty prop, String valStr) {
      Class oclass = prop.getValueClass();
      Comparable comparable = parseValue(valStr, oclass);
      if (comparable == null) {
         Collection collection = prop.getAllowedValues();
         comparable = getPropertyValue(valStr, collection);
      }

      return comparable;
   }

   public RangeListInt parseRangeListInt(String str) {
      if (str == null) {
         return null;
      }

      RangeListInt rangeListInt = new RangeListInt();
      String[] stringArray = Config.tokenize(str, " ,");

      for (int index = 0; index < stringArray.length; index++) {
         String text3 = stringArray[index];
         RangeInt rangeInt = this.parseRangeInt(text3);
         if (rangeInt == null) {
            return null;
         }

         rangeListInt.addRange(rangeInt);
      }

      return rangeListInt;
   }

   public VillagerProfession[] parseProfessions(String profStr) {
      if (profStr == null) {
         return null;
      }

      ArrayList arrayList = new ArrayList();
      String[] stringArray = Config.tokenize(profStr, " ");

      for (int index = 0; index < stringArray.length; index++) {
         String text3 = stringArray[index];
         VillagerProfession villagerProfession = this.parseProfession(text3);
         if (villagerProfession == null) {
            this.warn("Invalid profession: " + text3);
            return PROFESSIONS_INVALID;
         }

         arrayList.add(villagerProfession);
      }

      return arrayList.isEmpty() ? null : (VillagerProfession[]) arrayList.toArray(new VillagerProfession[arrayList.size()]);
   }

   public MatchBlock[] parseMatchBlocks(String propMatchBlocks) {
      if (propMatchBlocks == null) {
         return null;
      }

      ArrayList arrayList = new ArrayList();
      String[] stringArray = Config.tokenize(propMatchBlocks, " ");

      for (int index = 0; index < stringArray.length; index++) {
         String text3 = stringArray[index];
         MatchBlock[] amatchblock = this.parseMatchBlock(text3);
         if (amatchblock != null) {
            arrayList.addAll(Arrays.asList(amatchblock));
         }
      }

      return (MatchBlock[]) arrayList.toArray(new MatchBlock[arrayList.size()]);
   }

   static class INameGetterImpl implements INameGetter<Enum> {
      public String getString(Enum enumValue) {
         return enumValue.name();
      }
   }

   static class INameGetterImpl2 implements INameGetter<EnumDyeColor> {
      public String getString(EnumDyeColor enumDyeColor) {
         return enumDyeColor.getName();
      }
   }
}
