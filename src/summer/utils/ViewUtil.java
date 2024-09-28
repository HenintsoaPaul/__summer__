package src.summer.utils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import src.summer.beans.Mapping;
import src.summer.beans.ModelView;

import java.io.IOException;
import java.io.PrintWriter;

public abstract class ViewUtil {
    public static void show( Object value, Mapping mapping, HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        if ( isInstance( value ) ) {
            display( value, request, response );
        } else {
            print( mapping, value, response.getWriter() );
        }
    }

    private static boolean isInstance( Object instance ) {
        return instance.getClass().getName().equals( ModelView.class.getName() );
    }

    private static void print( Mapping mapping, Object value, PrintWriter out ) {
        out.println( "Controller: " + mapping.getControllerName() );
        out.println( "Method: " + mapping.getMethod().getName() );
        out.println( "Return Value: " + value );
    }

    private static void dispatchData( HttpServletRequest request, HttpServletResponse response, ModelView mv )
            throws ServletException, IOException {
        for ( String key : mv.getData().keySet() ) {
            request.setAttribute( key, mv.getObject( key ) );
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher( mv.getUrl() );
        dispatcher.forward( request, response );
    }

    private static void display( Object instance, HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        ModelView mv = (ModelView) instance;
        dispatchData( request, response, mv );
    }
}
