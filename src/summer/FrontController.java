package src.summer;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import src.summer.annotations.controller.RestApi;
import src.summer.beans.Mapping;
import src.summer.beans.ModelView;
import src.summer.beans.validation.ValidationLog;
import src.summer.exception.process.NoRouteForUrlException;
import src.summer.exception.process.NoRouteForVerbException;
import src.summer.handler.AuthorizationHandler;
import src.summer.utils.*;
import src.summer.utils.view.ModelViewUtil;
import src.summer.utils.view.ViewUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@MultipartConfig
public class FrontController extends HttpServlet {
    /**
     * Map containing the Mapping objects matching to their URLs.
     */
    private HashMap<String, Mapping> URLMappings = new HashMap<>();
    private final AuthorizationHandler authorizationHandler = new AuthorizationHandler();

    @Override
    public void init()
            throws ServletException {
        try {
            String packageName = getServletContext().getInitParameter("app.controllers.packageName");
            this.URLMappings = ScannerUtil.scanControllers(packageName);
        } catch (Exception e) {
            log("Error initializing FrontController", e);
            throw new ServletException("Initialization failed", e);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        processRequest(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String url = request.getRequestURI(), // something like "/summer/<blab>/<...>"
                route = RouterUtil.getRoute(url); // something like "<blab>/<...>"

        try {
            if (!this.URLMappings.containsKey(route)) {
                new NoRouteForUrlException(route).writeException(response);
                return;
            }

            Mapping mapping = this.URLMappings.get(route);

            String verb = request.getMethod();
            if (mapping.getVerbAction(verb) == null) {
                new NoRouteForVerbException(verb, url).writeException(response);
                return;
            }

            // Instance creation
            Class<?> clazz = ScannerUtil.getClass(mapping.getControllerName());
            Object ctlInstance = clazz.newInstance();

            // Session Injection
            if (SessionUtil.containsSummerSession(clazz)) {
                SessionUtil.injectSession(ctlInstance, request.getSession());
            }

            // Get the method params (+ validate params' values)
            Method ctlMethod = mapping.getVerbAction(verb).getAction();
            List<Object> ctlMethodParams;

            ValidationLog validationLog = new ValidationLog();
            ctlMethodParams = ParamUtil.getMethodParameterValues(ctlMethod, request, validationLog);

            // Redirect on form validation exception
            if (validationLog.hasErrors()) {
                ModelView mv = new ModelView(validationLog.getErrorPage(), null);
                ModelViewUtil.showFormValidationException(mv, validationLog, request, response);
            }

            // Verify User Authorization
            this.authorizationHandler.handle(ctlMethod, getServletContext(), request.getSession());

            Object methodResult = ctlMethod.invoke(ctlInstance, ctlMethodParams.toArray());

             if (ctlMethod.isAnnotationPresent(RestApi.class)) {
                Object jsonValue = ModelViewUtil.isInstance(methodResult) ?
                        ((ModelView) methodResult).getData() :
                        methodResult;
                ViewUtil.printJson(jsonValue, response);
            } else {
                ViewUtil.show(methodResult, mapping, request, response);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchFieldException | NoSuchMethodException e) {
            // Envoyer les exceptions vers une view custom...
            e.printStackTrace();
            throw new ServletException(e);
        }
    }
}
