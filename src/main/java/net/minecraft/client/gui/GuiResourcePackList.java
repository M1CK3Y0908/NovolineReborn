package net.minecraft.client.gui;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.util.EnumChatFormatting;

public abstract class GuiResourcePackList extends GuiListExtended {
   protected final Minecraft mc2;
   protected final List<ResourcePackListEntry> entries;

   public List<ResourcePackListEntry> getList() {
      return this.entries;
   }

   public ResourcePackListEntry getListEntry(int index) {
      return this.getList().get(index);
   }

   @Override
   public int getListWidth() {
      return this.width;
   }

   protected abstract String getListHeader();

   @Override
   protected void drawListHeader(int x, int y, Tessellator tessellator) {
      String text = "" + EnumChatFormatting.UNDERLINE + EnumChatFormatting.BOLD + this.getListHeader();
      this.mc2.fontRendererObj.getInt(text, x + this.width / 2 - this.mc2.fontRendererObj.getInt3(text) / 2, Math.min(this.top + 3, y), 16777215);
   }

   @Override
   protected int getSize() {
      return this.getList().size();
   }

   public GuiResourcePackList(Minecraft minecraft2, int i, List list) {
      super(minecraft2, 200, i, 32, i - 55 + 4, 36);
      this.mc2 = minecraft2;
      this.entries = list;
      this.showListBackground = false;
      this.handleInt2((int)(minecraft2.fontRendererObj.FONT_HEIGHT * 1.5F));
   }

   @Override
   protected int getScrollBarX() {
      return this.right - 6;
   }
}
