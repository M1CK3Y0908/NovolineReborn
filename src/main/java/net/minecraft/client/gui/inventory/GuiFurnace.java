package net.minecraft.client.gui.inventory;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;

public class GuiFurnace extends GuiContainer {
   private final InventoryPlayer playerInventory;
   private IInventory tileFurnace;
   private static final ResourceLocation furnaceGuiTextures = new ResourceLocation("textures/gui/container/furnace.png");

   private int getInt() {
      int field = this.tileFurnace.getField(2);
      int field2 = this.tileFurnace.getField(3);
      return field2 != 0 && field != 0 ? field * 24 / field2 : 0;
   }

   public GuiFurnace(InventoryPlayer playerInv, IInventory furnaceInv) {
      super(new ContainerFurnace(playerInv, furnaceInv));
      this.playerInventory = playerInv;
      this.tileFurnace = furnaceInv;
   }

   @Override
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(furnaceGuiTextures);
      int i = (this.width - this.xSize) / 2;
      int i2 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, i2, 0, 0, this.xSize, this.ySize);
      if (TileEntityFurnace.isBurning(this.tileFurnace)) {
         int intValue = this.getInt2();
         this.drawTexturedModalRect(i + 56, i2 + 36 + 12 - intValue, 176, 12 - intValue, 14, intValue + 1);
      }

      int intValue2 = this.getInt();
      this.drawTexturedModalRect(i + 79, i2 + 34, 176, 14, intValue2 + 1, 16);
   }

   private int getInt2() {
      int field = this.tileFurnace.getField(1);
      if (field == 0) {
         field = 200;
      }

      return this.tileFurnace.getField(0) * 13 / field;
   }

   @Override
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      String text = this.tileFurnace.getDisplayName().getUnformattedText();
      this.fontRendererObj.getInt(text, this.xSize / 2 - this.fontRendererObj.getInt3(text) / 2, 6, 4210752);
      this.fontRendererObj.getInt(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
   }
}
