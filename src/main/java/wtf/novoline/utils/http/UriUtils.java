package wtf.novoline.utils.http;

import com.google.common.base.Strings;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public final class UriUtils {
   public static String createParameters(Map<String, String> map2) {
      return map2.entrySet()
         .stream()
         .filter(entry -> entry.getValue() != null)
         .map(entry -> (String)entry.getKey() + "=" + URLEncoder.encode((String)entry.getValue(), StandardCharsets.UTF_8))
         .collect(Collectors.joining("&"));
   }

   public static Map parseQueryString(URI uri) {
      return Strings.isNullOrEmpty(uri.getQuery())
         ? Collections.emptyMap()
         : Arrays.stream(uri.getQuery().split("&"))
            .map(string -> string.split("="))
            .peek(stringArray -> stringArray[1] = URLDecoder.decode(stringArray[1], StandardCharsets.UTF_8))
            .collect(Collectors.toMap(stringArray -> (String)stringArray[0], stringArray -> (String)stringArray[1]));
   }

   public static URI createURI(String text, Map map2) {
      StringBuilder stringBuilder = new StringBuilder(text);
      if (!map2.isEmpty()) {
         stringBuilder.append("?");
         stringBuilder.append(createParameters(map2));
      }

      return URI.create(stringBuilder.toString());
   }
}
