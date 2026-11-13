package wtf.novoline.gui.clickgui;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import net.minecraft.util.ResourceLocation;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.ModuleSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.setting.Setting;
import wtf.novoline.setting.Setting.FeatureType;
import wtf.novoline.utils.render.CFontRenderer;
import wtf.novoline.utils.render.RenderUtils;

public class ModulePanel extends CopyOnWriteArrayList<SettingRow> implements ClickGuiComponent {
   private float settingsHeight;
   private static final ResourceLocation BIND_ICON = new ResourceLocation("novoline", "images/clickgui/neverlose/bind_rmb.png");
   private final Module module;
   private String title;
   private float x;
   private float y;
   private float width;

   @Override
   public void handleMouseRelease(float f, float f2, int i) {
      if (!this.isEmpty()) {
         this.forEach(settingRow -> settingRow.handleMouseRelease(f, f2, i));
      }
   }

   public float getWidth() {
      return this.width;
   }

   private boolean containsSetting(List<SettingRow> list, SettingRow settingRow2) {
      return list.stream().anyMatch(settingRow -> this.haveSameSignature(settingRow, settingRow2));
   }

   @Override
   public void handleKeyboardPress(char c, int i) {
      if (!this.isEmpty()) {
         this.forEach(settingRow -> settingRow.handleKeyboardPress(c, i));
      }
   }

   public void setY(float f) {
      this.y = f;
   }

   public void draw(float f, float f2, float f3) {
      float f4 = clickGuiScreen.getX() + this.x;
      float f5 = clickGuiScreen.getY() - clickGuiScreen.getScrollOffset() + this.y;
      RenderUtils.drawRect(f4, f5, f4 + this.width, f5 + getRowHeight(), -16577254);
      RenderUtils.drawLine(f4 + 4.0F, f5 + getRowHeight(), f4 + this.width - 4.0F, f5 + getRowHeight(), 1342203824);
      CFontRenderer cFontRenderer = Fonts.museoSans700Size14;
      String text = this.module != null ? this.module.getName() : this.title;
      float f6 = f4 + 5.0F;
      float f7 = f5 + getRowHeight() / 2.0F - Fonts.museoSans700Size14.getHeight() / 2.0F + 1.0F;
      float f8 = f6;
      cFontRenderer.drawString(text, f8, f7, -1);
      ComboSettingRow comboSettingRow = clickGuiScreen.getCategoryPanel().getComboSettingRow();
      if (!this.isEmpty()) {
         for (SettingRow settingRow2 : this) {
            if ((comboSettingRow == null || !comboSettingRow.equals(settingRow2))
               && settingRow2.getY() - clickGuiScreen.getScrollOffset() > 0.0F
               && settingRow2.getY() - clickGuiScreen.getScrollOffset() < ClickGuiScreen.getHeight()) {
               settingRow2.draw(f, f2, f3);
            }
         }
      }

      RenderUtils.drawColoredTexture(f4 + this.width - 12.0F, f5 + 5.0F, 8.0F, 8.0F, RenderUtils.withAlpha(-1, 100), BIND_ICON);
      Fonts.museoSans500Size11.drawString("Bind", f4 + this.width - 25.0F, f5 + 8.0F, RenderUtils.withAlpha(-1, 100));
   }

