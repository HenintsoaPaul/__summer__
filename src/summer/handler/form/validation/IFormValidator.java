package src.summer.handler.form.validation;

import src.summer.beans.validation.ValidationLog;

import java.lang.reflect.Field;

public interface IFormValidator {

    void validate(
            ValidationLog validationLog,
            Field field,
            Object fieldValue,
            String inputName
    );
}
