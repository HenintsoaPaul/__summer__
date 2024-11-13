package src.summer.utils;

import src.summer.annotations.form.validation.Required;
import src.summer.exception.form.RequiredParamException;
import src.summer.exception.form.SummerFormException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class FormValidatorUtil {
    /**
     * Valider chaque objet lie au formulaire
     * @param formObjects -- Liste des objets lies au formulaire
     */
    public static List<SummerFormException> validateForm( List<Object> formObjects ) {
        List<SummerFormException> errors = new ArrayList<>();
        for ( Object fo : formObjects ) {
            List<SummerFormException> arr = validateFormObject( fo );
            if ( !arr.isEmpty() ) {
                errors.addAll( arr );
            }
        }
        return errors;
    }

    /**
     * Parcourir la listes des attributs de l'objet {@code formField}, puis
     * verifier les validators de formulaire.
     * @param formObject Un Objet lie au formulaire
     */
    public static List<SummerFormException> validateFormObject( Object formObject ) {
        boolean isPrimitive = TypeUtil.isPrimitive( formObject );
        System.out.println( "IsPrimitive: " + isPrimitive );
        if ( isPrimitive ) return null;

        List<SummerFormException> errors = new ArrayList<>();
        for ( Field field : formObject.getClass().getDeclaredFields() ) {
            List<SummerFormException> ers = validateField( formObject, field );
            if ( !ers.isEmpty() ) {
                errors.addAll( ers );
            }
        }
        return errors;
    }

    public static List<SummerFormException> validateField( Object formObject, Field field ) {
        List<SummerFormException> errors = new ArrayList<>();
        try {
            if ( field.isAnnotationPresent( Required.class ) ) {
                field.setAccessible( true );
                Object fieldValue = field.get( formObject );
                if ( fieldValue == null || ( fieldValue instanceof String && ( ( String ) fieldValue ).trim().isEmpty() ) ) {
                    errors.add( new RequiredParamException( field ) );
                }
            }
        } catch ( IllegalAccessException e ) {
            System.err.println( "Erreur lors de l'accès au champ: " + field.getName() );
        }
        return errors;
    }
}
