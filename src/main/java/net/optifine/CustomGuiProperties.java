package net.optifine;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.config.ConnectedParser;
import net.optifine.config.Matches;
import net.optifine.config.NbtTagValue;
import net.optifine.config.RangeListInt;
import net.optifine.config.VillagerProfession;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorField;
import net.optifine.util.StrUtils;
import net.optifine.util.TextureUtils;
import java.util.Set;

public class CustomGuiProperties {
   private String fileName = null;
   private static final CustomGuiProperties.EnumVariant[] VARIANTS_HORSE = new CustomGuiProperties.EnumVariant[]{
      CustomGuiProperties.EnumVariant.HORSE,
      CustomGuiProperties.EnumVariant.DONKEY,
      CustomGuiProperties.EnumVariant.MULE,
      CustomGuiProperties.EnumVariant.LLAMA
   };
   private static final CustomGuiProperties.EnumVariant[] VARIANTS_DISPENSER = new CustomGuiProperties.EnumVariant[]{
      CustomGuiProperties.EnumVariant.DISPENSER, CustomGuiProperties.EnumVariant.DROPPER
   };
   private static final CustomGuiProperties.EnumVariant[] VARIANTS_INVALID = new CustomGuiProperties.EnumVariant[0];
   private String basePath = null;
   private static final EnumDyeColor[] COLORS_INVALID = new EnumDyeColor[0];
   private static final ResourceLocation ANVIL_GUI_TEXTURE = new ResourceLocation("textures/gui/container/anvil.png");
   private CustomGuiProperties.EnumContainer container = null;
   private Map<ResourceLocation, ResourceLocation> textureLocations = null;
   private static final ResourceLocation BEACON_GUI_TEXTURE = new ResourceLocation("textures/gui/container/beacon.png");
   private static final ResourceLocation BREWING_STAND_GUI_TEXTURE = new ResourceLocation("textures/gui/container/brewing_stand.png");
   private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
   private NbtTagValue nbtName = null;
   private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/crafting_table.png");
   private static final ResourceLocation HORSE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/horse.png");
   private static final ResourceLocation DISPENSER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/dispenser.png");
   private BiomeGenBase[] biomes = null;
   private RangeListInt heights = null;
   private static final ResourceLocation ENCHANTMENT_TABLE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/enchanting_table.png");
   private static final ResourceLocation FURNACE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/furnace.png");
   private Boolean large = null;
   private Boolean trapped = null;
   private Boolean christmas = null;
   private Boolean ender = null;
   private RangeListInt levels = null;
   private VillagerProfession[] professions = null;
   private static final ResourceLocation HOPPER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/hopper.png");
   private CustomGuiProperties.EnumVariant[] variants = null;
   private static final ResourceLocation INVENTORY_GUI_TEXTURE = new ResourceLocation("textures/gui/container/inventory.png");
   private static final ResourceLocation SHULKER_BOX_GUI_TEXTURE = new ResourceLocation("textures/gui/container/shulker_box.png");
   private EnumDyeColor[] colors = null;
   private static final ResourceLocation VILLAGER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/villager.png");

   private static EnumDyeColor[] parseEnumDyeColors(String str) {
      if (str == null) {
         return null;
      }

      str = str.toLowerCase();
      String[] stringArray = Config.tokenize(str, " ");
      EnumDyeColor[] aenumdyecolor = new EnumDyeColor[stringArray.length];

      for (int index = 0; index < stringArray.length; index++) {
         String text2 = stringArray[index];
         EnumDyeColor dyeColor = parseEnumDyeColor(text2);
         if (dyeColor == null) {
            warn("Invalid color: " + text2);
            return COLORS_INVALID;
         }

         aenumdyecolor[index] = dyeColor;
      }

      return aenumdyecolor;
   }

   public boolean matchesPos(CustomGuiProperties.EnumContainer ec, BlockPos pos, IBlockAccess blockAccess, GuiScreen screen) {
      if (!this.matchesGeneral(ec, pos, blockAccess)) {
         return false;
      }

      if (this.nbtName != null) {
         String text = getName(screen);
         if (!this.nbtName.matchesValue(text)) {
            return false;
         }
      }

      switch (ec) {
         case BEACON:
            return this.matchesBeacon(pos, blockAccess);
         case CHEST:
            return this.matchesChest(pos, blockAccess);
         case DISPENSER:
            return this.matchesDispenser(pos, blockAccess);
         default:
            return true;
      }
   }

   private boolean matchesHorse(Entity entity, IBlockAccess blockAccess) {
      return !(entity instanceof EntityHorse entityHorse) ? false : this.variants == null || Config.equalsOne(this.getHorseVariant(entityHorse), this.variants);
   }

