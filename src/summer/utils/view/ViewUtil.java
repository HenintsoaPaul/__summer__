package src.summer.utils.view;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import src.summer.beans.Mapping;
import src.summer.beans.ModelView;
import src.summer.beans.VerbAction;

import java.io.IOException;
import java.io.PrintWriter;

public abstract class ViewUtil {
    /**
     * This method handle the way we display the value object.
     * If ModelView, use its method.
     * Else (expect String or JSON if it is not a ModelView), just print it.
     */
    public static void show(
            Object value,
            Mapping mapping,
            HttpServletRequest request, HttpServletResponse response
    ) throws ServletException, IOException {

        if ( ModelViewUtil.isInstance( value ) ) {
            ModelViewUtil.goToView( ( ModelView ) value, request, response );
        } else {
            String verb = request.getMethod();
            printString( mapping, value, response.getWriter(), verb );
        }
    }

    private static void printString( Mapping mapping, Object value, PrintWriter out, String verb ) {
        VerbAction va = mapping.getVerbAction( verb );

        out.println( "Controller: " + mapping.getControllerName() );
        out.println( "Verb: " + verb );
        out.println( "Method: " + va.getAction().getName() );
        out.println( "Return Value: " + value );
    }

    public static void printJson( Object value, HttpServletResponse response )
            throws IOException {
        response.setContentType( "application/json" );

        String myJson = new Gson().toJson( value );

        response.getWriter().print( myJson );
    }
}
