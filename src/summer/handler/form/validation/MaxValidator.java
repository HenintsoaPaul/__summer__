package src.summer.handler.form.validation;

import src.summer.annotations.form.validation.Max;
import src.summer.beans.validation.ValidationLog;
import src.summer.exception.form.MaxParamException;
import src.summer.exception.form.NumberParamException;

import java.lang.reflect.Field;

public class MaxValidator implements IFormValidator {

    @Override
    public void validate(ValidationLog validationLog, Field field, Object fieldValue, String inputName) {
        this.validateAnnotationMax(validationLog, field, fieldValue, inputName);
    }

    private void validateAnnotationMax(
            ValidationLog validationLog,
            Field field,
            Object fieldValue,
            String inputName
    ) {
        Max maxAnnotation = field.getAnnotation(Max.class);

        if (maxAnnotation != null) {
            if (!Number.class.isAssignableFrom(field.getType()) || !(fieldValue instanceof Number)) {
                validationLog.addError(new NumberParamException(inputName));
                return;
            }

            double maxValue = maxAnnotation.value(),
                    value = ((Number) fieldValue).doubleValue();

            if (value > maxValue) {
                validationLog.addError(new MaxParamException(inputName, value, maxValue));
            }
        }
    }

}
