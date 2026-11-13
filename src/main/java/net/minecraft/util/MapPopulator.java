package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class MapPopulator {
   public static Map createMap(Iterable iterable, Iterable iterable2, Map map) {
      Iterator iterator2 = iterable2.iterator();

      for (Object object : iterable) {
         map.put(object, iterator2.next());
      }

      if (iterator2.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return map;
      }
   }

   public static Map createMap2(Iterable iterable, Iterable iterable2) {
      return createMap(iterable, iterable2, Maps.newLinkedHashMap());
   }
}
