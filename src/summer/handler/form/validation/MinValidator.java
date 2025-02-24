package src.summer.handler.form.validation;

import src.summer.annotations.form.validation.Min;
import src.summer.beans.validation.ValidationLog;
import src.summer.exception.form.IntParamException;
import src.summer.exception.form.MinParamException;
import src.summer.exception.form.NumberParamException;

import java.lang.reflect.Field;

public class MinValidator implements IFormValidator {

    @Override
    public void validate(ValidationLog validationLog, Field field, Object fieldValue, String inputName) {
        this.validateAnnotationMin(validationLog, field, fieldValue, inputName);
    }

    private void validateAnnotationMin(
            ValidationLog validationLog,
            Field field,
            Object fieldValue,
            String inputName
    ) {
        Min minAnnotation = field.getAnnotation(Min.class);

        if (minAnnotation != null) {
            if (!Number.class.isAssignableFrom(field.getType()) || !(fieldValue instanceof Number)) {
                validationLog.addError(new NumberParamException(inputName));
                return;
            }

            double minValue = minAnnotation.value(),
                    value = ((Number) fieldValue).doubleValue();

            if (value < minValue) {
                validationLog.addError(new MinParamException(inputName, value, minValue));
            }
        }
    }
}
