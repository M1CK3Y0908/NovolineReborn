package net.optifine.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import net.minecraft.src.Config;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorClass;
import net.optifine.reflect.ReflectorField;

public class ChunkUtils {
   private static ReflectorClass reflectorClass = new ReflectorClass(Chunk.class);
   private static ReflectorField reflectorField = getReflectorField();
   private static ReflectorField reflectorField2 = new ReflectorField(reflectorClass, int[].class, 0);

   private static ReflectorField getReflectorField() {
      try {
         Chunk chunk2 = new Chunk((World)null, 0, 0);
         ArrayList arrayList = new ArrayList();
         ArrayList arrayList2 = new ArrayList();
         Field[] declaredFields = Chunk.class.getDeclaredFields();

         for (int i = 0; i < declaredFields.length; i++) {
            Field field = declaredFields[i];
            if (field.getType() == boolean.class) {
               field.setAccessible(true);
               arrayList.add(field);
               arrayList2.add(field.get(chunk2));
            }
         }

         chunk2.setHasEntities(false);
         ArrayList arrayList3 = new ArrayList();

         for (Field field2 : (Iterable<Field>) arrayList) {
            arrayList3.add(field2.get(chunk2));
         }

         chunk2.setHasEntities(true);
         ArrayList arrayList4 = new ArrayList();

         for (Field field3 : (Iterable<Field>) arrayList) {
            arrayList4.add(field3.get(chunk2));
         }

         ArrayList arrayList5 = new ArrayList();

         for (int i2 = 0; i2 < arrayList.size(); i2++) {
            Field field4 = (Field)arrayList.get(i2);
            Boolean bool = (Boolean)arrayList3.get(i2);
            Boolean bool2 = (Boolean)arrayList4.get(i2);
            if (!bool && bool2) {
               arrayList5.add(field4);
               Boolean bool3 = (Boolean)arrayList2.get(i2);
               field4.set(chunk2, bool3);
            }
         }

         if (arrayList5.size() == 1) {
            Field field5 = (Field)arrayList5.get(0);
            return new ReflectorField(field5);
         }
      } catch (Exception exception) {
         Config.warn(exception.getClass().getName() + " " + exception.getMessage());
      }

      Config.warn("Error finding Chunk.hasEntities");
      return new ReflectorField(new ReflectorClass(Chunk.class), "hasEntities");
   }

   public static boolean hasEntities(Chunk chunk) {
      return Reflector.checkObject3(chunk, reflectorField);
   }
}