   private static void warn(String str) {
      Config.warn("[CustomGuis] " + str);
   }

   private static Map<ResourceLocation, ResourceLocation> parseTextureLocations(
      Properties props, String property, CustomGuiProperties.EnumContainer container, String pathPrefix, String basePath
   ) {
      HashMap hashMap = new HashMap();
      String property2 = props.getProperty(property);
      if (property2 != null) {
         ResourceLocation guiTextureLocation = getGuiTextureLocation(container);
         ResourceLocation resourceLocation2 = parseTextureLocation(property2, basePath);
         if (guiTextureLocation != null && resourceLocation2 != null) {
            hashMap.put(guiTextureLocation, resourceLocation2);
         }
      }

      String text2 = property + ".";

      for (String text3 : (Iterable<String>) (Iterable<String>) (Set) (props.keySet())) {
         if (text3.startsWith(text2)) {
            String text4 = text3.substring(text2.length());
            text4 = text4.replace('\\', '/');
            text4 = StrUtils.removePrefixSuffix(text4, "/", ".png");
            String text5 = pathPrefix + text4 + ".png";
            String property3 = props.getProperty(text3);
            ResourceLocation resourceLocation3 = new ResourceLocation(text5);
            ResourceLocation resourceLocation4 = parseTextureLocation(property3, basePath);
            hashMap.put(resourceLocation3, resourceLocation4);
         }
      }

      return hashMap;
   }

   public ResourceLocation getTextureLocation(ResourceLocation loc) {
      ResourceLocation resourceLocation = this.textureLocations.get(loc);
      return resourceLocation == null ? loc : resourceLocation;
   }

   private boolean matchesGeneral(CustomGuiProperties.EnumContainer ec, BlockPos pos, IBlockAccess blockAccess) {
      if (this.container != ec) {
         return false;
      } else {
         return this.biomes != null && !Matches.biome(blockAccess.getBiomeGenForCoords(pos), this.biomes)
            ? false
            : this.heights == null || this.heights.isInRange(pos.getY());
      }
   }

   public CustomGuiProperties(Properties props, String path) {
      ConnectedParser connectedParser = new ConnectedParser("CustomGuis");
      this.fileName = connectedParser.parseName(path);
      this.basePath = connectedParser.parseBasePath(path);
      this.container = (CustomGuiProperties.EnumContainer)connectedParser.parseEnum(
         props.getProperty("container"), CustomGuiProperties.EnumContainer.values(), "container"
      );
      this.textureLocations = parseTextureLocations(props, "texture", this.container, "textures/gui/", this.basePath);
      this.nbtName = connectedParser.parseNbtTagValue("name", props.getProperty("name"));
      this.biomes = connectedParser.parseBiomes(props.getProperty("biomes"));
      this.heights = connectedParser.parseRangeListInt(props.getProperty("heights"));
      this.large = connectedParser.parseBooleanObject(props.getProperty("large"));
      this.trapped = connectedParser.parseBooleanObject(props.getProperty("trapped"));
      this.christmas = connectedParser.parseBooleanObject(props.getProperty("christmas"));
      this.ender = connectedParser.parseBooleanObject(props.getProperty("ender"));
      this.levels = connectedParser.parseRangeListInt(props.getProperty("levels"));
      this.professions = connectedParser.parseProfessions(props.getProperty("professions"));
      CustomGuiProperties.EnumVariant[] acustomguiproperties$enumvariant = getContainerVariants(this.container);
      this.variants = (CustomGuiProperties.EnumVariant[])connectedParser.parseEnums(
         props.getProperty("variants"), acustomguiproperties$enumvariant, "variants", VARIANTS_INVALID
      );
      this.colors = parseEnumDyeColors(props.getProperty("colors"));
   }

   private boolean matchesChest(BlockPos pos, IBlockAccess blockAccess) {
      TileEntity tileEntity = blockAccess.getTileEntity(pos);
      if (tileEntity instanceof TileEntityChest tileEntityChest) {
         return this.matchesChest(tileEntityChest, pos, blockAccess);
      } else {
         return tileEntity instanceof TileEntityEnderChest tileEntityEnderChest ? this.matchesEnderChest(tileEntityEnderChest, pos, blockAccess) : false;
      }
   }

   private boolean matchesDispenser(BlockPos pos, IBlockAccess blockAccess) {
      return !(blockAccess.getTileEntity(pos) instanceof TileEntityDispenser tileEntityDispenser)
         ? false
         : this.variants == null || Config.equalsOne(this.getDispenserVariant(tileEntityDispenser), this.variants);
   }

