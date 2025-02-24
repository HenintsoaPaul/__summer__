package src.summer.handler.form.validation;

import src.summer.annotations.form.validation.Min;
import src.summer.beans.validation.ValidationLog;
import src.summer.exception.form.IntParamException;
import src.summer.exception.form.MinParamException;

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

            boolean isInteger = field.getType().isAssignableFrom(Integer.class);
            if (!isInteger) {
                validationLog.addError(new IntParamException(inputName));
            }

            int minValue = minAnnotation.value();
            Integer value = (Integer) fieldValue;

            if (value < minValue) {
                validationLog.addError(new MinParamException(inputName, value, minValue));
            }
        }
    }
}
