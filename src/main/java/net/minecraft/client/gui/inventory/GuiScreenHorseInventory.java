package net.minecraft.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiScreenHorseInventory extends GuiContainer {
   private IInventory playerInventory;
   private float mousePosx;
   private IInventory horseInventory;
   private float mousePosY;
   private static final ResourceLocation horseGuiTextures = new ResourceLocation("textures/gui/container/horse.png");
   private EntityHorse horseEntity;

   @Override
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(horseGuiTextures);
      int i = (this.width - this.xSize) / 2;
      int i2 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, i2, 0, 0, this.xSize, this.ySize);
      if (this.horseEntity.isChested()) {
         this.drawTexturedModalRect(i + 79, i2 + 17, 0, this.ySize, 90, 54);
      }

      if (this.horseEntity.isActive()) {
         this.drawTexturedModalRect(i + 7, i2 + 35, 0, this.ySize + 54, 18, 18);
      }

      GuiInventory.drawEntityOnScreen(
         i + 51, i2 + 60, 17, i + 51 - this.mousePosx, i2 + 75 - 50 - this.mousePosY, this.horseEntity
      );
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.mousePosx = mouseX;
      this.mousePosY = mouseY;
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.fontRendererObj.getInt(this.horseInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
      this.fontRendererObj.getInt(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
   }

   public GuiScreenHorseInventory(IInventory playerInv, IInventory horseInv, EntityHorse horse) {
      super(new ContainerHorseInventory(playerInv, horseInv, horse, Minecraft.getMinecraft().thePlayer));
      this.playerInventory = playerInv;
      this.horseInventory = horseInv;
      this.horseEntity = horse;
      this.allowUserInput = false;
   }
}
