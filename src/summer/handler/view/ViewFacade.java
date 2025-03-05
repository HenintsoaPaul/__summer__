package src.summer.handler.view;

import src.summer.handler.SummerResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ViewFacade {

    public void render(
            SummerResponse summerResponse,
            HttpServletRequest request,
            HttpServletResponse response
    )
            throws IOException, ServletException {
        if (summerResponse.getModelView() == null) {
            ModelViewUtil.printString(
                    summerResponse.getFormattedResponse(),
                    response
            );
        } else {
            ModelViewUtil.goToView(
                    summerResponse.getModelView(),
                    request,
                    response
            );
        }
    }
}
