package wtf.novoline.gui.clickgui;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.module.Module;
import wtf.novoline.module.Module.PressType;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.utils.render.RenderUtils;

public class KeyBindPopup implements ClickGuiComponent {
   private final float x;
   private boolean listening;
   private final float y;
   private final ModulePanel modulePanel;
   private final ComboSettingRow comboSettingRow;
   private static final Map<Integer, String> keyNames = new HashMap<>();

   @Override
   public boolean handleMouseClick(float f, float f2, int i) {
      if (!this.comboSettingRow.handleMouseClick(f, f2, i)) {
         return false;
      } else {
         float f3 = clickGuiScreen.getX() + this.x;
         float f4 = clickGuiScreen.getY() + this.y;
         if (!(f >= f3 + 53.0F) || !(f <= f3 + 104.0F) || !(f2 >= f4 + 20.0F) || !(f2 <= f4 + 30.0F)) {
            clickGuiScreen.getCategoryPanel().setKeyBindPopup(null);
            return false;
         } else if (i == 0) {
            this.listening = true;
            return false;
         } else if (i == 1) {
            this.modulePanel.getModule().setKeyBind(0);
            return false;
         } else {
            return true;
         }
      }
   }

   public boolean isListening() {
      return this.listening;
   }

   public void render(float f, float f2, float f3) {
      float f4 = clickGuiScreen.getX() + this.x;
      float f5 = clickGuiScreen.getY() + this.y;
      RenderUtils.drawShadow(f4, f5, f4 + 110.0F, f5 + 50.0F, 5.0F, -16509915);
      RenderUtils.drawRect(f4, f5, f4 + 110.0F, f5 + 50.0F, -16577254);
      Fonts.museoSans700Size14.drawStringWithShadow("Bind", f4 + 5.0F, f5 + 5.0F, -1);
      RenderUtils.drawRect(f4 + 2.0F, f5 + 12.0F, f4 + 108.0F, f5 + 13.0F, -16115934);
      Fonts.museoSans700Size14.drawStringWithShadow("Key", f4 + 5.0F, f5 + 23.0F, -6509895);
      String text = this.listening ? "..." : keyNames.computeIfAbsent(this.modulePanel.getModule().getKeyBind(), integer -> {
         if (integer < 0) {
            int i = Math.abs(integer + 1);
            if (i == 0) {
               return "LMB";
            } else if (i == 1) {
               return "RMB";
            } else {
               return i == 2 ? "MMB" : "Mouse " + i;
            }
         } else {
            String keyName = Keyboard.getKeyName(integer);
            return StringUtils.capitalize(keyName.toLowerCase(Locale.ROOT));
         }
      });
      RenderUtils.drawBorderedRect(f4 + 53.0F, f5 + 20.0F, f4 + 104.0F, f5 + 30.0F, 0.5F, -16314860, -15787215);
      Fonts.museoSans700Size14.drawStringWithShadow(text, f4 + 55.0F, f5 + 23.5F, -6509895);
      this.comboSettingRow.draw(f, f2, f3);
   }

   public KeyBindPopup(ModulePanel modulePanel2, float f, float f2) {
      this.x = f;
      this.y = f2;
      this.modulePanel = modulePanel2;
      final Module module2 = modulePanel2.getModule();
      this.comboSettingRow = new ComboSettingRow(
         new ComboSetting(module2, "Press type", module2.getPressType() == PressType.PRESS ? 0 : 1, new String[]{"Toggle", "Hold"}) {
            @Override
            public void interactCallback() {
               int currentElement = this.getCurrentElement();
               module2.setPressType(PressType.values()[currentElement]);
            }
         }, 107.0F
      );
      this.comboSettingRow.detachFromScroll();
      this.comboSettingRow.setX(f);
      this.comboSettingRow.setY(f2 + 32.0F);
   }

   public static void noop() {
   }

   @Override
   public void handleKeyboardPress(char c, int i) {
      if (this.listening) {
         if (i != 1) {
            this.modulePanel.getModule().setKeyBind(i);
         }

         this.listening = false;
      }
   }

   @Override
   public void handleMouseRelease(float f, float f2, int i) {
   }
}
