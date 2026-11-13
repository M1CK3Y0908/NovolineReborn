package wtf.novoline.lang;

public class ApiParseException extends ApiException {
   public ApiParseException(String text, Throwable throwable) {
      super(text, throwable);
   }

   public ApiParseException(String text) {
      super(text);
   }

   public ApiParseException(Throwable throwable) {
      super(throwable);
   }
}
