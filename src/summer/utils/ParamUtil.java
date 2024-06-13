package src.summer.utils;

import jakarta.servlet.http.HttpServletRequest;
import src.summer.annotations.Param;

import java.lang.reflect.Parameter;

public abstract class ParamUtil {
    public static String getParameterValue( Parameter parameter, HttpServletRequest request ) {
        String value = request.getParameter( parameter.getName() );
        if ( value == null ) {
            value = request.getParameter( parameter.getAnnotation( Param.class ).name() );
        }
        return value;
    }
}
