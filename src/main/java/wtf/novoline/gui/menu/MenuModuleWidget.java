package wtf.novoline.gui.menu;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import wtf.novoline.Novoline;
import wtf.novoline.enums.WidgetType;
import wtf.novoline.gui.TextSelection;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.ModuleSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.setting.Setting.FeatureType;
import wtf.novoline.utils.math.InterpolationUtils;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.notification.Notification;
import wtf.novoline.utils.notification.NotificationType;
import wtf.novoline.utils.render.RenderUtils;
import wtf.novoline.utils.render.shader.FloatSupplier;
import wtf.novoline.utils.render.shape.IRectRenderer;

public class MenuModuleWidget implements MenuWidget, ExpandableWidget {
   private boolean awaitingKeyBind;
   private MenuClickGuiScreen menuClickGuiScreen;
   public Module module;
   private float bottomY;
   private float x;
   private float y;
   private boolean expanded;
   private static final Minecraft minecraft = Minecraft.getMinecraft();
   public final List<MenuSettingWidget> settingWidgets;
   private int labelColor;
   private float arrowOffset;
   private static final Novoline novoline = Novoline.getNovoline();

   public MenuModuleWidget(MenuClickGuiScreen menuClickGuiScreen2, Module module2, float f, float f2) {
      this.menuClickGuiScreen = menuClickGuiScreen2;
      this.module = module2;
      this.x = f;
      this.y = f2;
      this.settingWidgets = new ArrayList<>();
      this.labelColor = -2039584;
      Novoline.getNovoline()
         .getSettingManager()
         .getSettings()
         .stream()
         .filter(setting2 -> setting2.getSettingType().equals(FeatureType.FEATURE) && ((ModuleSetting)setting2).getModule().getClass().equals(module2.getClass()))
         .map(setting2 -> (ModuleSetting)setting2)
         .forEach(moduleSetting -> {
            if (moduleSetting instanceof CheckboxSetting) {
               this.settingWidgets.add(new MenuCheckboxWidget(menuClickGuiScreen2, moduleSetting));
            } else if (moduleSetting instanceof NumberSetting) {
               this.settingWidgets.add(new MenuSliderWidget(menuClickGuiScreen2, moduleSetting));
            } else if (moduleSetting instanceof ComboSetting) {
               this.settingWidgets.add(new MenuComboWidget(menuClickGuiScreen2, moduleSetting));
            } else if (moduleSetting instanceof MultiComboSetting) {
               this.settingWidgets.add(new MenuMultiComboWidget(menuClickGuiScreen2, moduleSetting));
            } else if (moduleSetting instanceof ColorSetting) {
               this.settingWidgets.add(new MenuColorWidget(menuClickGuiScreen2, moduleSetting));
            }
         });
      this.layoutSettings();
   }

   private void layoutSettings() {
      float f = this.y + getHeaderHeight();

      for (MenuSettingWidget menuSettingWidget : this.settingWidgets) {
         if (menuSettingWidget.getModuleSetting().shouldShow()) {
            menuSettingWidget.setX(this.x);
            menuSettingWidget.setY(f);
            f += menuSettingWidget.getHeight();
         }
      }

      this.bottomY = f;
   }

   public float getBottomY() {
      return this.bottomY;
   }

   public float getTotalHeight(float f, float f2, float f3) {
      float f4 = 0.0F;
      if (this.expanded) {
         for (MenuSettingWidget menuSettingWidget : this.settingWidgets) {
            if (menuSettingWidget.getModuleSetting().shouldShow()) {
               f4 += menuSettingWidget.getTotalHeight(f, f2, f3);
            }
         }
      }

      return getHeaderHeight() + f4;
   }

   public boolean isExpanded() {
      return this.expanded;
   }

   public void setPosition(float f, float f2) {
      this.x = f;
      this.y = f2;
      this.layoutSettings();
   }

   public float getY() {
      return this.y;
   }

   @Override
   public void render(float f, float f2, float f3) {
      if (this.settingWidgets.size() > 0) {
         this.arrowOffset = SmoothingUtils.approachWithMinimumStep(this.arrowOffset, this.expanded ? 2.5F : -2.5F, 20.0F, 0.01F);
         GlStateManager.enableBlend();
         GlStateManager.disableTexture2D();
         RenderUtils.applyBlendFunc();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 0.8F);
         float f4 = this.x + getWidth() - 4.0F;
         float f5 = this.y + 7.0F;
         GL11.glLineWidth(2.0F);
         GL11.glEnable(2848);
         GL11.glBegin(3);
         GL11.glVertex2f(f4, f5 + this.arrowOffset / 2.0F);
         GL11.glVertex2f(f4 - 2.0F, f5 - this.arrowOffset / 2.0F);
         GL11.glVertex2f(f4 - 4.0F, f5 + this.arrowOffset / 2.0F);
         GL11.glEnd();
         GL11.glDisable(2848);
         GlStateManager.enableTexture2D();
         GlStateManager.disableBlend();
      }

