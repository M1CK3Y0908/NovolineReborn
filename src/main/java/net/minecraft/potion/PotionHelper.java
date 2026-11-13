package net.minecraft.potion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.src.Config;
import net.minecraft.util.IntegerCache;
import net.optifine.CustomColors;

public class PotionHelper {
   public static final String unusedString = "+0-1-2+3&4-4+13";
   public static final String text = "+0+1-2+3&4-4+13";
   public static final String text2 = "+0-1+2-3&4-4+13";
   public static final String text3 = "-5+6-7";
   public static final String text4 = "-0+1+2-3+13&4-4";
   public static final String text5 = "+0-1-2-3&4-4+13";
   public static final String text6 = "-0+1-2-3&4-4+13";
   public static final String text7 = "+14&13-13";
   public static final String text8 = "+5-6-7";
   public static final String text9 = "-0-1+2-3&4-4+13";
   public static final String text10 = "+0-1+2+3+13&4-4";
   public static final String text11 = "-0+3-4+13";
   public static final String text12 = "+0+1-2-3&4-4+13";
   public static final String text13 = null;
   private static final Map<Integer, String> potionRequirements = Maps.newHashMap();
   private static final Map<Integer, String> potionAmplifiers = Maps.newHashMap();
   private static final Map<Integer, Integer> DATAVALUE_COLORS = Maps.newHashMap();
   private static final String[] potionPrefixes = new String[]{
      "potion.prefix.mundane",
      "potion.prefix.uninteresting",
      "potion.prefix.bland",
      "potion.prefix.clear",
      "potion.prefix.milky",
      "potion.prefix.diffuse",
      "potion.prefix.artless",
      "potion.prefix.thin",
      "potion.prefix.awkward",
      "potion.prefix.flat",
      "potion.prefix.bulky",
      "potion.prefix.bungling",
      "potion.prefix.buttered",
      "potion.prefix.smooth",
      "potion.prefix.suave",
      "potion.prefix.debonair",
      "potion.prefix.thick",
      "potion.prefix.elegant",
      "potion.prefix.fancy",
      "potion.prefix.charming",
      "potion.prefix.dashing",
      "potion.prefix.refined",
      "potion.prefix.cordial",
      "potion.prefix.sparkling",
      "potion.prefix.potent",
      "potion.prefix.foul",
      "potion.prefix.odorless",
      "potion.prefix.rank",
      "potion.prefix.harsh",
      "potion.prefix.acrid",
      "potion.prefix.gross",
      "potion.prefix.stinky"
   };

