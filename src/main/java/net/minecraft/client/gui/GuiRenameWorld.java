package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

public class GuiRenameWorld extends GuiScreen {
   private GuiScreen parentScreen;
   private GuiTextField worldNameField;
   private final String saveName;

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      this.worldNameField.textboxKeyTyped(typedChar, keyCode);
      this.buttonList.get(0).enabled = this.worldNameField.getText().trim().length() > 0;
      if (keyCode == 28 || keyCode == 156) {
         this.actionPerformed(this.buttonList.get(0));
      }
   }

   @Override
   public void startClosing() {
      Keyboard.enableRepeatEvents(false);
   }

   @Override
   public void updateScreen() {
      this.worldNameField.updateCursorCounter();
   }

   public GuiRenameWorld(GuiScreen parentScreenIn, String saveNameIn) {
      this.parentScreen = parentScreenIn;
      this.saveName = saveNameIn;
   }

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.format("selectWorld.renameButton")));
      this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")));
      String worldName = this.mc.getSaveLoader().getWorldInfo(this.saveName).getWorldName();
      this.worldNameField = new GuiTextField(2, this.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
      this.worldNameField.setFocused(true);
      this.worldNameField.setText(worldName);
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.worldNameField.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         if (button.id == 1) {
            this.mc.displayGuiScreen(this.parentScreen);
         } else if (button.id == 0) {
            this.mc.getSaveLoader().renameWorld(this.saveName, this.worldNameField.getText().trim());
            this.mc.displayGuiScreen(this.parentScreen);
         }
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, I18n.format("selectWorld.renameTitle"), this.width / 2, 20, 16777215);
      this.drawString(this.fontRendererObj, I18n.format("selectWorld.enterName"), this.width / 2 - 100, 47, 10526880);
      this.worldNameField.drawTextBox();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }
}
