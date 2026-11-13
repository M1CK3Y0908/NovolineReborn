package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class GuiScreenOptionsSounds extends GuiScreen {
   protected String screenTitle = "Options";
   private String offLabel;
   private final GuiScreen parentScreen;
   private final GameSettings game_settings_4;

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 15, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled && button.id == 200) {
         this.mc.gameSettings.saveOptions();
         this.mc.displayGuiScreen(this.parentScreen);
      }
   }

   protected String getSoundVolume(SoundCategory soundCategory) {
      float soundLevel = this.game_settings_4.getSoundLevel(soundCategory);
      return soundLevel == 0.0F ? this.offLabel : (int)(soundLevel * 100.0F) + "%";
   }

   @Override
   public void initGui() {
      this.screenTitle = I18n.format("options.sounds.title");
      this.offLabel = I18n.format("options.off");
      this.buttonList
         .add(
            new GuiScreenOptionsSounds.Button(
               SoundCategory.MASTER.getCategoryId(), this.width / 2 - 155 + 0, this.height / 6 - 12 + 0, SoundCategory.MASTER, true
            )
         );
      int i = 2;

      for (SoundCategory soundCategory2 : SoundCategory.values()) {
         if (soundCategory2 != SoundCategory.MASTER) {
            this.buttonList
               .add(
                  new GuiScreenOptionsSounds.Button(
                     soundCategory2.getCategoryId(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), soundCategory2, false
                  )
               );
            i++;
         }
      }

      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done")));
   }

   public GuiScreenOptionsSounds(GuiScreen parentScreen, GameSettings gameSettings) {
      this.parentScreen = parentScreen;
      this.game_settings_4 = gameSettings;
   }

   class Button extends GuiButton {
      public boolean dragging;
      public float sliderValue = 1.0F;
      private final SoundCategory soundCategory;
      private final String categoryName;

      @Override
      public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
         if (super.mousePressed(mc, mouseX, mouseY)) {
            this.sliderValue = (float)(mouseX - (this.xPosition + 4)) / (this.width - 8);
            this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
            mc.gameSettings.setSoundLevel(this.soundCategory, this.sliderValue);
            mc.gameSettings.saveOptions();
            this.displayString = this.categoryName + ": " + GuiScreenOptionsSounds.this.getSoundVolume(this.soundCategory);
            this.dragging = true;
            return true;
         } else {
            return false;
         }
      }

      @Override
      protected void handleMinecraft(Minecraft minecraft2, int i) {
         if (this.visible) {
            if (this.dragging) {
               this.sliderValue = (float)(i - (this.xPosition + 4)) / (this.width - 8);
               this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
               minecraft2.gameSettings.setSoundLevel(this.soundCategory, this.sliderValue);
               minecraft2.gameSettings.saveOptions();
               this.displayString = this.categoryName + ": " + GuiScreenOptionsSounds.this.getSoundVolume(this.soundCategory);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (this.width - 8)), this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
         }
      }

      @Override
      protected int getHoverState(boolean mouseOver) {
         return 0;
      }

      public Button(int id, int x, int y, SoundCategory soundCategory, boolean wide) {
         super(id, x, y, wide ? 310 : 150, 20, "");
         this.soundCategory = soundCategory;
         this.categoryName = I18n.format("soundCategory." + soundCategory.getCategoryName());
         this.displayString = this.categoryName + ": " + GuiScreenOptionsSounds.this.getSoundVolume(soundCategory);
         this.sliderValue = GuiScreenOptionsSounds.this.game_settings_4.getSoundLevel(soundCategory);
      }

      @Override
      public void update() {
         if (this.dragging) {
            if (this.soundCategory != SoundCategory.MASTER) {
               GuiScreenOptionsSounds.this.game_settings_4.getSoundLevel(this.soundCategory);
            }

            GuiScreenOptionsSounds.this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         }

         this.dragging = false;
      }

      @Override
      public void playPressSound(SoundHandler soundHandlerIn) {
      }
   }
}
