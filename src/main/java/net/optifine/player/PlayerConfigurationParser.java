package net.optifine.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.http.HttpPipeline;
import net.optifine.http.HttpUtils;
import net.optifine.util.Json;

public class PlayerConfigurationParser {
   private String player = null;
   public static final String ACTIVE = "active";
   public static final String TYPE = "type";
   public static final String ITEMS = "items";

   public PlayerConfiguration parsePlayerConfiguration(JsonElement je) {
      if (je == null) {
         throw new JsonParseException("JSON object is null, player: " + this.player);
      }

      JsonObject jsonObject = (JsonObject)je;
      PlayerConfiguration playerConfiguration = new PlayerConfiguration();
      JsonArray jsonArray = (JsonArray)jsonObject.get("items");
      if (jsonArray != null) {
         for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject2 = (JsonObject)jsonArray.get(i);
            if (Json.getBoolean(jsonObject2, "active", true)) {
               String string = Json.getString(jsonObject2, "type");
               if (string == null) {
                  Config.warn("Item type is null, player: " + this.player);
               } else {
                  String string2 = Json.getString(jsonObject2, "model");
                  if (string2 == null) {
                     string2 = "items/" + string + "/model.cfg";
                  }

                  PlayerItemModel playerItemModel = this.downloadModel(string2);
                  if (playerItemModel != null) {
                     if (!playerItemModel.isUsePlayerTexture()) {
                        String string3 = Json.getString(jsonObject2, "texture");
                        if (string3 == null) {
                           string3 = "items/" + string + "/users/" + this.player + ".png";
                        }

                        BufferedImage bufferedImage = this.downloadTextureImage(string3);
                        if (bufferedImage == null) {
                           continue;
                        }

                        playerItemModel.setTextureImage(bufferedImage);
                        ResourceLocation resourceLocation = new ResourceLocation("optifine.net", string3);
                        playerItemModel.setTextureLocation(resourceLocation);
                     }

                     playerConfiguration.addPlayerItemModel(playerItemModel);
                  }
               }
            }
         }
      }

      return playerConfiguration;
   }

   public PlayerConfigurationParser(String player) {
      this.player = player;
   }

   private PlayerItemModel downloadModel(String modelPath) {
      String text = HttpUtils.getPlayerItemsUrl() + "/" + modelPath;

      try {
         byte[] bArray = HttpPipeline.get(text, Minecraft.getMinecraft().getProxy());
         String text2 = new String(bArray, "ASCII");
         return PlayerItemParser.parseItemModel((JsonObject)new JsonParser().parse(text2));
      } catch (Exception exception) {
         Config.warn("Error loading item model " + modelPath + ": " + exception.getClass().getName() + ": " + exception.getMessage());
         return null;
      }
   }

   private BufferedImage downloadTextureImage(String texturePath) {
      String text = HttpUtils.getPlayerItemsUrl() + "/" + texturePath;

      try {
         byte[] bArray = HttpPipeline.get(text, Minecraft.getMinecraft().getProxy());
         return ImageIO.read(new ByteArrayInputStream(bArray));
      } catch (IOException ioException) {
         Config.warn("Error loading item texture " + texturePath + ": " + ioException.getClass().getName() + ": " + ioException.getMessage());
         return null;
      }
   }
}
