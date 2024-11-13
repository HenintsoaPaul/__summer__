package src.summer.exception.form;

import javax.servlet.ServletException;

public abstract class SummerFormException extends ServletException {
    public SummerFormException( String message ) {
        super( message );
    }
}
