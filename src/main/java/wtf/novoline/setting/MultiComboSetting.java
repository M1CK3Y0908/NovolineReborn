package wtf.novoline.setting;

import java.util.ArrayList;
import java.util.List;
import wtf.novoline.module.Module;
import wtf.novoline.setting.ModuleSetting.CheckboxType;

public class MultiComboSetting extends ModuleSetting {
   private final List<MultiComboSetting.Option> elements = new ArrayList<>();

   public MultiComboSetting.Option byName(String text) {
      for (MultiComboSetting.Option option : this.elements) {
         if (option.getName().equals(text)) {
            return option;
         }
      }

      return null;
   }

   public List<MultiComboSetting.Option> getElements() {
      return this.elements;
   }

   public boolean isSelected(int i) {
      return this.getElements().get(i).isSelected();
   }

   public MultiComboSetting(Module module2, String text) {
      super(module2, text, "multicombo", CheckboxType.MULTI_COMBO);
   }

   public MultiComboSetting add(String text, boolean flag) {
      this.elements.add(new MultiComboSetting.Option(text, flag));
      return this;
   }

   public class Option {
      private final String name;
      private boolean selected;
      private final boolean defaultState;

      public void toggleSelection() {
         this.setSelected(!this.isSelected());
      }

      public Option(String text, boolean flag) {
         this.name = text;
         this.selected = flag;
         this.defaultState = flag;
      }

      public void setSelected(boolean flag) {
         this.selected = flag;
         MultiComboSetting.this.interactCallback();
      }

      public boolean isDefaultState() {
         return this.defaultState;
      }

      public String getName() {
         return this.name;
      }

      public boolean isSelected() {
         return this.selected;
      }
   }
}
