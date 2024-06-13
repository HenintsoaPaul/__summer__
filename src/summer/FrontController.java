package src.summer;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import src.summer.exception.SummerProcessException;
import src.summer.utils.RouterUtil;
import src.summer.utils.ScannerUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

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

    @SuppressWarnings( {"deprecation"} )
    protected void processRequest( HttpServletRequest request, HttpServletResponse response )
            throws IOException, ServletException {
        String url = request.getRequestURI(), // something like "/summer/<blab>/<...>"
                route = RouterUtil.getRoute( url ); // something like "<blab>/<...>"

        try {
            if ( !this.URLMappings.containsKey( route ) ) { // Verify existing route
                throw new SummerProcessException( "No route for URL \"" + route + "\"." );
            }

            Mapping mapping = this.URLMappings.get( route );
            String controllerName = mapping.getControllerName();
            Method method = mapping.getMethod();

            // Get the method
            Class<?> controllerClass = ScannerUtil.getControllerClass( controllerName );
            List<String> methodParams = new ArrayList<>();

            // Get method expected parameterNames
            List<String> methodExpectedParamNames = new ArrayList<>();
            System.out.println( "Expected params: " );
            for ( Parameter parameter : method.getParameters() ) {
                System.out.println( "- " + parameter.getName() );
                methodExpectedParamNames.add( parameter.getName() );
            }
            System.out.println( "Expected param ---\n" );

            // Get url params
            Enumeration<String> urlParamNames = request.getParameterNames();
            System.out.println( "Url params:" );
            while ( urlParamNames.hasMoreElements() ) {
                String param = urlParamNames.nextElement();
                System.out.println( "- " + param );
                if ( methodExpectedParamNames.contains( param ) ) {
                    methodParams.add( request.getParameter( param ) );
                }
            }
            System.out.println( "Url params ---\n" );

            // Display return value
            Object returnValue = method.invoke( controllerClass.newInstance(), methodParams.toArray() );
            displayValue( request, response, returnValue, mapping );
            System.out.println( "---\n" );

        } catch ( ClassNotFoundException | InstantiationException |
                  IllegalAccessException | InvocationTargetException e ) {
            throw new ServletException( e );
        }
    }

    private void displayValue( HttpServletRequest request, HttpServletResponse response,
                               Object value, Mapping mapping )
            throws IOException, ServletException {
        if ( value.getClass().getName().equals( ModelView.class.getName() ) ) {
            ModelView mv = ( ModelView ) value;
            String url = mv.getUrl(); // get the path to the view

            for ( String key : mv.getData().keySet() ) { // send data in mv with the dispatcher
                request.setAttribute( key, mv.getObject( key ) );
            }

            RequestDispatcher dispatcher = request.getRequestDispatcher( url );
            dispatcher.forward( request, response );
        } else {
            // Print [Controller - Method - returnValue]
            PrintWriter out = response.getWriter();
            out.println( "Controller: " + mapping.getControllerName() );
            out.println( "Method: " + mapping.getMethod().getName() );
            out.println( "Return Value: " + value );
        }
    }
}