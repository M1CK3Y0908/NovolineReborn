package net.optifine.reflect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ReflectorResolver {
   private static final List<IResolvable> list = Collections.synchronizedList(new ArrayList<>());
   private static boolean flag = false;

   public static void run() {
      if (!flag) {
         Iterator iterator2 = list.iterator();

         while (iterator2.hasNext()) {
            ((IResolvable)iterator2.next()).update3();
         }

         flag = true;
      }
   }

   protected static void handleIResolvable(IResolvable iResolvable) {
      if (!flag) {
         list.add(iResolvable);
      } else {
         iResolvable.update3();
      }
   }
}
