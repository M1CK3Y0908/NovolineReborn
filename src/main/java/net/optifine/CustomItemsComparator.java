package net.optifine;

import java.util.Comparator;
import net.minecraft.src.Config;

public class CustomItemsComparator implements Comparator {
   @Override
   public int compare(Object left, Object right) {
      CustomItemProperties customItemProperties = (CustomItemProperties)left;
      CustomItemProperties customItemProperties2 = (CustomItemProperties)right;
      return customItemProperties.weight != customItemProperties2.weight
         ? customItemProperties2.weight - customItemProperties.weight
         : (
            !Config.equals(customItemProperties.basePath, customItemProperties2.basePath)
               ? customItemProperties.basePath.compareTo(customItemProperties2.basePath)
               : customItemProperties.name.compareTo(customItemProperties2.name)
         );
   }
}
