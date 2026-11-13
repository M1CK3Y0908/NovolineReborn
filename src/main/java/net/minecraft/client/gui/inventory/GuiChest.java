package net.minecraft.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiChest extends GuiContainer {
   public IInventory upperChestInventory;
   private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
   public IInventory lowerChestInventory;
   private int inventoryRows;

   @Override
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.fontRendererObj.getInt(this.lowerChestInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
      this.fontRendererObj.getInt(this.upperChestInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
   }

   public GuiChest(IInventory upperInv, IInventory lowerInv) {
      super(new ContainerChest(upperInv, lowerInv, Minecraft.getMinecraft().thePlayer));
      this.upperChestInventory = upperInv;
      this.lowerChestInventory = lowerInv;
      this.allowUserInput = false;
      this.inventoryRows = lowerInv.getSizeInventory() / 9;
      this.ySize = 114 + this.inventoryRows * 18;
   }

   @Override
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
      int i = (this.width - this.xSize) / 2;
      int i2 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, i2, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
      this.drawTexturedModalRect(i, i2 + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
   }
}
