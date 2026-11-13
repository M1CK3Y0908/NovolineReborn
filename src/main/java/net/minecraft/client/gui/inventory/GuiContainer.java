package net.minecraft.client.gui.inventory;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import wtf.novoline.module.ModuleManager;

public abstract class GuiContainer extends GuiScreen {
   private long lastClickTime;
   private long dragItemDropDelay;
   private ItemStack shiftClickedSlot;
   private Slot theSlot;
   protected int xSize = 176;
   protected int ySize = 166;
   protected int guiLeft;
   private int dragSplittingLimit;
   private ItemStack returningStack;
   private int lastClickButton;
   private int dragSplittingButton;
   private Slot returningStackDestSlot;
   protected static final ResourceLocation inventoryBackground = new ResourceLocation("textures/gui/container/inventory.png");
   protected final Set<Slot> dragSplittingSlots = Sets.newHashSet();
   private int touchUpX;
   protected int guiTop;
   private Slot lastClickSlot;
   protected boolean dragSplitting;
   private int dragSplittingRemnant;
   private Slot currentDragTargetSlot;
   private boolean doubleClick;
   public Container inventorySlots;
   private ItemStack draggedStack;
   private boolean ignoreMouseUp;
   private int touchUpY;
   private long returningStackTime;
   private boolean isRightMouseClick;
   private Slot clickedSlot;

   private Slot getSlotAtPosition(int x, int y) {
      for (int index = 0; index < this.inventorySlots.inventorySlots.size(); index++) {
         Slot slot = this.inventorySlots.inventorySlots.get(index);
         if (this.isMouseOverSlot(slot, x, y)) {
            return slot;
         }
      }

      return null;
   }

   protected boolean isPointInRegion(int left, int top, int right, int bottom, int pointX, int pointY) {
      int guiLeft2 = this.guiLeft;
      int guiTop2 = this.guiTop;
      pointX -= guiLeft2;
      pointY -= guiTop2;
      return pointX >= left - 1 && pointX < left + right + 1 && pointY >= top - 1 && pointY < top + bottom + 1;
   }

   protected boolean checkHotbarKeys(int keyCode) {
      if (this.mc.thePlayer.inventory.getItemStack() == null && this.theSlot != null) {
         for (int index = 0; index < 9; index++) {
            if (keyCode == this.mc.gameSettings.keyBindsHotbar[index].getKeyCode()) {
               this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, index, 2);
               return true;
            }
         }
      }

