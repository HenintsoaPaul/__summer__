package src.summer.exception.form;

import java.lang.reflect.Field;

public class RequiredParamException extends SummerFormException {
    public RequiredParamException( Field field ) {
        super( "Field \"" + field.getName() + "\" is required." );
    }
}
