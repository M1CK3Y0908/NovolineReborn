package net.optifine.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

public class GuiQualitySettingsOF extends GuiScreen {
   private TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderOptions());
   private static net.minecraft.client.settings.GameSettings.Options[] enumOptions = new net.minecraft.client.settings.GameSettings.Options[]{
      net.minecraft.client.settings.GameSettings.Options.MIPMAP_LEVELS,
      net.minecraft.client.settings.GameSettings.Options.MIPMAP_TYPE,
      net.minecraft.client.settings.GameSettings.Options.AF_LEVEL,
      net.minecraft.client.settings.GameSettings.Options.AA_LEVEL,
      net.minecraft.client.settings.GameSettings.Options.CLEAR_WATER,
      net.minecraft.client.settings.GameSettings.Options.RANDOM_ENTITIES,
      net.minecraft.client.settings.GameSettings.Options.BETTER_GRASS,
      net.minecraft.client.settings.GameSettings.Options.BETTER_SNOW,
      net.minecraft.client.settings.GameSettings.Options.CUSTOM_FONTS,
      net.minecraft.client.settings.GameSettings.Options.CUSTOM_COLORS,
      net.minecraft.client.settings.GameSettings.Options.CONNECTED_TEXTURES,
      net.minecraft.client.settings.GameSettings.Options.NATURAL_TEXTURES,
      net.minecraft.client.settings.GameSettings.Options.CUSTOM_SKY,
      net.minecraft.client.settings.GameSettings.Options.CUSTOM_ITEMS,
      net.minecraft.client.settings.GameSettings.Options.CUSTOM_ENTITY_MODELS,
      net.minecraft.client.settings.GameSettings.Options.CUSTOM_GUIS,
      net.minecraft.client.settings.GameSettings.Options.EMISSIVE_TEXTURES
   };
   private GuiScreen prevScreen;
   private GameSettings settings;
   protected String title;

   @Override
   public void initGui() {
      this.title = I18n.format("of.options.qualityTitle");
      this.buttonList.clear();

      for (int index = 0; index < enumOptions.length; index++) {
         net.minecraft.client.settings.GameSettings.Options options2 = enumOptions[index];
         int i = this.width / 2 - 155 + index % 2 * 160;
         int i2 = this.height / 6 + 21 * (index / 2) - 12;
         if (!options2.getEnumFloat()) {
            this.buttonList.add(new GuiOptionButtonOF(options2.returnEnumOrdinal(), i, i2, options2, this.settings.getKeyBinding(options2)));
         } else {
            this.buttonList.add(new GuiOptionSliderOF(options2.returnEnumOrdinal(), i, i2, options2));
         }
      }

      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168 + 11, I18n.format("gui.done")));
   }

   @Override
   public void drawScreen(int x, int y, float f) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 15, 16777215);
      super.drawScreen(x, y, f);
      this.tooltipManager.drawTooltips(x, y, this.buttonList);
   }

   @Override
   protected void actionPerformed(GuiButton guiButton) {
      if (guiButton.enabled) {
         if (guiButton.id < 200 && guiButton instanceof GuiOptionButton) {
            this.settings.setOptionValue(((GuiOptionButton)guiButton).returnEnumOptions(), 1);
            guiButton.displayString = this.settings.getKeyBinding(net.minecraft.client.settings.GameSettings.Options.getEnumOptions(guiButton.id));
         }

         if (guiButton.id == 200) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(this.prevScreen);
         }

         if (guiButton.id != net.minecraft.client.settings.GameSettings.Options.AA_LEVEL.ordinal()) {
            ScaledResolution scaledResolution = new ScaledResolution(this.mc);
            this.setWorldAndResolution(this.mc, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
         }
      }
   }

   public GuiQualitySettingsOF(GuiScreen guiScreen, GameSettings gameSettings) {
      this.prevScreen = guiScreen;
      this.settings = gameSettings;
   }
}
