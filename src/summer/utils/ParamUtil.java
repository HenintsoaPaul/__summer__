package src.summer.utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import src.summer.annotations.Param;
import src.summer.annotations.Validate;
import src.summer.beans.SummerFile;
import src.summer.beans.validation.ValidationLog;
import src.summer.exception.SummerProcessException;
import src.summer.utils.constraint.ConstraintValidatorUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public abstract class ParamUtil {
    /**
     * Return a list of the values of the args required by the {@code method}.
     * The values are contained in the {@code request} object.
     * <p>
     * Prendre la liste des parametres(arguments) de la methode a appeler -> prendre la valeur de chq parametre dans le request
     */
    public static List<Object> getMethodParameterValues(
            Method method,
            HttpServletRequest request,
            ValidationLog validationLog
    )
            throws NoSuchFieldException, InvocationTargetException,
            InstantiationException, IllegalAccessException,
            NoSuchMethodException, ServletException,
            IOException {
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
    public static Object getParameterValue(Parameter parameter, HttpServletRequest request, ValidationLog validationLog)
            throws NoSuchFieldException, InstantiationException, IllegalAccessException, NoSuchMethodException,
            InvocationTargetException, ServletException, IOException {
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

            // Validate required field
            if (needToValidate) {
                ConstraintValidatorUtil.validateAnnotationRequired(validationLog, field, fieldValue, inputName);
            }

            // Cast field value, then validate value it...
            fieldValue = TypeUtil.cast(fieldValue, fieldType);
            if (needToValidate) {
                ConstraintValidatorUtil.validateField(validationLog, field, fieldValue, inputName);
            }

            // Set field value using the corresponding setter...
            Method setterMethod = getSetterMethod(paramClass, fieldName, fieldType);
            setterMethod.invoke(paramValue, fieldValue);
        }
        if (needToValidate) {
            validationLog.setLastInput(paramValue);
            validationLog.setErrorPage(parameter.getAnnotation(Validate.class).errorPage());
        }
        return paramValue;
    }

    private static List<String> getFieldsNames(String entityName, HttpServletRequest request) {
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

    private static String getSetterName(String fieldName) {
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    private static Method getSetterMethod(Class<?> clazz, String fieldName, Class<?> fieldType)
            throws NoSuchMethodException {
        return clazz.getDeclaredMethod(getSetterName(fieldName), fieldType);
    }
}
