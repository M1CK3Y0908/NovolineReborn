package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class GuiSleepMP extends GuiChat {
   @Override
   public void initGui() {
      super.initGui();
      this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height - 40, I18n.format("multiplayer.stopSleeping")));
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 1) {
         this.wakeFromSleep();
      } else {
         super.actionPerformed(button);
      }
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if (keyCode == 1) {
         this.wakeFromSleep();
      } else if (keyCode != 28 && keyCode != 156) {
         super.keyTyped(typedChar, keyCode);
      } else {
         String text = this.inputField.getText().trim();
         if (!text.isEmpty()) {
            this.mc.thePlayer.sendChatMessage(text);
         }

         this.inputField.setText("");
         this.mc.ingameGUI.getChatGUI().resetScroll();
      }
   }

   private void wakeFromSleep() {
      this.mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(this.mc.thePlayer, net.minecraft.network.play.client.C0BPacketEntityAction.Action.STOP_SLEEPING));
   }
}
