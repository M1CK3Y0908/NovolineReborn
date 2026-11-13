package wtf.novoline.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.net.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.CommandSuggestions;
import net.minecraft.world.WorldSettings.GameType;

public class MinecraftUtils {
   public static int getAdventureModeId() {
      return GameType.ADVENTURE.getID();
   }

   public static int getCreativeModeId() {
      return GameType.CREATIVE.getID();
   }

   public static int getSpectatorModeId() {
      return GameType.SPECTATOR.getID();
   }

   public static boolean isTouchscreen() {
      return Minecraft.getMinecraft().gameSettings.touchscreen;
   }

   public static long getSystemTime() {
      return Minecraft.getSystemTime();
   }

   public static String getUsername() {
      return Minecraft.getMinecraft().getSession().getUsername();
   }

   public static Proxy getProxy() {
      return Minecraft.getMinecraft().getProxy();
   }

   public static String getPlayerId() {
      return Minecraft.getMinecraft().getSession().getPlayerID();
   }

   public static String getGameDirectory() {
      return Minecraft.getMinecraft().mcDataDir.getAbsolutePath();
   }

   public static void displayScreen(CommandSuggestions commandSuggestions) {
      Minecraft.getMinecraft().displayGuiScreen(commandSuggestions.getGuiScreenRealmsProxy());
   }

   public static void clearResourcePack() {
      Minecraft.getMinecraft().getResourcePackRepository().clearResourcePack();
   }

   public static void refreshLanguage() {
      Minecraft.getMinecraft().refreshLanguage();
   }

   public static String getNameByUuid(String text) {
      return Minecraft.getMinecraft()
         .getSessionService()
         .fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(text), (String)null), false)
         .getName();
   }

   public static int getSurvivalModeId() {
      return GameType.SURVIVAL.getID();
   }
}
