package src.summer.utils.constraint;

import src.summer.annotations.form.validation.Min;
import src.summer.annotations.form.validation.Required;
import src.summer.beans.validation.ValidationLog;
import src.summer.exception.form.MinParamException;
import src.summer.exception.form.RequiredParamException;

import java.lang.reflect.Field;

public abstract class ConstraintValidatorUtil {
    public static void validateField( ValidationLog validationLog, Field field, Object fieldValue, String inputName ) {
        validateAnnotationRequired( validationLog, field, fieldValue, inputName );
        validateAnnotationMin( validationLog, field, fieldValue, inputName );
    }

    private static void validateAnnotationRequired( ValidationLog validationLog, Field field,
                                                    Object fieldValue, String inputName ) {
        if ( field.isAnnotationPresent( Required.class ) ) {
            if ( fieldValue == null || (
                    fieldValue instanceof String && ( ( String ) fieldValue ).trim().isEmpty()
            ) ) {
                validationLog.addError( new RequiredParamException( inputName ) );
            }
        }
    }

    private static void validateAnnotationMin( ValidationLog validationLog, Field field,
                                               Object fieldValue, String inputName ) {
        Min minAnnotation = field.getAnnotation( Min.class );
        if ( minAnnotation != null ) {
            int minValue = minAnnotation.value();
            if ( fieldValue instanceof Integer && ( ( Integer ) fieldValue ) < minValue ) {
                validationLog.addError( new MinParamException( inputName, ( Integer ) fieldValue, minValue ) );
            }
        }
    }
}
