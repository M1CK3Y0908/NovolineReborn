package net.optifine.player;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.src.Config;
import net.optifine.http.IFileDownloadListener;

public class PlayerConfigurationReceiver implements IFileDownloadListener {
   private String player = null;

   @Override
   public void fileDownloadFinished(String url, byte[] bytes, Throwable exception) {
      if (bytes != null) {
         try {
            String text = new String(bytes, "ASCII");
            JsonElement jsonElement = new JsonParser().parse(text);
            PlayerConfiguration playerConfiguration = new PlayerConfigurationParser(this.player).parsePlayerConfiguration(jsonElement);
            if (playerConfiguration != null) {
               playerConfiguration.update();
               PlayerConfigurations.setPlayerConfiguration(this.player, playerConfiguration);
            }
         } catch (Exception exception2) {
            Config.dbg("Error parsing configuration: " + url + ", " + exception2.getClass().getName() + ": " + exception2.getMessage());
         }
      }
   }

   public PlayerConfigurationReceiver(String player) {
      this.player = player;
   }
}
