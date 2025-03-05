package src.summer.handler.form.validation;

import src.summer.annotations.form.validation.Required;
import src.summer.beans.validation.ValidationLog;
import src.summer.exception.form.RequiredParamException;

import java.lang.reflect.Field;

public class RequiredValidator implements IFormValidator {

    @Override
    public void validate(ValidationLog validationLog, Field field, Object fieldValue, String inputName) {
        validateAnnotationRequired(validationLog, field, fieldValue, inputName);
    }

    /**
     * @param validationLog Object contenant la liste des erreurs de ValidationException
     * @param field         Attribut dont on veut verifier la validiter
     * @param fieldValue    Valeur de l'attribut a verifier
     * @param inputName     Nom du champ du formulaire necessaire pour l'affichage des erreurs de validation
     */
    public void validateAnnotationRequired(
            ValidationLog validationLog,
            Field field,
            Object fieldValue,
            String inputName
    ) {
        if (field.isAnnotationPresent(Required.class)) {
            if (fieldValue == null || fieldValue.toString().isEmpty()) {
                validationLog.addError(new RequiredParamException(inputName));
            }
        }
    }
}
