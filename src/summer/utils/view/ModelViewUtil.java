package src.summer.utils.view;

import src.summer.beans.ModelView;
import src.summer.beans.validation.ValidationLog;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class ModelViewUtil {
    /**
     * Verify whether an object is an instance of ModelView class.
     */
    public static boolean isInstance( Object instance ) {
        String className = instance.getClass().getName();
        return className.equals( ModelView.class.getName() );
    }

    /**
     * Dispatch data from the {@code ModelView} to the request object that will go to the page.
     * Then use {@code dispatcher.forward(req, resp)}.
     *
     * @param mv ModelView a afficher.
     */
    public static void goToView(
            ModelView mv,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        for (String key : mv.getData().keySet()) {
            request.setAttribute(key, mv.getObject(key));
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher(mv.getUrl());
        dispatcher.forward(request, response);
    }

    /**
     * @param modelViewInstance
     * @param validationLog Object contenant les erreurs de validation
     */
    public static void showFormValidationException(
            Object modelViewInstance,
            ValidationLog validationLog,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        // TODO: andramana commentena, puis defini depuis l'annotation
        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request) {
            @Override
            public String getMethod() {
                return "GET";
            }
        };
        // TODO: andramana commentena, puis defini depuis l'annotation

        ModelView mv = (ModelView) modelViewInstance;
        mv.addObject("validationLog", validationLog);

        goToView(mv, wrapper, response);
    }
}
