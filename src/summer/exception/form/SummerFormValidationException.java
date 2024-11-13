package src.summer.exception.form;

import javax.servlet.ServletException;
import java.util.List;

public class SummerFormValidationException extends ServletException {
    private String message;

    public SummerFormValidationException( List<SummerFormException> validationErrors ) {
        super();
        String msg = "";
        for ( SummerFormException e : validationErrors ) msg += "\n" + e.getMessage();
        this.message = msg;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
