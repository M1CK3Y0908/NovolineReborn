package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class GuiStreamIndicator {
   private static final ResourceLocation resourceLocation = new ResourceLocation("textures/gui/stream_indicator.png");
   private float floatValue = 1.0F;
   private int intValue = 1;
   private final Minecraft minecraft;

   public GuiStreamIndicator(Minecraft minecraft2) {
      this.minecraft = minecraft2;
   }

   private void handleInt2(int i, int i2, int i3) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 0.65F + 0.35000002F * this.floatValue);
      this.minecraft.getTextureManager().bindTexture(resourceLocation);
      float f = 150.0F;
      float f2 = 0.0F;
      float f3 = i2 * 0.015625F;
      float f4 = 1.0F;
      float f5 = (i2 + 16) * 0.015625F;
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(i - 16 - i3, 26, f).tex(f2, f5).endVertex();
      worldRenderer.pos(i - i3, 26, f).tex(f4, f5).endVertex();
      worldRenderer.pos(i - i3, 10, f).tex(f4, f3).endVertex();
      worldRenderer.pos(i - 16 - i3, 10, f).tex(f2, f3).endVertex();
      instance.draw();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void update() {
      if (this.minecraft.getIStream().isActive()) {
         this.floatValue = this.floatValue + 0.025F * this.intValue;
         if (this.floatValue < 0.0F) {
            this.intValue *= -1;
            this.floatValue = 0.0F;
         } else if (this.floatValue > 1.0F) {
            this.intValue *= -1;
            this.floatValue = 1.0F;
         }
      } else {
         this.floatValue = 1.0F;
         this.intValue = 1;
      }
   }

   public void handleInt(int i) {
      if (this.minecraft.getIStream().isActive()) {
         GlStateManager.enableBlend();
         int intValue = this.minecraft.getIStream().getInt();
         if (intValue > 0) {
            String text = intValue + "";
            int intValue2 = this.minecraft.fontRendererObj.getInt3(text);
            int i2 = i - intValue2 - 1;
            int i3 = 30 + this.minecraft.fontRendererObj.FONT_HEIGHT - 1;
            GlStateManager.disableTexture2D();
            Tessellator instance = Tessellator.getInstance();
            WorldRenderer worldRenderer = instance.getWorldRenderer();
            GlStateManager.color(0.0F, 0.0F, 0.0F, (0.65F + 0.35000002F * this.floatValue) / 2.0F);
            worldRenderer.begin(7, DefaultVertexFormats.POSITION);
            worldRenderer.pos(i2, i3, 0.0).endVertex();
            worldRenderer.pos(i, i3, 0.0).endVertex();
            worldRenderer.pos(i, 29, 0.0).endVertex();
            worldRenderer.pos(i2, 29, 0.0).endVertex();
            instance.draw();
            GlStateManager.enableTexture2D();
            this.minecraft.fontRendererObj.getInt(text, i - intValue2, 30, 16777215);
         }

         this.handleInt2(i, this.getInt2(), 0);
         this.handleInt2(i, this.getInt(), 17);
      }
   }

   private int getInt() {
      return this.minecraft.getIStream().isActive2() ? 48 : 32;
   }

   private int getInt2() {
      return this.minecraft.getIStream().isActive5() ? 16 : 0;
   }
}
