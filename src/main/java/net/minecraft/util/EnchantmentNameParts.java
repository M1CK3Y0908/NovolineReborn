package net.minecraft.util;

import java.util.Random;

public class EnchantmentNameParts {
   private static final EnchantmentNameParts instance = new EnchantmentNameParts();
   private Random rand = new Random();
   private String[] namePartsArray = "the elder scrolls klaatu berata niktu xyzzy bless curse light darkness fire air earth water hot dry cold wet ignite snuff embiggen twist shorten stretch fiddle destroy imbue galvanize enchant free limited range of towards inside sphere cube self other ball mental physical grow shrink demon elemental spirit animal creature beast humanoid undead fresh stale "
      .split(" ");

   public void reseedRandomGenerator(long seed) {
      this.rand.setSeed(seed);
   }

   public static EnchantmentNameParts getInstance() {
      return instance;
   }

   public String generateNewRandomName() {
      int i = this.rand.nextInt(2) + 3;
      String message = "";

      for (int innerIndex = 0; innerIndex < i; innerIndex++) {
         if (innerIndex > 0) {
            message = message + " ";
         }

         message = message + this.namePartsArray[this.rand.nextInt(this.namePartsArray.length)];
      }

      return message;
   }
}
