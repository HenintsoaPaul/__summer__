package src.summer.utils;

import com.google.gson.Gson;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import src.summer.beans.Mapping;
import src.summer.beans.ModelView;
import src.summer.beans.VerbAction;
import src.summer.beans.validation.ValidationLog;

import java.io.IOException;
import java.io.PrintWriter;

public abstract class ViewUtil {
    /**
     * This method handle the way we display the value object.
     * If ModelView, use its method.
     * Else (expect String or JSON if it is not a ModelView), just print it.
     */
    public static void show( Object value, Mapping mapping, HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        if ( ModelViewUtil.isInstance( value ) ) {
            goToView( ( ModelView ) value, true, request, response );
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
        PrintWriter out = response.getWriter();
        out.print( myJson );
    }

    /**
     * Dispatch data from the ModelView to the request object that will go to the page.
     * Then use dispatcher.forward(req, resp).
     */
    private static void goToView( ModelView mv, boolean onSuccess,
                                  HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        for ( String key : mv.getData().keySet() ) {
            request.setAttribute( key, mv.getObject( key ) );
        }
        String url = onSuccess ? mv.getUrl() : mv.getErrorUrl();
        RequestDispatcher dispatcher = request.getRequestDispatcher( url );
        dispatcher.forward( request, response );
    }

    public static void showFormValidationException( Object modelViewInstance, ValidationLog validationLog,
                                                    HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper( request ) {
            @Override
            public String getMethod() {
                return "GET";
            }
        };
        ModelView mv = ( ModelView ) modelViewInstance;
        mv.addObject( "validationLog", validationLog );
        goToView( mv, false, wrapper, response );
    }
}
