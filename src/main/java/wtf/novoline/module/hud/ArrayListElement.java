package wtf.novoline.module.hud;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.MultiComboSetting.Option;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.render.CFontRenderer;
import wtf.novoline.utils.render.RenderUtils;
import wtf.novoline.utils.render.ShadowRenderer;

public class ArrayListElement extends HudElement {
   private float width;
   private ScaledResolution scaledResolution;
   private final ShadowRenderer backgroundShadow = new ShadowRenderer(Novoline.framebufferManager);
   private float maxEntryWidth;
   private final ShadowRenderer textGlowShadow = new ShadowRenderer(Novoline.framebufferManager);
   private float height;

   private boolean hasVisibleSuffix(boolean flag, Module module2) {
      return !flag && !module2.getSuffix().isEmpty();
   }

   @Override
   public float getX() {
      return this.scaledResolution != null ? this.x + this.scaledResolution.getFloat() - this.getWidth() : 0.0F;
   }

   private boolean isFilteredOut(boolean flag, boolean flag2, Module module2) {
      return flag2 && module2.getKeyBind() == 0 || flag && module2.getCategory() == Category.VISUAL || module2.getCategory() == Category.HUD;
   }

   @Override
   public float getHeight() {
      return this.height;
   }

   public ArrayListElement() {
      super("arraylist", 0.0F, 0.0F, true);
   }

   @Override
   public float getWidth() {
      return this.width;
   }

   private float[] measureEntry(Module module2, CFontRenderer cFontRenderer, boolean flag, float f) {
      String name = module2.getName();
      String suffix = module2.getSuffix();
      float stringWidth = cFontRenderer.getStringWidth(name);
      float f2 = stringWidth + f;
      if (flag) {
         f2 += cFontRenderer.getStringWidth(suffix);
      }

      return new float[]{f2, stringWidth};
   }

   @Override
   public float getY() {
      return this.y;
   }

