package wtf.novoline.script;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractTypeAdapter<T> implements TypeAdapter<T>, TypeConverter {
   private final List<ScriptType> subTypes = new ArrayList<>();
   private final String typeName;
   protected final TypeRegistry typeRegistry;

   public List<ScriptType> getSubTypes() {
      return this.subTypes;
   }

   @Override
   public ScriptValue packDefault(Object object) {
      ScriptValue scriptValue = TypeAdapter.super.packDefault(object);
      TypeRegistry typeRegistry2 = TypeRegistry.getTypeRegistry();
      List subTypes2 = typeRegistry2.getTypeStore().getByName(this.typeName).getSubTypes();

      for (int i = 0; i < subTypes2.size() - 1; i++) {
         Class classValue = ((ScriptType)subTypes2.get(i)).getClassValue();
         typeRegistry2.<Object>getTypeAdapter(classValue).put(scriptValue, object);
      }

      return scriptValue;
   }

   public void registerSubAdapters() {
   }

   public TypeRegistry getTypeRegistry() {
      return this.typeRegistry;
   }

   public AbstractTypeAdapter(String text, TypeRegistry typeRegistry2) {
      this.typeName = text;
      this.typeRegistry = typeRegistry2;
   }

   public String getTypeName() {
      return this.typeName;
   }

   protected void addSubTypes(ScriptType... scriptTypes) {
      this.subTypes.addAll(Arrays.asList(scriptTypes));
   }
}
