package net.minecraft.client.gui.inventory;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiDispenser extends GuiContainer {
   private final InventoryPlayer playerInventory;
   private static final ResourceLocation dispenserGuiTextures = new ResourceLocation("textures/gui/container/dispenser.png");
   public IInventory dispenserInventory;

   public GuiDispenser(InventoryPlayer playerInv, IInventory dispenserInv) {
      super(new ContainerDispenser(playerInv, dispenserInv));
      this.playerInventory = playerInv;
      this.dispenserInventory = dispenserInv;
   }

   @Override
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(dispenserGuiTextures);
      int i = (this.width - this.xSize) / 2;
      int i2 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, i2, 0, 0, this.xSize, this.ySize);
   }

   @Override
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      String text = this.dispenserInventory.getDisplayName().getUnformattedText();
      this.fontRendererObj.getInt(text, this.xSize / 2 - this.fontRendererObj.getInt3(text) / 2, 6, 4210752);
      this.fontRendererObj.getInt(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
   }
}
