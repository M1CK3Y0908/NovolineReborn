package wtf.novoline.gui.menu;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import wtf.novoline.enums.WidgetType;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.ModuleSetting;
import wtf.novoline.utils.render.RenderUtils;
import wtf.novoline.utils.render.shader.FloatSupplier;

public class MenuColorWidget extends MenuSettingWidget {
   private float alpha;
   private static final ResourceLocation COLOR_PICKER_TEXTURE = new ResourceLocation("novoline", "images/color_picker.png");
   private float dragX;
   private boolean dragging;
   private float dragY;
   private float brightness;

   public MenuColorWidget(MenuClickGuiScreen menuClickGuiScreen2, ModuleSetting moduleSetting2) {
      super(menuClickGuiScreen2, moduleSetting2);
   }

   @Override
   public void tick() {
   }

   @Override
   public void renderText(FloatSupplier floatSupplier, WidgetType widgetType, float f, boolean flag) {
      if (!flag && widgetType == WidgetType.REGULAR) {
         floatSupplier.drawStringTopLeft(this.moduleSetting.getName() + ":", this.x + 4.0F, this.y + 10.0F, this.menuClickGuiScreen.getClickGuiTheme().getTextColor());
      }
   }

   private void applyColor(int i) {
      float[] fArray = RenderUtils.unpackColor255(i);
      fArray[3] = this.alpha * 255.0F;
      if (this.moduleSetting instanceof ColorSetting colorSetting) {
         colorSetting.setColor(RenderUtils.packColor(fArray));
         colorSetting.interactCallback();
      }
   }

   @Override
   public float getHeight() {
      return 40.0F;
   }

   @Override
   public boolean keyTyped(char c, int i) {
      return true;
   }

   @Override
   public void reset() {
   }

   @Override
   public void handleMouseRelease(float f, float f2, int i) {
      this.dragging = false;
   }

