package src.summer.exception;

import javax.servlet.ServletException;

public class SummerProcessException extends ServletException {
    public SummerProcessException( String message ) {
        super( message );
    }
}
