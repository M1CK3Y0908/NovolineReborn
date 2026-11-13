package net.minecraft.client.gui;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlider.Contract;
import net.minecraft.util.IntHashMap;

public class GuiPageButtonList extends GuiListExtended {
   private final List<GuiPageButtonList.GuiEntry> entries = Lists.newArrayList();
   private int page;
   private final IntHashMap<Gui> componentMap = new IntHashMap<>();
   private Gui activeComponent;
   private final List<GuiTextField> editBoxes = Lists.newArrayList();
   private GuiPageButtonList.GuiResponder responder;
   private final GuiPageButtonList.GuiListEntry[][] pages;

   public void setButtonsEnabled(boolean enabled) {
      for (GuiPageButtonList.GuiEntry guiEntry : this.entries) {
         if (guiEntry.leftComponent instanceof GuiButton) {
            ((GuiButton)guiEntry.leftComponent).enabled = enabled;
         }

         if (guiEntry.rightComponent instanceof GuiButton) {
            ((GuiButton)guiEntry.rightComponent).enabled = enabled;
         }
      }
   }

   @Override
   public int getSize() {
      return this.entries.size();
   }

   private GuiLabel getGuiLabel(int i, GuiPageButtonList.GuiLabelEntry guiLabelEntry, boolean flag) {
      GuiLabel guiLabel;
      if (flag) {
         guiLabel = new GuiLabel(this.mc.fontRendererObj, guiLabelEntry.getId(), i, this.width - i * 2);
      } else {
         guiLabel = new GuiLabel(this.mc.fontRendererObj, guiLabelEntry.getId(), i, 150);
      }

      guiLabel.visible = guiLabelEntry.isVisible();
      guiLabel.addLine(guiLabelEntry.getName());
      guiLabel.setCentered();
      return guiLabel;
   }

   @Override
   public int getListWidth() {
      return 400;
   }

   @Override
   public boolean mouseClicked(int mouseX, int mouseY, int mouseEvent) {
      boolean conditionMet = super.mouseClicked(mouseX, mouseY, mouseEvent);
      int slotIndexFromScreenCoords = this.getSlotIndexFromScreenCoords(mouseX, mouseY);
      if (slotIndexFromScreenCoords >= 0) {
         GuiPageButtonList.GuiEntry guipagebuttonlist$guientry = this.getListEntry(slotIndexFromScreenCoords);
         if (this.activeComponent != guipagebuttonlist$guientry.focusedComponent
            && this.activeComponent != null
            && this.activeComponent instanceof GuiTextField) {
            ((GuiTextField)this.activeComponent).setFocused(false);
         }

         this.activeComponent = guipagebuttonlist$guientry.focusedComponent;
      }

      return conditionMet;
   }

   private void setComponentVisible(Gui component, boolean visible) {
      if (component instanceof GuiButton) {
         ((GuiButton)component).visible = visible;
      } else if (component instanceof GuiTextField) {
         ((GuiTextField)component).setVisible(visible);
      } else if (component instanceof GuiLabel) {
         ((GuiLabel)component).visible = visible;
      }
   }

   private void createAllComponents() {
      for (GuiPageButtonList.GuiListEntry[] guiListEntrys : this.pages) {
         for (int i = 0; i < guiListEntrys.length; i += 2) {
            GuiPageButtonList.GuiListEntry guiListEntry = guiListEntrys[i];
            GuiPageButtonList.GuiListEntry guiListEntry2 = i < guiListEntrys.length - 1 ? guiListEntrys[i + 1] : null;
            boolean flag = guiListEntry2 == null;
            Gui component2 = this.createComponent(guiListEntry, 0, flag);
            flag = guiListEntry == null;
            Gui component3 = this.createComponent(guiListEntry2, 160, flag);
            GuiPageButtonList.GuiEntry guiEntry = new GuiPageButtonList.GuiEntry(component2, component3);
            this.entries.add(guiEntry);
            if (guiListEntry != null && component2 != null) {
               this.componentMap.addKey(guiListEntry.getId(), component2);
               if (component2 instanceof GuiTextField) {
                  this.editBoxes.add((GuiTextField)component2);
               }
            }

            if (guiListEntry2 != null && component3 != null) {
               this.componentMap.addKey(guiListEntry2.getId(), component3);
               if (component3 instanceof GuiTextField) {
                  this.editBoxes.add((GuiTextField)component3);
               }
            }
         }
      }
   }

