package src.summer.utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import src.summer.annotations.Param;
import src.summer.annotations.Validate;
import src.summer.beans.SummerFile;
import src.summer.beans.validation.ValidationLog;
import src.summer.exception.SummerProcessException;
import src.summer.handler.form.validation.FormValidatorFacade;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class ParamUtil {

    private final FormValidatorFacade formValidatorFacade = new FormValidatorFacade();

    /**
     * Return a list of the values of the args required by the {@code method}.
     * The values are contained in the {@code request} object.
     * <p>
     * Prendre la liste des parametres(arguments) de la methode a appeler -> prendre la valeur de chq parametre dans le request
     */
    public List<Object> getMethodParameterValues(
            Method method,
            HttpServletRequest request,
            ValidationLog validationLog
    )
            throws NoSuchFieldException, InvocationTargetException,
            InstantiationException, IllegalAccessException,
            NoSuchMethodException, ServletException,
            IOException
    {
        List<Object> methodParameterValues = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            Object value = getParameterValue(parameter, request, validationLog);
            methodParameterValues.add(value);
        }
        return methodParameterValues;
    }

    /**
     * @param parameter     Parametre(arg) de la methode controller dont on veut obtenir la valeur
     * @param request       Request contant les data du formulaire
     * @param validationLog Objet pour contenir les erreurs de validation des champs de formulaire
     */
    public Object getParameterValue(
            Parameter parameter,
            HttpServletRequest request,
            ValidationLog validationLog
    )
            throws NoSuchFieldException, InstantiationException,
            IllegalAccessException, NoSuchMethodException,
            InvocationTargetException, ServletException,
            IOException
    {
        Param annotation = parameter.getAnnotation(Param.class);
        if (annotation == null) {
            throw new SummerProcessException("ETU2443 - Parameters must be annotated with \"@Param\".");
        }
        String paramName = annotation.name();

        if (annotation.isFile()) {
            return SummerFile.getFileFromRequest(request, paramName);
        }

        Object paramValue = request.getParameter(paramName);
        Class<?> paramClass = parameter.getType();
        // Dans le cas ou {@code parameter} a ete envoye directement depuis le HttpRequest
        if (paramValue != null) {
            return TypeUtil.cast(paramValue, paramClass);
        }

        // Dans le cas ou {@code parameter} est un objet, dont les fields ont ete envoyes dans un formulaire
        paramValue = paramClass.newInstance();
        boolean needToValidate = parameter.isAnnotationPresent(Validate.class);

        for (String fieldName : getFieldsNames(paramName, request)) {
            Field field = paramClass.getDeclaredField(fieldName);
            Class<?> fieldType = field.getType();

            String inputName = paramName + "." + fieldName;
            Object fieldValue = request.getParameter(inputName);

            // Validate required field before cast to preserve null values
            if (needToValidate) {
                formValidatorFacade.validateRequired(validationLog, field, fieldValue, inputName);
            }

            // Cast field value, then validate value it then prevent null values
            fieldValue = TypeUtil.cast(fieldValue, fieldType);
            if (needToValidate) {
                formValidatorFacade.validateField(validationLog, field, fieldValue, inputName);
            }

            // Set field value using the corresponding setter...
            Method setterMethod = getSetterMethod(paramClass, fieldName, fieldType);
            setterMethod.invoke(paramValue, fieldValue);
        }

        // set last input and redirection page on error
        if (needToValidate) {
            validationLog.setLastInput(paramValue);
            validationLog.setErrorPage(parameter.getAnnotation(Validate.class).errorPage());
        }

        return paramValue;
    }

    /**
     * Extrait les noms des champs d'une entité à partir des paramètres de la requête HTTP.
     * Cette méthode analyse les paramètres de la requête pour identifier ceux qui appartiennent
     * à l'entité spécifiée et extrait leurs noms de champs.
     *
     * @param entityName le nom de l'entité dont les champs doivent être extraits
     * @param request l'objet HttpServletRequest contenant les paramètres à analyser
     * @return une liste des noms de champs de l'entité trouvés dans le request
     */
    private List<String> getFieldsNames(String entityName, HttpServletRequest request) {
        List<String> fields = new ArrayList<>();
        Enumeration<String> listParameterNames = request.getParameterNames();
        while (listParameterNames.hasMoreElements()) {
            String p = listParameterNames.nextElement();
            if (p.contains(entityName)) {
                int dotIndex = p.lastIndexOf('.');
                fields.add(p.substring(dotIndex + 1));
            }
        }
        return fields;
    }

    private String getSetterName(String fieldName) {
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    private Method getSetterMethod(Class<?> clazz, String fieldName, Class<?> fieldType)
            throws NoSuchMethodException {
        return clazz.getDeclaredMethod(getSetterName(fieldName), fieldType);
    }
}
