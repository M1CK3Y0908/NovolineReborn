package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import wtf.novoline.Novoline;
import wtf.novoline.gui.AltManagerScreen;
import wtf.novoline.gui.NovolineMainMenu;

public class GuiIngameMenu extends GuiScreen {
   private int visibleTime;
   private int saveStep;

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      switch (button.id) {
         case 0:
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
            break;
         case 1:
            boolean integratedServerRunning = this.mc.isIntegratedServerRunning();
            boolean flag = this.mc.isFlag2();
            button.enabled = false;
            this.mc.theWorld.sendQuittingDisconnectingPacket();
            this.mc.loadWorld((WorldClient)null);
            if (integratedServerRunning) {
               this.mc.displayGuiScreen(new NovolineMainMenu());
            } else if (flag) {
               RealmsBridge realmsBridge = new RealmsBridge();
               realmsBridge.handleGuiScreen(new NovolineMainMenu());
            } else {
               this.mc.displayGuiScreen(new GuiMultiplayer(new NovolineMainMenu()));
            }
         case 2:
         case 3:
         default:
            break;
         case 4:
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
            break;
         case 5:
            this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.getStatFileWriter()));
            break;
         case 6:
            this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
            break;
         case 7:
            this.mc.displayGuiScreen(new GuiShareToLan(this));
            break;
         case 8:
            ServerData serverData = Novoline.getNovoline().getServerData();
            this.mc.theWorld.sendQuittingDisconnectingPacket();
            this.mc.displayGuiScreen(new GuiConnecting(null, this.mc, serverData));
            break;
         case 9:
            AltManagerScreen altManagerScreen = Novoline.getNovoline().getAltManagerScreen();
            this.mc.displayGuiScreen(altManagerScreen);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, I18n.format("menu.game"), this.width / 2, 40, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      this.visibleTime++;
   }

   @Override
   public void initGui() {
      this.saveStep = 0;
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + -16, 98, 20, I18n.format("menu.options")));
      this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + -16, I18n.format("menu.returnToMenu")));
      this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 24 + -16, I18n.format("menu.returnToGame")));
      this.buttonList.add(new GuiButton(5, this.width / 2 - 100, this.height / 4 + 48 + -16, 98, 20, I18n.format("gui.achievements")));
      this.buttonList.add(new GuiButton(6, this.width / 2 + 2, this.height / 4 + 48 + -16, 98, 20, I18n.format("gui.stats")));
      this.buttonList.add(new GuiButton(9, this.width / 2 + 2, this.height / 4 + 72 + -16, 98, 20, "Alt Manager"));
      GuiButton guiButton = new GuiButton(7, this.width / 2 + 2, this.height / 4 + 96 + -16, 98, 20, I18n.format("menu.shareToLan"));
      guiButton.enabled = this.mc.isSingleplayer() && !this.mc.getIntegratedServer().getPublic();
      this.buttonList.add(guiButton);
      guiButton = new GuiButton(8, this.width / 2 - 100, this.height / 4 + 72 + -16, 98, 20, "Reconnect");
      guiButton.enabled = this.mc.getCurrentServerData() != null;
      this.buttonList.add(guiButton);
      if (!this.mc.isIntegratedServerRunning()) {
         this.buttonList.get(1).displayString = I18n.format("menu.disconnect");
      }
   }
}