   private static int parsePotionEffects(String input, int startIndex, int endIndex, int baseValue) {
      if (startIndex < input.length() && endIndex >= 0 && startIndex < endIndex) {
         int i = input.indexOf(124, startIndex);
         if (i >= 0 && i < endIndex) {
            int i2 = parsePotionEffects(input, startIndex, i - 1, baseValue);
            if (i2 > 0) {
               return i2;
            }

            int i3 = parsePotionEffects(input, i + 1, endIndex, baseValue);
            return i3 > 0 ? i3 : 0;
         } else {
            int i4 = input.indexOf(38, startIndex);
            if (i4 >= 0 && i4 < endIndex) {
               int i5 = parsePotionEffects(input, startIndex, i4 - 1, baseValue);
               if (i5 <= 0) {
                  return 0;
               }

               int i6 = parsePotionEffects(input, i4 + 1, endIndex, baseValue);
               return i6 <= 0 ? 0 : (i5 > i6 ? i5 : i6);
            } else {
               boolean flag = false;
               boolean flag2 = false;
               int i7 = 0;
               int i8 = 0;
               int nestedIndex = 0;
               char c0 = (char)-1;
               int i9 = 0;
               int i10 = 0;
               int i11 = 0;

               for (int startIndex2 = startIndex; startIndex2 < endIndex; startIndex2++) {
                  char c = input.charAt(startIndex2);
                  if (c >= '0' && c <= '9') {
                     if (flag) {
                        i10 = c - '0';
                        flag2 = true;
                     } else {
                        i9 *= 10;
                        i9 += c - '0';
                        i7 = 1;
                     }
                  } else if (c == '*') {
                     flag = true;
                  } else if (c == '!') {
                     if (i7 != 0) {
                        i11 += getPotionEffect((i8 != 0), flag2, (nestedIndex != 0), c0, i9, i10, baseValue);
                        nestedIndex = 0;
                        flag = false;
                        flag2 = false;
                        i7 = 0;
                        i10 = 0;
                        i9 = 0;
                        c0 = (char)-1;
                     }

                     i8 = 1;
                  } else if (c == '-') {
                     if (i7 != 0) {
                        i11 += getPotionEffect((i8 != 0), flag2, (nestedIndex != 0), c0, i9, i10, baseValue);
                        i8 = 0;
                        flag = false;
                        flag2 = false;
                        i7 = 0;
                        i10 = 0;
                        i9 = 0;
                        c0 = (char)-1;
                     }

                     nestedIndex = 1;
                  } else if (c != '=' && c != '<' && c != '>') {
                     if (c == '+' && i7 != 0) {
                        i11 += getPotionEffect((i8 != 0), flag2, (nestedIndex != 0), c0, i9, i10, baseValue);
                        i8 = 0;
                        nestedIndex = 0;
                        flag = false;
                        flag2 = false;
                        i7 = 0;
                        i10 = 0;
                        i9 = 0;
                        c0 = (char)-1;
                     }
                  } else {
                     if (i7 != 0) {
                        i11 += getPotionEffect((i8 != 0), flag2, (nestedIndex != 0), c0, i9, i10, baseValue);
                        i8 = 0;
                        nestedIndex = 0;
                        flag = false;
                        flag2 = false;
                        i7 = 0;
                        i10 = 0;
                        i9 = 0;
                        c0 = (char)-1;
                     }

                     if (c == '=') {
                        c0 = 0;
                     } else if (c == '<') {
                        c0 = 2;
                     } else if (c == '>') {
                        c0 = 1;
                     }
                  }
               }

               if (i7 != 0) {
                  i11 += getPotionEffect((i8 != 0), flag2, (nestedIndex != 0), c0, i9, i10, baseValue);
               }

               return i11;
            }
         }
      } else {
         return 0;
      }
   }

   public static int applyIngredient(int flags, String input) {
      int length2 = input.length();
      int count = 0;
      boolean conditionMet = false;
      boolean conditionMet2 = false;
      boolean conditionMet3 = false;
      int i = 0;

      for (int i2 = 0; i2 < length2; i2++) {
         int outerIndex = input.charAt(i2);
         if (outerIndex >= 48 && outerIndex <= 57) {
            i *= 10;
            i += outerIndex - 48;
            count = 1;
         } else if (outerIndex == 33) {
            if (count != 0) {
               flags = brewBitOperations(flags, i, conditionMet2, conditionMet, conditionMet3);
               conditionMet3 = false;
               conditionMet2 = false;
               count = 0;
               i = 0;
            }

            conditionMet = true;
         } else if (outerIndex == 45) {
            if (count != 0) {
               flags = brewBitOperations(flags, i, conditionMet2, conditionMet, conditionMet3);
               conditionMet3 = false;
               conditionMet = false;
               count = 0;
               i = 0;
            }

            conditionMet2 = true;
         } else if (outerIndex == 43) {
            if (count != 0) {
               flags = brewBitOperations(flags, i, conditionMet2, conditionMet, conditionMet3);
               conditionMet3 = false;
               conditionMet = false;
               conditionMet2 = false;
               count = 0;
               i = 0;
            }
         } else if (outerIndex == 38) {
            if (count != 0) {
               flags = brewBitOperations(flags, i, conditionMet2, conditionMet, conditionMet3);
               conditionMet = false;
               conditionMet2 = false;
               count = 0;
               i = 0;
            }

            conditionMet3 = true;
         }
      }

      if (count != 0) {
         flags = brewBitOperations(flags, i, conditionMet2, conditionMet, conditionMet3);
      }

      return flags & 32767;
   }

