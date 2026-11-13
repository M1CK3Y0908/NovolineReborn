package net.optifine.util;

import java.util.Arrays;
import java.util.HashSet;
import net.minecraft.client.settings.KeyBinding;

public class KeyUtils {
   public static void fixKeyConflicts(KeyBinding[] keys, KeyBinding[] keysPrio) {
      HashSet hashSet = new HashSet();

      for (int i = 0; i < keysPrio.length; i++) {
         KeyBinding keyBinding = keysPrio[i];
         hashSet.add(keyBinding.getKeyCode());
      }

      HashSet hashSet2 = new HashSet<>(Arrays.asList(keys));
      hashSet2.removeAll(Arrays.asList(keysPrio));

      for (KeyBinding keyBinding2 : (Iterable<KeyBinding>) hashSet2) {
         Integer keyCode = keyBinding2.getKeyCode();
         if (hashSet.contains(keyCode)) {
            keyBinding2.setKeyCode(0);
         }
      }
   }
}
