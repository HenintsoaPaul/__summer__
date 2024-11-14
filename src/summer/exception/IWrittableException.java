package src.summer.exception;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IWrittableException {
    void writeException( HttpServletResponse response ) throws IOException;
}
