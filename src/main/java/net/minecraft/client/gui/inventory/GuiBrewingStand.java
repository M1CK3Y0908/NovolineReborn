package net.minecraft.client.gui.inventory;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiBrewingStand extends GuiContainer {
   private final InventoryPlayer playerInventory;
   private IInventory tileBrewingStand;
   private static final ResourceLocation brewingStandGuiTextures = new ResourceLocation("textures/gui/container/brewing_stand.png");

   @Override
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(brewingStandGuiTextures);
      int i = (this.width - this.xSize) / 2;
      int i2 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, i2, 0, 0, this.xSize, this.ySize);
      int field = this.tileBrewingStand.getField(0);
      if (field > 0) {
         int i3 = (int)(28.0F * (1.0F - field / 400.0F));
         if (i3 > 0) {
            this.drawTexturedModalRect(i + 97, i2 + 16, 176, 0, 9, i3);
         }

         switch (field / 2 % 7) {
            case 0:
               i3 = 29;
               break;
            case 1:
               i3 = 24;
               break;
            case 2:
               i3 = 20;
               break;
            case 3:
               i3 = 16;
               break;
            case 4:
               i3 = 11;
               break;
            case 5:
               i3 = 6;
               break;
            case 6:
               i3 = 0;
         }

         if (i3 > 0) {
            this.drawTexturedModalRect(i + 65, i2 + 14 + 29 - i3, 185, 29 - i3, 12, i3);
         }
      }
   }

   public GuiBrewingStand(InventoryPlayer playerInv, IInventory iInventory) {
      super(new ContainerBrewingStand(playerInv, iInventory));
      this.playerInventory = playerInv;
      this.tileBrewingStand = iInventory;
   }

   @Override
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      String text = this.tileBrewingStand.getDisplayName().getUnformattedText();
      this.fontRendererObj.getInt(text, this.xSize / 2 - this.fontRendererObj.getInt3(text) / 2, 6, 4210752);
      this.fontRendererObj.getInt(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
   }
}
