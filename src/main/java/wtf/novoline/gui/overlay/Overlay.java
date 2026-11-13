package wtf.novoline.gui.overlay;

import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import wtf.novoline.Novoline;
import wtf.novoline.utils.notification.Notification;
import wtf.novoline.utils.notification.NotificationType;
import wtf.novoline.utils.render.BlurRenderer;

public class Overlay {
   protected final Minecraft minecraft = Minecraft.getMinecraft();
   protected final BlurRenderer blurRenderer = new BlurRenderer(Novoline.framebufferManager);
   protected final GuiScreen guiScreen;

   public void keyTyped(char c, int i) {
   }

   public void drawScreen(int i, int i2, float f) {
      List.copyOf(this.guiScreen.buttonList)
         .stream()
         .filter(Objects::nonNull)
         .filter(guiButton -> guiButton.id < 0)
         .forEach(guiButton -> guiButton.drawButton(this.minecraft, i, i2));
   }

   public void mouseReleased() {
   }

   public void actionPerformed(GuiButton guiButton2) {
   }

   public void mouseClicked(int i, int i2, int i3) {
   }

   protected void showNotification(NotificationType notificationType, String text, String text2, int i) {
      Novoline.getNovoline().getNotificationManager().show(new Notification(notificationType, text, text2, i));
   }

   public void onClose() {
   }

   public Overlay(GuiScreen guiScreen2) {
      this.guiScreen = guiScreen2;
   }

   public void init() {
   }
}
