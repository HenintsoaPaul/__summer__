package src.summer.exception.form;

import javax.servlet.ServletException;

public abstract class SummerFormException extends ServletException {
    protected String fieldName;

    public SummerFormException( String fieldName ) {
        super();
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public abstract String getCustomMessage();
}
