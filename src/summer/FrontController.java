package src.summer;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import src.summer.beans.Mapping;
import src.summer.handler.route.SummerFacade;
import src.summer.handler.route.SummerResponse;
import src.summer.utils.*;
import src.summer.utils.view.ViewUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@MultipartConfig
public class FrontController extends HttpServlet {

    /**
     * Map containing the Mapping objects matching to their URLs.
     */
    private HashMap<String, Mapping> URLMappings = new HashMap<>();
    private SummerFacade summerFacade;

    @Override
    public void init()
            throws ServletException {
        try {
            String packageName = getServletContext().getInitParameter("app.controllers.packageName");
            this.URLMappings = ScannerUtil.scanControllers(packageName);

            summerFacade = new SummerFacade(this.URLMappings, getServletContext());
        } catch (Exception e) {
            log("Error initializing FrontController", e);
            throw new ServletException("Initialization failed", e);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        processRequest(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            SummerResponse summerResponse = summerFacade.getResponse(request);
            ViewUtil.show(summerResponse, request, response);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchFieldException | NoSuchMethodException e) {
            // todo: Envoyer les exceptions vers une view custom...
            e.printStackTrace();
            throw new ServletException(e);
        }
    }
}
