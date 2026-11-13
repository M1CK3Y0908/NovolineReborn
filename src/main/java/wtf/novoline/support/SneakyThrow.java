package wtf.novoline.support;

public final class SneakyThrow {
   private SneakyThrow() {
   }

   public static RuntimeException sneak(Throwable throwable) {
      return SneakyThrow.<RuntimeException>rethrow(throwable);
   }

   @SuppressWarnings("unchecked")
   private static <T extends Throwable> T rethrow(Throwable throwable) throws T {
      throw (T)throwable;
   }
}
