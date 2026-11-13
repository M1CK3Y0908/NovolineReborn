package wtf.novoline.utils;

import java.util.HashMap;

public class MapBuilder<K, V> extends HashMap<K, V> {
   private MapBuilder() {
   }

   public MapBuilder<K, V> with(K value, V value2) {
      this.put((K)value, (V)value2);
      return this;
   }

   public static <K, V> MapBuilder<K, V> create() {
      return new MapBuilder<>();
   }
}
