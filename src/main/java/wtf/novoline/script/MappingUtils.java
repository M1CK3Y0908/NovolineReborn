package wtf.novoline.script;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import wtf.novoline.utils.Functions.Contract11;
import wtf.novoline.utils.Functions.Contract2;
import wtf.novoline.utils.Functions.Contract6;
import wtf.novoline.utils.Functions.Contract7;

public final class MappingUtils {
   public static <T, U, V, R> Contract11<Map<String, Object>, U, V, R> moo2oMappingFunction(Class<T> clazz, Contract11<T, U, V, R> contract11) {
      return (map2, object, object2) -> (R)contract11.apply((T) unpack(clazz, map2), object, object2);
   }

   public static <T, U> BiConsumer<Map<String, Object>, U> moMappingConsumer(Class<T> clazz, BiConsumer<T, U> biConsumer) {
      return (map2, object) -> biConsumer.accept((T) unpack(clazz, map2), object);
   }

   public static <T, U, R> BiFunction<Map<String, Object>, U, Map<String, Object>> mo2mMappingFunction(
      Class<T> clazz, Class<U> clazz2, Class<R> clazz3, BiFunction<T, U, R> biFunction
   ) {
      return (map2, object) -> pack(clazz3, biFunction.apply((T) unpack(clazz, map2), object));
   }

   public static <T> Supplier<List<Map<String, Object>>> mappingListSupplier(Class<T> clazz, Supplier<List<T>> supplier) {
      return () -> ((List)supplier.get()).stream().map(object -> pack(clazz, object)).toList();
   }

   public static <T, R> Function<T, Map<String, Object>> o2mMappingFunction(Class<R> clazz, Function<T, R> function2) {
      return object -> pack(clazz, function2.apply(object));
   }

   public static <T, U, V, W, R> Contract7<Map<String, Object>, U, V, W, R> mooo2oMappingFunction(Class<T> clazz, Contract7<T, U, V, W, R> contract7) {
      return (map2, object, object2, object3) -> (R)contract7.apply((T) unpack(clazz, map2), object, object2, object3);
   }

   public static <T, U, R> BiFunction<T, U, Map<String, Object>> oo2mMappingFunction(Class<R> clazz, BiFunction<T, U, R> biFunction) {
      return (object, object2) -> pack(clazz, biFunction.apply(object, object2));
   }

   private static Map pack(Class clazz, Object object5) {
      return object5 == null ? null : TypeRegistry.getTypeRegistry().<Object>getTypeAdapter(clazz).pack(object5);
   }

   public static <T, U, V, R> Contract11<T, U, V, Map<String, Object>> ooo2mMappingFunction(Class<R> clazz, Contract11<T, U, V, R> contract11) {
      return (object, object2, object3) -> pack(clazz, contract11.apply(object, object2, object3));
   }

   public static <T, U, R> BiFunction<Map<String, Object>, U, R> mo2oMappingFunction(Class<T> clazz, BiFunction<T, U, R> biFunction) {
      return (map2, object) -> (R)biFunction.apply((T) unpack(clazz, map2), object);
   }

   public static <T, U, V, W, R> Contract7<Map<String, Object>, U, V, W, Map<String, Object>> mooo2mMappingFunction(
      Class<T> clazz, Class<R> clazz2, Contract7<T, U, V, W, R> contract7
   ) {
      return (map2, object, object2, object3) -> pack(clazz2, contract7.apply((T) unpack(clazz, map2), object, object2, object3));
   }

   public static <T, U, R> BiFunction<Map<String, Object>, Map<String, Object>, R> mm2oMappingFunction(Class<T> clazz, Class<U> clazz2, BiFunction<T, U, R> biFunction) {
      return (map2, map3) -> (R)biFunction.apply((T) unpack(clazz, map2), (U) unpack(clazz2, map3));
   }

   public static <T> Supplier<Map<String, Object>> mappingSupplier(Class<T> clazz, Supplier<T> supplier) {
      return () -> pack(clazz, supplier.get());
   }

