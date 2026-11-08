package net.optifine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FieldLocatorTypes implements IFieldLocator {
    private Field field = null;

    public FieldLocatorTypes(Class cls, Class[] preTypes, Class type, Class[] postTypes, String errorName) {
        Field[] afield = cls.getDeclaredFields();
        List<Class> list = new ArrayList<>();

        for (int i = 0; i < afield.length; i++) {
            Field field = afield[i];
            list.add(field.getType());
        }

        List<Class> list1 = new ArrayList<>();
        list1.addAll(Arrays.asList(preTypes));
        list1.add(type);
        list1.addAll(Arrays.asList(postTypes));
        int l = Collections.indexOfSubList(list, list1);
        if (l < 0) {
            Config.warn("Field not found: " + errorName);
        } else {
            int j = Collections.indexOfSubList(list.subList(l + 1, list.size()), list1);
            if (j >= 0) {
                Config.warn("More than one match found for field: " + errorName);
            } else {
                int k = l + preTypes.length;
                this.field = afield[k];
            }
        }
    }

    @Override
    public Field getField() {
        return this.field;
    }
}