   private GuiTextField getGuiTextField(int i, GuiPageButtonList.EditBoxEntry editBoxEntry) {
      GuiTextField guiTextField = new GuiTextField(editBoxEntry.getId(), this.mc.fontRendererObj, i, 0, 150, 20);
      guiTextField.setText(editBoxEntry.getName());
      guiTextField.setGuiResponder(this.responder);
      guiTextField.setVisible(editBoxEntry.isVisible());
      guiTextField.setValidator(editBoxEntry.getValidator());
      return guiTextField;
   }

   private Gui createComponent(GuiPageButtonList.GuiListEntry entry, int xOffset, boolean fullWidth) {
      return entry instanceof GuiPageButtonList.GuiSlideEntry
         ? this.getGuiSlider(this.width / 2 - 155 + xOffset, (GuiPageButtonList.GuiSlideEntry)entry)
         : (
            entry instanceof GuiPageButtonList.GuiButtonEntry
               ? this.getGuiListButton(this.width / 2 - 155 + xOffset, (GuiPageButtonList.GuiButtonEntry)entry)
               : (
                  entry instanceof GuiPageButtonList.EditBoxEntry
                     ? this.getGuiTextField(this.width / 2 - 155 + xOffset, (GuiPageButtonList.EditBoxEntry)entry)
                     : (
                        entry instanceof GuiPageButtonList.GuiLabelEntry
                           ? this.getGuiLabel(this.width / 2 - 155 + xOffset, (GuiPageButtonList.GuiLabelEntry)entry, fullWidth)
                           : null
                     )
               )
         );
   }

   public void previousPage() {
      if (this.page > 0) {
         this.setPage(this.page - 1);
      }
   }

   public GuiPageButtonList(
      Minecraft mcIn,
      int widthIn,
      int heightIn,
      int topIn,
      int bottomIn,
      int slotHeightIn,
      GuiPageButtonList.GuiResponder responder,
      GuiPageButtonList.GuiListEntry[]... pages
   ) {
      super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
      this.responder = responder;
      this.pages = pages;
      this.showListBackground = false;
      this.createAllComponents();
      this.populateEntries();
   }

   public void setPage(int pageIndex) {
      if (pageIndex != this.page) {
         int page2 = this.page;
         this.page = pageIndex;
         this.populateEntries();
         this.updatePageVisibility(page2, pageIndex);
         this.amountScrolled = 0.0F;
      }
   }

   private GuiSlider getGuiSlider(int i, GuiPageButtonList.GuiSlideEntry guiSlideEntry) {
      GuiSlider guiSlider = new GuiSlider(
         this.responder, guiSlideEntry.getId(), i, 0, guiSlideEntry.getName(), guiSlideEntry.getMin(), guiSlideEntry.getMax(), guiSlideEntry.getDefaultValue(), guiSlideEntry.getFormatHelper()
      );
      guiSlider.visible = guiSlideEntry.isVisible();
      return guiSlider;
   }

   @Override
   protected int getScrollBarX() {
      return super.getScrollBarX() + 32;
   }

   public void handleKeyTyped(char typedChar, int keyCode) {
      if (this.activeComponent instanceof GuiTextField guiTextField) {
         if (!GuiScreen.isKeyComboCtrlV(keyCode)) {
            if (keyCode == 15) {
               guiTextField.setFocused(false);
               int i = this.editBoxes.indexOf(this.activeComponent);
               if (GuiScreen.isShiftKeyDown()) {
                  if (i == 0) {
                     i = this.editBoxes.size() - 1;
                  } else {
                     i--;
                  }
               } else if (i == this.editBoxes.size() - 1) {
                  i = 0;
               } else {
                  i++;
               }

               this.activeComponent = this.editBoxes.get(i);
               GuiTextField guiTextField2 = (GuiTextField)this.activeComponent;
               guiTextField2.setFocused(true);
               int i2 = guiTextField2.yPosition + this.slotHeight;
               int yPosition2 = guiTextField2.yPosition;
               if (i2 > this.bottom) {
                  this.amountScrolled = this.amountScrolled + (i2 - this.bottom);
               } else if (yPosition2 < this.top) {
                  this.amountScrolled = yPosition2;
               }
            } else {
               guiTextField.textboxKeyTyped(typedChar, keyCode);
            }
         } else {
            String clipboardString = GuiScreen.getClipboardString();
            String[] texts = clipboardString.split(";");
            int i3 = this.editBoxes.indexOf(this.activeComponent);
            int i4 = i3;

            for (String text : texts) {
               this.editBoxes.get(i4).setText(text);
               if (i4 == this.editBoxes.size() - 1) {
                  i4 = 0;
               } else {
                  i4++;
               }

               if (i4 == i3) {
                  break;
               }
            }
         }
      }
   }

