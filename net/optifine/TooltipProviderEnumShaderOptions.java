package net.optifine;

import java.awt.Rectangle;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.shadersmod.client.EnumShaderOption;
import net.shadersmod.client.GuiButtonEnumShaderOption;

public class TooltipProviderEnumShaderOptions implements TooltipProvider {
    @Override
    public Rectangle getTooltipBounds(GuiScreen guiScreen, int x, int y) {
        int i = guiScreen.width - 450;
        int j = 35;
        if (i < 10) {
            i = 10;
        }

        if (y <= j + 94) {
            j += 100;
        }

        int k = i + 150 + 150;
        int l = j + 84 + 10;
        return new Rectangle(i, j, k - i, l - j);
    }

    @Override
    public boolean isRenderBorder() {
        return true;
    }

    @Override
    public String[] getTooltipLines(GuiButton btn, int width) {
        if (!(btn instanceof GuiButtonEnumShaderOption)) {
            return null;
        }

        GuiButtonEnumShaderOption guibuttonenumshaderoption = (GuiButtonEnumShaderOption)btn;
        EnumShaderOption enumshaderoption = guibuttonenumshaderoption.getEnumShaderOption();
        return this.getTooltipLines(enumshaderoption);
    }

    private String[] getTooltipLines(EnumShaderOption option) {
        return TooltipProviderOptions.getTooltipLines(option.getResourceKey());
    }
}
