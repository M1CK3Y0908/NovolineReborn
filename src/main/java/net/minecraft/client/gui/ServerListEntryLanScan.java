package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class ServerListEntryLanScan implements GuiListExtended.IGuiListEntry {
   private final Minecraft mc = Minecraft.getMinecraft();

   @Override
   public boolean mousePressed(int slotIndex, int i, int i2, int i3, int i4, int i5) {
      return false;
   }

   @Override
   public void update() {
   }

   @Override
   public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
      int i = y + slotHeight / 2 - this.mc.fontRendererObj.FONT_HEIGHT / 2;
      this.mc
         .fontRendererObj
         .getInt(
            I18n.format("lanServer.scanning"),
            this.mc.currentScreen.width / 2 - this.mc.fontRendererObj.getInt3(I18n.format("lanServer.scanning")) / 2,
            i,
            16777215
         );

      String text = switch ((int)(Minecraft.getSystemTime() / 300L % 4L)) {
         default -> "O o o";
         case 1, 3 -> "o O o";
         case 2 -> "o o O";
      };
      this.mc
         .fontRendererObj
         .getInt(text, this.mc.currentScreen.width / 2 - this.mc.fontRendererObj.getInt3(text) / 2, i + this.mc.fontRendererObj.FONT_HEIGHT, 8421504);
   }

   @Override
   public void noop(int i) {
   }
}
