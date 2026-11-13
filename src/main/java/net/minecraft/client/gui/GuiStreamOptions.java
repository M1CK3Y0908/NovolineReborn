package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraft.util.EnumChatFormatting;

public class GuiStreamOptions extends GuiScreen {
   private static final Options[] optionsArray = new Options[]{
      Options.STREAM_BYTES_PER_PIXEL,
      Options.STREAM_FPS,
      Options.STREAM_KBPS,
      Options.STREAM_SEND_METADATA,
      Options.STREAM_VOLUME_MIC,
      Options.STREAM_VOLUME_SYSTEM,
      Options.STREAM_MIC_TOGGLE_BEHAVIOR,
      Options.STREAM_COMPRESSION
   };
   private boolean flag = false;
   private int intValue;
   private String text;
   private static final Options[] optionsArray2 = new Options[]{Options.STREAM_CHAT_ENABLED, Options.STREAM_CHAT_USER_FILTER};
   private String text2;
   private final GuiScreen guiScreen;
   private final GameSettings gameSettings;

   public GuiStreamOptions(GuiScreen guiScreen2, GameSettings gameSettings2) {
      this.guiScreen = guiScreen2;
      this.gameSettings = gameSettings2;
   }

   @Override
   public void initGui() {
      int i = 0;
      this.text2 = I18n.format("options.stream.title");
      this.text = I18n.format("options.stream.chat.title");

      for (Options options2 : optionsArray) {
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
                     this.gameSettings.getKeyBinding(options2)
                  )
               );
         }

         i++;
      }

      if (i % 2 == 1) {
         i++;
      }

      this.intValue = this.height / 6 + 24 * (i >> 1) + 6;
      i += 2;

      for (Options options3 : optionsArray2) {
         if (options3.getEnumFloat()) {
            this.buttonList
               .add(new GuiOptionSlider(options3.returnEnumOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), options3));
         } else {
            this.buttonList
               .add(
                  new GuiOptionButton(
                     options3.returnEnumOrdinal(),
                     this.width / 2 - 155 + i % 2 * 160,
                     this.height / 6 + 24 * (i >> 1),
                     options3,
                     this.gameSettings.getKeyBinding(options3)
                  )
               );
         }

         i++;
      }

      this.buttonList.add(new GuiButton(200, this.width / 2 - 155, this.height / 6 + 168, 150, 20, I18n.format("gui.done")));
      GuiButton guiButton = new GuiButton(201, this.width / 2 + 5, this.height / 6 + 168, 150, 20, I18n.format("options.stream.ingestSelection"));
      guiButton.enabled = this.mc.getIStream().isActive7() && this.mc.getIStream().getArray().length > 0 || this.mc.getIStream().isActive4();
      this.buttonList.add(guiButton);
   }

   @Override
   protected void actionPerformed(GuiButton guiButton) throws IOException {
      if (guiButton.enabled) {
         if (guiButton.id < 100 && guiButton instanceof GuiOptionButton) {
            Options options2 = ((GuiOptionButton)guiButton).returnEnumOptions();
            this.gameSettings.setOptionValue(options2, 1);
            guiButton.displayString = this.gameSettings.getKeyBinding(Options.getEnumOptions(guiButton.id));
            if (this.mc.getIStream().isActive() && options2 != Options.STREAM_CHAT_ENABLED && options2 != Options.STREAM_CHAT_USER_FILTER) {
               this.flag = true;
            }
         } else if (guiButton instanceof GuiOptionSlider) {
            if (guiButton.id == Options.STREAM_VOLUME_MIC.returnEnumOrdinal()) {
               this.mc.getIStream().update3();
            } else if (guiButton.id == Options.STREAM_VOLUME_SYSTEM.returnEnumOrdinal()) {
               this.mc.getIStream().update3();
            } else if (this.mc.getIStream().isActive()) {
               this.flag = true;
            }
         }

         if (guiButton.id == 200) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(this.guiScreen);
         } else if (guiButton.id == 201) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new GuiIngestServers(this));
         }
      }
   }

   @Override
   public void drawScreen(int i, int i2, float f) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.text2, this.width / 2, 20, 16777215);
      this.drawCenteredString(this.fontRendererObj, this.text, this.width / 2, this.intValue, 16777215);
      if (this.flag) {
         this.drawCenteredString(
            this.fontRendererObj,
            EnumChatFormatting.RED + I18n.format("options.stream.changes"),
            this.width / 2,
            20 + this.fontRendererObj.FONT_HEIGHT,
            16777215
         );
      }

      super.drawScreen(i, i2, f);
   }
}
