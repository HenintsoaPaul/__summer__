package src.summer;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import src.summer.utils.RouterUtil;
import src.summer.utils.ScannerUtil;

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
        PrintWriter out = response.getWriter();

        String url = request.getRequestURI(); // something like "/summer/<blabla>/<...>"
        String route = RouterUtil.getRoute( url ); // something like "<blabla>/<...>"

        try {
            Mapping mapping = this.URLMappings.get(route);
            out.println( "Controller: " + mapping.getControllerName() );
            out.println( "Method: " + mapping.getMethodName() );
        }
        catch(NullPointerException e) {
            out.println( "There is no Controller and Method for url : \"" + route + "\"" );
        }
    }
}