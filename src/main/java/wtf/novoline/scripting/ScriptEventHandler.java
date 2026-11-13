package wtf.novoline.scripting;

import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;
import wtf.novoline.module.Module;

public class ScriptEventHandler {
   private final Script script;
   private final Module module;
   private final ScriptEventHandler.Helper eventId;
   private final int priority;
   private final ScriptObjectMirror scriptObjectMirror;

   public Module getModule() {
      return this.module;
   }

   public int getPriority() {
      return this.priority;
   }

   public ScriptEventHandler.Helper getEventId() {
      return this.eventId;
   }

   public ScriptEventHandler(Script script2, Module module2, ScriptEventHandler.Helper helper, int i, ScriptObjectMirror scriptObjectMirror2) {
      this.script = script2;
      this.module = module2;
      this.eventId = helper;
      this.priority = i;
      this.scriptObjectMirror = scriptObjectMirror2;
   }

   public ScriptObjectMirror getScriptObjectMirror() {
      return this.scriptObjectMirror;
   }

   public Script getScript() {
      return this.script;
   }

   public static class Helper {
   }

   public static final class HelperChild extends ScriptEventHandler.Helper {
      private final Class<?> klass;

      @Override
      public boolean equals(Object object) {
         if (object == this) {
            return true;
         } else if (!(object instanceof ScriptEventHandler.HelperChild helperChild)) {
            return false;
         } else {
            if (!helperChild.canEqual(this)) {
               return false;
            }

            Class klass2 = this.getKlass();
            Class klass3 = helperChild.getKlass();
            return klass2 == null ? klass3 == null : klass2.equals(klass3);
         }
      }

      private HelperChild(Class<?> clazz) {
         this.klass = clazz;
      }

      public static ScriptEventHandler.HelperChild forClass(Class<?> clazz) {
         return new ScriptEventHandler.HelperChild(clazz);
      }

      protected boolean canEqual(Object object) {
         return object instanceof ScriptEventHandler.HelperChild;
      }

      public Class<?> getKlass() {
         return this.klass;
      }

      @Override
      public int hashCode() {
         Class klass2 = this.getKlass();
         return 59 + (klass2 == null ? 43 : klass2.hashCode());
      }

      @Override
      public String toString() {
         return "EventListener.EventId.ClassId(klass=" + this.getKlass() + ")";
      }
   }

   public static final class HelperChild2 extends ScriptEventHandler.Helper {
      public static final ScriptEventHandler.HelperChild2 ENABLE = forType(ScriptEventHandler.ToggleType.ENABLE);
      public static final ScriptEventHandler.HelperChild2 DISABLE = forType(ScriptEventHandler.ToggleType.DISABLE);
      private final ScriptEventHandler.ToggleType type;
      public static final ScriptEventHandler.HelperChild2 TOGGLE = forType(ScriptEventHandler.ToggleType.TOGGLE);

      @Override
      public boolean equals(Object object) {
         if (object == this) {
            return true;
         } else if (!(object instanceof ScriptEventHandler.HelperChild2 helperChild2)) {
            return false;
         } else {
            if (!helperChild2.canEqual(this)) {
               return false;
            }

            ScriptEventHandler.ToggleType type2 = this.getType();
            ScriptEventHandler.ToggleType type3 = helperChild2.getType();
            return type2 == null ? type3 == null : type2.equals(type3);
         }
      }

      @Override
      public String toString() {
         return "EventListener.EventId.TypeId(type=" + this.getType() + ")";
      }

      protected boolean canEqual(Object object) {
         return object instanceof ScriptEventHandler.HelperChild2;
      }

      public static ScriptEventHandler.HelperChild2 forType(ScriptEventHandler.ToggleType toggleType) {
         return new ScriptEventHandler.HelperChild2(toggleType);
      }

      @Override
      public int hashCode() {
         ScriptEventHandler.ToggleType type2 = this.getType();
         return 59 + (type2 == null ? 43 : type2.hashCode());
      }

      public ScriptEventHandler.ToggleType getType() {
         return this.type;
      }

      private HelperChild2(ScriptEventHandler.ToggleType toggleType) {
         this.type = toggleType;
      }
   }

   public enum ToggleType {
      TOGGLE,
      ENABLE,
      DISABLE;
   }
}
