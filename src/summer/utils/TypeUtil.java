package src.summer.utils;

import java.time.LocalDate;
import java.util.Optional;

public abstract class TypeUtil {
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
