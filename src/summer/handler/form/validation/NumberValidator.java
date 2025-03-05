package src.summer.handler.form.validation;

import src.summer.beans.validation.ValidationLog;
import src.summer.exception.form.NumberParamException;
import src.summer.utils.TypeUtil;

import java.lang.reflect.Field;

public abstract class NumberValidator implements IFormValidator {

    protected boolean isNumber(
            ValidationLog validationLog,
            Field field,
            String inputName
    ) {
        boolean isNumber = TypeUtil.isNumber(field.getType());
        if (!isNumber) {
            validationLog.addError(new NumberParamException(inputName));
        }
        return isNumber;
    }
}
