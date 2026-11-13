package net.optifine.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

public class GuiOtherSettingsOF extends GuiScreen implements GuiYesNoCallback {
   private TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderOptions());
   private GuiScreen prevScreen;
   protected String title;
   private static net.minecraft.client.settings.GameSettings.Options[] enumOptions = new net.minecraft.client.settings.GameSettings.Options[]{
      net.minecraft.client.settings.GameSettings.Options.LAGOMETER,
      net.minecraft.client.settings.GameSettings.Options.PROFILER,
      net.minecraft.client.settings.GameSettings.Options.SHOW_FPS,
      net.minecraft.client.settings.GameSettings.Options.ADVANCED_TOOLTIPS,
      net.minecraft.client.settings.GameSettings.Options.WEATHER,
      net.minecraft.client.settings.GameSettings.Options.TIME,
      net.minecraft.client.settings.GameSettings.Options.USE_FULLSCREEN,
      net.minecraft.client.settings.GameSettings.Options.FULLSCREEN_MODE,
      net.minecraft.client.settings.GameSettings.Options.ANAGLYPH,
      net.minecraft.client.settings.GameSettings.Options.AUTOSAVE_TICKS,
      net.minecraft.client.settings.GameSettings.Options.SCREENSHOT_SIZE,
      net.minecraft.client.settings.GameSettings.Options.SHOW_GL_ERRORS
   };
   private GameSettings settings;

   @Override
   public void confirmClicked(boolean conditionMet, int i) {
      if (conditionMet) {
         this.mc.gameSettings.resetSettings();
      }

      this.mc.displayGuiScreen(this);
   }

   public GuiOtherSettingsOF(GuiScreen guiScreen, GameSettings gameSettings) {
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

         if (guiButton.id == 210) {
            this.mc.gameSettings.saveOptions();
            GuiYesNo guiYesNo = new GuiYesNo(this, I18n.format("of.message.other.reset"), "", 9999);
            this.mc.displayGuiScreen(guiYesNo);
         }
      }
   }

   @Override
   public void initGui() {
      this.title = I18n.format("of.options.otherTitle");
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

      this.buttonList.add(new GuiButton(210, this.width / 2 - 100, this.height / 6 + 168 + 11 - 44, I18n.format("of.options.other.reset")));
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
