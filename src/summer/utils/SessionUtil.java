package src.summer.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import src.summer.beans.SummerSession;

import java.lang.reflect.Field;

public abstract class SessionUtil {
    private static boolean containsSummerSession( Class<?> controllerClass ) {
        for ( Field field : controllerClass.getDeclaredFields() ) {
            if ( field.getType().equals( SummerSession.class ) )
                return true;
        }
        return false;
    }

    private static void injectSession( Object controllerInstance, HttpSession session )
            throws IllegalAccessException {
        for ( Field field : controllerInstance.getClass().getDeclaredFields() ) {
            if ( field.getType().equals( SummerSession.class ) ) {
                field.setAccessible( true );
                field.set( controllerInstance, new SummerSession( session ) );
                return;
            }
        }
    }

    public static void injectSession(Class<?> ctlClass, Object ctlInstance, HttpServletRequest request)
            throws IllegalAccessException {
        if (SessionUtil.containsSummerSession(ctlClass)) {
            SessionUtil.injectSession(ctlInstance, request.getSession());
        }
    }
}
