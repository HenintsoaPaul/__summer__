package src.summer.exception;

import javax.servlet.ServletException;

public class SummerAuthorizationException extends ServletException {
    public SummerAuthorizationException( String message ) {
        super( "AUTHORIZATION FAILED! " + message );
    }
}
