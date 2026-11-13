package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;
import org.lwjgl.input.Keyboard;

public class GuiFlatPresets extends GuiScreen {
   private String presetsShare;
   private static final List<GuiFlatPresets.LayerItem> FLAT_WORLD_PRESETS = Lists.newArrayList();
   private GuiTextField presetField;
   private String presetsListLabel;
   private GuiFlatPresets.ListSlot presetList;
   private GuiButton selectButton;
   private String presetsTitle;
   private final GuiCreateFlatWorld parentScreen;

   private boolean canSelectPreset() {
      return this.presetList.selectedIndex > -1 && this.presetList.selectedIndex < FLAT_WORLD_PRESETS.size() || this.presetField.getText().length() > 1;
   }

   public void updateButtonState() {
      boolean canSelectPreset = this.canSelectPreset();
      this.selectButton.enabled = canSelectPreset;
   }

   private static void addPresetWithFeatures(String name, Item item, BiomeGenBase biome, List<String> features, FlatLayerInfo... layers) {
      addPresetInternal(name, item, 0, biome, features, layers);
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      this.presetField.mouseClicked(mouseX, mouseY, mouseButton);
      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   static {
      addPresetWithFeatures(
         "Classic Flat",
         Item.getItemFromBlock(Blocks.grass),
         BiomeGenBase.plains,
         Arrays.asList("village"),
         new FlatLayerInfo(1, Blocks.grass),
         new FlatLayerInfo(2, Blocks.dirt),
         new FlatLayerInfo(1, Blocks.bedrock)
      );
      addPresetWithFeatures(
         "Tunnelers' Dream",
         Item.getItemFromBlock(Blocks.stone),
         BiomeGenBase.extremeHills,
         Arrays.asList("biome_1", "dungeon", "decoration", "stronghold", "mineshaft"),
         new FlatLayerInfo(1, Blocks.grass),
         new FlatLayerInfo(5, Blocks.dirt),
         new FlatLayerInfo(230, Blocks.stone),
         new FlatLayerInfo(1, Blocks.bedrock)
      );
      addPresetWithFeatures(
         "Water World",
         Items.water_bucket,
         BiomeGenBase.deepOcean,
         Arrays.asList("biome_1", "oceanmonument"),
         new FlatLayerInfo(90, Blocks.water),
         new FlatLayerInfo(5, Blocks.sand),
         new FlatLayerInfo(5, Blocks.dirt),
         new FlatLayerInfo(5, Blocks.stone),
         new FlatLayerInfo(1, Blocks.bedrock)
      );
      addPresetInternal(
         "Overworld",
         Item.getItemFromBlock(Blocks.tallgrass),
         net.minecraft.block.BlockTallGrass.EnumType.GRASS.getMeta(),
         BiomeGenBase.plains,
         Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon", "lake", "lava_lake"),
         new FlatLayerInfo(1, Blocks.grass),
         new FlatLayerInfo(3, Blocks.dirt),
         new FlatLayerInfo(59, Blocks.stone),
         new FlatLayerInfo(1, Blocks.bedrock)
      );
      addPresetWithFeatures(
         "Snowy Kingdom",
         Item.getItemFromBlock(Blocks.snow_layer),
         BiomeGenBase.icePlains,
         Arrays.asList("village", "biome_1"),
         new FlatLayerInfo(1, Blocks.snow_layer),
         new FlatLayerInfo(1, Blocks.grass),
         new FlatLayerInfo(3, Blocks.dirt),
         new FlatLayerInfo(59, Blocks.stone),
         new FlatLayerInfo(1, Blocks.bedrock)
      );
      addPresetWithFeatures(
         "Bottomless Pit",
         Items.feather,
         BiomeGenBase.plains,
         Arrays.asList("village", "biome_1"),
         new FlatLayerInfo(1, Blocks.grass),
         new FlatLayerInfo(3, Blocks.dirt),
         new FlatLayerInfo(2, Blocks.cobblestone)
      );
      addPresetWithFeatures(
         "Desert",
         Item.getItemFromBlock(Blocks.sand),
         BiomeGenBase.desert,
         Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon"),
         new FlatLayerInfo(8, Blocks.sand),
         new FlatLayerInfo(52, Blocks.sandstone),
         new FlatLayerInfo(3, Blocks.stone),
         new FlatLayerInfo(1, Blocks.bedrock)
      );
      Item redstone2 = Items.redstone;
      BiomeGenBase desert2 = BiomeGenBase.desert;
      FlatLayerInfo[] flatLayerInfos = new FlatLayerInfo[]{
         new FlatLayerInfo(52, Blocks.sandstone), new FlatLayerInfo(3, Blocks.stone), new FlatLayerInfo(1, Blocks.bedrock)
      };
      BiomeGenBase biomeGenBase = desert2;
      Item item2 = redstone2;
      addPreset("Redstone Ready", item2, biomeGenBase, flatLayerInfos);
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 0 && this.canSelectPreset()) {
         this.parentScreen.setPresetString(this.presetField.getText());
         this.mc.displayGuiScreen(this.parentScreen);
      } else if (button.id == 1) {
         this.mc.displayGuiScreen(this.parentScreen);
      }
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if (!this.presetField.textboxKeyTyped(typedChar, keyCode)) {
         super.keyTyped(typedChar, keyCode);
      }
   }

   private static void addPreset(String name, Item item, BiomeGenBase biome, FlatLayerInfo... layers) {
      addPresetInternal(name, item, 0, biome, (List<String>)null, layers);
   }

