package src.summer.handler.form.validation;

import src.summer.annotations.form.validation.Min;
import src.summer.beans.validation.ValidationLog;
import src.summer.exception.form.MinParamException;

import java.lang.reflect.Field;

public class MinValidator extends NumberValidator {

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
            boolean isNumber = super.isNumber(validationLog, field, inputName);
            if (!isNumber) return;

            double minValue = minAnnotation.value(),
                    value = ((Number) fieldValue).doubleValue();

            if (value < minValue) {
                validationLog.addError(new MinParamException(inputName, value, minValue));
            }
        }
    }
}
