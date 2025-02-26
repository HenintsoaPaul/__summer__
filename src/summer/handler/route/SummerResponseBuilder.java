package src.summer.handler.route;

import src.summer.annotations.controller.RestApi;
import src.summer.beans.Mapping;
import src.summer.beans.ModelView;
import src.summer.beans.validation.ValidationLog;
import src.summer.utils.view.ModelViewUtil;

import java.lang.reflect.Method;

public class SummerResponseBuilder {

    private ValidationLog validationLog;
    private Mapping mapping;
    private Method ctlMethod;
    private String httpMethod;
    private Object methodResult;

    public SummerResponseBuilder(Mapping mapping, Object methodResult, String httpMethod) {
        this.mapping = mapping;
        this.httpMethod = httpMethod;
        this.ctlMethod = mapping.getMethod(httpMethod);
        this.methodResult = methodResult;
    }

    public SummerResponseBuilder(ValidationLog validationLog) {
        this.validationLog = validationLog;
    }

    private String buildStringResponse() {
        String stringResponse = "";

        stringResponse += "Controller: " + mapping.getControllerName() + "\n";
        stringResponse += "Method: " + ctlMethod.getName() + "\n";
//        stringResponse += "Route: " + route + "\n";
        stringResponse += "Method: " + httpMethod + "\n";
        stringResponse += "Return Value: " + methodResult + "\n";

        return stringResponse;
    }

    public SummerResponse buildOkResponse() {
        ModelView mv = null;
        String jsonResponse = null;

        if (ModelViewUtil.isInstance(methodResult)) {
            mv = (ModelView) methodResult;
        } else {
            jsonResponse = ctlMethod.isAnnotationPresent(RestApi.class) ?
                    ((ModelView) methodResult).getDataToJson() :
                    (String) methodResult;
        }

        return new SummerResponse(mv, jsonResponse);
    }

    public SummerResponse buildErrorResponse() {
        ModelView mv = new ModelView(validationLog.getErrorPage(), null);

        return new SummerResponse(mv, null);
    }
}
