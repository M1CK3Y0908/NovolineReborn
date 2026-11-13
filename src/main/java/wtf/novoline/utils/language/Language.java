package wtf.novoline.utils.language;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import wtf.novoline.Novoline;

public class Language {
   private static final Language.RuRuType ruRuType = Language.RuRuType.EN_US;
   private static final Map<Language.RuRuType, Map<String, String>> translations = new EnumMap<>(Language.RuRuType.class);

   public static void loadAll() {
      for (Language.RuRuType ruRuType3 : Language.RuRuType.values()) {
         try {
            ResourceLocation resourceLocation = new ResourceLocation("novoline", "lang/" + ruRuType3.getLanguageTag() + ".lang");
            IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation);
            translations.put(ruRuType3, parseFile(resource.getInputStream()));
         } catch (IOException ioException) {
            throw new RuntimeException("Couldn't load translation file for " + ruRuType3);
         }
      }
   }

   private static Map getTranslations(Language.RuRuType ruRuType3, Language.RuRuType ruRuType4) {
      Map map2 = translations.get(ruRuType3);
      if (map2 != null) {
         return map2;
      } else if (ruRuType4 != null) {
         return getTranslations(ruRuType4, null);
      } else {
         throw new IllegalArgumentException("Language file for %s is not present".formatted(ruRuType3));
      }
   }

   private static Map<String, String> parseFile(InputStream inputStream) throws IOException {
      return Arrays.stream(IOUtils.toString(inputStream, StandardCharsets.UTF_8).split("\n"))
         .filter(string -> !string.trim().isEmpty() && !string.startsWith("#"))
         .map(string -> string.split("="))
         .collect(Collectors.toMap(stringArray -> (String)stringArray[0], stringArray -> stringArray[1].trim()));
   }

   public static String translate(String text, Object... objects) {
      String text2 = (String)getTranslations(Novoline.getNovoline().getRuRuType(), ruRuType).get(text);
      if (text2 == null) {
         return "<Missing translation: " + text + ">";
      }

      if (objects.length == 0) {
         return text2;
      }

      try {
         return String.format(Locale.ENGLISH, text2, objects);
      } catch (IllegalFormatException illegalFormatException) {
         return "<" + illegalFormatException.getClass().getCanonicalName() + ": " + text + ">";
      }
   }

   public enum RuRuType {
      RU_RU("ru-RU"),
      EN_US("en-US");

      private final String languageTag;

      public String getLanguageTag() {
         return this.languageTag;
      }

      public static Optional<Language.RuRuType> byTagOptional(String text) {
         return Arrays.stream(values()).filter(ruRuType2 -> ruRuType2.getLanguageTag().equals(text)).findAny();
      }

      RuRuType(String text) {
         this.languageTag = text;
      }

      public static Language.RuRuType createRuRuType(String text) {
         return byTagOptional(text).orElse(Language.ruRuType);
      }
   }
}