      return false;
   }

   protected abstract void drawGuiContainerBackgroundLayer(float f, int i, int i2);

   private boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY) {
      return this.isPointInRegion(slotIn.xDisplayPosition, slotIn.yDisplayPosition, 16, 16, mouseX, mouseY);
   }

   private void updateDragSplitting() {
      ItemStack itemStack3 = this.mc.thePlayer.inventory.getItemStack();
      if (itemStack3 != null && this.dragSplitting) {
         this.dragSplittingRemnant = itemStack3.stackSize;

         for (Slot slot2 : this.dragSplittingSlots) {
            ItemStack itemStack4 = itemStack3.copy();
            int i = slot2.getStack() == null ? 0 : slot2.getStack().stackSize;
            Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemStack4, i);
            if (itemStack4.stackSize > itemStack4.getMaxStackSize()) {
               itemStack4.stackSize = itemStack4.getMaxStackSize();
            }

            if (itemStack4.stackSize > slot2.getItemStackLimit(itemStack4)) {
               itemStack4.stackSize = slot2.getItemStackLimit(itemStack4);
            }

            this.dragSplittingRemnant = this.dragSplittingRemnant - (itemStack4.stackSize - i);
         }
      }
   }

   private void drawItemStack(ItemStack stack, int x, int y, String altText) {
      GlStateManager.translate(0.0F, 0.0F, 32.0F);
      this.zLevel = 200.0F;
      this.itemRender.zLevel = 200.0F;
      this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
      this.itemRender.handleFontRenderer2(this.fontRendererObj, stack, x, y - (this.draggedStack == null ? 0 : 8), altText);
      this.zLevel = 0.0F;
      this.itemRender.zLevel = 0.0F;
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }

   private void drawSlot(Slot slotIn) {
      int xDisplayPosition2 = slotIn.xDisplayPosition;
      int yDisplayPosition2 = slotIn.yDisplayPosition;
      ItemStack itemStack = slotIn.getStack();
      boolean conditionMet = false;
      boolean conditionMet2 = slotIn == this.clickedSlot && this.draggedStack != null && !this.isRightMouseClick;
      ItemStack itemStack2 = this.mc.thePlayer.inventory.getItemStack();
      String text = null;
      if (slotIn == this.clickedSlot && this.draggedStack != null && this.isRightMouseClick && itemStack != null) {
         itemStack = itemStack.copy();
         itemStack.stackSize /= 2;
      } else if (this.dragSplitting && this.dragSplittingSlots.contains(slotIn) && itemStack2 != null) {
         if (this.dragSplittingSlots.size() == 1) {
            return;
         }

         if (Container.canAddItemToSlot(slotIn, itemStack2, true) && this.inventorySlots.canDragIntoSlot(slotIn)) {
            itemStack = itemStack2.copy();
            conditionMet = true;
            Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemStack, slotIn.getStack() == null ? 0 : slotIn.getStack().stackSize);
            if (itemStack.stackSize > itemStack.getMaxStackSize()) {
               text = "" + EnumChatFormatting.YELLOW + itemStack.getMaxStackSize();
               itemStack.stackSize = itemStack.getMaxStackSize();
            }

            if (itemStack.stackSize > slotIn.getItemStackLimit(itemStack)) {
               text = "" + EnumChatFormatting.YELLOW + slotIn.getItemStackLimit(itemStack);
               itemStack.stackSize = slotIn.getItemStackLimit(itemStack);
            }
         } else {
            this.dragSplittingSlots.remove(slotIn);
            this.updateDragSplitting();
         }
      }

      this.zLevel = 100.0F;
      this.itemRender.zLevel = 100.0F;
      if (itemStack == null) {
         String text2 = slotIn.getSlotTexture();
         if (text2 != null) {
            TextureAtlasSprite textureAtlasSprite = this.mc.getTextureMapBlocks().getAtlasSprite(text2);
            GlStateManager.disableLighting();
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            this.handleInt4(xDisplayPosition2, yDisplayPosition2, textureAtlasSprite);
            GlStateManager.enableLighting();
            conditionMet2 = true;
         }
      }

      if (!conditionMet2) {
         if (conditionMet) {
            handleInt(xDisplayPosition2, yDisplayPosition2, xDisplayPosition2 + 16, yDisplayPosition2 + 16, -2130706433);
         }

         GlStateManager.enableDepth();
         this.itemRender.renderItemAndEffectIntoGUI(itemStack, xDisplayPosition2, yDisplayPosition2);
         this.itemRender.handleFontRenderer2(this.fontRendererObj, itemStack, xDisplayPosition2, yDisplayPosition2, text);
      }

      this.itemRender.zLevel = 0.0F;
      this.zLevel = 0.0F;
   }

   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
   }

   public GuiContainer(Container inventorySlotsIn) {
      this.inventorySlots = inventorySlotsIn;
      this.ignoreMouseUp = true;
   }

   protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
      if (slotIn != null) {
         slotId = slotIn.slotNumber;
      }

      this.mc.playerController.windowClick(this.inventorySlots.windowId, slotId, clickedButton, clickType, this.mc.thePlayer);
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      if (mouseButton == 0) {
         for (GuiButton guiButton : this.buttonList) {
            if (guiButton.mousePressed(this.mc, mouseX, mouseY)) {
               switch (guiButton.id) {
                  case 1001:
                     ModuleManager.killaura.setEnabled(false);
                     break;
                  case 1002:
                     ModuleManager.inventoryManager.setEnabled(false);
                     break;
                  case 1003:
                     ModuleManager.autoArmor.setEnabled(false);
                     break;
                  case 1004:
                     ModuleManager.chestStealer.setEnabled(false);
               }

               guiButton.playPressSound(this.mc.getSoundHandler());
               return;
            }
         }
      }

      super.mouseClicked(mouseX, mouseY, mouseButton);
      boolean flag = mouseButton == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100;
      Slot slotAtPosition = this.getSlotAtPosition(mouseX, mouseY);
      long systemTime = Minecraft.getSystemTime();
      this.doubleClick = this.lastClickSlot == slotAtPosition && systemTime - this.lastClickTime < 250L && this.lastClickButton == mouseButton;
      this.ignoreMouseUp = false;
      if (mouseButton == 0 || mouseButton == 1 || flag) {
         int guiLeft2 = this.guiLeft;
         int guiTop2 = this.guiTop;
         boolean flag2 = mouseX < guiLeft2 || mouseY < guiTop2 || mouseX >= guiLeft2 + this.xSize || mouseY >= guiTop2 + this.ySize;
         int i = -1;
         if (slotAtPosition != null) {
            i = slotAtPosition.slotNumber;
         }

         if (flag2) {
            i = -999;
         }

         if (this.mc.gameSettings.touchscreen && flag2 && this.mc.thePlayer.inventory.getItemStack() == null) {
            this.mc.displayGuiScreen((GuiScreen)null);
            return;
         }

         if (i != -1) {
            if (this.mc.gameSettings.touchscreen) {
               if (slotAtPosition != null && slotAtPosition.getHasStack()) {
                  this.clickedSlot = slotAtPosition;
                  this.draggedStack = null;
                  this.isRightMouseClick = mouseButton == 1;
               } else {
                  this.clickedSlot = null;
               }
            } else if (!this.dragSplitting) {
               if (this.mc.thePlayer.inventory.getItemStack() == null) {
                  if (mouseButton == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
                     this.handleMouseClick(slotAtPosition, i, mouseButton, 3);
                  } else {
                     boolean flag3 = i != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                     byte b = 0;
                     if (flag3) {
                        this.shiftClickedSlot = slotAtPosition != null && slotAtPosition.getHasStack() ? slotAtPosition.getStack() : null;
                        b = 1;
                     } else if (i == -999) {
                        b = 4;
                     }

                     this.handleMouseClick(slotAtPosition, i, mouseButton, b);
                  }

                  this.ignoreMouseUp = true;
               } else {
                  this.dragSplitting = true;
                  this.dragSplittingButton = mouseButton;
                  this.dragSplittingSlots.clear();
                  if (mouseButton == 0) {
                     this.dragSplittingLimit = 0;
                  } else if (mouseButton == 1) {
                     this.dragSplittingLimit = 1;
                  } else if (mouseButton == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
                     this.dragSplittingLimit = 2;
                  }
               }
            }
         }
      }

      this.lastClickSlot = slotAtPosition;
      this.lastClickTime = systemTime;
      this.lastClickButton = mouseButton;
   }

   @Override
   protected void mouseReleased(int mouseX, int mouseY, int state) {
      Slot slotAtPosition = this.getSlotAtPosition(mouseX, mouseY);
      int guiLeft2 = this.guiLeft;
      int guiTop2 = this.guiTop;
      boolean flag = mouseX < guiLeft2 || mouseY < guiTop2 || mouseX >= guiLeft2 + this.xSize || mouseY >= guiTop2 + this.ySize;
      int i = -1;
      if (slotAtPosition != null) {
         i = slotAtPosition.slotNumber;
      }

      if (flag) {
         i = -999;
      }

      if (this.doubleClick && slotAtPosition != null && state == 0 && this.inventorySlots.canMergeSlot((ItemStack)null, slotAtPosition)) {
         if (isShiftKeyDown()) {
            if (slotAtPosition != null && slotAtPosition.inventory != null && this.shiftClickedSlot != null) {
               for (Slot slot2 : this.inventorySlots.inventorySlots) {
                  if (slot2 != null
                     && slot2.canTakeStack(this.mc.thePlayer)
                     && slot2.getHasStack()
                     && slot2.inventory == slotAtPosition.inventory
                     && Container.canAddItemToSlot(slot2, this.shiftClickedSlot, true)) {
                     this.handleMouseClick(slot2, slot2.slotNumber, state, 1);
                  }
               }
            }
         } else {
            this.handleMouseClick(slotAtPosition, i, state, 6);
         }

         this.doubleClick = false;
         this.lastClickTime = 0L;
      } else {
         if (this.dragSplitting && this.dragSplittingButton != state) {
            this.dragSplitting = false;
            this.dragSplittingSlots.clear();
            this.ignoreMouseUp = true;
            return;
         }

         if (this.ignoreMouseUp) {
            this.ignoreMouseUp = false;
            return;
         }

         if (this.clickedSlot != null && this.mc.gameSettings.touchscreen) {
            if (state == 0 || state == 1) {
               if (this.draggedStack == null && slotAtPosition != this.clickedSlot) {
                  this.draggedStack = this.clickedSlot.getStack();
               }

               boolean addItemToSlot = Container.canAddItemToSlot(slotAtPosition, this.draggedStack, false);
               if (i != -1 && this.draggedStack != null && addItemToSlot) {
                  this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, state, 0);
                  this.handleMouseClick(slotAtPosition, i, 0, 0);
                  if (this.mc.thePlayer.inventory.getItemStack() != null) {
                     this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, state, 0);
                     this.touchUpX = mouseX - guiLeft2;
                     this.touchUpY = mouseY - guiTop2;
                     this.returningStackDestSlot = this.clickedSlot;
                     this.returningStack = this.draggedStack;
                     this.returningStackTime = Minecraft.getSystemTime();
                  } else {
                     this.returningStack = null;
                  }
               } else if (this.draggedStack != null) {
                  this.touchUpX = mouseX - guiLeft2;
                  this.touchUpY = mouseY - guiTop2;
                  this.returningStackDestSlot = this.clickedSlot;
                  this.returningStack = this.draggedStack;
                  this.returningStackTime = Minecraft.getSystemTime();
               }

               this.draggedStack = null;
               this.clickedSlot = null;
            }
         } else if (this.dragSplitting && !this.dragSplittingSlots.isEmpty()) {
            this.handleMouseClick((Slot)null, -999, Container.getQuickcraftMask(0, this.dragSplittingLimit), 5);

            for (Slot slot3 : this.dragSplittingSlots) {
               this.handleMouseClick(slot3, slot3.slotNumber, Container.getQuickcraftMask(1, this.dragSplittingLimit), 5);
            }

            this.handleMouseClick((Slot)null, -999, Container.getQuickcraftMask(2, this.dragSplittingLimit), 5);
         } else if (this.mc.thePlayer.inventory.getItemStack() != null) {
            if (state == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
               this.handleMouseClick(slotAtPosition, i, state, 3);
            } else {
               boolean flag2 = i != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
               if (flag2) {
                  this.shiftClickedSlot = slotAtPosition != null && slotAtPosition.getHasStack() ? slotAtPosition.getStack() : null;
               }

               this.handleMouseClick(slotAtPosition, i, state, flag2 ? 1 : 0);
            }
         }
      }

      if (this.mc.thePlayer.inventory.getItemStack() == null) {
         this.lastClickTime = 0L;
      }

      this.dragSplitting = false;
   }

   @Override
   public void initGui() {
      super.initGui();
      this.mc.thePlayer.openContainer = this.inventorySlots;
      this.guiLeft = (this.width - this.xSize) / 2;
      this.guiTop = (this.height - this.ySize) / 2;
      this.buttonList.add(new GuiButton(1001, 3, 3, 126, 20, "Disable Killaura"));
      this.buttonList.add(new GuiButton(1003, 3, 24, 126, 20, "Disable AutoArmor"));
      this.buttonList.add(new GuiButton(1002, 3, 45, 126, 20, "Disable InvManager"));
      this.buttonList.add(new GuiButton(1004, 3, 66, 126, 20, "Disable ChestStealer"));
   }

   @Override
   public void startClosing() {
      if (this.mc.thePlayer != null) {
         this.inventorySlots.onContainerClosed(this.mc.thePlayer);
      }
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if (keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
         this.mc.thePlayer.closeScreen();
      }

      this.checkHotbarKeys(keyCode);
      if (this.theSlot != null && this.theSlot.getHasStack()) {
         if (keyCode == this.mc.gameSettings.keyBindPickBlock.getKeyCode()) {
            this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, 0, 3);
         } else if (keyCode == this.mc.gameSettings.keyBindSwapItem.getKeyCode()) {
            this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, isCtrlKeyDown() ? 1 : 0, 4);
         }
      }
   }

   @Override
   protected void handleInt7(int i, int i2, int i3) {
      Slot slotAtPosition = this.getSlotAtPosition(i, i2);
      ItemStack itemStack3 = this.mc.thePlayer.inventory.getItemStack();
      if (this.clickedSlot != null && this.mc.gameSettings.touchscreen) {
         if (i3 == 0 || i3 == 1) {
            if (this.draggedStack == null) {
               if (slotAtPosition != this.clickedSlot && this.clickedSlot.getStack() != null) {
                  this.draggedStack = this.clickedSlot.getStack().copy();
               }
            } else if (this.draggedStack.stackSize > 1 && slotAtPosition != null && Container.canAddItemToSlot(slotAtPosition, this.draggedStack, false)) {
               long systemTime = Minecraft.getSystemTime();
               if (this.currentDragTargetSlot == slotAtPosition) {
                  if (systemTime - this.dragItemDropDelay > 500L) {
                     this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
                     this.handleMouseClick(slotAtPosition, slotAtPosition.slotNumber, 1, 0);
                     this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
                     this.dragItemDropDelay = systemTime + 750L;
                     this.draggedStack.stackSize--;
                  }
               } else {
                  this.currentDragTargetSlot = slotAtPosition;
                  this.dragItemDropDelay = systemTime;
               }
            }
         }
      } else if (this.dragSplitting
         && slotAtPosition != null
         && itemStack3 != null
         && itemStack3.stackSize > this.dragSplittingSlots.size()
         && Container.canAddItemToSlot(slotAtPosition, itemStack3, true)
         && slotAtPosition.isItemValid(itemStack3)
         && this.inventorySlots.canDragIntoSlot(slotAtPosition)) {
         this.dragSplittingSlots.add(slotAtPosition);
         this.updateDragSplitting();
      }
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      if (!this.mc.thePlayer.isEntityAlive() || this.mc.thePlayer.isDead) {
         this.mc.thePlayer.closeScreen();
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      int guiLeft2 = this.guiLeft;
      int guiTop2 = this.guiTop;
      this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
      GlStateManager.disableRescaleNormal();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();
      super.drawScreen(mouseX, mouseY, partialTicks);
      RenderHelper.enableGUIStandardItemLighting();
      GlStateManager.pushMatrix();
      GlStateManager.translate(guiLeft2, guiTop2, 0.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableRescaleNormal();
      this.theSlot = null;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240 / 1.0F, 240 / 1.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

      for (int i = 0; i < this.inventorySlots.inventorySlots.size(); i++) {
         Slot slot2 = this.inventorySlots.inventorySlots.get(i);
         this.drawSlot(slot2);
         if (this.isMouseOverSlot(slot2, mouseX, mouseY) && slot2.canBeHovered()) {
            this.theSlot = slot2;
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int xDisplayPosition2 = slot2.xDisplayPosition;
            int yDisplayPosition2 = slot2.yDisplayPosition;
            GlStateManager.colorMask(true, true, true, false);
            this.drawGradientRect(xDisplayPosition2, yDisplayPosition2, xDisplayPosition2 + 16, yDisplayPosition2 + 16, -2130706433, -2130706433);
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
         }
      }

      RenderHelper.disableStandardItemLighting();
      this.drawGuiContainerForegroundLayer(mouseX, mouseY);
      RenderHelper.enableGUIStandardItemLighting();
      InventoryPlayer inventory2 = this.mc.thePlayer.inventory;
      ItemStack itemStack3 = this.draggedStack == null ? inventory2.getItemStack() : this.draggedStack;
      if (itemStack3 != null) {
         int i2 = this.draggedStack == null ? 8 : 16;
         String text = null;
         if (this.draggedStack != null && this.isRightMouseClick) {
            itemStack3 = itemStack3.copy();
            itemStack3.stackSize = MathHelper.ceiling_float_int(itemStack3.stackSize / 2.0F);
         } else if (this.dragSplitting && this.dragSplittingSlots.size() > 1) {
            itemStack3 = itemStack3.copy();
            itemStack3.stackSize = this.dragSplittingRemnant;
            if (itemStack3.stackSize == 0) {
               text = EnumChatFormatting.YELLOW + "0";
            }
         }

         this.drawItemStack(itemStack3, mouseX - guiLeft2 - 8, mouseY - guiTop2 - i2, text);
      }

      if (this.returningStack != null) {
         float f = (float)(Minecraft.getSystemTime() - this.returningStackTime) / 100.0F;
         if (f >= 1.0F) {
            f = 1.0F;
            this.returningStack = null;
         }

         int i3 = this.returningStackDestSlot.xDisplayPosition - this.touchUpX;
         int i4 = this.returningStackDestSlot.yDisplayPosition - this.touchUpY;
         int i5 = this.touchUpX + (int)(i3 * f);
         int i6 = this.touchUpY + (int)(i4 * f);
         this.drawItemStack(this.returningStack, i5, i6, (String)null);
      }

      GlStateManager.popMatrix();
      if (inventory2.getItemStack() == null && this.theSlot != null && this.theSlot.getHasStack()) {
         ItemStack stack2 = this.theSlot.getStack();
         this.renderToolTip(stack2, mouseX, mouseY);
      }

      GlStateManager.enableLighting();
      GlStateManager.enableDepth();
      RenderHelper.enableStandardItemLighting();
   }
}
