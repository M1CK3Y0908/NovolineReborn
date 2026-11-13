package wtf.novoline.lang;

public class ApiException extends Throwable {
   public ApiException(Throwable throwable) {
      super(throwable);
   }

   public ApiException(String text, Throwable throwable) {
      super(text, throwable);
   }

   public ApiException(String text) {
      super(text);
   }
}
