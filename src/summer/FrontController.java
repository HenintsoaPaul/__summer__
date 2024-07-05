package src.summer;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import src.summer.beans.Mapping;
import src.summer.exception.SummerProcessException;
import src.summer.utils.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class FrontController extends HttpServlet {
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

    @SuppressWarnings( { "deprecation" } )
    protected void processRequest( HttpServletRequest request, HttpServletResponse response )
            throws IOException, ServletException {
        String url = request.getRequestURI(), // something like "/summer/<blab>/<...>"
                route = RouterUtil.getRoute( url ); // something like "<blab>/<...>"

        try {
            if ( !this.URLMappings.containsKey( route ) ) { // Verify existing route
                throw new SummerProcessException( "No route for URL \"" + route + "\"." );
            }

            Mapping mapping = this.URLMappings.get( route );
            Method method = mapping.getMethod();

            // Instance creation
            Class<?> clazz = ScannerUtil.getClass( mapping.getControllerName() );
            Object newInstance = clazz.newInstance();

            // Session Injection
            if ( SessionUtil.containsSummerSession( clazz ) ) {
                SessionUtil.injectSession( newInstance, request.getSession() );
            }

            // Get the method params
            List<Object> methodParams = ParamUtil.getMethodParameterValues( method, request );

            // Display return value
            Object value = method.invoke( newInstance, methodParams.toArray() );
            ViewUtil.show( value, mapping, request, response );
        } catch ( ClassNotFoundException | InstantiationException | IllegalAccessException |
                  InvocationTargetException | NoSuchFieldException | NoSuchMethodException e ) {
            throw new ServletException( e );
        }
    }
}