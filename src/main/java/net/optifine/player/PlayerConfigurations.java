package net.optifine.player;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.optifine.http.FileDownloadThread;
import net.optifine.http.HttpUtils;

public class PlayerConfigurations {
   private static Map mapConfigurations = null;
   private static boolean reloadPlayerItems = Boolean.getBoolean("player.models.reload");
   private static long timeReloadPlayerItemsMs = System.currentTimeMillis();

   public static synchronized void setPlayerConfiguration(String player, PlayerConfiguration pc) {
      getMapConfigurations().put(player, pc);
   }

   private static Map getMapConfigurations() {
      if (mapConfigurations == null) {
         mapConfigurations = new HashMap();
      }

      return mapConfigurations;
   }

   public static synchronized PlayerConfiguration getPlayerConfiguration(AbstractClientPlayer player) {
      if (reloadPlayerItems && System.currentTimeMillis() > timeReloadPlayerItemsMs + 5000L) {
         EntityPlayerSP thePlayer2 = Minecraft.getMinecraft().thePlayer;
         if (thePlayer2 != null) {
            setPlayerConfiguration(thePlayer2.getNameClear(), (PlayerConfiguration)null);
            timeReloadPlayerItemsMs = System.currentTimeMillis();
         }
      }

      String nameClear = player.getNameClear();
      if (nameClear == null) {
         return null;
      }

      PlayerConfiguration playerConfiguration2 = (PlayerConfiguration)getMapConfigurations().get(nameClear);
      if (playerConfiguration2 == null) {
         playerConfiguration2 = new PlayerConfiguration();
         getMapConfigurations().put(nameClear, playerConfiguration2);
         PlayerConfigurationReceiver playerConfigurationReceiver = new PlayerConfigurationReceiver(nameClear);
         String text = HttpUtils.getPlayerItemsUrl() + "/users/" + nameClear + ".cfg";
         new FileDownloadThread(text, playerConfigurationReceiver).start();
      }

      return playerConfiguration2;
   }

   public static void renderPlayerItems(ModelBiped modelBiped, AbstractClientPlayer player, float scale, float partialTicks) {
      PlayerConfiguration playerConfiguration = getPlayerConfiguration(player);
      if (playerConfiguration != null) {
         playerConfiguration.renderPlayerItems(modelBiped, player, scale, partialTicks);
      }
   }
}
