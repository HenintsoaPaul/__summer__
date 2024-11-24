package src.summer.utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import src.summer.annotations.Param;
import src.summer.annotations.Validate;
import src.summer.beans.SummerFile;
import src.summer.beans.validation.ValidationLog;
import src.summer.exception.SummerProcessException;
import src.summer.utils.constraint.ConstraintValidatorUtil;

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
        ValidationLog validationLog = new ValidationLog();
        List<Object> values = new ArrayList<>();
        boolean validate = false;
        for ( Parameter parameter : method.getParameters() ) {
            if ( !parameter.getType().equals( validationLog.getClass() ) ) {
                if ( !parameter.isAnnotationPresent( Param.class ) ) {
                    throw new SummerProcessException( "ETU2443 - Parameters must be annotated with \"@Param\"." );
                }
                validate = parameter.isAnnotationPresent( Validate.class );
                values.add( getParameterValue( parameter, request, validationLog, validate ) );
            } else {
                if ( validate ) {
                    values.add( validationLog );
                }
            }
        }
        return values;
    }

    public static Object getParameterValue( Parameter parameter, HttpServletRequest request,
                                            ValidationLog validationLog, Boolean validate )
            throws NoSuchFieldException, InstantiationException, IllegalAccessException, NoSuchMethodException,
            InvocationTargetException, ServletException, IOException {
        Param annotation = parameter.getAnnotation( Param.class );
        String paramName = annotation.name();

        if ( annotation.isFile() ) {
            return SummerFile.getFileFromRequest( request, paramName );
        }

        Object paramValue = request.getParameter( paramName );
        Class<?> paramClass = parameter.getType();
        // Dans le cas ou {@code parameter} a ete directement depuis le HttpRequest
        if ( paramValue != null ) {
            return TypeUtil.cast( paramValue, paramClass );
        }

        // Dans le cas ou {@code parameter} est un objet, dont les fields ont ete envoyes dans un formulaire
        paramValue = paramClass.newInstance();
        for ( String fieldName : getFieldsNames( paramName, request ) ) {
            Field field = paramClass.getDeclaredField( fieldName );
            Class<?> fieldType = field.getType();
            Object fieldValue = TypeUtil.cast( request.getParameter( paramName + "." + fieldName ), fieldType );

            // TODO: validate field...
            if ( validate ) ConstraintValidatorUtil.validateField( validationLog, field, fieldValue );
            // ...

            String setterName = "set" + fieldName.substring( 0, 1 ).toUpperCase() + fieldName.substring( 1 );
            Method setterMethod = paramClass.getDeclaredMethod( setterName, fieldType );
            setterMethod.invoke( paramValue, fieldValue );
        }
        if ( validate ) {
            validationLog.setLastInput( paramValue );
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
