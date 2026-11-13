package net.optifine.reflect;

import java.lang.reflect.Field;

public class ReflectorField implements IResolvable {
   private IFieldLocator iFieldLocator = null;
   private boolean flag = false;
   private Field field = null;

   public Field getField() {
      if (this.flag) {
         return this.field;
      }

      this.flag = true;
      this.field = this.iFieldLocator.getField2();
      if (this.field != null) {
         this.field.setAccessible(true);
      }

      return this.field;
   }

   public ReflectorField(IFieldLocator iFieldLocator2) {
      this.iFieldLocator = iFieldLocator2;
      ReflectorResolver.handleIResolvable(this);
   }

   public ReflectorField(ReflectorClass reflectorClass, String text) {
      this(new FieldLocatorName(reflectorClass, text));
   }

   public void handleObject(Object object) {
      Reflector.checkObject4(null, this, object);
   }

   @Override
   public void update3() {
      this.getField();
   }

   public boolean isActive() {
      return this.getField() != null;
   }

   public Object getObject() {
      return Reflector.createObject6(null, this);
   }

   public ReflectorField(ReflectorClass reflectorClass, Class clazz) {
      this(reflectorClass, clazz, 0);
   }

   public ReflectorField(ReflectorClass reflectorClass, Class clazz, int i) {
      this(new FieldLocatorType(reflectorClass, clazz, i));
   }

   public ReflectorField(Field field2) {
      this(new FieldLocatorFixed(field2));
   }
}
