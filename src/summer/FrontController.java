package src.summer;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import src.summer.beans.Mapping;
import src.summer.handler.SummerFacade;
import src.summer.utils.*;

import java.io.IOException;
import java.util.HashMap;

@MultipartConfig
public class FrontController extends HttpServlet {

    private SummerFacade summerFacade;

    @Override
    public void init()
            throws ServletException {
        try {
            String packageName = getServletContext().getInitParameter("app.controllers.packageName");
            HashMap<String, Mapping> URLMappings = ScannerUtil.scanControllers(packageName);

            summerFacade = new SummerFacade(URLMappings, getServletContext());
        } catch (Exception e) {
            log("Error initializing FrontController", e);
            throw new ServletException("Initialization failed", e);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        processRequest(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        try {
            summerFacade.process(request, response);
        } catch (Exception e) {
            // todo: Envoyer les exceptions vers une view custom...
            throw new ServletException("Erreur de traitement", e); // Préservation du cause
        }
    }
}
