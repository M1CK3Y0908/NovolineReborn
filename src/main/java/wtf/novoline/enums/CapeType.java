package wtf.novoline.enums;

import wtf.novoline.cosmetic.CapeCosmeticChild1;
import wtf.novoline.utils.cosmetic.Cosmetic;

public enum CapeType {
   CAPE(CapeCosmeticChild1.class);

   private final Class<? extends Cosmetic> classValue;

   public Class<? extends Cosmetic> getClassValue() {
      return this.classValue;
   }

   CapeType(Class<? extends Cosmetic> clazz) {
      this.classValue = clazz;
   }
}
