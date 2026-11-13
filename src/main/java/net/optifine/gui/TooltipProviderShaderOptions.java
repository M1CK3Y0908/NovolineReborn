package net.optifine.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.src.Config;
import net.optifine.Lang;
import net.optifine.shaders.config.ShaderOption;
import net.optifine.shaders.gui.GuiButtonShaderOption;
import net.optifine.util.StrUtils;

public class TooltipProviderShaderOptions extends TooltipProviderOptions {
   private String[] makeTooltipLines(ShaderOption so, int width) {
      String nameText = so.getNameText();
      String text = Config.normalize(so.getDescriptionText()).trim();
      String[] texts = this.splitDescription(text);
      GameSettings gameSettings = Config.getGameSettings();
      String text2 = null;
      if (!nameText.equals(so.getName()) && gameSettings.advancedItemTooltips) {
         text2 = "§8" + Lang.get("of.general.id") + ": " + so.getName();
      }

      String text3 = null;
      if (so.getPaths() != null && gameSettings.advancedItemTooltips) {
         text3 = "§8" + Lang.get("of.general.from") + ": " + Config.arrayToString(so.getPaths());
      }

      String text4 = null;
      if (so.getValueDefault() != null && gameSettings.advancedItemTooltips) {
         String text5 = so.isEnabled() ? so.getValueText(so.getValueDefault()) : Lang.get("of.general.ambiguous");
         text4 = "§8" + Lang.getDefault() + ": " + text5;
      }

      ArrayList arrayList = new ArrayList();
      arrayList.add(nameText);
      arrayList.addAll(Arrays.asList(texts));
      if (text2 != null) {
         arrayList.add(text2);
      }

      if (text3 != null) {
         arrayList.add(text3);
      }

      if (text4 != null) {
         arrayList.add(text4);
      }

      return this.makeTooltipLines(width, arrayList);
   }

   private String[] makeTooltipLines(int width, List<String> args) {
      FontRenderer fontRendererObj2 = Config.getMinecraft().fontRendererObj;
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < args.size(); i++) {
         String text = args.get(i);
         if (text != null && text.length() > 0) {
            for (String text2 : fontRendererObj2.listFormattedStringToWidth(text, width)) {
               arrayList.add(text2);
            }
         }
      }

      return (String[]) arrayList.toArray(new String[arrayList.size()]);
   }

   @Override
   public String[] getTooltipLines(GuiButton btn, int width) {
      if (!(btn instanceof GuiButtonShaderOption)) {
         return null;
      }

      ShaderOption shaderOption = ((GuiButtonShaderOption)btn).getShaderOption();
      return this.makeTooltipLines(shaderOption, width);
   }

   private String[] splitDescription(String desc) {
      if (desc.length() <= 0) {
         return new String[0];
      }

      desc = StrUtils.removePrefix(desc, "//");
      String[] stringArray = desc.split("\\. ");

      for (int index = 0; index < stringArray.length; index++) {
         stringArray[index] = "- " + stringArray[index].trim();
         stringArray[index] = StrUtils.removeSuffix(stringArray[index], ".");
      }

      return stringArray;
   }
}
