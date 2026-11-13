package net.minecraft.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class EnumTypeAdapterFactory implements TypeAdapterFactory {
   private String getName(Object value) {
      return value instanceof Enum ? ((Enum)value).name().toLowerCase(Locale.US) : value.toString().toLowerCase(Locale.US);
   }

   @Override
   public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
      Class<? super T> t = typeToken.getRawType();
      if (!t.isEnum()) {
         return null;
      }

      final HashMap hashMap = Maps.newHashMap();

      for (Object object : t.getEnumConstants()) {
         hashMap.put(this.getName(object), object);
      }

      return new TypeAdapter<T>() {
         @Override
         public T read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
               reader.nextNull();
               return null;
            } else {
               return (T)hashMap.get(reader.nextString());
            }
         }

         @Override
         public void write(JsonWriter writer, T value) throws IOException {
            if (value == null) {
               writer.nullValue();
            } else {
               writer.value(EnumTypeAdapterFactory.this.getName(value));
            }
         }
      };
   }
}
