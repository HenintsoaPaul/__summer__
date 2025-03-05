package src.summer.utils;

import src.summer.exception.route.SummerRoutingException;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public abstract class RouterUtil {
    /**
     * Extrait la route d'une URL en supprimant le nom de l'application et en reconstruisant
     * le chemin avec les segments restants.
     *
     * @param uri l'URL complète à analyser (par exemple : "/monapp/users/profile")
     * @return la route reconstruite sans le nom de l'application (par exemple : "users/profile")
     */
    public static String getRoute(String uri) {
        // Split url to a List<string> using "/" as a delimiter
        List<String> urlSegments = Arrays.asList(uri.split("/"));

        // Remove the first element which is an empty string, and the second element which is the application name
        urlSegments = urlSegments.subList(2, urlSegments.size());

        // Join the remaining elements to get the route
        return String.join("/", urlSegments);
    }

    /**
     * Gère les paramètres présents dans une URI et les ajoute comme attributs à la requête HTTP.
     * Cette méthode extrait tous les paramètres après le symbole '?' dans l'URI et les stocke
     * comme attributs de la requête pour un accès ultérieur.
     *
     * @param route   l'URI à analyser (exemple : "/login?idTest=1&name=fufu")
     * @param request l'objet HttpServletRequest où les paramètres seront stockés
     * @throws SummerRoutingException si le format des paramètres est incorrect
     * @throws RuntimeException       si une erreur survient lors du décodage des paramètres
     */
    public static void gererParametreDansRoute(String route, HttpServletRequest request) throws SummerRoutingException {
        // Trouver le début des paramètres
        int indexBegin = route.indexOf("?");

        if (indexBegin == -1) {
            // pas de paramètre envoyé dans l'url
            return;
        }

        String params = route.substring(indexBegin + 1);

        // Diviser les paramètres par &
        String[] couples = params.split("&");

        for (String couple : couples) {
            String[] pair = couple.split("=");

            if (pair.length != 2) {
                throw new SummerRoutingException("Format d'URL incorrect");
            }

            try {
                // Déocoder les paramètres pour gérer les caractères spéciaux
                String cle = URLDecoder.decode(pair[0], StandardCharsets.UTF_8.toString());
                String valeur = URLDecoder.decode(pair[1], StandardCharsets.UTF_8.toString());

                request.setAttribute(cle, valeur);
            } catch (UnsupportedEncodingException e) {
                throw new SummerRoutingException("Erreur lors du décodage des paramètres", e);
            }
        }
    }
}
