package net.optifine;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.entity.Entity;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.optifine.override.PlayerControllerOF;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;

public class CustomGuis {
   private static Minecraft mc = Config.getMinecraft();
   private static PlayerControllerOF playerControllerOF = null;
   private static CustomGuiProperties[][] guiProperties = (CustomGuiProperties[][])null;
   public static boolean isChristmas = isChristmas();

   public static ResourceLocation getTextureLocation(ResourceLocation loc) {
      if (guiProperties == null) {
         return loc;
      }

      GuiScreen guiScreen = mc.currentScreen;
      if (!(guiScreen instanceof GuiContainer)) {
         return loc;
      }

      if (!loc.getResourceDomain().equals("minecraft") || !loc.getResourcePath().startsWith("textures/gui/")) {
         return loc;
      }

      if (playerControllerOF == null) {
         return loc;
      }

      IBlockAccess blockAccess = mc.theWorld;
      if (blockAccess == null) {
         return loc;
      }

      if (guiScreen instanceof GuiContainerCreative) {
         return getTexturePos(CustomGuiProperties.EnumContainer.CREATIVE, mc.thePlayer.getPosition(), blockAccess, loc, guiScreen);
      }

      if (guiScreen instanceof GuiInventory) {
         return getTexturePos(CustomGuiProperties.EnumContainer.INVENTORY, mc.thePlayer.getPosition(), blockAccess, loc, guiScreen);
      }

      BlockPos blockPos = playerControllerOF.getLastClickBlockPos();
      if (blockPos != null) {
         if (guiScreen instanceof GuiRepair) {
            return getTexturePos(CustomGuiProperties.EnumContainer.ANVIL, blockPos, blockAccess, loc, guiScreen);
         }

         if (guiScreen instanceof GuiBeacon) {
            return getTexturePos(CustomGuiProperties.EnumContainer.BEACON, blockPos, blockAccess, loc, guiScreen);
         }

         if (guiScreen instanceof GuiBrewingStand) {
            return getTexturePos(CustomGuiProperties.EnumContainer.BREWING_STAND, blockPos, blockAccess, loc, guiScreen);
         }

         if (guiScreen instanceof GuiChest) {
            return getTexturePos(CustomGuiProperties.EnumContainer.CHEST, blockPos, blockAccess, loc, guiScreen);
         }

         if (guiScreen instanceof GuiCrafting) {
            return getTexturePos(CustomGuiProperties.EnumContainer.CRAFTING, blockPos, blockAccess, loc, guiScreen);
         }

         if (guiScreen instanceof GuiDispenser) {
            return getTexturePos(CustomGuiProperties.EnumContainer.DISPENSER, blockPos, blockAccess, loc, guiScreen);
         }

         if (guiScreen instanceof GuiEnchantment) {
            return getTexturePos(CustomGuiProperties.EnumContainer.ENCHANTMENT, blockPos, blockAccess, loc, guiScreen);
         }

         if (guiScreen instanceof GuiFurnace) {
            return getTexturePos(CustomGuiProperties.EnumContainer.FURNACE, blockPos, blockAccess, loc, guiScreen);
         }

         if (guiScreen instanceof GuiHopper) {
            return getTexturePos(CustomGuiProperties.EnumContainer.HOPPER, blockPos, blockAccess, loc, guiScreen);
         }
      }

      Entity entity = playerControllerOF.getLastClickEntity();
      if (entity != null) {
         if (guiScreen instanceof GuiScreenHorseInventory) {
            return getTextureEntity(CustomGuiProperties.EnumContainer.HORSE, entity, blockAccess, loc);
         }

         if (guiScreen instanceof GuiMerchant) {
            return getTextureEntity(CustomGuiProperties.EnumContainer.VILLAGER, entity, blockAccess, loc);
         }
      }

      return loc;
   }

