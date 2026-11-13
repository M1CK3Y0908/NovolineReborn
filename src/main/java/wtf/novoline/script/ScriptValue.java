package wtf.novoline.script;

import java.lang.StackWalker.Option;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;
import wtf.novoline.scripting.ScriptContext;
import wtf.novoline.utils.ReflectionUtils;
import wtf.novoline.utils.Functions.Contract;
import wtf.novoline.utils.Functions.Contract10;
import wtf.novoline.utils.Functions.Contract11;
import wtf.novoline.utils.Functions.Contract12;
import wtf.novoline.utils.Functions.Contract2;
import wtf.novoline.utils.Functions.Contract3;
import wtf.novoline.utils.Functions.Contract4;
import wtf.novoline.utils.Functions.Contract5;
import wtf.novoline.utils.Functions.Contract6;
import wtf.novoline.utils.Functions.Contract7;
import wtf.novoline.utils.Functions.Contract8;
import wtf.novoline.utils.Functions.Contract9;
import wtf.novoline.support.SneakyThrow;

public interface ScriptValue {
   long UNUSED_LONG_CONSTANT = 72070940956393L;
   Map UNUSED_MAP = new HashMap(13);
   String[] stringArray = new String[]{
      "=\u0088æ@ËÆ \u0002®ø2\u0092×à¾Ø", "Ì5\u009eYa£:\u0012ÿ\u0091\u0000ý×ú¤\u0082J¸ë\u0014\u009a;c¾", "Ý¯\u0097e\u001e\u0085R»\u0005\u0003áà\\.Æ\u0097"
   };
   String[] stringArray2 = new String[3];

   default Enum getEnum(String text, Class clazz) {
      String string2 = this.getString(text);

      for (Enum enumValue : (Enum[])clazz.getEnumConstants()) {
         if (enumValue.name().equalsIgnoreCase(string2)) {
            return enumValue;
         }
      }

      return null;
   }

   default Object[] getArray(String text, Class clazz) {
      List list = ScriptContext.toList((ScriptObjectMirror)this.getObject(text));
      TypeAdapter typeAdapter = TypeRegistry.getTypeRegistry().getTypeAdapter(clazz);
      return list.stream().map(object -> typeAdapter.unpack((Map)object)).toArray();
   }

   Map<String, Object> asMap();

   default Optional getOptionalEnum(String text, Class clazz) {
      return Optional.ofNullable(this.getEnum(text, clazz));
   }

   default <T> void putSupplier(String text, Supplier<T> supplier) {
      this.put(text, supplier);
   }

   default BigDecimal getBigDecimal(String text) {
      return ValueUtils.createBigDecimal(this.getObject(text));
   }

   default void putObject(String text, Object object2, Class clazz) {
      if (object2 == null) {
         this.put(text, null);
      } else {
         TypeRegistry typeRegistry = TypeRegistry.getTypeRegistry();
         TypeAdapter typeAdapter = typeRegistry.getTypeAdapter(clazz);
         if (typeAdapter instanceof AbstractTypeAdapter abstractTypeAdapter) {
            ScriptType byName = typeRegistry.getTypeStore().getByName(abstractTypeAdapter.getTypeName());
            HashMap hashMap = new HashMap();

            for (ScriptType scriptType : byName.getSubTypes()) {
               TypeAdapter typeAdapter2 = typeRegistry.getTypeAdapter(scriptType.getClassValue());
               hashMap.putAll(typeAdapter2.pack(scriptType.getClassValue().cast(object2)));
            }

            this.put(text, hashMap);
         } else {
            this.put(text, typeAdapter.pack(object2));
         }
      }
   }

   default void putAll(ScriptValue scriptValue) {
      this.asMap().putAll(scriptValue.asMap());
   }

   default <T, U, V, W> void putConsumer4(String text, Contract4<T, U, V, W> contract4) {
      this.put(text, contract4);
   }

   default Object getJavaObject() {
      return this.getObject("_jObject");
   }

   default Object getUnpacked(String text, Class clazz) {
      return TypeRegistry.getTypeRegistry().getTypeAdapter(clazz).unpack(this.getScriptValue(text).asMap());
   }

   default int getInt(String text) {
      return this.getBigDecimal(text).intValue();
   }

   default <T, U> void putBiConsumer(String text, BiConsumer<T, U> biConsumer) {
      this.put(text, biConsumer);
   }

   default <T, U, V, W, X, Y, R> void putFunction6(String text, Contract8<T, U, V, W, X, Y, R> contract8) {
      this.put(text, contract8);
   }

   default ScriptValue getScriptValue(String text) {
      return createScriptValue((Map)this.getObject(text));
   }

   default <T, U, V, W, X, Y, Z> void putConsumer7(String text, Contract<T, U, V, W, X, Y, Z> contract) {
      this.put(text, contract);
   }

   default <T, U, V, R> void putFunction3(String text, Contract11<T, U, V, R> contract11) {
      this.put(text, contract11);
   }

   default <T, U, V, W, X, R> void putFunction5(String text, Contract12<T, U, V, W, X, R> contract12) {
      this.put(text, contract12);
   }

