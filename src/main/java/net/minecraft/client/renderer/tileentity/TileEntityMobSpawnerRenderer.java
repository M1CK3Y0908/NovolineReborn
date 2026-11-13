package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;

public class TileEntityMobSpawnerRenderer extends TileEntitySpecialRenderer<TileEntityMobSpawner> {
   public static void renderMob(MobSpawnerBaseLogic mobSpawnerLogic, double posX, double posY, double posZ, float partialTicks) {
      Entity cachedEntity = mobSpawnerLogic.getCachedEntity(mobSpawnerLogic.getSpawnerWorld());
      if (cachedEntity != null) {
         float f = 0.4375F;
         GlStateManager.translate(0.0F, 0.4F, 0.0F);
         GlStateManager.rotate(
            (float)(mobSpawnerLogic.getPrevMobRotation() + (mobSpawnerLogic.getMobRotation() - mobSpawnerLogic.getPrevMobRotation()) * partialTicks) * 10.0F,
            0.0F,
            1.0F,
            0.0F
         );
         GlStateManager.rotate(-30.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translate(0.0F, -0.4F, 0.0F);
         GlStateManager.scale(f, f, f);
         cachedEntity.setLocationAndAngles(posX, posY, posZ, 0.0F, 0.0F);
         Minecraft.getMinecraft().getRenderManager().renderEntityWithPosYaw(cachedEntity, 0.0, 0.0, 0.0, 0.0F, partialTicks);
      }
   }

   public void renderTileEntityAt(TileEntityMobSpawner tileEntityMobSpawner, double d, double d2, double d3, float f, int unused5) {
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)d + 0.5F, (float)d2, (float)d3 + 0.5F);
      renderMob(tileEntityMobSpawner.getSpawnerBaseLogic(), d, d2, d3, f);
      GlStateManager.popMatrix();
   }
}
