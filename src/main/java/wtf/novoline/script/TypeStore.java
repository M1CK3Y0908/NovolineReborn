package wtf.novoline.script;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TypeStore extends ArrayList<ScriptType> {
   public boolean add(ScriptType scriptType2) {
      scriptType2.setSubTypes(this.collectAncestors(scriptType2, new ArrayList()));
      return super.add(scriptType2);
   }

   public ScriptType getByName(String text) {
      return this.stream().filter(scriptType -> scriptType.getName().equals(text)).findFirst().orElse(null);
   }

   private TypeStore() {
   }

   public List collectAncestors(ScriptType scriptType2, List list) {
      list.add(scriptType2);
      if (scriptType2.isRoot()) {
         Collections.reverse(list);
         return list;
      }

      String parentName = scriptType2.getParentName();
      if (scriptType2.getName().equals(parentName)) {
         throw new IllegalArgumentException("Cannot inherit itself");
      }

      if (!this.isRegistered(parentName)) {
         throw new IllegalArgumentException("Super cast \"" + parentName + "\" is not registered, please check the addition order");
      }

      ScriptType byName = this.getByName(parentName);
      return this.collectAncestors(byName, list);
   }

   public static TypeStore getTypeStore() {
      return new TypeStore();
   }

   public void addTypes(ScriptType... scriptTypes) {
      Arrays.stream(scriptTypes).forEach(this::add);
   }

   public boolean isRegistered(String text) {
      return this.stream().anyMatch(scriptType -> scriptType.getName().equals(text));
   }
}
