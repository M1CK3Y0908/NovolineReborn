package net.optifine.player;

import java.awt.image.BufferedImage;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.util.ResourceLocation;

public class CapeImageBuffer extends ImageBufferDownload {
   private boolean elytraOfCape;
   private AbstractClientPlayer player;
   private ResourceLocation resourceLocation;

   public void cleanup() {
      this.player = null;
   }

   @Override
   public void skinAvailable() {
      if (this.player != null) {
         this.player.setLocationOfCape(this.resourceLocation);
         this.player.setElytraOfCape(this.elytraOfCape);
      }

      this.cleanup();
   }

   public CapeImageBuffer(AbstractClientPlayer player, ResourceLocation resourceLocation) {
      this.player = player;
      this.resourceLocation = resourceLocation;
   }

   public boolean isElytraOfCape() {
      return this.elytraOfCape;
   }

   @Override
   public BufferedImage parseUserSkin(BufferedImage imageRaw) {
      BufferedImage bufferedImage = CapeUtils.parseCape(imageRaw);
      this.elytraOfCape = CapeUtils.isElytraCape(imageRaw, bufferedImage);
      return bufferedImage;
   }
}
