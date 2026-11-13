package wtf.novoline.script;

import java.util.List;

public class ScriptType {
   private String parentName;
   private final String name;
   private final Class<?> classValue;
   private List<ScriptType> subTypes;

   public void setParentName(String text) {
      this.parentName = text;
   }

   public static ScriptType createScriptType(String text, Class<?> clazz) {
      return new ScriptType(text, clazz);
   }

   public String getName() {
      return this.name;
   }

   public List<ScriptType> getSubTypes() {
      return this.subTypes;
   }

   public boolean isRoot() {
      return this.parentName == null;
   }

   public String getParentName() {
      return this.parentName;
   }

   public void setSubTypes(List<ScriptType> list) {
      this.subTypes = list;
   }

   private ScriptType(String text, Class<?> clazz) {
      this.name = text;
      this.classValue = clazz;
   }

   public Class<?> getClassValue() {
      return this.classValue;
   }
}