   static {
      potionRequirements.put(Potion.regeneration.getId(), "0 & !1 & !2 & !3 & 0+6");
      potionRequirements.put(Potion.moveSpeed.getId(), "!0 & 1 & !2 & !3 & 1+6");
      potionRequirements.put(Potion.fireResistance.getId(), "0 & 1 & !2 & !3 & 0+6");
      potionRequirements.put(Potion.heal.getId(), "0 & !1 & 2 & !3");
      potionRequirements.put(Potion.poison.getId(), "!0 & !1 & 2 & !3 & 2+6");
      potionRequirements.put(Potion.weakness.getId(), "!0 & !1 & !2 & 3 & 3+6");
      potionRequirements.put(Potion.harm.getId(), "!0 & !1 & 2 & 3");
      potionRequirements.put(Potion.moveSlowdown.getId(), "!0 & 1 & !2 & 3 & 3+6");
      potionRequirements.put(Potion.damageBoost.getId(), "0 & !1 & !2 & 3 & 3+6");
      potionRequirements.put(Potion.nightVision.getId(), "!0 & 1 & 2 & !3 & 2+6");
      potionRequirements.put(Potion.invisibility.getId(), "!0 & 1 & 2 & 3 & 2+6");
      potionRequirements.put(Potion.waterBreathing.getId(), "0 & !1 & 2 & 3 & 2+6");
      potionRequirements.put(Potion.jump.getId(), "0 & 1 & !2 & 3 & 3+6");
      potionAmplifiers.put(Potion.moveSpeed.getId(), "5");
      potionAmplifiers.put(Potion.digSpeed.getId(), "5");
      potionAmplifiers.put(Potion.damageBoost.getId(), "5");
      potionAmplifiers.put(Potion.regeneration.getId(), "5");
      potionAmplifiers.put(Potion.harm.getId(), "5");
      potionAmplifiers.put(Potion.heal.getId(), "5");
      potionAmplifiers.put(Potion.resistance.getId(), "5");
      potionAmplifiers.put(Potion.poison.getId(), "5");
      potionAmplifiers.put(Potion.jump.getId(), "5");
   }

   private static int isFlagUnset(int flags, int bitIndex) {
      return checkFlag(flags, bitIndex) ? 0 : 1;
   }

   public static boolean getAreAmbient(Collection<PotionEffect> potionEffects) {
      Iterator iterator2 = potionEffects.iterator();

      while (iterator2.hasNext()) {
         if (!((PotionEffect)iterator2.next()).getIsAmbient()) {
            return false;
         }
      }

      return true;
   }

   private static int brewBitOperations(int flags, int bitIndex, boolean remove, boolean toggle, boolean forceSet) {
      if (forceSet) {
         if (!checkFlag(flags, bitIndex)) {
            return 0;
         }
      } else if (remove) {
         flags &= ~(1 << bitIndex);
      } else if (toggle) {
         if ((flags & 1 << bitIndex) == 0) {
            flags |= 1 << bitIndex;
         } else {
            flags &= ~(1 << bitIndex);
         }
      } else {
         flags |= 1 << bitIndex;
      }

      return flags;
   }

   private static int countSetFlags(int flags) {
      int i;
      for (i = 0; flags > 0; i++) {
         flags &= flags - 1;
      }

      return i;
   }

   public static int createInt(int i) {
      return (checkFlag(i, 5) ? 16 : 0)
         | (checkFlag(i, 4) ? 8 : 0)
         | (checkFlag(i, 3) ? 4 : 0)
         | (checkFlag(i, 2) ? 2 : 0)
         | (checkFlag(i, 1) ? 1 : 0);
   }

   private static int getPotionEffect(boolean includeNot, boolean multiply, boolean invert, int comparisonMode, int bitIndex, int multiplier, int flags) {
      int i = 0;
      if (includeNot) {
         i = isFlagUnset(flags, bitIndex);
      } else if (comparisonMode != -1) {
         if (comparisonMode == 0 && countSetFlags(flags) == bitIndex) {
            i = 1;
         } else if (comparisonMode == 1 && countSetFlags(flags) > bitIndex) {
            i = 1;
         } else if (comparisonMode == 2 && countSetFlags(flags) < bitIndex) {
            i = 1;
         }
      } else {
         i = isFlagSet(flags, bitIndex);
      }

      if (multiply) {
         i *= multiplier;
      }

      if (invert) {
         i *= -1;
      }

      return i;
   }

