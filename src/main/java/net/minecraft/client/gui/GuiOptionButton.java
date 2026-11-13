package net.minecraft.client.gui;


public class GuiOptionButton extends GuiButton {
   private final net.minecraft.client.settings.GameSettings.Options enumOptions;

   public GuiOptionButton(int i, int i2, int i3, String text) {
      this(i, i2, i3, (net.minecraft.client.settings.GameSettings.Options)null, text);
   }

   public GuiOptionButton(int i, int i2, int i3, net.minecraft.client.settings.GameSettings.Options options, String text) {
      super(i, i2, i3, 150, 20, text);
      this.enumOptions = options;
   }

   public net.minecraft.client.settings.GameSettings.Options returnEnumOptions() {
      return this.enumOptions;
   }

   public GuiOptionButton(int i, int i2, int i3, int i4, int i5, String text) {
      super(i, i2, i3, i4, i5, text);
      this.enumOptions = null;
   }
}
