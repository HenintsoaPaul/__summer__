package src.summer.utils;

import com.google.gson.Gson;
import javax.servlet.RequestDispatcher;
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
     * If String of JSON, just print it.
     * If ModelView, use its method.
     */
    public static void show( Object value, Mapping mapping, HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        if ( ModelViewUtil.isInstance( value ) ) {
            display( value, request, response );
        } else {
            String verb = request.getMethod();
            print( mapping, value, response.getWriter(), verb );
        }
    }

    private static void print( Mapping mapping, Object value, PrintWriter out, String verb ) {
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
        PrintWriter out = response.getWriter();
        out.print( myJson );
    }

    private static void display( Object instance, HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        ModelView mv = ( ModelView ) instance;
        dispatchData( request, response, mv );
    }

    /**
     * Dispatch data from the ModelView to the request object.
     * Then use dispatcher.forward(req, resp).
     */
    private static void dispatchData( HttpServletRequest request, HttpServletResponse response, ModelView mv )
            throws ServletException, IOException {
        for ( String key : mv.getData().keySet() ) {
            request.setAttribute( key, mv.getObject( key ) );
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher( mv.getUrl() );
        dispatcher.forward( request, response );
    }
}
