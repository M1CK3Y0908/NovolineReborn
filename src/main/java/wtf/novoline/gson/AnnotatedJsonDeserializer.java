package wtf.novoline.gson;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import wtf.novoline.api.GroupName;
import wtf.novoline.api.annotation.NoGroup;
import wtf.novoline.api.annotation.Property;
import wtf.novoline.support.SneakyThrow;

public class AnnotatedJsonDeserializer<T> implements JsonDeserializer<T> {
   private static final Gson gson = new Gson();

   @Override
   public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      try {
      JsonObject asJsonObject = jsonElement.getAsJsonObject();
      Object object = gson.fromJson(asJsonObject, type);

      for (Field field2 : object.getClass().getDeclaredFields()) {
         if (field2.isAnnotationPresent(NoGroup.class)) {
            NoGroup annotation2 = field2.getAnnotation(NoGroup.class);
            Object object2 = field2.getType().getConstructor().newInstance();

            for (Field field3 : object2.getClass().getDeclaredFields()) {
               field3.setAccessible(true);
               if (field3.isAnnotationPresent(GroupName.class)) {
                  field3.set(object2, annotation2.value());
               } else if (field3.isAnnotationPresent(SerializedName.class)) {
                  String value2 = field3.getAnnotation(SerializedName.class).value();

                  value2 = switch (annotation2.beforeKeyType()) {
                     case BEFORE_KEY -> annotation2.value() + "_" + value2;
                     case AFTER_KEY -> value2 + "_" + annotation2.value();
                     case NO_GROUP -> value2;
                  };
                  if (asJsonObject.has(value2)) {
                     field3.set(object2, this.fromJson(asJsonObject.get(value2), field3.getType()));
                  }
               }
            }

            field2.setAccessible(true);
            field2.set(object, object2);
         } else if (Arrays.stream(field2.getType().getDeclaredFields()).anyMatch(field -> field.isAnnotationPresent(Property.class))) {
            Object object3 = field2.getType().getConstructor().newInstance();

            for (Field field4 : field2.getType().getDeclaredFields()) {
               if (field4.isAnnotationPresent(Property.class)) {
                  Property annotation3 = field4.getAnnotation(Property.class);
                  if (asJsonObject.has(annotation3.value())) {
                     field4.setAccessible(true);
                     field4.set(object3, this.fromJson(asJsonObject.get(annotation3.value()), field4.getType()));
                  }
               }
            }

            field2.setAccessible(true);
            field2.set(object, object3);
         }
      }

      return (T)object;
   } catch (Throwable sneakyThrowable) {
         throw SneakyThrow.sneak(sneakyThrowable);
      }
   }

   private Object fromJson(JsonElement jsonElement, Class<?> clazz) {
      return gson.fromJson(jsonElement, clazz);
   }
}
