package net.optifine.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

public class GuiPerformanceSettingsOF extends GuiScreen {
   private TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderOptions());
   private GuiScreen prevScreen;
   private GameSettings settings;
   private static net.minecraft.client.settings.GameSettings.Options[] enumOptions = new net.minecraft.client.settings.GameSettings.Options[]{
      net.minecraft.client.settings.GameSettings.Options.SMOOTH_FPS,
      net.minecraft.client.settings.GameSettings.Options.SMOOTH_WORLD,
      net.minecraft.client.settings.GameSettings.Options.FAST_MATH,
      net.minecraft.client.settings.GameSettings.Options.CHUNK_UPDATES,
      net.minecraft.client.settings.GameSettings.Options.CHUNK_UPDATES_DYNAMIC,
      net.minecraft.client.settings.GameSettings.Options.RENDER_REGIONS,
      net.minecraft.client.settings.GameSettings.Options.LAZY_CHUNK_LOADING,
      net.minecraft.client.settings.GameSettings.Options.SMART_ANIMATIONS
   };
   protected String title;

   public GuiPerformanceSettingsOF(GuiScreen guiScreen, GameSettings gameSettings) {
      this.prevScreen = guiScreen;
      this.settings = gameSettings;
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
   public void initGui() {
      this.title = I18n.format("of.options.performanceTitle");
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
}
