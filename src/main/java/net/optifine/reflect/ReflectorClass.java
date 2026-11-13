package net.optifine.reflect;

import net.optifine.Log;

public class ReflectorClass implements IResolvable {
   private String text = null;
   private boolean flag = false;
   private Class classValue = null;

   public ReflectorClass(Class clazz) {
      this.classValue = clazz;
      this.text = clazz.getName();
      this.flag = true;
   }

   public ReflectorClass(String text2) {
      this.text = text2;
      ReflectorResolver.handleIResolvable(this);
   }

   @Override
   public void update3() {
      this.getType();
   }

   public boolean checkObject(Object object) {
      return this.getType() == null ? false : this.getType().isInstance(object);
   }

   public boolean isActive() {
      return this.getType() != null;
   }

   public String getText() {
      return this.text;
   }

   public Class getType() {
      if (this.flag) {
         return this.classValue;
      }

      this.flag = true;

      try {
         this.classValue = Class.forName(this.text);
      } catch (ClassNotFoundException classNotFoundException) {
         Log.log("(Reflector) Class not present: " + this.text);
      } catch (Throwable throwable) {
         throwable.printStackTrace();
      }

      return this.classValue;
   }
}
