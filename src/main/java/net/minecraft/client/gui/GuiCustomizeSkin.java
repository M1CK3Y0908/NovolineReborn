package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.optifine.gui.GuiButtonOF;
import net.optifine.gui.GuiScreenCapeOF;

public class GuiCustomizeSkin extends GuiScreen {
   private final GuiScreen parentScreen;
   private String title;

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         if (button.id == 210) {
            this.mc.displayGuiScreen(new GuiScreenCapeOF(this));
         }

         if (button.id == 200) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(this.parentScreen);
         } else if (button instanceof GuiCustomizeSkin.ButtonPart) {
            EnumPlayerModelParts playerModelParts = ((GuiCustomizeSkin.ButtonPart)button).playerModelParts;
            this.mc.gameSettings.switchModelPartEnabled(playerModelParts);
            button.displayString = this.getMessage(playerModelParts);
         }
      }
   }

   public GuiCustomizeSkin(GuiScreen parentScreenIn) {
      this.parentScreen = parentScreenIn;
   }

   private String getMessage(EnumPlayerModelParts playerModelParts) {
      String text;
      if (this.mc.gameSettings.getModelParts().contains(playerModelParts)) {
         text = I18n.format("options.on");
      } else {
         text = I18n.format("options.off");
      }

      return playerModelParts.getNameComponent().getFormattedText() + ": " + text;
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 20, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void initGui() {
      int i = 0;
      this.title = I18n.format("options.skinCustomisation.title");

      for (EnumPlayerModelParts enumPlayerModelParts : EnumPlayerModelParts.values()) {
         this.buttonList
            .add(
               new GuiCustomizeSkin.ButtonPart(enumPlayerModelParts.getPartId(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, enumPlayerModelParts)
            );
         i++;
      }

      if (i % 2 == 1) {
         i++;
      }

      this.buttonList.add(new GuiButtonOF(210, this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), I18n.format("of.options.skinCustomisation.ofCape")));
      i += 2;
      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), I18n.format("gui.done")));
   }

   class ButtonPart extends GuiButton {
      private final EnumPlayerModelParts playerModelParts;

      private ButtonPart(int buttonId, int x, int y, int widthIn, int heightIn, EnumPlayerModelParts playerModelParts) {
         super(buttonId, x, y, widthIn, heightIn, GuiCustomizeSkin.this.getMessage(playerModelParts));
         this.playerModelParts = playerModelParts;
      }
   }
}
