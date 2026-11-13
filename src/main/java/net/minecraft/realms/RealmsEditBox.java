package net.minecraft.realms;

import net.minecraft.client.gui.GuiTextField;

public class RealmsEditBox {
   private GuiTextField guiTextField;

   public void update() {
      this.guiTextField.updateCursorCounter();
   }

   public boolean isActive() {
      return this.guiTextField.isFocused();
   }

   public String getString() {
      return this.guiTextField.getText();
   }
}