   @Override
   public void draw(ScaledResolution scaledResolution2, float f, boolean flag) {
      if (!Minecraft.getMinecraft().gameSettings.showDebugInfo && ModuleManager.arrayList.isEnabled()) {
         this.scaledResolution = scaledResolution2;
         ArrayList arrayList2 = ModuleManager.arrayList;
         boolean flag2 = this.getX() < scaledResolution2.getScaledWidth_double() / 2.0;
         boolean flag3 = this.getY() > scaledResolution2.getFloat2() / 2.0F;
         boolean enabled = arrayList2.backgroundShadow.isEnabled();
         if (enabled) {
            this.backgroundShadow.render(Minecraft.getMinecraft().getFramebuffer(), scaledResolution2, arrayList2.backgroundColor.getColor(), 15.0F, 5.0F, 1.1F, true);
         }

         int currentElement = arrayList2.colorMode.getCurrentElement();
         List elements = arrayList2.arrayList.getElements();
         boolean selected = ((Option)elements.get(0)).isSelected();
         boolean selected2 = ((Option)elements.get(1)).isSelected();
         boolean selected3 = ((Option)elements.get(2)).isSelected();
         CFontRenderer cFontRenderer = Fonts.rubikRegularSize16;
         List list = flag3 ? ModuleManager.modulesByWidthReversed : ModuleManager.modulesByWidth;
         float currentValue = arrayList2.widthPadding.getCurrentValue();
         float currentValue2 = arrayList2.heightPadding.getCurrentValue();
         float currentValue3 = arrayList2.rightLineWidth.getCurrentValue();
         float f2 = cFontRenderer.getHeight() + currentValue2;
         this.height = 0.0F;
         this.maxEntryWidth = 0.0F;
         this.width = 0.0F;
         long l = (long)(arrayList2.cycleTime.getCurrentValue() * 1000.0F);
         int i = (int)arrayList2.step.getCurrentValue();
         float currentValue4 = arrayList2.fadeMin.getCurrentValue();
         float currentValue5 = arrayList2.fadeMax.getCurrentValue();
         float currentValue6 = arrayList2.saturation.getCurrentValue();
         float currentValue7 = arrayList2.brightness.getCurrentValue();
         int i2 = 0;
         if (flag3) {
            for (Module module2 : ModuleManager.modulesByWidth) {
               if (!this.isFilteredOut(selected2, selected3, module2)) {
                  boolean visibleSuffix = this.hasVisibleSuffix(selected, module2);
                  this.width = this.measureEntry(module2, cFontRenderer, visibleSuffix, currentValue)[0];
                  break;
               }
            }
         }

         for (Module module3 : (Iterable<Module>) list) {
            if (!this.isFilteredOut(selected2, selected3, module3)) {
               String name = module3.getName();
               String suffix = module3.getSuffix();
               boolean visibleSuffix2 = this.hasVisibleSuffix(selected, module3);
               float[] fArray = this.measureEntry(module3, cFontRenderer, visibleSuffix2, currentValue);
               float f3 = fArray[0];
               float f4 = fArray[1];
               if (!flag3 && this.width == 0.0F) {
                  this.width = f3;
               }

               float arrayListOffsetY = module3.getArrayListOffsetY();
               float arrayListOffsetX = module3.getArrayListOffsetX();
               float f5 = flag2 ? this.getX() + arrayListOffsetX - f3 + currentValue3 : this.getX() + this.getWidth() - arrayListOffsetX - currentValue3;
               int color = arrayList2.main.getColor();
               int color2 = arrayList2.status.getColor();
               int color3 = arrayList2.rightLineColor.getColor();
               if (currentElement == 1) {
                  color = color3 = RenderUtils.pulseBrightness(color, currentValue4, currentValue5, i2, i, l);
               } else if (currentElement == 2) {
                  color = color3 = RenderUtils.indexedHsbColor(i2, i, currentValue6, currentValue7, l);
               } else if (currentElement == 3) {
                  color = color3 = RenderUtils.indexedHsbColorMirrored(i2, i, currentValue6, currentValue7, l);
               } else if (currentElement == 4) {
                  color = color3 = RenderUtils.pulseBetweenColors(arrayList2.gradientFirst.getColor(), arrayList2.gradientSecond.getColor(), i2, i, l);
               }

               if (arrayListOffsetX > -2.0F) {
                  if (this.maxEntryWidth < f3) {
                     this.maxEntryWidth = f3;
                  }

                  float f6 = f3;
                  if (arrayList2.background.isEnabled()) {
                     if (enabled) {
                        this.backgroundShadow
                           .submit(
                              () -> RenderUtils.drawRect(
                                 f5, this.getY() + arrayListOffsetY, f5 + f6, this.getY() + arrayListOffsetY + f2, arrayList2.backgroundColor.getColor()
                              )
                           );
                     } else {
                        RenderUtils.drawRect(f5, this.getY() + arrayListOffsetY, f5 + f3, this.getY() + arrayListOffsetY + f2, arrayList2.backgroundColor.getColor());
                     }
                  }

                  if (arrayList2.rightLine.isEnabled()) {
                     float f7 = flag2 ? 0.0F : f3;
                     RenderUtils.drawRect(f5 + f7, this.getY() + arrayListOffsetY, f5 + f7 + currentValue3, this.getY() + arrayListOffsetY + f2, color3);
                  }

                  if (arrayList2.textGlow.isEnabled()) {
                     this.textGlowShadow
                        .submit(
                           () -> RenderUtils.drawRect(
                              f5,
                              this.y + arrayListOffsetY,
                              f5 + f6,
                              this.y + arrayListOffsetY + f2,
                              RenderUtils.withAlphaFraction(arrayList2.main.getColor(), arrayList2.textGlowAlpha.getCurrentValue() / 255.0F)
                           )
                        );
                  }

                  cFontRenderer.drawString(name, f5 + currentValue / 2.0F, this.getY() + arrayListOffsetY + (currentValue2 + cFontRenderer.getHeight() / 2.0F) / 2.0F, color);
                  if (visibleSuffix2) {
                     cFontRenderer.drawString(suffix, f5 + currentValue / 2.0F + f4, this.getY() + arrayListOffsetY + (currentValue2 + cFontRenderer.getHeight() / 2.0F) / 2.0F, color2);
                  }
               }

               module3.setArrayListOffsetY(SmoothingUtils.approachWithMinimumStep(module3.getArrayListOffsetY(), this.height, 10.0F, 0.02F));
               if (module3.isEnabled()) {
                  module3.setArrayListOffsetX(SmoothingUtils.approachWithMinimumStep(module3.getArrayListOffsetX(), f3, 10.0F, 0.02F));
                  this.height = this.height + (cFontRenderer.getHeight() + currentValue2);
                  i2++;
               } else {
                  module3.setArrayListOffsetX(SmoothingUtils.approachWithMinimumStep(module3.getArrayListOffsetX(), -3.0F - currentValue3, 10.0F, 0.02F));
               }
            }
         }

         if (arrayList2.textGlow.isEnabled()) {
            this.textGlowShadow
               .render(
                  Minecraft.getMinecraft().getFramebuffer(),
                  scaledResolution2,
                  RenderUtils.withAlphaFraction(arrayList2.main.getColor(), arrayList2.textGlowAlpha.getCurrentValue() / 255.0F),
                  arrayList2.textGlowRadius.getCurrentValue(),
                  arrayList2.textGlowSigma.getCurrentValue(),
                  1.0F,
                  false
               );
         }
      }
   }
}
