package src.summer.exception;

import jakarta.servlet.ServletException;

public class SummerProcessException extends ServletException {
    public SummerProcessException( String message ) {
        super( message );
    }
}
