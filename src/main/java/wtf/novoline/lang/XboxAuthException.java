package wtf.novoline.lang;

import java.util.Map;
import wtf.novoline.utils.MapBuilder;

public class XboxAuthException extends AuthException {
   private static final Map<Long, String> errorMessages = MapBuilder.<Long, String>create()
      .with(2148916233L, "The account doesn't have an Xbox account.")
      .with(2148916235L, "The account is from a country where Xbox Live is not available/banned.")
      .with(2148916238L, "The account is a child (under 18) and cannot proceed unless the account is added to a Family by an adult.");

   public XboxAuthException(String text) {
      super(text);
   }

   public XboxAuthException(long l2) {
      super(errorMessages.computeIfAbsent(l2, l -> "Unknown error code: " + l));
   }
}
