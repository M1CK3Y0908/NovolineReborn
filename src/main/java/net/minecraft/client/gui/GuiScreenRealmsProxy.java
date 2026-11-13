package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.realms.RealmsButton;

public class GuiScreenRealmsProxy extends GuiScreen {
   private CommandSuggestions commandSuggestions;

   @Override
   public void drawHoveringText(List list, int i, int i2) {
      super.drawHoveringText(list, i, i2);
   }

   @Override
   public void drawScreen(int i, int i2, float f) {
      this.commandSuggestions.handleInt(i, i2, f);
   }

   @Override
   public void updateScreen() {
      this.commandSuggestions.noop5();
      super.updateScreen();
   }

   @Override
   public void keyTyped(char c, int i) throws IOException {
      this.commandSuggestions.noop12(i);
   }

   @Override
   public final void actionPerformed(GuiButton guiButton) throws IOException {
      this.commandSuggestions.noop(((GuiButtonRealmsProxy)guiButton).getRealmsButton());
   }

   @Override
   public void drawDefaultBackground() {
      super.drawDefaultBackground();
   }

   @Override
   public void drawCreativeTabHoveringText(String text, int i, int i2) {
      super.drawCreativeTabHoveringText(text, i, i2);
   }

   @Override
   public void mouseReleased(int i, int i2, int i3) {
      this.commandSuggestions.noop2();
   }

   @Override
   public void renderToolTip(ItemStack itemStack, int i, int i2) {
      super.renderToolTip(itemStack, i, i2);
   }

   @Override
   public void drawGradientRect(int i, int i2, int i3, int i4, int i5, int i6) {
      super.drawGradientRect(i, i2, i3, i4, i5, i6);
   }

   public void handleString(String text, int i, int i2, int i3) {
      super.drawCenteredString(this.fontRendererObj, text, i, i2, i3);
   }

   public List<String> getList(String text, int i) {
      return this.fontRendererObj.listFormattedStringToWidth(text, i);
   }

   public GuiScreenRealmsProxy(CommandSuggestions commandSuggestions2) {
      this.commandSuggestions = commandSuggestions2;
      super.buttonList = Collections.synchronizedList(Lists.newArrayList());
   }

   public void update2() {
      super.buttonList.clear();
   }

   @Override
   public void update3() {
      super.update3();
   }

   @Override
   public void handleMouseInput() throws IOException {
      this.commandSuggestions.noop7();
      super.handleMouseInput();
   }

   @Override
   public void confirmClicked(boolean flag, int i) {
      this.commandSuggestions.noop11();
   }

   @Override
   public void initGui() {
      this.commandSuggestions.noop9();
      super.initGui();
   }

   public void handleRealmsButton(RealmsButton realmsButton) {
      super.buttonList.add(realmsButton.getGuiButtonRealmsProxy());
   }

   public int getInt2(String text) {
      return this.fontRendererObj.getInt3(text);
   }

   @Override
   public boolean doesGuiPauseGame() {
      return super.doesGuiPauseGame();
   }

   @Override
   public void handleInt7(int i, int i2, int i3) {
      this.commandSuggestions.noop3();
   }

   public void handleRealmsButton2(RealmsButton realmsButton) {
      super.buttonList.remove(realmsButton.getGuiButtonRealmsProxy());
   }

   @Override
   public void startClosing() {
      this.commandSuggestions.noop6();
      super.startClosing();
   }

   public List getList2() {
      ArrayList arrayList = Lists.newArrayListWithExpectedSize(super.buttonList.size());

      for (GuiButton guiButton : super.buttonList) {
         arrayList.add(((GuiButtonRealmsProxy)guiButton).getRealmsButton());
      }

      return arrayList;
   }

   public int getInt() {
      return this.fontRendererObj.FONT_HEIGHT;
   }

   @Override
   public void handleKeyboardInput() throws IOException {
      this.commandSuggestions.noop4();
      super.handleKeyboardInput();
   }

   public CommandSuggestions getCommandSuggestions() {
      return this.commandSuggestions;
   }

   @Override
   public void mouseClicked(int i, int i2, int i3) throws IOException {
      this.commandSuggestions.noop8();
      super.mouseClicked(i, i2, i3);
   }
}
