package net.minecraft.client.resources;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

public abstract class ResourcePackListEntry implements net.minecraft.client.gui.GuiListExtended.IGuiListEntry {
   protected final GuiScreenResourcePacks resourcePacksGUI;
   private static final ResourceLocation RESOURCE_PACKS_TEXTURE = new ResourceLocation("textures/gui/resource_packs.png");
   private static final IChatComponent INCOMPATIBLE_TITLE = new ChatComponentTranslation("resourcePack.incompatible");
   private static final IChatComponent INCOMPATIBLE_OLD_TEXT = new ChatComponentTranslation("resourcePack.incompatible.old");
   protected final Minecraft mc;
   private static final IChatComponent INCOMPATIBLE_NEW_TEXT = new ChatComponentTranslation("resourcePack.incompatible.new");

   protected boolean canBeSelected() {
      return true;
   }

   public ResourcePackListEntry(GuiScreenResourcePacks resourcePacksGUIIn) {
      this.resourcePacksGUI = resourcePacksGUIIn;
      this.mc = Minecraft.getMinecraft();
   }

   @Override
   public void update() {
   }

   protected boolean canRemoveFromSelected() {
      return this.resourcePacksGUI.hasResourcePackEntry(this);
   }

   protected abstract String getName();

   protected abstract String getDescription();

   @Override
   public boolean mousePressed(int slotIndex, int i, int i2, int i3, int i4, int i5) {
      if (this.canBeSelected() && i4 <= 32) {
         if (this.canAddToSelected()) {
            this.resourcePacksGUI.markChanged();
            int compatibility = this.getCompatibility();
            if (compatibility != 1) {
               String text = I18n.format("resourcePack.incompatible.confirm.title");
               String text2 = I18n.format("resourcePack.incompatible.confirm." + (compatibility > 1 ? "new" : "old"));
               this.mc.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback() {
                  @Override
                  public void confirmClicked(boolean result, int id) {
                     List<ResourcePackListEntry> list = ResourcePackListEntry.this.resourcePacksGUI.getListContaining(ResourcePackListEntry.this);
                     ResourcePackListEntry.this.mc.displayGuiScreen(ResourcePackListEntry.this.resourcePacksGUI);
                     if (result) {
                        list.remove(ResourcePackListEntry.this);
                        ResourcePackListEntry.this.resourcePacksGUI.getSelectedResourcePacks().add(0, ResourcePackListEntry.this);
                     }
                  }
               }, text, text2, 0));
            } else {
               this.resourcePacksGUI.getListContaining(this).remove(this);
               this.resourcePacksGUI.getSelectedResourcePacks().add(0, this);
            }

            return true;
         }

         if (i4 < 16 && this.canRemoveFromSelected()) {
            this.resourcePacksGUI.getListContaining(this).remove(this);
            this.resourcePacksGUI.getAvailableResourcePacks().add(0, this);
            this.resourcePacksGUI.markChanged();
            return true;
         }

         if (i4 > 16 && i5 < 16 && this.canMoveUp()) {
            List<ResourcePackListEntry> list3 = this.resourcePacksGUI.getListContaining(this);
            int i6 = list3.indexOf(this);
            list3.remove(this);
            list3.add(i6 - 1, this);
            this.resourcePacksGUI.markChanged();
            return true;
         }

         if (i4 > 16 && i5 > 16 && this.canMoveDown()) {
            List<ResourcePackListEntry> list = this.resourcePacksGUI.getListContaining(this);
            int i7 = list.indexOf(this);
            list.remove(this);
            list.add(i7 + 1, this);
            this.resourcePacksGUI.markChanged();
            return true;
         }
      }

      return false;
   }

   @Override
   public void noop(int i) {
   }

   protected boolean canMoveDown() {
      List<ResourcePackListEntry> list = this.resourcePacksGUI.getListContaining(this);
      int i = list.indexOf(this);
      return i >= 0 && i < list.size() - 1 && list.get(i + 1).canBeSelected();
   }

   protected boolean canAddToSelected() {
      return !this.resourcePacksGUI.hasResourcePackEntry(this);
   }

   protected abstract int getCompatibility();

   protected boolean canMoveUp() {
      List<ResourcePackListEntry> list = this.resourcePacksGUI.getListContaining(this);
      int i = list.indexOf(this);
      return i > 0 && list.get(i - 1).canBeSelected();
   }

   @Override
   public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
      int compatibility = this.getCompatibility();
      if (compatibility != 1) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         Gui.handleInt(x - 1, y - 1, x + listWidth - 9, y + slotHeight + 1, -8978432);
      }

      this.bindTexture();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
      String name = this.getName();
      String text = this.getDescription();
      if ((this.mc.gameSettings.touchscreen || isSelected) && this.canBeSelected()) {
         this.mc.getTextureManager().bindTexture(RESOURCE_PACKS_TEXTURE);
         Gui.handleInt(x, y, x + 32, y + 32, -1601138544);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         int i = mouseX - x;
         int i2 = mouseY - y;
         if (compatibility < 1) {
            name = INCOMPATIBLE_TITLE.getFormattedText();
            text = INCOMPATIBLE_OLD_TEXT.getFormattedText();
         } else if (compatibility > 1) {
            name = INCOMPATIBLE_TITLE.getFormattedText();
            text = INCOMPATIBLE_NEW_TEXT.getFormattedText();
         }

         if (this.canAddToSelected()) {
            if (i < 32) {
               Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         } else {
            if (this.canRemoveFromSelected()) {
               if (i < 16) {
                  Gui.drawModalRectWithCustomSizedTexture(x, y, 32.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  Gui.drawModalRectWithCustomSizedTexture(x, y, 32.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }

            if (this.canMoveUp()) {
               if (i < 32 && i > 16 && i2 < 16) {
                  Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }

            if (this.canMoveDown()) {
               if (i < 32 && i > 16 && i2 > 16) {
                  Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }
         }
      }

      int intValue = this.mc.fontRendererObj.getInt3(name);
      if (intValue > 157) {
         name = this.mc.fontRendererObj.trimStringToWidth(name, 157 - this.mc.fontRendererObj.getInt3("...")) + "...";
      }

      this.mc.fontRendererObj.drawStringWithShadow(name, x + 32 + 2, y + 1, 16777215);
      List<String> list = this.mc.fontRendererObj.listFormattedStringToWidth(text, 157);

      for (int outerIndex = 0; outerIndex < 2 && outerIndex < list.size(); outerIndex++) {
         this.mc.fontRendererObj.drawStringWithShadow(list.get(outerIndex), x + 32 + 2, y + 12 + 10 * outerIndex, 8421504);
      }
   }

   protected abstract void bindTexture();
}
