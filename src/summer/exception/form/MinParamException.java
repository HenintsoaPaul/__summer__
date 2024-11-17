package src.summer.exception.form;

import java.lang.reflect.Field;

public class MinParamException extends SummerFormException {
    int fieldValue, minValue;

    public MinParamException( Field field, int fieldValue, int minValue ) {
        super(field);
        this.fieldValue = fieldValue;
        this.minValue = minValue;
    }

    @Override
    public String getMessage() {
        return "Field \"" + field.getName() + "\" (" + fieldValue + ") is under min value (" + minValue + ").";
    }
}
