package src.summer.handler.form.validation;

import src.summer.annotations.form.validation.IntRange;
import src.summer.beans.validation.ValidationLog;
import src.summer.exception.form.IntParamException;
import src.summer.exception.form.IntRangeParamException;
import src.summer.utils.TypeUtil;

import java.lang.reflect.Field;

public class IntRangeValidator extends NumberValidator {

    @Override
    public void validate(ValidationLog validationLog, Field field, Object fieldValue, String inputName) {
        this.validateAnnotationIntRange(validationLog, field, fieldValue, inputName);
    }

    private void validateAnnotationIntRange(
            ValidationLog validationLog,
            Field field,
            Object fieldValue,
            String inputName
    ) {
        IntRange intRangeAnnotation = field.getAnnotation(IntRange.class);

        if (intRangeAnnotation != null) {
            boolean isNumber = super.isNumber(validationLog, field, inputName);
            if (!isNumber) return;

            if (!TypeUtil.isInteger(field.getType())) {
                validationLog.addError(new IntParamException(inputName));
                return;
            }

            int maxValue = intRangeAnnotation.maxValue(),
                    minValue = intRangeAnnotation.minValue(),
                    value = (Integer) fieldValue;

            if (minValue > value || value > maxValue) {
                validationLog.addError(new IntRangeParamException(inputName, value, minValue, maxValue));
            }
        }
    }
}