   private static CustomGuiProperties[][] propertyListToArray(List<List<CustomGuiProperties>> listProps) {
      if (listProps.isEmpty()) {
         return (CustomGuiProperties[][])null;
      }

      CustomGuiProperties[][] acustomguiproperties = new CustomGuiProperties[CustomGuiProperties.EnumContainer.VALUES.length][];

      for (int index = 0; index < acustomguiproperties.length; index++) {
         if (listProps.size() > index) {
            List<CustomGuiProperties> list = listProps.get(index);
            if (list != null) {
               CustomGuiProperties[] customGuiPropertiesArray = list.toArray(new CustomGuiProperties[list.size()]);
               acustomguiproperties[index] = customGuiPropertiesArray;
            }
         }
      }

      return acustomguiproperties;
   }

   private static void update(IResourcePack rp, List<List<CustomGuiProperties>> listProps) {
      String[] texts = ResUtils.collectFiles(rp, "optifine/gui/container/", ".properties", (String[])null);
      Arrays.sort(texts);

      for (int i = 0; i < texts.length; i++) {
         String text = texts[i];
         Config.dbg("CustomGuis: " + text);

         try {
            ResourceLocation resourceLocation = new ResourceLocation(text);
            InputStream inputStream = rp.getInputStream(resourceLocation);
            if (inputStream == null) {
               Config.warn("CustomGuis file not found: " + text);
            } else {
               PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
               propertiesOrdered.load(inputStream);
               inputStream.close();
               CustomGuiProperties customGuiProperties = new CustomGuiProperties(propertiesOrdered, text);
               if (customGuiProperties.isValid(text)) {
                  addToList(customGuiProperties, listProps);
               }
            }
         } catch (FileNotFoundException fileNotFoundException) {
            Config.warn("CustomGuis file not found: " + text);
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }

   private static boolean isChristmas() {
      Calendar calendar = Calendar.getInstance();
      return calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26;
   }

   public static void setPlayerControllerOF(PlayerControllerOF playerControllerOF) {
   }

   public static PlayerControllerOF getPlayerControllerOF() {
      return playerControllerOF;
   }

   public static void update() {
      guiProperties = (CustomGuiProperties[][])null;
      if (Config.isCustomGuis()) {
         ArrayList arrayList = new ArrayList();
         IResourcePack[] resourcePacks = Config.getResourcePacks();

         for (int i = resourcePacks.length - 1; i >= 0; i--) {
            update(resourcePacks[i], arrayList);
         }

         guiProperties = propertyListToArray(arrayList);
      }
   }

   private static ResourceLocation getTextureEntity(CustomGuiProperties.EnumContainer container, Entity entity, IBlockAccess blockAccess, ResourceLocation loc) {
      CustomGuiProperties[] customGuiPropertiesArray = guiProperties[container.ordinal()];
      if (customGuiPropertiesArray == null) {
         return loc;
      }

      for (int index = 0; index < customGuiPropertiesArray.length; index++) {
         CustomGuiProperties customGuiProperties = customGuiPropertiesArray[index];
         if (customGuiProperties.matchesEntity(container, entity, blockAccess)) {
            return customGuiProperties.getTextureLocation(loc);
         }
      }

      return loc;
   }

   private static ResourceLocation getTexturePos(
      CustomGuiProperties.EnumContainer container, BlockPos pos, IBlockAccess blockAccess, ResourceLocation loc, GuiScreen screen
   ) {
      CustomGuiProperties[] customGuiPropertiesArray = guiProperties[container.ordinal()];
      if (customGuiPropertiesArray == null) {
         return loc;
      }

      for (int index = 0; index < customGuiPropertiesArray.length; index++) {
         CustomGuiProperties customGuiProperties = customGuiPropertiesArray[index];
         if (customGuiProperties.matchesPos(container, pos, blockAccess, screen)) {
            return customGuiProperties.getTextureLocation(loc);
         }
      }

      return loc;
   }

   private static void addToList(CustomGuiProperties cgp, List<List<CustomGuiProperties>> listProps) {
      if (cgp.getContainer() == null) {
         warn("Invalid container: " + cgp.getContainer());
      } else {
         int i = cgp.getContainer().ordinal();

         while (listProps.size() <= i) {
            listProps.add(null);
         }

         List<CustomGuiProperties> list = listProps.get(i);
         if (list == null) {
            list = new ArrayList<>();
            listProps.set(i, list);
         }

         list.add(cgp);
      }
   }

   private static void warn(String str) {
      Config.warn("[CustomGuis] " + str);
   }
}
