package src.summer.utils;

import jakarta.servlet.http.HttpServletRequest;
import src.summer.annotations.Param;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class ParamUtil {
    public static List<Object> getMethodParameterValues( Method method, HttpServletRequest request ) {
        List<Object> values = new ArrayList<>();
        for ( Parameter parameter : method.getParameters() ) values.add( getParameterValue( parameter, request ) );
        return values;
    }

    public static Object getParameterValue( Parameter parameter, HttpServletRequest request ) {
        Object value = request.getParameter( parameter.getName() );
        if ( value == null ) {
            value = request.getParameter( parameter.getAnnotation( Param.class ).name() );
        }

        value = cast( value, parameter.getType() );
        return value;
    }

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
