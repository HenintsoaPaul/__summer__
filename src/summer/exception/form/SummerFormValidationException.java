package src.summer.exception.form;

import javax.servlet.ServletException;
import java.util.List;

public class SummerFormValidationException extends ServletException {
    private final String message;

    public SummerFormValidationException( List<SummerFormException> validationErrors ) {
        super();
        StringBuilder msg = new StringBuilder();
        for ( SummerFormException e : validationErrors ) msg.append( "\n" ).append( e.getMessage() );
        this.message = msg.toString();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
