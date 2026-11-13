package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiListButton extends GuiButton {
   private String localizationStr;
   private boolean value;
   private final GuiPageButtonList.GuiResponder guiResponder;

   @Override
   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      if (super.mousePressed(mc, mouseX, mouseY)) {
         this.value = !this.value;
         this.displayString = this.buildDisplayString();
         this.guiResponder.setEntryValue(this.id, this.value);
         return true;
      } else {
         return false;
      }
   }

   private String buildDisplayString() {
      return I18n.format(this.localizationStr) + ": " + (this.value ? I18n.format("gui.yes") : I18n.format("gui.no"));
   }

   public void setValue(boolean value) {
      this.value = value;
      this.displayString = this.buildDisplayString();
      this.guiResponder.setEntryValue(this.id, value);
   }

   public GuiListButton(GuiPageButtonList.GuiResponder responder, int id, int x, int y, String localizationStr, boolean initialValue) {
      super(id, x, y, 150, 20, "");
      this.localizationStr = localizationStr;
      this.value = initialValue;
      this.displayString = this.buildDisplayString();
      this.guiResponder = responder;
   }
}