   public static String getPotionPrefix(int dataValue) {
      int potionPrefixIndex = getPotionPrefixIndex(dataValue);
      return potionPrefixes[potionPrefixIndex];
   }

   public static int getPotionPrefixIndex(int dataValue) {
      return createInt(dataValue);
   }

   public static int createInt2(int i) {
      Integer integer = IntegerCache.getInteger(i);
      if (DATAVALUE_COLORS.containsKey(integer)) {
         return DATAVALUE_COLORS.get(integer);
      }

      int potionLiquidColor = calcPotionLiquidColor(createList(integer));
      DATAVALUE_COLORS.put(integer, potionLiquidColor);
      return potionLiquidColor;
   }

   public static boolean checkFlag(int flags, int bitIndex) {
      return (flags & 1 << bitIndex) != 0;
   }

   public static List createList(int i) {
      ArrayList arrayList = null;

      for (Potion potion2 : Potion.potionTypes) {
         if (potion2 != null && !potion2.isUsable()) {
            String text14 = potionRequirements.get(potion2.getId());
            if (text14 != null) {
               int i2 = parsePotionEffects(text14, 0, text14.length(), i);
               if (i2 > 0) {
                  int i3 = 0;
                  String text15 = potionAmplifiers.get(potion2.getId());
                  if (text15 != null) {
                     i3 = parsePotionEffects(text15, 0, text15.length(), i);
                     if (i3 < 0) {
                        i3 = 0;
                     }
                  }

                  if (potion2.isInstant()) {
                     i2 = 1;
                  } else {
                     i2 = 1200 * (i2 * 3 + (i2 - 1) * 2);
                     i2 >>= i3;
                     i2 = (int)Math.round(i2 * potion2.getEffectiveness());
                     if ((i & 16384) != 0) {
                        i2 = (int)Math.round(i2 * 0.75 + 0.5);
                     }
                  }

                  if (arrayList == null) {
                     arrayList = Lists.newArrayList();
                  }

                  PotionEffect potionEffect = new PotionEffect(potion2.getId(), i2, i3);
                  if ((i & 16384) != 0) {
                     potionEffect.update();
                  }

                  arrayList.add(potionEffect);
               }
            }
         }
      }

      return arrayList;
   }

   public static int calcPotionLiquidColor(Collection<PotionEffect> effects) {
      if (effects != null && !effects.isEmpty()) {
         float f = 0.0F;
         float f2 = 0.0F;
         float f3 = 0.0F;
         float f4 = 0.0F;

         for (PotionEffect potionEffect : effects) {
            if (potionEffect.getIsShowParticles()) {
               int liquidColor = Potion.potionTypes[potionEffect.getPotionID()].getLiquidColor();
               if (Config.isCustomColors()) {
                  liquidColor = CustomColors.getPotionColor(potionEffect.getPotionID(), liquidColor);
               }

               for (int i = 0; i <= potionEffect.getAmplifier(); i++) {
                  f += (liquidColor >> 16 & 0xFF) / 255.0F;
                  f2 += (liquidColor >> 8 & 0xFF) / 255.0F;
                  f3 += (liquidColor >> 0 & 0xFF) / 255.0F;
                  f4++;
               }
            }
         }

         if (f4 == 0.0F) {
            return 0;
         }

         f = f / f4 * 255.0F;
         f2 = f2 / f4 * 255.0F;
         f3 = f3 / f4 * 255.0F;
         return (int)f << 16 | (int)f2 << 8 | (int)f3;
      } else {
         return Config.isCustomColors() ? CustomColors.getPotionColor(0, 3694022) : 3694022;
      }
   }

   private static int isFlagSet(int flags, int bitIndex) {
      return checkFlag(flags, bitIndex) ? 1 : 0;
   }
}
