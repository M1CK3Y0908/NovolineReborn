package net.minecraft.client.resources;

import com.google.gson.JsonParseException;
import java.io.IOException;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackListEntryDefault extends ResourcePackListEntry {
   private final IResourcePack defaultResourcePack = this.mc.getResourcePackRepository().rprDefaultResourcePack;
   private final ResourceLocation resourcePackIcon;
   private static final Logger logger = LogManager.getLogger();

   @Override
   protected int getCompatibility() {
      return 1;
   }

   @Override
   protected boolean canMoveUp() {
      return false;
   }

   @Override
   protected boolean canBeSelected() {
      return false;
   }

   @Override
   protected boolean canMoveDown() {
      return false;
   }

   @Override
   protected String getDescription() {
      try {
         PackMetadataSection packMetadataSection = this.defaultResourcePack.getPackMetadata(this.mc.getResourcePackRepository().rprMetadataSerializer, "pack");
         if (packMetadataSection != null) {
            return packMetadataSection.getPackDescription().getFormattedText();
         }
      } catch (JsonParseException jsonParseException) {
         logger.error("Couldn't load metadata info", jsonParseException);
      } catch (IOException ioException) {
         logger.error("Couldn't load metadata info", ioException);
      }

      return EnumChatFormatting.RED + "Missing pack.mcmeta :(";
   }

   @Override
   protected String getName() {
      return "Default";
   }

   @Override
   protected boolean canAddToSelected() {
      return false;
   }

   @Override
   protected boolean canRemoveFromSelected() {
      return false;
   }

   public ResourcePackListEntryDefault(GuiScreenResourcePacks resourcePacksGUIIn) {
      super(resourcePacksGUIIn);

      DynamicTexture dynamicTexture;
      try {
         dynamicTexture = new DynamicTexture(this.defaultResourcePack.getPackImage());
      } catch (IOException ioException) {
         dynamicTexture = TextureUtil.missingTexture;
      }

      this.resourcePackIcon = this.mc.getTextureManager().getDynamicTextureLocation("texturepackicon", dynamicTexture);
   }

   @Override
   protected void bindTexture() {
      this.mc.getTextureManager().bindTexture(this.resourcePackIcon);
   }
}