   public void refreshSettings() {
      if (this.module != null) {
         List list = Novoline.getNovoline()
            .getSettingManager()
            .getSettings()
            .stream()
            .filter(setting2 -> setting2.getSettingType().equals(FeatureType.FEATURE))
            .filter(setting2 -> {
               Setting setting3 = setting2;
               return setting3 instanceof ModuleSetting && ((ModuleSetting)setting3).getModule().equals(this.module);
            })
            .map(ModuleSetting.class::cast)
            .filter(ModuleSetting::shouldShow)
            .map(moduleSetting -> {
               if (moduleSetting instanceof CheckboxSetting) {
                  return new CheckboxSettingRow(moduleSetting, this.width);
               } else if (moduleSetting instanceof NumberSetting) {
                  return new SliderSettingRow(moduleSetting, this.width);
               } else if (moduleSetting instanceof ComboSetting || moduleSetting instanceof MultiComboSetting) {
                  return new ComboSettingRow(moduleSetting, this.width);
               } else {
                  return moduleSetting instanceof ColorSetting ? new ColorSettingRow(moduleSetting, this.width) : null;
               }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
         CheckboxSettingRow checkboxSettingRow = new CheckboxSettingRow(new CheckboxSetting(this.module, "Enable", this.module.isEnabled()) {
            @Override
            public void interactCallback() {
               this.getModule().toggle();
            }
         }, this.width);
         list.add(0, checkboxSettingRow);

         for (SettingRow settingRow2 : (Iterable<SettingRow>) list) {
            if (!this.containsSetting(this, settingRow2)) {
               this.add(settingRow2);
            }
         }

         this.removeIf(settingRow -> !this.containsSetting(list, settingRow));

         for (int i = 0; i < list.size(); i++) {
            SettingRow settingRow3 = this.get(i);
            SettingRow settingRow4 = (SettingRow)list.get(i);
            if (!this.haveSameSignature(settingRow3, settingRow4)) {
               Collections.swap(this, i, this.indexOfSetting(this, settingRow4));
            }
         }
      }
   }

   public void layoutRows() {
      this.settingsHeight = 0.0F;

      for (SettingRow settingRow2 : this) {
         settingRow2.setX(this.x);
         settingRow2.setY(this.y + getRowHeight() + this.settingsHeight);
         settingRow2.setWidth(this.width);
         this.settingsHeight = this.settingsHeight + getRowHeight();
      }
   }

   public String getTitle() {
      return this.title;
   }

   @Override
   public boolean handleMouseClick(float f, float f2, int i) {
      float f3 = clickGuiScreen.getX() + this.x;
      float f4 = clickGuiScreen.getY() - clickGuiScreen.getScrollOffset() + this.y;
      if ((i == 1 || i == 0)
         && f >= f3 + this.getWidth() - 30.0F
         && f <= f3 + this.getWidth()
         && f2 >= f4
         && f2 <= f4 + getRowHeight()) {
         float f5 = f2 - clickGuiScreen.getY();
         if (f5 + 50.0F > ClickGuiScreen.getHeight()) {
            f5 -= 50.0F;
         }

         clickGuiScreen.getCategoryPanel().setKeyBindPopup(new KeyBindPopup(this, f - clickGuiScreen.getX() - 110.0F, f5));
         return false;
      } else {
         if (clickGuiScreen.getCategoryPanel().getKeyBindPopup() != null
            && !clickGuiScreen.getCategoryPanel().getKeyBindPopup().handleMouseClick(f, f2, i)) {
            return false;
         }

         if (!this.isEmpty()) {
            for (SettingRow settingRow2 : this) {
               SettingRow settingRow3 = settingRow2;
               if ((!(settingRow3 instanceof ColorSettingRow) || !((ColorSettingRow)settingRow3).isPickerOpen()) && !settingRow2.handleMouseClick(f, f2, i)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public void setX(float f) {
      this.x = f;
   }

   public void setWidth(float f) {
      this.width = f;
   }

   public static float getRowHeight() {
      return 17.0F;
   }

   public ModulePanel(Module module2, float f, float f2, float f3) {
      this.module = module2;
      this.x = f;
      this.y = f2;
      this.width = f3;
      this.refreshSettings();
   }

   public void setSettingsHeight(float f) {
      this.settingsHeight = f;
   }

   private boolean haveSameSignature(SettingRow settingRow2, SettingRow settingRow3) {
      return settingRow2.getModuleSetting().getSignature().equals(settingRow3.getModuleSetting().getSignature());
   }

   public float getSettingsHeight() {
      return this.settingsHeight;
   }

   public Module getModule() {
      return this.module;
   }

   public float getX() {
      return this.x;
   }

   public void resetRowAnimations() {
      if (!this.isEmpty()) {
         this.forEach(SettingRow::resetAnimation);
      }
   }

   public float getY() {
      return this.y;
   }

   public ModulePanel(String text, List<SettingRow> list, float f, float f2, float f3, float f4) {
      this.title = text;
      this.module = null;
      this.x = f2;
      this.y = f3;
      this.width = f4;
      this.settingsHeight = f;
      this.addAll(list);
   }

   private int indexOfSetting(List list, SettingRow settingRow2) {
      for (int i = 0; i < list.size(); i++) {
         if (this.haveSameSignature((SettingRow)list.get(i), settingRow2)) {
            return i;
         }
      }

      return -1;
   }

   public float getRowsHeight() {
      return getRowHeight() * this.size();
   }
}
