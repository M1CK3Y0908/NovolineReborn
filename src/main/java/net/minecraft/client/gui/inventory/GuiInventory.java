package net.minecraft.client.gui.inventory;

import java.awt.Color;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.player.InventoryMove;

public class GuiInventory extends InventoryEffectRenderer {
   private float oldMouseY;
   private float oldMouseX;

   @Override
   public void updateScreen() {
      if (this.mc.playerController.isInCreativeMode()) {
         this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.thePlayer));
      }

      this.updateActivePotionEffects();
   }

   public GuiInventory(EntityPlayer entityPlayer) {
      super(entityPlayer.inventoryContainer);
      this.allowUserInput = true;
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      if (this.mc.playerController.isInCreativeMode()) {
         this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.thePlayer));
      } else {
         super.initGui();
      }
   }

   @Override
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(inventoryBackground);
      int guiLeft2 = this.guiLeft;
      int guiTop2 = this.guiTop;
      this.drawTexturedModalRect(guiLeft2, guiTop2, 0, 0, this.xSize, this.ySize);
      drawEntityOnScreen(guiLeft2 + 51, guiTop2 + 75, 30, guiLeft2 + 51 - this.oldMouseX, guiTop2 + 75 - 50 - this.oldMouseY, this.mc.thePlayer);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      InventoryMove inventoryMove2 = ModuleManager.inventoryMove;
      if (inventoryMove2.isEnabled() && inventoryMove2.inventoryMove.getCurrentElement() == 1 && inventoryMove2.showStatus.isEnabled()) {
         String text;
         int i;
         if (inventoryMove2.queuedPackets.size() == 0) {
            text = "Inventory Synchronized";
            i = Color.GREEN.getRGB();
         } else {
            text = "Inventory Desynchronized";
            i = Color.RED.getRGB();
         }

         ScaledResolution scaledResolution = new ScaledResolution(this.mc);
         Fonts.rubikRegularSize16
            .drawStringWithNiceShadow(
               text, scaledResolution.getScaledWidth() / 2.0F - Fonts.rubikRegularSize16.getStringWidth(text) / 2.0F, scaledResolution.getScaledHeight() / 2.0F - 100.0F, i
            );
      }

      this.oldMouseX = mouseX;
      this.oldMouseY = mouseY;
   }

   @Override
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.fontRendererObj.getInt(I18n.format("container.crafting"), 86, 16, 4210752);
   }

   public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent) {
      GlStateManager.enableColorMaterial();
      GlStateManager.pushMatrix();
      GlStateManager.translate(posX, posY, 50.0F);
      GlStateManager.scale(-scale, scale, scale);
      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      float renderYawOffset2 = ent.renderYawOffset;
      float rotationYaw2 = ent.rotationYaw;
      float rotationPitch2 = ent.rotationPitch;
      float prevRotationYawHead2 = ent.prevRotationYawHead;
      float rotationYawHead2 = ent.rotationYawHead;
      GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
      RenderHelper.enableStandardItemLighting();
      GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-((float)Math.atan(mouseY / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
      ent.renderYawOffset = (float)Math.atan(mouseX / 40.0F) * 20.0F;
      ent.rotationYaw = (float)Math.atan(mouseX / 40.0F) * 40.0F;
      ent.rotationPitch = -((float)Math.atan(mouseY / 40.0F)) * 20.0F;
      ent.rotationYawHead = ent.rotationYaw;
      ent.prevRotationYawHead = ent.rotationYaw;
      GlStateManager.translate(0.0F, 0.0F, 0.0F);
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      renderManager.setPlayerViewY(180.0F);
      renderManager.setRenderShadow(false);
      renderManager.renderEntityWithPosYaw(ent, 0.0, 0.0, 0.0, 0.0F, 1.0F);
      renderManager.setRenderShadow(true);
      ent.renderYawOffset = renderYawOffset2;
      ent.rotationYaw = rotationYaw2;
      ent.rotationPitch = rotationPitch2;
      ent.prevRotationYawHead = prevRotationYawHead2;
      ent.rotationYawHead = rotationYawHead2;
      GlStateManager.popMatrix();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableRescaleNormal();
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.disableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 0) {
         this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.getStatFileWriter()));
      }

      if (button.id == 1) {
         this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
      }
   }
}
