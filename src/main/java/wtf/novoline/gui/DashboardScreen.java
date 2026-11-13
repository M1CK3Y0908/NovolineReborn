package wtf.novoline.gui;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import wtf.novoline.Novoline;
import wtf.novoline.gui.dashboard.DashboardMenu;
import wtf.novoline.module.ModuleManager;

public class DashboardScreen extends GuiScreen {
   public DashboardMenu dashboardMenu;

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }

   public DashboardScreen() {
      this.mc = Minecraft.getMinecraft();
      this.dashboardMenu = new DashboardMenu(90.0F, 60.0F);
   }

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
   }

   @Override
   public void keyTyped(char c, int i) throws IOException {
      this.dashboardMenu.keyTyped(c, i);
      super.keyTyped(c, i);
   }

   @Override
   public void drawScreen(int i, int i2, float f) {
      this.mc = Minecraft.getMinecraft();
      ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
      float f2 = (float)i * scaledResolution.getScaleFactor() / 2.0F;
      float f3 = (float)i2 * scaledResolution.getScaleFactor() / 2.0F;
      GlStateManager.pushMatrix();
      float scaleFactor = scaledResolution.getScaleFactor();
      double d = scaledResolution.getScaledWidth_double();
      double d2 = scaledResolution.getScaledHeight_double();
      scaledResolution.handleDouble2(d / 2.0 * scaleFactor);
      scaledResolution.handleDouble(d2 / 2.0 * scaleFactor);
      this.dashboardMenu.draw(f2, f3, f);
      GlStateManager.popMatrix();
   }

   @Override
   public void startClosing() {
      Keyboard.enableRepeatEvents(false);
   }

   @Override
   public void mouseClicked(int i, int i2, int i3) throws IOException {
      ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
      float f = (float)i * scaledResolution.getScaleFactor() / 2.0F;
      float f2 = (float)i2 * scaledResolution.getScaleFactor() / 2.0F;
      if (i3 == 0
         && f >= 10.0F
         && f <= 30.0F
         && f2 >= (float)scaledResolution.getScaledHeight() * scaledResolution.getScaleFactor() / 2.0F - 20.0F - 10.0F
         && f2 <= (float)scaledResolution.getScaledHeight() * scaledResolution.getScaleFactor() / 2.0F - 20.0F - 10.0F + 20.0F) {
         Novoline.getNovoline().getMenuClickGuiScreen().dashboardOpen = false;
         if (Novoline.getNovoline().getMenuClickGuiScreen().returnToClickGui) {
            Novoline.getNovoline().getMenuClickGuiScreen().returnToClickGui = false;
            ModuleManager.clickGui.clickGui.setCurrentElement(1);
            ModuleManager.clickGui.disable();
            ModuleManager.clickGui.enable();
         }
      }

      this.dashboardMenu.handleMouseClick(f, f2, i3);
   }

   @Override
   public void mouseReleased(int i, int i2, int i3) {
      ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
      float f = (float)i * scaledResolution.getScaleFactor() / 2.0F;
      float f2 = (float)i2 * scaledResolution.getScaleFactor() / 2.0F;
      this.dashboardMenu.mouseReleased(f, f2, i3);
   }
}