   @Override
   public String toString() {
      return "name: " + this.fileName + ", container: " + this.container + ", textures: " + this.textureLocations;
   }

   public static String getName(GuiScreen screen) {
      IWorldNameable worldNameable = getWorldNameable(screen);
      return worldNameable == null ? null : worldNameable.getDisplayName().getUnformattedText();
   }

   private boolean matchesEnderChest(TileEntityEnderChest teec, BlockPos pos, IBlockAccess blockAccess) {
      return this.matchesChest(false, false, false, true);
   }

   private boolean matchesChest(TileEntityChest tec, BlockPos pos, IBlockAccess blockAccess) {
      boolean flag = tec.adjacentChestXNeg != null || tec.adjacentChestXPos != null || tec.adjacentChestZNeg != null || tec.adjacentChestZPos != null;
      boolean flag2 = tec.getChestType() == 1;
      boolean isChristmas2 = CustomGuis.isChristmas;
      return this.matchesChest(flag, flag2, isChristmas2, false);
   }

   private CustomGuiProperties.EnumVariant getDispenserVariant(TileEntityDispenser ted) {
      return ted instanceof TileEntityDropper ? CustomGuiProperties.EnumVariant.DROPPER : CustomGuiProperties.EnumVariant.DISPENSER;
   }

   private static IWorldNameable createIWorldNameable(GuiScreen guiScreen, ReflectorField reflectorField) {
      Object object = Reflector.createObject6(guiScreen, reflectorField);
      return !(object instanceof IWorldNameable) ? null : (IWorldNameable)object;
   }

   private static IWorldNameable getWorldNameable(GuiScreen screen) {
      return screen instanceof GuiBeacon
         ? createIWorldNameable(screen, Reflector.reflectorField25)
         : (
            screen instanceof GuiBrewingStand
               ? createIWorldNameable(screen, Reflector.reflectorField26)
               : (
                  screen instanceof GuiChest
                     ? createIWorldNameable(screen, Reflector.reflectorField27)
                     : (
                        screen instanceof GuiDispenser
                           ? ((GuiDispenser)screen).dispenserInventory
                           : (
                              screen instanceof GuiEnchantment
                                 ? createIWorldNameable(screen, Reflector.reflectorField28)
                                 : (
                                    screen instanceof GuiFurnace
                                       ? createIWorldNameable(screen, Reflector.reflectorField29)
                                       : (screen instanceof GuiHopper ? createIWorldNameable(screen, Reflector.reflectorField30) : null)
                                 )
                           )
                     )
               )
         );
   }

   private static ResourceLocation parseTextureLocation(String str, String basePath) {
      if (str == null) {
         return null;
      }

      str = str.trim();
      String text = TextureUtils.fixResourcePath(str, basePath);
      if (!text.endsWith(".png")) {
         text = text + ".png";
      }

      return new ResourceLocation(basePath + "/" + text);
   }

   private CustomGuiProperties.EnumVariant getHorseVariant(EntityHorse entity) {
      switch (entity.getHorseType()) {
         case 0:
            return CustomGuiProperties.EnumVariant.HORSE;
         case 1:
            return CustomGuiProperties.EnumVariant.DONKEY;
         case 2:
            return CustomGuiProperties.EnumVariant.MULE;
         default:
            return null;
      }
   }

   private static CustomGuiProperties.EnumVariant[] getContainerVariants(CustomGuiProperties.EnumContainer cont) {
      return cont == CustomGuiProperties.EnumContainer.HORSE
         ? VARIANTS_HORSE
         : (cont == CustomGuiProperties.EnumContainer.DISPENSER ? VARIANTS_DISPENSER : new CustomGuiProperties.EnumVariant[0]);
   }

   public boolean matchesEntity(CustomGuiProperties.EnumContainer ec, Entity entity, IBlockAccess blockAccess) {
      if (!this.matchesGeneral(ec, entity.getPosition(), blockAccess)) {
         return false;
      }

      if (this.nbtName != null) {
         String name = entity.getName();
         if (!this.nbtName.matchesValue(name)) {
            return false;
         }
      }

      switch (ec) {
         case HORSE:
            return this.matchesHorse(entity, blockAccess);
         case VILLAGER:
            return this.matchesVillager(entity, blockAccess);
         default:
            return true;
      }
   }

