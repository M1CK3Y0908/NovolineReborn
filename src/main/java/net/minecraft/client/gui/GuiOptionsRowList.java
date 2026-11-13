package net.minecraft.client.gui;

import java.util.List;
import net.minecraft.client.Minecraft;

public class GuiOptionsRowList extends GuiListExtended {
   public GuiOptionsRowList() {
      super((Minecraft) null, 0, 0, 0, 0, 0);
   }

   private List<GuiOptionsRowList.Row> rows;

   @Override
   protected int getSize() {
      return this.rows.size();
   }

   @Override
   public int getListWidth() {
      return 400;
   }

   @Override
   protected int getScrollBarX() {
      return super.getScrollBarX() + 32;
   }

   public GuiOptionsRowList.Row getListEntry(int index) {
      return this.rows.get(index);
   }

   public static class Row implements GuiListExtended.IGuiListEntry {
      private final Minecraft mc = Minecraft.getMinecraft();
      private final GuiButton leftButton;
      private final GuiButton rightButton;

      public Row(GuiButton leftButton, GuiButton rightButton) {
         this.leftButton = leftButton;
         this.rightButton = rightButton;
      }

      @Override
      public void noop(int i) {
      }

      @Override
      public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
         if (this.leftButton != null) {
            this.leftButton.yPosition = y;
            this.leftButton.drawButton(this.mc, mouseX, mouseY);
         }

         if (this.rightButton != null) {
            this.rightButton.yPosition = y;
            this.rightButton.drawButton(this.mc, mouseX, mouseY);
         }
      }

      @Override
      public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
         if (this.leftButton.mousePressed(this.mc, mouseX, mouseY)) {
            if (this.leftButton instanceof GuiOptionButton) {
               this.mc.gameSettings.setOptionValue(((GuiOptionButton)this.leftButton).returnEnumOptions(), 1);
               this.leftButton.displayString = this.mc.gameSettings.getKeyBinding(net.minecraft.client.settings.GameSettings.Options.getEnumOptions(this.leftButton.id));
            }

            return true;
         } else if (this.rightButton != null && this.rightButton.mousePressed(this.mc, mouseX, mouseY)) {
            if (this.rightButton instanceof GuiOptionButton) {
               this.mc.gameSettings.setOptionValue(((GuiOptionButton)this.rightButton).returnEnumOptions(), 1);
               this.rightButton.displayString = this.mc.gameSettings.getKeyBinding(net.minecraft.client.settings.GameSettings.Options.getEnumOptions(this.rightButton.id));
            }

            return true;
         } else {
            return false;
         }
      }

      @Override
      public void update() {
         if (this.leftButton != null) {
            this.leftButton.update();
         }

         if (this.rightButton != null) {
            this.rightButton.update();
         }
      }
   }
}
