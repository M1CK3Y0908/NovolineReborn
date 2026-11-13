package wtf.novoline.lang;

public class AuthException extends Throwable {
   public AuthException(String text) {
      super(text);
   }

   public AuthException(Throwable throwable) {
      super(throwable);
   }

   public AuthException(String text, Throwable throwable) {
      super(text, throwable);
   }
}
