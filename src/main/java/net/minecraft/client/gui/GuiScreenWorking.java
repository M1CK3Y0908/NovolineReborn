package net.minecraft.client.gui;

import net.minecraft.util.IProgressUpdate;
import net.optifine.CustomLoadingScreen;
import net.optifine.CustomLoadingScreens;

public class GuiScreenWorking extends GuiScreen implements IProgressUpdate {
   private String title = "";
   private String status = "";
   private boolean doneWorking;
   private CustomLoadingScreen customLoadingScreen = CustomLoadingScreens.getCustomLoadingScreen();
   private int progress;

   @Override
   public void displayLoadingString(String message) {
      this.status = message;
      this.setLoadingProgress(0);
   }

   @Override
   public void displaySavingString(String message) {
      this.resetProgressAndMessage(message);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if (this.doneWorking) {
         if (!this.mc.isFlag2()) {
            this.mc.displayGuiScreen((GuiScreen)null);
         }
      } else {
         if (this.customLoadingScreen != null && this.mc.theWorld == null) {
            this.customLoadingScreen.drawBackground(this.width, this.height);
         } else {
            this.drawDefaultBackground();
         }

         if (this.progress > 0) {
            this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 70, 16777215);
            this.drawCenteredString(this.fontRendererObj, this.status + " " + this.progress + "%", this.width / 2, 90, 16777215);
         }

         super.drawScreen(mouseX, mouseY, partialTicks);
      }
   }

   @Override
   public void resetProgressAndMessage(String message) {
      this.title = message;
      this.displayLoadingString("Working...");
   }

   @Override
   public void setLoadingProgress(int progress) {
      this.progress = progress;
   }

   @Override
   public void setDoneWorking() {
      this.doneWorking = true;
   }
}
