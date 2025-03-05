package src.summer.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public abstract class TypeUtil {

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
        return Number.class.isAssignableFrom(getWrapperClass(clazz));
    }

    public static boolean isInteger(Class<?> clazz) {
        return Integer.class.isAssignableFrom(getWrapperClass(clazz));
    }

    // Convert primitive types to their corresponding wrapper classes
    private static Class<?> getWrapperClass(Class<?> primitiveType) {
        if (!primitiveType.isPrimitive()) {
            return primitiveType;
        }
        if (primitiveType == int.class) return Integer.class;
        if (primitiveType == double.class) return Double.class;
        if (primitiveType == long.class) return Long.class;
        if (primitiveType == float.class) return Float.class;
        if (primitiveType == short.class) return Short.class;
        if (primitiveType == byte.class) return Byte.class;
        if (primitiveType == boolean.class) return Boolean.class;
        if (primitiveType == char.class) return Character.class;
        return primitiveType;
    }
}
