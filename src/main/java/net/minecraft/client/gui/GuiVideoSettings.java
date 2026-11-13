package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.src.Config;
import net.optifine.Lang;
import net.optifine.gui.GuiAnimationSettingsOF;
import net.optifine.gui.GuiDetailSettingsOF;
import net.optifine.gui.GuiOptionButtonOF;
import net.optifine.gui.GuiOptionSliderOF;
import net.optifine.gui.GuiOtherSettingsOF;
import net.optifine.gui.GuiPerformanceSettingsOF;
import net.optifine.gui.GuiQualitySettingsOF;
import net.optifine.gui.GuiScreenOF;
import net.optifine.gui.TooltipManager;
import net.optifine.gui.TooltipProviderOptions;
import net.optifine.shaders.gui.GuiShaders;

public class GuiVideoSettings extends GuiScreenOF {
   protected String screenTitle = "Video Settings";
   private TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderOptions());
   private GuiScreen parentGuiScreen;
   private GameSettings guiGameSettings;
   private static net.minecraft.client.settings.GameSettings.Options[] videoOptions = new net.minecraft.client.settings.GameSettings.Options[]{
      net.minecraft.client.settings.GameSettings.Options.GRAPHICS,
      net.minecraft.client.settings.GameSettings.Options.RENDER_DISTANCE,
      net.minecraft.client.settings.GameSettings.Options.AMBIENT_OCCLUSION,
      net.minecraft.client.settings.GameSettings.Options.FRAMERATE_LIMIT,
      net.minecraft.client.settings.GameSettings.Options.AO_LEVEL,
      net.minecraft.client.settings.GameSettings.Options.VIEW_BOBBING,
      net.minecraft.client.settings.GameSettings.Options.GUI_SCALE,
      net.minecraft.client.settings.GameSettings.Options.USE_VBO,
      net.minecraft.client.settings.GameSettings.Options.GAMMA,
      net.minecraft.client.settings.GameSettings.Options.BLOCK_ALTERNATIVES,
      net.minecraft.client.settings.GameSettings.Options.DYNAMIC_LIGHTS,
      net.minecraft.client.settings.GameSettings.Options.DYNAMIC_FOV
   };

   @Override
   public void initGui() {
      this.screenTitle = I18n.format("options.videoTitle");
      this.buttonList.clear();

      for (int i = 0; i < videoOptions.length; i++) {
         net.minecraft.client.settings.GameSettings.Options options2 = videoOptions[i];
         if (options2 != null) {
            int i2 = this.width / 2 - 155 + i % 2 * 160;
            int i3 = this.height / 6 + 21 * (i / 2) - 12;
            if (options2.getEnumFloat()) {
               this.buttonList.add(new GuiOptionSliderOF(options2.returnEnumOrdinal(), i2, i3, options2));
            } else {
               this.buttonList
                  .add(new GuiOptionButtonOF(options2.returnEnumOrdinal(), i2, i3, options2, this.guiGameSettings.getKeyBinding(options2)));
            }
         }
      }

      int i4 = this.height / 6 + 21 * (videoOptions.length / 2) - 12;
      int i5 = this.width / 2 - 155 + 0;
      this.buttonList.add(new GuiOptionButton(231, i5, i4, Lang.get("of.options.shaders")));
      i5 = this.width / 2 - 155 + 160;
      this.buttonList.add(new GuiOptionButton(202, i5, i4, Lang.get("of.options.quality")));
      i4 += 21;
      i5 = this.width / 2 - 155 + 0;
      this.buttonList.add(new GuiOptionButton(201, i5, i4, Lang.get("of.options.details")));
      i5 = this.width / 2 - 155 + 160;
      this.buttonList.add(new GuiOptionButton(212, i5, i4, Lang.get("of.options.performance")));
      i4 += 21;
      i5 = this.width / 2 - 155 + 0;
      this.buttonList.add(new GuiOptionButton(211, i5, i4, Lang.get("of.options.animations")));
      i5 = this.width / 2 - 155 + 160;
      this.buttonList.add(new GuiOptionButton(222, i5, i4, Lang.get("of.options.other")));
      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168 + 11, I18n.format("gui.done")));
   }

   public static int getButtonHeight(GuiButton arg0) {
      return arg0.height;
   }

   @Override
   protected void actionPerformedRightClick(GuiButton arg1) {
      if (arg1.id == net.minecraft.client.settings.GameSettings.Options.GUI_SCALE.ordinal()) {
         this.actionPerformed(arg1, -1);
      }
   }

   public static int getButtonWidth(GuiButton arg0) {
      return arg0.width;
   }

   private void actionPerformed(GuiButton arg1, int arg2) {
      if (arg1.enabled) {
         int guiScale2 = this.guiGameSettings.guiScale;
         if (arg1.id < 200 && arg1 instanceof GuiOptionButton) {
            this.guiGameSettings.setOptionValue(((GuiOptionButton)arg1).returnEnumOptions(), arg2);
            arg1.displayString = this.guiGameSettings.getKeyBinding(net.minecraft.client.settings.GameSettings.Options.getEnumOptions(arg1.id));
         }

         if (arg1.id == 200) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(this.parentGuiScreen);
         }

         if (this.guiGameSettings.guiScale != guiScale2) {
            ScaledResolution scaledResolution = new ScaledResolution(this.mc);
            int scaledWidth = scaledResolution.getScaledWidth();
            int scaledHeight = scaledResolution.getScaledHeight();
            this.setWorldAndResolution(this.mc, scaledWidth, scaledHeight);
         }

         if (arg1.id == 201) {
            this.mc.gameSettings.saveOptions();
            GuiDetailSettingsOF guiDetailSettingsOF = new GuiDetailSettingsOF(this, this.guiGameSettings);
            this.mc.displayGuiScreen(guiDetailSettingsOF);
         }

         if (arg1.id == 202) {
            this.mc.gameSettings.saveOptions();
            GuiQualitySettingsOF guiQualitySettingsOF = new GuiQualitySettingsOF(this, this.guiGameSettings);
            this.mc.displayGuiScreen(guiQualitySettingsOF);
         }

         if (arg1.id == 211) {
            this.mc.gameSettings.saveOptions();
            GuiAnimationSettingsOF guiAnimationSettingsOF = new GuiAnimationSettingsOF(this, this.guiGameSettings);
            this.mc.displayGuiScreen(guiAnimationSettingsOF);
         }

         if (arg1.id == 212) {
            this.mc.gameSettings.saveOptions();
            GuiPerformanceSettingsOF guiPerformanceSettingsOF = new GuiPerformanceSettingsOF(this, this.guiGameSettings);
            this.mc.displayGuiScreen(guiPerformanceSettingsOF);
         }

         if (arg1.id == 222) {
            this.mc.gameSettings.saveOptions();
            GuiOtherSettingsOF guiOtherSettingsOF = new GuiOtherSettingsOF(this, this.guiGameSettings);
            this.mc.displayGuiScreen(guiOtherSettingsOF);
         }

         if (arg1.id == 231) {
            if (Config.isAntialiasing() || Config.isAntialiasingConfigured()) {
               Config.showGuiMessage(Lang.get("of.message.shaders.aa1"), Lang.get("of.message.shaders.aa2"));
               return;
            }

            if (Config.isAnisotropicFiltering()) {
               Config.showGuiMessage(Lang.get("of.message.shaders.af1"), Lang.get("of.message.shaders.af2"));
               return;
            }

            if (Config.getGameSettings().anaglyph) {
               Config.showGuiMessage(Lang.get("of.message.shaders.an1"), Lang.get("of.message.shaders.an2"));
               return;
            }

            this.mc.gameSettings.saveOptions();
            GuiShaders guiShaders = new GuiShaders(this, this.guiGameSettings);
            this.mc.displayGuiScreen(guiShaders);
         }
      }
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      this.actionPerformed(button, 1);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 15, 16777215);
      String version = Config.getVersion();
      if ("HD_U".equals("HD")) {
         version = "OptiFine HD M5";
      }

      if ("HD_U".equals("HD_U")) {
         version = "OptiFine HD M5 Ultra";
      }

      if ("HD_U".equals("L")) {
         version = "OptiFine M5 Light";
      }

      this.drawString(this.fontRendererObj, version, 2, this.height - 10, 8421504);
      int intValue = this.fontRendererObj.getInt3("Minecraft 1.8.9");
      this.drawString(this.fontRendererObj, "Minecraft 1.8.9", this.width - intValue - 2, this.height - 10, 8421504);
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.tooltipManager.drawTooltips(mouseX, mouseY, this.buttonList);
   }

   public GuiVideoSettings(GuiScreen parentScreenIn, GameSettings gameSettingsIn) {
      this.parentGuiScreen = parentScreenIn;
      this.guiGameSettings = gameSettingsIn;
   }

   public static String getGuiChatText(GuiChat arg0) {
      return arg0.inputField.getText();
   }
}
