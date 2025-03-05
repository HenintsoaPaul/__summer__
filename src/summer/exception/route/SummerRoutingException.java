package src.summer.exception.route;

import javax.servlet.ServletException;
import java.io.UnsupportedEncodingException;

public class SummerRoutingException extends ServletException {
    public SummerRoutingException(String message ) {
        super(message);
    }

    public SummerRoutingException(String message, UnsupportedEncodingException e) {
        super(message, e);
    }
}
