package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import wtf.novoline.utils.render.RenderUtils;

public class GuiLabel extends Gui {
   protected int intValue = 200;
   protected int intValue2 = 20;
   public boolean visible = true;
   private FontRenderer fontRenderer;
   public int intValue3;
   public int intValue4;
   public int intValue5;
   private List<String> lines;
   private boolean centered;
   private boolean labelBgEnabled;
   private int intValue6;
   private int intValue7;
   private int intValue8;
   private int intValue9;
   private int intValue10;

   public void addLine(String text) {
      this.lines.add(I18n.format(text));
   }

   public void handleMinecraft2(Minecraft minecraft2) {
      if (this.visible) {
         GlStateManager.enableBlend();
         RenderUtils.applyBlendFunc();
         this.handleMinecraft(minecraft2);
         int i = this.intValue5 + this.intValue2 / 2 + this.intValue10 / 2 - this.lines.size() * 10 / 2;

         for (int i2 = 0; i2 < this.lines.size(); i2++) {
            if (this.centered) {
               this.drawCenteredString(this.fontRenderer, this.lines.get(i2), this.intValue4 + this.intValue / 2, i + i2 * 10, this.intValue6);
            } else {
               this.drawString(this.fontRenderer, this.lines.get(i2), this.intValue4, i + i2 * 10, this.intValue6);
            }
         }
      }
   }

   public GuiLabel(FontRenderer fontRenderer2, int i, int i2, int i3) {
      this.fontRenderer = fontRenderer2;
      this.intValue3 = i;
      this.intValue4 = i2;
      this.intValue5 = 0;
      this.intValue = i3;
      this.intValue2 = 20;
      this.lines = Lists.newArrayList();
      this.centered = false;
      this.labelBgEnabled = false;
      this.intValue6 = -1;
      this.intValue7 = -1;
      this.intValue8 = -1;
      this.intValue9 = -1;
      this.intValue10 = 0;
   }

   public GuiLabel setCentered() {
      this.centered = true;
      return this;
   }

   protected void handleMinecraft(Minecraft minecraft2) {
      if (this.labelBgEnabled) {
         int i = this.intValue + this.intValue10 * 2;
         int i2 = this.intValue2 + this.intValue10 * 2;
         int i3 = this.intValue4 - this.intValue10;
         int i4 = this.intValue5 - this.intValue10;
         handleInt(i3, i4, i3 + i, i4 + i2, this.intValue7);
         this.drawHorizontalLine(i3, i3 + i, i4, this.intValue8);
         this.drawHorizontalLine(i3, i3 + i, i4 + i2, this.intValue9);
         this.drawVerticalLine(i3, i4, i4 + i2, this.intValue8);
         this.drawVerticalLine(i3 + i, i4, i4 + i2, this.intValue9);
      }
   }
}
