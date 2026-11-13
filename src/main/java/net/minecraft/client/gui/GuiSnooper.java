package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

public class GuiSnooper extends GuiScreen {
   private final List<String> keys = Lists.newArrayList();
   private final List<String> values = Lists.newArrayList();
   private String text;
   private String[] stringArray;
   private GuiButton toggleButton;
   private final GuiScreen parent;
   private final GameSettings settings;
   private GuiSnooper.StatsList statsList;

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         if (button.id == 2) {
            this.settings.saveOptions();
            this.settings.saveOptions();
            this.mc.displayGuiScreen(this.parent);
         }

         if (button.id == 1) {
            this.settings.setOptionValue(net.minecraft.client.settings.GameSettings.Options.SNOOPER_ENABLED, 1);
            this.toggleButton.displayString = this.settings.getKeyBinding(net.minecraft.client.settings.GameSettings.Options.SNOOPER_ENABLED);
         }
      }
   }

   public GuiSnooper(GuiScreen parent, GameSettings settings) {
      this.parent = parent;
      this.settings = settings;
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.statsList.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRendererObj, this.text, this.width / 2, 8, 16777215);
      int i = 22;

      for (String text2 : this.stringArray) {
         this.drawCenteredString(this.fontRendererObj, text2, this.width / 2, i, 8421504);
         i += this.fontRendererObj.FONT_HEIGHT;
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void initGui() {
      this.text = I18n.format("options.snooper.title");
      String text2 = I18n.format("options.snooper.desc");
      ArrayList arrayList = Lists.newArrayList();

      for (String text3 : this.fontRendererObj.listFormattedStringToWidth(text2, this.width - 30)) {
         arrayList.add(text3);
      }

      this.stringArray = (String[]) arrayList.toArray(new String[arrayList.size()]);
      this.keys.clear();
      this.values.clear();
      this.buttonList
         .add(
            this.toggleButton = new GuiButton(
               1, this.width / 2 - 152, this.height - 30, 150, 20, this.settings.getKeyBinding(net.minecraft.client.settings.GameSettings.Options.SNOOPER_ENABLED)
            )
         );
      this.buttonList.add(new GuiButton(2, this.width / 2 + 2, this.height - 30, 150, 20, I18n.format("gui.done")));
      boolean flag = this.mc.getIntegratedServer() != null && this.mc.getIntegratedServer().getPlayerUsageSnooper() != null;

      for (Entry entry : new TreeMap<>(this.mc.getPlayerUsageSnooper().getCurrentStats()).entrySet()) {
         this.keys.add((flag ? "C " : "") + (String)entry.getKey());
         this.values.add(this.fontRendererObj.trimStringToWidth((String)entry.getValue(), this.width - 220));
      }

      if (flag) {
         for (Entry entry2 : new TreeMap<>(this.mc.getIntegratedServer().getPlayerUsageSnooper().getCurrentStats()).entrySet()) {
            this.keys.add("S " + (String)entry2.getKey());
            this.values.add(this.fontRendererObj.trimStringToWidth((String)entry2.getValue(), this.width - 220));
         }
      }

      this.statsList = new GuiSnooper.StatsList();
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.statsList.handleMouseInput();
   }

   private class StatsList extends GuiSlot {
      @Override
      protected void drawBackground() {
      }

      @Override
      protected void elementClicked(int index, boolean doubleClick, int mouseX, int mouseY) {
      }

      @Override
      protected int getScrollBarX() {
         return this.width - 10;
      }

      @Override
      protected void drawSlot(int index, int x, int y, int height, int mouseX, int mouseY) {
         GuiSnooper.this.fontRendererObj.getInt(GuiSnooper.this.keys.get(index), 10, y, 16777215);
         GuiSnooper.this.fontRendererObj.getInt(GuiSnooper.this.values.get(index), 230, y, 16777215);
      }

      @Override
      protected int getSize() {
         return GuiSnooper.this.keys.size();
      }

      @Override
      protected boolean isSelected(int index) {
         return false;
      }

      public StatsList() {
         super(
            GuiSnooper.this.mc, GuiSnooper.this.width, GuiSnooper.this.height, 80, GuiSnooper.this.height - 40, GuiSnooper.this.fontRendererObj.FONT_HEIGHT + 1
         );
      }
   }
}
