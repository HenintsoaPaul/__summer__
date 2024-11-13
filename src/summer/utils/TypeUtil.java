package src.summer.utils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

public abstract class TypeUtil {
    public static Class<?>[] getAllPrimitiveWrapperClasses() {
        return new Class[]{
                Integer.class, Long.class, Float.class, Double.class,
                Boolean.class, Byte.class, Short.class, Character.class
        };
    }

    public static boolean isPrimitive( Object o ) {
        return Arrays.stream( getAllPrimitiveWrapperClasses() ).anyMatch( o::equals );
    }

    public static Object cast( Object value, Class<?> clazz ) {
        if ( clazz.isInstance( value ) ) {
            return clazz.cast( value );
        }

        Optional<String> stringValue = Optional.ofNullable( ( String ) value ).map( String::valueOf );
        switch ( clazz.getSimpleName().toLowerCase() ) {
            case "string":
                return stringValue.orElse( null );
            case "localdate":
                return LocalDate.parse( stringValue.orElse( "" ) );
            case "integer":
            case "int":
                return Integer.valueOf( stringValue.orElse( "0" ) );
            case "double":
            case "float":
                return Double.valueOf( stringValue.orElse( "0.0" ) );
            default:
                throw new IllegalArgumentException( "Unsupported type: " + clazz );
        }
    }
}
