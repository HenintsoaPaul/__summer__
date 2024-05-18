package src.summer;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import src.summer.utils.ScannerUtil;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class FrontController extends HttpServlet {
    private HashMap<String, Mapping> URLMappings = new HashMap<>();

    @Override
    public void init() throws ServletException {
        try {
            String packageName = getServletContext().getInitParameter( "app.controllers.packageName" );
            this.URLMappings = ScannerUtil.scanControllers( packageName );
        } 
        catch (Exception e) {
            log("Error initializing FrontController", e);
            throw new ServletException("Initialization failed", e);
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
        try {
            if ( !checked ) {
                String packageName = getServletContext().getInitParameter( "app.controllers.packageName" );
                controllersNames = getControllersNames( packageName );
                checked = true;
            }

            try ( PrintWriter writer = response.getWriter() ) {
                for ( String controllerName : controllersNames ) {
                    writer.println( controllerName );
                }
            }
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace();
        }
    }

    private List<String> getControllersNames( String packageName )
            throws ClassNotFoundException, IOException {
        List<String> controllerNames = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources( packageName.replace( ".", "/" ) );
        scanResources( resources, packageName, controllerNames, classLoader );
        return controllerNames;
    }

    private void scanResources( Enumeration<URL> resources, String packageName, List<String> controllerNames, ClassLoader classLoader )
            throws ClassNotFoundException, IOException {
        while ( resources.hasMoreElements() ) {
            URL resource = resources.nextElement();
            File file = new File( resource.getFile() );
            if ( file.isDirectory() ) {
                scanDirectory( file, packageName, controllerNames, classLoader );
            } else {
                scanFile( file, packageName, controllerNames, classLoader );
            }
        }
    }

    private void scanDirectory( File directory, String packageName, List<String> controllerNames, ClassLoader classLoader )
            throws ClassNotFoundException, IOException {
        File[] files = directory.listFiles();
        for ( File file : files ) {
            scanFile( file, packageName, controllerNames, classLoader );
        }
    }

    private void scanFile( File file, String packageName, List<String> controllerNames, ClassLoader classLoader )
            throws ClassNotFoundException {
        if ( file.getName().endsWith( ".class" ) ) {
            String className = packageName + "." + file.getName().replace( ".class", "" );
            Class<?> clazz = classLoader.loadClass( className );
            if ( clazz.isAnnotationPresent( Controller.class ) ) {
                controllerNames.add( className );
            }
        }
    }
}