   @Override
   public void render(float f, float f2, float f3) {
      float f4 = this.x + this.getWidth() - 20.0F;
      float f5 = this.y + 20.0F;
      float f6 = 15.0F;
      if (this.dragging) {
         this.dragX = f;
         this.dragY = f2;
         if (this.dragX >= this.x && this.dragX <= this.x + this.getWidth() && this.dragY > this.y && this.dragY <= this.y + this.getHeight()) {
            float f7 = this.dragX - f4;
            float f8 = this.dragY - f5;
            float f9 = (float)Math.sqrt(f7 * f7 + f8 * f8);
            ColorSetting colorSetting = (ColorSetting)this.moduleSetting;
            if (f9 <= f6 + 1.5F) {
               float f10 = Math.min(f9 / f6, 1.0F);
               float f11 = (float)(Math.atan2(f8, f7) / 2.0 / Math.PI) - 0.25F;
               int rgb = Color.getHSBColor(-f11, f10, this.brightness).getRGB();
               this.applyColor(rgb);
            } else if (this.dragX >= this.x + this.getWidth() - 43.0F
               && this.dragX <= this.x + this.getWidth() - 40.0F
               && this.dragY >= this.y + 3.0F
               && this.dragY <= this.y + this.getHeight() - 3.0F) {
               this.brightness = 1.0F + (this.y + 3.0F - this.dragY) / 34.0F;
               float[] fArray = RenderUtils.unpackColor255(colorSetting.getColor());
               float[] fArray2 = Color.RGBtoHSB((int)fArray[0], (int)fArray[1], (int)fArray[2], null);
               int rgb2 = Color.getHSBColor(fArray2[0], fArray2[1], this.brightness).getRGB();
               this.applyColor(rgb2);
            } else if (this.dragX >= this.x + 4.0F
               && this.dragX <= this.x + this.getWidth() - 40.0F - 6.0F
               && this.dragY >= this.y + this.getHeight() - 3.0F - 3.0F
               && this.dragY <= this.y + this.getHeight() - 3.0F) {
               this.alpha = Math.abs((this.x + 4.0F - this.dragX) / 60.0F);
               this.applyColor(colorSetting.getColor());
            }
         }
      }

      ColorSetting colorSetting2 = (ColorSetting)this.moduleSetting;
      float[] fArray3 = RenderUtils.unpackColor255(colorSetting2.getColor());
      float[] fArray4 = Color.RGBtoHSB((int)fArray3[0], (int)fArray3[1], (int)fArray3[2], null);
      this.brightness = fArray4[2];
      float f12 = 34.0F - 34.0F * this.brightness;
      this.alpha = fArray3[3] / 255.0F;
      float f13 = 60.0F * this.alpha;
      float f14 = fArray4[0];
      float f15 = fArray4[1];
      float f16 = f15 * f6;
      double d = (-f14 + 0.25F) * 2.0F * Math.PI;
      float f17 = f4 + (float)(f16 * Math.cos(d));
      float f18 = f5 + (float)(f16 * Math.sin(d));
      int color2 = colorSetting2.getColor();
      float[] fArray5 = RenderUtils.unpackColor255(color2);
      GlStateManager.enableTexture2D();
      GlStateManager.enableBlend();
      Minecraft.getMinecraft().getTextureManager().bindTexture(COLOR_PICKER_TEXTURE);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      RenderUtils.drawTexturedRectSquareTexture(this.x + 4.0F, this.y + 20.0F, 0.0F, 0.0F, 49.0F, 9.0F, 64);
      GlStateManager.disableBlend();
      if (fArray5[3] < 230.0F) {
         RenderUtils.drawRect(this.x + 4.0F, this.y + 20.0F, this.x + 52.0F, this.y + 28.0F, color2);
      } else if (MenuClickGuiScreen.shadersEnabled && ModuleManager.clickGui.glowSettings.isSelected(1)) {
         this.menuClickGuiScreen.bloomRenderer.submit(() -> RenderUtils.drawRect(this.x + 4.0F, this.y + 20.0F, this.x + 52.0F, this.y + 28.0F, color2));
      } else {
         RenderUtils.drawRect(this.x + 4.0F, this.y + 20.0F, this.x + 52.0F, this.y + 28.0F, color2);
      }

      RenderUtils.drawColorWheel(f4, f5, f6);
      RenderUtils.drawBorderedRect(f17 - 1.0F, f18 - 1.0F, f17 + 1.0F, f18 + 1.0F, 0.5F, 0, -1);
      float[] fArray6 = Color.RGBtoHSB((int)fArray5[0], (int)fArray5[1], (int)fArray5[2], null);
      RenderUtils.drawVerticalGradientRect(
         this.x + this.getWidth() - 43.0F,
         this.y + 3.0F,
         this.x + this.getWidth() - 40.0F,
         this.y + this.getHeight() - 3.0F,
         Color.getHSBColor(fArray6[0], fArray6[1], 1.0F).getRGB(),
         -16777216
      );
      RenderUtils.drawBorderedRect(
         this.x + this.getWidth() - 43.0F - 1.0F,
         this.y + 3.0F + f12 - 1.0F,
         this.x + this.getWidth() - 40.0F + 1.0F,
         this.y + 3.0F + f12 + 1.0F,
         0.5F,
         0,
         -1
      );
      RenderUtils.drawRect(
         this.x + 4.0F,
         this.y + this.getHeight() - 3.0F - 2.0F,
         this.x + this.getWidth() - 40.0F - 6.0F,
         this.y + this.getHeight() - 4.0F,
         this.menuClickGuiScreen.getClickGuiTheme().getSecondaryColor(false)
      );
      RenderUtils.drawRect(
         this.x + 4.0F,
         this.y + this.getHeight() - 3.0F - 2.0F,
         this.x + 4.0F + f13,
         this.y + this.getHeight() - 4.0F,
         this.menuClickGuiScreen.getClickGuiTheme().getSecondaryColor(true)
      );
      RenderUtils.drawBorderedRect(
         this.x + 4.0F + f13 - 1.0F,
         this.y + this.getHeight() - 3.0F - 3.0F - 1.0F,
         this.x + 4.0F + f13 + 1.0F,
         this.y + this.getHeight() - 3.0F + 1.0F,
         0.5F,
         0,
         -1
      );
   }

   @Override
   public void handleMouseClick(float f, float f2, int i) {
      if (i == 0) {
         this.dragging = true;
         this.dragX = f;
         this.dragY = f2;
      }
   }
}
