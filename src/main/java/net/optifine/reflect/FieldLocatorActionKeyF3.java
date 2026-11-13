package net.optifine.reflect;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;

public class FieldLocatorActionKeyF3 implements IFieldLocator {
   @Override
   public Field getField2() {
      Class<Minecraft> classValue = Minecraft.class;
      Field field2 = this.getField();
      if (field2 == null) {
         Config.log("(Reflector) Field not present: " + classValue.getName() + ".actionKeyF3 (field renderChunksMany not found)");
         return null;
      } else {
         Field field3 = ReflectorRaw.createField(Minecraft.class, field2, boolean.class);
         if (field3 == null) {
            Config.log("(Reflector) Field not present: " + classValue.getName() + ".actionKeyF3");
            return null;
         } else {
            return field3;
         }
      }
   }

   private Field getField() {
      Minecraft minecraft2 = Minecraft.getMinecraft();
      boolean renderChunksMany2 = minecraft2.renderChunksMany;
      Field[] declaredFields = Minecraft.class.getDeclaredFields();
      minecraft2.renderChunksMany = true;
      Field[] array = ReflectorRaw.createArray3(minecraft2, declaredFields, boolean.class, Boolean.TRUE);
      minecraft2.renderChunksMany = false;
      Field[] array2 = ReflectorRaw.createArray3(minecraft2, declaredFields, boolean.class, Boolean.FALSE);
      minecraft2.renderChunksMany = renderChunksMany2;
      HashSet hashSet = new HashSet<>(Arrays.asList(array));
      HashSet hashSet2 = new HashSet<>(Arrays.asList(array2));
      HashSet hashSet3 = new HashSet(hashSet);
      hashSet3.retainAll(hashSet2);
      Field[] fields = (Field[]) hashSet3.toArray(new Field[hashSet3.size()]);
      return fields.length != 1 ? null : fields[0];
   }
}
