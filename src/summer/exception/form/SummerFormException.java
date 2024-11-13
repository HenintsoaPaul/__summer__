package src.summer.exception.form;

import javax.servlet.ServletException;
import java.lang.reflect.Field;

public abstract class SummerFormException extends ServletException {
    protected Field field;

    public SummerFormException( Field field ) {
        super();
        this.field = field;
    }
}