   private void updatePageVisibility(int oldPage, int newPage) {
      for (GuiPageButtonList.GuiListEntry guiListEntry : this.pages[oldPage]) {
         if (guiListEntry != null) {
            this.setComponentVisible(this.componentMap.lookup(guiListEntry.getId()), false);
         }
      }

      for (GuiPageButtonList.GuiListEntry guiListEntry2 : this.pages[newPage]) {
         if (guiListEntry2 != null) {
            this.setComponentVisible(this.componentMap.lookup(guiListEntry2.getId()), true);
         }
      }
   }

   public int getPageCount() {
      return this.pages.length;
   }

   public void nextPage() {
      if (this.page < this.pages.length - 1) {
         this.setPage(this.page + 1);
      }
   }

   private void populateEntries() {
      this.entries.clear();

      for (int index = 0; index < this.pages[this.page].length; index += 2) {
         GuiPageButtonList.GuiListEntry guiListEntry = this.pages[this.page][index];
         GuiPageButtonList.GuiListEntry guiListEntry2 = index < this.pages[this.page].length - 1 ? this.pages[this.page][index + 1] : null;
         Gui gui = this.componentMap.lookup(guiListEntry.getId());
         Gui gui1 = guiListEntry2 != null ? this.componentMap.lookup(guiListEntry2.getId()) : null;
         GuiPageButtonList.GuiEntry guiEntry = new GuiPageButtonList.GuiEntry(gui, gui1);
         this.entries.add(guiEntry);
      }
   }

   public Gui getComponent(int id) {
      return this.componentMap.lookup(id);
   }

   private GuiListButton getGuiListButton(int i, GuiPageButtonList.GuiButtonEntry guiButtonEntry) {
      GuiListButton guiListButton = new GuiListButton(this.responder, guiButtonEntry.getId(), i, 0, guiButtonEntry.getName(), guiButtonEntry.getInitialValue());
      guiListButton.visible = guiButtonEntry.isVisible();
      return guiListButton;
   }

   public GuiPageButtonList.GuiEntry getListEntry(int index) {
      return this.entries.get(index);
   }

   public Gui getActiveComponent() {
      return this.activeComponent;
   }

   public int getPage() {
      return this.page;
   }

   public static class EditBoxEntry extends GuiPageButtonList.GuiListEntry {
      private final Predicate<String> validator;

      public EditBoxEntry(int id, String name, boolean visible, Predicate<String> validator) {
         super(id, name, visible);
         this.validator = (Predicate<String>)Objects.firstNonNull(validator, Predicates.alwaysTrue());
      }

      public Predicate<String> getValidator() {
         return this.validator;
      }
   }

   public static class GuiButtonEntry extends GuiPageButtonList.GuiListEntry {
      private final boolean initialValue;

      public GuiButtonEntry(int id, String name, boolean visible, boolean initialValue) {
         super(id, name, visible);
         this.initialValue = initialValue;
      }

      public boolean getInitialValue() {
         return this.initialValue;
      }
   }

   public static class GuiEntry implements GuiListExtended.IGuiListEntry {
      private final Minecraft mc = Minecraft.getMinecraft();
      private final Gui leftComponent;
      private Gui focusedComponent;
      private final Gui rightComponent;

      private void handleGuiLabel(GuiLabel guiLabel, int i, boolean flag) {
         guiLabel.intValue5 = i;
         if (!flag) {
            guiLabel.handleMinecraft2(this.mc);
         }
      }

      private boolean checkGuiButton(GuiButton guiButton, int i, int i2) {
         boolean flag = guiButton.mousePressed(this.mc, i, i2);
         if (flag) {
            this.focusedComponent = guiButton;
         }

         return flag;
      }

      public Gui getLeftComponent() {
         return this.leftComponent;
      }

      private void drawButton(GuiButton button, int y, int mouseX, int mouseY, boolean onlyUpdatePosition) {
         button.yPosition = y;
         if (!onlyUpdatePosition) {
            button.drawButton(this.mc, mouseX, mouseY);
         }
      }