   static ScriptValue getScriptValue() {
      return new ScriptValue.Record(new UnifiedMap<>());
   }

   default <T, U, V, W, X, Y, Z, A> void putConsumer8(String text, Contract3<T, U, V, W, X, Y, Z, A> contract3) {
      this.put(text, contract3);
   }

   default Optional getOptionalScriptValue(String text) {
      return Optional.ofNullable(this.getScriptValue(text));
   }

   static ScriptValue createFromObject(Object object2) {
      Object object3 = object2;
      if (object3 instanceof Map) {
         return createScriptValue((Map)object3);
      } else {
         throw new IllegalArgumentException("object is not a map");
      }
   }

   default void putRunnable(String text, Runnable runnable) {
      this.put(text, runnable);
   }

   default <T, R> void putFunction(String text, Function<T, R> function2) {
      this.put(text, function2);
   }

   default <T, U, V> void putConsumer3(String text, Contract2<T, U, V> contract2) {
      this.put(text, contract2);
   }

   default byte getByte(String text) {
      return this.getBigDecimal(text).byteValue();
   }

   default double getDouble(String text) {
      return this.getBigDecimal(text).doubleValue();
   }

   default <T, U, V, W, X> void putConsumer5(String text, Contract6<T, U, V, W, X> contract6) {
      this.put(text, contract6);
   }

   default void putEnum(String text, Enum<?> enumValue) {
      String text2 = enumValue.name().toLowerCase(Locale.ROOT);
      this.put(text, text2);
   }

   default short getShort(String text) {
      return this.getBigDecimal(text).shortValue();
   }

   default Optional getOptional(String text) {
      Object object2 = this.getObject(text);
      return object2 == null ? Optional.empty() : Optional.of(ValueUtils.valueOf(object2));
   }

   default float getFloat(String text) {
      return this.getBigDecimal(text).floatValue();
   }

   default <T, U, V, W, X, Y, Z, A, B> void putConsumer9(String text, Contract10<T, U, V, W, X, Y, Z, A, B> contract10) {
      this.put(text, contract10);
   }

   default <T> void putArray(String text, T[] values2, Class<T> clazz) {
      TypeAdapter typeAdapter = TypeRegistry.getTypeRegistry().getTypeAdapter(clazz);
      this.put(text, Arrays.stream(values2).map(typeAdapter::pack).toArray());
   }

   default void handleObject(Object object2) {
      this.put("_jObject", object2);
   }

   default long getLong(String text) {
      return this.getBigDecimal(text).longValue();
   }

   Object getObject(String text);

   default <T, U, V, W, X, Y> void putConsumer6(String text, Contract9<T, U, V, W, X, Y> contract9) {
      this.put(text, contract9);
   }

   static ScriptValue createScriptValue(Map map2) {
      return map2 == null ? null : new ScriptValue.Record(map2);
   }

   default <T, U, R> void putBiFunction(String text, BiFunction<T, U, R> biFunction) {
      this.put(text, biFunction);
   }

   default boolean checkString(String text) {
      return ValueUtils.booleanValue(this.getObject(text));
   }

   boolean containsKey(String text);

   default <T> void putPredicate(String text, Predicate<T> predicate) {
      this.put(text, predicate);
   }

   default <T> void putConsumer(String text, Consumer<T> consumer) {
      this.put(text, consumer);
   }

   int size();

   default String getString(String text) {
      return ValueUtils.valueOf(this.getObject(text));
   }

   void put(String text, Object object2);

   default <T, U, V, W, X, Y, Z, A, B, C> void putConsumer10(String text, Contract5<T, U, V, W, X, Y, Z, A, B, C> contract5) {
      this.put(text, contract5);
   }

   default <T, U, V, W, R> void putFunction4(String text, Contract7<T, U, V, W, R> contract7) {
      this.put(text, contract7);
   }

   final class Helper {
      private static final String J_OBJECT = "_jObject";
      private static final StackWalker stackWalker = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
      private static final java.lang.invoke.MethodHandles.Lookup lookup = MethodHandles.lookup();
      private static final MutableMap<String, MethodHandle> mutableMap = Maps.mutable.empty();

      private static MethodHandle unreflectContract(String text, Class clazz) {
      try {
         Method method = ReflectionUtils.lookupContractMethod(clazz, text);
         return lookup.unreflect(method);
      } catch (Throwable sneakyThrowable) {
         throw SneakyThrow.sneak(sneakyThrowable);
      }
   }

      private static MethodHandle get(String text, Class<?> clazz) {
         return mutableMap.computeIfAbsent(text, string -> unreflectContract(text, clazz));
      }
   }

   final class Helper2 {
   }

   record Record(Map<String, Object> values) implements ScriptValue {
      @Override
      public int size() {
         return this.values.size();
      }

      @Override
      public void put(String text, Object object2) {
         this.values.put(text, object2);
      }

      @Override
      public Object getObject(String text) {
         return this.values.get(text);
      }

      @Override
      public boolean containsKey(String text) {
         return this.values.containsKey(text);
      }

      @Override
      public Map<String, Object> asMap() {
         return this.values;
      }

      public Map<String, Object> getValues() {
         return this.values;
      }
   }
}
