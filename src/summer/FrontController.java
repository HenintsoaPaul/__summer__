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
import java.util.Enumeration;
import java.util.HashMap;

public class FrontController extends HttpServlet {
    private HashMap<String, Mapping> URLMappings = new HashMap<>();
    private HashMap<String, Object> formInputs = new HashMap<>();

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
        // Extract form input values
        Enumeration<String> names = request.getParameterNames();
        while ( names.hasMoreElements() ) {
            String name = names.nextElement();
            this.formInputs.put( name, request.getParameter( name ) );
        }

        processRequest( request, response );
    }

    protected void processRequest( HttpServletRequest request, HttpServletResponse response )
            throws IOException, ServletException {
        String url = request.getRequestURI(), // something like "/summer/<blab>/<...>"
                route = RouterUtil.getRoute( url ); // something like "<blab>/<...>"

        try {
            if ( !this.URLMappings.containsKey( route ) ) { // Verify existing route
                throw new SummerProcessException( "No route for URL \"" + route + "\"." );
            }

            Mapping mapping = this.URLMappings.get( route );
            String controllerName = mapping.getControllerName(),
                    methodName = mapping.getMethodName();

            // Execute the method
            Class<?> controllerClass = ScannerUtil.getControllerClass( controllerName );
            Method method = controllerClass.getDeclaredMethod( methodName );

            // Display return value
            @SuppressWarnings( "deprecation" ) Object returnValue = method.invoke( controllerClass.newInstance() );
            displayValue( request, response, returnValue, controllerName, methodName );

        } catch ( ClassNotFoundException | NoSuchMethodException | InstantiationException |
                  IllegalAccessException | InvocationTargetException e ) {
            throw new ServletException( e );
        }
    }

    private void displayValue( HttpServletRequest request, HttpServletResponse response,
                                      Object value, String controllerName, String methodName )
            throws IOException, ServletException {
        if ( value.getClass().getName().equals( ModelView.class.getName() ) ) {
            ModelView mv = ( ModelView ) value;
            String url = mv.getUrl(); // get the path to the view

            for ( String key : mv.getData().keySet() ) { // send data in mv with the dispatcher
                request.setAttribute( key, mv.getObject( key ) );
            }

            // Send data from a <form> to the controller
            if ( !this.formInputs.isEmpty() ) {
                for ( String key : this.formInputs.keySet() ) {
                    request.setAttribute( key, this.formInputs.get( key ) );
                }
                this.formInputs.clear();
            }

            RequestDispatcher dispatcher = request.getRequestDispatcher( url );
            dispatcher.forward( request, response );
        } else {
            // Print [Controller - Method - returnValue]
            PrintWriter out = response.getWriter();
            out.println( "Controller: " + controllerName );
            out.println( "Method: " + methodName );
            out.println( "Return Value: " + value );
        }
    }
}