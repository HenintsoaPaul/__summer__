package src.summer.exception;

import javax.servlet.ServletException;

public class SummerInitException extends ServletException {
    public SummerInitException( String message ) {
        super( message );
    }
}
