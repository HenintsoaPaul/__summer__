package src.summer;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import src.summer.utils.RouterUtil;
import src.summer.utils.ScannerUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

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
            throws IOException {
        processRequest( request, response );
    }

    @Override
    public void doPost( HttpServletRequest request, HttpServletResponse response )
            throws IOException {
        processRequest( request, response );
    }

    protected void processRequest( HttpServletRequest request, HttpServletResponse response )
            throws IOException {
        PrintWriter out = response.getWriter();

        String url = request.getRequestURI(); // something like "/summer/<blab>/<...>"
        String route = RouterUtil.getRoute( url ); // something like "<blab>/<...>"

        try {
            Mapping mapping = this.URLMappings.get( route );
            String controllerName = mapping.getControllerName(),
                    methodName = mapping.getMethodName();

            // Print [Controller - Method]
            out.println( "Controller: " + controllerName );
            out.println( "Method: " + methodName );

            // Execute the method
            Class<?> controllerClass = ScannerUtil.getControllerClass( controllerName );
            Method method = controllerClass.getDeclaredMethod( methodName );
            Object returnValue = method.invoke( controllerClass.newInstance() );

            // Display return value
            displayValue( response, returnValue );
        } catch ( NullPointerException e ) {
            out.println( "There is no Controller and Method for url : \"" + route + "\"" );
        } catch ( ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                  InvocationTargetException e ) {
            throw new RuntimeException( e );
        }
    }

    private static void displayValue( HttpServletResponse response, Object value )
            throws IOException {
        if ( value.getClass().getName().equals( ModelView.class.getName() ) ) {
            String url = ((ModelView) value).getUrl(); // get the path to the view
            response.sendRedirect( url ); // display view
        }
        else {
            response.getWriter().println( "Return Value: " + value );
        }
    }
}