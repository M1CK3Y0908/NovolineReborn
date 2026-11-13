package wtf.novoline.setting;

import wtf.novoline.module.Module;
import wtf.novoline.setting.ModuleSetting.CheckboxType;

public class ComboSetting extends ModuleSetting {
   private int currentElement;
   private final int defaultState;
   private boolean extended;
   private final String[] elements;

   public void setCurrentElement(int i) {
      this.currentElement = i;
   }

   public ComboSetting(Module module2, String text, int i, String... texts) {
      super(module2, text, "combo", CheckboxType.COMBO);
      this.currentElement = i;
      this.defaultState = i;
      this.elements = texts;
   }

   public String getCurrentElementAsString() {
      return this.elements[this.currentElement];
   }

   public int getCurrentElement() {
      return this.currentElement;
   }

   public String[] getElements() {
      return this.elements;
   }

   public void setExtended(boolean flag) {
      this.extended = flag;
   }

   public boolean isExtended() {
      return this.extended;
   }

   public void setCurrentElementByName(String text) {
      for (int i = 0; i < this.elements.length; i++) {
         if (this.elements[i].equals(text)) {
            this.currentElement = i;
            break;
         }
      }
   }

   public int getDefaultState() {
      return this.defaultState;
   }
}
