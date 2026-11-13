package net.optifine.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.src.Config;

public class GuiMessage extends GuiScreen {
   private final List listLines2 = Lists.newArrayList();
   private int ticksUntilEnable;
   private GuiScreen parentScreen;
   private String messageLine1;
   private String messageLine2;
   protected String confirmButtonText;

   @Override
   public void initGui() {
      this.buttonList.add(new GuiOptionButton(0, this.width / 2 - 74, this.height / 6 + 96, this.confirmButtonText));
      this.listLines2.clear();
      this.listLines2.addAll(this.fontRendererObj.listFormattedStringToWidth(this.messageLine2, this.width - 50));
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      if (--this.ticksUntilEnable == 0) {
         Iterator iterator2 = this.buttonList.iterator();

         while (iterator2.hasNext()) {
            ((GuiButton)iterator2.next()).enabled = true;
         }
      }
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      Config.getMinecraft().displayGuiScreen(this.parentScreen);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.messageLine1, this.width / 2, 70, 16777215);
      int i = 90;

      for (String text : (Iterable<String>) this.listLines2) {
         this.drawCenteredString(this.fontRendererObj, text, this.width / 2, i, 16777215);
         i += this.fontRendererObj.FONT_HEIGHT;
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public GuiMessage(GuiScreen parentScreen, String line1, String line2) {
      this.parentScreen = parentScreen;
      this.messageLine1 = line1;
      this.messageLine2 = line2;
      this.confirmButtonText = I18n.format("gui.done");
   }
}
