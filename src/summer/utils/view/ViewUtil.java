package src.summer.utils.view;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import src.summer.handler.route.SummerResponse;

import java.io.IOException;
import java.io.PrintWriter;

public abstract class ViewUtil {

    /**
     * This method handle the way we display the value object.
     * If ModelView, use its method.
     * Else (expect String or JSON if it is not a ModelView), just print it.
     */
    public static void show(
            SummerResponse summerResponse,
            HttpServletRequest request,
            HttpServletResponse response
    )
            throws IOException, ServletException {
        if (summerResponse.getModelView() == null) {
            PrintWriter out = response.getWriter();
            System.out.println("formattedResponse: " + summerResponse.getFormattedResponse());
            out.print(summerResponse.getFormattedResponse());
        } else {
            ModelViewUtil.goToView(summerResponse.getModelView(), request, response);
        }
    }
}
