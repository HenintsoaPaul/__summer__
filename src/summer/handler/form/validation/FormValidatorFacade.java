package src.summer.handler.form.validation;

import src.summer.beans.validation.ValidationLog;

import java.lang.reflect.Field;

public class FormValidatorFacade {

    private final RequiredValidator requiredValidator = new RequiredValidator();
    private final MinValidator minValidator = new MinValidator();
    private final MaxValidator maxValidator = new MaxValidator();
    private final IntRangeValidator intRangeValidator = new IntRangeValidator();
    private final RangeValidator rangeValidator = new RangeValidator();

    public void validateRequired(ValidationLog validationLog, Field field, Object fieldValue, String inputName) {
        requiredValidator.validate(validationLog, field, fieldValue, inputName);
    }

    public void validateField(ValidationLog validationLog, Field field, Object fieldValue, String inputName) {
        minValidator.validate(validationLog, field, fieldValue, inputName);

        maxValidator.validate(validationLog, field, fieldValue, inputName);

        intRangeValidator.validate(validationLog, field, fieldValue, inputName);

        rangeValidator.validate(validationLog, field, fieldValue, inputName);
    }
}
