package net.optifine.reflect;

import java.lang.reflect.Field;

public class FieldLocatorFixed implements IFieldLocator {
   private Field field;

   public FieldLocatorFixed(Field field2) {
      this.field = field2;
   }

   @Override
   public Field getField2() {
      return this.field;
   }
}
