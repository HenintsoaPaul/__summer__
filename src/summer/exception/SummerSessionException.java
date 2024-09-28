package src.summer.exception;

import javax.servlet.ServletException;

public class SummerSessionException extends ServletException {
    public SummerSessionException( String message ) {
        super( message );
    }
}
