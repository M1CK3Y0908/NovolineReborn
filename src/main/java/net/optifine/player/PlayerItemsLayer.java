package net.optifine.player;

import java.util.Map;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.src.Config;

public class PlayerItemsLayer implements LayerRenderer {
   private RenderPlayer renderPlayer = null;

   public PlayerItemsLayer(RenderPlayer renderPlayer) {
      this.renderPlayer = renderPlayer;
   }

   public static void register(Map renderPlayerMap) {
      Object playerKey = renderPlayerMap.keySet();
      boolean flag = false;

      for (Object object : (Iterable<Object>) playerKey) {
         if (renderPlayerMap.get(object) instanceof RenderPlayer renderPlayer2) {
            renderPlayer2.addLayer(new PlayerItemsLayer(renderPlayer2));
            flag = true;
         }
      }

      if (!flag) {
         Config.warn("PlayerItemsLayer not registered");
      }
   }

   protected void renderEquippedItems(EntityLivingBase entityLiving, float scale, float partialTicks) {
      if (Config.isShowCapes() && entityLiving instanceof AbstractClientPlayer abstractClientPlayer) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableRescaleNormal();
         GlStateManager.enableCull();
         PlayerConfigurations.renderPlayerItems(this.renderPlayer.getMainModel(), abstractClientPlayer, scale, partialTicks);
         GlStateManager.disableCull();
      }
   }

   @Override
   public void doRenderLayer(
      EntityLivingBase entityLiving,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ticksExisted,
      float headYaw,
      float rotationPitch,
      float scale
   ) {
      this.renderEquippedItems(entityLiving, scale, partialTicks);
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
