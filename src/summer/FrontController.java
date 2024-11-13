package src.summer;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import src.summer.annotations.controller.RestApi;
import src.summer.beans.Mapping;
import src.summer.beans.ModelView;
import src.summer.exception.SummerProcessException;
import src.summer.exception.form.SummerFormException;
import src.summer.exception.form.SummerFormValidationException;
import src.summer.utils.*;

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

    @Override
    public void init()
            throws ServletException {
        try {
            String packageName = getServletContext().getInitParameter( "app.controllers.packageName" );
            this.URLMappings = ScannerUtil.scanControllers( packageName );
        } catch ( Exception e ) {
            log( "Error initializing FrontController", e );
            throw new ServletException( "Initialization failed", e );
        }
    }

    @Override
    public void doGet( HttpServletRequest request, HttpServletResponse response )
            throws IOException, ServletException {
        processRequest( request, response );
    }

    @Override
    public void doPost( HttpServletRequest request, HttpServletResponse response )
            throws IOException, ServletException {
        processRequest( request, response );
    }

    protected void processRequest( HttpServletRequest request, HttpServletResponse response )
            throws IOException, ServletException {
        String url = request.getRequestURI(), // something like "/summer/<blab>/<...>"
                route = RouterUtil.getRoute( url ); // something like "<blab>/<...>"

        try {
            if ( !this.URLMappings.containsKey( route ) ) {
                response.setStatus( HttpServletResponse.SC_NOT_FOUND );
                response.getWriter().print( "There is no route for \"" + route + "\"" );
                return;
            }

            Mapping mapping = this.URLMappings.get( route );

            // Verify the route matchs the verb (isPOST, isGET)
            String verb = request.getMethod();
            if ( mapping.getVerbAction( verb ) == null ) {
                throw new SummerProcessException( "Invalid verb \"" + verb + "\" for this URL." );
            }

            // Instance creation
            Class<?> clazz = ScannerUtil.getClass( mapping.getControllerName() );
            Object newInstance = clazz.newInstance();

            // Session Injection
            if ( SessionUtil.containsSummerSession( clazz ) ) {
                SessionUtil.injectSession( newInstance, request.getSession() );
            }

            // Get the method params
            Method method = mapping.getVerbAction( verb ).getAction();
            List<Object> methodParams = ParamUtil.getMethodParameterValues( method, request );

            // Validate Params (If annotated)
            List<SummerFormException> errors = FormValidatorUtil.validateForm( methodParams );
            if ( !errors.isEmpty() ) {
                throw new SummerFormValidationException( errors );
            }

            Object value = method.invoke( newInstance, methodParams.toArray() );

            // Verify the method is annotated with '@Rest'
            if ( method.isAnnotationPresent( RestApi.class ) ) { // If true, show JSON
                Object jsonValue = ModelViewUtil.isInstance( value ) ?
                        ( ( ModelView ) value ).getData() : value;
                ViewUtil.printJson( jsonValue, response );
            } else { // Else, show value(String or ModelView)
                ViewUtil.show( value, mapping, request, response );
            }
        } catch ( ClassNotFoundException | InstantiationException | IllegalAccessException |
                  InvocationTargetException | NoSuchFieldException | NoSuchMethodException e ) {
            throw new ServletException( e );
        }
    }
}
