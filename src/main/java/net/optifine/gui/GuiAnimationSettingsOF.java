package net.optifine.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.optifine.Lang;

public class GuiAnimationSettingsOF extends GuiScreen {
   protected String title;
   private GuiScreen prevScreen;
   private static net.minecraft.client.settings.GameSettings.Options[] enumOptions = new net.minecraft.client.settings.GameSettings.Options[]{
      net.minecraft.client.settings.GameSettings.Options.ANIMATED_WATER,
      net.minecraft.client.settings.GameSettings.Options.ANIMATED_LAVA,
      net.minecraft.client.settings.GameSettings.Options.ANIMATED_FIRE,
      net.minecraft.client.settings.GameSettings.Options.ANIMATED_PORTAL,
      net.minecraft.client.settings.GameSettings.Options.ANIMATED_REDSTONE,
      net.minecraft.client.settings.GameSettings.Options.ANIMATED_EXPLOSION,
      net.minecraft.client.settings.GameSettings.Options.ANIMATED_FLAME,
      net.minecraft.client.settings.GameSettings.Options.ANIMATED_SMOKE,
      net.minecraft.client.settings.GameSettings.Options.VOID_PARTICLES,
      net.minecraft.client.settings.GameSettings.Options.WATER_PARTICLES,
      net.minecraft.client.settings.GameSettings.Options.RAIN_SPLASH,
      net.minecraft.client.settings.GameSettings.Options.PORTAL_PARTICLES,
      net.minecraft.client.settings.GameSettings.Options.POTION_PARTICLES,
      net.minecraft.client.settings.GameSettings.Options.DRIPPING_WATER_LAVA,
      net.minecraft.client.settings.GameSettings.Options.ANIMATED_TERRAIN,
      net.minecraft.client.settings.GameSettings.Options.ANIMATED_TEXTURES,
      net.minecraft.client.settings.GameSettings.Options.FIREWORK_PARTICLES,
      net.minecraft.client.settings.GameSettings.Options.PARTICLES
   };
   private GameSettings settings;

   public GuiAnimationSettingsOF(GuiScreen guiScreen, GameSettings gameSettings) {
      this.prevScreen = guiScreen;
      this.settings = gameSettings;
   }

   @Override
   public void initGui() {
      this.title = I18n.format("of.options.animationsTitle");
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

      this.buttonList.add(new GuiButton(210, this.width / 2 - 155, this.height / 6 + 168 + 11, 70, 20, Lang.get("of.options.animation.allOn")));
      this.buttonList.add(new GuiButton(211, this.width / 2 - 155 + 80, this.height / 6 + 168 + 11, 70, 20, Lang.get("of.options.animation.allOff")));
      this.buttonList.add(new GuiOptionButton(200, this.width / 2 + 5, this.height / 6 + 168 + 11, I18n.format("gui.done")));
   }

   @Override
   public void drawScreen(int x, int y, float f) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 15, 16777215);
      super.drawScreen(x, y, f);
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
            this.mc.gameSettings.setAllAnimations(true);
         }

         if (guiButton.id == 211) {
            this.mc.gameSettings.setAllAnimations(false);
         }

         ScaledResolution scaledResolution = new ScaledResolution(this.mc);
         this.setWorldAndResolution(this.mc, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
      }
   }
}
