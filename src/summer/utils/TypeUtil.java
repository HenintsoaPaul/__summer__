package src.summer.utils;

import java.time.LocalDate;
import java.util.Optional;

public abstract class TypeUtil {
    public static Object cast( Object value, Class<?> clazz ) {
        if ( clazz.isInstance( value ) ) {
            return clazz.cast( value );
        }

        Optional<String> stringValue = Optional.ofNullable( ( String ) value ).map( String::valueOf );
        return switch ( clazz.getSimpleName().toLowerCase() ) {
            case "string" -> stringValue.orElse( null );
            case "localdate" -> LocalDate.parse( stringValue.orElse( "" ) );
            case "integer", "int" -> Integer.valueOf( stringValue.orElse( "0" ) );
            case "double", "float" -> Double.valueOf( stringValue.orElse( "0.0" ) );
            default -> throw new IllegalArgumentException( "Unsupported type: " + clazz );
        };
    }
}
