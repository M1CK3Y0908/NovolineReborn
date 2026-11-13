package wtf.novoline.utils;

public class Functions {
   @FunctionalInterface
   public interface Contract<T, U, V, W, X, Y, Z> {
      void accept(T value, U value2, V value3, W value4, X value5, Y value6, Z value7);
   }

   @FunctionalInterface
   public interface Contract10<T, U, V, W, X, Y, Z, A, B> {
      void accept(T value, U value2, V value3, W value4, X value5, Y value6, Z value7, A value8, B value9);
   }

   @FunctionalInterface
   public interface Contract11<T, U, V, R> {
      R apply(T value, U value2, V value3);
   }

   @FunctionalInterface
   public interface Contract12<T, U, V, W, X, R> {
      R apply(T value, U value2, V value3, W value4, X value5);
   }

   @FunctionalInterface
   public interface Contract2<T, U, V> {
      void accept(T value, U value2, V value3);
   }

   @FunctionalInterface
   public interface Contract3<T, U, V, W, X, Y, Z, A> {
      void accept(T value, U value2, V value3, W value4, X value5, Y value6, Z value7, A value8);
   }

   @FunctionalInterface
   public interface Contract4<T, U, V, W> {
      void accept(T value, U value2, V value3, W value4);
   }

   @FunctionalInterface
   public interface Contract5<T, U, V, W, X, Y, Z, A, B, C> {
      void accept(T value, U value2, V value3, W value4, X value5, Y value6, Z value7, A value8, B value9, C value10);
   }

   @FunctionalInterface
   public interface Contract6<T, U, V, W, X> {
      void accept(T value, U value2, V value3, W value4, X value5);
   }

   @FunctionalInterface
   public interface Contract7<T, U, V, W, R> {
      R apply(T value, U value2, V value3, W value4);
   }

   @FunctionalInterface
   public interface Contract8<T, U, V, W, X, Y, R> {
      R apply(T value, U value2, V value3, W value4, X value5, Y value6);
   }

   @FunctionalInterface
   public interface Contract9<T, U, V, W, X, Y> {
      void accept(T value, U value2, V value3, W value4, X value5, Y value6);
   }
}
