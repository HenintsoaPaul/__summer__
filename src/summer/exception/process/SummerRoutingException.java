package src.summer.exception.process;

import src.summer.exception.IWrittableException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class SummerRoutingException extends ServletException implements IWrittableException {
    @Override
    public void writeException( HttpServletResponse response )
            throws IOException {
        response.setStatus( HttpServletResponse.SC_NOT_FOUND );
        response.setCharacterEncoding( "UTF-8" );
        response.setContentType( "text/plain" );

        String msg = this.getMessage();
        response.getWriter().print( msg );
    }
}
