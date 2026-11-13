package net.optifine.gui;

import java.awt.Rectangle;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.optifine.Lang;

public class TooltipProviderOptions implements TooltipProvider {
   @Override
   public String[] getTooltipLines(GuiButton btn, int width) {
      return !(btn instanceof IOptionControl) ? null : getTooltipLines(((IOptionControl)btn).getOption().getEnumString());
   }

   @Override
   public boolean isRenderBorder() {
      return false;
   }

   public static String[] getTooltipLines(String key) {
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < 10; i++) {
         String text = Lang.get(key + ".tooltip." + (i + 1), (String)null);
         if (text == null) {
            break;
         }

         arrayList.add(text);
      }

      return arrayList.size() <= 0 ? null : (String[]) arrayList.toArray(new String[arrayList.size()]);
   }

   @Override
   public Rectangle getRectangle(GuiScreen guiScreen, int i) {
      int i2 = guiScreen.width / 2 - 150;
      int i3 = guiScreen.height / 6 - 7;
      if (i <= i3 + 98) {
         i3 += 105;
      }

      int i4 = i2 + 150 + 150;
      int i5 = i3 + 84 + 10;
      return new Rectangle(i2, i3, i4 - i2, i5 - i3);
   }
}
