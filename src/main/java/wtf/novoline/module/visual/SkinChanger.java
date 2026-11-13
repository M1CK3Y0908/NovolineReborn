package wtf.novoline.module.visual;

import com.google.gson.JsonParser;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.net.URI;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import org.apache.commons.io.IOUtils;
import wtf.novoline.Novoline;
import wtf.novoline.command.Command;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;

public class SkinChanger extends Module {
   private final CheckboxSetting skinChanger = settingManager.register(new CheckboxSetting(this, "Apply for all", false));
   private String skinName = "Rqtted";
   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> this.setSuffix(this.skinName);
   private final Map<String, String> resolvedSkinNames = new WeakHashMap<>();

   public CheckboxSetting getSkinChanger() {
      return this.skinChanger;
   }

   public SkinChanger() {
      super(Category.VISUAL, "SkinChanger", "Changes your skin accordingly to the .setskin command");
      novoline.getCommandManager()
         .register(
            new Command("changes skin if SkinChanger enabled", "setskin") {
               @Override
               public void execute(String[] texts) {
                  if (texts.length == 2) {
                     String text = texts[1];

                     try {
                        this.novoline.sendChatMessage("Downloading skin...");
                        SkinChanger.this.downloadImageSkin(AbstractClientPlayer.getLocationSkin(text), text, false)
                           .loadTexture(SkinChanger.access$000().getResourceManager());
                        SkinChanger.this.skinName = text;
                     } catch (Exception exception) {
                        this.novoline.sendChatMessage("§cCouldn't download skin: " + exception.getMessage());
                        Novoline.taggedLogger.error(exception, "Couldn't download skin {}", text);
                        return;
                     }

                     this.novoline.sendChatMessage("§7Skin set to §3'" + text + "'§7.");
                  } else {
                     this.novoline.sendChatMessage("§cWrong command arguments!");
                     this.novoline.sendChatMessage("§c.setskin <name>");
                  }
               }
            }
         );
   }

   public ThreadDownloadImageData downloadImageSkin(ResourceLocation resourceLocation, String text, boolean flag) {
      TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
      ITextureObject texture2 = textureManager.getTexture(resourceLocation);
      if (texture2 == null || !flag) {
         text = StringUtils.stripControlCodes(text);
         String ifAbsent = this.resolvedSkinNames.computeIfAbsent(text, string -> {
            try {
               String text2 = IOUtils.toString(URI.create("https://api.mojang.com/users/profiles/minecraft/" + string));
               return JsonParser.parseString(text2).getAsJsonObject().get("id").getAsString();
            } catch (Exception exception) {
               novoline.sendChatMessage("Unable to fetch player profile for " + string + ": " + exception.getMessage());
               return null;
            }
         });
         if (ifAbsent != null) {
            texture2 = new ThreadDownloadImageData(
               null,
               String.format("https://crafatar.com/skins/%s", ifAbsent),
               DefaultPlayerSkin.getDefaultSkin(AbstractClientPlayer.getOfflineUUID(text)),
               new ImageBufferDownload()
            );
            textureManager.loadTexture(resourceLocation, texture2);
         }
      }

      return (ThreadDownloadImageData)texture2;
   }

   public ResourceLocation getResourceLocation() {
      try {
         ResourceLocation locationSkin = AbstractClientPlayer.getLocationSkin(this.skinName);
         this.downloadImageSkin(locationSkin, this.skinName, true).loadTexture(minecraft.getResourceManager());
      } catch (Exception exception) {
         novoline.sendChatMessage("Couldn't load skin: " + exception.getMessage());
      }

      return AbstractClientPlayer.getLocationSkin(this.skinName);
   }

   static Minecraft access$000() {
      return minecraft;
   }
}
