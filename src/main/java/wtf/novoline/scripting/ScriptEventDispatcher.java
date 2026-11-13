package wtf.novoline.scripting;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;
import org.openjdk.nashorn.internal.runtime.Context;
import org.tinylog.Logger;
import wtf.novoline.Novoline;
import wtf.novoline.event.Event;
import wtf.novoline.event.PacketReceiveEvent;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeAdapter;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.ScriptFunctionUtils.Helper;
import wtf.novoline.scripting.ScriptEventHandler.HelperChild;
import wtf.novoline.support.SneakyThrow;

public class ScriptEventDispatcher {
   private static final java.lang.invoke.MethodHandles.Lookup lookup = MethodHandles.lookup();
   private static final MethodHandle methodHandle;

   public void dispatchEvent(ScriptEventHandler scriptEventHandler, Event event2) {
      TypeAdapter typeAdapter = TypeRegistry.getTypeRegistry().getTypeAdapter(event2.getClass());
      if (typeAdapter != null) {
         try {
            Helper.minecraftContext.doSync(false);
            Map map = typeAdapter.pack(event2);
            this.setGlobals(scriptEventHandler.getScriptObjectMirror());

            try {
               scriptEventHandler.getScriptObjectMirror().call(null, map);
            } catch (Throwable throwable) {
               Logger.error(throwable, "Failed event execution");
               ScriptErrorReporter.reportEventFailure(scriptEventHandler, throwable);
               scriptEventHandler.getModule().disable();
            }

            if (!(scriptEventHandler.getEventId() instanceof HelperChild helperChild && helperChild.getKlass() == PacketReceiveEvent.class)) {
               Helper.minecraftContext.doSync(true);
            }

            typeAdapter.apply(ScriptValue.createScriptValue(map), event2);
         } catch (Throwable throwable2) {
            Novoline.taggedLogger.error(throwable2, "Failed to execute event hooks");
         }
      }
   }

   static {
      try {
         Method declaredMethod = ScriptObjectMirror.class.getDeclaredMethod("inGlobal", Runnable.class);
         declaredMethod.setAccessible(true);
         methodHandle = lookup.unreflect(declaredMethod);
      } catch (NoSuchMethodException | IllegalAccessException reflectiveOperationException) {
         throw new RuntimeException(reflectiveOperationException);
      }
   }

   public void setGlobals(ScriptObjectMirror scriptObjectMirror) {
      this.runInGlobal(scriptObjectMirror, () -> Context.getGlobal().put("mc", Helper.minecraftContext.getScriptValue().asMap(), false));
   }

   private void runInGlobal(ScriptObjectMirror scriptObjectMirror, Runnable runnable) {
      try {
      try {
         methodHandle.bindTo(scriptObjectMirror).invokeWithArguments(runnable);
      } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException reflectiveOperationException) {
         throw new RuntimeException("Unexpected error during global operations", reflectiveOperationException);
      }
   } catch (Throwable sneakyThrowable) {
         throw SneakyThrow.sneak(sneakyThrowable);
      }
   }

   public void dispatchToggle(ScriptEventHandler scriptEventHandler) {
      Helper.minecraftContext.doSync(false);
      this.setGlobals(scriptEventHandler.getScriptObjectMirror());

      try {
         scriptEventHandler.getScriptObjectMirror().call(null);
      } catch (Throwable throwable) {
         Logger.error(throwable, "Failed event execution");
         ScriptErrorReporter.reportEventFailure(scriptEventHandler, throwable);
         scriptEventHandler.getModule().disable();
      }

      Helper.minecraftContext.doSync(true);
   }
}