   public static <T, U, V, W, X> Contract6<Map<String, Object>, U, V, W, X> mooooMappingConsumer(Class<T> clazz, Contract6<T, U, V, W, X> contract6) {
      return (map2, object, object2, object3, object4) -> contract6.accept((T) unpack(clazz, map2), object, object2, object3, object4);
   }

   public static <T, R> Function<Map<String, Object>, R> m2oMappingFunction(Class<T> clazz, Function<T, R> function2) {
      return map2 -> (R)function2.apply((T) unpack(clazz, map2));
   }

   public static <T extends Enum<T>> Consumer<Set<String>> mappingEnumSetConsumer(Consumer<Set<T>> consumer, Class<T> clazz) {
      return set -> consumer.accept(
         (Set<T>)((Set)set.stream()
            .map(string -> string.toUpperCase(Locale.ROOT))
            .map(string -> Enum.valueOf(clazz, string))
            .collect(Collectors.toSet()))
      );
   }

   private static Object unpack(Class clazz, Map map4) {
      return map4 == null ? null : TypeRegistry.getTypeRegistry().getTypeAdapter(clazz).unpack(map4);
   }

   public static <T, U, R> BiFunction<T, Map<String, Object>, R> om2oMappingFunction(Class<U> clazz, BiFunction<T, U, R> biFunction) {
      return (object, map2) -> (R)biFunction.apply(object, (U) unpack(clazz, map2));
   }

   public static <T, U, V> Contract2<Map<String, Object>, U, V> mooMappingConsumer(Class<T> clazz, Contract2<T, U, V> contract2) {
      return (map2, object, object2) -> contract2.accept((T) unpack(clazz, map2), object, object2);
   }

   public static <T, R> Function<Map<String, Object>, Map<String, Object>> m2mMappingFunction(Class<T> clazz, Class<R> clazz2, Function<T, R> function2) {
      return map2 -> pack(clazz2, function2.apply((T) unpack(clazz, map2)));
   }

   public static <T> Supplier<Map<String, Object>[]> mappingArraySupplier(Class<T> clazz, Supplier<T[]> supplier) {
      return () -> Arrays.<Object>stream((Object[])supplier.get()).map(object -> pack(clazz, object)).toArray(Map[]::new);
   }

   public static <T, U, R> BiFunction<Map<String, Object>, Map<String, Object>, Map<String, Object>> mm2mMappingFunction(
      Class<T> clazz, Class<U> clazz2, Class<R> clazz3, BiFunction<T, U, R> biFunction
   ) {
      return (map2, map3) -> pack(clazz3, biFunction.apply((T) unpack(clazz, map2), (U) unpack(clazz2, map3)));
   }

   public static <T, U, V, R> Contract11<Map<String, Object>, Map<String, Object>, V, R> mmo2oMappingFunction(
      Class<T> clazz, Class<U> clazz2, Contract11<T, U, V, R> contract11
   ) {
      return (map2, map3, object) -> (R)contract11.apply((T) unpack(clazz, map2), (U) unpack(clazz2, map3), object);
   }

   public static Supplier<Set<String>> mappingEnumSetSupplier(Supplier<Set<? extends Enum<?>>> supplier) {
      return () -> (Set<String>) ((Set)supplier.get()).stream().map((enumValue) -> ((Enum) enumValue).name().toLowerCase(Locale.ROOT)).collect(Collectors.toSet());
   }

   public static <T> Consumer<Map<String, Object>> mappingConsumer(Class<T> clazz, Consumer<T> consumer) {
      return map2 -> consumer.accept((T) unpack(clazz, map2));
   }

   public static <T, U> BiConsumer<Map<String, Object>, Map<String, Object>> mmMappingConsumer(Class<T> clazz, Class<U> clazz2, BiConsumer<T, U> biConsumer) {
      return (map2, map3) -> biConsumer.accept((T) unpack(clazz, map2), (U) unpack(clazz2, map3));
   }
}
