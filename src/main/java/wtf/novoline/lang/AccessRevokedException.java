package wtf.novoline.lang;

public class AccessRevokedException extends AuthException {
   public AccessRevokedException() {
      super("Access to the application was revoked");
   }
}
