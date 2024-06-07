package net.risedata.register.utils;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description : 比对类
 * @ClassName CompareUtil
 * @Author lb
 * @Date 2022/7/1 15:21
 * @Version 1.0
 */
public class CompareUtil<T> {
    private Field[] fields;

    public CompareUtil(Class<T> compareClass) {
        Field[] fields2 = compareClass.getDeclaredFields();

        List<Field> fieldList = new ArrayList<>();
        for (Field field : fields2) {
            if (Modifier.isStatic(field.getModifiers())) {

                continue;
            }
            field.setAccessible(true);
            fieldList.add(field);
        }
        this.fields = fieldList.toArray(new Field[fieldList.size()]);
    }

    /**
     * 将2个对象比对前者比对后者如果值有改变则返回true
     *
     * @param t1
     * @param t2
     * @return
     */
    public boolean compare(T t1, T t2) {
        Object temp;
        Object temp2;
        try {
            for (Field field : this.fields) {
                temp = field.get(t1);
                temp2 = field.get(t2);
                if (temp == null && temp2 != null) {
                    return true;
                } else if (temp2 == null && temp != null) {
                    return true;
                }
                if (!temp.toString().equals(temp2.toString())) {
                    return true;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return false;
    }

    /**
     * 将t1的值改变成newT的值
     *
     * @param t1
     * @param newT
     */
    public void toValue(T t1, T newT) {
        Object temp2;
        try {
            for (Field field : this.fields) {
                temp2 = field.get(newT);

                field.set(t1, temp2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
