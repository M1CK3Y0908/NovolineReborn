package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class ServerListEntryLanDetected implements GuiListExtended.IGuiListEntry {
   private long lastClickTime = 0L;
   private final GuiMultiplayer owner;
   protected final net.minecraft.client.network.LanServerDetector.LanServer lanServer;
   protected final Minecraft mc;

   @Override
   public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
      this.mc.fontRendererObj.getInt(I18n.format("lanServer.title"), x + 32 + 3, y + 1, 16777215);
      this.mc.fontRendererObj.getInt(this.lanServer.getServerMotd(), x + 32 + 3, y + 12, 8421504);
      if (this.mc.gameSettings.hideServerAddress) {
         this.mc.fontRendererObj.getInt(I18n.format("selectServer.hiddenAddress"), x + 32 + 3, y + 12 + 11, 3158064);
      } else {
         this.mc.fontRendererObj.getInt(this.lanServer.getServerIpPort(), x + 32 + 3, y + 12 + 11, 3158064);
      }
   }

   protected ServerListEntryLanDetected(GuiMultiplayer guiMultiplayer, net.minecraft.client.network.LanServerDetector.LanServer lanServer2) {
      this.owner = guiMultiplayer;
      this.lanServer = lanServer2;
      this.mc = Minecraft.getMinecraft();
   }

   @Override
   public boolean mousePressed(int slotIndex, int i, int i2, int i3, int i4, int i5) {
      this.owner.selectServer(slotIndex);
      if (Minecraft.getSystemTime() - this.lastClickTime < 250L) {
         this.owner.connectToSelected();
      }

      this.lastClickTime = Minecraft.getSystemTime();
      return false;
   }

   @Override
   public void update() {
   }

   @Override
   public void noop(int i) {
   }

   public net.minecraft.client.network.LanServerDetector.LanServer getLanServer() {
      return this.lanServer;
   }
}
