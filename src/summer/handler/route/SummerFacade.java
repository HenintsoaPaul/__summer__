package src.summer.handler.route;

import src.summer.beans.Mapping;
import src.summer.beans.validation.ValidationLog;
import src.summer.exception.process.NoRouteForUrlException;
import src.summer.exception.process.NoRouteForHttpMethodException;
import src.summer.handler.AuthorizationHandler;
import src.summer.utils.ParamUtil;
import src.summer.utils.RouterUtil;
import src.summer.utils.ScannerUtil;
import src.summer.utils.SessionUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

public class SummerFacade {

    private final HashMap<String, Mapping> URLMappings;
    private final AuthorizationHandler authorizationHandler;

    private final ParamUtil paramUtil = new ParamUtil();
    private final ValidationLog validationLog = new ValidationLog();

    public SummerFacade(HashMap<String, Mapping> URLMappings, ServletContext context) {
        this.URLMappings = URLMappings;
        this.authorizationHandler = new AuthorizationHandler(context);
    }

    public SummerResponse getResponse(HttpServletRequest request)
            throws ServletException, ClassNotFoundException, InstantiationException,
            IllegalAccessException, IOException, NoSuchFieldException, InvocationTargetException,
            NoSuchMethodException {

        String modelViewUrl = "";
        SummerResponse summerResponse = null;
        boolean isRedirection = isRedirectionUrl(request.getRequestURI());

        // while null or redirection, make another call
        while (modelViewUrl.isEmpty() || isRedirection) {
            System.out.println("modelViewUrl: " + modelViewUrl + " | isRedirection: " + isRedirection);

            HttpSummerRequestWrapper httpSummerRequestWrapper = isRedirection ?
                    new HttpSummerRequestWrapper(request, modelViewUrl, true) :
                    new HttpSummerRequestWrapper(request, modelViewUrl);

            summerResponse = getResponse(httpSummerRequestWrapper);

            if (summerResponse.getModelView() == null) {
                // if string
                modelViewUrl = summerResponse.getStringResponse();
            } else {
                // if ModelView
                modelViewUrl = summerResponse.getModelView().getUrl();
            }

            isRedirection = isRedirectionUrl(modelViewUrl);
        }

        return summerResponse;
    }

    private boolean isRedirectionUrl(String url) {
        return url.startsWith("redirect:");
    }

    private SummerResponse getResponse(HttpSummerRequestWrapper summerRequestWrapper)
            throws ServletException, ClassNotFoundException, InstantiationException,
            IllegalAccessException, IOException, NoSuchFieldException, InvocationTargetException,
            NoSuchMethodException {
        String route = getRoute(summerRequestWrapper),
                httpMethod = summerRequestWrapper.getMethod();

        Mapping mapping = getMapping(route, httpMethod);

        Class<?> ctlClass = ScannerUtil.getClass(mapping.getControllerName());

        Object ctlInstance = getCtlInstance(mapping);

        SessionUtil.injectSession(ctlClass, ctlInstance, summerRequestWrapper);

        Method ctlMethod = mapping.getVerbAction(httpMethod).getAction();

        List<Object> ctlMethodParams = getCtlMethodParams(ctlMethod, summerRequestWrapper);

        if (validationLog.hasErrors()) {
            SummerResponseBuilder responseBuilder = new SummerResponseBuilder(validationLog);
            return responseBuilder.buildErrorResponse();
        }

        this.authorizationHandler.handle(ctlMethod, summerRequestWrapper.getSession());

        assert ctlMethodParams != null;
        Object methodResult = ctlMethod.invoke(ctlInstance, ctlMethodParams.toArray());

        // methodResult possible types : ModelView - String(simple, JSON)
        SummerResponseBuilder responseBuilder = new SummerResponseBuilder(mapping, methodResult, httpMethod);
        return responseBuilder.buildOkResponse();
    }

    private String getRoute(HttpServletRequest request) {
        // like "/my_project/<blab>/<...>"
        String url = request.getRequestURI();
        // like "<blab>/<...>"
        return RouterUtil.getRoute(url);
    }

    private Mapping getMapping(String route, String httpMethod) throws NoRouteForUrlException, NoRouteForHttpMethodException {
        if (!this.URLMappings.containsKey(route)) {
//            throw new NoRouteForUrlException(route).writeException(response);
//            return;
            throw new NoRouteForUrlException(route);
        }

        Mapping mapping = this.URLMappings.get(route);

        if (mapping.getVerbAction(httpMethod) == null) {
//            new NoRouteForHttpMethodException(httpMethod, url).writeException(response);
//            return;
            throw new NoRouteForHttpMethodException(route, httpMethod);
        }

        return mapping;
    }

    private Object getCtlInstance(Mapping mapping) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<?> ctlClass = ScannerUtil.getClass(mapping.getControllerName());
        return ctlClass.newInstance();
    }

    private List<Object> getCtlMethodParams(Method ctlMethod, HttpServletRequest request) throws ServletException, IOException, NoSuchFieldException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<Object> ctlMethodParams = paramUtil.getMethodParameterValues(ctlMethod, request, validationLog);

        if (validationLog.hasErrors()) {
            return null;
        }

        return ctlMethodParams;
    }
}
