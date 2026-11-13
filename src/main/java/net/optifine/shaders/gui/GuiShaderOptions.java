package net.optifine.shaders.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import net.optifine.Lang;
import net.optifine.gui.GuiScreenOF;
import net.optifine.gui.TooltipManager;
import net.optifine.gui.TooltipProviderShaderOptions;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.config.ShaderOption;
import net.optifine.shaders.config.ShaderOptionProfile;
import net.optifine.shaders.config.ShaderOptionScreen;

public class GuiShaderOptions extends GuiScreenOF {
   private TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderShaderOptions());
   private String screenName = null;
   public static final String text = "<profile>";
   public static final String OPTION_REST = "*";
   private String screenText = null;
   private boolean changed = false;
   protected String title = "Shader Options";
   public static final String text2 = "<empty>";
   private GuiScreen prevScreen;
   private GameSettings settings;

   @Override
   public void initGui() {
      this.title = I18n.format("of.options.shaderOptionsTitle");
      int intValue = Shaders.createInt(this.screenName);
      ShaderOption[] shaderPackOptions = Shaders.getShaderPackOptions(this.screenName);
      if (shaderPackOptions != null) {
         int i = MathHelper.ceiling_double_int(shaderPackOptions.length / 9.0);
         if (intValue < i) {
            intValue = i;
         }

         for (int i2 = 0; i2 < shaderPackOptions.length; i2++) {
            ShaderOption shaderOption2 = shaderPackOptions[i2];
            if (shaderOption2 != null && shaderOption2.isVisible()) {
               int i3 = i2 % intValue;
               int i4 = i2 / intValue;
               int i5 = Math.min(this.width / intValue, 200);
               int i6 = (this.width - i5 * intValue) / 2;
               int i7 = i3 * i5 + 5 + i6;
               int i8 = 30 + i4 * 20;
               int i9 = i5 - 10;
               String buttonText = getButtonText(shaderOption2, i9);
               GuiButtonShaderOption guiButtonShaderOption2;
               if (Shaders.isShaderPackOptionSlider(shaderOption2.getName())) {
                  guiButtonShaderOption2 = new GuiSliderShaderOption(100 + i2, i7, i8, i9, 20, shaderOption2, buttonText);
               } else {
                  guiButtonShaderOption2 = new GuiButtonShaderOption(100 + i2, i7, i8, i9, 20, shaderOption2, buttonText);
               }

               guiButtonShaderOption2.enabled = shaderOption2.isEnabled();
               this.buttonList.add(guiButtonShaderOption2);
            }
         }
      }

      this.buttonList.add(new GuiButton(201, this.width / 2 - 120 - 20, this.height / 6 + 168 + 11, 120, 20, I18n.format("controls.reset")));
      this.buttonList.add(new GuiButton(200, this.width / 2 + 20, this.height / 6 + 168 + 11, 120, 20, I18n.format("gui.done")));
   }

   @Override
   protected void actionPerformedRightClick(GuiButton btn) {
      if (btn instanceof GuiButtonShaderOption guiButtonShaderOption) {
         ShaderOption shaderOption = guiButtonShaderOption.getShaderOption();
         if (isShiftKeyDown()) {
            shaderOption.resetValue();
         } else if (guiButtonShaderOption.isSwitchable()) {
            shaderOption.prevValue();
         }

         this.updateAllButtons();
         this.changed = true;
      }
   }

   @Override
   protected void actionPerformed(GuiButton guiButton) {
      if (guiButton.enabled) {
         if (guiButton.id < 200 && guiButton instanceof GuiButtonShaderOption guiButtonShaderOption) {
            ShaderOption shaderOption = guiButtonShaderOption.getShaderOption();
            if (shaderOption instanceof ShaderOptionScreen) {
               String name = shaderOption.getName();
               GuiShaderOptions guiShaderOptions = new GuiShaderOptions(this, this.settings, name);
               this.mc.displayGuiScreen(guiShaderOptions);
               return;
            }

            if (isShiftKeyDown()) {
               shaderOption.resetValue();
            } else if (guiButtonShaderOption.isSwitchable()) {
               shaderOption.nextValue();
            }

            this.updateAllButtons();
            this.changed = true;
         }

         if (guiButton.id == 201) {
            ShaderOption[] ashaderoption = Shaders.getChangedOptions(Shaders.getShaderPackOptions());

            for (int index = 0; index < ashaderoption.length; index++) {
               ShaderOption name = ashaderoption[index];
               name.resetValue();
               this.changed = true;
            }

            this.updateAllButtons();
         }

         if (guiButton.id == 200) {
            if (this.changed) {
               Shaders.saveShaderPackOptions();
               this.changed = false;
               Shaders.uninit();
            }

            this.mc.displayGuiScreen(this.prevScreen);
         }
      }
   }

   @Override
   public void startClosing() {
      super.startClosing();
      if (this.changed) {
         Shaders.saveShaderPackOptions();
         this.changed = false;
         Shaders.uninit();
      }
   }

   @Override
   public void drawScreen(int x, int y, float f) {
      this.drawDefaultBackground();
      if (this.screenText != null) {
         this.drawCenteredString(this.fontRendererObj, this.screenText, this.width / 2, 15, 16777215);
      } else {
         this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 15, 16777215);
      }

      super.drawScreen(x, y, f);
      this.tooltipManager.drawTooltips(x, y, this.buttonList);
   }

   private void updateAllButtons() {
      for (GuiButton guiButton2 : this.buttonList) {
         if (guiButton2 instanceof GuiButtonShaderOption guiButtonShaderOption2) {
            ShaderOption shaderOption2 = guiButtonShaderOption2.getShaderOption();
            if (shaderOption2 instanceof ShaderOptionProfile) {
               ((ShaderOptionProfile)shaderOption2).updateProfile();
            }

            guiButtonShaderOption2.displayString = getButtonText(shaderOption2, guiButtonShaderOption2.getButtonWidth());
            guiButtonShaderOption2.valueChanged();
         }
      }
   }

   public static String getButtonText(ShaderOption so, int btnWidth) {
      String nameText = so.getNameText();
      if (so instanceof ShaderOptionScreen) {
         return nameText + "...";
      }

      FontRenderer fontRendererObj2 = Config.getMinecraft().fontRendererObj;
      int i = fontRendererObj2.getInt3(": " + Lang.getOff()) + 5;

      while (fontRendererObj2.getInt3(nameText) + i >= btnWidth && nameText.length() > 0) {
         nameText = nameText.substring(0, nameText.length() - 1);
      }

      String text3 = so.isChanged() ? so.getValueColor(so.getValue()) : "";
      String valueText = so.getValueText(so.getValue());
      return nameText + ": " + text3 + valueText;
   }

   public GuiShaderOptions(GuiScreen guiScreen, GameSettings gameSettings) {
      this.prevScreen = guiScreen;
      this.settings = gameSettings;
   }

   public GuiShaderOptions(GuiScreen guiScreen, GameSettings gameSettings, String screenName) {
      this(guiScreen, gameSettings);
      this.screenName = screenName;
      if (screenName != null) {
         this.screenText = Shaders.translate("screen." + screenName, screenName);
      }
   }
}
