package src.summer.utils;

import jakarta.servlet.http.HttpSession;

import java.util.Enumeration;
import java.util.HashMap;

public abstract class SessionUtil {
    public static HashMap<String, Object> sessionToMap( HttpSession session ) {
        HashMap<String, Object> result = new HashMap<>();
        Enumeration<String> keys = session.getAttributeNames();
        while ( keys.hasMoreElements() ) {
            String key = keys.nextElement();
            result.put( key, session.getAttribute( key ) );
        }
        return result;
    }
}
