package src.summer.exception;

import jakarta.servlet.ServletException;

public class SummerInitException extends ServletException {
    public SummerInitException( String message ) {
        super( message );
    }
}
