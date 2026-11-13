package net.minecraft.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.realms.RealmsSimpleScrolledSelectionList;
import net.minecraft.util.MathHelper;
import net.minecraft.client.Minecraft;

public class GuiSimpleScrolledSelectionListProxy extends GuiSlot {
   public GuiSimpleScrolledSelectionListProxy() {
      super((Minecraft) null, 0, 0, 0, 0, 0);
   }

   private RealmsSimpleScrolledSelectionList realmsSimpleScrolledSelectionList;

   public int getMouseX() {
      return super.mouseX;
   }

   @Override
   protected int getContentHeight() {
      return this.realmsSimpleScrolledSelectionList.getInt5();
   }

   @Override
   protected int getSize() {
      return this.realmsSimpleScrolledSelectionList.getInt6();
   }

   @Override
   protected boolean isSelected(int i) {
      return this.realmsSimpleScrolledSelectionList.alwaysFalse();
   }

   public int getMouseY() {
      return super.mouseY;
   }

   @Override
   protected int getScrollBarX() {
      return this.realmsSimpleScrolledSelectionList.getInt3();
   }

   public int getWidth() {
      return super.width;
   }

   @Override
   public void handleMouseInput() {
      super.handleMouseInput();
   }

   @Override
   protected void elementClicked(int i, boolean flag, int i2, int i3) {
      this.realmsSimpleScrolledSelectionList.noop();
   }

   @Override
   protected void drawSlot(int i, int i2, int i3, int i4, int i5, int i6) {
      this.realmsSimpleScrolledSelectionList.update();
   }

   @Override
   protected void drawBackground() {
      this.realmsSimpleScrolledSelectionList.noop2();
   }

   @Override
   public void drawScreen(int i, int i2, float f) {
      if (this.visible) {
         this.mouseX = i;
         this.mouseY = i2;
         this.drawBackground();
         int scrollBarX = this.getScrollBarX();
         int i3 = scrollBarX + 6;
         this.bindAmountScrolled();
         GlStateManager.disableLighting();
         GlStateManager.disableFog();
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         int i4 = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
         int i5 = this.top + 4 - (int)this.amountScrolled;
         if (this.hasListHeader) {
            this.drawListHeader(i4, i5, instance);
         }

         this.drawSelectionBox(i4, i5, i, i2);
         GlStateManager.disableDepth();
         this.handleInt3(0, this.top);
         this.handleInt3(this.bottom, this.height);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
         GlStateManager.disableAlpha();
         GlStateManager.shadeModel(7425);
         GlStateManager.disableTexture2D();
         int maxScroll = this.getMaxScroll();
         if (maxScroll > 0) {
            int i6 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
            i6 = MathHelper.clamp_int(i6, 32, this.bottom - this.top - 8);
            int i7 = (int)this.amountScrolled * (this.bottom - this.top - i6) / maxScroll + this.top;
            if (i7 < this.top) {
               i7 = this.top;
            }

            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldRenderer.pos(scrollBarX, this.bottom, 0.0).tex(0.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(i3, this.bottom, 0.0).tex(1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(i3, this.top, 0.0).tex(1.0, 0.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(scrollBarX, this.top, 0.0).tex(0.0, 0.0).color(0, 0, 0, 255).endVertex();
            instance.draw();
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldRenderer.pos(scrollBarX, i7 + i6, 0.0).tex(0.0, 1.0).color(128, 128, 128, 255).endVertex();
            worldRenderer.pos(i3, i7 + i6, 0.0).tex(1.0, 1.0).color(128, 128, 128, 255).endVertex();
            worldRenderer.pos(i3, i7, 0.0).tex(1.0, 0.0).color(128, 128, 128, 255).endVertex();
            worldRenderer.pos(scrollBarX, i7, 0.0).tex(0.0, 0.0).color(128, 128, 128, 255).endVertex();
            instance.draw();
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldRenderer.pos(scrollBarX, i7 + i6 - 1, 0.0).tex(0.0, 1.0).color(192, 192, 192, 255).endVertex();
            worldRenderer.pos(i3 - 1, i7 + i6 - 1, 0.0).tex(1.0, 1.0).color(192, 192, 192, 255).endVertex();
            worldRenderer.pos(i3 - 1, i7, 0.0).tex(1.0, 0.0).color(192, 192, 192, 255).endVertex();
            worldRenderer.pos(scrollBarX, i7, 0.0).tex(0.0, 0.0).color(192, 192, 192, 255).endVertex();
            instance.draw();
         }

         this.drawListDecorations(i, i2);
         GlStateManager.enableTexture2D();
         GlStateManager.shadeModel(7424);
         GlStateManager.enableAlpha();
         GlStateManager.disableBlend();
      }
   }
}
