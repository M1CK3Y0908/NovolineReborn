package net.optifine.shaders.gui;

import java.util.ArrayList;
import java.util.Properties;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraft.src.Config;
import net.optifine.Lang;
import net.optifine.shaders.IShaderPack;
import net.optifine.shaders.Shaders;
import net.optifine.util.ResUtils;

class GuiSlotShaders extends GuiSlot {
   private long lastClickedCached = 0L;
   private ArrayList shaderslist;
   final GuiShaders shadersGui;
   private int selectedIndex;

   public int getSelectedIndex() {
      return this.selectedIndex;
   }

   public GuiSlotShaders(GuiShaders par1GuiShaders, int width, int height, int top, int bottom, int slotHeight) {
      super(par1GuiShaders.getMc(), width, height, top, bottom, slotHeight);
      this.shadersGui = par1GuiShaders;
      this.updateList();
      this.amountScrolled = 0.0F;
      int i = this.selectedIndex * 16;
      int i2 = (bottom - 30) / 2;
      if (i > i2) {
         this.scrollBy(i - i2);
      }
   }

   @Override
   protected void drawSlot(int index, int posX, int posY, int contentY, int mouseX, int mouseY) {
      String text = (String)this.shaderslist.get(index);
      if (text.equals("OFF")) {
         text = Lang.get("of.options.shaders.packNone");
      } else if (text.equals("(internal)")) {
         text = Lang.get("of.options.shaders.packDefault");
      }

      this.shadersGui.handleString(text, this.width / 2, posY + 1);
   }

   @Override
   protected void drawBackground() {
   }

   public void updateList() {
      this.shaderslist = Shaders.listOfShaders();
      this.selectedIndex = 0;
      int i = 0;

      for (int innerIndex = this.shaderslist.size(); i < innerIndex; i++) {
         if (((String)this.shaderslist.get(i)).equals(Shaders.currentShaderName)) {
            this.selectedIndex = i;
            break;
         }
      }
   }

   private boolean checkCompatible(IShaderPack sp, final int index) {
      if (sp == null) {
         return true;
      }

      Properties properties2 = ResUtils.readProperties(sp.getResourceAsStream("/shaders/shaders.properties"), "Shaders");
      if (properties2 == null) {
         return true;
      }

      String property = properties2.getProperty("version.1.8.9");
      if (property == null) {
         return true;
      }

      property = property.trim();
      if (Config.compareRelease("M5", property) >= 0) {
         return true;
      }

      String text2 = ("HD_U_" + property).replace('_', ' ');
      String text3 = I18n.format("of.message.shaders.nv1", text2);
      String text4 = I18n.format("of.message.shaders.nv2");
      GuiYesNoCallback guiYesNoCallback = new GuiYesNoCallback() {
         @Override
         public void confirmClicked(boolean result, int id) {
            if (result) {
               GuiSlotShaders.this.selectIndex(index);
            }

            GuiSlotShaders.this.mc.displayGuiScreen(GuiSlotShaders.this.shadersGui);
         }
      };
      GuiYesNo guiYesNo = new GuiYesNo(guiYesNoCallback, text3, text4, 0);
      this.mc.displayGuiScreen(guiYesNo);
      return false;
   }

   @Override
   protected int getSize() {
      return this.shaderslist.size();
   }

   @Override
   protected boolean isSelected(int index) {
      return index == this.selectedIndex;
   }

   @Override
   protected int getContentHeight() {
      return this.getSize() * 18;
   }

   private void selectIndex(int index) {
      this.selectedIndex = index;
      this.lastClickedCached = this.lastClicked;
      Shaders.setShaderPack((String)this.shaderslist.get(index));
      Shaders.uninit();
      this.shadersGui.updateButtons();
   }

   @Override
   protected int getScrollBarX() {
      return this.width - 6;
   }

   @Override
   protected void elementClicked(int index, boolean doubleClicked, int mouseX, int mouseY) {
      if (index != this.selectedIndex || this.lastClicked != this.lastClickedCached) {
         IShaderPack shaderPack = Shaders.getShaderPack((String)this.shaderslist.get(index));
         if (this.checkCompatible(shaderPack, index)) {
            this.selectIndex(index);
         }
      }
   }

   @Override
   public int getListWidth() {
      return this.width - 20;
   }
}
