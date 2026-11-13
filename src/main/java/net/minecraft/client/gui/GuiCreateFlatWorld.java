package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;

public class GuiCreateFlatWorld extends GuiScreen {
   private GuiButton addLayerButton;
   private FlatGeneratorInfo theFlatGeneratorInfo = FlatGeneratorInfo.getDefaultFlatGenerator();
   private String flatWorldTitle;
   private String tileLabel;
   private final GuiCreateWorld createWorldGui;
   private GuiButton removeLayerButton;
   private GuiButton editLayerButton;
   private String heightLabel;
   private GuiCreateFlatWorld.Details createFlatWorldListSlotGui;

   public GuiCreateFlatWorld(GuiCreateWorld createWorldGuiIn, String presetString) {
      this.createWorldGui = createWorldGuiIn;
      this.setPresetString(presetString);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.createFlatWorldListSlotGui.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRendererObj, this.flatWorldTitle, this.width / 2, 8, 16777215);
      int i = this.width / 2 - 92 - 16;
      this.drawString(this.fontRendererObj, this.tileLabel, i, 32, 16777215);
      this.drawString(this.fontRendererObj, this.heightLabel, i + 2 + 213 - this.fontRendererObj.getInt3(this.heightLabel), 32, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      int count = this.theFlatGeneratorInfo.getFlatLayers().size() - this.createFlatWorldListSlotGui.selectedIndex - 1;
      if (button.id == 1) {
         this.mc.displayGuiScreen(this.createWorldGui);
      } else if (button.id == 0) {
         this.createWorldGui.chunkProviderSettingsJson = this.getPresetString();
         this.mc.displayGuiScreen(this.createWorldGui);
      } else if (button.id == 5) {
         this.mc.displayGuiScreen(new GuiFlatPresets(this));
      } else if (button.id == 4 && this.canModifySelection()) {
         this.theFlatGeneratorInfo.getFlatLayers().remove(count);
         this.createFlatWorldListSlotGui.selectedIndex = Math.min(
            this.createFlatWorldListSlotGui.selectedIndex, this.theFlatGeneratorInfo.getFlatLayers().size() - 1
         );
      }

      this.theFlatGeneratorInfo.updateLayerYValues();
      this.updateButtonState();
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.createFlatWorldListSlotGui.handleMouseInput();
   }

   private boolean canModifySelection() {
      return this.createFlatWorldListSlotGui.selectedIndex > -1
         && this.createFlatWorldListSlotGui.selectedIndex < this.theFlatGeneratorInfo.getFlatLayers().size();
   }

   public String getPresetString() {
      return this.theFlatGeneratorInfo.toString();
   }

