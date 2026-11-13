package net.minecraft.client.gui;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiResourcePackSelected extends GuiResourcePackList {
   @Override
   protected String getListHeader() {
      return I18n.format("resourcePack.selected.title");
   }

   public GuiResourcePackSelected(Minecraft minecraft2, int i, List list) {
      super(minecraft2, i, list);
   }
}