      if (this.expanded) {
         this.layoutSettings();

         for (MenuSettingWidget menuSettingWidget : this.settingWidgets) {
            if (menuSettingWidget.getModuleSetting().shouldShow()) {
               menuSettingWidget.render(f, f2, f3);
            }
         }

         if (!this.settingWidgets.isEmpty()) {
            RenderUtils.drawVerticalGradientRect(
               this.x, this.y + getHeaderHeight(), this.x + getWidth(), this.y + getHeaderHeight() + 4.0F, 1610612736, 3092790
            );
            MenuSettingWidget menuSettingWidget2 = null;

            for (MenuSettingWidget menuSettingWidget3 : this.settingWidgets) {
               if (menuSettingWidget3.getModuleSetting().shouldShow()) {
                  menuSettingWidget2 = menuSettingWidget3;
               }
            }

            if (menuSettingWidget2 != null) {
               RenderUtils.drawVerticalGradientRect(
                  this.x, menuSettingWidget2.getY() + menuSettingWidget2.getHeight() - 4.0F, this.x + getWidth(), menuSettingWidget2.getY() + menuSettingWidget2.getHeight(), 3092790, 1610612736
               );
            }
         }
      }
   }

   public static float getHeaderHeight() {
      return 14.0F;
   }

   @Override
   public void tick() {
   }

   public List<MenuSettingWidget> getSettingWidgets() {
      return this.settingWidgets;
   }

   @Override
   public void handleMouseClick(float f, float f2, int i) {
      if (f > this.x && f < this.x + getWidth() && f2 >= this.y && f2 < this.y + getHeaderHeight()) {
         if (i == 0) {
            this.module.toggle();
         } else if (i == 1) {
            this.expanded = !this.expanded;
         } else if (i == 2) {
            this.menuClickGuiScreen.buttons.stream().flatMap(menuCategoryPanel -> menuCategoryPanel.moduleWidgets.stream()).forEach(menuModuleWidget -> menuModuleWidget.awaitingKeyBind = false);
            this.awaitingKeyBind = true;
         }
      }

      if (this.expanded) {
         Iterator iterator2 = this.settingWidgets.iterator();

         while (iterator2.hasNext()) {
            ((MenuWidget)iterator2.next()).handleMouseClick(f, f2, i);
         }
      }
   }

   public void addRects(IRectRenderer iRectRenderer, float f) {
      TextSelection textSelection = this.menuClickGuiScreen.getTextSelection();
      String text = textSelection.getTextBuffer().getText();
      if (textSelection.isVisible() && text.length() >= 3 && this.module.getName().toLowerCase(Locale.ROOT).contains(text)) {
         Color color2 = new Color(this.menuClickGuiScreen.getClickGuiTheme().getModuleBackgroundColor());
         iRectRenderer.addRect(
            this.x,
            this.y,
            getWidth(),
            getHeaderHeight(),
            RenderUtils.lerpColor(color2.getRGB(), color2.brighter().brighter().getRGB(), (float)InterpolationUtils.oscillate(0.3, 1.0, 750L))
         );
      } else {
         iRectRenderer.addRect(this.x, this.y, getWidth(), getHeaderHeight(), this.menuClickGuiScreen.getClickGuiTheme().getModuleBackgroundColor());
      }

      if (this.expanded) {
         for (MenuSettingWidget menuSettingWidget : this.settingWidgets) {
            if (menuSettingWidget.getModuleSetting().shouldShow()) {
               menuSettingWidget.addRects(iRectRenderer, f);
            }
         }
      }
   }

   public static float getWidth() {
      return MenuCategoryPanel.getWidth();
   }

   @Override
   public boolean keyTyped(char c, int i) {
      if (this.awaitingKeyBind) {
         this.awaitingKeyBind = false;
         boolean flag = false;
         if (i == 1) {
            this.module.setKeyBind(0);
            flag = true;
         } else {
            this.module.setKeyBind(i);
         }

         novoline.getNotificationManager()
            .show(
               new Notification(
                  NotificationType.INFO,
                  "Click GUI",
                  "Feature " + this.module.getName() + " was " + (flag ? "unbound" : "bound to " + Keyboard.getKeyName(i)),
                  3000L
               )
            );
         return false;
      } else {
         return true;
      }
   }

   public float getX() {
      return this.x;
   }

   public void setX(float f) {
      this.x = f;
   }

   @Override
   public void handleMouseRelease(float f, float f2, int i) {
      if (this.expanded) {
         Iterator iterator2 = this.settingWidgets.iterator();

         while (iterator2.hasNext()) {
            ((MenuWidget)iterator2.next()).handleMouseRelease(f, f2, i);
         }
      }
   }

   @Override
   public void reset() {
      Iterator iterator2 = this.settingWidgets.iterator();

      while (iterator2.hasNext()) {
         ((MenuWidget)iterator2.next()).reset();
      }

      this.awaitingKeyBind = false;
   }

   public void setY(float f) {
      this.y = f;
   }

   @Override
   public void renderText(FloatSupplier floatSupplier, WidgetType widgetType, float f, boolean flag) {
      this.labelColor = RenderUtils.smoothBlendColors(
         this.labelColor, this.module.isEnabled() ? this.menuClickGuiScreen.getClickGuiTheme().getSecondaryColor(true) : -2039584, 14.0F
      );
      String text = this.awaitingKeyBind ? ">> Press any key <<" : this.module.getName();
      if (!this.awaitingKeyBind && Keyboard.isKeyDown(29) && this.module.getKeyBind() != 0) {
         text = text + " [" + Keyboard.getKeyName(this.module.getKeyBind()) + "]";
      }

      if (widgetType == WidgetType.FEATURE && this.module.isEnabled() == flag) {
         floatSupplier.drawStringCentered(text, this.x + getWidth() / 2.0F, this.y + getHeaderHeight() / 2.0F + 1.0F, this.labelColor);
      }

      if (this.expanded) {
         for (MenuSettingWidget menuSettingWidget : this.settingWidgets) {
            if (menuSettingWidget.getModuleSetting().shouldShow()) {
               menuSettingWidget.renderText(floatSupplier, widgetType, f, flag);
            }
         }
      }
   }
}
