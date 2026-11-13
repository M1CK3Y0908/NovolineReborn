package net.optifine.reflect;

import java.lang.reflect.Constructor;
import net.optifine.Log;
import wtf.novoline.utils.ArrayUtils;

public class ReflectorConstructor implements IResolvable {
   private ReflectorClass reflectorClass = null;
   private Class[] classArray = null;
   private boolean flag = false;
   private Constructor constructor = null;

   @Override
   public void update3() {
      this.getConstructor();
   }

   public Constructor getConstructor() {
      if (this.flag) {
         return this.constructor;
      }

      this.flag = true;
      Class type = this.reflectorClass.getType();
      if (type == null) {
         return null;
      }

      try {
         this.constructor = createConstructor(type, this.classArray);
         if (this.constructor == null) {
            Log.dbg("(Reflector) Constructor not present: " + type.getName() + ", params: " + ArrayUtils.joinWithComma(this.classArray));
         }

         if (this.constructor != null) {
            this.constructor.setAccessible(true);
         }
      } catch (Throwable throwable) {
         throwable.printStackTrace();
      }

      return this.constructor;
   }

   public boolean isActive() {
      return this.flag ? this.constructor != null : this.getConstructor() != null;
   }

   public void update() {
      this.flag = true;
      this.constructor = null;
   }

   private static Constructor createConstructor(Class clazz, Class[] clazzs) {
      Constructor[] declaredConstructors = clazz.getDeclaredConstructors();

      for (int i = 0; i < declaredConstructors.length; i++) {
         Constructor constructor2 = declaredConstructors[i];
         Class[] parameterTypes = constructor2.getParameterTypes();
         if (Reflector.checkArray(clazzs, parameterTypes)) {
            return constructor2;
         }
      }

      return null;
   }

   public ReflectorConstructor(ReflectorClass reflectorClass2, Class[] clazzs) {
      this.reflectorClass = reflectorClass2;
      this.classArray = clazzs;
      ReflectorResolver.handleIResolvable(this);
   }
}
