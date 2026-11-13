package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

public class ScreenChatOptions extends GuiScreen {
   private static final net.minecraft.client.settings.GameSettings.Options[] CHAT_OPTIONS = new net.minecraft.client.settings.GameSettings.Options[]{
      net.minecraft.client.settings.GameSettings.Options.CHAT_VISIBILITY,
      net.minecraft.client.settings.GameSettings.Options.CHAT_COLOR,
      net.minecraft.client.settings.GameSettings.Options.CHAT_LINKS,
      net.minecraft.client.settings.GameSettings.Options.CHAT_OPACITY,
      net.minecraft.client.settings.GameSettings.Options.CHAT_LINKS_PROMPT,
      net.minecraft.client.settings.GameSettings.Options.CHAT_SCALE,
      net.minecraft.client.settings.GameSettings.Options.CHAT_HEIGHT_FOCUSED,
      net.minecraft.client.settings.GameSettings.Options.CHAT_HEIGHT_UNFOCUSED,
      net.minecraft.client.settings.GameSettings.Options.CHAT_WIDTH,
      net.minecraft.client.settings.GameSettings.Options.REDUCED_DEBUG_INFO
   };
   private final GuiScreen parentScreen;
   private String title;
   private final GameSettings game_settings;

   @Override
   public void initGui() {
      int i = 0;
      this.title = I18n.format("options.chat.title");

      for (net.minecraft.client.settings.GameSettings.Options options2 : CHAT_OPTIONS) {
         if (options2.getEnumFloat()) {
            this.buttonList
               .add(new GuiOptionSlider(options2.returnEnumOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), options2));
         } else {
            this.buttonList
               .add(
                  new GuiOptionButton(
                     options2.returnEnumOrdinal(),
                     this.width / 2 - 155 + i % 2 * 160,
                     this.height / 6 + 24 * (i >> 1),
                     options2,
                     this.game_settings.getKeyBinding(options2)
                  )
               );
         }

         i++;
      }

      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 120, I18n.format("gui.done")));
   }

   public ScreenChatOptions(GuiScreen parentScreenIn, GameSettings gameSettingsIn) {
      this.parentScreen = parentScreenIn;
      this.game_settings = gameSettingsIn;
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 20, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         if (button.id < 100 && button instanceof GuiOptionButton) {
            this.game_settings.setOptionValue(((GuiOptionButton)button).returnEnumOptions(), 1);
            button.displayString = this.game_settings.getKeyBinding(net.minecraft.client.settings.GameSettings.Options.getEnumOptions(button.id));
         }

         if (button.id == 200) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(this.parentScreen);
         }
      }
   }
}
