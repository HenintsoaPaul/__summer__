package src.summer.beans;

import javax.servlet.http.HttpSession;
import src.summer.exception.SummerSessionException;

public class SummerSession {
    private final HttpSession session;

    public SummerSession( HttpSession sess ) {
        this.session = sess;
    }

    // CREATE
    public void addAttribute( String name, Object value )
            throws SummerSessionException {
        if ( this.session.getAttribute( name ) != null ) {
            throw new SummerSessionException( "Cannot duplicate keys in the Session." );
        }
        this.session.setAttribute( name, value );
    }

    // READ
    public HttpSession getSession() {
        return session;
    }

    public Object getAttribute( String key ) {
        return this.session.getAttribute( key );
    }

    // DELETE
    public void destroy() {
        this.session.invalidate();
    }
}
