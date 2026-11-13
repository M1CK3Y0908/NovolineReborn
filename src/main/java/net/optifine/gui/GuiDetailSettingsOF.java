package net.optifine.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

public class GuiDetailSettingsOF extends GuiScreen {
   private TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderOptions());
   private static net.minecraft.client.settings.GameSettings.Options[] enumOptions = new net.minecraft.client.settings.GameSettings.Options[]{
      net.minecraft.client.settings.GameSettings.Options.CLOUDS,
      net.minecraft.client.settings.GameSettings.Options.CLOUD_HEIGHT,
      net.minecraft.client.settings.GameSettings.Options.TREES,
      net.minecraft.client.settings.GameSettings.Options.RAIN,
      net.minecraft.client.settings.GameSettings.Options.SKY,
      net.minecraft.client.settings.GameSettings.Options.STARS,
      net.minecraft.client.settings.GameSettings.Options.SUN_MOON,
      net.minecraft.client.settings.GameSettings.Options.SHOW_CAPES,
      net.minecraft.client.settings.GameSettings.Options.FOG_FANCY,
      net.minecraft.client.settings.GameSettings.Options.FOG_START,
      net.minecraft.client.settings.GameSettings.Options.TRANSLUCENT_BLOCKS,
      net.minecraft.client.settings.GameSettings.Options.HELD_ITEM_TOOLTIPS,
      net.minecraft.client.settings.GameSettings.Options.DROPPED_ITEMS,
      net.minecraft.client.settings.GameSettings.Options.ENTITY_SHADOWS,
      net.minecraft.client.settings.GameSettings.Options.VIGNETTE,
      net.minecraft.client.settings.GameSettings.Options.ALTERNATE_BLOCKS,
      net.minecraft.client.settings.GameSettings.Options.SWAMP_COLORS,
      net.minecraft.client.settings.GameSettings.Options.SMOOTH_BIOMES
   };
   private GuiScreen prevScreen;
   protected String title;
   private GameSettings settings;

   @Override
   public void initGui() {
      this.title = I18n.format("of.options.detailsTitle");
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
      }
   }

   @Override
   public void drawScreen(int x, int y, float f) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 15, 16777215);
      super.drawScreen(x, y, f);
      this.tooltipManager.drawTooltips(x, y, this.buttonList);
   }

   public GuiDetailSettingsOF(GuiScreen guiScreen, GameSettings gameSettings) {
      this.prevScreen = guiScreen;
      this.settings = gameSettings;
   }
}
