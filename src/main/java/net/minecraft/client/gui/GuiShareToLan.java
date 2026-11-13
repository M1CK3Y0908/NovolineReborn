package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class GuiShareToLan extends GuiScreen {
   private String gameMode = "survival";
   private boolean allowCommands;
   private GuiButton allowCommandsButton;
   private GuiButton gameModeButton;
   private final GuiScreen parentScreen;

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, I18n.format("lanServer.title"), this.width / 2, 50, 16777215);
      this.drawCenteredString(this.fontRendererObj, I18n.format("lanServer.otherPlayers"), this.width / 2, 82, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public GuiShareToLan(GuiScreen parentScreen) {
      this.parentScreen = parentScreen;
   }

   private void updateButtonLabels() {
      this.gameModeButton.displayString = I18n.format("selectWorld.gameMode") + " " + I18n.format("selectWorld.gameMode." + this.gameMode);
      this.allowCommandsButton.displayString = I18n.format("selectWorld.allowCommands") + " ";
      if (this.allowCommands) {
         this.allowCommandsButton.displayString = this.allowCommandsButton.displayString + I18n.format("options.on");
      } else {
         this.allowCommandsButton.displayString = this.allowCommandsButton.displayString + I18n.format("options.off");
      }
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 102) {
         this.mc.displayGuiScreen(this.parentScreen);
      } else if (button.id == 104) {
         if (this.gameMode.equals("spectator")) {
            this.gameMode = "creative";
         } else if (this.gameMode.equals("creative")) {
            this.gameMode = "adventure";
         } else if (this.gameMode.equals("adventure")) {
            this.gameMode = "survival";
         } else {
            this.gameMode = "spectator";
         }

         this.updateButtonLabels();
      } else if (button.id == 103) {
         this.allowCommands = !this.allowCommands;
         this.updateButtonLabels();
      } else if (button.id == 101) {
         this.mc.displayGuiScreen((GuiScreen)null);
         String text = this.mc.getIntegratedServer().shareToLAN(net.minecraft.world.WorldSettings.GameType.getByName(this.gameMode), this.allowCommands);
         ChatComponentStyle chatComponentStyle;
         if (text != null) {
            chatComponentStyle = new ChatComponentTranslation("commands.publish.started", text);
         } else {
            chatComponentStyle = new ChatComponentText("commands.publish.failed");
         }

         this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponentStyle);
      }
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(101, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("lanServer.start")));
      this.buttonList.add(new GuiButton(102, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")));
      this.buttonList.add(this.gameModeButton = new GuiButton(104, this.width / 2 - 155, 100, 150, 20, I18n.format("selectWorld.gameMode")));
      this.buttonList.add(this.allowCommandsButton = new GuiButton(103, this.width / 2 + 5, 100, 150, 20, I18n.format("selectWorld.allowCommands")));
      this.updateButtonLabels();
   }
}
