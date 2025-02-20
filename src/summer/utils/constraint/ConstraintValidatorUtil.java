package src.summer.utils.constraint;

import src.summer.annotations.form.validation.IntRange;
import src.summer.annotations.form.validation.Max;
import src.summer.annotations.form.validation.Min;
import src.summer.annotations.form.validation.Required;
import src.summer.beans.validation.ValidationLog;
import src.summer.exception.form.*;

import java.lang.reflect.Field;

public abstract class ConstraintValidatorUtil {
    public static void validateField(ValidationLog validationLog, Field field, Object fieldValue, String inputName) {
        validateAnnotationMin(validationLog, field, fieldValue, inputName);
        validateAnnotationMax(validationLog, field, fieldValue, inputName);
        validateAnnotationIntRange(validationLog, field, fieldValue, inputName);
    }

    /**
     * @param validationLog Object contenant la liste des erreurs de ValidationException
     * @param field         Attribut dont on veut verifier la validiter
     * @param fieldValue    Valeur de l'attribut a verifier
     * @param inputName     Nom du champ du formulaire necessaire pour l'affichage des erreurs de validation
     */
    public static void validateAnnotationRequired(
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

    private static void validateAnnotationMin(
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

    private static void validateAnnotationMax(
            ValidationLog validationLog,
            Field field,
            Object fieldValue,
            String inputName
    ) {
        Max maxAnnotation = field.getAnnotation(Max.class);

        if (maxAnnotation != null) {

            boolean isInteger = field.getType().isAssignableFrom(Integer.class);
            if (!isInteger) {
                validationLog.addError(new IntParamException(inputName));
            }

            int maxValue = maxAnnotation.value();
            Integer value = (Integer) fieldValue;

            if (value > maxValue) {
                validationLog.addError(new MaxParamException(inputName, value, maxValue));
            }
        }
    }

    private static void validateAnnotationIntRange(
            ValidationLog validationLog,
            Field field,
            Object fieldValue,
            String inputName
    ) {
        IntRange maxAnnotation = field.getAnnotation(IntRange.class);

        if (maxAnnotation != null) {

            boolean isInteger = field.getType().isAssignableFrom(Integer.class);
            if (!isInteger) {
                validationLog.addError(new IntParamException(inputName));
            }

            int maxValue = maxAnnotation.maxValue(),
                    minValue = maxAnnotation.minValue();
            Integer value = (Integer) fieldValue;

            if (minValue > value || value > maxValue) {
                validationLog.addError(new IntRangeParamException(inputName, value, minValue, maxValue));
            }
        }
    }
}