   public boolean isValid(String path) {
      if (this.fileName == null || this.fileName.length() <= 0) {
         warn("No name found: " + path);
         return false;
      } else if (this.basePath == null) {
         warn("No base path found: " + path);
         return false;
      } else if (this.container == null) {
         warn("No container found: " + path);
         return false;
      } else if (this.textureLocations.isEmpty()) {
         warn("No texture found: " + path);
         return false;
      } else if (this.professions == ConnectedParser.PROFESSIONS_INVALID) {
         warn("Invalid professions or careers: " + path);
         return false;
      } else if (this.variants == VARIANTS_INVALID) {
         warn("Invalid variants: " + path);
         return false;
      } else if (this.colors == COLORS_INVALID) {
         warn("Invalid colors: " + path);
         return false;
      } else {
         return true;
      }
   }

   private static ResourceLocation getGuiTextureLocation(CustomGuiProperties.EnumContainer container) {
      if (container == null) {
         return null;
      }

      switch (container) {
         case ANVIL:
            return ANVIL_GUI_TEXTURE;
         case BEACON:
            return BEACON_GUI_TEXTURE;
         case BREWING_STAND:
            return BREWING_STAND_GUI_TEXTURE;
         case CHEST:
            return CHEST_GUI_TEXTURE;
         case CRAFTING:
            return CRAFTING_TABLE_GUI_TEXTURE;
         case CREATIVE:
            return null;
         case DISPENSER:
            return DISPENSER_GUI_TEXTURE;
         case ENCHANTMENT:
            return ENCHANTMENT_TABLE_GUI_TEXTURE;
         case FURNACE:
            return FURNACE_GUI_TEXTURE;
         case HOPPER:
            return HOPPER_GUI_TEXTURE;
         case HORSE:
            return HORSE_GUI_TEXTURE;
         case INVENTORY:
            return INVENTORY_GUI_TEXTURE;
         case SHULKER_BOX:
            return SHULKER_BOX_GUI_TEXTURE;
         case VILLAGER:
            return VILLAGER_GUI_TEXTURE;
         default:
            return null;
      }
   }

   private static EnumDyeColor parseEnumDyeColor(String str) {
      if (str == null) {
         return null;
      }

      EnumDyeColor[] aenumdyecolor = EnumDyeColor.values();

      for (int index = 0; index < aenumdyecolor.length; index++) {
         EnumDyeColor enumDyeColor = aenumdyecolor[index];
         if (enumDyeColor.getName().equals(str)) {
            return enumDyeColor;
         }

         if (enumDyeColor.getUnlocalizedName().equals(str)) {
            return enumDyeColor;
         }
      }

      return null;
   }

   public CustomGuiProperties.EnumContainer getContainer() {
      return this.container;
   }

   private boolean matchesChest(boolean isLarge, boolean isTrapped, boolean isChristmas, boolean isEnder) {
      return this.large != null && this.large != isLarge
         ? false
         : (
            this.trapped != null && this.trapped != isTrapped
               ? false
               : (this.christmas != null && this.christmas != isChristmas ? false : this.ender == null || this.ender == isEnder)
         );
   }

   private boolean matchesBeacon(BlockPos pos, IBlockAccess blockAccess) {
      if (!(blockAccess.getTileEntity(pos) instanceof TileEntityBeacon tileEntityBeacon)) {
         return false;
      } else {
         if (this.levels != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            tileEntityBeacon.writeToNBT(nbtTagCompound);
            int integer = nbtTagCompound.getInteger("Levels");
            if (!this.levels.isInRange(integer)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean matchesVillager(Entity entity, IBlockAccess blockAccess) {
      if (!(entity instanceof EntityVillager entityVillager)) {
         return false;
      } else {
         if (this.professions != null) {
            int profession = entityVillager.getProfession();
            int intValue = Reflector.createInt(entityVillager, Reflector.reflectorField23, -1);
            if (intValue < 0) {
               return false;
            }

            boolean flag = false;

            for (int i = 0; i < this.professions.length; i++) {
               if (this.professions[i].matches(profession, intValue)) {
                  flag = true;
                  break;
               }
            }

            if (!flag) {
               return false;
            }
         }

         return true;
      }
   }

   public enum EnumContainer {
      ANVIL,
      BEACON,
      BREWING_STAND,
      CHEST,
      CRAFTING,
      DISPENSER,
      ENCHANTMENT,
      FURNACE,
      HOPPER,
      HORSE,
      VILLAGER,
      SHULKER_BOX,
      CREATIVE,
      INVENTORY;

      public static final CustomGuiProperties.EnumContainer[] VALUES = values();
   }

   private enum EnumVariant {
      HORSE,
      DONKEY,
      MULE,
      LLAMA,
      DISPENSER,
      DROPPER;
   }
}