      @Override
      public void noop(int i) {
         this.drawComponent(this.leftComponent, i, 0, 0, true);
         this.drawComponent(this.rightComponent, i, 0, 0, true);
      }

      private void mousePressedTextField(GuiTextField textField, int mouseX, int mouseY, int mouseButton) {
         textField.mouseClicked(mouseX, mouseY, mouseButton);
         if (textField.isFocused()) {
            this.focusedComponent = textField;
         }
      }

      public GuiEntry(Gui leftComponent, Gui rightComponent) {
         this.leftComponent = leftComponent;
         this.rightComponent = rightComponent;
      }

      public Gui getRightComponent() {
         return this.rightComponent;
      }

      @Override
      public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
         this.drawComponent(this.leftComponent, y, mouseX, mouseY, false);
         this.drawComponent(this.rightComponent, y, mouseX, mouseY, false);
      }

      private void handleGui(Gui gui2) {
         if (gui2 != null && gui2 instanceof GuiButton) {
            this.handleGuiButton((GuiButton)gui2);
         }
      }

      private void handleGuiButton(GuiButton guiButton) {
         guiButton.update();
      }

      private boolean mousePressedComponent(Gui component, int mouseX, int mouseY, int mouseButton) {
         if (component == null) {
            return false;
         }

         if (component instanceof GuiButton) {
            return this.checkGuiButton((GuiButton)component, mouseX, mouseY);
         }

         if (component instanceof GuiTextField) {
            this.mousePressedTextField((GuiTextField)component, mouseX, mouseY, mouseButton);
         }

         return false;
      }

      @Override
      public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseButton, int relativeX, int relativeY) {
         boolean conditionMet = this.mousePressedComponent(this.leftComponent, mouseX, mouseY, mouseButton);
         boolean conditionMet2 = this.mousePressedComponent(this.rightComponent, mouseX, mouseY, mouseButton);
         return conditionMet || conditionMet2;
      }

      @Override
      public void update() {
         this.handleGui(this.leftComponent);
         this.handleGui(this.rightComponent);
      }

      private void drawTextField(GuiTextField textField, int y, boolean onlyUpdatePosition) {
         textField.yPosition = y;
         if (!onlyUpdatePosition) {
            textField.drawTextBox();
         }
      }

      private void drawComponent(Gui component, int y, int mouseX, int mouseY, boolean onlyUpdatePosition) {
         if (component != null) {
            if (component instanceof GuiButton) {
               this.drawButton((GuiButton)component, y, mouseX, mouseY, onlyUpdatePosition);
            } else if (component instanceof GuiTextField) {
               this.drawTextField((GuiTextField)component, y, onlyUpdatePosition);
            } else if (component instanceof GuiLabel) {
               this.handleGuiLabel((GuiLabel)component, y, onlyUpdatePosition);
            }
         }
      }
   }

   public static class GuiLabelEntry extends GuiPageButtonList.GuiListEntry {
      public GuiLabelEntry(int id, String name, boolean visible) {
         super(id, name, visible);
      }
   }

   public static class GuiListEntry {
      private final int id;
      private final String name;
      private final boolean visible;

      public GuiListEntry(int id, String name, boolean visible) {
         this.id = id;
         this.name = name;
         this.visible = visible;
      }

      public boolean isVisible() {
         return this.visible;
      }

      public int getId() {
         return this.id;
      }

      public String getName() {
         return this.name;
      }
   }

   public interface GuiResponder {
      void setEntryValue(int i, float f);

      void setEntryValue(int i, boolean flag);

      void setEntryValue(int i, String text);
   }

   public static class GuiSlideEntry extends GuiPageButtonList.GuiListEntry {
      private final Contract formatHelper;
      private final float min;
      private final float max;
      private final float defaultValue;

      public float getMin() {
         return this.min;
      }

      public float getMax() {
         return this.max;
      }

      public Contract getFormatHelper() {
         return this.formatHelper;
      }

      public float getDefaultValue() {
         return this.defaultValue;
      }

      public GuiSlideEntry(int id, String name, boolean visible, Contract formatHelper, float min, float max, float defaultValue) {
         super(id, name, visible);
         this.formatHelper = formatHelper;
         this.min = min;
         this.max = max;
         this.defaultValue = defaultValue;
      }
   }
}
