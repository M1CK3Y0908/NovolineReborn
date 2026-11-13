package net.minecraft.client.gui;

import java.io.IOException;
import java.util.Iterator;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import wtf.novoline.gui.NovolineMainMenu;

public class GuiGameOver extends GuiScreen implements GuiYesNoCallback {
   private int enableButtonsTimer;
   private boolean unusedFlag = false;

   @Override
   public void updateScreen() {
      super.updateScreen();
      this.enableButtonsTimer++;
      if (this.enableButtonsTimer == 20) {
         Iterator iterator2 = this.buttonList.iterator();

         while (iterator2.hasNext()) {
            ((GuiButton)iterator2.next()).enabled = true;
         }
      }
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }

   @Override
   public void confirmClicked(boolean result, int id) {
      if (result) {
         this.mc.theWorld.sendQuittingDisconnectingPacket();
         this.mc.loadWorld((WorldClient)null);
         this.mc.displayGuiScreen(new NovolineMainMenu());
      } else {
         this.mc.thePlayer.respawnPlayer();
         this.mc.displayGuiScreen((GuiScreen)null);
      }
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      switch (button.id) {
         case 0:
            this.mc.thePlayer.respawnPlayer();
            this.mc.displayGuiScreen((GuiScreen)null);
            break;
         case 1:
            if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
               this.mc.displayGuiScreen(new NovolineMainMenu());
            } else {
               GuiYesNo guiYesNo = new GuiYesNo(
                  this, I18n.format("deathScreen.quit.confirm"), "", I18n.format("deathScreen.titleScreen"), I18n.format("deathScreen.respawn"), 0
               );
               this.mc.displayGuiScreen(guiYesNo);
               guiYesNo.update2();
            }
      }
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
         if (this.mc.isIntegratedServerRunning()) {
            this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 96, I18n.format("deathScreen.deleteWorld")));
         } else {
            this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 96, I18n.format("deathScreen.leaveServer")));
         }
      } else {
         this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 72, I18n.format("deathScreen.respawn")));
         this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 96, I18n.format("deathScreen.titleScreen")));
         if (this.mc.getSession() == null) {
            this.buttonList.get(1).enabled = false;
         }
      }

      Iterator iterator2 = this.buttonList.iterator();

      while (iterator2.hasNext()) {
         ((GuiButton)iterator2.next()).enabled = false;
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawGradientRect(0, 0, this.width, this.height, 1615855616, -1602211792);
      GlStateManager.pushMatrix();
      GlStateManager.scale(2.0F, 2.0F, 2.0F);
      boolean conditionMet = this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled();
      String text = conditionMet ? I18n.format("deathScreen.title.hardcore") : I18n.format("deathScreen.title");
      this.drawCenteredString(this.fontRendererObj, text, this.width / 2 / 2, 30, 16777215);
      GlStateManager.popMatrix();
      if (conditionMet) {
         this.drawCenteredString(this.fontRendererObj, I18n.format("deathScreen.hardcoreInfo"), this.width / 2, 144, 16777215);
      }

      this.drawCenteredString(
         this.fontRendererObj,
         I18n.format("deathScreen.score") + ": " + EnumChatFormatting.YELLOW + this.mc.thePlayer.getScore(),
         this.width / 2,
         100,
         16777215
      );
      super.drawScreen(mouseX, mouseY, partialTicks);
   }
}