   public GuiFlatPresets(GuiCreateFlatWorld parentScreen) {
      this.parentScreen = parentScreen;
   }

   private static void addPresetInternal(String name, Item item, int metadata, BiomeGenBase biome, List<String> features, FlatLayerInfo... layers) {
      FlatGeneratorInfo flatGeneratorInfo = new FlatGeneratorInfo();

      for (int i = layers.length - 1; i >= 0; i--) {
         flatGeneratorInfo.getFlatLayers().add(layers[i]);
      }

      flatGeneratorInfo.setBiome(biome.biomeID);
      flatGeneratorInfo.updateLayerYValues();
      if (features != null) {
         for (String text : features) {
            flatGeneratorInfo.getWorldFeatures().put(text, Maps.newHashMap());
         }
      }

      FLAT_WORLD_PRESETS.add(new GuiFlatPresets.LayerItem(item, metadata, name, flatGeneratorInfo.toString()));
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.presetList.handleMouseInput();
   }

   @Override
   public void startClosing() {
      Keyboard.enableRepeatEvents(false);
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      Keyboard.enableRepeatEvents(true);
      this.presetsTitle = I18n.format("createWorld.customize.presets.title");
      this.presetsShare = I18n.format("createWorld.customize.presets.share");
      this.presetsListLabel = I18n.format("createWorld.customize.presets.list");
      this.presetField = new GuiTextField(2, this.fontRendererObj, 50, 40, this.width - 100, 20);
      this.presetList = new GuiFlatPresets.ListSlot();
      this.presetField.setMaxStringLength(1230);
      this.presetField.setText(this.parentScreen.getPresetString());
      this.buttonList
         .add(this.selectButton = new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("createWorld.customize.presets.select")));
      this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")));
      this.updateButtonState();
   }

   @Override
   public void updateScreen() {
      this.presetField.updateCursorCounter();
      super.updateScreen();
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.presetList.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRendererObj, this.presetsTitle, this.width / 2, 8, 16777215);
      this.drawString(this.fontRendererObj, this.presetsShare, 50, 30, 10526880);
      this.drawString(this.fontRendererObj, this.presetsListLabel, 50, 70, 10526880);
      this.presetField.drawTextBox();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   static class LayerItem {
      public Item item;
      public int metadata;
      public String displayName;
      public String presetData;

      public LayerItem(Item item, int metadata, String name, String presetData) {
         this.item = item;
         this.metadata = metadata;
         this.displayName = name;
         this.presetData = presetData;
      }
   }

   class ListSlot extends GuiSlot {
      public int selectedIndex = -1;

      @Override
      protected void drawSlot(int entryID, int x, int y, int z, int mouseXIn, int mouseYIn) {
         GuiFlatPresets.LayerItem guiflatpresets$layeritem = GuiFlatPresets.FLAT_WORLD_PRESETS.get(entryID);
         this.drawPresetIcon(x, y, guiflatpresets$layeritem.item, guiflatpresets$layeritem.metadata);
         GuiFlatPresets.this.fontRendererObj.getInt(guiflatpresets$layeritem.displayName, x + 18 + 5, y + 6, 16777215);
      }

      @Override
      protected void drawBackground() {
      }

      @Override
      protected int getSize() {
         return GuiFlatPresets.FLAT_WORLD_PRESETS.size();
      }

      private void handleInt(int i, int i2) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(Gui.statIcons);
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldRenderer.pos(i + 0, i2 + 18, GuiFlatPresets.this.zLevel).tex(0 * 0.0078125F, 18 * 0.0078125F).endVertex();
         worldRenderer.pos(i + 18, i2 + 18, GuiFlatPresets.this.zLevel).tex(18 * 0.0078125F, 18 * 0.0078125F).endVertex();
         worldRenderer.pos(i + 18, i2 + 0, GuiFlatPresets.this.zLevel).tex(18 * 0.0078125F, 0 * 0.0078125F).endVertex();
         worldRenderer.pos(i + 0, i2 + 0, GuiFlatPresets.this.zLevel).tex(0 * 0.0078125F, 0 * 0.0078125F).endVertex();
         instance.draw();
      }

      private void drawPresetIcon(int x, int y, Item item, int metadata) {
         this.drawPresetIconBackground(x + 1, y + 1);
         GlStateManager.enableRescaleNormal();
         RenderHelper.enableGUIStandardItemLighting();
         GuiFlatPresets.this.itemRender.handleItemStack(new ItemStack(item, 1, metadata), x + 2, y + 2);
         RenderHelper.disableStandardItemLighting();
         GlStateManager.disableRescaleNormal();
      }

      private void drawPresetIconBackground(int x, int y) {
         this.handleInt(x, y);
      }

      @Override
      protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
         this.selectedIndex = slotIndex;
         GuiFlatPresets.this.updateButtonState();
         GuiFlatPresets.this.presetField.setText(GuiFlatPresets.FLAT_WORLD_PRESETS.get(GuiFlatPresets.this.presetList.selectedIndex).presetData);
      }

      @Override
      protected boolean isSelected(int slotIndex) {
         return slotIndex == this.selectedIndex;
      }

      public ListSlot() {
         super(GuiFlatPresets.this.mc, GuiFlatPresets.this.width, GuiFlatPresets.this.height, 80, GuiFlatPresets.this.height - 37, 24);
      }
   }
}
