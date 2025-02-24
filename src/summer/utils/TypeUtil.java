package src.summer.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

public abstract class TypeUtil {
    public static Class<?>[] getAllPrimitiveWrapperClasses() {
        return new Class[]{
                Integer.class, Long.class, Float.class, Double.class,
                Boolean.class, Byte.class, Short.class, Character.class
        };
    }

    public static boolean isPrimitive(Object o) {
        return Arrays.asList(getAllPrimitiveWrapperClasses()).contains(o);
    }

    public static Object cast(Object value, Class<?> clazz) {
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }

        Optional<String> stringValue = Optional.ofNullable((String) value).map(String::valueOf);
        switch (clazz.getSimpleName().toLowerCase()) {
            case "string":
                return stringValue.orElse(null);
            case "localdate":
                if (stringValue.isPresent()) {
                    String data = stringValue.get();
                    return data.isEmpty() ? null : LocalDate.parse(data);
                } else {
                    return null;
                }
            case "localdatetime":
                if (stringValue.isPresent()) {
                    String data = stringValue.get();
                    return data.isEmpty() ? null : LocalDateTime.parse(data);
                } else {
                    return null;
                }
            case "integer":
            case "int":
                String val = stringValue.orElse("0");
                val = val.isEmpty() ? "0" : val;
                return Integer.parseInt(val); // if null or empty => 0
            case "double":
            case "float":
                return Double.parseDouble(stringValue.orElse("0.0"));
            default:
                throw new IllegalArgumentException("Unsupported type: " + clazz);
        }
    }

    public static boolean isNumber(Class<?> clazz) {
        return Number.class.isAssignableFrom(clazz);
    }
}
