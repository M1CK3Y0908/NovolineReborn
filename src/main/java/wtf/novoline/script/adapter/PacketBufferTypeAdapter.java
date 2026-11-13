package wtf.novoline.script.adapter;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;
import org.objectweb.asm.Type;
import org.tinylog.Logger;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.support.SneakyThrow;

public final class PacketBufferTypeAdapter extends AbstractTypeAdapter<PacketBuffer> {
   private static final MutableMap<String, MethodHandle> mutableMap = Maps.mutable.empty();
   private static final java.lang.invoke.MethodHandles.Lookup lookup = MethodHandles.lookup();

   private static MethodHandle createLambdaHandle(Method method3) throws IllegalAccessException, LambdaConversionException {
      Class declaringClass = method3.getDeclaringClass();
      MethodHandle methodHandle = lookup.unreflect(method3);

      CallSite callSite = switch (method3.getParameterCount()) {
         case 0 -> method3.getReturnType() == void.class
            ? LambdaMetafactory.metafactory(
               lookup, "run", MethodType.methodType(Runnable.class, declaringClass), MethodType.methodType(void.class), methodHandle, MethodType.methodType(void.class)
            )
            : LambdaMetafactory.metafactory(
               lookup,
               "get",
               MethodType.methodType(Supplier.class, declaringClass),
               MethodType.methodType(C17PacketCustomPayloadTypeAdapter.eraseReferenceType(method3.getReturnType())),
               methodHandle,
               MethodType.methodType(method3.getReturnType())
            );
         case 1 -> {
            Class clazz = method3.getParameterTypes()[0];
            yield method3.getReturnType() == void.class
               ? LambdaMetafactory.metafactory(
                  lookup,
                  "accept",
                  MethodType.methodType(Consumer.class, declaringClass),
                  MethodType.methodType(void.class, C17PacketCustomPayloadTypeAdapter.eraseReferenceType(clazz)),
                  methodHandle,
                  MethodType.methodType(void.class, clazz)
               )
               : LambdaMetafactory.metafactory(
                  lookup,
                  "apply",
                  MethodType.methodType(Function.class, declaringClass),
                  MethodType.methodType(
                     C17PacketCustomPayloadTypeAdapter.eraseReferenceType(method3.getReturnType()), C17PacketCustomPayloadTypeAdapter.eraseReferenceType(clazz)
                  ),
                  methodHandle,
                  MethodType.methodType(method3.getReturnType(), clazz)
               );
         }
         default -> null;
      };
      return callSite != null ? callSite.getTarget() : null;
   }

   static {
      for (Method method3 : PacketBuffer.class.getMethods()) {
         if (method3.getDeclaringClass() != Object.class && !Modifier.isStatic(method3.getModifiers()) && isOverridingMethod(method3)) {
            String name = method3.getName();
            if (!name.equals("toString") && !name.equals("hashCode") && !name.equals("equals")) {
               try {
                  MethodHandle lambdaHandle = createLambdaHandle(method3);
                  String text = name;
                  if (lambdaHandle != null) {
                     if (mutableMap.containsKey(name)) {
                        long l = mutableMap.keySet().stream().filter(string -> string.startsWith(name + "$")).count() + 1L;
                        text = text + "$" + l;
                     }

                     mutableMap.put(text, lambdaHandle);
                  }
               } catch (IllegalAccessException | LambdaConversionException exception) {
                  Logger.error(exception, "Unable to create lambda for method {}({})", method3.getName(), Type.getMethodDescriptor(method3));
               }
            }
         }
      }
   }

   private static boolean isOverridingMethod(Method method3) {
      return isMethodOverridden(method3, method3.getDeclaringClass().getSuperclass());
   }

   public PacketBufferTypeAdapter(TypeRegistry typeRegistry) {
      super("PacketBuffer", typeRegistry);
   }

   public PacketBuffer unpack(Map map2) {
      return (PacketBuffer)ScriptValue.createScriptValue(map2).getJavaObject();
   }

   private static boolean isMethodOverridden(Method method3, Class clazz) {
      if (!Arrays.stream(clazz.getDeclaredMethods())
         .anyMatch(
            method2 -> method2.getName().equals(method3.getName())
               && method2.getParameterCount() == method3.getParameterCount()
               && Arrays.equals(method2.getParameterTypes(), method3.getParameterTypes())
         )) {
         return clazz == Object.class ? false : isMethodOverridden(method3, clazz.getSuperclass());
      } else {
         return true;
      }
   }

   public ScriptValue put(ScriptValue scriptValue, PacketBuffer packetBuffer) {
      try {
      for (java.util.Map.Entry entry : mutableMap.entrySet()) {
         scriptValue.put((String)entry.getKey(), (Object)((MethodHandle)entry.getValue()).bindTo(packetBuffer).invoke());
      }

      return scriptValue;
   } catch (Throwable sneakyThrowable) {
         throw SneakyThrow.sneak(sneakyThrowable);
      }
   }
}
