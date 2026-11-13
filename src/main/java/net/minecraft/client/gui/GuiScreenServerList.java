package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

public class GuiScreenServerList extends GuiScreen {
   private final GuiScreen parentScreen;
   private GuiTextField serverAddressField;
   private final ServerData serverData;

   public GuiScreenServerList(GuiScreen guiScreen, ServerData serverData2) {
      this.parentScreen = guiScreen;
      this.serverData = serverData2;
   }

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.format("selectServer.select")));
      this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")));
      this.serverAddressField = new GuiTextField(2, this.fontRendererObj, this.width / 2 - 100, 116, 200, 20);
      this.serverAddressField.setMaxStringLength(128);
      this.serverAddressField.setFocused(true);
      this.serverAddressField.setText(this.mc.gameSettings.lastServer);
      this.buttonList.get(0).enabled = this.serverAddressField.getText().length() > 0 && this.serverAddressField.getText().split(":").length > 0;
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.serverAddressField.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, I18n.format("selectServer.direct"), this.width / 2, 20, 16777215);
      this.drawString(this.fontRendererObj, I18n.format("addServer.enterIp"), this.width / 2 - 100, 100, 10526880);
      this.serverAddressField.drawTextBox();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if (this.serverAddressField.textboxKeyTyped(typedChar, keyCode)) {
         this.buttonList.get(0).enabled = this.serverAddressField.getText().length() > 0 && this.serverAddressField.getText().split(":").length > 0;
      } else if (keyCode == 28 || keyCode == 156) {
         this.actionPerformed(this.buttonList.get(0));
      }
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         if (button.id == 1) {
            this.parentScreen.confirmClicked(false, 0);
         } else if (button.id == 0) {
            this.serverData.serverIP = this.serverAddressField.getText();
            this.parentScreen.confirmClicked(true, 0);
         }
      }
   }

   @Override
   public void updateScreen() {
      this.serverAddressField.updateCursorCounter();
   }

   @Override
   public void startClosing() {
      Keyboard.enableRepeatEvents(false);
      this.mc.gameSettings.lastServer = this.serverAddressField.getText();
      this.mc.gameSettings.saveOptions();
   }
}
