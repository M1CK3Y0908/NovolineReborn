package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public class GuiScreenCustomizePresets extends GuiScreen {
   private GuiScreenCustomizePresets.ListPreset presetList;
   private static final List<GuiScreenCustomizePresets.Info> presets = Lists.newArrayList();
   protected String title = "Customize World Presets";
   private GuiButton selectButton;
   private String listText;
   private GuiTextField export;
   private GuiCustomizeWorldScreen parentScreen;
   private String shareText;

   @Override
   public void startClosing() {
      Keyboard.enableRepeatEvents(false);
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.presetList.handleMouseInput();
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      this.export.mouseClicked(mouseX, mouseY, mouseButton);
      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   public GuiScreenCustomizePresets(GuiCustomizeWorldScreen parentScreen) {
      this.parentScreen = parentScreen;
   }

   private boolean hasValidSelection() {
      return this.presetList.selectedIndex > -1 && this.presetList.selectedIndex < presets.size() || this.export.getText().length() > 1;
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if (!this.export.textboxKeyTyped(typedChar, keyCode)) {
         super.keyTyped(typedChar, keyCode);
      }
   }

   public void updateButtonValidity() {
      this.selectButton.enabled = this.hasValidSelection();
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      Keyboard.enableRepeatEvents(true);
      this.title = I18n.format("createWorld.customize.custom.presets.title");
      this.shareText = I18n.format("createWorld.customize.presets.share");
      this.listText = I18n.format("createWorld.customize.presets.list");
      this.export = new GuiTextField(2, this.fontRendererObj, 50, 40, this.width - 100, 20);
      this.presetList = new GuiScreenCustomizePresets.ListPreset();
      this.export.setMaxStringLength(2000);
      this.export.setText(this.parentScreen.getSettingsJson());
      this.buttonList
         .add(this.selectButton = new GuiButton(0, this.width / 2 - 102, this.height - 27, 100, 20, I18n.format("createWorld.customize.presets.select")));
      this.buttonList.add(new GuiButton(1, this.width / 2 + 3, this.height - 27, 100, 20, I18n.format("gui.cancel")));
      this.updateButtonValidity();
   }

   @Override
   public void updateScreen() {
      this.export.updateCursorCounter();
      super.updateScreen();
   }

   static {
      net.minecraft.world.gen.ChunkProviderSettings.Factory factory = net.minecraft.world.gen.ChunkProviderSettings.Factory.jsonToFactory(
         "{ \"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":512.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":5000.0, \"mainNoiseScaleY\":1000.0, \"mainNoiseScaleZ\":5000.0, \"baseSize\":8.5, \"stretchY\":8.0, \"biomeDepthWeight\":2.0, \"biomeDepthOffset\":0.5, \"biomeScaleWeight\":2.0, \"biomeScaleOffset\":0.375, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":255 }"
      );
      ResourceLocation resourceLocation = new ResourceLocation("textures/gui/presets/water.png");
      presets.add(new GuiScreenCustomizePresets.Info(I18n.format("createWorld.customize.custom.preset.waterWorld"), resourceLocation, factory));
      factory = net.minecraft.world.gen.ChunkProviderSettings.Factory.jsonToFactory(
         "{\"coordinateScale\":3000.0, \"heightScale\":6000.0, \"upperLimitScale\":250.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":80.0, \"mainNoiseScaleY\":160.0, \"mainNoiseScaleZ\":80.0, \"baseSize\":8.5, \"stretchY\":10.0, \"biomeDepthWeight\":1.0, \"biomeDepthOffset\":0.0, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":0.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":63 }"
      );
      resourceLocation = new ResourceLocation("textures/gui/presets/isles.png");
      presets.add(new GuiScreenCustomizePresets.Info(I18n.format("createWorld.customize.custom.preset.isleLand"), resourceLocation, factory));
      factory = net.minecraft.world.gen.ChunkProviderSettings.Factory.jsonToFactory(
         "{\"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":512.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":5000.0, \"mainNoiseScaleY\":1000.0, \"mainNoiseScaleZ\":5000.0, \"baseSize\":8.5, \"stretchY\":5.0, \"biomeDepthWeight\":2.0, \"biomeDepthOffset\":1.0, \"biomeScaleWeight\":4.0, \"biomeScaleOffset\":1.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":63 }"
      );
      resourceLocation = new ResourceLocation("textures/gui/presets/delight.png");
      presets.add(new GuiScreenCustomizePresets.Info(I18n.format("createWorld.customize.custom.preset.caveDelight"), resourceLocation, factory));
      factory = net.minecraft.world.gen.ChunkProviderSettings.Factory.jsonToFactory(
         "{\"coordinateScale\":738.41864, \"heightScale\":157.69133, \"upperLimitScale\":801.4267, \"lowerLimitScale\":1254.1643, \"depthNoiseScaleX\":374.93652, \"depthNoiseScaleZ\":288.65228, \"depthNoiseScaleExponent\":1.2092624, \"mainNoiseScaleX\":1355.9908, \"mainNoiseScaleY\":745.5343, \"mainNoiseScaleZ\":1183.464, \"baseSize\":1.8758626, \"stretchY\":1.7137525, \"biomeDepthWeight\":1.7553768, \"biomeDepthOffset\":3.4701107, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":2.535211, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":63 }"
      );
      resourceLocation = new ResourceLocation("textures/gui/presets/madness.png");
      presets.add(new GuiScreenCustomizePresets.Info(I18n.format("createWorld.customize.custom.preset.mountains"), resourceLocation, factory));
      factory = net.minecraft.world.gen.ChunkProviderSettings.Factory.jsonToFactory(
         "{\"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":512.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":1000.0, \"mainNoiseScaleY\":3000.0, \"mainNoiseScaleZ\":1000.0, \"baseSize\":8.5, \"stretchY\":10.0, \"biomeDepthWeight\":1.0, \"biomeDepthOffset\":0.0, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":0.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":20 }"
      );
      resourceLocation = new ResourceLocation("textures/gui/presets/drought.png");
      presets.add(new GuiScreenCustomizePresets.Info(I18n.format("createWorld.customize.custom.preset.drought"), resourceLocation, factory));
      factory = net.minecraft.world.gen.ChunkProviderSettings.Factory.jsonToFactory(
         "{\"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":2.0, \"lowerLimitScale\":64.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":80.0, \"mainNoiseScaleY\":160.0, \"mainNoiseScaleZ\":80.0, \"baseSize\":8.5, \"stretchY\":12.0, \"biomeDepthWeight\":1.0, \"biomeDepthOffset\":0.0, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":0.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":6 }"
      );
      resourceLocation = new ResourceLocation("textures/gui/presets/chaos.png");
      presets.add(new GuiScreenCustomizePresets.Info(I18n.format("createWorld.customize.custom.preset.caveChaos"), resourceLocation, factory));
      factory = net.minecraft.world.gen.ChunkProviderSettings.Factory.jsonToFactory(
         "{\"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":512.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":80.0, \"mainNoiseScaleY\":160.0, \"mainNoiseScaleZ\":80.0, \"baseSize\":8.5, \"stretchY\":12.0, \"biomeDepthWeight\":1.0, \"biomeDepthOffset\":0.0, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":0.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":true, \"seaLevel\":40 }"
      );
      resourceLocation = new ResourceLocation("textures/gui/presets/luck.png");
      presets.add(new GuiScreenCustomizePresets.Info(I18n.format("createWorld.customize.custom.preset.goodLuck"), resourceLocation, factory));
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      switch (button.id) {
         case 0:
            this.parentScreen.loadSettingsJson(this.export.getText());
            this.mc.displayGuiScreen(this.parentScreen);
            break;
         case 1:
            this.mc.displayGuiScreen(this.parentScreen);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.presetList.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 8, 16777215);
      this.drawString(this.fontRendererObj, this.shareText, 50, 30, 10526880);
      this.drawString(this.fontRendererObj, this.listText, 50, 70, 10526880);
      this.export.drawTextBox();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   static class Info {
      public String name;
      public ResourceLocation texture;
      public net.minecraft.world.gen.ChunkProviderSettings.Factory settings;

      public Info(String name, ResourceLocation texture, net.minecraft.world.gen.ChunkProviderSettings.Factory settings) {
         this.name = name;
         this.texture = texture;
         this.settings = settings;
      }
   }

   class ListPreset extends GuiSlot {
      public int selectedIndex = -1;

      private void drawIcon(int x, int y, ResourceLocation texture) {
         int i = x + 5;
         GuiScreenCustomizePresets.this.drawHorizontalLine(i - 1, i + 32, y - 1, -2039584);
         GuiScreenCustomizePresets.this.drawHorizontalLine(i - 1, i + 32, y + 32, -6250336);
         GuiScreenCustomizePresets.this.drawVerticalLine(i - 1, y - 1, y + 32, -2039584);
         GuiScreenCustomizePresets.this.drawVerticalLine(i + 32, y - 1, y + 32, -6250336);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(texture);
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldRenderer.pos(i + 0, y + 32, 0.0).tex(0.0, 1.0).endVertex();
         worldRenderer.pos(i + 32, y + 32, 0.0).tex(1.0, 1.0).endVertex();
         worldRenderer.pos(i + 32, y + 0, 0.0).tex(1.0, 0.0).endVertex();
         worldRenderer.pos(i + 0, y + 0, 0.0).tex(0.0, 0.0).endVertex();
         instance.draw();
      }

      @Override
      protected int getSize() {
         return GuiScreenCustomizePresets.presets.size();
      }

      @Override
      protected void drawBackground() {
      }

      @Override
      protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
         this.selectedIndex = slotIndex;
         GuiScreenCustomizePresets.this.updateButtonValidity();
         GuiScreenCustomizePresets.this.export
            .setText(GuiScreenCustomizePresets.presets.get(GuiScreenCustomizePresets.this.presetList.selectedIndex).settings.toString());
      }

      @Override
      protected boolean isSelected(int slotIndex) {
         return slotIndex == this.selectedIndex;
      }

      public ListPreset() {
         super(
            GuiScreenCustomizePresets.this.mc,
            GuiScreenCustomizePresets.this.width,
            GuiScreenCustomizePresets.this.height,
            80,
            GuiScreenCustomizePresets.this.height - 32,
            38
         );
      }

      @Override
      protected void drawSlot(int entryID, int x, int y, int slotHeight, int mouseXIn, int mouseYIn) {
         GuiScreenCustomizePresets.Info guiscreencustomizepresets$info = GuiScreenCustomizePresets.presets.get(entryID);
         this.drawIcon(x, y, guiscreencustomizepresets$info.texture);
         GuiScreenCustomizePresets.this.fontRendererObj.getInt(guiscreencustomizepresets$info.name, x + 32 + 10, y + 14, 16777215);
      }
   }
}
