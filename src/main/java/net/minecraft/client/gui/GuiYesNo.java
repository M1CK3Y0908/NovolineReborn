package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.resources.I18n;

public class GuiYesNo extends GuiScreen {
   private final List<String> multilineMessage = Lists.newArrayList();
   protected GuiYesNoCallback parentScreen;
   protected String messageLine1;
   private String messageLine2;
   protected String confirmButtonText;
   private int ticksUntilEnable;
   protected String cancelButtonText;
   protected int parentButtonClickedId;

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

   public void update2() {
      this.ticksUntilEnable = 20;
      Iterator iterator2 = this.buttonList.iterator();

      while (iterator2.hasNext()) {
         ((GuiButton)iterator2.next()).enabled = false;
      }
   }

   public GuiYesNo(
      GuiYesNoCallback parentScreenIn,
      String messageLine1In,
      String messageLine2In,
      String confirmButtonTextIn,
      String cancelButtonTextIn,
      int parentButtonClickedIdIn
   ) {
      this.parentScreen = parentScreenIn;
      this.messageLine1 = messageLine1In;
      this.messageLine2 = messageLine2In;
      this.confirmButtonText = confirmButtonTextIn;
      this.cancelButtonText = cancelButtonTextIn;
      this.parentButtonClickedId = parentButtonClickedIdIn;
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      this.parentScreen.confirmClicked(button.id == 0, this.parentButtonClickedId);
   }

   @Override
   public void initGui() {
      this.buttonList.add(new GuiOptionButton(0, this.width / 2 - 155, this.height / 6 + 96, this.confirmButtonText));
      this.buttonList.add(new GuiOptionButton(1, this.width / 2 - 155 + 160, this.height / 6 + 96, this.cancelButtonText));
      this.multilineMessage.clear();
      this.multilineMessage.addAll(this.fontRendererObj.listFormattedStringToWidth(this.messageLine2, this.width - 50));
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.messageLine1, this.width / 2, 70, 16777215);
      int i = 90;

      for (String text : this.multilineMessage) {
         this.drawCenteredString(this.fontRendererObj, text, this.width / 2, i, 16777215);
         i += this.fontRendererObj.FONT_HEIGHT;
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public GuiYesNo(GuiYesNoCallback parentScreenIn, String messageLine1In, String messageLine2In, int parentButtonClickedIdIn) {
      this.parentScreen = parentScreenIn;
      this.messageLine1 = messageLine1In;
      this.messageLine2 = messageLine2In;
      this.parentButtonClickedId = parentButtonClickedIdIn;
      this.confirmButtonText = I18n.format("gui.yes");
      this.cancelButtonText = I18n.format("gui.no");
   }
}
