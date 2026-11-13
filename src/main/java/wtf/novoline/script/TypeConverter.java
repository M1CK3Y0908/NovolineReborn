package wtf.novoline.script;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public interface TypeConverter {
   long UNUSED_LONG_CONSTANT = 131650487246392L;

   default Function<String, Boolean> is(Object object) {
      return string -> this.isInstanceOf(object, string);
   }

   default Map packAsType(Object object, String text) {
      TypeRegistry typeRegistry = TypeRegistry.getTypeRegistry();
      ScriptType byName = typeRegistry.getTypeStore().getByName(text);
      HashMap hashMap = new HashMap();

      for (ScriptType scriptType : byName.getSubTypes()) {
         TypeAdapter typeAdapter = typeRegistry.getTypeAdapter(scriptType.getClassValue());
         hashMap.putAll(typeAdapter.pack(scriptType.getClassValue().cast(object)));
      }

      return hashMap;
   }

   default Function<String, Map<String, Object>> as(Object object) {
      return string -> this.packAsType(object, string);
   }

   default boolean isInstanceOf(Object object, String text) {
      return TypeRegistry.getTypeRegistry().getTypeStore().getByName(text).getClassValue().isInstance(object);
   }
}