   public void updateButtonState() {
      boolean canModifySelection = this.canModifySelection();
      this.removeLayerButton.enabled = canModifySelection;
      this.editLayerButton.enabled = canModifySelection;
      this.editLayerButton.enabled = false;
      this.addLayerButton.enabled = false;
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      this.flatWorldTitle = I18n.format("createWorld.customize.flat.title");
      this.tileLabel = I18n.format("createWorld.customize.flat.tile");
      this.heightLabel = I18n.format("createWorld.customize.flat.height");
      this.createFlatWorldListSlotGui = new GuiCreateFlatWorld.Details();
      this.buttonList
         .add(
            this.addLayerButton = new GuiButton(
               2, this.width / 2 - 154, this.height - 52, 100, 20, I18n.format("createWorld.customize.flat.addLayer") + " (NYI)"
            )
         );
      this.buttonList
         .add(
            this.editLayerButton = new GuiButton(
               3, this.width / 2 - 50, this.height - 52, 100, 20, I18n.format("createWorld.customize.flat.editLayer") + " (NYI)"
            )
         );
      this.buttonList
         .add(this.removeLayerButton = new GuiButton(4, this.width / 2 - 155, this.height - 52, 150, 20, I18n.format("createWorld.customize.flat.removeLayer")));
      this.buttonList.add(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("gui.done")));
      this.buttonList.add(new GuiButton(5, this.width / 2 + 5, this.height - 52, 150, 20, I18n.format("createWorld.customize.presets")));
      this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")));
      this.addLayerButton.visible = this.editLayerButton.visible = false;
      this.theFlatGeneratorInfo.updateLayerYValues();
      this.updateButtonState();
   }

   public void setPresetString(String presetString) {
      this.theFlatGeneratorInfo = FlatGeneratorInfo.createFlatGeneratorFromString(presetString);
   }

   class Details extends GuiSlot {
      public int selectedIndex = -1;

      public Details() {
         super(GuiCreateFlatWorld.this.mc, GuiCreateFlatWorld.this.width, GuiCreateFlatWorld.this.height, 43, GuiCreateFlatWorld.this.height - 60, 24);
      }

      @Override
      protected void drawBackground() {
      }

      @Override
      protected int getScrollBarX() {
         return this.width - 70;
      }

      @Override
      protected boolean isSelected(int slotIndex) {
         return slotIndex == this.selectedIndex;
      }

      @Override
      protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
         this.selectedIndex = slotIndex;
         GuiCreateFlatWorld.this.updateButtonState();
      }

      private void drawItemStack(int x, int y, ItemStack itemStack) {
         this.drawItemStackBackground(x + 1, y + 1);
         GlStateManager.enableRescaleNormal();
         if (itemStack != null && itemStack.getItem() != null) {
            RenderHelper.enableGUIStandardItemLighting();
            GuiCreateFlatWorld.this.itemRender.handleItemStack(itemStack, x + 2, y + 2);
            RenderHelper.disableStandardItemLighting();
         }

         GlStateManager.disableRescaleNormal();
      }

      @Override
      protected void drawSlot(int entryID, int x, int y, int z, int mouseXIn, int mouseYIn) {
         FlatLayerInfo flatLayerInfo = GuiCreateFlatWorld.this.theFlatGeneratorInfo
            .getFlatLayers()
            .get(GuiCreateFlatWorld.this.theFlatGeneratorInfo.getFlatLayers().size() - entryID - 1);
         IBlockState layerMaterial = flatLayerInfo.getLayerMaterial();
         Block block2 = layerMaterial.getBlock();
         Item itemFromBlock = Item.getItemFromBlock(block2);
         ItemStack itemStack2 = block2 != Blocks.air && itemFromBlock != null ? new ItemStack(itemFromBlock, 1, block2.getMetaFromState(layerMaterial)) : null;
         String text = itemStack2 == null ? "Air" : itemFromBlock.getItemStackDisplayName(itemStack2);
         if (itemFromBlock == null) {
            if (block2 == Blocks.water || block2 == Blocks.flowing_water) {
               itemFromBlock = Items.water_bucket;
            } else if (block2 == Blocks.lava || block2 == Blocks.flowing_lava) {
               itemFromBlock = Items.lava_bucket;
            }

            if (itemFromBlock != null) {
               itemStack2 = new ItemStack(itemFromBlock, 1, block2.getMetaFromState(layerMaterial));
               text = block2.getLocalizedName();
            }
         }

         this.drawItemStack(x, y, itemStack2);
         GuiCreateFlatWorld.this.fontRendererObj.getInt(text, x + 18 + 5, y + 3, 16777215);
         String text2;
         if (entryID == 0) {
            text2 = I18n.format("createWorld.customize.flat.layer.top", flatLayerInfo.getLayerCount());
         } else if (entryID == GuiCreateFlatWorld.this.theFlatGeneratorInfo.getFlatLayers().size() - 1) {
            text2 = I18n.format("createWorld.customize.flat.layer.bottom", flatLayerInfo.getLayerCount());
         } else {
            text2 = I18n.format("createWorld.customize.flat.layer", flatLayerInfo.getLayerCount());
         }

         GuiCreateFlatWorld.this.fontRendererObj.getInt(text2, x + 2 + 213 - GuiCreateFlatWorld.this.fontRendererObj.getInt3(text2), y + 3, 16777215);
      }

      private void handleInt(int i, int i2) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(Gui.statIcons);
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldRenderer.pos(i + 0, i2 + 18, GuiCreateFlatWorld.this.zLevel).tex(0 * 0.0078125F, 18 * 0.0078125F).endVertex();
         worldRenderer.pos(i + 18, i2 + 18, GuiCreateFlatWorld.this.zLevel).tex(18 * 0.0078125F, 18 * 0.0078125F).endVertex();
         worldRenderer.pos(i + 18, i2 + 0, GuiCreateFlatWorld.this.zLevel).tex(18 * 0.0078125F, 0 * 0.0078125F).endVertex();
         worldRenderer.pos(i + 0, i2 + 0, GuiCreateFlatWorld.this.zLevel).tex(0 * 0.0078125F, 0 * 0.0078125F).endVertex();
         instance.draw();
      }

      @Override
      protected int getSize() {
         return GuiCreateFlatWorld.this.theFlatGeneratorInfo.getFlatLayers().size();
      }

      private void drawItemStackBackground(int x, int y) {
         this.handleInt(x, y);
      }
   }
}
