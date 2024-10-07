package src.summer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import src.summer.annotations.RestApi;
import src.summer.beans.Mapping;
import src.summer.beans.ModelView;
import src.summer.exception.SummerProcessException;
import src.summer.utils.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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
//            TODO: Tsy mamerina exception tsony fa message fotsiny. De asina status code 404
            if ( !this.URLMappings.containsKey( route ) ) { // Verify existing route
                throw new SummerProcessException( "No route for URL \"" + route + "\"." );
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