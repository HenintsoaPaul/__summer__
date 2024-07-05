package src.summer.exception;

import jakarta.servlet.ServletException;

public class SummerSessionException extends ServletException {
    public SummerSessionException( String message ) {
        super( message );
    }
}
