package src;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import src.annotations.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class FrontController extends HttpServlet {
    boolean checked = false;
    List<String> controllersNames = new ArrayList<>();

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
        try {
            if ( !checked ) {
                // get controllers' package name
                String packageName = getServletContext().getInitParameter( "app.controllers.packageName" );

                controllersNames = getControllersNames( packageName );
                checked = true;
            }

            PrintWriter writer = response.getWriter();
            for ( String controllerName : controllersNames ) {
                writer.println( controllerName );
            }
        } catch ( ClassNotFoundException e ) {
            throw new RuntimeException( e );
        }
    }

    protected List<String> getControllersNames( String packageName )
            throws ClassNotFoundException {
        List<String> controllerNames = new ArrayList<>();
        Class<?>[] classes = Class.forName( packageName ).getClasses();

        for ( Class<?> clazz : classes ) {
            if ( clazz.isAnnotationPresent( Controller.class ) ) {
                String className = clazz.getSimpleName();
                controllerNames.add( className );
            }
        }
        return controllerNames;
    }
}
