package net.optifine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.chunk.Chunk;

public class ChunkUtils {
    private static Field fieldHasEntities = null;
    private static boolean fieldHasEntitiesMissing = false;

    public static boolean hasEntities(Chunk chunk) {
        if (fieldHasEntities == null) {
            if (fieldHasEntitiesMissing) {
                return true;
            }

            fieldHasEntities = findFieldHasEntities(chunk);
            if (fieldHasEntities == null) {
                fieldHasEntitiesMissing = true;
                return true;
            }
        }

        try {
            return fieldHasEntities.getBoolean(chunk);
        } catch (Exception exception) {
            Config.warn("Error calling Chunk.hasEntities");
            Config.warn(exception.getClass().getName() + " " + exception.getMessage());
            fieldHasEntitiesMissing = true;
            return true;
        }
    }

    private static Field findFieldHasEntities(Chunk chunk) {
        try {
            List list = new ArrayList();
            List list1 = new ArrayList();
            Field[] afield = Chunk.class.getDeclaredFields();

            for (int i = 0; i < afield.length; i++) {
                Field field = afield[i];
                if (field.getType() == boolean.class) {
                    field.setAccessible(true);
                    list.add(field);
                    list1.add(field.get(chunk));
                }
            }

            chunk.setHasEntities(false);
            List list2 = new ArrayList();

            for (Field field1 : (Iterable<Field>) (Iterable) list) {
                list2.add(field1.get(chunk));
            }

            chunk.setHasEntities(true);
            List list3 = new ArrayList();

            for (Field field2 : (Iterable<Field>) (Iterable) list) {
                list3.add(field2.get(chunk));
            }

            List list4 = new ArrayList();

            for (int j = 0; j < list.size(); j++) {
                Field field3 = (Field)list.get(j);
                Boolean obool = (Boolean)list2.get(j);
                Boolean obool1 = (Boolean)list3.get(j);
                if (!obool && obool1) {
                    list4.add(field3);
                    Boolean obool2 = (Boolean)list1.get(j);
                    field3.set(chunk, obool2);
                }
            }

            if (list4.size() == 1) {
                return (Field)list4.get(0);
            }
        } catch (Exception exception) {
            Config.warn(exception.getClass().getName() + " " + exception.getMessage());
        }

        Config.warn("Error finding Chunk.hasEntities");
        return null;
    }
}
