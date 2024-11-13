package src.summer.exception.form;

import java.lang.reflect.Field;

public class RequiredParamException extends SummerFormException {
    public RequiredParamException( Field field ) {
        super( field );
    }

    @Override
    public String getMessage() {
        return "Field \"" + field.getName() + "\" is required.";
    }
}
