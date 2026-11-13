package net.optifine.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class CloudRenderer {
   private boolean updated = false;
   private boolean renderFancy = false;
   private boolean updateRenderFancy = false;
   private int updateCloudTickCounter = 0;
   private Vec3 updateCloudColor = new Vec3(-1.0, -1.0, -1.0);
   private double updatePlayerX = 0.0;
   private double updatePlayerY = 0.0;
   float partialTicks;
   private Vec3 cloudColor;
   private double updatePlayerZ = 0.0;
   private int glListClouds = -1;
   private Minecraft mc;
   int cloudTickCounter;

   public boolean shouldUpdateGlList() {
      if (!this.updated) {
         return true;
      }

      if (this.renderFancy != this.updateRenderFancy) {
         return true;
      }

      if (this.cloudTickCounter >= this.updateCloudTickCounter + 20) {
         return true;
      }

      if (Math.abs(this.cloudColor.xCoord - this.updateCloudColor.xCoord) > 0.003) {
         return true;
      }

      if (Math.abs(this.cloudColor.yCoord - this.updateCloudColor.yCoord) > 0.003) {
         return true;
      }

      if (Math.abs(this.cloudColor.zCoord - this.updateCloudColor.zCoord) > 0.003) {
         return true;
      }

      Entity entity = this.mc.getRenderViewEntity();
      boolean flag = this.updatePlayerY + entity.getEyeHeight() < 128.0 + this.mc.gameSettings.ofCloudsHeight * 128.0F;
      return entity.prevPosY + entity.getEyeHeight() < 128.0 + this.mc.gameSettings.ofCloudsHeight * 128.0F != flag;
   }

   public CloudRenderer(Minecraft mc) {
      this.mc = mc;
      this.glListClouds = GLAllocation.generateDisplayLists(1);
   }

   public void endUpdateGlList() {
      GL11.glEndList();
      this.updateRenderFancy = this.renderFancy;
      this.updateCloudTickCounter = this.cloudTickCounter;
      this.updateCloudColor = this.cloudColor;
      this.updatePlayerX = this.mc.getRenderViewEntity().prevPosX;
      this.updatePlayerY = this.mc.getRenderViewEntity().prevPosY;
      this.updatePlayerZ = this.mc.getRenderViewEntity().prevPosZ;
      this.updated = true;
      GlStateManager.resetColor();
   }

   public void prepareToRender(boolean renderFancy, int cloudTickCounter, float partialTicks, Vec3 cloudColor) {
      this.renderFancy = renderFancy;
      this.cloudTickCounter = cloudTickCounter;
      this.partialTicks = partialTicks;
      this.cloudColor = cloudColor;
   }

   public void renderGlList() {
      Entity entity = this.mc.getRenderViewEntity();
      double xCoordinate = entity.prevPosX + (entity.posX - entity.prevPosX) * this.partialTicks;
      double yCoordinate = entity.prevPosY + (entity.posY - entity.prevPosY) * this.partialTicks;
      double zCoordinate = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * this.partialTicks;
      double d = this.cloudTickCounter - this.updateCloudTickCounter + this.partialTicks;
      float f = (float)(xCoordinate - this.updatePlayerX + d * 0.03);
      float f2 = (float)(yCoordinate - this.updatePlayerY);
      float f3 = (float)(zCoordinate - this.updatePlayerZ);
      GlStateManager.pushMatrix();
      if (this.renderFancy) {
         GlStateManager.translate(-f / 12.0F, -f2, -f3 / 12.0F);
      } else {
         GlStateManager.translate(-f, -f2, -f3);
      }

      GlStateManager.callList(this.glListClouds);
      GlStateManager.popMatrix();
      GlStateManager.resetColor();
   }

   public void startUpdateGlList() {
      GL11.glNewList(this.glListClouds, 4864);
   }

   public void reset() {
      this.updated = false;
   }
}
