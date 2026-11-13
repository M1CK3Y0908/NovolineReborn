package wtf.novoline.script;

import java.util.HashMap;
import java.util.Map;

public interface TypeAdapter<T> {
   long UNUSED_LONG_CONSTANT = 116343857568364L;
   Map UNUSED_MAP = new HashMap(13);
   String[] stringArray = new String[]{
      "cê*j\u0081\u000eÎ\u0084",
      "ØµË\u009f2\u0015\u008eÍµ\u0090³Úê+}Æ",
      "_ë\té\u0007\u0092\u0089à",
      "À]áEÍÜ3ç\u008fÕ.<)«\u008dÏ",
      "9Í¦É%ô¦N",
      "Ty\u0099\u0099J\u0093-\u007f",
      "Ì\u0000>\u0083A_Ür"
   };
   String[] stringArray2 = new String[7];

   default ScriptValue put(ScriptValue scriptValue, T value) {
      return ScriptValue.getScriptValue();
   }

   default Map packWithSubTypes(Object object) {
      ScriptValue scriptValue = ScriptValue.getScriptValue();
      if (this instanceof AbstractTypeAdapter abstractTypeAdapter) {
         TypeRegistry typeRegistry = TypeRegistry.getTypeRegistry();

         for (ScriptType scriptType : typeRegistry.getTypeStore().getByName(abstractTypeAdapter.getTypeName()).getSubTypes()) {
            ((TypeAdapter<Object>)typeRegistry.getTypeAdapter(scriptType.getClassValue())).put(scriptValue, object);
         }
      }

      return scriptValue.asMap();
   }

   default T unpack(Map<String, Object> map) {
      ScriptValue scriptValue = ScriptValue.createScriptValue(map);
      return (T)this.apply(scriptValue, (T)scriptValue.getJavaObject());
   }

   default Object applyWithSubTypes(ScriptValue scriptValue, Object object) {
      if (scriptValue == null) {
         return object;
      }

      if (this instanceof AbstractTypeAdapter abstractTypeAdapter) {
         TypeRegistry typeRegistry = TypeRegistry.getTypeRegistry();

         for (ScriptType scriptType : typeRegistry.getTypeStore().getByName(abstractTypeAdapter.getTypeName()).getSubTypes()) {
            ((TypeAdapter<Object>)typeRegistry.getTypeAdapter(scriptType.getClassValue())).apply(scriptValue, object);
         }
      }

      return object;
   }

   default Object apply(ScriptValue scriptValue, T value) {
      return value;
   }

   default ScriptValue packDefault(Object object) {
      ScriptValue scriptValue = ScriptValue.getScriptValue();
      scriptValue.handleObject(object);
      scriptValue.putRunnable("update", () -> this.put(scriptValue, (T)object));
      scriptValue.putRunnable("commit", () -> this.apply(scriptValue, (T)object));
      if (this instanceof TypeConverter typeConverter) {
         scriptValue.put("as", typeConverter.as(object));
         scriptValue.put("is", typeConverter.is(object));
      }

      scriptValue.putFunction("equals", MappingUtils.m2oMappingFunction(object.getClass(), object::equals));
      scriptValue.putSupplier("hashCode", object::hashCode);
      scriptValue.putSupplier("toString", object::toString);
      return scriptValue;
   }

   default Map<String, Object> pack(T value) {
      return this.put(this.packDefault(value), (T)value).asMap();
   }
}
