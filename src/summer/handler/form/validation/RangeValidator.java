package src.summer.handler.form.validation;

import src.summer.annotations.form.validation.Range;
import src.summer.beans.validation.ValidationLog;
import src.summer.exception.form.RangeParamException;

import java.lang.reflect.Field;

public class RangeValidator extends NumberValidator {

    @Override
    public void validate(ValidationLog validationLog, Field field, Object fieldValue, String inputName) {
        this.validateAnnotationRange(validationLog, field, fieldValue, inputName);
    }

    private void validateAnnotationRange(
            ValidationLog validationLog,
            Field field,
            Object fieldValue,
            String inputName
    ) {
        Range rangeAnnotation = field.getAnnotation(Range.class);

        if (rangeAnnotation != null) {
            boolean isNumber = super.isNumber(validationLog, field, inputName);
            if (!isNumber) return;

            double maxValue = rangeAnnotation.maxValue(),
                    minValue = rangeAnnotation.minValue(),
                    value = (java.lang.Double) fieldValue;

            if (minValue > value || value > maxValue) {
                validationLog.addError(new RangeParamException(inputName, value, minValue, maxValue));
            }
        }
    }
}
