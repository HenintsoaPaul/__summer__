package src.summer.handler;

import src.summer.annotations.controller.RestApi;
import src.summer.beans.Mapping;
import src.summer.beans.ModelView;
import src.summer.beans.validation.ValidationLog;
import src.summer.handler.view.ModelViewUtil;

import java.lang.reflect.Method;

public class SummerResponseBuilder {

    private ValidationLog validationLog;
    private Mapping mapping;
    private Method ctlMethod;
    private Object methodResult;
    private HttpSummerRequestWrapper summerRequestWrapper;

    public SummerResponseBuilder(ValidationLog validationLog) {
        this.validationLog = validationLog;
    }

    public SummerResponseBuilder(Mapping mapping, Object methodResult, HttpSummerRequestWrapper summerRequestWrapper) {
        this.mapping = mapping;
        this.methodResult = methodResult;
        this.summerRequestWrapper = summerRequestWrapper;
        this.ctlMethod = mapping.getMethod(summerRequestWrapper.getMethod());
    }

    private String formatStringResponse() {
        String stringResponse = "";

        stringResponse += "CtlName: " + mapping.getControllerName() + "CtlMethod: " + ctlMethod.getName() + "\n";
        stringResponse += "Route: " + summerRequestWrapper.getRequestURI() + " | HttpMethod: " + summerRequestWrapper.getMethod() + "\n";
        stringResponse += "Return Value: " + methodResult + "\n";

        return stringResponse;
    }

    public SummerResponse build() {
        if (validationLog != null) {
            return buildOkResponse();
        } else {
            return buildErrorResponse();
        }
    }

    public SummerResponse buildOkResponse() {
        ModelView mv = null;
        String jsonResponse = null, formattedResponse = null;

        if (ModelViewUtil.isInstance(methodResult)) {
            mv = (ModelView) methodResult;
        } else {
            jsonResponse = ctlMethod.isAnnotationPresent(RestApi.class) ?
                    ((ModelView) methodResult).getDataToJson() :
                    (String) methodResult;

            formattedResponse = formatStringResponse();
        }

        return new SummerResponse(mv, jsonResponse, formattedResponse);
    }

    public SummerResponse buildErrorResponse() {
        ModelView mv = new ModelView(validationLog.getErrorPage(), null);

        return new SummerResponse(mv, null, null);
    }
}
