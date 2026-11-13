package net.minecraft.client.gui.inventory;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiContainerCreative extends InventoryEffectRenderer {
   private static final ResourceLocation creativeInventoryTabs = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
   private GuiTextField searchField;
   private List<Slot> originalSlots;
   private Slot destroyItemSlot;
   private static InventoryBasic creativeInventory = new InventoryBasic("tmp", true, 45);
   private static int selectedTabIndex = CreativeTabs.tabBlock.getTabIndex();
   private boolean wasClicking;
   private CreativeCrafting creativeCraftingListener;
   private float currentScroll;
   private boolean isScrolling;
   private boolean clearSearch;

   protected boolean renderCreativeInventoryHoveringText(CreativeTabs creativeTabs, int i, int i2) {
      int tabColumn = creativeTabs.getTabColumn();
      int i3 = 28 * tabColumn;
      if (tabColumn == 5) {
         i3 = this.xSize - 28 + 2;
      } else if (tabColumn > 0) {
         i3 += tabColumn;
      }

      int i4;
      if (creativeTabs.isTabInFirstRow()) {
         i4 = -32;
      } else {
         i4 = 0 + this.ySize;
      }

      if (this.isPointInRegion(i3 + 3, i4 + 3, 23, 27, i, i2)) {
         this.drawCreativeTabHoveringText(I18n.format(creativeTabs.getTranslatedTabLabel()), i, i2);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void initGui() {
      if (this.mc.playerController.isInCreativeMode()) {
         super.initGui();
         this.buttonList.clear();
         Keyboard.enableRepeatEvents(true);
         this.searchField = new GuiTextField(0, this.fontRendererObj, this.guiLeft + 82, this.guiTop + 6, 89, this.fontRendererObj.FONT_HEIGHT);
         this.searchField.setMaxStringLength(15);
         this.searchField.update();
         this.searchField.setVisible(false);
         this.searchField.setTextColor(16777215);
         int selectedTabIndex2 = selectedTabIndex;
         selectedTabIndex = -1;
         this.setCurrentCreativeTab(CreativeTabs.creativeTabArray[selectedTabIndex2]);
         this.creativeCraftingListener = new CreativeCrafting(this.mc);
         this.mc.thePlayer.inventoryContainer.onCraftGuiOpened(this.creativeCraftingListener);
      } else {
         this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
      }
   }

   protected void drawTab(CreativeTabs tab) {
      boolean flag = tab.getTabIndex() == selectedTabIndex;
      boolean tabInFirstRow = tab.isTabInFirstRow();
      int tabColumn = tab.getTabColumn();
      int i = tabColumn * 28;
      int i2 = 0;
      int i3 = this.guiLeft + 28 * tabColumn;
      int guiTop2 = this.guiTop;
      if (flag) {
         i2 += 32;
      }

      if (tabColumn == 5) {
         i3 = this.guiLeft + this.xSize - 28;
      } else if (tabColumn > 0) {
         i3 += tabColumn;
      }

      if (tabInFirstRow) {
         guiTop2 -= 28;
      } else {
         i2 += 64;
         guiTop2 += this.ySize - 4;
      }

      GlStateManager.disableLighting();
      this.drawTexturedModalRect(i3, guiTop2, i, i2, 28, 32);
      this.zLevel = 100.0F;
      this.itemRender.zLevel = 100.0F;
      i3 += 6;
      guiTop2 = guiTop2 + 8 + (tabInFirstRow ? 1 : -1);
      GlStateManager.enableLighting();
      GlStateManager.enableRescaleNormal();
      ItemStack iconItemStack = tab.getIconItemStack();
      this.itemRender.renderItemAndEffectIntoGUI(iconItemStack, i3, guiTop2);
      this.itemRender.handleFontRenderer(this.fontRendererObj, iconItemStack, i3, guiTop2);
      GlStateManager.disableLighting();
      this.itemRender.zLevel = 0.0F;
      this.zLevel = 0.0F;
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

   public int getSelectedTabIndex() {
      return selectedTabIndex;
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if (selectedTabIndex != CreativeTabs.tabAllSearch.getTabIndex()) {
         if (GameSettings.isKeyDown(this.mc.gameSettings.keyBinding)) {
            this.setCurrentCreativeTab(CreativeTabs.tabAllSearch);
         } else {
            super.keyTyped(typedChar, keyCode);
         }
      } else {
         if (this.clearSearch) {
            this.clearSearch = false;
            this.searchField.setText("");
         }

         if (!this.checkHotbarKeys(keyCode)) {
            if (this.searchField.textboxKeyTyped(typedChar, keyCode)) {
               this.updateCreativeSearch();
            } else {
               super.keyTyped(typedChar, keyCode);
            }
         }
      }
   }

   @Override
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      CreativeTabs creativeTabs = CreativeTabs.creativeTabArray[selectedTabIndex];
      if (creativeTabs.drawInForegroundOfTab()) {
         GlStateManager.disableBlend();
         this.fontRendererObj.getInt(I18n.format(creativeTabs.getTranslatedTabLabel()), 8, 6, 4210752);
      }
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      if (mouseButton == 0) {
         int i = mouseX - this.guiLeft;
         int i2 = mouseY - this.guiTop;

         for (CreativeTabs creativeTabs : CreativeTabs.creativeTabArray) {
            if (this.isMouseOverTab(creativeTabs, i, i2)) {
               return;
            }
         }
      }

      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void updateScreen() {
      if (!this.mc.playerController.isInCreativeMode()) {
         this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
      }

      this.updateActivePotionEffects();
   }

   @Override
   protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
      this.clearSearch = true;
      boolean flag = clickType == 1;
      clickType = slotId == -999 && clickType == 0 ? 4 : clickType;
      if (slotIn == null && selectedTabIndex != CreativeTabs.tabInventory.getTabIndex() && clickType != 5) {
         InventoryPlayer inventory2 = this.mc.thePlayer.inventory;
         if (inventory2.getItemStack() != null) {
            if (clickedButton == 0) {
               this.mc.thePlayer.getEntityItem3(inventory2.getItemStack());
               this.mc.playerController.sendPacketDropItem(inventory2.getItemStack());
               inventory2.setItemStack((ItemStack)null);
            }

            if (clickedButton == 1) {
               ItemStack itemStack = inventory2.getItemStack().splitStack(1);
               this.mc.thePlayer.getEntityItem3(itemStack);
               this.mc.playerController.sendPacketDropItem(itemStack);
               if (inventory2.getItemStack().stackSize == 0) {
                  inventory2.setItemStack((ItemStack)null);
               }
            }
         }
      } else if (slotIn == this.destroyItemSlot && flag) {
         for (int i = 0; i < this.mc.thePlayer.inventoryContainer.getInventory().size(); i++) {
            this.mc.playerController.sendSlotPacket((ItemStack)null, i);
         }
      } else if (selectedTabIndex == CreativeTabs.tabInventory.getTabIndex()) {
         if (slotIn == this.destroyItemSlot) {
            this.mc.thePlayer.inventory.setItemStack((ItemStack)null);
         } else if (clickType == 4 && slotIn != null && slotIn.getHasStack()) {
            ItemStack itemStack2 = slotIn.decrStackSize(clickedButton == 0 ? 1 : slotIn.getStack().getMaxStackSize());
            this.mc.thePlayer.getEntityItem3(itemStack2);
            this.mc.playerController.sendPacketDropItem(itemStack2);
         } else if (clickType == 4 && this.mc.thePlayer.inventory.getItemStack() != null) {
            this.mc.thePlayer.getEntityItem3(this.mc.thePlayer.inventory.getItemStack());
            this.mc.playerController.sendPacketDropItem(this.mc.thePlayer.inventory.getItemStack());
            this.mc.thePlayer.inventory.setItemStack((ItemStack)null);
         } else {
            this.mc
               .thePlayer
               .inventoryContainer
               .slotClick(slotIn == null ? slotId : ((GuiContainerCreative.CreativeSlot)slotIn).slot.slotNumber, clickedButton, clickType, this.mc.thePlayer);
            this.mc.thePlayer.inventoryContainer.detectAndSendChanges();
         }
      } else if (clickType != 5 && slotIn.inventory == creativeInventory) {
         InventoryPlayer inventory3 = this.mc.thePlayer.inventory;
         ItemStack itemStack3 = inventory3.getItemStack();
         ItemStack stack2 = slotIn.getStack();
         if (clickType == 2) {
            if (stack2 != null && clickedButton >= 0 && clickedButton < 9) {
               ItemStack itemStack4 = stack2.copy();
               itemStack4.stackSize = itemStack4.getMaxStackSize();
               this.mc.thePlayer.inventory.setInventorySlotContents(clickedButton, itemStack4);
               this.mc.thePlayer.inventoryContainer.detectAndSendChanges();
            }

            return;
         }

         if (clickType == 3) {
            if (inventory3.getItemStack() == null && slotIn.getHasStack()) {
               ItemStack itemStack5 = slotIn.getStack().copy();
               itemStack5.stackSize = itemStack5.getMaxStackSize();
               inventory3.setItemStack(itemStack5);
            }

            return;
         }

         if (clickType == 4) {
            if (stack2 != null) {
               ItemStack itemStack6 = stack2.copy();
               itemStack6.stackSize = clickedButton == 0 ? 1 : itemStack6.getMaxStackSize();
               this.mc.thePlayer.getEntityItem3(itemStack6);
               this.mc.playerController.sendPacketDropItem(itemStack6);
            }

            return;
         }

         if (itemStack3 != null && stack2 != null && itemStack3.isItemEqual(stack2)) {
            if (clickedButton == 0) {
               if (flag) {
                  itemStack3.stackSize = itemStack3.getMaxStackSize();
               } else if (itemStack3.stackSize < itemStack3.getMaxStackSize()) {
                  itemStack3.stackSize++;
               }
            } else if (itemStack3.stackSize <= 1) {
               inventory3.setItemStack((ItemStack)null);
            } else {
               itemStack3.stackSize--;
            }
         } else if (stack2 != null && itemStack3 == null) {
            inventory3.setItemStack(ItemStack.copyItemStack(stack2));
            itemStack3 = inventory3.getItemStack();
            if (flag) {
               itemStack3.stackSize = itemStack3.getMaxStackSize();
            }
         } else {
            inventory3.setItemStack((ItemStack)null);
         }
      } else {
         this.inventorySlots.slotClick(slotIn == null ? slotId : slotIn.slotNumber, clickedButton, clickType, this.mc.thePlayer);
         if (Container.getDragEvent(clickedButton) == 2) {
            for (int i2 = 0; i2 < 9; i2++) {
               this.mc.playerController.sendSlotPacket(this.inventorySlots.getSlot(45 + i2).getStack(), 36 + i2);
            }
         } else if (slotIn != null) {
            ItemStack stack3 = this.inventorySlots.getSlot(slotIn.slotNumber).getStack();
            this.mc.playerController.sendSlotPacket(stack3, slotIn.slotNumber - this.inventorySlots.inventorySlots.size() + 9 + 36);
         }
      }
   }

   private boolean needsScrollBars() {
      return selectedTabIndex != CreativeTabs.tabInventory.getTabIndex()
         && CreativeTabs.creativeTabArray[selectedTabIndex].shouldHidePlayerInventory()
         && ((GuiContainerCreative.ContainerCreative)this.inventorySlots).canScroll();
   }

   public GuiContainerCreative(EntityPlayer entityPlayer) {
      super(new GuiContainerCreative.ContainerCreative(entityPlayer));
      entityPlayer.openContainer = this.inventorySlots;
      this.allowUserInput = true;
      this.ySize = 136;
      this.xSize = 195;
   }

   private void updateCreativeSearch() {
      GuiContainerCreative.ContainerCreative containerCreative = (GuiContainerCreative.ContainerCreative)this.inventorySlots;
      containerCreative.itemList.clear();

      for (Item item2 : Item.itemRegistry) {
         if (item2 != null && item2.getCreativeTab() != null) {
            item2.getSubItems(item2, (CreativeTabs)null, containerCreative.itemList);
         }
      }

      for (Enchantment enchantment2 : Enchantment.enchantmentsBookList) {
         if (enchantment2 != null && enchantment2.type != null) {
            Items.enchanted_book.getAll(enchantment2, containerCreative.itemList);
         }
      }

      Iterator iterator2 = containerCreative.itemList.iterator();
      String text = this.searchField.getText().toLowerCase();

      while (iterator2.hasNext()) {
         ItemStack itemStack = (ItemStack)iterator2.next();
         boolean flag = false;
         Iterator iterator3 = itemStack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips).iterator();

         while (iterator3.hasNext()) {
            if (EnumChatFormatting.getTextWithoutFormattingCodes((String)iterator3.next()).toLowerCase().contains(text)) {
               flag = true;
               break;
            }
         }

         if (!flag) {
            iterator2.remove();
         }
      }

      this.currentScroll = 0.0F;
      containerCreative.scrollTo(0.0F);
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      int eventDWheel = Mouse.getEventDWheel();
      if (eventDWheel != 0 && this.needsScrollBars()) {
         int count = ((GuiContainerCreative.ContainerCreative)this.inventorySlots).itemList.size() / 9 - 5;
         if (eventDWheel > 0) {
            eventDWheel = 1;
         }

         if (eventDWheel < 0) {
            eventDWheel = -1;
         }

         this.currentScroll = (float)(this.currentScroll - (double)eventDWheel / count);
         this.currentScroll = MathHelper.clamp_float(this.currentScroll, 0.0F, 1.0F);
         ((GuiContainerCreative.ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
      }
   }

   protected boolean isMouseOverTab(CreativeTabs tab, int mouseX, int mouseY) {
      int tabColumn = tab.getTabColumn();
      int i = 28 * tabColumn;
      if (tabColumn == 5) {
         i = this.xSize - 28 + 2;
      } else if (tabColumn > 0) {
         i += tabColumn;
      }

      int i2;
      if (tab.isTabInFirstRow()) {
         i2 = -32;
      } else {
         i2 = 0 + this.ySize;
      }

      return mouseX >= i && mouseX <= i + 28 && mouseY >= i2 && mouseY <= i2 + 32;
   }

   @Override
   protected void mouseReleased(int mouseX, int mouseY, int state) {
      if (state == 0) {
         int i = mouseX - this.guiLeft;
         int i2 = mouseY - this.guiTop;

         for (CreativeTabs creativeTabs : CreativeTabs.creativeTabArray) {
            if (this.isMouseOverTab(creativeTabs, i, i2)) {
               this.setCurrentCreativeTab(creativeTabs);
               return;
            }
         }
      }

      super.mouseReleased(mouseX, mouseY, state);
   }

   private void setCurrentCreativeTab(CreativeTabs creativeTabs) {
      int selectedTabIndex2 = selectedTabIndex;
      selectedTabIndex = creativeTabs.getTabIndex();
      GuiContainerCreative.ContainerCreative guicontainercreative$containercreative = (GuiContainerCreative.ContainerCreative)this.inventorySlots;
      this.dragSplittingSlots.clear();
      guicontainercreative$containercreative.itemList.clear();
      creativeTabs.displayAllRelevantItems(guicontainercreative$containercreative.itemList);
      if (creativeTabs == CreativeTabs.tabInventory) {
         Container container = this.mc.thePlayer.inventoryContainer;
         if (this.originalSlots == null) {
            this.originalSlots = guicontainercreative$containercreative.inventorySlots;
         }

         guicontainercreative$containercreative.inventorySlots = Lists.newArrayList();

         for (int innerIndex = 0; innerIndex < container.inventorySlots.size(); innerIndex++) {
            GuiContainerCreative.CreativeSlot creativeSlot = new GuiContainerCreative.CreativeSlot(container.inventorySlots.get(innerIndex), innerIndex);
            guicontainercreative$containercreative.inventorySlots.add(creativeSlot);
            if (innerIndex >= 5 && innerIndex < 9) {
               int i = innerIndex - 5;
               int i2 = i / 2;
               int i3 = i % 2;
               creativeSlot.xDisplayPosition = 9 + i2 * 54;
               creativeSlot.yDisplayPosition = 6 + i3 * 27;
            } else if (innerIndex >= 0 && innerIndex < 5) {
               creativeSlot.yDisplayPosition = -2000;
               creativeSlot.xDisplayPosition = -2000;
            } else if (innerIndex < container.inventorySlots.size()) {
               int i4 = innerIndex - 9;
               int i5 = i4 % 9;
               int i6 = i4 / 9;
               creativeSlot.xDisplayPosition = 9 + i5 * 18;
               if (innerIndex >= 36) {
                  creativeSlot.yDisplayPosition = 112;
               } else {
                  creativeSlot.yDisplayPosition = 54 + i6 * 18;
               }
            }
         }

         this.destroyItemSlot = new Slot(creativeInventory, 0, 173, 112);
         guicontainercreative$containercreative.inventorySlots.add(this.destroyItemSlot);
      } else if (selectedTabIndex2 == CreativeTabs.tabInventory.getTabIndex()) {
         guicontainercreative$containercreative.inventorySlots = this.originalSlots;
         this.originalSlots = null;
      }

      if (this.searchField != null) {
         if (creativeTabs == CreativeTabs.tabAllSearch) {
            this.searchField.setVisible(true);
            this.searchField.setCanLoseFocus(false);
            this.searchField.setFocused(true);
            this.searchField.setText("");
            this.updateCreativeSearch();
         } else {
            this.searchField.setVisible(false);
            this.searchField.setCanLoseFocus(true);
            this.searchField.setFocused(false);
         }
      }

      this.currentScroll = 0.0F;
      guicontainercreative$containercreative.scrollTo(0.0F);
   }

   @Override
   protected void renderToolTip(ItemStack stack, int x, int y) {
      if (selectedTabIndex == CreativeTabs.tabAllSearch.getTabIndex()) {
         List tooltip = stack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);
         CreativeTabs creativeTab = stack.getItem().getCreativeTab();
         if (creativeTab == null && stack.getItem() == Items.enchanted_book) {
            Map enchantments = EnchantmentHelper.getEnchantments(stack);
            if (enchantments.size() == 1) {
               Enchantment enchantmentById = Enchantment.getEnchantmentById((Integer)enchantments.keySet().iterator().next());

               for (CreativeTabs creativeTabs : CreativeTabs.creativeTabArray) {
                  if (creativeTabs.hasRelevantEnchantmentType(enchantmentById.type)) {
                     creativeTab = creativeTabs;
                     break;
                  }
               }
            }
         }

         if (creativeTab != null) {
            tooltip.add(1, "" + EnumChatFormatting.BOLD + EnumChatFormatting.BLUE + I18n.format(creativeTab.getTranslatedTabLabel()));
         }

         for (int i = 0; i < tooltip.size(); i++) {
            if (i == 0) {
               tooltip.set(i, stack.getRarity().rarityColor + (String)tooltip.get(i));
            } else {
               tooltip.set(i, EnumChatFormatting.GRAY + (String)tooltip.get(i));
            }
         }

         this.drawHoveringText(tooltip, x, y);
      } else {
         super.renderToolTip(stack, x, y);
      }
   }

   @Override
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      RenderHelper.enableGUIStandardItemLighting();
      CreativeTabs creativeTabs = CreativeTabs.creativeTabArray[selectedTabIndex];

      for (CreativeTabs creativeTabs2 : CreativeTabs.creativeTabArray) {
         this.mc.getTextureManager().bindTexture(creativeInventoryTabs);
         if (creativeTabs2.getTabIndex() != selectedTabIndex) {
            this.drawTab(creativeTabs2);
         }
      }

      this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/creative_inventory/tab_" + creativeTabs.getBackgroundImageName()));
      this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
      this.searchField.drawTextBox();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      int i = this.guiLeft + 175;
      int i2 = this.guiTop + 18;
      int i3 = i2 + 112;
      this.mc.getTextureManager().bindTexture(creativeInventoryTabs);
      if (creativeTabs.shouldHidePlayerInventory()) {
         this.drawTexturedModalRect(i, i2 + (int)((i3 - i2 - 17) * this.currentScroll), 232 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);
      }

      this.drawTab(creativeTabs);
      if (creativeTabs == CreativeTabs.tabInventory) {
         GuiInventory.drawEntityOnScreen(this.guiLeft + 43, this.guiTop + 45, 20, this.guiLeft + 43 - mouseX, this.guiTop + 45 - 30 - mouseY, this.mc.thePlayer);
      }
   }

   @Override
   public void startClosing() {
      super.startClosing();
      if (this.mc.thePlayer != null && this.mc.thePlayer.inventory != null) {
         this.mc.thePlayer.inventoryContainer.removeCraftingFromCrafters(this.creativeCraftingListener);
      }

      Keyboard.enableRepeatEvents(false);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      boolean buttonDown = Mouse.isButtonDown(0);
      int guiLeft2 = this.guiLeft;
      int guiTop2 = this.guiTop;
      int i = guiLeft2 + 175;
      int i2 = guiTop2 + 18;
      int i3 = i + 14;
      int i4 = i2 + 112;
      if (!this.wasClicking && buttonDown && mouseX >= i && mouseY >= i2 && mouseX < i3 && mouseY < i4) {
         this.isScrolling = this.needsScrollBars();
      }

      if (!buttonDown) {
         this.isScrolling = false;
      }

      this.wasClicking = buttonDown;
      if (this.isScrolling) {
         this.currentScroll = (mouseY - i2 - 7.5F) / (i4 - i2 - 15.0F);
         this.currentScroll = MathHelper.clamp_float(this.currentScroll, 0.0F, 1.0F);
         ((GuiContainerCreative.ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
      }

      super.drawScreen(mouseX, mouseY, partialTicks);

      for (CreativeTabs creativeTabs : CreativeTabs.creativeTabArray) {
         if (this.renderCreativeInventoryHoveringText(creativeTabs, mouseX, mouseY)) {
            break;
         }
      }

      if (this.destroyItemSlot != null
         && selectedTabIndex == CreativeTabs.tabInventory.getTabIndex()
         && this.isPointInRegion(this.destroyItemSlot.xDisplayPosition, this.destroyItemSlot.yDisplayPosition, 16, 16, mouseX, mouseY)) {
         this.drawCreativeTabHoveringText(I18n.format("inventory.binSlot"), mouseX, mouseY);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableLighting();
   }

   @Override
   protected void updateActivePotionEffects() {
      int guiLeft2 = this.guiLeft;
      super.updateActivePotionEffects();
      if (this.searchField != null && this.guiLeft != guiLeft2) {
         this.searchField.xPosition = this.guiLeft + 82;
      }
   }

   static class ContainerCreative extends Container {
      public List<ItemStack> itemList = Lists.newArrayList();

      @Override
      public boolean canDragIntoSlot(Slot slot2) {
         return slot2.inventory instanceof InventoryPlayer || slot2.yDisplayPosition > 90 && slot2.xDisplayPosition <= 162;
      }

      public void scrollTo(float f) {
         int count = (this.itemList.size() + 9 - 1) / 9 - 5;
         int i = (int)(f * count + 0.5);
         if (i < 0) {
            i = 0;
         }

         for (int nestedIndex = 0; nestedIndex < 5; nestedIndex++) {
            for (int outerIndex = 0; outerIndex < 9; outerIndex++) {
               int i2 = outerIndex + (nestedIndex + i) * 9;
               if (i2 >= 0 && i2 < this.itemList.size()) {
                  GuiContainerCreative.creativeInventory.setInventorySlotContents(outerIndex + nestedIndex * 9, this.itemList.get(i2));
               } else {
                  GuiContainerCreative.creativeInventory.setInventorySlotContents(outerIndex + nestedIndex * 9, (ItemStack)null);
               }
            }
         }
      }

      public boolean canScroll() {
         return this.itemList.size() > 45;
      }

      @Override
      public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
         return slotIn.yDisplayPosition > 90;
      }

      public ContainerCreative(EntityPlayer entityPlayer) {
         InventoryPlayer inventoryPlayer = entityPlayer.inventory;

         for (int index = 0; index < 5; index++) {
            for (int innerIndex = 0; innerIndex < 9; innerIndex++) {
               this.addSlotToContainer(new Slot(GuiContainerCreative.creativeInventory, index * 9 + innerIndex, 9 + innerIndex * 18, 18 + index * 18));
            }
         }

         for (int nestedIndex = 0; nestedIndex < 9; nestedIndex++) {
            this.addSlotToContainer(new Slot(inventoryPlayer, nestedIndex, 9 + nestedIndex * 18, 112));
         }

         this.scrollTo(0.0F);
      }

      @Override
      public boolean canInteractWith(EntityPlayer playerIn) {
         return true;
      }

      @Override
      public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
         if (index >= this.inventorySlots.size() - 9 && index < this.inventorySlots.size()) {
            Slot slot = this.inventorySlots.get(index);
            if (slot != null && slot.getHasStack()) {
               slot.putStack((ItemStack)null);
            }
         }

         return null;
      }

      @Override
      protected void handleInt(int i, int i2, EntityPlayer entityPlayer) {
      }
   }

   class CreativeSlot extends Slot {
      private final Slot slot;

      @Override
      public boolean isItemValid(ItemStack stack) {
         return this.slot.isItemValid(stack);
      }

      public CreativeSlot(Slot slot2, int i) {
         super(slot2.inventory, i, 0, 0);
         this.slot = slot2;
      }

      @Override
      public void putStack(ItemStack stack) {
         this.slot.putStack(stack);
      }

      @Override
      public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
         this.slot.onPickupFromSlot(playerIn, stack);
      }

      @Override
      public String getSlotTexture() {
         return this.slot.getSlotTexture();
      }

      @Override
      public void onSlotChanged() {
         this.slot.onSlotChanged();
      }

      @Override
      public int getSlotStackLimit() {
         return this.slot.getSlotStackLimit();
      }

      @Override
      public ItemStack decrStackSize(int amount) {
         return this.slot.decrStackSize(amount);
      }

      @Override
      public boolean getHasStack() {
         return this.slot.getHasStack();
      }

      @Override
      public ItemStack getStack() {
         return this.slot.getStack();
      }

      @Override
      public boolean isHere(IInventory inv, int slotIn) {
         return this.slot.isHere(inv, slotIn);
      }

      @Override
      public int getItemStackLimit(ItemStack stack) {
         return this.slot.getItemStackLimit(stack);
      }
   }
}
