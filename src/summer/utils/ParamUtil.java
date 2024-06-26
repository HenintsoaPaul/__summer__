package src.summer.utils;

import jakarta.servlet.http.HttpServletRequest;
import src.summer.annotations.Param;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

public abstract class ParamUtil {
    public static List<Object> getMethodParameterValues( Method method, HttpServletRequest request )
            throws NoSuchFieldException, InvocationTargetException, InstantiationException, IllegalAccessException,
            NoSuchMethodException, ClassNotFoundException {
        List<Object> values = new ArrayList<>();
        for ( Parameter parameter : method.getParameters() ) {
            values.add( getParameterValue( parameter, request ) );
        }
        return values;
    }

    public static Object getParameterValue( Parameter parameter, HttpServletRequest request )
            throws NoSuchFieldException, InstantiationException, IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        Param annotation = parameter.getAnnotation( Param.class );
        String paramName = annotation == null ? parameter.getName() : annotation.name();
        Object paramValue = request.getParameter( paramName );
        Class<?> paramType = parameter.getType();

        if ( paramValue != null ) {
            return cast( paramValue, paramType );
        }
        paramValue = paramType.newInstance();
        for ( String fieldName : getFieldsNames( paramName, request ) ) {
            Field field = paramType.getDeclaredField( fieldName );
            Class<?> fieldType = field.getType();
            Object fieldValue = cast( request.getParameter( paramName + "." + fieldName ), fieldType );

            String setterName = "set" + fieldName.substring( 0, 1 ).toUpperCase() + fieldName.substring( 1 );
            Method setterMethod = paramType.getDeclaredMethod( setterName, fieldType );
            setterMethod.invoke( paramValue, fieldValue );
        }
        return paramValue;
    }

    private static List<String> getFieldsNames( String entityName, HttpServletRequest request ) {
        List<String> fields = new ArrayList<>();
        Enumeration<String> listParameterNames = request.getParameterNames();
        while ( listParameterNames.hasMoreElements() ) {
            String p = listParameterNames.nextElement();
            if ( p.contains( entityName ) ) {
                int dotIndex = p.lastIndexOf( '.' );
                fields.add( p.substring( dotIndex + 1 ) );
            }
        }
        return fields;
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
