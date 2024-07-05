package src.summer.utils;

import jakarta.servlet.http.HttpSession;
import src.summer.beans.SummerSession;

import java.lang.reflect.Field;

public abstract class SessionUtil {
    public static boolean containsSummerSession( Class<?> controllerClass ) {
        for ( Field field : controllerClass.getDeclaredFields() ) {
            if ( field.getType().equals( SummerSession.class ) )
                return true;
        }
        return false;
    }

    public static void injectSession( Object controllerInstance, HttpSession session )
            throws IllegalAccessException {
        for ( Field field : controllerInstance.getClass().getDeclaredFields() ) {
            if ( field.getType().equals( SummerSession.class ) ) {
                field.setAccessible( true );
                field.set( controllerInstance, new SummerSession( session ) );
                return;
            }
        }
    }
}
