package src.summer.utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import src.summer.annotations.Param;
import src.summer.beans.SummerFile;
import src.summer.exception.SummerProcessException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public abstract class ParamUtil {
    /**
     * Return a list of the values of the args required by the {@code method}
     * that are contained in the {@code request} object.
     */
    public static List<Object> getMethodParameterValues( Method method, HttpServletRequest request )
            throws NoSuchFieldException, InvocationTargetException, InstantiationException, IllegalAccessException,
            NoSuchMethodException, ServletException, IOException {
        List<Object> values = new ArrayList<>();
        for ( Parameter parameter : method.getParameters() ) {
            if ( !parameter.isAnnotationPresent( Param.class ) )
                throw new SummerProcessException( "ETU2443 - Parameters must be annotated with \"@Param\"." );
            values.add( getParameterValue( parameter, request ) );
        }
        return values;
    }

    public static Object getParameterValue( Parameter parameter, HttpServletRequest request )
            throws NoSuchFieldException, InstantiationException, IllegalAccessException, NoSuchMethodException,
            InvocationTargetException, ServletException, IOException {
        Param annotation = parameter.getAnnotation( Param.class );

        if ( annotation == null )
            throw new SummerProcessException( "ETU2443 - Parameters must be annotated with \"@Param\"." );

        // todo: Sprint 12
        String paramName = annotation.name();
        if ( annotation.isFile() ) {
            return SummerFile.getFileFromRequest( request, paramName );
        }

        Object paramValue = request.getParameter( paramName );
        Class<?> paramType = parameter.getType();

        if ( paramValue != null ) {
            return TypeUtil.cast( paramValue, paramType );
        }
        paramValue = paramType.newInstance();
        for ( String fieldName : getFieldsNames( paramName, request ) ) {
            Field field = paramType.getDeclaredField( fieldName );
            Class<?> fieldType = field.getType();
            Object fieldValue = TypeUtil.cast( request.getParameter( paramName + "." + fieldName ), fieldType );

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
}
