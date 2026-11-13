package net.minecraft.client.settings;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IntHashMap;

public class KeyBinding implements Comparable<KeyBinding> {
   private static final List<KeyBinding> keybindArray = Lists.newArrayList();
   private int pressTime;
   private final String keyDescription;
   private static final IntHashMap<KeyBinding> hash = new IntHashMap<>();
   private int keyCode;
   private boolean pressed;
   private final int keyCodeDefault;
   private static final Set<String> keybindSet = Sets.newHashSet();
   private final String keyCategory;

   public static void unPressAllKeys() {
      Iterator iterator2 = keybindArray.iterator();

      while (iterator2.hasNext()) {
         ((KeyBinding)iterator2.next()).unpressKey();
      }
   }

   public void setKeyCode(int keyCode) {
      this.keyCode = keyCode;
   }

   public int getKeyCode() {
      return this.keyCode;
   }

   public int compareTo(KeyBinding arg1) {
      int i = I18n.format(this.keyCategory).compareTo(I18n.format(arg1.keyCategory));
      if (i == 0) {
         i = I18n.format(this.keyDescription).compareTo(I18n.format(arg1.keyDescription));
      }

      return i;
   }

   public boolean isPressed() {
      if (this.pressTime == 0) {
         return false;
      }

      this.pressTime--;
      return true;
   }

   public boolean isKeyDown() {
      return this.pressed;
   }

   private void unpressKey() {
      this.pressTime = 0;
      this.pressed = false;
   }

   public int getKeyCodeDefault() {
      return this.keyCodeDefault;
   }

   public static Set<String> getKeybinds() {
      return keybindSet;
   }

   public String getKeyCategory() {
      return this.keyCategory;
   }

   public static void setKeyBindState(int keyCode, boolean pressed) {
      if (keyCode != 0) {
         KeyBinding keyBinding = hash.lookup(keyCode);
         if (keyBinding != null) {
            keyBinding.pressed = pressed;
         }
      }
   }

   public KeyBinding(String description, int keyCode, String category) {
      this.keyDescription = description;
      this.keyCode = keyCode;
      this.keyCodeDefault = keyCode;
      this.keyCategory = category;
      keybindArray.add(this);
      hash.addKey(keyCode, this);
      keybindSet.add(category);
   }

   public static void onTick(int keyCode) {
      if (keyCode != 0) {
         KeyBinding keyBinding = hash.lookup(keyCode);
         if (keyBinding != null) {
            keyBinding.pressTime++;
         }
      }
   }

   public String getKeyDescription() {
      return this.keyDescription;
   }

   public static void resetKeyBindingArrayAndHash() {
      hash.clearMap();

      for (KeyBinding keyBinding2 : keybindArray) {
         hash.addKey(keyBinding2.keyCode, keyBinding2);
      }
   }

   public void setKeyDown(boolean pressed) {
      this.pressed = pressed;
   }
}
