package net.minecraft.client.gui;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsAnvilLevelStorageSource;
import net.minecraft.realms.RealmsButton;

public class CommandSuggestions {
   public int intValue;
   public static final int intValue2 = 8;
   protected Minecraft mc;
   public static final int intValue3 = 8;
   public static final int intValue4 = 64;
   public static final int intValue5 = 64;
   public static final int intValue6 = 8;
   private GuiScreenRealmsProxy guiScreenRealmsProxy = new GuiScreenRealmsProxy(this);
   public static final int intValue7 = 8;
   public int intValue8;
   public static final int intValue9 = 8;
   public static final int intValue10 = 8;
   public static final int intValue11 = 8;
   public static final int intValue12 = 40;

   public void handleInt(int i, int i2, float f) {
      for (int i3 = 0; i3 < this.guiScreenRealmsProxy.getList2().size(); i3++) {
         ((RealmsButton)this.guiScreenRealmsProxy.getList2().get(i3)).handleInt2(i, i2);
      }
   }

   public void handleRealmsButton2(RealmsButton realmsButton) {
      this.guiScreenRealmsProxy.handleRealmsButton2(realmsButton);
   }

   public GuiScreenRealmsProxy getGuiScreenRealmsProxy() {
      return this.guiScreenRealmsProxy;
   }

   public void noop2() {
   }

   public void noop7() {
   }

   public void noop8() {
   }

   public int getInt2() {
      return this.guiScreenRealmsProxy.width;
   }

   public int getInt3() {
      return this.guiScreenRealmsProxy.height;
   }

   public void noop5() {
   }

   public void noop11() {
   }

   public void noop9() {
   }

   public void noop(RealmsButton realmsButton) {
   }

   public void handleRealmsButton(RealmsButton realmsButton) {
      this.guiScreenRealmsProxy.handleRealmsButton(realmsButton);
   }

   public static String createString(String text) {
      return I18n.format(text);
   }

   public void noop6() {
   }

   public void noop3() {
   }

   public RealmsAnvilLevelStorageSource getRealmsAnvilLevelStorageSource() {
      return new RealmsAnvilLevelStorageSource(Minecraft.getMinecraft().getSaveLoader());
   }

   public List<String> getList(String text, int i) {
      return this.guiScreenRealmsProxy.getList(text, i);
   }

   public int getInt(String text) {
      return this.guiScreenRealmsProxy.getInt2(text);
   }

   public static RealmsButton createRealmsButton(int i, int i2, String text) {
      return new RealmsButton(0, i, i2, text);
   }

   public void update() {
      this.guiScreenRealmsProxy.update2();
   }

   public void noop4() {
   }

   public void handleString(String text, int i, int i2, int i3) {
      this.guiScreenRealmsProxy.handleString(text, i, i2, i3);
   }

   public void noop12(int i) {
   }

   public void update2() {
      this.guiScreenRealmsProxy.drawDefaultBackground();
   }

   public int visibleLines() {
      return this.guiScreenRealmsProxy.getInt();
   }
}
