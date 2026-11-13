package wtf.novoline.script;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import wtf.novoline.script.adapter.MinecraftTypeAdapter;
import wtf.novoline.utils.Functions.Contract11;
import wtf.novoline.utils.Functions.Contract7;

public final class ScriptFunctionUtils {
   public static <T, U> Consumer<U> consumer(ScriptFunctionUtils.Component<T> component, Consumer<U> consumer2) {
      return object2 -> {
         component.doSync(true);
         consumer2.accept(object2);
         component.doSync(false);
      };
   }

   public static <T> Runnable runnable(ScriptFunctionUtils.Component<T> component, Runnable runnable2) {
      return () -> {
         component.doSync(true);
         runnable2.run();
         component.doSync(false);
      };
   }

   public static <T, R> Supplier<R> supplier(ScriptFunctionUtils.Component<T> component, Supplier<R> supplier2) {
      return () -> {
         component.doSync(true);
         Object object6 = supplier2.get();
         component.doSync(false);
         return (R)object6;
      };
   }

   public static <S, T, U, R> BiFunction<T, U, R> biFunction(ScriptFunctionUtils.Component<S> component, BiFunction<T, U, R> biFunction2) {
      return (object2, object3) -> {
         component.doSync(true);
         Object object6 = biFunction2.apply(object2, object3);
         component.doSync(false);
         return (R)object6;
      };
   }

   public static <S, T, U, V, W, R> Contract7<T, U, V, W, R> quadFunction(ScriptFunctionUtils.Component<S> component, Contract7<T, U, V, W, R> contract7) {
      return (object2, object3, object4, object5) -> {
         component.doSync(true);
         Object object6 = contract7.apply(object2, object3, object4, object5);
         component.doSync(false);
         return (R)object6;
      };
   }

   public static <S, T, U, V, R> Contract11<T, U, V, R> triFunction(ScriptFunctionUtils.Component<S> component, Contract11<T, U, V, R> contract11) {
      return (object2, object3, object4) -> {
         component.doSync(true);
         Object object6 = contract11.apply(object2, object3, object4);
         component.doSync(false);
         return (R)object6;
      };
   }

   public static <S, T, R> Function<T, R> function(ScriptFunctionUtils.Component<S> component, Function<T, R> function2) {
      return object2 -> {
         component.doSync(true);
         Object object6 = function2.apply(object2);
         component.doSync(false);
         return (R)object6;
      };
   }

   public static final class Component<T> {
      private final TypeAdapter<T> typeAdapter;
      private final ScriptValue scriptValue;
      private final T object;

      public ScriptValue getScriptValue() {
         return this.scriptValue;
      }

      public void doSync(boolean flag) {
         if (this.getTypeAdapter() instanceof AbstractTypeAdapter abstractTypeAdapter) {
            TypeRegistry typeRegistry = TypeRegistry.getTypeRegistry();
            ScriptType byName = typeRegistry.getTypeStore().getByName(abstractTypeAdapter.getTypeName());
            if (byName != null) {
               byName.getSubTypes().stream().map(scriptType -> typeRegistry.getTypeAdapter(scriptType.getClassValue())).forEach(typeAdapter2 -> this.sync(typeAdapter2, flag));
            }
         } else {
            this.sync(this.getTypeAdapter(), flag);
         }
      }

      private Component(TypeAdapter<T> typeAdapter3, ScriptValue scriptValue2, T value) {
         this.typeAdapter = typeAdapter3;
         this.scriptValue = scriptValue2;
         this.object = (T)value;
      }

      public TypeAdapter<T> getTypeAdapter() {
         return this.typeAdapter;
      }

      public static <T> ScriptFunctionUtils.Component<T> createComponent(TypeAdapter<T> typeAdapter3, ScriptValue scriptValue2, T value) {
         return new ScriptFunctionUtils.Component<>(typeAdapter3, scriptValue2, (T)value);
      }

      public T getObject() {
         return this.object;
      }

      public synchronized void sync(TypeAdapter typeAdapter3, boolean flag) {
         if (flag) {
            if (!(typeAdapter3 instanceof MinecraftTypeAdapter)) {
               ScriptFunctionUtils.Helper.minecraftContext
                  .getTypeAdapter()
                  .apply(ScriptFunctionUtils.Helper.minecraftContext.getScriptValue(), ScriptFunctionUtils.Helper.minecraftContext.getObject());
            }

            typeAdapter3.apply(this.getScriptValue(), this.getObject());
         } else {
            if (!(typeAdapter3 instanceof MinecraftTypeAdapter)) {
               ScriptFunctionUtils.Helper.minecraftContext
                  .getTypeAdapter()
                  .put(ScriptFunctionUtils.Helper.minecraftContext.getScriptValue(), ScriptFunctionUtils.Helper.minecraftContext.getObject());
            }

            typeAdapter3.put(this.getScriptValue(), this.getObject());
         }
      }
   }

   public static final class Helper {
      public static final ScriptValue scriptValue = ScriptValue.getScriptValue();
      public static final ScriptFunctionUtils.Component<Minecraft> minecraftContext = ScriptFunctionUtils.Component.createComponent(
         TypeRegistry.getTypeRegistry().getTypeAdapter(Minecraft.class), scriptValue, Minecraft.getMinecraft()
      );
   }
}
