package net.minecraft.client.gui;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

public class GuiRepair extends GuiContainer implements ICrafting {
   private InventoryPlayer playerInventory;
   private GuiTextField nameField;
   private ContainerRepair anvil;
   private static final ResourceLocation anvilResource = new ResourceLocation("textures/gui/container/anvil.png");

   @Override
   public void sendProgressBarUpdate(Container containerIn, int varToUpdate, int newValue) {
   }

   @Override
   public void initGui() {
      super.initGui();
      Keyboard.enableRepeatEvents(true);
      int i = (this.width - this.xSize) / 2;
      int i2 = (this.height - this.ySize) / 2;
      this.nameField = new GuiTextField(0, this.fontRendererObj, i + 62, i2 + 24, 103, 12);
      this.nameField.setTextColor(-1);
      this.nameField.update2();
      this.nameField.update();
      this.nameField.setMaxStringLength(30);
      this.inventorySlots.removeCraftingFromCrafters(this);
      this.inventorySlots.onCraftGuiOpened(this);
   }

   public GuiRepair(InventoryPlayer inventoryIn, World worldIn) {
      super(new ContainerRepair(inventoryIn, worldIn, Minecraft.getMinecraft().thePlayer));
      this.playerInventory = inventoryIn;
      this.anvil = (ContainerRepair)this.inventorySlots;
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      GlStateManager.disableLighting();
      GlStateManager.disableBlend();
      this.nameField.drawTextBox();
   }

   @Override
   public void startClosing() {
      super.startClosing();
      Keyboard.enableRepeatEvents(false);
      this.inventorySlots.removeCraftingFromCrafters(this);
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if (this.nameField.textboxKeyTyped(typedChar, keyCode)) {
         this.renameItem();
      } else {
         super.keyTyped(typedChar, keyCode);
      }
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
   }

   private void renameItem() {
      String text = this.nameField.getText();
      Slot slot = this.anvil.getSlot(0);
      if (slot != null && slot.getHasStack() && !slot.getStack().hasDisplayName() && text.equals(slot.getStack().getDisplayName())) {
         text = "";
      }

      this.anvil.updateItemName(text);
      this.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("MC|ItemName", new PacketBuffer(Unpooled.buffer()).writeString(text)));
   }

   @Override
   public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
      if (slotInd == 0) {
         this.nameField.setText(stack == null ? "" : stack.getDisplayName());
         this.nameField.setEnabled(stack != null);
         if (stack != null) {
            this.renameItem();
         }
      }
   }

   @Override
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      GlStateManager.disableLighting();
      GlStateManager.disableBlend();
      this.fontRendererObj.getInt(I18n.format("container.repair"), 60, 6, 4210752);
      if (this.anvil.maximumCost > 0) {
         int i = 8453920;
         boolean flag = true;
         String text2 = I18n.format("container.repair.cost", this.anvil.maximumCost);
         if (this.anvil.maximumCost >= 40 && !this.mc.thePlayer.capabilities.isCreativeMode) {
            text2 = I18n.format("container.repair.expensive");
            i = 16736352;
         } else if (!this.anvil.getSlot(2).getHasStack()) {
            flag = false;
         } else if (!this.anvil.getSlot(2).canTakeStack(this.playerInventory.player)) {
            i = 16736352;
         }

         if (flag) {
            int i2 = 0xFF000000 | (i & 16579836) >> 2 | i & 0xFF000000;
            int i3 = this.xSize - 8 - this.fontRendererObj.getInt3(text2);
            if (this.fontRendererObj.getUnicodeFlag()) {
               handleInt(i3 - 3, 65, this.xSize - 7, 77, -16777216);
               handleInt(i3 - 2, 66, this.xSize - 8, 76, -12895429);
            } else {
               this.fontRendererObj.getInt(text2, i3, 68, i2);
               this.fontRendererObj.getInt(text2, i3 + 1, 67, i2);
               this.fontRendererObj.getInt(text2, i3 + 1, 68, i2);
            }

            this.fontRendererObj.getInt(text2, i3, 67, i);
         }
      }

      GlStateManager.enableLighting();
   }

   @Override
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(anvilResource);
      int i = (this.width - this.xSize) / 2;
      int i2 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, i2, 0, 0, this.xSize, this.ySize);
      int i3 = i + 59;
      int i4 = i2 + 20;
      int i5 = this.ySize + (this.anvil.getSlot(0).getHasStack() ? 0 : 16);
      this.drawTexturedModalRect(i3, i4, 0, i5, 110, 16);
      if ((this.anvil.getSlot(0).getHasStack() || this.anvil.getSlot(1).getHasStack()) && !this.anvil.getSlot(2).getHasStack()) {
         this.drawTexturedModalRect(i + 99, i2 + 45, this.xSize, 0, 28, 21);
      }
   }

   @Override
   public void sendAllWindowProperties(Container container2, IInventory iInventory) {
   }

   @Override
   public void updateCraftingInventory(Container containerToSend, List<ItemStack> itemsList) {
      this.sendSlotContents(containerToSend, 0, containerToSend.getSlot(0).getStack());
   }
}
