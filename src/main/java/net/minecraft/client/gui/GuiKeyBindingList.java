package net.minecraft.client.gui;

import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.ArrayUtils;

public class GuiKeyBindingList extends GuiListExtended {
   private int maxListLabelWidth = 0;
   private final GuiControls controlsScreen;
   private final Minecraft mc2;
   private final GuiListExtended.IGuiListEntry[] listEntries;

   public GuiKeyBindingList(GuiControls controls, Minecraft mcIn) {
      super(mcIn, controls.width, controls.height, 63, controls.height - 32, 20);
      this.controlsScreen = controls;
      this.mc2 = mcIn;
      KeyBinding[] keyBindings2 = ArrayUtils.clone(mcIn.gameSettings.keyBindings);
      this.listEntries = new GuiListExtended.IGuiListEntry[keyBindings2.length + KeyBinding.getKeybinds().size()];
      Arrays.sort(keyBindings2);
      int i = 0;
      String text = null;

      for (KeyBinding keyBinding : keyBindings2) {
         String keyCategory = keyBinding.getKeyCategory();
         if (!keyCategory.equals(text)) {
            text = keyCategory;
            this.listEntries[i++] = new GuiKeyBindingList.CategoryEntry(keyCategory);
         }

         int intValue = mcIn.fontRendererObj.getInt3(I18n.format(keyBinding.getKeyDescription()));
         if (intValue > this.maxListLabelWidth) {
            this.maxListLabelWidth = intValue;
         }

         this.listEntries[i++] = new GuiKeyBindingList.KeyEntry(keyBinding);
      }
   }

   @Override
   public GuiListExtended.IGuiListEntry getListEntry(int index) {
      return this.listEntries[index];
   }

   @Override
   protected int getScrollBarX() {
      return super.getScrollBarX() + 15;
   }

   @Override
   protected int getSize() {
      return this.listEntries.length;
   }

   @Override
   public int getListWidth() {
      return super.getListWidth() + 32;
   }

   public class CategoryEntry implements GuiListExtended.IGuiListEntry {
      private final String labelText;
      private final int labelWidth;

      @Override
      public void noop(int i) {
      }

      @Override
      public boolean mousePressed(int slotIndex, int i, int i2, int i3, int i4, int i5) {
         return false;
      }

      @Override
      public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
         GuiKeyBindingList.this.mc2
            .fontRendererObj
            .getInt(
               this.labelText,
               GuiKeyBindingList.this.mc2.currentScreen.width / 2 - this.labelWidth / 2,
               y + slotHeight - GuiKeyBindingList.this.mc2.fontRendererObj.FONT_HEIGHT - 1,
               16777215
            );
      }

      @Override
      public void update() {
      }

      public CategoryEntry(String text) {
         this.labelText = I18n.format(text);
         this.labelWidth = GuiKeyBindingList.this.mc2.fontRendererObj.getInt3(this.labelText);
      }
   }

   public class KeyEntry implements GuiListExtended.IGuiListEntry {
      private final KeyBinding keybinding;
      private final String keyDesc;
      private final GuiButton btnChangeKeyBinding;
      private final GuiButton btnReset;

      @Override
      public void noop(int i) {
      }

      @Override
      public void update() {
         this.btnChangeKeyBinding.update();
         this.btnReset.update();
      }

      @Override
      public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
         boolean flag = GuiKeyBindingList.this.controlsScreen.buttonId == this.keybinding;
         GuiKeyBindingList.this.mc2
            .fontRendererObj
            .getInt(
               this.keyDesc,
               x + 90 - GuiKeyBindingList.this.maxListLabelWidth,
               y + slotHeight / 2 - GuiKeyBindingList.this.mc2.fontRendererObj.FONT_HEIGHT / 2,
               16777215
            );
         this.btnReset.xPosition = x + 190;
         this.btnReset.yPosition = y;
         this.btnReset.enabled = this.keybinding.getKeyCode() != this.keybinding.getKeyCodeDefault();
         this.btnReset.drawButton(GuiKeyBindingList.this.mc2, mouseX, mouseY);
         this.btnChangeKeyBinding.xPosition = x + 105;
         this.btnChangeKeyBinding.yPosition = y;
         this.btnChangeKeyBinding.displayString = GameSettings.getKeyDisplayString(this.keybinding.getKeyCode());
         boolean flag2 = false;
         if (this.keybinding.getKeyCode() != 0) {
            for (KeyBinding keyBinding : GuiKeyBindingList.this.mc2.gameSettings.keyBindings) {
               if (keyBinding != this.keybinding && keyBinding.getKeyCode() == this.keybinding.getKeyCode()) {
                  flag2 = true;
                  break;
               }
            }
         }

         if (flag) {
            this.btnChangeKeyBinding.displayString = EnumChatFormatting.WHITE
               + "> "
               + EnumChatFormatting.YELLOW
               + this.btnChangeKeyBinding.displayString
               + EnumChatFormatting.WHITE
               + " <";
         } else if (flag2) {
            this.btnChangeKeyBinding.displayString = EnumChatFormatting.RED + this.btnChangeKeyBinding.displayString;
         }

         this.btnChangeKeyBinding.drawButton(GuiKeyBindingList.this.mc2, mouseX, mouseY);
      }

      private KeyEntry(KeyBinding keyBinding) {
         this.keybinding = keyBinding;
         this.keyDesc = I18n.format(keyBinding.getKeyDescription());
         this.btnChangeKeyBinding = new GuiButton(0, 0, 0, 75, 20, I18n.format(keyBinding.getKeyDescription()));
         this.btnReset = new GuiButton(0, 0, 0, 50, 20, I18n.format("controls.reset"));
      }

      @Override
      public boolean mousePressed(int slotIndex, int i, int i2, int i3, int i4, int i5) {
         if (this.btnChangeKeyBinding.mousePressed(GuiKeyBindingList.this.mc2, i, i2)) {
            GuiKeyBindingList.this.controlsScreen.buttonId = this.keybinding;
            return true;
         } else if (this.btnReset.mousePressed(GuiKeyBindingList.this.mc2, i, i2)) {
            GuiKeyBindingList.this.mc2.gameSettings.setOptionKeyBinding(this.keybinding, this.keybinding.getKeyCodeDefault());
            KeyBinding.resetKeyBindingArrayAndHash();
            return true;
         } else {
            return false;
         }
      }
   }
}
