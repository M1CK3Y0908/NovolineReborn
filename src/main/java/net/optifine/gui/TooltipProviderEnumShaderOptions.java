package net.optifine.gui;

import java.awt.Rectangle;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.optifine.shaders.config.EnumShaderOption;
import net.optifine.shaders.gui.GuiButtonDownloadShaders;
import net.optifine.shaders.gui.GuiButtonEnumShaderOption;

public class TooltipProviderEnumShaderOptions implements TooltipProvider {
   @Override
   public boolean isRenderBorder() {
      return true;
   }

   private String[] getTooltipLines(EnumShaderOption option) {
      return TooltipProviderOptions.getTooltipLines(option.getResourceKey());
   }

   @Override
   public Rectangle getRectangle(GuiScreen guiScreen, int i) {
      int i2 = guiScreen.width - 450;
      int i3 = 35;
      if (i2 < 10) {
         i2 = 10;
      }

      if (i <= 129) {
         i3 += 100;
      }

      int i4 = i2 + 150 + 150;
      int i5 = i3 + 84 + 10;
      return new Rectangle(i2, i3, i4 - i2, i5 - i3);
   }

   @Override
   public String[] getTooltipLines(GuiButton btn, int width) {
      if (btn instanceof GuiButtonDownloadShaders) {
         return TooltipProviderOptions.getTooltipLines("of.options.shaders.DOWNLOAD");
      }

      if (!(btn instanceof GuiButtonEnumShaderOption)) {
         return null;
      }

      EnumShaderOption enumShaderOption = ((GuiButtonEnumShaderOption)btn).getEnumShaderOption();
      return this.getTooltipLines(enumShaderOption);
   }
}
